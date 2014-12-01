/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2006 Art Technology Group, Inc.
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
package atg.projects.store.repository.servlet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import atg.adapter.gsa.EnumPropertyDescriptor;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.SortDirectives;
import atg.repository.servlet.PossibleValues;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;

/**
 * Extends PossibleValues droplet to return the list of options for enumerated
 * properties with localized labels.
 * 
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/repository/servlet/StorePossibleValues.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */

public class StorePossibleValues extends PossibleValues {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/repository/servlet/StorePossibleValues.java#3 $$Change: 635816 $";
  
  /**
   * Overrides base method to return the sorted list of localized values for enumerated
   * properties.
   * First calls super method and if results consist of EnumeratedOptionPossibleValue
   * then wraps results into EnumeratedOptionPossibleValueWrapper objects.
   * EnumeratedOptionPossibleValueWrapper objects return localized label for
   * the wrapped enumerated option. The list of EnumeratedOptionPossibleValueWrapper
   * objects is returned sorted by label.
   * All other types of results are returned untouched.
   */
  public Object getRepositoryValues(Repository pRepository,
      String pItemDescriptorName, String pPropertyName,
      boolean pUseCodeForValue, SortDirectives pSortDirectives) {
    
    Object result =  super.getRepositoryValues(pRepository, pItemDescriptorName,
        pPropertyName, pUseCodeForValue, pSortDirectives);
    
    return wrapEnumeratedOptionPossibleValues(result, pRepository, pItemDescriptorName, pPropertyName);
  }
  
  /**
   * Wraps EnumeratedOptionPossibleValue objects with EnumeratedOptionPossibleValueWrapper
   * that contains localized labels for enumerated property values.
   * Wrapped objects are sorted by labels.
   * If results doesn't contain EnumeratedOptionPossibleValue objects original
   * results are returned.
   * 
   * @param pOriginalResults original unwrapped results
   * @param pRepository repository
   * @param pItemDescriptorName item descriptor name
   * @param pPropertyName property name
   * @return wrapped results with localized labels for enumerated property values.
   */
  protected Object wrapEnumeratedOptionPossibleValues(Object pOriginalResults, Repository pRepository,
                                                      String pItemDescriptorName, String pPropertyName){
    List sortedLocalizedValues = null;
    
    // check if we are dealing with array of PossibleValue objects
    if (pOriginalResults != null && pOriginalResults instanceof PossibleValues.PossibleValue[]){
      PossibleValue[] values = (PossibleValue[])pOriginalResults;
      
      //check that array consists of EnumeratedOptionPossibleValue objects
      if (values.length > 0 && values[0] instanceof EnumeratedOptionPossibleValue){
        
        // yes, it's array of EnumeratedOptionPossibleValue objects
        try {
          // get property descriptor to determine resource bundle name
          RepositoryItemDescriptor itemDescriptor = pRepository.getItemDescriptor( pItemDescriptorName );        
          RepositoryPropertyDescriptor propertyDescriptor = null;
  
          if ( ! StringUtils.isEmpty(pPropertyName) && itemDescriptor != null ) {
            propertyDescriptor =
              (RepositoryPropertyDescriptor) itemDescriptor.getPropertyDescriptor( pPropertyName );
          }
          // get resource bundle for the property
          ResourceBundle bundle = getResourceBundle(propertyDescriptor.getResourceBundleName());
          
          // loop through the array and wrap its objects into EnumeratedOptionPossibleValueWrapper
          EnumeratedOptionPossibleValueWrapper[] localizedValues = new EnumeratedOptionPossibleValueWrapper[values.length];          
          for (int i=0; i<values.length; i++){
            if (values[i] instanceof EnumeratedOptionPossibleValue){
              localizedValues[i] = new EnumeratedOptionPossibleValueWrapper((EnumeratedOptionPossibleValue)values[i], bundle);
            }
          }
          
          //sort results by label
          sortedLocalizedValues =  Arrays.asList(localizedValues);
          Collections.sort(sortedLocalizedValues);
          
        } catch (RepositoryException ex) {
          if (isLoggingError())
              logError(ex);
        }
      }
    }
    return (sortedLocalizedValues != null)? sortedLocalizedValues : pOriginalResults;
  }
  
  /**
   * Get resource bundle for the request locale.
   * @param pResourceBundleName resource bundle name
   * @return resource bundle
   */
  protected ResourceBundle getResourceBundle(String pResourceBundleName){
    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    RequestLocale requestLocale = request.getRequestLocale();
    Locale currentLocale = requestLocale == null ? Locale.getDefault() : requestLocale.getLocale();
    return LayeredResourceBundle.getBundle(pResourceBundleName, currentLocale);
  }
   
  /** A wrapper inner class for EnumeratedOptionPossibleValue. Contains localized label.*/
  public class EnumeratedOptionPossibleValueWrapper implements Comparable<EnumeratedOptionPossibleValueWrapper>{
    
    EnumPropertyDescriptor.EnumeratedOption mEnumeratedOption;
    EnumeratedOptionPossibleValue mEnumeratedOptionPossibleValue;
    ResourceBundle mResourceBundle;

    /** Create a possible value backed by a repository item. */
    protected EnumeratedOptionPossibleValueWrapper(EnumeratedOptionPossibleValue pEnumeratedOptionPossibleValue,
        ResourceBundle pResourceBundle) {
      mEnumeratedOption = (EnumPropertyDescriptor.EnumeratedOption)pEnumeratedOptionPossibleValue.getUnderlyingObject();
      mEnumeratedOptionPossibleValue = pEnumeratedOptionPossibleValue;
      mResourceBundle = pResourceBundle;
    }

    /** Return the localized value for this option. */
    public String getLabel() {
      String label = mEnumeratedOptionPossibleValue.getLabel();
      if (mResourceBundle != null && mEnumeratedOption.getResource() != null){
        label = mResourceBundle.getString(mEnumeratedOption.getResource());
      }
      return label;
    }

    /** Get the settable value. */
    public Object getSettableValue() {
      return mEnumeratedOptionPossibleValue.getSettableValue();
    }

    /** Return the underlying object. In this case, the repository item. */
    public Object getUnderlyingObject() {
      return mEnumeratedOptionPossibleValue.getUnderlyingObject();
    }   
    
    /** Compare EnumeratedOptionPossibleValueWrapper alphabetically by label */
    public int compareTo(EnumeratedOptionPossibleValueWrapper pObject) {
      return this.getLabel().compareTo(pObject.getLabel());
    }
  } // end inner-class EnumeratedOptionPossibleValueWrapper

}
