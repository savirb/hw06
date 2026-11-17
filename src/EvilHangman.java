public class EvilHangman extends Hangman {

	public EvilHangman() {
		this("engDictionary.txt");
	}

	public EvilHangman(String filename) {
		super(filename);
		// same game as Hangman, but the solution works from the whole word list instead of one word
		solution = new EvilSolution(wordList);
	}
}
