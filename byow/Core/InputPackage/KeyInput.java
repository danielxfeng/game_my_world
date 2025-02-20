package byow.Core.InputPackage;

import edu.princeton.cs.introcs.StdDraw;

/**
 * This class is used to represent the input by the keyboard.
 */
public class KeyInput implements InputMethods {
    @Override
    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return Character.toLowerCase(StdDraw.nextKeyTyped());
            }
        }
    }

    @Override
    public boolean possibleNextInput() {
        return true;
    }
}
