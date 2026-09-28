package org.jswebserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;

// Boots an embedded Jetty server hosting jswebserver, so the fat jar can be run directly with
// "java -jar" or "java -cp jar:jarlib/*" as jswebserver.sh does, with no external servlet container.
public class ServerLauncher {

  public static void main(String[] args) throws Exception {
    // jswebserver.sh (Linux) sets the JSWEBSRVRPORT env var; the Windows start script instead
    // passes -Djswebserver.port=..., since replacing only one env var via WMI on Windows would
    // wipe out the rest of the process's environment block (PATH, TEMP, etc).
    int port = Integer.parseInt(System.getProperty("jswebserver.port", System.getenv().getOrDefault("JSWEBSRVRPORT", "8080")));

    Server server = new Server(port);

    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/"); // root context, so req.getContextPath() is "" at runtime, as jswebserver expects
    context.addServlet(new ServletHolder(new jswebserver()), "/*");

    server.setHandler(context);
    server.start();

    System.out.println("jswebserver listening on port " + port);
    server.join();
  }

}
