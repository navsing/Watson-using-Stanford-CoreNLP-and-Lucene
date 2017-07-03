Hi all,

Based on popular request, here's a very quick set of instructions on how to use Maven for your project:

1. First, install Maven if you don't have it installed already. For this, follow the instructions in the "Installation" section of this document: https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html. Ignore everything else in the document! Also, I am assuming you have Java already installed.

2. Download the skeleton project for HW3 I added to D2L (under Content/Homeworks). This is preconfigured to include the Lucene 6 dependencies. OPTIONAL: if you're curious, open the pom.xml file, to see how dependency configuration works in maven. IMPORTANT NOTE: maven will download the actual jar files for you. You simply tell it what you need in the pom.xml file.

3. OPTIONAL: The configuration in the skeleton project in D2L should be sufficient for HW3. But if you want to add more libraries, first, search for them on http://search.maven.org/, and then copy their information in the pom.xml file.

4. Add your code to the file src/main/java/edu/arizona/cs/App.java

5. Compile with the command: "mvn compile". Note that the first time you compile, it will take longer, because maven will download all the dependencies you need. This happens just once. The result of the compilation is a target/ directory, which contains your classes.

6. Execute with the command: "mvn exec:java -Pprofile1”.

Another OPTIONAL step:
If your class is called something else, edit the pom.xml file and replace <mainClass>edu.arizona.cs.App</mainClass> with your class. Similarly, if your main class takes command line arguments, edit the <arguments> block below <mainClass>.

If you would like to run multiple classes through maven, create a different <profile> for each in the pom file. Please see the example currently in pom.xml.

If you prefer not to use the command line in steps 5 and 6, you can import your Maven project into Eclipse by going to File / Import / Existing Maven Project. (This path might be different depending on OS and Eclipse version; please post here if you see something else.)
