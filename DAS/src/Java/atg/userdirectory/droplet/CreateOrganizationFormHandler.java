/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.userdirectory.droplet;

// Java classes
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.SystemException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

// DAS classes
import atg.droplet.TransactionalFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.core.util.StringUtils;
import atg.core.util.ResourceUtils;
import atg.repository.RepositoryException;
import atg.droplet.DropletException;
import atg.servlet.RequestLocale;

// DPS classes
import atg.userdirectory.repository.*;
import atg.userdirectory.*;

// DSS classes

// DCS classes

/**
 * This formhandler is used to create a new organization 
 * in the user directory.  Since the {@link atg.userdirectory.UserDirectory
 * <code>UserDirectory</code>} interface does not provide factory
 * methods to create new organizations, we rely on a concrete implementation
 * to create the Organizations.  This is the 
 * <code>getRepositoryUserDirectory</code> method.  
 * 
 * <p>In addition to creating a new organization the following properties
 * can be set on the organization <br>
 *
 * <UL>
 *   <LI>Organization Name
 *   <LI>Organization Description
 *   <LI>Parent Organization
 * </UL>
 *
 * <BR>Finally, this formhandler provides additonal functionality to
 *  create relative roles for the newly created organization as well
 *  as assign those roles to a particular principal.
 *
 * <P>The following properties will typically be set in a properties file
 *   <dl>
 *   <dt>assignableFunctionNames
 *   <dd>The list of "functions" that should get assigned to a user.  
 *    These functions are used to extract a relativeRole from an
 *    organization and then assign that relativeRole to the user.
 *   <dt>functionNames
 *   <dd>This is a list of "functions" that will be used to create
 *    relativeRoles on a particular organization.
 *   <dt>repositoryUserDirectory
 *   <dd>Specifies a repositoryUserDirectory component that is used
 *    to interact with the userdirectory API.  It has to be a
 *    concrete implementation of the userdirectory API because we need
 *    to be able to create objects from it.
 *   </dl>
 *
 *  <P>The formhandler supports the following submit methods
 *  <dl>
 *    <dt>createOrganization
 *    <dd>This function actually creates a new organization using the 
 *    the <code>repositoryUserDirectory</code> as the means to create
 *    the new organization.
 *  </dl>
 * 
 *  <P>The following properties might be set in the jhtml page
 *  <dl>
 *    <dt>organizationName
 *    <dd>The name of the organization that is going to be created.  This
 *    will typically be set in form input fields.
 *    <dt>organizationDescription
 *    <dd>The description of the organization that is going to be created.
 *    This will typcially be set in form input fields.
 *    <dt>parentOrganizationId
 *    <dd>The id of the parentOrganization for the newly created organization.
 *    <dt>createRelativeRoles
 *    <dd>If this property is set to true, then relativeRoles will be created.
 *    a relative role is created using the list of functions specified in the
 *    <code>functionNames</code> string array.
 *    
 *    <dt>assignRelativeRoles
 *    <dd>If this property is set to true, then the list of relativeRoles
 *     specified in the <code>assignableFunctionNames</code> will be
 *     assigned to a user.
 *    
 *    <dt>userId
 *    <dd>The user whom the relativeRoles listed in
 *    <code>assignableFunctionNames</code> will be assigned to.
 *  </dl>
 *
 * 
 * 
 * @see atg.userdirectory.repository.RepositoryUserDirectory
 * @author Ashley J. Streb
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/userdirectory/droplet/CreateOrganizationFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: A form handler that creates organizations and relative roles
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 */

public class CreateOrganizationFormHandler
  extends TransactionalFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/userdirectory/droplet/CreateOrganizationFormHandler.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String ERR_ORG_ALREADY_EXISTS = 
    "ERR_ORG_ALREADY_EXISTS";

  public static final String ERR_NO_ORG_NAME = 
    "ERR_NO_ORG_NAME";

  public static final String ERR_NO_ORG_DESC = 
    "ERR_NO_ORG_DESC";

  public static final String ERR_NO_PARENT_ORG_ID = 
    "ERR_NO_PARENT_ORG_ID";
  
  public static final String ERR_UNABLE_TO_ADD_ORG = 
    "ERR_UNABLE_TO_ADD_ORG";
  
  public static final String ERR_NO_USER_PKEY = 
    "ERR_NO_USER_PKEY";

  public static final String ERR_UNABLE_TO_ASSIGN_ROLES = 
    "ERR_UNABLE_TO_ASSIGN_ROLES";
  
  public static final String ERR_UNABLE_TO_CREATE_ROLES = 
    "ERR_UNABLE_TO_CREATE_ROLES";
  
  public static final String ERR_UNABLE_ASSIGN_REL_ROLE =
    "ERR_UNABLE_ASSIGN_REL_ROLE";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------


  //---------------------------------------------------------------------
  // property: organization
  Organization mOrganization;

  /**
   * Return the organization property.  This property gets set by the
   * <code>createOrganization()</code> method.  This allows later functions
   * such as postCreateOrganization to access the newly created organization.
   *
   * This method should <i>not</i> be set via jhtml or properties file
   * @return the newly created organization
   */
  public Organization getOrganization() {
    return mOrganization;
  }

  /**
   * Set the organization property.
   * @param pOrganization
   */
  public void setOrganization(Organization pOrganization) {
    mOrganization = pOrganization;
  }

  //---------------------------------------------------------------------
  // property: userId
  String mUserId;

  /**
   * The user who will get assigned relativeRoles if the 
   * <code>assignRelativeRoles</code> property is set to true.  The id 
   * value of this property is used to lookup the user in the userdirectory
   * by their primaryKey.
   *
   * @return the id of the user whom will be assigned relativeRoles
   */
  public String getUserId() {
    return mUserId;
  }

  /**
   * Set the userId property.
   * @param pUserId
   */
  public void setUserId(String pUserId) {
    mUserId = pUserId;
  }



  //---------------------------------------------------------------------
  // property: createRelativeRoles
  boolean mCreateRelativeRoles = true;

  /**
   * Indicates whether or not relativeRoles should be created for a particular
   * organization/function combination.  This defaults to true.
   *
   * @return true if relativeRoles should be created
   */
  public boolean isCreateRelativeRoles() {
    return mCreateRelativeRoles;
  }

  /**
   * Set the createRelativeRoles property.
   * @param pCreateRelativeRoles
   * @beaninfo description: true if relative roles are to be created
   *                        for this organization
   */
  public void setCreateRelativeRoles(boolean pCreateRelativeRoles) {
    mCreateRelativeRoles = pCreateRelativeRoles;
  }

  //---------------------------------------------------------------------
  // property: assignRelativeRoles
  boolean mAssignRelativeRoles = true;

  /**
   * Indicates whether or not relativeRoles should be assigned to
   * the user specified by the <code>userId</code> property. This
   * defaults to true.
   * 
   * @return true if relative roles should be assigned
   */
  public boolean isAssignRelativeRoles() {
    return mAssignRelativeRoles;
  }

  /**
   * Set the assignRelativeRoles property.
   * @param pAssignRelativeRoles
   * @beaninfo description: true if any created relative roles are
   *     to be assigned to the user filling out the form
   */
  public void setAssignRelativeRoles(boolean pAssignRelativeRoles) {
    mAssignRelativeRoles = pAssignRelativeRoles;
  }

  //---------------------------------------------------------------------
  // property: assignableFunctionNames
  String[] mAssignableFunctionNames;

  /**
   * Lists the relativeRoles that should be assigned to a 
   * particular user.
   * @return the list of relativeRoles that should be assigned
   */
  public String[] getAssignableFunctionNames() {
    return mAssignableFunctionNames;
  }

  /**
   * Set the assignableFunctionNames property.
   * @param pAssignableFunctionNames
   * @beaninfo description: a list of function names that will
   *        be assigned to the user filling out the form
   */
  public void setAssignableFunctionNames(String[] pAssignableFunctionNames) {
    mAssignableFunctionNames = pAssignableFunctionNames;
  }

  //---------------------------------------------------------------------
  // property: FunctionNames
  String[] mFunctionNames;

  /**
   * The list of fucntion names.  This list will be used to create
   * relativeRoles specific to the newly created organization.
   * @return the list of functions
   */
  public String[] getFunctionNames() {
    return mFunctionNames;
  }

  /**
   * Set the FunctionNames property.
   * @param pFunctionNames
   * @beaninfo description: a list of function names when creating relative
   *        roles for this organization
   */
  public void setFunctionNames(String[] pFunctionNames) {
    mFunctionNames = pFunctionNames;
  }

  //---------------------------------------------------------------------
  // property: orgNameRequired
  boolean mOrgNameRequired = true;

  /**
   * Indicates whether or not an organization name is required.
   * Defaults to true.
   *
   * @return true if the org name is required
   */
  public boolean isOrgNameRequired() {
    return mOrgNameRequired;
  }

  /**
   * Set the orgNameRequired property.
   * @param pOrgNameRequired
   * @beaninfo description: if true, then a name for the organization
   *       is required
   */
  public void setOrgNameRequired(boolean pOrgNameRequired) {
    mOrgNameRequired = pOrgNameRequired;
  }

  
  //---------------------------------------------------------------------
  // property: orgDescriptionRequired
  boolean mOrgDescriptionRequired = false;

  /**
   * Indicates whether or not an organization description is required
   * or not. False by default.
   *
   * @return true if the description is required
   */
  public boolean isOrgDescriptionRequired() {
    return mOrgDescriptionRequired;
  }

  /**
   * Set the orgDescriptionRequired property.
   * @param pOrgDescriptionRequired
   * @beaninfo description: if true, then a description for the new
   *        organization is required
   */
  public void setOrgDescriptionRequired(boolean pOrgDescriptionRequired) {
    mOrgDescriptionRequired = pOrgDescriptionRequired;
  }

  
  //---------------------------------------------------------------------
  // property: parentOrgRequired
  boolean mParentOrgRequired = false;

  /**
   * Indicats whether or not a parentOrg is required in order
   * to create a new organization.
   *
   * @return true if a parentOrg is required in order to create 
   * a new organization
   */
  public boolean isParentOrgRequired() {
    return mParentOrgRequired;
  }

  /**
   * Set the parentOrgRequired property.
   * @param pParentOrgRequired
   * @beaninfo description: if true, than a parent organization must
   *        be assigned to this new organization
   */
  public void setParentOrgRequired(boolean pParentOrgRequired) {
    mParentOrgRequired = pParentOrgRequired;
  }

  //---------------------------------------------------------------------
  // property: userLocale
  String mUserLocale;

  /**
   * Return the userLocale property.
   * @return users locale.
   */
  public String getUserLocale() {
    return mUserLocale;
  }

  /**
   * Set the userLocale property.
   * @param pUserLocale
   * @beaninfo description: The locale to use when printing localized
   *        messages
   */
  public void setUserLocale(String pUserLocale) {
    mUserLocale = pUserLocale;
  }


  //---------------------------------------------------------------------
  // property: createOrganizationSuccessURL
  String mCreateOrganizationSuccessURL;

  /**
   * URL to redirect to on success.
   *
   * @return location to redirect to on success
   */
  public String getCreateOrganizationSuccessURL() {
    return mCreateOrganizationSuccessURL;
  }

  /**
   * Set the createOrganizationSuccessURL property.
   * @param pCreateOrganizationSuccessURL
   * @beaninfo description the URL to redirect to if the organization
   *        is successfully created
   */
  public void setCreateOrganizationSuccessURL(String pCreateOrganizationSuccessURL) {
    mCreateOrganizationSuccessURL = pCreateOrganizationSuccessURL;
  }

  
  //---------------------------------------------------------------------
  // property: createOrganizationErrorURL
  String mCreateOrganizationErrorURL;

  /**
   * URL to redirect to on error.
   *
   * @return location to redirect to on error
   */
  public String getCreateOrganizationErrorURL() {
    return mCreateOrganizationErrorURL;
  }

  /**
   * Set the createOrganizationErrorURL property.
   * @param pCreateOrganizationErrorUR
   * @beaninfo description: the URL to redirect to if there was an error
   *        trying to create the organization
   */
  public void setCreateOrganizationErrorURL(String pCreateOrganizationErrorURL) {
    mCreateOrganizationErrorURL = pCreateOrganizationErrorURL;
  }

  

  //---------------------------------------------------------------------
  // property: OrganizationName
  String mOrganizationName;

  /**
   * The name of the organization that is going to be created
   *
   * @return organization name
   */
  public String getOrganizationName() {
    return mOrganizationName;
  }

  /**
   * Set the OrganizationName property.
   * @param pOrganizationName
   * @beaninfo the name of the organization to create
   */
  public void setOrganizationName(String pOrganizationName) {
    mOrganizationName = pOrganizationName;
  }

  
  //---------------------------------------------------------------------
  // property: OrganizationDescription
  String mOrganizationDescription;

  /**
   * The description of the organization that is going to be created
   *
   * @return the description of the organization that is about to be created
   */
  public String getOrganizationDescription() {
    return mOrganizationDescription;
  }

  /**
   * Set the OrganizationDescription property.
   * @param pOrganizationDescription
   * @beaninfo description: A description of the newly created organization
   */
  public void setOrganizationDescription(String pOrganizationDescription) {
    mOrganizationDescription = pOrganizationDescription;
  }

  
  //---------------------------------------------------------------------
  // property: ParentOrganizationId
  String mParentOrganizationId;

  /**
   * The id of the organization that will act as the parent for the newly created
   * organization.
   *
   * @return id of the parent organization
   */
  public String getParentOrganizationId() {
    return mParentOrganizationId;
  }

  /**
   * Set the ParentOrganizationId property.
   * @param pParentOrganizationId
   * @beaninfo description: The id of the parent organization of this
   *        new organization
   */
  public void setParentOrganizationId(String pParentOrganizationId) {
    mParentOrganizationId = pParentOrganizationId;
  }

  //---------------------------------------------------------------------
  // property: RepositoryUserDirectory
  RepositoryUserDirectory mRepositoryUserDirectory;

  /**
   * References the userDirectory that will be used to create
   * and manipulate organizations.
   *
   * @return the RepositoryUserDirectory
   */
  public RepositoryUserDirectory getRepositoryUserDirectory() {
    return mRepositoryUserDirectory;
  }

  /**
   * Set the RepositoryUserDirectory property.
   * @param pRepositoryUserDirectory
   * @beaninfo description: The user directory used to create the 
   *        organization
   */
  public void setRepositoryUserDirectory(RepositoryUserDirectory pRepositoryUserDirectory) {
    mRepositoryUserDirectory = pRepositoryUserDirectory;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  
  /**
   * Called before any work is done by the <code>handleCreateOrganization
   * </code> method.  This currently does nothing.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preCreateOrganization(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
  }

  /**
   * This is called after all work is done by the <code>
   * handleCreateOrganization</code> method.  This will
   * first check to see if the <code>createRelativeRoles</code> method
   * should be invoked by checking the createRelativeRoles property.
   *
   * <P>After creatRelativeRoles is optionally called, the method
   * will see if the transaction is rolled back.  If it is not then
   * it will see if it should invoke the <code>assignRelativeRoles</code>
   * by checking the assignRelativeRoles property.
   *
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postCreateOrganization(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    if (isCreateRelativeRoles())
      createRelativeRoles(getOrganization(), pRequest, pResponse);
    
    if (isRollbackTransaction()) {
      return;
    }

    if (isAssignRelativeRoles())
      assignRelativeRoles(getOrganization(), pRequest, pResponse);
  }


  /**
   * This method creates relative roles for a particular organization.
   * An example of this is an organization might have a relativeRole
   * such as <b>Approver For Acme</code>.  That is, the user has the
   * <i>Approver</i> funtion, but it is relative to the <i>Acme</i>
   * organization.
   *
   * <P>This method creates relative roles by iterating through
   * the list of functions listed in the <code>functionNames</code>
   * property.  This roles are created relative to the 
   * <code>pOrganization</code> parameter.
   *
   * <P>If an error is encountered, the transaction will be marked
   * for rollback and a droplet exception will be generated.
   *
   * @param pOrganization organization for w2hich relative roles
   * will be created
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   */
  protected void createRelativeRoles(Organization pOrganization, 
                                     DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse) 
  {
    try {
    if (getFunctionNames() != null && getFunctionNames().length > 0) {
      for (int i=0; i<getFunctionNames().length; i++) {
        if (isLoggingDebug()) {
          logDebug("About to create relative role: " + getFunctionNames()[i] +
                   " for organization: " + pOrganization.getName());
        }
        pOrganization.createRelativeRole(getFunctionNames()[i]);
      }
    }
    }
    catch (DirectoryModificationException dme) {
      if (isLoggingError())
        logError(dme);
      setRollbackOnly();
      addFormException(ERR_UNABLE_TO_CREATE_ROLES, pRequest, pResponse);
    }
  }
  
  /**
   * This method will assign relativeRoles to the user whose
   * primary key is returned by the {@link #getUserId<code>getUserId</code>}
   * method.
   *
   * <P>After the user is extracted from the userDirectory via
   * the primaryKey, the values returned by the 
   * <code>assignableFunctionNames</code> method will be used
   * to get relativeRoles from the organization and then assign
   * them to the user.
   *
   * @param pOrganization the organization that the roles should be relative to
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   */
  protected void assignRelativeRoles(Organization pOrganization,
                                     DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse) 
  {
    if (StringUtils.isBlank(getUserId())) {
      addFormException(ERR_NO_USER_PKEY, pRequest, pResponse);
      setRollbackOnly();
      return;
    }

    User user = 
      getRepositoryUserDirectory().findUserByPrimaryKey(getUserId());
    
    if (user == null) {
      addFormException(ERR_NO_USER_PKEY, pRequest, pResponse);
      setRollbackOnly();
      return;
    }
    
    try {
      if (getAssignableFunctionNames() != null && 
          getAssignableFunctionNames().length > 0) {
        for (int i=0; i<getAssignableFunctionNames().length; i++) {
          if (isLoggingDebug())
            logDebug("About to assign role: " + 
                     getAssignableFunctionNames()[i] + " to user");

          Role role = 
            pOrganization.getRelativeRole(getAssignableFunctionNames()[i]);

          if (role == null) {
            if (isLoggingError()) {
              String msg = getStringResource(ERR_UNABLE_TO_ASSIGN_ROLES, 
                                             getLocale(pRequest, pResponse));
              logError(msg);
            }
            continue;

          }
          user.assignRole(role);
        }
      }
    }
    catch (DirectoryModificationException dme) {
      if (isLoggingError())
        logError(dme);
      addFormException(ERR_UNABLE_TO_ASSIGN_ROLES, pRequest, pResponse);
      setRollbackOnly();
      return;
    }
  }

  /**
   * This method is responsible for creating a new organization.
   * This method acts as a controller method for invoking other methods
   * in the correct order.  It will invoke the <code>preCreateOrganization,
   * createOrganization, and postCreateOrganization</code> methods.
   *
   * <P>If a form error is ever generated, the method will redirect
   * to the <code>createOrganizationErrorURL</code>.  After all processing
   * is done by the form the form will redirect to either the 
   * <code>createOrganizationErrorURL</code> or the 
   * <code>createOrganizationSuccessURL</code>.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleCreateOrganization(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    if (isLoggingDebug())
      logDebug("In handleCreateOrganization method");

    if (!checkFormRedirect(null, getCreateOrganizationErrorURL(), 
                           pRequest, pResponse))
      return false;
    
    preCreateOrganization(pRequest, pResponse);

    if (!checkFormRedirect(null, getCreateOrganizationErrorURL(), 
                           pRequest, pResponse))
      return false;
    
    createOrganization(pRequest, pResponse);

    if (!checkFormRedirect(null, getCreateOrganizationErrorURL(), 
                           pRequest, pResponse))
      return false;
    
    postCreateOrganization(pRequest, pResponse);

    return checkFormRedirect (getCreateOrganizationSuccessURL(), 
                              getCreateOrganizationErrorURL(), 
                              pRequest, pResponse);
  }

  
  /**
   * This method is responsible for creating a new organization.
   * It will first check to make sure that the necessary information
   * to create an organization is supplied.  It will check
   * the following properties:
   *
   * <UL>
   *   <LI>orgNameRequired 
   *   <LI>orgDescriptionRequired
   *   <LI>parentOrgRequired
   * </UL>
   *
   * <P>If any of the above checks fail, a droplet exception will
   * be generated specifying what piece of information was missing
   * and will return from the method.
   *
   * <P>Next the organization will be created using the 
   * <code>repositoryUserDirectory</code> property.  If an organization
   * is successfully created, the {@link #setOrganization(Organization)
   * <code>setOrganization</code>} method will be called.  Setting
   * the organization for the remainder of the formhandlers lifetime
   * allows later methods, such as the postCreateOrganization method
   * to access the organization.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   */
  protected void createOrganization(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
  {
    if (isLoggingDebug())
      logDebug("About to create organization");

    if (isOrgNameRequired()) {
      if (StringUtils.isBlank(getOrganizationName())) {
        addFormException(ERR_NO_ORG_NAME, pRequest, pResponse);
        return;
      }
    }

    if (isOrgDescriptionRequired()) {
      if (StringUtils.isBlank(getOrganizationDescription())) {
        addFormException(ERR_NO_ORG_DESC, pRequest, pResponse);
        return;
      }
    }

    if (isParentOrgRequired()) {
      if (StringUtils.isBlank(getParentOrganizationId())) {
        addFormException(ERR_NO_PARENT_ORG_ID, pRequest, pResponse);
        return;
      }
      
    }

    // check to see if the organization already exists.  If it does, we cannot create 
    // a new organization.
    Organization parentOrg = null;
    if (!StringUtils.isBlank(getParentOrganizationId())) {
      parentOrg = 
        getRepositoryUserDirectory().findOrganizationByPrimaryKey(getParentOrganizationId());
    }

    //unused Organization existingOrg = null;
    if (parentOrg != null &&
        !StringUtils.isBlank(getOrganizationName())) {
      Collection childOrgs = parentOrg.getChildOrganizations();
      if (childOrgs != null &&
          childOrgs.size() > 0) {
        Iterator it = childOrgs.iterator();
        while (it.hasNext()) {
          Organization childOrg = (Organization) it.next();
          if (childOrg.getName() != null &&
              childOrg.getName().equals(getOrganizationName())) {
            addFormException(ERR_ORG_ALREADY_EXISTS, pRequest, pResponse);
            return;
          }
        }
      }
    }

    try {
      Organization organization =
        getRepositoryUserDirectory().
	createOrganization(getOrganizationName(),
			   getOrganizationDescription(),
			   getParentOrganizationId());
      setOrganization(organization);
    }
    catch (RepositoryException rpe) {
      addFormException(ERR_UNABLE_TO_ADD_ORG, pRequest, pResponse);
      if (isLoggingError()) 
        logError(rpe);
      setRollbackOnly();
      return;
    }
    catch (DirectoryModificationException dme) {
      addFormException(ERR_UNABLE_TO_ADD_ORG, pRequest, pResponse);
      if (isLoggingError())
        logError(dme);
      setRollbackOnly();
      return;
    }
  }

  /**
   * Adds a form exception to the formhandler.  The exception
   * that is generated is created using the <code>pErrorKey</code>
   * to obtain a resource using the {@link #getStringResource(String, Locale)
   * <code>getStringResource</code>} method.
   *
   * @param pErrorKey the resource key
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   */
  protected void addFormException(String pErrorKey, 
                                  DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
  {
    String msg = getStringResource(pErrorKey, getLocale(pRequest, pResponse));
    addFormException(new DropletException(msg));
  }

  // Utility Methods 

  /**
   * Returns either the Locale from the Request object (if it isn't NULL),
   * or the Locale from the JVM.
   * @param pRequest the servlet's request
   * @return Either the Locale from the Request object, or the Locale 
   * from the JVM.
   */
  protected Locale getLocale (DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse) 
  {
    if (! StringUtils.isBlank(getUserLocale())) {
      return RequestLocale.getCachedLocale(getUserLocale());
    }
    else if (pRequest.getRequestLocale() != null) {
        return pRequest.getRequestLocale().getLocale();
    }
    else {
      return Locale.getDefault();
    }
  }

  /**
   * This method acts as a utility method to obtain a given
   * resource for a particular key.  This is used to lookup error
   * messages for the users locale.
   *
   * @param pResourceName the resource key used to obtain the resource
   * @param pLocale the users locale for which the resource bundle will be 
   *                obtained.
   * @return The String resource
   */
  public String getStringResource (String pResourceName,
                                   Locale pLocale)
  {
    try {
      return Utils.getClientStringResource(pResourceName, pLocale);
    } catch (MissingResourceException exc) {
      // Print the error to to stderr for good measure since these
      // exceptions might be getting called from a static initializer
      // and exceptions thrown during that could have some funky
      // results.
      if (isLoggingError())
        logError(exc);
    }
    return pResourceName;
  }

  /**
   * This method will call the <code>setRollbackTransaction</code>
   * method on the superclass.  Indicating that the transaction
   * should be rolled back.
   *
   */
  protected void setRollbackOnly() 
  {
    setRollbackTransaction(true);
  }

  
  
}   // end of class
