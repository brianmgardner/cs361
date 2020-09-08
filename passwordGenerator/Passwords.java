import java.io.FileNotFoundException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;


public class Passwords {

    // 26 x 26 table with the frequency with which each letter follows each other letter in the text
    static int[][] followersTable = new int[26][26];
    // the total counts of each letter (minus the ending letters of words since you didn't record any follower for them)
    static int[] counts = new int[26];
    // the count of the number of times each letter begins a word
    static int[] starters = new int[26];
    // offset for indexing the ascii value of chars into followersTable
    final static int OFFSET = 97;


    // Scan the reference text, count the frequency combos, and store into followersTable
    public static void parseText(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        // loop thru the input text line by line
        while (sc.hasNextLine()) {
            // convert everything to lowercase for simplicity, remove apostrophes, and replace any chars outside of the alphabet with spaces
            String curLine = sc.nextLine().toLowerCase().replaceAll("'","").replaceAll("[^a-z]+", " ");
            // loop thru the current line while incrementing frequency combos
            for (int i = 0; i < curLine.length() - 1; i++) {
                char curChar = curLine.charAt(i);
                char nextChar = curLine.charAt(i + 1);
                // ignore spaces
                if (curChar != ' ' && nextChar != ' ') {
                    counts[(int) curChar - OFFSET]++;
                    followersTable[(int) curChar - OFFSET][(int) nextChar - OFFSET]++;
                    // curChar is the start of a new word
                    if (i > 0 && curLine.charAt(i - 1) == ' ')
                        starters[(int) curChar - OFFSET]++;
                }
            }
        }
    }

    // take in an array of 26 ints and return a new array where each respective index contains the rounded probability out of 100
    private static int[] getProbabilities(int[] ar) {
        assert(ar.length == 26);
        int total = 0;
        for (Integer val : ar)
            total += val;
        int[] res = new int[ar.length];
        for (int i = 0; i < res.length; i++) {
            double prob = 1.0 * ar[i] / total;
            res[i] = (int) Math.round(prob * 100);
        }
        return res;
    }


    // Returns a weighted, randomized char based off of probs[]
    private static char getRandomWeightedChar(int[] probs, Random rand) {
        ArrayList<Character> bag = new ArrayList<>(100);
        // for every instance of a char in probs[], add it once to the bag of 100 chars
        for (int i = 0; i < probs.length; i++) {
            for (int j = 0; j < probs[i]; j++) {
                bag.add((char) (i + OFFSET));
            }
        }
        return bag.get(rand.nextInt(bag.size()));
    }


    // print out the FollowersTable in a readable format
    private static void printFollowersTable() {
        // print top row of letters
        System.out.print("    ");
        for (int r = 0; r < followersTable.length; r++) {
            char letter = (char) (r + OFFSET);
            System.out.printf("%5C", letter);
        }
        System.out.println();
        for (int r = 0; r < followersTable.length; r++) {
            char letter = (char) (r + OFFSET);
            System.out.printf("%C:  ", letter);
            for (int c = 0; c < followersTable[r].length; c++) {
                int amt = followersTable[r][c];
                System.out.printf("%5d", amt);
            }
            System.out.println();
        }
        System.out.println();
    }


    public static void main(String[] args) throws FileNotFoundException {
        String fileName = args[0];
        int N = Integer.parseInt(args[1]);
        int k = Integer.parseInt(args[2]);

        Random rand = new Random();

        parseText(new File(fileName));

        ArrayList<String> passwords = new ArrayList<>(N);
        // generate N passwords
        for (int pwIndex = 0; pwIndex < N; pwIndex++) {
            char[] curPW = new char[k];
            curPW[0] = getRandomWeightedChar(getProbabilities(starters), rand);
            // each password is k characters long
            for (int charIndex = 1; charIndex < k; charIndex++) {
                curPW[charIndex] = getRandomWeightedChar(getProbabilities(followersTable[(int) (curPW[charIndex - 1]) - OFFSET]), rand);
            }
            passwords.add(String.valueOf(curPW));
        }

        printFollowersTable();

        // print out the passwords
        System.out.println("Passwords are:");
        for (String pw : passwords) {
            System.out.println(" " + pw);
        }
    }
}
