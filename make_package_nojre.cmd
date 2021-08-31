
"D:\Program Files\Launch4j\launch4jc.exe" config.xml

@rem certutil -hashfile scheduling.exe SHA512 | find /i /v "sha512" | find /i /v "certutil" > latest.textfile
@rem certutil -hashfile schedulinglauncher.exe SHA512 | find /i /v "sha512" | find /i /v "certutil" >> latest.textfile
@rem powershell "Compress-Archive -Force -Path schedulingjre schedulingjre.zip"
python get_hashes.py

java -jar w:\Launcher\SignTool.jar -sign scheduling.exe privatekey
@rem java -jar w:\Launcher\SignTool.jar -sign schedulingjre.zip privatekey
java -jar w:\Launcher\SignTool.jar -sign latesthashes.textfile privatekey

@rem ren Scheduling.exe Scheduling%1.exe
@rem del Scheduling*.zip
@rem powershell "Compress-Archive -Path scheduling.exe,schedulinglauncher.exe,jre Scheduling.zip"

@rem certutil -hashfile scheduling.zip SHA512 | find /i /v "sha512" | find /i /v "certutil" > latest.textfile
