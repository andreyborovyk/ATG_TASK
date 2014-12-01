/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

package atg.droplet.sql;

// atg classes
import atg.service.jdbc.*;
import atg.core.util.StringUtils;
import atg.droplet.*;
import atg.servlet.*;

// java classes
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * This form handler allows a SwitchingDataSource to be switch through jhtml.
 *
 * @see SwitchingDataSource
 *
 * @beaninfo
 *   description: Switch a SwitchingDataSource's dataSource.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Sam Perman
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SwitchDataSourceFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class SwitchDataSourceFormHandler extends GenericFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SwitchDataSourceFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Properties
  //-------------------------------------


  //---------------------------------------------------------------------------
  // property:SwitchingDataSource
  //---------------------------------------------------------------------------

  private SwitchingDataSource mSwitchingDataSource;
  public void setSwitchingDataSource(SwitchingDataSource pSwitchingDataSource) {
    mSwitchingDataSource = pSwitchingDataSource;
  }

  /**
   * This is the data source being controlled by this form handler.
   * The switch will happen on this data source.
   * @beaninfo
   *          description: The switching data source being updated by this form handler
   **/
  public SwitchingDataSource getSwitchingDataSource() {
    return mSwitchingDataSource;
  }

  //---------------------------------------------------------------------------
  // property:NextDataSource
  //---------------------------------------------------------------------------

  private String mNextDataSource;
  public void setNextDataSource(String pNextDataSource) {
    mNextDataSource = pNextDataSource;
  }

  /**
   * The name of the data source to switch to.  This name is mapped to a DataSource
   * in the SwitchingDataSource.
   * @beaninfo
   *          description: The name of the data source to switch to
   **/
  public String getNextDataSource() {
    return mNextDataSource;
  }


  //---------------------------------------------------------------------------
  // property:SuccessURL
  //---------------------------------------------------------------------------

  private String mSuccessURL;
  public void setSuccessURL(String pSuccessURL) {
    mSuccessURL = pSuccessURL;
  }

  /**
   * Where to go on success
   * @beaninfo
   *          description: The URL to go to if the operation is successful
   **/
  public String getSuccessURL() {
    return mSuccessURL;
  }


  //---------------------------------------------------------------------------
  // property:ErrorURL
  //---------------------------------------------------------------------------

  private String mErrorURL;
  public void setErrorURL(String pErrorURL) {
    mErrorURL = pErrorURL;
  }

  /**
   * where to go on error
   * @beaninfo
   *          description: The URL to go to if the operation fails
   **/
  public String getErrorURL() {
    return mErrorURL;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * Switching a data source is a two step process.  This handler
   * performs step one: preparing the data source.  It will call the method
   * prepareNextDataSource on hte SwitchingDataSource.
   *
   * @param pRequest The request object.
   * @param pResponse The response object.
   * @return true if succesful, false otherwise
   * @exception ServletException
   * @exception IOException
   * @see SwitchingDataSource#prepareNextDataSource
   **/
  public boolean handlePrepareForSwitch(DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (! checkFormRedirect (null, getErrorURL(), pRequest, pResponse)) {
      if(isLoggingDebug())
        logDebug("checkFormRedirect returned false.");
        
      return false;
    }

    try {
      SwitchingDataSource switcher = getSwitchingDataSource();
      String next = getNextDataSource();
      if(isLoggingDebug())
        logDebug("Switching from current to " + getNextDataSource());
      if(next == null) {
        addFormException(new DropletException("Next data source name is null."));
        return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);  
      }      
      switcher.prepareNextDataSource(next);
    }
    catch(Exception e) {
      if (isLoggingError()) {
        logError(e);
      }
      String excMsg = StringUtils.isBlank(e.getMessage()) ? "An exception occured, check the error log." : e.getMessage();      
      addFormException(new DropletException(excMsg));
    }
    return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);    
  }

  /**
   * This handler method performs the second step in the switch
   * process: the switch.  It calls the performSwitch method on the
   * SwitchingDataSource
   *
   * @param pRequest The request object.
   * @param pResponse The response object.
   * @return true if succesful, false otherwise
   * @exception ServletException
   * @exception IOException
   * @see SwitchingDataSource#performSwitch
   **/
  public boolean handleSwitchDataSource(DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (! checkFormRedirect (null, getErrorURL(), pRequest, pResponse)) {
      if(isLoggingDebug())
        logDebug("checkFormRedirect returned false.");
        
      return false;      
    }

    try {
      if(isLoggingDebug())
        logDebug("Switching from current to " + getNextDataSource());
      SwitchingDataSource switcher = getSwitchingDataSource();

      switcher.performSwitch();
    }
    catch(Exception e) {
      if (isLoggingError()) {
        logError(e);
      }
      String excMsg = StringUtils.isBlank(e.getMessage()) ? "An exception occured, check the error log." : e.getMessage();
      addFormException(new DropletException(excMsg));
    }

    return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
  }
} // end of class
