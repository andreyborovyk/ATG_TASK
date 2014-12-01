<%--
  Page fragment containing application drop-down selector for the asset manager header.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/applicationSelector.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>

<dspel:page>

  <dspel:getvalueof var="state" param="stateParam"/>
  <dspel:getvalueof var="task" param="taskParam"/>
  
  <script type="text/javascript" >

    var currentApp;
     
    function switchApplication() {
      var switcher = document.getElementById("appSwitcher");
      var applicationURL = switcher.value;
      //set previously selected value - this is for the case when user clicks 'Cancel' in the confirmation dialog  
      switcher.value = currentApp; 
      atg.assetmanager.saveconfirm.saveBeforeLeaveParentFrame(applicationURL);
    }
  
    function rememberApplication() {
      currentApp = document.getElementById("appSwitcher").value;       
    }
  
  </script>

  <select id="appSwitcher" onchange="switchApplication()" onfocus="rememberApplication()">
    <c:forEach var="app" items="${state.applications}">
      <c:choose>
        <c:when test="${app.enabled}">
          <c:choose>
            <c:when test="${not empty app.applicationURL}">
              <c:set var="url" value="${app.applicationURL}"/>
            </c:when>
            <c:otherwise>
              <biz:getTaskURL var="activityInfo" task="${task}"
                                activityId="${app.applicationId}"
                                useTaskActivity="${false}"/>    
              <c:set var="url" value="${activityInfo.URL}"/>
            </c:otherwise>
          </c:choose>
          <c:set var="disabled" value=""/>
        </c:when>
        <c:otherwise>
          <c:set var="disabled" value="disabled"/>
          <c:set var="url" value=""/>
        </c:otherwise>
      </c:choose>
  
                  
      <option <c:out value='${disabled}'/> value="<c:out value='${url}'/>"  
              <c:if test="${app.applicationId == state.selectedApplication.applicationId}">selected</c:if>>
        <c:out value="${app.displayName}"/>
      </option>
    </c:forEach>
  </select>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/applicationSelector.jsp#2 $$Change: 651448 $ --%>