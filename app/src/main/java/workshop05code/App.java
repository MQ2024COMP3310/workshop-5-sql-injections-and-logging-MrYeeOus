package workshop05code;

import java.io.BufferedReader;
//Included for the logging exercise
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    private static final Logger logger = Logger.getLogger(App.class.getName());
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            logger.log(Level.SEVERE, e1.getMessage());
        }
    }

    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO,"Wordle created and connected.");
        } else {
            logger.log(Level.WARNING,"Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO,"Wordle structures in place.");
        } else {
            logger.log(Level.WARNING,"Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                logger.log(Level.CONFIG, line);
                wordleDatabaseConnection.addValidWord(i, line);
                i++;
            }

        } catch (IOException e) {
            System.out.println("Something went wrong. Sorry!");
            logger.log(Level.SEVERE, e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                if (guess.matches("^[a-z]{4}")) {
                    System.out.println("You've guessed '" + guess + "'.");

                    if (wordleDatabaseConnection.isValidWord(guess)) {
                        System.out.println("Success! It is in the the list.\n");
                    } else {
                        logger.log(Level.INFO, "Invalid guess: " + guess);
                        System.out.println("Sorry. This word is NOT in the the list.\n");
                    }

                } else {
                    System.out.println("Sorry. This word is NOT in the list.\n");
                    logger.log(Level.WARNING, "Invalid user sequence: " + guess);
                }
                    System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                    guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

    }
}