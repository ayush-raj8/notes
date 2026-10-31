package oop.abstracts;

/** Not allowed: abstract and final together (a final class can never be extended). */
public abstract final class AbstractFinalBroken {
  abstract void run();
}
