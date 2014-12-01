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

import java.io.IOException;

import atg.core.util.ResourceUtils;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.Repository;

import java.io.File;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * <P>
 * A basic implementation of the TypeMapper interface that may be a useful
 * starting point for other implementations. It adds getMapping(File) which 
 * is used and required by the RL framework.
 * </P>
 *
 * @author Mark Stewart
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/repository/loader/BaseTypeMapper.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 **/

public
abstract
class BaseTypeMapper 
  extends GenericService 
  implements TypeMapper
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION 
    = "$Id: //product/DAS/version/10.0.3/Java/atg/repository/loader/BaseTypeMapper.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants

  /** Resource bundle **/
  static final String MY_RESOURCE_NAME
   = "atg.repository.loader.Resources";

  static java.util.ResourceBundle sResourceBundle =
    java.util.ResourceBundle.getBundle
    (MY_RESOURCE_NAME, java.util.Locale.getDefault());
  
  static String sNullArgMsg = ResourceUtils.getMsgResource
    ("BaseTypeMapper.apiNullArg", MY_RESOURCE_NAME, sResourceBundle);

  //-------------------------------------
  // Member variables

  //entries for these maps must only be added at startup
  HashMap mByHandlerMap = new HashMap();
  HashMap mByDescriptorMap = new HashMap();
  HashMap mByRepositoryMap = new HashMap();

  //ditto for these arrays
  ContentHandler[] mHandlers;
  ArrayList[] mByHandlerLists;

  //-------------------------------------
  // Properties



  //-------------------------------------
  // property: TypeMappings

  /** the type mappings this mapper will manage */
  TypeMapping[] mTypeMappings;

  /**
   * Sets the type mappings this mapper will manage
   **/
  public void setTypeMappings(TypeMapping[] pTypeMappings) {
    mTypeMappings = pTypeMappings;
  }

  /**
   * Returns the type mappings this mapper will manage
   **/
  public TypeMapping[] getTypeMappings() {
    return mTypeMappings;
  }

  //-------------------------------------
  // property: FolderTypeMapping

  /** the TypeMapping for a content repository folder item descriptor */
  TypeMapping mFolderTypeMapping;

  /**
   * Sets the TypeMapping for a content repository folder item descriptor
   **/
  public void setFolderTypeMapping(TypeMapping pFolderTypeMapping) {
    mFolderTypeMapping = pFolderTypeMapping;
  }

  /**
   * Returns the TypeMapping for a content repository folder item descriptor
   **/
  public TypeMapping getFolderTypeMapping() 
    throws UnknownTypeMappingException 
  {
    if(mFolderTypeMapping==null) {
      String msg = ResourceUtils.getMsgResource
        ("ExtFilterTypeMapper.noMappingForFolders", MY_RESOURCE_NAME, sResourceBundle);
      throw new UnknownTypeMappingException(msg);
    }
    return mFolderTypeMapping;
  }
  
  //-------------------------------------
  // Constructors

  // just the default no-arg constructor

  //-------------------------------------
  // Methods

  //-------------------------------------
  // GenericService methods

  public void doStartService() throws ServiceException
  {
    if(!buildValidConfig())
      throw new ServiceException();
  }

  public void doStopService() throws ServiceException
  {
    // subclasses may need/wish to perform cleanup work here
    // if necessary
  }

  /**
   * Confirms that the basic configuration of our TypeMappings are correct. 
   * Concrete subclasses will likely want to call this before performing 
   * additional validation in their override.
   */
  public boolean buildValidConfig()
  {
    TypeMapping[] mappings = getTypeMappings();
    if(mappings==null) {
      if(isLoggingError()) {
        String msg = ResourceUtils.getMsgResource
          ("BaseTypeMapper.mappingArrayNull", MY_RESOURCE_NAME, sResourceBundle);
        logError(msg);
      }
      return false;
    }
    
    mHandlers = new ContentHandler[mappings.length];
    mByHandlerLists = new ArrayList[mappings.length];
    boolean bOk = true;
    
    //make sure that all elements of the array exist and are valid
    for(int i=0; i<mappings.length; i++) {
      TypeMapping mapping = mappings[i];
      
      if(mapping==null) {
        String[] args = new String[1];
        args[0] = Integer.toString(i);
        String msg = ResourceUtils.getMsgResource
          ("BaseTypeMapper.nullMapping", MY_RESOURCE_NAME, sResourceBundle, args);
        bOk = false;
        continue;
      }
      
      String errorMsg = null;
      if((errorMsg = mapping.isValidConfig()) != null) {
        if(isLoggingError()) {
          String[] args = new String[2];
          args[0] = Integer.toString(i);
          args[1] = errorMsg;
          String msg = ResourceUtils.getMsgResource
            ("BaseTypeMapper.invalidTypeMapping", MY_RESOURCE_NAME, sResourceBundle, args);
          logError(msg);
        }
        bOk = false;
      }
      else { //check for the content handler
        
        // set ourselves as the mapping's logger if they don't already have one
        if(mapping instanceof InternalTypeMapping) {
          InternalTypeMapping internalMapping = (InternalTypeMapping) mapping;
          if(internalMapping.getApplicationLogging()==null)
            internalMapping.setApplicationLogging(this);
        }

        // We handle the setup for the less commonly used getContentHandlerByX
        // methods.

        //by content handler
        ContentHandler cbf = mapping.getContentHandler();
        int useIdx = i;
        int idx = indexForHandler(mHandlers, cbf);
        if(idx != -1) 
          useIdx = idx;
        else
          mHandlers[useIdx] = cbf;
        
        ArrayList handlerMappings = mByHandlerLists[useIdx];
        if(handlerMappings==null)
          handlerMappings = new ArrayList();
        handlerMappings.add(mapping);
        mByHandlerLists[useIdx] = handlerMappings;
        
        //by item descriptor name map
        String descriptorName = mapping.getItemDescriptorName();
        ArrayList descriptorMappings = (ArrayList)mByDescriptorMap.get(descriptorName);
        if(descriptorMappings==null)
          descriptorMappings = new ArrayList();
        descriptorMappings.add(mapping);
        mByDescriptorMap.put(descriptorName, descriptorMappings);
        
        //by repository name map
        Repository repository = mapping.getRepository();
        String repositoryPath = ((GenericService)repository).getAbsoluteName();
        ArrayList repositoryMappings = (ArrayList) mByRepositoryMap.get(repositoryPath);
        if(repositoryMappings==null)
          repositoryMappings = new ArrayList();
        repositoryMappings.add(mapping);
        mByRepositoryMap.put(repositoryPath, repositoryMappings);
      }
    }
    
    if(mFolderTypeMapping!=null) {
      String folderErrorMsg = mFolderTypeMapping.isValidConfig();
      if(folderErrorMsg!=null) {
        bOk = false;
        if(isLoggingError()) {
          logError("folder type mapping error: " + folderErrorMsg);
        }
      }
    }
    
    return bOk;
  }


  //-------------------------------------
  // TypeMapper implementation

  /**
   * Returns the most appropriate TypeMapping for the provided 
   * identifier.
   * @param pIdentifier the value used to locate a TypeMapping
   * @return the TypeMapping corresponding to the provided argument.
   * @throws InvalidArgumentException if the class of pIdentifier is 
   *   not supported by the implementation
   * @throws UnknownTypeMappingException if no suitable TypeMapping 
   *   can be found for the provided argument
   * 
   */	
  public TypeMapping getMapping(Object pIdentifier) 
    throws IllegalArgumentException, UnknownTypeMappingException
  {
    if(pIdentifier==null)
      apiHandleNullArg();

    if(pIdentifier instanceof File)
      return getMapping((File)pIdentifier);

    // nothing else is supported by default but subclasses
    // are of course free to override as needed. Using typed 
    // versions of getMapping() is preferable in any case.

    String[] args = new String[1];
    args[0] = pIdentifier.getClass().getName();
    String msg = ResourceUtils.getMsgResource
      ("BaseTypeMapper.unsupportedParmType", MY_RESOURCE_NAME, sResourceBundle, args);
    throw new IllegalArgumentException(msg);
  }  

  /** 
   * A typed version of the getMapping method, since Strings are 
   * expected to be common.
   *<p>This implementation assumes the String is a file name extension
   * that uniquely identifies a single TypeMapping.
   * @param pIdentifier the value used to locate a TypeMapping
   * @return the TypeMapping corresponding to the provided argument.
   * @throws UnknownTypeMappingException if no suitable TypeMapping 
   *   can be found for the provided argument
   */
  public abstract TypeMapping getMapping(String pIdentifier)
    throws UnknownTypeMappingException;
  
  
  /** 
   * Returns a collection of TypeMappings that reference the provided 
   * content handler.
   * @param pClass the name of the content handler class 
   * @return a Collection of TypeMappings matching pClass
   * @throws UnknownTypeMappingException if no suitable TypeMapping 
   *   can be found for the provided argument
   */
  public Collection getMappingsByContentHandler(ContentHandler pHandler)
    throws UnknownTypeMappingException
  {
    //find the handler in the array of unique content handler components
    int idx = indexForHandler(mHandlers, pHandler); 
    if(-1 == idx)
      throw new UnknownTypeMappingException("no type mappings for provided ContentHandler");

    return mByHandlerLists[idx];
  }
  
  
  /** 
   * Returns a collection of TypeMappings that reference the provided 
   * item descriptor name.
   * @param pName the item descriptor name 
   * @return a Collection of TypeMappings matching pName
   * @throws UnknownTypeMappingException if no suitable TypeMapping 
   *   can be found for the provided argument
   */
  public Collection getMappingsByDescriptorName(String pName)
    throws UnknownTypeMappingException
  {
    Collection c = (Collection) mByDescriptorMap.get(pName);
    if(c==null) {
      String[] args = new String[1];
      args[0] = pName;
      String msg = ResourceUtils.getMsgResource
        ("BaseTypeMapper.unknownTypeMappingForDesc", MY_RESOURCE_NAME, sResourceBundle, args);
    }
    return c;
  }
  
  /** 
   * Returns a collection of TypeMappings that reference the provided 
   * Repository path.
   * @param pName the item descriptor name 
   * @return a Collection of TypeMappings matching pName
   * @throws UnknownTypeMappingException if no suitable TypeMapping 
   *   can be found for the provided argument
   */
  public Collection getMappingsByRepository(String pRepositoryPath)
    throws UnknownTypeMappingException
  {
    Collection c = (Collection) mByRepositoryMap.get(pRepositoryPath);
    if(c==null) {
      String[] args = new String[1];
      args[0] = pRepositoryPath;
      String msg = ResourceUtils.getMsgResource
        ("BaseTypeMapper.unknownTypeMappingForRepos", MY_RESOURCE_NAME, sResourceBundle, args);
    }
    return c;
  }


  //-------------------------------------
  // RL required variants of getMapping()

  /**
   * This is the method called by the RL framework itself. 
   */
  public abstract TypeMapping getMapping(File pFile)
    throws IllegalArgumentException, UnknownTypeMappingException;


  //-------------------------------------
  // Utility Methods
  
  protected void apiHandleNullArg()
  {
    throw new IllegalArgumentException(sNullArgMsg);
  }

  private int indexForHandler(ContentHandler[] pHandlers, ContentHandler pHandler)
  {
    int idx = -1;
    for(int i=0; i < pHandlers.length; i++) {
      ContentHandler f = pHandlers[i];
      if(f!=null && (f == pHandler)) {
        idx = i;
        break;
      }
    }
    return idx;
  }

  //-------------------------------------
}
