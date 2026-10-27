import { expect, test } from "@playwright/test";

test("home opens a module and an unwritten lesson", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByRole("heading", { name: "Notes" })).toBeVisible();
  await page.getByRole("link", { name: "Computer Networks" }).click();
  await expect(page.getByRole("heading", { name: "Computer Networks" })).toBeVisible();
  await page.getByRole("link", { name: "8. HTTP and Application-Layer Networking" }).click();
  await expect(
    page.getByRole("heading", { name: "HTTP and Application-Layer Networking" }),
  ).toBeVisible();
  await page.getByRole("main").getByRole("link", { name: "The HTTP request and response" }).click();
  await expect(page.getByText("This lesson is not written yet.")).toBeVisible();
  const overflows = await page.evaluate(
    () => document.documentElement.scrollWidth > document.documentElement.clientWidth + 1,
  );
  expect(overflows).toBe(false);
});

test("a written Module 1 lesson shows its principle section", async ({ page }) => {
  await page.goto("/cn/learn/foundations/what-a-network-must-do");
  await expect(page.getByRole("heading", { name: "What a network must do" })).toBeVisible();
  await expect(page.getByRole("heading", { name: "First principles" })).toBeVisible();
  await expect(page.getByRole("button", { name: "Mark lesson finished" })).toBeVisible();
});

test("the handshake timeline steps without overflowing the page", async ({ page }) => {
  await page.goto("/cn/learn/tcp/handshake");
  const timeline = page.getByRole("group", { name: "Three-way handshake, then the first request" });
  await timeline.getByRole("button", { name: "Next step" }).click();
  await timeline.getByRole("button", { name: "Next step" }).click();
  await expect(timeline.getByTestId("tcp-timeline-description")).toContainText(
    "SYN, ACK seq=300 ack=101",
  );
  const overflows = await page.evaluate(
    () => document.documentElement.scrollWidth > document.documentElement.clientWidth + 1,
  );
  expect(overflows).toBe(false);
});

test("unknown routes show not found", async ({ page }) => {
  await page.goto("/cn/learn/not-a-module");
  await expect(page.getByRole("heading", { name: "Page not found" })).toBeVisible();
});

test("narrow viewports open the menu", async ({ page }, testInfo) => {
  test.skip(testInfo.project.name !== "mobile", "The menu button is for narrow viewports.");
  await page.goto("/cn");
  await page.getByRole("button", { name: "Menu" }).click();
  await expect(
    page.getByRole("navigation", { name: "Primary" }).getByRole("link", { name: "Why" }),
  ).toBeVisible();
});
