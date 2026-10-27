import { designCodingQuestionSchema, type DesignCodingQuestion } from "../../../schema";
import { carRentalOptimization } from "./car-rental-optimization";
import { graphSerialize } from "./graph-serialize";
import { grepContext } from "./grep-context";
import { httpRouteMatching } from "./http-route-matching";
import { insertRemoveGetMin } from "./insert-remove-getmin";
import { loggerRateLimiterCleanup } from "./logger-rate-limiter-cleanup";
import { mostRecentlySearched } from "./most-recently-searched";
import { randomTreeNode } from "./random-tree-node";
import { rangePreprocessQuery } from "./range-preprocess-query";
import { relatedPracticeQuestions } from "./related-practice";
import { restaurantWaitlist } from "./restaurant-waitlist";
import { secureLinkedList } from "./secure-linked-list";

const raw: DesignCodingQuestion[] = [
  secureLinkedList,
  loggerRateLimiterCleanup,
  mostRecentlySearched,
  graphSerialize,
  randomTreeNode,
  grepContext,
  rangePreprocessQuery,
  restaurantWaitlist,
  httpRouteMatching,
  insertRemoveGetMin,
  carRentalOptimization,
  ...relatedPracticeQuestions,
];

export const designCodingQuestions: DesignCodingQuestion[] = raw.map((q) =>
  designCodingQuestionSchema.parse(q),
);

const byId = new Map(designCodingQuestions.map((q) => [q.id, q]));
const bySlug = new Map(designCodingQuestions.map((q) => [q.slug, q]));

export function findDesignCodingQuestion(slug: string): DesignCodingQuestion | undefined {
  return bySlug.get(slug);
}

export function findDesignCodingQuestionById(id: string): DesignCodingQuestion | undefined {
  return byId.get(id);
}

export function designCodingCounts() {
  const reported = designCodingQuestions.filter((q) => q.classification === "reported-google");
  const hello = designCodingQuestions.filter((q) => q.classification === "hello-interview-guide");
  const practice = designCodingQuestions.filter((q) => q.classification === "related-practice");
  const unverified = designCodingQuestions.filter((q) => q.verification === "unverified");
  const partial = designCodingQuestions.filter((q) => q.verification === "partially-verified");
  return {
    total: designCodingQuestions.length,
    reported: reported.length,
    reportedVerified: reported.filter((q) => q.verification === "verified").length,
    hello: hello.length,
    practice: practice.length,
    unverified: unverified.length,
    partial: partial.length,
  };
}
