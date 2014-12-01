<%@ page language="java" import="java.io.*,java.util.*,org.w3c.dom.*" %>
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
  *  This page handles the display for shared page gear views.
  *  It only displays headlines.
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
    * Represents the Url to this instance of the controller.  We pass this value to the 
    * headlines stylesheet which preprends it to each article reference.  
    * The value is calculated below.
    **/
   
   String articleGearUrl="";   
   
   /**
    * The stylesheet to render headlines...
    **/
   String headlinesStylesheetUrl=pafEnv.getGearInstanceParameter("sharedHeadlinesStylesheetUrl");

%>

<core:CreateUrl id="theUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
 <core:UrlParam param="xmlprotocol_action" value="getarticle"/>
 <core:UrlParam param="paf_dm" value="full"/>
 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
 <% articleGearUrl=theUrl.getNewUrl();%>
</core:CreateUrl>

<mt:XMLHeadlines id="theObj" pafEnv="<%=pafEnv%>" categoryFilter="true">
  <dsp:param name="xmlInput" value="<%=theObj.getXMLHeadlines()%>"/>
  <dsp:droplet name="XMLTransform">
    <dsp:param name="input" param="xmlInput"/>
    <dsp:param name="validate" value="false"/>
    <dsp:param name="template" value="<%=headlinesStylesheetUrl%>"/>
    <dsp:param name="passParams" value="local"/>
    <dsp:param name="articleGearUrl" value="<%=articleGearUrl%>"/>
    <dsp:param name="headlinesUrl" value="this was passed as a parameter!"/>
    <dsp:oparam name="failure">
      <i18n:message key="error_xform_docs"/>
    </dsp:oparam>
  </dsp:droplet>
</mt:XMLHeadlines>

<core:CreateUrl id="fullGearUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
 <core:UrlParam param="paf_dm" value="full"/>
 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
 <a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="full_screen_link" /></a>
</core:CreateUrl>

<core:CreateUrl id="fullGearUrl" url="<%=pafEnv.getOriginalRequestURI()%>">
 <core:UrlParam param="xmlprotocol_action" value="getcategories"/>
 <core:UrlParam param="paf_dm" value="full"/>
 <core:UrlParam param="paf_gear_id" value="<%=pafEnv.getGear().getId()%>"/>
 <a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="news_cat_link" /></a>
</core:CreateUrl>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocol.war/sharedfeed.jsp#2 $$Change: 651448 $--%>
