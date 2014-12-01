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

package atg.droplet.sql;

import java.sql.*;
import java.io.IOException;
import java.io.File;

import javax.servlet.http.*;
import javax.servlet.*;
import javax.sql.*;
import javax.transaction.*;

import atg.droplet.*;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.servlet.*;
import atg.core.io.FileUtils;

/**
 * A form handler/bean that provides functionality for creating, dropping, 
 * and providing information about the state of a set of tables.  Two alternate
 * methods of connecting to the database are used, dependent upon the property
 * UseDataSource.  If UseDataSource is set to true, the DataSource and
 * TransactionManager must be configured and will be used for acquiring 
 * database connections.  If UseDataSource is false or not explicitly set,
 * the ConnectionURL will be used for acquiring database connections.
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SQLTableManager.java#3 $$Change: 655658 $
 * @updated $DateTime: 2011/06/30 14:17:14 $$Author: tterhune $
 */
public class SQLTableManager extends GenericFormHandler {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SQLTableManager.java#3 $$Change: 655658 $";

  public SQLTableManager() {
  }

  //--------- Property: Tables -----------
  SQLTableConfig[] mTables;
  /**
   * Sets the property Tables.  This must be in an order such that any
   * table that references another table must be after it in the list.
   * Tables are created in the order that they are specified in the list,
   * and dropped in the reverse order.
   */
  public void setTables(SQLTableConfig[] pTables) {
    mTables = pTables;
  }
  /**
   * @return The value of the property Tables.
   */
  public SQLTableConfig[] getTables() {
    if (mTables == null)
      mTables = new SQLTableConfig[0];
    return mTables;
  }

  //--------- Property: ConnectionURL -----------
  String mConnectionURL;
  /**
   * Sets the property ConnectionURL.
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

  //--------- Property: DBErrorURL -----------
  String mDBErrorURL;
  /**
   * Sets the property DBErrorURL.
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
  public void setUseDataSource(boolean pUseDataSource) {
    mUseDataSource = pUseDataSource;
  }

  /**
   * @return The value of the property UseDataSource.
   */
  public boolean getUseDataSource() {
    return mUseDataSource;
  }

  //--------- Property: DataSource -----------
  DataSource mDataSource;

  /**
   * Sets the DataSource
   * @beaninfo
   *   description: sets the DataSource to use for db connections
   * if UseDataSource is set to true.
   */
  public void setDataSource(DataSource pDataSource) {
    mDataSource = pDataSource;
  }

  /**
   * @return The value of the property DataSource.
   */
  public DataSource getDataSource() {
    return mDataSource;
  }

  //--------- Property: TransactionManager -----------

  TransactionManager mTransactionManager;

  /**
   * Sets the TransactionManager
   * @beaninfo
   *   description: sets the TransactionManager in the event
   * that UseDataSource is set to true.
   */
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   * @return The value of the property TransactionManager.
   */
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  /**
   * Test if a table exists by trying to count the # of rows in the table.
   * This is not the most efficient way, but it is the most accurate.  The
   * dmd.getTables method will return all tables on the server unless you
   * specify the right catalog for some databases (sybase, oracle).  
   */
  boolean tableExists(Connection pConn, String pTableName) {
    Statement stmt = null;
    ResultSet rs = null;
    // unused boolean success=false;
    try {
      stmt = pConn.createStatement();

      rs = stmt.executeQuery("select count(*) from " + pTableName);
      if (!rs.next())
        return false;
    }
    catch (SQLException exc) {
      return false;
    }
    finally {
      try {
        if (rs != null)
          rs.close();
      }
      catch (SQLException exc) {
      }
      try {
        if (stmt != null)
          stmt.close();
      }
      catch (SQLException exc) {
      }
    }
    return true;
  }

  /** 
   * Returns true if the table exists.  Does not require a db connection 
   * as an argument.
   */
  boolean tableExists(String pTableName) {
    Connection conn = null;
    try {
      conn = getConnection();
      return tableExists(conn, pTableName);
    }
    catch (SQLException exc) {
      return false;
    }
    finally {
      if (mDataSource != null) {
        try {
          if (conn != null)
            conn.close();
        }
        catch (SQLException exc) {
        }
      }
      else {
        try {
          conn.commit();
        }
        catch (SQLException exc) {
        }
        try {
          if (conn != null)
            conn.close();
        }
        catch (SQLException exc) {
        }
      }
    }
  }

  /**
   * Go through the list of tables names that we were supplied with.
   * If we don't find one of them, we return false.
   */
   public boolean getTableStatus(boolean pAllTablesExist) {
    SQLTableConfig[] tables = getTables();
    for (int i = 0; i < tables.length; i++) {
      if (!tableExists(tables[i].getTableName())) {
        if (pAllTablesExist)
          return false;
      }
      else if (!pAllTablesExist)
        return false;
    }
    /* Either no tables exist or all tables exist if we get here */
    return true;
  }

  /**
   * Returns true if all specified tables exist
   */
  public boolean getAllTablesExist() {
    return getTableStatus(true);
  }

  /** 
   * Returns true if none of the specified tables exist
   */
  public boolean getNoTablesExist() {
    return getTableStatus(false);
  }

  /**
   * Drops all of the tables
   */
  public void dropTables() throws SQLException {
    Statement stmt = null;
    Connection conn = null;
    boolean success = false;
    try {
      conn = getConnection();
      stmt = conn.createStatement();

      // unused DatabaseMetaData dmd = conn.getMetaData();
      SQLTableConfig[] tables = getTables();
      for (int i = tables.length - 1; i >= 0; i--) {
        if (tableExists(conn, tables[i].getTableName())) {
          /*
           * Treat this as a warning.  I think that on some databases, 
           * we get more tables in the table list than we are operating
           * on in the current context.
           */
          try {
            stmt.executeUpdate("drop table " + tables[i].getTableName());
          }
          catch (SQLException exc) {
            if (isLoggingWarning())
              logWarning(
                "SQLException occurred dropping table: "
                  + tables[i].getTableName()
                  + ": "
                  + exc);
          }
        }
      }
      success = true;
    }
    catch (SQLException exc) {
      if (isLoggingError())
        logError("SQLException occurred dropping tables: " + exc);
      throw exc;
    }
    finally {
      if (success) {
        commit(conn);
      }
      else {
        rollback(conn);
      }
      try {
        if (stmt != null)
          stmt.close();
      }
      catch (SQLException exc) {
      }
      try {
        if (conn != null)
          conn.close();
      }
      catch (SQLException exc) {
      }
    }
  }

  /**
   * Returns the SQL that will be used to implement the dropTables
   * operation
   */
  public String getDropSQL() {
    SQLTableConfig[] tables = getTables();
    StringBuffer buf = new StringBuffer();
    String newLine = System.getProperty("line.separator");
    if (newLine == null)
      newLine = "\n";
    for (int i = 0; i < tables.length; i++) {
      buf.append("DROP TABLE ");
      buf.append(tables[i].getTableName());
      if (i != tables.length - 1) {
        buf.append(";");
        buf.append(newLine);
      }
    }
    return buf.toString();
  }

  /**
   * Returns the SQL that will be used to implement the create tables
   * operation
   */
  public String getCreateSQL() {
    Connection conn = null;
    try {
      conn = getConnection();
      return getCreateSQL(conn);
    }
    catch (SQLException exc) {
      return exc.toString();
    }
    finally {
      try {
        if (conn != null)
          conn.close();
      }
      catch (SQLException exc) {
      }
    }
  }

  /**
   * Need to use the connection passed in to get the database name we 
   * are on.
   */
  String getCreateSQL(Connection conn) {
    String currentTableName = null;
    try {
      String dbName = getDatabaseName(conn);
      SQLTableConfig[] tables = getTables();
      StringBuffer buf = new StringBuffer();
      String newLine = System.getProperty("line.separator");
      if (newLine == null)
        newLine = "\n";
      for (int i = 0; i < tables.length; i++) {
        currentTableName = tables[i].getTableName();
        buf.append(FileUtils.readFileString(tables[i].getSQL(dbName)));
        if (i != tables.length - 1) {
          /* 
           * I think that this is the only oddity here.  We may need
           * to do these each in a separate statement if this is not
           * portable.
           */
          if (dbName.equalsIgnoreCase("oracle")) {
            buf.append("/");
            buf.append(newLine);
          }
          buf.append(newLine);
        }
      }
      if (tables.length > 0 && dbName.equalsIgnoreCase("oracle")) {
        buf.append("/");
        buf.append(newLine);
      }
      return buf.toString();
    }
    catch (IOException exc) {
      if (isLoggingError())
        logError(
          "Can't read sql definition file for creating table: "
            + currentTableName
            + " : "
            + exc);
    }
    return null;
  }

  /**
   * Returns the canonical name for the database that we're using
   */
  public String getDatabaseName(Connection conn) {
    try {
      String dbName = conn.getMetaData().getDatabaseProductName();

      dbName = dbName.toLowerCase();
      if (dbName.indexOf("oracle") != -1)
        dbName = "oracle";
      else if (dbName.indexOf("sybase") != -1)
        dbName = "sybase";
      else if (dbName.indexOf("microsoft") != -1)
        dbName = "mssql";
      else if (dbName.indexOf("mysql") != -1)
        dbName = "mysql";
      else if (dbName.indexOf("informix") != -1)
        dbName = "informix";
      else if (dbName.indexOf("db2") != -1)
        dbName = "db2";

      if (isLoggingDebug())
        logDebug("Database name is: " + dbName);
      return dbName;
    }
    catch (SQLException exc) {
      if (isLoggingError())
        logError("SQLException occurred : " + exc);
    }
    return null;
  }

  /**
   * Creates the tables.
   */
  public void createTables() throws SQLException {
    Connection conn = null;
    int status;
    Statement stmt = null;
    boolean success = false;
    String newline = System.getProperty("line.separator");
    if (newline == null)
      newline = "\n";

    try {
      conn = getConnection();
      stmt = conn.createStatement();
      String dbName = getDatabaseName(conn);

      /*
       * The separator needs to get set whenever you have a jdbc driver
       * that cannot execute multiple statements in the sql.   Right now,
       * this only fails for oracle.
       *
       * We're very strict here about the separator to avoid having  
       * to parse the context.  Even so, there's probably ways we can
       * get confused.
       */
      String separator = null;
      if (dbName.equals("oracle")
        || dbName.equals("mysql")
        || dbName.equals("informix"))
        separator = newline + "/" + newline;

      SQLTableConfig[] tables = getTables();
      for (int i = 0; i < tables.length; i++) {
        String sql = FileUtils.readFileString(tables[i].getSQL(dbName));
        int ix;

        if (separator != null) {
          while ((ix = sql.indexOf(separator)) != -1) {
            String statementSql = sql.substring(0, ix);
            if (isLoggingDebug())
              logDebug("executing sql in createTables: '" + sql);
            status = stmt.executeUpdate(statementSql);
            if (isLoggingDebug())
              logDebug(
                "executed update in createTables, status code was: '" + status);

            sql = sql.substring(ix + separator.length());
          }
        }

        if (isLoggingDebug())
          logDebug("executing sql in createTables: '" + sql);
        status = stmt.executeUpdate(sql);
        if (isLoggingDebug())
          logDebug(
            "executed update in createTables, status code was: '" + status);

      }
      success = true;
    }
    catch (SQLException exc) {
      if (isLoggingError())
        logError("SQLException occurred : " + getCreateSQL() + ": " + exc);
      throw exc;
    }
    catch (IOException exc) {
      if (isLoggingError())
        logError("Can't open SQL file to create tables: " + exc);
    }
    finally {
      if (success) {
        commit(conn);
      }
      else {
        rollback(conn);
      }

      try {
        if (stmt != null)
          stmt.close();
      }
      catch (SQLException exc) {
      }
      try {
        if (conn != null)
          conn.close();
      }
      catch (SQLException exc) {
      }
    }
  }

  /**
   * Creates the tables specified by executing the SQL specified
   */
  public boolean handleCreateTables(
    DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    TransactionDemarcation td = null;
    try {
      if (mUseDataSource){
        if (mTransactionManager != null) {
          td = new TransactionDemarcation();  
          td.begin (mTransactionManager, TransactionDemarcation.REQUIRED);
        }
      }
     
      createTables();

    }
    catch (SQLException exc) {
      addFormException(
        new DropletException(
          "Database error occurred trying to create tables",
          exc,
          "dbError"));
      if (getDBErrorURL() != null)
        pResponse.sendLocalRedirect(getDBErrorURL(), pRequest);
    }
    catch (TransactionDemarcationException tde)
    {
      addFormException(
          new DropletException(
            "Transaction error occurred trying to create tables",
            tde,
            "dbError"));
        if (getDBErrorURL() != null)
          pResponse.sendLocalRedirect(getDBErrorURL(), pRequest);
    }   
    finally
    {
      try
        {
          if (td != null)
            td.end();
        }
      catch (TransactionDemarcationException tde)
        {
        addFormException(
            new DropletException(
              "Transaction error occurred trying to create tables",
              tde,
              "dbError"));
          if (getDBErrorURL() != null)
            pResponse.sendLocalRedirect(getDBErrorURL(), pRequest);
        }
    }
    return true;
  }

  /**
   * Drops each of the tables specified by the TableNames property 
   */
  public boolean handleDropTables(
    DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    TransactionDemarcation td = null;
    try {
      if (mUseDataSource){
        if (mTransactionManager != null) {
          td = new TransactionDemarcation();  
          td.begin (mTransactionManager, TransactionDemarcation.REQUIRED);
        }
      }
      
      dropTables();
 
    }
    catch (SQLException exc) {
      addFormException(
        new DropletException(
          "Database error occurred trying to drop the tables",
          exc,
          "dbError"));
      if (getDBErrorURL() != null)
        pResponse.sendLocalRedirect(getDBErrorURL(), pRequest);
    }
    catch (TransactionDemarcationException tde)
    {
      addFormException(
          new DropletException(
            "Transaction error occurred trying to drop the tables",
            tde,
            "dbError"));
        if (getDBErrorURL() != null)
          pResponse.sendLocalRedirect(getDBErrorURL(), pRequest);
    }   
    finally
    {
      try
        {
          if (td != null)
            td.end();
        }
      catch (TransactionDemarcationException tde)
        {
        addFormException(
            new DropletException(
              "Transaction error occurred trying to drop the tables",
              tde,
              "dbError"));
          if (getDBErrorURL() != null)
            pResponse.sendLocalRedirect(getDBErrorURL(), pRequest);
        }
    }
    return true;
  }

  /**
   * Performs a commit action dependent upon whether the connection
   * was received from a DataSource or DriverManager.
   */
  void commit(Connection pConnection) {
    if (mUseDataSource) {
      // do nothing
    }
    else {
      try {
        pConnection.commit();
      }
      catch (SQLException exc) {
      }
    }
  }

  /**
   * Performs a rollback action dependent upon whether the connection
   * was received from a DataSource or DriverManager.
   */
  void rollback(Connection pConnection) {
    if (mUseDataSource) {
      if (mTransactionManager != null) {
        try {
          mTransactionManager.setRollbackOnly();
        }
        catch (SystemException se) {
          if (isLoggingError()) {
            logError(se);
          }
        }
      }
    }
    else
      try {
        pConnection.rollback();
      }
      catch (SQLException se) {
      }
  }

  /*
   * Returns a database Connection from one of two possible sources
   * dependent upon the property UseDataSource.  If UseDataSource is
   * set to true a connection is acquired from the DataSource.  If false,
   * the connection is acquired from the DriverManager.
   */
  Connection getConnection() throws SQLException {
    Connection conn = null;

    // return connection dependent upon DataSource property
    if (mUseDataSource) {
      if (mDataSource != null)
        conn = mDataSource.getConnection();
    }
    else {
      conn = DriverManager.getConnection(getConnectionURL());
    }

    return conn;
  }
}
