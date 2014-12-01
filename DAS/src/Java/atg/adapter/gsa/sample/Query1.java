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
 * Demonstrate some basic query operations.
 *
 * <table border="1" cellpadding="3" cellspacing="0" width="100%">
 *  <tr bgcolor="#ccccff">
 *   <td colspan="2"><font size="+2"><b>GSA Code Sample</b></font>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Product</b></td><td>DAS</td></tr>
 *
 *  <tr><td width="20%"><b>Description</b></td>
 *   <td>Demonstrate some basic query operations.
 *   </td>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Author</b></td><td>mgk</td></tr>
 *  <tr><td width="20%"><b>Creation Date</b></td><td>3/3/0</td></tr>
 *
 *  <tr>
 *   <td width="20%"><b>Usage</b></td>
 *   <td>java atg.adapter.gsa.sample.Query1</td>
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
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/Query1.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class Query1
extends Harness
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/Query1.java#2 $$Change: 651448 $";

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
    ** This example demonstrates how do perform some simple repository
    ** queries. In the repository API all queries are performed using Query
    ** or QueryExpression objects. A QueryExpression is a building block you
    ** can use to create simple or complex queries. A Query is a repository
    ** query that can be executed. A Query can also be used as a building
    ** block to create more complicated queries. Here we perform a simple
    ** query to find user repository items whose age property is 5.
    */

    // queries are created using QueryBuilders and executed by
    // RepositoryViews. A Query is defined in the context of a specific item
    // descriptor and thus must be built and executed with the right
    // QueryBuilder and RepositoryView.
    RepositoryItemDescriptor userDesc = pRepository.getItemDescriptor("user");
    RepositoryView userView = userDesc.getRepositoryView();
    QueryBuilder userBuilder = userView.getQueryBuilder();

    // create a QueryExpression that represents the property age
    QueryExpression age =
      userBuilder.createPropertyQueryExpression("age");

    // create a QueryExpression that represents the constant 5
    QueryExpression five =
      userBuilder.createConstantQueryExpression(Integer.valueOf(5));

    // now we build our query: age = 5
    Query ageIsFive =
      userBuilder.createComparisonQuery(age, five, QueryBuilder.EQUALS);

    // finally, execute the query and get the results
    RepositoryItem[] answer = userView.executeQuery(ageIsFive);

    pln("running query: age = 5");
    if (answer == null)
      {
        pln("no items were found");
      }
    else
      {
        for (int i=0; i<answer.length; i++)
          pln("id: " + answer[i].getRepositoryId());
      }

    // now let's try a slightly more complicated query:
    //    age < 5 AND login STARTS WITH "j"

    // reuse the building blocks we have to create
    // the "age < 5" query
    Query ageLTFive =
      userBuilder.createComparisonQuery(age, five, QueryBuilder.LESS_THAN);

    // create the "login STARTS WITH j" query
    QueryExpression login =
      userBuilder.createPropertyQueryExpression("login");

    QueryExpression j =
      userBuilder.createConstantQueryExpression("j");

    Query startsWithJ =
      userBuilder.createPatternMatchQuery(login, j, QueryBuilder.STARTS_WITH);


    // now AND the two pieces together. You can AND together as many
    // Query pieces as you like: we only have two in our example
    Query[] pieces = { ageLTFive, startsWithJ };
    Query andQuery = userBuilder.createAndQuery(pieces);

    // execute the query and get the results
    answer = userView.executeQuery(andQuery);

    pln("running query: age < 5 AND login STARTS WITH j");
    if (answer == null)
      {
        pln("no items were found");
      }
    else
      {
        for (int i=0; i<answer.length; i++)
          {
            RepositoryItem item = answer[i];
            String id = item.getRepositoryId();
            String l = (String)item.getPropertyValue("login");
            Integer a = (Integer)item.getPropertyValue("age");
            pln("item: " + id + ", login=" + l + ", age=" + a);
          }
      }

    // instead of running a query to return all the items, you can also get a
    // count of how many items would be returned by the query. this is akin
    // to a "SELECT COUNT(*)" operation in the DB
    pln("counting query: age < 5");
    int count = userView.executeCountQuery(ageLTFive);
    pln("count is: " + count);
   }

  //-------------------------------------
  /**
   * Main routine. This example uses no command line arguments
   **/
  public static void main(String[] pArgs)
    throws Exception
  {
    runParser(Query1.class.getName(), pArgs);
  }

} // end of class Query1
