@rem ##### UPDATE THESE VARIABLES FOR EACH NEW COMPUTER #####
set LAUNCH4J="C:\Program Files (x86)\Launch4j\launch4jc.exe"




%LAUNCH4J% config.xml

python get_hashes.py

java -jar \workspace\Launcher\SignTool.jar -sign scheduling.exe privatekey
java -jar \workspace\Launcher\SignTool.jar -sign latesthashes.textfile privatekey
