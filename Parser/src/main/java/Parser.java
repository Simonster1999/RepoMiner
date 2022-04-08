import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;

public class Parser {

    public static void main(String[] args) throws IOException, ParseException {

        XmlParser xmlParser = new XmlParser();

        if (args.length == 0) {
            JsonParser jsonParser = new JsonParser();
            System.out.println(jsonParser.parse("CoverageData.json"));
        }

        else if (args.length == 2) {
            String xmlPath = args[0];
            String tool = args[1];
            JSONArray json = xmlParser.parseXML(xmlPath, tool);

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