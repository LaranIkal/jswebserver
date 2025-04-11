package org.jswebserver;

//import java.net.URL;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerLauncher {
	public static void main(String[] args) {

		//if (args.length != 1) {
		  // System.err.println("Usage: java -jar embedded-jetty.jar <port> <context-path>");
		//     System.exit(1);
		//}
		String contextPath = "/"; // args[1]; -- Can be switched to use args.

    Integer port = 9696;
    if(System.getenv("JSWEBSRVRPORT") != null) { port = Integer.parseInt(System.getenv("JSWEBSRVRPORT")); }

		// Create a basic Jetty server object that will listen on port 9696.
		Server server = new Server(port);

		// Create a WebAppContext to hold the servlet.
		WebAppContext context = new WebAppContext();
		context.setContextPath(contextPath);
		context.setResourceBase(".");
		//context.setDescriptor("src/main/webapp/WEB-INF/web.xml");

		// Add the content servlet to the context with dynamic path.
		ServletHolder servletHolder = new ServletHolder(new jswebserver());
		context.addServlet(servletHolder, "/*");

		// Set the context handler to the server.
		server.setHandler(context);

		try {
			// Start the server.
			server.start();
			System.out.println("Server started at http://localhost:" +  Integer.toString(port) + " contextPath:" + contextPath);
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
