curl.exe --output scheduling.zip --url https://happycherry.net/updates/scheduling/scheduling.zip

powershell "Expand-Archive -Path scheduling.zip -DestinationPath %LocalAppData%/scheduling"

powershell "$WScriptShell = New-Object -ComObject WScript.Shell; $Shortcut = $WScriptShell.CreateShortcut(\"%UserProfile%/Desktop/scheduling.lnk\"); $Shortcut.TargetPath = \"%LocalAppData%/scheduling/scheduling.exe\"; $Shortcut.Save()"

del scheduling.zip
SET installerpath=%cd%\scheduling_installer.bat

cd /d "%UserProfile%\Desktop"
"scheduling.lnk"

del %installerpath%
