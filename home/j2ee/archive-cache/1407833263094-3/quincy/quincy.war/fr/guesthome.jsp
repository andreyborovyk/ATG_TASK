<%@ taglib uri="/dspTaglib" prefix="dsp" %> 
<%@ page contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="atg.servlet.*"%> 
<dsp:page>

<dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<!-- main outside table. two collumns -->
<table width=625 cellspacing=0 cellpadding=0 border=0>
  <tr valign=top>

    <!-- left collumn -->
    <td bgcolor=#003366 rowspan=2><dsp:include page="nav.jsp" ></dsp:include></td>
  
    <!-- gutter-->
    <td>&nbsp;</td>
  
    <!-- banner area -->
    <td colspan=3>
    <img src="images/banner-quincy-small.gif" hspace=20 vspace=4 alt="Quincy Funds">
   
 </td>
    
  </tr>
  <tr valign=top>
    <!-- gutter-->
    <td>&nbsp;</td>

    <!-- middle collumn -->
    <td><table cellspacing=0 cellpadding=0 border=0 width=250 
       background="images/purple-lines.gif"></td>
  </tr>
  <tr valign=top>
    <td colspan=2><img src="images/banner-features.gif" alt="fonctionnalités"></td>
  </tr>

  <!-- Features -->
    <!-- display warning if language is Japanese and they don't have the
    character set -->
    <dsp:droplet name="/atg/targeting/TargetingFirst">
      <dsp:param bean="/atg/registry/RepositoryTargeters/Images/JapaneseWarning" name="targeter"/>
      <dsp:param name="howMany" value="1"/>
      <dsp:oparam name="output">
        <dsp:valueof param="element.description"/>
        <p>
      </dsp:oparam>
    </dsp:droplet>

  <dsp:droplet name="/atg/targeting/TargetingRange">
    <dsp:param bean="/atg/registry/RepositoryTargeters/Features/Features" name="targeter"/>
    <dsp:param name="howMany" value="4"/>
    <dsp:param name="fireContentEvent" value="false"/>
    <dsp:param name="fireContentTypeEvent" value="false"/>
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
      <tr><td colspan=3><img src="images/d.gif" width=1 height=10></td></tr>
    </dsp:oparam>
    <dsp:oparam name="empty">
      <TD></TD> <TD><B>Aucune fonctionnalité n'a été trouvée.</B> </TD>
    </dsp:oparam>

  </dsp:droplet>

  <tr valign=top>
    <td colspan=2><dsp:a href="featurelist.jsp">
      <img src="images/but-indexfeatures.gif" alt="index des fonctionnalités" border=0></dsp:a></td>
  </tr>
  </table>
  
  <!-- signup promotion -->
  <dsp:droplet name="/atg/targeting/TargetingFirst">
    <dsp:param bean="/atg/registry/Slots/QFHomePageSlot" name="targeter"/>
    <dsp:param name="howMany" value="1"/>
    <dsp:param name="fireContentEvent" value="false"/>
    <dsp:param name="fireContentTypeEvent" value="false"/>
    <dsp:oparam name="output">
     		<img src="images/d.gif" vspace=4><br>
        <dsp:a href="signup.jsp"><img border="0" alt="Inscrivez-vous !" src="<dsp:valueof param="element.imageURL"/>"></dsp:a>
    </dsp:oparam>
  </dsp:droplet>
<dsp:form method="POST" action="<%=ServletUtil.getRequestURI(request)%>">

  <table border=0>
    <tr valign=top>
      <td>
       <dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="../index.jsp"/>
       <dsp:input bean="ProfileFormHandler.updateErrorURL" type="HIDDEN" value="../index.jsp"/>
       Choisissez une langue :</td>
      <td><dsp:select bean="ProfileFormHandler.value.locale">
            <dsp:option value="en_US"/>Anglais
            <dsp:option value="fr_FR"/>Français
            <dsp:option value="de_DE"/>Allemand
            <dsp:option value="ja_JP"/>Japonais
          </dsp:select><br>
          <dsp:input bean="ProfileFormHandler.update" type="submit" value="Modifier"/>
        </td>
    </tr>
  </table>
  </dsp:form>

  <br></td>
  
  <!-- gutter-->
  <td>&nbsp;</td>
  
  
  <!-- right collumn -->
  <td>
  <!-- success image -->
  <dsp:droplet name="/atg/targeting/TargetingFirst">
    <dsp:param bean="/atg/registry/RepositoryTargeters/Images/successPromo" name="targeter"/>
    <dsp:param name="howMany" value="1"/>
    <dsp:param name="fireContentEvent" value="false"/>
    <dsp:param name="fireContentTypeEvent" value="false"/>
    <dsp:oparam name="output">
      <img border="0" alt="récits d'investissements réussis" src="<dsp:valueof param="element.imageURL"/>"><br>
    </dsp:oparam>
  </dsp:droplet>

  <!-- News -->
  <table cellspacing=0 cellpadding=0 border=0 background="images/greenish-lines.gif" width=250>
    <tr>
      <td colspan=2><img src="images/banner-news.gif" alt="infos"></td>
    </tr>
     <tr><td colspan=2><img src="images/d.gif" vspace=4></td></tr>
    <dsp:droplet name="/atg/targeting/TargetingRange">
      <dsp:param bean="/atg/registry/RepositoryTargeters/News/News" name="targeter"/>
      <dsp:param bean="Profile.numbernewsitems" name="howMany"/>
      <dsp:param name="fireContentEvent" value="false"/>
      <dsp:param name="fireContentTypeEvent" value="false"/>
      <dsp:param name="sortProperties" value="-date"/>
     
      <dsp:oparam name="output">
        <tr valign=top>
          <td><dsp:a href="news.jsp">
          <dsp:param name="ElementId" param="element.repositoryId"/>
          <img src="images/news-bullet.gif" hspace=6 vspace=4 border=0></dsp:a></td>
          <td><dsp:a href="news.jsp">
          <dsp:param name="ElementId" param="element.repositoryId"/>
          <dsp:valueof param="element.Headline"/></dsp:a></td>
        </tr>
         <tr><td colspan=2><img src="images/d.gif" vspace=4></td></tr>
      </dsp:oparam>
      <dsp:oparam name="empty">
        <tr valign=top">
          <TD><img src="images/news-bullet.gif" hspace=6 vspace=4></TD>
          <TD>Aucune info aujourd'hui !<br> 
          <dsp:valueof bean="Profile.strategy"/> 
           <dsp:valueof bean="Profile.goals"/></TD>
        <tr><td colspan=2>&nbsp;<br></td></tr>
  
      </dsp:oparam>
    </dsp:droplet>
    
    <tr>
      <td colspan=2><dsp:a href="newslist.jsp"><img
      src="images/but-readnews.gif" alt="plus d'infos" border=0></dsp:a></td>
    </tr>
  </table>
  <img src="images/d.gif" vspace=4><br>
  <!-- Fundlist -->
  <table cellspacing=0 cellpadding=0 border=0 background="images/yellow-lines.gif">
    <tr valign=top>
      <td><img src="images/banner-funds.gif" alt="Nos fonds">
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
              <dsp:valueof param="element.fundName"/><br></dsp:a>
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
              <dsp:valueof param="element.fundName"/><br></dsp:a>
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
              <dsp:valueof param="element.fundName"/><br></dsp:a>
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
  <dsp:a href="fundlist.jsp"><img src="images/banner-compare.gif" alt="comparer" border=0></dsp:a>


  </td></tr>
  
  
  </table>
</td>

<!-- end of main outside table -->
</tr>
</table>
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/guesthome.jsp#2 $$Change: 651448 $ */%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/guesthome.jsp#2 $$Change: 651448 $--%>
