/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.taglib.core;

/****************************************
 * 
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/TagAttributeTypes.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public interface TagAttributeTypes
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/TagAttributeTypes.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    // null type
    public static final byte kNullType = 16;

    // type boolean
    public static final byte kBooleanType = 14;

    // type byte
    public static final byte kByteType = 0;
    
    // short type
    public static final byte kShortType = 1;

    // char type
    public static final byte kCharType = 2;

    // int type
    public static final byte kIntType = 3;

    // long type
    public static final byte kLongType = 4;

    // float type
    public static final byte kFloatType = 5;

    // double type
    public static final byte kDoubleType = 6;

    // boolean object type
    public static final byte kBooleanObjectType = 15;

    // byte object type
    public static final byte kByteObjectType = 7;

    // short object type
    public static final byte kShortObjectType = 8;

    // char object type
    public static final byte kCharObjectType = 9;

    // int object type
    public static final byte kIntObjectType = 10;

    // long object type
    public static final byte kLongObjectType = 11;

    // float object type
    public static final byte kFloatObjectType = 12;

    // double object type
    public static final byte kDoubleObjectType = 13;
    
    // arrayish types
    public static final byte kCollectionValuesType = 50;
    public static final byte kObjectArrayValuesType = 51;
    public static final byte kIteratorValuesType = 52;
    public static final byte kEnumerationValuesType = 53;
    public static final byte kMapValuesType = 54;
    public static final byte kInvalidValuesType = 55;

    public static final byte kBooleanArrayValuesType = 56;
    public static final byte kByteArrayValuesType = 57;
    public static final byte kCharArrayValuesType = 58;
    public static final byte kShortArrayValuesType = 59;
    public static final byte kIntArrayValuesType = 60;
    public static final byte kLongArrayValuesType = 61;
    public static final byte kFloatArrayValuesType = 62;
    public static final byte kDoubleArrayValuesType = 63;

} // end of interface
