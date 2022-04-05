import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;


public class Parser {
    public static void main(String[] args) {

        try {
            File inputFile = new File(System.getProperty("user.home") +
                    "/jackson-dataformat-xml/target/site/jacoco/jacoco.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("counter");

            JSONObject json = new JSONObject();
            JSONObject item = new JSONObject();
            for (int temp = nList.getLength()-6; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    float covered = Integer.parseInt(eElement.getAttribute("covered"));
                    float missed = Integer.parseInt(eElement.getAttribute("missed"));
                    float percentage = (covered/(missed+covered))*100;
                    item.put(eElement.getAttribute("type"), (int)percentage+"%");
                }
            }
            json.put(doc.getDocumentElement().getAttribute("name"), item);
            FileWriter file = new FileWriter("CoverageData.json");
            file.write(json.toJSONString());
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}