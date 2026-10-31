package oop.abstracts;

sealed interface SnpFigure permits SnpDisc {}

final class SnpDisc implements SnpFigure {}

/** Not allowed: a class outside the permits list extends a sealed type. */
final class SnpTriangle implements SnpFigure {}

public class AbsSealedNotPermittedBroken {}
