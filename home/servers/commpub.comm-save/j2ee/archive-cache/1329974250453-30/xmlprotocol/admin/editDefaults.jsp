<%@ page language="java" %>
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
  * First step of instance configuration of the gear
  **/
 
%> 
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/xmlprotocoltaglib" prefix="mt" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:importbean bean="/atg/portal/gear/xmlprotocol/XmlProtocolFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">
<i18n:bundle baseName="atg.portal.gear.xmlprotocol.ContentResource" localeAttribute="userLocale" changeResponseLocale="false" />


<%
   
  String successUrl = null;
  String errorUrl=null;
  String cancelUrl=null;
  String origURI= pafEnv.getOriginalRequestURI(); 
  String gearID = pafEnv.getGear().getId();
  String pageID = request.getParameter("pageId");
  String pageURL = request.getParameter("pageURL");
  String communityID = request.getParameter("communityId");
 
  //set up all of our instance variables...
  String categories =  pafEnv.getGearUserDefaultValue("categories");
  String numSharedHeadlines = pafEnv.getGearUserDefaultValue("numSharedHeadlines");
  String numFullHeadlines = pafEnv.getGearUserDefaultValue("numFullHeadlines");
  
%>

<i18n:message id="submitButton" key="submit_button" />
<i18n:message id="cancelButton" key="cancel_button" />

<dsp:setvalue bean="XmlProtocolFormHandler.numSharedHeadlines" value="<%= numSharedHeadlines %>"/>
<dsp:setvalue bean="XmlProtocolFormHandler.numFullHeadlines" value="<%= numFullHeadlines %>"/>


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
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="instanceConfig"/>
    <core:UrlParam param="config_page" value="DefaultConfig"/>
    <core:UrlParam param="msg" value="error" />
    <%errorUrl=theUrl2.getNewUrl();%>
  </core:CreateUrl>


      
  <dsp:form enctype="multipart/form-data" method="post" action="<%=origURI%>">
  
  <dsp:input type="hidden" bean="XmlProtocolFormHandler.successUrl" value="<%= successUrl %>" /> 
  <dsp:input type="hidden" bean="XmlProtocolFormHandler.configureDefaults" value="true"/>
  <table>  

    <dsp:droplet name="ErrorMessageForEach">
      <dsp:param name="exceptions" bean="XmlProtocolFormHandler.formExceptions"/>
      <dsp:oparam name="outputStart"><tr></dsp:oparam>
      <dsp:oparam name="output">
        <td><font style="color: red"><dsp:valueof param="message">No
        message</dsp:valueof></font></td>
      </dsp:oparam>
      <dsp:oparam name="outputEnd"></tr></dsp:oparam>
    </dsp:droplet>

    <tr>
      <td><b><i18n:message key="user_defaults_config" /></b></td>
    </tr>
    <tr/>
    <tr/>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="num_headlines_shared" /><font></td>
      <td><font size="-1">
          <dsp:select bean="XmlProtocolFormHandler.NumSharedHeadlines">
             <dsp:option value="3"/>3
             <dsp:option value="5"/>5
             <dsp:option value="10"/>10
             <dsp:option value="20"/>20
          </dsp:select>
         
      </font></td>
    </tr>
    <tr>
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="num_headlines_full" /><font></td>
      <td><font size="-1">
          <dsp:select bean="XmlProtocolFormHandler.NumFullHeadlines">
             <dsp:option value="3"/>3
             <dsp:option value="5"/>5
             <dsp:option value="10"/>10
             <dsp:option value="20"/>20
          </dsp:select>
         
      </font></td>
    </tr>    
    <mt:ConfigureCategories id="configCats" pafEnv="<%=pafEnv%>" configureDefaults="true">
    	<%String[] categoryNames = configCats.getCategoryNames();
    	  String[] categoryIDs = configCats.getCategoryIDs();
    	  String[] categoryChecked = configCats.getCategoryChecked();
    	%>
    	<core:If value="<%=configCats.supportsMutlipleCategories()%>">
	   <tr>
	     <td NOWRAP align="left"><font size="-1" ><b><i18n:message key="cat_to_display" /></b></font></td>
`	   </tr>

    	   <core:ForEach id="categoriesForEach" values="<%= categoryIDs%>" elementId="theCategory">
    	  	<tr> 
    	  	<td><font size="-1" ><%=categoryNames[categoriesForEach.getIndex()]%></font></td>
    	  	<td><dsp:input type="checkbox" bean="XmlProtocolFormHandler.selectedCategories" value="<%=categoryIDs[categoriesForEach.getIndex()]%>" checked="<%=Boolean.valueOf(categoryChecked[categoriesForEach.getIndex()]).booleanValue()%>"/> </td>
    	  	</tr>
    	   </core:ForEach>
    	</core:If>
    
    	<core:IfNot value="<%=configCats.supportsMutlipleCategories()%>">
               <tr>
               <td NOWRAP align="left"><font size="-1" ><i18n:message key="cat_to_display" /><font></td>
               <td>
          	   <dsp:select bean="XmlProtocolFormHandler.selectedCategories" multiple="<%=false%>">
    	      <core:ForEach id="categoriesForEach" values="<%= categoryIDs%>" elementId="theCategory">
    	         <dsp:option value="<%=categoryIDs[categoriesForEach.getIndex()]%>" selected="<%=Boolean.valueOf(categoryChecked[categoriesForEach.getIndex()]).booleanValue()%>"/><%=categoryNames[categoriesForEach.getIndex()]%>
       	      </core:ForEach>
       	   </dsp:select>
               </td>
               </tr>
       	</core:IfNot>
   </mt:ConfigureCategories>    

    
   <tr>
      <td>
  
  <%-- Set up the gear id. --%>
  <dsp:input type="hidden" bean="XmlProtocolFormHandler.gearId" value="<%= pafEnv.getGear().getId() %>"/>
  
  <%-- Set up the error url. --%>
  <dsp:input type="hidden" bean="XmlProtocolFormHandler.errorUrl" value="<%= errorUrl %>"/>

  <dsp:input type="hidden" bean="XmlProtocolFormHandler.cancelURL" value="<%= cancelUrl %>"/>
  </td><td>
  <dsp:input type="submit" bean="XmlProtocolFormHandler.submit" value="<%= submitButton %>"/>
  <dsp:input type="submit" bean="XmlProtocolFormHandler.cancel" value="<%= cancelButton %>"/>
</dsp:form>
   </td>
  </tr>
  </table>


</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocol.war/admin/editDefaults.jsp#2 $$Change: 651448 $--%>
