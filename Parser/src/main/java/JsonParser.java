import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.*;
import java.util.Formatter;


public class JsonParser {
    /* The purpose of this class is to parse and retrieve coverage metric data
    * from an inputted json file */

    public String parse(String jsonFile) throws IOException, ParseException {

        /* object that allows us to parse json */
        JSONParser parser = new JSONParser();
        JSONArray toolList;

        /* retrieve all the data in the json file */
        try {
            toolList = (JSONArray) parser.parse(new FileReader(jsonFile));
        } catch (Exception e) {
            return "";
        }
        String mode;
        String table = "";

        if (jsonFile.equals("MutationData.json")) {
            mode = "mutation";
            table += "Tool Name,Mutation Cov.";
        }
        else {
            mode = "coverage";
            table += "Tool Name,Branch,Instruction,Line,Method,Class,Statement,Condition";
        }

        /* iterate over each tool in the jsonArray and passing to getData method */
        for (Object tool : toolList){
            table += getData((JSONObject) tool, mode);
        }

        /* return fmt as a string */
        return table;
    }
    private String getData(JSONObject tool, String mode){

        /* store all the metrics in a variable called values */
        JSONObject values = (JSONObject) tool.get("tool");
        if (values == null) return null;

        if (mode.equals("mutation")) {
            String name = isNull((String) values.get("NAME"));
            String mutationCov = isNull((String) values.get("MUTATION"));

            return "\n" + name + "," + mutationCov;
        }
        else if (mode.equals("coverage")) {
            /* also check if we receive any null values */
            String name = isNull((String) values.get("NAME"));
            String branch = isNull((String) values.get("BRANCH"));
            String instruction = isNull((String) values.get("INSTRUCTION"));
            String line = isNull((String) values.get("LINE"));
            String method = isNull((String) values.get("METHOD"));
            String clss = isNull((String) values.get("CLASS"));
            String statement = isNull((String) values.get("STATEMENT"));
            String condition = isNull((String) values.get("CONDITION"));

            return "\n" + name + "," + branch + "," + instruction + "," + line + "," + method + "," + clss + "," + statement + "," + condition;
        }
        return null;
    }

    private String isNull(String value){
        /* if this method is passed a string that is null we return a "X" instead */
        if(value == null) {
            return "-";
        }
        return value;
    }
}
