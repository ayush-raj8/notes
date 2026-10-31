package solid.isp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import solid.isp.after.AllInOne;
import solid.isp.after.BasicPrinter;
import solid.isp.after.DocScanner;
import solid.isp.after.FaxSender;
import solid.isp.after.MultiFunction;
import solid.isp.after.Office;
import solid.isp.after.Printer;
import solid.isp.before.Machine;
import solid.isp.before.ScanClient;

class IspTest {
  @Test
  void fatInterfaceMakesAPrinterFailAtRuntime() {
    Machine printerOnly = new solid.isp.before.BasicPrinter();
    assertThrows(UnsupportedOperationException.class, () -> ScanClient.archive(printerOnly));
    assertFalse(printerOnly.fax("memo")); // silent no-op: nobody learns the fax never left
  }

  @Test
  void roleInterfacesDocumentWhatADeviceCanDo() {
    assertTrue(Printer.class.isAssignableFrom(BasicPrinter.class));
    assertFalse(DocScanner.class.isAssignableFrom(BasicPrinter.class));
    assertFalse(FaxSender.class.isAssignableFrom(BasicPrinter.class));
    assertTrue(MultiFunction.class.isAssignableFrom(AllInOne.class));
  }

  @Test
  void aClientTestNeedsOnlyALambda() {
    assertEquals("archived:fake", Office.archive(() -> "fake"));
  }

  @Test
  void combinedRoleKeepsTheSmallOnes() {
    AllInOne all = new AllInOne();
    assertEquals("printed:page-1", all.copy());
    assertEquals("printed:memo fax=true", Office.broadcast(all, all, "memo"));
    assertEquals("printed:memo", Office.report(new BasicPrinter(), "memo"));
  }
}
