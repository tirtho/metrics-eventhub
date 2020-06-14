Prerequisites
1. Need Azure subscription and an Azure Event Hub created
2. Maven and Java installed in the development machine

For development in Eclipse, 
1. Open Eclipse in a new workspace
2. import 'Existing Maven Project'
3. select the folder (from this codebase), where you see the pom.xml file 

In order to build and run the standalone java program, run the following, from the home folder.
1. Make sure you create the Azure Event Hub and place the properties from it to the eventhub.properties file here.
2. From command line in the home folder, run 
	> mvn install
3. From command line in the home folder, run 
	> mvn package


If you are sending data to Event Hub run
> java -jar .\target\vm-metrics-to-eventhub-0.0.1-SNAPSHOT-jar-with-dependencies.jar META-INF/eventhub.properties send

If you are receiving data from Event Hub, run
> java -jar .\target\vm-metrics-to-eventhub-0.0.1-SNAPSHOT-jar-with-dependencies.jar META-INF/eventhub.properties receive
