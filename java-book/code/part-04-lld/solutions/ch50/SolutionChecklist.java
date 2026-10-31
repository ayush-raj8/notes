import java.util.List;
import lld.framework.DesignSheet;
import lld.framework.InterviewStep;
import lld.framework.MermaidWriter;
import lld.framework.NounVerbExtractor;
import lld.framework.Relation;
import lld.framework.Requirement;

/** Exercises 1-5 of chapter 50 in one runnable file. */
public class SolutionChecklist {
  public static void main(String[] args) {
    // Ex 1: the minutes of the three steps before any code is written.
    int beforeCode = InterviewStep.CLARIFY_REQUIREMENTS.minutes()
        + InterviewStep.IDENTIFY_ENTITIES.minutes()
        + InterviewStep.DEFINE_RELATIONSHIPS.minutes();
    System.out.println("before code: " + beforeCode);

    // Ex 2: extract candidates for a locker prompt.
    var c = NounVerbExtractor.extract(List.of(
        Requirement.functional("Courier stores Parcel"),
        Requirement.functional("Customer collects Parcel")));
    System.out.println(c.classes() + " " + c.methods());

    // Ex 3 + 4: sheet with inheritance and a reasoned pattern, then validate.
    DesignSheet sheet = new DesignSheet("Locker")
        .requirement(Requirement.functional("Courier stores Parcel"))
        .entity("Parcel").entity("Courier").entity("Compartment")
        .relate("Courier", Relation.Kind.ASSOCIATION, "Parcel", "stores")
        .relate("Compartment", Relation.Kind.AGGREGATION, "Parcel", "0..1")
        .api("Receipt store(Parcel p)")
        .pattern("Strategy", "compartment choice varies by parcel size");
    System.out.println(sheet.validate());

    // Ex 5: diagram from the sheet.
    System.out.println(MermaidWriter.classDiagram(sheet));
  }
}
