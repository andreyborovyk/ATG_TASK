<%--
  --  exportStatus.jsp
  --  
  --  This page produces the JSON response to the polling of the export status. 
  --   
  --  The response contains the following properties:
  --     complete : "true" or "false"
  --      message : the error or warning message to display to the user 
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dsp"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="json"  uri="http://www.atg.com/taglibs/json"                  %>
<%@ page contentType="application/json" %>

<dsp:page>
  <c:set var="exportStatusPath" value="/atg/web/assetmanager/transfer/ExportStatus"/>
  <dsp:importbean var="exportStatus"
                  bean="/atg/web/assetmanager/transfer/ExportStatus"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

  <json:object>
    <json:property name="complete">
      <c:out value="${exportStatus.completeFlag}"/>  <%-- true or false --%>
    </json:property>
    <json:property name="completeCount">
      <c:out value="${exportStatus.completeCount}"/>  <%-- number of assets exported so far --%>
    </json:property>
    <json:property name="statusMessage">
      <c:out value="${exportStatus.statusMessage}"/>  <%-- status message --%>
    </json:property>
    <c:if test="${exportStatus.completeFlag}">
      <json:array name="messages" escapeXml="false">
        <dsp:droplet name="ErrorMessageForEach">
          <dsp:param bean="${exportStatusPath}.formExceptions" name="exceptions"/>
          <dsp:oparam name="output">
            <json:property>
              <dsp:valueof valueishtml="true" param="message"/> <%-- one or more error message to be put in a dialog --%>
            </json:property>
          </dsp:oparam>
        </dsp:droplet>
      </json:array>
    </c:if>
  </json:object>

</dsp:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/exportStatus.jsp#2 $$Change: 651448 $--%>
