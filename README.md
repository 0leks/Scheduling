# Scheduling

## TODO About this app

## Installation Instructions
- Download or copy scheduling_installer.bat to the desktop and double click it
- The scheduling_installer.bat script will:
    - download the executable and jre files from the server and place them in appdata
    - create a .lnk on the desktop to the app
    - delete itself


## Developer zone

### To update files:
- Export ok.schedule.Driver to scheduling.jar as a runnable jar
- run the make_package.cmd command (or make_package_nojre.cmd if no changes to the jre)
- delete latesthashes.textfile from the server
- upload scheduling.exe, scheduling.exe.sig, schedulingjre.zip, schedulingjre.zip.sig, latesthashes.textfile.sig to the server
- then upload latesthashes.textfile to the server (the reason to delete and split them up is to avoid race conditions should someone's app try to update while the files are getting uploaded. Until latesthashes.textfile is changed, noone will try to update)


### To update the certificate:
- run SignTool -generate (SignTool.jar is built from ok.launcher.util.SignTool.java)
- replace the public key in ok.launcher.Utils.PUBLIC_KEY
- copy private_key to the same directory as make_package.cmd


TOOO
- Needs ability to easily swap employee in position 4 and 6 for example
- Easier way to see distribution of position assignments (perhaps over the months)?
