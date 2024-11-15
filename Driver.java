import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.Properties;

public class Driver {
    public static void main(String[] args) {
        String file = "Covid-19 Twitter Dataset (Apr-Jun 2020).csv";
        //each individual tweet
        String line;
        //store sentences
        ArrayList<Sentence> sentences = new ArrayList<>();

        ///
        ///        TEMPORAL RANGE HERE
        /// 
        //Example of a shorter temporal range:
        //String temporalRange = "May 22 2020-May 24 2020";
        ///
        //Full temporal range:
        String temporalRange = "April 19 2020-May 24 2020";
        ///
        /// 
        /// 
        /// 

        //initialize the StanfordCoreNLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        //reading the CSV file and converting each line to a Sentence object
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                //convert line to sentence object
                Sentence s = Sentence.convertLine(line);
                if (s != null && s.keep(temporalRange)) {
                    sentences.add(s);
                }
            }
            //error
        } catch (Exception exception) {
            System.out.println("Error reading the file:");
            exception.printStackTrace();
        }

        //check if temporal range is out of bounds of tweets
        if (sentences.isEmpty()) {
            System.out.println("No tweets found within the temporal range: " + temporalRange);
            return;
        }

        //count word frequencies from all sentences
        HashMap<String, Integer> individualWords = printTopWords(sentences);

        //finding the maximum word count length
        Map.Entry<String, Integer> maxEntry = null;
        for (Map.Entry<String, Integer> entry : individualWords.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                //update max if another word with higher frequency is found
                maxEntry = entry;
            }
        }

        //determine the length of the highest word count
        int maxValueLen;
        if (maxEntry != null) {
            maxValueLen = maxEntry.getValue().toString().length();
        } else {
            //no entries found
            maxValueLen = 0;
        }        
        ArrayList<String> results = new ArrayList<String>();

        //sorting the top words
        for (Map.Entry<String, Integer> set : individualWords.entrySet()) {
            String value = set.getValue().toString();
            while (value.length() < maxValueLen) {
                value = " " + value;
            }
            results.add(value + " of " + set.getKey());
        }
        Collections.sort(results, Collections.reverseOrder());

        //printing the top 100 words
        System.out.println("Top 100 Words:");
        for (int i = 0; i < results.size() && i < 100; i++) {
            System.out.println(results.get(i));
        }

        Map<Integer, Integer> sentimentCounts = new HashMap<>();
        int totalSentences = sentences.size();
        //count how many words we are going throuhg
        int processed = 0;
        //after how many words we will print an update
        int progressInterval = 1000;

        //start analysis and provide updates along the way
        System.out.println("\nStarting Sentiment Analysis...");
        for (Sentence sentence : sentences) {
            //get sentiment
            int sentiment = sentence.getSentiment(pipeline);
            //increment that sentiments score
            sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            //increment amount we processed
            processed++;
            if (processed % progressInterval == 0) {
                //print update if its been 1000
                System.out.println("Processed " + processed + " out of " + totalSentences + " sentences...");
            }
        }

        //show sentiment counts
        System.out.println("\nOverall Sentiment Counts:");
        for (Map.Entry<Integer, Integer> entry : sentimentCounts.entrySet()) {
            System.out.println("Sentiment " + (entry.getKey() + 1) + ": " + entry.getValue());
        }

    }

    public static HashMap<String, Integer> printTopWords(ArrayList<Sentence> sentences) {
        HashMap<String, Integer> individualWords = new HashMap<>();

        for (int i = 0; i < sentences.size(); i++) {
            Sentence sentence = sentences.get(i);
            ArrayList<String> words = sentence.splitSentence();
            //count words frequence
            for (String word : words) {
                individualWords.put(word, individualWords.getOrDefault(word, 0) + 1);
            }
        }

        //map of word frequencies
        return individualWords;
    }
}
