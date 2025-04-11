Dim WinScriptHost
Set WinScriptHost = CreateObject("WScript.Shell")
WinScriptHost.Run Chr(34) & "jswebserver.bat" & Chr(34), 0

'#CarlosKassab - Sep/12/2021 - Add next message after jswebserver started:
jswebserverMsg = "jswebserver started in the background, now you can use whitefoxsqltool, "
jswebserverMsg = jswebserverMsg & "open the web page http://localhost:9595 in your browser to confirm."
jswebserverMsg = jswebserverMsg & vbCrLf & "You can close the command window you used to run jswebserver."
WScript.Echo jswebserverMsg

Set WinScriptHost = Nothing
