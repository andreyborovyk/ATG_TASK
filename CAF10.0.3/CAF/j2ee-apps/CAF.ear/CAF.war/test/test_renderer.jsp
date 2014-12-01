<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="caf"   uri="http://www.atg.com/taglibs/caf" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<caf:outputXhtml targetId="divLocalesForLanguage1">
  <h3>
    <c:choose>
      <c:when test="${model.localeCountForLanguage gt 0}">
        Locales for <c:out value="${model.language}"/>
      </c:when>
      <c:otherwise>
        No locales for language <c:out value="${model.language}"/>
      </c:otherwise>
    </c:choose>
  </h3>
  <table border="0" 
       cellpadding="0" 
       cellspacing="0">
    <tr>
      <th align="left">
        Description&nbsp;
      </th>
      <th align="left">
        Abbreviation&nbsp;
      </th>
      <th align="left">
        Country&nbsp;
      </th>
    </tr>
    <c:forEach var="locale" 
               items="${model.localesForLanguage}">
      <tr>
        <td class="cell">
          <c:out value="${locale.displayName}"/>
        </td>
        <td class="cell">
          <c:out value="${locale.language}"/>_<c:out value="${locale.country}"/>
        </td>
        <td class="cell">
          <c:out value="${locale.displayCountry}"/>
        </td>
      </tr>
    </c:forEach>
  </table>
  <br/>
</caf:outputXhtml>

<caf:outputXhtml targetId="divLocalesForLanguage2">
  <h3>
    <c:choose>
      <c:when test="${model.localeCountForLanguage gt 0}">
        Locales for <c:out value="${model.language}"/>
      </c:when>
      <c:otherwise>
        No locales for language <c:out value="${model.language}"/>
      </c:otherwise>
    </c:choose>
  </h3>
  <table border="0" 
       cellpadding="0" 
       cellspacing="0">
    <tr>
      <th align="left">
        Description&nbsp;
      </th>
      <th align="left">
        Abbreviation&nbsp;
      </th>
      <th align="left">
        Country&nbsp;
      </th>
    </tr>
    <c:forEach var="locale" 
               items="${model.localesForLanguage}">
      <tr>
        <td class="cell">
          <c:out value="${locale.displayName}"/>
        </td>
        <td class="cell">
          <c:out value="${locale.language}"/>_<c:out value="${locale.country}"/>
        </td>
        <td class="cell">
          <c:out value="${locale.displayCountry}"/>
        </td>
      </tr>
    </c:forEach>
  </table>
  <br/>
</caf:outputXhtml>

<caf:outputJavaScript>
  var html = "<h3>";
  <c:choose>
  <c:when test="${model.localeCountForLanguage gt 0}">
    html += "Locales for <c:out value="${model.language}"/>";
  </c:when>
  <c:otherwise>
    html += "No locales for language <c:out value="${model.language}"/>";
  </c:otherwise>
  </c:choose>
  html += "</h3>";
  html += "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><th align=\"left\">Description&nbsp;</th><th align=\"left\">Abbreviation&nbsp;</th><th align=\"left\">Country&nbsp;</th></tr>";
  <c:forEach var="locale" 
             items="${model.localesForLanguage}">
  html += "<tr><td class=\"cell\"><c:out value="${locale.displayName}"/></td><td class=\"cell\"><c:out value="${locale.language}"/>_<c:out value="${locale.country}"/></td><td class=\"cell\"><c:out value="${locale.displayCountry}"/></td></tr>";
    </c:forEach>
  html += "</table><br/>";
  document.getElementById("divLocalesForLanguage3").innerHTML = html;
</caf:outputJavaScript>

<caf:outputJavaScript>
  var html = "<h3>";
  <c:choose>
  <c:when test="${model.localeCountForLanguage gt 0}">
    html += "Locales for <c:out value="${model.language}"/>";
  </c:when>
  <c:otherwise>
    html += "No locales for language <c:out value="${model.language}"/>";
  </c:otherwise>
  </c:choose>
  html += "</h3>";
  html += "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><th align=\"left\">Description&nbsp;</th><th align=\"left\">Abbreviation&nbsp;</th><th align=\"left\">Country&nbsp;</th></tr>";
  <c:forEach var="locale" 
             items="${model.localesForLanguage}">
  html += "<tr><td class=\"cell\"><c:out value="${locale.displayName}"/></td><td class=\"cell\"><c:out value="${locale.language}"/>_<c:out value="${locale.country}"/></td><td class=\"cell\"><c:out value="${locale.displayCountry}"/></td></tr>";
    </c:forEach>
  html += "</table><br/>";
  document.getElementById("divLocalesForLanguage4").innerHTML = html;
</caf:outputJavaScript>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/test_renderer.jsp#2 $$Change: 651448 $--%>
