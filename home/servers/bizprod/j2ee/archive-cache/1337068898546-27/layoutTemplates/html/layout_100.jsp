<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>
<paf:InitializeEnvironment id="pafEnv">

<% String thisRegionLayoutName = "100_Middle"; %>

<center>
<table border="0" width="96%" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" width="100%" >
      <%@ include file="region_template.jspf" %>
    </td>
  </tr>
</table>
</center>

</paf:InitializeEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/templates/layoutTemplates.war/web/html/layout_100.jsp#2 $$Change: 651448 $--%>
