import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.*;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Parser {
    public static void main(String[] args) {

        try {
            File inputFile = new File(System.getProperty("user.home") +
                    "/jackson-dataformat-xml/target/site/jacoco/jacoco.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("counter");

            for (int temp = nList.getLength()-6; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Covered : "
                            + eElement.getAttribute("covered") + " Missed: "
                            + eElement.getAttribute("missed") + " Type: "
                            + eElement.getAttribute("type"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}