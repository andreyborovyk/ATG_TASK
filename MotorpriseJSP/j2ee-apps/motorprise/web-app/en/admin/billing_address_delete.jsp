<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Delete Billing Address"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="company_admin.jsp">Company Admin</dsp:a> &gt;
    <dsp:a href="billing_addresses.jsp">Billing Address</dsp:a> &gt; 
	<dsp:a href="billing_address_edit.jsp">Edit Billing Address</dsp:a> &gt; Delete Billing Address</span> </td>
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
            <td colspan=2 valign="top"><span class=big>Company Administration</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr> 
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;Delete Billing Address</td>
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
                  <td align=right width="18%"><span class=smallb>Company Name</span></td>
                  <td width=67%>Central Auto</td>
                </tr>
                <tr> 
                  <td valign="top" align="right" width="18%"><span class=smallb>Address</span></td>
                  <td width="67%">123 Balboa Blvd.
                    <br>
                  </td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>City</span></td>
                  <td width="67%">San Fransisco</td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>State/Province</span></td>
                  <td width="67%">CA</td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>Zip/Postal Code</span></td>
                  <td valign="top" width="67%">93245</td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>Country</span></td>
                  <td width="67%">USA</td>
                </tr>
                <tr> 
                  <td height="56" width="18%"></td>
                  <td height="56" width="67%"><b> 
                    <input type="submit" value="Delete" name="submit">
                    &nbsp; 
                    <input type="submit" value=" Cancel" name="submit">
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/billing_address_delete.jsp#2 $$Change: 651448 $--%>
