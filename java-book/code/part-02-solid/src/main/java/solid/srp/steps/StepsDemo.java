package solid.srp.steps;

// ANCHOR: steps-demo
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import solid.srp.model.Line;
import solid.srp.parts.InMemoryInvoiceRepository;
import solid.srp.parts.OutboxNotifier;
import solid.srp.step1.InvoiceStep1;
import solid.srp.step2.InvoiceStep2;
import solid.srp.step3.InvoiceStep3;
import solid.srp.step4.InvoiceStep4;

public final class StepsDemo {
  public static void main(String[] args) {
    Line a = new Line("A", 2, 4999);
    Line b = new Line("B", 1, 2001);

    InvoiceStep1 s1 = new InvoiceStep1("INV-1");
    s1.add(a);
    s1.add(b);
    Map<String, Long> db = new HashMap<>();
    s1.toText();
    s1.save(db);
    System.out.println("step1 stored=" + db.get("INV-1"));

    InvoiceStep2 s2 = new InvoiceStep2("INV-1");
    s2.add(a);
    s2.add(b);
    s2.toText();
    s2.save(db);
    List<String> outbox = new ArrayList<>();
    s2.sendReceipt(outbox);
    System.out.println("step2 stored=" + db.get("INV-1") + " mail=" + outbox);

    InMemoryInvoiceRepository repo = new InMemoryInvoiceRepository();
    InvoiceStep3 s3 = new InvoiceStep3("INV-1", repo);
    s3.add(a);
    s3.add(b);
    s3.toText();
    s3.save();
    System.out.println("step3 stored=" + repo.find("INV-1").orElse(-1L));

    OutboxNotifier notifier = new OutboxNotifier();
    InvoiceStep4 s4 = new InvoiceStep4("INV-1", repo, notifier);
    s4.add(a);
    s4.add(b);
    s4.process();
    System.out.println("step4 " + s4.toText() + " mail=" + notifier.outbox);
  }
}
// ANCHOR_END: steps-demo
