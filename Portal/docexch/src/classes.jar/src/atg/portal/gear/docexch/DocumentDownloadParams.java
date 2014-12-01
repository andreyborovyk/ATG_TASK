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

package atg.portal.gear.docexch;

/****************************************
 * this is a session-scoped nucleas component used to store 
 * attributes for use with the docexch doc download feature
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocumentDownloadParams.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DocumentDownloadParams
{
  //----------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocumentDownloadParams.java#2 $$Change: 651448 $";

  //----------------------------------------
  // Constants
  //----------------------------------------

  //----------------------------------------
  // Member Variables
  //----------------------------------------

  //----------------------------------------
  // Properties
  //----------------------------------------

  //----------------------------------------
  // DocexchGearId
  private String mDocexchGearId;
  /**
   * set DocexchGearId
   * @param pDocexchGearId the DocexchGearId
   */
  public void setDocexchGearId(String pDocexchGearId) { mDocexchGearId = pDocexchGearId; }
  /**
   * get DocexchGearId
   * @return the DocexchGearId
   */
  public String getDocexchGearId() { return mDocexchGearId; }

  //----------------------------------------
  // DocexchCommunityId
  private String mDocexchCommunityId;
  /**
   * set DocexchCommunityId
   * @param pDocexchCommunityId the DocexchCommunityId
   */
  public void setDocexchCommunityId(String pDocexchCommunityId) { mDocexchCommunityId = pDocexchCommunityId; }
  /**
   * get DocexchCommunityId
   * @return the DocexchCommunityId
   */
  public String getDocexchCommunityId() { return mDocexchCommunityId; }

  //----------------------------------------
  // DocexchPageId
  private String mDocexchPageId;
  /**
   * set DocexchPageId
   * @param pDocexchPageId the DocexchPageId
   */
  public void setDocexchPageId(String pDocexchPageId) { mDocexchPageId = pDocexchPageId; }
  /**
   * get DocexchPageId
   * @return the DocexchPageId
   */
  public String getDocexchPageId() { return mDocexchPageId; }

  //----------------------------------------
  // DocexchDocumentId
  private String mDocexchDocumentId;
  /**
   * set DocexchDocumentId
   * @param pDocexchDocumentId the DocexchDocumentId
   */
  public void setDocexchDocumentId(String pDocexchDocumentId) { mDocexchDocumentId = pDocexchDocumentId; }
  /**
   * get DocexchDocumentId
   * @return the DocexchDocumentId
   */
  public String getDocexchDocumentId() { return mDocexchDocumentId; }

  //----------------------------------------
  // Constructors
  //----------------------------------------

  //----------------------------------------
  /**
   * Constructs an instanceof DocumentDownloadParams
   */
  public DocumentDownloadParams()
  {

  }

  //----------------------------------------
  // Object Methods 
  //----------------------------------------

  //----------------------------------------
  // Static Methods 
  //----------------------------------------

} // end of class
