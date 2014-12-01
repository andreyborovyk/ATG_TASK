<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>


<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

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
 String gearTextColor = cp.getGearTextColor();
 String highlightTextColor = cp.getHighlightTextColor();
 String highlightBGColor   = cp.getHighlightBackgroundColor();
 if (cp.getHighlightBackgroundColor() == null || cp.getHighlightBackgroundColor() == "" ) {
    highlightTextColor = "000000";
    highlightBGColor   = "cccccc";
 }
%>
<br>
<%@ include file="searchBox.jspf" %>
<br><br>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/search/search.war/html/sharedSearch.jsp#2 $$Change: 651448 $--%>
