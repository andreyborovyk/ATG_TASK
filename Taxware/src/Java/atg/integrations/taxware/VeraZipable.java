/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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
package atg.integrations.taxware;

import java.util.*;

/**
 *
 * <p>This inteface exists to support storing verazip information
 * on the order.
 *
 * <p>Since any given order could have any number of addresses associated
 * with it, we have a conception of an array of addresses. Each
 * address array index should support storing a ZipResult associated
 * with the address.
 *
 * <p>Note that the ZipResult stored at each index could
 * have one or more ZipResultItems associated with it.
 *
 * @see OrderImpl
 * @see VeraZipOrderImpl
 * @see ZipResult
 * @see ZipResultItem
 * @author Charles Morehead
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/VeraZipable.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/


public interface VeraZipable {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/VeraZipable.java#2 $$Change: 651448 $";


  /** Ship to address name. Not required, but
   * useful for getZipRequestForAddress() implementations and calls */
  static final String SHIP_TO_ADDRESS_NAME = "ship-to";

  /** Ship to address name. Not required, but
   * useful for getZipRequestForAddress() implementations and calls */
  static final String BILLING_ADDRESS_NAME = "billing";

  /** Maximum number of addressed to verify */
  int getMaxCountOfZipAddresses();

  /** Create a ZipRequest for the specified address (the address to be
   * verified). If his method returns null, that means that the specified
   * address does not need to be checked.
   */
  ZipRequest getZipRequestForAddress(int idxAddress);

  /** Get the index for the specified address name.
   * This is kind of tedious, but makes it possible
   * for a non-zip method (like TaxWareCalculateSalesTax)
   * that is interested in Zip info to get the
   * current zip info associated with an address by
   * name, rather than by magic number.<p>
   *
   * Should return -1 if there is no address by that name.
   *
   */
  int getIndexForAddressName(String pAddress);
  
  /* Get the specified address name for the given index */
  String getAddressNameForIndex(int index);

  /** Set the ZipResult for a specified address.
   * This is called by the TaxWareVerifyZipInfo order
   * processing object. */
  void setZipResultAt(int idx, ZipResult pZipResult);

  /** Get the zip result at the specified index. */
  ZipResult getZipResultAt(int idx);

  /** Set the zip error string -- the error string associated
   * with the zip code. There can only be one of these at a time.
   */
  void setZipErrorString(String pError);

  /** Get the current zip error string. */
  String getZipErrorString();

  /** Set whether a zip error exists. This should probably
   * only be used with a "false" value, since generically
   * seting zip error isn't terribly informative. */
  void setZipError(boolean pIsError);

  /** Return true if the is a zip code error currently on the order. */
  boolean isZipError();  
}
