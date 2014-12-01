<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>
<paf:InitializeEnvironment id="pafEnv">

  <table border="0" cellpadding="1" cellspacing="0" width="100%">
        <tr>
          <td valign="top" bgcolor="#333333">
            <table border=0 width="100%" cellpadding="0" cellspacing="0">
              <tr>
                <td valign="top" bgcolor="#<%= pafEnv.getPage().getColorPalette().getGearBackgroundColor() %>">
                  <font class="small">

<%--  
  This file will begin the encapsulation of a gear shared mode output 
  - there should also be a gear_html_post_treatment.jsp
  - these file are related and if one is altered the other may need changes too.
  - and these type of rendering files would be called from within the 
    region_template.jsp
  - your configuration may vary
--%>
</paf:InitializeEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/templates/layoutTemplates.war/web/html/gear_pre_treatment.jsp#2 $$Change: 651448 $--%>
