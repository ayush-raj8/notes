import { cleanup, render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it } from "vitest";
import { ProgressProvider } from "@/app/progress/ProgressContext";
import { AppRoutes } from "@/app/routes";
import { createMemoryStorage } from "@/progress/storage";
import { serializeProgress, markWhy, emptyProgress } from "@/progress/model";

function renderAt(path: string) {
  return render(
    <ProgressProvider storage={createMemoryStorage()}>
      <MemoryRouter initialEntries={[path]}>
        <AppRoutes />
      </MemoryRouter>
    </ProgressProvider>,
  );
}

describe("application shell", () => {
  it("shows the curriculum and an unwritten lesson", async () => {
    const user = userEvent.setup();
    renderAt("/");
    expect(screen.getByRole("heading", { name: "Notes" })).toBeInTheDocument();
    await user.click(screen.getByRole("link", { name: "Computer Networks" }));
    expect(screen.getByRole("heading", { name: "Computer Networks" })).toBeInTheDocument();
    expect(screen.getByText(/46 of 67 lessons written/)).toBeInTheDocument();
    await user.click(screen.getByRole("link", { name: "11. Performance and Reliability" }));
    expect(
      screen.getByRole("heading", { name: "Performance and Reliability" }),
    ).toBeInTheDocument();
    await user.click(
      within(screen.getByRole("main")).getByRole("link", {
        name: "Latency, bandwidth, and jitter",
      }),
    );
    expect(screen.getByText("This lesson is not written yet.")).toBeInTheDocument();
    expect(screen.getByText("Latency, bandwidth, throughput, and jitter.")).toBeInTheDocument();
  });

  it("shows not-found and a pending why answer", async () => {
    const user = userEvent.setup();
    renderAt("/cn/learn/missing");
    expect(screen.getByRole("heading", { name: "Page not found" })).toBeInTheDocument();
    cleanup();
    renderAt("/cn/why");
    await user.click(screen.getByRole("link", { name: /Why can a low-bandwidth-delay product/ }));
    expect(screen.getByText("Answer not written yet.")).toBeInTheDocument();
  });

  it("opens the menu and imports progress only after confirmation", async () => {
    const user = userEvent.setup();
    renderAt("/cn/review");
    await user.click(screen.getByRole("button", { name: "Menu" }));
    expect(screen.getByRole("link", { name: "Why" })).toBeVisible();
    expect(screen.getByText("Nothing is due.")).toBeInTheDocument();

    const exported = serializeProgress(
      markWhy(emptyProgress(), "why.11", "missed", "2020-01-01T00:00:00.000Z"),
    );
    const input = screen.getByLabelText("Import a progress file");
    await user.upload(input, new File(["{}"], "bad.json", { type: "application/json" }));
    expect(await screen.findByRole("alert")).toHaveTextContent(/not a progress export/i);

    await user.upload(input, new File([exported], "progress.json", { type: "application/json" }));
    expect(
      await screen.findByText(/Replace the progress stored in this browser/),
    ).toBeInTheDocument();
    await user.click(screen.getByRole("button", { name: "Replace progress" }));
    expect(
      screen.getByRole("link", { name: /Why does TCP use a three-way handshake/ }),
    ).toBeInTheDocument();
  });
});
