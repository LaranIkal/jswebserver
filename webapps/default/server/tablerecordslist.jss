
load('webapps/default/config.jss')
load('webapps/default/server/lib/Utils.jss')

function CreateHtmlData(webPageParams) { 

  var webPageParamsArray = webPageParams.split("&")
  var webPageName = webPageParamsArray[0].split("/")[1] // First array element has the web page name.

  var myHtml = "<!DOCTYPE html>\
      <html>\
      <head>\n\
        <script type=\"text/javascript\" src=\"/" + webPageName + "/client/js/hellohtml.jsc\"></script>\n\
        <link rel=\"stylesheet\" type=\"text/css\" href=\"/" + webPageName + "/client/css/hellohtml.css\">\n\
      </head>\n\
      <body style=\"margin-left: 3px; margin-top: 0px;\">\n"

  myHtml += "\n\
        <!-- Using a CSS to build a container for the title -->\n\
        <div class=\"container\">\n\
          <div style=\"display: inline-block; margin-left:3px;\">\n\
            <a href=\"/" + webPageName + "/hellohtml\" target=\"_blank\" style=\"display: block; text-align: center;\">\n\
            <img src=\"/" + webPageName + "/client/img/icefox_blue.png\" name=\"logowhitefox\" border=\"0\" width=\"90\" height=\"30\"></a>\n\
          </div>\n\
          <div class=\"vertical-center\" style=\"display: inline-block; margin-left: 30;\">\n\
            <H2><font color=\"339966\">Listing Modules Table From SQLite DB.</font></H2>\n\
          </div>\n\
          <div>\n\
            <a href=\"/default/server/download/downloadtablerecordslist.jss\" style=\"text-decoration: none\"><p id=\"menu\" title=\"Click To Download List As csv File.\">Download List</p></a>\n\
          </div>\n\
        </div>\n"

  myHtml += "<p><table id=\"myTable\"><tbody>\
              <tr>\
                <th>TITLE</th>\
                <th>ACTIVE</th>\
              </tr>"

  var conn = getConnection("sqlite");
  var modulesListQuery = "SELECT title, active FROM eplsite_modules"

  try {
    var stmt = conn.prepareStatement(modulesListQuery)
    var resultSet = stmt.executeQuery()

    while (resultSet.next()) {
      myHtml += "<tr><td>" + resultSet.getString(1) + "</td>"
      myHtml += "<td>" + resultSet.getString(2) + "</td>"
      myHtml += "</tr>"            
    }

  } finally {
    if (resultSet)
      try { resultSet.close(); } catch(e) {}
    if (stmt)
      try { stmt.close();} catch (e) { print( e ); }
    if (conn)        
      try { conn.close(); } catch (e) { print( e ); }          
  }  

  myHtml += "</tbody></table></p>"

  // Add statistics link.
  myHtml += "<p><div id=\"StatTime\"></div></p>\n\
            <div> Server Date/Time: <p id=\"refreshDateTime\"> </p></div>\n"

  myHtml += "</body></html>\n"
  
  return(myHtml) 

} 

CreateHtmlData(webPageParams)




