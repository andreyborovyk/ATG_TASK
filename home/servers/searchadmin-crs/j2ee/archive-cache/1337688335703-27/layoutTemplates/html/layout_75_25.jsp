<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>
<paf:InitializeEnvironment id="pafEnv">

<%
 String thisRegionLayoutName; 
%>

<center>
<table border="0" width="96%" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" width="75%" >
      <% thisRegionLayoutName = "75_25_Left"; %>
      <%@ include file="region_template.jspf" %>
    </td>
    <td>
      <img src="/portal/layoutTemplates/images/clear.gif" width="8" height="1" border="0">
    </td>
    <td valign="top" width="25%">

      <% thisRegionLayoutName = "75_25_Right"; %>
      <%@ include file="region_template.jspf" %>
    </td>
  </tr>
</table>
</center>

</paf:InitializeEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/templates/layoutTemplates.war/web/html/layout_75_25.jsp#2 $$Change: 651448 $--%>
