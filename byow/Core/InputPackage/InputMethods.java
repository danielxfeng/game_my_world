package byow.Core.InputPackage;

/**
 * This interface is used to represent the input commands.
 */
public interface InputMethods {
    /**
     * Return the next input.
     */
    char getNextKey();

    /**
     * Return if there is a next input.
     */
    boolean possibleNextInput();
}
