/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.repository;

import atg.adapter.gsa.GSAPropertyDescriptor;

import atg.nucleus.Nucleus;

import atg.projects.store.crypto.DESEncryptor;
import atg.projects.store.crypto.Encryptor;
import atg.projects.store.crypto.EncryptorException;
import atg.projects.store.logging.LogUtils;

import atg.repository.RepositoryImpl;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

import java.io.Serializable;


/**
 * A property descriptor to encrypt and decrypt string values.
 * <p>
 * Adapted from atg.repository.PasswordPropertyDescriptor You can pass in a
 * different Encryptor implementation class by adding an attribute tag to your
 * property definition as follows:
 *
 * <pre>
 *
 * <item-descriptor name="images"> <table name="MY_TABLE"
 * id-column-name="ID" type="primary"> <property name="secureProperty"
 * cache-mode="disabled" data-type="string"
 * property-type="atg.repository.EncryptionPropertyDescriptor"> <attribute
 * name="encryptorClass" value="com.mycompany.crypto.MySuperEncryptor"/>
 * </property> </table> </item-descriptor>
 *
 * </pre>
 *
 * <p>
 *
 * @author ATG
 * @version $1.1$
 */
public class EncryptionPropertyDescriptor extends GSAPropertyDescriptor {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/repository/EncryptionPropertyDescriptor.java#2 $$Change: 651448 $";


  /**
   * Class version.
   */
  
  /**
   * Type name.
   */
  public static final String TYPE_NAME = "encrypted";

  /**
   * Encryptor component name.
   */
  public static final String ENCRYPTOR_COMPONENT = "encryptorComponent";

  /**
   * One way encrypt key constant.
   */
  public static final String ENCRYPT_KEY = "encryptKey";

  /**
   * One way decrypt key constant.
   */
  public static final String DECRYPT_KEY = "decryptKey";

  /**
   * Two way key constant.
   */
  public static final String KEY = "key";

  static {
    RepositoryPropertyDescriptor.registerPropertyDescriptorClass(TYPE_NAME, EncryptionPropertyDescriptor.class);
  }

  /**
   * Null object.
   */
  public final static NullObject NULL_OBJECT = new NullObject();

  /**
   * Encryptor.
   */
  protected Encryptor mEncryptor = new DESEncryptor();

  /**
   * Encrypt key.
   */
  private byte[] mEncryptKey = null;

  /**
   * Decrypt key.
   */
  private byte[] mDecryptKey = null;

  //-------------------------------------
  // Constructors
  //-------------------------------------
  /**
   * Constructor.
   */
  public EncryptionPropertyDescriptor() {
    super();
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * @return true if queryable, false - otherwise.
   */
  public boolean isQueryable() {
    return false;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * Sets the property of this type for the item descriptor provided.
   * @param pItem - item to set value in
   * @param pValue - value to set
   */
  public void setPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    if (pValue == null) {
      return;
    }

    try {
      super.setPropertyValue(pItem, mEncryptor.encrypt(pValue.toString()));
    } catch (EncryptorException ee) {
      logError(LogUtils.formatMajor("Failed to encrypt property"), ee);
      throw new IllegalArgumentException("Failed to encrypt property " + getItemDescriptor().getItemDescriptorName());
    }
  }

  /**
   * Get value of the item.
   * @param pItem - item to get value of
   * @param pValue - value name to get
   * @return the value of the underlying property.
   */
  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    // If the object value is null or "the null object", then simply return
    // null.
    if ((pValue == null) || (pValue.equals(RepositoryItemImpl.NULL_OBJECT))) {
      return null;
    }

    try {
      return super.getPropertyValue(pItem, mEncryptor.decrypt(pValue.toString()));
    } catch (EncryptorException ee) {
      logError(LogUtils.formatMajor("Failed to decrypt property"), ee);
      throw new IllegalArgumentException("Failed to decrypt property " + getItemDescriptor().getItemDescriptorName());
    }
  }

  /**
   * Catch the attribute values that we care about and store them in member
   * variables.
   * @param pAttributeName - attribute name
   * @param pValue - value
   */
  public void setValue(String pAttributeName, Object pValue) {
    super.setValue(pAttributeName, pValue);

    if ((pValue == null) || (pAttributeName == null)) {
      return;
    }

    if (pAttributeName.equalsIgnoreCase(ENCRYPTOR_COMPONENT)) {
      resolveComponent(pValue);
    } else if (pAttributeName.equalsIgnoreCase(ENCRYPT_KEY)) {
      setEncryptKey(pValue);
    } else if (pAttributeName.equalsIgnoreCase(DECRYPT_KEY)) {
      setDecryptKey(pValue);
    } else if (pAttributeName.equalsIgnoreCase(KEY)) {
      setKeyAttribute(pValue);
    }
  }

  /**
   * Instantiate encryptor.
   *
   * @param pValue - value
   */
  private void instantiateEncryptor(Object pValue) {
    try {
      mEncryptor = (Encryptor) Class.forName(pValue.toString()).newInstance();

      if (mEncryptKey != null) {
        try {
          mEncryptor.acceptEncryptKey(mEncryptKey);
        } catch (EncryptorException ee) {
          logError(LogUtils.formatMajor("Problem setting encryptKey attribute"), ee);
        }
      }

      if (mDecryptKey != null) {
        try {
          mEncryptor.acceptDecryptKey(mDecryptKey);
        } catch (EncryptorException ee) {
          logError(LogUtils.formatMajor("Problem setting decryptKey attribute"), ee);
        }
      }
    } catch (ClassNotFoundException cnfe) {
      logError(LogUtils.formatMajor("Encryptor class not found"), cnfe);
    } catch (InstantiationException ie) {
      logError(LogUtils.formatMajor("Encryptor class could not be instantiated"), ie);
    } catch (IllegalAccessException iae) {
      logError(LogUtils.formatMajor("Access problem instantiating Encryptor class. Make sure constructor is public"),
        iae);
    } catch (ClassCastException cce) {
      logError("Invalid type for Encryptor class must implement " + "atg.projects.store.crypto.Encryptor: ", cce);
    } catch (ExceptionInInitializerError eiie) {
      logError(LogUtils.formatMajor("Problem instantiating Encryptor class"), eiie);
    } catch (LinkageError le) {
      logError(LogUtils.formatMajor("Problem instantiating Encryptor class"), le);
    }
  }

  /**
  * Resolve the component.
   * @param pValue - component name
  */
  private void resolveComponent(Object pValue) {
    try {
      Nucleus nucleus = Nucleus.getGlobalNucleus();

      if (nucleus != null) {
        mEncryptor = (Encryptor) Nucleus.getGlobalNucleus().resolveName(pValue.toString());

        if (mEncryptKey != null) {
          try {
            mEncryptor.acceptEncryptKey(mEncryptKey);
          } catch (EncryptorException ee) {
            logError(LogUtils.formatMajor("Problem setting encryptKey attribute"), ee);
          }
        }

        if (mDecryptKey != null) {
          try {
            mEncryptor.acceptDecryptKey(mDecryptKey);
          } catch (EncryptorException ee) {
            logError(LogUtils.formatMajor("Problem setting decryptKey attribute"), ee);
          }
        }
      }
    } catch (ClassCastException cce) {
      logError("Invalid type for Encryptor class must implement " + "atg.projects.store.crypto.Encryptor: ", cce);
    }
  }

  /**
   * Set encrypt key.
   *
   * @param pValue - encrypt key
   */
  private void setEncryptKey(Object pValue) {
    try {
      if (mEncryptor == null) {
        mEncryptKey = pValue.toString().getBytes();
      } else {
        mEncryptor.acceptEncryptKey(pValue.toString().getBytes());
      }
    } catch (EncryptorException ee) {
      logError(LogUtils.formatMajor("Could not set encryptKey attribute"), ee);
    }
  }

  /**
   * Set decrypt key.
   *
   * @param pValue - decrypt key
   */
  private void setDecryptKey(Object pValue) {
    try {
      if (mEncryptor == null) {
        mDecryptKey = pValue.toString().getBytes();
      } else {
        mEncryptor.acceptDecryptKey(pValue.toString().getBytes());
      }
    } catch (EncryptorException ee) {
      logError(LogUtils.formatMajor("Could not set decryptKey attribute"), ee);
    }
  }

  /**
   * Set attribute key.
   *
   * @param pValue - value
   */
  private void setKeyAttribute(Object pValue) {
    try {
      if (mEncryptor == null) {
        mEncryptKey = pValue.toString().getBytes();
        mDecryptKey = pValue.toString().getBytes();
      } else {
        mEncryptor.acceptEncryptKey(pValue.toString().getBytes());
        mEncryptor.acceptDecryptKey(pValue.toString().getBytes());
      }
    } catch (EncryptorException ee) {
      logError(LogUtils.formatMajor("Could not set key attribute"), ee);
    }
  }

  /**
   * Logs an error for the repository we are part of.
   * @param pError - error text
   */
  public void logError(String pError) {
    logError(LogUtils.formatMajor(pError), null);
  }

  /**
   * Log error.
   *
   * @param pError - error text
   * @param pThrowable - error object
   */
  void logError(String pError, Throwable pThrowable) {
    if (getItemDescriptor() != null){
      RepositoryImpl ri = (RepositoryImpl) getItemDescriptor().getRepository();
  
      if (ri.isLoggingError()) {
        ri.logError(LogUtils.formatMajor("Error with repository property: " + getName() + " item-descriptor " +
            getItemDescriptor().getItemDescriptorName() + ": " + pError), pThrowable);
      }
    }else{
      pThrowable.printStackTrace();
    }
  }

  /**
   * Log debug.
   *
   * @param pMessage - message
   */
  public void logDebug(String pMessage) {
    if (getItemDescriptor() != null){
      RepositoryImpl ri = (RepositoryImpl) getItemDescriptor().getRepository();
  
      if (ri.isLoggingDebug()) {
        ri.logDebug("Repository property: " + getName() + " item-descriptor " +
          getItemDescriptor().getItemDescriptorName() + ": " + pMessage);
      }
    }
  }

  /**
   * @return the name this type uses in the XML file.
   */
  public String getTypeName() {
    return TYPE_NAME;
  }

  /**
   * @return property type.
   */
  public Class getPropertyType() {
    return java.lang.String.class;
  }

  /**
   * @param pClass - property type.
   */
  public void setPropertyType(Class pClass) {
    if (pClass != java.lang.String.class) {
      throw new IllegalArgumentException("encrypted properties must be java.lang.String");
    }

    super.setPropertyType(pClass);
  }

  /**
   * Set component property type.
   *
   * @param pClass - class
   */
  public void setComponentPropertyType(Class pClass) {
    if (pClass != null) {
      throw new IllegalArgumentException("encrypted properties must be scalars");
    }
  }

  /**
   * Set property item description.
   *
   * @param pDesc - property item description
   */
  public void setPropertyItemDescriptor(RepositoryItemDescriptor pDesc) {
    if (pDesc != null) {
      throw new IllegalArgumentException("encrypted properties must be java.lang.String");
    }
  }

  /**
   * Set component item descriptor.
   *
   * @param pDesc - component item descriptor
   */
  public void setComponentItemDescriptor(RepositoryItemDescriptor pDesc) {
    if (pDesc != null) {
      throw new IllegalArgumentException("encrypted properties must be scalars");
    }
  }

  /** Placeholder that represents a 'null' value in the changes table. */
  static class NullObject implements Serializable {

    /**
     * Represent an object as a string.
     * @return string object representation
     */
    public String toString() {
      return "NULL";
    }
  }
}
