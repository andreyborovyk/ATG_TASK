<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%/* list company and user who is logged in */%>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userdirectory/droplet/HasFunction"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

<!-- table for brandnav layout-->
<table border="0" cellpadding="0" cellspacing="0" width=100%>
  <tr>
    <td>
    <!--table for the upper half of brandnav -->
    <table border="0" cellpadding="0" cellspacing="0" width="100%">
      <tr>
        <td bgcolor="#F7D774">
          <!-- banner -->
          <table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="#F7D774">
          <tr>
            <td><dsp:img src="../images/motorprise-banner-left.gif" alt="Motorprise"/></td>
            <td align="right"><dsp:img src="../images/motorprise-banner-right.jpg"/></td>
          </tr>
        </table> <!-- end banner -->
        </td>
        <td bgcolor="#FFFFFF"><dsp:img src="../images/d.gif"/></td>
        <!-- right side logo, name and logout -->
      
        <td align="right">
        <!-- check if they are logged in -->
        <dsp:droplet name="Switch">
          <dsp:param bean="Profile.transient" name="value"/>
          <dsp:oparam name="false">   <!-- known users -->
            <table border=0 cellpadding=0 cellspacing=0 width=100% height="74">
              <tr>
                <td><!-- logo box -->
                <table border="0" cellpadding="3" cellspacing="0" width="100%" height="49">
                  <tr>
                    <td bgcolor="#FFB24C" align="center">
                    <dsp:droplet name="IsEmpty">
                      <dsp:param bean="Profile.parentOrganization.companyLogo.url" name="value"/>
                      <dsp:oparam name="false">
                        <dsp:getvalueof id="logo" idtype="String" bean="Profile.parentOrganization.companyLogo.url">                      
                          <dsp:img src="<%=logo%>"/>
                        </dsp:getvalueof>  
                      </dsp:oparam>
                      <dsp:oparam name="true">
                        <nobr><b>
                        <dsp:valueof bean="Profile.parentOrganization.name"/></b></nobr><br> 
                      </dsp:oparam>
                    </dsp:droplet>
                    </td>
                  </tr>
                </table><!-- end logo box -->
                </td>
              </tr>
              <tr>
                <td bgcolor="#FFFFFF"><dsp:img src="../images/d.gif"/></td>
              </tr>
              <!-- user name and log out link-->
              <tr>
                <td>
                <table border="0" cellpadding="3" cellspacing="0" width="100%" height="25">
                  <tr>
                    <td bgcolor="#FF9933" align="center">
                      <nobr><dsp:valueof bean="Profile.firstName"/> 
                      <dsp:valueof bean="Profile.lastName"/> is logged in</b> |
                      <b>
                      
                      <dsp:a bean="ProfileFormHandler.logout" href="../../index.jsp" value="">
                        <font color="#ffffff">Log out</font>
                      </dsp:a></b></nobr>
                    </td>
                  </tr>
                </table>
                </td>
              </tr>
            </table>        
          </dsp:oparam> <!-- end logged in oparam -->

          <!-- anonymous/transient users-->
          <dsp:oparam name="true">  
            <!-- right side orange boxes - log in link -->
            <table border="0" cellpadding="0" cellspacing="0" width="100%" height="74">
              <tr>
                <td>
                <table border="0" cellpadding="7" cellspacing="0" width="100%" height="36">
                  <tr>
                    <td bgcolor="#FFCC66" align="center"><dsp:a href="../home.jsp">
                    <b><font color="#000000">Browse Motorprise Catalog</font></b></dsp:a></td>
                  </tr>
                </table>
                </td>
              </tr>
              <tr>
                <td height="1"><dsp:img src="../images/d.gif"/></td>
              </tr>
              <tr>
                <td>
                <table border="0" cellpadding="7" cellspacing="0" width="100%" height="37">
                  <tr>
                    <td bgcolor="#FF9900" align="center"><dsp:a href="../../"><b><font color="#FFFFFF">Customer log in</font></b></dsp:a></td>
                  </tr>
                </table>
                </td>
              </tr>
            </table>
          </dsp:oparam>
        </dsp:droplet>
        </td>
      </tr>
    </table>
    </td>
  </tr>

  <tr><td><dsp:img src="../images/d.gif"/></td></tr>

  <!-- Search block and navigation-->
  <tr>
    <td>
    <table border="0" cellpadding="0" cellspacing="0" width="100%">
      <tr>
        <td bgcolor="#999999">
        <dsp:include page="../search/SimpleSearchFragment.jsp"></dsp:include></td>
        <td width="1"><dsp:img src="../images/d.gif"/></td>
  
        <td valign="top" align="left">  <!-- car graphic and main navigation-->
        <table border=0 cellpadding=0 cellspacing=0 width="100%" height="100%">
          <tr>
            <td align="right" bgcolor="#000000"><dsp:img src="../images/motorprise-bar.jpg"/></td>
          </tr>
          <tr>
            <td><dsp:img src="../images/d.gif"/></td>
          </tr>
          <tr>
            <td align="right" valign="top" bgcolor="#666666">
            <table border=0 cellpadding=0 cellspacing=0 width=100% height=24>
              <tr>
                <td><dsp:img src="../images/d.gif"/></td>
    
                <!-- global navigation links - only for members -->
                <dsp:droplet name="Switch"> 
                  <dsp:param bean="Profile.transient" name="value"/>
                  <dsp:oparam name="false">
                    
                    <!--  display link if user has admin role -->
                    <dsp:droplet name="HasFunction">
                      <dsp:param bean="Profile.id" name="userId"/>
                      <dsp:param name="function" value="admin"/>
                      <dsp:oparam name="true">
                        <dsp:droplet name="Switch">
                          <dsp:param bean="Profile.currentLocation" name="value"/>
                          <dsp:oparam name="admin">
                            <td align="center"><dsp:a href="../admin/business_units.jsp">
                            <b><font color="#FDD30E" size=-1>Company Admin</font></b></dsp:a></td>
                          </dsp:oparam>
                          <dsp:oparam name="default">
                            <td align="center"><dsp:a href="../admin/business_units.jsp">
                            <b><font color="#FFFFFF" size=-1>Company Admin</font></b></dsp:a></td>
                          </dsp:oparam>
                        </dsp:droplet>
                      </dsp:oparam>
                    </dsp:droplet>

                    <dsp:droplet name="Switch">
                      <dsp:param bean="Profile.currentLocation" name="value"/>
                      <dsp:oparam name="product_catalog">
                        <td align="center"><dsp:a href="../home.jsp"><b><font color="#FDD30E" size=-1>Product Catalog</font></b></dsp:a></td>
                      </dsp:oparam>
                      <dsp:oparam name="default">
                        <td align="center"><dsp:a href="../home.jsp">
                        <b><font color="#FFFFFF" size=-1>Product Catalog</font></b></dsp:a></td>
                      </dsp:oparam>
                    </dsp:droplet>
                    
                    <dsp:droplet name="Switch">
                      <dsp:param bean="Profile.currentLocation" name="value"/>
                      <dsp:oparam name="my_account">
                        <td align="center"><dsp:a href="../user/my_account.jsp">
                        <b><font color="#FDD30E" size=-1>My Account</font></b></dsp:a></td>
                      </dsp:oparam>
                      <dsp:oparam name="default">
                        <td align="center"><dsp:a href="../user/my_account.jsp">
                        <b><font color="#FFFFFF" size=-1>My Account</font></b></dsp:a></td>
                      </dsp:oparam>
                    </dsp:droplet>
                    
                    <dsp:droplet name="Switch">
                      <dsp:param bean="Profile.currentLocation" name="value"/>
                      <dsp:oparam name="current_order">
                        <td align="center"><dsp:a href="../checkout/cart.jsp">
                        <b><font color="#FDD30E" size=-1>Current Order</font></b></dsp:a></td>
                      </dsp:oparam>
                      <dsp:oparam name="default">
                        <td align="center"><dsp:a href="../checkout/cart.jsp">
                        <b><font color="#FFFFFF" size=-1>Current Order</font></b></dsp:a></td>
                      </dsp:oparam>
                    </dsp:droplet>
                    
                  </dsp:oparam>
                </dsp:droplet>
              </tr>
            </table>
            </td>
          </tr>
        </table>
        </td>
      </tr>
    </table>
    </td>
  </tr>
  <tr>
    <td><dsp:img src="../images/d.gif"/></td>
  </tr>
</table>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/common/BrandNav.jsp#2 $$Change: 651448 $--%>
