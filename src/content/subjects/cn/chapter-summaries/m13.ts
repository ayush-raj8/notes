import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m13",
  status: "complete",
  readingMinutes: { min: 14, max: 20 },
  thesis:
    "Troubleshooting is eliminating layers in a fixed order, name, TCP, TLS, HTTP, then app, so each command rules one cause in or out.",
  outcomes: [
    "Apply the elimination order name → TCP → TLS → HTTP → app to any failing request.",
    "For each of the 12 canonical scenarios, name likely causes and the first commands to run.",
    "Read command output as evidence for or against a hypothesis, not as a verdict.",
    "Name the common mistake in each scenario that sends the investigation the wrong way.",
  ],
  coverage: [
    {
      bullet: "DNS resolves, but TCP connection establishment fails.",
      notes:
        "• Causes: nothing listening (RST), firewall or security group dropping SYN (timeout), wrong port, wrong IP from stale or split-horizon DNS, no route.\n• First: `dig +short name`, then `curl -v --connect-timeout 5 http://ip:port`, `ss -ltn` on the server, `tcpdump 'tcp[tcpflags] & tcp-syn != 0'`.\n• Read: RST fast = port closed; silence = filtered or dropped; SYN-ACK but no ACK = return path problem.\n• Common mistake: trusting `ping` success. ICMP allowed says nothing about the TCP port.",
    },
    {
      bullet: "TCP connects, but TLS negotiation fails.",
      notes:
        "• Causes: expired or wrong-name certificate, missing intermediate, no common protocol or cipher, SNI missing or wrong, client lacks the CA, clock skew, plaintext on a TLS port.\n• First: `curl -v https://host`, `openssl s_client -connect ip:443 -servername host -showcerts`, check dates and SAN.\n• Read: the alert in the output (certificate unknown, handshake failure, protocol version) names the class.\n• Common mistake: connecting by IP without SNI and testing a different (default) certificate than clients get.",
    },
    {
      bullet: "TLS succeeds, but HTTP requests return 502 or 503.",
      notes:
        "• Causes: proxy cannot reach or reset by the backend (502), no healthy backend or overload (503), backend returned garbage, upstream timeout, wrong upstream port or scheme.\n• First: `curl -v` for the headers and which server answered, proxy error log, `ss -ltn` and health state on the backend, `curl` the backend directly.\n• Read: the 502/503 comes from the proxy, so the fault sits behind it or in the proxy-to-backend hop.\n• Common mistake: debugging TLS or DNS again, when the client-side path is already proven working.",
    },
    {
      bullet: "Requests intermittently time out.",
      notes:
        "• Causes: one bad backend in a pool, packet loss or retransmission, conntrack or NAT drops, DNS returning a dead record, saturated queue, rate limiting.\n• First: loop `curl -w '%{time_connect} %{time_starttransfer} %{time_total}\\n'`, `ss -ti` for retrans, `ping -c 100` for loss, per-backend error rate, `tcpdump` during a failure.\n• Read: which phase stalls and whether failures cluster on one IP or one time window.\n• Common mistake: testing once and calling it healthy. Intermittent needs counts across many requests and each backend.",
    },
    {
      bullet: "Latency increases while CPU usage stays low.",
      notes:
        "• Causes: waiting on a downstream call, lock or pool wait, connection pool exhausted, disk or network I/O, packet loss and retransmission, queueing at a proxy.\n• First: phase timings with `curl -w`, `ss -ti` (rtt, retrans), `ss -tn` Send-Q/Recv-Q, app traces or pool metrics, `tcpdump` for the time before the first byte.\n• Read: time to first byte up with fast connect points at the app or its dependencies; slow connect points at the network.\n• Common mistake: treating low CPU as proof the server is fine. Blocked waiting uses no CPU.",
    },
    {
      bullet: "A service works on localhost but not from another machine.",
      notes:
        "• Causes: bound to 127.0.0.1 instead of 0.0.0.0, host firewall, security group, wrong interface or route, container port not published.\n• First: `ss -ltn` (look at the local address column), `ip addr`, `curl` to the host's own non-loopback IP, then from the remote host `curl -v` and `nc -vz`.\n• Read: listener on `127.0.0.1:port` explains it at once; listening on `0.0.0.0` moves the search to the firewall and path.\n• Common mistake: testing only from the server itself, where loopback always works.",
    },
    {
      bullet: "A server has thousands of connections and stops accepting new ones.",
      notes:
        "• Causes: FD limit (EMFILE), accept backlog full, conntrack table full, worker or thread pool exhausted, ephemeral port exhaustion on a client or proxy, slow clients holding connections.\n• First: `ss -s`, `ss -ltn` (Recv-Q vs Send-Q on the listener), `ulimit -n` and `/proc/<pid>/limits`, `dmesg | grep conntrack`, count states with `ss -tan | awk '{print $1}' | sort | uniq -c`.\n• Read: many ESTABLISHED = leak or slow clients; many TIME_WAIT = churn; full listener queue = slow accept loop.\n• Common mistake: restarting the service. It clears the symptom and hides the leak or limit.",
    },
    {
      bullet: "A client can reach an IP address but not a hostname.",
      notes:
        "• Causes: resolver down or wrong in `/etc/resolv.conf`, search domain or ndots effects, split-horizon DNS, stale cache, IPv6 AAAA record unreachable, missing record, /etc/hosts override.\n• First: `dig name`, `dig @8.8.8.8 name` vs the configured resolver, `getent hosts name`, `curl -v --resolve name:443:ip`.\n• Read: `dig` and the system resolver disagreeing points at local config or NSS; AAAA answered but IPv6 path dead gives long delays.\n• Common mistake: testing with `dig` only. Applications use the system resolver path (`getent`), which can differ.",
    },
    {
      bullet: "A connection is repeatedly reset.",
      notes:
        "• Causes: nothing listening, server app crash or abort, proxy or LB idle timeout closing, firewall injecting RST, stale NAT mapping, TLS or protocol mismatch, receive buffer closed with unread data.\n• First: `tcpdump -nn 'tcp[tcpflags] & tcp-rst != 0'` and find who sent it, `ss -ti`, app and proxy logs at the reset time, compare idle time with timeouts.\n• Read: the sender of the RST and the time since the last data identify the culprit; TTL of the RST can reveal a middlebox.\n• Common mistake: blaming the endpoint that reports the error, which is only the receiver of the RST.",
    },
    {
      bullet: "Small requests work, but large responses hang.",
      notes:
        "• Causes: MTU / PMTUD black hole (ICMP fragmentation-needed filtered), tunnel or VPN overhead, MSS not clamped, broken offload, window or buffer stall.\n• First: `ping -M do -s 1472 host` and step the size down, `tracepath host`, `tcpdump` for repeated retransmits of full-size segments, `ip link` for MTU.\n• Read: handshake and small replies succeed; full-size segments are retransmitted forever and never ACKed.\n• Common mistake: blaming the application or server load, when the stall begins at exactly the packet size that does not fit.",
    },
    {
      bullet: "A service experiences periodic latency spikes.",
      notes:
        "• Causes: GC or batch jobs, cron or backup traffic, DNS TTL expiry causing re-resolution, retransmission timeouts, connection-pool recycling or reconnect storms, TCP keepalive or idle expiry, noisy neighbors.\n• First: line up the spike period with a timeline (cron, TTL, max-connection-age), graph p99 per phase, `curl -w` loop, `ss -ti` for retrans, `tcpdump` across a spike.\n• Read: a fixed period points at a scheduled cause; the stalled phase identifies the layer.\n• Common mistake: looking at averages. Spikes disappear in the mean and appear in p99.",
    },
    {
      bullet: "One microservice cannot reach another despite both being healthy locally.",
      notes:
        "• Causes: network policy or security group between them, wrong service name or port, DNS or service-discovery mismatch, mTLS or cert trust failure, sidecar/proxy config, different network or namespace, bound to loopback.\n• First: from the caller's own environment (not your laptop): `dig svc`, `curl -v http://svc:port`, `ss -ltn` on the target, then `tcpdump` at both ends and compare.\n• Read: SYN seen at the target but no reply means the target or host firewall; not seen means the path or policy.\n• Common mistake: testing from a different network position than the caller, so the result tells you nothing about the real path.",
    },
  ],
  mentalModel:
    "• Elimination order: name → TCP → TLS → HTTP → app. Do not skip a layer, and do not move on until the one below is proven.\n• Name: does the name resolve to the address the caller will use (`dig`, `getent`)?\n• TCP: can a connection open to that IP:port from the caller's vantage point (`curl -v`, `ss`, `tcpdump`)? RST, timeout, and success are three different answers.\n• TLS: does the handshake complete with the right name and a trusted chain (`openssl s_client`)?\n• HTTP: does the server or proxy return the right status (`curl -v`)? Which hop produced it?\n• App: only then look at logs, pools, dependencies, and CPU.\n• Every step produces evidence: record what you saw and which hypotheses it eliminates.",
  failuresAndSignals: [
    "Wrong vantage point: the test passes from your laptop but fails for the real caller. Signal is that the failing client's own `curl -v` and capture disagree with yours; always test from the failing environment.",
    "Layer skipped: hours spent on app logs while DNS returns a stale record. Signal is a hostname that fails and the same IP that works; the order name → TCP → TLS → HTTP → app would have shown it in minutes.",
    "Silence vs refusal confused: timeout treated as 'port closed'. Signal is no RST in the capture (firewall drop) rather than an immediate RST (nothing listening).",
    "Average-based diagnosis of an intermittent fault: dashboards look green while p99 and one backend fail. Signal is per-backend and per-phase data that the mean hides.",
    "Restarting to fix: connection leaks, FD limits, and backlog overflow clear temporarily and return. Signal is the same state counts (`ss -s`) growing again after the restart.",
  ],
  whyAnchors: ["why.45", "why.50", "why.55"],
  connections: {
    before:
      "• Modules 1–12: each scenario reuses a mechanism from earlier modules: DNS (Module 7), TCP (Module 5), TLS (Module 9), proxies (Module 10), performance (Module 11), and sockets and tools (Module 12).",
    after:
      "• End of the course path: apply the same elimination order to production incidents, then return to the owning module for depth on the cause you find.",
  },
  selfCheck: [
    {
      id: "order",
      prompt:
        "A request to https://svc fails. State the order of checks and the single command for each step.",
      answer:
        "Name: `dig svc`. TCP: `curl -v --connect-timeout 5 https://svc` or `nc -vz ip 443`. TLS: `openssl s_client -connect ip:443 -servername svc`. HTTP: `curl -v` and read the status and headers. App: logs, pool and dependency metrics. Stop at the first layer that fails.",
    },
    {
      id: "localhost",
      prompt:
        "A service works with `curl localhost:8080` but not from another host. What is the first command and what are you reading?",
      answer:
        "`ss -ltn` on the server; read the local address. `127.0.0.1:8080` means it is bound to loopback only. If it shows `0.0.0.0` or the host IP, check the firewall, security group, or port publishing next.",
    },
    {
      id: "silence-vs-rst",
      prompt: "What does an immediate RST tell you compared with a connect timeout?",
      answer:
        "An immediate RST means the host answered and nothing is listening on that port. A timeout means SYNs got no answer: drop by a firewall, no route, or a dead host.",
    },
  ],
} satisfies ChapterSummary;
