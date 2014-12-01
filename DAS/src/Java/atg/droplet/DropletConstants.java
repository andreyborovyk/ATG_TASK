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

/*
 * Defines constants needed for Droplet.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletConstants.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public interface DropletConstants {
  //-------------------------------------
  /**
   * Class version string
   */

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletConstants.java#2 $$Change: 651448 $";

  //----------------------------------
  /**
   * The prefix(es) to use for recognizing references to Droplet properties
   */
  public final static String DROPLET_PROPERTY_PREFIX = "property:";
  public final static String DROPLET_BEAN_PREFIX = "bean:";

  /**
   * The prefix to use for recognizing references to Droplet parameters
   */
  public final static String DROPLET_PARAM_PREFIX = "param:";

  //----------------------------------
  /**
   * The prefix to use for identifying Droplet form arguments
   */
  public final static String DROPLET_EVENT_PREFIX = "_D:";

  /**
   * The prefix to use for identifying "submit values" rendered for this tag
   */
  public final static String DROPLET_SUBMIT_VALUE_PREFIX = "_DSV:";


  /**
   * The session secret parameter name.
   */
  public final static String DROPLET_SESSION_CONF = "_dynSessConf";

  //-------------------------------------
  /**
   * The name of the query argument to use for identifying Droplet
   * page requests
   */
  public final static String DROPLET_ARGUMENTS = "_DARGS";

  //-------------------------------------
  /**
   * The name of the query argument that identifies the value for
   * an anchor property
   */
  public final static String DROPLET_ANCHOR_VALUE = "_DAV";

  //----------------------------------
  /**
   * The prefix to use for Anchor IDs
   */
  public final static String DROPLET_ANCHOR_QUALIFIER = "_A:";

  //-------------------------------------
  public static final int PRIORITY_DEFAULT = 0;

  //-------------------------------------
  /**
   * The submit tag has a different default priority to get it to run last
   */
  public static final int SUBMIT_PRIORITY_DEFAULT = -10;

  //-------------------------------------
  /**
   * The name of the request attribute for the droplet event servlet
   */
  public static String DROPLET_EVENT_ATTRIBUTE = "_DropletEventServlet";

  //-------------------------------------
  /**
   * The name of the request attribute for the vector of beforeGet objects
   * that implement DropletFormHandler
   */
  public static String DROPLET_BEFORE_GET_ATTRIBUTE = "_DropletBeforeGet";
  
  //-------------------------------------
  /**
   * The name of the request attribute for the vector of beforeGet objects
   * that implement ObjectFormHandler
   */
  public static String OBJECT_BEFORE_GET_ATTRIBUTE = "_ObjectBeforeGet";

  //-------------------------------------
  /**
   * The name of the request attribute for the Vector of DropletFormExceptions
   * that occurred trying to deliver the events for this request.
   */
  public static String DROPLET_EXCEPTIONS_ATTRIBUTE = "DropletExceptions";

  //-------------------------------------
  /**
   * The name of the request attribute for the Vector of unchecked exceptions
   * that occurred trying to deliver the events for this request.
   */
  public static String UNCHECKED_DROPLET_EXCEPTIONS_ATTRIBUTE = "UncheckedDropletExceptions";

}
