
"D:\ProgramFiles\Java\jdk-15.0.1\bin\jlink.exe" --module-path "%JAVA_HOME%\jmods";bin --add-modules ok.scheduling --output jre
"D:\Program Files\Launch4j\launch4jc.exe" config.xml

ren Scheduling.exe Scheduling%1.exe
del Scheduling*.zip
powershell "Compress-Archive -Path Scheduling%1.exe,jre Scheduling%1.zip"

