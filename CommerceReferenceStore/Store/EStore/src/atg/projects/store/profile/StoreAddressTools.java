/*<ATGCOPYRIGHT>
 * Copyright (C) 2010-2011 Art Technology Group, Inc.
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

package atg.projects.store.profile;

import atg.core.util.Address;

/**
 * Contains useful functions for Address manipulation
 * 
 * @author ckearney
 */
public class StoreAddressTools {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/profile/StoreAddressTools.java#2 $$Change: 651448 $";


  /**
   * Compares the properties of two addresses equality. If all properties are
   * equal then the addresses are equal.
   * 
   * @param pAddressA An Address
   * @param pAddressB An Address
   * @return A boolean indicating whether or not pAddressA and pAddressB 
   * represent the same address.
   */
  public static boolean compare(Address pAddressA, Address pAddressB){
    
    if((pAddressA == null) && (pAddressB != null)){
      return false;
    }
    if((pAddressA != null) && (pAddressB == null)){
      return false;
    }
    if((pAddressA == null) || (pAddressB == null)){
      return true;
    }

    if(!(pAddressA instanceof Address))
      return false;
    
    if(!(pAddressB instanceof Address))
      return false;
        
    String aFirstName = pAddressA.getFirstName();
    String bFirstName = pAddressB.getFirstName();

    String aLastName = pAddressA.getLastName();
    String bLastName = pAddressB.getLastName();

    String aAddress1 = pAddressA.getAddress1();
    String bAddress1 = pAddressB.getAddress1();

    String aAddress2 = pAddressA.getAddress2();
    String bAddress2 = pAddressB.getAddress2();

    String aCity = pAddressA.getCity();
    String bCity = pAddressB.getCity();

    String aState = pAddressA.getState();
    String bState = pAddressB.getState();

    String aPostalCode = pAddressA.getPostalCode();
    String bPostalCode = pAddressB.getPostalCode();

    String aCountry = pAddressA.getCountry();
    String bCountry = pAddressB.getCountry();

    if((aFirstName == null) &&
       ((bFirstName != null)))
      return false;
    if((aLastName == null) &&
            ((bLastName != null)))
      return false;
    if((aAddress1 == null) &&
            ((bAddress1 != null)))
      return false;
    if((aAddress2 == null) &&
            ((bAddress2 != null)))
      return false;
    if((aCity == null) &&
            ((bCity != null)))
      return false;
    if((aState == null) &&
            ((bState != null)))
      return false;
    if((aPostalCode == null) &&
            ((bPostalCode != null)))
      return false;
    if((aCountry == null) &&
       ((bCountry != null)))
      return false;
    
    if(
       (((aFirstName == null) && (bFirstName == null)) ||
         (aFirstName.equals(bFirstName)))
       &&
       (((aLastName == null) && (bLastName == null)) ||
         (aLastName.equals(bLastName)))
       &&
       (((aAddress1 == null) && (bAddress1 == null)) ||
         (aAddress1.equals(bAddress1)))
       &&
       (((aAddress2 == null) && (bAddress2 == null)) ||
         (aAddress2.equals(bAddress2)))
       &&
       (((aCity == null) && (bCity == null)) ||
         (aCity.equals(bCity)))
       &&
       (((aState == null) && (bState == null)) ||
         (aState.equals(bState)))
       &&
       (((aPostalCode == null) && (bPostalCode == null)) ||
         (aPostalCode.equals(bPostalCode)))
       &&
       (((aCountry == null) && (bCountry == null)) ||
         (aCountry.equals(bCountry)))
       )
    {
      return true;
    }
    else
      return false;
  }
}
