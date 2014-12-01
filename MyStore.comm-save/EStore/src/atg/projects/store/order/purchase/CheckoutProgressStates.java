/*<ATGCOPYRIGHT>
 * Copyright (C) 2010 Art Technology Group, Inc.
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

package atg.projects.store.order.purchase;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains available checkout milestones and current checkout progress level. 
 * @author Alexandr Gontarev
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/CheckoutProgressStates.java#3 $$Change: 635816 $ 
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class CheckoutProgressStates
{
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/CheckoutProgressStates.java#3 $$Change: 635816 $";

  private String mCurrentLevel;
  
  private Map<String, Integer> mCheckoutProgressLevels = new HashMap<String, Integer>();  

  /**
   * currentLevel property determinates which pages can be displayied to user. I.e. BILLING level means that user can see all pages
   * but confrimation, SHIPPING level means that user can see his cart contents ant shipping page.
   */
  public String getCurrentLevel()
  {
    return mCurrentLevel;
  }

  public void setCurrentLevel(String pCurrentLevel)
  {
    mCurrentLevel = pCurrentLevel;
  }
  
  /**
   * Numeric representation of currentLevel property 
   */
  public int getCurrentLevelAsInt()
  {
    Integer currentLevel = mCheckoutProgressLevels.get(mCurrentLevel);
    return currentLevel == null ? 0 : currentLevel;
  }
  
  /**
   * checoutProgressLevels property contains checkout milestones available for user. These milestones are stored in form of map,
   * linking milestone name with it's level int value.
   * Milestones with lower level are available for displaying to user.
   */
  public Map<String, Integer> getCheckoutProgressLevels()
  {
    return mCheckoutProgressLevels;
  }
  
  @SuppressWarnings("unchecked") //Ok, cause there should be Map<String, String> as input parameter (set by Nucleus)
  public void setCheckoutProgressLevels(Map pCheckoutProgressLevels)
  {
    mCheckoutProgressLevels.clear();
    if (pCheckoutProgressLevels == null)
    {
      return;
    }
    for (Object entryObj: pCheckoutProgressLevels.entrySet())
    {
      Map.Entry entry = (Map.Entry)entryObj;
      mCheckoutProgressLevels.put(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString()));
    }
  }
  
  public static enum DEFAULT_STATES
  {
    CART, SHIPPING, BILLING, CONFIRM;
  }
}
