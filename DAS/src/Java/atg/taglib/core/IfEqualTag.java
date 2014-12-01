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

import javax.servlet.jsp.JspException;

/*******************************************************************************
 * The atg:IfEqual tag tests to see if the value of the first comparison
 * attribute is equal to the value of the second comparison attribute. If so,
 * then the body of the tag is rendered, if not, then the body is not rendered.
 * The first comparison attribute is defined as the attribute with a name of the
 * form xxx1, such as int1, long1, object1 etc. The second comparison attribute
 * is defined as the attribute with a name of the form xxx2, such as int2,
 * long2, object2 etc. This tag can compare any combination of two primitives,
 * java.lang.Comparable objects, or non-java.lang.Comparable objects.
 * <p>
 * For the case where two primitives of the same type are compared, the
 * comparison is done using the "<code>==</code>" operator.
 * <p>
 * For the case where two primitives of different types are compared, the lower
 * type is cast up to the higher type and then compared using the "
 * <code>==</code>" operator.
 * <p>
 * For the case where a primitive is compared with a Comparable object that has
 * a primitive equivalent (i.e. Integer, Short, Boolean etc.), the primitive is
 * replaced with its Comparable object equivalent, and the narrower of the two
 * now Comparable objects is widened to the type of the wider Comparable object,
 * then compared using the <code>Comparable.compareTo()</code> method. The
 * body is rendered if and only if the <code>Comparable.compareTo()</code>
 * method returns 0.
 * <p>
 * For the case where two Comparable objects with primitive equivalents are
 * compared, the narrower object is cast to the type of the wider object, then
 * compared using the <code>Comparable.compareTo()</code> method. If the
 * objects are of the same type, then no cast is made.
 * <p>
 * For the case where two non-Comparable objects are compared, or a
 * non-Comparable object and a Comparable object are compared, and a
 * java.util.Comparator instance attribute is supplied, the result of the
 * comparison is based on the result of calling
 * <code>Comparator.compare(object1,
 * object2)</code>. The body of the tag is
 * rendered if and only if the call to <code>Comparator.compare()</code>
 * returns 0.
 * <p>
 * For the case where two non-Comparable objects are compared, or a
 * non-Comparable object and a Comparable object are compared, and a Comparator
 * instance attribute is not supplied, the result of the comparison is based on
 * the result of calling <code>object1.equals(object2)</code>. The body of
 * the tag is rendered if and only if the call to
 * <code>object1.equals(object2)</code> returns true.
 * <p>
 * If more than two comparison attributes are supplied for comparison, not
 * including a Comparator instance attribute, then no comparison is made and the
 * body of the tag is not rendered.
 * <p>
 * If more than one first comparison attribute is supplied, or more than one
 * second comparison attribute is supplied, then no comparison is made and the
 * body of the tag is not rendered.
 * <p>
 * incorrect example: <code>
 * <pre>
 * 
 *  &lt;atg:IfEqual int1=&quot;&lt;%= myProfile.getAge() %&gt;&quot; 
 *               long1=&quot;&lt;%= yourProfile.getAgeAsFloat() %&gt;&quot;&gt;
 *    ...
 *  &lt;/atg:IfEqual&gt;
 *  
 * </pre>
 * </code>
 * <p>
 * correct example:
 * <p>
 * <code>
 * <pre>
 * 
 *  &lt;atg:IfEqual int1=&quot;&lt;%= myProfile.getAge() %&gt;&quot; 
 *               long2=&quot;&lt;%= yourProfile.getAgeAsFloat() %&gt;&quot;&gt;
 *   ...
 *  &lt;/atg:IfEqual&gt;
 *  
 * </pre>
 * </code>
 * <p>
 * If one primitive and one non-Comparable object attribute are supplied, then
 * no comparison is made and the body of the tag is not rendered.
 * <p>
 * If one primitive and one Comparable object attribute are supplied, and the
 * Comparable object attribute does not have a primitive equivalent, then no
 * comparison is made and the body of the tag is not rendered.
 * 
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id:
 *          //product/DAS/main/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/IfEqualTag.java#4
 *          $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ******************************************************************************/

public class IfEqualTag extends EqualityBooleanConditionalTag {
  //----------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/IfEqualTag.java#2 $$Change: 651448 $";

  //----------------------------------------
  // Constants
  //----------------------------------------

  //----------------------------------------
  // Member Variables
  //----------------------------------------

  //----------------------------------------
  // Properties
  //----------------------------------------

  //----------------------------------------
  // Constructors
  //----------------------------------------

  //----------------------------------------
  /**
   * Constructs an instanceof IfEqualTag
   */
  public IfEqualTag() {

  }

  //----------------------------------------
  // Object Methods
  //----------------------------------------

  //----------------------------------------
  /**
   * test primitives for equality
   * 
   * @param pType
   *          the type of the prmitive comparables
   * @return whether or not we should render the body
   */
  public int testPrimitiveCombos(byte pType) {
    switch (pType) {
    case kBooleanType:
      if (isBoolean1() == isBoolean2()) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kByteType:
      if (getByte1() == getByte2()) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kCharType:
      if (getChar1() == getChar2()) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kShortType:
      if (getShort1() == getShort2()) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kIntType:
      if (getInt1() == getInt2()) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kLongType:
      if (getLong1() == getLong2()) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kFloatType:
      if (getFloat1() == getFloat2()) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kDoubleType:
      if (getDouble1() == getDouble2()) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kBooleanObjectType:
      if (getObject1().equals(getObject2())) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kByteObjectType:
      if (((Byte) getObject1()).compareTo((Byte) getObject2()) == 0) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kCharObjectType:
      if (((Character) getObject1()).compareTo((Character) getObject2()) == 0) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kShortObjectType:
      if (((Short) getObject1()).compareTo((Short) getObject2()) == 0) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kIntObjectType:
      if (((Integer) getObject1()).compareTo((Integer) getObject2()) == 0) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kLongObjectType:
      if (((Long) getObject1()).compareTo((Long) getObject2()) == 0) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kFloatObjectType:
      if (((Float) getObject1()).compareTo((Float) getObject2()) == 0) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kDoubleObjectType:
      if (((Double) getObject1()).compareTo((Double) getObject2()) == 0) {
        doneTesting();
        return EVAL_BODY_INCLUDE;
      }
      else
        return SKIP_BODY;
    case kNullType:
      return SKIP_BODY;
    }

    return SKIP_BODY;
  }

  //----------------------------------------
  // GenericTag methods
  //----------------------------------------

  //----------------------------------------
  /**
   * render this tag if the args are equal
   */
  public int doStartTag() throws JspException {
    try {
      if (isChildOfExclusiveIf() && getExclusiveIfTag().isTesting() == false)
        return SKIP_BODY;

      // if there were an invalid number of args, don't render body
      if (invalidArgs())
        return SKIP_BODY;

      // compare primitives
      if (comparingIntPrimitives()) {
        if (getInt1() == getInt2()) {
          doneTesting();
          return EVAL_BODY_INCLUDE;
        }
        else
          return SKIP_BODY;
      }
      if (comparingBytePrimitives()) {
        if (getByte1() == getByte2()) {
          doneTesting();
          return EVAL_BODY_INCLUDE;
        }
        else
          return SKIP_BODY;
      }
      if (comparingCharPrimitives()) {
        if (getChar1() == getChar2()) {
          doneTesting();
          return EVAL_BODY_INCLUDE;
        }
        else
          return SKIP_BODY;
      }
      if (comparingShortPrimitives()) {
        if (getShort1() == getShort2()) {
          doneTesting();
          return EVAL_BODY_INCLUDE;
        }
        else
          return SKIP_BODY;
      }
      if (comparingLongPrimitives()) {
        if (getLong1() == getLong2()) {
          doneTesting();
          return EVAL_BODY_INCLUDE;
        }
        else
          return SKIP_BODY;
      }
      if (comparingFloatPrimitives()) {
        if (getFloat1() == getFloat2()) {
          doneTesting();
          return EVAL_BODY_INCLUDE;
        }
        else
          return SKIP_BODY;
      }
      if (comparingDoublePrimitives()) {
        if (getDouble1() == getDouble2()) {
          doneTesting();
          return EVAL_BODY_INCLUDE;
        }
        else
          return SKIP_BODY;
      }
      if (comparingBooleanPrimitives()) {
        if (isBoolean1() == isBoolean2()) {
          doneTesting();
          return EVAL_BODY_INCLUDE;
        }
        else
          return SKIP_BODY;
      }

      // if we have a comparator, use it
      if (getComparator() != null) {
        if (comparingObjects()) {
          if (getComparator().compare(getObject1(), getObject2()) == 0) {
            doneTesting();
            return EVAL_BODY_INCLUDE;
          }
        }
        return SKIP_BODY;
      }

      // see if we're comparing any combination of primitives
      // and comparable objects with primitive equivalents
      if (comparingPrimitiveComparables()) {
        return testPrimitiveCombos(normalizePrimitiveComparables());
      }

      if (comparingComparableObjects()) {
        Comparable comparable1 = (Comparable) getObject1();
        Comparable comparable2 = (Comparable) getObject2();

        if (comparable1.compareTo(comparable2) == 0) {
          doneTesting();
          return EVAL_BODY_INCLUDE;
        }
        return SKIP_BODY;
      }

      // if we're comparing objects, but they're not comparable, use
      // the dot equals operator to test for equality
      if (comparingObjects()) {
        if (getObject1().equals(getObject2())) {
          doneTesting();
          return EVAL_BODY_INCLUDE;
        }
        return SKIP_BODY;
      }

      return SKIP_BODY;
    }
    finally {
      super.cleanup();
    }

  }

  //----------------------------------------
  // Static Methods
  //----------------------------------------

} // end of class
