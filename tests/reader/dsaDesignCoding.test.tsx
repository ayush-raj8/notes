import { render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it } from "vitest";
import { ProgressProvider } from "@/app/progress/ProgressContext";
import { AppRoutes } from "@/app/routes";
import { createMemoryStorage } from "@/progress/storage";

function renderAt(path: string) {
  return render(
    <ProgressProvider storage={createMemoryStorage()}>
      <MemoryRouter initialEntries={[path]}>
        <AppRoutes />
      </MemoryRouter>
    </ProgressProvider>,
  );
}

describe("DSA design coding section", () => {
  it("is reachable from the notes home without removing Computer Networks", async () => {
    const user = userEvent.setup();
    renderAt("/");
    expect(screen.getByRole("link", { name: "Computer Networks" })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "DSA" })).toBeInTheDocument();
    await user.click(screen.getByRole("link", { name: "DSA" }));
    expect(screen.getByRole("heading", { name: "DSA" })).toBeInTheDocument();
    await user.click(screen.getByRole("link", { name: "PYQ — Design Coding" }));
    expect(screen.getByRole("heading", { name: "PYQ — Design Coding" })).toBeInTheDocument();
    expect(screen.getByLabelText("Question counts")).toHaveTextContent(/verified reported/i);
  });

  it("opens a question detail page with implementation and sources", async () => {
    const user = userEvent.setup();
    renderAt("/dsa/pyq/design-coding");
    await user.click(screen.getByRole("link", { name: "Secure Linked List / Hash Chain" }));
    expect(
      screen.getByRole("heading", { name: "Secure Linked List / Hash Chain" }),
    ).toBeInTheDocument();
    expect(screen.getByText("dc.secure-linked-list")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Implementation (Python 3)" })).toBeInTheDocument();
    expect(screen.getAllByText(/hash\(data \+ next\.hashValue\)/).length).toBeGreaterThan(0);
  });

  it("filters by source classification", async () => {
    const user = userEvent.setup();
    renderAt("/dsa/pyq/design-coding");
    const source = screen.getByLabelText("Source");
    await user.selectOptions(source, "related-practice");
    const primary = screen.getByRole("heading", { name: "Primary index" }).closest("section");
    expect(primary).toBeTruthy();
    expect(within(primary as HTMLElement).getByText(/0 questions/)).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "LRU Cache" })).toBeInTheDocument();
  });

  it("keeps Computer Networks routes working", () => {
    renderAt("/cn");
    expect(screen.getByRole("heading", { name: "Computer Networks" })).toBeInTheDocument();
  });
});
