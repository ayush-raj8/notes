/// <reference types="vitest/config" />
import { fileURLToPath, URL } from "node:url";
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

const pagesBase = process.env.GITHUB_PAGES === "true" ? "/notes/" : "/";

export default defineConfig({
  base: pagesBase,
  plugins: [react()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  test: {
    environment: "jsdom",
    setupFiles: "./tests/setup.ts",
    exclude: ["node_modules/**", "dist/**", "tests/e2e/**"],
  },
});
