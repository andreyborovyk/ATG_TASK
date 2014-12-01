<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>



<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  会社管理"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">会社管理</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; 
    <dsp:a href="users.jsp">ユーザの表示</dsp:a> &gt; 
    <dsp:a href="user.jsp">ユーザアカウント</dsp:a> &gt; 
    <dsp:a href="user_edit_info.jsp">ユーザプロファイルの編集</dsp:a> &gt; 
    役割の削除
    </span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    
        <table border=0 cellpadding=4 width=80%>
          <tr>
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"><span class=big>会社管理</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          
          <tr> 
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;役割の削除</td>
                </tr>
              </table>
            </td>
          </tr>
          
          <dsp:form action="roles.jsp" method="post">
          <tr><td><span class=help>このユーザについて削除する役割を選択してください</span></td></tr>
        
          <tr valign=top>
            <td>
              &nbsp;<input type=checkbox>&nbsp;購入者 - USMW<br>
              &nbsp;<input type=checkbox>&nbsp;承認者 - USMW<br>
              &nbsp;<input type=checkbox>&nbsp;管理者 - USMW</td>
            </td> 
          </tr>
          <tr> 
            <td><input type="submit" value="削除">&nbsp;<input type="reset" value="キャンセル"></td>
          </tr>   
          </dsp:form>
    
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
           
        
          <%--  vertical space --%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
        </table>
    </td>
  </tr>


</table>
</div>
</body>
</html>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/admin/role_delete.jsp#2 $$Change: 651448 $--%>
