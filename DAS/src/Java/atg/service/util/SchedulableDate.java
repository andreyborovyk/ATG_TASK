/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.service.util;

import atg.service.scheduler.*;
import atg.nucleus.ServiceException;

/**
 * This service is an extension of the CurrentDate service, but adds the capability
 * of having it's time fixed on a periodic basis through the scheduler.
 *
 * @author Bob Mason
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/util/SchedulableDate.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
class SchedulableDate
extends CurrentDate
implements Schedulable
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/util/SchedulableDate.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  //-------------------------------------
  // Member Variables
  //-------------------------------------
  int mUpdateJobId = -1;

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: Scheduler
  Scheduler mScheduler;

  /**
   * Sets property Scheduler
   * @beaninfo expert: true
   **/
  public void setScheduler(Scheduler pScheduler) {
    mScheduler = pScheduler;
  }

  /**
   * Returns property Scheduler
   **/
  public Scheduler getScheduler() {
    return mScheduler;
  }

  //-------------------------------------
  // property: UpdateSchedule
  Schedule mUpdateSchedule;

  /**
   * Sets property UpdateSchedule
   * @beaninfo description: the schedule on which the repository contents will be rescanned
   **/
  public void setUpdateSchedule(Schedule pUpdateSchedule) {
    mUpdateSchedule = pUpdateSchedule;
  }

  /**
   * Returns property UpdateSchedule
   **/
  public Schedule getUpdateSchedule() {
    return mUpdateSchedule;
  }  

  //-------------------------------------
  // property: UpdateJobName
  String mUpdateJobName = "Reset Time";

  /**
   * Sets property UpdateJobName
   **/
  public void setUpdateJobName(String pUpdateJobName) {
    mUpdateJobName = pUpdateJobName;
  }

  /**
   * Returns property UpdateJobName
   **/
  public String getUpdateJobName() {
    return mUpdateJobName;
  }

  //-------------------------------------
  // property: UpdateJobDescription
  String mUpdateJobDescription;

  /**
   * Sets property UpdateJobDescription
   **/
  public void setUpdateJobDescription(String pUpdateJobDescription) {
    mUpdateJobDescription = pUpdateJobDescription;
  }

  /**
   * Returns property UpdateJobDescription
   **/
  public String getUpdateJobDescription() {
    return mUpdateJobDescription;
  }
  
  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof SchedulableDate
   */
  public SchedulableDate() {
  }


  /**
   * Setup the schedule to periodically set the time
   */
  public void doStartService()
    throws ServiceException
  {
    super.doStartService();

   if (getScheduler () != null && getUpdateSchedule () != null) {
      ScheduledJob j = new ScheduledJob (getUpdateJobName(),
                                         getUpdateJobDescription(),
                                         getAbsoluteName (),
                                         getUpdateSchedule (),
                                         this,
                                         ScheduledJob.REUSED_THREAD);
      mUpdateJobId = getScheduler ().addScheduledJob (j);
    }
  }

  /**
   * Stops the schedule for 
   * @exception ServiceException if an error occurred during the operation
   **/
  public void doStopService ()
    throws ServiceException
  {
    if (getScheduler () != null && mUpdateJobId != -1) {
      getScheduler ().removeScheduledJob (mUpdateJobId);
      mUpdateJobId = -1;
    }
  }

  //-------------------------------------
  // Schedulable methods
  //-------------------------------------

  /**
   * Reload the global promotions
   * @param pScheduler calling the job
   * @param pJob the ScheduledJob
   **/
  public void performScheduledTask (Scheduler pScheduler,
                                    ScheduledJob pJob)
  {
    setTime(System.currentTimeMillis());
    
    if (isLoggingDebug())
      logDebug("scheduled task: reseting time to " + getTimeAsDate());
  }

} // end of class
