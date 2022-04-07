import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.Formatter;


public class JsonParser {
    /* The purpose of this class is to parse and retrieve coverage metric data
    * from an inputted json file */

    public String parse(String jsonFile) throws IOException, ParseException {

        /* object that allows us to parse json */
        JSONParser parser = new JSONParser();

        /* retrieve all the data in the json file */
        JSONArray toolList = (JSONArray) parser.parse(new FileReader(jsonFile));

        /* a formatter object to print the result in a table with some styling */
        Formatter fmt = new Formatter();
        fmt.format("%15s %15s %15s %15s\n", "---------------", "---------------", "---------------", "---------------");
        fmt.format("%15s %15s %15s %15s\n", "Tool Name", "Branch", "Line", "Method");
        fmt.format("%15s %15s %15s %15s\n", "---------------", "---------------", "---------------", "---------------");

        /* iterate over each tool in the jsonArray and passing to getData method */
        for(Object tool : toolList){
            getData((JSONObject) tool, fmt);
        }

        /* return fmt as a string */
        return fmt.toString();
    }
    private void getData(JSONObject tool, Formatter fmt){

        /* store all the metrics in a variable called values */
        JSONObject values = (JSONObject) tool.get("tool");

        /* also check if we receive any null values */
        String name = isNull((String) values.get("NAME"));
        String branch = isNull((String) values.get("BRANCH"));
        String line = isNull((String) values.get("LINE"));
        String method = isNull((String) values.get("METHOD"));

        /* assign one row of the table to contain
         the metric data from the tool that was passed */
        fmt.format("%15s %15s %15s %15s\n", name, branch, line, method);
    }

    private String isNull(String value){
        /* if this method is passed a string that is null we return a "X" instead */
        if(value == null) {
            return "X";
        }
        return value;
    }
}
