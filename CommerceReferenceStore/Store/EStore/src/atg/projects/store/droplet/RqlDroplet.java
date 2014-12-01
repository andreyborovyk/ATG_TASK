/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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

package atg.projects.store.droplet;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.logging.LogUtils;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import javax.transaction.*;


/**
 * <p>
 * This droplet is used in favor of the ATG RQLQueryForEach droplet. There are
 * two reasons for this. The first is that this droplet must be configured
 * from outside the JSP to prevent the setting of an RQL query string in the
 * JSP template. The second is that this droplet returns the result set (array
 * of repository items) which allows other droplets to loop through them
 * appropriately (Range for example). It also wraps the results in a
 * transaction, something RQLQueryForEach does not do.
 * </p>
 *
 * <p>
 * The repository, itemDescriptorName, and queryRql properties must be set on
 * the nucleus component that uses this class. For example:
 * <pre>
 *
 *  $class=com.awedirect.base.droplet.RqlDroplet
 *  transactionManager=/atg/dynamo/transaction/TransactionManager
 *  repository=/atg/commerce/catalog/ProductCatalog
 *  itemDescriptorName=promotionRelationship
 *  queryRql=contractType.code = ?0
 *
 * </pre>
 * </p>
 *
 * <p>
 * this droplet takes the following parameters
 *
 * <dl>
 * <dt>
 * numRQLParams
 * </dt>
 * <dd>
 * The parameter that defines the URL, relative or absolute, to which this page
 * that called the servlet will be redirected.
 * </dd>
 * </dl>
 *
 *
 * <dl>
 * <dt>
 * param#
 * </dt>
 * <dd>
 * The parameter.
 * </dd>
 * </dl>
 *
 *
 * <dl>
 * <dt>
 * rqlQuery
 * </dt>
 * <dd>
 * The parameter that defines a rqlQuery
 * </dd>
 * </dl>
 * </p>
 *
 * <p>
 * <b>Example </b><br>
 * <pre>
 *
 *
 *  &lt;dsp:droplet name=&quot;RqlDroplet&quot;&gt;
 *    &lt;param name=&quot;numRQLParams&quot; value=&quot;1&quot;&gt;
 *    &lt;param name=&quot;param0&quot; value=&quot;atg.com&quot;&gt;
 *    &lt;dsp:oparam name=&quot;output&quot;&gt;
 *      &lt;dsp:droplet name=&quot;ForEach&quot;&gt;
 *        &lt;dsp:param name=&quot;array&quot; param=&quot;items&quot;/&gt;
 *        &lt;dsp:oparam name=&quot;output&quot;&gt;
 *          &lt;dsp:getvalueof id=&quot;url&quot; idtype=&quot;java.lang.string&quot; param=&quot;element.url&quot;&gt;
 *          &lt;dsp:a page=&quot;&lt;=url&gt;&quot;&gt;
 *            &lt;dsp:valueof param=&quot;element.name&quot;/&gt;
 *          &lt;/dsp:a&gt;&lt;br&gt;
 *          &lt;/dsp:getvalueof&gt;
 *        &lt;/dsp:oparam&gt;
 *      &lt;/dsp:droplet&gt;
 *    &lt;/dsp:oparam&gt;
 *  &lt;/dsp:droplet&gt;
 *
 *
 * </pre>
 * </p>
 *
 * <p>
 * Parameters: <br>
 * &nbsp;&nbsp; <tt>empty</tt>- Rendered if no results found. <br>
 * &nbsp;&nbsp; <tt>output</tt>- Rendered on successful query. <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; <tt>items</tt>- Array of RepositoryItem object
 * that match the query. <br>
 * </p>
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class RqlDroplet extends DynamoServlet {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/RqlDroplet.java#2 $$Change: 651448 $";

  /**
   * XA failure message.
   */
  public static final String XA_FAILURE = "Failure during transaction commit";

  /**
   * Output parameter name.
   */
  public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

  /**
   * Empty parameter name.
   */
  public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

  /**
   * Query RQL parameter name.
   */
  public static final ParameterName QUERY_RQL = ParameterName.getParameterName("queryRQL");

  /**
   * Number of RQL parameters parameter name.
   */
  public static final ParameterName NUMBER_OF_RQL_PARAMS = ParameterName.getParameterName("numRQLParams");

  /**
   * Parameter prefix.
   */
  public static final String PARAM_PREFIX = "param";

  /**
   * Items name.
   */
  public static final String ITEMS = "items";

  /**
   * Repository.
   */
  private Repository mRepository;

  /**
   * Item descriptor name.
   */
  private String mItemDescriptorName;

  /**
   * Query RQL.
   */
  private String mQueryRql;

  /**
   * List of RQL paramters.
   */
  private List mRqlParameters;

  /**
   * Transaction manager.
   */
  private TransactionManager mTransactionManager;

  /**
   * Statement map.
   */
  private Map mStatementMap = new HashMap();
  
  /**
   * Boolean that specifies whether resulted items should be returned as array
   * or as collection. False by default.
   */
  private boolean mResultAsCollection = false;

  /**
   *
   * @return Repository.
   */
  public Repository getRepository() {
    return mRepository;
  }

  /**
   *
   * @param pRepository - repository.
   */
  public void setRepository(Repository pRepository) {
    mRepository = pRepository;
  }

  /**
   *
   * @return item descriptor name.
   */
  public String getItemDescriptorName() {
    return mItemDescriptorName;
  }

  /**
   *
   * @param pItemDescriptorName - item descriptor name.
   */
  public void setItemDescriptorName(String pItemDescriptorName) {
    mItemDescriptorName = pItemDescriptorName;
  }

  /**
   *
   * @return RQL query text.
   */
  public String getQueryRql() {
    return mQueryRql;
  }

  /**
   *
   * @param pQueryRql - RQL query text.
   */
  public void setQueryRql(String pQueryRql) {
    mQueryRql = pQueryRql;
  }

  /**
   *
   * @return list of RQL query parameters.
   */
  public List getRqlParameters() {
    return mRqlParameters;
  }

  /**
   *
   * @param pRqlParameters - list of RQL query parameters.
   */
  public void setRqlParameters(List pRqlParameters) {
    mRqlParameters = pRqlParameters;
  }

  /**
   *
   * @return transaction manager.
   */
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  /**
   *
   * @param pTransactionManager - transaction manager.
   */
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   *
   * @return statement map.
   */
  public Map getStatementMap() {
    return mStatementMap;
  }
  
  /**
  *
  * @return  boolean that specifies whether result should be returned as collection
  *          or as array.
  */
 public boolean isResultAsCollection() {
   return mResultAsCollection;
 }

 /**
  *
  * @param pQueryRql - boolean that specifies whether result should be returned as collection
  *                    or as array.
  */
 public void setResultAsCollection(boolean pResultAsCollection) {
   mResultAsCollection = pResultAsCollection;
 }

  /**
   * Service method.
   * @param pRequest DynamoHttpServletRequest
   * @param pResponse DynamoHttpServletResponse
   *
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    RepositoryItem[] results = null;

    Repository repository = getRepository();
    String descName = getItemDescriptorName();

    // Check to see if queryRQL was passed in from page
    String query = pRequest.getParameter(QUERY_RQL);

    if (query == null) {
      query = getQueryRql();
    }

    Object[] queryParams = getQueryParameters(pRequest, pResponse);

    if (repository == null) {
      if (isLoggingError()) {
        String err = "No repository for this droplet used on page " + pRequest.getRequestURI();
        logError(LogUtils.formatMajor(err));
      }
    }

    if (StringUtils.isEmpty(descName)) {
      if (isLoggingError()) {
        String err = "No item descriptor name for this droplet used on page " + pRequest.getRequestURI();
        logError(LogUtils.formatMajor(err));
      }
    }

    if (StringUtils.isEmpty(query)) {
      if (isLoggingError()) {
        String err = "No query for this droplet used on page " + pRequest.getRequestURI();
        logError(LogUtils.formatMajor(err));
      }
    }

    try {
      // parse the rql statement, storing the statement in
      // the map for performance
      RqlStatement statement = (RqlStatement) getStatementMap().get(query);

      if (statement == null) {
        statement = RqlStatement.parseRqlStatement(query);
        getStatementMap().put(query, statement);
      }

      if (isLoggingDebug()) {
        logDebug("Querying with statement " + statement);
      }

      results = performQuery(repository, descName, statement, queryParams);
    } catch (RepositoryException re) {
      if (isLoggingError()) {
        String err = "RepositoryException using droplet on page " + pRequest.getRequestURI();
        logError(LogUtils.formatMajor(err), re);
      }
    }

    if (isLoggingDebug()) {
      int numResults = 0;

      if (results != null) {
        numResults = results.length;
      }

      logDebug("Found " + numResults + " items for query");
    }

    if ((results == null) || (results.length <= 0)) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    } else {
      if (isResultAsCollection()){
        //return items as collection        
        Collection <RepositoryItem> resultedCollections = new ArrayList <RepositoryItem>(results.length);
        for(RepositoryItem item : results){
          resultedCollections.add(item);
        }
        pRequest.setParameter(ITEMS, resultedCollections);
      }else{
        //return items as array
        pRequest.setParameter(ITEMS, results);
      } 
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
  }

  /**
   * Get query parameters.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   *
   * @return array of query parameters
   *
   * @throws ServletException if error occurs
   * @throws IOException if an error occurs
   */
  protected Object[] getQueryParameters(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    // Stores the number of rql parameters as a string
    String numRQLParamsString = pRequest.getParameter(NUMBER_OF_RQL_PARAMS);

    // The integer representation of the number of RQL parameters
    int numRQLParams = 0;

    // The object parameter array
    Object[] params = null;

    // If the string representation of the number of RQL parameters
    // is null then we will assume no substitution and use the default
    // supplied by getRqlParamters
    if (null == numRQLParamsString) {
      if (isLoggingDebug()) {
        logDebug("No parameter quantity indicated checking rqlParameters property");
      }

      // No parameter query use property setting
      List defaultParams = getRqlParameters();

      // build the query params
      int size = 0;

      if (params != null) {
        size = defaultParams.size();
      }

      Object[] queryParams = new Object[size];

      for (int i = 0; i < size; i++) {
        queryParams[i] = defaultParams.get(i);
      }
    } else {
      // Convert the string representation of the number of rql
      // parameters into an integer. If we are unable to then we
      // probably got a number format exception which we should
      // report as an error
      try {
        numRQLParams = Integer.parseInt(numRQLParamsString);
      } catch (NumberFormatException n) {
        if (isLoggingError()) {
          // Log this specific error
          logError("numRQLParams passed is not a number on page " + pRequest.getRequestURI());
        }

        // Return gracefully
        return null;
      }

      if (isLoggingDebug()) {
        logDebug("numRQLParams passed is " + numRQLParams);
      }

      // Create an array of parameters for the number of parameters
      // specified
      params = new Object[numRQLParams];

      // The temporary parameter value holder
      String paramValue = null;

      // Now loop through all the parameters defined and grab their values
      for (int i = 0; i < numRQLParams; i++) {
        // Grab each parameter valu and dump it into the params array
        paramValue = (String) pRequest.getObjectParameter(PARAM_PREFIX + i);

        // If debug log is turned on
        if (isLoggingDebug()) {
          // Get all the parameters of interest
          logDebug(PARAM_PREFIX + i + "=" + paramValue);
        }

        params[i] = paramValue;
      }
    }

    return params;
  }

  /**
   * <p>
   * Performs the query against the view of the particular repository.
   * </p>
   *
   * @param pRepository - repository
   * @param pViewName - view name
   * @param pStatement - statement
   * @param pParams - parameters
   *
   * @return selected data array
   *
   * @throws RepositoryException if an error occurs
   */
  protected RepositoryItem[] performQuery(Repository pRepository, String pViewName, RqlStatement pStatement,
    Object[] pParams) throws RepositoryException {
    RepositoryItem[] items = null;

    // begin transaction
    Transaction trx = ensureTransaction();

    try {
      // execute query
      RepositoryView view = pRepository.getView(pViewName);
      items = pStatement.executeQuery(view, pParams);
    } finally {
      if (trx != null) {
        try {
          trx.commit();
        } catch (RollbackException exc) {
          if (isLoggingError()) {
            logError(LogUtils.formatMajor(XA_FAILURE), exc);
          }
        } catch (HeuristicMixedException exc) {
          if (isLoggingError()) {
            logError(LogUtils.formatMajor(XA_FAILURE), exc);
          }
        } catch (HeuristicRollbackException exc) {
          if (isLoggingError()) {
            logError(LogUtils.formatMajor(XA_FAILURE), exc);
          }
        } catch (SystemException exc) {
          if (isLoggingError()) {
            logError(LogUtils.formatMajor(XA_FAILURE), exc);
          }
        }
      }
    }

    return items;
  }

  /**
   * Attempts to get current transaction from TransactionManager. If no
   * existing transaction, attempts to start one.
   *
   * @return transaction 
   */
  private Transaction ensureTransaction() {
    TransactionManager trxMgr = getTransactionManager();

    try {
      Transaction trx = trxMgr.getTransaction();

      if (trx == null) {
        trxMgr.begin();

        return trxMgr.getTransaction();
      } else {
        // transaction already exists, don't start and don't commit
        return null;
      }
    } catch (NotSupportedException exc) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("failure getting transaction: "), exc);
      }
    } catch (SystemException exc) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("failure getting transaction: "), exc);
      }
    }

    return null;
  }
}
