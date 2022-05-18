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
        float meanSuite = getMeanOfSuite(matrix);
        float standardDiv = getStandardDeviation(matrix, meanSuite);
        JSONObject Diversity = new JSONObject();
        JSONObject vals = new JSONObject();
        vals.put("AVERAGEOFAVERAGE", averageOfAverage);
        vals.put("MEANSUITE", meanSuite);
        vals.put("STANDARDDIV", standardDiv);
        Diversity.put(divType, vals);

        return Diversity;
    }
    private HashMap<String, ArrayList<Float>> parseFile(CSVReader reader) throws CsvValidationException, IOException {
        HashMap<String, ArrayList<Float>> testValueMatrix = new HashMap<>();
        ArrayList<Float> values = new ArrayList<>();
        int iterations = 0;
        String[] columns;
        while ((columns = reader.readNext()) != null) {
            if(iterations <= 0){
                iterations++;
                continue;
            }
            for(int i = 0; i < columns.length; i++){
                if(i == 0){
                    continue;
                }
                values.add(Float.parseFloat(columns[i]));
            }
            testValueMatrix.put(columns[0], values);
        }
        return testValueMatrix;
    }
    private float getAverageOfAverage(HashMap<String, ArrayList<Float>> matrix){
        ArrayList<Float> meanPerColumn = new ArrayList<Float>();
        for(ArrayList<Float> column : matrix.values()){
            float total = 0;
            for(float value : column){
                total += value;
            }
            float mean = total/(column.size()-1);
            meanPerColumn.add(mean);
        }
        float total = 0;
        for(float value : meanPerColumn){
            total += value;
        }

        return total/meanPerColumn.size();
    }
    private float getMeanOfSuite(HashMap<String, ArrayList<Float>> matrix){
        float total = 0;
        int n = 0;

        for(ArrayList<Float> column : matrix.values()){
            for(float value : column){
                total += value;
                n++;
            }
        }
        float mean = total/(n-1);

        return mean;
    }
    private float getStandardDeviation(HashMap<String, ArrayList<Float>> matrix, float mean) throws CsvValidationException, IOException {
        float s = 0;
        int n = matrix.size();
        for(ArrayList<Float> column : matrix.values()){
            for(float value : column){
                s += (float) Math.sqrt(Math.pow(value - mean, 2) / (n*(column.size()-1)));
            }
        }
        return s;
    }
}
