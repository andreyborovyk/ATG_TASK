/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.repository;

import atg.repository.*;
import atg.repository.content.ContentRootPathProvider;
import atg.repository.content.ContentRepository;
import atg.repository.content.ContentRepositoryItem;
import java.io.File;

/**
 * This is a simple read-only property descriptor which takes a path name
 * and converts this to a java.io.File object.   You can optionally prepend
 * a prefix onto the path name.
 * <p>
 * You can use this property to expose content through the GSA which is
 * stored in the system as a path name.  If you define this property in an
 * item-descriptor with the content="true" attribute set, the ItemPath of the
 * content item is used (optionally in conjunction with the pathPrefix value) 
 * to determine the file system path of the file for this property.
 * <p>
 * If this property is not part of a content repository item, you must 
 * specify a pathName property feature descriptor attribute.  That attribute
 * specifies the name of a property to use to retrieve the path of the file.
 * The path is appended onto the pathPrefix if one is specified.
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/repository/FilePropertyDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class FilePropertyDescriptor extends RepositoryPropertyDescriptor {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/repository/FilePropertyDescriptor.java#2 $$Change: 651448 $";

  //-------------------------------------
  /** Attribute names */
  protected static final String PATH_PREFIX = "pathPrefix";
  protected static final String PATH_NAME_PROPERTY = "pathNameProperty";
  protected static final String TYPE_NAME = "file";

  /** Values of these attributes for this particular property */
  protected String mPathNameProperty = null; 
  protected String mPathPrefix = null;

  static {
    RepositoryPropertyDescriptor.registerPropertyDescriptorClass(TYPE_NAME, 
                                        FilePropertyDescriptor.class);
  }

  //-------------------------------------
  /**
   * Constructs a new FilePropertyDescriptor
   **/
  public FilePropertyDescriptor () {
    super();
  }

  //-------------------------------------
  /**
   * Constructs a new FilePropertyDescriptor for a particular property.
   **/
  public FilePropertyDescriptor(String pPropertyName)
  {
    super(pPropertyName);
  }

  //-------------------------------------
  /**
   * Constructs a new RepositoryPropertyDescriptor with the given 
   * property name, property type, and short description.
   **/
  public FilePropertyDescriptor(String pPropertyName, 
				      Class pPropertyType,
				      String pShortDescription)
  {
    super(pPropertyName, pPropertyType, pShortDescription);
  }

  //-------------------------------------
  /**
   * Returns property Queryable
   **/
  public boolean isQueryable() {
    return false;
  }

  //-------------------------------------
  /**
   * Returns property Writable.
   */
  public boolean isWritable() {
    return false;
  }

  //-------------------------------------
  /**
   * This method is called to retrieve a read-only value for this property.
   *
   * Once a repository has computed the value it would like to return for
   * this property, this property descriptor gets a chance to modify it
   * based on how the property is defined.  For example, if null is to
   * be returned, we return the default value.
   */
  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    Object pathName;
    ContentRepository contentRepository = null;
    if (mPathNameProperty != null)
      pathName = pItem.getPropertyValue(mPathNameProperty);
    /*
     * See if we are a content item and use the item's path
     */
    else if (pItem instanceof ContentRepositoryItem) {
      pathName = ((ContentRepositoryItem) pItem).getItemPath();
      contentRepository = (ContentRepository) pItem.getRepository();
    }
    else
      throw new IllegalArgumentException("No pathNameProperty set for property type: 'file'");
    if (pathName == null) 
      return null;

    // prefer use of a ContentRootPathProvider property on the content repository component
    if(contentRepository != null) {
      ContentRootPathProvider pathProvider = contentRepository.getContentRootPathProvider();
      if(pathProvider!=null) {
        String rootPath = pathProvider.getContentRootPath();
        // A null directory in File constructor is okay; its synonymous with current directory.
        // But it's easier to understand for the customer if we require an explicit value.
        // We disallow an empty string for the same reason. The ContentRootPathProvider impl 
        // should enforce these rules so that we shouldn't have to check here but...
        if((rootPath == null) || "".equals(rootPath)) 
          return new File(pathName.toString());
        else
          return new File(rootPath, pathName.toString());
      }
    }
    // otherwise use the pathPrefix attribute if available
    if (mPathPrefix == null) 
      return new File(pathName.toString());
    else 
      return new File(mPathPrefix + pathName.toString());
  }

  /**
   * Catch the attribute values that we care about and store them in 
   * member variables.
   */
  public void setValue(String pAttributeName, Object pValue) {
    super.setValue(pAttributeName, pValue);

    if (pValue == null || pAttributeName == null) return;
    if (pAttributeName.equalsIgnoreCase(PATH_NAME_PROPERTY))
      mPathNameProperty = pValue.toString();
    if (pAttributeName.equalsIgnoreCase(PATH_PREFIX))
      mPathPrefix = pValue.toString();
  }

  /**
   * Returns the name this type uses in the XML file.
   */
  public String getTypeName() {
    return TYPE_NAME;
  }

  public Class getPropertyType() {
    return java.io.File.class;
  }

  /**
   * Perform type checking.
   */
  public void setPropertyType(Class pClass) {    
    if (pClass != java.io.File.class)
      throw new IllegalArgumentException("file properties must be java.io.File");
    super.setPropertyType(pClass);
  }
  public void setComponentPropertyType(Class pClass) {
    if (pClass != null)
      throw new IllegalArgumentException("file properties must be scalars");
  }
  public void setPropertyItemDescriptor(RepositoryItemDescriptor pDesc) {
    if (pDesc != null)
      throw new IllegalArgumentException("file properties must be java.io.File");
  }
  public void setComponentItemDescriptor(RepositoryItemDescriptor pDesc) {
    if (pDesc != null)
      throw new IllegalArgumentException("file properties must be scalars");
  }

}
