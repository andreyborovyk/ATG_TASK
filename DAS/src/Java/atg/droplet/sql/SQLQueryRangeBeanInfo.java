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

import java.beans.*;

import atg.droplet.*;
import atg.servlet.DynamoServlet;

/**
 * <p>BeanInfo for the SQLQueryRange droplet.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SQLQueryRangeBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class SQLQueryRangeBeanInfo extends DropletBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SQLQueryRangeBeanInfo.java#2 $$Change: 651448 $";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  private final static ParamDescriptor[] sOutputDescriptorsIter = {
    new ParamDescriptor("index", "loop index (0-based)",
                        Integer.class, false, false),
    new ParamDescriptor("count", "loop count (1-based)",
                        Integer.class, false, false),
    /*
     * XXX - need to use the queryBean as a BeanTyper for this - I don't 
     * think there's a way to type the parameters if we are using
     * the querySQL directly.
     */
    new ParamDescriptor("element", "current array element",
                        Object.class, false, false),
    new ParamDescriptor("key", "current key if the array is a Dictionary",
                        Object.class, true, false)
  };

  private final static ParamDescriptor[] sOutputDescriptorsStartEnd = {
    new ParamDescriptor("hasPrev", "true if there are any items before the current array set",
                        String.class, false, false),
    new ParamDescriptor("hasNext", "true if there are any items after the current array set",
                        String.class, false, false),
    new ParamDescriptor("prevStart", "count of the first element of the previous array set (1-based)",
                        String.class, true, false),
    new ParamDescriptor("prevEnd", "count of the last element of the previous array set (1-based)",
                        String.class, true, false),
    new ParamDescriptor("prevHowMany", "number of elements in the previous array set",
                        String.class, true, false),
    new ParamDescriptor("nextStart", "count of the first element of the next array set (1-based)",
                        String.class, true, false),
    new ParamDescriptor("nextEnd", "count of the last element of the next array set (1-based)",
                        String.class, true, false),
    new ParamDescriptor("nextHowMany", "number of elements in the next array set",
                        String.class, true, false)
  };

  private final static ParamDescriptor[] sOutputDescriptorsAll = 
  merge(sOutputDescriptorsIter, sOutputDescriptorsStartEnd);

  private final static ParamDescriptor[] sErrorDescriptors = {
    new ParamDescriptor("SQLException", "SQL exception",
                        Object.class, true, false)
  };

  private final static ParamDescriptor[] sParamDescriptors = {
    new ParamDescriptor("querySQL", 
			"SQL statement that returns a result set.  Refers to paraemters with :<name>.  If you have this, you need connectionURL or dataSource and can't have queryBean.",
                        String.class, true, true),
    new ParamDescriptor("connectionURL", 
			"A JDBC URL referring to the database to open, typically: jdbc:atgpool:<connection-pool-name>",
                        String.class, true, false),
    new ParamDescriptor("dataSource", 
			"A DataSource used to acquire database connections,typically: bean:/atg/dynamo/service/jdbc/JTDataSource",
                        String.class, true, false),
    new ParamDescriptor("transactionManager", 
			"A TransactionManager used to manage Transactions if DataSource is set, typically: bean:/atg/dynamo/transaction/TransactionManager",
                        String.class, true, false),
    new ParamDescriptor("queryBean", 
			"Alternative to querySQL and connectionURL - specifies a SQLQuery bean that defines the query",
                        atg.service.util.SQLQuery.class, true, true),
    new ParamDescriptor("start", "starting count (1-based)",
                        String.class, true, false),
    new ParamDescriptor("howMany", "number of array items in the range",
                        String.class, false, false),
    new ParamDescriptor("outputStart", "rendered before any output tags, if array is not empty",
                        DynamoServlet.class, true, true, sOutputDescriptorsStartEnd),
    new ParamDescriptor("outputEnd", "rendered after any output tags, if array is not empty",
                        DynamoServlet.class, true, true, sOutputDescriptorsStartEnd),
    new ParamDescriptor("output", "rendered once for each array element",
                        DynamoServlet.class, false, true, sOutputDescriptorsAll),
    new ParamDescriptor("empty", "rendered if array is empty",
                        DynamoServlet.class, true, true),
    new ParamDescriptor("error", "rendered if there is a SQL error",
                        DynamoServlet.class, true, true, sErrorDescriptors)
  };

  private final static BeanDescriptor sBeanDescriptor = 
  createBeanDescriptor(SQLQueryRange.class, 
		       "atg.ui.document.wizard.SQLQueryRangeDropletWizard",
		       "This servlet renders an output parameter once for a selected subset of rows returned by a SQL query.",
		       sParamDescriptors);

  //-------------------------------------
  // METHODS
  //-------------------------------------

  //-------------------------------------
  /**
   * Returns the BeanDescriptor for this bean, which will in turn 
   * contain ParamDescriptors for the droplet.
   **/
  public BeanDescriptor getBeanDescriptor() {
    return sBeanDescriptor;
  }

  //----------------------------------------
}
