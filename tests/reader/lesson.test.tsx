import { cleanup, render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it } from "vitest";
import { ProgressProvider } from "@/app/progress/ProgressContext";
import { AppRoutes } from "@/app/routes";
import { createMemoryStorage } from "@/progress/storage";
import { describeTcpTimelineStep, tcpTimelineScenarios } from "@/visuals/models/tcpTimeline";

function renderAt(path: string) {
  return render(
    <ProgressProvider storage={createMemoryStorage()}>
      <MemoryRouter initialEntries={[path]}>
        <AppRoutes />
      </MemoryRouter>
    </ProgressProvider>,
  );
}

describe("lesson renderer", () => {
  it("renders a complete Module 1 lesson with checks and finish", async () => {
    const user = userEvent.setup();
    renderAt("/cn/learn/foundations/what-a-network-must-do");
    expect(screen.getByRole("heading", { name: "What a network must do" })).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "First principles" })).toBeInTheDocument();
    expect(screen.getByText(/Two processes on one Linux machine/)).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Sources" })).toBeInTheDocument();

    const check = screen.getByText(/No device on the path copies packets/);
    const item = check.closest("li");
    expect(item).not.toBeNull();
    await user.click(
      within(item as HTMLElement).getByRole("button", { name: "Reveal model answer" }),
    );
    expect(screen.getByText(/The sender's packet arrived/)).toBeInTheDocument();
    await user.click(within(item as HTMLElement).getByRole("button", { name: "Knew it" }));

    await user.click(screen.getByRole("button", { name: "Mark lesson finished" }));
    expect(screen.getByText("Finished")).toBeInTheDocument();
  });

  it("shows a written why answer from Module 1", async () => {
    renderAt("/cn/why/why-layers");
    expect(
      screen.getByRole("heading", {
        name: "Why is TCP/IP divided into layers instead of using one protocol for everything?",
      }),
    ).toBeInTheDocument();
    expect(screen.getByText(/The jobs need different knowledge/)).toBeInTheDocument();
    expect(
      screen.getByRole("link", { name: /Primary lesson: Why networks are layered/ }),
    ).toBeInTheDocument();
  });

  it("embeds a Module 2 why answer in its primary lesson", () => {
    renderAt("/cn/learn/ethernet/arp");
    expect(screen.getByRole("heading", { name: "ARP and the ARP cache" })).toBeInTheDocument();
    expect(
      screen.getByRole("link", {
        name: "32. Why do we need ARP if every device already has an IP address?",
      }),
    ).toBeInTheDocument();
    expect(
      screen.getByText(/nothing in a 32-bit IPv4 address determines that MAC/),
    ).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Mark lesson finished" })).toBeInTheDocument();
  });

  it("embeds Module 3 why answers and its worked example", () => {
    renderAt("/cn/learn/addressing/gateway-and-longest-prefix");
    expect(
      screen.getByRole("heading", { name: "Default gateways and longest-prefix matching" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("link", {
        name: "33. Why does a host send traffic to its default gateway instead of directly to a remote server's MAC address?",
      }),
    ).toBeInTheDocument();
    expect(screen.getByText(/Routing tables overlap on purpose/)).toBeInTheDocument();
    expect(screen.getByText("Picking a route on a laptop with a VPN")).toBeInTheDocument();
  });

  it("steps the TCP timeline in the handshake lesson", async () => {
    const user = userEvent.setup();
    renderAt("/cn/learn/tcp/handshake");
    expect(screen.getByRole("heading", { name: "Establishing a connection" })).toBeInTheDocument();
    expect(
      screen.getByRole("link", {
        name: "11. Why does TCP use a three-way handshake rather than a two-way handshake?",
      }),
    ).toBeInTheDocument();

    const scenario = tcpTimelineScenarios["handshake-basic"];
    if (!scenario) throw new Error("missing handshake-basic");
    const group = screen.getByRole("group", { name: scenario.title });
    const view = within(group);
    const description = view.getByTestId("tcp-timeline-description");
    expect(description).toHaveTextContent(describeTcpTimelineStep(scenario, 0));
    expect(view.getByRole("button", { name: "Previous step" })).toBeDisabled();

    await user.click(view.getByRole("button", { name: "Next step" }));
    expect(description).toHaveTextContent(describeTcpTimelineStep(scenario, 1));
    expect(view.getByText("SYN seq=100")).toBeInTheDocument();

    await user.click(view.getByRole("button", { name: "Next step" }));
    expect(description).toHaveTextContent("SYN, ACK seq=300 ack=101");
    expect(view.getByText(/Server \(SYN-RECEIVED\) knows/)).toBeInTheDocument();

    await user.selectOptions(view.getByLabelText("Jump to step"), "3");
    expect(description).toHaveTextContent(describeTcpTimelineStep(scenario, 3));
    expect(view.getByText(/Server \(ESTABLISHED\) knows/)).toBeInTheDocument();

    const last = scenario.steps.length - 1;
    await user.selectOptions(view.getByLabelText("Jump to step"), String(last));
    expect(view.getByRole("button", { name: "Next step" })).toBeDisabled();
    expect(screen.getByRole("button", { name: "Mark lesson finished" })).toBeInTheDocument();
  });

  it("marks retransmissions and shows the FIN and RST timelines in termination", () => {
    renderAt("/cn/learn/tcp/termination");
    expect(
      screen.getByRole("group", { name: tcpTimelineScenarios["teardown-fin"]?.title }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("group", { name: tcpTimelineScenarios["teardown-rst"]?.title }),
    ).toBeInTheDocument();
    expect(screen.getByText(/TIME_WAIT keeps the side that closed first/)).toBeInTheDocument();
    cleanup();
    renderAt("/cn/learn/tcp/reliability");
    expect(screen.getByText(/TCP promises to deliver bytes in order/)).toBeInTheDocument();
  });

  it("embeds Module 4 why answers in their primary lessons", () => {
    renderAt("/cn/learn/routing/routing-vs-forwarding");
    expect(screen.getByRole("heading", { name: "Routing versus forwarding" })).toBeInTheDocument();
    expect(
      screen.getByRole("link", {
        name: "36. Why do we need both routing protocols and forwarding tables?",
      }),
    ).toBeInTheDocument();
    expect(
      screen.getByText(/They answer the same question at different speeds/),
    ).toBeInTheDocument();
    cleanup();
    renderAt("/cn/learn/routing/icmp");
    expect(
      screen.getByRole("link", {
        name: "52. Why can traceroute show a path that is not the path your TCP connection used?",
      }),
    ).toBeInTheDocument();
    expect(screen.getByText(/Traceroute does not watch your connection/)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Mark lesson finished" })).toBeInTheDocument();
  });

  it("embeds the Module 6 why answer and its worked example", () => {
    renderAt("/cn/learn/udp/udp-use-cases");
    expect(screen.getByRole("heading", { name: "When UDP is the right tool" })).toBeInTheDocument();
    expect(
      screen.getByRole("link", {
        name: "18. Why might UDP be preferable to TCP for real-time communication?",
      }),
    ).toBeInTheDocument();
    expect(screen.getByText(/Real-time data has a deadline/)).toBeInTheDocument();
    expect(screen.getByText("One lost voice packet, over TCP and over UDP")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Mark lesson finished" })).toBeInTheDocument();
    cleanup();
    renderAt("/cn/learn/udp/quic-and-tradeoffs");
    expect(screen.getByRole("heading", { name: "QUIC, and TCP versus UDP" })).toBeInTheDocument();
    expect(screen.getByText("One lost packet, three streams")).toBeInTheDocument();
  });

  it("embeds the Module 7 why answers, worked examples, and failure investigation", () => {
    renderAt("/cn/learn/dns/hierarchy");
    expect(screen.getByRole("heading", { name: "The DNS hierarchy" })).toBeInTheDocument();
    expect(
      screen.getByRole("link", {
        name: "21. Why do we need DNS if IP addresses uniquely identify network interfaces?",
      }),
    ).toBeInTheDocument();
    expect(screen.getByText(/An IP address says where an interface is/)).toBeInTheDocument();
    cleanup();
    renderAt("/cn/learn/dns/resolution");
    expect(
      screen.getByText("A cold lookup of `shop.example.com`, from `getaddrinfo()` to the answer"),
    ).toBeInTheDocument();
    cleanup();
    renderAt("/cn/learn/dns/caching");
    expect(
      screen.getByRole("link", {
        name: "22. Why does DNS use caching, and what trade-offs does caching introduce?",
      }),
    ).toBeInTheDocument();
    expect(screen.getByText(/Without caching, every lookup would walk/)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Mark lesson finished" })).toBeInTheDocument();
    cleanup();
    renderAt("/cn/learn/dns/transport-and-failures");
    expect(screen.getByRole("heading", { name: "Investigation" })).toBeInTheDocument();
    expect(screen.getByText("dig +cd api.example.com")).toBeInTheDocument();
  });

  it("embeds the Module 8 why answers and keeps notes-style bullets", () => {
    renderAt("/cn/learn/http/request-response");
    expect(
      screen.getByRole("heading", { name: "The HTTP request and response" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("link", { name: "1. If TCP already exists, why do we need HTTP?" }),
    ).toBeInTheDocument();
    expect(screen.getByText(/TCP delivers an ordered byte stream/)).toBeInTheDocument();
    expect(screen.getByText(/• One self-describing message format/)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Mark lesson finished" })).toBeInTheDocument();
    cleanup();
    renderAt("/cn/learn/http/http-versions");
    expect(
      screen.getByRole("link", {
        name: "28. Why can HTTP/2 still experience head-of-line blocking?",
      }),
    ).toBeInTheDocument();
    expect(screen.getByText("Six requests to one server, three ways")).toBeInTheDocument();
    cleanup();
    renderAt("/cn/learn/http/reuse-and-caching");
    expect(screen.getByText(/A new connection costs a TCP handshake/)).toBeInTheDocument();
    cleanup();
    renderAt("/cn/learn/http/intermediaries");
    expect(
      screen.getByRole("link", {
        name: "54. Why does gRPC use HTTP/2 (or HTTP/3) rather than a raw TCP byte stream?",
      }),
    ).toBeInTheDocument();
    expect(
      screen.getByText("A gRPC service that stays unbalanced after scaling out"),
    ).toBeInTheDocument();
  });

  it("embeds the Module 10 why answers, examples, and edge investigation", () => {
    renderAt("/cn/learn/nat/nat");
    expect(
      screen.getByRole("link", {
        name: "37. Why does NAT exist, and what problems does it introduce?",
      }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("link", { name: "38. Why isn't NAT itself a security mechanism?" }),
    ).toBeInTheDocument();
    expect(screen.getByText(/NAT rewrites addresses; it does not decide/)).toBeInTheDocument();
    expect(screen.getByText("One HTTPS connection through a home NAT")).toBeInTheDocument();
    cleanup();
    renderAt("/cn/learn/nat/load-balancing");
    expect(screen.getByText(/A health check is one cheap probe/)).toBeInTheDocument();
    expect(screen.getByText("How long a dead backend keeps getting traffic")).toBeInTheDocument();
    cleanup();
    renderAt("/cn/learn/nat/connectivity-failures");
    expect(screen.getByRole("heading", { name: "Investigation" })).toBeInTheDocument();
    expect(screen.getByText("conntrack -L -d 198.51.100.10")).toBeInTheDocument();
  });
});
