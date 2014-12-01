<%--
  Task outcome confirmation dialog for asset manager UI.
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/taskOutcomeConfirm.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="sessionInfo"
                    bean="/atg/web/assetmanager/SessionInfo"/>
  <dspel:importbean var="profile"
                    bean="/atg/userprofiling/Profile"/>
  <dspel:importbean var="formHandler"
                    bean="/atg/epub/servlet/FireWorkflowOutcomeFormHandler"/>
  
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  
  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title>
        <fmt:message key="common.pageTitle"/>
      </title>
      
      <dspel:include page="components/head.jsp"/>

      <script type="text/javascript">
        function setTextareaFocus() {
          if (document.getElementById("noteText") && parent.document.getElementById("confirm").style.display == "block") {
            document.getElementById("noteText").focus();
          }
        }

        function doAction() {
          document.body.style.cursor = "wait";
          var elem = document.getElementById("okButton");
          elem.className = "button disabled";
          elem.onclick = function() { return false; };
          elem = document.getElementById("cancelButton");
          elem.className = "button disabled";
          elem.onclick = function() { return false; };
          document.actionFormName.submit();
        }
        
        function doCancel() {
          parent.showIframe("confirm");
          var actionSelect = parent.document.getElementById("formTaskOutcome");
          actionSelect.selectedIndex = 0;
        }
      </script>

    </head>

    <body id="assetBrowser" onload="setTextareaFocus()">

      <%-- If not logged in (session expired) close window and refresh parent --%>
      <c:if test="${profile.transient}">
        <script type="text/javascript">
          parent.showIframe("confirm");
          parent.location.reload();
        </script>
      </c:if>

      <c:set var="showCustomForm" value="${not empty param.outcomeFormURI}"/>

      <h1>
        <fmt:message key="taskOutcomeConfirm.header"/>
      </h1>

      <c:set var="formErrors" value="false"/>
      <c:if test="${not empty formHandler.formExceptions}">
        <c:set var="formErrors" value="true"/>
      </c:if>

      <c:choose>
        <c:when test="${not empty param.formSubmitted and not formErrors}">
          <pws:getCurrentContext var="context"/>
          <c:choose>
            <c:when test="${not empty context and not empty context.process}">
              <biz:getProcessURL var="processInfo" process="${context.process}" projectView="5"/>
              <c:set var="destinationURL" value="${processInfo.URL}"/>
            </c:when>
            <c:otherwise>
              <c:set var="destinationURL" value="${config.bccCommunityRoot}"/>
            </c:otherwise>
          </c:choose>
          <script type="text/javascript">
            parent.showIframe("confirm");
            parent.location = "<c:out value='${destinationURL}'/>";
            //parent.location.reload();
          </script>
        </c:when> 
        <c:when test="${not empty param.formSubmitted and formErrors}">

          <div id="confirmContentBody">
            <div id="confirmScrollContainer">
              <p class="confirmMessage">
                <c:forEach var="outcomeException" items="${formHandler.formExceptions}">
                  <c:set var="exMessage" value="${outcomeException.message}"/>
                  <c:if test="${exMessage eq null}">
                    <c:set var="exMessage" value="${outcomeException}"/>
                  </c:if>
                  <p><span class="error"><c:out value="${exMessage}"/></span></p>
                </c:forEach>
                <br/>
              </p>
            </div>
          </div>
          <div id="assetBrowserFooterRight">
            <a href="javascript:doCancel()" class="button" title="<fmt:message key='common.ok.title'/>">
              <span>
                <fmt:message key="common.ok"/>
              </span>
            </a>
          </div>
          <div id="assetBrowserFooterLeft">
          </div>
        </c:when> 
        <c:otherwise>

          <dspel:form name="actionFormName" formid="actionFormName" action="taskOutcomeConfirm.jsp" method="post">
            <input type="hidden" name="formSubmitted" value="true"/>
            <dspel:input type="hidden" bean="FireWorkflowOutcomeFormHandler.processId" value="${param.processId}"/>
            <c:if test="${param.projectId != null}">
              <dspel:input type="hidden" bean="FireWorkflowOutcomeFormHandler.projectId" value="${param.projectId}"/>
            </c:if>
            <dspel:input type="hidden" bean="FireWorkflowOutcomeFormHandler.taskElementId" 
                         value="${param.taskId}"/>
            <dspel:input type="hidden" bean="FireWorkflowOutcomeFormHandler.outcomeElementId" 
                         value="${param.outcomeId}"/>
            <dspel:input type="hidden" priority="-1" bean="FireWorkflowOutcomeFormHandler.fireWorkflowOutcome" 
                         value="1"/>

            <div id="confirmContentBody">
              <div id="confirmScrollContainer">
                <p class="confirmMessage">
                  <c:if test="${not formErrors}">
                    <c:choose>
                      <c:when test="${showCustomForm}">
                        <dspel:include page="${param.outcomeFormURI}">
                          <dspel:param name="subjectId" value="${param.projectId}"/>
                        </dspel:include>
                      </c:when>
                      <c:otherwise>
                        <web-ui:decodeParameterValue var="outcomeName" value="${param.outcomeName}"/>
                        <fmt:message key="taskOutcomeConfirm.areYouSure">
                          <fmt:param value="${outcomeName}"/>
                        </fmt:message>
                      </c:otherwise>
                    </c:choose>
                    <br/>
                  </c:if>
                </p>
                <p>
                  <label for="noteText">
                    <fmt:message key="taskOutcomeConfirm.enterNote"/>:<br />
                  </label>
                  <dspel:textarea id="noteText"
                                  iclass="taskOutcomeNote"
                                  bean="FireWorkflowOutcomeFormHandler.actionNote"/>
                </p>
              </div>
            </div>

            <div id="assetBrowserFooterRight">
              <a id="okButton" href="javascript:doAction()" class="button" title="<fmt:message key='common.ok.title'/>">
                <span>
                  <fmt:message key="common.ok"/>
                </span>
              </a>
              <a id="cancelButton" href="javascript:doCancel()" class="button abTrigger" title="<fmt:message key='common.cancel.title'/>">
                <span>
                  <fmt:message key="common.cancel"/>
                </span>
              </a>
            </div>
            <div id="assetBrowserFooterLeft">
            </div>

          </dspel:form>
        </c:otherwise>
      </c:choose>

    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/taskOutcomeConfirm.jsp#2 $$Change: 651448 $--%>
