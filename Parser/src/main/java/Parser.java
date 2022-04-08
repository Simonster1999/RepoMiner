import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Parser {

    public static void main(String[] args) throws IOException, ParseException {

        // No arguments given, will attempt to print the coverage reports gathered
        if (args.length == 0) {
            JsonParser jsonParser = new JsonParser();
            System.out.println(jsonParser.parse("CoverageData.json"));
        }

        // Two arguments given, assumed to be a path from User to a tools xml report, and what tool was used
        else if (args.length == 2) {
            String xmlPath = args[0];
            String tool = args[1];
            XmlParser xmlParser = new XmlParser();
            JSONArray toolList = null;
            JSONObject json = xmlParser.parseXML(xmlPath, tool);

            // Check if a json object was returned
            if (json == null) return;

            try {
                JSONParser parser = new JSONParser();
                // See if there exist coverage reports from previous tools in CoverageData.json
                FileReader fileReader = new FileReader("CoverageData.json");
                // Save previous reports
                toolList = (JSONArray) parser.parse(fileReader);
                fileReader.close();
            } catch (Exception e) {}

            // If there were not any previous coverage report, create an empty json array
            if (toolList == null) {
                toolList = new JSONArray();
            }

            // Add latest coverage report to the json array
            toolList.add(json);

            try {
                // Write updated json array containing coverage reports to CoverageData.json
                FileWriter fileWriter = new FileWriter("CoverageData.json");
                fileWriter.write(toolList.toJSONString());
                fileWriter.close();

            } catch (Exception e) { e.printStackTrace(); }
        }
        else return;
    }
}