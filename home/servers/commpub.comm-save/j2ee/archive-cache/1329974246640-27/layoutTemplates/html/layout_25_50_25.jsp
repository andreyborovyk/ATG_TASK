<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>
<paf:InitializeEnvironment id="pafEnv">

<%
 String thisRegionLayoutName;
 String clearGIF = "/portal/layoutTemplates/images/clear.gif";
 %>
<center>
<table border="0" width="96%" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" width="25%" >
      <% thisRegionLayoutName = "25_50_25_Left"; %>
      <%@ include file="region_template.jspf" %>
    </td>
    <td>
      <img src='<%= clearGIF %>' width="8" height="1" border="0">
    </td>
    <td valign="top" width="50%">
      <% thisRegionLayoutName = "25_50_25_Middle"; %>
      <%@ include file="region_template.jspf" %>
    </td>
    <td>
      <img src='<%= clearGIF %>' width="8" height="1" border="0">
    </td>
    <td valign="top" width="25%">
      <% thisRegionLayoutName = "25_50_25_Right"; %>
      <%@ include file="region_template.jspf" %>
    </td>
  </tr>
</table>
</center>

</paf:InitializeEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/templates/layoutTemplates.war/web/html/layout_25_50_25.jsp#2 $$Change: 651448 $--%>
