import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * A Solution that never commits to a hidden word. It keeps every word that
 * still fits the revealed letters, and on each guess it keeps the largest
 * "word family" (the words sharing the same positions of the guessed letter).
 *
 * The inherited target field always holds one word from the current family,
 * so isSolved(), printProgress() and getTarget() are reused from Solution
 * unchanged, and addGuess() reuses Solution's code to reveal letters.
 */
public class EvilSolution extends Solution {

	private ArrayList<String> candidates;

	public EvilSolution(ArrayList<String> wordList) {
		// a random word sets the random length; Solution fills in the blanks for that length
		super(pickRandomWord(wordList));
		candidates = new ArrayList<>();
		for (String word : wordList) {
			if (word.length() == target.length()) {
				candidates.add(word);
			}
		}
	}

	@Override
	public boolean addGuess(char guess) {
		if (candidates.isEmpty()) {
			return false;
		}
		candidates = largestFamily(guess);
		target = candidates.get(0);
		// every word in the family has the guess in the same spots,
		// so revealing the letters of one of them reveals the family's pattern
		return super.addGuess(guess);
	}

	/**
	 * Splits the remaining words into families by where the guess appears and
	 * returns the largest family. Ties go to the family that reveals fewer
	 * letters (so the guess is more likely to count as wrong), and then to the
	 * family found first.
	 */
	private ArrayList<String> largestFamily(char guess) {
		Map<String, ArrayList<String>> families = new LinkedHashMap<>(); // keeps the order found
		for (String word : candidates) {
			String pattern = familyPattern(word, guess);
			ArrayList<String> family = families.get(pattern);
			if (family == null) {
				family = new ArrayList<>();
				families.put(pattern, family);
			}
			family.add(word);
		}

		ArrayList<String> best = null;
		int bestReveals = 0;
		for (Map.Entry<String, ArrayList<String>> entry : families.entrySet()) {
			ArrayList<String> family = entry.getValue();
			int reveals = countOccurrences(entry.getKey(), guess);
			boolean bigger = best == null || family.size() > best.size();
			boolean sameSizeFewerReveals = best != null && family.size() == best.size()
					&& reveals < bestReveals;
			if (bigger || sameSizeFewerReveals) {
				best = family;
				bestReveals = reveals;
			}
		}
		return best;
	}

	/** For example, "heal" with guess 'e' gives "_e__". */
	private static String familyPattern(String word, char guess) {
		StringBuilder pattern = new StringBuilder();
		for (int i = 0; i < word.length(); i++) {
			pattern.append(word.charAt(i) == guess ? guess : '_');
		}
		return pattern.toString();
	}

	private static int countOccurrences(String text, char letter) {
		int count = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == letter) {
				count++;
			}
		}
		return count;
	}

	/** Picking a random word (instead of a random length) favors lengths with more words. */
	private static String pickRandomWord(ArrayList<String> wordList) {
		if (wordList.isEmpty()) {
			return "";
		}
		return wordList.get(new Random().nextInt(wordList.size()));
	}
}
