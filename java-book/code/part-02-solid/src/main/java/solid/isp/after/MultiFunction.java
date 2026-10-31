package solid.isp.after;

// ANCHOR: multi-function
/** A combined role for clients that really need several. Built from the small ones. */
public interface MultiFunction extends Printer, DocScanner, FaxSender {
  default String copy() {
    return print(scan());
  }
}
// ANCHOR_END: multi-function
