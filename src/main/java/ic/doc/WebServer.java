package ic.doc;

import ic.doc.web.HTMLResultPage;
import ic.doc.web.IndexPage;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;

public class WebServer {

    public WebServer() throws Exception {
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));

        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(new ServletHolder(new Website()), "/*");
        server.setHandler(handler);

        server.start();
    }

    static class Website extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String query = req.getParameter("q");
            String downloadFormat = req.getParameter("format");
            if (query == null) {
                new IndexPage().writeTo(resp);
            } else {

                if (downloadFormat == null) {
                    downloadFormat = "html";
                }

                QueryProcessor processor = new QueryProcessor();
                if (downloadFormat.equals("markdown")) {

                    String markdownContent = processor.processMd(query);
                    File tempfile = File.createTempFile("query-result", ".md");
                    
                    try (PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(tempfile)))) {
                        out.println(markdownContent);
                    }

                    resp.setContentType("text/markdown");
                    resp.setHeader("Content-Disposition", "attachment; filename=\"query-result.md\"");
                    Files.copy(tempfile.toPath(), resp.getOutputStream());
                    tempfile.deleteOnExit();
                } else {
                    // TODO
                    new HTMLResultPage(query, processor.process(query)).writeTo(resp);
                }
            }

        }
    }

    public static void main(String[] args) throws Exception {
        new WebServer();
    }
}

