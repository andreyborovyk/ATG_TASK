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

package atg.service.idgen.sample;

import atg.service.idgen.*;

/**
 * Simple example of using a transient id generator
 *
 * @author mgk
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/idgen/sample/Example1.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class Example1
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/idgen/sample/Example1.java#2 $$Change: 651448 $";

  //-------------------------------------
  /**
   * Main
   **/
  public static void main(String argv[])
    throws Exception
  {
    // normally applications access the standard id generator service
    // at /atg/dynamo/service/IdGenerator
    // which is started up with Dynamo
    // for this example we will just use our own id generator to demonstrate
    // the APIs
    TransientIdGenerator gen = new TransientIdGenerator();
    gen.initialize();

    // get a few ids in the default id space (notice that you don't need to
    // specify a name space if you don't want to)
    for (int i=0; i<3; i++)
      {
        p("generateLongId() -> " + gen.generateLongId());
      }

    // generate a long id in the "foo" name space
    // note that by default autoCreate is true for our
    // id generator, so the "foo" space is created for us
    p("generateLongId(foo) -> " + gen.generateLongId("foo"));

    // create a new id space: for the SQLIdGenerator which most apps will
    // use, the id spaces are configured in XML. Here we create an
    // IdSpace using the Java API to demonstrate how to do it.
    p("Creating new id space.");
    IdSpace barSpace = new IdSpace("bar",  // name of id space
                                   100,    // starting id (seed)
                                   "B",    // prefix
                                   null);  // suffix

    gen.addIdSpace(barSpace);

    // generate a few long ids in our newly added space
    p("generateLongId(bar) -> " + gen.generateLongId("bar"));
    p("generateLongId(bar) -> " + gen.generateLongId("bar"));

    // see how the "foo" space is independent of the bar space
    p("generateLongId(foo) -> " + gen.generateLongId("foo"));

    // now generate some String ids. String ids use the prefix and suffix
    // properties of the id space. these properties are not consulted when
    // long ids are generated. notice too that within an id space the same
    // pool of ids is used for String and long ids.
    p("generateStringId(bar) -> " + gen.generateStringId("bar"));
    p("generateStringId(bar) -> " + gen.generateStringId("bar"));
    p("generateStringId(bar) -> " + gen.generateStringId("bar"));
    p("generateLongId(bar) -> " + gen.generateLongId("bar"));
    p("generateLongId(bar) -> " + gen.generateLongId("bar"));

    // IdGenerators can throw the checked exception IdGeneratorException
    // this exception indicates an id could not be generatored. common
    // causes include: a) DB trouble for the SQLIdGenerator; and b) asking
    // for an id in a name space that doesn't exist when autoCreate is false.
    // production applications should catch this exception

    // let's force an exception to see an example
    gen.setAutoCreate(false);
    try
      {
        p("generateStringId(bogus) -> " + gen.generateStringId("bogus"));
      }
    catch (IdGeneratorException ige)
      {
        p("rats, couldn't get an id");
        ige.printStackTrace();
      }

  }

  // print utils
  public static void p(Object o) { System.out.println(o); }
  public static void p(long l) { System.out.println(l); }

} // end of class Example1

