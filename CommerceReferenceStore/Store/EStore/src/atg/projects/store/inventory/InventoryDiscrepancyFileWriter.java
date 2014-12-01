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
package atg.projects.store.inventory;

import atg.projects.store.io.StoreFileWriter;
import atg.projects.store.logging.LogUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;


/**
 * <p>
 * This class is used when a "ATPC" inventory file is processed. The
 * "ATPC" (acronym for Ability To Promise Count) files are processed
 * by the DailyInventoryFileReader. If there is a discrepancy between
 * the incoming inventory level, and the inventory level in the ATG
 * inventory repository, then an "InventoryDiscrepancyMessage" is created.
 * This class will take these messages, and create the file, and write
 * it to the NFS-mounted SAP directory.
 *
 * <p>
 * The SAP team has explained that they never use the existing
 * discrepancy file. This is really just for tracking down an issue
 * if the inventories become wildly out of synch, and is not used
 * in day-to-day operations.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/inventory/InventoryDiscrepancyFileWriter.java#2 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class InventoryDiscrepancyFileWriter extends StoreFileWriter {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/inventory/InventoryDiscrepancyFileWriter.java#2 $$Change: 651448 $";

  /**
   * Default prefix constant.
   */
  static final String DEFAULT_PREFIX = "ATPDISCREPANCY";

  /**
   * Default prefix.
   */
  protected String mPrefix = DEFAULT_PREFIX;

  /**
   * @return the prefix.
   */
  public String getPrefix() {
    return mPrefix;
  }

  /**
   * @param pPrefix - the prefix to set.
   */
  public void setPrefix(String pPrefix) {
    mPrefix = pPrefix;
  }

  /**
   * <p>
   * This method iterates through the passed in list of InventoryDiscrepancyMessage
   * beans and writes the bean contents to a flat file on the file system.
   *
   * <p>
   * As with all FileWriters in the system, this one uses the configured
   * "FileDirectory" to determine the location to write the file. Each
   * file has the timestamp appended to it.
   *
   * @param pList - list
   * @throws IOException if IO error occurs
   * @see atg.projects.store.inventory.InventoryDiscrepancyMessage
   */
  public void write(List pList) throws IOException {
    FileWriter out = null;
    String fileDirectory = getFileDirectory();
    String fileName = getFileName();
    StringBuffer sb = new StringBuffer();

    for (int j = 0; j < pList.size(); j++) {
      sb.append(createDiscrepancyLine((InventoryDiscrepancyMessage) pList.get(j)));

      InventoryDiscrepancyMessage message = (InventoryDiscrepancyMessage) pList.get(j);

      if (isLoggingDebug()) {
        logDebug("Discrepancy details: desc: " + message.getDescription() + " current qty: " +
          message.getOldQuantity() + " new qty: " + message.getNewQuantity() + " Sku id: " + message.getSkuId());
      }
    }

    try {
      // create File and FileOutputStream
      File f = new File(fileDirectory + "/" + fileName);
      out = new FileWriter(f);
      out.write(sb.toString());
      out.close();
    } catch (IOException ioe) {
      if (isLoggingError()) {
        logError(LogUtils.formatCritical("Error writing to file: " + ioe));
      }

      return;
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException io) {
          if (isLoggingError()) {
            logError(LogUtils.formatCritical("IOException trying to close file: " + io));
          }

          return;
        }
      }
    }
  }

  /**
   * Creates a line to add to the discrepancy file.
   *
   * @param pMessage - new message to add
   * @return resulted string with message
   */
  public String createDiscrepancyLine(InventoryDiscrepancyMessage pMessage) {
    return getPrefix() + getDelimiter() + pMessage.getSkuId() + getDelimiter() + pMessage.getDescription() +
    getDelimiter() + pMessage.getNewQuantity() + getDelimiter() + pMessage.getOldQuantity() + "\n";
  }
}
