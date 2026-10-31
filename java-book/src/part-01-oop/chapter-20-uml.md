# Chapter 20 — UML

## What and why

UML is the drawing exam papers use for class structure and behaviour. This chapter maps each symbol to the exact Java it means, so you can **read a diagram and write the code**, or **read code and draw the diagram**.

Package: `oop.uml`. Diagram sources live in `java-book/diagrams/uml/` as a Mermaid file (`.mmd`) and a PlantUML file (`.puml`) per diagram. Tests read those files and check them against the Java (state transitions, call order, class members).

## Exam flash rules

- `+` public, `-` private, `#` protected, `~` package-private.
- Underlined member = `static`. Italic name or `{abstract}` = `abstract`. `<<interface>>` = `interface`.
- Mermaid writes static as a trailing `$` and abstract as a trailing `*`; generics as `Name~T~`.
- Generalization `--|>` (Mermaid `<|--`) = `extends`. Realization `..|>` (`<|..`) = `implements`.
- A class has **one** superclass and **any number** of interfaces.
- Dependency `..>` = used inside a method (parameter, local, return, static call). **No field.**
- Association `-->` = a **field** of the other type.
- Aggregation (hollow diamond) = whole–part, part **created outside**, can be shared and outlives the whole.
- Composition (filled diamond) = whole **creates and owns** the part; part ends with the whole.
- Strength order: dependency < association < aggregation < composition.
- The arrowhead shows **navigability**: the class at the tail holds a reference to the class at the head.
- Multiplicity: `1` final non-null field · `0..1` nullable / `Optional` · `*` or `0..*` collection · `1..*` non-empty collection · `n` fixed count.
- Sequence diagram: lifelines left to right, arrows top to bottom = calls in time order. `alt` = `if / else`, `loop` = loop.
- State diagram: states = `enum`, labelled arrows = transitions, `[*]` = start / end.
- Activity diagram: action = statement, diamond = `if`, fork / join = concurrent branches then wait for all.
- Use-case diagram: actor = role (interface), use case = operation (method); `<<include>>` always runs, `<<extend>>` runs only under a condition.

## Rules table — symbol to Java

| UML | Mermaid | Java |
|-----|---------|------|
| `+ name: Type` | `+name: Type` | `public Type name;` |
| `- name: Type` | `-name: Type` | `private Type name;` |
| `# name: Type` | `#name: Type` | `protected Type name;` |
| `~ name: Type` | `~name: Type` | `Type name;` (package-private) |
| underlined member | trailing `$` | `static` |
| italic class / `{abstract}` | `<<abstract>>`, trailing `*` | `abstract` |
| `<<interface>>` | `<<interface>>` | `interface` |
| `<<enumeration>>` | `<<enumeration>>` | `enum` |
| `{readOnly}` | — | `final` |
| `Repository<T>` | `Repository~T~` | `interface Repository<T>` |
| `A --|> B` | `B <\|-- A` | `class A extends B` |
| `A ..|> I` | `I <\|.. A` | `class A implements I` |
| `A ..> B` | `A ..> B` | `B` only in params / locals / returns of `A` |
| `A --> B` | `A --> B` | `private B b;` in `A` |
| `A o-- B` | `A o-- B` | `List<B>` / `B` field filled from outside |
| `A *-- B` | `A *-- B` | field created inside `A`; not exposed |

Java rules that diagrams quietly assume (each fails with a real message):

| Case | Compiler message |
|------|------------------|
| `new` on an abstract class | `Shape is abstract; cannot be instantiated` |
| `new` on an interface | `Stack is abstract; cannot be instantiated` |
| Class misses an interface method | `Employee is not abstract and does not override abstract method id() in Payable` |
| Abstract method with a body | `abstract methods cannot have a body` |
| Assign an interface constant | `cannot assign a value to static final variable MAX` |
| `-` member from another top-level class | `secret has private access in UmlPrivateOwner` |
| `extends A, B` | `'{' expected` |
| `List<int>` for `Repository~T~` | `unexpected type … required: reference found: int` |
| `protected` interface method | `modifier protected not allowed here` |
| Multiplicity `1` as a `final` field no constructor sets | `variable rooms not initialized in the default constructor` |
| Instance operation called on the class | `non-static method inc() cannot be referenced from a static context` |

## Walkthrough

### Class diagram notation

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/NotationDemo.java:notation-types}}
```

```text
ids: 1 2, created: 2
area: 3.14
empty: Optional.empty
first: Optional[x]
```

Mermaid, with the source file included so the diagram and the page cannot drift:

```mermaid
{{#include ../../diagrams/uml/class-notation.mmd}}
```

PlantUML (`diagrams/uml/class-notation.puml`):

```plantuml
{{#include ../../diagrams/uml/class-notation.puml}}
```

### Code → diagram, mechanically

`UmlDump` prints the member lines from the compiled class, using the same symbols:

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/UmlDump.java:uml-dump}}
```

```text
class Base {
  -created: int$
  -id: int
  #label: String
  ~tag: String
  +area() double*
  +created() int$
  +id() int
}
class Repository {
  <<interface>>
  +findFirst() Optional~T~
  +save(T) void
}
```

A test compares these lines with the `Base` block of `class-notation.mmd`. Constructors are left out of the dump; add them by hand if the question asks.

### Relationships at a glance

| Relationship | Mermaid | Reads as | Java shape | Lifetime test |
|--------------|---------|----------|-----------|---------------|
| Dependency | `A ..> B` | A uses B | B in a parameter, local, return or static call | none |
| Association | `A --> B` | A knows B | field `B b` | independent |
| Aggregation | `A o-- B` | A has B (shared) | field filled from outside | B survives A |
| Composition | `A *-- B` | A owns B | field built inside A | B ends with A |
| Generalization | `B <\|-- A` | A is a B | `extends` | — |
| Realization | `I <\|.. A` | A implements I | `implements` | — |

```mermaid
{{#include ../../diagrams/uml/relationships.mmd}}
```

```plantuml
{{#include ../../diagrams/uml/relationships.puml}}
```

### Association and navigability

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/AssociationDemo.java:association-uni}}
```

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/AssociationDemo.java:association-bi}}
```

```text
order -> customer: Asha
Order-typed fields in Customer: 0 (customer cannot navigate to orders)
author -> books: 1
book -> author: Rao
```

| Diagram | Navigable | Java |
|---------|-----------|------|
| `Order --> Customer` | order → customer | `Order` has a `Customer` field |
| `Order -- Customer` | unspecified | exam default: treat as both unless told |
| `Author <--> Book` | both | a field on each side, set together in one method |

### Aggregation

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/AggregationDemo.java:aggregation-demo}}
```

```text
club size after disband: 0
p1 still in country: true
p1 usable: Ira
```

### Composition

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/CompositionDemo.java:composition-demo}}
```

```text
rooms: [kitchen, hall]
Room kitchen closed
Room hall closed
rooms after close: []
multiplicity 1..* enforced: a house needs 1..* rooms
```

Composition recipe in Java: parts built inside the whole, constructor private or package-private, no getter that returns the part itself, and the whole releases the parts.

### Dependency

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/DependencyDemo.java:dependency-demo}}
```

```text
Q1 Q1
Formatter-typed fields: Report=0, Holder=1
```

### Generalization and realization

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/InheritRealize.java:inherit-realize}}
```

```text
Dog is an Animal: true, sound=woof
pop=b size=1
Stack realized: true, Sized realized: true
Swimmer extends: [Sized, Walker]
Dog superclass: Animal
```

An interface can extend several interfaces (`Swimmer --|> Walker`, `Swimmer --|> Sized`). A class extends one class.

### Multiplicity

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/MultiplicityDemo.java:multiplicity-demo}}
```

```text
lines=1, coupon present=false
1..* violated: at least one line (1..*)
1 violated: customer is mandatory (1)
fixed 4: 4
```

| Multiplicity | Java |
|--------------|------|
| `1` | `final` field, `Objects.requireNonNull` in the constructor |
| `0..1` | nullable field, `Optional` accessor |
| `*` / `0..*` | `List<B>` (may be empty) |
| `1..*` | `List<B>` + constructor check `isEmpty()` |
| `n` (fixed) | array of length `n` or `n` fields |

### Sequence diagram

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/SequenceDemo.java:sequence-demo}}
```

```text
success path -> true
Client -> OrderService: place(A1)
OrderService -> Inventory: reserve(A1)
OrderService -> Payment: charge(50)
OrderService -> Notifier: send(confirmed)
declined path -> false
Client -> OrderService: place(A1)
OrderService -> Inventory: reserve(A1)
OrderService -> Payment: charge(50)
OrderService -> Inventory: release(A1)
```

```mermaid
{{#include ../../diagrams/uml/sequence-order.mmd}}
```

```plantuml
{{#include ../../diagrams/uml/sequence-order.puml}}
```

The `alt` fragment has two branches; one run follows exactly one of them. A test checks that each logged run is a path through the diagram.

| Sequence element | Java |
|------------------|------|
| Lifeline | object / class |
| Solid arrow `->>` | method call |
| Dashed arrow `-->>` | return |
| `alt … else` | `if … else` |
| `loop` | `for` / `while` |
| `opt` | `if` without `else` |
| Activation bar | the call is on the stack |

### State diagram

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/StateDemo.java:state-demo}}
```

```text
NEW --PAY--> PAID
PAID --SHIP--> SHIPPED
SHIPPED --DELIVER--> DELIVERED
CANCEL not allowed in SHIPPED
```

```mermaid
{{#include ../../diagrams/uml/state-order.mmd}}
```

```plantuml
{{#include ../../diagrams/uml/state-order.puml}}
```

A test reads every `X --> Y : EVENT` line from `state-order.mmd`, asserts `next(X, EVENT) == Y`, and asserts that **every pair not drawn** throws `IllegalStateException`.

### Activity diagram

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/ActivityDemo.java:activity-demo}}
```

```text
[validate cart, reject: empty cart]
[validate cart, charge card, reserve stock, ship]
```

```plantuml
{{#include ../../diagrams/uml/activity-checkout.puml}}
```

Mermaid has no activity diagram; `activity-checkout.mmd` is a flowchart approximation:

```mermaid
{{#include ../../diagrams/uml/activity-checkout.mmd}}
```

### Use-case diagram

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/UseCaseDemo.java:usecase-demo}}
```

```text
ordered A1 for 100
ordered A1 for 90
[addProduct(A1), placeOrder(A1), pay(), placeOrder(A1), applyCoupon(SAVE10), pay()]
```

```plantuml
{{#include ../../diagrams/uml/usecase-shop.puml}}
```

Mermaid has no use-case diagram; `usecase-shop.mmd` is a flowchart approximation:

```mermaid
{{#include ../../diagrams/uml/usecase-shop.mmd}}
```

| Use-case element | Java |
|------------------|------|
| Actor | interface of that role (`CustomerActions`) |
| Use case | method |
| System boundary | the class implementing the interfaces |
| `<<include>>` | unconditional call (`pay()`) |
| `<<extend>>` | guarded call (`if (coupon != null) applyCoupon`) |

## Predict the output

**P1.** Composition / ownership order.

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/exam/PredictComposition.java:predict-composition}}
```

<details><summary>Answer</summary>

```text
delete a
delete b
delete folder
```

Parts are deleted by the whole, in list order, before the whole's own line.

</details>

**P2.** Aggregation: shared parts.

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/exam/PredictAggregationShared.java:predict-aggregation-shared}}
```

<details><summary>Answer</summary>

```text
0 1
true
Notes
```

`clear()` drops the reference, not the object; the other library still holds it.

</details>

**P3.** A one-way link that was meant to be two-way.

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/exam/PredictNavigation.java:predict-navigation}}
```

<details><summary>Answer</summary>

```text
1
null
NPE
```

The diagram says `<-->`, the code only wired one side. A bidirectional association needs both references set in one method.

</details>

**P4.** Read a nested call chain as a sequence diagram.

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/exam/PredictSequenceNested.java:predict-sequence-nested}}
```

<details><summary>Answer</summary>

```text
A.a start
B.b start
C.c
B.b end
B.b start
C.c
B.b end
A.a end
```

Calls nest like activation bars: each `b()` runs to completion, including `c()`, before the next line of `a()`.

</details>

**P5.** State machine trace.

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/exam/PredictDoorState.java:predict-door-state}}
```

<details><summary>Answer</summary>

```text
OPEN OPEN CLOSED LOCKED LOCKED CLOSED 
```

Events with no outgoing arrow from the current state leave it unchanged here (`lock` while `OPEN`, `open` while `LOCKED`).

</details>

**P6.** Underlined (static) member.

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/exam/PredictStaticMember.java:predict-static-member}}
```

<details><summary>Answer</summary>

```text
1 2 3
3 3
```

`issued` is one shared counter; `a.issued` reads the same static through an instance expression.

</details>

**P7.** Dependency vs association by reflection.

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/exam/PredictDependencyFields.java:predict-dependency-fields}}
```

<details><summary>Answer</summary>

```text
0
1
```

`Report ..> Printer` has no `Printer` field; `Office --> Printer` has one.

</details>

**P8.** Multiplicity `0..1`.

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/exam/PredictOptionalEnd.java:predict-optional-end}}
```

<details><summary>Answer</summary>

```text
P1
none
null
```

The field is `null` when the end is absent; the `Optional` accessor hides that from callers.

</details>

## Compile or not?

| # | Stem | Result | Why |
|---|------|--------|-----|
| C1 | `Shape s = new Shape();` (abstract class) | **fails** | `Shape is abstract; cannot be instantiated` |
| C2 | `Stack s = new Stack();` (interface) | **fails** | `Stack is abstract; cannot be instantiated` |
| C3 | `class Employee implements Payable` without `id()` | **fails** | `is not abstract and does not override abstract method id() in Payable` |
| C4 | `abstract double area() { return 0; }` | **fails** | `abstract methods cannot have a body` |
| C5 | `Limits.MAX = 20;` (interface constant) | **fails** | `cannot assign a value to static final variable MAX` |
| C6 | read `private` field of another top-level class | **fails** | `secret has private access in UmlPrivateOwner` |
| C7 | `class C extends A, B {}` | **fails** | `'{' expected` |
| C8 | `List<int> numbers;` | **fails** | `unexpected type … required: reference found: int` |
| C9 | `protected double area();` in an interface | **fails** | `modifier protected not allowed here` |
| C10 | `private final List<Room> rooms;` never assigned | **fails** | `variable rooms not initialized in the default constructor` |
| C11 | `Counter.inc();` on an instance method | **fails** | `non-static method inc() cannot be referenced from a static context` |
| C12 | `class Parrot extends Bird implements Flyer, Singer` | **compiles** | one class + many interfaces |

The failures, with full compiler text:

<div class="compiler-error">

```java
{{#include ../../code/part-01-oop/broken/UmlAbstractInstantiateBroken.java}}
```

```text
part-01-oop/broken/UmlAbstractInstantiateBroken.java:10: error: Shape is abstract; cannot be instantiated
    Shape s = new Shape();
              ^
```

```java
{{#include ../../code/part-01-oop/broken/UmlInterfaceInstantiateBroken.java}}
```

```text
part-01-oop/broken/UmlInterfaceInstantiateBroken.java:10: error: Stack is abstract; cannot be instantiated
    Stack s = new Stack();
              ^
```

```java
{{#include ../../code/part-01-oop/broken/UmlRealizeMissingBroken.java}}
```

```text
part-01-oop/broken/UmlRealizeMissingBroken.java:11: error: Employee is not abstract and does not override abstract method id() in Payable
  static class Employee implements Payable {
         ^
```

```java
{{#include ../../code/part-01-oop/broken/UmlAbstractBodyBroken.java}}
```

```text
part-01-oop/broken/UmlAbstractBodyBroken.java:5: error: abstract methods cannot have a body
  abstract double area() {
                  ^
```

```java
{{#include ../../code/part-01-oop/broken/UmlInterfaceConstantAssignBroken.java}}
```

```text
part-01-oop/broken/UmlInterfaceConstantAssignBroken.java:10: error: cannot assign a value to static final variable MAX
    Limits.MAX = 20;
          ^
```

```java
{{#include ../../code/part-01-oop/broken/UmlPrivateMemberBroken.java}}
```

```text
part-01-oop/broken/UmlPrivateMemberBroken.java:10: error: secret has private access in UmlPrivateOwner
    System.out.println(new UmlPrivateOwner().secret);
                                            ^
```

```java
{{#include ../../code/part-01-oop/broken/UmlMultipleExtendsBroken.java}}
```

```text
part-01-oop/broken/UmlMultipleExtendsBroken.java:9: error: '{' expected
  static class C extends A, B {}
                          ^
```

```java
{{#include ../../code/part-01-oop/broken/UmlGenericPrimitiveBroken.java}}
```

```text
part-01-oop/broken/UmlGenericPrimitiveBroken.java:11: error: unexpected type
  static List<int> numbers;
              ^
  required: reference
  found:    int
```

```java
{{#include ../../code/part-01-oop/broken/UmlInterfaceProtectedBroken.java}}
```

```text
part-01-oop/broken/UmlInterfaceProtectedBroken.java:6: error: modifier protected not allowed here
    protected double area();
                     ^
```

```java
{{#include ../../code/part-01-oop/broken/UmlFinalNotInitializedBroken.java}}
```

```text
part-01-oop/broken/UmlFinalNotInitializedBroken.java:10: error: variable rooms not initialized in the default constructor
    private final List<Room> rooms;
                             ^
```

```java
{{#include ../../code/part-01-oop/broken/UmlStaticContextBroken.java}}
```

```text
part-01-oop/broken/UmlStaticContextBroken.java:14: error: non-static method inc() cannot be referenced from a static context
    Counter.inc();
           ^
```

</div>

C12 compiles and runs:

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/exam/CompileOkMultiRealize.java:compile-ok-multi-realize}}
```

```text
bird fly hello
```

## Which relationship?

MCQ-style: name the relationship from `A…` to `B` in each pair.

```java
{{#include ../../code/part-01-oop/src/main/java/oop/uml/exam/WhichRelationship.java:which-relationship}}
```

<details><summary>Answer</summary>

| # | Relationship | Why |
|---|--------------|-----|
| 1 | **Dependency** `A1 ..> B` | `B` only as a return type and created in a method; no field |
| 2 | **Association** `A2 --> B` (`0..1`) | field assigned from outside through a setter; no ownership claimed |
| 3 | **Composition** `A3 *-- B` (`2`) | parts created inside, final field, not passed in |
| 4 | **Generalization** `A4 --\|> B` | `extends` |
| 5 | **Realization** `A5 ..\|> I` | `implements` |
| 6 | **Aggregation** `A6 o-- B` (`*`) | collection handed in through the constructor; `B` objects live outside `A6` |

For 2 and 6 an examiner may accept "association" for both; say **aggregation** only when the question describes whole–part.

</details>

## Gotchas

1. **Arrow direction is navigability, not "who is bigger".** `Order --> Customer` means `Order` has the field (`AssociationDemo`, `PredictNavigation`).
2. **Dependency with a field is an association.** Count the fields (`DependencyDemo`, `PredictDependencyFields`).
3. **Composition needs ownership in code.** If the part is passed in or returned to callers, it is aggregation at best (`CompositionDemo` vs `AggregationDemo`).
4. **`1..*` is a rule your constructor enforces**, not something the type system knows (`MultiplicityDemo`, `CompositionDemo`).
5. **Unreachable or unlisted transitions must be rejected.** A state diagram is a total function; every undrawn pair is an error or an explicit no-op (`StateDemo`, `PredictDoorState`).

## UML

The types of this chapter, drawn from the code (`diagrams/uml/chapter-types.mmd`, `chapter-types.puml`):

```mermaid
{{#include ../../diagrams/uml/chapter-types.mmd}}
```

```plantuml
{{#include ../../diagrams/uml/chapter-types.puml}}
```

## Interview Q&A

1. **Q:** Association vs aggregation vs composition? **A:** Field / field filled from outside, shared part / field created and owned inside (`AggregationDemo`, `CompositionDemo`).
2. **Q:** How do you show a dependency in code? **A:** The other type appears only in parameters, locals, returns or static calls, never as a field (`DependencyDemo`).
3. **Q:** Generalization vs realization? **A:** `extends` (solid line, hollow triangle) vs `implements` (dashed line, hollow triangle) (`InheritRealize`).
4. **Q:** What does the arrowhead on an association mean? **A:** Navigability: the tail class holds a reference to the head class (`AssociationDemo`).
5. **Q:** How do you implement multiplicity `1..*`? **A:** Collection field plus an `isEmpty()` check in the constructor (`MultiplicityDemo`).
6. **Q:** How do you implement `0..1`? **A:** Nullable field, `Optional` accessor (`PredictOptionalEnd`).
7. **Q:** How is an abstract class drawn? **A:** Italic name or `<<abstract>>`; abstract members italic or `{abstract}`; Mermaid `*` (`NotationDemo`).
8. **Q:** How is a static member drawn? **A:** Underlined; Mermaid `$` (`PredictStaticMember`).
9. **Q:** `<<include>>` vs `<<extend>>`? **A:** Always-called step vs optional, guarded step (`UseCaseDemo`).
10. **Q:** What does a sequence diagram show that a class diagram does not? **A:** Call order over time between objects (`SequenceDemo`).
11. **Q:** What must a state diagram define for code? **A:** States, events, and a result or an error for every pair (`StateDemo`).
12. **Q:** Can a class have two superclasses in UML for Java? **A:** No, `extends A, B` fails to compile (`UmlMultipleExtendsBroken`).
13. **Q:** Where does `final` appear on a class diagram? **A:** `{readOnly}` on an attribute; `<<final>>` stereotype on a class (`class-notation.puml`).
14. **Q:** Why can't you instantiate a `<<interface>>` box? **A:** No constructor; `X is abstract; cannot be instantiated`.

## Exam writing prompts

Diagram → code. The diagram sources are in `diagrams/uml/exercises/`; Mermaid shown below, PlantUML next to it in the repo.

**W1 (8 marks).** Write the Java for this diagram. A `Library` creates and owns its `Book`s; a `Member` only references them.

```mermaid
{{#include ../../diagrams/uml/exercises/library.mmd}}
```

Answer: `solutions/ch20/SolutionLibrary.java`

```java
{{#include ../../code/part-01-oop/solutions/ch20/SolutionLibrary.java}}
```

```text
city owns 1 book(s)
Asha borrowed Notes
```

**W2 (8 marks).** Write the Java for this diagram. `Employee` is abstract; `Manager` pays `5000 + bonus`, `Intern` pays `1000`.

```mermaid
{{#include ../../diagrams/uml/exercises/employee.mmd}}
```

Answer: `solutions/ch20/SolutionEmployee.java`

```java
{{#include ../../code/part-01-oop/solutions/ch20/SolutionEmployee.java}}
```

```text
Mia 5500
Ivo 1000
total 6500
```

**W3 (5 marks).** Write an `enum Door` with `on(String event)` implementing this state diagram. Events with no arrow from the current state leave it unchanged.

```mermaid
{{#include ../../diagrams/uml/exercises/door-state.mmd}}
```

Answer: `solutions/ch20/SolutionDoorState.java`

```java
{{#include ../../code/part-01-oop/solutions/ch20/SolutionDoorState.java}}
```

```text
CLOSED --open--> OPEN
OPEN --lock--> OPEN
OPEN --close--> CLOSED
CLOSED --lock--> LOCKED
LOCKED --open--> LOCKED
LOCKED --unlock--> CLOSED
```

## Exercises

**E1 — Diagram → code (sequence).** Implement `Atm`, `Bank`, `Dispenser` so that running `withdraw(1234, 100)` logs exactly the three arrows of `exercises/atm-sequence.mmd`.

```mermaid
{{#include ../../diagrams/uml/exercises/atm-sequence.mmd}}
```

Solution `solutions/ch20/SolutionAtmSequence.java`:

```java
{{#include ../../code/part-01-oop/solutions/ch20/SolutionAtmSequence.java}}
```

```text
Atm->>Bank: verify(1234)
Atm->>Bank: withdraw(100)
Atm->>Dispenser: dispense(100)
```

**E2 — Code → diagram (class).** Draw the class diagram for `CompositionDemo.House` and `CompositionDemo.Room`, with visibility and the multiplicity. Answer: `diagrams/uml/solutions/composition.mmd`; a test checks its member lines against `UmlDump`.

```mermaid
{{#include ../../diagrams/uml/solutions/composition.mmd}}
```

**E3 — Code → diagram (sequence).** Draw the sequence diagram of the **declined** run of `SequenceDemo` (use only the calls the code makes). Answer: `diagrams/uml/solutions/sequence-failure.mmd`; a test compares its arrows with the logged calls.

```mermaid
{{#include ../../diagrams/uml/solutions/sequence-failure.mmd}}
```

**E4 — Classify.** Do `WhichRelationship` and check against the answer table above.

**E5 — Use case → code.** Map this diagram to two actor interfaces and a service. `Borrow book` includes `Check availability`; `Pay fine` extends `Return book` only when the book is late.

```plantuml
{{#include ../../diagrams/uml/exercises/usecase-library.puml}}
```

Solution `solutions/ch20/SolutionUseCases.java`:

```java
{{#include ../../code/part-01-oop/solutions/ch20/SolutionUseCases.java}}
```

```text
borrowed Notes
unavailable Notes
returned late
[add(Notes), borrow(Notes), checkAvailability(Notes), borrow(Notes), checkAvailability(Notes), returnBook(Notes), payFine(2)]
```

The class view of that solution (`diagrams/uml/solutions/usecase-library.mmd`):

```mermaid
{{#include ../../diagrams/uml/solutions/usecase-library.mmd}}
```

## Recap checklist

- [x] Class notation: visibility, static, abstract, interface, generics
- [x] Association, aggregation, composition, dependency, generalization, realization
- [x] Multiplicity and navigability mapped to fields and constructors
- [x] Sequence, state, activity, use-case diagrams mapped to code
- [x] Mermaid inline + PlantUML sources under `diagrams/uml/`
- [x] Diagram → code and code → diagram exercises
- [x] Exam banks: flash rules, 8 predict, 12 compile-or-not, 6-way relationship MCQ, 3 writing prompts, 5 exercises
