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

package atg.service.datacollection;

import java.util.Vector;

import atg.nucleus.GenericService;

/**
 *
 * Sample data source implementation. To be a data source, you
 * can:
 * <p>
 * <ol>
 * <li>subclass DataSource
 * <li>encapsulate DataSource (i.e., declare a DataSource object in
 * your object and pass through the DataListener method calls
 * <li>implement the DataListener calls yourself
 * <li>if you are a GenericService, and your datapoints are LogEvents, and
 * you don't feel like writing code you can just call sendLogEvent
 * </ol>
 *
 * This class is not used by the data collection package. DataSource
 * serves as a sample of the design pattern to follow to be source of
 * data items. The GenericSummarizer is also a source of data items, and
 * implements the relevant DataListener calls itself.
 *
 * @author mgk
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/datacollection/DataSource.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see GenericSummarizer
 */

public class DataSource extends GenericService
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/datacollection/DataSource.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  /** the data listener list */
  Vector mDataListeners;

  //-------------------------------------
  // Constructors

  //-------------------------------------
  /**
   * Construct a DataSource
   */
  public DataSource()
  {
  }

  //-------------------------------------
  // DataSource design pattern

  //-------------------------------------
  /**
   * Sends a data item to all of the listeners
   **/
  public synchronized void sendDataItem(Object pDataItem)
  {
    if (mDataListeners != null)
      {
        int len = mDataListeners.size();
        for (int i = 0; i < len; i++)
          ((DataListener)mDataListeners.elementAt(i)).addDataItem(pDataItem);
      }
  }

  //-------------------------------------
  /**
   * Adds a listener to the list of data listeners
   **/
  public synchronized void addDataListener(DataListener pListener)
  {
    if (mDataListeners == null) mDataListeners = new Vector();
    mDataListeners.addElement (pListener);
  }

  //-------------------------------------
  /**
   * Removes a listener from the list of data listeners
   **/
  public synchronized void removeDataListener(DataListener pListener)
  {
    if (mDataListeners != null)
      mDataListeners.removeElement(pListener);
  }

  //-------------------------------------
  /**
   * Returns the list of data listeners as an array property
   **/
  public synchronized DataListener[] getDataListeners()
  {
    if (mDataListeners == null)
      return new DataListener[0];

    DataListener[] ret = new DataListener[mDataListeners.size()];
    mDataListeners.copyInto(ret);
    return ret;
  }

  //-------------------------------------
  /**
   * Returns the number of data listeners
   **/
  public synchronized int getDataListenerCount()
  {
    return (mDataListeners == null) ? 0 : mDataListeners.size();
  }

} // end of class
