Included is the code and imported libraries for my sentiment analyzer:
  A machine learning program that takes a set of 150,000 tweets about COVID-19 and analyzes the sentiment of that tweet was towards coronavirus. 
  The large dataset was over a course of months to analyze the changing public perception on the virus.

The sentence class contains the code necessary to break down each individual tweet while the driver class contains the code necessary to run the tweets from the "Covid-19 Twitter Dataset (Apr-Jun 2020)".

You can analyze the changing sentiment by modifying the "temporalRange" string in the Sentence.java class inside the "keep" method at the bottom of the class.
By modifying the range I was able to analyze the changing sentiment towards the virus and understand the most commonly used terms during each week/month to understand the changing public perception.
  

The code provided can be compiled on your respective devices using the following commands for Windows: 

javac -classpath ".;.\stanford-corenlp-3.9.2.jar;.\ejml-0.23.jar;.\stanford-english-corenlp-2018-10-05-models.jar" *.java

java -classpath ".;.\stanford-corenlp-3.9.2.jar;.\ejml-0.23.jar;.\stanford-english-corenlp-2018-10-05-models.jar" Driver
