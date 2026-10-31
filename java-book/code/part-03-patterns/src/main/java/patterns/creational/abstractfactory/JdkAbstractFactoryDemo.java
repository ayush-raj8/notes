package patterns.creational.abstractfactory;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

// ANCHOR: jdk-abstract-factory-demo
public final class JdkAbstractFactoryDemo {
  public static void main(String[] args) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(new InputSource(new StringReader("<a><b/><b/></a>")));
    System.out.println(doc.getDocumentElement().getTagName());
    System.out.println(doc.getElementsByTagName("b").getLength());
  }
}
// ANCHOR_END: jdk-abstract-factory-demo
