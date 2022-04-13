import org.json.simple.JSONArray;
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
            else if (tool.equals("Clover")) return Clover(doc);
            else if (tool.equals("Jmockit")) return Jmockit(doc);
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

    private JSONObject Jmockit (Document doc) {

        NodeList nList = doc.getElementsByTagName("lineToCover");
        JSONObject tool = new JSONObject();
        JSONObject metrics = new JSONObject();
        float linesCovered = 0;
        float linesTotal = nList.getLength();
        float branchesCovered = 0;
        float totalBranchesToCover = 0;
        float testCov = 0;
        float testTotal = 0;

        metrics.put("NAME", "Jmockit");

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);

            if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) nNode;

                if (Boolean.parseBoolean(e.getAttribute("covered"))) linesCovered++;
                if (!e.getAttribute("branchesToCover").equals("") && !e.getAttribute("coveredBranches").equals("")) {
                    totalBranchesToCover += Float.parseFloat(e.getAttribute("branchesToCover"));
                    branchesCovered += Float.parseFloat(e.getAttribute("coveredBranches"));
                }
                if (Boolean.parseBoolean(e.getAttribute("covered"))) {
                    if (!e.getAttribute("branchesToCover").equals("") && !e.getAttribute("coveredBranches").equals("")) {
                        testCov += Float.parseFloat(e.getAttribute("coveredBranches"));
                        testTotal += Float.parseFloat(e.getAttribute("branchesToCover"));
                    }
                    else {
                        testCov++;
                        testTotal++;
                    }
                }
// The coverage percentage for a source file is calculated as 100 * (NE + NFE) / (NS + NF), where NS is the total number of line segments,
// NF the number of non-final fields, NE the number of executed segments, and NFE the number of fully exercised fields.

// NE - covL, NFE - covB, NS - totL, NF - totB
            }
        }
        tool.put("tool", metrics);
        //System.out.println(linesCovered + " " + linesTotal + " " + branchesCovered + " " + totalBranchesToCover);
        //System.out.println(((float)linesCovered / linesTotal));
        //System.out.println(((float)linesCovered / (linesTotal + linesCovered)));
        //System.out.println(((float)branchesCovered / totalBranchesToCover));
        //System.out.println(((float)(linesCovered+branchesCovered) / (linesTotal+totalBranchesToCover)));
        //System.out.println(((float)(linesCovered+branchesCovered) / (linesTotal+totalBranchesToCover + linesCovered+branchesCovered)));
        System.out.println(testCov / testTotal);
        System.out.println((linesCovered + branchesCovered) / (linesTotal + totalBranchesToCover));
        System.out.println((testCov + linesCovered) / (testTotal + linesTotal));
        return null; //tool
    }
}
