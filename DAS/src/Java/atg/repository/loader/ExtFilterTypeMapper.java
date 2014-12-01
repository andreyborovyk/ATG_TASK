/*<ATGCOPYRIGHT>
 * Copyright (C) 2003-2011 Art Technology Group, Inc.
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

package atg.repository.loader ;


import atg.core.util.ResourceUtils;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

import java.io.File;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * <P>
 * An implementation of the TypeMapper interface that maps file extensions
 * to TypeMappings. The caller is expected to provide a File object to the
 * getMapping() method. Alternatively a string representing a file extension
 * can be provided. The shipping LoaderManager calls getMapping(File) exclusively.
 * </P>
 * 
 * <P>
 * ATG recommends that customer written TypeMapper implementations extend 
 * BaseTypeMapper as demonstrated here, overriding getMapping(File) as neeeded.
 * </P>
 *
 * @author Mark Stewart
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/repository/loader/ExtFilterTypeMapper.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 **/

public
class ExtFilterTypeMapper
  extends BaseTypeMapper
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION 
    = "$Id: //product/DAS/version/10.0.3/Java/atg/repository/loader/ExtFilterTypeMapper.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants

  /** Resource bundle **/
  static final String MY_RESOURCE_NAME
    = "atg.repository.loader.Resources";
  
  static java.util.ResourceBundle sResourceBundle =
    java.util.ResourceBundle.getBundle
    (MY_RESOURCE_NAME, java.util.Locale.getDefault());
  
  //-------------------------------------
  // Member variables

  //entries for this map must only be added at startup
  HashMap mByExtensionMap = new HashMap();

  //-------------------------------------
  // Properties

  //-------------------------------------
  // property: Extensions

  /** the expressions that should be applied for the corresponding TypeMappings in base class */
  String[] mExtensions;

  /**
   * Sets the expressions that should be applied for the corresponding TypeMappings in base class
   **/
  public void setExtensions(String[] pExtensions) {
    mExtensions = pExtensions;
  }

  /**
   * Returns the expressions that should be applied for the corresponding TypeMappings in base class
   **/
  public String[] getExtensions() {
    return mExtensions;
  }
  

  //-------------------------------------
  // Constructors

  //-------------------------------------
  public ExtFilterTypeMapper () 
  {
  }


  //-------------------------------------
  // Methods
  
  //-------------------------------------
  // GenericService methods

  public void doStartService() throws ServiceException
  {
    if(!buildValidConfig())
      throw new ServiceException();
  }

  /**
   * Confirm that the basic configuration of our TypeMappings are correct
   */
  public boolean buildValidConfig()
  {
    // our superclass validates the array of TypeMapping components we're configured 
    // with and sets their logging facilities to go through this component if
    // they don't have logging configured otherwise.
    boolean bOk = super.buildValidConfig();

    // get the corresponding array of file extensions
    // and make sure they match up okay then create a map
    // using them as keys and the type mappings as the values.
    String[] exps = getExtensions();
    TypeMapping[] mappings = getTypeMappings();

    if(exps==null) {
      if(isLoggingError()) {
        String msg = ResourceUtils.getMsgResource
          ("ExtfilterTypeMapper.extensionArrayNull", MY_RESOURCE_NAME, sResourceBundle);
        logError(msg);
      }
      return false;
    }

    mExtensions = new String[exps.length];
    
    if(exps.length != mappings.length) {
      if(isLoggingError()) {
        String msg = ResourceUtils.getMsgResource
          ("ExtfilterTypeMapper.expressionTypeMappingsArraysNotEqual", 
           MY_RESOURCE_NAME, sResourceBundle);
        logError(msg);
      }
      bOk = false;
    }

    for(int i=0; i<exps.length; i++) {
      String s = exps[i];
      if(s==null || "".equals(s.trim())) {
        if(isLoggingError()) {
          String[] args = new String[1];
          args[0] = Integer.toString(i);
          String msg = ResourceUtils.getMsgResource
            ("ExtfilterTypeMapper.emptyExpressionStringNotValid", 
             MY_RESOURCE_NAME, sResourceBundle, args);
          logError(msg);
        }
        bOk = false;
      }
      else {
        mExtensions[i] = checkExtension(exps[i]);
        //by key map
        if(mByExtensionMap.put(exps[i], mappings[i])!= null) {
          //the keys are file name extensions so they must be unique
          String[] args = new String[1];
          args[0] = exps[i];
          String msg = ResourceUtils.getMsgResource
            ("ExtfileTypeMapper.duplicateExtension", MY_RESOURCE_NAME, 
             sResourceBundle, args);
          if(isLoggingError())
            logError(msg);
          bOk = false; 
        }
        
      }
    }
    
    return bOk;
  }
  
  /** 
   * A typed version of the getMapping method, since Strings are 
   * expected to be common. 
   *
   * <p>This implementation assumes that the string argument is a 
   * file extension (without a leading dot) that uniquely identifies a 
   * single TypeMapping. We use it here more as a helper method.
   *
   * @param pIdentifier the value used to locate a TypeMapping
   * @return the TypeMapping corresponding to the provided argument.
   * @throws UnknownTypeMappingException if no suitable TypeMapping 
   *   can be found for the provided argument
   */
  public TypeMapping getMapping(String pIdentifier)
    throws UnknownTypeMappingException
  {
    TypeMapping mapping = (TypeMapping) mByExtensionMap.get(pIdentifier);
    if(mapping==null) {
      String[] args = new String[1];
      args[0] = pIdentifier;
      String msg = ResourceUtils.getMsgResource
        ("ExtfilterTypeMapper.unknownTypeMappingForExt", MY_RESOURCE_NAME, sResourceBundle, args);
      throw new UnknownTypeMappingException(msg);
    }
    return mapping;
  }

  /**
   * Override the BaseTypeMapper implementation to use the file extension
   * of the file name. The LoaderManager _exclusively_ calls this version of 
   * getMapping().
   */
  public TypeMapping getMapping(File pFile)
    throws IllegalArgumentException, UnknownTypeMappingException
  {
    if (isLoggingDebug()) {
        logDebug("getMapping(file) for " + pFile);  
    }

    if(pFile==null) 
      apiHandleNullArg();

    // there should only ever be a single folder type mapping!
    if(pFile.isDirectory())
      return getFolderTypeMapping();

    String fileName = pFile.getName();
    //get the extension part of the file name
    int dot = fileName.lastIndexOf('.');
    if(-1==dot) { //no dot, pass the whole file name?
      String[]  args = new String[1];
      args[0] = pFile.getAbsolutePath();
      String msg = ResourceUtils.getMsgResource
        ("ExtFilterTypeMapper.fileHasNoExtension", MY_RESOURCE_NAME, sResourceBundle, args);
      throw new IllegalArgumentException(msg);
    }
    String ext = fileName.substring(dot);
    return getMapping(ext);
  }

  //-------------------------------------
  //Utility Methods
  
  /**
   * Insures there's a '.' prefixing the extension
   */
  String checkExtension(String pExtension)
  {
    int dot = pExtension.lastIndexOf('.');
    if(-1==dot) { //no dot
      return "." + pExtension;      
    }
    return pExtension;
  }


  //-------------------------------------
}
