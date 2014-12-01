<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/ProfileLookUp"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserAddFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Neuen Benutzer erstellen"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="company_admin.jsp">Unternehmensverwaltung</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
    Neuen Benutzer erstellen</span></td>
  </tr>
  

  <tr valign=top> 
    <td width="55"><dsp:img src="../images/d.gif" hspace="27"/></td>

    <% /*  main content area */ %>
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
        <td colspan=2><span class=small>Der folgende Benutzer wurde erstellt. Er hat Zugriff auf alle Daten des Unternehmens bzw. des Gesch‰ftsbereichs, solange keine weiteren Beschr‰nkungen festgelegt werden.</span></td>
      </tr>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr>
        <td align=right><span class=smallb>Name</span></td>
        <td width=75%><dsp:valueof bean="MultiUserAddFormHandler.users[0].value.firstName">keine Kennung</dsp:valueof>&nbsp;<dsp:valueof bean="MultiUserAddFormHandler.users[0].value.middleName"/>&nbsp;<dsp:valueof bean="MultiUserAddFormHandler.users[0].value.lastName"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Anmeldung</span></td>
        <td><dsp:valueof bean="MultiUserAddFormHandler.users[0].value.login">keine Kennung</dsp:valueof></td> 
      </tr>
      <tr>
        <td align=right><span class=smallb>E-Mail</span></td>
        <td><dsp:valueof bean="MultiUserAddFormHandler.users[0].value.email">keine Kennung</dsp:valueof></td> 
      </tr>
      <tr>
        
          <td align=right valign=top><span class=smallb>Rolle</span></td>
            <td>
             <dsp:droplet name="ProfileLookUp">
              <dsp:param bean="MultiUserAddFormHandler.users[0].repositoryid" name="id"/>
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
          <dsp:param bean="MultiUserAddFormHandler.users[0].value.locale" name="value"/>
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
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr>
        <td colspan=2><dsp:a href="user.jsp"><dsp:param bean="MultiUserAddFormHandler.users[0].repositoryid" name="userId"/><dsp:param name="createUser" value="new"/><span class=smallb>Benutzerprofil vollst‰ndig anzeigen</span></dsp:a><br><dsp:a href="company_admin.jsp"><span class=smallb>ZurÅEk zu <dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a></td>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/new_user2.jsp#2 $$Change: 651448 $--%>
