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
 * This example shows how to determine what item descriptors
 * are defined in a repository and alos demostrates how to execute a
 * query that counts items instead of returning them.
 *
 * <table border="1" cellpadding="3" cellspacing="0" width="100%">
 *  <tr bgcolor="#ccccff">
 *   <td colspan="2"><font size="+2"><b>GSA Code Sample</b></font>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Product</b></td><td>DAS</td></tr>
 *
 *  <tr><td width="20%"><b>Description</b></td>
 *   <td>This example shows how to determine what item descriptors
 *       are defined in a repository and also demonstrates how to execute a
 *       query that counts items instead of returning them.
 *   </td>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Author</b></td><td>mgk</td></tr>
 *  <tr><td width="20%"><b>Creation Date</b></td><td>3/6/0</td></tr>
 *
 *  <tr>
 *   <td width="20%"><b>Usage</b></td>
 *   <td>java atg.adapter.gsa.sample.ItemDescriptors</td>
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
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/ItemDescriptors.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class ItemDescriptors
extends Harness
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/ItemDescriptors.java#2 $$Change: 651448 $";

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

    /*
     * Sometimes you don't know what item descriptors are defined in a
     * repository. For example a GUI builder tool might need to discover the
     * item descriptors defined in a repository at runtime. An item
     * descriptor us in effect a dynamic type. This example demonstrates some
     * of the APIs available that make type information available at
     * runtime. This is as much a Dynamic Beans example as it is a repository
     * example--the two are closely related.
     **/

    // ask the repository for the names of all the item descriptors it knows
    // about
    pln("looking up descriptor names...");
    String[] names = pRepository.getItemDescriptorNames();
    for (int i=0; i<names.length; i++)
      pln("\t" + names[i]);

    // now let's execute a query for each descriptor. As discussed in Query1,
    // queries are defined in the context of an item descriptor.
    pln("executing a query for each descriptor...");
    for (int i=0; i<names.length; i++)
      {
        RepositoryItemDescriptor d;
        try
          {
            d = pRepository.getItemDescriptor(names[i]);
          }
        catch (RepositoryException re)
          {
            // this should not happen because we are using the names the
            // repository just told us are valid, but in general you should
            // catch repository exceptions and decide what to do about them
            // in your applications
            pln("weird... could not find descriptor: " + names[i]);
            re.printStackTrace();
            continue;
          }

        RepositoryView v = d.getRepositoryView();
        QueryBuilder b = v.getQueryBuilder();
        
        // this is a query that returns all the items within a given item
        // descriptor. Care should be used with this query as if used alone
        // it will scan the entire DB table when executed.
        Query all = b.createUnconstrainedQuery();

        // instead of running the query as-is we will get a count of how many
        // items would be returned by the query. For the GSA this becomes an
        // SQL query like: select count(*) from my_table, which is relatively
        // cheap, even for large tables. You can execute a count of any
        // Query object. For example in a UI you might execute a count query
        // to determine whether or not there are too many items for a UI to
        // display.
        p("   querying " + names[i] + "... ");
        int count = v.executeCountQuery(all);
        pln("found <" + count + "> items");
      }
   }

  //-------------------------------------
  /**
   * Main routine. This example uses no command line arguments
   **/
  public static void main(String[] pArgs)
    throws Exception
  {
    runParser(ItemDescriptors.class.getName(), pArgs);
  }

} // end of class ItemDescriptors
