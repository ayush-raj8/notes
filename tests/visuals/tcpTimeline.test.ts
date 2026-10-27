import { describe, expect, it } from "vitest";
import { curriculum } from "@/content/subjects/cn/load";
import {
  describeTcpTimelineStep,
  getTcpTimelineStep,
  segmentSummary,
  tcpTimelineScenarios,
  type TcpTimelineStep,
} from "@/visuals/models/tcpTimeline";
import { getVisualView, implementedVisualIds } from "@/visuals/registry";

type Expected = Pick<TcpTimelineStep, "from" | "flags" | "seq" | "ack" | "stateA" | "stateB"> &
  Partial<Pick<TcpTimelineStep, "len" | "delivery" | "isRetransmit">>;

function expectSteps(id: string, expected: Expected[]) {
  const scenario = tcpTimelineScenarios[id];
  expect(scenario, id).toBeDefined();
  expect(scenario?.steps).toHaveLength(expected.length);
  expected.forEach((want, index) => {
    const step = getTcpTimelineStep(id, index);
    expect(step, `${id} step ${index}`).toMatchObject({ index, ...want });
  });
}

describe("tcp-timeline model", () => {
  it("is registered as implemented with a view", () => {
    expect(implementedVisualIds.has("tcp-timeline")).toBe(true);
    expect(getVisualView("tcp-timeline")).toBeDefined();
    expect(getTcpTimelineStep("handshake-basic", 99)).toBeUndefined();
    expect(getTcpTimelineStep("no-such-scenario", 0)).toBeUndefined();
  });

  it("steps the basic handshake with RFC 9293 Figure 6 numbers", () => {
    expectSteps("handshake-basic", [
      { from: null, flags: [], seq: null, ack: null, stateA: "CLOSED", stateB: "LISTEN" },
      {
        from: "a",
        flags: ["SYN"],
        seq: 100,
        ack: null,
        stateA: "SYN-SENT",
        stateB: "SYN-RECEIVED",
      },
      {
        from: "b",
        flags: ["SYN", "ACK"],
        seq: 300,
        ack: 101,
        stateA: "ESTABLISHED",
        stateB: "SYN-RECEIVED",
      },
      {
        from: "a",
        flags: ["ACK"],
        seq: 101,
        ack: 301,
        stateA: "ESTABLISHED",
        stateB: "ESTABLISHED",
      },
      {
        from: "a",
        flags: ["PSH", "ACK"],
        seq: 101,
        ack: 301,
        len: 78,
        stateA: "ESTABLISHED",
        stateB: "ESTABLISHED",
      },
      {
        from: "b",
        flags: ["ACK"],
        seq: 301,
        ack: 179,
        stateA: "ESTABLISHED",
        stateB: "ESTABLISHED",
      },
    ]);
    const synAck = getTcpTimelineStep("handshake-basic", 2);
    expect(synAck?.bKnows).toMatch(/does not know whether its SYN arrived/);
    expect(getTcpTimelineStep("handshake-basic", 5)?.aKnows).toMatch(/does not know whether/);
  });

  it("rejects an old duplicate SYN with a RST before the real handshake", () => {
    expectSteps("handshake-old-duplicate", [
      { from: null, flags: [], seq: null, ack: null, stateA: "CLOSED", stateB: "LISTEN" },
      {
        from: "a",
        flags: ["SYN"],
        seq: 100,
        ack: null,
        delivery: "in-flight",
        stateA: "SYN-SENT",
        stateB: "LISTEN",
      },
      { from: "a", flags: ["SYN"], seq: 90, ack: null, stateA: "SYN-SENT", stateB: "SYN-RECEIVED" },
      {
        from: "b",
        flags: ["SYN", "ACK"],
        seq: 300,
        ack: 91,
        stateA: "SYN-SENT",
        stateB: "SYN-RECEIVED",
      },
      { from: "a", flags: ["RST"], seq: 91, ack: null, stateA: "SYN-SENT", stateB: "LISTEN" },
      {
        from: "a",
        flags: ["SYN"],
        seq: 100,
        ack: null,
        stateA: "SYN-SENT",
        stateB: "SYN-RECEIVED",
      },
      {
        from: "b",
        flags: ["SYN", "ACK"],
        seq: 400,
        ack: 101,
        stateA: "ESTABLISHED",
        stateB: "SYN-RECEIVED",
      },
      {
        from: "a",
        flags: ["ACK"],
        seq: 101,
        ack: 401,
        stateA: "ESTABLISHED",
        stateB: "ESTABLISHED",
      },
    ]);
  });

  it("refuses a SYN to a closed port with RST, ACK", () => {
    expectSteps("handshake-refused", [
      {
        from: null,
        flags: [],
        seq: null,
        ack: null,
        stateA: "CLOSED",
        stateB: "CLOSED (no listener)",
      },
      {
        from: "a",
        flags: ["SYN"],
        seq: 100,
        ack: null,
        stateA: "SYN-SENT",
        stateB: "CLOSED (no listener)",
      },
      {
        from: "b",
        flags: ["RST", "ACK"],
        seq: 0,
        ack: 101,
        stateA: "CLOSED",
        stateB: "CLOSED (no listener)",
      },
    ]);
    expect(getTcpTimelineStep("handshake-refused", 2)?.aKnows).toMatch(/ECONNREFUSED/);
  });

  it("closes gracefully with FIN, half-close data, and TIME-WAIT on the active closer", () => {
    expectSteps("teardown-fin", [
      { from: null, flags: [], seq: null, ack: null, stateA: "ESTABLISHED", stateB: "ESTABLISHED" },
      {
        from: "a",
        flags: ["FIN", "ACK"],
        seq: 100,
        ack: 300,
        stateA: "FIN-WAIT-1",
        stateB: "CLOSE-WAIT",
      },
      { from: "b", flags: ["ACK"], seq: 300, ack: 101, stateA: "FIN-WAIT-2", stateB: "CLOSE-WAIT" },
      {
        from: "b",
        flags: ["PSH", "ACK"],
        seq: 300,
        ack: 101,
        len: 50,
        stateA: "FIN-WAIT-2",
        stateB: "CLOSE-WAIT",
      },
      { from: "a", flags: ["ACK"], seq: 101, ack: 350, stateA: "FIN-WAIT-2", stateB: "CLOSE-WAIT" },
      {
        from: "b",
        flags: ["FIN", "ACK"],
        seq: 350,
        ack: 101,
        stateA: "TIME-WAIT",
        stateB: "LAST-ACK",
      },
      { from: "a", flags: ["ACK"], seq: 101, ack: 351, stateA: "TIME-WAIT", stateB: "CLOSED" },
      { from: null, flags: [], seq: null, ack: null, stateA: "CLOSED", stateB: "CLOSED" },
    ]);
  });

  it("aborts a half-open connection with a RST taken from the ACK field", () => {
    expectSteps("teardown-rst", [
      { from: null, flags: [], seq: null, ack: null, stateA: "ESTABLISHED", stateB: "ESTABLISHED" },
      {
        from: null,
        flags: [],
        seq: null,
        ack: null,
        stateA: "ESTABLISHED",
        stateB: "CLOSED (state lost)",
      },
      {
        from: "a",
        flags: ["PSH", "ACK"],
        seq: 300,
        ack: 100,
        len: 10,
        stateA: "ESTABLISHED",
        stateB: "CLOSED (state lost)",
      },
      {
        from: "b",
        flags: ["RST"],
        seq: 100,
        ack: null,
        stateA: "CLOSED",
        stateB: "CLOSED (state lost)",
      },
    ]);
    expect(getTcpTimelineStep("teardown-rst", 3)?.aKnows).toMatch(/ECONNRESET/);
  });

  it("closes simultaneously through CLOSING into TIME-WAIT on both sides", () => {
    expectSteps("teardown-simultaneous", [
      { from: null, flags: [], seq: null, ack: null, stateA: "ESTABLISHED", stateB: "ESTABLISHED" },
      {
        from: "a",
        flags: ["FIN", "ACK"],
        seq: 100,
        ack: 300,
        stateA: "FIN-WAIT-1",
        stateB: "ESTABLISHED",
      },
      {
        from: "b",
        flags: ["FIN", "ACK"],
        seq: 300,
        ack: 100,
        stateA: "FIN-WAIT-1",
        stateB: "FIN-WAIT-1",
      },
      {
        from: "b",
        flags: ["FIN", "ACK"],
        seq: 300,
        ack: 100,
        stateA: "CLOSING",
        stateB: "FIN-WAIT-1",
      },
      {
        from: "a",
        flags: ["FIN", "ACK"],
        seq: 100,
        ack: 300,
        stateA: "CLOSING",
        stateB: "CLOSING",
      },
      { from: "a", flags: ["ACK"], seq: 101, ack: 301, stateA: "CLOSING", stateB: "TIME-WAIT" },
      { from: "b", flags: ["ACK"], seq: 301, ack: 101, stateA: "TIME-WAIT", stateB: "TIME-WAIT" },
      { from: null, flags: [], seq: null, ack: null, stateA: "CLOSED", stateB: "CLOSED" },
    ]);
  });

  it("separates the original from the retransmission in loss scenarios", () => {
    const fast = tcpTimelineScenarios["loss-fast-retransmit"]?.steps ?? [];
    const sent1100 = fast.filter((step) => step.from === "a" && step.seq === 1100);
    expect(sent1100.map((step) => [step.isRetransmit, step.delivery])).toEqual([
      [false, "lost"],
      [true, "arrives"],
    ]);
    const dupAcks = fast.filter((step) => step.from === "b" && step.ack === 1100 && step.sack);
    expect(dupAcks.map((step) => step.sack)).toEqual(["1200–1300", "1200–1400", "1200–1500"]);
    expect(fast.at(-1)).toMatchObject({ from: "b", ack: 1500 });

    const timeout = tcpTimelineScenarios["loss-timeout"]?.steps ?? [];
    expect(
      timeout.filter((step) => step.seq === 2000).map((step) => [step.isRetransmit, step.delivery]),
    ).toEqual([
      [false, "lost"],
      [true, "lost"],
      [true, "arrives"],
    ]);
    expect(timeout.filter((step) => step.from === null)).toHaveLength(3);
    expect(timeout.at(-1)).toMatchObject({ from: "b", ack: 2100 });
  });

  it("keeps every step internally consistent", () => {
    for (const scenario of Object.values(tcpTimelineScenarios)) {
      scenario.steps.forEach((step, index) => {
        const where = `${scenario.id} step ${index}`;
        expect(step.index, where).toBe(index);
        expect(step.aKnows.length + step.bKnows.length, where).toBeGreaterThan(0);
        if (step.from === null) {
          expect(step.flags, where).toEqual([]);
          expect(segmentSummary(step), where).toBe("No segment");
        } else {
          expect(step.flags.length, where).toBeGreaterThan(0);
          expect(step.seq, where).not.toBeNull();
          expect(step.ack !== null, where).toBe(step.flags.includes("ACK"));
        }
        expect(describeTcpTimelineStep(scenario, index), where).toContain(
          `Step ${index + 1} of ${scenario.steps.length}: ${step.label}.`,
        );
      });
    }
  });

  it("is referenced only with scenarios that exist", () => {
    const sections = curriculum.lessons.flatMap((lesson) =>
      lesson.sections.flatMap((section) =>
        section.type === "viz" && section.vizId === "tcp-timeline" ? [section] : [],
      ),
    );
    expect(sections.length).toBeGreaterThan(0);
    for (const section of sections) {
      expect(tcpTimelineScenarios[section.scenario], section.scenario).toBeDefined();
    }
  });
});
