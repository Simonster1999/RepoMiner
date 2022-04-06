import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.util.Formatter;

public class Parser {
    public static void main(String[] args) {

        /*String s = "---------------";
        Formatter fmt = new Formatter();
        fmt.format("%15s %15s %15s %15s\n", "Tool", "Stm", "Branch", "Line");
        fmt.format("%15s %15s %15s %15s\n", s, s, s, s);
        fmt.format("%15s %15s %15s %15s\n", "Jacoco", "50", "40", "X");
        fmt.format("%15s %15s %15s %15s\n", "Cover", "20", "X", "50");
        System.out.println(fmt);*/
        /*if (args[0]=="jacoco") {
            String XMLpath;
        }*/

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
            json.put("Jacoco", item);
            //json.put("test", item);
            FileWriter file = new FileWriter("CoverageData.json");
            //FileWriter file = new FileWriter("CoverageData.json"); //Append
            file.write(json.toJSONString());
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}