/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.ui;

import atg.beans.DynamicBeanState;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

import atg.ui.repository.RepositoryItemTable;
import atg.ui.repository.RepositoryResources;
import atg.ui.repository.model.RepositoryFolderNode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * An ACC editor for RepositoryItems of type "promotionalContent". The path
 * property is auto calculated from the name and parent folder.
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class PromotionalContentEditor extends RepositoryItemTable implements PropertyChangeListener {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/ui/PromotionalContentEditor.java#2 $$Change: 651448 $";

  /**
   * File name property name.
   */
  public static final String NAME_PROPERTY = RepositoryResources.getString("folderFileNameProperty");

  /**
   * Folder path property name.
   */
  public static final String PATH_PROPERTY = RepositoryResources.getString("folderPathProperty");

  /**
   * Parent folder property name.
   */
  public static final String PARENT_FOLDER_PROPERTY = RepositoryResources.getString("folderParentFolderProperty");

  /**
   * Bean.
   */
  protected Object mBean;

  /**
   * Create a card panel for each type of folder and editing components for
   * each type's properties.
   */
  public PromotionalContentEditor() {
    super();

    String[] readOnly = { PATH_PROPERTY };
    setReadOnlyProperties(readOnly);
  }

  /**
   * Override to add/remove propertyChangeListeners.
   *
   * @param pBean - bean object
   */
  public void setBean(Object pBean) {
    if (mBean instanceof DynamicBeanState) {
      ((DynamicBeanState) mBean).removePropertyChangeListener(this);
    }

    mBean = pBean;

    if (mBean instanceof DynamicBeanState) {
      ((DynamicBeanState) mBean).addPropertyChangeListener(this);
    }

    super.setBean(mBean);
  }

  //---------------------------------
  // PropertyChangeLister interface.
  //-------------------------------

  /**
   * If the file name is updated, update the ImageEditor file name field.
   *
   * @param event "PropertyChange" event
   */
  public void propertyChange(PropertyChangeEvent event) {
    String name = event.getPropertyName();
    Object newValue = event.getNewValue();

    try {
      // when the "parentFolder" is updated, update the "path"
      if (name.equals(PARENT_FOLDER_PROPERTY)) {
        // the "path" is the folder path plus the file name 
        String fileName = (String) DynamicBeans.getPropertyValue(mBean, NAME_PROPERTY);
        String folderPath = null;

        if (newValue instanceof RepositoryFolderNode) {
          folderPath = ((RepositoryFolderNode) newValue).getItemPath();
        }

        String newPath = constructPath(fileName, folderPath);

        DynamicBeans.setPropertyValue(mBean, PATH_PROPERTY, newPath);
      }

      // when the file "name" changes, update the "path"
      if (name.equals(NAME_PROPERTY)) {
        // the "path" is the folder path plus the file name
        Object obj = DynamicBeans.getPropertyValue(mBean, PARENT_FOLDER_PROPERTY);

        String folderPath = null;

        if (obj instanceof RepositoryFolderNode) {
          folderPath = ((RepositoryFolderNode) obj).getItemPath();
        }

        String newPath = constructPath((String) newValue, folderPath);

        DynamicBeans.setPropertyValue(mBean, PATH_PROPERTY, newPath);
      }
    } catch (PropertyNotFoundException e) {
      System.err.println(e);
    }
  }

  /**
   * The path is constructed from a file name and a folder path.
   *
   * @param pFileName file name
   * @param pFolderPath foder path
   *
   * @return string that contains path which constructed from a file name and folder path
   */
  protected String constructPath(String pFileName, String pFolderPath) {
    if ((pFileName == null) && (pFolderPath == null)) {
      return "/";
    }

    if (pFileName == null) {
      return pFolderPath;
    }

    if (pFolderPath == null) {
      return pFileName;
    }

    if ((pFolderPath.length() > 0) && (pFolderPath.charAt(pFolderPath.length() - 1) == '/')) {
      return pFolderPath + pFileName;
    } else {
      return pFolderPath + "/" + pFileName;
    }
  }
}
