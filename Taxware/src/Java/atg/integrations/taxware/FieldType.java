/* <ATGCOPYRIGHT>
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
 * "Dynamo" is a trademark of Art Technology Group, Inc. </ATGCOPYRIGHT>
 */

package atg.integrations.taxware;

/**
 *
 * <p>A FieldType specifies an interface that types for fields much
 *    support. A FieldType must be able to parse, validate, and write out
 *    fields of a given type.
 *
 * @see FieldDefinition
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/FieldType.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

interface FieldType {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/FieldType.java#2 $$Change: 651448 $";

  /** validate the value objValue. The actually class of objValue may vary
   *  according to type.
   */
  void validate(String strName, Object objValue, int length);

  /** Write the value objValue to the specified location. Note
   *  that is is important not to change the size of the buffer.
   *  so setCharAt should be used.
   */
  void setInBuffer(StringBuffer strbuf,
                   int offset, int length, Object objValue);
  Object parseFromString(String strFieldName, String strValue);

  Object convertToClass(String strFieldName, Object objOrig,
                        Class classTarget);
}
