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
  * Main entry pooint for instance configuration of the gear
  **/
 
%> 
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">

<i18n:bundle baseName="atg.portal.gear.xmlprotocol.ContentResource" localeAttribute="userLocale" changeResponseLocale="false" />

<% String origURI= pafEnv.getOriginalRequestURI();
   String gearID = pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String communityID = request.getParameter("paf_community_id");

   String clearGif = response.encodeURL("images/clear.gif");
   String infoGif = response.encodeURL("images/info.gif");
 %>

  <TABLE WIDTH="667" BORDER="0" CELLSPACING="0" CELLPADDING="0">
    
    <tr>
     <td/>
    </tr>
 
    
<tr><td>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" value='<%= request.getParameter("msg") %>'/>
  <dsp:oparam name="success"><font class="info"><img src="<%= infoGif %>">&nbsp;&nbsp;<i18n:message key="changes_updated" /></font></dsp:oparam>
  <dsp:oparam name="cancel"><font class="info"><img src="<%= infoGif %>">&nbsp;&nbsp;<i18n:message key="changes_not_saved" /></font></dsp:oparam>
  <dsp:oparam name="error"><font class="error"><i18n:message key="changes_error" /></font></dsp:oparam>
</dsp:droplet>
</td></tr>

    <tr/>
    <tr/>
    <tr/>
    <tr>
      <td colspan=2>
      <core:CreateUrl id="editGearUrl" url="<%= origURI %>">
        <core:UrlParam param="paf_dm" value="full"/>
        <core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="DisplayText"/>
        <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
        <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
        <core:UrlParam param="paf_page_url" value="<%= pageURL %>"/>
        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
        <a href="<%= editGearUrl.getNewUrl() %>"><font size="2"><i18n:message key="edit_gear_config" /></font></a>
      </core:CreateUrl>
      </td>
    </tr>
    <tr>
      <td><font size="-2"><i18n:message key="edit_gear_config_helper" /></font></td>
      <td/>
    </tr> 
    <tr/>
    <tr>
      <td colspan=2>
      <core:CreateUrl id="editGearUrl" url="<%= origURI %>">
        <core:UrlParam param="paf_dm" value="full"/>
        <core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="DefaultConfig"/>
        <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
        <core:UrlParam param="paf_gear_id" value="<%= pageID %>"/>
        <core:UrlParam param="paf_page_url" value="<%= pageURL %>"/>
        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
        <a href="<%= editGearUrl.getNewUrl() %>"><font size="2"><i18n:message key="edit_user_def" /></font></a>
      </core:CreateUrl>
      </td>
    </tr>
    <tr>
      <td><font size="-2"><i18n:message key="edit_user_def_helper" /></font></td>
      <td/>
    </tr> 
    <tr/>
    


  </TABLE>


</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocol.war/admin/displayInstanceConfig.jsp#2 $$Change: 651448 $--%>
