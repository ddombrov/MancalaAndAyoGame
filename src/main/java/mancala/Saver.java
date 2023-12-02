package mancala;

/**
 * The following code is based off of code from lab 10 in CIS*2430
 */

import java.io.IOException;
import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Saver {

    private Path filepath;
    private Scanner fileScanner;
    private int lineNumber;
    protected ArrayList<String> contents;
    private static final long serialVersionUID = 5000;

    /**
     * Save an object to the file name given
     */
    public void saveObject(final Serializable toSave, final String filename) throws IOException {
        filepath = Path.of("./assets", filename);
        contents = new ArrayList<>();
    }

    public void write(final String saveObj) {
        contents.add(String.join("\n", contents));
        // return;
    }

    /**
     * Write the contents to the user's file system
     * 
     * Outputs the contents buffer to the file
     * 
     * @throws IOException If an IOException occurs
     */
    public void save() throws IOException {
        Files.write(filepath, contents);
    }

    public Serializable loadObject(final String filename) throws IOException {

        filepath = Path.of("./assets", filename);
        if (Files.notExists(filepath)) {
            throw new IOException("Could not open file '" + filepath + "'");
        }
        lineNumber = 0;
        fileScanner = new Scanner(Files.newInputStream(filepath));
        return null;
    }

    public List<String> getFileContents() throws IOException {
        return Files.readAllLines(filepath);
    }

    private String cleanString(final String rawString) {
        return rawString.trim();
    }

    /**
     * Read a single line from the file.
     * 
     * @return The next line in the file
     * @throws FileFormatException If there are no more lines to read
     */
    public String readLine() throws FileFormatException {
        try {
            lineNumber++;
            return cleanString(fileScanner.nextLine());
        } catch (NoSuchElementException err) {
            throw new FileFormatException("End of file reached unexpectedly");
        }
    }

    /**
     * Read an integer from the file.
     * 
     * @return The integer read from the file
     * @throws FileFormatException If the next line is not an integer or if there
     *                             are no more lines to read
     */
    public int readInt() throws FileFormatException {
        final String intLine = readLine();

        try {
            return Integer.parseInt(intLine);
        } catch (NumberFormatException err) {
            throw new FileFormatException(filepath, lineNumber, intLine + " could not be parsed as an integer");
        }
    }

    /**
     * Read a boolean from the file
     * 
     * @return The boolean read from the file
     * @throws FileFormatException If the next line is not a boolean or if there are
     *                             no more lines to read
     */
    public boolean readBoolean() throws FileFormatException {
        final String boolLine = readLine();

        if ("false".equalsIgnoreCase(boolLine)) {
            return false;
        }
        if ("true".equalsIgnoreCase(boolLine)) {
            return true;
        }

        throw new FileFormatException(filepath, lineNumber, boolLine + " could not be parsed as a boolean");
    }

    // private void loadListOption() {

    // String saveFileName = getUserLine("Enter file name to load");

    // try {
    // FileReader fr = new FileReader(saveFileName);

    // String listTitle = fr.readLine();

    // TodoList newList = new TodoList(listTitle);

    // int numItems = fr.readInt();
    // for (int i = 0; i < numItems; i++) {
    // String itemTitle = fr.readLine();
    // String itemDesc = fr.readLine();
    // LocalDateTime dueDate = fr.readDateTime();
    // int priority = fr.readInt();
    // boolean completed = fr.readBoolean();

    // newList.addItem(itemTitle, itemDesc, dueDate, priority);

    // if (completed) {
    // newList.completeItem(newList.getIndexByTitle(itemTitle));
    // }

    // }

    // this.list = newList;

    // System.out.println("List loaded from file '" + fr.getFilePath() + "'");

    // } catch (FileFormatException err) {
    // System.out.println(err.getMessage());
    // } catch (Exception err) {
    // System.out.println(err);
    // }

    // private void saveListOption(TodoList list) {
    // String saveFileName = getUserLine("Enter name for save file");
    // FileWriter fw = new FileWriter(saveFileName);

    // fw.write(list);

    // for (Item item : list) {
    // fw.write(item);
    // }

    // try {
    // fw.save();
    // } catch (IOException err) {
    // System.out.println(err.getMessage());
    // }

    // System.out.println("Saved to '" + fw.getFilePath() + "'");
    // }

    // }

}
