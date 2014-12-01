<%--
  Logout page for asset manager UI.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/logout.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="profileFormHandler"
                    bean="/atg/userprofiling/InternalProfileFormHandler"/>
  <dspel:importbean var="profileErrorMessageForEach"
                    bean="/atg/userprofiling/ProfileErrorMessageForEach"/>

  <c:url var="thisURL" value="/logout.jsp"/>
  <c:url var="successURL" value="/logout.jsp"/>

  <c:if test="${param.DPSLogout == 'true'}">
    <script type="text/javascript">
       parent.location.href = '<c:out value="${config.bccCommunityRoot}"/>';
    </script>
  </c:if>


  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <c:set var="multiEditSessionInfo" value="${sessionInfo.multiEditSessionInfo}"/>


  <fmt:setBundle basename="${config.resourceBundle}"/>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title>
        <fmt:message key="common.pageTitle"/>
      </title>
      <dspel:include page="/components/head.jsp"/>
    </head>

    <body id="logoutBody">
      <div id="centered">

        <%-- Display a logout form. --%>
        <dspel:form name="logoutForm" action="${successURL}" method="post">

          <dspel:input type="hidden" bean="InternalProfileFormHandler.logoutSuccessURL"
                       value="${successURL}"/>
          <dspel:input type="hidden" bean="InternalProfileFormHandler.logoutErrorURL"
                       value="${thisURL}"/>

          <%-- Hidden submit button that is clicked using JavaScript
               when the OK button is clicked. --%>
          <dspel:input id="logout" type="submit" style="display:none"
                       bean="InternalProfileFormHandler.logout" value=""/>

          <%-- Display errors, if any, from a previous logout attempt. --%>
          <div class="logoutTop">
            <c:if test="${profileFormHandler.formError}">
              <strong><ul>
                <dspel:droplet name="ProfileErrorMessageForEach">
                  <dspel:param name="exceptions" bean="InternalProfileFormHandler.formExceptions"/>
                  <dspel:oparam name="output">
                    <li>
                      <dspel:valueof param="message"/>
                    </li>
                  </dspel:oparam>
                </dspel:droplet>
              </ul></strong>
            </c:if>
          </div>

          <%-- Confirmation message --%>
          <div class="logoutMiddle">
            <c:if test="${multiEditSessionInfo.projectIdsWaiting ne null}">
              <fmt:message key="logout.multiEditProjectsInProgress"/><br>
              <c:forEach items="${multiEditSessionInfo.projectIdsWaiting}" var="projectWaitingId">
                  <pws:getProcess var="process" projectId="${projectWaitingId}"/><br>
              <c:out value="${process.displayName}"/>
              </c:forEach>
              <br><br>
             </c:if>

            <fmt:message key="logout.confirmation"/>
          </div>

          <div class="logoutBottom">

            <%-- OK button --%>
            <dspel:a iclass="buttonSmall go"
                     href="javascript:document.logoutForm.logout.click()">
              <fmt:message key="common.ok"/>
            </dspel:a>
            &nbsp;

            <%-- Cancel button --%>
            <dspel:a iclass="buttonSmall" href="javascript:parent.hideLogoutConfirmDialog()">
              <fmt:message key="common.cancel"/>
            </dspel:a>
          </div>
        </dspel:form>

      </div>

    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/logout.jsp#2 $$Change: 651448 $ --%>
