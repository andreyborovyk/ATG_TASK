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

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import atg.core.util.NumberFormat;

import atg.core.util.ResourceUtils;
import atg.servlet.DynamoHttpServletRequest;

/**
 * This TagConverter can take an obnject that represents the number of a credit card
 * and mask out parts of that number.  This is used in display purposes where the users
 * credit card should not be displayed to the screen.  The following things can be configured
 * for controlling what is masked out:
 *
 * <P>
 *
 *   <UL>
 *      <LI>Mask character - The character that is used to mask out the numbers of the credit card.
 *          If no value is supplied, this will default to 'X'.
 *      <LI>Number of characters to display - This is the number of characters, starting from the end,
 *          to display.  If no value is displayed, it will default to 4.
 *      <LI>Number of characters in a grouping - After each grouping, a space will be inserted
 *          in the output.  If zero, no spaces will be inserted.  It defaults to 0.
 *   </UL>
 *
 * <P>
 *
 * Here is an example of how to use this: <BR>
 * <blockquote>
 * <code>&lt;valueof param="paymentGroup.creditCardNumber" CreditCard&gt;no number&lt;/valueof&gt;</code>
 * </blockquote>
 *
 * <BR>
 *
 * You can also specify the masking character to use, the number of characters to not mask, and the size
 * of each grouping of characters:
 * <blockquote>
 * <code>&lt;valueof param="paymentGroup.creditCardNumber" CreditCard maskCharacter="#" numCharsUnmasked="6" groupingSize="2"&gt;no number&lt;/valueof&gt;</code>
 * </blockquote>
 *
  
 * @author Ashley J. Streb
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/CreditCardTagConverter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CreditCardTagConverter
    implements TagConverter {

    //-------------------------------------
    // Class version string

    public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/CreditCardTagConverter.java#2 $$Change: 651448 $";
    
    //--------------------------------------------------
    // Constants
    
    public static final String NAME = "CreditCard";
    
    static final String CREDIT_CARD_ATTRIBUTE = NAME;
    static final String MASK_CHAR = "maskcharacter";
    static final String NUM_CHARS_UNMASKED = "numcharsunmasked";
    static final String GROUPING_SIZE = "groupingsize";
    static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";
    
    //--------------------------------------------------
    // Member Variables

    private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME);

    private final static TagAttributeDescriptor[] sTagAttributeDescriptors = {
	new TagAttributeDescriptor(CREDIT_CARD_ATTRIBUTE, 
	   "Indicates that the desired element should be formatted as a credit card",
	   false, true),
	new TagAttributeDescriptor(MASK_CHAR,
	   "If this attribute is present, it is used to mask the number of the credit card",
	   true, false),
	new TagAttributeDescriptor(NUM_CHARS_UNMASKED,
            "Number of characters at the end of the credit card to display",
	    true, false),
	new TagAttributeDescriptor(GROUPING_SIZE,
            "Number of characters to group by (a space is inserted between each group)",
	    true, false),
	new TagAttributeDescriptor(RequiredTagConverter.REQUIRED_ATTRIBUTE, 
	   "If this attribute is present, this value is required",
	   true, false)
    };  
    
    //--------------------------------------------------
    // Properties
    
    //--------------------
    // property: mMaskChar
    
    /** The character that is used to mask out the numbers of a credit card if not supplied via
     * attribute property. 
     */
    String mMaskChar;
    
    public void setMaskChar(String pMaskChar) {
	mMaskChar = pMaskChar;
    }
    
    public String getMaskChar() {
	return mMaskChar;
    }
    
    //----------------------
    // property: mNumCharsUnmasked

    /** The number of characters to leave unmasked. */
    Integer mNumCharsUnmasked;
    
    public void setNumCharsUnmasked(Integer pNumCharsUnmasked) {
	mNumCharsUnmasked = pNumCharsUnmasked;
    }
    
    public Integer getNumCharsUnmasked() {
	return mNumCharsUnmasked;
    }

    //----------------------
    // property: groupingSize

    /** The number of characters in each space-separated grouping in the output string.
        If zero, no grouping is performed. */
    Integer mGroupingSize;
    
    public void setGroupingSize(Integer pGroupingSize) {
	mGroupingSize = pGroupingSize;
    }
    
    public Integer getGroupingSize() {
	return mGroupingSize;
    }

    //--------------------------------------------------
    // Constructors
    
    /**
     * Empty Constructor
     *
     */
    public CreditCardTagConverter() {
    }
    
    //--------------------------------------------------
    // Methods 
    
    /**
     * Returns the name of this converter.  That is, what must be put into
     * the various jhtml tags to be able to invoke this TagConverter.
     * Remember that this value is case sensitive.
     *
     * @return the name of the TagConverter
     */
    public String getName() {
	return NAME;
    }
    
    public TagAttributeDescriptor[] getTagAttributeDescriptors() {
	return sTagAttributeDescriptors;
    }
    
    /**
     * Convert the supplied credit card number to a string that is proper
     * for displaying to the end user.  This is done by taking the credit
     * card number and masking out all the digits except for the last four.
     * This resulting String is returned for display.
     *
     * <P>
     *
     * The character tat is used to mask out all of the digits is optionally
     * supplied, but defaults to an 'X'.
     *
     * @param pRequest a value of type 'DynamoHttpServletRequest'
     * @param pValue a value of type 'Object'
     * @param pAttributes a value of type 'Properties'
     * @return a value of type 'String'
     * @exception TagConversionException if an error occurs
     */
    public String convertObjectToString(DynamoHttpServletRequest pRequest,
					Object pValue,
					Properties pAttributes)
	throws TagConversionException
    {
	// if the value is currently null then don't worry about doing anything
	if (pValue == null)
	    return (String)pValue;

	return formatCreditCard(pValue, pAttributes);
    }
    
    /**
     * This just ensures that if the property is required that it has been supplied.
     * If it has not then an exception is thrown.  Else, let the value pass through.
     *
     * @param pRequest the servlet request object
     * @param pValue the credit card number
     * @param pAttributes attributes passed in via jhtml
     * @return the credit card number
     * @exception TagConversionException if an error occurs
     */
    public Object convertStringToObject(DynamoHttpServletRequest pRequest,
					String pValue,
					Properties pAttributes) 
	throws TagConversionException
    {
	if (pValue != null) 
	    pValue = pValue.trim();

	if (pValue == null || pValue.length() == 0) {
	    // Do we also have the "required" attribute in here - if this is an error
	    if (pAttributes.getProperty(RequiredTagConverter.REQUIRED_ATTRIBUTE) == null) {
		return null;
	    } else {
                String msg = ResourceUtils.getMsgResource(
                                "missingRequiredValue", MY_RESOURCE_NAME, 
                                sResourceBundle);
		throw new TagConversionException(msg, "missingRequiredValue");
	    }
	} else {
	    return pValue;
	}
    }

    /**
     * Format and return the credit card object so that it is suitable
     * for display to the end user.
     * <P>
     * If more characters are being masked than there are to be masked, it
     * will default to masking all characters.  
     *
     * A space will be inserted after each grouping, by default every
     * fourth character in the string returned. 
     *
     * @param pCreditCard an object that should represent the Credit Card number
     * @param pAttributes attributes passed into TagConverter via jhtml
     * @return the formatted Credit Card object
     */
    public String formatCreditCard(Object pCreditCard,
				   Properties pAttributes)
    {
    	String maskCharacter = getMaskCharacter(pAttributes);
    	int numUnmasked = getNumberCharactersUnmasked(pAttributes);
    	int groupingSize = getSizeOfGrouping(pAttributes);
        
        String creditCardNumber = NumberFormat.formatCreditCardNumber(pCreditCard.toString(),maskCharacter,numUnmasked,groupingSize);
    	
        return creditCardNumber;
    }

    /**
     * Get the default mask character that is to be used to mask the numbers
     * of the credit card object.  The order that is searched for this property
     * is:
     *
     * <UL>
     *   <LI>Propery set via attributes in jhtml
     *   <LI>Property of this class 
     *   <LI>Uses default mask character
     * </UL>
     *
     *
     * @param pAttributes a value of type 'Properties'
     * @return a value of type 'String'
     */
    String getMaskCharacter(Properties pAttributes) {
	String maskChar = pAttributes.getProperty(MASK_CHAR);
	if (maskChar != null) {
	    return maskChar;
	} else if (mMaskChar != null) {
	    return mMaskChar;
	} else {
	    return NumberFormat.DEFAULT_CREDITCARD_MASK;
	}
    }

    /**
     * Get the number of characters that should not be masked by
     * the mask character.  These characters will be displayed
     * as their actual values.
     *
     * <UL>
     *   <LI>Propery set via attributes in jhtml
     *   <LI>Property of this class 
     *   <LI>Uses default number of characters
     * </UL>
     *
     *
     * @param pAttributes a value of type 'Properties'
     * @return a value of type 'String'
     */
    int getNumberCharactersUnmasked(Properties pAttributes) {
	String numUnmasked = pAttributes.getProperty(NUM_CHARS_UNMASKED);
	if (numUnmasked != null) {
	    return Integer.parseInt(numUnmasked);
	} else if (mNumCharsUnmasked != null) {
	    return mNumCharsUnmasked.intValue();
	} else {
	    return NumberFormat.DEFAULT_CREDITCARD_UNMASKED;
	}
    }
    
    /**
     * Get the number of characters in each grouping.  A space
     * will be inserted after each grouping in the output.
     *
     * <UL>
     *   <LI>Propery set via attributes in jhtml
     *   <LI>Property of this class 
     *   <LI>Uses default grouping size
     * </UL>
     *
     *
     * @param pAttributes a value of type 'Properties'
     * @return a value of type 'String'
     */
    int getSizeOfGrouping(Properties pAttributes) {
	String groupSize = pAttributes.getProperty(GROUPING_SIZE);
	if (groupSize != null) {
	    return Integer.parseInt(groupSize);
	} else if (mGroupingSize != null) {
	    return mGroupingSize.intValue();
	} else {
	    return NumberFormat.DEFAULT_CREDITCARD_GROUPING;
	}
    }
    
    

}   // end of class
