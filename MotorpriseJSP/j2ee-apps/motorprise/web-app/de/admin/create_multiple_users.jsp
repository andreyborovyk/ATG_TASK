<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserAddFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Mehrere Benutzer erstellen"/></dsp:include>

<dsp:setvalue bean="MultiUserAddFormHandler.clear" value=""/>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Unternehmensverwaltung</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; 
    Mehrere Benutzer erstellen</td>
  </tr>


  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>
    <dsp:form action="create_users2.jsp" method="post">  
    <input type="hidden" name="b2bOp" value="add">
    <table border=0 cellpadding=4 width=80%>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Unternehmensverwaltung</span></td>
      </tr>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Mehrere Benutzer erstellen</td></tr></table>
        </td>
      </tr>

      <tr><td><dsp"img src="../images/d.gif" vspace="0"/></td></tr>

      <tr>
        <td><dsp:input bean="MultiUserAddFormHandler.count" maxlength="1" size="1" type="text" value=""/> Benutzer erstellen. 
        <p>
        <input type="submit" value="Benutzer erstellen">  
        </td>      
      </tr>

    </table>
    </dsp:form>
    </td>
  </tr>

</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/create_multiple_users.jsp#2 $$Change: 651448 $--%>
