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
 * Create a new repository item for the user.
 *
 * <table border="1" cellpadding="3" cellspacing="0" width="100%">
 *  <tr bgcolor="#ccccff">
 *   <td colspan="2"><font size="+2"><b>GSA Code Sample</b></font>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Product</b></td><td>DAS</td></tr>
 *
 *  <tr><td width="20%"><b>Description</b></td>
 *   <td>Create a new repository item for a user
 *   </td>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Author</b></td><td>mgk</td></tr>
 *  <tr><td width="20%"><b>Creation Date</b></td><td>2/29/0</td></tr>
 *
 *  <tr>
 *   <td width="20%"><b>Usage</b></td>
 *   <td>java atg.adapter.gsa.sample.CreateItem [id]</td>
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
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/CreateItem.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class CreateItem
extends Harness
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/CreateItem.java#2 $$Change: 651448 $";

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

    // creating an item changes the contents of the repository
    // so we need to use a mtuable repository
    MutableRepository mr = (MutableRepository)pRepository;

    // name of item descriptor describing the type of item we will
    // create, most all repository APIs use an item descriptor or
    // the name of an item descriptor
    String descriptorName = "user";

    // see if an id was specified
    String id = getArg(0);

    // create a new item, let the repsoitory generate the id, this is how
    // items are typically created
    MutableRepositoryItem item;
    if (id == null)
      {
        item = mr.createItem(descriptorName);
      }
    // but you can also tell the repository to use a specific id, but by
    // doing so you take on the repsonsibility of guaranteeing that the id is
    // unique within this item descriptor
    else
      {
        item = mr.createItem(id, descriptorName);
      }

    // set a property of the item, notice that setPropertyValue() and
    // getPropertyValue() are the dynamic beans equivalent of the getXXX()
    // and setXXX() Java Beans property accessors
    item.setPropertyValue("password", "secret");

    // ... set more properties here if you like ...

    // at this point the item has not been presisted in the database: it is
    // still only in memory. So now we make it persistent. Notice that we may
    // get back a different Java object which represents the item we've
    // created. Be sure to use the new object as we do here
    try
      {
        item = (MutableRepositoryItem)mr.addItem(item);
      }
    catch (RepositoryException re)
      {
        p("Rats, got an exception");
        re.printStackTrace();
      }

    // the repository generates unique ids for new items, this is how you get
    // the id of a repository item
    id = item.getRepositoryId();

    // even though we already know the name of the item descriptor in this
    // example, this is how you would get it from any given item
    String name = item.getItemDescriptor().getItemDescriptorName();
    pln("Added item, id=" + id + ", descriptor=" + name);
   }

  //-------------------------------------
  /**
   * Main routine. This example uses one optional command line arguments
   **/
  public static void main(String[] pArgs)
    throws Exception
  {
    String[] args = pArgs;

    // see if they've specified an id as the first argument
    // (we ignore "-" args which are passed on to the TemplateParser)
    if (args.length > 0 && !args[0].startsWith("-"))
      args = saveArgs(args, 1);

    runParser(CreateItem.class.getName(), args);
  }

} // end of class CreateItem
