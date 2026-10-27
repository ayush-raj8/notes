export const subjects = [
  {
    id: "cn",
    slug: "cn",
    title: "Computer Networks",
    summary:
      "How networks move data, from the problem a protocol solves through the failures you have to debug.",
  },
] as const;

export type Subject = (typeof subjects)[number];

export function findSubject(slug: string): Subject | undefined {
  return subjects.find((subject) => subject.slug === slug);
}
