<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">

<i18n:bundle baseName="atg.portal.templates.DefaultTemplateResources" localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message key="imageroot" id="i18n_imageroot"/>

<%
    String gearTitleContext = pafEnv.getCommunity().getGearTitleTemplate().getServletContext();
    String clearGIF = "/portal/layoutTemplates/images/clear.gif";
    String gearTitleBGColor = "#" + pafEnv.getPage().getColorPalette().getGearTitleBackgroundColor();
    String gearTitleBGColorParam = pafEnv.getPage().getColorPalette().getGearTitleBackgroundColor(); 
%>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td width="7"><img src='<%= clearGIF %>' width="7 " height="1" border="0"></td>
        <td><img src='<%= clearGIF %>' width="116" height="1" border="0"></td>
        <td width="10"><img src='<%= clearGIF %>' width="1" height="1" border="0"></td>
        <td width="1"><img src='<%= clearGIF %>' width="71" height="1" border="0"></td>
    </tr>
    <tr>
        <td valign="top" align="right" width="7 ">
            <table height="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td><img src='<%= "/portal/layoutTemplates/images/tc_tl_corner_" + gearTitleBGColorParam + ".gif" %>' width="7" height="7" border="0"></td>
                </tr>
                <tr>
                    <td bgcolor='<%= gearTitleBGColor %>'><img src='<%= clearGIF %>' width="7" height="13" border="0"></td>
                </tr>
            </table>
        </td>
        <!-- 4a8cd6 -->
        <td bgcolor="<%= gearTitleBGColor %>" width="95%" nowrap>
            <font class="small" color='<%= "#" + pafEnv.getPage().getColorPalette().getGearTitleTextColor() %>'>&nbsp;&nbsp;<%= pafEnv.getGear().getName(response.getLocale()) %></font>
        </td>
        <td width="10"  bgcolor='<%= gearTitleBGColor %>'><img src='<%= clearGIF %>' width="10" height="20" border="0"></td>
        <core:exclusiveIf>
            <core:if value="<%= pafEnv.isRegisteredUser() %>">
            <core:ExclusiveIf>
                <core:If value='<%= pafEnv.getGear().hasMode("userConfig", "full") &&  pafEnv.getCommunity().isAllowPersonalizedPages() %>'>
                    <core:CreateUrl id="fullGearUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
                        <core:UrlParam param="paf_dm" value="full"/>
                        <core:UrlParam param="paf_gm" value="userConfig"/>
                        <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
                        <paf:encodeUrlParam param="paf_success_url" value="<%= pafEnv.getOriginalRequestURI() %>"/>
                        <td nowrap valign="middle" align="right" bgcolor="<%= gearTitleBGColor %>"><a href="<%= fullGearUrl.getNewUrl() %>"><img src='<%= "/portal/layoutTemplates" + "/"+i18n_imageroot+"/tc_edit_b.gif" %>'  border="0"></a><img src='<%= clearGIF %>' width="1" height="14" border="0"><img src='<%= clearGIF %>' width="10" height="14" border="0"></td>
                    </core:CreateUrl>
                </core:If>
                <core:DefaultCase>
                    <td nowrap valign="middle" align="right" bgcolor="<%= gearTitleBGColor %>"><img src='<%= clearGIF %>' width="30" height="14" border="0"><img src='<%= clearGIF %>'width="1" height="14" border="0"><img src='<%= clearGIF %>' width="10" height="14" border="0"></td>
                </core:DefaultCase>
            </core:ExclusiveIf>
        </core:if>
        <core:defaultCase>
            <core:ExclusiveIf>
                <core:If value='<%= pafEnv.getGear().hasMode("userConfig", "full") %>'>
	<i18n:message key="loginnow" id="loginnow">
 	 <i18n:messageArg value="<%= pafEnv.getGear().getName(response.getLocale()) %>"/>
	</i18n:message>
                    <core:CreateUrl id="loginSuccessURL" url="<%= pafEnv.getOriginalRequestURI() %>">
                        <core:UrlParam param="paf_dm" value='<%= request.getParameter("paf_dm")%>'/>
                        <core:UrlParam param="paf_gear_id" value='<%= request.getParameter("paf_gear_id")%>'/>
                        <core:UrlParam param="paf_gm" value='<%= request.getParameter("paf_gm")%>'/>

                        <core:CreateUrl id="loginURL" url="/portal/authentication/html/loginForm.jsp">
                            <paf:encodeUrlParam param="successURL" value="<%= loginSuccessURL.getNewUrl()  %>"/>
                            <paf:encodeUrlParam param="userMessage" value="<%= loginnow %>"/>
                            <core:UrlParam param="col_pb"  value="<%= pafEnv.getPage().getColorPalette().getPageBackgroundColor() %>"/>
                            <core:UrlParam param="col_pt"  value="<%= pafEnv.getPage().getColorPalette().getPageTextColor() %>"/>
                            <core:UrlParam param="col_gtb" value="<%= gearTitleBGColorParam %>"/>
                            <core:UrlParam param="col_gtt" value="<%= pafEnv.getPage().getColorPalette().getGearTitleTextColor() %>"/>
                            <core:UrlParam param="col_gb"  value="<%= pafEnv.getPage().getColorPalette().getGearBackgroundColor() %>"/>
                            <core:UrlParam param="col_gt"  value="<%= pafEnv.getPage().getColorPalette().getGearTextColor() %>"/>
                            <td nowrap valign="middle" align="right" bgcolor="<%= gearTitleBGColor %>"><a href="<%= loginURL.getNewUrl() %>" ><img src='<%= "/portal/layoutTemplates" + "/"+i18n_imageroot+"/tc_edit_b.gif" %>' border="0" alt='<i18n:message key="login-first-alt-tag"/>' ></a><img src='<%= clearGIF %>' width="1" height="14" border="0"><img src='<%= clearGIF %>' width="10" height="14" border="0"></td>
                        </core:CreateUrl>
                    </core:CreateUrl>
                </core:If>

                <core:DefaultCase>
                    <td nowrap valign="middle" align="right" bgcolor="<%= gearTitleBGColor %>"><img src='<%= clearGIF %>' width="30" height="14" border="0"><img src='<%= clearGIF %>' width="1" height="14" border="0"><img src='<%= clearGIF %>' width="10" height="14" border="0"></td>
                </core:DefaultCase>
            </core:ExclusiveIf>
        </core:defaultCase>
    </core:exclusiveIf>
    </tr>
</table>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/templates/layoutTemplates.war/web/html/titlebar_template.jsp#2 $$Change: 651448 $--%>
