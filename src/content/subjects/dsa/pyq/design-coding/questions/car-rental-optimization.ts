import type { DesignCodingQuestion } from "../../../schema";

export const carRentalOptimization: DesignCodingQuestion = {
  id: "dc.car-rental-optimization",
  slug: "car-rental-optimization",
  title: "Car Rental Optimization",
  classification: "hello-interview-guide",
  level: "L5",
  interviewFormat: "Coding (Hello Interview Google L5 guide)",
  priority: "important",
  verification: "verified",
  categories: ["scheduling-heaps", "intervals-range"],
  sources: [
    {
      label: "Hello Interview — Google L5 guide",
      url: "https://www.hellointerview.com/guides/google/l5",
      note: "Lists Car Rental Optimization among most commonly asked coding questions.",
    },
    {
      label: "Hello Interview community — Car Rental Optimization",
      url: "https://www.hellointerview.com/community/questions/rental-car-assignment/cm6jwxse800j9ui4biheeyqm2",
      note: "Assign cars to rental requests to minimize cars used; reuse when returnTime <= next pickupTime; heap of availability.",
    },
  ],
  coreApproach:
    "Sort requests by pickup time; min-heap of (returnTime, carId); reuse the earliest-free car when return ≤ pickup; else allocate a new car id.",
  summaryComplexity: "O(n log n) time · O(n) space",
  originalPrompt:
    "Given N cars and rental requests (pickupTime, returnTime, id), assign cars to maximize utilization while using the minimum number of cars. A car freed at time T may serve a pickup at T.",
  originalRequirements: [
    "Assign each request a carId.",
    "Minimize number of cars used (≤ N available).",
    "Non-overlapping reuse: return at T allows pickup at T.",
  ],
  clarifyingQuestions: [
    "What if more cars are needed than N?",
    "Can pickup equal return on the same car?",
    "Are request ids unique?",
  ],
  approach: {
    whyStructure:
      "Interval reuse is classic greedy + min-heap of end times. Sorting by start lets you always consider the earliest free resource.",
    invariant: "Heap contains busy cars ordered by return time. Assigned count never exceeds need.",
    operations: "Sort by pickup; while heap top return ≤ pickup, reuse that carId; else new car.",
    whyNotSimpler:
      "Assigning a fresh car each time wastes fleet size. Checking all cars linearly is O(n²).",
    tradeoffs: "Greedy earliest-available is optimal for minimizing machines for intervals.",
  },
  algorithm: [
    "Sort requests by pickup.",
    "Heap of (return, carId); next_car id counter.",
    "For each request: pop reusable cars; assign; push new return.",
  ],
  pseudocode: `sort requests by pickup
heap = []  # (return, carId)
next_id = 0
for req in requests:
  if heap and heap[0].return <= req.pickup:
    _, car = heappop(heap)
  else:
    car = next_id; next_id += 1
  assign[req.id] = car
  heappush(heap, (req.return, car))`,
  implementation: `from __future__ import annotations

import heapq
from typing import Any


def assign_cars(n: int, requests: list[dict[str, int]]) -> list[dict[str, int]]:
    ordered = sorted(requests, key=lambda r: r["pickup"])
    heap: list[tuple[int, int]] = []  # return_time, car_id
    next_car = 0
    out: dict[int, int] = {}
    for req in ordered:
        if heap and heap[0][0] <= req["pickup"]:
            _, car = heapq.heappop(heap)
        else:
            if next_car >= n:
                raise ValueError("need more than N cars")
            car = next_car
            next_car += 1
        out[req["id"]] = car
        heapq.heappush(heap, (req["return"], car))
    return [{"requestId": rid, "carId": out[rid]} for rid in out]


if __name__ == "__main__":
    res = assign_cars(
        3,
        [
            {"id": 1, "pickup": 0, "return": 5},
            {"id": 2, "pickup": 2, "return": 7},
            {"id": 3, "pickup": 5, "return": 9},
        ],
    )
    by = {r["requestId"]: r["carId"] for r in res}
    assert by[1] == by[3] and by[1] != by[2]
    print("ok")
`,
  complexity: [{ operation: "assign_cars", time: "O(n log n)", space: "O(n)" }],
  dryRun: {
    setup: "Requests (0,5), (2,7), (5,9) with N=3.",
    steps: ["Car0 → first; car1 → second.", "Third pickup 5 ≥ car0 return 5 → reuse car0."],
    result: "Two cars used; assignments match the HI example idea.",
  },
  edgeCases: [
    "Pickup == previous return.",
    "All overlapping → n cars.",
    "N too small.",
    "Empty requests.",
  ],
  followUps: [
    {
      text: "Return assignment list vs only minimum car count.",
      kind: "practice",
    },
  ],
  checklist: [
    "Hello Interview guide classification.",
    "Sort by pickup.",
    "Heap by return; ≤ for reuse.",
    "O(n log n).",
  ],
  relatedQuestionIds: ["rp.moving-average"],
};
