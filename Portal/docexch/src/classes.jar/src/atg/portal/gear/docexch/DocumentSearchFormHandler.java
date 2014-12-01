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


package atg.portal.gear.docexch;

import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.repository.servlet.*;
import atg.repository.*;

import atg.servlet.*;
import atg.nucleus.Nucleus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.*;
import java.io.IOException;

/**
 * The class extends SearchFormHandler so that it can take parameters as strings
 * and convert them to the object types required by the SearchFormHandler and 
 * only search for data in the current gear instance.  We check a gear instance 
 * parameter.  If the gear instance parameter "gearIdPropertyName" is not set, 
 * then we assume the whole repository is searchable.
 *
 * @author Will Sargent and Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocumentSearchFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DocumentSearchFormHandler extends SearchFormHandler
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocumentSearchFormHandler.java#2 $$Change: 651448 $";

    //---------------------------------------
    // Properties

    /** The nucleus path to the repository to search */
    String mRepositoryPath;

    //-------------------------------------
    /**
     * Sets The nucleus path to the repository to search
     **/
    public void setRepositoryPath(String pRepositoryPath) {
        mRepositoryPath = pRepositoryPath;
    }

    //-------------------------------------
    /**
     * Returns The nucleus path to the repository to search
     **/
    public String getRepositoryPath() {
        return mRepositoryPath;
    }


    /** The name of the item type to search */
    String mItemTypeName;

    //-------------------------------------
    /**
     * Sets The name of the item type to search
     **/
    public void setItemTypeName(String pItemTypeName) {
        mItemTypeName = pItemTypeName;
    }

    //-------------------------------------
    /**
     * Returns The name of the item type to search
     **/
    public String getItemTypeName() {
        return mItemTypeName;
    }


    /** The name of the property of the item type we are searching that contains the gear id. */
    String mGearIdPropertyName;

    //-------------------------------------
    /**
     * Sets The name of the property of the item type we are searching that contains the gear id.
     **/
    public void setGearIdPropertyName(String pGearIdPropertyName) {
        mGearIdPropertyName = pGearIdPropertyName;
    }

    //-------------------------------------
    /**
     * Returns The name of the property of the item type we are searching that contains the gear id.
     **/
    public String getGearIdPropertyName() {
        return mGearIdPropertyName;
    }


    /** The id of the current gear instance.  This is used to narrow 
     * the result set to only the current gear's data by including 
     * gearIdPropertyName = gearId. */
    String mGearId;

    //-------------------------------------
    /**
     * Sets The id of the current gear instance.  This is used to
     * narrow the result set to only the current gear's data by 
     * including gearIdPropertyName = gearId.
     **/
    public void setGearId(String pGearId) {
        mGearId = pGearId;
    }

    //-------------------------------------
    /**
     * Returns The id of the current gear instance.  This is used to
     * narrow the result set to only the current gear's data by 
     * including gearIdPropertyName = gearId.
     **/
    public String getGearId() {
        return mGearId;
    }


    /** Name of property containing searchable title */
    String mTitlePropertyName;

    //-------------------------------------
    /**
     * Sets Name of property containing searchable title
     **/
    public void setTitlePropertyName(String pTitlePropertyName) {
        mTitlePropertyName = pTitlePropertyName;
    }

    //-------------------------------------
    /**
     * Returns Name of property containing searchable title
     **/
    public String getTitlePropertyName() {
        return mTitlePropertyName;
    }


    /** name of property containing author */
    String mAuthorPropertyName;

    //-------------------------------------
    /**
     * Sets name of property containing author
     **/
    public void setAuthorPropertyName(String pAuthorPropertyName) {
        mAuthorPropertyName = pAuthorPropertyName;
    }

    //-------------------------------------
    /**
     * Returns name of property containing author
     **/
    public String getAuthorPropertyName() {
        return mAuthorPropertyName;
    }


    /** name of property containing description */
    String mDescriptionPropertyName;

    //-------------------------------------
    /**
     * Sets name of property containing description
     **/
    public void setDescriptionPropertyName(String pDescriptionPropertyName) {
        mDescriptionPropertyName = pDescriptionPropertyName;
    }

    //-------------------------------------
    /**
     * Returns name of property containing description
     **/
    public String getDescriptionPropertyName() {
        return mDescriptionPropertyName;
    }


    /** name of property of author object containing firstname value */
    String mAuthorFirstNameProperty;

    //-------------------------------------
    /**
     * Sets name of property of author object containing firstname value
     **/
    public void setAuthorFirstNameProperty(String pAuthorFirstNameProperty) {
        mAuthorFirstNameProperty = pAuthorFirstNameProperty;
    }

    //-------------------------------------
    /**
     * Returns name of property of author object containing firstname value
     **/
    public String getAuthorFirstNameProperty() {
        return mAuthorFirstNameProperty;
    }

    /** name of property of author object containing lastname value */
    String mAuthorLastNameProperty;

    //-------------------------------------
    /**
     * Sets name of property of author object containing lastname value
     **/
    public void setAuthorLastNameProperty(String pAuthorLastNameProperty) {
        mAuthorLastNameProperty = pAuthorLastNameProperty;
    }

    //-------------------------------------
    /**
     * Returns name of property of author object containing lastname value
     **/
    public String getAuthorLastNameProperty() {
        return mAuthorLastNameProperty;
    }


    /** A holding place for the advancedSearchPropertyNames because
     *  the SearchFormHandler clears it out too aggressively.
     */
    HashMap mTempAdvancedSearchPropertyValues;

    //-------------------------------------
    /**
     * Sets tempAdvancedSearchPropertyValues because the SearchFormHandler
     * is a little too aggressive about clearing this one out. 
     **/
    public void setTempAdvancedSearchPropertyValues(HashMap pTempAdvancedSearchPropertyValues) {
        mTempAdvancedSearchPropertyValues = pTempAdvancedSearchPropertyValues;
    }

    //-------------------------------------
    /**
     * Returns tempAdvancedSearchPropertyValues because the SearchFormHandler
     * is a little too aggressive about clearing this one out. 
     **/
    public HashMap getTempAdvancedSearchPropertyValues() {
        return mTempAdvancedSearchPropertyValues;
    }

    public HashMap getAdvancedSearchPropertyValues() {
        if (getTempAdvancedSearchPropertyValues() == null)
            return null;

        return (HashMap)getTempAdvancedSearchPropertyValues().clone();
    }

    
    //-------------------------------------
    // Handlers
    //-------------------------------------
    
    /**
     * For each each item type in that repository, call generateSearchResultSet
     * to generate a subResultSet for that item type based on query parameters.
     * Each subResultSet will be both merged together in resultSet as well as
     * stored in the property SearchResultsByItemType by item type.
     * @param pRequest the servlet's request
     * @param pResponse the servlet's response
     * @return false to stop form processing
     * @exception ServletException if there was an error while executing the code
     * @exception IOException if there was an error with servlet io
     */
    public boolean handleSearch(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
        throws ServletException, IOException
    {
        preHandleSearch(pRequest, pResponse);
        return super.handleSearch(pRequest, pResponse);
    }


    /**
     * prepare the search criteria
     */
    public void preHandleSearch(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
    {
        // repository
        Repository rep = (Repository)pRequest.resolveName(getRepositoryPath());
        Repository [] reps = {rep};
        setRepositories(reps);

        // item type        
        String [] itemtypes = {getItemTypeName()};
        setItemTypes(itemtypes);

        // advanced search property -- for gear instance specific data 
        if (getGearIdPropertyName() != null) {
            String advancedSearchProperty = getItemTypeName() + "." + getGearIdPropertyName();
            String [] advancedSearchProps = {advancedSearchProperty};
            HashMap valmap = new HashMap();
            valmap.put (getGearIdPropertyName(), getGearId());
            HashMap advancedSearchValues = new HashMap();
            advancedSearchValues.put (getItemTypeName(), valmap);
            setAdvancedSearchPropertyNames(advancedSearchProps);
            setTempAdvancedSearchPropertyValues(advancedSearchValues);            
            setDoAdvancedSearch(true);
            if (isLoggingDebug()) {
                logDebug("doing advanced search with the following props & values");
            }
        } 
        else {
            setDoAdvancedSearch(false);
        }

        // keyword search properties
        ArrayList keywordProps = new ArrayList();
        addProp(getTitlePropertyName(), keywordProps);
        addProp(getDescriptionPropertyName(), keywordProps);        
        String authorProp = getAuthorPropertyName();
        String authorFirst = getAuthorFirstNameProperty();
        String authorLast = getAuthorLastNameProperty();
        if (authorProp != null && authorFirst != null)
            addProp(authorProp + "." + authorFirst, keywordProps);
        if (authorProp != null && authorLast != null)
            addProp(authorProp + "." + authorLast, keywordProps);            
        
        if (keywordProps.size() != 0) {
            // ick. can't use ArrayList.toArray because I need a String [].
            String keywordArray [] = new String [keywordProps.size()];
            Iterator iter = keywordProps.iterator();
            int ii=0;
            while (iter.hasNext())
                keywordArray[ii++] = iter.next().toString();
            setKeywordSearchPropertyNames(keywordArray);
            setDoKeywordSearch(true);
            if (isLoggingDebug())
                logDebug("doing keyword search");
        }
        else {
            setDoKeywordSearch(false);
        }
     
        setPrepared(false);
    }

    /**
     */
    void addProp(String pProp, ArrayList pList) {
        if (pProp != null) {            
            String prop = getItemTypeName() + "." + pProp;
            pList.add(prop);
            if (isLoggingDebug())
                logDebug("added prop to list: " + pProp);
        }
    }
    
 /**
   * Split property names into type and name from type.name
   *  REMOVE ME WHEN THE SUPERCLASS FIXES PR 44432
   */
  protected void splitPropertyNames(String[] pTypes, String[] pSource,
				  HashMap pDest, String pSearchType ) {
		for (int i=0; i<pTypes.length;i++) {
			pDest.put(pTypes[i], new ArrayList());
		}
    String [] nameVal;
    ArrayList names;
    for (int i=0; i<pSource.length; i++) {
      nameVal = StringUtils.splitStringAtCharacter(pSource[i], '.');
      if ( (nameVal.length == 2) && pDest.containsKey(nameVal[0]) ) {
        names = (ArrayList)pDest.get(nameVal[0]);
        names.add(nameVal[1]);
      }
      else if ( (nameVal.length > 2) && pDest.containsKey(nameVal[0]) ) {
        names = (ArrayList)pDest.get(nameVal[0]);
        int ix = pSource[i].indexOf('.');
        String sub = pSource[i].substring(ix + 1);
        names.add(sub);
      }
      else {
        if (isLoggingError()) {
            logError("invalid property for search: " + pSource[i]);
        }
      }
    }
  }
}
