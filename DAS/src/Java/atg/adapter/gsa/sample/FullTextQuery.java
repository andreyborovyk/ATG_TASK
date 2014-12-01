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
 * Demonstrate use of the full-text query feature.
 *
 * <table border="1" cellpadding="3" cellspacing="0" width="100%">
 *  <tr bgcolor="#ccccff">
 *   <td colspan="2"><font size="+2"><b>GSA Code Sample</b></font>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Product</b></td><td>DAS</td></tr>
 *
 *  <tr><td width="20%"><b>Description</b></td>
 *   <td>Demonstrate use of the full-text query feature.
 *   </td>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Author</b></td><td>mgk</td></tr>
 *  <tr><td width="20%"><b>Creation Date</b></td><td>3/3/0</td></tr>
 *
 *  <tr>
 *   <td width="20%"><b>Usage</b></td>
 *   <td>java atg.adapter.gsa.sample.FullTextQuery</td>
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
 *   <td width="20%"><b>Keywords</b></td><td>GSA, query, repository, full-text</td>
 *  </tr>
 * </table>
 *
 * @author mstewart
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/FullTextQuery.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class FullTextQuery
extends Harness
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/FullTextQuery.java#2 $$Change: 651448 $";

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
    pln("### Running Sample Full-Text Query ###");
    pln(CLASS_VERSION);
    pln("");

    /*
    ** This example demonstrates how do perform some simple full-text repository
    ** queries. In the repository API all queries are performed using Query
    ** or QueryExpression objects. A QueryExpression is a building block you
    ** can use to create simple or complex queries. A Query is a repository
    ** query that can be executed. A Query can also be used as a building
    ** block to create more complicated queries. Here we perform a simple
    ** query to find user repository items whose story property 
    ** includes text in which the word 'dog' appears within 10 words of the
    ** word 'cat'.
    */

    // queries are created using QueryBuilders and executed by
    // RepositoryViews. A Query is defined in the context of a specific item
    // descriptor and thus must be built and executed with the right
    // QueryBuilder and RepositoryView.
    RepositoryItemDescriptor userDesc = pRepository.getItemDescriptor("user");
    RepositoryView userView = userDesc.getRepositoryView();
    QueryBuilder userBuilder = userView.getQueryBuilder();

    // create a QueryExpression that represents the property, story
    QueryExpression comment =
      userBuilder.createPropertyQueryExpression("story");

    // create a QueryExpression that represents a search expression
    // using the NEAR operator. 
    QueryExpression dogNearCat =
      userBuilder.createConstantQueryExpression("NEAR((dog, cat), 10)");

		// define the format being used by the search expression
		// appropriate to the database being used. This assumes an Oracle
		// database with the interMedia/Context full-text search option installed.
		QueryExpression format =
			userBuilder.createConstantQueryExpression("ORACLE_CONTEXT");
		
		// pick a minimum required score that the results must meet or exceed
		// in order to be returned by the full-text search engine.
    // See your search engine vendor's docs for more information on the meaning
    // and use of the score value.
    QueryExpression minScore =
      userBuilder.createConstantQueryExpression(Integer.valueOf(1));
		
		
    // now we build our query: comment contains 'dog' within 10 words of 'cat'
    Query dogTenWordsNearCat =
      userBuilder.createTextSearchQuery(comment, dogNearCat, format, minScore);

    // finally, execute the query and get the results
    RepositoryItem[] answer = userView.executeQuery(dogTenWordsNearCat);

    pln("running query: story contains 'dog' within 10 words of 'cat' ");
    if (answer == null)
      {
        pln("no items were found");
      }
    else
      {
        for (int i=0; i<answer.length; i++)
          pln("id: " + answer[i].getRepositoryId());
      }

   }

  //-------------------------------------
  /**
   * Main routine. This example uses no command line arguments
   **/
  public static void main(String[] pArgs)
    throws Exception
  {
    runParser(FullTextQuery.class.getName(), pArgs);
  }

} // end of class FullTextQuery
