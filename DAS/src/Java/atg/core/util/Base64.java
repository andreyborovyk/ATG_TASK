/* -*- Mode: C++; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 2 -*- */
/* 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is the Netscape Portable Runtime (NSPR).
 * 
 * The Initial Developer of the Original Code is Netscape
 * Communications Corporation.  Portions created by Netscape are 
 * Copyright (C) 1998-2000 Netscape Communications Corporation.  All
 * Rights Reserved.
 * 
 * Contributor(s):
 * 
 * Alternatively, the contents of this file may be used under the
 * terms of the GNU General Public License Version 2 or later (the
 * "GPL"), in which case the provisions of the GPL are applicable 
 * instead of those above.  If you wish to allow use of your 
 * version of this file only under the terms of the GPL and not to
 * allow others to use your version of this file under the MPL,
 * indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by
 * the GPL.  If you do not delete the provisions above, a recipient
 * may use your version of this file under either the MPL or the
 * GPL. 
 */

package atg.core.util;

/**
 * Utility classes for encoding and decoding using the Base64 algorithm.
 * 
 * This code is derived from the source for PL_Base64Encode and 
 * PL_Base64Decode, from 
 * http://lxr.mozilla.org/seamonkey/source/nsprpub/lib/libc/src/base64.c
 */
public class Base64 {
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/core/util/Base64.java#2 $$Change: 651448 $";


  static String sBase = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  static byte[] sBaseArray = sBase.getBytes();

  static void encode3to4(byte[] src, int srcIndex, byte[] dest, int destIndex) {
    int b32 = 0;
    int j = 18;
    for (int i=srcIndex; i < srcIndex+3; i++) {
      b32 <<= 8;
      b32 |= src[i] & 0xFF;
    }
    for (int i=destIndex; i < destIndex+4; i++) {
      dest[i] = sBaseArray[ ((b32 >> j) & 0x3F) ];
      j -= 6;
    }
  }

  static void encode2to4(byte[] src, int srcIndex, byte[] dest, int destIndex) {
    dest[destIndex+0] = sBaseArray[ ((src[srcIndex]>>2) & 0x3F) ];
    dest[destIndex+1] = sBaseArray[ (((src[srcIndex] & 0x03) << 4) | 
                                     ((src[srcIndex+1] >> 4) & 0x0F)) ];
    dest[destIndex+2] = sBaseArray[ ((src[srcIndex+1] & 0x0F) << 2) ];
    dest[destIndex+3] = (byte) '=';
  }

  static void encode1to4(byte[] src, int srcIndex, byte[] dest, int destIndex) {
    dest[destIndex+0] = sBaseArray[ ((src[srcIndex]>>2) & 0x3F) ];
    dest[destIndex+1] = sBaseArray[ ((src[srcIndex] & 0x03) << 4) ];
    dest[destIndex+2] = (byte) '=';
    dest[destIndex+3] = (byte) '=';
  }

  static void encode(byte[] src, byte[] dest) {
    int srclen = src.length;
    int srcIndex = 0;
    int destIndex = 0;
    while (srclen >= 3) {
      encode3to4(src, srcIndex, dest, destIndex);
      srcIndex += 3;
      destIndex += 4;
      srclen -= 3;
    }
    switch (srclen) {
    case 2:
      encode2to4(src, srcIndex, dest, destIndex);
      break;
    case 1:
      encode1to4(src, srcIndex, dest, destIndex);
      break;
    case 0:
      break;
    default:
      throw new Error(); // should never occur
    }
  }

  public final static byte[] encodeToByteArray(byte[] pData) {
    byte[] dest = new byte[((pData.length + 2)/3) * 4];
    encode(pData, dest);
    return dest;
  }

  public final static byte[] encodeToByteArray(String pData) {
    if (pData == null)
      return null;
    return encodeToByteArray(pData.getBytes());
  }

  public static String encodeToString(byte[] pData) {
    if (pData == null)
      return null;
    return new String(encodeToByteArray(pData));
  }

  public static String encodeToString(String pData) {
    if (pData == null)
      return null;
    return new String(encodeToByteArray(pData));
  }

  static int codetovalue(byte[] a, int i) {
    byte c = a[i];
    if ((c >= 'A') && (c <= 'Z')) {
      return (c - 'A');
    } else if ((c >= 'a') && (c <= 'z')) {
      return ((c - 'a') + 26);
    } else if ((c >= '0') && (c <= '9')) {
      return ((c - '0') + 52);
    } else if ('+' == c) {
      return 62;
    } else if ('/' == c) {
      return 63;
    } else {
      throw new NumberFormatException("Couldn't decode character " + c +
                                      " ('" + (char)c + "') at offset " + i);
    }
  }

  static void decode4to3(byte[] src, int srcIndex, byte[] dest, int destIndex) {
    int b32 = 0;
    int bits;
    for (int i = 0; i < 4; i++) {
      bits = codetovalue(src, srcIndex+i);
      b32 <<= 6;
      b32 |= bits;
    }
    dest[destIndex+0] = (byte)((b32 >> 16) & 0xFF);
    dest[destIndex+1] = (byte)((b32 >>  8) & 0xFF);
    dest[destIndex+2] = (byte)((b32      ) & 0xFF);
  }

  static void decode3to2(byte[] src, int srcIndex, byte[] dest, int destIndex) {
    int b32 = 0;
    int ubits;
    int bits = codetovalue(src, srcIndex);
    b32 = bits;
    b32 <<= 6;
    bits = codetovalue(src, srcIndex+1);
    b32 |= bits;
    b32 <<= 4;
    bits = codetovalue(src, srcIndex+2);
    ubits = bits;
    b32 |= (ubits >> 2);
    dest[destIndex+0] = (byte)((b32 >> 8) & 0xFF);
    dest[destIndex+1] = (byte)((b32     ) & 0xFF);
  }

  static void decode2to1(byte[] src, int srcIndex, byte[] dest, int destIndex) {
    int bits = codetovalue(src, srcIndex);
    int ubits = bits;
    int b32 = (ubits << 2);
    bits = codetovalue(src, srcIndex+1);
    ubits = bits;
    b32 |= (ubits >> 4);
    dest[destIndex] = (byte)b32;
  }

  static void decode(byte[] src, int srclen, byte[] dest) {
    int srcIndex = 0;
    int destIndex = 0;
    while (srclen >= 4) {
      decode4to3(src, srcIndex, dest, destIndex);
      srcIndex += 4;
      destIndex += 3;
      srclen -= 4;
    }
    switch (srclen) {
    case 3:
      decode3to2(src, srcIndex, dest, destIndex);
      break;
    case 2:
      decode2to1(src, srcIndex, dest, destIndex);
      break;
    case 1:
      throw new NumberFormatException();
    case 0:
      break;
    default:
      throw new Error(); // coding error
    }
  }

  public static byte[] decodeToByteArray(byte[] src) {
    int srclen = src.length;
    if (0 == (srclen & 3)) {
      if (srclen == 0)
        return new byte[0];
      if ('=' == src[ srclen-1 ]) {
        if ('=' == src[ srclen-2 ]) {
          srclen -= 2;
        } else {
          srclen -= 1;
        }
      }
    }
    int destlen = ((srclen * 3) / 4);
    byte[] dest = new byte[destlen];
    decode(src, srclen, dest);
    return dest;
  }

  public static byte[] decodeToByteArray(String src) {
    return decodeToByteArray(src.getBytes());
  }

  public static String decodeToString(byte[] src) {
    return new String(decodeToByteArray(src));
  }

  public static String decodeToString(String src) {
    return new String(decodeToByteArray(src.getBytes()));
  }

  /**
   * Main program, useful for testing. Encodes and decodes the 
   * parameter string.
   */
  public static void main(String[] pArgs) {
    String encoded = encodeToString(pArgs[0].getBytes());
    System.out.println(encoded);
    System.out.println(decodeToString(encoded));
  }
    
}
