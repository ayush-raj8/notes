package oop.abstracts;

/** Not allowed: abstract and static together (static methods are not overridden). */
public abstract class AbstractStaticBroken {
  abstract static void run();
}
