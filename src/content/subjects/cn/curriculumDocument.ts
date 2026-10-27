import { curriculum, lessonPath } from "./load";

function lessonLine(id: string, title: string): string {
  return `- \`${id}\` — ${title}`;
}

export function renderCurriculumMarkdown(): string {
  const lines: string[] = [
    "# Curriculum",
    "",
    "This inventory is generated from `src/content`. Edit the catalog, then run `pnpm curriculum:doc`.",
    "",
    "Lesson ids and question ids are stable. A later session fills a lesson body. It does not renumber the syllabus.",
    "",
    "Content status in the app is separate from this list. A lesson listed here may still be unwritten.",
    "",
  ];

  for (const module of curriculum.modules) {
    lines.push(`## Module ${module.number}: ${module.title}`, "");
    lines.push(`Slug: \`${module.slug}\`. Id: \`${module.id}\`.`, "");
    lines.push(module.summary, "");
    lines.push("### Lessons", "");
    for (const lesson of module.lessons) {
      lines.push(lessonLine(lesson.id, lesson.title));
      lines.push(`  - Path: \`${lessonPath(lesson)}\``);
      if (lesson.ownsTopics.length === 0) {
        lines.push(
          "  - Framing lesson for the scenarios below. It does not add a syllabus bullet.",
        );
      }
      for (const topic of lesson.ownsTopics) {
        lines.push(`  - Owns \`${topic.id}\`: ${topic.bullet}`);
      }
    }
    lines.push("", "### Syllabus bullets", "");
    for (const bullet of module.bullets) {
      lines.push(`- ${bullet}`);
    }
    lines.push("");
  }

  lines.push("## Why Does Networking Work This Way?", "");
  lines.push(
    "Core questions `why.01` through `why.50` are the required collection. `why.51` through `why.55` are the approved additions.",
    "",
  );
  for (const group of [
    "protocol-layering",
    "tcp-and-transport",
    "dns-http-and-tls",
    "routing-and-addressing",
    "performance-and-reliability",
  ] as const) {
    const title = curriculum.whyQuestions.find((question) => question.group === group);
    const groupTitle =
      group === "protocol-layering"
        ? "Protocol layering and architecture"
        : group === "tcp-and-transport"
          ? "TCP and transport"
          : group === "dns-http-and-tls"
            ? "DNS, HTTP and TLS"
            : group === "routing-and-addressing"
              ? "Routing and addressing"
              : "Performance and reliability";
    void title;
    lines.push(`### ${groupTitle}`, "");
    for (const question of curriculum.whyQuestions.filter((item) => item.group === group)) {
      const tag = question.set === "added" ? " (added)" : "";
      lines.push(`- \`${question.id}\`${tag} — ${question.prompt}`);
      lines.push(`  - Primary lesson: \`${question.primaryLessonId}\``);
    }
    lines.push("");
  }

  return `${lines.join("\n").trim()}\n`;
}
