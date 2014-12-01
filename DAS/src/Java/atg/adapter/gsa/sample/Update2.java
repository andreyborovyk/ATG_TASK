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

package atg.adapter.gsa.sample;

import atg.repository.*;

import javax.transaction.*;
import atg.dtm.*;
import atg.adapter.gsa.GSARepository;

/**
 * Demonstrate how the version property of an item changes automatically
 * when the item is changed.
 *
 * <table border="1" cellpadding="3" cellspacing="0" width="100%">
 *  <tr bgcolor="#ccccff">
 *   <td colspan="2"><font size="+2"><b>GSA Code Sample</b></font>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Product</b></td><td>DAS</td></tr>
 *
 *  <tr><td width="20%"><b>Description</b></td> <td>Demonstrate how the
 * version property of an item changes automatically when the item is
 * changed. This example performs two updates of the same repository
 * item. After the first update, the program sleeps for 10 seconds (can
 * override on command line) before performing the second update. During this
 * period if you change the value in the version property column for the item
 * in the database, you can see how the GSA's optimistic locking system
 * detects the change. </td></tr>
 *
 *  <tr><td width="20%"><b>Author</b></td><td>mgk</td></tr>
 *  <tr><td width="20%"><b>Creation Date</b></td><td>2/29/0</td></tr>
 *
 *  <tr>
 *   <td width="20%"><b>Usage</b></td>
 *   <td>java atg.adapter.gsa.sample.Update2.java [id]</td>
 *  </tr>
 *
 *  <tr>
 *    <td width="20%"><b>Requirements</b></td>
 *    <td>must be run from <code>DYNAMO_HOME</code> with ATG
 *        <ccode>CLASSPATH</code>, also requires
 *        <code>gsa-template.xml</code> to be in the current directory
 *    </td>
 *  </tr>
 *
 *  <tr>
 *   <td width="20%"><b>Keywords</b></td><td>GSA, query, repository</td>
 *  </tr>
 * </table>
 *
 * @author mgk
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/Update2.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class Update2
extends Harness
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/Update2.java#2 $$Change: 651448 $";

  /** delay in seconds between update attempts */
  static int sDelay = 10;

  //-------------------------------------
  /**
   * Run our sample.
   * @param pRepository repository to use
   * @excetion RepositoryException if there is repository trouble
   **/
  public void go(Repository pRepository)
    throws RepositoryException
  {
    // print header
    pln("### Running Sample ###");
    pln(CLASS_VERSION);
    pln("");

    // we need to use a mtuable repository
    MutableRepository mr = (MutableRepository)pRepository;

    // this is a lot like the update example. Here we will actually update
    // the item twice, with a delay in between. At each point we print the
    // version property. The version property is an optional property of an
    // item descriptor that is used to implement distributed optimistic
    // locking. The GSA internally checks the version property (if enabled)
    // when it performs updates. This allows the GSA to detect the situation
    // wherein another Dynamo (or DB script) changed a DB item that is
    // cached.

    // the only tricky part here is that we have to put each update in its
    // own transaction for this to work. For more information on JTA
    // transactions, see the appropriate J2EE and Dynamo documentation.

    // get ready for taking control of the transactions
    TransactionDemarcation td = new TransactionDemarcation();
    TransactionManager mgr =
      ((GSARepository)pRepository).getTransactionManager();
    MutableRepositoryItem item;

    try
      {
        try
          {
            // start a new transaction
            td.begin(mgr, TransactionDemarcation.REQUIRES_NEW);
        
            // create an item and add it for testing our update
            pln("creating a new user in prepration for updating...");
            item = mr.createItem("user");

            item = (MutableRepositoryItem)mr.addItem(item);
            pln("added user, id=" + item.getRepositoryId());
            pln("version: " + item.getPropertyValue("version"));

            // do first update
            item.setPropertyValue("password", "pass1");
            mr.updateItem(item);
            pln("updated, version = " + item.getPropertyValue("version"));
          }
        finally
          {
            // commit the transaction
            td.end();
          }

        // sleep
        pln("sleeping for " + sDelay + " seconds before second update...");
        try {Thread.sleep(sDelay * 1000);} catch (InterruptedException e) {}
        pln("performing second update");

        try
          {
            // start second transaction
            td.begin(mgr, TransactionDemarcation.REQUIRES_NEW);
        
            item.setPropertyValue("password", "pass2");
            mr.updateItem(item);
            pln("updated, version = " + item.getPropertyValue("version"));
          }
        finally
          {
            // commit the transaction
            td.end();
          }
      }
    catch (TransactionDemarcationException tde)
      {
        pln("Transaction ERROR:");
        tde.printStackTrace();
      }
  }

  //-------------------------------------
  /**
   * Main routine. This example uses one optional argument.
   **/
  public static void main(String[] pArgs)
    throws Exception
  {
    String[] args = pArgs;

    // pull argument if specified
    if (args.length > 0 && !args[0].startsWith("-"))
      args = saveArgs(args, 1);

    String delay = getArg(0);
    if (delay != null) sDelay = Integer.parseInt(delay);

    runParser(Update2.class.getName(), args);
  }

} // end of class Update2.java
