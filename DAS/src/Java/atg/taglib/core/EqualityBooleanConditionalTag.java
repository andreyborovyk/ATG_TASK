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

import java.util.*;

/****************************************
 * This class is the subclass for any tag that will compare
 * several combinations of variable types.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/EqualityBooleanConditionalTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class EqualityBooleanConditionalTag
    extends BooleanConditionalTag
    implements TagAttributeTypes
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/EqualityBooleanConditionalTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // first arg counter
    protected short mFirstArgCounter;

    // second arg counter
    protected short mSecondArgCounter;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // FirstArgType
    private byte mFirstArgType;
    /**
     * set FirstArgType
     * @param pFirstArgType the FirstArgType
     */
    public void setFirstArgType(byte pFirstArgType) { mFirstArgType = pFirstArgType; }
    /**
     * get FirstArgType
     * @return the FirstArgType
     */
    public byte getFirstArgType() { return mFirstArgType; }

    //----------------------------------------
    // SecondArgType
    private byte mSecondArgType;
    /**
     * set SecondArgType
     * @param pSecondArgType the SecondArgType
     */
    public void setSecondArgType(byte pSecondArgType) { mSecondArgType = pSecondArgType; }
    /**
     * get SecondArgType
     * @return the SecondArgType
     */
    public byte getSecondArgType() { return mSecondArgType; }

    //----------------------------------------
    // FirstArgPrimitive
    private boolean mFirstArgPrimitive;
    /**
     * set FirstArgPrimitive
     * @param pFirstArgPrimitive the FirstArgPrimitive
     */
    public void setFirstArgPrimitive(boolean pFirstArgPrimitive) 
    { mFirstArgPrimitive = pFirstArgPrimitive; }
    /**
     * get FirstArgPrimitive
     * @return the FirstArgPrimitive
     */
    public boolean isFirstArgPrimitive() { return mFirstArgPrimitive; }

    //----------------------------------------
    // SecondArgPrimitive
    private boolean mSecondArgPrimitive;
    /**
     * set SecondArgPrimitive
     * @param pSecondArgPrimitive the SecondArgPrimitive
     */
    public void setSecondArgPrimitive(boolean pSecondArgPrimitive) 
    { mSecondArgPrimitive = pSecondArgPrimitive; }
    /**
     * get SecondArgPrimitive
     * @return the SecondArgPrimitive
     */
    public boolean isSecondArgPrimitive() { return mSecondArgPrimitive; }

    //----------------------------------------
    // FirstArgComparable
    private boolean mFirstArgComparable;
    /**
     * set FirstArgComparable
     * @param pFirstArgComparable the FirstArgComparable
     */
    public void setFirstArgComparable(boolean pFirstArgComparable) 
    { mFirstArgComparable = pFirstArgComparable; }
    /**
     * get FirstArgComparable
     * @return the FirstArgComparable
     */
    public boolean isFirstArgComparable() { return mFirstArgComparable; }

    //----------------------------------------
    // SecondArgComparable
    private boolean mSecondArgComparable;
    /**
     * set SecondArgComparable
     * @param pSecondArgComparable the SecondArgComparable
     */
    public void setSecondArgComparable(boolean pSecondArgComparable) 
    { mSecondArgComparable = pSecondArgComparable; }
    /**
     * get SecondArgComparable
     * @return the SecondArgComparable
     */
    public boolean isSecondArgComparable() { return mSecondArgComparable; }

    //----------------------------------------
    // FirstArgPrimitiveComparable
    private boolean mFirstArgPrimitiveComparable;
    /**
     * set FirstArgPrimitiveComparable
     * @param pFirstArgPrimitiveComparable the FirstArgPrimitiveComparable
     */
    public void setFirstArgPrimitiveComparable(boolean pFirstArgPrimitiveComparable) 
    { mFirstArgPrimitiveComparable = pFirstArgPrimitiveComparable; }
    /**
     * get FirstArgPrimitiveComparable
     * @return the FirstArgPrimitiveComparable
     */
    public boolean isFirstArgPrimitiveComparable() { return mFirstArgPrimitiveComparable; }

    //----------------------------------------
    // SecondArgPrimitiveComparable
    private boolean mSecondArgPrimitiveComparable;
    /**
     * set SecondArgPrimitiveComparable
     * @param pSecondArgPrimitiveComparable the SecondArgPrimitiveComparable
     */
    public void setSecondArgPrimitiveComparable(boolean pSecondArgPrimitiveComparable) 
    { mSecondArgPrimitiveComparable = pSecondArgPrimitiveComparable; }
    /**
     * get SecondArgPrimitiveComparable
     * @return the SecondArgPrimitiveComparable
     */
    public boolean isSecondArgPrimitiveComparable() { return mSecondArgPrimitiveComparable; }

    //----------------------------------------
    // Comparator
    private Comparator mComparator;
    /**
     * set Comparator
     * @param pComparator the Comparator
     */
    public void setComparator(Comparator pComparator) { mComparator = pComparator; }
    /**
     * get Comparator
     * @return the Comparator
     */
    public Comparator getComparator() { return mComparator; }

    //----------------------------------------
    // Object1
    private Object mObject1;
    /**
     * set Object1
     * @param pObject1 the Object1
     */
    public void setObject1(Object pObject1) 
    { 
	processObject1(pObject1);
    }
    protected void processObject1(Object pObject1)
    {
	if(getObject1() == null) {
	    mFirstArgCounter++;
	}

	mObject1 = pObject1; 

	if(getObject1() instanceof Comparable) {
	    setFirstArgComparable(true);
	    
	    if(getObject1() instanceof java.lang.Byte) {
		setFirstArgPrimitiveComparable(true);
		setFirstArgType(kByteObjectType);
	    }
	    else if(getObject1() instanceof java.lang.Character) {
		setFirstArgPrimitiveComparable(true);
		setFirstArgType(kCharObjectType);
	    }
	    else if(getObject1() instanceof java.lang.Short) {
		setFirstArgPrimitiveComparable(true);
		setFirstArgType(kShortObjectType);
	    }
	    else if(getObject1() instanceof java.lang.Integer) {
		setFirstArgPrimitiveComparable(true);
		setFirstArgType(kIntObjectType);
	    }
	    else if(getObject1() instanceof java.lang.Long) {
		setFirstArgPrimitiveComparable(true);
		setFirstArgType(kLongObjectType);
	    }
	    else if(getObject1() instanceof java.lang.Float) {
		setFirstArgPrimitiveComparable(true);
		setFirstArgType(kFloatObjectType);
	    }
	    else if(getObject1() instanceof java.lang.Double) {
		setFirstArgPrimitiveComparable(true);
		setFirstArgType(kDoubleObjectType);
	    }
	}
	else if(getObject1() instanceof java.lang.Boolean) {
	    setFirstArgComparable(true);
	    setFirstArgPrimitiveComparable(true);
	    setFirstArgType(kBooleanObjectType);
	}
    }
    /**
     * get Object1
     * @return the Object1
     */
    public Object getObject1() { return mObject1; }

    //----------------------------------------
    // Object2
    private Object mObject2;
    /**
     * set Object2
     * @param pObject2 the Object2
     */
    public void setObject2(Object pObject2)
    {
	processObject2(pObject2);
    }
    protected void processObject2(Object pObject2) 
    { 
	if(getObject2() == null)
	    mSecondArgCounter++;
	    
	mObject2 = pObject2; 

	if(getObject2() instanceof Comparable) {
	    setSecondArgComparable(true);
	    
	    if(getObject2() instanceof java.lang.Byte) {
		setSecondArgPrimitiveComparable(true);
		setSecondArgType(kByteObjectType);
	    }
	    else if(getObject2() instanceof java.lang.Character) {
		setSecondArgPrimitiveComparable(true);
		setSecondArgType(kCharObjectType);
	    }
	    else if(getObject2() instanceof java.lang.Short) {
		setSecondArgPrimitiveComparable(true);
		setSecondArgType(kShortObjectType);
	    }
	    else if(getObject2() instanceof java.lang.Integer) {
		setSecondArgPrimitiveComparable(true);
		setSecondArgType(kIntObjectType);
	    }
	    else if(getObject2() instanceof java.lang.Long) {
		setSecondArgPrimitiveComparable(true);
		setSecondArgType(kLongObjectType);
	    }
	    else if(getObject2() instanceof java.lang.Float) {
		setSecondArgPrimitiveComparable(true);
		setSecondArgType(kFloatObjectType);
	    }
	    else if(getObject2() instanceof java.lang.Double) {
		setSecondArgPrimitiveComparable(true);
		setSecondArgType(kDoubleObjectType);
	    }
	}
	else if(getObject2() instanceof java.lang.Boolean) {
	    setSecondArgComparable(true);
	    setSecondArgPrimitiveComparable(true);
	    setSecondArgType(kBooleanObjectType);
	}
    }
    /**
     * get Object2
     * @return the Object2
     */
    public Object getObject2() { return mObject2; }

    //----------------------------------------
    // Boolean1
    private boolean mBoolean1;
    /**
     * set Boolean1
     * @param pBoolean1 the Boolean1
     */
    public void setBoolean1(boolean pBoolean1) 
    { 
	mBoolean1 = pBoolean1; 

	if(!isBoolean1Set()) {
	    mFirstArgCounter++;
	    setBoolean1Set(true);
	    setFirstArgPrimitive(true);
	    setFirstArgType(kBooleanType);
	}
    }
    /**
     * get Boolean1
     * @return the Boolean1
     */
    public boolean isBoolean1() { return mBoolean1; }

    //----------------------------------------
    // Boolean1Set
    private boolean mBoolean1Set;
    /**
     * set Boolean1Set
     * @param pBoolean1Set the Boolean1Set
     */
    public void setBoolean1Set(boolean pBoolean1Set) { mBoolean1Set = pBoolean1Set; }
    /**
     * get Boolean1Set
     * @return the Boolean1Set
     */
    public boolean isBoolean1Set() { return mBoolean1Set; }

    //----------------------------------------
    // Boolean2
    private boolean mBoolean2;
    /**
     * set Boolean2
     * @param pBoolean2 the Boolean2
     */
    public void setBoolean2(boolean pBoolean2) 
    { 
	mBoolean2 = pBoolean2; 

	if(!isBoolean2Set()) {
	    mSecondArgCounter++;
	    setBoolean2Set(true);
	    setSecondArgPrimitive(true);
	    setSecondArgType(kBooleanType);
	}
    }
    /**
     * get Boolean2
     * @return the Boolean2
     */
    public boolean isBoolean2() { return mBoolean2; }

    //----------------------------------------
    // Boolean2Set
    private boolean mBoolean2Set;
    /**
     * set Boolean2Set
     * @param pBoolean2Set the Boolean2Set
     */
    public void setBoolean2Set(boolean pBoolean2Set) { mBoolean2Set = pBoolean2Set; }
    /**
     * get Boolean2Set
     * @return the Boolean2Set
     */
    public boolean isBoolean2Set() { return mBoolean2Set; }

    //----------------------------------------
    // Char1
    private char mChar1;
    /**
     * set Char1
     * @param pChar1 the Char1
     */
    public void setChar1(char pChar1) 
    { 
	mChar1 = pChar1; 

	if(!isChar1Set()) {
	    mFirstArgCounter++;
	    setChar1Set(true);
	    setFirstArgPrimitive(true);
	    setFirstArgType(kCharType);
	}
    }
    /**
     * get Char1
     * @return the Char1
     */
    public char getChar1() { return mChar1; }

    //----------------------------------------
    // Char1Set
    private boolean mChar1Set;
    /**
     * set Char1Set
     * @param pChar1Set the Char1Set
     */
    public void setChar1Set(boolean pChar1Set) { mChar1Set = pChar1Set; }
    /**
     * get Char1Set
     * @return the Char1Set
     */
    public boolean isChar1Set() { return mChar1Set; }

    //----------------------------------------
    // Char2
    private char mChar2;
    /**
     * set Char2
     * @param pChar2 the Char2
     */
    public void setChar2(char pChar2) 
    { 
	mChar2 = pChar2; 
	
	if(!isChar2Set()) {
	    mSecondArgCounter++;
	    setChar2Set(true);
	    setSecondArgPrimitive(true);
	    setSecondArgType(kCharType);
	}
    }
    /**
     * get Char2
     * @return the Char2
     */
    public char getChar2() { return mChar2; }

    //----------------------------------------
    // Char2Set
    private boolean mChar2Set;
    /**
     * set Char2Set
     * @param pChar2Set the Char2Set
     */
    public void setChar2Set(boolean pChar2Set) { mChar2Set = pChar2Set; }
    /**
     * get Char2Set
     * @return the Char2Set
     */
    public boolean isChar2Set() { return mChar2Set; }

    //----------------------------------------
    // Byte1
    private byte mByte1;
    /**
     * set Byte1
     * @param pByte1 the Byte1
     */
    public void setByte1(byte pByte1) 
    { 
	mByte1 = pByte1; 

	if(!isByte1Set()) {
	    mFirstArgCounter++;
	    setByte1Set(true);
	    setFirstArgPrimitive(true);
	    setFirstArgType(kByteType);
	}
    }
    /**
     * get Byte1
     * @return the Byte1
     */
    public byte getByte1() { return mByte1; }

    //----------------------------------------
    // Byte1Set
    private boolean mByte1Set;
    /**
     * set Byte1Set
     * @param pByte1Set the Byte1Set
     */
    public void setByte1Set(boolean pByte1Set) { mByte1Set = pByte1Set; }
    /**
     * get Byte1Set
     * @return the Byte1Set
     */
    public boolean isByte1Set() { return mByte1Set; }

    //----------------------------------------
    // Byte2
    private byte mByte2;
    /**
     * set Byte2
     * @param pByte2 the Byte2
     */
    public void setByte2(byte pByte2) 
    { 
	mByte2 = pByte2; 
	
	if(!isByte2Set()) {
	    mSecondArgCounter++;
	    setByte2Set(true);
	    setSecondArgPrimitive(true);
	    setSecondArgType(kByteType);
	}
    }
    /**
     * get Byte2
     * @return the Byte2
     */
    public byte getByte2() { return mByte2; }

    //----------------------------------------
    // Byte2Set
    private boolean mByte2Set;
    /**
     * set Byte2Set
     * @param pByte2Set the Byte2Set
     */
    public void setByte2Set(boolean pByte2Set) { mByte2Set = pByte2Set; }
    /**
     * get Byte2Set
     * @return the Byte2Set
     */
    public boolean isByte2Set() { return mByte2Set; }

    //----------------------------------------
    // Short1
    private short mShort1;
    /**
     * set Short1
     * @param pShort1 the Short1
     */
    public void setShort1(short pShort1) 
    { 
	mShort1 = pShort1; 

	if(!isShort1Set()) {
	    mFirstArgCounter++;
	    setShort1Set(true);
	    setFirstArgPrimitive(true);
	    setFirstArgType(kShortType);
	}
    }
    /**
     * get Short1
     * @return the Short1
     */
    public short getShort1() { return mShort1; }

    //----------------------------------------
    // Short1Set
    private boolean mShort1Set;
    /**
     * set Short1Set
     * @param pShort1Set the Short1Set
     */
    public void setShort1Set(boolean pShort1Set) { mShort1Set = pShort1Set; }
    /**
     * get Short1Set
     * @return the Short1Set
     */
    public boolean isShort1Set() { return mShort1Set; }

    //----------------------------------------
    // Short2
    private short mShort2;
    /**
     * set Short2
     * @param pShort2 the Short2
     */
    public void setShort2(short pShort2) 
    { 
	mShort2 = pShort2; 

	if(!isShort2Set()) {
	    mSecondArgCounter++;
	    setShort2Set(true);
	    setSecondArgPrimitive(true);
	    setSecondArgType(kShortType);
	}
    }
    /**
     * get Short2
     * @return the Short2
     */
    public short getShort2() { return mShort2; }

    //----------------------------------------
    // Short2Set
    private boolean mShort2Set;
    /**
     * set Short2Set
     * @param pShort2Set the Short2Set
     */
    public void setShort2Set(boolean pShort2Set) { mShort2Set = pShort2Set; }
    /**
     * get Short2Set
     * @return the Short2Set
     */
    public boolean isShort2Set() { return mShort2Set; }

    //----------------------------------------
    // Int1
    private int mInt1;
    /**
     * set Int1
     * @param pInt1 the Int1
     */
    public void setInt1(int pInt1) 
    { 
	mInt1 = pInt1; 

	if(!isInt1Set()) {
	    mFirstArgCounter++;
	    setInt1Set(true);
	    setFirstArgPrimitive(true);
	    setFirstArgType(kIntType);
	}
    }
    /**
     * get Int1
     * @return the Int1
     */
    public int getInt1() { return mInt1; }

    //----------------------------------------
    // Int1Set
    private boolean mInt1Set;
    /**
     * set Int1Set
     * @param pInt1Set the Int1Set
     */
    public void setInt1Set(boolean pInt1Set) { mInt1Set = pInt1Set; }
    /**
     * get Int1Set
     * @return the Int1Set
     */
    public boolean isInt1Set() { return mInt1Set; }

    //----------------------------------------
    // Int2
    private int mInt2;
    /**
     * set Int2
     * @param pInt2 the Int2
     */
    public void setInt2(int pInt2) 
    { 
	mInt2 = pInt2; 
	
	if(!isInt2Set()) {
	    mSecondArgCounter++;
	    setInt2Set(true);
	    setSecondArgPrimitive(true);
	    setSecondArgType(kIntType);
	}
    }
    /**
     * get Int2
     * @return the Int2
     */
    public int getInt2() { return mInt2; }

    //----------------------------------------
    // Int2Set
    private boolean mInt2Set;
    /**
     * set Int2Set
     * @param pInt2Set the Int2Set
     */
    public void setInt2Set(boolean pInt2Set) { mInt2Set = pInt2Set; }
    /**
     * get Int2Set
     * @return the Int2Set
     */
    public boolean isInt2Set() { return mInt2Set; }

    //----------------------------------------
    // Long1
    private long mLong1;
    /**
     * set Long1
     * @param pLong1 the Long1
     */
    public void setLong1(long pLong1) 
    { 
	mLong1 = pLong1; 

	if(!isLong1Set()) {
	    mFirstArgCounter++;
	    setLong1Set(true);
	    setFirstArgPrimitive(true);
	    setFirstArgType(kLongType);
	}
    }
    /**
     * get Long1
     * @return the Long1
     */
    public long getLong1() { return mLong1; }

    //----------------------------------------
    // Long1Set
    private boolean mLong1Set;
    /**
     * set Long1Set
     * @param pLong1Set the Long1Set
     */
    public void setLong1Set(boolean pLong1Set) { mLong1Set = pLong1Set; }
    /**
     * get Long1Set
     * @return the Long1Set
     */
    public boolean isLong1Set() { return mLong1Set; }

    //----------------------------------------
    // Long2
    private long mLong2;
    /**
     * set Long2
     * @param pLong2 the Long2
     */
    public void setLong2(long pLong2) 
    { 
	mLong2 = pLong2; 
	
	if(!isLong2Set()) {
	    mSecondArgCounter++;
	    setLong2Set(true);
	    setSecondArgPrimitive(true);
	    setSecondArgType(kLongType);
	}
    }
    /**
     * get Long2
     * @return the Long2
     */
    public long getLong2() { return mLong2; }

    //----------------------------------------
    // Long2Set
    private boolean mLong2Set;
    /**
     * set Long2Set
     * @param pLong2Set the Long2Set
     */
    public void setLong2Set(boolean pLong2Set) { mLong2Set = pLong2Set; }
    /**
     * get Long2Set
     * @return the Long2Set
     */
    public boolean isLong2Set() { return mLong2Set; }

    //----------------------------------------
    // Float1
    private float mFloat1;
    /**
     * set Float1
     * @param pFloat1 the Float1
     */
    public void setFloat1(float pFloat1) 
    { 
	mFloat1 = pFloat1; 

	if(!isFloat1Set()) {
	    mFirstArgCounter++;
	    setFloat1Set(true);
	    setFirstArgPrimitive(true);
	    setFirstArgType(kFloatType);
	}
    }
    /**
     * get Float1
     * @return the Float1
     */
    public float getFloat1() { return mFloat1; }

    //----------------------------------------
    // Float1Set
    private boolean mFloat1Set;
    /**
     * set Float1Set
     * @param pFloat1Set the Float1Set
     */
    public void setFloat1Set(boolean pFloat1Set) { mFloat1Set = pFloat1Set; }
    /**
     * get Float1Set
     * @return the Float1Set
     */
    public boolean isFloat1Set() { return mFloat1Set; }

    //----------------------------------------
    // Float2
    private float mFloat2;
    /**
     * set Float2
     * @param pFloat2 the Float2
     */
    public void setFloat2(float pFloat2) 
    { 
	mFloat2 = pFloat2; 

	if(!isFloat2Set()) {
	    mSecondArgCounter++;
	    setFloat2Set(true);
	    setSecondArgPrimitive(true);
	    setSecondArgType(kFloatType);
	}
    }
    /**
     * get Float2
     * @return the Float2
     */
    public float getFloat2() { return mFloat2; }

    //----------------------------------------
    // Float2Set
    private boolean mFloat2Set;
    /**
     * set Float2Set
     * @param pFloat2Set the Float2Set
     */
    public void setFloat2Set(boolean pFloat2Set) { mFloat2Set = pFloat2Set; }
    /**
     * get Float2Set
     * @return the Float2Set
     */
    public boolean isFloat2Set() { return mFloat2Set; }

    //----------------------------------------
    // Double1
    private double mDouble1;
    /**
     * set Double1
     * @param pDouble1 the Double1
     */
    public void setDouble1(double pDouble1) 
    { 
	mDouble1 = pDouble1; 

	if(!isDouble1Set()) {
	    mFirstArgCounter++;
	    setDouble1Set(true);
	    setFirstArgPrimitive(true);
	    setFirstArgType(kDoubleType);
	}
    }
    /**
     * get Double1
     * @return the Double1
     */
    public double getDouble1() { return mDouble1; }

    //----------------------------------------
    // Double1Set
    private boolean mDouble1Set;
    /**
     * set Double1Set
     * @param pDouble1Set the Double1Set
     */
    public void setDouble1Set(boolean pDouble1Set) { mDouble1Set = pDouble1Set; }
    /**
     * get Double1Set
     * @return the Double1Set
     */
    public boolean isDouble1Set() { return mDouble1Set; }

    //----------------------------------------
    // Double2
    private double mDouble2;
    /**
     * set Double2
     * @param pDouble2 the Double2
     */
    public void setDouble2(double pDouble2) 
    { 
	mDouble2 = pDouble2; 

	if(!isDouble2Set()) {
	    mSecondArgCounter++;
	    setDouble2Set(true);
	    setSecondArgPrimitive(true);
	    setSecondArgType(kDoubleType);
	}
    }
    /**
     * get Double2
     * @return the Double2
     */
    public double getDouble2() { return mDouble2; }

    //----------------------------------------
    // Double2Set
    private boolean mDouble2Set;
    /**
     * set Double2Set
     * @param pDouble2Set the Double2Set
     */
    public void setDouble2Set(boolean pDouble2Set) { mDouble2Set = pDouble2Set; }
    /**
     * get Double2Set
     * @return the Double2Set
     */
    public boolean isDouble2Set() { return mDouble2Set; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof EqualityBooleanConditionalTag
     */
    public EqualityBooleanConditionalTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * are more than two first args, or more than two second args set?
     * are there more than two values up for comparison? less than two?
     * let's find out!
     */
    public boolean invalidArgs()
    {
	if(mFirstArgCounter > 1 || mSecondArgCounter > 1) {
	    return true;
	}
	if((mFirstArgCounter + mSecondArgCounter) != 2) {
	    return true;
	}

	return false;
    }

    //----------------------------------------
    /**
     * are we comparing 2 boolean primitives
     */
    public boolean comparingBooleanPrimitives()
    {
	if(isBoolean1Set() && isBoolean2Set())
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    /**
     * are we comparing 2 byte primitives?
     */
    public boolean comparingBytePrimitives()
    {
	if(isByte1Set() && isByte2Set())
	    return true;
	else 
	    return false;
    }

    //----------------------------------------
    /**
     * are we comparing 2 char primitives?
     */
    public boolean comparingCharPrimitives()
    {
	if(isChar1Set() && isChar2Set())
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    /**
     * are we comparing 2 short primitives?
     */
    public boolean comparingShortPrimitives()
    {
	if(isShort1Set() && isShort2Set())
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    /**
     * are we comparing 2 int primitives?
     */
    public boolean comparingIntPrimitives()
    {
	if(isInt1Set() && isInt2Set())
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    /**
     * are we comparing 2 long primitives?
     */
    public boolean comparingLongPrimitives()
    {
	if(isLong1Set() && isLong2Set())
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    /**
     * are we comparing 2 float primitives?
     */
    public boolean comparingFloatPrimitives()
    {
	if(isFloat1Set() && isFloat2Set())
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    /**
     * are we comparing 2 double primitives?
     */
    public boolean comparingDoublePrimitives()
    {
	if(isDouble1Set() && isDouble2Set())
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    /**
     * are we comparing 2 objects?
     */
    public boolean comparingObjects()
    {
	if(getObject1() != null && getObject2() != null)
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    /**
     * are we comparing 2 comparable objects?
     */
    public boolean comparingComparableObjects()
    {
	if(isFirstArgComparable() &&
	   isSecondArgComparable())
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    /**
     * are we comparing a combination of primitives and comparables
     * with primitive equivalents?
     */
    public boolean comparingPrimitiveComparables()
    {
	if((isFirstArgPrimitiveComparable() || isFirstArgPrimitive()) &&
	   (isSecondArgPrimitiveComparable() || isSecondArgPrimitive()))
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    /**
     * normalize the primitive comparables
     * return the type of both primitive comparables after the normalization
     */
    public byte normalizePrimitiveComparables()
    {
	if(getFirstArgType() == getSecondArgType()) {
	    return getFirstArgType();
	}
	else {
	    if(isFirstArgPrimitive() &&
	       isSecondArgPrimitive()) {
		switch(getSecondArgType()) {
		case kBooleanType:
		    return kNullType;
		case kByteType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kCharType:
			// char and byte
			setChar2((char) getByte2());
			return kCharType;
		    case kShortType:
			// short and byte
			setShort2((short) getByte2());
			return kShortType;
		    case kIntType:
			// int and byte
			setInt2((int) getByte2());
			return kIntType;
		    case kLongType:
			// long and byte
			setLong2((long) getByte2());
			return kLongType;
		    case kFloatType:
			// float and byte
			setFloat2((float) getByte2());
			return kFloatType;
		    case kDoubleType:
			// double and byte
			setDouble2((double) getByte2());
			return kDoubleType;
		    }
		    break;
		case kCharType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and char
			setChar1((char) getByte1());
			return kCharType;
		    case kShortType:
			// short and char
			setShort2((short) getChar2());
			return kShortType;
		    case kIntType:
			// int and char
			setInt2((int) getChar2());
			return kIntType;
		    case kLongType:
			// long and char
			setLong2((long) getChar2());
			return kLongType;
		    case kFloatType:
			// float and char
			setFloat2((float) getChar2());
			return kFloatType;
		    case kDoubleType:
			// double and char
			setDouble2((double) getChar2());
			return kDoubleType;
		    }
		    break;
		case kShortType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and short
			setShort1((short) getByte1());
			return kShortType;
		    case kCharType:
			// char and short
			setShort1((short) getChar1());
			return kShortType;
		    case kIntType:
			// int and short
			setInt2((int) getShort2());
			return kIntType;
		    case kLongType:
			// long and short
			setLong2((long) getShort2());
			return kLongType;
		    case kFloatType:
			// float and short
			setFloat2((float) getShort2());
			return kFloatType;
		    case kDoubleType:
			// double and short
			setDouble2((double) getShort2());
			return kDoubleType;
		    }
		    break;
		case kIntType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and int
			setInt1((int) getByte1());
			return kIntType;
		    case kCharType:
			// char and int
			setInt1((int) getChar1());
			return kIntType;
		    case kShortType:
			// short and int
			setInt1((int) getShort1());
			return kIntType;
		    case kLongType:
			// long and int
			setLong2((long) getInt2());
			return kLongType;
		    case kFloatType:
			// float and int
			setFloat2((float) getInt2());
			return kFloatType;
		    case kDoubleType:
			// double and int
			setDouble2((double) getInt2());
			return kDoubleType;
		    }
		    break;
		case kLongType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and long
			setLong1((long) getByte1());
			return kLongType;
		    case kCharType:
			// char and long
			setLong1((long) getChar1());
			return kLongType;
		    case kShortType:
			// short and long
			setLong1((long) getShort1());
			return kLongType;
		    case kIntType:
			// int and long
			setLong1((long) getInt1());
			return kLongType;
		    case kFloatType:
			// float and long
			setFloat2((float) getLong2());
			return kFloatType;
		    case kDoubleType:
			// double and long
			setDouble2((double) getLong2());
			return kDoubleType;
		    }
		    break;
		case kFloatType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and float
			setFloat1((float) getByte1());
			return kFloatType;
		    case kCharType:
			// char and float
			setFloat1((float) getChar1());
			return kFloatType;
		    case kShortType:
			// short and float
			setFloat1((float) getShort1());
			return kFloatType;
		    case kIntType:
			// int and float
			setFloat1((float) getInt1());
			return kFloatType;
		    case kLongType:
			// long and float
			setFloat1((float) getLong1());
			return kFloatType;
		    case kDoubleType:
			// double and float
			setDouble2((double) getFloat2());
			return kDoubleType;
		    }
		    break;
		case kDoubleType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and double
			setDouble1((double) getByte1());
			return kDoubleType;
		    case kCharType:
			// char and double
			setDouble1((double) getChar1());
			return kDoubleType;
		    case kShortType:
			// short and double
			setDouble1((double) getShort1());
			return kDoubleType;
		    case kIntType:
			// int and double
			setDouble1((double) getInt1());
			return kDoubleType;
		    case kLongType:
			// long and double
			setDouble1((double) getLong1());
			return kDoubleType;
		    case kFloatType:
			// float and double
			setDouble1((double) getFloat1());
			return kDoubleType;
		    }
		    break;
		}
	    }

	    else if(isFirstArgPrimitiveComparable() &&
		    isSecondArgPrimitiveComparable()) {
		switch(getSecondArgType()) {
		case kBooleanObjectType:
		    return kNullType;
		case kByteObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kCharObjectType:
			// Character and Byte
			setChar1(((Character) getObject1()).charValue());
			setChar2((char) ((Byte) getObject2()).byteValue());
			return kCharType;
		    case kShortObjectType:
			// Short and Byte
			setShort1(((Short) getObject1()).shortValue());
			setShort2((short) ((Byte) getObject2()).byteValue());
			return kShortType;
		    case kIntObjectType:
			// Integer and Byte
			setInt1(((Integer) getObject1()).intValue());
			setInt2((int) ((Byte) getObject2()).byteValue());
			return kIntType;
		    case kLongObjectType:
			// Long and Byte
			setLong1(((Long) getObject1()).longValue());
			setLong2((long) ((Byte) getObject2()).byteValue());
			return kLongType;
		    case kFloatObjectType:
			// Float and Byte
			setFloat1(((Float) getObject1()).floatValue());
			setFloat2((float) ((Byte) getObject2()).byteValue());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and Byte
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) ((Byte) getObject2()).byteValue());
			return kDoubleType;
		    }
		    break;
		case kCharObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and Character
			setChar1((char) ((Byte) getObject1()).byteValue());
			setChar2(((Character) getObject2()).charValue());
			return kCharType;
		    case kShortObjectType:
			// Short and Character
			setShort1(((Short) getObject1()).shortValue());
			setShort2((short) ((Character) getObject2()).charValue());
			return kShortType;
		    case kIntObjectType:
			// Integer and Character
			setInt1(((Integer) getObject1()).intValue());
			setInt2((int) ((Character) getObject2()).charValue());
			return kIntType;
		    case kLongObjectType:
			// Long and Character
			setLong1(((Long)  getObject1()).longValue());
			setLong2((long) ((Character) getObject2()).charValue());
			return kLongType;
		    case kFloatObjectType:
			// Float and Character
			setFloat1(((Float) getObject1()).floatValue());
			setFloat2((float) ((Character) getObject2()).charValue());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and Character
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) ((Character) getObject2()).charValue());
			return kDoubleType;
		    }
		    break;
		case kShortObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and Short
			setShort1((short) ((Byte) getObject1()).byteValue());
			setShort2(((Short) getObject2()).shortValue());
			return kShortType;
		    case kCharObjectType:
			// Character and Short
			setShort1((short) ((Character) getObject1()).charValue());
			setShort2(((Short) getObject2()).shortValue());
			return kShortType;
		    case kIntObjectType:
			// Integer and Short
			setInt1(((Integer) getObject1()).intValue());
			setInt2((int) ((Short) getObject2()).shortValue());
			return kIntType;
		    case kLongObjectType:
			// Long and Short
			setLong1(((Long) getObject1()).longValue());
			setLong2((long) ((Short) getObject2()).shortValue());
			return kLongType;
		    case kFloatObjectType:
			// Float and Short
			setFloat1(((Float) getObject1()).floatValue());
			setFloat2((float) ((Short) getObject2()).shortValue());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and Short
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) ((Short) getObject2()).shortValue());
			return kDoubleType;
		    }
		    break;
		case kIntObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and Integer
			setInt1((int) ((Byte) getObject1()).byteValue());
			setInt2(((Integer) getObject2()).intValue());
			return kIntType;
		    case kCharObjectType:
			// Character and Integer
			setInt1((int) ((Character) getObject1()).charValue());
			setInt2(((Integer) getObject2()).intValue());
			return kIntType;
		    case kShortObjectType:
			// Short and Integer
			setInt1((int) ((Short) getObject1()).shortValue());
			setInt2(((Integer) getObject2()).intValue());
			return kIntType;
		    case kLongObjectType:
			// Long and Integer
			setLong1(((Long) getObject1()).longValue());
			setLong2((long) ((Integer) getObject2()).intValue());
			return kLongType;
		    case kFloatObjectType:
			// Float and Integer
			setFloat1(((Float) getObject1()).floatValue());
			setFloat2((float) ((Integer) getObject2()).intValue());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and Integer
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) ((Integer) getObject2()).intValue());
			return kDoubleType;
		    }
		    break;
		case kLongObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and Long
			setLong1((long) ((Byte) getObject1()).byteValue());
			setLong2(((Long) getObject2()).longValue());
			return kLongType;
		    case kCharObjectType:
			// Character and Long
			setLong1((long) ((Character) getObject1()).charValue());
			setLong2(((Long) getObject2()).longValue());
			return kLongType;
		    case kShortObjectType:
			// Short and Long
			setLong1((long) ((Short) getObject1()).shortValue());
			setLong2(((Long) getObject2()).longValue());
			return kLongType;
		    case kIntObjectType:
			// Integer and Long
			setLong1((long) ((Integer) getObject1()).intValue());
			setLong2(((Long) getObject2()).longValue());
			return kLongType;
		    case kFloatObjectType:
			// Float and Long
			setFloat1(((Float) getObject1()).floatValue());
			setFloat2((float) ((Long) getObject2()).longValue());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and Long
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) ((Long) getObject2()).longValue());
			return kDoubleType;
		    }
		    break;
		case kFloatObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and Float
			setFloat1((float) ((Byte) getObject1()).byteValue());
			setFloat2(((Float) getObject2()).floatValue());
			return kFloatType;
		    case kCharObjectType:
			// Character and Float
			setFloat1((float) ((Character) getObject1()).charValue());
			setFloat2(((Float) getObject2()).floatValue());
			return kFloatType;
		    case kShortObjectType:
			// Short and Float
			setFloat1((float) ((Short) getObject1()).shortValue());
			setFloat2(((Float) getObject2()).floatValue());
			return kFloatType;
		    case kIntObjectType:
			// Integer and Float
			setFloat1((float) ((Integer) getObject1()).intValue());
			setFloat2(((Float) getObject2()).floatValue());
			return kFloatType;
		    case kLongObjectType:
			// Long and Float
			setFloat1((float) ((Long) getObject1()).longValue());
			setFloat2(((Float) getObject2()).floatValue());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and Float
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) ((Float) getObject2()).floatValue());
			return kDoubleType;
		    }
		    break;
		case kDoubleObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and Double
			setDouble1((double) ((Byte) getObject1()).byteValue());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    case kCharObjectType:
			// Character and Double
			setDouble1((double) ((Character) getObject1()).charValue());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    case kShortObjectType:
			// Short and Double
			setDouble1((double) ((Short) getObject1()).shortValue());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    case kIntObjectType:
			// Integer and Double
			setDouble1((double) ((Integer) getObject1()).intValue());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    case kLongObjectType:
			// Long and Double
			setDouble1((double) ((Long) getObject1()).longValue());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    case kFloatObjectType:
			// Float and Double
			setDouble1((double) ((Float) getObject1()).floatValue());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    }
		    break;
		}
	    }
	    else if(isFirstArgPrimitive() &&
		    isSecondArgPrimitiveComparable()) {
		switch(getSecondArgType()) {
		case kBooleanObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			// boolean and Boolean
			setBoolean2(((Boolean) getObject2()).booleanValue());
			return kBooleanType;
		    }
		    break;
		case kByteObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and Byte
			setByte2(((Byte) getObject2()).byteValue());
			return kByteType;
		    case kCharType:
			// char and Byte
			setChar2((char) ((Byte) getObject2()).byteValue());
			return kCharType;
		    case kShortType:
			// short and Byte
			setShort2((short) ((Byte) getObject2()).byteValue());
			return kShortType;
		    case kIntType:
			// int and Byte
			setInt2((int) ((Byte) getObject2()).byteValue());
			return kIntType;
		    case kLongType:
			// long and Byte
			setLong2((long) ((Byte) getObject2()).byteValue());
			return kLongType;
		    case kFloatType:
			// float and Byte
			setFloat2((float) ((Byte) getObject2()).byteValue());
			return kFloatType;
		    case kDoubleType:
			// double and Byte
			setDouble2((double) ((Byte) getObject2()).byteValue());
			return kDoubleType;
		    }
		    break;
		case kCharObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and Character
			setChar1((char) getByte1());
			setChar2(((Character) getObject2()).charValue());
			return kCharType;
		    case kCharType:
			// char and Character
			setChar2(((Character) getObject2()).charValue());
			return kCharType;
		    case kShortType:
			// short and Character
			setShort2((short) ((Character) getObject2()).charValue());
			return kShortType;
		    case kIntType:
			// int and Character
			setInt2((int) ((Character) getObject2()).charValue());
			return kIntType;
		    case kLongType:
			// long and Character
			setLong2((long) ((Character) getObject2()).charValue());
			return kLongType;
		    case kFloatType:
			// float and Character
			setFloat2((float) ((Character) getObject2()).charValue());
			return kFloatType;
		    case kDoubleType:
			// double and Character
			setDouble2((double) ((Character) getObject2()).charValue());
			return kDoubleType;
		    }
		    break;
		case kShortObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and Short
			setShort1((short) getByte1());
			setShort2(((Short) getObject2()).shortValue());
			return kShortType;
		    case kCharType:
			// char and Short
			setShort1((short) getChar1());
			setShort2(((Short) getObject2()).shortValue());
			return kShortType;
		    case kShortType:
			// short and Short
			setShort2(((Short) getObject2()).shortValue());
			return kShortType;
		    case kIntType:
			// int and Short
			setInt2((int) ((Short) getObject2()).shortValue());
			return kIntType;
		    case kLongType:
			// long and Short
			setLong2((long) ((Short) getObject2()).shortValue());
			return kLongType;
		    case kFloatType:
			// float and Short
			setFloat2((float) ((Short) getObject2()).shortValue());
			return kFloatType;
		    case kDoubleType:
			// double and Short
			setDouble2((double) ((Short) getObject2()).shortValue());
			return kDoubleType;
		    }
		    break;
		case kIntObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and Integer
			setInt1((int) getByte1());
			setInt2(((Integer) getObject2()).intValue());
			return kIntType;
		    case kCharType:
			// char and Integer
			setInt1((int) getChar1());
			setInt2(((Integer) getObject2()).intValue());
			return kIntType;
		    case kShortType:
			// short and Integer
			setInt1((int) getShort1());
			setInt2(((Integer) getObject2()).intValue());
			return kIntType;
		    case kIntType:
			// int and Integer
			setInt2(((Integer) getObject2()).intValue());
			return kIntType;
		    case kLongType:
			// long and Integer
			setLong2((long) ((Integer) getObject2()).intValue());
			return kLongType;
		    case kFloatType:
			// float and Integer
			setFloat2((float) ((Integer) getObject2()).intValue());
			return kFloatType;
		    case kDoubleType:
			// double and Integer
			setDouble2((double) ((Integer) getObject2()).intValue());
			return kDoubleType;
		    }
		    break;
		case kLongObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and Long
			setLong1((long) getByte1());
			setLong2(((Long) getObject2()).longValue());
			return kLongType;
		    case kCharType:
			// char and Long
			setLong1((long) getChar1());
			setLong2(((Long) getObject2()).longValue());
			return kLongType;
		    case kShortType:
			// short and Long
			setLong1((long) getShort1());
			setLong2(((Long) getObject2()).longValue());
			return kLongType;
		    case kIntType:
			// int and Long
			setLong1((long) getInt1());
			setLong2(((Long) getObject2()).longValue());
			return kLongType;
		    case kLongType:
			// long and Long
			setLong2(((Long) getObject2()).longValue());
			return kLongType;
		    case kFloatType:
			// float and Long
			setFloat2((float) ((Long) getObject2()).longValue());
			return kFloatType;
		    case kDoubleType:
			// double and Long
			setDouble2((double) ((Long) getObject2()).longValue());
			return kDoubleType;
		    }
		case kFloatObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and Float
			setFloat1((float) getByte1());
			setFloat2(((Float) getObject2()).floatValue());
			return kFloatType;
		    case kCharType:
			// char and Float
			setFloat1((float) getChar1());
			setFloat2(((Float) getObject2()).floatValue());
			return kFloatType;
		    case kShortType:
			// short and Float
			setFloat1((float) getShort1());
			setFloat2(((Float) getObject2()).floatValue());
			return kFloatType;
		    case kIntType:
			// int and Float
			setFloat1((float) getInt1());
			setFloat2(((Float) getObject2()).floatValue());
			return kFloatType;
		    case kLongType:
			// long and Float
			setFloat1((float) getLong1());
			setFloat2(((Float) getObject2()).floatValue());
			return kFloatType;
		    case kFloatType:
			// float and Float
			setFloat2(((Float) getObject2()).floatValue());
			return kFloatType;
		    case kDoubleType:
			// double and Float
			setDouble2((double) ((Float) getObject2()).floatValue());
			return kDoubleType;
		    }
		case kDoubleObjectType:
		    switch(getFirstArgType()) {
		    case kBooleanType:
			return kNullType;
		    case kByteType:
			// byte and Double
			setDouble1((double) getByte1());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    case kCharType:
			// char and Double
			setDouble1((double) getChar1());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    case kShortType:
			// short and Double
			setDouble1((double) getShort1());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    case kIntType:
			// int and Double
			setDouble1((double) getInt1());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    case kLongType:
			// long and Double
			setDouble1((double) getLong1());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    case kFloatType:
			// float and Double
			setDouble1((double) getFloat1());
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    case kDoubleType:
			// double and Double
			setDouble2(((Double) getObject2()).doubleValue());
			return kDoubleType;
		    }
		}
	    }

	    else if(isFirstArgPrimitiveComparable() &&
		    isSecondArgPrimitive()) {
		switch(getSecondArgType()) {
		case kBooleanType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			// Boolean and boolean
			setBoolean1(((Boolean) getObject1()).booleanValue());
			return kBooleanType;
		    }
		    break;
		case kByteType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and byte
			setByte1(((Byte) getObject1()).byteValue());
			return kByteType;
		    case kCharObjectType:
			// Character and byte
			setChar1(((Character) getObject1()).charValue());
			setChar2((char) getByte2());
			return kCharType;
		    case kShortObjectType:
			// Short and byte
			setShort1(((Short) getObject1()).shortValue());
			setShort2((short) getByte2());
			return kShortType;
		    case kIntObjectType:
			// Int and byte
			setInt1(((Integer) getObject1()).intValue());
			setInt2((int) getByte2());
			return kIntType;
		    case kLongObjectType:
			// Long and byte
			setLong1(((Long) getObject1()).longValue());
			setLong2((long) getByte2());
			return kLongType;
		    case kFloatObjectType:
			// Float and byte
			setFloat1(((Float) getObject1()).floatValue());
			setFloat2((float) getByte2());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and byte
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) getByte2());
			return kDoubleType;
		    }
		    break;
		case kCharType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and char
			setChar1((char) ((Byte) getObject1()).byteValue());
			return kCharType;
		    case kCharObjectType:
			// Character and char
			setChar1(((Character) getObject1()).charValue());
			return kCharType;
		    case kShortObjectType:
			// Short and char
			setShort1(((Short) getObject1()).shortValue());
			setShort2((short) getChar2());
			return kShortType;
		    case kIntObjectType:
			// Integer and char
			setInt1(((Integer) getObject1()).intValue());
			setInt2((int) getChar2());
			return kIntType;
		    case kLongObjectType:
			// Long and char
			setLong1(((Long) getObject1()).longValue());
			setLong2((long) getChar2());
			return kLongType;
		    case kFloatObjectType:
			// Float and char
			setFloat1(((Float) getObject1()).floatValue());
			setFloat2((float) getChar2());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and char
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) getChar2());
			return kDoubleType;
		    }
		    break;
		case kShortType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and short
			setShort1((short) ((Byte) getObject1()).byteValue());
			return kShortType;
		    case kCharObjectType:
			// Character and short
			setShort1((short) ((Character) getObject1()).charValue());
			return kShortType;
		    case kShortObjectType:
			// Short and short
			setShort1(((Short) getObject1()).shortValue());
			return kShortType;
		    case kIntObjectType:
			// Integer and short
			setInt1(((Integer) getObject1()).intValue());
			setInt2((int) getShort2());
			return kIntType;
		    case kLongObjectType:
			// Long and short
			setLong1(((Long) getObject1()).longValue());
			setLong2((long) getShort2());
			return kLongType;
		    case kFloatObjectType:
			// Float and short
			setFloat1(((Float) getObject1()).floatValue());
			setFloat2((short) getShort2());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and short
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) getShort2());
			return kDoubleType;
		    }
		    break;
		case kIntType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and int
			setInt1((int) ((Byte) getObject1()).byteValue());
			return kIntType;
		    case kCharObjectType:
			// Character and int
			setInt1((int) ((Character) getObject1()).charValue());
			return kIntType;
		    case kShortObjectType:
			// Short and int
			setInt1((int) ((Short) getObject1()).shortValue());
			return kIntType;
		    case kIntObjectType:
			// Integer and int
			setInt1(((Integer) getObject1()).intValue());
			return kIntType;
		    case kLongObjectType:
			// Long and int
			setLong1(((Long) getObject1()).longValue());
			setLong2((long) getInt2());
			return kLongType;
		    case kFloatObjectType:
			// Float and int
			setFloat1(((Float) getObject1()).floatValue());
			setFloat2((float) getInt2());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and int
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) getInt2());
			return kDoubleType;
		    }
		    break;
		case kLongType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and long
			setLong1((long) ((Byte) getObject1()).byteValue());
			return kLongType;
		    case kCharObjectType:
			// Character and long
			setLong1((long) ((Character) getObject1()).charValue());
			return kLongType;
		    case kShortObjectType:
			// Short and long
			setLong1((long) ((Short) getObject1()).shortValue());
			return kLongType;
		    case kIntObjectType:
			// Integer and long
			setLong1((long) ((Integer) getObject1()).intValue());
			return kLongType;
		    case kLongObjectType:
			// Long and long
			setLong1(((Long) getObject1()).longValue());
			return kLongType;
		    case kFloatObjectType:
			// Float and long
			setFloat1(((Float) getObject1()).floatValue());
			setFloat2((float) getLong2());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and long
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) getLong2());
			return kDoubleType;
		    }
		case kFloatType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and float
			setFloat1((float) ((Byte) getObject1()).byteValue());
			return kFloatType;
		    case kCharObjectType:
			// Character and float
			setFloat1((float) ((Character) getObject1()).charValue());
			return kFloatType;
		    case kShortObjectType:
			// Short and float
			setFloat1((float) ((Short) getObject1()).shortValue());
			return kFloatType;
		    case kIntObjectType:
			// Integer and float
			setFloat1((float) ((Integer) getObject1()).intValue());
			return kFloatType;
		    case kLongObjectType:
			// Long and float
			setFloat1((float) ((Long) getObject1()).longValue());
			return kFloatType;
		    case kFloatObjectType:
			// Float and float
			setFloat1(((Float) getObject1()).floatValue());
			return kFloatType;
		    case kDoubleObjectType:
			// Double and float
			setDouble1(((Double) getObject1()).doubleValue());
			setDouble2((double) getFloat2());
			return kDoubleType;
		    }
		case kDoubleType:
		    switch(getFirstArgType()) {
		    case kBooleanObjectType:
			return kNullType;
		    case kByteObjectType:
			// Byte and double
			setDouble1((double) ((Byte) getObject1()).byteValue());
			return kDoubleType;
		    case kCharObjectType:
			// Character and double
			setDouble1((double) ((Character) getObject1()).charValue());
			return kDoubleType;
		    case kShortObjectType:
			// Short and double
			setDouble1((double) ((Short) getObject1()).shortValue());
			return kDoubleType;
		    case kIntObjectType:
			// Integer and double
			setDouble1((double) ((Integer) getObject1()).intValue());
			return kDoubleType;
		    case kLongObjectType:
			// Long and double
			setDouble1((double) ((Long) getObject1()).longValue());
			return kDoubleType;
		    case kFloatObjectType:
			// Float and double
			setDouble1((double) ((Float) getObject1()).floatValue());
			return kDoubleType;
		    case kDoubleObjectType:
			// Double and double
			setDouble1(((Double) getObject1()).doubleValue());
			return kDoubleType;
		    }
		}
	    }
	}

	return kNullType;
    }

    //----------------------------------------
    // GenericTag methods
    //----------------------------------------

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();

	cleanup();
    }
    /**
     * 
     */
    protected void cleanup() {
      setFirstArgType(kNullType);
      setSecondArgType(kNullType);
      setFirstArgPrimitive(false);
      setSecondArgPrimitive(false);
      setFirstArgComparable(false);
      setSecondArgComparable(false);
      setFirstArgPrimitiveComparable(false);
      setSecondArgPrimitiveComparable(false);

      setComparator(null);
      mObject1 = null;
      mObject2 = null;
      mBoolean1Set = false;
      mBoolean2Set = false;
      mByte1Set = false;
      mByte2Set = false;
      mChar1Set = false;
      mChar2Set = false;
      mShort1Set = false;
      mShort2Set = false;
      mInt1Set = false;
      mInt2Set = false;
      mLong1Set = false;
      mLong2Set = false;
      mFloat1Set = false;
      mFloat2Set = false;
      mDouble1Set = false;
      mDouble2Set = false;

      mFirstArgCounter = 0;
      mSecondArgCounter = 0;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
