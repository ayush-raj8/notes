package oop.abstracts;

sealed interface SmmFigure permits SmmDisc {}

/** Not allowed: a permitted subclass must be final, sealed or non-sealed. */
class SmmDisc implements SmmFigure {}

public class AbsSealedMissingModifierBroken {}
