

function CreateHtmlData(webPageParams, session, response) { 

  //response.sendRedirect("/default/server/indexredirected.jss") // Uncomment to see how redirection is working.

  var myHtml = "\
    <html>\
      <head>\
        <title>jswebserver: a small web framework for JavaScript</title>\
      </head>\
      <body>\
        <h1>Welcome to JsWebServer</h1>\
        <p>Here some samples:</p>\
        <a href=\"/default/client/html/myform.html\" target=\"_blank\" style=\"display: block; text-align: left;\">\
        Html form with Ajax, css and client JavaScript sample\
        </a><br>\
        <a href=\"/default/server/tablerecordslist.jss\" target=\"_blank\" style=\"display: block; text-align: left;\">\
        Server JavaScript with db table records display and file download sample\
        </a>\
      </body>\
    </html>\
  "
  
  return(myHtml)
}

CreateHtmlData(webPageParams, session, response)

