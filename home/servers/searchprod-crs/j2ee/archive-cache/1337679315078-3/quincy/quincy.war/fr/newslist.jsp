<%@ taglib uri="/dspTaglib" prefix="dsp" %> 
<%@ page contentType="text/html; charset=ISO-8859-1" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<HTML><HEAD>
<TITLE>Liste des infos</TITLE>
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
        <td colspan=2><img src="images/banner-quincy-small.gif" hspace=20 vspace=4></td>
      </tr>
      <tr valign=top>
        <td>
        <table border=0 width=400 cellpadding=4 cellspacing=0>
          <tr>
            <td><img src="images/d.gif" hspace=2></td>
            <td><h2>Infos</h2>

            <!-- List all news items -->
            <dsp:droplet name="/atg/targeting/TargetingForEach">
              <dsp:param bean="/atg/registry/RepositoryTargeters/News/NewsList" name="targeter"/>
              <dsp:param name="fireContentEvent" value="false"/>
              <dsp:param name="fireContentTypeEvent" value="false"/>
              <dsp:param name="sortProperties" value="-date"/>
              <dsp:oparam name="outputStart">
                <table border=0 cellpaddng=2>
              </dsp:oparam>
               <dsp:oparam name="output">
                <tr valign=top>
                  <td><dsp:a href="news.jsp">
                    <dsp:param name="ElementId" param="element.repositoryId"/>
                    <img src="images/news-bullet.gif" hspace=6 vspace=4 border=0></dsp:a></td> 
                  <td><dsp:param name="ElementId" param="element.repositoryId"/>
                    <dsp:a href="news.jsp">
                    <dsp:param name="ElementId" param="element.repositoryId"/>
                    <b><dsp:valueof param="element.headline"/></b></dsp:a>
                    <br>
                    Par  <dsp:valueof param="element.author"/></td>
                </tr>
              </dsp:oparam>
              <dsp:oparam name="outputEnd">
                </table>
              </dsp:oparam>    
              <dsp:oparam name="empty">
                Aucune info aujourd'hui !
              </dsp:oparam>
            </dsp:droplet>
           </td>
          </tr>
         </table>
         </td>
       </tr>
     </table>
     </td>
   </tr>
</table>

<dsp:include page="../footer.jsp" />

</BODY> </HTML>
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/newslist.jsp#2 $$Change: 651448 $ */%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/newslist.jsp#2 $$Change: 651448 $--%>
