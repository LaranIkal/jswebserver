# jswebserver
Create Web Applications With JavaScript on the Server And Client Side Using Your Own WebServer



===>>> What is jswebserver:
  - A very light web server app that works with Java 25 and up.
    It is based on Jetty  (https://jetty.org/index.html) and GRAALVM for
    Java(https://www.graalvm.org/java/) with JavaScript engine.

===>>> jswebserver install:

  1.- Download and install GraalVM Java JDK 25:

    1.1.- Go to https://www.graalvm.org/downloads/
    1.2.- Select your OS, Windows or Linux from the list.
    1.3.- Download the latest Java JDK 25 version in a ZIP file, normally if you click on the Download button, it will download the ZIP file.
    1.4.- Using the filemanager, navigate to the folder where you downloaded the ZIP file.
    1.5.- Right-click the ZIP file and select Extract All… (or use a tool like 7-Zip/WinRAR).
          You will have a folder similar to this folder name: graalvm-community-25.3.4.1+1.1
          To keep the path short, rename folder to:graalvmjdk25

    1.6.- Move the folder to a short location, like this: C:\Apps\graalvmjdk25

Note. To run the jswebserver you can use other JDK than GraalVM, like: https://adoptium.net/ JDK 25

Set Environment Variables on Windows

    Press Win + R, type sysdm.cpl, and press Enter.
    Go to the Advanced tab and click Environment Variables.
    Under System variables, find Path and click Edit.
    Click New and add the path to the GraalVM bin directory.
        Example: C:\Apps\graalvmjdk25\bin
    Click OK on all windows to save.


  2.- Uncompress jswebserver zip file to any directory of your choice.

  3.- Download the latest version of JDBC Drivers: Sample, Go to the download page and download the latest version of the driver. 
    At the time of this writing, the latest version for SQLite is:
    https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.34.0/sqlite-jdbc-3.34.0.jar

    * Store the sqlite jdbc jar into jswebserver/jarlib directory.

  Note. When you download a new JDBC driver, it maybe needed to restart jswebserver.

===>>> starting, and stopping jswebserver:
  Windows:
    - Open the file manager, go to the jswebserver directory
    ***- Open jswebserver-start.vbs and search for javaHome = "C:\Apps\graalvmjdk25" and set the correct value according your system.
    - Start jswebserver: Double click jswebserver-start.vbs
    ** Stop jswebserver: Double click jswebserver-stop.vbs

  * To check computer resources used, open the task manager, go to Details and look for javaw.exe
  We have seen that javaw.exe plus the web browser, jswebserver is using 370 MB RAM

  Linux:
    - Open the file manager, go to the jswebserver directory
    - Start jswebserver: Right-Click on the file manager window and open terminal, the run: ./jswebserver.sh start
    ** Stop jswebserver: in the jswebserver directory, Right-Click on the file manager window and open terminal, the run: ./jswebserver.sh stop

  
*** NEVER DELETE the .pid file from jswebserver directory, it is used to stop jswebserver.

===>>> Directories and files inside jswebserver:

  jarlib: Directory with all Java jars you need for your JavaScript
          server side scripts, like jdbc drivers, etc.
        
  webapps: Directory containing all webapps directories like the default one included
  

# Compiling the source code:

- Install Java from graalvm(https://www.graalvm.org/) community(https://github.com/graalvm/graalvm-ce-builds/releases), 
last jss version I used : Java GraalVM Community LTS 25
- Install Maven(https://maven.apache.org/download.cgi), last jss version, I used : Apache Maven 3.9.12
- Configure your path variable to run Java and Maven, example on Linux:

Add to your .bashrc, and restart your computer:

export PATH="$HOME/Apps/Maven/apache-maven-3.9.12/bin:$PATH"
export JAVA_HOME="$HOME/Apps/Java/graalvm-community-25.3.4.1+1.1"
export PATH="$JAVA_HOME/bin:$PATH"

Open the file manager and go to the directory where you have jss, right-(mouse)clik on empty space and open a terminal window.

Happily compile with: mvn clean package

- Ensure jswebserver is stopped.
- Open file manager and go to directory jswebserver/target
- Copy jar: jswebserver-1.1.jar to directory jswebserver/serversample/jswebserver
- Start jswebserver



