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

import java.io.Serializable;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.sql.*;
import java.beans.IntrospectionException;
import java.beans.PropertyEditorSupport;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.transaction.*;
import javax.sql.*;

import atg.core.util.StringUtils;
import atg.core.util.IntHashtable;
import atg.droplet.*;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.service.util.SQLQuery;
import atg.nucleus.*;

import atg.beans.*;
import atg.dtm.*;

/**
 * This form handler implements a common base class for inserting, updating,
 * and deleting objects in a specified SQL table.
 *
 * This bean has a "value" property which itself has properties which represent
 * the properties of the item being operated on by the form handler.
 * <p>
 * The bean itself is in the following states:
 *    <dl>
 *    <dt>STATE_NOTSET
 *    <dd>the bean does not represent a valid item in the table.  any attempt
 *    to get properties of the value, returns only the properties explicitly
 *    set.
 *    <dt>STATE_VALID
 *    <dd>values for the key properties have been set and a query has
 *    successfully returned one or more items
 *    <dt>STATE_INVALID
 *    <dd>the key properties have been set, but no record was found
 *    for a lookup, update, or delete.
 *    <dt>STATE_CONFIG_ERROR
 *    <dd>a configuration error occurred
 *    The error conditions are:
 *    <ul>
 *       <li>lookup, update, or delete when one or more specified key properties
 *       are not set
 *       <li>update when no key properties are defined or when a value for
 *       a specified column property is missing.
 *    </ul>
 *    <dt>STATE_DB_ERROR
 *    <dd>a database error occurred
 *  </dl>
 * <p>
 * The bean supports the following submit methods:
 *    <dl>
 *    <dt>lookup
 *    <dd>if the key properties are set, the a query is made.  If a valid
 *    item is found, the bean is marked as valid and its values are available
 *    as properties of the "value" property.
 *    <dt>update
 *    <dd>update the row (or rows) that correspond to the current values of
 *    the key properties
 *    <dt>delete
 *    <dd>removes the row (or rows) that correspond to the current value of
 *    the key properties
 *    <dt>insert
 *    <dd>adds a new row to the table using the current state of the value
 *    <dt>reset
 *    <dd>removes all properties, errors, and sets the state to STATE_NOTSET
 *    </dl>
 * <p>
 * The <i>value</i> property has sub-properties of its own that represent
 * the values of columns in the current row being operated on by the form
 * handler.  For example, if you want to set or get the "firstName" column
 * in a table, you'd reference this as "FormHandler.value.firstName" using
 * the bean syntax.
 * <p>
 * The name of each sub-property of <i>value</i> should either be
 * in the key or column lists.  The list of keys is used in the lookup,
 * update, and delete operations to define which row (or rows) should be
 * operated on.  The list of columns are used in lookup, update, and insert
 * to specify which columns are arguments to that operation.  For example,
 * the lookup operation will retrieve the values of the column properties.
 * The update operation will modify all columns in the table that are in the
 * list.
 * <p>
 * The SimpleSQLFormHandler bean has the following properties:
 *   <dl>
 *   <dt>tableName
 *   <dd>Specifies the name of the table to operate on.
 *   <dt>UseDataSource
 *   <dd>This property is used to determine how to acquire database connections.
 *   If set to true, DataSource will be used to acquire database connections and
 *   DataSource and TransactionManager must be configured.  If set to false or not
 *   explicitly set it will default to false and connectionURL will be used
 *   to acquire database connections.
 *   <dt>DataSource
 *   <dd>The DataSource used to acquire database connections if UseDataSource
 *   is set to true.
 *   <dt>TransactionManager
 *   <dd>The TransactionManager used to manage transactions in the case that
 *   UseDataSource is set to true.
 *   <dt>connectionURL
 *   <dd>The URL of the connection to use.  Typically, this should be the URL
 *   of a connection pool of the form: "jdbc:atgpool:&lt;poolName&gt;"
 *   <dt>keyColumns
 *   <dd>Specifies the list of column names to use as keys to lookup, delete,
 *   and update rows.
 *   <dt>columns
 *   <dd>Specifies the list of column names to lookup, update, and insert.  If
 *   this is not set or set to null, all columns in the table are used.
 *   <dt>valueIndex
 *   <dd>This property is used only in the lookup
 *   operation, when the lookup operation can retrieve more than one row.
 *   It specifies which row should be used to populate the sub-properties
 *   of the value property.
 *   </dl>
 * <p>
 * The SimpleSQLFormHandler also has a set of properties that are used to
 * control navigation after a form operation (lookup, update, delete, insert)
 * has been completed.  These properties specify the URLs to redirect on
 * certain error and success conditions.  If the value for a particular
 * condition is not set, the form is left on the page defined as the action
 * for that form (i.e. no redirect takes place).  Each operation has its own
 * SuccessURL and ErrorURL properties.  Thus the following properties are
 * available:
 * <ul>
 * <li>lookupSuccessURL
 * <li>lookupErrorURL
 * <li>updateSuccessURL
 * <li>updateErrorURL
 * <li>insertSuccessURL
 * <li>insertErrorURL
 * <li>deleteSuccessURL
 * <li>deleteErrorURL
 * </ul>
 *
 * A separate URL is used if a database error occurs when submitting the
 * form:
 * <ul>
 * <li>dbErrorURL
 * </ul>
 *
 * Note that these URLs are all relative to the page that is the "action"
 * page of the form.  You can either specify the values of these URLs
 * in the .properties file of the form handler, or you can set them in the
 * jhtml file itself using a hidden tag.  For example:
 * <pre>
 *  &lt;input type=hidden bean="FormHandler.lookupErrorURL" value="/notFound.jhtml"&gt;
 * </pre>
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SimpleSQLFormHandler.java#3 $$Change: 658351 $
 * @updated $DateTime: 2011/07/19 16:08:54 $$Author: tterhune $
 * @beaninfo
 *   description: Inserts, updates, and deletes objects in an SQL table
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 */
public class SimpleSQLFormHandler extends GenericFormHandler {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SimpleSQLFormHandler.java#3 $$Change: 658351 $";

  public SimpleSQLFormHandler() {
  }

  SQLFormHashtable mValue = new SQLFormHashtable(this);

  //-------------- Constants -----------------

  //----------------------------------------
  /**
   * The constants that define the different states the form handler
   * can be in
   */
  public final static int STATE_NOTSET = 1;
  public final static int STATE_INVALID = 2;
  public final static int STATE_VALID = 3;
  public final static int STATE_CONFIG_ERROR = 4;
  public final static int STATE_DB_ERROR = 5;

  /**
   * An internal state.  If auto-lookup is true, and the user has set
   * values of our value property, but not yet tried to get any values,
   * we are in a limbo state.  The first attempt to either get the state
   * or get a value of the value property will cause a lookup to be performed
   * at which point the state changes to VALID, INVALID, or an ERROR state
   */
  final static int STATE_PENDING = 6;

  //--------------------------------------------
  /**
   * These are internal constants used for checkFormSubmit and its
   * cohorts
   */
  final static int STATUS_SUCCESS = 0;
  final static int STATUS_ERROR_STAY = 1;
  final static int STATUS_ERROR_REDIRECT = 2;

  //------------- Static variables ---------------

  static Dictionary sTableDescriptor = new Hashtable();

  //--------------------------------------------
  String [] mKeyColumns;

  /**
   * Returns the list of columns used as keys to determine what row to edit.
   * These columns are used for all operations, but are optional for the
   * insert operation.  For lookup, update, and delete, this list of
   * columns is used to identify the row to retrieve, update, or delete.
   * For insert, these columns can be specified to avoid inserting a
   * row whose key columns match an existing row.  In this case a form
   * error is generated that you can use to report this information to the
   * user.
   *
   * @beaninfo
   *   description: Contains the list of column names to use as keys.  The
   *   values of the key columns must uniquely select a single row to edit.
   */
  public String [] getKeyColumns() {
    return mKeyColumns;
  }
  /**
   * Sets the keyColumns property
   */
  public void setKeyColumns(String [] pKeyColumns) {
    mKeyColumns = trimStringArray(pKeyColumns);
  }

  //--------- Property: ConnectionURL -----------
  String mConnectionURL = "jdbc:atgpool:ConnectionPool";
  /**
   * Sets the property ConnectionURL.
   *
   * @beaninfo
   *   description: URL used to connect with the database
   */
  public void setConnectionURL(String pConnectionURL) {
    mConnectionURL = pConnectionURL;
  }
  /**
   * @return The value of the property ConnectionURL.
   */
  public String getConnectionURL() {
    return mConnectionURL;
  }

  //--------------------------------------------
  String [] mColumns;
  /**
   * @return the value of the columns property.  This specifies the list of
   * column names that are used in the lookup, update, and insert operations.
   *
   * @beaninfo
   *   description: Column names used in SQL lookup,insert,update operations
   */
  public String [] getColumns() {
    return mColumns;
  }
  /**
   * Sets the value of the columns property.  This specifies the list of
   * the column names that are used in the lookup, update, and insert
   * operations.
   */
  public void setColumns(String [] pColumns) {
    mColumns = trimStringArray(pColumns);
  }

  //--------------------------------------------
  /**
   * @return the list of all columns that this form handler uses including
   * both the keyColumns and the columns.  If a column appears in both
   * lists, it will only appear in this list once.
   *
   * @beaninfo
   *   description: Both key columns and the column names used in SQL operations
   */
  public String [] getAllColumns() {
    /*
     * If the columns isn't set, we use "all" columns.  Obviously this will
     * already include the key columns
     */
    if (mColumns == null)
      return getColumnNames(getTableName());
    Vector v = new Vector();
    for (int i = 0; i < mColumns.length; i++) {
      if (!v.contains(mColumns[i]))
        v.addElement(mColumns[i]);
    }
    if (mKeyColumns != null) {
      for (int i = 0; i < mKeyColumns.length; i++) {
  if (!v.contains(mKeyColumns[i]))
    v.addElement(mKeyColumns[i]);
      }
    }
    String [] all = new String[v.size()];
    v.copyInto(all);
    return all;
  }

  //--------------------------------------------
  String mTableName;
  /**
   * @return the value of the tableName property.
   * @beaninfo
   *   description: The SQL table on which this operates
   */
  public String getTableName() {
    return mTableName;
  }
  /**
   * Sets the value of the tableName property.  This specifies the name
   * of the table to be used by the lookup, update, delete, and insert
   * operations
   */
  public void setTableName(String pTableName) {
    mTableName = pTableName;
  }

  //--------------------------------------------
  /**
   * Returns a dictionary of values that represent the state of the
   * current row in the database.  These values are split into two
   * groups: those in the keyColumns list, and those in the columns list.
   * The keyColumn properties are used to lookup the row to perform either
   * the lookup, update, or delete operations.  The columns properties are
   * the columns that are looked up by the lookup operation, updated by
   * the update operation and inserted by the insert operation.
   * @beaninfo
   *   description: A dictionary of values representing the current row in
   *                the database
   */
  public Dictionary getValue() {
    return mValue;
  }

  //--------- Property: AutoLookup -----------
  boolean mAutoLookup = true;
  /**
   * Sets the property AutoLookup.  If this property is true, the form
   * handler will automatically do a "lookup" when you attempt to get
   * a property of a value without having done an update, insert, or delete
   * after having set some sub-properties of <code>value</code>.
   * @beaninfo
   *   description: Toggles on/off auto-lookup mode.  When on, auto lookup
   * does a lookup operation if no other operation has been done.
   */
  public void setAutoLookup(boolean pAutoLookup) {
    mAutoLookup = pAutoLookup;
  }
  /**
   * @return The value of the property AutoLookup.
   */
  public boolean getAutoLookup() {
    return mAutoLookup;
  }

  //--------- Property: InitKeyColumnMap -----------
  Properties mInitKeyColumnMap = null;
  /**
   * Sets the property InitKeyColumnMap.  This property can be used
   * to initialize the values of key columns from nucleus properties.
   * You can provide one entry with the key set to the name of the
   * key column, and the value set to the absolute path name of the
   * property you want to initialize it to.  For example, to initialize
   * a key value from a parameter in the request, you'd say:
   *   <code>myKeyName=/OriginatingRequest.myParamName</code>
   * where <code>myKeyName</code> is the name of an entry in your keyColumns
   * list and <code>myParamName</code> is the name of a parameter in the
   * current request.
   * <p>
   * The key values are set in the beforeSet and/or beforeGet methods as
   * called by the droplets system for form submission and form rendering
   * respectively.
   *
   * @beaninfo
   *   description: Stores a mapping of key column names to absolute path
   *   names of property values in nucleus used to initialize the key
   *   column before the form is submitted or values of the form handler
   *   are obtained to render a form.
   */
  public void setInitKeyColumnMap(Properties pInitKeyColumnMap) {
    mInitKeyColumnMap = pInitKeyColumnMap;
  }
  /**
   * @return The value of the property InitKeyColumnMap.
   */
  public Properties getInitKeyColumnMap() {
    return mInitKeyColumnMap;
  }

  //----------------------------------------------
  // The list of URLs that we redirect to on different error/success conditions
  //----------------------------------------------

  //--------- Property: LookupSuccessURL -----------
  String mLookupSuccessURL;
  /**
   * Sets the property LookupSuccessURL.
   * @beaninfo
   *   description: The URL to proceed to after a successful lookup
   */
  public void setLookupSuccessURL(String pLookupSuccessURL) {
    mLookupSuccessURL = pLookupSuccessURL;
  }
  /**
   * @return The value of the property LookupSuccessURL.
   */
  public String getLookupSuccessURL() {
    return mLookupSuccessURL;
  }

  //--------- Property: UpdateSuccessURL -----------
  String mUpdateSuccessURL;
  /**
   * Sets the property UpdateSuccessURL.
   * @beaninfo
   *   description: The URL to proceed to after a successful update
   */
  public void setUpdateSuccessURL(String pUpdateSuccessURL) {
    mUpdateSuccessURL = pUpdateSuccessURL;
  }
  /**
   * @return The value of the property UpdateSuccessURL.
   */
  public String getUpdateSuccessURL() {
    return mUpdateSuccessURL;
  }

  //--------- Property: DeleteSuccessURL -----------
  String mDeleteSuccessURL;
  /**
   * Sets the property DeleteSuccessURL.
   * @beaninfo
   *   description: The URL to proceed to after a successful deletion
   */
  public void setDeleteSuccessURL(String pDeleteSuccessURL) {
    mDeleteSuccessURL = pDeleteSuccessURL;
  }
  /**
   * @return The value of the property DeleteSuccessURL.
   */
  public String getDeleteSuccessURL() {
    return mDeleteSuccessURL;
  }

  //--------- Property: InsertSuccessURL -----------
  String mInsertSuccessURL;
  /**
   * Sets the property InsertSuccessURL.
   * @beaninfo
   *   description: The URL to proceed to after a successful insertion
   */
  public void setInsertSuccessURL(String pInsertSuccessURL) {
    mInsertSuccessURL = pInsertSuccessURL;
  }
  /**
   * @return The value of the property InsertSuccessURL.
   */
  public String getInsertSuccessURL() {
    return mInsertSuccessURL;
  }

  //--------- Property: LookupErrorURL -----------
  String mLookupErrorURL;
  /**
   * Sets the property LookupErrorURL.
   * @beaninfo
   *   description: The URL to proceed to if an error occurs during a lookup
   */
  public void setLookupErrorURL(String pLookupErrorURL) {
    mLookupErrorURL = pLookupErrorURL;
  }
  /**
   * @return The value of the property LookupErrorURL.
   */
  public String getLookupErrorURL() {
    return mLookupErrorURL;
  }

  //--------- Property: UpdateErrorURL -----------
  String mUpdateErrorURL;
  /**
   * Sets the property UpdateErrorURL.
   * @beaninfo
   *   description: The URL to proceed to if an error occurs during an update
   */
  public void setUpdateErrorURL(String pUpdateErrorURL) {
    mUpdateErrorURL = pUpdateErrorURL;
  }
  /**
   * @return The value of the property UpdateErrorURL.
   */
  public String getUpdateErrorURL() {
    return mUpdateErrorURL;
  }

  //--------- Property: DeleteErrorURL -----------
  String mDeleteErrorURL;
  /**
   * Sets the property DeleteErrorURL.
   * @beaninfo
   *   description: The URL to proceed to if an error occurs during a deletion
   */
  public void setDeleteErrorURL(String pDeleteErrorURL) {
    mDeleteErrorURL = pDeleteErrorURL;
  }
  /**
   * @return The value of the property DeleteErrorURL.
   */
  public String getDeleteErrorURL() {
    return mDeleteErrorURL;
  }

  //--------- Property: InsertErrorURL -----------
  String mInsertErrorURL;
  /**
   * Sets the property InsertErrorURL.
   * @beaninfo
   *   description: The URL to proceed to if an error occurs during an insertion
   */
  public void setInsertErrorURL(String pInsertErrorURL) {
    mInsertErrorURL = pInsertErrorURL;
  }
  /**
   * @return The value of the property InsertErrorURL.
   */
  public String getInsertErrorURL() {
    return mInsertErrorURL;
  }

  //--------- Property: DBErrorURL -----------
  String mDBErrorURL;
  /**
   * Sets the property DBErrorURL.
   * @beaninfo
   *   description: The URL to proceed to if a database error occurs
   */
  public void setDBErrorURL(String pDBErrorURL) {
    mDBErrorURL = pDBErrorURL;
  }
  /**
   * @return The value of the property DBErrorURL.
   */
  public String getDBErrorURL() {
    return mDBErrorURL;
  }

  //--------- Property: UseDataSource -----------
  boolean mUseDataSource = false;

  /**
   * Sets the property UseDataSource
   * @beaninfo
   *   description: value determines DataSource or
   * ConnectionPool as database connection source.
   */
  public void setUseDataSource (boolean pUseDataSource)
  { mUseDataSource = pUseDataSource; }

  /**
   * @return The value of the property UseDataSource.
   */
  public boolean getUseDataSource ()
  { return mUseDataSource; }

  //--------- Property: DataSource -----------
  DataSource mDataSource;

  /**
   * Sets the DataSource
   * @beaninfo
   *   description: sets the DataSource to use for db connections
   * if UseDataSource is set to true.
   */
  public void setDataSource (DataSource pDataSource)
  { mDataSource = pDataSource; }

  /**
   * @return The value of the property DataSource.
   */
  public DataSource getDataSource ()
  { return mDataSource; }

  //--------- Property: TransactionManager -----------

  TransactionManager mTransactionManager;

  /**
   * Sets the TransactionManager
   * @beaninfo
   *   description: sets the TransactionManager in the event
   * that UseDataSource is set to true.
   */
  public void setTransactionManager
    (TransactionManager pTransactionManager)
  { mTransactionManager = pTransactionManager; }

  /**
   * @return The value of the property TransactionManager.
   */
  public TransactionManager getTransactionManager ()
  { return mTransactionManager; }


  //-------------------------------------
  /**
   * The name of a catalog.   If this is null (which it probably will be
   * almost always), then the value is determined by querying our
   * Connection.
   */
  String mMetaDataCatalogName = null;

  /**
   * The name of a catalog, used internally only for a
   * DatabaseMetaData.getColumns() call.  If this is null (the default),
   * then we query the Connection for this information.
   * @param String the name of a catalog
   * @see java.sql.DatabaseMetaData
   * @beaninfo
   *    description: String representing the catalog name
   */
  public void setMetaDataCatalogName (String pMetaDataCatalogName)
  { mMetaDataCatalogName = pMetaDataCatalogName;}
  /**
   * Get the catalog name
   * @return the catalog name
   */
  public String getMetaDataCatalogName ()
  { return mMetaDataCatalogName; }


  //-------------------------------------
  /**
   * A String representing a schema name pattern.
   * This value is null by default, and the Connection is queried for
   * this information..
   */
  String mMetaDataSchemaPattern = null;

  /**
   * Set a string representing a schema name pattern, used internally only
   * for a DatabaseMetaData.getColumns() call.  If this is null (the
   * default), then we query the Connection for this information.
   * @param String the schema name
   * @see java.sql.DatabaseMetaData
   * @beaninfo
   *    description: String representing schema name pattern.
   */
  public void setMetaDataSchemaPattern (String pMetaDataSchemaPattern)
  { mMetaDataSchemaPattern = pMetaDataSchemaPattern;}

  /**
   * Get the schema name pattern.
   * @return the schema name
   */
  public String getMetaDataSchemaPattern ()
  { return mMetaDataSchemaPattern; }


  //-------------------------------------
  // property: TablePrefix

  /**
   * Sets a String to be prepended <b>as-is</b> to the table name (tableName).
   * If it is not null (the default), it is used in constructing the SQL for
   * all queries except for internal DatabaseMetaData calls, where
   * metaDataSchemaPattern and metaDataCatalogName are used instead.
   */
  String mTablePrefix;

  /**
   * Sets a String to be prepended <b>as-is</b> to the table name (tableName).
   * If it is not null (the default), it is used in constructing the SQL for
   * all queries except for internal DatabaseMetaData calls, where
   * metaDataSchemaPattern and metaDataCatalogName are used instead.
   *
   * For instance, say Dynamo is connecting to the database as the account
   * "user"; but the table being accessed actually belongs to the account
   * "adm".  Account "adm" has granted "user" select, insert, delete, and
   * update privileges, but has not granted create privileges. In
   * order for SimpleSQLFormHandler to behave properly in this situation, we
   * need to set two properties in the form handler component:
   *
   * <ul>
   *   <li><code>metaDataSchemaPattern=adm</code>
   *   <li><code>tablePrefix=adm.</code>
   * </ul>
   *
   * Note that the dot at the end of "adm." is part of the string being
   * assigned.  The metaDataSchemaPattern is used by internal getMetaData
   * calls.  The tablePrefix is used in constructing all the SQL that is
   * used by the form handler.
   *
   * Why two properties?  This is necessary because some databases require an
   * explicit schema.table pattern for, say, inserting on a table not owned
   * by the user.  Other databases provide a synonym object.  Still other
   * databases have a different punctuation mark than '.'
   *
   * @param String the prefix to be prepended to tableName
   * @beaninfo
   *   description: a string to be prepended as-is tableName for
   *   all user queries.
   **/
  public void setTablePrefix(String pTablePrefix) {
    mTablePrefix = pTablePrefix;
  }

  /**
   * Returns property TablePrefix
   **/
  public String getTablePrefix() {
    return mTablePrefix;
  }

  /**
   * Represents either tableName or, if tablePrefix is not null (the
   * default), will contain "tablePrefix+tableName." No punctuation is
   * inserted by this method between tablePrefix and tableName.
   */
  String mQualifiedTableName = null;

  /**
   * A convenience method to set and retrieve the qualified table name:
   * tablePrefix+tableName.
   * @return either the simple table name, or "tablePrefix+tableName" (no
   * punctuation is inserted), or null if tableName is null.
   */
  protected String getQualifiedTableName() {
    // if it's been set already, return it
    if (mQualifiedTableName != null)
      return mQualifiedTableName;

    if (mTableName == null)
      return null;

    if (mTablePrefix != null){ // user has provided schemaName
      mQualifiedTableName = mTablePrefix + mTableName;
    }else{
      mQualifiedTableName = mTableName;
    }
    return mQualifiedTableName;
  }

  //--------------------------------------------
  /**
   * Before set process any form values, make sure our key columns
   * are set from the mapping specified.
   */
  public boolean beforeSet(DynamoHttpServletRequest request,
                DynamoHttpServletResponse response)
        throws DropletFormException
  {
    boolean status = super.beforeSet(request, response);

    setKeyColumnsFromMap(request, response);
    return status;
  }

  //--------------------------------------------
  /**
   * Before any form values are gotten, make sure we set any key
   * values.
   */
  public void beforeGet(DynamoHttpServletRequest request,
              DynamoHttpServletResponse response) {
    super.beforeGet(request, response);

    setKeyColumnsFromMap(request, response);
  }

  //--------------------------------------------
  public boolean handleLookup(DynamoHttpServletRequest pRequest,
              DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    int status = checkKeyFormSubmit(getLookupErrorURL(), pRequest, pResponse);
    if (status != STATUS_SUCCESS)
      return status == STATUS_ERROR_STAY;

    if (!doLookup()) {
      return processError(getLookupErrorURL(), pRequest, pResponse);
    }

    if (!checkFormSuccess("lookup", getLookupSuccessURL(), pRequest, pResponse)) return false;

    return true;
  }

  //--------------------------------------------
  public boolean handleDelete(DynamoHttpServletRequest pRequest,
              DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    int status = checkKeyFormSubmit(getDeleteErrorURL(), pRequest, pResponse);
    if (status != STATUS_SUCCESS)
      return status == STATUS_ERROR_STAY;

    if (!doDelete()) {
      return processError(getDeleteErrorURL(), pRequest, pResponse);
    }

    if (!checkFormSuccess("delete", getDeleteSuccessURL(), pRequest, pResponse))
      return false;

    return true;
  }

  //--------------------------------------------
  public boolean handleUpdate(DynamoHttpServletRequest pRequest,
              DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    int status = checkKeyFormSubmit(getUpdateErrorURL(), pRequest, pResponse);
    if (status != STATUS_SUCCESS)
      return status == STATUS_ERROR_STAY;

    if (!doUpdate()) {
      return processError(getUpdateErrorURL(), pRequest, pResponse);
    }

    if (!checkFormSuccess("update", getUpdateSuccessURL(), pRequest, pResponse))
      return false;

    return true;
  }

  //--------------------------------------------
  public boolean handleInsert(DynamoHttpServletRequest pRequest,
              DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    int status = checkFormSubmit(getInsertErrorURL(), pRequest, pResponse);
    if (status != STATUS_SUCCESS)
      return status == STATUS_ERROR_STAY;

    if (!doInsert()) {
      return processError(getInsertErrorURL(), pRequest, pResponse);
    }

    if (!checkFormSuccess("insert", getInsertSuccessURL(), pRequest, pResponse))
      return false;

    return true;
  }

  //--------------------------------------------
  /**
   * This resets the state of the form handler component, clearing out
   * the values of any properties and setting the state to STATE_NOTSET.
   */
  public boolean handleReset(DynamoHttpServletRequest pRequest,
              DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    mValue = new SQLFormHashtable(this);
    resetFormExceptions();
    setState(STATE_NOTSET);

    if (!checkFormSuccess("reset", null, pRequest, pResponse)) return false;

    return true;
  }

  //--------------------------------------------
  int mState = STATE_NOTSET;
  /**
   * @beaninfo
   *   description: The state of the form handler (i.e. VALID, NOTSET,
   * INVALID, CONFIG_ERROR, DB_ERROR)
   */
  public int getState() {
    /*
     * Force the state to be VALID, INVALID etc. if the user checks the state
     * first.
     */
    if (mState == STATE_PENDING)
      doLookup();
    return mState;
  }
  /** Returns the internal state */
  int getInternalState() {
    return mState;
  }
  void setState(int pState) {
    mState = pState;
  }

  //---------------------------------
  /**
   * Returns the value of the read-only valid property.  This property is
   * set to true if the most recent operation (lookup, update, insert, or
   * deleted was successfully completed) leaving the sub-properties of the
   * value property in a valid state.
   * @beaninfo
   *   description: True if the form handler represents a valid item in
   * the database.
   */
  public boolean getValid() {
    return mState == STATE_VALID;
  }

  //---------------------------------
  // Methods which generate SQL strings
  //---------------------------------

  //---------------------------------
  /**
   * Returns the SQL string that specifies a select query.  This particular
   * SQL statement does include the values of the key values to be used to
   * lookup the row.
   */
  String getLookupSQL() {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT ");
    if (mColumns == null)
      sb.append("*");
    else {
      for (int i = 0; i < mColumns.length; i++) {
        sb.append(mColumns[i]);
  if (i != mColumns.length - 1) sb.append(", ");
      }
    }
    sb.append(" FROM ");
    sb.append(getQualifiedTableName());
    if (mKeyColumns != null) {
      sb.append(" WHERE ");
      // unused Dictionary value = getValue();
      for (int i = 0; i < mKeyColumns.length; i++) {
        sb.append(mKeyColumns[i]);
  sb.append("=?");
  if (i != mKeyColumns.length - 1)
    sb.append(" AND ");
      }
    }
    if (isLoggingDebug())
      logDebug("Executing Lookup. SQL is: " + sb.toString());
    return sb.toString();
  }

  //---------------------------------
  /**
   * Returns a SQL string that specifies an UPDATE statement to modify
   * one or more rows in the current table.  Because we're using the prepared
   * statement stuff, this SQL actually does not depend on the contents of the
   * update operation itself and so could be reused if that turns out to be
   * worthwhile.
   */
  String getUpdateSQL() {
    StringBuffer sb = new StringBuffer();
    sb.append("UPDATE ");
    sb.append(getQualifiedTableName());
    sb.append(" SET ");
    String [] columns = getColumnNamesToUse();
    boolean first = true;
    for (int i = 0; i < columns.length; i++) {
      int j;
      for (j = 0; j < mKeyColumns.length; j++)
        if (columns[i].equalsIgnoreCase(mKeyColumns[j]))
    break;
      if (j == mKeyColumns.length) {
  if (!first) sb.append(", ");
  first = false;
  sb.append(columns[i]);
  sb.append("=?");
      }
    }
    if (mKeyColumns != null) {
      sb.append(" WHERE ");
      for (int i = 0; i < mKeyColumns.length; i++) {
        sb.append(mKeyColumns[i]);
  sb.append("=?");
  if (i != mKeyColumns.length - 1)
    sb.append(" AND ");
      }
    }
    if (isLoggingDebug())
      logDebug("Executing Update. SQL is: " + sb.toString());
    return sb.toString();
  }

  //---------------------------------
  /**
   * Returns a SQL string that specifies an INSERT statement to insert
   * a row into the current table.  Because we're using the prepared statement
   * stuff, this SQL actually does not depend on the contents of the update
   * operation itself and so could be reused if that turns out to be
   * worthwhile.
   */
  String getInsertSQL() {
    StringBuffer sb = new StringBuffer();
    sb.append("INSERT INTO ");
    sb.append(getQualifiedTableName());
    sb.append(" ( ");
    String [] columns = getColumnNamesToUse();
    for (int i = 0; i < columns.length; i++) {
      sb.append(columns[i]);
      if (i != columns.length - 1) sb.append(", ");
    }
    sb.append(") VALUES (");
    for (int i = 0; i < columns.length; i++) {
      sb.append('?');
      if (i != columns.length - 1) sb.append(", ");
    }
    sb.append(')');
    if (isLoggingDebug())
      logDebug("Executing Insert. SQL is: " + sb.toString());
    return sb.toString();
  }

  //---------------------------------
  /**
   * Returns a SQL string that specifies a DELETE statement to remove
   * the currently specified set of rows from the table.
   */
  String getDeleteSQL() {
    StringBuffer sb = new StringBuffer();
    sb.append("DELETE FROM ");
    sb.append(getQualifiedTableName());
    if (mKeyColumns != null) {
      sb.append(" WHERE ");
      for (int i = 0; i < mKeyColumns.length; i++) {
        sb.append(mKeyColumns[i]);
  sb.append("=?");
  if (i != mKeyColumns.length - 1)
    sb.append(" AND ");
      }
    }
    if (isLoggingDebug())
      logDebug("Executing Delete. SQL is: " + sb.toString());
    return sb.toString();
  }

  //---------------------------------
  // Methods which use JDBC to lookup/update/delete/insert rows
  //---------------------------------

  //---------------------------------
  /**
   * Looks up a row in the database.  The valueIndex property determines
   * which row in the returned set of rows should be used to populate the
   * sub properties of the value property.  This places a
   * TransactionDemarcation around the Lookup operation.
   */
  boolean doLookup()    {

    // First check to see what style of Connections we are using.
    // If we are not using the DataSource do not wrap the call
    // in a TransactionDemarcation.
    if (!mUseDataSource )
        return doNonDemarcatedLookup();

    // OK, We're using the DataSource, not ConnectionPool.
    // Wrap the call to the lookup operation in a TransactionDemarcation.
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = new TransactionDemarcation ();

    try {
        td.begin (tm, TransactionDemarcation.REQUIRED);
        return doNonDemarcatedLookup();
    } catch (TransactionDemarcationException tde) {
        if (isLoggingError())
            logError(tde);
        return false;
    } finally {
        try {
            td.end();
        } catch (TransactionDemarcationException tde) {
            if (isLoggingError())
                logError(tde);
            return false;
        }
    }
  }

  //---------------------------------
  /**
   * Looks up a row in the database.  The valueIndex property determines
   * which row in the returned set of rows should be used to populate the
   * sub properties of the value property.  This does not place a
   * TransactionDemarcation around the Lookup operation.
   */
  boolean doNonDemarcatedLookup() {

    Connection c = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    boolean success = false;
    if (!hasAllKeyColumns(true)) {
      setState(STATE_CONFIG_ERROR);
      return false;
    }
    try {
      c = getConnection();
      stmt = c.prepareStatement(getLookupSQL());
      /*
       * Now set the key property values
       */
      for (int i = 0; i < mKeyColumns.length; i++) {
  stmt.setObject(i + 1, getColumnValueAsSQLType(mKeyColumns[i]));
      }

      rs = stmt.executeQuery();
      success = true;
      if (!rs.next()) {
        setState(STATE_INVALID);
  return false;
      }
      else {
        /*
   * Do not get this before we've validated that the table exists.
   */
  String [] columns = getColumnNamesToUse();

  // unused int col;
  for (int i = 0; i < columns.length; i++) {
    Object value = rs.getObject(i+1);
    if (value != null)
      mValue.put(StringUtils.toUpperCase(columns[i]), value);
    else
      mValue.remove(StringUtils.toUpperCase(columns[i]));
  }
  if (rs.next()) {
    addFormException(new DropletException("More than one row matches the keyColumns for this component", "lookupFoundMultipleRows"));
    setState(STATE_CONFIG_ERROR);
  }
  else setState(STATE_VALID);
      }
    }
    catch (SQLException exc) {
      setState(STATE_DB_ERROR);
      addFormException(new DropletException("A database error occurred looking up the row: " + exc.toString(), exc, "dbErrorOnLookup"));
      return false;
    }
    finally {
      try { if (rs != null) rs.close(); } catch (SQLException exc) {}
      try { if (stmt != null) stmt.close(); } catch (SQLException exc) {}
      try {
        if (c != null) {
          if (success) commit(c);
          else rollback(c);
          c.close();
      }
      } catch (SQLException exc) {}
    }
    return true;
  }

  //------------------------
  /**
   * Updates a row in the database.  Returns true if the update succeeded,
   * false if it failed.  This places a TransactionDemarcation around the
   * Update operation.
   */
  boolean doUpdate()    {

    // First check to see what style of Connections we are using.
    // If we are not using the DataSource do not wrap the call
    // in a TransactionDemarcation.
    if (!mUseDataSource )
        return doNonDemarcatedUpdate();

    // OK, We're using the DataSource, not ConnectionPool.
    // Wrap the call to the update operation in a TransactionDemarcation.
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = new TransactionDemarcation ();

    try {
        td.begin (tm, TransactionDemarcation.REQUIRED);
        return doNonDemarcatedUpdate();
    } catch (TransactionDemarcationException tde) {
        if (isLoggingError())
            logError(tde);
        return false;
    } finally {
        try {
            td.end();
        } catch (TransactionDemarcationException tde) {
            if (isLoggingError())
                logError(tde);
            return false;
        }
    }
  }

  //------------------------
  /**
   * Updates a row in the database.  Returns true if the update succeeded,
   * false if it failed.  This does not place a TransactionDemarcation
   * around the Update operation.
   */
  boolean doNonDemarcatedUpdate() {
    Connection c = null;
    PreparedStatement ps = null;
    boolean success = false;
    /*
     * Don't allow updates of the entire table!
     */
    if (mKeyColumns == null || mKeyColumns.length == 0 ||
  !hasAllKeyColumns(true) || !hasAllColumns(true)) {
      setState(STATE_CONFIG_ERROR);
      return false;
    }
    try {
      c = getConnection();

      /*
       */
      ps = c.prepareStatement(getUpdateSQL());

      String [] columns = getColumnNamesToUse();

      /*
       * Set the column properties to update
       */
      int v = 1;
      for (int i = 0; i < columns.length; i++) {
        int j;
  /*
   * We need to skip the key columns since the mysql database may not
   * let you update them even if their values haven't changed.
   * To let users change the key columns, we'd have to keep track
   * of the original values anyway.
   */
        for (j = 0; j < mKeyColumns.length; j++)
    if (columns[i].equalsIgnoreCase(mKeyColumns[j]))
      break;
  if (j == mKeyColumns.length) { /* not a key column */
    Object obj = getColumnValueAsSQLType(columns[i]);
    if (obj == null)
      ps.setNull(v, getColumnSQLType(columns[i]));
    else
      ps.setObject(v, obj);
    v++;
  }
      }

      /*
       * Now set the key property values
       */
      for (int i = 0; i < mKeyColumns.length; i++) {
  ps.setObject(i + v, getColumnValueAsSQLType(mKeyColumns[i]));
      }
      int status = ps.executeUpdate();
      if (status == 0) {
        setState(STATE_INVALID);
  return false;
      }
      if (status > 1) {
        setState(STATE_CONFIG_ERROR);
  addFormException(new DropletException("The key columns map to more than one row for the update", "triedToUpdateMultipleRows"));
  success = false;
      }
      else {
  success = true;
  setState(STATE_VALID);
      }
    }
    catch (SQLException exc) {
      setState(STATE_DB_ERROR);
      addFormException(new DropletException("A database error occurred updating a row: " + exc.toString(), exc, "dbUpdateError"));
      return false;
    }
    finally {
      try { if (ps != null) ps.close(); } catch (SQLException exc) {}
      try {
        if (c != null) {
          if (success) commit(c);
          else rollback(c);
          c.close();
      }
      } catch (SQLException exc) {}
    }
    return true;
  }

  //------------------------
  /**
   * Inserts a row into the database.  Returns true if the insert succeeded,
   * false if it failed.  This places a TransactionDemarcation around the
   * Insert operation.
   *
   * petere: Had to add the boolean 'result' local to work around a
   * java.lang.Verify exception that begain occuring with JDK 1.2.
   * This result variable replaces return statements. According to Sun
   * (http://java.sun.com/products/jdk/1.2/changes.html#n16) javac in
   * JDK 1.1 and 1.2 generate incorrect byte code for certain
   * combinations of try/finally use statements.  Apparently jikes does too.
   */
  boolean doInsert()    {

    // First check to see what style of Connections we are using.
    // If we are not using the DataSource do not wrap the call
    // in a TransactionDemarcation.
    if (!mUseDataSource )
        return doNonDemarcatedInsert();

    // OK, We're using the DataSource, not ConnectionPool.
    // Wrap the call to the insert operation in a TransactionDemarcation.
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = new TransactionDemarcation ();

    try {
        td.begin (tm, TransactionDemarcation.REQUIRED);
        return doNonDemarcatedInsert();
    } catch (TransactionDemarcationException tde) {
        if (isLoggingError())
            logError(tde);
        return false;
    } finally {
        try {
            td.end();
        } catch (TransactionDemarcationException tde) {
            if (isLoggingError())
                logError(tde);
            return false;
        }
    }
  }

  //------------------------
  /**
   * Inserts a row into the database.  Returns true if the insert succeeded,
   * false if it failed.  This does not place a TransactionDemarcation
   * around the Insert operation.
   *
   * petere: Had to add the boolean 'result' local to work around a
   * java.lang.Verify exception that begain occuring with JDK 1.2.
   * This result variable replaces return statements. According to Sun
   * (http://java.sun.com/products/jdk/1.2/changes.html#n16) javac in
   * JDK 1.1 and 1.2 generate incorrect byte code for certain
   * combinations of try/finally use statements.  Apparently jikes does too.
   */
  boolean doNonDemarcatedInsert() {
    Connection c = null;
    PreparedStatement ps = null;
    boolean success = false;
    boolean result = true;
    /*
     * Make sure we have all of the columns that we need
     */
    if (!hasAllColumns(true)) {
      setState(STATE_CONFIG_ERROR);
      return false;
    }
    try {
      c = getConnection();

      /*
       * First we see if there's already a row in the table that matches
       * the keyColumns (if there are any).  If so, we throw a "can't insert"
       * error.
       */
      if (hasAllKeyColumns(false)) {
        PreparedStatement stmt = null;
  ResultSet rs = null;
        try {
    stmt = c.prepareStatement(getLookupSQL());

    /*
     * Now set the key property values
     */
    for (int i = 0; i < mKeyColumns.length; i++) {
      stmt.setObject(i + 1, getColumnValueAsSQLType(mKeyColumns[i]));
    }

    rs = stmt.executeQuery();
    /*
     * We already found a row in the table with these keys.
     */
    if (rs.next()) {
      addFormException(new DropletException("An attempt was made to insert a row whose key columns match an existing row", "insertDuplicateRow"));
      setState(STATE_INVALID);
            result = false;
    }
  }
  finally {
    try {
      if (rs != null) rs.close();
    }
    catch (SQLException exc) {}
    if (stmt != null) stmt.close();
  }
      }

      if ( result )
      {
        ps = c.prepareStatement(getInsertSQL());

        String [] columns = getColumnNamesToUse();

        /*
       * Set the column properties to update
       */
        for (int i = 0; i < columns.length; i++) {
          Object obj = getColumnValueAsSQLType(columns[i]);
          if (obj == null)
            ps.setNull(i+1, getColumnSQLType(columns[i]));
          else
            ps.setObject(i+1, obj);
        }
        ps.executeUpdate();
        setState(STATE_VALID);
        success = true;
      }
    }
    /*
     */
    catch (SQLException exc) {
      setState(STATE_DB_ERROR);
      addFormException(new DropletException("A database error occurred trying to insert a row: " + exc.toString(), exc, "dbInsertError"));
      result = false;
    }
    finally {
      try {
        if (ps != null) ps.close();
      }
      catch (SQLException exc) {}
      try {
        if (c != null) {
      if (success) commit(c); else rollback(c);
      c.close();
  }
      } catch (SQLException exc) {}
    }

    return result;
  }

  //------------------------
  /**
   * Deletes a row (or rows) from the database.
   * Returns true if the delete succeeded, false if it failed.
   * This places a TransactionDemarcation around the Delete operation.
   */
  boolean doDelete()    {

    // First check to see what style of Connections we are using.
    // If we are not using the DataSource do not wrap the call
    // in a TransactionDemarcation.
    if (!mUseDataSource )
        return doNonDemarcatedDelete();

    // OK, We're using the DataSource, not ConnectionPool.
    // Wrap the call to the delete operation in a TransactionDemarcation.
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = new TransactionDemarcation ();

    try {
        td.begin (tm, TransactionDemarcation.REQUIRED);
        return doNonDemarcatedDelete();
    } catch (TransactionDemarcationException tde) {
        if (isLoggingError())
            logError(tde);
        return false;
    } finally {
        try {
            td.end();
        } catch (TransactionDemarcationException tde) {
            if (isLoggingError())
                logError(tde);
            return false;
        }
    }
  }

  //------------------------
  /**
   * Deletes a row (or rows) from the database.
   * Returns true if the delete succeeded, false if it failed.
   * This does not place a TransactionDemarcation around the Delete
   * operation.
   */
  boolean doNonDemarcatedDelete() {
    Connection c = null;
    PreparedStatement ps = null;
    boolean success = false;
    /*
     * Don't allow deletes of the entire table and make sure we have
     * all specified key values before proceeding.
     */
    if (mKeyColumns == null || mKeyColumns.length == 0 ||
  !hasAllKeyColumns(true)) {
      setState(STATE_CONFIG_ERROR);
      return false;
    }
    try {
      c = getConnection();

      /*
       */
      ps = c.prepareStatement(getDeleteSQL());

      /*
       * Set the column properties to update
       */
      for (int i = 0; i < mKeyColumns.length; i++) {
        ps.setObject(i+1, getColumnValueAsSQLType(mKeyColumns[i]));
      }
      ps.executeUpdate();
      /*
       */
      setState(STATE_VALID);
      success = true;
    }
    /*
     */
    catch (SQLException exc) {
      setState(STATE_DB_ERROR);
      addFormException(new DropletException("A database error occurred trying to delete a row: " + exc.toString(), exc, "dbDeleteError"));
      return false;
    }
    finally {
      try { if (ps != null) ps.close(); } catch (SQLException exc) {}
      try {
        if (c != null) {
          if (success) commit(c);
          else rollback(c);
          c.close();
      }
      } catch (SQLException exc) {}
    }
    return true;
  }

  //------------------------
  // Utility methods
  //------------------------

  //--------------------------------------------
  boolean processError(String pSpecificURL, DynamoHttpServletRequest pRequest,
             DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    String redirectURL = null;
    if (getInternalState() == STATE_DB_ERROR) redirectURL = getDBErrorURL();
    if (redirectURL == null) redirectURL = pSpecificURL;
    if (redirectURL != null) {
      if (isLoggingDebug())
        logDebug("error - redirecting to: " + redirectURL);
      pResponse.sendLocalRedirect(redirectURL, pRequest);
      return false;
    }
    return true;
  }

  //------------------------
  /**
   * This method determines whether or not any errors have occurred in
   * processing the form arguments.  It also makes sure that the form
   * handler is configured properly.
   *
   * If so, it handles any specified redirect for this error condition and
   * sets the state
   */
  int checkFormSubmit(String pErrorURL,
            DynamoHttpServletRequest pRequest,
          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    if (getFormError())
      return processError(pErrorURL, pRequest, pResponse) ? STATUS_ERROR_STAY :
                              STATUS_ERROR_REDIRECT;
    if ( mUseDataSource )   {
        if (mTableName == null || mDataSource == null || mTransactionManager == null ) {
        setState(STATE_CONFIG_ERROR);
        addFormException(new DropletException("missing value for tableName " +
            "and/or DataSource and/or TransactionManager") );
        return !processError(pErrorURL, pRequest, pResponse) ?
            STATUS_ERROR_REDIRECT : STATUS_ERROR_STAY;
        }
    } else {
        if (mTableName == null || mConnectionURL == null) {
        setState(STATE_CONFIG_ERROR);
        addFormException(new DropletException("missing value for tableName and/or connectionURL properties"));
        return !processError(pErrorURL, pRequest, pResponse) ?
              STATUS_ERROR_REDIRECT : STATUS_ERROR_STAY;
        }
    }

    return STATUS_SUCCESS;
  }

  //------------------------
  int checkKeyFormSubmit(String pErrorURL,
           DynamoHttpServletRequest pRequest,
         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    int status;
    if ((status = checkFormSubmit(pErrorURL, pRequest, pResponse)) != STATUS_SUCCESS)
      return status;

    if (!hasAllKeyColumns(true)) {
      setState(STATE_CONFIG_ERROR);
      return !processError(null, pRequest, pResponse) ? STATUS_ERROR_REDIRECT : STATUS_ERROR_STAY;
    }
    return STATUS_SUCCESS;
  }

  //------------------------
  /**
   * If the form was successfully submitted, see if we should redirect
   * after processing the form data.  If we redirect, false is returned.
   * otherwise
   */
  boolean checkFormSuccess(String pOperation,
           String pSpecificURL,
         DynamoHttpServletRequest pRequest,
           DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {

    if (isLoggingDebug()) {
      logDebug("Successfully completed " + pOperation + ".  Contents of value follow:");
      for (Enumeration e = getValue().keys(); e.hasMoreElements(); ) {
        Object key = e.nextElement();
        logDebug("name=" + key + " value=" + getValue().get(key) + " class=" +
      getValue().get(key).getClass().getName());
      }
      logDebug("(end of values)");
    }

    if (pSpecificURL != null) {
      if (isLoggingDebug())
        logDebug("success - redirecting to: " + pSpecificURL);
      pResponse.sendLocalRedirect(pSpecificURL, pRequest);
      return false;
    }
    return true;
  }

  //------------------------
  /**
   * Does the form handler have values for all of the key properties?
   */
  boolean hasAllKeyColumns(boolean pReportError) {
    /*
     * Having NO key columns is different than having all of them!
     */
    if (mKeyColumns == null || mKeyColumns.length == 0) {
      if (pReportError) addFormException(new DropletException("There is no value for the keyColumns property on: " + getAbsoluteName(), "noValueForKeyColumns"));
      /*
       * If we are not reporting an error, it is OK to have a null key columns
       * array.  If we are, we should return an error here.
       */
      return false;
    }
    for (int i = 0; i < mKeyColumns.length; i++) {
      if (mValue.get(StringUtils.toUpperCase(mKeyColumns[i])) == null) {
        if (pReportError)
    addFormException(new DropletFormException("You did not supply a value for the " + mKeyColumns[i] + " property",  getAbsoluteName() + ".value." + mKeyColumns[i], "missingValueForKeyColumn"));
        return false;
      }
    }
    return true;
  }

  //------------------------
  /**
   * Does the form handler have values for all of the column properties?
   */
  boolean hasAllColumns(boolean pReportError) {
    String [] columns = getColumnNamesToUse();
    for (int i = 0; i < columns.length; i++) {
      String columnName = StringUtils.toUpperCase(columns[i]);
      if (getColumnRequired(columnName) && mValue.get(columnName) == null) {
        if (pReportError)
    addFormException(new DropletFormException("missing value for column: " + columns[i],
        getAbsoluteName() + ".value." + columns[i],
      "missingValueForColumn"));
        return false;
      }
    }
    return true;
  }

  //---------------------------
  /**
   * Returns the names of the columns that we are to update in the order
   * that we are to update them.
   */
  String [] getColumnNamesToUse() {
    String [] columns;
    if (mColumns == null) columns = getColumnNames(getTableName());
    else columns = mColumns;
    return columns;
  }

  //------------------------
  /**
   * Returns the ordered list of columns names in the specified table.
   */
  String [] getColumnNames(String pTableName) {
    TableDescriptor cdesc = getTableDescriptor(pTableName);
    /* This is only null for error conditions */
    if (cdesc == null) return new String[0];
    return cdesc.columnNames;
  }

  //------------------------
  /**
   * Returns the type for a specific column in the table.
   */
  int getColumnSQLType(String pColumnName) {
    TableDescriptor cdesc = getTableDescriptor(getTableName());
    /* This is only null for error conditions */
    if (cdesc == null) return 0;
    return ((Integer)cdesc.columnTypes.get(pColumnName.toUpperCase())).intValue();
  }

  //------------------------
  /**
   * Returns a boolean if this column is required.
   */
  boolean getColumnRequired(String pColumnName) {
    TableDescriptor cdesc = getTableDescriptor(getTableName());
    /* This is only null for error conditions */
    if (cdesc == null) return false;
    /* An entry is only added if it is required */
    return cdesc.columnRequired.get(pColumnName) != null;
  }

  /**
   * Returns the currently stored value as a SQL type
   */
  Object getColumnValueAsSQLType(String pColumnName) {
    try {
      Object obj = SQLQuery.convertToSQLType(mValue.get(StringUtils.toUpperCase(pColumnName)), getColumnSQLType(pColumnName));
      if (isLoggingDebug())
        logDebug("setting column " + pColumnName + " to: " + obj + " (class="
           + (obj != null ? obj.getClass().getName() : "null") + ")");
      return obj;
    }
    catch (IllegalArgumentException exc) {
      addFormException(new DropletFormException("Can't convert the object to the appropriate SQL type", exc, getAbsoluteName() + ".value." + pColumnName, "javaToJDBCConversionFailed"));
    }
    return null;
  }

  /**
   * Gets the table descriptor for the specified table.  This
   * places a TransactionDemarcation around the operation.
   */
  synchronized TableDescriptor getTableDescriptor(String pTableName)    {

    // First check to see what style of Connections we are using.
    // If we are not using the DataSource do not wrap the call
    // in a TransactionDemarcation.
    if (!mUseDataSource )
        return getNonDemarcatedTableDescriptor(pTableName);

    // OK, We're using the DataSource, not ConnectionPool.
    // Wrap the operation (of getting the descriptor) in a
    // TransactionDemarcation.
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = new TransactionDemarcation ();

    try {
        td.begin (tm, TransactionDemarcation.REQUIRED);
        return getNonDemarcatedTableDescriptor(pTableName);
    } catch (TransactionDemarcationException tde) {
        if (isLoggingError())
            logError(tde);
        return null;
    } finally {
        try {
            td.end();
        } catch (TransactionDemarcationException tde) {
            if (isLoggingError())
                logError(tde);
            return null;
        }
    }
  }

  /**
   * Gets the table descriptor for the specified table.  This
   * does not place a TransactionDemarcation around the operation.
   */
  TableDescriptor getNonDemarcatedTableDescriptor(String pTableName) {

    TableDescriptor cdesc = (TableDescriptor) sTableDescriptor.get(getConnectionString() + ":::" + pTableName);
    if (cdesc == null) {
      Connection c = null;
      ResultSet rs = null;
      boolean success = false;
      Vector columnNames = new Vector();
      Dictionary columnTypes = new Hashtable();
      Dictionary columnRequired = new Hashtable();
      boolean autoCommitSwitched = false;
      String useTableName = null;
      String catalogName = mMetaDataCatalogName;
      String schemaName = mMetaDataSchemaPattern;
      try {
        c = getConnection();

	DatabaseMetaData dmd = c.getMetaData();
	String database = dmd.getDatabaseProductName();
	database = database.toLowerCase();

	/*
	 * On oracle, the catalog forces all tables to be upper case
	 */
	if (database.indexOf("oracle") != -1) {
	  useTableName = StringUtils.toUpperCase(pTableName);
	  /*
	   * oracle requires that we pass in the user name as the schema
	   * name.
	   */
          // only if the configurable schemaName is blank...
          if (schemaName == null)
            schemaName = dmd.getUserName();

	}else{
	  useTableName = pTableName;
        }

        // if db uses upper case ids, convert
        if (dmd.storesUpperCaseIdentifiers()){
          if (schemaName != null)
            schemaName = StringUtils.toUpperCase(schemaName);
          if (catalogName != null)
            catalogName = StringUtils.toUpperCase(catalogName);
          if (useTableName != null)
            useTableName = StringUtils.toUpperCase(useTableName);
        }

	/*
	 * On sybase, we need to force the driver out of a current
	 * transaction before we try to get the list of columns
	 */
        if (database.indexOf("sybase") != -1) {
    try {
      if (!c.getAutoCommit()) {
        autoCommitSwitched = true;
        rollback(c);
        // we need to test this
        c.setAutoCommit(true);
        rollback(c);
      }
    }
    catch (SQLException exc) {
    }
  }

        /*
	 * Explicit use of the catalog here prevents sybase from returning
	 * all of the columns in the server.  It should only return those
	 * for this user's table (otherwise we get duplicates if more than
	 * one user has a table of this name)
	 */
        String catalog =
          (catalogName != null) ? catalogName : c.getCatalog();

	if (isLoggingDebug())
	  logDebug("Catalog name is: " + catalog
                   + " user name is: " + dmd.getUserName()
                   + " schema name is: " + schemaName
                   +" table name is: " + useTableName);

	rs = dmd.getColumns(catalog, schemaName, useTableName, null);

	while (rs.next()) {
	  String n = rs.getString(4);
	  columnNames.addElement(n);
	  columnTypes.put(n.toUpperCase(), new Integer(rs.getShort(5)));
	  int nullable = rs.getInt(11);
	  if (nullable == DatabaseMetaData.columnNoNulls)
	    columnRequired.put(n.toUpperCase(), Boolean.TRUE);
	  /*
	   * Mark this true only after we get the first column.  On oracle
	   * the getColumns just returns an empty result set if the table
	   * does not exist.
	   */
	  success = true;
	}
      }
      catch (SQLException exc) {
        if (isLoggingError())
    logError("database error: " + exc);
        addFormException(new DropletException("Can't get the table information for table: " + pTableName + " :" + exc.toString(), exc, "dbMetadataError"));
  return null;
      }
      finally {
        try {
    if (autoCommitSwitched)
            c.setAutoCommit(false);
  }
  catch (SQLException exc) {
  }

        try { if (rs != null) rs.close(); } catch (SQLException exc) {}
  try {
    if (c != null) {
        if (success) commit(c);
        else rollback(c);
          c.close();
    }
  } catch (SQLException exc) {}
      }
      String [] cnames = new String[columnNames.size()];
      if (isLoggingDebug())
        logDebug("Getting column names/types for table: " + pTableName);
      for (int i = 0; i < cnames.length; i++) {
        cnames[i] = (String) columnNames.elementAt(i);
        if (isLoggingDebug())
    logDebug("Column " + i + " name=" + cnames[i] + " has type=" +
             columnTypes.get(cnames[i].toUpperCase()) + " is required=" +
       (columnRequired.get(cnames[i].toUpperCase()) == null ?
       "false" : "true"));
      }
      cdesc = new TableDescriptor(cnames, columnTypes, columnRequired);
      /*
       * SQLINA returns a 0 length column list if the table does not
       * exist instead of getting an error.  If we cache this, it will
       * screw us up later.
       */
      if (success)
        sTableDescriptor.put(getConnectionString() + ":::" +pTableName, cdesc);
      else
        return null;
    }
    return cdesc;
  }


  //--------------------------------------------
  /** Removes white space from the strings in a string array */
  String [] trimStringArray(String [] pArray) {
    if (pArray != null) {
      if (pArray.length == 0) return null;
      /*
       * Need to get rid of the white space in the names
       */
      String [] trimmed = new String[pArray.length];
      for (int i = 0; i < pArray.length; i++)
        trimmed[i] = pArray[i].trim();
      pArray = trimmed;

      /* nucleus turns foo= into this case */
      if (pArray.length == 1 && pArray[0].length() == 0)
        return null;
    }
    /* Treat zero length arrays the same as if they were empty */
    // would always NPE due to the null check above else if (pArray.length == 0)
    //  return null;
    return pArray;
  }

  /*
   * Go through the initKeyColumnMap and set each key value based on the
   * properties we get out.
   *
   * This gets called from beforeSet and from beforeGet (if not called in
   * beforeSet).
   *
   * We only do this once for each request.  To prevent doing it in both
   * the beforeSet and beforeGet methods, we have to stick in a key in
   * the request that is unique for this form handler.
   */
  void setKeyColumnsFromMap(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) {
    try {
      String [] keys = getKeyColumns();
      String myKey;

      if (mInitKeyColumnMap != null && keys != null &&
          pRequest.getParameter((myKey = String.valueOf(this.hashCode()))) == null) {
  pRequest.setParameter(myKey, Boolean.TRUE);
  for (int i = 0; i < keys.length; i++) {
    String propertyPath = mInitKeyColumnMap.getProperty(keys[i]);
    if (propertyPath != null) {
      Object value = DropletDescriptor.getPropertyValue(
                 pRequest, pResponse,
                 propertyPath, true, null, null);
      if (isLoggingDebug()) {
        logDebug("setting key column name=" + keys[i] + " to value=" + value + " (from property=" + propertyPath + ")");
      }
      if (value == null)
        mValue.remove(StringUtils.toUpperCase(keys[i]));
      else {
        mValue.put(StringUtils.toUpperCase(keys[i]), value);
        if (mAutoLookup)
          setState(STATE_PENDING);
      }
    }
  }
      }
    }
    catch (ServletException exc) {
      addFormException(new DropletException("Can't set initKeyColumnMap", exc, "badInitKeyColumnMap"));
    }
  }

  /**
   * Returns a string dependent upon whether we are using
   * ConnectionPool or DataSource.  If ConnectionPool we
   * return the ConnectionURL, if DataSource the service
   * name of the DataSource.
   */
  String getConnectionString()  {

    String sourceString = null;

    if ( mUseDataSource )   {
        if ( mDataSource instanceof GenericService ) {
            GenericService service = (GenericService)mDataSource;
            sourceString = service.getAbsoluteName();
        } else {
            sourceString = String.valueOf(mDataSource.hashCode());
        }
    } else  {
        sourceString = getConnectionURL();
    }

    return sourceString;
  }

  /**
   * Performs a commit action dependent upon whether the connection
   * was received from a DataSource or DriverManager.
   */
  void commit( Connection pConnection ) {
    if ( mUseDataSource )  {
        // do nothing
    } else  {
        try { pConnection.commit(); } catch( SQLException exc ) {}
    }
  }

  /**
   * Performs a rollback action dependent upon whether the connection
   * was received from a DataSource or DriverManager.
   */
  void rollback( Connection pConnection )   {
    if ( mUseDataSource )    {
        if ( mTransactionManager != null )  {
            try {
                mTransactionManager.setRollbackOnly();
            } catch( SystemException se )   {
                if ( isLoggingError() ) {
                    logError( se );
                }
            }
        }
    } else
        try { pConnection.rollback(); } catch( SQLException se ) {}
  }

  /*
   * Returns a database Connection from one of two possible sources
   * dependent upon the property UseDataSource.  If UseDataSource is
   * set to true a connection is acquired from the DataSource.  If false,
   * the connection is acquired from the DriverManager.
   */
  Connection getConnection()
  throws SQLException
  {
    Connection conn = null;

    // return connection dependent upon DataSource property
    if ( mUseDataSource )    {
        if ( mDataSource != null )
            conn = mDataSource.getConnection();
    } else    {
        conn = DriverManager.getConnection(getConnectionURL());
    }

    return conn;
  }

  /**
   * This registers the mapping between the FormHashtable class and
   * the "dynamic beans" mechanism which allows to set/get these values
   * from dynamo server pages.
   */
  static {
    DynamicBeans.registerPropertyMapper(SQLFormHashtable.class,
          new SQLFormHashtablePropertyMapper());
  }

  /**
   * This class implements a property editor which does normal integer
   * parsing, but also check for 'true' treating it as 1 and 'false" as 0.
   *
   * This is necessary because some SQL databases use integer as their
   * bit/boolean type.
   */
  public static class FlexibleIntegerPropertyEditor extends PropertyEditorSupport {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SimpleSQLFormHandler.java#3 $$Change: 658351 $";

    public FlexibleIntegerPropertyEditor() {}
    static Integer INTEGER_TRUE = new Integer(1);
    static Integer INTEGER_FALSE = new Integer(0);
    public void setAsText(String pStr) throws IllegalArgumentException {
      /*
       * Return an empty string here so that we can convert it to a "null"
       * value in the SQL conversion.  The droplets system won't deliver
       * the value if we return null here.
       */
      if (pStr == null || pStr.trim().length() == 0) setValue(new String(""));
      else {
  if (pStr.equalsIgnoreCase("true")) setValue(INTEGER_TRUE);
  else if (pStr.equalsIgnoreCase("false")) setValue(INTEGER_FALSE);
  else setValue(Integer.valueOf(pStr.trim()));
      }
    }
    public String getJavaInitializationString()
    {
      return String.valueOf(this.getValue());
    }
    public String getAsText() {
      if (this.getValue() instanceof Number)
  return this.getValue().toString();
      else
  return "";
    }
  }

  /**
   * This class implements a property editor which does normal double
   * parsing, but also check for 'true' treating it as 1.0 and 'false" as 0.0.
   *
   * This is necessary because some SQL databases (oracle) don't type numbers
   * very strictly so they are used for booleans.
   */
  public static class FlexibleDoublePropertyEditor extends PropertyEditorSupport {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SimpleSQLFormHandler.java#3 $$Change: 658351 $";

    public FlexibleDoublePropertyEditor() {}
    static Double DOUBLE_TRUE = new Double(1.0);
    static Double DOUBLE_FALSE = new Double(0.0);
    public void setAsText(String pStr) throws IllegalArgumentException {
      /*
       * Return an empty string here so that we can convert it to a "null"
       * value in the SQL conversion.  The droplets system won't deliver
       * the value if we return null here.
       */
      if (pStr == null || pStr.trim().length() == 0) setValue(new String(""));
      else {
  if (pStr.equalsIgnoreCase("true")) setValue(DOUBLE_TRUE);
  else if (pStr.equalsIgnoreCase("false")) setValue(DOUBLE_FALSE);
  else setValue(Double.valueOf(pStr.trim()));
      }
    }
    public String getJavaInitializationString()
    {
      return String.valueOf(this.getValue());
    }
    public String getAsText() {
      if (this.getValue() instanceof Number)
  return this.getValue().toString();
      else
  return "";
    }
  }
}

/*
 * This class stores "mapped" property values to implement our
 * value property.
 * <p>
 * This can't be an inner class because we end up trying to
 * serialize the property value which can't be serialized because
 * it requires serializing the "this" value
 */
class SQLFormHashtable extends Hashtable {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SimpleSQLFormHandler.java#3 $$Change: 658351 $";

  static final long serialVersionUID = 8805513557987144739L;

  transient SimpleSQLFormHandler mFormHandler;

  SQLFormHashtable(SimpleSQLFormHandler pFormHandler) {
    mFormHandler = pFormHandler;
  }
  String [] getNames() {
    if (mFormHandler == null) return null;
    return mFormHandler.getAllColumns();
  }
  TableDescriptor getTableDescriptor() {
    if (mFormHandler == null) return null;
    return mFormHandler.getTableDescriptor(mFormHandler.getTableName());
  }
  public Object getProperty(String pName) {
    if (mFormHandler.getInternalState() == SimpleSQLFormHandler.STATE_PENDING)
      mFormHandler.doLookup();
    return get(pName);
  }

  public void setProperty(String pName, Object pValue) {
    if (pValue == null)
      super.remove(pName);
    else
      super.put(pName, pValue);
    /*
     * If a user changes some property values of the component, and
     * auto-lookup is turned on, we mark the state of the form handler
     * as pending.  We'll do a lookup before an attempt to get values
     * unless the user does an explicit operation in the meantime.
     */
    if (mFormHandler.mAutoLookup)
      mFormHandler.setState(SimpleSQLFormHandler.STATE_PENDING);
  }
}

class SQLFormHashtablePropertyMapper implements DynamicPropertyMapper {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SimpleSQLFormHandler.java#3 $$Change: 658351 $";

  /**
   * Gets the value of the dynamic property from the specified object.
   */
  public Object getPropertyValue(Object pBean, String pPropertyName)
       throws PropertyNotFoundException
  {
    pPropertyName = StringUtils.toUpperCase(pPropertyName);
    return ((SQLFormHashtable)pBean).getProperty(pPropertyName);
  }

  /**
   * Sets the value of the dynamic property from the specified object.
   */
  public void setPropertyValue(Object pBean, String pPropertyName, Object pValue)
  {
    pPropertyName = StringUtils.toUpperCase(pPropertyName);
    ((SQLFormHashtable)pBean).setProperty(pPropertyName, pValue);
  }

  //-------------------------------------
  /**
   * Gets a DynamicBeanInfo that describes the given dynamic bean.
   *
   * @return the DynamicBeanInfo describing the bean.
   * @throws IntrospectionException if no information is available.
   */
  public DynamicBeanInfo getBeanInfo(Object pBean)
       throws IntrospectionException
  {
     SQLFormHashtable formHash = (SQLFormHashtable) pBean;

     String [] columns = formHash.getNames();
     TableDescriptor tableDescriptor = formHash.getTableDescriptor();
     return new SQLFormDynamicBeanInfo(columns, tableDescriptor);
  }
}

class SQLFormDynamicBeanInfo extends SimpleDynamicBeanInfo {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SimpleSQLFormHandler.java#3 $$Change: 658351 $";

  static final long serialVersionUID = -7728632617586864076L;

  String [] mColumns;
  TableDescriptor mDescriptor;
  SQLFormDynamicBeanInfo(String [] pColumns, TableDescriptor pDescriptor) {
    mColumns = pColumns;
    mDescriptor = pDescriptor;
  }

  public String [] getPropertyNames() {
    return mColumns;
  }

  public DynamicPropertyDescriptor getPropertyDescriptor(String pName) {
    DynamicPropertyDescriptor d = new DynamicPropertyDescriptor(pName);
    Integer sqlType = null;
    if (mDescriptor != null) {
      sqlType = (Integer) mDescriptor.columnTypes.get(StringUtils.toUpperCase(pName));
    }
    if (sqlType == null) {
      d.setPropertyType(String.class);
    }
    else {
      d.setPropertyType(getClassFromSQLType(sqlType.intValue()));
      if (sqlType.intValue() == Types.INTEGER ||
          sqlType.intValue() == Types.TINYINT ||
          sqlType.intValue() == Types.SMALLINT ||
    sqlType.intValue() == Types.BIGINT) {
        d.setPropertyEditorClass(SimpleSQLFormHandler.FlexibleIntegerPropertyEditor.class);
      }
      if (sqlType.intValue() == Types.DECIMAL) {
        d.setPropertyEditorClass(SimpleSQLFormHandler.FlexibleDoublePropertyEditor.class);
      }
    }
    return d;
  }

  /*
   * This implements a simple mapping from SQL types to Java types that
   * these SQL types are naturally represented as.  Mainly, this allows
   * the UI to determine how to treat SimpleSQLFormHandler.value. properties
   * in the editor.  It can, for example, use a checkbox for a BIT type.
   */
  static IntHashtable sSQLTypeToClass = new IntHashtable();
  {
    sSQLTypeToClass.put(Types.BIT, Boolean.class);
    sSQLTypeToClass.put(Types.TINYINT, Integer.class);
    sSQLTypeToClass.put(Types.SMALLINT, Integer.class);
    sSQLTypeToClass.put(Types.INTEGER, Integer.class);
    sSQLTypeToClass.put(Types.BIGINT, Integer.class);

    sSQLTypeToClass.put(Types.FLOAT, Float.class);
    sSQLTypeToClass.put(Types.REAL, Double.class);
    sSQLTypeToClass.put(Types.DOUBLE, Double.class);
    sSQLTypeToClass.put(Types.NUMERIC, Double.class);

    /* Need to support doubles here even though decimal can be an integer too */
    sSQLTypeToClass.put(Types.DECIMAL, Double.class);

    sSQLTypeToClass.put(Types.CHAR, String.class);
    sSQLTypeToClass.put(Types.VARCHAR, String.class);
    sSQLTypeToClass.put(Types.LONGVARCHAR, String.class);
  }
  static Class getClassFromSQLType(int pSQLType) {
    Class c = (Class) sSQLTypeToClass.get(pSQLType);
    if (c == null) return String.class;
    return c;
  }

  public DynamicPropertyDescriptor [] getPropertyDescriptors() {
    DynamicPropertyDescriptor [] darray = new DynamicPropertyDescriptor[mColumns.length];

    for (int i = 0; i < darray.length; i++) {
      darray[i] = getPropertyDescriptor(mColumns[i]);
    }
    return darray;
  }
}


class TableDescriptor implements Serializable {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SimpleSQLFormHandler.java#3 $$Change: 658351 $";

  static final long serialVersionUID = -5355036151563225640L;

  String [] columnNames; /** Names of each column */
  Dictionary columnTypes; /** java.sql.Types values for each column */
  Dictionary columnRequired; /** boolean for columns that are required */
  TableDescriptor(String [] names, Dictionary types, Dictionary required) {
    columnNames = names;
    columnTypes = types;
    columnRequired = required;
  }

}

