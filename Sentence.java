import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import org.ejml.simple.SimpleMatrix;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class Sentence {
        private String text;
        private String author;
        private String timestamp;

        public String toString() {
                String collect = "";
                collect += "{author:" + this.author + ", sentence:\"" + this.text + "\", timestamp:\"" + this.timestamp + "\"}";
                return collect;
        }

        public Sentence(String text, String author, String timestamp) {
                this.text = text;
                this.author = author;
                this.timestamp = timestamp;
        }

        public String getText() {
                return text;
        }
        
        public String setText(String text) {
                this.text = text;
                return text;
        }
        
        public String getAuthor() {
                return author;
        }
        
        public String setAuthor(String author) {
                this.author = author;
                return author;
        }
        
        public String getTimestamp() {
                return timestamp;
        }
        
        public String setTimestamp(String timestamp) {
                this.timestamp = timestamp;
                return timestamp;
        }

        public static Sentence convertLine(String line) {
                ArrayList<String> pieces = new ArrayList<>();
                String store = "";
                boolean seen = false;
                int letter = 0;
                int index = 0;
                while (letter < line.length()) {
                        if (line.charAt(letter) == '\"') {
                                seen = !seen;
                        }
                        if (seen == false && index < 7) {
                                if (line.charAt(letter) == ',') {
                                        pieces.add(store);
                                        store = "";
                                        index++;
                                } else {
                                        store += line.charAt(letter);
                                }
                        } else {
                                store += line.charAt(letter);
                        }
                        letter++;
                }
                
                pieces.add(store);
                String date = pieces.get(2);
                String username = pieces.get(4);
                String tweet = pieces.get(7);

                tweet = tweet.replaceAll("\\.", "").replaceAll("\\,", "").replaceAll("\\/", "");
                tweet = tweet.replaceAll("\\;", "").replaceAll("\\'", "").replaceAll("\\<", "");
                tweet = tweet.replaceAll("\\>", "").replaceAll("\\?", "").replaceAll("\\:", "");
                tweet = tweet.replaceAll("\"", "").replaceAll("\\]", "").replaceAll("\\+", "");
                tweet = tweet.replaceAll("\\!", "").replaceAll("\\@", "").replaceAll("\\$", "");
                tweet = tweet.replaceAll("\\%", "").replaceAll("\\^", "").replaceAll("\\&", "");
                tweet = tweet.replaceAll("\\*", "").replaceAll("\\)", "").replaceAll("\\(", "");
            
                //System.out.println(date);
                String[] dateParts = date.split("/");
                int month = Integer.parseInt(dateParts[0]);
                String[] months = {"", "January", "February", "March", "April", "May", 
                        "June", "July", "August", "September", "October", "November", "December"};
                String monthName = months[month];
                dateParts[2] = "2020";
                String finalDate = monthName + " " + dateParts[1] + " " + dateParts[2];
                return new Sentence(tweet, username, finalDate);
        }

        public ArrayList<String> splitSentence() {
                String[] stopwords = {"a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", 
                        "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot", 
                        "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", 
                        "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", 
                        "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", 
                        "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", 
                        "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours ourselves", "out", "over", "own", "same", 
                        "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", 
                        "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", 
                        "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", 
                        "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", 
                        "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", 
                        "yourselves"};
                ArrayList<String> words = new ArrayList<String>();
                this.text = this.text.toLowerCase();
                String[] word = this.text.split(" ");

                ArrayList <String> stopwordsList = new ArrayList<>(Arrays.asList(stopwords));

                for (int i = 0; i < word.length; i++) {
                        if (!stopwordsList.contains(word[i])) {
                            words.add(word[i]);
                        }
                }
                return words;
        }

        public int getSentiment(){
            
                String tweet = this.text;
                Properties props = new Properties();
                props.setProperty("annotators", "tokenize, ssplit, pos, parse, sentiment");
                StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
                Annotation annotation = pipeline.process(this.text);
                CoreMap sentence = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                return RNNCoreAnnotations.getPredictedClass(tree);
                
        }

        

        public static boolean keep(String timestamp) {
                String temporalRange = "May 31 2020-June 02 2020";

                String [] splitDate = temporalRange.split("-");
                String startDateString = splitDate[0];
                String endDateString = splitDate[1];
                //System.out.println(temporalRange);
                //System.out.println(timestamp);
        
                try {
                        DateFormat formatter = new SimpleDateFormat("MMMM dd yyyy");
                        Date date = formatter.parse(timestamp);
                        Timestamp timeStampDate = new Timestamp(date.getTime());

                    //check if the sentence date is within the specified range
                    Timestamp startTimestamp =new Timestamp(formatter.parse(startDateString).getTime());
                    Timestamp endTimestamp= new Timestamp(formatter.parse(endDateString).getTime());  
                    if (timeStampDate.after(startTimestamp) && timeStampDate.before(endTimestamp)) {
                        System.out.println("Date is within the range.");
                        return true;
                    } else {
                       //System.out.println("Date is NOT within the range.");
                        return false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    System.out.println("Exception :" + e);
                    return false;
                }
        }
        

        /*public static boolean keep(String timestamp) {
                String temporalRange = "May 31 2009-Jun 02 2009";
                String [] splitDate = temporalRange.split("-");
                String startDate = splitDate[0];
                String endDate = splitDate[1];
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dddd/yyyy");

                try {
                        Date givenDate1 = dateFormat.parse(startDate);
                        Date givenDate2 = dateFormat.parse(endDate);
                        Date givenDate3 = dateFormat.parse(this.timestamp);
                        Timestamp startStamp = new Timestamp(givenDate1.getTime());
                        Timestamp endStamp = new Timestamp(givenDate2.getTime());
                        Timestamp tweetStamp = new Timestamp(givenDate3.getTime());
                                if(tweetStamp.compareTo(startStamp) + tweetStamp.compareTo(endStamp) == 0) {
                                        return true;
                                }
                } catch (ParseException e) {
                    e.printStackTrace();
                    System.out.println("Exception :" + e);
                    return false;
                }

                

        }
        */
        

}
