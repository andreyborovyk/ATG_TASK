/* <ATGCOPYRIGHT>
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

import sun.misc.CharacterDecoder;
import sun.misc.CharacterEncoder;
import sun.misc.UCDecoder;
import sun.misc.UCEncoder;

import java.io.IOException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * A simple class for performing encryption/decryption operations using the
 * javax.crypto package.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/crypto/DESEncryptor.java#2 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DESEncryptor extends AbstractEncryptor {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/crypto/DESEncryptor.java#2 $$Change: 651448 $";


  /**
   * Class version string.
   */
  
  /**
   * Crypto algorithm name.
   */
  protected static final String ALGORITHM = "DES";

  /**
   * 24 byte key string.
   */
  protected static final String KEY = "J9c&W;!n";

  /**
   * Encoder.
   */
  private transient static CharacterEncoder mEncoder = new UCEncoder();

  /**
   * Decoder.
   */
  private transient static CharacterDecoder mDecoder = new UCDecoder();

  /**
   * Key string.
   */
  private byte[] mKeyString = null;

  /**
   * Key specification.
   */
  private transient DESKeySpec mKeySpec = null;

  /**
   * Secret key.
   */
  private transient SecretKey mKey = null;

  /**
   * Encrypt cypher.
   */
  private transient javax.crypto.Cipher mEncryptCypher = null;

  /**
   * Decrypt cypher.
   */
  private transient javax.crypto.Cipher mDecryptCypher = null;

  //-------------------------------------
  // Constructors.
  //-------------------------------------

  /**
   * Default constructor.
   */
  public DESEncryptor() {
    super();
  }

  //-------------------------------------
  // Methods.
  //-------------------------------------

  /**
   * This is two way encryption, so encrypt/decrypt keys are the same.
   * @param pValue - key
   * @throws EncryptorException if encryption error occurs
   */
  protected final void doAcceptEncryptKey(byte[] pValue)
    throws EncryptorException {
    acceptKey(pValue);
  }

  /**
   * This is two way encryption, so encrypt/decrypt keys are the same.
   * @param pValue - key
   * @throws EncryptorException if encryption error occurs
   */
  protected final void doAcceptDecryptKey(byte[] pValue)
    throws EncryptorException {
    acceptKey(pValue);
  }

  /**
   * Initialize the KeySpec.
   *
   * @param pValue new key
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  private void acceptKey(byte[] pValue) throws EncryptorException {
    mKeyString = pValue;
  }

  /**
   * Initialize DESEncrytor.
   *
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected final void doInit() throws EncryptorException {
    try {
      if (mKeyString == null) {
        mKeyString = KEY.getBytes();
      }

      mKeySpec = new DESKeySpec(mKeyString);
      mKey = new SecretKeySpec(mKeySpec.getKey(), ALGORITHM);
      mEncryptCypher = javax.crypto.Cipher.getInstance(ALGORITHM);
      mEncryptCypher.init(javax.crypto.Cipher.ENCRYPT_MODE, mKey);
      mDecryptCypher = javax.crypto.Cipher.getInstance(ALGORITHM);
      mDecryptCypher.init(javax.crypto.Cipher.DECRYPT_MODE, mKey);
    } catch (NoSuchAlgorithmException nsae) {
      throw new EncryptorException(nsae);
    } catch (NoSuchPaddingException nspe) {
      throw new EncryptorException(nspe);
    } catch (InvalidKeyException nske) {
      throw new EncryptorException(nske);
    }
  }

  /**
   * Performs encryption of array of bytes.
   *
   * @param pValue array of bytes to encrypt
   * @return encrypted array of bytes
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected final byte[] doEncrypt(byte[] pValue) throws EncryptorException {
    try {
      return mEncryptCypher.doFinal(pValue);
    } catch (IllegalBlockSizeException ibse) {
      throw new EncryptorException(ibse);
    } catch (BadPaddingException bpe) {
      throw new EncryptorException(bpe);
    }
  }

  /**
   * Performs decription of array of bytes.
   *
   * @param pValue decrypt array of bytes
   * @return decrypted array of bytes
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  protected final byte[] doDecrypt(byte[] pValue) throws EncryptorException {
    try {
      return mDecryptCypher.doFinal(pValue);
    } catch (IllegalBlockSizeException ibse) {
      throw new EncryptorException(ibse);
    } catch (BadPaddingException bpe) {
      throw new EncryptorException(bpe);
    }
  }

  /**
   * Once encrypted, string data may no longer be a string because the
   * encrypted data is binary and may contain null characters, thus it may
   * need to be encoded using a encoder such as Base64, UUEncode (ASCII only)
   * or UCEncode(ASCII independent).
   * @param pValue Value to encode
   * @throws EncryptorException This exception indicates that an error
   * occured while performing a cryptograpy operation.
   * @return Encoded data
   */
  protected String encodeToString(byte[] pValue) throws EncryptorException {
    return mEncoder.encode(pValue);
  }

  /**
   * Decode to byte array.
   *
   * @param pValue - value to decode
   * @return byte array
   * @throws EncryptorException if encryption error occurs
   */
  protected byte[] decodeToByteArray(String pValue) throws EncryptorException {
    try {
      return mDecoder.decodeBuffer(pValue);
    } catch (IOException ioe) {
      throw new EncryptorException("Failed to decode byte array: ", ioe);
    }
  }
} // end of class Encryptor
