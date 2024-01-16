package ic.doc;
import static java.util.Map.Entry;

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
    }

    public String process(String query) {
        StringBuilder results = new StringBuilder();

        Boolean found = false;

        for (String searchableQuery : queryInfo.keySet()) {
            if (query.toLowerCase().contains(searchableQuery)) {
                results.append(queryInfo.get(searchableQuery));
                results.append(System.lineSeparator());
                found = true;
            }
        }

        if (!found) {
            results.append("Sorry, we do not have Information for the query you've entered");
            results.append(System.lineSeparator());
        }

        return results.toString();
    }
}
