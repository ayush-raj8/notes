# Chapter 49 — Memento

## What and why

**Memento** = capture an object's internal state so it can be restored later, without exposing that state to outsiders. Exams ask for the three roles (Originator, Memento, Caretaker), why the memento must be opaque, and the shallow-copy trap when the state holds collections.

## Problem

An editor needs undo. If the history stores the live `StringBuilder`, later edits corrupt every saved "snapshot". The originator must copy its state into a memento; the caretaker stores mementos and never reads their fields.

## Exam flash rules

- Roles: **Originator** (creates/restores), **Memento** (opaque snapshot), **Caretaker** (stores mementos, never peeks).
- The caretaker holds mementos as an opaque type; only the originator reads the fields.
- Narrow access: private nested memento fields + private constructor keep outsiders from forging or inspecting state.
- A memento that stores a **reference** to a mutable collection is a **shallow** snapshot — later mutations show up in the "past".
- Correct snapshot of a list: `List.copyOf(live)` (or a deep copy of each element if elements are mutable).
- `Collections.unmodifiableList(live)` is a **view**, not a copy: it tracks later adds to `live` and rejects its own `add`.
- A memento can be restored **more than once**; it is not consumed by `restore`.
- Bound caretaker memory: drop the oldest entry when the stack exceeds a limit (`removeLast` on a deque used as a stack).
- Undo by memento stores state; undo by Command (ch 42) stores the inverse action — different trade-offs.
- `transient` fields are **not** written by Java serialization; they come back as defaults (`0` / `null`).
- `static` fields are not part of an instance's serialized state.
- `ObjectOutputStream` / `ObjectInputStream` constructors and `writeObject` / `readObject` throw checked `IOException`.
- A non-static inner class needs an outer instance: `new Outer().new Inner()`, not `new Inner()` from `static` context.
- Record components are `final`; you cannot assign `s.text = …` after construction.
- JDK: `Matcher.toMatchResult()` freezes match state; `ArrayList.clone()` is a shallow list copy.

## Rules table

| Case | Result | Compiler message |
|------|--------|------------------|
| Caretaker reads `m.text` (private field) | fails | `text has private access in Memento` |
| `new Editor.Memento(...)` outside `Editor` | fails | `Memento(String) has private access in Memento` |
| `ObjectOutputStream` without handling `IOException` | fails | `unreported exception IOException; must be caught or declared to be thrown` |
| `restore` assigns a `final` field | fails | `cannot assign a value to final variable text` |
| `new Memento(...)` for a non-static inner class from `static` | fails | `non-static variable this cannot be referenced from a static context` |
| Assign a record component | fails | `cannot assign a value to final variable text` |

## Walkthrough

### Originator and opaque memento

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/Editor.java:editor}}
```

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/History.java:history}}
```

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/EditorDemo.java:editor-demo}}
```

```text
{{#include ../../code/part-03-patterns/output/EditorDemo.txt}}
```

`History` only calls `push` / `pop`; it never reads `Memento` fields. `Editor.restore` does.

### Shallow vs deep snapshot

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/Doc.java:doc}}
```

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/DocDemo.java:doc-demo}}
```

```text
{{#include ../../code/part-03-patterns/output/DocDemo.txt}}
```

`saveShallow` aliases the live list, so `add("c")` appears in the shallow snap. `saveDeep` keeps `[a, b]`.

### Real I/O — file as memento

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/GameState.java:game-state}}
```

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/GameSaver.java:game-saver}}
```

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/GameDemo.java:game-demo}}
```

```text
{{#include ../../code/part-03-patterns/output/GameDemo.txt}}
```

`sessionToken` is `transient` → `null` after load. The file bytes are the memento; the path is the caretaker's handle.

### Does not compile

<div class="compiler-error">

```java
{{#include ../../code/part-03-patterns/broken/MementoPrivateFieldBroken.java}}
```

```text
{{#include ../../code/part-03-patterns/output/MementoPrivateFieldBroken.txt}}
```

```text
{{#include ../../code/part-03-patterns/output/MementoPrivateCtorBroken.txt}}
```

```text
{{#include ../../code/part-03-patterns/output/MementoUncheckedIoBroken.txt}}
```

```text
{{#include ../../code/part-03-patterns/output/MementoFinalRestoreBroken.txt}}
```

```text
{{#include ../../code/part-03-patterns/output/MementoInnerStaticBroken.txt}}
```

```text
{{#include ../../code/part-03-patterns/output/MementoRecordMutateBroken.txt}}
```

</div>

Sources: `broken/MementoPrivateFieldBroken.java`, `MementoPrivateCtorBroken.java`, `MementoUncheckedIoBroken.java`, `MementoFinalRestoreBroken.java`, `MementoInnerStaticBroken.java`, `MementoRecordMutateBroken.java`.

## Memento vs similar patterns

| Pattern | Stores | Who restores | Typical use |
|---------|--------|--------------|-------------|
| **Memento** | a state snapshot | the originator | undo, checkpoints, save games |
| **Command** (ch 42) | a request (+ inverse) | the command's `undo()` | undo by inverse action |
| **Prototype** (ch 32) | a clone of an object | the clone is a new object | copy for reuse, not history |
| **State** (ch 43) | current behaviour object | context swaps states | change behaviour, not restore past |
| **Snapshot** (Iterator, ch 46) | a frozen view of a collection | reader of the snapshot | fail-safe iteration |

Command undo needs a true inverse; Memento undo works for any change but costs memory proportional to state size.

## JDK usage

| API | Memento role |
|-----|--------------|
| `Matcher.toMatchResult()` | freezes the current match as a `MatchResult` |
| `ArrayList.clone()` / `Object.clone()` | shallow snapshot of a list / object |
| `List.copyOf` | immutable deep-enough copy of a list of immutables |
| Java serialization (`ObjectOutputStream`) | externalised memento (bytes / file) |
| `java.util.Properties.store` / `load` | text-file checkpoint (see exercise 4) |

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/JdkMementoDemo.java:jdk-demo}}
```

```text
{{#include ../../code/part-03-patterns/output/JdkMementoDemo.txt}}
```

`first` stayed at `"1"` while the matcher moved on. The clone shares the `StringBuilder` element (`x!`) but not later list adds (`new`).

## Predict the output

**P1.** Undo stack restores earlier text and cursor.

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/exam/PredictUndoStack.java:predict-undo-stack}}
```

<details><summary>Answer</summary>

```text
{{#include ../../code/part-03-patterns/output/PredictUndoStack.txt}}
```

Trace: after `c`, pop restores `"ab"` (cursor 2); next pop restores `"a"` (cursor 1); stack empty → `false`; text stays `"a"`.

</details>

**P2.** Same memento restored twice; another originator can use it.

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/exam/PredictRestoreTwice.java:predict-restore-twice}}
```

<details><summary>Answer</summary>

```text
{{#include ../../code/part-03-patterns/output/PredictRestoreTwice.txt}}
```

Trace: `"one"` + `"X"` → `oneX`; restore → `"one"` cursor 3; second editor restores the same opaque memento; `toString` is `"Memento"` (no leaked fields).

</details>

**P3.** Shallow vs deep list snapshot.

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/exam/PredictDocShallow.java:predict-doc-shallow}}
```

<details><summary>Answer</summary>

```text
{{#include ../../code/part-03-patterns/output/PredictDocShallow.txt}}
```

Trace: shallow aliases live → `[a, b]`; deep is `[a]`. Restoring deep then `add("z")` mutates a **new** list; deep snap stays `[a]`.

</details>

**P4.** View vs copy.

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/exam/PredictViewVsCopy.java:predict-view-vs-copy}}
```

<details><summary>Answer</summary>

```text
{{#include ../../code/part-03-patterns/output/PredictViewVsCopy.txt}}
```

Trace: `unmodifiableList` tracks `live` → `[a, b]`; `List.copyOf` and `new ArrayList<>(live)` stay `[a]`. Both view and copy reject `add`.

</details>

**P5.** Serialization skips `transient` and `static`.

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/exam/PredictTransient.java:predict-transient}}
```

<details><summary>Answer</summary>

```text
{{#include ../../code/part-03-patterns/output/PredictTransient.txt}}
```

Trace: `kept=10` restored; `skipped` / `label` → defaults `0` / `null`; `finalField` is written; `created` is static so stays `999`; new object → `false`.

</details>

**P6.** Bounded history drops the oldest.

```java
{{#include ../../code/part-03-patterns/src/main/java/patterns/behavioral/memento/exam/PredictBoundedHistory.java:predict-bounded}}
```

<details><summary>Answer</summary>

```text
{{#include ../../code/part-03-patterns/output/PredictBoundedHistory.txt}}
```

Trace: limit 2 → after typing `1`,`2`,`3` size is 2 (empty and `"1"` snaps dropped the empty). Pop → `"12"`; pop → `"1"`; third pop is empty, text stays `"1"`.

</details>

## Compile or not?

| # | Source | Answer | Reason |
|---|--------|--------|--------|
| C1 | `MementoPrivateFieldBroken` | **fails** | private field of nested `Memento` |
| C2 | `MementoPrivateCtorBroken` | **fails** | private constructor; cannot forge |
| C3 | `MementoUncheckedIoBroken` — looks fine | **fails** | `IOException` not handled |
| C4 | `MementoFinalRestoreBroken` | **fails** | cannot assign `final` field |
| C5 | `MementoInnerStaticBroken` — looks fine | **fails** | non-static inner needs outer instance |
| C6 | `MementoRecordMutateBroken` — looks fine | **fails** | record components are `final` |

## Gotchas

1. Storing a live collection reference in the memento: `Doc.saveShallow`, `PredictDocShallow`.
2. Confusing `unmodifiableList` (view) with `List.copyOf` (copy): `PredictViewVsCopy`.
3. Caretaker peeking into memento fields breaks encapsulation: `MementoPrivateFieldBroken`.
4. `transient` / `static` surprise after deserialize: `PredictTransient`, `GameDemo`.
5. Unbounded undo history: use a limit — `History`, `PredictBoundedHistory`.

## UML

```mermaid
classDiagram
  class Editor {
    -StringBuilder text
    -int cursor
    +save() Memento
    +restore(Memento)
  }
  class Memento {
    -String text
    -int cursor
  }
  class History {
    -Deque~Memento~ stack
    -int limit
    +push(Memento)
    +pop() Optional~Memento~
  }
  class Doc {
    +saveShallow() Snap
    +saveDeep() Snap
    +restore(Snap)
  }
  class Snap {
    <<record>>
    +List~String~ lines
  }
  class GameState
  class GameSaver {
    +save(GameState, Path)$
    +load(Path)$ GameState
  }
  Editor +-- Memento : nested
  History o--> Memento
  Doc ..> Snap
  GameSaver ..> GameState
```

Diagram → code: `History` → `Memento` is association only; the nested class link `Editor +-- Memento` is where restore logic lives.

## Interview Q&A

1. **Q:** What are the Memento roles? **A:** Originator, Memento, Caretaker — `Editor`, `Editor.Memento`, `History`.
2. **Q:** Why opaque? **A:** So the caretaker cannot depend on (or mutate) internal fields — `MementoPrivateFieldBroken`.
3. **Q:** Memento vs Command for undo? **A:** Memento stores state; Command stores an inverse action — ch 42 vs this chapter.
4. **Q:** Shallow vs deep? **A:** Shallow aliases mutable structure; deep copies it — `DocDemo`.
5. **Q:** How to hide memento construction? **A:** Private nested class + private constructor — `Editor.Memento`, `MementoPrivateCtorBroken`.
6. **Q:** Can one memento restore many times? **A:** Yes — `PredictRestoreTwice`.
7. **Q:** How to bound memory? **A:** Drop oldest when over limit — `History`, `PredictBoundedHistory`.
8. **Q:** What does `transient` do? **A:** Skips the field in serialization — `PredictTransient`, `GameState.sessionToken`.
9. **Q:** JDK examples? **A:** `Matcher.toMatchResult()`, `ArrayList.clone()` — `JdkMementoDemo`.
10. **Q:** Is `unmodifiableList` a memento? **A:** No — it is a live view; use `List.copyOf` — `PredictViewVsCopy`.
11. **Q:** File as memento? **A:** Serialize state to a path; load returns a new object — `GameSaver`.
12. **Q:** Why prefer a static nested memento? **A:** A non-static inner needs an outer instance and holds a hidden reference — `MementoInnerStaticBroken`.

## Exam writing prompts

1. (5) Write an `Originator` with `save()` / `restore(Memento)` for a single `int` counter; the memento type may be `Integer`.
2. (8) Write `Editor` with private nested `Memento` (text + cursor) and a `History` stack with a size limit.
3. (5) Explain with code why `new Snap(liveList)` is wrong and how `List.copyOf` fixes it.
4. (3) State two differences between Memento undo and Command undo.

## Exercises

1. Counter with push-before-change undo — `solutions/ch49/SolutionCounterUndo.java`.
2. Deep snapshot of a list of mutable cells — `solutions/ch49/SolutionDeepSnapshot.java`.
3. Undo **and** redo with mementos; new edit clears redo — `solutions/ch49/SolutionUndoRedo.java`.
4. Text-file checkpoint via `Properties` — `solutions/ch49/SolutionPropertiesCheckpoint.java`.
5. Deep-copy graph through serialization — `solutions/ch49/SolutionSerializedCopy.java`.

```text
{{#include ../../code/part-03-patterns/output/SolutionCounterUndo.txt}}
```

```text
{{#include ../../code/part-03-patterns/output/SolutionDeepSnapshot.txt}}
```

```text
{{#include ../../code/part-03-patterns/output/SolutionUndoRedo.txt}}
```

```text
{{#include ../../code/part-03-patterns/output/SolutionPropertiesCheckpoint.txt}}
```

```text
{{#include ../../code/part-03-patterns/output/SolutionSerializedCopy.txt}}
```

## Recap checklist

- [x] Problem, roles, UML
- [x] Opaque nested memento, shallow vs deep, file serialization, JDK demos
- [x] vs-similar table and JDK usage
- [x] Exam flash rules, predict (6), compile-or-not (6), writing prompts (4)
- [x] Gotchas, Q&A, exercises with solutions
