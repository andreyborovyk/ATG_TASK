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
  * Handles user personalization for the gear
  **/
 
%> 

<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/xmlprotocoltaglib" prefix="mt" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:importbean bean="/atg/portal/gear/xmlprotocol/XmlProtocolFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<paf:InitializeGearEnvironment id="pafEnv">
<i18n:bundle baseName="atg.portal.gear.xmlprotocol.ContentResource" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<%
  //get the user personalization settings
  String categories = pafEnv.getGearUserParameter("categories");
  String numSharedHeadlines = pafEnv.getGearUserParameter("numSharedHeadlines");
  String numFullHeadlines = pafEnv.getGearUserParameter("numFullHeadlines");
  String successUrl = pafEnv.getPageURI(pafEnv.getPage());

%>
<dsp:setvalue bean="XmlProtocolFormHandler.gearEnv" value="<%= pafEnv %>"/>
<dsp:setvalue bean="XmlProtocolFormHandler.numSharedHeadlines" value="<%= numSharedHeadlines %>"/>
<dsp:setvalue bean="XmlProtocolFormHandler.numFullHeadlines" value="<%= numFullHeadlines %>"/>
<dsp:setvalue bean="XmlProtocolFormHandler.categories" value="<%= categories %>"/>



<dsp:form method="POST" action="<%= pafEnv.getOriginalRequestURI() %>">
  <dsp:input type="hidden" bean="XmlProtocolFormHandler.successUrl" value="<%= successUrl %>" /> 

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
      <td><b><i18n:message key="personalize_news" /></b></td>
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
      <td NOWRAP align="left"><font size="-1" ><i18n:message key="num_headlines_full" /></font></td>
      <td><font size="-1">
          <dsp:select bean="XmlProtocolFormHandler.NumFullHeadlines">
             <dsp:option value="3"/>3
             <dsp:option value="5"/>5
             <dsp:option value="10"/>10
             <dsp:option value="20"/>20
          </dsp:select>
         
      </font></td>
    </tr>

    <tr/>
    <tr/>
    <mt:ConfigureCategories id="configCats" pafEnv="<%=pafEnv%>">
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
           <tr><font size="-1" >
           <td NOWRAP align="left"><font size="-1" ><i18n:message key="cat_to_display" /><font></td>
           <td>
      	   <dsp:select bean="XmlProtocolFormHandler.selectedCategories" multiple="<%=false%>">
	      <core:ForEach id="categoriesForEach" values="<%= categoryIDs%>" elementId="theCategory">
	         <dsp:option value="<%=categoryIDs[categoriesForEach.getIndex()]%>" selected="<%=Boolean.valueOf(categoryChecked[categoriesForEach.getIndex()]).booleanValue()%>"/><%=categoryNames[categoriesForEach.getIndex()]%>
   	      </core:ForEach>
   	   </dsp:select>
           </td>
           </font></tr>
   	</core:IfNot>
   </mt:ConfigureCategories>    
     
   <tr>
      <td>
  
  <%-- Set up the gear id. --%>
  <dsp:input type="hidden" bean="XmlProtocolFormHandler.gearId" value="<%= pafEnv.getGear().getId() %>"/>
  
  <%-- Set up the cancel URL. --%>
  <core:CreateUrl id="contentGearUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="paf_dm" value="shared"/>
    <core:UrlParam param="paf_gm" value="content"/>
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <dsp:input type="hidden" bean="XmlProtocolFormHandler.cancelUrl" value="<%= contentGearUrl.getNewUrl() %>"/>
  </core:CreateUrl>
  
  <%-- Set up the error url. --%>
  <core:CreateUrl id="thisFormUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gm" value="userConfig"/>
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <dsp:input type="hidden" bean="XmlProtocolFormHandler.errorUrl" value="<%= thisFormUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <i18n:message id="submitButton" key="submit_button" />
  <i18n:message id="cancelButton" key="cancel_button" />

  <dsp:input type="hidden" bean="XmlProtocolFormHandler.cancelURL" value="<%= pafEnv.getOriginalRequestURI() %>"/>
  </td><td>
  <dsp:input type="submit" bean="XmlProtocolFormHandler.submit" value="<%= submitButton %>"/>
  <dsp:input type="submit" bean="XmlProtocolFormHandler.cancel" value="<%= cancelButton %>"/>

   </td>
  </tr>
  </table>
  
  </dsp:form>

</dsp:page>

</paf:InitializeGearEnvironment>
<%-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocol.war/userConfig.jsp#2 $$Change: 651448 $--%>
