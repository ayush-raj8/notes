package oop.access;

/** Intentionally invalid: extending a final class. */
final class FinalClassParent {}

class FinalClassChild extends FinalClassParent {}

public final class AccessFinalClassBroken {}
