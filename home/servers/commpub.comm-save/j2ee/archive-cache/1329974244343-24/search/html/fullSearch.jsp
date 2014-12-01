<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%@ page import="java.io.*,java.util.*,atg.portal.framework.*" %>
<%@ page import="java.lang.Integer" %>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

<dsp:importbean bean="/atg/portal/gear/search/RepositoryPortalSearchFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/For"/>

<i18n:bundle baseName="atg.portal.gear.search.RepositorySearchResource" localeAttribute="userLocale" changeResponseLocale="false" />

<%
 String clearGif = response.encodeURL("../images/clear.gif");
 String commId   = gearEnv.getCommunity().getId();
 String commName = gearEnv.getCommunity().getName(response.getLocale());
 String pageId   = gearEnv.getPage().getId();
 String pageName = gearEnv.getPage().getName();

 String resultsPerPage = gearEnv.getGearInstanceParameter("resultsPerPage");
 String maxResults = gearEnv.getGearInstanceParameter("maxResults");
 String maxResultsPerRep = gearEnv.getGearInstanceParameter("maxResultsPerRepository");
 String maxGearsPerRep = gearEnv.getGearInstanceParameter("maxGearsPerRepository");

 atg.portal.framework.ColorPalette cp = gearEnv.getPage().getColorPalette();
 String gearTextColor      = cp.getGearTextColor();
 String highlightTextColor = cp.getHighlightTextColor();
 String highlightBGColor   = cp.getHighlightBackgroundColor();
 if (cp.getHighlightBackgroundColor() == null || cp.getHighlightBackgroundColor() == "" ) {
    highlightTextColor = "000000";
    highlightBGColor   = "cccccc";
 }
%>



<%-- insert search box --%>
<blockquote><br>
<%@ include file="searchBox.jspf" %>
</blockquote>
<dsp:getvalueof id="searchHandler" idtype="atg.portal.gear.search.RepositoryPortalSearchFormHandler" bean="RepositoryPortalSearchFormHandler">

<blockquote>


  <font class="small_bold" color="<%=gearTextColor%>"><i18n:message key="search_results_title"/></font>

 <core:ExclusiveIf>

 <core:If value="<%= searchHandler.getTotalResultsNum() <= 0 %>">
   <font class="smaller_bold" color="<%=gearTextColor%>" ><i18n:message key="search_full_no_results_found"/></font><br />
 </core:If>

 <core:IfNot value="<%= searchHandler.getTotalResultsNum() <= 0 %>">

  <%@ include file="pageNavigation.jspf" %>

<table border="0" cellpadding="0" cellspacing="0" width="80%">
  <core:ForEach id="results" values="<%= searchHandler.getSearchResults() %>"
             castClass="atg.portal.framework.search.PortalSearchResult"
             elementId="result">

    <% 
       String gearId = result.getGearId();
       Gear gear = Utilities.getPortal("default").getGearById(gearId);
       String resultGearName = gear.getName(response.getLocale());
       String resultCommName = gear.getParentCommunity().getName(response.getLocale());
       
     %>
<tr bgcolor="<%=highlightBGColor%>"><td colspan="2"><img src="<%=clearGif%>" height="1" width="1" border="0"></td></tr>
<tr>
<td colspan="2"><font class="small" color="<%=gearTextColor%>">&nbsp;&nbsp;
 <a class="gear_content" href="<%= result.getLinkURI() %>"> <%=  result.getDescription() %></a>
 </font>
</td>
</tr>
<tr bgcolor="<%=highlightBGColor%>">
 <td><font class="smaller_bold" color="<%=highlightTextColor%>">&nbsp;&nbsp;
 <i18n:message key="search_item_info_gear"/>&nbsp;</font><font class="smaller" color="<%=highlightTextColor%>"><%= resultGearName %> &nbsp;&nbsp;</td>
 <td><font class="smaller_bold" color="<%=highlightTextColor%>">
  <i18n:message key="search_item_info_community"/>&nbsp;</font><font class="smaller" color="<%=highlightTextColor%>"><%= resultCommName %>
 </font></td>
 </tr>
 <tr>
 <td colspan="2"><font class="smaller" color="<%=gearTextColor%>">

  <%= result.getSummary() %>

 <br><br><br>
 </font></td>
 </tr>

 </core:ForEach>
</table>

<%@ include file="pageNavigation.jspf" %>


  </core:IfNot>
</core:ExclusiveIf>
</blockquote>
</dsp:getvalueof>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/search/search.war/html/fullSearch.jsp#2 $$Change: 651448 $--%>
