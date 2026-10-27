export const subjects = [
  {
    id: "cn",
    slug: "cn",
    title: "Computer Networks",
    summary:
      "How networks move data, from the problem a protocol solves through the failures you have to debug.",
  },
  {
    id: "dsa",
    slug: "dsa",
    title: "DSA",
    summary:
      "Interview-oriented coding notes. Starts with PYQ — Design Coding for Google L3–L5 implementation rounds.",
  },
] as const;

export type Subject = (typeof subjects)[number];

export function findSubject(slug: string): Subject | undefined {
  return subjects.find((subject) => subject.slug === slug);
}
