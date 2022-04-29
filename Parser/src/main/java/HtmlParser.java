import org.json.simple.JSONObject;
import java.io.File;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HtmlParser {
    public JSONObject parseHtml (String htmlPath, String tool) {
        try {
            if (tool.equals("PITest")) return PITest(htmlPath);
            else if (tool.equals("LittleDarwin")) return LittleDarwin(htmlPath);
            else System.out.println("The tool: " + tool + " is not supported");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject PITest (String path) {
        JSONObject tool = new JSONObject();
        JSONObject metrics = new JSONObject();
        metrics.put("NAME", "PITest");
        String[] dirs = new File(path).list();

        try {
            File report = new File (path + "/" + dirs[0] + "/index.html");
            Document doc = Jsoup.parse(report, "UTF-8");
            Elements elements = doc.getElementsByAttributeValue("class", "coverage_legend");
            // Get second element with class=coverage_legend
            String[] full = elements.get(1).text().split("/");

            float percentage = (Float.parseFloat(full[0]) / Float.parseFloat(full[1])) * 100;
            metrics.put("MUTATION", (int) percentage + "%");
            tool.put("tool", metrics);
            return tool;
        } catch (Exception e) {}
        return null;
    }

    private JSONObject LittleDarwin (String path) {
        JSONObject tool = new JSONObject();
        JSONObject metrics = new JSONObject();
        metrics.put("NAME", "LittleDarwin");
        File report = new File(path);

        try {
            Document doc = Jsoup.parse(report, "UTF-8");
            Elements elements = doc.getElementsByAttributeValue("class", "coverage_legend");
            // Get first element with class=coverage_legend
            String[] full = elements.get(0).text().split("/");

            float percentage = (Float.parseFloat(full[0]) / Float.parseFloat(full[1])) * 100;
            metrics.put("MUTATION", (int) percentage + "%");
            tool.put("tool", metrics);
            return tool;
        } catch (Exception e) {}
        return null;
    }
}
