<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<HTML><HEAD>
<TITLE>Feature List</TITLE>
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
        <table border=0 width=500 cellpadding=4 cellspacing=0>
          <tr>
            <td><img src="images/d.gif" hspace=2></td>
            <td>
                <h2>Features</h2>

            <!-- list all features - only brokers see "broker" features -->
            <dsp:droplet name="/atg/targeting/TargetingForEach">
              <dsp:param bean="/atg/registry/RepositoryTargeters/Features/AllFeatures" name="targeter"/>
              <dsp:param name="sortProperties" value="+title"/>
              <dsp:param name="fireContentEvent" value="false"/>
              <dsp:param name="fireContentTypeEvent" value="false"/>
              <dsp:oparam name="outputStart">
                <table border=0 cellpaddng=2>
              </dsp:oparam>
              
              <dsp:oparam name="output">
                <tr valign=top>
                  <td><dsp:a href="feature.jsp">
                      <dsp:param name="ElementId" param="element.repositoryId"/>
                      <img vspace="2" border="0" hspace="8" src="<dsp:valueof param="element.smallImageURL">images/features/noimage.gif</dsp:valueof>"></dsp:a></td>
                  <td><dsp:a href="feature.jsp">
                      <dsp:param name="ElementId" param="element.repositoryId"/>
                      <b><dsp:valueof param="element.title"/></b></dsp:a><br>
                      <dsp:valueof param="element.headline"/><br>
                      <dsp:valueof param="element.author"/></td>
                </tr>
              </dsp:oparam>
              
              <dsp:oparam name="outputEnd">
                </table>
              </dsp:oparam>    
              <dsp:oparam name="empty">
                No features today.
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
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/en/featurelist.jsp#2 $$Change: 651448 $--%>
