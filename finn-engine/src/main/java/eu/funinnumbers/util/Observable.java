/*
 * @(#)Observable.java	1.39 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package eu.funinnumbers.util;

import java.util.Vector;

/**
 * This class represents an observable object, or "data"
 * in the model-view paradigm. It can be subclassed to represent an
 * object that the application wants to have observed.
 * <p/>
 * An observable object can have one or more observers. An observer
 * may be any object that implements interface <tt>Observer</tt>. After an
 * observable instance changes, an application calling the
 * <code>Observable</code>'s <code>notifyObservers</code> method
 * causes all of its observers to be notified of the change by a call
 * to their <code>update</code> method.
 * <p/>
 * The order in which notifications will be delivered is unspecified.
 * The default implementation provided in the Observable class will
 * notify Observers in the order in which they registered interest, but
 * subclasses may change this order, use no guaranteed order, deliver
 * notifications on separate threads, or may guarantee that their
 * subclass follows this order, as they choose.
 * <p/>
 * Note that this notification mechanism is has nothing to do with threads
 * and is completely separate from the <tt>wait</tt> and <tt>notify</tt>
 * mechanism of class <tt>Object</tt>.
 * <p/>
 * When an observable object is newly created, its setType of observers is
 * empty. Two observers are considered the same if and only if the
 * <tt>equals</tt> method returns true for them.
 *
 * @author Chris Warth
 * @version 1.39, 11/17/05
 * @see java.util.Observable#notifyObservers()
 * @see java.util.Observable#notifyObservers(java.lang.Object)
 * @see java.util.Observer
 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
 * @since JDK1.0
 */
public class Observable {

    /**
     * signals if the observable has changes.
     */
    private boolean changed = false;

    /**
     * Vector of observers for this observable.
     */
    private final Vector observers;

    /**
     * Construct an Observable with zero Observers.
     */
    public Observable() {
        observers = new Vector(); //NOPMD
    }

    /**
     * Adds an observer to the setType of observers for this object, provided
     * that it is not the same as some observer already in the setType.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param obs an observer to be added.
     * @throws NullPointerException if the parameter o is null.
     */
    public synchronized void addObserver(final Observer obs) { //NOPMD
        if (obs == null) {
            throw new IllegalArgumentException();
        }
        if (!observers.contains(obs)) {
            observers.addElement(obs);
        }
    }

    /**
     * Deletes an observer from the setType of observers of this object.
     * Passing <CODE>null</CODE> to this method will have no effect.
     *
     * @param obs the observer to be deleted.
     */
    public synchronized void deleteObserver(final Observer obs) { //NOPMD
        this.observers.removeElement(obs);
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to
     * indicate that this object has no longer changed.
     * <p/>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other
     * words, this method is equivalent to:
     * <blockquote><tt>
     * notifyObservers(null)</tt></blockquote>
     *
     * @see java.util.Observable#clearChanged()
     * @see java.util.Observable#hasChanged()
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void notifyObservers() {
        notifyObservers(null);
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to indicate
     * that this object has no longer changed.
     * <p/>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     *
     * @param arg any object.
     * @see java.util.Observable#clearChanged()
     * @see java.util.Observable#hasChanged()
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void notifyObservers(final Object arg) {

        synchronized (this) { //NOPMD
            /* We don't want the Observer doing callbacks into
            * arbitrary code while holding its own Monitor.
            * The code where we extract each Observable from
            * the Vector and store the state of the Observer
            * needs synchronization, but notifying observers
            * does not (should not).  The worst result of any
            * potential race-condition here is that:
            * 1) a newly-added Observer will miss a
            *   notification in progress
            * 2) a recently unregistered Observer will be
            *   wrongly notified when it doesn't care
            */

        }
        if (!changed) {
            return;
        }

        for (int i = 0; i < observers.size(); i++) {
            final Observer observer = (Observer) observers.elementAt(i);
            observer.update(this, arg);
        }

        clearChanged();
    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    public synchronized void deleteObservers() { //NOPMD
        observers.removeAllElements();
    }

    /**
     * Marks this <tt>Observable</tt> object as having been changed; the
     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
     */
    public synchronized void setChanged() { //NOPMD
        changed = true;
    }

    /**
     * Indicates that this object has no longer changed, or that it has
     * already notified all of its observers of its most recent change,
     * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>.
     * This method is called automatically by the
     * <code>notifyObservers</code> methods.
     *
     * @see java.util.Observable#notifyObservers()
     * @see java.util.Observable#notifyObservers(java.lang.Object)
     */
    protected synchronized void clearChanged() { //NOPMD
        changed = false;
    }

    /**
     * Tests if this object has changed.
     *
     * @return <code>true</code> if and only if the <code>setChanged</code>
     *         method has been called more recently than the
     *         <code>clearChanged</code> method on this object;
     *         <code>false</code> otherwise.
     * @see java.util.Observable#clearChanged()
     * @see java.util.Observable#setChanged()
     */
    public synchronized boolean hasChanged() { //NOPMD
        return changed;
    }

    /**
     * Returns the number of observers of this <tt>Observable</tt> object.
     *
     * @return the number of observers of this object.
     */
    public synchronized int countObservers() { //NOPMD
        return observers.size();
    }
}
