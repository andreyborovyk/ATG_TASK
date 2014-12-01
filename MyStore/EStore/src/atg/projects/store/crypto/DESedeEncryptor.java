/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2010 Art Technology Group, Inc. All Rights
 * Reserved. No use, copying or distribution of this work may be made except in
 * accordance with a valid license agreement from Art Technology Group. This
 * notice must be included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/
package atg.projects.store.crypto;

import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;

import org.apache.commons.codec.binary.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * A simple class for performing encryption/decryption operations using the
 * javax.crypto package.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/crypto/DESedeEncryptor.java#3 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class DESedeEncryptor extends AbstractEncryptor {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/crypto/DESedeEncryptor.java#3 $$Change: 635816 $";


  /**
   * Class vertion string.
   */
  
  //-------------------------------------
  // Constants.
  //-------------------------------------

  /** Logger. */
  public static ApplicationLogging mLogger = ClassLoggingFactory.getFactory().getLoggerForClass(DESedeEncryptor.class);

  /**
   * Algorithm name - DES-EDE ("triple-DES").
   */
  private static final String ALGORITHM = "DESede";

  /**
   * 24 byte key string for use with DESede.
   */
  private static final String KEY = "2kCo5j0ca#L.0ije@~l;S!*2";

  /**
   * Encoder.
   */
  private transient static Base64 mEncoder = new Base64();

  /**
   * Key string.
   */
  private byte[] mKeyString = null;

  /**
   * Key specification.
   */
  private transient DESedeKeySpec mKeySpec = null;

  /**
   * Secret key.
   */
  private transient SecretKey mKey = null;

  /**
   * Encrypt cipher.
   */
  private transient javax.crypto.Cipher mEncryptCypher = null;

  /**
   * Decrypt cipher.
   */
  private transient javax.crypto.Cipher mDecryptCypher = null;

  /**
   * Security providers.
   */
  private List mSecurityProviders = null;

  //-------------------------------------
  // Constructors.
  //-------------------------------------

  /**
   * Default constructor.
   */
  public DESedeEncryptor() {
    super();
  }

  //-------------------------------------
  // Methods.
  //-------------------------------------

  /**
   * @return list of security providers.
   */
  public List getSecurityProviders() {
    return mSecurityProviders;
  }

  /**
   * Set the list of Providers from the property.
   * @param pSecurityProviders - security providers list
   */
  public void setSecurityProviders(List pSecurityProviders) {
    mSecurityProviders = pSecurityProviders;
  }

  /**
   * @return the logger.
   */
  private ApplicationLogging getLogger() {
    return mLogger;
  }

  /**
   * This is two way encryption, so encrypt/decrypt keys are the same.
   * @param pValue - value
   * @throws EncryptorException if encryptor error occurs
   */
  protected final void doAcceptEncryptKey(byte[] pValue)
    throws EncryptorException {
    acceptKey(pValue);
  }

  /**
   * This is two way encryption, so encrypt/decrypt keys are the same.
   * @param pValue - value
   * @throws EncryptorException if encryptor error occurs
   */
  protected final void doAcceptDecryptKey(byte[] pValue)
    throws EncryptorException {
    acceptKey(pValue);
  }

  /**
   * Initialize the KeySpec.
   *
   * @param pValue key in byte[]
   * @throws EncryptorException This exception indicates that a severe error
   * occurred while performing a cryptography operation.
   */
  protected final void acceptKey(byte[] pValue) throws EncryptorException {
    mKeyString = pValue;
  }

  /**
   * Initialize DESedeEncryptor.
   *
   * @throws EncryptorException This exception indicates that a severe error
   * occurred while performing a cryptography operation.
   */
  protected final void doInit() throws EncryptorException {
    try {
      addSecurityProviders();

      if (mKeyString == null) {
        mKeyString = KEY.getBytes();
      }

      mKeySpec = new DESedeKeySpec(mKeyString);
      mKey = new SecretKeySpec(mKeySpec.getKey(), ALGORITHM);
      mEncryptCypher = javax.crypto.Cipher.getInstance(ALGORITHM);
      mEncryptCypher.init(javax.crypto.Cipher.ENCRYPT_MODE, mKey);
      mDecryptCypher = javax.crypto.Cipher.getInstance(ALGORITHM);
      mDecryptCypher.init(javax.crypto.Cipher.DECRYPT_MODE, mKey);
    } catch (NoSuchAlgorithmException nsae) {
      throw new EncryptorException("Failed to initialize encryptor: ", nsae);
    } catch (NoSuchPaddingException nspe) {
      throw new EncryptorException("Failed to initialize encryptor: ", nspe);
    } catch (InvalidKeyException nske) {
      throw new EncryptorException("Failed to initialize encryptor: ", nske);
    }
  }

  /**
   * {@inheritDoc}
   */
  protected final byte[] doEncrypt(byte[] pValue) throws EncryptorException {
    try {
      return mEncryptCypher.doFinal(pValue);
    } catch (IllegalBlockSizeException ibse) {
      throw new EncryptorException("Failed to encrypt: ", ibse);
    } catch (BadPaddingException bpe) {
      throw new EncryptorException("Failed to encrypt: ", bpe);
    }
  }

  /**
   * {@inheritDoc}
   */
  protected final byte[] doDecrypt(byte[] pValue) throws EncryptorException {
    try {
      return mDecryptCypher.doFinal(pValue);
    } catch (IllegalBlockSizeException ibse) {
      throw new EncryptorException("Failed to decrypt: ", ibse);
    } catch (BadPaddingException bpe) {
      throw new EncryptorException("Failed to decrypt: ", bpe);
    }
  }

  /**
   * Once encrypted, string data may no longer be a string because the
   * encrypted data is binary and may contain null characters, thus it may
   * need to be encoded using a encoder such as Base64, UUEncode (ASCII only)
   * or UCEncode(ASCII independent).
   * @param pValue - value
   * @throws EncryptorException This exception indicates that an error
   * occurred while performing a cryptography operation.
   * @return encoded data
   */
  protected String encodeToString(byte[] pValue) throws EncryptorException {
    return new String(mEncoder.encode(pValue));
  }

  /**
   * Decode <code>pValue</code> into array of bytes.
   *
   * @param pValue decoded string
   * @return decoded array of bytes
   * @throws EncryptorException This exception indicates that a severe error
   * occurred while performing a cryptography operation.
   */
  protected byte[] decodeToByteArray(String pValue) throws EncryptorException {
    return mEncoder.decode(pValue.getBytes());
  }

  /**
   * This method add's new security provider
   * at the top of the list of exsisting providers.
  */
  protected void addSecurityProviders() {
    int position = 1;
    List securityProviders = getSecurityProviders();

    if (securityProviders != null) {
      Iterator securityProvidersIter = securityProviders.iterator();

      String securityProvider = null ;
      while (securityProvidersIter.hasNext()) {
        try {
          securityProvider = (String) securityProvidersIter.next();
          Class providerClass = Class.forName(securityProvider);
          Provider provider = (Provider) providerClass.newInstance();
          Security.insertProviderAt(provider, position);
          position++;
        } catch (InstantiationException inste) {
          if (getLogger().isLoggingWarning()) {
            getLogger().logWarning("Unable to add provider: " + securityProvider + ".Proceeding with default settings.");
          }
        } catch (IllegalAccessException iae) {
          if (getLogger().isLoggingWarning()) {
            getLogger().logWarning("Unable to add provider: " + securityProvider + ".Proceeding with default settings.");
          }
        } catch (ClassNotFoundException cnfe) {
          if (getLogger().isLoggingWarning()) {
            getLogger().logWarning("Unable to add provider: " + securityProvider + ".Proceeding with default settings.");
          }
        }
      }
    }
  }
}
