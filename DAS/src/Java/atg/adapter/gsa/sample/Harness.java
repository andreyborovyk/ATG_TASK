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

import javax.transaction.*;
import atg.dtm.*;

import atg.repository.*;
import atg.adapter.gsa.*;
import atg.adapter.gsa.xml.TemplateParser;


/**
 * Template for code samples, also serves as a base class with
 * utility routines.
 * <p>
 * <table border="1" cellpadding="3" cellspacing="0" width="100%">
 *  <tr bgcolor="#ccccff">
 *   <td colspan="2"><font size="+2"><b>GSA Code Sample</b></font>
 *  </tr>
 *
 *  <tr><td width="20%"><b>Product</b></td><td>DAS</td></tr>
 *  <tr><td width="20%"><b>Author</b></td><td>mgk</td></tr>
 *  <tr><td width="20%"><b>Creation Date</b></td><td>2/28/0</td></tr>
 *
 *  <tr>
 *    <td width="20%"><b>Requirements</b></td>
 *    <td>must be run from <code>DYNAMO_HOME</code> with ATG
 *        <ccode>CLASSPATH</code>
 *    </td>
 *  </tr>
 *
 *  <tr>
 *   <td width="20%"><b>Keywords</b></td><td>GSA, query, repository</td>
 *  </tr>
 * </table>
 *
 * @author mgk
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/Harness.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public abstract
class Harness
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/sample/Harness.java#2 $$Change: 651448 $";

  /** name of template file to use for test */
  static String mTemplateFileName = "gsa-sample.xml";

  //-------------------------------------
  /**
   * This method gets called with an initialized repository.
   * @param pRepository repository to use
   * @exception RepositoryException if the sample raises an exception
   **/
  public abstract void go(Repository pRepository)
    throws RepositoryException
    ;

  //-------------------------------------
  /**
   * Run the template parser to initialize our
   * environment and run the specified sample program in that
   * environment.
   *
   * @param pSampleClassName name of sample class to run
   * @param pArgs command line args to pass to TemplateParser
   **/
  public static void runParser(String pSampleClassName, String pArgs[])
    throws Exception
  {
    pln("+++++++++++++++++++++++++++++");
    pln("++ Initializing Repository ++");
    pln("+++++++++++++++++++++++++++++");

    // add our args to those from command line
    int length = pArgs == null ? 0 : pArgs.length;
    String[] combinedArgs = new String[length + 3];
    System.arraycopy(pArgs, 0, combinedArgs, 0, length);

    String methodName = pSampleClassName + '.' + "goTransactionally";
    String[] ourArgs =  { "-invoke", methodName, mTemplateFileName };
    System.arraycopy(ourArgs, 0, combinedArgs, length, 3);

    // run our sample (the go() method)
    TemplateParser.main(combinedArgs);
  }

  //-------------------------------------
  /**
   * Run the go method in its own transaction
   **/
  public void goTransactionally(Repository pRepository)
    throws RepositoryException
  {
    try
      {
        TransactionDemarcation td = new TransactionDemarcation();
        TransactionManager mgr =
          ((GSARepository)pRepository).getTransactionManager();
        try
          {
            td.begin(mgr, TransactionDemarcation.REQUIRES_NEW);
            go(pRepository);
          }
        finally
          {
            td.end();
          }
      }
    catch (TransactionDemarcationException tde)
      {
        tde.printStackTrace();
      }
  }
  
  //-------------------------------------
  /**
   * Utility methods to print things
   **/
  public static void p(Object o)  { System.out.print(o); }
  public static void p(long l)  { System.out.print(l); }

  public static void pln(Object o)  { System.out.println(o); }
  public static void pln(long l)  { System.out.println(l); }

  /**
   * Arguments ussed by some samples. One wouldn't do this kind of thing in a
   * production environment as it makes all the smaples single threaded. This
   * is just a quick and dirty convenience for the samples to work with a
   * minimum of fuss.
   **/
  private static String[] sArgs;

  //-------------------------------------
  /**
   * Save the specified number of arguments, and return the rest
   **/
  synchronized static String[] saveArgs(String[] pArgs, int pCount)
  {
    // save the args we want
    sArgs = new String[pCount];
    System.arraycopy(pArgs, 0, sArgs, 0, pCount);

    // return the rest
    String[] rest = new String[pArgs.length - pCount];
    System.arraycopy(pArgs, pCount, rest, 0, pArgs.length - pCount);

    return rest;
  }

  //-------------------------------------
  /**
   * Get the specified argument
   **/
  synchronized static String getArg(int pIndex)
  {
    try { return sArgs[pIndex]; }
    catch (Exception e) { return null; }
  }

} // end of class Harness
