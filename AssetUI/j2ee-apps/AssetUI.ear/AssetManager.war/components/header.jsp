<%--
  Page fragment containing header for asset manager UI.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/header.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="profile"
                    bean="/atg/userprofiling/Profile"/>

  <dspel:getvalueof var="projectContext" param="projectContextParam"/>
  <dspel:getvalueof var="project" param="projectParam"/>
  <dspel:getvalueof var="task" param="taskParam"/>
  <dspel:getvalueof var="activity" param="activityParam"/>
  
    
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <script type="text/javascript" >

  function goHome() {

    if (actionsAreLocked()) return;

    atg.assetmanager.saveconfirm.saveBeforeLeaveParentFrame("<c:out value='${config.bccCommunityRoot}'/>");
  }

  function showUtilities() {
    var divRef = dojo.byId('utilDD');
    
    if (divRef.className == "show") {
      divRef.className = "hide";
      utilMenuIframeBlocker.destroy();
    }
    else {
      divRef.className = "show";
      utilMenuIframeBlocker = new dijit.BackgroundIframe(divRef);
    }
    
  }

  function popup(url, name, width, height) {
    atg_popupSettings="toolbar=no,location=no,directories=no,"+
             "status=no,menubar=no,scrollbars=yes,"+
             "resizable=no,width="+width+",height="+height;
    atg_myNewWindow=window.open(url,name,atg_popupSettings);
  }

  </script>

  <%-- the Utilities menu --%>

  <%-- set the reporting URL --%>
  <dspel:droplet name="/atg/dynamo/droplet/ComponentExists">
    <dspel:param name="path" value="/atg/cognos/Configuration"/>
    <dspel:oparam name="true">
      <c:set var="reportingUnavailable" value="${false}"/>
      <c:set var="reportingURL"><dspel:valueof bean="/atg/cognos/Configuration.gatewayURI"/></c:set>
      <c:if test="${empty reportingURL}">
        <!-- Reporting available but a valid URL wasnt found -->
        <c:set var="reportingUnavailable" value="${true}"/>
      </c:if>
    </dspel:oparam>
    <dspel:oparam name="false">
      <!-- Reporting not available -->
      <c:set var="reportingUnavailable" value="${true}"/>
    </dspel:oparam>
  </dspel:droplet>

  <%-- set the documentation URL from the utilities menu configuration --%>
  <c:catch var="docsUnavailable">
    <c:set var="docsURL" value="${requestScope.managerConfig.configuration.headerUtilitiesMenu.docsURL}"/>
  </c:catch>
  <%-- not found in the configuration use the default value for documentation URL --%>
  <c:if test="${not empty docsUnavailable or empty docsURL}">
    <c:catch var="docsUnavailable_bcc">
      <c:set var="docsURL">
        <dspel:valueof bean="/atg/biz/Configuration.headerUtilitiesMenu.docsURL"/>
      </c:set>
    </c:catch>
    <c:if test="${not empty docsUnavailable_bcc or empty docsURL}">
      <c:set var="docsURL" value="http://www.atg.com/repositories/ContentCatalogRepository_en/manuals/ATG9.1"/>
    </c:if>
  </c:if>

  <%-- set the about URL from the utilities menu configuration --%>
  <c:catch var="aboutUnavailable">
    <c:set var="aboutURL" value="${requestScope.managerConfig.configuration.headerUtilitiesMenu.aboutURL}"/>
  </c:catch>
  <%-- not found in the configuration use the default value for about URL --%>
  <c:if test="${not empty aboutUnavailable or empty aboutURL}">
    <%--  the default BCC about URL --%>
    <c:catch var="aboutUnavailable_bcc">
      <c:set var="aboutURL">
        <dspel:valueof bean="/atg/bizui/Configuration.headerUtilitiesMenu.aboutURL"/>
      </c:set>
    </c:catch>
    <c:if test="${not empty aboutUnavailable_bcc or empty aboutURL}">
      <c:set var="bccContextRoot">
        <dspel:valueof bean='/atg/bizui/Configuration.contextRoot'/>
      </c:set>
      <c:url var="aboutURL" context="${bccContextRoot}" value="/about.jsp"/>
    </c:if>
  </c:if>

  <!-- the utilities menu's div element -->
  <div id="utilDD" class="hide">
    <ul>
      <%-- Display the reporting menu item --%>
      <c:if test="${not reportingUnavailable}">
      <li class="reporting">
        <a href="<c:out value='${reportingURL}' escapeXml='${false}'/>"
           target="_blank"
           onclick="showUtilities();">
          <fmt:message key="utility.navigation.reports.label"/>
        </a>
      </li>
      </c:if>
      <%-- Display the documentation menu item --%>
      <li class="docs">
        <a href="<c:out value='${docsURL}' escapeXml='${false}'/>"
           target="_blank"
           onclick="showUtilities();">
          <fmt:message key="utility.navigation.docs.label"/>
        </a>
      </li>
      <%-- Display the BCC about menu item --%>
      <li class="about">
        <a href="javascript:showUtilities();popup('<c:out value="${aboutURL}" escapeXml="${false}"/>', 'AboutWindow', 400, 440)">
          <fmt:message key='utility.navigation.about.label'/>
        </a>
      </li>
    </ul>
  </div>

  <asset-ui:getControlCenterState var="state"
                                  service="/atg/remote/controlcenter/service/ControlCenterService"/>
  <web-ui:collectionPropertySize var="applicationsNumber"
                                 collection="${state.applications}" />
                                  
  <div id="globalHeader">
    <div id="logoHeader">
      <div id="logoHeaderRight">
        <c:choose>
          <c:when test="${not profile.transient}">
            <dspel:tomap var="profileItem" bean="Profile"/>

            <c:set var="firstName" value="${profileItem.firstName}"/>
            <c:set var="lastName"  value="${profileItem.lastName}"/>
            <c:if test="${empty firstName or empty lastName}">
              <c:set var="firstName" value="${profileItem.login}"/>
              <c:set var="lastName"  value=""/>
            </c:if>

            <fmt:message key="header.welcome">
              <fmt:param value="${firstName}"/>
              <fmt:param value="${lastName}"/>
            </fmt:message>

            <dspel:a href="javascript:showUtilities()" iclass="utils">
              <fmt:message key="header.utilities"/>
            </dspel:a>
            
            <c:if test="${applicationsNumber < 2}">
              <dspel:a href="javascript:goHome()" iclass="home">
                <fmt:message key="header.home"/>
              </dspel:a>
            </c:if>
            
            <c:url var="logoutPage" value="/logout.jsp">
            </c:url>

            <dspel:a href="javascript:atg.assetmanager.saveconfirm.saveBeforeLeaveParentFrame(null, showLogoutConfirmDialog, 'showLogoutConfirmDialog')" iclass="logout">
              <fmt:message key="header.logout"/>
            </dspel:a>

          </c:when>
          <c:otherwise>

            <dspel:a href="${config.bccCommunityRoot}">
              <fmt:message key="header.login"/>
            </dspel:a>

          </c:otherwise>
        </c:choose>
      </div>

      <div id="logoHeaderLeft">
        <c:choose>
          <c:when test="${applicationsNumber < 2}">
            <c:if test="${not empty requestScope.managerConfig.configuration and not empty requestScope.managerConfig.configuration.headerResource}">
              <fmt:bundle basename="${requestScope.managerConfig.configuration.resourceBundle}">
                <span class="headerString">
                  <fmt:message key="${requestScope.managerConfig.configuration.headerResource}"/>
                </span>
              </fmt:bundle>
            </c:if>
          </c:when>
          
          <c:otherwise>  
            <dspel:include page="applicationSelector.jsp">
              <dspel:param name="stateParam" value="${state}"/>
              <dspel:param name="taskParam"  value="${task}"/>
            </dspel:include>
          </c:otherwise>
        </c:choose>
               
        <%-- project and task --%>                                        
        <c:choose>
          <c:when test="${not empty project}">
            <c:choose>
              <c:when test="${not empty activity}">
                <c:set var="projectType" value="${activity.displayName}"/>
              </c:when>
              <c:otherwise>
                <fmt:message var="projectType" key="common.unknownProjectType"/>
              </c:otherwise>
            </c:choose>
            <span class="currentProject">
              <fmt:message key="assetManager.projectName"/>
              <%-- TBD: Implement project metadata display --%>
              <%--
              <a href="#">
                <c:out value="${project.displayName}"/>
              </a>
              --%>
              <span title="<c:out value='${projectType}'/>">
                <c:out value="${project.displayName}"/>
              </span>
            </span>
            <span class="currentTask">
              <fmt:message key="assetManager.taskName"/>
              <c:choose>
                <c:when test="${not empty task}">
                  <%-- Display the task outcome selector --%>
                  <dspel:include page="../taskOutcomes.jsp">
                    <dspel:param name="projectContext" value="${projectContext}"/>
                    <dspel:param name="task"           value="${task}"/>
                  </dspel:include>
                </c:when>
                <c:otherwise>
                  <fmt:message key="common.noTask"/>
                </c:otherwise>
              </c:choose>
            </span>
          </c:when>
          <c:otherwise>
            <span class="currentProject">
              <c:choose>
                <c:when test="${not empty activity}">
                    <fmt:message key="assetManager.activityName"/>
                    <c:out value="${activity.displayName}"/>
                    <c:if test="${not empty activity.displayName2}">
                      (<c:out value="${activity.displayName2}"/>)
                    </c:if>
                </c:when>

                <c:otherwise>
                  <fmt:message key="common.noActivity"/>
                </c:otherwise>
              </c:choose>
            </span>
          </c:otherwise>
        </c:choose>
      
      </div>
    </div>
  </div>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/header.jsp#2 $$Change: 651448 $ --%>
