package org.jswebserver;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
//import jakarta.servlet.http.Part;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.io.*;
import org.graalvm.polyglot.*;

@WebServlet("/*")
public class jswebserver extends HttpServlet {

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Set default content type to text/html
    resp.setContentType("text/html");
    super.service(req, resp);
  }



	@SuppressWarnings("unused")
  @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String requestURI = req.getRequestURI();
		String contextPath = req.getContextPath(); // Check why it is comming empty
    //Get the file asked in the URL, example: "/default/client/html/welcome.html" 
		String requestURIcontextPath = requestURI.substring(contextPath.length()); 
    // The full USL might be: http://localhost:9696/default/client/html/welcome.html

    HttpSession session = req.getSession();
    String webPageParams = "webapps" + requestURIcontextPath; // Set path and filename as first parameter

		resp.setContentType("text/html;");

		if (requestURIcontextPath == null || requestURIcontextPath.equals("/")) {
			String welcomePage = "<html>" + 
															"<head>" +
																"<title>jswebserver: a small web framework for JavaScript</title>" + 
															"</head>" + 
															"<body>" +
																"<h1>Welcome to JsWebServer. Check the readme on the project files.</h1>" +
                                "<h1>If you have the default webapp, you may click <a href=/default/client/html/myform.html target=\"blank\">here</a></h1>" +
															"</body>" +
															"</html>";
			resp.getWriter().write(welcomePage);
			return;
		}

		File file = new File("webapps", requestURIcontextPath); // webapps is the parent directory of the file.
		if (!file.exists() || file.isDirectory()) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
		}

    if( requestURIcontextPath.contains("/client/")) {
      resp.setContentType(Files.probeContentType(Paths.get(file.getAbsolutePath())));
      resp.setContentLengthLong(file.length());

      try (FileInputStream fis = new FileInputStream(file);
      OutputStream os = resp.getOutputStream()) {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
          os.write(buffer, 0, bytesRead);
        }
      }
    } else {
      Map<String, String[]> parameterMap = req.getParameterMap();
      Set<String> parameterNames = parameterMap.keySet();
      for (String paramName : parameterNames) {
        String[] paramValues = parameterMap.get(paramName); // Maybe the same param name has more than one value ==>> ('key1', ['value1', 'value2'])
        for (String paramValue : paramValues) {
          //resp.getWriter().println("Parameter: " + paramName + ", Value: " + paramValue);
          webPageParams += "&" + paramName + "=" + paramValue;
        }
      }
    
      if(requestURIcontextPath.contains("/download/")) {
        downloadFile(webPageParams, req, resp, session); // Reads and evaluates JavaScript program to download file
      } else {
        getPageResponse(webPageParams, req, resp, session); // Reads and evaluates JavaScript file
      }
      
    }

	}



  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String requestURI = req.getRequestURI();
		String contextPath = req.getContextPath(); // Check why it is comming empty
    //Get the file asked in the URL, example: "/default/client/html/welcome.html" 
		String requestURIcontextPath = requestURI.substring(contextPath.length()); 
    // The full USL might be: http://localhost:9696/default/client/html/welcome.html

		File file = new File("webapps", requestURIcontextPath); // webapps is the parent directory of the file.
		if (!file.exists() || file.isDirectory()) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
		}

    HttpSession session = req.getSession();
    String webPageParams = "webapps" + requestURIcontextPath; // Set path and filename as first parameter

    // Ensure the upload directory exists
    // File uploadDir = new File(UPLOAD_DIR);
    // if (!uploadDir.exists()) {
    //     uploadDir.mkdir();
    // }

    // Handle file upload
    // for (Part part : req.getParts()) {
    //     String fileName = part.getSubmittedFileName();
    //     if (fileName != null && !fileName.isEmpty()) {
    //         File file = new File(uploadDir, fileName);
    //         try (InputStream inputStream = part.getInputStream();
    //               FileOutputStream outputStream = new FileOutputStream(file)) {
    //             byte[] buffer = new byte[1024];
    //             int bytesRead;
    //             while ((bytesRead = inputStream.read(buffer)) != -1) {
    //                 outputStream.write(buffer, 0, bytesRead);
    //             }
    //         }
    //     }
    // }

    Map<String, String[]> parameterMap = req.getParameterMap();
    Set<String> parameterNames = parameterMap.keySet();
    for (String paramName : parameterNames) {
      String[] paramValues = parameterMap.get(paramName); // Maybe the same param name has more than one value ==>> ('key1', ['value1', 'value2'])
      for (String paramValue : paramValues) {
        //resp.getWriter().println("Parameter: " + paramName + ", Value: " + paramValue);
        webPageParams += "&" + paramName + "=" + paramValue;
      }
    }

    //resp.getWriter().println("File uploaded successfully!");
    if( requestURIcontextPath.contains("/download/")) {
      downloadFile(webPageParams, req, resp, session); // Reads and evaluates JavaScript program to download file
    } else {
      getPageResponse(webPageParams, req, resp, session); // Reads and evaluates JavaScript file
    }
  }  


//########################################################################
  //# Methods used by routes and actions
  //########################################################################

  /** Runs the logic for any web page.
   * It serves both, get and post HTTP methods.
   *
   *  @param webPageParams the web page name and variables 
   *  @throws IOException
   *  @throws FileNotFoundException 
   */

  protected void getPageResponse(String webPageParams, HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException, FileNotFoundException {
  //protected String getPageResponse(String webPageParams, HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws Exception {

    String jsServerFile = webPageParams.split("&")[0]; // Get the JavaScript path and filename to execute.

    Context jsContext = Context.newBuilder("js")
                        .allowAllAccess(true)
                        .allowHostAccess(HostAccess.ALL)
                        .allowIO(true)
                        .option("engine.WarnInterpreterOnly", "false")
                        .build();
      
    // Expose variables values as an array variable to JavaScript
    jsContext.getBindings("js").putMember("webPageParams", webPageParams);
    jsContext.getBindings("js").putMember("session", session);
    jsContext.getBindings("js").putMember("response", resp);
    jsContext.getBindings("js").putMember("request", req);  

    // Read and evaluate main JavaScript File.
    String myJsFile = "";
    String line;
    BufferedReader br = new BufferedReader(new FileReader(jsServerFile));
    while ((line = br.readLine()) != null) { myJsFile += line + "\n"; }
    br.close();

    String jsResponse = jsContext.eval("js", myJsFile).toString();

    //return(jsResponse);
    resp.getWriter().write(jsResponse);
  }



//########################################################################
  //# Methods used by routes and actions
  //########################################################################

  /** Runs the logic for any web page.
   * It serves both, get and post HTTP methods.
   *
   *  @param webPageParams the web page name and variables 
   *  @throws IOException
   *  @throws FileNotFoundException 
   */

   protected void downloadFile(String webPageParams, HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException, FileNotFoundException {
    //protected String getPageResponse(String webPageParams, HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws Exception {
  
      String jsServerFile = webPageParams.split("&")[0]; // Get the JavaScript path and filename to execute.
  
      Context jsContext = Context.newBuilder("js")
                          .allowAllAccess(true)
                          .allowHostAccess(HostAccess.ALL)
                          .allowIO(true)
                          .option("engine.WarnInterpreterOnly", "false")
                          .build();
        
      // Expose variables values as an array variable to JavaScript
      jsContext.getBindings("js").putMember("webPageParams", webPageParams);
      jsContext.getBindings("js").putMember("session", session);
      jsContext.getBindings("js").putMember("response", resp);
      jsContext.getBindings("js").putMember("request", req);  
  
      // Read and evaluate main JavaScript File.
      String myJsFile = "";
      String line;
      BufferedReader br = new BufferedReader(new FileReader(jsServerFile));
      while ((line = br.readLine()) != null) { myJsFile += line + "\n"; }
      br.close();
  
      String downloadFileName = jsContext.eval("js", myJsFile).toString();

      String[] filePathName = downloadFileName.split("/");
      String fileName = filePathName[filePathName.length-1];

      resp.setContentType("application/octet-stream");
      resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

      try (FileInputStream fileInputStream = new FileInputStream(downloadFileName);
        OutputStream outputStream = resp.getOutputStream()) {

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.flush();
      }      

    }

}
