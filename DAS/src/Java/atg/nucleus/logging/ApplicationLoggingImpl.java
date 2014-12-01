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

package atg.nucleus.logging;
import atg.nucleus.Nucleus;
import atg.nucleus.ServiceEvent;

import java.util.*;

/**
 *
 * <p>This is a straightforward implementation of ApplicationLogging
 * that sends events to LogListeners.
 *
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/nucleus/logging/ApplicationLoggingImpl.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public 
class ApplicationLoggingImpl extends VariableArgumentApplicationLoggingImpl
implements ApplicationLoggingSender 
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/nucleus/logging/ApplicationLoggingImpl.java#2 $$Change: 651448 $";


  final static LogListener[] EMPTY_LOG_LISTENERS_ARRAY = new LogListener[0];
  
  
  //-------------------------------------
  // Properties

  /** Flag if logging info messages */
  boolean mLoggingInfo = DEFAULT_LOG_INFO_STATUS;

  /** Flag if logging warning messages */
  boolean mLoggingWarning = DEFAULT_LOG_WARNING_STATUS;

  /** Flag if logging error messages */
  boolean mLoggingError = DEFAULT_LOG_ERROR_STATUS;

  /** Flag if logging debug messages */
  boolean mLoggingDebug = DEFAULT_LOG_DEBUG_STATUS;


  /** Flag if logging debug messages */
  boolean mLoggingTrace = DEFAULT_LOG_TRACE_STATUS;
  

  boolean mAutoInitListeners = false;

  //-------------------------------------
  // Member variables
  //-------------------------------------

  /** The list of log listeners */
  Vector mLogListeners;

  //-------------------------------------  
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "UnknownService";

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }


  /**
   * Set the property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }
  

  /**
   * Returns misspelled property LoggingIdentifer (should be <b>loggingIdentif<u>i</u>er</b>)
   * @deprecated Use the correctly spelled <b>loggingIdentif<u>i</u>er</b> property
   **/
  public String getLoggingIdentifer() {
    return getLoggingIdentifier();
  }


  //--------------------------------
  // property: autoInitListeners

  /** Whether to automatically initialize the log listeners
   * with the list from the Global Nucleus. */
  public synchronized void setAutoInitListeners(boolean pAutoInitListeners) {
    mAutoInitListeners = pAutoInitListeners;
  }

  /** Whether to automatically initialize the log listeners
   * with the list from the Global Nucleus. */
  public synchronized boolean isAutoInitListeners() {
    return mAutoInitListeners;
  }
  

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Creates a new ApplicationLoggingImpl
   */
  public ApplicationLoggingImpl(){
  }
  
  /**
   * Creates a new ApplicationLoggingImpl with the specified originator
   * that will be applied to all logging messages.
   **/
  public ApplicationLoggingImpl (String pLoggingIdentifier)
  {
    this(pLoggingIdentifier, false);
  }


  /**
   * Creates a new ApplicationLoggingImpl with the specified originator
   * that will be applied to all logging messages.
   **/
  public ApplicationLoggingImpl (String pLoggingIdentifier, boolean pAutoInitListeners)
  {
    mLoggingIdentifier = pLoggingIdentifier;
    mAutoInitListeners = pAutoInitListeners;
  }
  


  //-------------------------------------  
  /** Initialize this component from a service event. */
  public void initializeFromServiceEvent(
    ServiceEvent pEvent) {
    String strAbsoluteName =
      pEvent.getServiceConfiguration().getServiceName();
    if (strAbsoluteName != null) {
      setLoggingIdentifier(strAbsoluteName);
    }
    if (getLogListenerCount() == 0) {
      addGlobalLogListeners();
    }
    mAutoInitListeners = false;
  }

  //-------------------------------------    
  protected void addGlobalLogListeners() {
    if (Nucleus.getGlobalNucleus() != null) {
      LogListener[] listeners = 
        Nucleus.getGlobalNucleus().getLogListeners();
      for (int i = 0; i < listeners.length; i++) {
        addLogListener(listeners[i]);
      }
    }
  }

  //-------------------------------------
  /**
   *
   * Adds a listener to the list of log listeners
   **/
  public synchronized void addLogListener (LogListener pListener)
  {
    if (mLogListeners == null) mLogListeners = new Vector ();
    mLogListeners.addElement (pListener);
  }

  //-------------------------------------
  /**
   *
   * Removes a listener from the list of log listeners
   **/
  public synchronized void removeLogListener (LogListener pListener)
  {
    if (mLogListeners != null)
    mLogListeners.removeElement (pListener);
  }

  //-------------------------------------
  /**
   *
   * Returns the number of log listeners
   **/
  public int getLogListenerCount ()
  {
    return (mLogListeners == null) ? 0 : mLogListeners.size ();
  }

  //-------------------------------------
  /**
   *
   * Sends a LogEvent to all of the listeners
   **/
  public synchronized void sendLogEvent (LogEvent pLogEvent)
  {
    if (mAutoInitListeners) {
      addGlobalLogListeners();
      mAutoInitListeners = false;
    }
    if (mLogListeners != null) {
      int len = mLogListeners.size ();
      for (int i = 0; i < len; i++) {
        ((LogListener) mLogListeners.elementAt (i)).logEvent (pLogEvent);
      }
    }
  }

  //-------------------------------------
  // ApplicationLogging methods
  //-------------------------------------
  /**
   * This method returns whether or not an info log event should be
   * broadcast.
   * @return boolean true if info log events should be logged<BR>
   *                 false if info log events should not be logged
   **/
  public boolean isLoggingInfo ()
  {
    return mLoggingInfo;
  }

  //-------------------------------------
  /**
   * Sets whether or not info log events should be logged.
   **/
  public void setLoggingInfo (boolean pLogging)
  {
    mLoggingInfo = pLogging;
  }

  //-------------------------------------
  /**
   * This method returns whether or not an warning log event should be
   * broadcast.
   * @return boolean true if warning log events should be logged<BR>
   *                 false if warning log events should not be logged
   **/
  public boolean isLoggingWarning ()
  {
    return mLoggingWarning;
  }

  //-------------------------------------
  /**
   * Sets whether or not warning log events should be logged.
   **/
  public void setLoggingWarning (boolean pLogging)
  {
    mLoggingWarning = pLogging;
  }

  //-------------------------------------
  /**
   * This method returns whether or not an error log event should be
   * broadcast.
   * @return boolean true if error log events should be logged<BR>
   *                 false if error log events should not be logged
   **/
  public boolean isLoggingError ()
  {
    return mLoggingError;
  }

  //-------------------------------------
  /**
   * Sets whether or not error log events should be logged.
   **/
  public void setLoggingError (boolean pLogging)
  {
    mLoggingError = pLogging;
  }

  //-------------------------------------
  /**
   * This method returns whether or not an debug log event should be
   * broadcast.
   * @return boolean true if debug log events should be logged<BR>
   *                 false if debug log events should not be logged
   **/
  public boolean isLoggingDebug ()
  {
    return mLoggingDebug;
  }

  //-------------------------------------
  /**
   * Sets whether or not debug log events should be logged.
   **/
  public void setLoggingDebug (boolean pLogging)
  {
    mLoggingDebug = pLogging;
  }

  //-------------------------------------
  /**
   * This method returns whether or not an trace log event should be
   * broadcast.
   * @return boolean true if trace log events should be logged<BR>
   *                 false if trace log events should not be logged
   **/
  public boolean isLoggingTrace ()
  {
    return mLoggingTrace;
  }

  //-------------------------------------
  /**
   * Sets whether or not trace log events should be logged.
   **/
  public void setLoggingTrace (boolean pLogging)
  {
    mLoggingTrace = pLogging;
  }
  

  //-------------------------------------
  /**
   * Logs an info event with the specified message
   **/
  public void logInfo (String pMessage)
  {
    logInfo (pMessage, null);
  }

  //-------------------------------------
  /**
   * Logs an info event with the specified Throwable
   **/
  public void logInfo (Throwable pThrowable)
  {
    logInfo (null, pThrowable);
  }

  //-------------------------------------
  /**
   * Logs an info event with the specified message and Throwable
   **/
  public void logInfo (String pMessage, Throwable pThrowable)
  {
    sendLogEvent (new InfoLogEvent (pMessage, getLoggingIdentifier(), pThrowable));
  }

  //-------------------------------------
  /**
   * Logs an warning event with the specified message
   **/
  public void logWarning (String pMessage)
  {
    logWarning (pMessage, null);
  }

  //-------------------------------------
  /**
   * Logs an warning event with the specified Throwable
   **/
  public void logWarning (Throwable pThrowable)
  {
    logWarning (null, pThrowable);
  }

  //-------------------------------------
  /**
   * Logs an warning event with the specified message and Throwable
   **/
  public void logWarning (String pMessage, Throwable pThrowable)
  {
    sendLogEvent(new WarningLogEvent(pMessage, getLoggingIdentifier(), pThrowable));
  }

  //-------------------------------------
  /**
   * Logs an error event with the specified message
   **/
  public void logError (String pMessage)
  {
    logError (pMessage, null);
  }

  //-------------------------------------
  /**
   * Logs an error event with the specified Throwable
   **/
  public void logError (Throwable pThrowable)
  {
    logError (null, pThrowable);
  }

  //-------------------------------------
  /**
   * Logs an error event with the specified message and Throwable
   **/
  public void logError (String pMessage, Throwable pThrowable)
  {
    sendLogEvent (new ErrorLogEvent (pMessage, getLoggingIdentifier(), pThrowable));
  }

  //-------------------------------------
  /**
   * Logs an debug event with the specified message
   **/
  public void logDebug (String pMessage)
  {
    logDebug (pMessage, null);
  }

  //-------------------------------------
  /**
   * Logs an debug event with the specified Throwable
   **/
  public void logDebug (Throwable pThrowable)
  {
    logDebug (null, pThrowable);
  }

  //-------------------------------------
  /**
   * Logs an debug event with the specified message and Throwable
   **/
  public void logDebug (String pMessage, Throwable pThrowable)
  {
    sendLogEvent (new DebugLogEvent (pMessage, getLoggingIdentifier(), pThrowable));
  }


  //-------------------------------------
  /**
   * Logs an trace event with the specified message
   **/
  public void logTrace (String pMessage)
  {
    logTrace (pMessage, null);
  }

  //-------------------------------------
  /**
   * Logs an trace event with the specified Throwable
   **/
  public void logTrace (Throwable pThrowable)
  {
    logTrace (null, pThrowable);
  }

  //-------------------------------------
  /**
   * Logs an trace event with the specified message and Throwable
   **/
  public void logTrace (String pMessage, Throwable pThrowable)
  {
    sendLogEvent (new TraceLogEvent (pMessage, getLoggingIdentifier(), pThrowable));
  }
  

  /**
   * Return an array representing the list of LogListeners. This method
   * returns a new array each time invoked.
   */
  public LogListener[] getLogListeners() {
    if ((mLogListeners == null) || (mLogListeners.size() == 0))  {
      return EMPTY_LOG_LISTENERS_ARRAY;
    }

    LogListener[] resultArray = new LogListener[mLogListeners.size()];
    mLogListeners.copyInto(resultArray);
    return resultArray;
  }

  //-------------------------------------
  public ApplicationLogging getLoggingForVlogging() {
    return this;
  }

}
