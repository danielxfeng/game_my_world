package byow.Core.InputPackage;

/**
 * This class is used to represent the input by a string.
 */
public class StrInput implements InputMethods{

    /**
     * Store the input string.
     */
    private final String str;
    /**
     * The current index of the string.
     */
    private int index;

    public StrInput(String str) {
        this.str = str;
        this.index = 0;
    }

    @Override
    public char getNextKey() {
        char c =  Character.toLowerCase(this.str.charAt(this.index));
        this.index++;
        return c;
    }

    @Override
    public boolean possibleNextInput() {
        return index < str.length();
    }
}
