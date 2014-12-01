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

/**
 * Create a new repository item for a user and demonstrate how to update it.
 *
 * <table border="1" cellpadding="3" cellspacing="0" width="100%">
 *  <tr bgcolor="#ccccff">
 *   <td colspan="2"><font size="+2"><b>GSA Code Sample</b></font>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Product</b></td><td>DAS</td></tr>
 *
 *  <tr><td width="20%"><b>Description</b></td>
 *   <td>Ceate a new repository item for a user and demonstrate
 *       how to update it
 *   </td>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Author</b></td><td>mgk</td></tr>
 *  <tr><td width="20%"><b>Creation Date</b></td><td>2/29/0</td></tr>
 *
 *  <tr>
 *   <td width="20%"><b>Usage</b></td>
 *   <td>java atg.adapter.gsa.sample.UpdateItem [id]</td>
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
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/UpdateItem.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class UpdateItem
extends Harness
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/UpdateItem.java#2 $$Change: 651448 $";

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

    // create an item and add it for testing our update. You might also try
    // modifying this example to update an item that already exists. Have a
    // look at the GetItem example.
    pln("creating a new user in prepration for updating...");
    MutableRepositoryItem item = mr.createItem("user");

    item = (MutableRepositoryItem)mr.addItem(item);
    pln("added user, id=" + item.getRepositoryId());

    // now set a property
    item.setPropertyValue("password", "toothbrush");

    // ... set more properties here if you like ...

    // this updates the item persistently in the data store. It is worth noting
    // here that you can actually omit this call and the item will still be
    // updated in the data store if it is a persistent item. This is due to
    // the way transactions are handled in D5. See the programmer's guide for
    // more info.
    mr.updateItem(item);

    pln("Updated user, id=" + item.getRepositoryId());
   }

  //-------------------------------------
  /**
   * Main routine. This example uses no command line arguments
   **/
  public static void main(String[] pArgs)
    throws Exception
  {
    runParser(UpdateItem.class.getName(), pArgs);
  }

} // end of class UpdateItem
