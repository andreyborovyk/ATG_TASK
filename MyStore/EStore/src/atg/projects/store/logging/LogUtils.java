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
package atg.projects.store.logging;


/**
 * <p>This class provides static methods to format error messages to meet
 * the error logging requirements.  The logging requirements
 * are as follows:
 * <p>Each error message must be on a single line of wrapped text.
 * The error messages will be formatted as follows:
 * <p><b>Dynamo Criticality&lt;FS&gt;Date&lt;FS&gt;Time&lt;fs&gt;Store
 * Prefix&lt;fs&gt;Store Criticality&lt;fs&gt;Error message</b>
 * <p>&lt;fs&gt; = field separator.  Totality recommends using 4 spaces as a
 * field separator. It's preferred to have a field separator between the
 * date and time fields.
 * <p>For example:<br>
 * **** Error Sun Jun 24 2003    21:08:05 PDT    BP    Critical
 * Process XYZ unable to connect to the database
 * <p>The parsing will look for the critically, and then parse the message
 * accordingly
 * <p>Please use the following levels of critically:
 * <ul>
 * <li>Critical:  An error message that requires a system administrator
 * to fix something immediately.
 * <li>Major:  An error message that requires a system administrator
 * to perform some sort of investigation.
 * <li>Minor:  An error message that doesn't require action by a
 * system administrator. These error messages are used to corroborate Critical and Major messages.
 * </ul>
 * <p>This class contains static methods to convert strings to the desired
 * criticality
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/logging/LogUtils.java#3 $
 */
public class LogUtils {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/logging/LogUtils.java#3 $$Change: 635816 $";

  /**
   * The field separator.
   */
  public static final String FS = "\t";

  /**
   * The critical prefix.
   */
  public static final String CRITICAL_PREFIX = FS + "BP" + FS + "Critical" + FS;

  /**
   * The majore prefix.
   */
  public static final String MAJOR_PREFIX = FS + "BP" + FS + "Major" + FS;

  /**
   * The minor prefix.
   */
  public static final String MINOR_PREFIX = FS + "BP" + FS + "Minor" + FS;

  /**
   * Private constructor.
   */
  private LogUtils() {
  }

  /**
   * Formats a string by prefixing it with the critical error
   * prefix.
   * @param pString String to format
   * @return formatted string
   */
  public static String formatCritical(String pString) {
    return CRITICAL_PREFIX + pString;
  }

  /**
   * Formats a string by prefixing it with the major error
   * prefix.
   * @param pString String to format
   * @return formatted string
   */
  public static String formatMajor(String pString) {
    return MAJOR_PREFIX + pString;
  }

  /**
   * Formats a string by prefixing it with the minor error
   * prefix.
   * @param pString String to format
   * @return formatted string
   */
  public static String formatMinor(String pString) {
    return MINOR_PREFIX + pString;
  }
}
