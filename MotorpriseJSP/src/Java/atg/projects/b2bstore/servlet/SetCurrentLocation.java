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
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/ 

package atg.projects.b2bstore.servlet;

// Java classes
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Collections;
import java.util.Properties;

// DAS classes
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.nucleus.ServiceException;
import atg.servlet.ServletUtil;


/**
 * This servlet is inserted in request pipeline after DynamoServlet. This servlet checks the page
 * accessed by the request and sets a location value in <code>Profile.currentLocation</code> property.
 * A map containing page URLs or folders and values to be set is configured according to store rules.
 * Based on the location property of Profile, corresponding location URL can be highlighted in Store.
 *
 * Location map can be configured in two ways.
 * <dl>
 * <dd> Absolute path name of the page
 * <p> ex: /Dynamo/solutions/Motorprise/en/home.jhtml
 * <br> Whenever any request access this page then the value corresponding to this name in the map
 * is updated in <code>Profile.currentLocation</code>
 *
 * <dd> Any folder in document root.
 * <p> ex: /Dyanmo/solutions/Motorprise/en/catalog/
 * <br> if absolute file name of the request is not found in the map then folder of the file accessed
 * is checked in the locationMap.
 *
 * <br> All the folder names are sorted in descending order so that the closest path of the file
 * name requested is matched.
 *</dl>
 *
 * <P> <code>Profile.currentLocation</code> is an enumerated property containing valid option
 * values and codes. So all the values in locationMap has to exist in property definition
 * of <code>currentLocation</code>.
 * @author Manoj Potturu
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/servlet/SetCurrentLocation.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.projects.b2bstore.servlet.WASetCurrentLocation
 */

public class SetCurrentLocation extends InsertableServletImpl
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/servlet/SetCurrentLocation.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String JHTML_EXTENSION = ".jhtml";
  public static final String JSP_EXTENSION = ".jsp";
  public static final String SLASH_EXTENSION = "/";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------


  /** name of the location property */
  String mLocationProperty = "currentLocation";

  //-------------------------------------
  /**
   * Sets name of the location property
   **/
  public void setLocationProperty(String pLocationProperty) {
    mLocationProperty = pLocationProperty;
  }

  //-------------------------------------
  /**
   * Returns name of the location property
   **/
  public String getLocationProperty() {
    return mLocationProperty;
  }

  
  /** Location Map. */
  Properties mLocationMap;

  //-------------------------------------
  /**
   * Sets Location Map.
   **/
  public void setLocationMap(Properties pLocationMap) {
    mLocationMap = pLocationMap;

    // Sort the location names in locationMap and store it in SortedNameList so that
    // the closest path of the file is matched when comparing.
    sortLocationMap();

  }

  //-------------------------------------
  /**
   * Returns Location Map.
   **/
  public Properties getLocationMap() {
    return mLocationMap;
  }

  /** Sorted  location name list */
  ArrayList mSortedNameList;

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  public SetCurrentLocation() {}

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

   
  public void service (DynamoHttpServletRequest pRequest,
                       DynamoHttpServletResponse pResponse)
    throws IOException, ServletException
  {

    // Set the location value of the page/folder configured in locationMap into
    // Profile.location.
    setLocation(pRequest, pResponse);
    
    passRequest (pRequest, pResponse);
  }


  /**
   * This method checks the path name of the file accessed against the locationMap and
   * sets the value of path name in Profile
   *
   * @param pRequest
   * @param pResponse
   *
   **/
  protected void setLocation(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
  {

    String uri = pRequest.getRequestURI();
    String location;
    Enumeration keys;
    String key;

    // Check if the page requested ends in .jhtml then try to match the file name in property
    // names. Otherwise try to match the folder name of the page in SortedNameList. The first
    // one matched will be the closest match since the names in this list are sorted in
    // descending order.
    if ( uri.endsWith(JHTML_EXTENSION) || uri.endsWith(JSP_EXTENSION)) {

      // check whether file name exists in locationMap
      if ((location = getLocationMap().getProperty(uri)) != null) {
        setUserLocation(pRequest, pResponse, location);
      } else {

        // if file name doesn't exist in locationMap then check for folder path of filename.
        for ( int i = 0; i< mSortedNameList.size(); i++) {
          key = (String)mSortedNameList.get(i);

          // if name in sortedList exists in requested path then match exists
          if ( uri.indexOf(key) != -1) {
            setUserLocation(pRequest, pResponse, getLocationMap().getProperty(key));
          } // end of if ()
        } // end of for ()
      
      } // end of else
      // Sometime folder name is requested instead of exact page, do the same as above.

    }else if ( uri.endsWith(SLASH_EXTENSION)) {
      for ( int i = 0; i< mSortedNameList.size(); i++) {
        key = (String)mSortedNameList.get(i);
        if ( uri.indexOf(key) != -1) {
          setUserLocation(pRequest, pResponse, getLocationMap().getProperty(key));
        } // end of if ()
      } // end of for ()
      
    } // end of if ()
    
  }

  /**
   * This method sets the value passed in <code>Profile.currentLocation</code> property.
   *
   * @param pRequest
   * @param pResponse
   * @param pLocation the location value to be set in profile.
   **/
  protected void setUserLocation(DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse,
                                              String pLocation)
  {

    MutableRepositoryItem pItem = (MutableRepositoryItem)ServletUtil.getCurrentUserProfile();

    if (pItem != null) {
      pItem.setPropertyValue(getLocationProperty(), pLocation);
    } // end of if ()
  }

  /**
   * This method  sorts the path names in locationMap in descending order and populates
   * SortedNameList with these values.
   *
   **/
  protected void sortLocationMap()
  {
    Enumeration keys = getLocationMap().propertyNames();
    
    ArrayList keysList = new ArrayList();

    // Get all the location names and populate arrayList so that it can be sorted.
    while ( keys.hasMoreElements()) {
      keysList.add(keys.nextElement());
    } // end of while ()

    // sort arraylist in descending order.
    Collections.sort(keysList, Collections.reverseOrder());

    mSortedNameList = keysList;

  }

}   // end of class


  
