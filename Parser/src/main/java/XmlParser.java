import mockit.coverage.data.CoverageData;
import mockit.coverage.data.FileCoverageData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class XmlParser {

    public JSONObject parseXML (String xmlPath, String tool) {

        try {
            if (tool.equals("Jmockit")) return Jmockit(System.getProperty("user.home") + xmlPath);

            File inputFile = new File(System.getProperty("user.home") + xmlPath);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            if (tool.equals("Jacoco")) return Jacoco(doc);
            else if (tool.equals("Clover")) return Clover(doc);
            else System.out.println("The tool: " + tool + " is not supported");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject Jacoco (Document doc) {

        NodeList nList = doc.getElementsByTagName("counter");
        JSONObject tool = new JSONObject();
        JSONObject metrics = new JSONObject();

        metrics.put("NAME", "Jacoco");
        for (int i = nList.getLength()-6; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                float covered = Integer.parseInt(eElement.getAttribute("covered"));
                float missed = Integer.parseInt(eElement.getAttribute("missed"));
                float percentage = (covered/(missed+covered))*100;
                metrics.put(eElement.getAttribute("type"), (int)percentage+"%");
            }
        }
        tool.put("tool", metrics);
        return tool;
    }

    private JSONObject Clover (Document doc) {

        NodeList nList = doc.getElementsByTagName("metrics");
        JSONObject tool = new JSONObject();
        JSONObject metrics = new JSONObject();
        Node nNode = nList.item(0);

        metrics.put("NAME", "Clover");

        if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element e = (Element) nNode;

            float stmCov = Float.parseFloat(e.getAttribute("coveredstatements")) / Float.parseFloat(e.getAttribute("statements")) * 100;
            float metCov = Float.parseFloat(e.getAttribute("coveredmethods")) / Float.parseFloat(e.getAttribute("methods")) * 100;
            float conCov = Float.parseFloat(e.getAttribute("coveredconditionals")) / Float.parseFloat(e.getAttribute("conditionals")) * 100;

            metrics.put("STATEMENT", (int) stmCov + "%");
            metrics.put("METHOD", (int) metCov + "%");
            metrics.put("CONDITION", (int) conCov + "%");
            tool.put("tool", metrics);
            return tool;
        }
        else return null;
    }

    private JSONObject Jmockit (String path){

        JSONObject tool = new JSONObject();
        JSONObject metrics = new JSONObject();

        try {
            File test = new File(path);
            CoverageData data = mockit.coverage.data.CoverageData.readDataFromFile(test);

            float totalLines = 0;
            float coveredLines = 0;
            for (FileCoverageData fileData : data.getFileToFileData().values()) {
                totalLines += fileData.getTotalItems();
                coveredLines += fileData.getCoveredItems();
            }
            float percentage = (coveredLines / totalLines) * 100;

            metrics.put("NAME", "Jmockit");
            metrics.put("LINE", (int) percentage + "%");
            tool.put("tool", metrics);
        }catch(Exception e){}

        return tool;
    }
}
