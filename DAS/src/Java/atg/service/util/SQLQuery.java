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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.service.util;

import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Date;
import java.util.Vector;
import java.sql.*;
import javax.sql.*;
import javax.transaction.*;

import java.beans.IntrospectionException;

import atg.core.util.IntHashtable;
import atg.core.util.IntVector;
import atg.core.util.StringUtils;
import atg.beans.*;
import atg.nucleus.GenericService;

/**
 * This class is a Java bean that implements a SQL query.  It is designed
 * so that a single instance of this component can be used simultaneously
 * for different SQL queries.  In other words, this query component can
 * be defined as "globally scoped" and used simultaneously by different
 * requests.
 * <p>
 * It takes the following properties:
 * <dl>
 * <dt>dataSource
 * <dd>The dataSource used to acquire a database connection.  The dataSource
 * and the connectionURL are two alternative methods used for acquiring a
 * connection to the database.  If the dataSource is non-null, the dataSource
 * method will be used.  In this case, a transactionManager must also be
 * configured.
 * <dt>transactionManager
 * <dd>The transactionManager used in the event that a dataSource is configured.
 * <dt>connectionURL
 * <dd>The URL of the JDBC connection to use.  Typically this is a reference
 * to a connection in a connectionPool like: jdbc:atgpool:ConnectionPool.
 * <dt>querySQL
 * <dd>The SQL statement to execute.  This statement should return a
 * ResultSet.  In includes a set of parameterized variables where each
 * variable is preceeded by :&lt;varname&gt;.  For example:
 * <pre>
 *   select * from PERSON where name = :myName
 * </pre>
 * Values for the variables in the query, in this case 'myName' are supplied
 * through the Dictionary supplied to the executeQuery method.
 * <dt>bufferSize
 * <dd>This indicates the number of rows to read at one time.  As soon as
 * the last row is read in, the statement and connection are closed.
 * </dl>
 *
 * The query is executed by calling the executeQuery method which returns
 * a SQLQueryResults.  This object implements the Enumeration interface
 * which you can use to iterate through the elements returned by the query.
 * When you are finished with this object, you should call the "close()" method
 * which closes the statement and connection used to execute the query.
 * This is typically done in a finally clause so that you close the connection
 * even if an Exception occurs during the processing.
 * <p>
 * The connection and statement are automatically closed when you read the
 * last element returned by the query, but it does not hurt to close the
 * SQLQueryResults explicitly.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/util/SQLQuery.java#3 $$Change: 658351 $
 * @updated $DateTime: 2011/07/19 16:08:54 $$Author: tterhune $
 * @beaninfo
 *   description: SQL query service
 *   attribute: functionalComponentCategory SQL Queries
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/sqlquerycomp.gif
 */
public class SQLQuery extends GenericService {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/service/util/SQLQuery.java#3 $$Change: 658351 $";

  public SQLQuery () {
  }

  //--------- Property: QuerySQL -----------
  String mQuerySQL;
  /**
   * Sets the property QuerySQL.  This specifies the SQL that defines the
   * query to be performed.  The SQL contains variables
   * of the form :&lt;varname&gt;  The values of these variables are supplied
   * in the Dictionary queryArguments property.
   * @beaninfo
   *   description: The SQL query to be performed
   */
  public void setQuerySQL(String pQuerySQL) {
    mQuerySQL = pQuerySQL;
    invalidateQueryInfo();
  }
  /**
   * @return The value of the property QuerySQL.
   */
  public String getQuerySQL() {
    return mQuerySQL;
  }

  //--------- Property: ConnectionURL -----------
  String mConnectionURL;
  /**
   * Sets the property ConnectionURL.  This is the URL of the JDBC connection
   * to process the query on.
   * @beaninfo
   *   description: The URL used to connect to the database
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

  //--------- Property: BufferSize -----------
  int mBufferSize = 10;
  /**
   * Sets the property BufferSize.  The value must be greater than 0.  This
   * indicates the number of values to read in at one time.
   * @beaninfo
   *   description: The number of values to be read in at one time
   */
  public void setBufferSize(int pBufferSize) {
    if (pBufferSize <= 0) pBufferSize = 1;
    mBufferSize = pBufferSize;
  }
  /**
   * @return The value of the property BufferSize.
   */
  public int getBufferSize() {
    return mBufferSize;
  }

  //--------- Property: DataSource -----------
  DataSource mDataSource = null;

  /**
   * Sets the DataSource
   * @beaninfo
   *   description: sets the DataSource to use for db connections
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
   * that DataSource is set.
   */
  public void setTransactionManager
    (TransactionManager pTransactionManager)
  { mTransactionManager = pTransactionManager; }

  /**
   * @return The value of the property TransactionManager.
   */
  public TransactionManager getTransactionManager ()
  { return mTransactionManager; }

  /**
   * Executes a query on the given connection with the specified arguments.
   * The dictionary supplied in this call is merged with the dictionary
   * found in the queryArguments property.  Values in the argument to this
   * method override those in the queryArguments property.
   *
   * @return an Enumeration containing the results placed into a convenient
   * dictionary form.  You must process the Enumeration before closing the
   * connection.
   *
   * @beaninfo
   *   description: Executes a query with additional arguments
   */
  public SQLQueryResults executeQuery(Dictionary pExtraArguments)
   	throws SQLException {
    Object [] argValues = getArgumentValues(pExtraArguments);
    int [] argTypes = getArgumentTypes();

    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
      conn = getConnection();
      stmt = conn.prepareStatement(getProcessedQuerySQL());

      String dbgArgs = "";
      for (int i = 0; i < argValues.length; i++) {
        stmt.setObject(i+1, convertToSQLType(argValues[i], argTypes[i]));
	if (isLoggingDebug()) {
	  if (i != 0) dbgArgs += ", ";
	  dbgArgs += argValues[i];
	}
      }

      if (isLoggingDebug())
        logDebug("executing query: '" + getProcessedQuerySQL() + "' arguments=(" + dbgArgs + ")");

      rs = stmt.executeQuery();

      boolean useDataSource = (mDataSource == null) ? false : true;
      SQLQueryResults queryResults =
         new SQLQueryResults(useDataSource, conn, stmt, rs, this, getBufferSize());

      /*
       * Now that we're about to return the queryResults, this will get
       * closed when the queryResults object itself is closed.
       */
      conn = null;
      stmt = null;
      rs = null;

      return queryResults;
    }
    catch (SQLException exc) {
      if (isLoggingError())
        logError("SQLException occurred executing query: " + getQuerySQL() + ": " + exc);
      throw exc;
    }
    finally {
      try {
	if (rs != null) rs.close();
	if (stmt != null) stmt.close();
	if (conn != null) rollback( conn );
      }
      catch (SQLException exc2) {}
      if (conn != null) conn.close();
    }
  }

  /**
   * Returns the ordered list of argument values for this query
   * <p>
   * You must be careful to check for null elements in the array in
   * case the user did not supply a value for a particular argument.
   */
  Object [] getArgumentValues(Dictionary pDict) {
    String [] argNames = getArgumentList();
    Object [] argValues = new Object[argNames.length];
    //unused Vector v = null;
    for (int i = 0; i < argNames.length; i++) {
      if (pDict != null) argValues[i] = pDict.get(argNames[i]);
    }
    return argValues;
  }

  /**
   * The list of argument names found in the query.
   */
  String [] mArgumentList;
  public String [] getArgumentList() {
    if (mArgumentList == null) {
      validateQueryInfo();
    }
    return mArgumentList;
  }

  /**
   * The list of SQL types for each argument.  This array should be the
   * same size as the ArgumentList array.
   */
  int [] mArgumentTypes;
  public int [] getArgumentTypes() {
    if (mArgumentTypes == null) {
      validateQueryInfo();
    }
    return mArgumentTypes;
  }

  /**
   * The SQL statement that has had variable names replaced with ?
   * for JDBC.
   * @beaninfo
   *   description: The SQL statement that has variable names replaced with ?'s
   */
  String mProcessedQuerySQL;
  public String getProcessedQuerySQL() {
    if (mProcessedQuerySQL == null)
      validateQueryInfo();
    return mProcessedQuerySQL;
  }

  /**
   * Invalidate and validate the argumentList, and processedQuerySQL
   * statements.
   */
  void invalidateQueryInfo() {
    mArgumentList = null;
    mProcessedQuerySQL = null;
    mArgumentTypes = null;
  }
  void validateQueryInfo() {
    String sql = getQuerySQL();
    if (sql != null) {
      Vector args = new Vector();
      IntVector types = new IntVector();
      int lastIx = 0;
      boolean inQuotes = false;
      StringBuffer processedSQL = new StringBuffer();
      for (int i = 0; i < sql.length(); i++) {
        char c = sql.charAt(i);
        if (c == '\'') inQuotes = !inQuotes;
        else if (!inQuotes && sql.charAt(i) == ':') {
	  int endIx = skipTo(" \t\n\r", sql, i);
	  if (endIx > i) {
	    String paramName = sql.substring(i+1, endIx);
	    String typeName = null;
	    int type;
	    int tsix, teix;

	    if ((tsix = paramName.indexOf('(')) != -1 &&
	         (teix = paramName.indexOf(')', tsix)) != -1) {
	      typeName = paramName.substring(tsix+1, teix);
	      paramName = paramName.substring(0, tsix);
	    }

	    args.addElement(paramName);
	    if (typeName == null) type = Types.NULL;
	    else type = convertNameToSQLType(typeName);
	    types.addElement(type);
	  }
	  processedSQL.append(sql.substring(lastIx, i));
	  processedSQL.append('?');
	  lastIx = endIx;
	  i = endIx;
	}
      }
      if (lastIx < sql.length())
        processedSQL.append(sql.substring(lastIx));
      mProcessedQuerySQL = processedSQL.toString();
      mArgumentList = new String[args.size()];
      args.copyInto(mArgumentList);
      mArgumentTypes = new int[args.size()];
      types.copyInto(mArgumentTypes);
    }
  }

  /**
   * Returns the index of the first character in a String that is in the
   * list of specified characters.  If the end of the string is reached,
   * first, the index returned is equal to the length of the string.
   */
  int skipTo(String pChars, String pString, int pStartIndex) {
    /**
     * Keep on going until we hit either the end of the string or until
     * we find one of the characters in the list.  We also stop if we
     * see a ) without seeing a corresponding (.  This is because ) can
     * be a SQL separator character, but is also part of the parameter
     * name thing if we have that.
     */
    boolean inParamType = false;
    while (pStartIndex < pString.length() &&
           pChars.indexOf(pString.charAt(pStartIndex)) == -1) {
      char c = pString.charAt(pStartIndex);
      if (c == '(')
        inParamType = true;
      else if (c == ')') {
        if (inParamType) inParamType = false;
        else return pStartIndex;
      }
      pStartIndex++;
    }
    return pStartIndex;
  }

  static Hashtable sNameToSQLTypeMap = new Hashtable();
  static {
    sNameToSQLTypeMap.put("BIT", new Integer(Types.BIT));
    sNameToSQLTypeMap.put("TINYINT", new Integer(Types.TINYINT));
    sNameToSQLTypeMap.put("SMALLINT", new Integer(Types.SMALLINT));
    sNameToSQLTypeMap.put("INTEGER", new Integer(Types.INTEGER));
    sNameToSQLTypeMap.put("BIGINT", new Integer(Types.BIGINT));
    sNameToSQLTypeMap.put("FLOAT", new Integer(Types.FLOAT));
    sNameToSQLTypeMap.put("REAL", new Integer(Types.REAL));
    sNameToSQLTypeMap.put("DOUBLE", new Integer(Types.DOUBLE));
    sNameToSQLTypeMap.put("NUMERIC", new Integer(Types.NUMERIC));
    sNameToSQLTypeMap.put("DECIMAL", new Integer(Types.DECIMAL));
    sNameToSQLTypeMap.put("CHAR", new Integer(Types.CHAR));
    sNameToSQLTypeMap.put("VARCHAR", new Integer(Types.VARCHAR));
    sNameToSQLTypeMap.put("LONGVARCHAR", new Integer(Types.LONGVARCHAR));
    sNameToSQLTypeMap.put("DATE", new Integer(Types.DATE));
    sNameToSQLTypeMap.put("TIME", new Integer(Types.TIME));
    sNameToSQLTypeMap.put("TIMESTAMP", new Integer(Types.TIMESTAMP));
    sNameToSQLTypeMap.put("BINARY", new Integer(Types.BINARY));
    sNameToSQLTypeMap.put("VARBINARY", new Integer(Types.VARBINARY));
    sNameToSQLTypeMap.put("LONGVARBINARY", new Integer(Types.LONGVARBINARY));
    sNameToSQLTypeMap.put("NULL", new Integer(Types.NULL));
  }

  /**
   * Converts a String representing a SQL type name to a SQLType.
   * (e.g. "INTEGER" to java.sql.Types.INTEGER).
   */
  public static int convertNameToSQLType(String pTypeName) {
    Integer v = (Integer) sNameToSQLTypeMap.get(StringUtils.toUpperCase(pTypeName));
    if (v == null) return Types.NULL;
    else return v.intValue();
  }

  /** Table of tables indexed by the from type */
  static Dictionary sToSQLTypeMap = new Hashtable();

  /**
   * Given a SQLtype, convert the object passed in to a type suitable to
   * be put into a setObject call for a JDBC call requiring this type.
   * If no specific conversion technique has been registered for the type
   * of conversion, the original object is returned.
   */
  public static Object convertToSQLType(Object pInst, int pSQLType) {
    if (pInst == null) return null;
    IntHashtable converterMap = (IntHashtable) sToSQLTypeMap.get(pInst.getClass());
    if (converterMap != null) {
      SQLTypeConverter cvt = (SQLTypeConverter) converterMap.get(pSQLType);
      if (cvt != null) {
        return cvt.convert(pInst);
      }
    }
    return pInst;
  }

  static synchronized void registerToSQLConverter(Class pFromClass,
  						  int pToSQLType,
						  SQLTypeConverter pConverter) {
    IntHashtable map;
    if ((map = (IntHashtable) sToSQLTypeMap.get(pFromClass)) == null) {
      map = new IntHashtable();
      sToSQLTypeMap.put(pFromClass, map);
    }
    map.put(pToSQLType, pConverter);
  }

  static final Integer INTEGER_ZERO = new Integer(0);
  static final Integer INTEGER_ONE = new Integer(1);

  static {
    SQLTypeConverter cvt;
    /**
     * This is the String to Boolean converter
     */
    registerToSQLConverter(String.class, java.sql.Types.BIT,
	 new SQLTypeConverter() {
	   public Object convert(Object pObject) {
	     String s = pObject.toString().trim().toLowerCase();
	     if (s.equals("true")) return Boolean.TRUE;
	     else if (s.equals("false")) return Boolean.FALSE;
	     throw new IllegalArgumentException("can't convert: " + s + " to a Boolean");
	   }
	 });

    /**
     * This is the Integer to Boolean converter
     */
    registerToSQLConverter(Integer.class, java.sql.Types.BIT,
	 new SQLTypeConverter() {
	   public Object convert(Object pObject) {
	     if (pObject instanceof Integer) {
	       Integer s = (Integer) pObject;
	       if (s.intValue() != 0) return Boolean.TRUE;
	       else return Boolean.FALSE;
	     }
	     throw new IllegalArgumentException("can't convert: " + pObject + " to a Boolean");
	   }
	 });

    /**
     * This is the Boolean to INTEGER converter.  We use it for a few of
     * the number based types.
     */
    cvt = new SQLBooleanToIntegerConverter();
    registerToSQLConverter(Boolean.class, java.sql.Types.INTEGER, cvt);
    registerToSQLConverter(Boolean.class, java.sql.Types.DECIMAL, cvt);
    registerToSQLConverter(Boolean.class, java.sql.Types.NUMERIC, cvt);

    /**
     * This is the String to Integer converter
     */
    cvt = new SQLStringToIntegerConverter();
    registerToSQLConverter(String.class, java.sql.Types.INTEGER, cvt);
    registerToSQLConverter(String.class, java.sql.Types.DECIMAL, cvt);
    registerToSQLConverter(String.class, java.sql.Types.NUMERIC, cvt);

    /**
     * This is the String to Double converter
     */
    cvt = new SQLStringToDoubleConverter();
    registerToSQLConverter(String.class, java.sql.Types.FLOAT, cvt);
    registerToSQLConverter(String.class, java.sql.Types.REAL, cvt);
    registerToSQLConverter(String.class, java.sql.Types.DOUBLE, cvt);

    /**
     * This is the Date to Timestamp converter
     */
    cvt = new SQLTimestampConverter();
    registerToSQLConverter(Date.class, java.sql.Types.TIMESTAMP, cvt);
    registerToSQLConverter(Date.class, 11, cvt); /* this is for oracle */
    /**
     * This is the Date to java.sql.Date converter
     */
    registerToSQLConverter(Date.class, java.sql.Types.DATE,
	 new SQLTypeConverter() {
	   public Object convert(Object pObject) {
	     if (pObject == null) return null;
	     if (pObject instanceof Date) {
	       Date d = (Date) pObject;
	       return new java.sql.Date(d.getTime());
	     }
	     throw new IllegalArgumentException("can't convert: " + pObject + " to a java.sql.Date");
	   }
	 });
    /**
     * This is the Date to java.sql.Time converter
     */
    registerToSQLConverter(Date.class, java.sql.Types.TIME,
	 new SQLTypeConverter() {
	   public Object convert(Object pObject) {
	     if (pObject == null) return null;
	     if (pObject instanceof Date) {
	       Date d = (Date) pObject;
	       return new java.sql.Time(d.getTime());
	     }
	     throw new IllegalArgumentException("can't convert: " + pObject + " to a java.sql.Time");
	   }
	 });
  }

  /**
   * Performs a commit action dependent upon whether the connection
   * was received from a DataSource or DriverManager.
   */
  void commit( Connection pConnection ) {
    if ( mDataSource != null )  {
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
    if ( mDataSource != null )    {
        if ( mTransactionManager != null )  {
            try {
                if (mTransactionManager.getStatus() != Status.STATUS_NO_TRANSACTION)
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
   * dependent upon the property DataSource.  If DataSource is
   * non-null a connection is acquired from the DataSource.  If false,
   * the connection is acquired from the DriverManager.
   */
  Connection getConnection()
  throws SQLException
  {
    Connection conn = null;

    // return connection dependent upon DataSource property
    if ( mDataSource != null )  {
        conn = mDataSource.getConnection();
    } else    {
        conn = DriverManager.getConnection(getConnectionURL());
    }

    return conn;
  }

}

interface SQLTypeConverter {
  Object convert(Object pType);
}

class SQLBooleanToIntegerConverter implements SQLTypeConverter {
  static final Integer INTEGER_ZERO = new Integer(0);
  static final Integer INTEGER_ONE = new Integer(1);
  public Object convert(Object pObject) {
    if (pObject == null) return null;
    if (pObject instanceof Boolean) {
      Boolean b = (Boolean) pObject;
      if (b.booleanValue()) return INTEGER_ZERO;
      else return INTEGER_ONE;
    }
    throw new IllegalArgumentException("can't convert: " + pObject + " to an integer");
  }
}

class SQLStringToDoubleConverter implements SQLTypeConverter {
  public Object convert(Object pObject) {
     if (pObject == null) return null;
     String s = pObject.toString().trim();
     if (s.length() == 0) return null;
     return new Double(s);
  }
}

class SQLStringToIntegerConverter implements SQLTypeConverter {
  static final Integer INTEGER_ZERO = new Integer(0);
  static final Integer INTEGER_ONE = new Integer(1);
  public Object convert(Object pObject) {
     if (pObject == null) return null;
     String s = pObject.toString().trim();
     if (s.length() == 0) return null;
     /*
      * Need to handle boolean to INTEGER conversion unfortunately.
      */
     if (s.equalsIgnoreCase("true")) return INTEGER_ONE;
     else if (s.equalsIgnoreCase("false")) return INTEGER_ZERO;
     return new Integer(s);
  }
}

class SQLTimestampConverter implements SQLTypeConverter {
  public Object convert(Object pObject) {
    if (pObject == null) return null;
    if (pObject instanceof Date) {
      Date d = (Date) pObject;
      return new Timestamp(d.getTime());
    }
    throw new IllegalArgumentException("can't convert: " + pObject + " to a timestamp");
  }
}
