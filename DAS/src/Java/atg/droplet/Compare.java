/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

package atg.droplet;

import javax.servlet.*;
import javax.servlet.http.*;

import atg.nucleus.naming.ParameterName;
import atg.servlet.*;

import java.io.*;

/**
 * The Compare servlet renders one of its oparms based on
 * the relative values of the <i>obj1</i> and <i>obj2</i> input
 * params.  It takes as parameters:
 * <dl>
 *
 * <dt>obj1  
 *
 * <dd>the first object to be compared.  the object must be 
 *     an instance of comparable.  think of this object as the left
 *     side of the equation or inequation.  e.g. obj1 > obj2 
 *
 * <dt>obj2  
 *
 * <dd>the second object to be compared.  the object must be 
 *     an instance of comparable.  think of this object as the right
 *     side of the equation or inequation.  e.g. obj1 > obj2 
 *
 * </dl>
 * 
 * It renders the following oparams:
 * <dl>
 * 
 * <dt>output
 *
 * <dd>In the output param, we set the param value which is the value 
 * returned by calling compare(obj1, obj2), converted to a String.
 *
 * <dt>greaterthan
 *
 * <dd>This oparam is rendered if obj1 > obj2.
 *
 * <dt>lessthan
 *
 * <dd>This oparam is rendered if obj1 < obj2.
 *
 * <dt>equal
 *
 * <dd>This oparam is rendered if obj1 == obj2.
 *
 * <dt>noncomparable
 *
 * <dd>This oparam is rendered the objects cannot be compared. 
 *
 * <dt>default
 *
 * <dd>This oparam is rendered if no other oparam has been handled, or if
 * either parameter is null.
 *
 * </dl>
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/Compare.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class Compare extends DynamoServlet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Compare.java#2 $$Change: 651448 $";
  public final static ParameterName OBJ1 = ParameterName.getParameterName("obj1");
  public final static ParameterName OBJ2 = ParameterName.getParameterName("obj2");
  public final static ParameterName DEFAULT = ParameterName.getParameterName("default");
  public final static ParameterName EQUAL = ParameterName.getParameterName("equal");
  public final static ParameterName GREATERTHAN = ParameterName.getParameterName("greaterthan");
  public final static ParameterName LESSTHAN = ParameterName.getParameterName("lessthan");
  public final static ParameterName NONCOMPARABLE = ParameterName.getParameterName("noncomparable");

  //-------------------------------------
  /**
   * Render the Compare
   */
  public void service(DynamoHttpServletRequest pReq, 
                      DynamoHttpServletResponse pRes) 
  		throws ServletException, IOException {
    boolean handled = false; 
    try {
      Comparable comp1 = (Comparable)pReq.getObjectParameter(OBJ1);
      Comparable comp2 = (Comparable)pReq.getObjectParameter(OBJ2);
      // Evaluate default oparam if either argument is null
      if (comp1 == null || comp2 == null)
        handled = pReq.serviceLocalParameter(NONCOMPARABLE, pReq, pRes);
      else {
        // Perform comparison
        int value;
        if (comp1 instanceof Number && comp2 instanceof Number) {
          // Comparable.compareTo doesn't handle numbers of different types
          // so use a special method
          value = compareNumbers((Number) comp1, (Number) comp2);
        } else {
          value = comp1.compareTo(comp2);
        }
        
        if (value == 0)
          handled = pReq.serviceLocalParameter(EQUAL, pReq, pRes);
        else if (value > 0)
          handled = pReq.serviceLocalParameter(GREATERTHAN, pReq, pRes);
        else if (value < 0)
          handled = pReq.serviceLocalParameter(LESSTHAN, pReq, pRes);
        
      }
    }
    catch (ClassCastException e) {      
      handled = pReq.serviceLocalParameter(NONCOMPARABLE, pReq, pRes);
    }
    if (!handled)
      handled = pReq.serviceLocalParameter(DEFAULT, pReq, pRes);  
  }

  int compareNumbers(Number number1, Number number2) {
    // If either value is floating point, compare the two as doubles.
    if (number1 instanceof Float || number1 instanceof Double ||
        number2 instanceof Float || number2 instanceof Double)
    {
      // Use Double.compareTo to take advantage of its handling of NaN,
      // -0.0, etc.
      return new Double(number1.doubleValue()).compareTo(
            new Double(number2.doubleValue()));
    } else {
     // Otherwise, compare the two integral numbers as longs.
     long long1 = number1.longValue();
     long long2 = number2.longValue();
     return long1 < long2 ? -1 : (long1 == long2 ? 0 : 1);
    }
  }
}
