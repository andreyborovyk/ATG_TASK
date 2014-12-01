/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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
 * </ATGCOPYRIGHT>
 */
package atg.epub;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.epub.project.Process;
import atg.epub.project.ProcessHome;
import atg.epub.project.ProjectConstants;
import atg.nucleus.GenericService;
import atg.process.action.ActionConstants;
import atg.process.action.ActionException;
import atg.repository.RepositoryItem;
import atg.security.Persona;
import atg.security.ThreadSecurityManager;
import atg.security.User;
import atg.userdirectory.UserDirectoryUserAuthority;
import atg.versionmanager.VersionManager;
import atg.versionmanager.WorkingContext;
import atg.versionmanager.Workspace;
import atg.versionmanager.exceptions.VersionException;
import atg.workflow.ActorAccessException;
import atg.workflow.MissingWorkflowDescriptionException;
import atg.workflow.WorkflowConstants;
import atg.workflow.WorkflowException;
import atg.workflow.WorkflowManager;
import atg.workflow.WorkflowView;

import javax.ejb.CreateException;
import javax.transaction.TransactionManager;

/**
 * This class is meant as a starting point for building a programatic import of data into a versioned
 * repository or file system. This class provides the shell code which creates a project and advances
 * the workflow. The /Content Administration/import.wdl and /Content Administration/import-staging.wdl
 * workflows are meant to be used with this code.
 *
 * In order to use this code, the importUserData() method should be overridden in a subclass. This is where
 * all the logic for importing the data should be inserted.
 *
 * Note that the logic in this class is designed to execute within a single transaction. If the amount of
 * data being imported is substantial, then it is the responsibility of the developer to implement transaction
 * batching in the importUserData() method.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/Publishing/version/10.0.3/pws/sample-code/classes.jar/src/atg/epub/ProgramaticImportService.java#2 $
 */
public abstract class ProgramaticImportService extends GenericService {
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/Publishing/version/10.0.3/pws/sample-code/classes.jar/src/atg/epub/ProgramaticImportService.java#2 $$Change: 651448 $";

  //-------------------------------------
  /**
   * This abstract method is meant to be overridden in the user's subclass. This is where all the logic for
   * importing the user's data is to be done.
   */
  public abstract void importUserData(Process pProcess, TransactionDemarcation pTD) throws Exception;

  //-------------------------------------
  // property: transactionManager
  //-------------------------------------
  private TransactionManager mTransactionManager = null;

  /**
   * @return Returns the transactionManager.
   */
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  /**
   * @param pTransactionManager The transactionManager to set.
   */
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  //-------------------------------------
  // property: versionManager
  //-------------------------------------
  private VersionManager mVersionManager = null;

  /**
   * @return Returns the versionManager.
   */
  public VersionManager getVersionManager() {
    return mVersionManager;
  }

  /**
   * @param pVersionManager The versionManager to set.
   */
  public void setVersionManager(VersionManager pVersionManager) {
    mVersionManager = pVersionManager;
  }

  //-------------------------------------
  // property: workflowManager
  //-------------------------------------
  private WorkflowManager mWorkflowManager = null;

  /**
   * @return Returns the workflowManager.
   */
  public WorkflowManager getWorkflowManager() {
    return mWorkflowManager;
  }

  /**
   * @param pWorkflowManager The workflowManager to set.
   */
  public void setWorkflowManager(WorkflowManager pWorkflowManager) {
    mWorkflowManager = pWorkflowManager;
  }

  //-------------------------------------
  // property: userAuthority
  //-------------------------------------
  private UserDirectoryUserAuthority mUserAuthority = null;

  /**
   * Returns the UserAuthority
   */
  public UserDirectoryUserAuthority getUserAuthority() {
    return mUserAuthority;
  }

  /**
   * Sets the UserAuthority
   */
  public void setUserAuthority(UserDirectoryUserAuthority pUserAuthority) {
    mUserAuthority = pUserAuthority;
  }

  //-------------------------------------
  // property: personaPrefix
  //-------------------------------------
  private String mPersonaPrefix = "Profile$login$";

  /**
   * Returns the PersonaPrefix which is supplied for login
   */
  public String getPersonaPrefix() {
    return mPersonaPrefix;
  }

  /**
   * Sets the PersonaPrefix
   */
  public void setPersonaPrefix(String pPersonaPrefix) {
    mPersonaPrefix = pPersonaPrefix;
  }

  //-------------------------------------
  // property: userName
  //-------------------------------------
  private String mUserName = "publishing";

  /**
   * Returns the UserName which is supplied upon checkin and for logging in
   */
  public String getUserName() {
    return mUserName;
  }

  /**
   * Sets the UserName
   */
  public void setUserName(String pUserName) {
    mUserName = pUserName;
  }

  //-------------------------------------
  // property: workflowName
  //-------------------------------------
  private String mWorkflowName = "/Content Administration/import.wdl";

  /**
   * Returns the workflowName property
   */
  public String getWorkflowName() {
    return mWorkflowName;
  }

  /**
   * Sets the workflowName property
   */
  public void setWorkflowName(String string) {
    mWorkflowName = string;
  }

  //-------------------------------------
  // property: taskOutcomeId
  //-------------------------------------
  private String mTaskOutcomeId = "4.1.1";

  /**
   * @return Returns the taskOutcomeId.
   */
  public String getTaskOutcomeId() {
    return mTaskOutcomeId;
  }

  /**
   * @param pTaskOutcomeId The taskOutcomeId to set.
   */
  public void setTaskOutcomeId(String pTaskOutcomeId) {
    mTaskOutcomeId = pTaskOutcomeId;
  }

  //-------------------------------------
  // property: projectName
  //-------------------------------------
  private String mProjectName = "Content Administration Import";

  /**
   * @return Returns the projectName.
   */
  public String getProjectName() {
    return mProjectName;
  }

  /**
   * @param pProjectName The projectName to set.
   */
  public void setProjectName(String pProjectName) {
    mProjectName = pProjectName;
  }

  //-------------------------------------
  // Constructor
  //-------------------------------------
  public ProgramaticImportService() {
  }

  //-------------------------------------
  /**
   * This is the starting point for the service. In order to start it, the executeImport() method needs to
   * be called by another service. This method begins a transaction and sets the security
   * context on the thread for the user specified in the userName property. Next, it creates a project
   * and then calls importUserData(). Next, it attempts to advance the project's workflow. Finally, it
   * unsets the security context and commits the transaction.
   *
   * <b>NOTE!!! - This code only creates a single transaction and is suitable for imports which can fit
   * into the context of a single transaction. If you are doing large imports, then you must handle batching
   * the transaction in the importUserData() method.</b>
   */
  public void executeImport() throws VersionException, WorkflowException, CreateException, ActionException,
            TransactionDemarcationException, Exception
  {
    TransactionDemarcation td = new TransactionDemarcation();
    boolean rollback = true;

    try {
      td.begin(getTransactionManager());

      assumeUserIdentity();

      String projectName = getProjectName();

      ProcessHome processHome = ProjectConstants.getPersistentHomes().getProcessHome();
      Process process = processHome.createProcessForImport(projectName, getWorkflowName());
      String wkspName = process.getProject().getWorkspace();
      Workspace wksp = getVersionManager().getWorkspaceByName(wkspName);
      WorkingContext.pushDevelopmentLine(wksp);

      importUserData(process, td);

      advanceWorkflow(process);
      rollback = false;
    }
    catch(VersionException e) {
      throw e;
    }
    catch (TransactionDemarcationException e) {
      throw e;
    }
    catch (CreateException e) {
      throw e;
    }
    catch (WorkflowException e) {
      throw e;
    }
    catch (ActionException e) {
      throw e;
    }
    catch (Exception e) {
      throw e;
    }
    finally {
      releaseUserIdentity();
      try {
        td.end(rollback);
      }
      catch (TransactionDemarcationException tde) {
        throw tde;
      }
      WorkingContext.popDevelopmentLine();
    }
  }

  //-------------------------------------
  /**
   * This method advances the workflow to the next state. If using an unaltered copy of the import-late
   * or import-early workflows, then the taskOutcomeId property should not need to be changed
   * (default is '4.1.1'). If you are using a different workflow or an altered version of the import-xxxx
   * workflows, then the taskOutcomeId can be found in the wdl file for the respective workflow.
   *
   * @param pProcess the atg.epub.project.Process object (the project)
   */
  protected void advanceWorkflow(Process pProcess) throws WorkflowException, ActionException
  {
    RepositoryItem processWorkflow = pProcess.getProject().getWorkflow();
    String workflowProcessName = processWorkflow.getPropertyValue("processName").toString();
    String subjectId = pProcess.getId();

    try {
      // an alternative would be to use the global workflow view at
      WorkflowView wv = getWorkflowManager().getWorkflowView(ThreadSecurityManager.currentUser());

      wv.fireTaskOutcome(workflowProcessName, WorkflowConstants.DEFAULT_WORKFLOW_SEGMENT,
                          subjectId,
                          getTaskOutcomeId(),
                          ActionConstants.ERROR_RESPONSE_DEFAULT);

    } catch (MissingWorkflowDescriptionException e) {
      throw e;
    } catch (ActorAccessException e) {
      throw e;
    } catch (ActionException e) {
      throw e;
    } catch (UnsupportedOperationException e) {
      throw e;
    }
  }

  //-------------------------------------
  /**
   * This method sets the security context for the current thread so that the code executes correctly
   * against secure resources.
   *
   * @return true if the identity was assumed, false otherwise
   */
  protected boolean assumeUserIdentity() {
    if (getUserAuthority() == null)
        return false;

    User newUser = new User();
    Persona persona = (Persona) getUserAuthority().getPersona(getPersonaPrefix() + getUserName());
    if (persona == null)
        return false;

    // create a temporary User object for the identity
    newUser.addPersona(persona);

    // replace the current User object
    ThreadSecurityManager.setThreadUser(newUser);

    return true;
  }

  //-------------------------------------
  /**
   * This method unsets the security context on the current thread.
   */
  protected void releaseUserIdentity()
  {
    ThreadSecurityManager.setThreadUser(null);
  }
}
