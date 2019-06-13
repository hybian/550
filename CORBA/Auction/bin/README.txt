//start orbd
orbd -ORBInitialPort 1050 -ORBInitialHost localhost

//in src folder:

//compile source code
javac AuctionApp\*.java server\*.java client\*.java

//run server&client
java server.AuctionServer -ORBInitialPort 1050 -ORBInitialHost localhost
java client.AuctionClient -ORBInitialPort 1050 -ORBInitialHost localhost
