package oop.uml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// ANCHOR: uml-dump
/** Code -> diagram: print the Mermaid member lines of a class from its compiled form. */
public final class UmlDump {
  private UmlDump() {}

  static String symbol(int m) {
    if (Modifier.isPublic(m)) return "+";
    if (Modifier.isProtected(m)) return "#";
    if (Modifier.isPrivate(m)) return "-";
    return "~";
  }

  static String type(java.lang.reflect.Type t) {
    return t.getTypeName()
        .replaceAll("[a-zA-Z0-9_]+\\.", "") // drop package prefixes
        .replaceAll("[A-Za-z0-9_]+\\$", "") // drop outer class prefixes
        .replace('<', '~')
        .replace('>', '~');
  }

  public static List<String> members(Class<?> c) {
    List<String> out = new ArrayList<>();
    List<Field> fields = new ArrayList<>(List.of(c.getDeclaredFields()));
    fields.removeIf(Field::isSynthetic);
    fields.sort(Comparator.comparing(Field::getName));
    for (Field f : fields) {
      out.add(symbol(f.getModifiers()) + f.getName() + ": " + type(f.getGenericType())
          + (Modifier.isStatic(f.getModifiers()) ? "$" : ""));
    }
    List<Method> methods = new ArrayList<>(List.of(c.getDeclaredMethods()));
    methods.removeIf(m -> m.isSynthetic() || m.getName().startsWith("lambda$"));
    methods.sort(Comparator.comparing(Method::getName).thenComparing(Method::toString));
    for (Method m : methods) {
      StringBuilder params = new StringBuilder();
      for (java.lang.reflect.Type p : m.getGenericParameterTypes()) {
        if (params.length() > 0) params.append(", ");
        params.append(type(p));
      }
      int mod = m.getModifiers();
      String sym = c.isInterface() && !Modifier.isPrivate(mod) ? "+" : symbol(mod);
      out.add(sym + m.getName() + "(" + params + ") " + type(m.getGenericReturnType())
          + (Modifier.isStatic(mod) ? "$" : "")
          + (Modifier.isAbstract(mod) && !c.isInterface() ? "*" : ""));
    }
    return out;
  }

  public static String classBlock(Class<?> c) {
    StringBuilder sb = new StringBuilder("class " + c.getSimpleName() + " {\n");
    if (c.isInterface()) sb.append("  <<interface>>\n");
    for (String line : members(c)) sb.append("  ").append(line).append('\n');
    return sb.append("}").toString();
  }

  public static void main(String[] args) {
    System.out.println(classBlock(NotationDemo.Base.class));
    System.out.println(classBlock(NotationDemo.Repository.class));
  }
}
// ANCHOR_END: uml-dump
