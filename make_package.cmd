
"D:\ProgramFiles\Java\jdk-15.0.1\bin\jlink.exe" --module-path "%JAVA_HOME%\jmods";bin;"W:\Launcher" --add-modules ok.scheduling,ok.launcher --output jre
"D:\Program Files\Launch4j\launch4jc.exe" config.xml
"D:\Program Files\Launch4j\launch4jc.exe" config_launcher.xml

certutil -hashfile scheduling.exe SHA512 | find /i /v "sha512" | find /i /v "certutil" > latest.textfile
certutil -hashfile schedulinglauncher.exe SHA512 | find /i /v "sha512" | find /i /v "certutil" >> latest.textfile

@rem ren Scheduling.exe Scheduling%1.exe
del Scheduling*.zip
powershell "Compress-Archive -Path scheduling.exe,schedulinglauncher.exe,jre Scheduling.zip"

certutil -hashfile scheduling.zip SHA512 | find /i /v "sha512" | find /i /v "certutil" > latest.textfile
