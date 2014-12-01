/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.repository.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.adapter.xml.XMLRepositoryItem;
import atg.beans.DynamicPropertyDescriptor;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;

/**
 * This class is an extension of RepositoryFormHandler that has a few
 * additional features.
 *
 * <p>(1) You can refer to repository items by "name" rather than the
 * (less user friendly) repository id.  For example, instead of
 * setting the <code>repositoryId</code> property to
 * "/targetedcontent/foo.html," one can set <code>itemName</code> to
 * "foo," and also set the <code>itemFolder</code> and
 * <code>itemExtension</code> properties to "/targetedcontent/" and
 * ".html," respectively.  Setting these properties has the effect of
 * also updating the <code>repositoryId</code> property to the
 * corresponding repository id.
 *
 * <p>(2) There is a special <code>content</code> property, which
 * represents the content associated with the repository item.  The
 * <code>contentPropertyName</code> property specifies the actual
 * property name of the item which is to be treated as the "content."
 * Before the content property is set in the item, we insert paragraph
 * tags where the text's paragraphs start and end, thus making the
 * content appropriate for an item in an XML or HTML repository.  For
 * example, if the <code>contentParagraphTagName</code> property is
 * set to "p," the content's paragraphs are enclosed in the &lt;p&gt;
 * and &lt;/p&gt; tags.  When the content property is read back into
 * the form, the paragraph tags are stripped off again, so that the
 * user only sees regular text.
 *
 * @see RepositoryFormHandler
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/repository/servlet/XMLRepositoryFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: A form handler that creates, updates, and deletes
 *                repository items
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 **/

public class XMLRepositoryFormHandler extends RepositoryFormHandler {
  //-------------------------------------
  // Constants
  //-------------------------------------

  /** Class version string **/
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/repository/servlet/XMLRepositoryFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables
  //-------------------------------------

  static final String MY_USER_RESOURCES_NAME
  = "atg.repository.servlet.RepositoryServletUserResources";

  /** property: itemName **/
  String mItemName = null;

  /** property: itemFolder **/
  String mItemFolder = null;

  /** property: itemExtension **/
  String mItemExtension = null;

  /** property: itemPathWordSeparator **/
  String mItemPathWordSeparator = null;

  /** property: contentPropertyName **/
  String mContentPropertyName = null;

  /** Indicates whether contentPropertyName has been validated **/
  boolean mValidatedContentPropertyName = false;

  /** property: contentParagraphTagName **/
  String mContentParagraphTagName = null;

  /** property: XMLEncoding **/
   String mXMLEncoding = null;

  /** property: content **/
  String mContent = null;

  /** property: linksDictionary **/
  Properties mLinksDictionary;

  /** property: claimPropertyName **/
  String mClaimPropertyName;

  //-------------------------------------
  // Methods
  //-------------------------------------

  //-------------------------------------
  /**
   * Returns the item's name.
   *
   * @beaninfo
   *  description: Repository item's name
   **/
  public String getItemName() {
    return mItemName;
  }

  //-------------------------------------
  /**
   * Sets the item's name and, consequently, updates the
   * <code>repositoryId</code> property to the corresponding
   * repository id.
   **/
  public void setItemName(String pItemName) {
    mItemName = pItemName;
    updateRepositoryId();
  }

  //-------------------------------------
  /**
   * Returns the item's folder in the repository.
   *
   * @beaninfo
   *  description: Repository item's folder in the repository
   **/
  public String getItemFolder() {
    return mItemFolder;
  }

  //-------------------------------------
  /**
   * Sets the item's folder in the repository and, consequently,
   * updates the <code>repositoryId</code> property to the repository
   * id corresponding to the item's name, folder, and extension.  The
   * folder is expected to end with a "/" (e.g., "/targetedcontent/").
   **/
  public void setItemFolder(String pItemFolder) {
    mItemFolder = pItemFolder;
    updateRepositoryId();
  }

  //-------------------------------------
  /**
   * Returns the item's file extension, if any.
   *
   * @beaninfo
   *  description: Repository item's file extension
   **/
  public String getItemExtension() {
    return mItemExtension;
  }

  //-------------------------------------
  /**
   * Sets the item's file extension and, consequently, updates the
   * <code>repositoryId</code> property to the repository id
   * corresponding to the item's name, folder, and extension.  The
   * extension is expected to start with a "." (e.g., ".html").
   **/
  public void setItemExtension(String pItemExtension) {
    mItemExtension = pItemExtension;
    updateRepositoryId();
  }

  //-------------------------------------
  /**
   * Returns the string to replace spaces with in the item's path.
   *
   * @beaninfo
   *  description: String used to replace spaces with in the item's
   *  path
   **/
  public String getItemPathWordSeparator() {
    return mItemPathWordSeparator;
  }

  //-------------------------------------
  /**
   * Sets the string to replace spaces with in the item's path.  For
   * example, if <code>itemPathWordSeparator</code> is set to "_" then
   * the id "/targeted content/my item.html" will be converted into
   * "/targeted_content/my_item.html."
   **/
  public void setItemPathWordSeparator(String pItemPathWordSeparator) {
    mItemPathWordSeparator = pItemPathWordSeparator;
  }

  //-------------------------------------
  /**
   * Updates the <code>repositoryId</code> property to the repository
   * id corresponding to the item's name, folder, and extension.  If
   * <code>itemName</code> is not specified, the
   * <code>repositoryId</code> property is not set.  Otherwise, the
   * repository id is constructed by concatenating
   * <code>itemFolder</code>, <code>itemName</code>, and
   * <code>itemExtension</code>.  If <code>itemFolder</code> and/or
   * <code>itemExtension</code> are not specified, they are not
   * included.  For example, one could leave <code>itemFolder</code>
   * unset, but set <code>itemName</code> and
   * <code>itemExtension</code> to "/targetedcontent/foo" and ".html,"
   * respectively.
   **/
  void updateRepositoryId() {
    if (getItemName() == null)
      return;

    String id = "";
    if (getItemFolder() != null)
      id += getItemFolder();
    id += getItemName();
    if (getItemExtension() != null)
      id += getItemExtension();

    if (getItemPathWordSeparator() != null)
      id = StringUtils.replace(id, " ", getItemPathWordSeparator());

    setRepositoryId(id);
  }

  //-------------------------------------
  /**
   * Returns the property name of the item's "content" property.
   *
   * @beaninfo
   *  description: Property name of the item's content property
   **/
  public String getContentPropertyName() {
    return mContentPropertyName;
  }

  //-------------------------------------
  /**
   * Sets the property name of the item's "content" property.  The
   * specified property must exist in the item descriptor used by this
   * form handler, and it must be of type String.
   **/
  public void setContentPropertyName(String pContentPropertyName) {
    mContentPropertyName = pContentPropertyName;
    mValidatedContentPropertyName = false;
  }

  //-------------------------------------
  /**
   * Returns true if the given property name exists in the item
   * descriptor used by this form handler, and is of type String,
   * false otherwise.
   **/
  boolean isValidContentPropertyName() {
    if (mValidatedContentPropertyName)
      return true;

    String propertyName = getContentPropertyName();
    if (propertyName == null)
      return false;

    RepositoryItemDescriptor descriptor;
    try {
      descriptor = getItemDescriptor();
    }
    catch (RepositoryException e) {
      if (isLoggingError())
        logError("Can't obtain repository item descriptor");
      return false;
    }
    if (!descriptor.hasProperty(propertyName)) {
      if (isLoggingError())
        logError("Invalid property name " + propertyName +
                 " for item descriptor " + descriptor.getItemDescriptorName());
      return false;
    }
    DynamicPropertyDescriptor property =
      descriptor.getPropertyDescriptor(propertyName);
    Class propertyType = property.getPropertyType();
    if (propertyType != String.class) {
      if (isLoggingError())
        logError("Property " + propertyName + " (" + propertyType +
                 ") is not a String");
      return false;
    }
    mValidatedContentPropertyName = true;
    return true;
  }

  //-------------------------------------
  /**
   * Returns the name of the XML/HTML tag in which the content's
   * paragraphs should be enclosed.  If not specified, the "content"
   * property will in effect be treated as a regular repository item
   * property.
   *
   * @beaninfo
   *  description: The name of the XML tag in which the content's
   *  paragraphs should be enclosed
   **/
  public String getContentParagraphTagName() {
    return mContentParagraphTagName;
  }

  //-------------------------------------
  /**
   * Sets the name of the XML/HTML tag in which the content's
   * paragraphs should be enclosed.
   **/
  public void setContentParagraphTagName(String pContentParagraphTagName) {
    mContentParagraphTagName = pContentParagraphTagName;
  }

  //-------------------------------------
  /**
   * Returns the name of the encoding type to be used for the actual
   *  XML file.  This should be a valid XML encoding type.  If not
   * specified, the default encoding type will be used to write the
   * XML file for new items.
   *
   * @beaninfo
   *  description: The name of the encoding type to be used for the actual
   *  XML file.  This should be a valid XML encoding type.
   **/
  public String getXmlEncoding() {
    if (mXMLEncoding != null)
      return mXMLEncoding;

    if (getExtractDefaultValuesFromItem()) {
      try {
  String id = getRepositoryId();
  MutableRepository mutableRepository = getRepository();
  String defaultViewName = mutableRepository.getDefaultViewName();
  XMLRepositoryItem item =
    (XMLRepositoryItem) mutableRepository.getItemForUpdate(id,
                                                           defaultViewName);
  if (item != null)
    return item.getXMLEncoding();
      }
      catch (RepositoryException e) {
    //just swallow a failure
      }
    }

    return null;
  }

  //-------------------------------------
  /**
   * Sets the name of the encoding type to be used for the actual
   * XML file.  This should be a valid XML encoding type.
   **/
  public void setXmlEncoding(String pXMLEncoding) {
    mXMLEncoding = pXMLEncoding;
  }

  //-------------------------------------
  /**
   * Returns the set of properties/dictionary of HTTP links which
   * correspond to some content.  e.g. ATG corresponds to
   * http://www.atg.com.
   *
   * @beaninfo
   *  description: The set of properties/dictionary of HTTP links which
   *  correspond to some content.
   **/
  public Properties getLinksDictionary () {
    return mLinksDictionary;
  }

  //-------------------------------------
  /**
   * Sets the set of properties/dictionary of HTTP links which
   * correspond to some content.  e.g. ATG corresponds to
   * http://www.atg.com.
   **/
  public void setLinksDictionary (Properties pLinksDictionary) {
    mLinksDictionary = pLinksDictionary;
  }

  //-------------------------------------
  /**
   * Returns the item's content.  This value is obtained by looking up
   * the value of the item's "content" property (either in the value
   * table, or in the repository item itself), and stripping off the
   * paragraph tags.
   *
   * @beaninfo
   *  description: The item's content, without the paragraph tags
   **/
  public String getContent() {
    if (mContent != null)
      return mContent;

    if (!isValidContentPropertyName())
      return null;

    String content = (String) getValueProperty(getContentPropertyName());
    if ((content == null) && getExtractDefaultValuesFromItem())
      content = (String) getItemProperty(getContentPropertyName());

    return untagContent(content);
  }

  //-------------------------------------
  /**
   * Sets the item's content.  This has the effect of setting the
   * value of <code>value</code>.<i>content</i>, where <i>content</i>
   * is specified by <code>contentPropertyName</code>.
   *
   * Before the item's content is set, links to any known content
   * are added.
   *
   * The property value is set to the item's content after HTML
   * paragraph tags are added to it.
   **/
  public void setContent(String pContent) {
    mContent = addLinksToContent(pContent);
    if (isValidContentPropertyName())
      setValueProperty(getContentPropertyName(), tagContent(mContent));
  }

  //-------------------------------------
  /**
   * Add links to any known content.
   **/
  String addLinksToContent(String pContent) {

    if ((pContent == null) ||
        (getLinksDictionary() == null)) {
      return pContent;
    }

    Enumeration keys = mLinksDictionary.propertyNames();

    /* Note that this is not terribly efficient.  This is mainly for
       demonstration purposes.  An assumption was made that the number
       of times one needs to add links is small. */
    String content = pContent;
    while (keys.hasMoreElements()) {
      String key = String.valueOf(keys.nextElement());
      content = replace(content, key, mLinksDictionary.getProperty(key));
    }

    return content;
  }

  //-------------------------------------
  /**
   *
   * Returns a copy of the src string where all occurrences of pFrom
   * are replaced by copies of an HTML link to pTo.  If pFrom or pTo
   * is empty, pSrc is returned.  If pSrc does not have pFrom in it at
   * all, pSrc will be returned.
   *
   * This is based on the atg.core.util.StringUtils.replace method.
   *
   * @param pSrc the source string
   * @param pFrom the string to be replaced
   * @param pTo the string to substitute
   * @return the result string
   **/
  private String replace(String pSrc, String pFrom, String pTo) {
    StringBuffer result = null;
    String link = null;
    int index;
    int searchStart = 0;
    int substringStart = 0;

    if (pFrom.equals("") || pTo.equals(""))
      return pSrc;

    /**
     * While there is an occurrence of pFrom still left AND it is not
     * already in an HTML link, we replace pFrom with an HTML link to pTo.
     **/
    while ((index = pSrc.indexOf(pFrom, searchStart)) != -1) {
      // search for next occurence of </a> which is the closing
      // parameter for an HTML link.
      int index2 = pSrc.indexOf("</a>", index);

      // set where next search of pFrom should start, which is right
      // after current location of pFrom.
      searchStart = index + pFrom.length();

      // check to make sure this occurence of pFrom is not in a link,
      // i.e. </a> is not right after pFrom.  if it is in a link then
      // this is not considered a successful search for pFrom.
      if (index2 == searchStart) {
        searchStart = searchStart + 4; // start next search after </a>
        continue;
      }

      // get the substring from where we began the search for pFrom to
      // where pFrom is currently located.
      String tmp = pSrc.substring(substringStart,index);
      substringStart = searchStart;

      // if we haven't created a string buffer and a link, create it
      // now.
      if (result == null) {
        link = "<a href=\"" + pTo + "\">" + pFrom + "</a>";
        result = new StringBuffer(tmp.length() + link.length());
      }

      result.append(tmp);
      result.append(link);
    }
    /*
     * For efficiency, if there was no occurrence of pFrom, just return
     * pSrc directly
     */
    if (result == null) return pSrc;

    /* Tack on the remainder */
    result = result.append(pSrc.substring(searchStart));

    return result.toString();
  }

  //-------------------------------------
  /**
   * Tags the content with paragraph breaks.  For example, if
   * <code>contentParagraphTagName</code> is "p", each paragraph is
   * preceeded with "&lt;p&gt;" and ends with "&lt;/p&gt;."
   **/
  String tagContent(String pContent) {
    if ((pContent == null) ||
        (getContentParagraphTagName() == null))
      return pContent;

    String content = StringUtils.replace(pContent.trim(), "\r\n", "\n");
    String startTag = "\n<" + getContentParagraphTagName() + ">\n";
    String endTag = "\n</" + getContentParagraphTagName() + ">\n";
    StringBuffer tagged = new StringBuffer();
    tagged.append(startTag);
    int i;
    while ((i = content.indexOf("\n\n")) != -1) {
      tagged.append(content.substring(0, i));
      tagged.append(endTag);
      tagged.append(startTag);
      content = content.substring(i).trim();
    }
    tagged.append(content);
    tagged.append(endTag);
    return tagged.toString();
  }

  //-------------------------------------
  /**
   * Gets rid of any paragraph tags in the content.
   **/
  String untagContent(String pContent) {
    if ((pContent == null) ||
        (getContentParagraphTagName() == null))
      return pContent;
    String startTag = "<" + getContentParagraphTagName() + ">\n";
    String endTag = "\n</" + getContentParagraphTagName() + ">";
    String untagged = StringUtils.replace(pContent.trim(), startTag, "");
    untagged = StringUtils.replace(untagged, endTag, "");
    return untagged.trim();
  }

  //-------------------------------------
  /**
   * Returns property ClaimPropertyName
   *
   * @beaninfo
   *   description: name of the property that gets checked whenever a
   *                claim is made to see if the claim can happen.
   *                e.g. ClaimPropertyName = "editorId".  If a claim
   *                is made, the editorId set in the form is
   *                compared to the editorId in the repository.  If
   *                the editorId in the repository is null or the
   *                same as the editorId set in the form then the
   *                claim is allowed to happen.
   *   displayName: ClaimPropertyName
   **/
  public String getClaimPropertyName () { return mClaimPropertyName; }

  //-------------------------------------
  /**
   * Sets property ClaimPropertyName
   **/
  public void setClaimPropertyName (String pClaimPropertyName) {
    mClaimPropertyName = pClaimPropertyName;
  }

  //-------------------------------------
  /**
   *
   * Checks to see if the current editor can claim the requested
   * article.  If the article is not being edited by someone else then
   * the article's meta-data is updated to reflect that the current
   * editor has claimed it.
   *
   * <p> It then updates the rest of the article's meta-data.  See
   * atg.repository.servlet.RepositoryFormHandler.handleUpdate().
   *
   * @see atg.repository.servlet.RepositoryFormHandler.handleUpdate
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing
   * the code
   * @exception IOException if there was an error with servlet io
   **/
  public boolean handleClaimUpdate(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (isLoggingDebug()) {
      logDebug("XMLRepositoryFormHandler.handleClaimUpdate()");
    }

    try {
      // get the item to update
      String id = getRepositoryId();
      MutableRepository mutableRepository = getRepository();
      String defaultViewName = mutableRepository.getDefaultViewName();
      MutableRepositoryItem item =
        mutableRepository.getItemForUpdate(id, defaultViewName);
      if (item == null) {
        addFormException(new DropletException
                         ("Repository item with id " + id + " does not exist",
                          "itemDoesNotExist"));
        return true;
      }

      if (mClaimPropertyName == null) {
        // if the claim property name is null then we don't know what
        // property we need to check to see if a claim can be made
        logWarning("CliamPropertyName not set.  Allowing claim of item.");

      }
      else {

        String PersonWhoWishesToClaim
          = (String) getValue().get(StringUtils.toUpperCase(getClaimPropertyName()));
        String PersonWhoHasAlreadyClaimed
          = (String) item.getPropertyValue(getClaimPropertyName());

        if (isLoggingDebug()) {
          logDebug("ID of person who wishes to claim: " +
                   PersonWhoWishesToClaim);
          logDebug("ID of person who has already claimed: " +
                   PersonWhoHasAlreadyClaimed);
        }

        /* The current person cannot claim this item if someone
           already has.  Therefore if the ID of the person who has
           already claimed this item exists and it is not the same as
           the ID of the person who wishes to claim, then this person
           cannot claim it. */
        if ((PersonWhoHasAlreadyClaimed != null) &&
            (PersonWhoHasAlreadyClaimed.equals(PersonWhoWishesToClaim)
             == false)) {

          /* For the request, if we can get the user's locale, use that for
             the resource bundle, otherwise use the resource bundle for the
             default locale. */
          RequestLocale reqLocale = pRequest.getRequestLocale();
          Locale userLocale = null;
          if (reqLocale != null)
            userLocale = reqLocale.getLocale();
          else
            userLocale = Locale.getDefault();
          ResourceBundle userResourceBundle
            = ResourceUtils.getBundle(MY_USER_RESOURCES_NAME,
                                      userLocale);

          /* Add form exception with the correct message. */
          Object[] args = {PersonWhoHasAlreadyClaimed};
          String msg = ResourceUtils.getMsgResource("itemAlreadyClaimed",
                                                    MY_USER_RESOURCES_NAME,
                                                    userResourceBundle,
                                                    args);
          addFormException(new DropletException
                           (msg, "itemAlreadyClaimed"));
          return true;
        }
      }

      // update the repository
      if (isLoggingDebug()) {
        logDebug("about to call RepositoryFormHandler.handleUpdate()");
      }

      return handleUpdate(pRequest, pResponse);

    }
    catch (RepositoryException e) {
      addFormException(new DropletException
                       ("Repository error updating item",
                        e, "errorUpdatingItem"));
      if (isLoggingError())
        logError("error updating item: ", e);
    }

    return true;
  }

  //-------------------------------------
  /**
   *
   * Updates the repository item given by the current repository id.
   * Values stored in the <code>value</code> property are saved into
   * the repository item.  Also, the XML encoding (which is not
   * technically a property of the repository item) is updated through
   * the XMLDeclaration.  If any errors occur in the process, form
   * errors will be added.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing
   * the code
   * @exception IOException if there was an error with servlet io
   **/
  protected void updateItem(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    try {
      // get the item to update
      String id = getRepositoryId();
      MutableRepository mutableRep = getRepository();
      String defaultViewName = mutableRep.getDefaultViewName();
      XMLRepositoryItem item =
        (XMLRepositoryItem) mutableRep.getItemForUpdate(id,
                                                        defaultViewName);
      if (item == null) {
        addFormException(new DropletException
                         ("Repository item with id " + id + " does not exist",
                          "itemDoesNotExist"));
        return;
      }

      item.setXMLEncoding(getXmlEncoding());

      // update the item with the current form submission values
      updateItemProperties(item, getValue());


      // don't proceed to commit the update if we have any errors
      if (getFormError()) return;

      // check the changes into the repository
      getRepository().updateItem(item);
    }
    catch (RepositoryException e) {
      addFormException(new DropletException
           ("Repository error updating item",
      e, "errorUpdatingItem"));
      if (isLoggingError())
  logError("error updating item: ", e);
    }
  }

  //-------------------------------------
}
