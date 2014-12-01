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

import java.beans.PropertyEditor;

/**
 * This example demonstrates how to determine at runtime what properties a
 * repository item has.
 *
 * <table border="1" cellpadding="3" cellspacing="0" width="100%">
 *  <tr bgcolor="#ccccff">
 *   <td colspan="2"><font size="+2"><b>GSA Code Sample</b></font>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Product</b></td><td>DAS</td></tr>
 *
 *  <tr><td width="20%"><b>Description</b></td>
 *   <td>This examples deonstrates how to determine at runtime what
 *       properties a repository item has.
 *   </td>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Author</b></td><td>mgk</td></tr>
 *  <tr><td width="20%"><b>Creation Date</b></td><td>3/6/0</td></tr>
 *
 *  <tr>
 *   <td width="20%"><b>Usage</b></td>
 *   <td>java atg.adapter.gsa.sample.PropertyDescriptors</td>
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
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/PropertyDescriptors.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class PropertyDescriptors
extends Harness
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/PropertyDescriptors.java#2 $$Change: 651448 $";

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
     * This example is similar to the ItemDescriptor example in that it
     * demonstrates some of the APIs available that make type information
     * available at runtime. This is as much a Dynamic Beans example as it is
     * a repository example--the two are closely related.
     **/

    // first let's get a repository item (just as in the first example,
    // id of item to delete
    String repositoryId = getArg(0);

    // name of item descriptor describing type of item to delete
    String itemDescriptorName = getArg(1);

    pln("Getting item, id=" + repositoryId +
        ", descriptor=" + itemDescriptorName + " ...");
    RepositoryItem item = pRepository.getItem(repositoryId, itemDescriptorName);

    // make sure we have an item
    if (item == null)
      {
        pln("not found, try another id or descriptor name.");
        return;
      }
    else
      {
        pln("woohoo, found it.");
      }

    // ok, now let's pretend we don't know where this item came from, for
    // example maybe it was passed to us. Here is how we can find out about
    // this RepositoryItem (which you'll recall is a DynamicBean too)
    
    // the place to start is the ItemDescriptor
    RepositoryItemDescriptor itemDesc = item.getItemDescriptor();

    // the name of the item descriptor can be thought of as the name of the
    // "type" of the item. (remember we're pretending we don't know the
    // descriptor name already to show that you can get everything you need
    // from the item itself)
    pln("The item is a <" + itemDesc.getItemDescriptorName() + "> item");

    // great, we know what to call our item. What properties does it have?
    // Well, just like Java beans, Dynamic Beans allow you to discover their
    // properties at runtime. And repository items are dynamic beans.
    RepositoryPropertyDescriptor[] properties =
      (RepositoryPropertyDescriptor[])itemDesc.getPropertyDescriptors();
    
    // first let's just print the property names
    p("+++ property names +++"); 
    for (int i=0; i<properties.length; i++)
      {
        if (i > 0)         p(", ");
        if (i % 3 == 0)    pln("");

        RepositoryPropertyDescriptor pd = properties[i];
        p(pd.getName());
      }
    pln("\n-------------------");

    // names are nice, but we need to know more about the properties to use
    // them, so now let's get more info
    pln("+++++ property details +++++"); 
    for (int i=0; i<properties.length; i++)
      {
        RepositoryPropertyDescriptor pd = properties[i];

        // name
        String name = pd.getName();

        // the type of the property
        Class type = pd.getPropertyType();

        // is this property required to have a value. This is good to know
        // when creating new repository items. All the required properties
        // for an item must have a value before the item can be added to the
        // persistent data store
        boolean required = pd.isRequired();  

        // the default value, this is the value the property has when the
        // repository item is first created
        Object defaultValue = pd.getDefaultValue();

        // this is a "hint" that querying on this property is
        // efficient. The GSA lets you query on any properties, but it is a
        // good practice to set this to "false" for properties that cannot be
        // efficiently queried -and- to make sure each of your queries uses
        // at least one queryable property.
        boolean queryable = pd.isQueryable();

        pln("property: " + name);
        pln("\ttype: " + type);
        pln("\trequired: " + required);
        pln("\tdefault: " + defaultValue);
        pln("\tqueryable: " + queryable);

        // if this is an enumerated property, let's print the possible String
        // values it can represent
        PropertyEditor pe = pd.createPropertyEditor();
        if (pe != null)
          {
            String[] tags = pe.getTags();
            if (tags != null)
              {
                pln("\tenum values:");
                for (int j=0; j<tags.length; j++)
                  pln("\t  " + tags[j]);
              }
          }
        pln("");
      }



    // as an exercise you might want to try to print the property details for
    // all of the different types of items in the repository instead of just
    // for a single type as we've done here. Hint: check out the
    // ItemDescriptors example
   }

  //-------------------------------------
  /**
   * Main routine. This example uses no command line arguments
   **/
  public static void main(String[] pArgs)
    throws Exception
  {
    // save the first two arguments, pass on the rest
    if (pArgs.length < 2)
      usage();
    else
      runParser(PropertyDescriptors.class.getName(), saveArgs(pArgs, 2));
  }

  //-------------------------------------
  /**
   * Print usage message and exit
   **/
  public static void usage()
  {
    pln("Usage: " + PropertyDescriptors.class.getName() +
        " <id> <item-descriptor-name>");
    System.exit(1);
  }

} // end of class PropertyDescriptors
