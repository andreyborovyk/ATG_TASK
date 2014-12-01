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
package atg.projects.store.scenario.action;

import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;

import atg.process.action.ActionException;
import atg.process.action.ActionImpl;
import atg.process.action.FailedActionInfo;

import atg.projects.store.io.EmailListWriter;

import atg.repository.RepositoryItem;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Custom action to generate a list of emails for targeted email campaigns.
 *
 * @author ATG
 * @version $Id: GenerateEmailList.java,v 1.4 2004/09/10 15:58:02 sdere
 *          Exp $
 */
public class GenerateEmailList extends ActionImpl {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/scenario/action/GenerateEmailList.java#3 $$Change: 635816 $";

  /**
   * File name constant.
   */
  static final String FILE_NAME = "fileName";

  /**
   * File name.
   */
  private String mFileName;

  /**
   * Email list writer.
   */
  private EmailListWriter mWriter;

  /**
   * Sets the name of the output file..
   *
   * @param pFileName - file name of output file.
   */
  public void setFileName(String pFileName) {
    mFileName = pFileName;
  }

  /**
   * Retrieves the name of the output file.
   *
   * @return output file name.
   */
  public String getFileName() {
    return mFileName;
  }

  /**
   * Returns the name of the action.
   *
   * @return the action name.
   */
  public String getActionName() {
    return "generateEmailList";
  }

  /**
   * Retrieves the e-mail list writer component.
   *
   * @return the writer.
   */
  public EmailListWriter getWriter() {
    return mWriter;
  }

  /**
   * Sets the e-mail list writer component.
   *
   * @param pWriter - the writer to set.
   */
  public void setWriter(EmailListWriter pWriter) {
    mWriter = pWriter;
  }

  /**
   * Configures the action using the given configuration object.  The
   * configuration object is typically a global Nucleus component which is
   * configured with the information necessary for the action's operation.
   *
   * @param pConfiguration - configuration
   *
   * @exception ProcessException if the action could not be configured - for
   *            example, because some of the required properties are missing
   *            from the configuration
   */
  public void configure(Object pConfiguration) throws ProcessException {
    GenerateEmailListConfiguration config = (GenerateEmailListConfiguration) pConfiguration;

    setWriter(config.getWriter());
  }

  /**
   * Override OOTB method so we can store our required parameters.
   *
   * @param pParameters - parameters
   * @see atg.process.action.Action#initialize(java.util.Map)
   * @throws ProcessException If the initialization could not be performed - for
   *            example, because some of the required parameters are missing
   */
  public void initialize(Map pParameters) throws ProcessException {
    super.initialize(pParameters);

    storeRequiredParameter(pParameters, FILE_NAME, String.class);
  }

  /**
   * Override OOTB method so we can set the file name.
   * @param pContext - process execution context
   * @see atg.process.action.ActionImpl#executeAction(atg.process.ProcessExecutionContext)
   * @throws ProcessException If the action could not be performed - for
   *            example, because some of the required parameters are missing
   */
  protected void executeAction(ProcessExecutionContext pContext)
    throws ProcessException {
    // We do not employ this method, since the email sender is capable
    // of processing arrays of recipients.
    setFileName((String) getParameterValue(FILE_NAME, pContext));
  }

  /**
   * Executes the action in the given process execution context.  The context
   * can be used to evaluate any parameter Expressions.
   *
   * @param pContext - process execution context
   *
   * @throws ActionException if the action failed when executed in the given
   *            context
   */
  public void execute(ProcessExecutionContext pContext)
    throws ActionException {
    RepositoryItem profile = pContext.getSubject();
    List recipients = new ArrayList();
    EmailListWriter writer = getWriter();

    try {
      executeAction(pContext);
      writer.setFileName(getFileName());
    } catch (ProcessException procExc) {
      throw new atg.process.action.ActionException(new FailedActionInfo(pContext, procExc));
    }

    recipients.add(profile);

    // output the list to a file
    try {
      writer.write(recipients);
    } catch (IOException exc) {
      exc.printStackTrace();
    }
  }

  /**
   * Executes the action in each of the given process execution contexts.  Each
   * context can be used to evaluate any parameter Expressions.
   *
   * @param pContexts if the action
   *
   * @exception ActionException if the action failed when executed in any of
   *            the given contexts
   */
  public void execute(ProcessExecutionContext[] pContexts)
    throws ActionException {
    if (pContexts.length == 1) {
      execute(pContexts[0]);

      return;
    }

    RepositoryItem[] recipients = new RepositoryItem[pContexts.length];

    for (int i = 0; i < pContexts.length; i++) {
      recipients[i] = pContexts[i].getSubject();
    }

    // output list to file
    EmailListWriter writer = getWriter();

    try {
      executeAction(pContexts[0]);
      writer.setFileName(getFileName());
      writer.write(recipients);
    } catch (ProcessException procExc) {
      throw new ActionException(new FailedActionInfo(pContexts[0], procExc));
    } catch (IOException exc) {
      exc.printStackTrace();
    }
  }
}
