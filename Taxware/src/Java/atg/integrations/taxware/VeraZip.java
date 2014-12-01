/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

package atg.integrations.taxware;

/** 
 * This class implements the code to call into the jverazip library. 
 *
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/VeraZip.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class VeraZip
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/VeraZip.java#2 $$Change: 651448 $";

  /** Native method to verify zipcode. */
  native static int Call_VeraZip(String inbuffer, byte outbuffer[]);
  final static int TABLEMAX = 300;
  final static int OUTPUTSTRINGSIZE = 208 + 70 * TABLEMAX + 1;
  final static int FULLOUTSTRSIZE = 208 + 70 * TABLEMAX + 1;
  // final static int MAXOUTBUFFERSIZE = 307286;

  /** Verify the zip code information in inbuff, write the
   * result to outbuffer.
   */
  public static int VerifyZip(String inbuffer, StringBuffer outbuffer)
  {
    byte outdata[] = new byte[FULLOUTSTRSIZE];
    int ComplCode = -1;
    int outbuflength = FULLOUTSTRSIZE;

    ComplCode = Call_VeraZip(inbuffer, outdata);

  /*
   * Perform output data formatting
   */
    // outbuflength = ComplCode / 100;
    // ComplCode = ComplCode - outbuflength * 100;
    //String convert = new String(outdata, 0, outbuflength);
    String convert = new String(outdata);
    outbuffer.append(convert);

    return ComplCode;
  }

  /** Load the jverazip library. */
  static        
  {
    System.loadLibrary("jverazip");
  }
}
