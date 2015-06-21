Getting started
===============

N-Way cache is a java 8 library that uses maven.

### Prerequisite

* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) in the path
* JAVA_HOME set to JDK 8 (org.mirko.cache.example: `set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_45`)
* [Maven](https://maven.apache.org/download.cgi) 3.3.3 installed somewhere in the path

You can test your java environment configuration launching the follow command

`mvn -v`

the output is something like
 
    C:\git>mvn -v
    Apache Maven 3.3.3 (7994120775791599e205a5524ec3e0dfe41d4a06; 2015-04-22T12:57:37+01:00)
    Maven home: C:\git\apache-maven-3.3.3\bin\..
    Java version: 1.8.0_45, vendor: Oracle Corporation
    Java home: C:\Program Files\Java\jdk1.8.0_45\jre
    Default locale: en_GB, platform encoding: Cp1252
    OS name: "windows 7", version: "6.1", arch: "amd64", family: "dos"


### Build from the source code (Make the jar)

* Open the console/shell
* Enter in the `nway` folder
* Execute `mvn package`

The jar will be found in the `target` sub-folder `nway-1.0-SNAPSHOT.jar`

### Generate the website/reports

* Open the console/shell
* Enter in the `nway` folder
* Execute `mvn site`

The site is in the `target\site` sub-folder

### Clean the target folder

* Open the console/shell
* Enter in the `nway` folder
* Execute `mvn clean`

### How to run the example

* Open the console/shell
* Enter in the `nway` folder
* Execute `mvn install`
* Enter in the `nway-example` folder
* Execute `mvn package`
* Enter in `target`
* Execute `java -jar nway-example-1.0.0-SNAPSHOT.jar`

Now in about 10 seconds the application servers is running at port 8080.

* Open your browser at http://localhost:8080
