 <%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<DECLAREPARAM NAME="ElementId" CLASS="java.lang.String" DESCRIPTION="Id of the repository element to retrieve">
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<HTML> <HEAD>

<dsp:droplet name="/atg/targeting/RepositoryLookup">
  <dsp:param bean="/atg/demo/QuincyFunds/repositories/Funds/Funds" name="repository"/>
  <dsp:param name="itemDescriptor" value="fund"/>
  <dsp:param name="id" param="ElementId"/>
  <dsp:oparam name="output">
    <TITLE><dsp:valueof param="element.fundName"/></TITLE>
    </HEAD>
    <body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

    <table border=0 cellpadding=4 cellspacing=0>
      <tr valign=top>
        <td width=120 bgcolor=#003366 rowspan=2>
        <!-- left bar navigation -->
        <dsp:include page="nav.jsp" ></dsp:include></td>

        <td>
        <table border=0>
          <tr>
            <td colspan=2><img src="images/banner-quincy-small.gif" hspace=20 vspace=4>
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param bean="Profile.usertype" name="value"/>
              <dsp:oparam name="broker">
                <br><img src="images/brokerconnection.gif">
              </dsp:oparam>
            </dsp:droplet></td>
    
          </tr>
          <tr valign=top>
            <td>
            <table border=0 width=500 cellpadding=4 cellspacing=0>
              <tr>
                <td><img src="images/d.gif" hspace=4></td>
                <td>
                <dsp:a href="fundlist.jsp">Fund List</dsp:a>
                <p>
                <b><font size=+2><dsp:valueof param="element.fundName"/></font></b>
                <!-- fund info sub-navigation -->
                <p>
                Overview  | <font color=#003366><u>Portfolio Info</font></u> |
                <font color=#003366><u>Key Statistics</font></u> |
                <font color=#003366><u>Management Team</u></font>
                
    
                <p>
                <!-- set value of template to broker template or standard template -->
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param bean="Profile.usertype" name="value"/>
                  <dsp:oparam name="default">
                    <dsp:droplet name="/atg/dynamo/droplet/xml/XMLTransform">
                      <dsp:param name="input" param="element.document"/>
                      <dsp:param name="template" value="funds-investor-template.xsl"/>
                     
                      <dsp:oparam name="failure">
                        <p> Failure to transform XML document: <dsp:valueof param="input"/>
                      </dsp:oparam>
                    </dsp:droplet>
                    <dsp:a href="sendprospectus.jsp">
                    <p><dsp:param name="ElementId" param="ElementId"/>Send me the application and prospectus</dsp:a>
                  </dsp:oparam>
                  <dsp:oparam name="broker">
                    <dsp:droplet name="/atg/dynamo/droplet/xml/XMLTransform">
                      <dsp:param name="input" param="element.document"/>
                      <dsp:param name="template" value="funds-broker-template.xsl"/>
                 
                      <dsp:oparam name="failure">
                        <p> Failure to transform XML document: <dsp:valueof param="input"/>
                      </dsp:oparam>
    
                    </dsp:droplet>
                  </dsp:oparam>
                  <dsp:oparam name="unset">
                    Unset
                  </dsp:oparam>
                </dsp:droplet>
            
                </td>
              </tr>
            </table></td>
          </tr>
        </table>
        </td>
      </tr>
    </table>

  </dsp:oparam>
  <dsp:oparam name="empty">
    You must access this page with a link that contains the repository id
  of the fund you want to see passed as a parameter.
  </dsp:oparam>
</dsp:droplet>

</BODY> </HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/de/fund.jsp#2 $$Change: 651448 $--%>
