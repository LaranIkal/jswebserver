
function updatepage( str, DivFieldToUpdate )
{		
	document.getElementById(DivFieldToUpdate).innerHTML = str;
}


function xmlhttpPostValueType(urlToPost, formId, taskId, selectFieldId, targetDivFieldId) {   
		var xmlHttpReq = false
		var self = this
		var qstr = ""

		var selectedIndexValue = document.getElementById(selectFieldId).selectedIndex
	
		TheTimeOut = setTimeout("document.body.style.cursor='wait'", 1)
		try {	
			document.getElementById(targetDivFieldId).innerHTML = "Processing, please wait..."
			
			if (window.XMLHttpRequest) {
				self.xmlHttpReq = new XMLHttpRequest() // Mozilla/Safari/Chrome
			} else if (window.ActiveXObject) {
				self.xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP") // IE
			}
			
			self.xmlHttpReq.open('POST', urlToPost, true)
			self.xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
			self.xmlHttpReq.onreadystatechange = function() {
				if (self.xmlHttpReq.readyState == 4) {
					updatepage(self.xmlHttpReq.responseText, targetDivFieldId)
					clearTimeout(TheTimeOut)
					document.body.style.cursor='default'
				}
			}
		
			qstr = 'taskId=' + escape(taskId)  // NOTE: no '?' before querystring
			qstr += '&valueSelected=' + escape(selectedIndexValue)    
      //alert("Variables:" + qstr)
      
			self.xmlHttpReq.send(qstr)
		} catch(err){clearTimeout(TheTimeOut); document.body.style.cursor='default';}
}
	
	
