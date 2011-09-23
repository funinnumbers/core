/*
* ActionProtocolManager.java
*
* Created on 20 ����� 2008, 3:43 ��
*
* To change this template, choose Tools | Template Manager
* and open the template in the editor.
*/

package eu.funinnumbers.guardian.communication.actionprotocol;

import eu.funinnumbers.guardian.util.HashMap;
import eu.funinnumbers.util.Logger;

/**
 * @author User
 */
public final class ActionProtocolManager {

    /**
     * Unique instance of this class.
     */
    private static ActionProtocolManager instance;

    /**
     * first hashmap action(type)-id.
     */
    private final HashMap typeMap;

    /**
     * second hashmap action(unique)-id.
     */
    private final HashMap actionMap;

    /**
     * Creates a new instance of ActionProtocolManager.
     */
    protected ActionProtocolManager() {
        typeMap = new HashMap();
        actionMap = new HashMap();

        // Start the Action Protocol listening thread
        Logger.getInstance().debug("Starting the Action Protocol Receiver");
        final Receiver actrcv = new Receiver();
        new Thread(actrcv).start();
    }

    /**
     * singleton design pattern.
     *
     * @return instance of ActionProtocolManager that is allways the same.
     */
    public static ActionProtocolManager getInstance() {
        synchronized (ActionProtocolManager.class) {
            if (instance == null) {
                instance = new ActionProtocolManager();
            }
        }

        return instance;
    }

    /**
     * Retrieve a GuardianAction from the map based on the type of the action.
     *
     * @param type the type of the GuardianAction.
     * @return the GuardianAction.
     */
    public AbstractGuardianAction getType(final int type) {
        if (typeMap.containsKey(new Integer(type))) { //NOPMD
            return (AbstractGuardianAction) typeMap.get(new Integer(type)); //NOPMD
        }
        return null;
    }

    /**
     * Set the GuardianAction in the map based on the type of the action.
     *
     * @param guardAct the Guardian action to add.
     */
    public void setType(final AbstractGuardianAction guardAct) {
        typeMap.put(new Integer(guardAct.getType()), guardAct); //NOPMD
    }

    /**
     * Retrieve an ActionSender from the map based on the key of the event.
     *
     * @param actionID the ID of the AbstractGuardianAction associated to the ActionSender.
     * @return the ActionSender
     */
    public Sender getActionProtocol(final int actionID) {
        if (actionMap.containsKey(new Integer(actionID))) { //NOPMD
            return (Sender) actionMap.get(new Integer(actionID)); //NOPMD
        }
        return null;
    }

    /**
     * inserts a key in the hashmap for the specific actionProtocol.
     *
     * @param action the ActionSender to insert.
     */
    public void addActionProtocol(final Sender action) {
        actionMap.put(new Integer(action.getAction().getID()), action);  //NOPMD
    }

    /**
     * Clear the HashMap with the Actions.
     */
    public void removeActions() {
        actionMap.clear();
    }

}
