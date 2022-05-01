import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Parser {

    public static void main(String[] args) throws IOException, ParseException {

        // No arguments given, will attempt to print the code coverage and mutation summaries
        if (args.length == 0) {
            JsonParser jsonParser = new JsonParser();
            System.out.println(jsonParser.parse("CoverageData.json"));
            System.out.println(jsonParser.parse("MutationData.json"));
        }

        // Two arguments given, assumed to be a path from User to a tools report, and what tool was used
        else if (args.length == 2) {
            String reportPath = args[0];
            String tool = args[1];
            XmlParser xmlParser = new XmlParser();
            HtmlParser htmlParser = new HtmlParser();
            JSONParser parser = new JSONParser();
            JSONArray toolList = null;
            JSONObject json = null;
            String summaryFile = "";

            if (tool.equals("PITest") || tool.equals("LittleDarwin")) {
                json = htmlParser.parseHtml(reportPath, tool);
                summaryFile = "MutationData.json";
            }
            else {
                json = xmlParser.parseXML(reportPath, tool);
                summaryFile = "CoverageData.json";
            }

            // Check if a json object was returned
            if (json == null) return;

            try {
                // Check if the summaryFile has reports from previous tools
                FileReader fileReader = new FileReader(summaryFile);
                // Save previous reports
                toolList = (JSONArray) parser.parse(fileReader);
                fileReader.close();
            } catch (Exception e) {}

            // If there were not any previous reports, create an empty json array
            if (toolList == null) {
                toolList = new JSONArray();
            }

            // Add the latest report to the json array
            toolList.add(json);

            try {
                // Write updated json array containing reports to CoverageData.json
                FileWriter fileWriter = new FileWriter(summaryFile);
                fileWriter.write(toolList.toJSONString());
                fileWriter.close();

            } catch (Exception e) { e.printStackTrace(); }
        }
        // If an incorrect amount of arguments was given, exit
        else return;
    }
}