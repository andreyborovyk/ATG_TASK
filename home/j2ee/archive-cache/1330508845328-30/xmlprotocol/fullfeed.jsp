<%@ page language="java" import="java.io.*,java.util.*,org.w3c.dom.*,java.util.ArrayList" %>
<%@ page import="atg.servlet.*" %>

<%
/*<ATGCOPYRIGHT>
 
 * Copyright (C) 2001-2010 Art Technology Group, Inc.
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

 /**
  *  This page handles the display for full page gear views.
  *  It can display:
  *   
  *	- headlines
  *	- categories
  *     - articles
  * 
  * 
  * The following params are passed in the query string of the 
  * request URL to determine what actions to perform:
  *
  * <code>xmlprotocol_action</code> determines what type of information to 
  * display (i.e. headlines, categories, or an article)
  * 
  * <code>xmlprotocol_bookmarks</code> the set of bookmarks associated with 
  * a previous request that we want to use as a starting point for the new request
  * 
  * <code>xmlprotocol_categories</code> the set of categories associated with a previous
  * request that we want to continue using.  For example, if the user chooses to view
  * one particular category, then we need to override the default personalized categories
  *
  * <code>articleID</code> the unique identifier of an article to retrieve
  *
  *
  * After the service provider response is received via custom tags, the page
  * hands off control to the XMLTransform droplet for rendering.  We retain strict
  * separation between the controller (this page), business logic and back-end 
  * implementation details (via custom tags and Java classes), and information rendering (HTML or 
  * other mark-up via XSLT stylesheets).
  *
  **/


%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/xmlprotocoltaglib" prefix="mt" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/xml/XMLTransform"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>


<paf:InitializeGearEnvironment id="pafEnv">
<i18n:bundle baseName="atg.portal.gear.xmlprotocol.ContentResource" localeAttribute="userLocale" changeResponseLocale="false" />

<%

   /*
    
    XMLTransform uses getOutputStream() directly, and the J2EE spec says that you
    are not supposed to mix it with getWriter().  Explicitly disable it. 
   */
   
   DynamoHttpServletRequest dynRequest = ServletUtil.getDynamoRequest(request);
   DynamoHttpServletResponse dynResponse = dynRequest.getResponse();
   dynResponse.setStrictOutputAccess(false);
   
   
   
   /**
    * Placeholder for whether the request is to display headlines, an article, or a cetegory
    **/
   String theAction= "";       
   
   /**
    * Represents the unique identifier for an article to retrieve from a service.
    * We need to pass this as a parameter to the headlines stylsheet so a proper URL to the article
    * can be constructed in the output markup.
    **/
   String articleID="";         
   
   /**
    * Represents the Url to this instance of the controller.  We pass this value to the 
    * headlines stylesheet which preprends it to each article reference.  
    * The value is calculated below.
    **/
   String articleGearUrl="";   

   /**
    * Represents the Url to this instance of the controller.  We pass this value to the 
    * categories stylesheet which preprends it to each category reference to retriece 
    * headlines.   
    **/
   String headlinesGearUrl=""; 

   /**
    * The current healines position in a set of folders
    **/
   String bookmarks= request.getParameter("xmlprotocol_bookmarks");   
   
   /** 
    * The current news category if the user is viewing a single category
    * as opposed to their personalized view
    **/ 
   String categories =  request.getParameter("xmlprotocol_categories");
   
   /**
    * The stylesheets for rendering...
    **/
   String headlinesStylesheetUrl=
        pafEnv.getGearInstanceParameter("fullHeadlinesStylesheetUrl");
   
   String articleStylesheetUrl=
        pafEnv.getGearInstanceParameter("fullArticleStylesheetUrl");

   String categoriesStylesheetUrl=
        pafEnv.getGearInstanceParameter("fullCategoriesStylesheetUrl");
%>

<core:CreateUrl id="theUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
 <core:UrlParam param="xmlprotocol_action" value="getarticle"/>
 <core:UrlParam param="paf_dm" value="full"/>
 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
 <% articleGearUrl=theUrl.getNewUrl();%>
</core:CreateUrl>

<core:CreateUrl id="theUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
 <core:UrlParam param="xmlprotocol_action" value="getcategoryheadlines"/>
 <core:UrlParam param="paf_dm" value="full"/>
 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
 <% headlinesGearUrl=theUrl.getNewUrl();%>
</core:CreateUrl>

<%//Determine the view and default to headlines if nothing passed %>

<core:IfNotNull value='<%= request.getParameter("xmlprotocol_action")%>'>
      <% theAction = request.getParameter("xmlprotocol_action");%>
</core:IfNotNull>


<core:IfNull value='<%= request.getParameter("xmlprotocol_action")%>'>
      <% theAction = "getheadlines";%>
</core:IfNull>


<core:Switch value="<%=theAction%>">
   
   <core:Case value="getarticle">
      	<%articleID=request.getParameter("articleID");%>
	<mt:XMLArticle id="theObj" articleID="<%=articleID%>" pafEnv="<%=pafEnv%>">
	  <dsp:param name="xmlInput" value="<%=theObj.getXMLArticle()%>"/>
	  <dsp:droplet name="XMLTransform">
	    <dsp:param name="input" param="xmlInput"/>
	    <dsp:param name="validate" value="false"/>
	    <dsp:param name="template" value="<%=articleStylesheetUrl%>"/>
	    <dsp:param name="passParams" value="local"/>
	    <dsp:param name="articleGearUrl" value="<%=articleGearUrl%>"/>

	    <dsp:oparam name="failure">
	       <i18n:message key="error_xform_docs"/><br />
	    </dsp:oparam>
	  </dsp:droplet>
	</mt:XMLArticle>
	<% //Navigation URLs %>
	<core:CreateUrl id="fullGearUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
	 <core:UrlParam param="paf_dm" value="full"/>
	 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
	 <a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="headlines_link" /></a>
	</core:CreateUrl>
	
	<core:CreateUrl id="fullGearUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
	 <core:UrlParam param="xmlprotocol_action" value="getcategories"/>
	 <core:UrlParam param="paf_dm" value="full"/>
	 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
	 <a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="news_cat_link" /></a>
	</core:CreateUrl>
	
   </core:Case>

   <core:Case value="getcategories">
	<mt:XMLCategories id="theObj" pafEnv="<%=pafEnv%>">
	  <dsp:param name="xmlInput" value="<%=theObj.getXMLCategories()%>"/>
	  <dsp:droplet name="XMLTransform">
	    <dsp:param name="input" param="xmlInput"/>
	    <dsp:param name="validate" value="false"/>
	    <dsp:param name="template" value="<%=categoriesStylesheetUrl%>"/>
	    <dsp:param name="passParams" value="local"/>
	    <dsp:param name="headlinesGearUrl" value="<%=headlinesGearUrl%>"/>
	    <dsp:oparam name="failure">
	      <i18n:message key="error_xfrom_docs" /><br/>
	    </dsp:oparam>
	  </dsp:droplet>
	</mt:XMLCategories>   
	<% //Navigation URLs %>
	<core:CreateUrl id="fullGearUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
	 <core:UrlParam param="paf_dm" value="full"/>
	 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
	 <a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="headlines_link" /></a>
	</core:CreateUrl>
   </core:Case>	

   <core:Case value="getsearch">
   	Get the search....
   </core:Case>

   <core:Case value="getallheadlines">
	<mt:XMLHeadlines id="theObj" pafEnv="<%=pafEnv%>" categoryFilter="false">
	  <dsp:param name="xmlInput" value="<%=theObj.getXMLHeadlines()%>"/>
	  <dsp:droplet name="XMLTransform">
	    <dsp:param name="input" param="xmlInput"/>
	    <dsp:param name="validate" value="false"/>
	    <dsp:param name="template" value="<%=headlinesStylesheetUrl%>"/>
	    <dsp:param name="passParams" value="local"/>
	    <dsp:param name="articleGearUrl" value="<%=articleGearUrl%>"/>
	    <dsp:oparam name="failure">
	      <i18n:message key="error_xfrom_docs" /><br/>
	    </dsp:oparam>
	  </dsp:droplet>
	</mt:XMLHeadlines>
	<core:CreateUrl id="fullGearUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
	 <core:UrlParam param="paf_dm" value="full"/>
	 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
	 <a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="view_pers_headlines" /></a>
	</core:CreateUrl>
	<core:CreateUrl id="fullGearUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
	 <core:UrlParam param="xmlprotocol_action" value="getcategories"/>
	 <core:UrlParam param="paf_dm" value="full"/>
	 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
	 <a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="news_cat_link" /></a>
	</core:CreateUrl>

   </core:Case>
   <core:Case value="getcategoryheadlines">
	
	<%ArrayList theParams = new ArrayList();
	  
	  if (bookmarks!=null){
	     theParams.add("bookmarks="+bookmarks);
	  }
	  if (categories!=null){
	     theParams.add("categories="+categories);
	  }
	  String newBookmarks="";%>
	<mt:XMLHeadlines id="theObj" pafEnv="<%=pafEnv%>" categoryFilter="false" params="<%=theParams%>">
	  <%newBookmarks = theObj.getBookmarks();%>
	  <dsp:param name="xmlInput" value="<%=theObj.getXMLHeadlines()%>"/>
	  <dsp:droplet name="XMLTransform">
	    <dsp:param name="input" param="xmlInput"/>
	    <dsp:param name="validate" value="false"/>
	    <dsp:param name="template" value="<%=headlinesStylesheetUrl%>"/>
	    <dsp:param name="passParams" value="local"/>
	    <dsp:param name="articleGearUrl" value="<%=articleGearUrl%>"/>
	    <dsp:oparam name="failure">
	       <i18n:message key="error_xfrom_docs" /><br/>
	    </dsp:oparam>
	  </dsp:droplet>
	</mt:XMLHeadlines>
	
	<% //Navigation URLs %>
	<core:CreateUrl id="fullGearUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
	 <core:UrlParam param="paf_dm" value="full"/>
	 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
 	 <core:UrlParam param="xmlprotocol_bookmarks" value="<%=newBookmarks%>"/>      
	 <core:UrlParam param="xmlprotocol_action" value="getcategoryheadlines"/>
         <core:UrlParam param="xmlprotocol_categories" value="<%=categories%>"/>
  
	 <a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="more_link" /></a>
	</core:CreateUrl>

	<core:CreateUrl id="fullGearUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
	 <core:UrlParam param="xmlprotocol_action" value="getcategories"/>
	 <core:UrlParam param="paf_dm" value="full"/>
	 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
	 <a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="news_cat_link" /></a>
	</core:CreateUrl>

   </core:Case>
   
   <core:DefaultCase>
	
	<%ArrayList theParams = new ArrayList();
	  if (bookmarks!=null){
	     theParams.add("bookmarks="+bookmarks);
	  }
	  String newBookmarks="";%>
	<mt:XMLHeadlines id="theObj" pafEnv="<%=pafEnv%>" categoryFilter="true" params="<%=theParams%>">
	  <%newBookmarks = theObj.getBookmarks();%>
	  <dsp:param name="xmlInput" value="<%=theObj.getXMLHeadlines()%>"/>
	  <dsp:droplet name="XMLTransform">
	    <dsp:param name="input" param="xmlInput"/>
	    <dsp:param name="validate" value="false"/>
	    <dsp:param name="template" value="<%=headlinesStylesheetUrl%>"/>
	    <dsp:param name="passParams" value="local"/>
	    <dsp:param name="articleGearUrl" value="<%=articleGearUrl%>"/>
	    <dsp:oparam name="failure">
	      <i18n:message key="error_xfrom_docs" /><br/>
	    </dsp:oparam>
	  </dsp:droplet>
	</mt:XMLHeadlines>
	
	<% //Navigation URLs %>
	<core:CreateUrl id="fullGearUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
	 <core:UrlParam param="paf_dm" value="full"/>
	 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
 	 <core:UrlParam param="xmlprotocol_bookmarks" value="<%=newBookmarks%>"/>      
	 <a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="more_link" /></a>
	</core:CreateUrl>

	<core:CreateUrl id="fullGearUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
	 <core:UrlParam param="xmlprotocol_action" value="getcategories"/>
	 <core:UrlParam param="paf_dm" value="full"/>
	 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
	 <a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="news_cat_link" /></a>
	</core:CreateUrl>

   </core:DefaultCase>
   
</core:Switch>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocol.war/fullfeed.jsp#2 $$Change: 651448 $--%>
