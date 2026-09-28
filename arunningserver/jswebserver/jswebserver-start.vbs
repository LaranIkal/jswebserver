Option Explicit

' jswebserver-start.vbs
' Starts jswebserver completely hidden - no console window is ever created (javaw.exe, not java.exe),
' and no cmd.exe/.bat wrapper in between, so the PID saved below is the real java process, not a
' launcher shell around it. That's what makes jswebserver-stop.vbs able to stop it reliably.

Dim fso, wmi, scriptDir, pidFile, port, javaHome, javawPath
Set fso = CreateObject("Scripting.FileSystemObject")
scriptDir = fso.GetParentFolderName(WScript.ScriptFullName)
pidFile = scriptDir & "\jswebserver.pid"
port = "9595" ' must match the port your whitefoxsqltool / browser URL uses

javaHome = "C:\Apps\graalvmjdk25\bin" ' path to your GraalVM JDK's bin folder - edit this to match your install
javawPath = javaHome & "\javaw.exe"

Set wmi = GetObject("winmgmts:\\.\root\cimv2")

If Not fso.FileExists(javawPath) Then
  WScript.Echo "javaw.exe not found at " & javawPath & vbCrLf & "Fix the javaHome path at the top of this script."
  WScript.Quit 1
End If

Dim existingPid : existingPid = ReadPidFile()
If existingPid <> "" And IsAlive(existingPid) Then
  WScript.Echo "jswebserver is already running, ProcessID " & existingPid & "."
  WScript.Quit 1
End If
If fso.FileExists(pidFile) Then fso.DeleteFile pidFile ' stale pidfile left over from a crash

Dim commandLine
commandLine = Chr(34) & javawPath & Chr(34) & " -Djswebserver.port=" & port & _
              " --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow " & _
              "-cp jswebserver-1.1.jar;jarlib\* org.jswebserver.ServerLauncher"

Dim startup, processClass, processId, result
Set startup = wmi.Get("Win32_ProcessStartup").SpawnInstance_
startup.ShowWindow = 0 ' SW_HIDE - belt-and-suspenders; javaw already creates no window at all

Set processClass = wmi.Get("Win32_Process")
result = processClass.Create(commandLine, scriptDir, startup, processId)

If result <> 0 Then
  WScript.Echo "Failed to start jswebserver (Win32_Process.Create error " & result & ")."
  WScript.Quit 1
End If

fso.CreateTextFile(pidFile, True).Write processId

WScript.Echo "jswebserver started in the background, ProcessID " & processId & "." & vbCrLf & _
             "You can now use whitefoxsqltool - open http://localhost:" & port & " in your browser to confirm." & vbCrLf & _
             "Run jswebserver-stop.vbs to stop it."

Function ReadPidFile()
  ReadPidFile = ""
  If fso.FileExists(pidFile) Then ReadPidFile = Trim(fso.OpenTextFile(pidFile, 1).ReadAll())
End Function

Function IsAlive(pid)
  If pid = "" Then
    IsAlive = False
  Else
    IsAlive = (wmi.ExecQuery("Select ProcessId From Win32_Process Where ProcessId = " & pid).Count > 0)
  End If
End Function
