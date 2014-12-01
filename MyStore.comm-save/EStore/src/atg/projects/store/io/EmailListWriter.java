/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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
package atg.projects.store.io;

import atg.projects.store.profile.StorePropertyManager;

import atg.repository.RepositoryItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;


/**
 * The EmailListWriter creates the output file with the list of emails generated
 * from the GenerateEmailList action.
 *
 * @author ATG
 * @version 1.2
 */
public class EmailListWriter extends StoreFileWriter {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/io/EmailListWriter.java#3 $$Change: 635816 $";

  /**
   * Property manager.
   */
  private StorePropertyManager mPropertyManager;

  /**
   * Profile property list.
   */
  private List mProfilePropertyList;

  /**
   * Retrieves the PropertyManager component.
   *
   * @return StorePropertyManager
   */
  private StorePropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  /**
   * Sets the PropertyManager component.
   *
   * @param pPropertyManager
   *            The propertyManager to set.
   */
  public void setPropertyManager(StorePropertyManager pPropertyManager) {
    mPropertyManager = pPropertyManager;
  }

  /**
   * @return the profile property list.
   */
  public List getProfilePropertyList() {
    return mProfilePropertyList;
  }

  /**
   * @param pProfilePropertyList - the profile property list to set.
   */
  public void setProfilePropertyList(List pProfilePropertyList) {
    mProfilePropertyList = pProfilePropertyList;
  }

  /**
   * Outputs the specified profile properties to the output file.
   *
   * @param pEmailList - list of profiles
   * @throws IOException if there was an error while executing the code
   */
  public void write(List pEmailList) throws IOException {
    RepositoryItem profile;
    RepositoryItem emailRecipient;
    Iterator iterator = pEmailList.iterator();
    StorePropertyManager propManager = getPropertyManager();
    FileWriter fileWriter = null;
    BufferedWriter writer = null;
    File file = new File(getFileDirectory(), getFileName());
    Iterator propertyIterator = getProfilePropertyList().iterator();

    String propertyName;
    String propertyValue;

    try {
      fileWriter = new FileWriter(file);
      writer = new BufferedWriter(fileWriter);

      while (iterator.hasNext()) {
        StringBuffer buffer = new StringBuffer();
        profile = (RepositoryItem) iterator.next();
        emailRecipient = (RepositoryItem) profile.getPropertyValue(propManager.getEmailRecipientPropertyName());

        // iterate over proeprty list to get the properties we want
        while (propertyIterator.hasNext()) {
          propertyName = (String) propertyIterator.next();
          propertyValue = (String) profile.getPropertyValue(propertyName);
          //row = row + propertyValue + getDelimiter();
          buffer.append(propertyValue).append(getDelimiter());
        }

        // add the sourceCode value from the emailRecipient repository
        // item
        //
        // row = row +
        //       (String)emailRecipient.getPropertyValue(propManager.getSourceCodePropertyName());
        //
        buffer.append((String) emailRecipient.getPropertyValue(propManager.getSourceCodePropertyName()));

        if (isLoggingDebug()) {
          logDebug("Row: " + buffer.toString());
        }

        writer.write(buffer.toString());
        writer.newLine();
      }
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  /**
   * Outputs the specified profile properties to the output file.
   *
   * @param pItems - an array of profiles
   * @throws IOException if there was an error while executing the code
   */
  public void write(RepositoryItem[] pItems) throws IOException {
    RepositoryItem profile;
    RepositoryItem emailRecipient;
    FileWriter fileWriter = null;
    BufferedWriter writer = null;
    StorePropertyManager propManager = getPropertyManager();
    File file = new File(getFileDirectory() + getFileName());

    try {
      fileWriter = new FileWriter(file);
      writer = new BufferedWriter(fileWriter);

      String propertyName;
      String propertyValue;

      for (int i = 0; i < pItems.length; i++) {
        profile = (RepositoryItem) pItems[i];
        emailRecipient = (RepositoryItem) profile.getPropertyValue(propManager.getEmailRecipientPropertyName());

        Iterator propertyIterator = getProfilePropertyList().iterator();
        StringBuffer buffer = new StringBuffer();

        // iterate over proeprty list to get the properties we want
        while (propertyIterator.hasNext()) {
          propertyName = (String) propertyIterator.next();
          propertyValue = (String) profile.getPropertyValue(propertyName);
          buffer.append(propertyValue).append(getDelimiter());
        }

        // add the sourceCode value from the
        // emailRecipient repository item
        if (emailRecipient != null) {
          String sourceCode = (String) emailRecipient.getPropertyValue(propManager.getSourceCodePropertyName());

          if (sourceCode != null) {
            buffer.append(sourceCode);
          } else {
            if (isLoggingDebug()) {
              logDebug("Profile " + profile.getRepositoryId() + " does not have a sourceCode");
            }

            buffer.append("empty sourceCode");
          }
        } else {
          if (isLoggingDebug()) {
            logDebug("Profile " + profile.getRepositoryId() + " does not have an emailRecipient");
          }

          buffer.append("empty sourceCode");
        }

        if (isLoggingDebug()) {
          logDebug("Row: " + buffer.toString());
        }

        writer.write(buffer.toString());
        writer.newLine();
        buffer.setLength(0);
      }
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }
}
