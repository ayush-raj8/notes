import type { Confidence } from "@/progress/model";

const options: { value: Confidence; label: string }[] = [
  { value: "known", label: "Knew it" },
  { value: "shaky", label: "Shaky" },
  { value: "missed", label: "Missed" },
];

export function ConfidenceButtons({
  value,
  onChange,
  legend,
}: {
  value?: Confidence;
  onChange: (confidence: Confidence) => void;
  legend: string;
}) {
  return (
    <fieldset className="confidence">
      <legend>{legend}</legend>
      <div className="confidence-options">
        {options.map((option) => (
          <button
            key={option.value}
            type="button"
            className={
              value === option.value ? "button confidence-button is-selected" : "button secondary"
            }
            aria-pressed={value === option.value}
            onClick={() => onChange(option.value)}
          >
            {option.label}
          </button>
        ))}
      </div>
    </fieldset>
  );
}
