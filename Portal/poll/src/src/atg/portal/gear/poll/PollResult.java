/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.portal.gear.poll;


/**
 * 
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/PollResult.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class PollResult
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/PollResult.java#2 $$Change: 651448 $";

  //---------------------------------------------------------------------------
  // property: responseId
  String mResponseId;

  public void setResponseId(String pResponseId) {
    mResponseId = pResponseId;
  }

  public String getResponseId() {
    return mResponseId;
  }

  //---------------------------------------------------------------------------
  // property: responseText
  String mResponseText;

  public void setResponseText(String pResponseText) {
    mResponseText = pResponseText;
  }

  public String getResponseText() {
    return mResponseText;
  }

  //---------------------------------------------------------------------------
  // property: shortName
  String mShortName;

  public void setShortName(String pShortName) {
    mShortName = pShortName;
  }

  public String getShortName() {
    return mShortName;
  }

  //---------------------------------------------------------------------------
  // property: count
  int mCount;

  public void setCount(int pCount) {
    mCount = pCount;
  }
  public void setCount(String pCount) {
    mCount = Integer.parseInt(pCount);
  }


  public int getCount() {
    return mCount;
  }

  //---------------------------------------------------------------------------
  // property: percentage
  int mPercentage;

  public void setPercentage(int pPercentage) {
    mPercentage = pPercentage;
  }

  public void setPercentage(String pPercentage) {
    mPercentage = Integer.parseInt(pPercentage);
  }

  public int getPercentage() {
    return mPercentage;
  }

  //---------------------------------------------------------------------------
  // property: barColor
  String mBarColor;

  public void setBarColor(String pBarColor) {
    mBarColor = pBarColor;
  }

  public String getBarColor() {
    return mBarColor;
  }


  //---------------------------------------------------------------------------
  // constructor

 public PollResult(String pResponseId, String pResponseText, String pShortName, String pCount, String pPercentage,String pBarColor) {
     setResponseId(pResponseId);
     setResponseText(pResponseText);
     setShortName(pShortName);
     setCount(pCount);
     setPercentage(pPercentage);
     setBarColor(pBarColor);
  }
} // end of class
