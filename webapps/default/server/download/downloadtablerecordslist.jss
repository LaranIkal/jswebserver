
load('webapps/default/config.jss')
load('webapps/default/server/lib/Utils.jss')

function CreateHtmlData(webPageParams) { 

  var webPageParamsArray = webPageParams.split("&")
  var webPageName = webPageParamsArray[0].split("/")[1] // First array element has the web page name.
  var webPagePath = "webapps/" + webPageName

  try {
    // Get FileName
    var now = new Date()
    var strDateTime = [now.getFullYear(), now.getMonth() + 1, now.getDate(),   
                      now.getHours(), now.getMinutes(), now.getSeconds()].join("")
    // Concatenate program name and DateTime to get full new file name.
    var exportFileName = webPagePath + "/tmp/" + webPageParamsArray[0].split("/")[3] + strDateTime + ".csv"
    
    var FileWriter = Java.type("java.io.FileWriter")
    var fw = new FileWriter(exportFileName)

    // File header line
    fw.write("\"Title\",\"Active\"\n")
   
    var conn = getConnection("sqlite");
    var modulesListQuery = "SELECT title, active FROM eplsite_modules"
    var stmt = conn.prepareStatement(modulesListQuery)
    var resultSet = stmt.executeQuery()

    while (resultSet.next()) {
      fw.write("\"" + resultSet.getString(1) + "\"")
      fw.write(",\"" + resultSet.getString(2) + "\"\n")         
    }
    fw.close()

  } finally {
      if (resultSet)
        try { resultSet.close(); } catch(e) {}
      if (stmt)
        try { stmt.close();} catch (e) { print( e ); }
      if (conn)        
        try { conn.close(); } catch (e) { print( e ); }          
  } 
  
  return(exportFileName) 

} 

CreateHtmlData(webPageParams)




