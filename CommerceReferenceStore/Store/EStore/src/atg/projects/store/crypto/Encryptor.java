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


/**
 * A simple interface for performing encryption/decryption operations.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/crypto/Encryptor.java#2 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface Encryptor {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/crypto/Encryptor.java#2 $$Change: 651448 $";

  /**
   * Class version string.
   */
  
  //-------------------------------------
  // Methods.
  //-------------------------------------

  /**
   * Sets the <code>pValue</code> property. This must get called before any
   * encryption operations take place.
   *
   * @param pValue The <code>byte[]</code> to set as the key property.
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  public void acceptEncryptKey(byte[] pValue) throws EncryptorException;

  /**
   * Sets the <code>key</code> property. This must get called before any
   * encryption operations take place.
   *
   * @param pValue The <code>byte[]</code> to set as the key property.
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  public void acceptDecryptKey(byte[] pValue) throws EncryptorException;

  /**
   * Encrypts <code>pValue</code> string.
   *
   * @param pValue String to encrypt.
   * @return Encrypted string.
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  public String encrypt(String pValue) throws EncryptorException;

  /**
   * Encrypts <code>pValue</code> byte[].
   *
   * @param pValue byte[] array to encrypt.
   * @return encrypted byte[] array
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  public byte[] encrypt(byte[] pValue) throws EncryptorException;

  /**
   * Decrypts encrypted <code>pValue</code> string.
   *
   * @param pValue encrypted String
   * @return decrypted String
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  public String decrypt(String pValue) throws EncryptorException;

  /**
   * Decrypts encrypted <code>pValue</code> byte[].
   *
   * @param pValue encrypted array of byte[]
   * @return decrypted array of byte[]
   * @throws EncryptorException This exception indicates that a severe error
   * occured while performing a cryptograpy operation.
   */
  public byte[] decrypt(byte[] pValue) throws EncryptorException;
}
