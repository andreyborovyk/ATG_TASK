/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES 
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, 
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.naming;

import java.util.*;
import atg.core.util.*;

/**
 *
 * <p>This is a straightforward implementation of NameContext.  This
 * also implements NameContextBindingEventSource, meaning that it
 * notifies listeners whenever a binding or removal occurs.
 *
 * <p>In this implementation, the absolute name of a child is formed
 * by taking the parent's absolute name, adding a '/', and appending
 * the child's name.  This behavior can be changed by overriding
 * getAbsoluteElementName().
 *
 * <p>This implementation attempts to avoid creating any objects if
 * the Context is not used to store elements - if no elements or
 * listeners are added, then no Dictionaries or Vectors will be
 * created.
 *
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/naming/NameContextImpl.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
class NameContextImpl
implements NameContext, NameContextBindingEventSource
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/naming/NameContextImpl.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  /** The elements in the context */
  Dictionary mElements;

  /** The list of binding listeners */
  Vector mBindingListeners;

  /** The NameContext containing this */
  NameContext mNameContext;

  /** The name of this within its NameContext */
  String mName;

  //-------------------------------------
  /**
   *
   * Constructs a new NameContextImpl
   **/
  public NameContextImpl ()
  {
  }

  //-------------------------------------
  // property: NameContextBindingListeners
  NameContextBindingListener [] mNameContextBindingListeners;

  /**
   * Returns property NameContextBindingListeners
   **/
  public synchronized NameContextBindingListener [] getNameContextBindingListeners() {
    NameContextBindingListener [] listeners = new NameContextBindingListener[getBindingListeners().size()];
    for (int c=0; c < listeners.length; c++)
      listeners[c] = (NameContextBindingListener)getBindingListeners().elementAt(c);
    
    return listeners;
  }  

  //-------------------------------------
  /**
   *
   * Returns the list of elements, creating it if it doesn't exist
   **/
  Dictionary getElements ()
  {
    synchronized (this) {
      if (mElements == null) {
        mElements = new Hashtable ();
      }
    }
    return mElements;
  }

  //-------------------------------------
  /**
   *
   * Returns the list of binding listeners, creating it if it doesn't
   * exist
   **/
  Vector getBindingListeners ()
  {
    synchronized (this) {
      if (mBindingListeners == null) {
        mBindingListeners = new Vector ();
      }
    }
    return mBindingListeners;
  }

  //-------------------------------------
  /**
   *
   * Returns true if anyone is listening for binding events
   **/
  synchronized boolean hasBindingListeners ()
  {
    return (mBindingListeners != null &&
            getBindingListeners ().size () > 0);
  }

  //-------------------------------------
  /**
   *
   * Sends a bound event to all of the listeners
   **/
  synchronized void sendBoundEvent (NameContextBindingEvent pEvent)
  {
    if (hasBindingListeners ()) {
      Vector v = getBindingListeners ();
      int len = v.size ();
      for (int i = 0; i < len; i++) {
        ((NameContextBindingListener) v.elementAt (i)).
          nameContextElementBound (pEvent);
      }
    }
  }

  //-------------------------------------
  /**
   *
   * Sends an unbound event to all of the listeners
   **/
  synchronized void sendUnboundEvent (NameContextBindingEvent pEvent)
  {
    if (hasBindingListeners ()) {
      Vector v = getBindingListeners ();
      int len = v.size ();
      for (int i = 0; i < len; i++) {
        ((NameContextBindingListener) v.elementAt (i)).
          nameContextElementUnbound (pEvent);
      }
    }
  }

  //-------------------------------------
  /**
   *
   * Searches up the tree until it finds the root NameContext
   **/
  public NameContext getRoot ()
  {
    NameContext c;
    for (c = this; c.getNameContext () != null; c = c.getNameContext ());
    return c;
  }

  //-------------------------------------
  // NameContext methods
  //-------------------------------------
  /**
   *
   * Returns the element bound to the specified name.
   **/
  public synchronized Object getElement (String pName)
  {
    if (mElements == null) return null;
    else return getElements ().get (pName);
  }

  //-------------------------------------
  /**
   *
   * Binds the specified element to the specified name.
   **/
  public synchronized void putElement (String pName,
                                       Object pElement)
  {
    // Remove any existing binding
    removeElement (pName);

    // Add the element
    getElements ().put (pName, pElement);

    // Notify the object and any other listeners
    if (pElement instanceof NameContextBindingListener ||
        hasBindingListeners ()) {

      // Create the event
      NameContextBindingEvent ev = 
        new NameContextBindingEvent (pName, pElement, this);

      // Notify the value
      if (pElement instanceof NameContextBindingListener) {
        ((NameContextBindingListener) pElement).nameContextElementBound (ev);
      }

      // Notify the listeners
      sendBoundEvent (ev);
    }
  }

  //-------------------------------------
  /**
   *
   * Removes the binding for the specified name.
   **/
  public synchronized void removeElement (String pName)
  {
    if (mElements != null) {
      Object val = getElements ().remove (pName);

      // Notify the object and any other listeners
      if (val instanceof NameContextBindingListener ||
          hasBindingListeners ()) {

        // Create the event
        NameContextBindingEvent ev = 
          new NameContextBindingEvent (pName, val, this);

        // Notify the value
        if (val instanceof NameContextBindingListener) {
          ((NameContextBindingListener) val).nameContextElementBound (ev);
        }

        // Notify the listeners
        sendUnboundEvent (ev);
      }
    }
  }

  //-------------------------------------
  /**
   *
   * Returns true if the name has and element bound to it, false if
   * not.
   **/
  public boolean isElementBound (String pName)
  {
    return (mElements != null &&
            getElements ().get (pName) != null);
  }

  //-------------------------------------
  /**
   *
   * Returns the list of element names as an Enumeration
   **/
  public Enumeration listElementNames ()
  {
    if (mElements == null) return EmptyEnumeration.getOneof ();
    return getElements ().keys ();
  }

  //-------------------------------------
  /**
   *
   * Returns the list of bound elements as an Enumeration
   **/
  public Enumeration listElements ()
  {
    if (mElements == null) return EmptyEnumeration.getOneof ();
    return getElements ().elements ();
  }

  //-------------------------------------
  // NameContextBindingEventSource methods
  //-------------------------------------
  /**
   *
   * Adds the specified listener to the list of listeners that will be
   * notified whenever an element is bound into or unbound from this
   * NameContext.
   **/
  public synchronized void 
  addNameContextBindingListener (NameContextBindingListener pListener)
  {
    getBindingListeners ().addElement (pListener);
  }

  //-------------------------------------
  /**
   *
   * Removes the specified listener from the list of listeners that
   * will be notified whenever an element is bound into or unbound
   * from this NameContext.
   **/
  public synchronized void 
  removeNameContextBindingListener (NameContextBindingListener pListener)
  {
    if (hasBindingListeners ()) {
      getBindingListeners ().removeElement (pListener);
    }
  }

  //-------------------------------------
  // NameContextBindingListener methods
  //-------------------------------------
  /**
   *
   * This is called to notify this context that it is being bound into
   * a NameContext.
   **/
  public synchronized void 
  nameContextElementBound (NameContextBindingEvent pEvent)
  {
    if (pEvent.getElement () == this) {
      mNameContext = pEvent.getNameContext ();
      mName = pEvent.getName ();
    }
  }

  //-------------------------------------
  /**
   *
   * This is called to notify this context that it is being unbound
   * from a NameContext.
   **/
  public synchronized void 
  nameContextElementUnbound (NameContextBindingEvent pEvent)
  {
    if (pEvent.getElement () == this) {
      mNameContext = null;
      mName = null;
    }
  }

  //-------------------------------------
  // NameContextElement methods
  //-------------------------------------
  /**
   *
   * Returns the NameContext into which this element has been bound,
   * or null if the element is not bound in a NameContext.
   **/
  public NameContext getNameContext ()
  {
    return mNameContext;
  }

  //-------------------------------------
  /**
   *
   * Returns the name by which this element is know within its enclosing
   * NameContext.
   **/
  public String getName ()
  {
    return mName;
  }

  //-------------------------------------
}
