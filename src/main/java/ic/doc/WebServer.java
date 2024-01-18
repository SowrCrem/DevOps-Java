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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
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
                        ProcessBuilder pandocProcessBuilder = new ProcessBuilder("pandoc", "-s", "--from=markdown", "--to=pdf");
                        Process pandocProcess = pandocProcessBuilder.start();

                        try (OutputStreamWriter writer = new OutputStreamWriter(pandocProcess.getOutputStream(), StandardCharsets.UTF_8)) {
                            writer.write(markdownContent);
                        }

                        InputStream pandocInputStream = pandocProcess.getInputStream();

                        InputStream pandocErrorStream = pandocProcess.getErrorStream();
                        String errorOutput = new String(IOUtils.toByteArray(pandocErrorStream), StandardCharsets.UTF_8);
                        if (!errorOutput.isEmpty()) {
                            System.err.println("pandoc error output: " + errorOutput);
                            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return;
                        }

                        resp.setContentType("application/pdf");
                        resp.setHeader("Content-Disposition", "attachment; filename=\"query-result.pdf\"");
                        IOUtils.copy(pandocInputStream, resp.getOutputStream());

                        int exitCode;
                        try {
                            exitCode = pandocProcess.waitFor();
                        } catch (InterruptedException e) {
                            throw new IOException("Failed to wait for pandoc process completion.", e);
                        }

                        if (exitCode != 0) {
                            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new WebServer();
    }
}

