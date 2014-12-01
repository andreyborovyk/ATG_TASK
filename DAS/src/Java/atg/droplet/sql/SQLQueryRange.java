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

package atg.droplet.sql;

import javax.servlet.*;
import javax.servlet.http.*;

import atg.droplet.*;
import atg.servlet.*;
import atg.service.util.SQLQuery;
import atg.service.util.SQLQueryResults;
import atg.nucleus.ServiceEvent;
import atg.nucleus.ServiceException;
import atg.nucleus.logging.LogListener;

import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.SQLException;

/**
 * This servlet executes a SQL query and
 * renders its <i>output</i> parameter for each element returned
 * by the query.  The query can either be specified as a reference to
 * an SQLQuery bean, or it can be specified inline with parameters.
 * <p>
 * For each row returned by the query, we set the following parameters:
 * <dl>
 * <dt>index
 * <dd>The 0-based index of the returned row
 * <dt>count
 * <dd>The 1-based number of the returned row
 * <dt>element
 * <dt>A dynamic bean that has properties for accessing the values returned
 * in the result set.  You can access the values either by name or by column
 * index.  The name refers to either the column name or the column alias
 * if one was used in the query.  To access values by name, you say:
 * "element.name".  To access values by column index, you say:
 * element.columns[i] where "i" is the 0-based index of the returned column.
 * </dt>
 *
 * <p>As mentioned before, there are two ways that you can specify
 * a query to the SQLQueryForEach droplet.  You can refer to an existing SQLQuery
 * bean using the queryBean parameter.  For example, you might say:
 * <pre>
 *   &lt;param name="queryBean" value="bean:/mySite/MySQLQuery"&gt;
 * </pre>
 * <p>
 * Alternatively, you can specify the SQL directly in the jhtml file with
 * the querySQL parameter.  In this case, you also have to specify the
 * values of the "dataSource" and "transactionManager" parameters. Thus your
 * droplet declaration would include the following parameters:
 * <pre>
 *   &lt;param name="myAge" value="bean:MyUser.age"&gt;
 *   &lt;param name="querySQL" value="select * from person where age=:myAge"&gt;
 *   &lt;param name="dataSource" value="bean:/atg/dynamo/service/jdbc/JTDataSource">&gt;
 *   &lt;param name="transactionManager" value="bean:/atg/dynamo/transaction/TransactionManager">&gt;
 * </pre>
 * The values for the ":name" constructs in your query are taken from the
 * parameters in your request.
 *
 * Two input parameters lets you control which subset of
 * elements in the returned query that are displayed.  They are:
 *
 * <dl>
 * <dt>start
 * <dd>Specifies the starting index (1-based).  For example, to start at
 * the beginning of the array, set <i>start</i> to 1.  If <i>start</i>
 * points past the end of the array, the <i>empty</i> parameter will be
 * rendered.
 *
 * <dt>howMany
 * <dd>Specifies the number of items in the array set to display.  If the
 * combination of <i>start</i> and <i>howMany</i> point past the end of
 * the array, rendering stops after the end of the array is reached.
 * </dl>
 *
 * You can also define a set of output parameters which you can use to
 * control the formatting for the returned results:
 * <dl>
 *
 * <dt>outputStart
 * <dd>This parameter is rendered before any output tags if the array is not
 * empty.
 *
 * <dt>outputEnd
 * <dd>This parameter is rendered after all output tags if the array is not
 * empty.
 *
 * <dt>output
 * <dd>This parameter is rendered for once for each element in the array.
 *
 * <dt>empty
 * <dd>This optional parameter is rendered if the array contains no elements.
 *
 * <dt>error
 * <dd>This optional parameter is rendered if there was an error executing
 * the query.
 *
 * </dl>
 * These parameters are set when rendering the "output" parameters.  They
 * allow you to build links to render the next and previous set of elements.
 *
 * <dt>end
 * <dd>This parameter is set before any of the output parameters are
 * rendered.  It indicates the (1-based) count of the last element in
 * the current array set.
 *
 * <dt>size
 * <dd>This parameter is set before any of the output parameters are
 * rendered.  It indicates the number of elements in the full array.
 *
 * <dl>
 * <dt>hasPrev
 * <dd>This parameter is set to true or false before any of the output
 * parameters are rendered.  It indicates whether there are any array
 * items before the current array set.
 *
 * <dt>prevStart
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasPrev</i> is true.  It indicates the value
 * of <i>start</i> that should be used to display the previous array set.
 *
 * <dt>prevEnd
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasPrev</i> is true.  It indicates the
 * (1-based) count of the last element in the previous array set.
 *
 * <dt>prevHowMany
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasPrev</i> is true.  It indicates the
 * number of elements in the previous array set.
 *
 * <dt>hasNext
 * <dd>This parameter is set to true or false before any of the output
 * parameters are rendered.  It indicates whether there are any array
 * items after the current array set.
 *
 * <dt>nextStart
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasNext</i> is true.  It indicates the value
 * of <i>start</i> that should be used to display the next array set.
 *
 * <dt>nextEnd
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasNext</i> is true.  It indicates the
 * (1-based) count of the last element in the next array set.
 *
 * <dt>nextHowMany
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasNext</i> is true.  It indicates the
 * number of elements in the next array set.
 * </dl>
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SQLQueryRange.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class SQLQueryRange extends Range {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SQLQueryRange.java#2 $$Change: 651448 $";

  /**
   * The names of the parameters to this bean
   */
  static final String ERROR = "error";

  /**
   * The names of outputs for this bean
   */
  static final String SQL_EXCEPTION = "SQLException";

  /**
   * Returns the enumeration from the SQLQuery that contains the elements
   * in the result set
   */
  public Object getArray(DynamoHttpServletRequest pReq) {
    return pReq.getLocalParameter(SQLQueryForEach.SQL_QUERY_RESULTS_PARAM);
  }

  /**
   * Renders the list of items retrieved by the query
   */
  public void service(DynamoHttpServletRequest pReq,
                      DynamoHttpServletResponse pRes)
  		throws ServletException, IOException {
    SQLQueryResults results = null;
    SQLQuery queryBean = SQLQueryForEach.createSQLQuery(this, pReq);
    Dictionary queryArguments = new DictionaryParameterResolver(pReq);

    try {
      results = queryBean.executeQuery(queryArguments);
      pReq.setParameter(SQLQueryForEach.SQL_QUERY_RESULTS_PARAM, results);
      super.service(pReq, pRes);
    }
    catch (SQLException exc) {
      pReq.setParameter(SQL_EXCEPTION, exc);
      pReq.serviceLocalParameter(ERROR, pReq, pRes);
      if (isLoggingError())
        logError("Unable to execute query due to SQLException: " + exc);
    }
    finally {
      try {
        if (results != null) results.close();
      }
      catch (SQLException exc) {}
    }
  }
}
