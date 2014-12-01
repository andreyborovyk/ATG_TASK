<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserAddFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  複数のユーザの作成"/></dsp:include>

<dsp:setvalue bean="MultiUserAddFormHandler.clear" value=""/>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">会社管理</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; 
    複数のユーザの作成</td>
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
        <td colspan=2><span class=big>会社管理</span></td>
      </tr>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;複数のユーザの作成</td></tr></table>
        </td>
      </tr>

      <tr><td><dsp"img src="../images/d.gif" vspace="0"/></td></tr>

      <tr>
        <td>ユーザを<dsp:input bean="MultiUserAddFormHandler.count" maxlength="1" size="1" type="text" value=""/> 作成します。 
        <p>
        <input type="submit" value="ユーザの作成">  
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/admin/create_multiple_users.jsp#2 $$Change: 651448 $--%>
