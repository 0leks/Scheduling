
"D:\ProgramFiles\Java\jdk-15.0.1\bin\jlink.exe" --module-path "%JAVA_HOME%\jmods";bin;"W:\Launcher" --add-modules ok.scheduling,ok.launcher --output jre
"D:\Program Files\Launch4j\launch4jc.exe" config.xml
"D:\Program Files\Launch4j\launch4jc.exe" config_launcher.xml

@rem certutil -hashfile scheduling.exe SHA512 | find /i /v "sha512" | find /i /v "certutil" > latest.textfile
@rem certutil -hashfile schedulinglauncher.exe SHA512 | find /i /v "sha512" | find /i /v "certutil" >> latest.textfile
python get_hashes.py

java -jar w:\Launcher\SignTool.jar -sign scheduling.exe privatekey
java -jar w:\Launcher\SignTool.jar -sign schedulinglauncher.exe privatekey
java -jar w:\Launcher\SignTool.jar -sign latesthashes.textfile privatekey

@rem ren Scheduling.exe Scheduling%1.exe
del Scheduling*.zip
powershell "Compress-Archive -Path scheduling.exe,schedulinglauncher.exe,jre Scheduling.zip"

@rem certutil -hashfile scheduling.zip SHA512 | find /i /v "sha512" | find /i /v "certutil" > latest.textfile
