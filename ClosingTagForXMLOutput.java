import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;

public class XmlGenerator {

    public static void main(String[] args) {
        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);

            // Start the document
            xmlStreamWriter.writeStartDocument();

            // Start the root element
            xmlStreamWriter.writeStartElement("root");

            // Write child elements
            writeXmlElement(xmlStreamWriter, "element1", "value1");
            writeXmlElement(xmlStreamWriter, "element2", "value2");

            // Close the root element
            xmlStreamWriter.writeEndElement();

            // End the document
            xmlStreamWriter.writeEndDocument();

            // Flush and close the writer
            xmlStreamWriter.flush();
            xmlStreamWriter.close();

            // Print the generated XML
            System.out.println(stringWriter.toString());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private static void writeXmlElement(XMLStreamWriter writer, String elementName, String text) throws XMLStreamException {
        // Start the element
        writer.writeStartElement(elementName);

        // Write the text content
        writer.writeCharacters(text);

        // End the element
        writer.writeEndElement();
    }
}
