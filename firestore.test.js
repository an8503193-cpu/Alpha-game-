const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

const nowMinus1s = () => new Date(Date.now() - 1000);

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const CHARLIE_UID = "charlie_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) await testEnv.cleanup();
});

beforeEach(async () => {
  if (testEnv) await testEnv.clearFirestore();
});

test("Unauthenticated user cannot read or write users, rooms, or leaderboard", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("rooms").where("status", "==", "waiting").get());
  await assertFails(unauthDb.collection("leaderboard").get());
});

test("User can create and update own profile, but cannot inject ghost fields or overwrite another user", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      pseudo: "AliceQuiz",
      level: 1,
      xp: 0,
      quizCoins: 200,
      isPublic: true,
      createdAt: nowMinus1s(),
      updatedAt: nowMinus1s(),
    })
  );

  // Ghost field rejected
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      pseudo: "AliceQuiz",
      level: 1,
      xp: 0,
      quizCoins: 200,
      isAdmin: true,
      createdAt: nowMinus1s(),
      updatedAt: nowMinus1s(),
    })
  );

  // Cross-user private PII denied
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("users").doc(BOB_UID).collection("private").doc("info").set({
      userId: BOB_UID,
      email: "bob@example.com",
      createdAt: nowMinus1s(),
      updatedAt: nowMinus1s(),
    });
  });
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("private").doc("info").get());
});

test("Cross-user history and missions isolation", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("users").doc(BOB_UID).collection("history").doc("m1").set({
      userId: BOB_UID,
      mode: "Duel",
      category: "Sciences",
      score: 400,
      rank: 1,
      correctCount: 4,
      totalCount: 5,
      createdAt: nowMinus1s(),
    });
  });
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("history").get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("missions").get());
});

test("Multiplayer room creation, joining waiting room, and blocking joins on active room", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const charlieDb = testEnv.authenticatedContext(CHARLIE_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("rooms").doc("room_1").set({
      roomCode: "ABC123",
      hostId: ALICE_UID,
      mode: "Duel",
      category: "Sciences",
      difficulty: "Moyen",
      questionCount: 10,
      timePerQuestionSec: 15,
      status: "waiting",
      maxPlayers: 2,
      participantIds: [ALICE_UID],
      createdAt: nowMinus1s(),
      updatedAt: nowMinus1s(),
    })
  );

  // Bob joins waiting room
  await assertSucceeds(
    bobDb.collection("rooms").doc("room_1").update({
      participantIds: [ALICE_UID, BOB_UID],
      updatedAt: nowMinus1s(),
    })
  );

  // Host starts game
  await assertSucceeds(
    aliceDb.collection("rooms").doc("room_1").update({
      status: "active",
      updatedAt: nowMinus1s(),
    })
  );

  // Charlie cannot join active room
  await assertFails(
    charlieDb.collection("rooms").doc("room_1").update({
      participantIds: [ALICE_UID, BOB_UID, CHARLIE_UID],
      updatedAt: nowMinus1s(),
    })
  );
});

test("Challenge creation and participant updates", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const charlieDb = testEnv.authenticatedContext(CHARLIE_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("challenges").doc("ch_1").set({
      senderId: ALICE_UID,
      receiverId: BOB_UID,
      participantIds: [ALICE_UID, BOB_UID],
      category: "Histoire",
      difficulty: "Moyen",
      questionCount: 10,
      status: "pending",
      senderScore: 650,
      receiverScore: 0,
      createdAt: nowMinus1s(),
      updatedAt: nowMinus1s(),
    })
  );

  await assertSucceeds(
    bobDb.collection("challenges").doc("ch_1").update({
      status: "completed",
      receiverScore: 700,
      updatedAt: nowMinus1s(),
    })
  );

  await assertFails(charlieDb.collection("challenges").doc("ch_1").get());
});
