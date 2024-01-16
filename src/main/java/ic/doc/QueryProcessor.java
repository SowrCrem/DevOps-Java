package ic.doc;

import java.util.HashMap;
import java.util.Map;

public class QueryProcessor {

    private Map<String, String> queryInfo = new HashMap<>();

    public void populateInfo() {
        queryInfo.put("shakespeare",
        "William Shakespeare (26 April 1564 - 23 April 1616) was an\n" +
        "English poet, playwright, and actor, widely regarded as the greatest\n" +
        "writer in the English language and the world's pre-eminent dramatist. \n");
        queryInfo.put("asimov",
        "Isaac Asimov (2 January 1920 - 6 April 1992) was an\n" +
        "American writer and professor of Biochemistry, famous for\n" +
        "his works of hard science fiction and popular science. \n");
        queryInfo.put("imperial",
        "Imperial College London was founded in 1907 by the state for\n" +
        "advanced university-level training in science and technology,\n" +
        "and for the promotion of research in support of industry\n" +
        "throughout the British Empire. The college joined the University\n" +
        "of London on 22 July 1908, with the City and Guilds College joining in 1910.\n");
        queryInfo.put("london",
        "London is the capital and largest city of England and the United Kingdom,\n" +
        "with a population of around 8.8 million. It stands on the River Thames in\n" +
        "south-east England at the head of a 50-mile (80 km) estuary down to the North\n" +
        "Sea and has been a major settlement for nearly two millennia.");
        queryInfo.put("the strongest",
        "Who is Aaron Thomas to you?,\n" +
        "He's the strongest.\n" +
        "Nah. I'd win.\n" +
        "Stand proud. You are Strong.");
        queryInfo.put("aaron thomas",
        "He's the strongest.\n" +
        "the honoured one.\n" +
        "the strongest.");
        queryInfo.put("Chelsea",
        "the objectively best football club in the world.");
        
    
    }

    public QueryProcessor () {
        populateInfo();
    }

    public String process(String query) {
        StringBuilder results = new StringBuilder();

        for (String searchableQuery : queryInfo.keySet()) {
            if (query.toLowerCase().contains(searchableQuery)) {
                results.append(queryInfo.get(searchableQuery));
                results.append(System.lineSeparator());
            }
        }

        return results.toString();
    }

    public String processMd(String query) {
        StringBuilder markdownResults = new StringBuilder();

        for (String searchableQuery : queryInfo.keySet()) {
            if (query.toLowerCase().contains(searchableQuery)) {
                String result = queryInfo.get(searchableQuery);
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
