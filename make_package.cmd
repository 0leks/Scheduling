@rem ##### UPDATE THESE VARIABLES FOR EACH NEW COMPUTER #####
set OK_LAUNCHER=\workspace\launcher
set JAVA_HOME="C:\Program Files\Java\jdk-15.0.2"
set LAUNCH4J="C:\Program Files (x86)\Launch4j\launch4jc.exe"



rmdir /s /q schedulingjre
%JAVA_HOME%\bin\jlink.exe --module-path %JAVA_HOME%\jmods;bin;%OK_LAUNCHER%\bin --add-modules ok.scheduling,ok.launcher --output schedulingjre
%LAUNCH4J% config.xml

@rem certutil -hashfile scheduling.exe SHA512 | find /i /v "sha512" | find /i /v "certutil" > latest.textfile
@rem certutil -hashfile schedulinglauncher.exe SHA512 | find /i /v "sha512" | find /i /v "certutil" >> latest.textfile
powershell "Compress-Archive -Force -Path schedulingjre schedulingjre.zip"
python get_hashes.py

java -jar .\SignTool.jar -sign scheduling.exe privatekey
java -jar .\SignTool.jar -sign schedulingjre.zip privatekey
java -jar .\SignTool.jar -sign latesthashes.textfile privatekey

@rem ren Scheduling.exe Scheduling%1.exe
@rem del Scheduling*.zip
@rem powershell "Compress-Archive -Path scheduling.exe,schedulinglauncher.exe,jre Scheduling.zip"

@rem certutil -hashfile scheduling.zip SHA512 | find /i /v "sha512" | find /i /v "certutil" > latest.textfile
