/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc. All Rights
 * Reserved. No use, copying or distribution of this work may be made except in
 * accordance with a valid license agreement from Art Technology Group. This
 * notice must be included on all copies, modifications and derivatives of this
 * work. Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT
 * THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES. "Dynamo" is a trademark of Art Technology
 * Group, Inc.
 </ATGCOPYRIGHT>*/
package atg.projects.store.crypto;

import java.io.Serializable;


/**
 * A simple abstract class for performing encryption/decryption.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/crypto/AbstractEncryptor.java#2 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public abstract class AbstractEncryptor implements Encryptor, Serializable {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/crypto/AbstractEncryptor.java#2 $$Change: 651448 $";


  /**
   * Class version string.
   */
  

  /**
   * Was initialized.
   */
  private transient boolean mInitialized = false;

  /**
   * Monitor.
   */
  private final transient Object mMonitor = new Object();

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Default constructor.
   */
  public AbstractEncryptor() {
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * {@inheritDoc}
   */
  public final void acceptEncryptKey(byte[] pValue) throws EncryptorException {
    synchronized (mMonitor) {
      if (!mInitialized) {
        doAcceptEncryptKey(pValue);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public final void acceptDecryptKey(byte[] pValue) throws EncryptorException {
    synchronized (mMonitor) {
      if (!mInitialized) {
        doAcceptDecryptKey(pValue);
      }
    }
  }

  /**
   * Call this init method before every cryptograpy operation.
   *
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected final void init() throws EncryptorException {
    if (!mInitialized) {
      synchronized (mMonitor) {
        if (!mInitialized) {
          doInit();
          mInitialized = true;
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public final String encrypt(String pValue) throws EncryptorException {
    init();

    return postEncrypt(encodeToString(doEncrypt(preEncrypt(pValue.getBytes()))));
  }

  /**
   * {@inheritDoc}
   */
  public final String decrypt(String pValue) throws EncryptorException {
    init();

    return postDecrypt(new String(doDecrypt(decodeToByteArray(preDecrypt(pValue)))));
  }

  /**
   * {@inheritDoc}
   */
  public final byte[] encrypt(byte[] pValue) throws EncryptorException {
    init();

    return postEncrypt(doEncrypt(preEncrypt(pValue)));
  }

  /**
   * {@inheritDoc}
   */
  public final byte[] decrypt(byte[] pValue) throws EncryptorException {
    init();

    return postDecrypt(doDecrypt(preDecrypt(pValue)));
  }

  /**
   * Process <code>pValue</code> before encrypt.
   *
   * @param pValue String to encrypt
   * @return String after processing
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected String preEncrypt(String pValue) throws EncryptorException {
    return pValue;
  }

  /**
   * Process <code>pValue</code> before decrypt.
   *
   * @param pValue String to decrypt
   * @return String after processing
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected String preDecrypt(String pValue) throws EncryptorException {
    return pValue;
  }

  /**
   * Process <code>pValue</code> after encrypt.
   *
   * @param pValue String after encryption
   * @return String after processing
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected String postEncrypt(String pValue) throws EncryptorException {
    return pValue;
  }

  /**
   * Process <code>pValue</code> after decrypt.
   *
   * @param pValue String after decryption
   * @return String after processing
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected String postDecrypt(String pValue) throws EncryptorException {
    return pValue;
  }

  /**
   * Process <code>pValue</code> before encrypt.
   *
   * @param pValue byte[] to encrypt
   * @return byte[] after processing
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected byte[] preEncrypt(byte[] pValue) throws EncryptorException {
    return pValue;
  }

  /**
   * Process <code>pValue</code> before decrypt.
   *
   * @param pValue byte[] to decrypt
   * @return byte[] after processing
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected byte[] preDecrypt(byte[] pValue) throws EncryptorException {
    return pValue;
  }

  /**
   * Process <code>pValue</code> after encrypt.
   *
   * @param pValue byte[] after encryption
   * @return byte[] after processing
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected byte[] postEncrypt(byte[] pValue) throws EncryptorException {
    return pValue;
  }

  /**
   * Process <code>pValue</code> after decrypt.
   *
   * @param pValue byte[] after decryption
   * @return byte[] after processing
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected byte[] postDecrypt(byte[] pValue) throws EncryptorException {
    return pValue;
  }

  /**
   * Override this method if encoding of the raw encrypted data is necessary.
   *
   * Once encrypted, string data may no longer a string because the encrypted
   * data is binary and may contain null characters, thus it may need to be
   * encoded using a encoder such as Base64, UUEncode (ASCII only) or
   * UCEncode(ASCII independent).
   *
   * @param pValue array of byte[] to encrypt
   * @return resulted string
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected String encodeToString(byte[] pValue) throws EncryptorException {
    return new String(pValue);
  }

  /**
   * Override this method to decode the data back into raw encrypted data if
   * you have used a character encoder.
   *
   * @param pValue string to decode
   * @return resuled byte[]
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected byte[] decodeToByteArray(String pValue) throws EncryptorException {
    return pValue.getBytes();
  }

  //------------------------------------
  // Abstract methods
  //------------------------------------
  /**
   * Implement this to accept a byte array as a key.
   *
   * @param pValue byte array
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected abstract void doAcceptEncryptKey(byte[] pValue)
    throws EncryptorException;

  /**
   * Implement this to accept a byte array as a key.
   *
   * @param pValue byte array
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected abstract void doAcceptDecryptKey(byte[] pValue)
    throws EncryptorException;

  /**
   * Implement this and do your init.
   *
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected abstract void doInit() throws EncryptorException;

  /**
   * Implement this with the actual encryption operation.
   *
   * @param pValue array of bytes
   * @return encrypted array of bytes
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected abstract byte[] doEncrypt(byte[] pValue) throws EncryptorException;

  /**
   * Implement this with the actual decryption operation.
   *
   * @param pValue array of bytes
   * @return decrypted array of bytes
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected abstract byte[] doDecrypt(byte[] pValue) throws EncryptorException;
}
