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

package atg.portal.gear.docexch;

import atg.repository.*;
import atg.servlet.*;
import atg.droplet.DropletException;
import java.io.IOException;
import javax.servlet.ServletException;
import atg.nucleus.naming.ParameterName;


/**
 * This assembles an RQL Query for getting a list of documents from the db
 * using spaghetti logic specific to the document exchange gear and then
 * gives the query to the RQLQueryRange droplet.
 *
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/RQLQueryBuilder.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class RQLQueryBuilder extends DynamoServlet
{
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/RQLQueryBuilder.java#2 $$Change: 651448 $";

  public static final ParameterName GEAR_ID_PROPERTY_NAME = ParameterName.getParameterName("gearIdPropertyName");
  public static final ParameterName REPOSITORY = ParameterName.getParameterName("repository");
  public static final ParameterName ITEM_DESCRIPTOR = ParameterName.getParameterName("itemDescriptor");
  public static final ParameterName START = ParameterName.getParameterName("start");
  public static final ParameterName HOW_MANY = ParameterName.getParameterName("howMany");
  public static final ParameterName SORT_PROPERTY = ParameterName.getParameterName("sortProperty");
  public static final ParameterName SORT_DIRECTION = ParameterName.getParameterName("sortDirection");


  public void service(DynamoHttpServletRequest pReq,
                      DynamoHttpServletResponse pRes)
    throws ServletException, IOException
  {
    String query = createQuery(pReq,pRes);
    if (isLoggingDebug())
      logDebug("Query = " + query );
    pReq.setParameter("queryRQL", query);
    pReq.serviceLocalParameter("query", pReq, pRes);
 }

  String createQuery(DynamoHttpServletRequest pReq,
                     DynamoHttpServletResponse pRes)
    throws ServletException, IOException
  {
    String gearIdProp = getGearIdProperty(pReq, pRes);

    // basic query
    StringBuffer query = new StringBuffer();
    if (gearIdProp == null)
      query.append("ALL");
    else
      query.append(gearIdProp).append("=:gearInstanceRef");

    if (isLoggingDebug())
      logDebug("basic query done.  query = " + query.toString());

    /*
    From bug #44682:

        So you're aware, you CAN'T rely on the repository to do
        correctly internationalized sorting for you.  It only
        does sorting based on the numeric id of the characters
        as they're represented in UNICODE.

    And so the "ORDER BY" code here is commented out.  Instead, we get an
    unsorted list here, and use the collator (outside of this class)
    to do i18n sorting.

    WS 8/20/01

    String orderByProperty = getOrderByProperty(pReq,pRes);
    if (orderByProperty != null) {
      query.append(" ORDER BY ").append(orderByProperty);
      String orderByOrder = getOrderByOrder(pReq,pRes);
      if (orderByOrder != null)
        query.append(" ").append(orderByOrder);
    }
    */

    if (isLoggingDebug())
      logDebug("sort directive done.  query = " + query.toString());

    return query.toString();
  }

  String getOrderByProperty (DynamoHttpServletRequest pReq,
                             DynamoHttpServletResponse pRes)
  {
    return (String)pReq.getLocalParameter(SORT_PROPERTY);
  }

  String getOrderByOrder (DynamoHttpServletRequest pReq,
                          DynamoHttpServletResponse pRes)
  {
    return (String)pReq.getLocalParameter(SORT_DIRECTION);
  }

  String getGearIdProperty(DynamoHttpServletRequest pReq,
                      DynamoHttpServletResponse pRes)
    throws ServletException, IOException
  {
    // lookup repository
    Repository rep = null;
    Object obj = pReq.getLocalParameter(REPOSITORY);
    if (obj != null) {
      if (obj instanceof Repository) {
        rep = (Repository) obj;
      }
      // should be repository, but just in case
      else if (obj instanceof String) {
        rep = (Repository) pReq.resolveName((String)obj);
      }
    }
    else {
      throw new ServletException("required parameter '"
                                  + REPOSITORY + "' not passed to droplet");
    }
    if (rep == null) {
      throw new ServletException("passed parameter '"
                                  + REPOSITORY + "' does not refer to a valid repository");
    }


    // get item descriptor
    RepositoryItemDescriptor desc;
    obj = pReq.getLocalParameter(ITEM_DESCRIPTOR);
    if (obj == null) {
      throw new ServletException("required parameter '"
                                  + ITEM_DESCRIPTOR + "' not passed to droplet");
    }
    try {
      desc = rep.getItemDescriptor((String)obj);
    }
    catch (RepositoryException re) {
      if (isLoggingError())
        logError("unable to create ItemDescriptor due to RepositoryException",re);
      // rethrow
      throw new ServletException("invalid ItemDescriptor passed to droplet");
    }
    if (desc == null) {
      throw new ServletException("invalid ItemDescriptor passed to droplet");
    }

    obj = pReq.getLocalParameter(GEAR_ID_PROPERTY_NAME);
    if (obj == null)
      return null;
    if (desc.hasProperty((String)obj))
      return (String)obj;
    return null;
  }
}


