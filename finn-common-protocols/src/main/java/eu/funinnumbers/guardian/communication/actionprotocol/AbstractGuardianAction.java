/**
 * AbstractGuardianAction.java
 *
 * Created on 29  2008, 3:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 **/

package eu.funinnumbers.guardian.communication.actionprotocol;

import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.StorableEntity;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.storage.StorageService;
import eu.funinnumbers.util.Logger;

import java.util.Vector;

/**
 * AbstractGuardianAction will be implemanted by actions in future eu.funinnumbers.games.
 */
public abstract class AbstractGuardianAction {

    /**
     * unique id per action.
     */
    private int actionID;

    /**
     * a vector containing the targets of the action.
     */
    private Vector targets;

    /**
     * indicates when an action is completed.
     */
    private boolean completed;

    /**
     * indicates the successful implementation of an action.
     */

    private boolean success;

    /**
     * instantiation of ActionSender.
     */
    private Sender action;

    /**
     * parametres of the action.
     */
    private String params;


    /**
     * Default constructor  of eu.funinnumbers.guardian action.
     */
    public AbstractGuardianAction() {
        // Initiate internal variables
        targets = new Vector(); // NOPMD
        completed = false;
        success = false;
        params = "";

        // Get exclusive access to maxID
        synchronized (AbstractGuardianAction.class) {

            try {
                if (!EchoProtocolManager.getInstance().isStation()) { //NOPMD
                    ActionID actID = null;
                    // Get stored ActionID Object
                    actID = (ActionID) StorageService.getInstance()
                            .listEntities(StorableEntity.ACTIONIDENTITY)
                            .firstElement();
                    // Get next ID
                    actionID = actID.increaseActionID();

                    StorageService.getInstance().add(actID);

                } else {
                    ActionID.actionID++;
                    actionID = ActionID.actionID;
                }

            } catch (Exception ex) {
                Logger.getInstance().debug("No ActionID object found in StorageService.", ex);
                final ActionID actID = new ActionID();
                actID.setActionID(1);
                actionID = 1;

                if (!EchoProtocolManager.getInstance().isStation()) {
                    StorageService.getInstance().add(actID);
                }
            }

            // Store the updated value;

        }
    }

    /**
     * Returns the ID of the current action.
     *
     * @return the ID of the action.
     */
    public final int getID() {
        return actionID;
    }

    /**
     * Sets the ID of the current action.
     *
     * @param actionId the ID of the action.
     */
    public final void setID(final int actionId) {
        actionID = actionId;
    }

    /**
     * Adds a target to participate in this action.
     *
     * @param target a Guardian that should participate in this action.
     */    
    public final void addTarget(final Guardian target) {
        targets.addElement(target);
    }

    /**
     * Adds a targets Vector to participate in this action .
     *
     * @param targetsParam the Vector containing the  Guardians that should participate in this action.
     */
    public final void addTargets(final Vector targetsParam) {
        this.targets = targetsParam;
    }

    /**
     * Returns a vector with the target neighbours, i.e. the guardians that will participate in this action
     *
     * @return a vector of Neighbours.
     */
    public final Vector getTargets() {
        return targets;
    }

    /**
     * @return parameters.
     */
    public String getParams() {
        return params;
    }

    /**
     * @param parameter of the action are set here.
     */
    public void setParams(final String parameter) {
        this.params = parameter;
    }

    /**
     * This method initiates the action.
     */
    public final void actionPerformed() {
        // Make first check
        if (checkAction()) {
            /* We want to do this actions
             * Agree with all targets to commit to this actions.
             */
            action = new Sender(this);
            action.run();
//            if (this.isSuccess()) {
//                FinnLogger.getInstance().actionSucceeded();
//
//            } else {
//                FinnLogger.getInstance().actionFailed();
//            }

        } else {
            // Action has completed (but failed)
            setComplete(true);
            setSuccess(false);
        }
    }

    /**
     * Checks if this action has completed or not.
     *
     * @return True if the action has completed, otherwise false.
     */
    public final boolean isComplete() {
        return completed;
    }

    /**
     * Sets the status of this action -- this method is used ONLY by the ActionSender.
     *
     * @param complete the status of this action.
     */
    public final void setComplete(final boolean complete) {
        completed = complete;
    }

    /**
     * Checks if this action has completed successfully or not.
     *
     * @return True if the action has completed successfully, otherwise false.
     */
    public final boolean isSuccess() {
        return success;
    }

    /**
     * Sets the status of this action -- this method is used ONLY by the ActionSender.
     *
     * @param succ the status of this action.
     */
    public final void setSuccess(final boolean succ) {
        success = succ;
    }

    /**
     * This methid is executed by the ActionSender when all parties have agreed to commit to this action.
     */
    public final void executePartA() {
        // Make sure that this is a proper call to this function

        if (action == null) {
            return;
        }

        /* Perform the actual actions (part A).*/
        doPartA();

        /*  Set the status of this actions.    */
        setComplete(true);
        setSuccess(true);
    }

    /**
     * This methid is executed by the ActionSender when all parties have agreed to commit to this action.
     */
    public final void executePartB() {
        Logger.getInstance().debug("Executing partB...");

        /* Perform the actual actions (part B)*/
        doPartB();

        /* Set the status of this actions*/
        setComplete(true);
        setSuccess(true);
    }


    /**
     * Returns the type of the action -- a unique identity for this action (type).
     *
     * @return an identity for the type of this action.
     */
    public abstract int getType();

    /**
     * Checks if this action will be performed or not based on local rules.
     *
     * @return True if the action must be performed.
     */
    protected abstract boolean checkAction();

    /**
     * Implements the part of the action that is executed in the eu.funinnumbers.guardian that
     * inialiazed the action.
     */
    protected abstract void doPartA();

    /**
     * Implements the part of the action that is executed by the guardians that
     * take part in the action.
     */
    protected abstract void doPartB();

}

