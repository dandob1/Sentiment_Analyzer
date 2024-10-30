public class ExtractTest {
        public static void main(String[] args) {

       String line = "0,23,4/19/2020 0:00,,MichelleCarbert,,\"Ottawa, Ontario\",horribl tragedi; nova scotia today famili get closur pandem funer.:(";
       

		Sentence expected = new Sentence("horribl tragedi nova scotia today famili get closur pandem funer", 
			"MichelleCarbert", "April 19 2020");
		Sentence result = Sentence.convertLine(line);
		System.out.println("test1: " + expected.getText().equals(result.getText()));
		System.out.println("test2: " + expected.getAuthor().equals(result.getAuthor()));
		System.out.println("test3: " + expected.getTimestamp().equals(result.getTimestamp()));

		line = "0,92,4/19/2020 0:00,,dandouglas33,JoeNBC,,januari nd total control one person come china control go";
		expected = new Sentence("januari nd total control one person come china control go", 
			"dandouglas33", "April 19 2020");
		result = Sentence.convertLine(line);
		System.out.println("test4: " + expected.getText().equals(result.getText()));
		System.out.println("test5: " + expected.getAuthor().equals(result.getAuthor()));
		System.out.println("test6: " + expected.getTimestamp().equals(result.getTimestamp()));





        }
}
