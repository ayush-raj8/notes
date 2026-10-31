# Java book plan

Chapter order and coverage checklist. Tick an item only when prose **and** a compiling (or `broken/`) code file prove it.

**Standards:** every chapter from 01 onward must satisfy [`CONTENT_STANDARDS.md`](CONTENT_STANDARDS.md) (exam flash rules, predict-output bank, compile-or-not bank, writing prompts, marking-scheme trail). Topic rows below are the *subject* contract; the exam block is the *paper* contract.

Status key: `[ ]` todo · `[x]` done · file path after the tick proves the item.

### Per-chapter exam block (required from ch 01)

Copy under each chapter when writing it; tick with proving paths:

- [ ] Exam flash rules (8–15 one-liners or tight table)
- [ ] Predict the output (≥5 stems, `code/.../exam/` or mains, verbatim answers)
- [ ] Compile or not? (≥4 stems, real `javac` text when fail)
- [ ] Exam writing prompts (≥3 short “write / fix” items)
- [ ] Classic traps for this topic covered as predict or compile stems (not prose-only)

---

## Part 0 — Setup

### Chapter 00 — Hello and include anchors — DONE

- [x] Baseline example — `code/part-01-oop/src/main/java/oop/hello/Hello.java`
- [x] Broken return-type-only overload — `code/part-01-oop/broken/HelloBroken.java`
- [x] Fixed overload — `code/part-01-oop/src/main/java/oop/hello/HelloFixed.java`
- [x] Edge case blank/null — `code/part-01-oop/src/main/java/oop/hello/HelloEdge.java`
- [x] JUnit tests — `code/part-01-oop/src/test/java/oop/hello/HelloTest.java`
- [x] Include anchors + real `javac` / program output in chapter Markdown
- [x] mdBook + Maven parent + path-filtered CI + Pages assemble
- [x] Exam shape demo (flash + 1 predict + 1 compile) — see chapter 00; full banks start at ch 01

---

## Part 1 — Java OOP

### Chapter 01 — Classes, objects, memory — DONE

- [x] Classes/objects — `code/part-01-oop/src/main/java/oop/memory/Point.java`, `ClassesObjectsDemo.java`
- [x] Stack/heap, references — `StackHeapDemo.java`
- [x] Pass-by-value proof — `PassByValueDemo.java`
- [x] `this` — `ThisDemo.java`, `ThisShadowGotcha.java`
- [x] `var` — `VarLocals.java`, `broken/VarFieldBroken.java`, `VarFieldFixed.java`
- [x] Exam flash rules
- [x] Predict the output (≥5) — `oop/memory/exam/Predict*.java`
- [x] Compile or not? (≥4) — `broken/VarFieldBroken`, `ThisReassignBroken`, `VarNullBroken`, `VarNoInitBroken`
- [x] Exam writing prompts (≥3)
- [x] Gotchas + UML + interview Q&A + exercises (`solutions/ch01/`)

### Chapter 02 — Constructors — DONE

- [x] Default, parameterized, private, copy — `DefaultBox`, `Account`, `AccountDemo`
- [x] Chaining `this()` / `super()` — `ThisChain`, `Animal`/`Dog`
- [x] First-statement rule + compiler error — `broken/ThisNotFirstBroken`, `SuperNotFirstBroken`
- [x] Constructor not inherited — `broken/CtorNotInheritedBroken`, `CtorNotInheritedFixed`
- [x] Default suppressed when any ctor declared — `OnlyParam` / `MissingDefaultBroken` / `OnlyParamFixed`
- [x] Exam flash / predict≥5 / compile≥4 / writing≥3 — `oop/ctors/exam/*`, broken ctors
- [x] Gotchas + UML + exercises — `CopyEdge`, `MethodNamedLikeClassGotcha`, `solutions/ch02/`

### Chapter 03 — Initialization order — DONE

- [x] Static blocks, instance init blocks, field initializers, constructor — `code/part-01-oop/src/main/java/oop/init/StaticBlockDemo.java`, `InstanceOrderDemo.java`, `InitBlockThisChain.java`
- [x] Parent–child order demo with captured output — `InitParent.java`, `InitChild.java`, `ParentChildDemo.java`
- [x] Forward references + compiler errors — `broken/InitStaticBeforeBroken.java`, `InitForwardInstanceBroken.java`, `ForwardReferenceEdge.java`
- [x] Class-init triggers, failed static init — `InitTriggerDemo.java`, `StaticInitFailureDemo.java`
- [x] Exam flash / predict≥5 / compile≥4 / writing≥3 — `oop/init/exam/Predict*.java`, `CompileForwardOk.java`, `broken/Init*Broken.java`
- [x] Gotchas + UML + exercises — `OverrideInCtorGotcha.java`, `solutions/ch03/`
- [x] JUnit — `code/part-01-oop/src/test/java/oop/init/InitTest.java`
- [x] Chapter — `src/part-01-oop/chapter-03-initialization-order.md`

### Chapter 04 — Access, static, final — DONE

- [x] Four access levels + cross-package demo — `code/part-01-oop/src/main/java/oop/access/AccessLevels.java`, `SamePackageDemo.java`, `PackagePrivateHelper.java`, `oop/access/other/OtherPackageDemo.java`, `OtherSubclass.java`; `broken/AccessPrivateBroken`, `AccessPackageBroken`, `AccessProtectedBroken`, `AccessProtectedViaRefBroken`, `AccessPackageClassBroken`, `AccessPrivateClassBroken`, `AccessNarrowOverrideBroken`
- [x] `static`, `final` (var/method/class/param) — `FinalDemo.java`, `FinalMethodDemo.java`, `FinalRefEdge.java`; `broken/AccessFinalVarBroken`, `AccessFinalParamBroken`, `AccessFinalMethodBroken`, `AccessFinalClassBroken`, `AccessBlankFinalBroken`
- [x] Static vs instance, static import — `Counter.java`, `StaticNullGotcha.java`, `StaticContextFixed.java`, `broken/AccessStaticThisBroken`, `oop/access/other/Consts.java`, `OtherConsts.java`, `StaticImportDemo.java`, `StaticImportShadowGotcha.java`, `broken/AccessStaticImportAmbiguousBroken`
- [x] Exam flash rules — chapter 04 Markdown
- [x] Predict the output (≥5) — `oop/access/exam/PredictStaticCounter`, `PredictStaticShared`, `PredictStaticNull`, `PredictFinalRef`, `PredictStaticDispatch`, `PredictPrivateSameClass`
- [x] Compile or not? (≥4) — `broken/Access*Broken` (11 stems incl. `exam/CompileFinalDeferredOk`)
- [x] Exam writing prompts (≥3)
- [x] Classic traps as predict/compile stems — static via `null`, `final` reference, protected via parent ref, static hiding, private per class
- [x] Gotchas + UML + exercises — `solutions/ch04/`, tests `src/test/java/oop/access/AccessTest.java`
- [x] Chapter — `src/part-01-oop/chapter-04-access-static-final.md`

### Chapter 05 — Nested classes — DONE

- [x] Static nested, inner (member), local, anonymous — `code/part-01-oop/src/main/java/oop/nested/StaticNestedDemo.java`, `InnerDemo.java`, `LocalClassDemo.java`, `AnonymousDemo.java`, `NamesDemo.java`
- [x] Outer member access, `Outer.this` — `InnerDemo.java`, `ShadowDemo.java`, `solutions/ch05/SolutionOuterThis.java`
- [x] `outer.new Inner()` — `InnerDemo.java`, `exam/CompileOkOuterNew.java`, `broken/NestedOuterNewWrongBroken.java`, `broken/NestedInnerFromStaticBroken.java`
- [x] Shadowing, effectively-final capture — `ShadowDemo.java`, `exam/PredictShadow.java`, `exam/PredictCapture.java`, `broken/NestedCaptureMutatedBroken.java`, `broken/NestedAnonMutatesCaptureBroken.java`
- [x] Inner class memory-leak trap — `LeakTrap.java`, `solutions/ch05/SolutionFixLeak.java`
- [x] When to use which — `WhichKind.java` + chapter table
- [x] Inner class in interfaces — `InterfaceNested.java`, `broken/NestedInterfacePrivateBroken.java`
- [x] Nested enums/records/interfaces — `NestedKinds.java`, `LocalRecordDemo.java`, `broken/NestedRecordReadsInstanceBroken.java`
- [x] Exam flash rules
- [x] Predict the output (7) — `oop/nested/exam/Predict*.java`
- [x] Compile or not? (10 fail + 1 pass) — `broken/Nested*.java`, `exam/CompileOkOuterNew.java`
- [x] Exam writing prompts (5)
- [x] Classic traps (static context, capture after mutation, anon init order, shadowing) as stems
- [x] Gotchas + UML + interview Q&A + exercises (`solutions/ch05/`)
- [x] JUnit — `src/test/java/oop/nested/NestedTest.java`
- [x] Chapter — `src/part-01-oop/chapter-05-nested-classes.md`

### Chapter 06 — Overloading — DONE

- [x] Allowed: different count / types / order — `code/part-01-oop/src/main/java/oop/overload/OverloadBasics.java`
- [x] Not allowed: return only, throws only, modifiers only, names only, erasure — `code/part-01-oop/broken/OverloadReturnOnlyBroken.java`, `OverloadThrowsOnlyBroken.java`, `OverloadModifiersOnlyBroken.java`, `OverloadNamesOnlyBroken.java`, `OverloadErasureBroken.java`
- [x] Resolution: exact > widening > boxing > varargs — `ResolutionOrder.java`, `exam/PredictResolution.java`, `exam/PredictWiden.java`
- [x] Ambiguity: `null`, varargs vs fixed — `broken/OverloadNullAmbiguousBroken.java`, `AmbiguityFixed.java`, `VarargsEdge.java`, `exam/PredictVarargs.java`
- [x] Overloading with inheritance, overloading `main` — `OverloadInherit.java`, `OverloadMain.java`, `exam/PredictInherit.java`, `exam/PredictMain.java`
- [x] Static vs instance overloads — `StaticInstanceOverload.java`
- [x] Generics + erasure clash — `broken/OverloadErasureBroken.java`
- [x] Exam flash / predict≥5 / compile / writing / gotchas / UML / exercises — chapter Markdown, `solutions/ch06/SolutionOverload.java`
- [x] JUnit — `src/test/java/oop/overload/OverloadTest.java`
- [x] Chapter — `src/part-01-oop/chapter-06-overloading.md`

### Chapter 07 — Overriding — DONE

- [x] Same signature, covariant return — `OverrideBasics`, `OverrideCovariant`
- [x] Access cannot narrow; checked exceptions cannot broaden — broken + `OverrideAccessWiden`, `OverrideExceptions`
- [x] `@Override`; cannot override `static`/`private`/`final` — `OverridePrivateFinal`, broken
- [x] Static hiding vs overriding; field hiding; `super.method()` — `OverrideStaticHiding`, `OverrideFieldHiding`, `OverrideSuperChain`
- [x] Generics / bridge methods — `OverrideBridge`
- [x] Default methods + diamond — `OverrideDefaultDiamond`
- [x] Constructors not overridden — `OverrideCtorNotInherited`
- [x] Overridable call from constructor trap — `OverrideCtorTrap` / Fixed
- [x] Abstract overriding — `OverrideAbstract`
- [x] Exam block + chapter — `chapter-07-overriding.md`, `oop/override/exam/*`

### Chapter 08 — Overload vs override vs hide vs shadow — DONE

- [x] Comparison table — `src/part-01-oop/chapter-08-overload-override-hide.md`
- [x] One runnable demo file covering all four — `code/part-01-oop/src/main/java/oop/compare/CompareAllFour.java`
- [x] Overload-looks-like-override trap, field hiding, shadowing — `CompareOverloadTrap.java`, `CompareFieldHide.java`, `CompareShadow.java`
- [x] Exam flash rules + MCQ traps
- [x] Predict the output (6) — `oop/compare/exam/Predict*.java`
- [x] Compile or not? (7 fail + 1 pass) — `broken/Compare*Broken.java`, `exam/CompareFieldHideOk.java`
- [x] Exam writing prompts (4)
- [x] Classic traps as stems — `equals(Foo)` overload, static via `null`, `private` not overridden, anonymous shadowing
- [x] Gotchas + UML + interview Q&A + exercises — `solutions/ch08/`
- [x] JUnit — `src/test/java/oop/compare/CompareTest.java`

### Chapter 09 — Polymorphism — DONE

- [x] Compile-time vs runtime — `code/part-01-oop/src/main/java/oop/poly/PolyCompileVsRuntime.java`, `PzTypes.java`
- [x] Upcast/downcast, `instanceof` + pattern matching — `PolyUpcastDowncast.java`, `PolyInstanceofPattern.java`; `broken/PolyDowncastNoCastBroken`, `PolyMethodNotOnParentBroken`, `PolyUnrelatedCastBroken`, `PolyInstanceofUnrelatedBroken`, `PolyPatternScopeBroken`, `PolySwitchDominatedBroken`, `PolySwitchNotExhaustiveBroken`
- [x] `ClassCastException` — `PolyClassCast.java`, `PolyUncaught.java`, `PolyArrayStore.java`; `broken/PolyFinalCastInterfaceBroken`, `PolyGenericInvariantBroken`, `exam/CompileCastInterfaceOk.java`
- [x] Dynamic dispatch + `javap` snippet (real `javap -c -p` output) — `PolyDispatchBytecode.java`
- [x] Exam flash rules + MCQ traps
- [x] Predict the output (9) — `oop/poly/exam/Predict*.java`
- [x] Compile or not? (9 fail + 1 pass) — `broken/Poly*Broken.java`, `exam/CompileCastInterfaceOk.java`
- [x] Exam writing prompts (4)
- [x] Classic traps as stems — overload by static type, array covariance, pattern scope, `getClass()` vs `instanceof`, cast of `null`, non-final class to interface cast
- [x] Gotchas + UML + interview Q&A + exercises — `solutions/ch09/`
- [x] JUnit — `src/test/java/oop/poly/PolyTest.java`
- [x] Chapter — `src/part-01-oop/chapter-09-polymorphism.md`

### Chapter 10 — Encapsulation, inheritance, abstraction — DONE

- [x] Abstract classes vs interfaces (default/static/private) — `code/part-01-oop/src/main/java/oop/abstracts/AbstractClassDemo.java`, `InterfaceMembers.java`, `AbstractVsInterface.java`, `InterfaceStaticNotInherited.java`, `NoAbstractMethodsEdge.java`; `broken/Abstract*Broken.java` (6), `broken/AbsInterface*Broken.java` (5)
- [x] Functional interfaces; multiple inheritance via interfaces — `FunctionalInterfaces.java`, `MultipleInterfaces.java`; `broken/AbsFunctionalTwoAbstractBroken.java`, `broken/AbsLambdaNotFunctionalBroken.java`
- [x] Sealed classes/interfaces + `permits` — `SealedShapes.java`, `SealedClassDemo.java`; `broken/AbsSealedNotPermittedBroken.java`, `AbsSealedMissingModifierBroken.java`, `AbsSealedSwitchNotExhaustiveBroken.java`
- [x] Encapsulation + inheritance basics — `EncapsulationDemo.java`, `InheritanceDemo.java`; `broken/AbsEncapsulationPrivateBroken.java`, `AbsInheritancePrivateBroken.java`
- [x] Exam block: flash rules (16), predict (10) `oop/abstracts/exam/Predict*.java`, compile-or-not (11) `broken/*` + `exam/CompileInterfaceHelpersOk.java`, `CompileAbstractNoMethodsOk.java`, writing prompts (6)
- [x] Gotchas + UML + interview Q&A + exercises — `AbstractCtorOrder.java`, `solutions/ch10/`
- [x] JUnit — `src/test/java/oop/abstracts/AbstractsTest.java`
- [x] Chapter — `src/part-01-oop/chapter-10-encapsulation-inheritance-abstraction.md`

### Chapter 11 — Composition vs inheritance — DONE

- [x] Fragile base class demo — `CountingHashSet`, `FragileVersions`
- [x] Delegation — `ForwardingSet`, `CountingSet`, `EngineSwap`
- [x] Leak + designed-for-extension — `StackLeak`, `DesignedForExtension`
- [x] Exam block: flash rules, predict (7) `oop/compose/exam/Predict*.java`, compile-or-not (5 fail + 1 pass) `broken/Compose*Broken.java` + `exam/CompileOkDelegate.java`, writing prompts (4)
- [x] Gotchas + UML + interview Q&A + exercises — `solutions/ch11/`
- [x] JUnit — `src/test/java/oop/compose/ComposeTest.java`
- [x] Chapter — `src/part-01-oop/chapter-11-composition-vs-inheritance.md`

### Chapter 12 — Object contract — DONE

- [x] `equals` / `hashCode` / `toString` — `code/part-01-oop/src/main/java/oop/objectc/DefaultObjectDemo.java`, `GoodPoint.java`, `GoodPointDemo.java`, `SymmetryDemo.java`, `EqualityPitfalls.java`
- [x] `clone` shallow vs deep; `finalize` deprecation — `CloneDemo.java`, `CopyConstructor.java`, `FinalizeWarned.java`, `FinalizeDeprecated.java`
- [x] Broken equals in `HashSet` — `BrokenContracts.java`, `MutableKeyGotcha.java`, `exam/PredictHashSetNoHash.java`
- [x] Exam flash rules — chapter 12 Markdown
- [x] Predict the output (9) — `oop/objectc/exam/Predict*.java` + `MutableKeyGotcha.java`
- [x] Compile or not? (6 fail + 2 pass) — `broken/Objectc*Broken.java` (EqualsOverload, EqualsReturn, HashCodeLong, ToStringAccess, CloneUnchecked, CloneProtected), `exam/PredictEqualsOverload.java`, `FinalizeWarned.java`
- [x] Exam writing prompts (4)
- [x] Classic traps as stems — `equals(Pt)` overload, `==` on String/Integer, mutable key, shallow clone, array equals
- [x] Gotchas + UML + interview Q&A + exercises — `solutions/ch12/`
- [x] JUnit — `src/test/java/oop/objectc/ObjectcTest.java`
- [x] Chapter — `src/part-01-oop/chapter-12-object-contract.md`

### Chapter 13 — Immutability, records, enums — DONE

- [x] Immutable class recipe, defensive copies — `code/part-01-oop/src/main/java/oop/immut/ImmutableRoster.java`, `LeakyRoster.java`, `DefensiveArray.java`, `FinalVsImmutable.java`
- [x] Records (compact constructors) — `RecordBasics.java`, `RecordCompact.java`, `RecordArrayGotcha.java`, `broken/ImmutRecord*Broken.java`, `ImmutCompactAssignBroken.java`
- [x] Enums: fields, abstract methods, `EnumMap`, singleton — `EnumBasics.java`, `EnumAbstract.java`, `EnumMapDemo.java`, `EnumSingleton.java`, `broken/ImmutEnum*Broken.java`
- [x] Exam flash rules — chapter 13 Markdown
- [x] Predict the output (7) — `oop/immut/exam/Predict*.java`
- [x] Compile or not? (10 fail + 1 pass) — `broken/Immut*Broken.java`, `exam/CompileOkRecordExtras.java`
- [x] Exam writing prompts (5)
- [x] Classic traps as stems — `final` list, unmodifiable view vs copy, record array component, enum `getClass()`, enum singleton
- [x] Gotchas + UML + interview Q&A + exercises — `solutions/ch13/`
- [x] JUnit — `src/test/java/oop/immut/ImmutTest.java`
- [x] Chapter — `src/part-01-oop/chapter-13-immutability-records-enums.md`

### Chapter 14 — Generics essentials — DONE

- [x] Bounded types — `code/part-01-oop/src/main/java/oop/generics/BoundedDemo.java`, `broken/GenBoundMissingBroken.java`
- [x] Wildcards (PECS) — `PecsDemo.java`, `WildcardCapture.java`, `exam/PredictPecsCopy.java`, `broken/GenWildcardAddBroken.java`, `exam/CompileOkWildcardRead.java`
- [x] Erasure — `ErasureDemo.java`, `ArrayCovariance.java`, `GenericArrayWorkaround.java`, `StaticSharedGotcha.java`, `broken/GenNewTBroken.java`, `GenNewArrayBroken.java`, `GenInstanceofBroken.java`, `GenStaticTBroken.java`, `GenThrowableBroken.java`
- [x] Generic methods — `GenericBasics.java`, `exam/PredictInference.java`, `exam/PredictBoundedMax.java`
- [x] Exam flash rules — chapter 14 Markdown
- [x] Predict the output (7) — `oop/generics/exam/Predict*.java` (incl. `PredictRawPollution`, `PredictArrayStore` with real stderr)
- [x] Compile or not? (9 fail + 1 pass) — `broken/Gen*Broken.java`, `exam/CompileOkWildcardRead.java`
- [x] Exam writing prompts (5)
- [x] Classic traps as stems — invariance, raw-type heap pollution, array covariance, shared statics, unbounded `T`
- [x] Gotchas + UML + interview Q&A + exercises — `solutions/ch14/`
- [x] JUnit — `src/test/java/oop/generics/GenericsTest.java`
- [x] Chapter — `src/part-01-oop/chapter-14-generics.md`

### Chapter 15 — Comparable, Iterable, AutoCloseable — DONE

- [x] `Comparable` vs `Comparator` — `ComparableBasics`, `ComparatorDemo`, `CompareOverflowGotcha`, `CompareToEqualsGotcha`, `NotComparableGotcha`
- [x] `Iterable` / `Iterator` — `IterableDemo`, `IteratorRemoveDemo`
- [x] `AutoCloseable` / `Closeable` — `AutoCloseableDemo`, `CloseableEdge`, `NullResourceEdge`
- [x] Exam flash rules
- [x] Predict the output (8) — `oop/contracts/exam/Predict*.java`
- [x] Compile or not? (9 fail + 1 pass) — `broken/Cont*Broken.java`, `exam/CompileTwrOk.java`
- [x] Exam writing prompts (4)
- [x] Classic traps as stems — `a - b` overflow, `TreeSet` ignoring `equals`, CME in for-each, close order, suppressed
- [x] Gotchas + UML + interview Q&A + exercises — `solutions/ch15/`
- [x] JUnit — `src/test/java/oop/contracts/ContractsTest.java`
- [x] Chapter — `src/part-01-oop/chapter-15-comparable-iterable-autocloseable.md`

### Chapter 16 — Exceptions — DONE

- [x] Hierarchy: `Throwable` / `Error` / `Exception` / `RuntimeException` — `code/part-01-oop/src/main/java/oop/ex/HierarchyDemo.java`
- [x] Checked vs unchecked — `CheckedVsUncheckedDemo.java`, `broken/ExUnreportedBroken.java`
- [x] `try` / `catch` / `finally`; finally-vs-return traps — `FinallyReturnDemo.java`, `FinallyExitDemo.java`, `exam/PredictFinallyReturn`, `PredictFinallyOverride`, `PredictFinallyRef`, `PredictFinallyThrows`, `PredictNestedTry`, `PredictCatchOrder`, `broken/ExCatchOrderBroken.java`, `ExNeverThrownBroken.java`, `ExCatchSubclassOfUnthrownBroken.java`
- [x] Multi-catch, rethrow, try-with-resources + suppressed — `MultiCatchDemo.java`, `TwrSuppressedDemo.java`, `PredictRethrow`, `broken/ExMultiCatchRelatedBroken.java`, `ExMultiCatchAssignBroken.java`, `ExTwrCatchMissingBroken.java`
- [x] Custom exceptions, chaining (`cause`) — `InsufficientFundsException.java`, `CustomExceptionDemo.java`, `ChainDemo.java`
- [x] Overriding + exceptions rules — `OverrideExceptionsOk.java`, `broken/ExOverrideBroadenBroken.java`, `ExOverrideNewCheckedBroken.java`
- [x] Stack traces; `throw` vs `throws` — `StackTraceDemo.java`, `ThrowVsThrowsDemo.java`, `broken/ExThrowNonThrowableBroken.java`, `ExUnreachableAfterThrowBroken.java`
- [x] Anti-patterns (swallow, catch `Exception`) — `AntiPatternsDemo.java`, `CatchTooBroadGotcha.java`
- [x] Exceptions in constructors / static init — `CtorThrowsDemo.java`, `StaticInitFailureDemo.java`, `exam/PredictCtorThrows`, `broken/ExCtorImplicitSuperBroken.java`, `ExDefaultCtorBroken.java`
- [x] `StackOverflowError` / `OutOfMemoryError` — `ErrorsDemo.java`, `RuntimeMessagesDemo.java`, `broken/ExInstanceofUnrelatedBroken.java`
- [x] Exam flash rules (15)
- [x] Predict the output (15) — `oop/ex/exam/Predict*.java` (incl. `PredictNestedOrder`, `PredictTwr`, `PredictLoopFinally`, `PredictStaticInit`, `PredictUncaughtAborts`), `FinallyExitDemo`, `TwrSuppressedDemo`
- [x] Compile or not? (19 fail + 2 pass) — `broken/Ex*Broken.java` (incl. `ExStaticInitCheckedBroken`, `ExStaticInitNoCompleteBroken`, `ExTwrCloseCheckedBroken`, `ExGenericThrowableBroken`, `ExCatchNonThrowableBroken`), `exam/CompileCatchUncheckedOk.java`, `exam/CompileCatchExceptionOk.java`
- [x] Resource lifecycle, half-built objects — `ExRes.java`, `ExTwrOrder.java`, `ExCtorFailure.java`
- [x] Exam writing prompts (5)
- [x] Gotchas + UML + interview Q&A + exercises — `solutions/ch16/`
- [x] JUnit — `src/test/java/oop/ex/ExTest.java`, `ExExtrasTest.java`
- [x] Chapter — `src/part-01-oop/chapter-16-exceptions.md`

### Chapter 17 — I/O — DONE

- [x] `File` vs NIO.2 `Path` / `Files` — `code/part-01-oop/src/main/java/oop/io/FileVsPathDemo.java`, `PathOpsDemo.java`, `IoTemp.java`, `broken/IoPathNewBroken.java`, `IoPathExistsBroken.java`
- [x] Byte streams; character streams + charsets — `ByteStreamsDemo.java`, `ReadCountGotcha.java`, `CharsetDemo.java`, `CharStreamsDemo.java`, `broken/IoReadIntToCharBroken.java`, `IoStringCharsetNameBroken.java`, `IoReaderBufferedWrongTypeBroken.java`, `IoUnreportedFileReaderBroken.java`
- [x] `Scanner`; `System.in/out/err` — `ScannerDemo.java`, `StdStreamsDemo.java`, `exam/PredictScannerNextLine`
- [x] Serialization (`Serializable`, `transient`, `serialVersionUID`, custom `readObject`) — `SerialBasics.java`, `SerialCustom.java`, `SerialParentGotcha.java`, `SerialNotSerializableGotcha.java`, `exam/PredictTransient`, `PredictSerialParentCtor`, `broken/IoWriteObjectBroken.java`
- [x] Try-with-resources; walk directories; large files — `TwrFilesDemo.java`, `WalkDirDemo.java`, `LargeFileDemo.java`, `broken/IoStringWriterCloseBroken.java`
- [x] Common bugs (flush/close, encoding) — `FlushCloseGotcha.java`, `CharsetDemo.java`, `exam/PredictFlushMissing`, `PredictCharsetLength`, `PredictFileDelete`, `PredictReadByte`, `PredictPathResolve`
- [x] Exam flash rules (15)
- [x] Predict the output (8) — `oop/io/exam/Predict*.java`
- [x] Compile or not? (8 fail + 1 pass) — `broken/Io*Broken.java`, `exam/CompileScannerPrintOk.java`
- [x] Exam writing prompts (5)
- [x] Gotchas + UML + interview Q&A + exercises — `solutions/ch17/`
- [x] JUnit — `src/test/java/oop/io/IoTest.java`
- [x] Chapter — `src/part-01-oop/chapter-17-io.md`

### Chapter 18 — Collections and streams — DONE

- [x] List/Set/Map/Queue/Deque/`PriorityQueue` + complexity table — `code/part-01-oop/src/main/java/oop/coll/CollListDemo.java`, `CollFailFast.java`, `CollSetDemo.java`, `CollMapDemo.java`, `CollMutableKey.java`, `CollQueueDeque.java`, `CollPriorityQueue.java`, `CollSorting.java`, `CollViews.java`, `CollLru.java`; complexity proven by call counts in `CollComplexityProof.java`
- [x] Streams basics, `Optional` — `CollStreamBasics.java`, `CollStreamLazy.java`, `CollCollectors.java`, `CollOptional.java`
- [x] Exam flash rules (16)
- [x] Predict the output (12) — `oop/coll/exam/Predict*.java` (`RemoveIndexVsObject`, `AsList`, `ForEachRemove`, `PriorityQueue`, `TreeSetEquality`, `StreamOrder`, `StreamReuse`, `ToMap`, `OptionalOrElse`, `MapCalls`, `DequeStack`), `CollMutableKey`
- [x] Compile or not? (11 fail + 1 pass) — `broken/Coll*Broken.java`, `exam/CompileDiamondOk.java`
- [x] Exam writing prompts (6) — `solutions/ch18/Solution*.java` (7 files)
- [x] Classic traps as stems — `remove(int)` vs `remove(Object)`, `Set<Short>.remove`, for-each remove, `Arrays.asList(int[])`, `TreeSet` vs `equals`, `orElse` eager, stream laziness/reuse, `toMap` duplicate
- [x] Gotchas + UML + interview Q&A + exercises
- [x] JUnit — `src/test/java/oop/coll/CollTest.java`
- [x] Chapter — `src/part-01-oop/chapter-18-collections-streams.md`

### Chapter 19 — Concurrency essentials — DONE

- [x] Threads, `synchronized`, `volatile`, `ReentrantLock` — `ThreadBasics`, `StartVsRun`, `ThreadStates`, `DaemonDemo`, `InterruptDemo`, `RaceCounter`, `SyncCounter`, `Reentrancy`, `VolatileDemo`, `WaitNotifyDemo`, `LockDemo`, `DeadlockDemo`, `LockOrdering`
- [x] `ConcurrentHashMap`, `ExecutorService`, `CompletableFuture` — `ConcurrentMapDemo`, `ExecutorDemo`, `ExecutorLifecycle`, `FutureExceptionDemo`, `CompletableDemo`
- [x] Thread-safe singleton variants — `SingletonVariants.java`
- [x] Exam flash rules
- [x] Predict the output (9) — `oop/conc/exam/Predict*.java`
- [x] Compile or not? (10 fail + 2 pass) — `broken/Conc*Broken.java`, `exam/CompileOkCallableChecked.java`, `exam/CompileOkSyncNotInherited.java`
- [x] Exam writing prompts (4; three with solutions) — `solutions/ch19/WritingSyncStack.java`, `WritingDclSingleton.java`, `WritingTimeout.java`; exercises 1–5 — `solutions/ch19/Solution*.java`
- [x] Classic traps as stems — `run()` vs `start()`, `volatile` `count++`, different locks, `notify` without monitor, `ConcurrentHashMap` null
- [x] Gotchas + UML + interview Q&A + exercises — `solutions/ch19/`
- [x] JUnit — `src/test/java/oop/conc/ConcTest.java`
- [x] Chapter — `src/part-01-oop/chapter-19-concurrency.md`

### Chapter 20 — UML — DONE

- [x] Class diagram notation (visibility, static, abstract, interface, generics) — `code/part-01-oop/src/main/java/oop/uml/NotationDemo.java`, `UmlDump.java`; `diagrams/uml/class-notation.{mmd,puml}`
- [x] Relationships mapped to Java: association, aggregation, composition, dependency, generalization, realization, multiplicity, navigability — `AssociationDemo`, `AggregationDemo`, `CompositionDemo`, `DependencyDemo`, `InheritRealize`, `MultiplicityDemo`; `diagrams/uml/relationships.{mmd,puml}`
- [x] Sequence, use-case, state, activity — `SequenceDemo`, `UseCaseDemo`, `StateDemo`, `ActivityDemo`; `diagrams/uml/sequence-order`, `usecase-shop`, `state-order`, `activity-checkout` (`.mmd` + `.puml`)
- [x] Mermaid inline + PlantUML sources — `diagrams/uml/*.mmd` (included into the chapter), `diagrams/uml/*.puml` (PlantUML `-syntax` checked)
- [x] Read-diagram→code and read-code→diagram exercises — `diagrams/uml/exercises/`, `diagrams/uml/solutions/`, `solutions/ch20/*.java`
- [x] Exam flash rules
- [x] Predict the output (8) — `oop/uml/exam/Predict*.java`
- [x] Compile or not? (11 fail + 1 pass) — `broken/Uml*Broken.java`, `exam/CompileOkMultiRealize.java`
- [x] Relationship MCQ — `exam/WhichRelationship.java`
- [x] Exam writing prompts (3, diagram → code) — `solutions/ch20/SolutionLibrary`, `SolutionEmployee`, `SolutionDoorState`
- [x] Classic traps as stems — navigability not wired, dependency vs field, aggregation vs composition lifetime, static member, `1..*` guard
- [x] Gotchas + UML + interview Q&A + exercises (5)
- [x] JUnit (diagram files checked against code: state transitions, call order, class members) — `src/test/java/oop/uml/UmlTest.java`
- [x] Chapter — `src/part-01-oop/chapter-20-uml.md`

---

## Part 2 — SOLID and design principles

### Chapter 21 — SRP — DONE

- [ ] Violating code, real-world failure, stepwise refactor, tests, UML before/after

### Chapter 22 — OCP — DONE

- [ ] Violating code, failure, stepwise refactor, tests, UML before/after

### Chapter 23 — LSP — DONE

- [ ] Contract rules (pre/post, covariance, exceptions)
- [ ] Square/Rectangle + second non-trivial example
- [ ] Violating code, refactor, tests, UML

### Chapter 24 — ISP — DONE

- [ ] Violating code, failure, stepwise refactor, tests, UML

### Chapter 25 — DIP — DONE

- [ ] Violating code, failure, stepwise refactor, tests, UML

### Chapter 26 — Other principles — DONE

- [x] DRY, KISS, YAGNI, Law of Demeter — `code/part-02-solid/src/main/java/solid/principles/{dry,kiss,yagni,demeter}/`
- [x] Composition over inheritance, Tell-Don't-Ask — `composition/`, `tda/`
- [x] High cohesion / low coupling — `cohesion/CohesionDemo.java`
- [x] When NOT to apply SOLID — `notsolid/NotSolidDemo.java`
- [x] Exam flash / predict≥5 / compile≥4 / writing≥3 — `principles/exam/*`, `broken/Prin*Broken.java`
- [x] Gotchas + UML + exercises — `gotchas/PrinciplesGotchas.java`, `solutions/ch26/`
- [x] JUnit — `src/test/java/solid/principles/PrinciplesTest.java`
- [x] Chapter — `src/part-02-solid/chapter-26-other-principles.md`

### Chapter 27 — Smells and refactoring — DONE

- [x] Short catalogue with code before/after — `solid/smells/{longmethod,params,primitive,envy,switchtype,flag,nulls,bequest}/`
- [x] Stepwise long-method refactor — `LongMethodDemo`, `LongMethodSteps` + `SmellsTest.everyRefactoringStepKeepsTheOutput`
- [x] Exam flash / predict≥5 / compile≥4 / writing≥3 — `smells/exam/*`, `broken/Smell*Broken.java`
- [x] Gotchas + UML + exercises — `gotchas/SmellsGotchas.java`, `solutions/ch27/`
- [x] JUnit — `src/test/java/solid/smells/SmellsTest.java`
- [x] Chapter — `src/part-02-solid/chapter-27-smells-refactoring.md`

---

## Part 3 — Design patterns

Each pattern chapter: problem, UML, 3+ code files, vs-similar table, JDK usage.

### Creational

- [ ] Ch 28 Singleton (all variants + pitfalls)
- [ ] Ch 29 Factory Method
- [ ] Ch 30 Abstract Factory
- [ ] Ch 31 Builder
- [ ] Ch 32 Prototype

### Structural

- [ ] Ch 33 Adapter
- [ ] Ch 34 Decorator
- [ ] Ch 35 Facade
- [ ] Ch 36 Proxy
- [x] Ch 37 Composite — `src/part-03-patterns/chapter-37-composite.md`, `code/part-03-patterns/src/main/java/patterns/structural/composite/`, `broken/Composite*`, `solutions/ch37/`, `CompositeTest`; exam: predict ×6, compile ×6, writing ×4
- [x] Ch 38 Bridge — `src/part-03-patterns/chapter-38-bridge.md`, `code/part-03-patterns/src/main/java/patterns/structural/bridge/`, `broken/Bridge*`, `solutions/ch38/`, `BridgeTest`; exam: predict ×6, compile ×7, writing ×4
- [x] Ch 39 Flyweight — `src/part-03-patterns/chapter-39-flyweight.md`, `code/part-03-patterns/src/main/java/patterns/structural/flyweight/`, `broken/Fly*`, `solutions/ch39/`, `FlyweightTest`; exam: predict ×6, compile ×6, writing ×4

### Behavioral

- [x] Ch 40 Strategy — `src/part-03-patterns/chapter-40-strategy.md`, `code/part-03-patterns/src/main/java/patterns/behavioral/strategy/`, `broken/Strategy*`, `solutions/ch40/`, `StrategyTest`; exam: predict ×6, compile ×7, writing ×4
- [x] Ch 41 Observer — `src/part-03-patterns/chapter-41-*.md`, `code/part-03-patterns` (exam block complete)
- [x] Ch 42 Command — `src/part-03-patterns/chapter-42-*.md`, `code/part-03-patterns` (exam block complete)
- [x] Ch 43 State — `src/part-03-patterns/chapter-43-*.md`, `code/part-03-patterns` (exam block complete)
- [x] Ch 44 Template Method — `src/part-03-patterns/chapter-44-*.md`, `code/part-03-patterns` (exam block complete)
- [x] Ch 45 Chain of Responsibility — `src/part-03-patterns/chapter-45-*.md`, `code/part-03-patterns` (exam block complete)
- [x] Ch 46 Iterator — `src/part-03-patterns/chapter-46-*.md`, `code/part-03-patterns` (exam block complete)
- [x] Ch 47 Mediator — `src/part-03-patterns/chapter-47-*.md`, `code/part-03-patterns` (exam block complete)
- [x] Ch 48 Visitor — `src/part-03-patterns/chapter-48-*.md`, `code/part-03-patterns` (exam block complete)
- [x] Ch 49 Memento — `src/part-03-patterns/chapter-49-*.md`, `code/part-03-patterns` (exam block complete)

---

## Part 4 — LLD practice

### Chapter 50 — Interview framework — DONE

- [ ] Requirements, entities, relationships, APIs, patterns, extensibility, concurrency

### Case studies (51–70)

Each: requirements, UML, multi-file Java solution, tests, extension discussion.

- [ ] 51 Parking Lot
- [ ] 52 Elevator
- [ ] 53 Vending Machine
- [ ] 54 ATM
- [ ] 55 LRU/LFU Cache
- [ ] 56 Logger framework
- [ ] 57 Rate Limiter
- [ ] 58 Splitwise
- [x] 59 BookMyShow — `src/part-04-lld/chapter-59-bookmyshow.md`, `lld/bookmyshow/`, `broken/bookmyshow/`, `solutions/ch59/`, `BookMyShowTest`
- [x] 60 Chess — `src/part-04-lld/chapter-60-chess.md`, `lld/chess/`, `broken/chess/`, `solutions/ch60/`, `ChessTest`
- [x] 61 Tic-Tac-Toe — `chapter-61-tic-tac-toe.md`, `lld/tictactoe/`
- [x] 62 Library Management — `chapter-62-library-management.md`, `lld/library/`
- [x] 63 Notification System — `chapter-63-notification-system.md`, `lld/notification/`
- [x] 64 Pub-Sub — `chapter-64-pub-sub.md`, `lld/pubsub/`
- [x] 65 Snake and Ladder — `chapter-65-snake-and-ladder.md`, `lld/snakeladder/`
- [x] 66 Food Delivery — `chapter-66-food-delivery.md`, `lld/food/`
- [x] 67 Ride Sharing — `chapter-67-ride-sharing.md`, `lld/rideshare/`
- [x] 68 File System — `src/part-04-lld/chapter-68-file-system.md`, `lld/fs/`, `broken/fs/`, `solutions/ch68/`, `FsTest`
- [x] 69 In-memory KV store — `src/part-04-lld/chapter-69-kv-store.md`, `lld/kv/`, `broken/kv/`, `solutions/ch69/`, `KvTest`
- [x] 70 Task Scheduler — `src/part-04-lld/chapter-70-task-scheduler.md`, `lld/scheduler/`, `broken/scheduler/`, `solutions/ch70/`, `SchedulerTest`
