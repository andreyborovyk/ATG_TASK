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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.repository;

import atg.security.*;
import java.security.*;
import atg.core.util.Base64;

/**
 *
 * <p>This is a simple property descriptor which you set with a plain
 * text password value.  It hashes this value and sets another
 * specified property in the same item.  If this property is read, it
 * will just return the value of that other property, which will
 * presumably contain the hashed value.  This means that the value
 * read from this property will be different from the value written
 * into this property.
 *
 * <p>You must pass to it either an atg.security.LoginUserAuthority as
 * a userAuthority attribute, or you can pass the algorithm attribute
 * which specifies an algorithm to use for
 * java.security.MessageDigest.  If you use the algorithm attribute,
 * you can also set the useBase64 to false if you don't want the byte
 * array converted to a base64 string.  In this case, your
 * hashedProperty will need to be specified as a binary type so that
 * it can accept a byte[] value.
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/repository/PasswordPropertyDescriptor.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $*/
public class PasswordPropertyDescriptor extends RepositoryPropertyDescriptor {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/repository/PasswordPropertyDescriptor.java#2 $$Change: 651448 $";

  //-------------------------------------
  /** Attribute names */
  static final String ALGORITHM = "algorithm";
  static final String USER_AUTHORITY = "userAuthority";
  static final String HASHED_PROPERTY = "hashedProperty";
  static final String PASSWORD_HASHER = "passwordHasher";
  static final String USE_BASE64 = "useBase64";
  static final String TYPE_NAME = "password";

  /** Values of these attributes for this particular property */
  transient LoginUserAuthority mUserAuthority = null; 
    PasswordHasher mPasswordHasher = null;
  String mAlgorithm = null;
  String mHashedPropertyName = null;
  boolean mUseBase64 = true;

  static {
    RepositoryPropertyDescriptor.registerPropertyDescriptorClass(TYPE_NAME, 
                                        PasswordPropertyDescriptor.class);
  }

  //-------------------------------------
  /**
   * Constructs a new PasswordPropertyDescriptor
   **/
  public PasswordPropertyDescriptor () {
    super();
  }

  //-------------------------------------
  /**
   * Constructs a new PasswordPropertyDescriptor for a particular property.
   **/
  public PasswordPropertyDescriptor(String pPropertyName)
  {
    super(pPropertyName);
  }

  //-------------------------------------
  /**
   * Constructs a new RepositoryPropertyDescriptor with the given 
   * property name, property type, and short description.
   **/
  public PasswordPropertyDescriptor(String pPropertyName, 
				      Class pPropertyType,
				      String pShortDescription)
  {
    super(pPropertyName, pPropertyType, pShortDescription);
  }

  //-------------------------------------
  /**
   * Returns property Queryable
   **/
  public boolean isQueryable() {
    return false;
  }

  //-------------------------------------
  /**
   * Sets the property of this type for the item descriptor provided.  
   */
  public void setPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    if (mHashedPropertyName == null)
      throw new IllegalArgumentException("password property must be configured with a hashedPassword attribute:" + 
              getItemDescriptor().getItemDescriptorName() + "." + getName());
    
    Object hashedValue;
    if (mUserAuthority != null) {
      hashedValue = mUserAuthority.getPasswordHasher().encryptPassword(pValue.toString());
    }
    else if(mPasswordHasher != null) {
	hashedValue = mPasswordHasher.encryptPassword(pValue.toString());
    }
    else {
      if (mAlgorithm == null)
        mAlgorithm = "MD5";
      try {
        MessageDigest digest = MessageDigest.getInstance(mAlgorithm);
        byte [] hashBytes = digest.digest(((String) pValue).getBytes());
        if (mUseBase64) 
          hashedValue = Base64.encodeToString(hashBytes);
        else
          hashedValue = hashBytes;
      }
      // should have caught this as startup...
      catch (NoSuchAlgorithmException exc) {
        throw new IllegalArgumentException(exc.toString());
      }
    }
      
    pItem.setPropertyValue(mHashedPropertyName, hashedValue);
  }

  //-------------------------------------
  /**
   *
   * Returns the value of the underlying property.
   **/
  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue)
  {
    return pItem.getPropertyValue (mHashedPropertyName);
  }

  //-------------------------------------
  /**
   * Catch the attribute values that we care about and store them in 
   * member variables.
   */
  public void setValue(String pAttributeName, Object pValue) {
    super.setValue(pAttributeName, pValue);

    try {
      if (pValue == null || pAttributeName == null) return;
      if (pAttributeName.equalsIgnoreCase(ALGORITHM)) { 
        mAlgorithm = pValue.toString();
        MessageDigest.getInstance(mAlgorithm); // test out this algorithm...
      }
      else if (pAttributeName.equalsIgnoreCase(USER_AUTHORITY))
        mUserAuthority = (LoginUserAuthority) pValue;
      else if (pAttributeName.equalsIgnoreCase(HASHED_PROPERTY)) {
        mHashedPropertyName = pValue.toString();
      }
      else if( pAttributeName.equalsIgnoreCase(PASSWORD_HASHER)) {
	  mPasswordHasher = (PasswordHasher) pValue;
      }
      else if (pAttributeName.equalsIgnoreCase(USE_BASE64))
        mUseBase64 = pValue.toString().trim().equalsIgnoreCase("true");
    }
    catch (ClassCastException exc) {
      logError("Invalid type for password hasher property: " + exc);
    }
    catch (NoSuchAlgorithmException exc) {
      logError("Invalid value for algorithm attribute: " + exc);
    }
    if (mAlgorithm != null && mUserAuthority != null)
      logError("Specify only one of algorithm or passwordHasher arguments");
  }

  //------------------------------------
  /**
   * Logs an error for the repository we are part of.
   */
  void logError(String pError) {
    RepositoryImpl ri = (RepositoryImpl) getItemDescriptor().getRepository();
    if (ri.isLoggingError()) 
      ri.logError("Improperly configured property: " + getName() + 
                  " item-descriptor " + 
                  getItemDescriptor().getItemDescriptorName() + ": " + pError);
  }

  /**
   * Returns the name this type uses in the XML file.
   */
  public String getTypeName() {
    return TYPE_NAME;
  }

  public Class getPropertyType() {
    return java.lang.String.class;
  }

  /**
   * Perform type checking.
   */
  public void setPropertyType(Class pClass) {
    if (pClass != java.lang.String.class)
      throw new IllegalArgumentException("password properties must be java.lang.String");
    super.setPropertyType(pClass);
  }
  public void setComponentPropertyType(Class pClass) {
    // SEE BUG85618 throw new IllegalArgumentException("password properties must be scalars");
  }
  public void setPropertyItemDescriptor(RepositoryItemDescriptor pDesc) {
    throw new IllegalArgumentException("password properties must be java.lang.String");
  }
  public void setComponentItemDescriptor(RepositoryItemDescriptor pDesc) {
    throw new IllegalArgumentException("password properties must be scalars");
  }

}
