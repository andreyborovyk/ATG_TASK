<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Rechnungsanschrift löschen"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="company_admin.jsp">Unternehmensverwaltung</dsp:a> &gt;
    <dsp:a href="billing_addresses.jsp">Rechnungsanschrift</dsp:a> &gt; 
	<dsp:a href="billing_address_edit.jsp">Rechnungsanschrift bearbeiten</dsp:a> &gt; Rechnungsanschrift löschen</span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    
   <dsp:form action="billing_addresses.jsp" method="post">
        <table border=0 cellpadding=4 width=80%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"><span class=big>Unternehmensverwaltung</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr> 
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;Rechnungsanschrift löschen</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr> 
            <td><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr> 
            <td colspan=2> 
        <% /*  Address 1 */ %>
        <table width="100%" border="0" cellpadding="5">
				<tr> 
                  <td align=right width="18%"><span class=smallb>Firmenname</span></td>
                  <td width=67%>Autozentrale</td>
                </tr>
                <tr> 
                  <td valign="top" align="right" width="18%"><span class=smallb>Adresse</span></td>
                  <td width="67%">Königsallee 123
                    <br>
                  </td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>Ort</span></td>
                  <td width="67%">München</td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>Bundesland</span></td>
                  <td width="67%">Bayern</td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>PLZ</span></td>
                  <td valign="top" width="67%">93245</td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>Land</span></td>
                  <td width="67%">Deutschland</td>
                </tr>
                <tr> 
                  <td height="56" width="18%"></td>
                  <td height="56" width="67%"><b> 
                    <input type="submit" value="Löschen" name="submit">
                    &nbsp; 
                    <input type="submit" value=" Abbrechen" name="submit">
                    </b></td>
                </tr>
              
                
              </table>
            </td>
          </tr>
          <%--  vertical space --%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
        </table>
    </dsp:form>
    </td>
  </tr>


</table>
</div>
</body>
</html>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/billing_address_delete.jsp#2 $$Change: 651448 $--%>
