<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<div id="globalHeader">
  <div id="logoHeader">

    <div id="logoHeaderRight">

      <c:choose>
        <c:when test="${profile.transient == false}">

          <c:set var="firstName"><dsp:valueof bean="Profile.firstName"/></c:set>
          <c:set var="lastName"><dsp:valueof bean="Profile.lastName"/></c:set>
          <fmt:message key="page-message-logged-in" bundle="${bundle}">
            <fmt:param value="${firstName}" />
            <fmt:param value="${lastName}" />
          </fmt:message>

          <span>|</span>

          <a class="home" href='/atg/bcc/home'><fmt:message key="page-label-bcchome" bundle="${bundle}" /></a>

          <span>|</span>

          <a class="logout" href='<c:out value="${logoutURL}"/>'><fmt:message key="page-label-logout" bundle="${bundle}" /></a>

        </c:when>

        <c:otherwise>
          <a href='<c:out value="${loginURL}"/>'><fmt:message key="page-label-login" bundle="${bundle}"/></a><br/>&nbsp;
        </c:otherwise>
      </c:choose>

    </div>

    <a href='/atg/bcc/home'>
      <div id="logoHeaderLeft">
        <h1>Business Control Center</h1>
      </div>
    </a>

  </div>
</div>

</dsp:page>

<%-- @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/includes/branding_banner.jsp#2 $$Change: 651448 $--%>
