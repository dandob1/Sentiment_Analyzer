import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;



public class Driver {
                 public static void main(String[] args) {
                        String file = "Covid-19 Twitter Dataset (Apr-Jun 2020).csv";
                        String line;
                        ArrayList<Sentence> sentences = new ArrayList<>();
                        try (BufferedReader br =
                             new BufferedReader(new FileReader(file))) {
                                while ((line = br.readLine()) != null) {
                                        //System.out.println(line);
                                        Sentence s = Sentence.convertLine(line);  
                                        //System.out.println(s.getAuthor());
                                        sentences.add(s);
                                }      
                        } catch (Exception exception) {
                                System.out.println(exception);
                                exception.printStackTrace();
                        }
        
        
                        HashMap<String, Integer> individualWords = printTopWords(sentences);
        
        
                        Map.Entry<String, Integer> maxEntry = null;
                        for (Map.Entry<String, Integer> entry : individualWords.entrySet())
                                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                                        maxEntry = entry;
                                        int maxValueLen = maxEntry.getValue().toString().length();
                                        ArrayList <String> results = new ArrayList<String>();
                                for (Map.Entry set : individualWords.entrySet()){
                                        String value = set.getValue().toString();
                                        while(value.length() < maxValueLen)
                                                value = " " + value;
                                                results.add(value + " of " + set.getKey());
                                }
                                Collections.sort(results);
                                Collections.reverse(results);
                                for (int i = 0; i < results.size() && i < 100; i++) 
                                        System.out.println(results.get(i));
                                
                                //print sentiment for all the csv
                                for(int i = 0; i < sentences.size(); i++) {
                                        System.out.println("\n" + (sentences.get(i)));
                                        System.out.println("Sentiment: " + (sentences.get(i).getSentiment()));
                                }

                                //store the sentences within the date to a new array list
                                ArrayList<Sentence> good = new ArrayList<>();
                                for (int i = 0; i < sentences.size(); i++) {
                                        if (Sentence.keep(sentences.get(i).getTimestamp()) == true) {
                                                sentences.add(sentences.get(i));
                                        }
                                }
        
                                //print out the arraylist with the dates
                                for(int i = 0; i< good.size(); i++) {
                                        if (good.get(i).getText().length() > 0) {
                                                System.out.println(good.get(i));
                                                System.out.println(good.get(i).getSentiment());
                                        }
                                }
        
                }
      


       
        public static HashMap<String, Integer> printTopWords (ArrayList<Sentence> sentences) {
        	
                HashMap<String, Integer> individualWords = new HashMap<>();

                for (int i = 0; i < sentences.size(); i ++) {
                        Sentence sentence = sentences.get(i);
                        ArrayList<String> words = sentence.splitSentence();

                        for(int j = 0; j < words.size(); j++) {
                                if (individualWords.containsKey(words.get(j))) {
                                        individualWords.put((String) words.get(j), individualWords.get(words.get(j)) + 1);
                                } else {
                                        individualWords.put( (String) words.get(j), (Integer) 1);
                                }
                        }
                }
            
                //System.out.println(individualWords);
                return individualWords; 
        }

}
