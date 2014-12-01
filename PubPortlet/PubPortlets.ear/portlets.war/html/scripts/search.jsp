<%@ page contentType="application/x-javascript" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>

<dsp:page>


<c:choose>
  <c:when test="${!empty param.searchHandlerPath}">
    <dsp:importbean bean="${param.searchHandlerPath}"
      var="searchHandler"/>

<!--

function changeSearchType(formId, searchActionURL) {
  setFormElement(formId, 'handlerAction', <c:out value="${searchHandler.ACTION_CHANGE_TYPE}"/>);
  setFormAction(formId, searchActionURL);
  submitForm(formId);
}

function changeSearchDirectory(formId, searchActionURL) {
  setFormElement(formId, 'handlerAction', <c:out value="${searchHandler.ACTION_CHANGE_CURRENT_DIRECTORY}"/>);
  setFormAction(formId, searchActionURL);
  submitForm(formId);
}

function clickSearchFolder(formId, folderName, searchActionURL) {
  setFormElement(formId, 'clickedFolder', folderName);
  setFormElement(formId, 'handlerAction', <c:out value="${searchHandler.ACTION_CLICK_FOLDER}"/>);
  setFormAction(formId, searchActionURL);
  submitForm(formId);
  return false;
}

function submitSearch(formId) {
  setFormElement(formId, 'handlerAction', <c:out value="${searchHandler.ACTION_SEARCH}"/>);
  submitForm(formId);
}

-->
  </c:when>
</c:choose>

</dsp:page>

<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/scripts/search.jsp#2 $$Change: 651448 $--%>
