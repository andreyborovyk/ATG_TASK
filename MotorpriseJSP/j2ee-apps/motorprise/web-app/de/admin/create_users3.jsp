<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/ProfileLookUp"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserAddFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Mehrere Benutzer erstellen"/></dsp:include>

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
   

  
    <table border=0 cellpadding=4 width=80%>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Unternehmensverwaltung</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
      </tr>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Neuen Benutzer erstellen</td></tr></table>
        </td>
      </tr>
      <tr>
        <td colspan=2><span class=help>Folgende Benutzer wurden erstellt. Sie haben Zugriff auf alle Daten des Unternehmens bzw. des Gesch‰ftsbereichs.</span></td>
      </tr>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      
      <dsp:droplet name="/atg/dynamo/droplet/For">
        <dsp:param bean="MultiUserAddFormHandler.count" name="howMany"/>
        <dsp:oparam name="output">
          <tr>
            <td align=right><span class=smallb>Benutzer <dsp:valueof param="count"/></span></td>
          </tr>
          <tr>
            <td align=right><span class=smallb>Name</span></td>
            <td width=75%><dsp:valueof bean="MultiUserAddFormHandler.users[param:index].value.firstName"/>

            <dsp:valueof bean="MultiUserAddFormHandler.users[param:index].value.middleName"/>

            <dsp:valueof bean="MultiUserAddFormHandler.users[param:index].value.lastName"/>
            </td>
          </tr>
          <tr>
            <td align=right><span class=smallb>Anmeldung</span></td>
            <td><dsp:valueof bean="MultiUserAddFormHandler.users[param:index].value.login"/></td> 
          </tr>
          <tr>
            <td align=right><span class=smallb>Rolle</span></td>
            <td>
            <dsp:droplet name="ProfileLookUp">
              <dsp:param bean="MultiUserAddFormHandler.users[param:index].repositoryid" name="id"/>
              <dsp:param name="elementName" value="user"/>
              <dsp:oparam name="output">
               <dsp:droplet name="ForEach">
                 <dsp:param name="array" param="user.roles"/>
                 <dsp:param name="elementName" value="roleName"/>
                 <dsp:oparam name="output">
                  <dsp:valueof param="roleName.name"/><BR>
                 </dsp:oparam>
               </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
            </td>
          </tr>

         <tr>
          <td align=right><span class=smallb>Sprache</span></td>
          <td>
          <dsp:droplet name="Switch">
            <dsp:param bean="MultiUserAddFormHandler.users[param:index].value.locale" name="value"/>
            <dsp:oparam name="en_US">
              Englisch
            </dsp:oparam>
            <dsp:oparam name="de_DE">
              Deutsch
            </dsp:oparam>      
            <dsp:oparam name="ja_JP">
              Japaner
            </dsp:oparam> 
          </dsp:droplet>
          </td>
        </tr>
        </dsp:oparam>
      </dsp:droplet>

      <tr>
        <td></td>
        <td><br>
       <span class=smallb><dsp:a href="company_admin.jsp">ZurÅEk zu <dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a></span>
<%--  <input type="submit" value=" Done "> --%>
        </td>
      </tr>     
      <tr>
        <td></td>
        <td></td>
      </tr>
      <%--  vertical space --%>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
  
    </table>
  
    </td>
  </tr>
</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/create_users3.jsp#2 $$Change: 651448 $--%>
