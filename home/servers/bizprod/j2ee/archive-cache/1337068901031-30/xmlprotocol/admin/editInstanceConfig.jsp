<%@ page language="java" import="java.io.*,java.util.*,org.w3c.dom.*,java.util.ArrayList" %>
 
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
  * Second step in instance configuration of the gear
  **/
 
%> 
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/xmlprotocoltaglib" prefix="mt" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<dsp:importbean bean="/atg/portal/gear/xmlprotocol/XmlProtocolInstanceFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>


<paf:InitializeGearEnvironment id="pafEnv">
<i18n:bundle baseName="atg.portal.gear.xmlprotocol.ContentResource" localeAttribute="userLocale" changeResponseLocale="false" />


<%
  String successUrl=null;
  String errorUrl=null;
  String cancelUrl=null;
  String thisFormUrl=null;
  String origURI= pafEnv.getOriginalRequestURI();
  String gearID = pafEnv.getGear().getId();
  String pageID = request.getParameter("pageId");
  String pageURL = request.getParameter("pageURL");
  String communityID = request.getParameter("communityId");
  
  //set up all of our instance variables...
  
  String instanceUserID = pafEnv.getGearInstanceParameter("instanceUserID");
  String instancePassword = pafEnv.getGearInstanceParameter("instancePassword");
  String authenticationUrl = pafEnv.getGearInstanceParameter("authenticationUrl");
  String articleUrl = pafEnv.getGearInstanceParameter("articleUrl");
  			
  String headlinesUrl = pafEnv.getGearInstanceParameter("headlinesUrl");
  String categoriesUrl = pafEnv.getGearInstanceParameter("categoriesUrl");
  String feedAdaptor = pafEnv.getGearInstanceParameter("feedAdaptor");
  String fullArticleStylesheetUrl = pafEnv.getGearInstanceParameter("fullArticleStylesheetUrl");
  String fullHeadlinesStylesheetUrl = pafEnv.getGearInstanceParameter("fullHeadlinesStylesheetUrl");
  String fullCategoriesStylesheetUrl = pafEnv.getGearInstanceParameter("fullCategoriesStylesheetUrl");
  String sharedHeadlinesStylesheetUrl = pafEnv.getGearInstanceParameter("sharedHeadlinesStylesheetUrl");
  String sharedCategoriesStylesheetUrl = pafEnv.getGearInstanceParameter("sharedCategoriesStylesheetUrl");
  
%>

<i18n:message id="submitButton" key="submit_button" />
<i18n:message id="cancelButton" key="cancel_button" />

  <core:CreateUrl id="theUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="instanceConfig"/>
    <core:UrlParam param="msg" value="success" />
    <%successUrl=theUrl.getNewUrl();%>
  </core:CreateUrl>

  <core:CreateUrl id="theUrl3" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="instanceConfig"/>
    <core:UrlParam param="msg" value="cancel" />
    <%cancelUrl=theUrl3.getNewUrl();%>
  </core:CreateUrl>


  <core:CreateUrl id="theUrl2" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <core:UrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="instanceConfig"/>
    <core:UrlParam param="config_page" value="DisplayText"/>
    <core:UrlParam param="msg" value="error" />
    <%errorUrl=theUrl2.getNewUrl();%>
  </core:CreateUrl>


<dsp:setvalue bean="XmlProtocolInstanceFormHandler.instanceUserID" value="<%= instanceUserID %>"/>
<dsp:setvalue bean="XmlProtocolInstanceFormHandler.instancePassword" value="<%= instancePassword %>"/>

<dsp:setvalue bean="XmlProtocolInstanceFormHandler.authenticationUrl" value="<%= authenticationUrl %>"/>
<dsp:setvalue bean="XmlProtocolInstanceFormHandler.articleUrl" value="<%= articleUrl %>"/>

<dsp:setvalue bean="XmlProtocolInstanceFormHandler.headlinesUrl" value="<%= headlinesUrl %>"/>
<dsp:setvalue bean="XmlProtocolInstanceFormHandler.categoriesUrl" value="<%= categoriesUrl %>"/>
<dsp:setvalue bean="XmlProtocolInstanceFormHandler.feedAdaptor" value="<%= feedAdaptor %>"/>
<dsp:setvalue bean="XmlProtocolInstanceFormHandler.fullHeadlinesStylesheetUrl" value="<%= fullHeadlinesStylesheetUrl %>"/>
<dsp:setvalue bean="XmlProtocolInstanceFormHandler.fullArticleStylesheetUrl" value="<%= fullArticleStylesheetUrl %>"/>
<dsp:setvalue bean="XmlProtocolInstanceFormHandler.fullCategoriesStylesheetUrl" value="<%= fullCategoriesStylesheetUrl %>"/>
<dsp:setvalue bean="XmlProtocolInstanceFormHandler.sharedHeadlinesStylesheetUrl" value="<%= sharedHeadlinesStylesheetUrl %>"/>
<dsp:setvalue bean="XmlProtocolInstanceFormHandler.sharedCategoriesStylesheetUrl" value="<%= sharedCategoriesStylesheetUrl %>"/>


  <dsp:form enctype="multipart/form-data" method="post" action="<%=origURI%>">

  
  <dsp:input type="hidden" bean="XmlProtocolInstanceFormHandler.successUrl" value="<%= successUrl %>" /> 
  

  <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_community_id" value="<%= communityID %>"/>
  

  <table>  

    <dsp:droplet name="ErrorMessageForEach">
      <dsp:param name="exceptions" bean="XmlProtocolInstanceFormHandler.formExceptions"/>
      <dsp:oparam name="outputStart"><tr></dsp:oparam>
      <dsp:oparam name="output">
        <td><font style="color: red"><dsp:valueof param="message">No
        message</dsp:valueof></font></td>
      </dsp:oparam>
      <dsp:oparam name="outputEnd"></tr></dsp:oparam>
    </dsp:droplet>

    <tr>
      <td><b><i18n:message key="instance_config_label" /></b></td>
    </tr>
    <tr/>
    <tr/>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><b><i18n:message key="inst_auth" /></b><font></td>
      <td/>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="user_id" /><font></td>
      <td><font size="-1"><dsp:input type="text" size="20" bean="XmlProtocolInstanceFormHandler.instanceUserID"/></font></td>
    </tr>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="password" /><font></td>
      <td><font size="-1"><dsp:input type="text" size="20" bean="XmlProtocolInstanceFormHandler.instancePassword"/></font></td>
    </tr>
    <tr/>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><b><i18n:message key="service_config" /></b><font></td>
      <td/>
    </tr>

    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="service_provider" /><font></td>
    
      
          <td><font size="-1">
       	  <dsp:input type="text"  bean="XmlProtocolInstanceFormHandler.feedAdaptor" />
       	  
          </td>
      
     </tr>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="auth_url" /><font></td>
      <td><font size="-1"><dsp:input type="text" size="60" bean="XmlProtocolInstanceFormHandler.authenticationUrl"/></font></td>
    </tr>

    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="cat_url" /><font></td>
      <td><font size="-1"><dsp:input type="text" size="60" bean="XmlProtocolInstanceFormHandler.categoriesUrl"/></font></td>
    </tr>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="headlines_url" /><font></td>
      <td><font size="-1"><dsp:input type="text" size="60" bean="XmlProtocolInstanceFormHandler.headlinesUrl"/></font></td>
    </tr>
    <tr/>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="article_url" /><font></td>
      <td><font size="-1"><dsp:input type="text" size="60" bean="XmlProtocolInstanceFormHandler.articleUrl"/></font></td>
    </tr>
    <tr/>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><b><i18n:message key="feed_disp_config" /></b><font></td>
      <td/>
    </tr>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="full_cat_style" /><font></td>
      <td><font size="-1"><dsp:input type="text" size="60" bean="XmlProtocolInstanceFormHandler.fullCategoriesStylesheetUrl"/></font></td>
    </tr>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="full_article_style" /><font></td>
      <td><font size="-1"><dsp:input type="text" size="60" bean="XmlProtocolInstanceFormHandler.fullArticleStylesheetUrl"/></font></td>
    </tr>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="full_headlines_style" /><font></td>
      <td><font size="-1"><dsp:input type="text" size="60" bean="XmlProtocolInstanceFormHandler.fullHeadlinesStylesheetUrl"/></font></td>
    </tr>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="shared_cat_style" /><font></td>
      <td><font size="-1"><dsp:input type="text" size="60" bean="XmlProtocolInstanceFormHandler.sharedCategoriesStylesheetUrl"/></font></td>
    </tr>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="shared_headlines_style" /><font></td>
      <td><font size="-1"><dsp:input type="text" size="60" bean="XmlProtocolInstanceFormHandler.sharedHeadlinesStylesheetUrl"/></font></td>
    </tr>

    
    <tr>
      <td>
	  <%-- Set up the gear id. --%>
	  <dsp:input type="hidden" bean="XmlProtocolInstanceFormHandler.gearId" value="<%= pafEnv.getGear().getId() %>"/>

	  <%-- Set up the error url. --%>
          
          <dsp:input type="hidden" bean="XmlProtocolInstanceFormHandler.errorUrl" value="<%= errorUrl %>"/>

	  <dsp:input type="hidden" bean="XmlProtocolInstanceFormHandler.cancelURL" value="<%= cancelUrl %>"/>
          
	  </td><td>
	  <dsp:input type="submit" bean="XmlProtocolInstanceFormHandler.submit" value="<%= submitButton %>"/>
	  <dsp:input type="submit" bean="XmlProtocolInstanceFormHandler.cancel" value="<%= cancelButton %>"/>
	</dsp:form>
    </td>
  </tr>
  </table>


</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocol.war/admin/editInstanceConfig.jsp#2 $$Change: 651448 $--%>
