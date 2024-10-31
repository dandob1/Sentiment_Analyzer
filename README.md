Included is the code and imported libraries for my sentiment analyzer:
  A machine learning program that takes a set of 150,000 tweets about COVID-19 and analyzes the sentiment of that tweet was towards coronavirus. 
  The large dataset was over a course of months to analyze the changing public perception on the virus.

Unfortunately the "stanford-english-corenlp-2018-10-05-models" cannot be uploaded to the repository due to githubs maximum file capacity. Even with LFS installed, github still won't allow the upload without purchasing extra storage.

The sentence class contains the code necessary to break down each individual tweet while the driver class contains the code necessary to run the tweets from the "Covid-19 Twitter Dataset (Apr-Jun 2020)".

You can analyze the changing sentiment by modifying the "temporalRange" string in the Sentence.java class inside the "keep" method at the bottom of the class.
By modifying the range I was able to analyze the changing sentiment towards the virus and understand the most commonly used terms during each week/month to understand the changing public perception.

I have attached images below of the output of the code as I cannot include all the necessary files to run it: 
![image](https://github.com/user-attachments/assets/6ef9a5a1-acb5-4573-ad90-3e00158a7348)
![image](https://github.com/user-attachments/assets/b447a54d-0b3f-4b20-b411-54fb9da8ebbf)
