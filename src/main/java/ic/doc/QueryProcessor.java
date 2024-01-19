package ic.doc;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;

public class QueryProcessor {

    private JSONObject queryInfo;

    public void populateInfo() {
        try {
            // Read content from the JSON file
            BufferedReader jsonFile = new BufferedReader(new FileReader("src/main/java/ic/doc/queryInfo.json"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = jsonFile.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            queryInfo = new JSONObject(stringBuilder.toString());
            jsonFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public QueryProcessor() {
        populateInfo();
    }

    public String process(String query) {
        StringBuilder results = new StringBuilder();

        for (String searchableQuery : queryInfo.keySet()) {
            System.out.println(searchableQuery);
            if (query.toLowerCase().contains(searchableQuery)) {
                results.append(queryInfo.getString(searchableQuery));
                results.append(System.lineSeparator());
            }
        }

        return results.toString();
    }

    public String processMarkdown(String query) {
        StringBuilder markdownResults = new StringBuilder();

        for (String searchableQuery : queryInfo.keySet()) {
            if (query.toLowerCase().contains(searchableQuery)) {
                String result = queryInfo.getString(searchableQuery);
                markdownResults.append(generateMarkdownSection(searchableQuery, result));
            }
        }

        return markdownResults.toString();
    }

    private String generateMarkdownSection(String title, String content) {
        StringBuilder section = new StringBuilder();
        // Title as a header
        section.append("## ").append(title.substring(0, 1).toUpperCase() + title.substring(1)).append("\n\n");
        // Content with proper line breaks
        section.append(content.replace("\n", "  \n")).append("\n");
        // Add a horizontal line for separation
        section.append("---\n\n");
        return section.toString();
    }
}
