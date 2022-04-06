import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XmlParser {

    public JSONObject parseXML (String xmlPath, String tool) {
        try {
            File inputFile = new File(System.getProperty("user.home") + xmlPath);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            if (tool.equals("Jacoco")) return Jacoco(doc);
            // else if ()
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject Jacoco (Document doc) {

        NodeList nList = doc.getElementsByTagName("counter");
        JSONObject json = new JSONObject();
        JSONObject item = new JSONObject();

        for (int i = nList.getLength()-6; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                float covered = Integer.parseInt(eElement.getAttribute("covered"));
                float missed = Integer.parseInt(eElement.getAttribute("missed"));
                float percentage = (covered/(missed+covered))*100;
                item.put(eElement.getAttribute("type"), (int)percentage+"%");
            }
        }
        json.put("Jacoco", item);
        return json;
    }
}
