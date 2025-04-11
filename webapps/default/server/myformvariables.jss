

function CreateHtmlData(variables, session) { 

  var myHtml = "";

  var varValues = variables.split("&")
  var webPageName = varValues[0] // First array element has the web page name.
  var fName = new Array("","")
  var lName = new Array("","")
  var ccPassword = new Array("","")
  var message = new Array("","")
  
  myHtml += "<p> User: " + session.getAttribute("User") + "</p>"
  
  if( varValues.length > 1 ) { // Get variables values if they exists
    try { 
          function myFunction(myVar, index, arr) {
            if(myVar.substr(0, 5) === "fname") fName = arr[index].split("=")
            
            if(myVar.substr(0, 5) === "lname") lName = arr[index].split("=")
                    
            if(myVar.substr(0, 10) === "ccpassword") ccPassword = arr[index].split("=")
                   
            if(myVar.substr(0, 7) === "message") message = arr[index].split("=")                                
          }      
          
          varValues.forEach(myFunction)
    } finally {}
  }
 
  
  myHtml += "<p><h2>Variables And Values</h2></p>"
  myHtml += "<p>" + fName[0] + "=" + fName[1] + "</p>"
  myHtml += "<p>" + lName[0] + "=" + lName[1] + "</p>"
  myHtml += "<p>" + ccPassword[0] + "=" + ccPassword[1] + "</p>"
  myHtml += "<p>" + message[0] + "=" + message[1] + "</p>"
  
  session.setAttribute("User", fName[1])
  
  return(myHtml)
}

CreateHtmlData(webPageParams, session)

