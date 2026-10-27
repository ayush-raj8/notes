import { expect, test } from "@playwright/test";

test("home opens a module and an unwritten lesson", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByRole("heading", { name: "Notes" })).toBeVisible();
  await page.getByRole("link", { name: "Computer Networks" }).click();
  await expect(page.getByRole("heading", { name: "Computer Networks" })).toBeVisible();
  await page.getByRole("link", { name: "1. Networking Foundations" }).click();
  await expect(page.getByRole("heading", { name: "Networking Foundations" })).toBeVisible();
  await page.getByRole("main").getByRole("link", { name: "What a network must do" }).click();
  await expect(page.getByText("This lesson is not written yet.")).toBeVisible();
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
