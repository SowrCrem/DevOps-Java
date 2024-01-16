package ic.doc.web;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HTMLResultPage implements Page {

    private final String query;
    private final String answer;

    public HTMLResultPage(String query, String answer) {
        this.query = query;
        this.answer = answer;
    }

    public void writeTo(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();

        // Header
        writer.println("<html>");
        writer.println("<head><title>" + query + "</title></head>");
        writer.println("<body>");

        // Content
        if (answer == null || answer.isEmpty()) {
            writer.println("<h1>Sorry</h1>");
            writer.print("<p>Sorry, we didn't understand <em>" + query + "</em></p>");
        } else {
            writer.println("<h1>" + query + "</h1>");
            writer.println("<p>" + answer.replace("\n", "<br>") + "</p>");
        }

        writer.println(
                "<form>" + 
                        "<p>Select Download Format:</p>" +
                        "<input type=\"radio\" id=\"markdown\" name=\"format\" value=\"markdown\">" +
                        "<label for=\"markdown\">Markdown</label><br>" + 
                        "<input type=\"radio\" id=\"html\" name=\"format\" value=\"html\">" +
                        "<label for=\"html\">html</label><br>" +
                        "<br>" +
                        "<input type=\"submit\" value=\"Submit\">" +
                        "</form>");
                        
        writer.println("<p><a href=\"/\">Back to Search Page</a></p>");

        // Footer
        writer.println("</body>");
        writer.println("</html>");
    }
}
