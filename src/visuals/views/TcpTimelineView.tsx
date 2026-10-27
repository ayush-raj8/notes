import { useId, useState } from "react";
import {
  describeTcpTimelineStep,
  directionText,
  getTcpTimelineScenario,
  segmentSummary,
  type TcpTimelineScenario,
  type TcpTimelineStep,
} from "../models/tcpTimeline";
import type { VisualViewProps } from "./types";

function SegmentRow({
  scenario,
  step,
  current,
}: {
  scenario: TcpTimelineScenario;
  step: TcpTimelineStep;
  current: boolean;
}) {
  const towardB = step.from === "a";
  const classes = [
    "tcp-row",
    current ? "is-current" : "",
    step.from === null ? "is-event" : "",
    step.delivery === "lost" ? "is-lost" : "",
    step.delivery === "in-flight" ? "is-in-flight" : "",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <li className={classes} aria-current={current ? "step" : undefined}>
      <span className="tcp-cell tcp-state">{step.stateA}</span>
      <span className="tcp-cell tcp-wire">
        <span className="tcp-step-number">{step.index + 1}.</span>
        {step.from === null ? (
          <span className="tcp-event">{step.label}</span>
        ) : (
          <>
            <span className="tcp-direction">
              {towardB ? "" : "◀ "}
              {directionText(scenario, step)}
              {towardB ? " ▶" : ""}
            </span>
            <span className="tcp-segment">
              {step.isRetransmit ? <span className="tcp-tag">Retransmission</span> : null}
              <code>{segmentSummary(step)}</code>
              {step.delivery === "lost" ? <span className="tcp-tag">✕ Lost</span> : null}
              {step.delivery === "in-flight" ? <span className="tcp-tag">In flight</span> : null}
            </span>
          </>
        )}
      </span>
      <span className="tcp-cell tcp-state">{step.stateB}</span>
    </li>
  );
}

export function TcpTimelineView({
  scenario: scenarioId,
  learningObjective,
  caption,
}: VisualViewProps) {
  const scenario = getTcpTimelineScenario(scenarioId);
  const [index, setIndex] = useState(0);
  const selectId = useId();
  const headingId = useId();

  if (!scenario) {
    return (
      <div className="empty">
        <strong>Unknown timeline scenario: {scenarioId}.</strong>
        <p>{caption}</p>
      </div>
    );
  }

  const last = scenario.steps.length - 1;
  const step = scenario.steps[index] ?? scenario.steps[0];
  if (!step) return null;
  const shown = scenario.steps.slice(0, index + 1);

  return (
    <div className="tcp-timeline" role="group" aria-labelledby={headingId}>
      <h3 id={headingId}>{scenario.title}</h3>
      <p className="meta">
        <span>Objective: {learningObjective}</span>
      </p>

      <div className="tcp-controls">
        <button
          type="button"
          className="button secondary"
          onClick={() => setIndex((value) => Math.max(0, value - 1))}
          disabled={index === 0}
        >
          Previous step
        </button>
        <button
          type="button"
          className="button"
          onClick={() => setIndex((value) => Math.min(last, value + 1))}
          disabled={index === last}
        >
          Next step
        </button>
        <label htmlFor={selectId} className="tcp-jump">
          Jump to step
          <select
            id={selectId}
            value={index}
            onChange={(event) => setIndex(Number(event.target.value))}
          >
            {scenario.steps.map((item) => (
              <option key={item.index} value={item.index}>
                {item.index + 1}. {item.label}
              </option>
            ))}
          </select>
        </label>
      </div>

      <div className="tcp-ladder-wrap">
        <div className="tcp-ladder-head" aria-hidden="true">
          <span>{scenario.labels.a}</span>
          <span>Segment</span>
          <span>{scenario.labels.b}</span>
        </div>
        <ol
          className="tcp-ladder"
          aria-label={`Steps 1 to ${index + 1} of ${scenario.steps.length}`}
        >
          {shown.map((item) => (
            <SegmentRow key={item.index} scenario={scenario} step={item} current={item === step} />
          ))}
        </ol>
      </div>

      <p className="tcp-description" aria-live="polite" data-testid="tcp-timeline-description">
        {describeTcpTimelineStep(scenario, index)}
      </p>

      <dl className="tcp-knows">
        <div>
          <dt>
            {scenario.labels.a} ({step.stateA}) knows
          </dt>
          <dd>{step.aKnows || "Nothing new."}</dd>
        </div>
        <div>
          <dt>
            {scenario.labels.b} ({step.stateB}) knows
          </dt>
          <dd>{step.bKnows || "Nothing new."}</dd>
        </div>
      </dl>

      <p className="muted">{caption}</p>
      <p className="muted">Left out: {scenario.omitted}</p>
    </div>
  );
}
