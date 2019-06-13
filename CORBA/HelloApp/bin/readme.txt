//start orbd
orbd -ORBInitialPort 1050 -ORBInitialHost localhost

//compile source code
javac HelloApp\*.java server\*.java client\*.java

//in src folder:
java server.HelloServer -ORBInitialPort 1050 -ORBInitialHost localhost
java client.HelloClient -ORBInitialPort 1050 -ORBInitialHost localhost
