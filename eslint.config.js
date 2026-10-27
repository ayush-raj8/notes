import js from "@eslint/js";
import jsxA11y from "eslint-plugin-jsx-a11y";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import globals from "globals";
import tseslint from "typescript-eslint";

export default tseslint.config(
  {
    ignores: [
      "dist/**",
      "node_modules/**",
      "playwright-report/**",
      "test-results/**",
      "coverage/**",
    ],
  },
  js.configs.recommended,
  ...tseslint.configs.recommended,
  jsxA11y.flatConfigs.recommended,
  {
    files: ["**/*.{ts,tsx}"],
    languageOptions: {
      ecmaVersion: 2022,
      globals: globals.browser,
    },
    plugins: {
      "react-hooks": reactHooks,
      "react-refresh": reactRefresh,
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      "react-refresh/only-export-components": ["warn", { allowConstantExport: true }],
    },
  },
  {
    files: ["src/app/progress/ProgressContext.tsx"],
    rules: {
      "react-refresh/only-export-components": "off",
    },
  },
  {
    files: ["src/content/**/*.ts"],
    rules: {
      "no-restricted-imports": [
        "error",
        {
          patterns: [
            {
              group: [
                "react",
                "react-dom",
                "react-router-dom",
                "@/app/**",
                "@/reader/**",
                "@/visuals/**",
                "@/progress/**",
              ],
              message:
                "Curriculum content stays independent of React, routing, and progress storage.",
            },
          ],
        },
      ],
    },
  },
  {
    files: ["src/visuals/models/**/*.ts"],
    rules: {
      "no-restricted-imports": [
        "error",
        {
          patterns: [
            {
              group: ["react", "react-dom", "@/reader/**", "@/app/**"],
              message: "Visualization models stay free of React.",
            },
          ],
        },
      ],
    },
  },
  {
    files: ["vite.config.ts", "playwright.config.ts", "scripts/**/*.ts", "tests/e2e/**/*.ts"],
    languageOptions: {
      globals: globals.node,
    },
  },
);
