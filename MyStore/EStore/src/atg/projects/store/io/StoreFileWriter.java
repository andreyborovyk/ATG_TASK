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

import atg.nucleus.GenericService;

import java.io.IOException;

import java.util.List;


/**
 * Abstract class for Store FileWriters. This class just creates the
 * common getters and setters that all file writers will need. As a result
 * there is very little Javadoc here.
 *
 * All subclasses should override/implement the "write" method.
 *
 * @see EmailListWriter
 * @see atg.projects.store.inventory.InventoryDiscrepancyFileWriter
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public abstract class StoreFileWriter extends GenericService {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/io/StoreFileWriter.java#3 $$Change: 635816 $";

  /**
   * Delimiter.
   */
  protected Character mDelimiter;

  /**
   * File directory.
   */
  protected String mFileDirectory;

  /**
   * File name.
   */
  protected String mFileName;

  /**
   * @return the delimiter.
   */
  public Character getDelimiter() {
    return mDelimiter;
  }

  /**
   * @param pDelimiter - the delimiter to set.
   */
  public void setDelimiter(Character pDelimiter) {
    mDelimiter = pDelimiter;
  }

  /**
   * @return the file directory.
   */
  public String getFileDirectory() {
    return mFileDirectory;
  }

  /**
   * @param pFileDirectory - the file directory to set.
   */
  public void setFileDirectory(String pFileDirectory) {
    mFileDirectory = pFileDirectory;
  }

  /**
   * @return the file name.
   */
  public String getFileName() {
    return mFileName;
  }

  /**
   * @param pFileName - the file name to set.
   */
  public void setFileName(String pFileName) {
    mFileName = pFileName;
  }

  /**
   * This is the method that is responsible for writing to a file.
   * The list can contain any object type that is written to file.
   *
   * @param pList - list of items to write
   * @throws IOException i/o exception
   *
   */
  public void write(List pList) throws IOException {
  }
}
