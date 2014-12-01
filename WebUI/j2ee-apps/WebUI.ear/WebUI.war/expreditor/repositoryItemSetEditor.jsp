<%--
  An expression editor for RepositoryItemSetExpression instances.

  @param  expression  The expression to be rendered
  @param  container   The ID of the container for this expression editor
  @param  editorId    A unique identifier for this expression editor

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/repositoryItemSetEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt"               %>
<%@ taglib prefix="ee"     uri="http://www.atg.com/taglibs/expreditor_rt"       %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt"           %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramExpression" param="expression"/>
  <dspel:getvalueof var="paramContainer"  param="container"/>
  <dspel:getvalueof var="paramEditorId"   param="editorId"/>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Get the repository path and item type for this expression --%>
  <c:set var="repositoryPath" value="${paramExpression.itemSetRepository}"/>
  <c:set var="repositoryItemType" value="${paramExpression.itemSetItemType}"/>

  <%-- Get the name of the repository for the given path --%>
  <dspel:importbean var="repository" bean="${repositoryPath}"/>
  <c:set var="repositoryName" value="${repository.repositoryName}"/>

  <%-- Determine if a tree component has been specified --%>
  <c:set var="treePath" value=""/>
  <ee:getConstructAttribute var="treeRegistry"
                            expression="${paramExpression}"
                            attribute="treeRegistry"/>
  <c:if test="${not empty treeRegistry}">
    <web-ui:getTree var="treePath"
                    repository="${repository.absoluteName}"
                    itemType="${repositoryItemType}"
                    treeRegistry="${treeRegistry}"/>
  </c:if>

  <%-- Get the URL that displays the repository item picker --%>
  <ee:getConstructAttribute var="pickerURL"
                            expression="${paramExpression}"
                            attribute="pickerURL"/>

  <%-- Determine the text to be displayed in the link that displays the input
       field --%>
  <c:choose>
    <c:when test="${not empty paramExpression.value}">
      <c:set var="valueText" value="${paramExpression.text}"/>
    </c:when>
    <c:otherwise>
      <fmt:message var="valueText" key="expressionEditor.blankString"/>
    </c:otherwise>
  </c:choose>

  <%-- Derive IDs for page elements --%>
  <c:set var="idSuffix" value="${paramEditorId}${paramExpression.identifier}"/>
  <c:set var="labelId" value="terminalLabel_${idSuffix}"/>

  <%-- Get the name of the JavaScript function to be called when the link is clicked --%>
  <ee:getConstructAttribute var="onclick"
                            expression="${paramExpression}"
                            attribute="onclick"/>

  <%-- Determine if more than one item can be selected --%>
  <ee:getConstructAttribute var="allowMulti"
                            expression="${paramExpression}"
                            attribute="allowMulti"/>
  <c:if test="${allowMulti eq null}">
    <c:set var="allowMulti" value="false"/>
  </c:if>
  
  <ee:getConstructAttribute var="mapMode"
      expression="${paramExpression}"
      attribute="mapMode"/>
  
  <%-- Render the link --%>
  <a id="<c:out value='${labelId}'/>"
     class="itemTerminalLabel terminalLabel expreditorControl"
     style="white-space: normal"
     hidefocus="true"
     href="javascript:<c:out value="${onclick}"/>('<c:out value="${paramContainer}"/>',
                                                  '<c:out value="${paramExpression.identifier}"/>',
                                                  '<c:out value="${paramEditorId}"/>',
                                                  '<c:out value="${repositoryPath}"/>',
                                                  '<c:out value="${repositoryName}"/>',
                                                  '<c:out value="${repositoryItemType}"/>',
                                                  '<c:out value="${treePath}"/>',
                                                  '<c:out value="${pickerURL}" escapeXml="false"/>',
                                                  '<c:out value="${allowMulti}"/>'
                                                  <c:if test="${not empty mapMode}">
                                                    , '<c:out value="${mapMode}"/>'
                                                  </c:if>
                                                  )"><c:out value="${valueText}"/></a>
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/repositoryItemSetEditor.jsp#2 $$Change: 651448 $ --%>
