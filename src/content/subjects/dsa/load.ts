export {
  designCodingCounts,
  designCodingQuestions,
  findDesignCodingQuestion,
  findDesignCodingQuestionById,
} from "./pyq/design-coding/questions";

export const dsaHomePath = "/dsa";
export const dsaDesignCodingPath = "/dsa/pyq/design-coding";

export function designCodingQuestionPath(slug: string): string {
  return `${dsaDesignCodingPath}/${slug}`;
}

export const dsaSections = [
  {
    id: "pyq-design-coding",
    title: "PYQ — Design Coding",
    path: dsaDesignCodingPath,
    summary:
      "Google interview design-coding PYQs and Hello Interview guide problems, with related LeetCode practice kept separate.",
  },
] as const;
