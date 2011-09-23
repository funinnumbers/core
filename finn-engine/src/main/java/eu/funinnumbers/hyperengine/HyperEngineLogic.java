package eu.funinnumbers.hyperengine;


import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.hyperengine.eventwriter.EventManager;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;
import eu.funinnumbers.util.eventconsumer.EventConsumer;

import java.util.HashMap;
import java.util.Vector;


/**
 * The logic behind eu.funinnumbers.engine coordination.
 */
public class HyperEngineLogic implements Observer { //NOPMD

    /**
     * HashMap containing the players per game.
     */
    private HashMap<String, Vector<String>> gamesPlayersMap = new HashMap<String, Vector<String>>(); //NOPMD

    /**
     * Association of event type with game.
     */
    private HashMap<String, String> eventTypes = new HashMap<String, String>(); //NOPMD

    /**
     * Association of players to game.
     */
    private HashMap<String, String> playersMap = new HashMap<String, String>(); //NOPMD

    /**
     * The unique instance of HyperEngineLogic.
     */
    private static HyperEngineLogic thisLogic = null;

    /**
     * Tug of War game prefix.
     */
    public static final String TOW = "TOW";

    /**
     * Chromatize it game prefix.
     */
    public static final String CI = "CI";

    /**
     * Chromatize images game prefix.
     */
    public static final String CIMAGE = "CImage";

    /**
     * Hyper Station App game prefix.
     */
    public static final String HYPERAPP = "HA";

    /**
     * Magnetize words game prefix.
     */
    public static final String MAGNETIZE = "MW";

    private final HashMap<String, String> guardians;

    /**
     * Default constructor.
     */
    private HyperEngineLogic() {

        // Observe all coming events from engines
        EventConsumer.getInstance().addObserver(this);
        Logger.getInstance().debug("HyperEngineLogic has been created");

        // Fill in the eu.funinnumbers.games.
        gamesPlayersMap.put("TOW", new Vector());
        gamesPlayersMap.put("CImage", new Vector());
        gamesPlayersMap.put("MW", new Vector());
        gamesPlayersMap.put("CI", new Vector());
        gamesPlayersMap.put("HA", new Vector());

        eventTypes.put("TOWInit", "TOW");
        eventTypes.put("CImageinit", "CImage");
        eventTypes.put("MWinit", "MW");
        eventTypes.put("CIinit", "CI");
        eventTypes.put("HAinit", "HA");

        guardians = new HashMap<String, String>();
    }

    public static HyperEngineLogic getInstance() {
        synchronized (HyperEngineLogic.class) {
            // Check if an instance has already been created
            if (thisLogic == null) {
                // Create a new instance if not
                thisLogic = new HyperEngineLogic();
            }
        }

        // Return the EchoProtocolManager instance
        return thisLogic;
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param obs the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     *            method.
     */
    public void update(final Observable obs, final Object arg) {

        // Accept all events from Engines
        if (!(arg instanceof Event)) {
            return;
        }
        //Do your stuff with the event
        final Event event = (Event) arg;
        Logger.getInstance().debug("Event received from Engine ");

        //Add event to Event manager.
        EventManager.getInstance().writeEventToFile(event);
        Logger.getInstance().debug("type " + event.getType());

        // New player event
        if (eventTypes.containsKey(event.getType())) {
            Logger.getInstance().debug(event.getDescription() + " " + eventTypes.get(event.getType()));
            initEvent(event.getDescription(), eventTypes.get(event.getType()));
        }

        /*for (String game : gamesPlayersMap.keySet()) {
            // Player is playing the game for the first time.
            ;
        }*/
    }

    /**
     * Checks previous status of the player and adds the mac address to the corresponding game.
     *
     * @param playerMac  The mac address as String.
     * @param gamePrefix The prefix of the game as String.
     */
    public void initEvent(final String playerMac, final String gamePrefix) { //NOPMD
        // Update Player Map
        playersMap.put(playerMac, gamePrefix);

        String foundOnGame = "nogame";
        // Remove player from any other previous game
        for (String game : gamesPlayersMap.keySet()) {

            // Players considered active for this game
            final Vector players = gamesPlayersMap.get(game);

            if (players.contains(playerMac)) {
                foundOnGame = game;
                break;
            }
        }
        Logger.getInstance().debug("Player found on " + foundOnGame);

        if (foundOnGame.equals("nogame")) {

            gamesPlayersMap.get(gamePrefix).add(playerMac);
            Logger.getInstance().debug("Adding Player [" + playerMac + "] to " + gamePrefix);

            guardians.put(playerMac, gamePrefix);
            EventManager.getInstance().updateStats(checkStats());


        } else if (!foundOnGame.equals(gamePrefix)) {

            gamesPlayersMap.get(foundOnGame).remove(playerMac);
            Logger.getInstance().debug("Removing Player [" + playerMac + "] from " + foundOnGame);
            gamesPlayersMap.get(gamePrefix).add(playerMac);

            guardians.put(playerMac, gamePrefix);
            Logger.getInstance().debug("Adding Player [" + playerMac + "] to " + gamePrefix);
            EventManager.getInstance().updateStats(checkStats());

        }
    }

    private HyperStats checkStats() {
        return new HyperStats(gamesPlayersMap.get(TOW).size(), gamesPlayersMap.get(CI).size(),
                gamesPlayersMap.get(CIMAGE).size(), gamesPlayersMap.get(HYPERAPP).size(),
                gamesPlayersMap.get(MAGNETIZE).size(), guardians);
    }

    /**
     * Returns the list of active players of a specific game.
     *
     * @param gamePrefix the prefix of the game.
     * @return the list of players as Vector.
     */
    public Vector<String> getPlayersForGame(final String gamePrefix) {
        return gamesPlayersMap.get(gamePrefix);

    }

    /**
     * Returns the HashMap containing the active players per game.
     *
     * @return the HashMap containing the active players per game.
     */
    public HashMap<String, Vector<String>> getGamesPlayerMap() {
        return gamesPlayersMap;
    }

    public HashMap<String, String> getPlayersMap() {
        return playersMap;
    }

    /**
     * Returns the number of active players of a specific game.
     *
     * @param gamePrefix the prefix of the game.
     * @return the number of players.
     */
    public int getNumberOfPlayers(final String gamePrefix) {
        return gamesPlayersMap.get(gamePrefix).size();
    }

    /**
     * Handle events from TOW game.
     *
     * @param event The event to be processed.
     */
    private void handleTOW(final Event event) {
        Logger.getInstance().debug("Tug of War event.");

        // This is an Init event from a new player
        if (event.getType().equals("TOWinit")) {

            // Extract player's mac
            final String playerMac = event.getDescription().substring(0, event.getDescription().indexOf("$"));
            final String gamePrefix = "TOW";

            // Add the new Player to active players.
            initEvent(playerMac, gamePrefix);
        }
    }

    /**
     * Handle events from CImage game.
     *
     * @param event The event to be processed.
     */
    private void handleCIm(final Event event) {
        Logger.getInstance().debug("Chromatize Image event.");
    }

    /**
     * Handle events from MW game.
     *
     * @param event The event to be processed.
     */
    private void handleMW(final Event event) { //NOPMD
        Logger.getInstance().debug("Magnetize Words event.");

        // This is an Init event from a new player
        if (event.getType().equals("MWinit")) {

            final String playerMac = event.getDescription();
            final String gamePrefix = "MW";

            // Add the new Player to active players.
            initEvent(playerMac, gamePrefix);


            // This is a stop event from a player
        } else if (event.getType().equals("MWstop")) {
            final String playerMac = event.getDescription();
            // If player exists, remove him from MW game
            if (gamesPlayersMap.get("MW").contains(playerMac)) {
                gamesPlayersMap.get("MW").removeElement(playerMac);

            }

            // Other event regarding MW game
        } else if (event.getType().startsWith("MW")) {


        }
    }
}
