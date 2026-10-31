import { chapterSummarySchema, type ChapterSummary } from "../chapterSummarySchema";
import { summary as m01 } from "./m01";
import { summary as m02 } from "./m02";
import { summary as m03 } from "./m03";
import { summary as m04 } from "./m04";
import { summary as m05 } from "./m05";
import { summary as m06 } from "./m06";
import { summary as m07 } from "./m07";
import { summary as m08 } from "./m08";
import { summary as m09 } from "./m09";
import { summary as m10 } from "./m10";
import { summary as m11 } from "./m11";
import { summary as m12 } from "./m12";
import { summary as m13 } from "./m13";

/**
 * Chapter summaries keyed by module id.
 * Register each module file here. Missing modules fail the catalog test.
 */
const records: ChapterSummary[] = [
  m01,
  m02,
  m03,
  m04,
  m05,
  m06,
  m07,
  m08,
  m09,
  m10,
  m11,
  m12,
  m13,
].map((summary) => chapterSummarySchema.parse(summary));

export const chapterSummaries: ChapterSummary[] = records;

export const chapterSummaryByModuleId = new Map(
  chapterSummaries.map((summary) => [summary.moduleId, summary]),
);
