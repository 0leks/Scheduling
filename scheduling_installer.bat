
SET installationpath=%LocalAppData%/scheduling


curl.exe --output schedulingjre.zip --url https://happycherry.net/updates/scheduling/schedulingjre.zip
powershell "Expand-Archive -Path schedulingjre.zip -DestinationPath %installationpath%"
move schedulingjre.zip %installationpath%

curl.exe --output %installationpath%/scheduling.exe --url https://happycherry.net/updates/scheduling/scheduling.exe
powershell "$WScriptShell = New-Object -ComObject WScript.Shell; $Shortcut = $WScriptShell.CreateShortcut(\"%UserProfile%/Desktop/scheduling.lnk\"); $Shortcut.TargetPath = \"%LocalAppData%/scheduling/scheduling.exe\"; $Shortcut.Save()"

SET installerpath=%cd%\scheduling_installer.bat

cd /d "%UserProfile%\Desktop"
"scheduling.lnk"

del %installerpath%
