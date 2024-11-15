import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import java.util.Date;

public class Sentence {

    //body of tweet
    private String text;
    //author of tweet.
    private String author;
    //timestamp of tweet
    private String timestamp;

    public Sentence(String text, String author, String timestamp) {
        this.text = text;
        this.author = author;
        this.timestamp = timestamp;
    }

    public String toString() {
        return "{author:" + author + ", sentence:\"" + text + "\", timestamp:\"" + timestamp + "\"}";
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /// This will calculate the sentiment of each word on a scale of 1-5
    /// 1 for very negative, 2 for negative, 3 for neutral, 4 for positive, 5 for very positive
    /// @return int the sentiment
    public int getSentiment(StanfordCoreNLP pipeline) {
        String tweet = this.text;
        Annotation annotation = pipeline.process(tweet);

        //check if theres a sentence
        if (annotation.get(CoreAnnotations.SentencesAnnotation.class).isEmpty()) {
            return -1;
        }
        CoreMap sentence = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
        Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        return RNNCoreAnnotations.getPredictedClass(tree);
    }

    /// Check if a sentence's timestamp is in the range
    /// @param temporalRange the date the range can be between
    /// @return boolean whether or not it was in the range
    public boolean keep(String temporalRange) {
        try {
            String[] dates = temporalRange.split("-");
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd yyyy");

            Date startDate = formatter.parse(dates[0].trim());
            Date endDate = formatter.parse(dates[1].trim());
            Date tweetDate = formatter.parse(this.timestamp);
            return (tweetDate.equals(startDate) || tweetDate.after(startDate)) &&
                    (tweetDate.equals(endDate) || tweetDate.before(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /// Gets rid of any stopwords and splits the sentence
    /// @return ArrayList<String> any of the words that werent filtered out
    public ArrayList<String> splitSentence() {

        ArrayList<String> words = new ArrayList<>();

        // Remove punctuation and convert to lowercase
        String cleanText = text.replaceAll("[\\p{Punct}]", "").toLowerCase();
        String[] pieces = cleanText.split("\\s+");

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

        ArrayList<String> stopwordsList = new ArrayList<>();
        for (int i = 0; i < stopwords.length; i++) {
            String stopword = stopwords[i];
            stopwordsList.add(stopword);
        }

        for (String piece : pieces) {
            if (!piece.isEmpty() && !stopwordsList.contains(piece)) {
                words.add(piece);
            }
        }
        return words;
    }

    /// Converts a line of CSV data into a Sentence object by parsing the line into individual components.
    /// The method extracts the date, author, and text
    /// @param line the tweet line
    /// @return Sentence returns a sentence object
    public static Sentence convertLine(String line) {
        //store parsed pieces from the line
        ArrayList<String> pieces = new ArrayList<>();
        String store = "";
        boolean seen = false;
        int letter = 0;
    
        //loop through each character in the line
        while (letter < line.length()) {
            char currentChar = line.charAt(letter);
    
            if (currentChar == '\"') {
                //switch seen when we have a quote
                seen = !seen;
            } else if (currentChar == ',' && !seen) {
                //if a comma is found outside quotes, it's a delimiter
                pieces.add(store.trim());
                store = "";
            } else {
                //add the character to the current piece
                store += currentChar;
            }
            letter++;
        }
        //add the last piece after the loop ends
        pieces.add(store.trim());
    
        //each line needs to have at least 8 pieces
        if (pieces.size() < 8) {
            return null;
        }
    
        String date = pieces.get(2);
        String username = pieces.get(4);
        String tweet = pieces.get(7);
    
        //get rid of punctuation and convert to lowercase
        tweet = tweet.replaceAll("[\\.,/;'<>?:\"\\]\\+!@\\$%\\^&*()]", "").toLowerCase();
    
        //format the date from Month Day 2020
        String[] dateParts = date.split("/");
        if (dateParts.length < 2) {
            return null;
        }
        int monthIndex = Integer.parseInt(dateParts[0]);
        String[] months = { "", "January", "February", "March", "April", "May",
                "June", "July", "August", "September", "October", "November", "December" };
        String monthName;
        if (monthIndex >= 1 && monthIndex <= 12) {
            monthName = months[monthIndex];
        } else {
            return null;
        }
        dateParts[2] = "2020";
        String finalDate = monthName + " " + dateParts[1] + " " + dateParts[2];
    
        //return a new sentence with the the good information
        return new Sentence(tweet, username, finalDate);
    }
    
}