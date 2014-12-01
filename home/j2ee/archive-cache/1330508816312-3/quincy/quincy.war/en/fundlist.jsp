<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<HTML> <HEAD>
<TITLE>Fund list</TITLE>
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
        <td colspan=2><img src="images/banner-quincy-small.gif" hspace=20vspace=4>
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param bean="Profile.usertype" name="value"/>
          <dsp:oparam name="broker">
            <br><img src="images/brokerconnection.gif">
          </dsp:oparam>
        </dsp:droplet></td>
      </tr>
      <tr valign=top>
        <td>
        <table border=0 cellpadding=4 cellspacing=0>
          <tr>
            <td><img src="images/d.gif" hspace=2></td>
            <td><h2>Funds</h2>
            <!-- Stock -->
            <dsp:droplet name="/atg/targeting/TargetingForEach">
              <dsp:param bean="/atg/registry/RepositoryTargeters/Funds/stockFunds" name="targeter"/>
              <dsp:param name="fireContentEvent" value="false"/>
              <dsp:param name="fireContentTypeEvent" value="false"/>
              <dsp:oparam name="outputStart">
                <b>Stock Mutual Funds </b>
                <ul>
              </dsp:oparam>
              <dsp:oparam name="output">
                <dsp:a href="fund.jsp">
                <dsp:param name="ElementId" param="element.repositoryId"/>
                <dsp:valueof param="element.fundName"/></dsp:a> - 
                <dsp:valueof param="element.Symbol"/><br>
              </dsp:oparam>
              <dsp:oparam name="outputEnd">
                </ul>
              </dsp:oparam>
   
            </dsp:droplet>

            <!-- Bond -->
            <dsp:droplet name="/atg/targeting/TargetingForEach">
              <dsp:param bean="/atg/registry/RepositoryTargeters/Funds/bondFunds" name="targeter"/>
              <dsp:param name="fireContentEvent" value="false"/>
              <dsp:param name="fireContentTypeEvent" value="false"/>
              <dsp:oparam name="outputStart">
                <b>Bond Mutual Funds</b>
                <ul>
              </dsp:oparam>
              <dsp:oparam name="output">
                <dsp:a href="fund.jsp">
                <dsp:param name="ElementId" param="element.repositoryId"/>
                <dsp:valueof param="element.fundName"/></dsp:a> - 
                <dsp:valueof param="element.Symbol"/><br>
              </dsp:oparam>
              <dsp:oparam name="outputEnd">
                </ul>
              </dsp:oparam>
            </dsp:droplet>
 
            <!-- Market -->
            <dsp:droplet name="/atg/targeting/TargetingForEach">
              <dsp:param bean="/atg/registry/RepositoryTargeters/Funds/marketFunds" name="targeter"/>
              <dsp:param name="fireContentEvent" value="false"/>
              <dsp:param name="fireContentTypeEvent" value="false"/>
              <dsp:oparam name="outputStart">
                <b>Money Market Mutual Funds</b>
                <ul>  
              </dsp:oparam>
              <dsp:oparam name="output">
                <dsp:a href="fund.jsp">
                <dsp:param name="ElementId" param="element.repositoryId"/>
                <dsp:valueof param="element.fundName"/></dsp:a> - 
                <dsp:valueof param="element.Symbol"/><br>
              </dsp:oparam>
              <dsp:oparam name="outputEnd">
                </ul>
              </dsp:oparam>
            </dsp:droplet></td>
          </tr>
        </table></td>
      </tr>
    </table></td>
  </tr>
</table>

<dsp:include page="../footer.jsp" />

</BODY> </HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/en/fundlist.jsp#2 $$Change: 651448 $--%>
