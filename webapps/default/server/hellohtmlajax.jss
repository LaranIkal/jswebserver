

function CreateHtmlData(variables, session, response) { 

  var myHtml = "";

  var varValues = variables.split("&")
  var webPageName = varValues[0] // First array element has the web page name.
  var taskId = new Array("","")
  var valueSelected = new Array("","")
  var returnValue = ""
		
  //myHtml += "<p> User: " + session.getAttribute("User") + "</p>"
 
  if( varValues.length > 1 ) { // Get variables values if they exists
    try { 
      function myFunction(myVar, index, arr) {
        if(myVar.substr(0, 6) === "taskId") taskId = arr[index].split("=")
        
        if(myVar.substr(0, 13) === "valueSelected") valueSelected = arr[index].split("=")
                            
      }      
          
      varValues.forEach(myFunction)
    } finally {}
  }
 
  
		if( valueSelected[1] == 1 ) {
			returnValue = "ConstantValue"
		}	else if( valueSelected[1] == 2 ) {
			returnValue = "QueryField"
		}	else if( valueSelected[1] == 3 ) {
			returnValue = "CrossReference"
		} else if( valueSelected[1] == 4 ) {
			returnValue = "TransformationScript"
		}

  myHtml += "<p><b>Value Selected: </b>" + returnValue + "</p>"
  
 
  // session.setAttribute("User", fName[1])
  response.setContentType("text/html")
  
  return(myHtml)
}

CreateHtmlData(webPageParams, session, response)

