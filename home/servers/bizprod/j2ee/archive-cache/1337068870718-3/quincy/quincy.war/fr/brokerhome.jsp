<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=ISO-8859-1" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<!-- start doc - index has opening html tags -->

<table width=625 cellspacing=0 cellpadding=0 border=0>
<tr valign=top>

  <!-- left collumn -->
  <td bgcolor=#003366 rowspan=2>
    <dsp:include page="nav.jsp" ></dsp:include></td>
  
  <!-- gutter-->
  <td>&nbsp;</td>
  
  <!-- banner area -->
  <td colspan=3>
    <img src="images/banner-broker-red.gif" vspace=4 
      hspace=38 alt="Quincy Funds"><br>
    <img src="images/brokerconnection.gif"></td>
    
</tr>
<tr valign=top>

  <!-- gutter-->
  <td>&nbsp;</td>

  <!-- middle collumn -->
  <!-- News -->
  <td><table cellspacing=0 width=250 cellpadding=0 border=0 background="images/greenish-lines.gif">
  <tr>
    <td colspan=2><img src="images/banner-news.gif" alt="infos"></td>
  </tr>
  <tr><td colspan=2><img src="images/d.gif" vspace=4></td></tr>
  <dsp:droplet name="/atg/targeting/TargetingRange">
    <dsp:param bean="/atg/registry/RepositoryTargeters/News/News" name="targeter"/>
    <dsp:param bean="Profile.numbernewsitems" name="howMany"/>
    <dsp:param name="sortProperties" value="-date"/>
    
    <dsp:oparam name="output">
      <tr valign=top>
        <td><dsp:a href="news.jsp">
        <dsp:param name="ElementId" param="element.repositoryId"/>
        <img src="images/news-bullet.gif" hspace=6 vspace=4 border=0></dsp:a></td>
        <td><dsp:a href="news.jsp">
        <dsp:param name="ElementId" param="element.repositoryId"/>
        <dsp:valueof param="element.headline"/></dsp:a>
        </font></td>
      </tr>
      <tr><td colspan=2><img src="images/d.gif" vspace=4></td></tr>
    </dsp:oparam>
    <dsp:oparam name="empty">
      <tr valign=top>
        <TD><img src="images/news-bullet.gif" hspace=6 vspace=4></TD>
        <TD>Aucune info aujourd'hui !<br></TD>
     </tr>
     <tr><td colspan=2><img src="images/d.gif" vspace=4></td></tr>
    </dsp:oparam>
  </dsp:droplet>
    
  <tr>
    <td colspan=2><dsp:a href="newslist.jsp"><img src="images/but-readnews.gif" 
     border=0 alt="plus d'infos"></dsp:a></td>
  </tr>
</table>
<img src="images/d.gif" vspace=4><br>

<!-- Features -->  
<table cellspacing=0 cellpadding=0 border=0 width=250 
  background="images/purple-lines.gif">
   
  <tr valign=top>
    <td colspan=2>
    <img src="images/banner-features.gif" alt="fonctionnalités"></td>
  </tr>

  <dsp:droplet name="/atg/targeting/TargetingRange">
    <dsp:param bean="/atg/registry/RepositoryTargeters/Features/BrokerFeatures" name="targeter"/>
    <dsp:param bean="Profile.numberfeatureitems" name="howMany"/>
    <dsp:param name="sortProperties" value="+title"/>
    <dsp:oparam name="outputStart">
      <tr><td colspan=2><img src="images/d.gif" vspace=4></td></tr>
    </dsp:oparam>
    <dsp:oparam name="output">
      <tr valign=top>
        <td><dsp:a href="feature.jsp">
        <dsp:param name="ElementId" param="element.repositoryId"/>
        <img height="73" vspace="2" border="0" hspace="8" width="73" src="<dsp:valueof param="element.smallImageURL">images/features/noimage.gif</dsp:valueof>"></dsp:a></td>
        <td><dsp:a href="feature.jsp">
        <dsp:param name="ElementId" param="element.repositoryId"/>
        <b><dsp:valueof param="element.title"/></b></dsp:a><br>
        <dsp:valueof param="element.headline"/></td>
      </tr>
      <tr><td colspan=2><img src="images/d.gif" width=1 height=10></td></tr>
    </dsp:oparam>
    <dsp:oparam name="empty">
      <dsp:valueof bean="Profile.goals"/>
      <TD>.</TD> <TD><B>Aucune fonctionnalité n'a été trouvée.</B> </TD>
    </dsp:oparam>
  </dsp:droplet>
  <tr valign=top>
    <td colspan=2><dsp:a href="featurelist.jsp">
    <img src="images/but-indexfeatures.gif" alt="index des fonctionnalités" border=0></dsp:a><br></td>

  </tr>
</table>  
<br></td>
  <!-- gutter-->
  <td>&nbsp;</td>
  
  <!-- right collumn -->
  <td>
  <!-- client list -->
  <table cellspacing=0 cellpadding=0 border=0>
  <tr valign=top>
    <td colspan=2>
    <dsp:include page="clientlist.jsp" ></dsp:include>

  <br>
  <!-- Funds -->
  <table cellspacing=0 cellpadding=0 border=0 width=250
    background="images/yellow-lines.gif">
  <tr valign=top>
    <td>
    <img src="images/banner-funds.gif" alt="Nos fonds">
    <table border=0 cellspacing=0 cellpadding=0>
    <tr>
      <td><img src="images/d.gif" vspace=4></td>
      <td></td>
    </tr>
    <tr>
      <td>&nbsp;&nbsp;</td>
      <td>
         <!-- Stock -->
          <dsp:droplet name="/atg/targeting/TargetingForEach">
            <dsp:param bean="/atg/registry/RepositoryTargeters/Funds/stockFunds" name="targeter"/>
            <dsp:oparam name="output">
	      <dsp:a href="fund.jsp">
   	      <dsp:param name="ElementId" param="element.repositoryId"/>
              <dsp:valueof param="element.fundName"/></dsp:a><br>
            </dsp:oparam>
            <dsp:param name="fireContentEvent" value="false"/>
            <dsp:param name="fireContentTypeEvent" value="false"/>
          </dsp:droplet> 

          <!-- Bond -->
          <dsp:droplet name="/atg/targeting/TargetingForEach">
            <dsp:param bean="/atg/registry/RepositoryTargeters/Funds/bondFunds" name="targeter"/>
            <dsp:oparam name="outputStart">
              <img src="images/d.gif" vspace=4><br>
            </dsp:oparam>
            <dsp:oparam name="output">
              <dsp:a href="fund.jsp">
              <dsp:param name="ElementId" param="element.repositoryId"/>
              <dsp:valueof param="element.fundName"/></dsp:a><br>
            </dsp:oparam>
            <dsp:param name="fireContentEvent" value="false"/>
            <dsp:param name="fireContentTypeEvent" value="false"/>
          </dsp:droplet>
          
          <!-- Market -->
          <dsp:droplet name="/atg/targeting/TargetingForEach">
            <dsp:param bean="/atg/registry/RepositoryTargeters/Funds/marketFunds" name="targeter"/>
            <dsp:oparam name="outputStart">
              <img src="images/d.gif" vspace=4><br>
            </dsp:oparam>
            <dsp:oparam name="output">
	      <dsp:a href="fund.jsp">
	      <dsp:param name="ElementId" param="element.repositoryId"/>
              <dsp:valueof param="element.fundName"/></dsp:a><br>
            </dsp:oparam>
            <dsp:param name="fireContentEvent" value="false"/>
            <dsp:param name="fireContentTypeEvent" value="false"/>
          </dsp:droplet>
      </td>
    </tr>
    <tr>
    <td><img src="images/d.gif" vspace=4></td>
    <td></td>
  </tr>
  </table>

  <img src="images/but-brokerfundtools.gif" alt="comparer"></td>
  </tr>
   
  </table>
  <!-- broker sales tools -->
  <table cellspacing=0 cellpadding=0 border=0>
  <tr><td colspan=2><img src="images/d.gif" vspace=4></td></tr>
  <tr valign=top>
    <td colspan=2><img src="images/banner-salestools.gif"></td>
  </tr>
  <tr valign=top>
    <td><table border=0 cellspacing=0 cellpadding=0>
    <tr>
      <td><img src="images/d.gif" vspace=4></td>
      <td></td>
    </tr>
    <tr>
      <td>&nbsp;&nbsp;</td>
      <td>
      <u><font color=#003366>Présentations Quincy</font></u><br>
      <u><font color=#003366>Comparaisons de fonds</font></u><br>
      <u><font color=#003366>Perspectives disponibles</font></u></td>
    </tr>
    </table>
    </td>
  </tr>  
  </table>
  </td>

</tr>
    
</table>
  
</td>

<!-- end of main outside table -->
</tr>
</table>

<dsp:include page="../footer.jsp" />

<!-- end doc, index has closing tags -->
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/brokerhome.jsp#2 $$Change: 651448 $ */%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/brokerhome.jsp#2 $$Change: 651448 $--%>
