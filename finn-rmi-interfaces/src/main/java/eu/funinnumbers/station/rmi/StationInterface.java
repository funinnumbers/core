/*
 * StationInterface.java
 *
 * Created on May 23, 2008, 9:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.funinnumbers.station.rmi;

import eu.funinnumbers.db.model.event.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Interface of Station used by the Engine.
 */
public abstract interface StationInterface extends Remote {

    /**
     * The name of the RMI class.
     */
    String RMI_NAME = "Station";

    /**
     * This method is used from the Engine to notify the Station about an event.
     *
     * @param event the event to distribute to all stations
     * @throws RemoteException if failed to connect to a particular eu.funinnumbers.station
     */
    void addEvent(final Event event) throws RemoteException;


    /**
     * This method is used from the Engine to complete Station's registration.
     *
     * @throws RemoteException if failed to connect to a particular eu.funinnumbers.station
     */
    void completeRegistration() throws RemoteException;

}
