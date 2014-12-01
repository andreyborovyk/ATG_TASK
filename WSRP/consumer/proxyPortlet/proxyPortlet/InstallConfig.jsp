 <%--
 InstallConfig gear mode main page for the consumer proxy portlet.
 It will show the links for the various configuration pages as per the proposed UI
 and will include their content as per the context.
 --%>

<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>


<dspel:page>

<dspel:importbean bean="/atg/wsrp/consumer/ProducerFormHandler" var="ProducerFormHandler"/>

<%--
  Set the GEAR DEFINITION ID so that the InitializeGearEnvironment 
  tag will work.  This is required on all installConfig pages. 
 --%>   
<% request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION, (String) request.getParameter("paf_gear_def_id") ); %>

<%--<paf:InitializeGearEnvironment id="pafEnv">--%>
<%--<paf:PortalAdministratorCheck>--%>
<paf:hasRole roles="portal-admin">


<HTML><HEAD>
<TITLE>Proxy Portlet: Install Config</TITLE>
<META http-equiv=Content-Type content="text/html; charset=utf-8">
<style type="text/css">
 <!--

body { font-family:verdana,arial,geneva,helvetica,sans-serif ; }
 font { font-family:verdana,arial,geneva,helvetica,sans-serif ; }


 a          { font-family:verdana,arial,geneva,helvetica,sans-serif ; font-size: 10px; }
 a:hover    { text-decoration:underline; }
 a:active   { text-decoration:underline; color: #0000CC;}

 a.portaladmin_nav       { color: #4D4E4F; text-decoration: none; }
 a.portaladmin_leftnav   { color: #4D4E4F; text-decoration: none; }

 a.breadcrumb   { font-size: 10px; text-decoration:none; }
 a.gear_nav     { font-weight:700; text-transform:lowercase; text-decoration:none; }
 a.gear_content { font-weight:300 }
 a.admin_link   { font-size: 10px; font-weight:300; text-decoration:none; }

 .smaller {font-size:10px; }
 .small   {font-size:12px; }
 .medium  {font-size:13px; }
 .large   {font-size:15px; }
 .larger  {font-size:17px; }

 .smaller_bold   {font-size:10px; font-weight:700 }
 .small_bold     {font-size:12px; font-weight:700 }
 .medium_bold    {font-size:13px; font-weight:700 }
 .large_bold     {font-size:15px; font-weight:700 }
 .larger_bold    {font-size:17px; font-weight:700 }
 .humungous_bold { font-size:22px;font-weight:700}

 .breadcrumb_link {font-size:10px; color : 0000cc}
 .small_link {font-size:12px; color : 0000cc}


 .info   {font-size:10px; color : 000000}
 .error  {font-size:10px; color : cc0000}

 .helpertext {font-size:10px; color : 333333}
 .adminbody  {font-size:10px; color : 333333}
 .subheader  {font-size:10px; color : 333333; font-weight:700}
 .pageheader {font-size:12px; color : FFFFFF; font-weight:700}
 .pageheader_edit {font-size:12px; color : #4D4E4F; font-weight:700}

 -->
 </STYLE>
</head>

<BODY bgColor="#ffffff">
<br>

<c:set var="config_page" value="${param['config_page']}"/>

<c:choose>

 <c:when test="${config_page eq 'Producer1'}">
    <jsp:include page="configProducer1.jsp"/>
  </c:when>

 <c:when test="${config_page eq 'Producer2'}">
    <jsp:include page="configProducer2.jsp"/>
  </c:when>

 <c:when test="${config_page eq 'Producer3'}">

  <c:choose>        <%--show registration page only if registration is required --%>
    <c:when test="${ProducerFormHandler.registrationRequired == true}" >
        <jsp:include page="configProducer3.jsp"/>
    </c:when>
    <c:otherwise>
        <jsp:include page="configProducer4.jsp"/>
    </c:otherwise>
  </c:choose>

  </c:when>

 <c:when test="${config_page eq 'Producer4'}">
    <jsp:include page="configProducer4.jsp"/>
  </c:when>

 <c:when test="${config_page eq 'Delete'}">
    <jsp:include page="deleteProducer.jsp"/>
  </c:when>

  <c:otherwise>
    <jsp:include page="configProducer1.jsp"/>
  </c:otherwise>

</c:choose>

</BODY></HTML>

</paf:hasRole>
<%--</paf:PortalAdministratorCheck>--%>
<%--</paf:InitializeGearEnvironment>--%>

</dspel:page>
<%-- @version $Id: //product/WSRP/version/10.0.3/consumer/proxyPortlet/proxyPortlet/InstallConfig.jsp#2 $$Change: 651448 $--%>
