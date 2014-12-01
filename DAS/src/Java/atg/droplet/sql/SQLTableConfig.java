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

import java.io.File;

/**
 * This class stores the information needed to manage a table's schema
 * automatically.  This includes the SQL used to define that table, perhaps
 * defined separately each database. 
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SQLTableConfig.java#3 $$Change: 655658 $
 * @updated $DateTime: 2011/06/30 14:17:14 $$Author: tterhune $
 */
public class SQLTableConfig {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/SQLTableConfig.java#3 $$Change: 655658 $";

  public SQLTableConfig () {
  }

  //--------- Property: TableName -----------
  String mTableName;
  /**
   * Sets the property TableName.
   */
  public void setTableName(String pTableName) {
    mTableName = pTableName;
  }
  /**
   * @return The value of the property TableName.
   */
  public String getTableName() {
    return mTableName;
  }

  //--------- Property: DefaultSQL -----------
  File mDefaultSQL;
  /**
   * Sets the property DefaultSQL.
   */
  public void setDefaultSQL(File pDefaultSQL) {
    mDefaultSQL = pDefaultSQL;
  }
  /**
   * @return The value of the property DefaultSQL.
   */
  public File getDefaultSQL() {
    return mDefaultSQL;
  }

  //--------- Property: OracleSQL -----------
  File mOracleSQL;
  /**
   * Sets the property OracleSQL.
   */
  public void setOracleSQL(File pOracleSQL) {
    mOracleSQL = pOracleSQL;
  }
  /**
   * @return The value of the property OracleSQL.
   */
  public File getOracleSQL() {
    return mOracleSQL;
  }

  //--------- Property: SybaseSQL -----------
  File mSybaseSQL;
  /**
   * Sets the property SybaseSQL.
   */
  public void setSybaseSQL(File pSybaseSQL) {
    mSybaseSQL = pSybaseSQL;
  }
  /**
   * @return The value of the property SybaseSQL.
   */
  public File getSybaseSQL() {
    return mSybaseSQL;
  }

  //--------- Property: MssqlSQL -----------
  File mMssqlSQL;
  /**
   * Sets the property MssqlSQL.
   */
  public void setMssqlSQL(File pMssqlSQL) {
    mMssqlSQL = pMssqlSQL;
  }
  /**
   * @return The value of the property MssqlSQL.
   */
  public File getMssqlSQL() {
    return mMssqlSQL;
  }

  //--------- Property: MysqlSQL -----------
  File mMysqlSQL;
  /**
   * Sets the property MysqlSQL.
   */
  public void setMysqlSQL(File pMysqlSQL) {
    mMysqlSQL = pMysqlSQL;
  }
  /**
   * @return The value of the property MysqlSQL.
   */
  public File getMysqlSQL() {
    return mMysqlSQL;
  }

  //--------- Property: InformixSQL -----------
  File mInformixSQL;
  /**
   * Sets the property InformixSQL.
   */
  public void setInformixSQL(File pInformixSQL) {
    mInformixSQL = pInformixSQL;
  }
  /**
   * @return The value of the property InformixSQL.
   */
  public File getInformixSQL() {
    return mInformixSQL;
  }

  //--------- Property: DB2SQL -----------
  File mDb2SQL;
  /**
   * Sets the property DB2SQL.
   */
  public void setDb2SQL(File pDb2SQL) {
    mDb2SQL = pDb2SQL;
  }
  /**
   * @return The value of the property DB2SQL.
   */
  public File getDb2SQL() {
    return mDb2SQL;
  }

  /**
   * Returns the SQL File for the specified database.
   */
  public File getSQL(String pDBName) {
    File f = null;
    if (pDBName.equalsIgnoreCase("oracle"))
      f = getOracleSQL();
    else if (pDBName.equalsIgnoreCase("sybase"))
      f = getSybaseSQL();
    else if (pDBName.equalsIgnoreCase("mssql"))
      f = getMssqlSQL();
    else if (pDBName.equalsIgnoreCase("mysql"))
      f = getMysqlSQL();
    else if (pDBName.equalsIgnoreCase("informix"))
      f = getInformixSQL();
    else if (pDBName.equalsIgnoreCase("db2"))
      f = getDb2SQL();
    if (f == null) f = getDefaultSQL();
    return f;
  }
}
