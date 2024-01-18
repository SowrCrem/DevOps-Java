package ic.doc;

import ic.doc.web.HTMLResultPage;
import ic.doc.web.IndexPage;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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
            String outputFormat = req.getParameter("format");

            if (query == null) {
                new IndexPage().writeTo(resp);
            } else {
                QueryProcessor processor = new QueryProcessor();
                if (outputFormat.equals("html")) {
                    new HTMLResultPage(query, processor.process(query)).writeTo(resp);
                } else {    // downloadFormat.equals("Markdown" || "PDF")
                    String markdownContent = processor.processMarkdown(query);

                    if (outputFormat.equals("markdown")) {
                        File tempfile = File.createTempFile("query-result", ".md");
                        
                        try (PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(tempfile)))) {
                            out.println(markdownContent);
                        }

                        resp.setContentType("text/markdown");
                        resp.setHeader("Content-Disposition", "attachment; filename=\"query-result.md\"");
                        Files.copy(tempfile.toPath(), resp.getOutputStream());
                        tempfile.deleteOnExit();
                    } else {    // downloadFormat.equals("PDF")
                        File tempMarkdownFile = File.createTempFile("query-result", ".md");
                        File tempPdfFile = File.createTempFile("query-result", ".pdf");
                    
                        try (PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(tempMarkdownFile)))) {
                            out.println(markdownContent);
                        }
                    
                        // Use ProcessBuilder to execute the pandoc command
                        ProcessBuilder processBuilder = new ProcessBuilder(
                            "docker", "run",
                            "--rm",
                            "--volume", "`pwd`:/data",
                            "--user", "`id -u`:`id -g`",
                            "pandoc/latex",
                            tempMarkdownFile.getAbsolutePath(),
                            "-o", tempPdfFile.getAbsolutePath()
                        );

                        processBuilder.redirectErrorStream(true);

                        Process process = processBuilder.start();

                        try {
                            int exitCode = process.waitFor();
                        
                            if (exitCode == 0) {
                                // Set response headers for PDF download
                                resp.setContentType("application/pdf");
                                resp.setHeader("Content-Disposition", "attachment; filename=\"query-result.pdf\"");
                                Files.copy(tempPdfFile.toPath(), resp.getOutputStream());
                            } else {
                                resp.getWriter().println("Error converting Markdown to PDF");
                                
                                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        // Log or print the output
                                        System.out.println(line);
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                                
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    // Log or print the output
                                    System.out.println(line);
                                }
                            }
                        }
                    
                        // Cleanup temp files
                        tempMarkdownFile.deleteOnExit();
                        tempPdfFile.deleteOnExit();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new WebServer();
    }
}

