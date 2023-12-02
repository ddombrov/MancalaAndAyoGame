package mancala;

import java.nio.file.Path;

public class FileFormatException extends Exception {
    private static final long serialVersionUID = 1;

    public FileFormatException() {
        super("The file is incorrectly formatted");
    }

    public FileFormatException(final String message) {
        super(message);
    }

    public FileFormatException(final Path filePath, final int lineNumber, String errorMessage) {
        super(filePath.toAbsolutePath().toString() + ":Line " + Integer.toString(lineNumber) + " " + errorMessage);
    }

}