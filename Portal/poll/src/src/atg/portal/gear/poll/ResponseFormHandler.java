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

import java.io.*;
import java.util.*;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.transaction.*;

import atg.servlet.*;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import javax.servlet.ServletException;
import atg.repository.RepositoryItem;
import atg.repository.*;
import atg.nucleus.Nucleus;
import atg.userprofiling.Profile;
import atg.portal.framework.*;
import atg.portal.alert.*;
import atg.beans.*;
import atg.portal.nucleus.NucleusComponents;
import atg.core.util.StringUtils;

import atg.repository.servlet.RepositoryFormHandler;

/**
 * This is a form handler for manipulating pollResponse items. 
 *
 * @see atg.repository.servlet.RepositoryFormHandler
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/ResponseFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ResponseFormHandler
extends RepositoryFormHandler
{
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/ResponseFormHandler.java#2 $$Change: 651448 $";


  //----------------------------------------
  // property: successUrl
  private String mSuccessUrl;

  public void setSuccessUrl(String pSuccessUrl)
    { mSuccessUrl = pSuccessUrl; }

  public String getSuccessUrl()
    { return mSuccessUrl; }

}
