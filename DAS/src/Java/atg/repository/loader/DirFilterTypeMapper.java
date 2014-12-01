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
 * An implementation of the TypeMapper interface that maps directory paths
 * to TypeMappings. The caller is expected to provide a File object to the
 * getMapping() method. The shipping LoaderManager calls getMapping(File) 
 * exclusively.
 * </P>
 * 
 * <P>
 * ATG recommends that customer written TypeMapper implementations extend 
 * BaseTypeMapper as demonstrated here, overriding getMapping(File) as neeeded.
 * </P>
 *
 * @author Wendy Gordon
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/repository/loader/DirFilterTypeMapper.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 **/

public
class DirFilterTypeMapper
  extends BaseTypeMapper
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION 
    = "$Id: //product/DAS/version/10.0.3/Java/atg/repository/loader/DirFilterTypeMapper.java#2 $$Change: 651448 $";

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
  HashMap mByDirectoryMap = new HashMap();

  //-------------------------------------
  // Properties

  //-------------------------------------
  // property: Directories

  /** the directory paths that should be applied for the corresponding 
   * TypeMappings in base class */
  File[] mDirectories;

  /**
   * Sets the directory paths that should be applied for the corresponding 
   * TypeMappings in base class
   **/
  public void setDirectories(File[] pDirectories) {
    mDirectories = pDirectories;
  }

  /**
   * Returns the expressions that should be applied for the corresponding 
   * TypeMappings in base class
   **/
  public File[] getDirectories() {
    return mDirectories;
  }


  //-------------------------------------
  // Constructors

  //-------------------------------------
  public DirFilterTypeMapper () 
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

    File[] exps = getDirectories();
    TypeMapping[] mappings = getTypeMappings();

    if(exps==null) {
      if(isLoggingError()) {
        String msg = ResourceUtils.getMsgResource
          ("DirFilterTypeMapper.directoryArrayNull", MY_RESOURCE_NAME, sResourceBundle);
        logError(msg);
      }
      return false;
    }

    mDirectories = new File[exps.length];
    
    if(exps.length != mappings.length) {
      if(isLoggingError()) {
        String msg = ResourceUtils.getMsgResource
          ("DirFilterTypeMapper.expressionTypeMappingsArraysNotEqual", 
           MY_RESOURCE_NAME, sResourceBundle);
        logError(msg);
      }
      bOk = false;
    }

    for (int i=0; i<exps.length; i++) {
       File dir = exps[i];
       if(!dir.isDirectory()) { //no dot
           String[] args = new String[1];
           args[0] = exps[i].getAbsolutePath();
           String msg = ResourceUtils.getMsgResource
               ("DirFilterTypeMapper.fileIsNotADirectory",MY_RESOURCE_NAME,sResourceBundle,args);
           if (isLoggingError())
               logError(msg);
           bOk = false;
       }
       mDirectories[i] = dir;
       if (isLoggingDebug()) {
            logDebug("Adding mapping for " + dir);
       }
       //by key map
       if(mByDirectoryMap.put(exps[i].getAbsolutePath(), mappings[i])!= null) {
         //the keys are directory names so they must be unique
         String[] args = new String[1];
         args[0] = exps[i].getAbsolutePath();
         String msg = ResourceUtils.getMsgResource
           ("DirFilterTypeMapper.duplicateDirectory", MY_RESOURCE_NAME, 
            sResourceBundle, args);
         if(isLoggingError())
           logError(msg);
         bOk = false; 
       }
    }
     
    return bOk;
  }
  
  /** 
   * A typed version of the getMapping method, since Strings are 
   * expected to be common. 
   * 
   * <p>This implementation assumes the String is the path of a directory
   * that uniquely identifies a single TypeMapping. We use it here more as 
   * a helper method.
   *
   * @param pIdentifier the value used to locate a TypeMapping
   * @return the TypeMapping corresponding to the provided argument.
   * @throws UnknownTypeMappingException if no suitable TypeMapping 
   *   can be found for the provided argument
   */
  public TypeMapping getMapping(String pIdentifier)
    throws UnknownTypeMappingException
  {
    if (isLoggingDebug()) {
        logDebug("getMapping(str) for " + pIdentifier);  
    }
    //FIXME:should we check if the string has a leading dot?
    TypeMapping mapping = (TypeMapping) mByDirectoryMap.get(pIdentifier);
    if(mapping==null) {
      String[] args = new String[1];
      args[0] = pIdentifier;
      String msg = ResourceUtils.getMsgResource
        ("DirFilterTypeMapper.unknownTypeMappingForDir", MY_RESOURCE_NAME, sResourceBundle, args);
      throw new UnknownTypeMappingException(msg);
    }
    return mapping;
  }

  /**
   * Override the BaseTypeMapper implementation to use the path of the provided
   * File object's parent folder. The LoaderManager _exclusively_ calls this version of 
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

    String directory = pFile.getParent();
    return getMapping(directory);
  }

  //-------------------------------------
}
