/*<ATGCOPYRIGHT>

 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.portal.gear.xmlprotocol.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A wrapper class for caching byte array contents
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/ByteArrayElementContents.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ByteArrayElementContents implements ElementContents{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/ByteArrayElementContents.java#2 $$Change: 651448 $";


  protected byte[] mContents = null;

  /**
   * Constructors
   */
  public ByteArrayElementContents(){
  }

  /**
   * Constructs a cache wrapper for a byte array
   *
   * @param pContents the byte array to cache
   */
  public ByteArrayElementContents(byte[] pContents){
    this.setContents(pContents);
  }

  /**
   * Constructs a cache wrapper for a byte array from a given ByteArrayInputStream
   *
   * @param pContents the ByteArrayInputStream containing byte contents to cache
   *
   * @exception IOException if there was an error setting the contents
   */
  public ByteArrayElementContents(ByteArrayInputStream pContents) throws IOException{
    this.setContents(pContents);
  }

  /**
   * Returns the contents
   *
   * @returns byte[] the contents
   */

  public synchronized byte[] getContents() {
    try{
      return mContents;
    }catch (RuntimeException e){
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Return the contents as a ByteArrayInputStream
   *
   * @returns ByteArrayInputStream
   */
  public ByteArrayInputStream getContentsAsStream(){
    return new ByteArrayInputStream(mContents);
  }

  /**
   * Set the contents
   *
   * @param pContents the contents as a byte[]
   */
  public synchronized void setContents(byte[] pContents){
    try{
      mContents = pContents;
    }catch (Exception e){
      e.printStackTrace();
      mContents = null;
    }
  }

  /**
   * Set the contents
   *
   * @parameters pContents the contents as a ByteArrayInputStream
   */
  public synchronized void setContents(ByteArrayInputStream pContents) throws IOException{
    ByteArrayOutputStream out = null;
    try{
      out = new ByteArrayOutputStream();
      this.copyStream(pContents, out);
      mContents = out.toByteArray();
    }finally{
      if (out != null){
        out.close();
      }
    }
  }


  /**
   * Returns the size of the contents in bytes
   */
  public int getSize(){
    if (mContents != null){
      return mContents.length;
    }
    return 0;
  }

  /**
   * Utility method for copying a stream
   */
  private void copyStream(InputStream in, OutputStream out) throws IOException{
    synchronized (in){
      synchronized (out){
        byte[] buffer = new byte[256];
        while (true){
          int bytesRead = in.read(buffer);
          if (bytesRead == -1){
            break;
          }
          out.write(buffer,0,bytesRead);
        }
      }
    }
  }

}
