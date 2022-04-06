import org.json.simple.JSONObject;
import java.io.FileWriter;

public class Parser {

    public static void main(String[] args) {

        XmlParser xmlParser = new XmlParser();

        if (args.length == 0) {}

        else if (args.length == 2) {
            String xmlPath = args[0];
            String tool = args[1];
            JSONObject json = xmlParser.parseXML(xmlPath, tool);

            if (json == null) return;

            try {
                FileWriter file;
                if (tool.equals("Jacoco")) { file = new FileWriter("CoverageData.json"); }
                else { file = new FileWriter("CoverageData.json", true); }

                file.write(json.toJSONString());
                file.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else return;
    }
}