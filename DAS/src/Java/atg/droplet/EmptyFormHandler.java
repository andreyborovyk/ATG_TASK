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

package atg.droplet;

import atg.nucleus.GenericService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/*
 * This interface is implemented by nucleus components that are referenced
 * by an input tag's property attribute that want to be notified of 
 * important events in the processing of a form.  Methods in the bean are
 * called before and after any getX or setX methods of the bean are called.
 * <p>
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/EmptyFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class EmptyFormHandler extends GenericService implements DropletFormHandler {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/EmptyFormHandler.java#2 $$Change: 651448 $";

  /**
   * Called when a form is submitted that references this bean before any set
   * methods have been called, i.e. first thing in processing the form data.
   * <p/>
   * If multiple form handlers exist for the same form, they are called
   * in an order based on the priority of the input tag that references
   * the form.
   *
   * @return true if form processing should continue, false if it should
   *         be aborted
   */
  public boolean beforeSet(DynamoHttpServletRequest request,
                           DynamoHttpServletResponse response)
    throws DropletFormException {
    return true;
  }

  /**
   * Called after all form arguments have been submitted, before the
   * page is serviced (or rendered).  If the a form is submitted to the
   * same page it is rendered, this method is called before the beforeGet
   * method of the DropletFormHandler.
   * <p/>
   * This method is suitable for doing form submit processing on forms
   * that do not have a submit button (i.e. have only a single text field)
   * or for cleaning up after the form has been submitted.
   */
  public boolean afterSet(DynamoHttpServletRequest request,
                          DynamoHttpServletResponse response)
    throws DropletFormException {
    return true;
  }

  /**
   * Called when a form is rendered that references this bean.  This call
   * is made before the service method of the page is invoked.  If form
   * data is processed during the same request, it will be called after
   * all form data has been processed.
   * <p/>
   * If multiple form handlers exist for the same form, they are called
   * in an order based on the priority of the input tag that references
   * the form.
   */
  public void beforeGet(DynamoHttpServletRequest request,
                        DynamoHttpServletResponse response) {
  }

  /**
   * Called after the page containing this form has been rendered,
   * but before the output stream is closed (unless it was closed explicitly by the page)
   * <p/>
   * This method is suitable for releasing any resources obtained during
   * the processing of the request, logging of the operation performed,
   * etc.
   */
  public void afterGet(DynamoHttpServletRequest request,
                       DynamoHttpServletResponse response) {
  }

  /**
   * This is called if an exception occurred when getting or setting
   * the values of the form.  This method is called once
   * for each exception that occurs when processing the setX method.
   */
  public void handleFormException(DropletFormException exception,
                                  DynamoHttpServletRequest request,
                                  DynamoHttpServletResponse response) {
  }

  /**
   * This is called if an unchecked exception occurred when getting or setting
   * the values of the form.  This method is called once
   * for each exception that occurs when processing the setX, getX or handle
   * method.
   */
  public void handleUncheckedFormException(Throwable exception,
                                           DynamoHttpServletRequest request,
                                           DynamoHttpServletResponse response) {
  }
}
