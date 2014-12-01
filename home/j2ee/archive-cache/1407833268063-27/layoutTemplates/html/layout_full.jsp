<%@ page errorPage="errorPage.jsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>

<paf:InitializeEnvironment id="pafEnv">
<blockquote>
<table border="0" cellpadding="1" cellspacing="0" >
<tr>
<td bgcolor="#660066"><table width="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#<%= pafEnv.getPage().getColorPalette().getGearBackgroundColor() %>"><tr><td valign="top">
<paf:PrepareGearRenderers id="gearRenderers">
  <paf:GetGearMode id="gearMode" defaultGearMode="content">
    <paf:PrepareGearRenderer gearRenderers="<%= gearRenderers.getGearRenderers() %>"
                             gear="<%= pafEnv.getGear() %>"
                             gearMode="<%= gearMode %>" />
  </paf:GetGearMode>
<%
   // On WebSphere the JspWriter needs to be flushed to the ServletOutputStream
   // As the tag uses the PrintWriter to write its content.  Inorder for the
   // Content to appear in the correct order the JspWriter needs to be flushed.
   if(atg.servlet.ServletUtil.isWebSphere())
      out.flush();
%>
  <paf:RenderPreparedGear gear="<%= pafEnv.getGear() %>"
                          gearRenderers="<%= gearRenderers.getGearRenderers() %>" />
</paf:PrepareGearRenderers>

</td></tr></table></td></tr></table>
</blockquote>

</paf:InitializeEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/templates/layoutTemplates.war/web/html/layout_full.jsp#2 $$Change: 651448 $--%>
