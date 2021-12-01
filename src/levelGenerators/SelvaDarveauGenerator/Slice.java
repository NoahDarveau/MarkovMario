package levelGenerators.SelvaDarveauGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Slice {
    // Keeping track of "following" slices.
    private int totalFollow;
    private final Map<Slice, Integer> followTimes;

    // Keeping track of specific aspects of slices (pieces, the flag, and the start).
    private final char[] pieces;
    private boolean isFlag;
    private boolean isMario;

    // Basic constructor for a slice.
    public Slice() {
        pieces = new char[16];
        totalFollow = 0;
        followTimes = new HashMap<>();
        isFlag = false;
        isMario = false;
    }

    // Getters and setters.
    public void setFlag(boolean newFlag) {
        this.isFlag = newFlag;
    }
    public boolean getFlag() {
        return this.isFlag;
    }
    public void setMario(boolean newMario) {
        this.isMario = newMario;
    }
    public boolean getMario() {
        return this.isMario;
    }

    public char getChar(int index) { return this.pieces[index]; }
    public int getNextSlices() {
        return totalFollow;
    }

    // Converts a piece to its string counterpart.
    public String toString() {
        return String.valueOf(pieces);
    }

    // Replace the value at a given slice with another char.
    public void replaceChar(char newChar, int index) {
        this.pieces[index] = newChar;
    }

    // Adds the "following" slice to the current slice.
    public void addNextSlice(Slice slice) {
        if (followTimes.containsKey(slice)) {
            followTimes.put(slice, followTimes.get(slice) + 1);
        } else {
            followTimes.put(slice, 1);
        }
        totalFollow++;
    }

    // Gets a random slice using the Markov Chain.
    public Slice getMarkovSlice(Random rng) {
        int r = rng.nextInt(totalFollow);
        int sum = 0;

        for (Map.Entry<Slice, Integer> e : followTimes.entrySet()) {
            sum += e.getValue();
            if (r < sum) {
                return e.getKey();
            }
        }
        return null;
    }

    // Checks if a given block is a solid tile that can be walked on.
    private boolean isSolid(char c) {
        return  c == 'X' || c == '#' || c == '%' || c == '@' || c == '?' || c == '!' || c == ']' ||
                c == 'Q' || c == '1' || c == '2' || c == 'D' || c == 'S' || c == 'C' || c == 't' ||
                c == 'U' || c == 'L' || c == 'B' || c == '<' || c == '>' || c == '[' || c == 'T';
    }

    // Gets the height of the ground of a certain slice.
    public int getGroundHeight() {
        int height = -1;
        for (int i = 15; i >= 0; --i) {
            if (isSolid(pieces[i])) {
                height = i;
            }
        }

        return height;
    }



}