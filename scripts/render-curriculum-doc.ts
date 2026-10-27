import { writeFileSync } from "node:fs";
import { renderCurriculumMarkdown } from "../src/content/subjects/cn/curriculumDocument";

writeFileSync(new URL("../CURRICULUM.md", import.meta.url), renderCurriculumMarkdown(), "utf8");
