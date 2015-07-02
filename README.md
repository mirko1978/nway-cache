Getting started
===============

N-Way cache is a java 8 library that uses maven.

Documentation
==============

The documentation is provided inside the maven site.  
A copy is also available on the follow links:  
       
* [Usage](docs/usage.md)
* [Design](docs/design.md)
* [Getting started](docs/getting_started.md) 
* [Cache explained](docs/cache.md)
* [Diagrams](docs/diagrams.md)
* [NWay Example](docs/NWay_design.md)
* [PDF design document](docs/NWay_design.pdf)

Quick Start
===========

From a console:

java -jar nway-example-1.0.0-SNAPSHOT.jar

Prerequisite
============

* JDK 8: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html in the path
* JAVA_HOME set to JDK 8 (com.thetradedesk.cache.example: `set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_45`)
* Maven https://maven.apache.org/download.cgi  3.3.3 installed somewhere in the path

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


Build from the source code (Make the jar)
=========================================

* Open the console/shell
* Enter in the `nway` folder
* Execute `mvn package`

The jar will be found in the `target` sub-folder `nway-1.0-SNAPSHOT.jar`

Generate the website/reports
============================

* Open the console/shell
* Enter in the `nway` folder
* Execute `mvn site`

The site is in the `target\site` sub-folder

Clean the target folder
========================

* Open the console/shell
* Enter in the `nway` folder
* Execute `mvn clean`

How to run the example
=======================

* Open the console/shell
* Enter in the `nway` folder
* Execute `mvn install`
* Enter in the `nway-example` folder
* Execute `mvn package`
* Enter in `target`
* Execute `java -jar nway-example-1.0.0-SNAPSHOT.jar`

Now in about 10 seconds the application servers is running at port 8080.

* Open your browser at http://localhost:8080

For any question you can reach me on [GitHub](https://github.com/mirko1978)

## License

Copyright 2015 Mirko Bernardoni

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
