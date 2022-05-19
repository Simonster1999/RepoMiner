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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVParser {
    public JSONObject parseCSV (String CSVPath, String divType) throws CsvValidationException, IOException {
        CSVReader reader = new CSVReader(new FileReader(CSVPath));
        HashMap<String, ArrayList<Float>> matrix = parseFile(reader);
        float averageOfAverage = getAverageOfAverage(matrix);
        float standardDiv = getStandardDeviation(matrix, averageOfAverage);
        JSONObject Diversity = new JSONObject();
        JSONObject vals = new JSONObject();
        vals.put("AVERAGEOFAVERAGE", averageOfAverage);
        vals.put("STANDARDDIV", standardDiv);
        vals.put("DIVTYPE", divType);
        Diversity.put("tool", vals);

        return Diversity;
    }
    private HashMap<String, ArrayList<Float>> parseFile(CSVReader reader) throws CsvValidationException, IOException {
        HashMap<String, ArrayList<Float>> testValueMatrix = new HashMap<>();
        int iterations = 0;
        String[] columns;
        while ((columns = reader.readNext()) != null) {
            if(iterations <= 0){
                iterations++;
                continue;
            }
            ArrayList<Float> values = new ArrayList<>();
            for(int i = 1; i < columns.length; i++){
                values.add(Float.parseFloat(columns[i]));
            }
            testValueMatrix.put(columns[0], values);
        }
        return testValueMatrix;
    }
    private float getAverageOfAverage(HashMap<String, ArrayList<Float>> matrix){
        float mean = 0;
        int n = 0;
        for(ArrayList<Float> column : matrix.values()){
            float total = 0;
            for(float value : column){
                total += value;
            }
            mean += total/(column.size()-1);
            n++;
        }
        return mean/n;
    }
    private float getStandardDeviation(HashMap<String, ArrayList<Float>> matrix, float mean) throws CsvValidationException, IOException {
        float s = 0;
        int n = 0;
        for(ArrayList<Float> column : matrix.values()){
            for(float value : column){
                s += (float) Math.pow(value - mean, 2);
                n++;
            }
        }
        float sd = s / (n*(n- matrix.size()));
        return (float)Math.sqrt(sd);
    }
}
