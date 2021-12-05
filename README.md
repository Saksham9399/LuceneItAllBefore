# LuceneItAllBefore
A Group Based Assignment as part of CS7IS3


Step 1: Go to root in administrative mode by:- sudo su -

Step 2: change directory to assignment folder cd LuceneItAllBefore

Step 3: Do an mvn clean and install mvn clean install

Step 4: Run the driver program to run the code mvn exec:java -Dexec.mainClass="driver_program"

if the driver programm throws and error detlete the index files from the root of the repository and do the whole process again.

Step 5: Run trec eval to get the map scores ./trec_eval/trec_eval qrels-assignment2.part1 results.txt
