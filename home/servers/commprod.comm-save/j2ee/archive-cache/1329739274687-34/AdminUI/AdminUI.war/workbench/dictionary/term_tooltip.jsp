<%--
JSP, used to show dojo tooltip in term dicts tree when mouse over terms. Collect synonyms of current term.
dojo tooltip widget will load this page when need.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_tooltip.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:getvalueof var="termId" param="termId"/>
<dictionary:termFindByPrimaryKey termId="${termId}" var="term"/>
<fmt:message key="termdicts.dojo.term.tooltip" var="tooltipHead"/>
<%--!!!Important!!! Use c:out to unescape !!--%>
<c:set var="tooltip"><c:out value="${tooltipHead} ${term.name}:"/></c:set>
<c:choose>
  <c:when test="${not empty term.synonyms}">
    <c:forEach items="${term.synonyms}" var="synonym">
      <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${synonym.language}"/>
      <c:set var="tooltip">
        ${tooltip}<br/>
        <c:out value="${synonym.name}"/> - ${synonym.relationship},
        ${synonym.speechPart}, ${localizedLanguage}
      </c:set>
    </c:forEach>
  </c:when>
  <c:otherwise>
    <c:set var="tooltip">
      ${tooltip}
      <fmt:message key="termdicts.dojo.term.tooltip.none"/>
    </c:set>
  </c:otherwise>
</c:choose>

<admin-dojo:jsonObject>
  <admin-dojo:jsonValue name="tooltip" value="${tooltip}"/>
</admin-dojo:jsonObject>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_tooltip.jsp#2 $$Change: 651448 $--%>
