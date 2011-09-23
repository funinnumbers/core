package eu.funinnumbers.hyperengine;

import java.util.HashMap;

/**
 * Maintains the number of players per game.
 */
public class HyperStats {

    /**
     * number of players for TOW.
     */
    private final int tow;

    /**
     * number of players for CI.
     */
    private final int ci;

    /**
     * number of players for CImage.
     */
    private final int cimage;

    /**
     * number of players at Foyer.
     */
    private final int ha;

    /**
     * number of players for MW.
     */
    private final int mw;

    /**
     * Key: Mac Address, Value: current Game.
     */
    private final HashMap<String, String> guardians;

    /**
     * Default Constructor.
     *
     * @param tow    number of players for TOW.
     * @param ci     number of players for CI.
     * @param cimage number of players for CImage.
     * @param ha     number of players at Foyer.
     * @param mw     number of players for MW.
     */
    public HyperStats(final int tow, final int ci, final int cimage, final int ha, final int mw, final HashMap<String, String> guardians) {
        this.tow = tow;
        this.ci = ci;
        this.cimage = cimage;
        this.ha = ha;
        this.mw = mw;
        this.guardians = guardians;
    }

    public int getTow() {
        return tow;
    }

    public int getCi() {
        return ci;
    }

    public int getCimage() {
        return cimage;
    }

    public int getHa() {
        return ha;
    }

    public int getMw() {
        return mw;
    }


    public HashMap<String, String> getGuardians() {
        return guardians;
    }
}
