package org.jswebserver;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;

@WebServlet("/*")
public class jswebserver extends HttpServlet {

  // webapps is resolved once to an absolute, normalized path. Every requested file is checked
  // against it below, so a URL like "/default/../../../../etc/passwd" can never escape webapps.
  private static final Path WEBAPPS_ROOT = new File("webapps").getAbsoluteFile().toPath().normalize();

  // One GraalVM engine for the whole server's lifetime. It's not used to run any script itself -
  // each request still gets its own fresh Context for isolation (Contexts aren't safe to share
  // across concurrent threads, and sharing one would leak JS globals between requests/users).
  // What sharing the engine buys us is that every Context built from it reuses the engine's
  // already-warmed compiled code, so per-request Context creation stops paying JS engine startup cost.
  private Engine polyglotEngine;

  @Override
  public void init() throws ServletException {
    polyglotEngine = Engine.newBuilder("js", "regex").option("engine.WarnInterpreterOnly", "false").build();
  }

  @Override
  public void destroy() {
    if (polyglotEngine != null) polyglotEngine.close();
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html"); // default, overridden below for static files and downloads
    super.service(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getRequestURI().substring(req.getContextPath().length()); // e.g. "/default/client/html/welcome.html"

    if (path.isEmpty() || path.equals("/")) { resp.getWriter().write(welcomePage()); return; }

    File file = resolveFile(path, resp);
    if (file == null) return; // resolveFile() already sent the 404

    if (path.contains("/client/")) { serveStaticFile(file, resp); return; }

    handleDynamicRequest(path, req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getRequestURI().substring(req.getContextPath().length());

    if (resolveFile(path, resp) == null) return; // resolveFile() already sent the 404

    handleDynamicRequest(path, req, resp);
  }

//########################################################################
  //# Request routing helpers
  //########################################################################

  /** Resolves the requested path to a file under webapps and blocks any attempt to escape that
   *  directory (e.g. "../../etc/passwd"). Sends a 404 and returns null if the file doesn't exist,
   *  is a directory, or resolves outside webapps. */
  private File resolveFile(String path, HttpServletResponse resp) throws IOException {
    Path resolved = WEBAPPS_ROOT.resolve("." + path).normalize(); // "." + path keeps it relative to WEBAPPS_ROOT
    File file = resolved.toFile();
    if (!resolved.startsWith(WEBAPPS_ROOT) || !file.exists() || file.isDirectory()) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }
    return file;
  }

  /** Streams a static file under .../client/ (html, css, js, images, etc) as-is. */
  private void serveStaticFile(File file, HttpServletResponse resp) throws IOException {
    String contentType = Files.probeContentType(file.toPath()); // can be null for unknown/uncommon extensions
    resp.setContentType(contentType != null ? contentType : "application/octet-stream");
    resp.setContentLengthLong(file.length());
    try (FileInputStream fis = new FileInputStream(file); OutputStream os = resp.getOutputStream()) { fis.transferTo(os); }
  }

  /** Builds "webapps/<path>&param=value&..." from the request parameters, then dispatches to the
   *  .jss page runner or the file-download runner depending on the URL. Shared by GET and POST. */
  private void handleDynamicRequest(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
    StringBuilder webPageParams = new StringBuilder("webapps").append(path);
    for (Map.Entry<String, String[]> param : req.getParameterMap().entrySet()) {
      for (String value : param.getValue()) { webPageParams.append('&').append(param.getKey()).append('=').append(value); } // same param name can repeat: key=[v1, v2]
    }

    HttpSession session = req.getSession();
    if (path.contains("/download/")) { downloadFile(webPageParams.toString(), req, resp, session); }
    else { getPageResponse(webPageParams.toString(), req, resp, session); }
  }

  private String welcomePage() {
    return "<html><head><title>jswebserver: a small web framework for JavaScript</title></head>" +
           "<body><h1>Welcome to JsWebServer. Check the readme on the project files.</h1>" +
           "<h1>If you have the default webapp, you may click <a href=/default/client/html/myform.html target=\"blank\">here</a></h1>" +
           "</body></html>";
  }

//########################################################################
  //# Methods used by routes and actions
  //########################################################################

  /** Runs the requested .jss file and writes its return value as the HTTP response body. Serves both GET and POST. */
  protected void getPageResponse(String webPageParams, HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
    resp.getWriter().write(runJsFile(webPageParams, req, resp, session));
  }

  /** Runs the requested .jss file, which must return the path of the file to send, then streams
   *  that file back to the browser as an attachment. Serves both GET and POST. */
  protected void downloadFile(String webPageParams, HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
    String downloadFileName = runJsFile(webPageParams, req, resp, session);
    String fileName = downloadFileName.substring(downloadFileName.lastIndexOf('/') + 1);

    resp.setContentType("application/octet-stream");
    resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

    try (FileInputStream fis = new FileInputStream(downloadFileName); OutputStream os = resp.getOutputStream()) { fis.transferTo(os); }
  }

  /** Creates a fresh polyglot context from the shared engine (contexts are not thread-safe and must
   *  not be shared across requests, so each request gets its own and it's closed in a try-with-resources
   *  to avoid leaking native GraalVM resources), exposes request/response/session/webPageParams to
   *  JavaScript, then evaluates the target .jss file. */
  private String runJsFile(String webPageParams, HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
    String jsServerFile = webPageParams.split("&")[0]; // path and filename of the .jss file to execute

    try (Context jsContext = Context.newBuilder("js", "regex")
                        .engine(polyglotEngine)
                        .allowAllAccess(true)
                        .allowHostClassLookup(className -> true)
                        .build()) {

      jsContext.getBindings("js").putMember("webPageParams", webPageParams);
      jsContext.getBindings("js").putMember("session", session);
      jsContext.getBindings("js").putMember("response", resp);
      jsContext.getBindings("js").putMember("request", req);

      String source = Files.readString(Path.of(jsServerFile), StandardCharsets.UTF_8);
      return jsContext.eval("js", source).toString();
    }
  }

}
