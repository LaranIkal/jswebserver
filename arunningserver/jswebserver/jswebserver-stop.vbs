Option Explicit

' jswebserver-stop.vbs
' Reads the PID written by jswebserver-start.vbs and terminates exactly that process - no
' guessing based on process name or command line, so it can't accidentally hit an unrelated java.exe.

Dim fso, wmi, scriptDir, pidFile, pid, results, p

Set fso = CreateObject("Scripting.FileSystemObject")
scriptDir = fso.GetParentFolderName(WScript.ScriptFullName)
pidFile = scriptDir & "\jswebserver.pid"
Set wmi = GetObject("winmgmts:\\.\root\cimv2")

If Not fso.FileExists(pidFile) Then
  WScript.Echo "jswebserver is not currently running!"
  WScript.Quit 1
End If

pid = Trim(fso.OpenTextFile(pidFile, 1).ReadAll())
Set results = wmi.ExecQuery("Select * From Win32_Process Where ProcessId = " & pid)

If results.Count = 0 Then
  fso.DeleteFile pidFile ' stale pidfile, nothing to kill
  WScript.Echo "jswebserver is not currently running!"
  WScript.Quit 1
End If

For Each p In results
  p.Terminate()
Next

fso.DeleteFile pidFile
WScript.Echo "jswebserver stopped, ProcessID " & pid & "."
