import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVParser {
    public JSONObject parseCSV (String CSVPath, String divType) throws CsvValidationException, IOException {
        CSVReader reader = new CSVReader(new FileReader(CSVPath));
        JSONObject Diversity = new JSONObject();
        JSONObject values = getDiversityPerTest(reader);
        values.put("TOTAL DIVERSITY", getTotalDiversity(values));
        Diversity.put(divType, values);

        return Diversity;
    }
    private JSONObject getDiversityPerTest(CSVReader reader) throws CsvValidationException, IOException {
        JSONObject values = new JSONObject();
        int iterations = 0;
        String[] columns;
        
        while ((columns = reader.readNext()) != null) {
            if(iterations <= 0){
                iterations++;
                continue;
            }
            float count = 0;
            float total = 0;
            for(int i = 0; i < columns.length; i++){
                if(i == 0){
                    continue;
                }
                count++;
                total += Float.parseFloat(columns[i]);
            }
            float average = total/(count-1);
            values.put(columns[0], average);
        }

        return values;
    }
    private float getTotalDiversity(JSONObject values){
        float total = 0;
        float count = 0;
        for(Object obj : values.values()){
            total += Float.parseFloat(obj.toString());
            count++;
        }

        return total/count;
    }
}
