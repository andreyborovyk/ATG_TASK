<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="caf"   uri="http://www.atg.com/taglibs/caf" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:choose>
  <c:when test="${model.localeCountForLanguage gt 0}">
    <c:set var="title" value="Locales for language ${model.language}"/>
  </c:when>
  <c:otherwise>
    <c:set var="title" value="No locales for language ${model.language}"/>
  </c:otherwise>
</c:choose>

<c:choose>
  <c:when test="${header.Accept == 'text/xml'}">
    <localesForLanguage language="<c:out value="${model.language}"/>"
                        title="<c:out value="${title}"/>">
      <localeHeading>
        <column name="Description"/>
        <column name="Abbreviation"/>
        <column name="Country"/>
      </localeHeading>
      <c:forEach var="locale" 
                 items="${model.localesForLanguage}">
        <locale displayName="<c:out value="${locale.displayName}"/>"
                language="<c:out value="${locale.language}"/>"
                displayCountry="<c:out value="${locale.displayCountry}"/>"
                country="<c:out value="${locale.country}"/>"/>
      </c:forEach>
    </localesForLanguage>
  </c:when>
  <c:otherwise>
    <%-- TODO: Opera support --%>
  </c:otherwise>
</c:choose>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/test_renderer_xml.jsp#2 $$Change: 651448 $--%>
