package levelGenerators.SelvaDarveauGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Slice {

    // total number of followups
    private int totalFollow;

    // the blocks in this slice. pieces[0] is the top of the screen, pieces[15] is the bottom
    private char[] pieces;

    // a map that keeps track of each slice that can follow this one and the number of times that it did
    private Map<Slice, Integer> followTimes;

    // whether this slice contains a flag character
    private boolean isFlag;

    // whether this slice contains a Mario start character
    private boolean isMario;

    /**
     *  Basic constructor
     */
    public Slice() {
        pieces = new char[16];
        totalFollow = 0;
        followTimes = new HashMap<Slice, Integer>();
        isFlag = false;
        isMario = false;
    }


    /**
     * @return a string representing the blocks in this slice
     */
    public String toString() {
        return String.valueOf(pieces);
    }


    /**
     * Replaces the character at the index slice with the given character
     * @param newChar the character to add
     * @param index the index to replace with the input character
     */
    public void replaceCharacter(char newChar, int index) {
        this.pieces[index] = newChar;
    }


    /**
     * Specify whether this slice contains a flag ('F')
     * @param newFlag the new isFlag value
     */
    public void setFlag(boolean newFlag) {
        this.isFlag = newFlag;
    }


    /**
     * Returns whether this slice contains a flag ('F')
     * @return the value of isFlag
     */
    public boolean getFlag() {
        return this.isFlag;
    }


    /**
     * Specify whether this slice contains a Mario start location ('M')
     * @param newMario the new isMario value
     */
    public void setMario(boolean newMario) {
        this.isMario = newMario;
    }


    /**
     * Returns whether this slice contains a Mario start ('M')
     * @return the value of isMario
     */
    public boolean getMario() {
        return this.isMario;
    }


    /**
     * Get the character at the specified index in this slice
     * @param index the index of the piece you want
     * @return the char in that index
     */
    public char getChar(int index) {
        return this.pieces[index];
    }


    /**
     * totalFollow getter
     * @return the total number of followups to this slice
     */
    public int getTotalFollow() {
        return totalFollow;
    }


    /**
     * Add a followup slice to this one
     * @param slice the slice that follows this one
     */
    public void addFollowupSlice(Slice slice) {
        if (followTimes.containsKey(slice)) {
            followTimes.put(slice, followTimes.get(slice) + 1);
        } else {
            followTimes.put(slice, 1);
        }
        totalFollow++;
    }



    /**
     * Picks a random slice from its followups based on Markov Chain rules
     * @param rng a random number generator
     * @return the chosen slice
     */
    public Slice getRandomSlice(int rng) {
        return null;
    }


    /**
     * Checks whether a given block is solid
     * @param c the character to check
     * @return true if the character represents a solid block, false otherwise
     */
    public boolean isSolid(char c) {
        return  c == 'X' || c == '#' || c == '@' || c == '!' || c == 'B' || c == 'C' ||
                c == 'Q' || c == '<' || c == '>' || c == '[' || c == ']' || c == '?' ||
                c == 'S' || c == 'U' || c == 'D' || c == '%' || c == 't' || c == 'T';
    }


    /**
     * Finds the height of the lowest solid ground Mario can stand on
     * @return the height of the ground, starting from 0
     */
    public int getGroundHeight() {
        int ret = -1;

        for (int i = 15; i >= 0; i--) {
            if (isSolid(getChar(i))) {
                ret = i;
            }
            else {
                if (ret > -1) {
                    return ret;
                }
            }
        }

        return -1; //Getting here means we never found a solid block with air above it
    }
}