<%--
  --  previewJSON.jsp
  --  
  --  This page produces the json response to success when the  
  --  quietFormSubmitter.js is used to submit to a form handler
  --  from export.jsp.
  --
  --  The json response contains the following properties: 
  --     publishToTopic : The name of the topic to which this data should
  --             be published so that the UI can respond to it. 
  --     previewDataTable : The fully formatted preview data in table format, 
  --             ready to be plopped directly into the web page. 
  --     errors: An array of localized error messages generated during 
  --             the execution of the action.
  --
  --  This page expects the following parameters : 
  --      formHandlerPath :  The nucleus path to the form handler that forwarded to this page. 
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dsp"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="json"  uri="http://www.atg.com/taglibs/json"                  %>
<%@ page contentType="application/json" pageEncoding="UTF-8" %>

<dsp:page>

  <dsp:getvalueof var="formHandlerPath"     param="formHandlerPath"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

  <dsp:importbean var="formHandler" bean="${formHandlerPath}"/>
  <json:object>
    <json:property name="publishToTopic">
      <c:out value="/atg/assetmanager/exportPreview"/>
    </json:property>

    <json:property name="previewDataTable" escapeXml="false">
      <c:if test="${not empty formHandler.previewData}">
        <table class="data">
          <c:forEach var="previewRow" items="${formHandler.previewData}" varStatus="loopStatus">
            <%-- first row is table header.  make it bold --%>        
            <c:set var="cellStyle" value=""/>
            <c:if test="${loopStatus.index eq 0}">
              <c:set var="cellStyle" value="contentHeaderBold"/>
            </c:if>

            <tr>
              <%-- render a table cell for each previewCell --%>
              <c:forEach var="previewCell" items="${previewRow}">
                <td><div class="<c:out value='${cellStyle}'/>" title="<c:out value='${previewCell.type}'/>"><c:out value="${previewCell.value}"/></div></td>
              </c:forEach>
            </tr>
          </c:forEach>
        </table>
      </c:if>
    </json:property>

    <json:array name="errors">
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="${formHandlerPath}.exportStatus.formExceptions" name="exceptions"/>
          <dsp:oparam name="output">
            <json:property>
              <dsp:valueof valueishtml="true" param="message"/>
            </json:property>
          </dsp:oparam>
      </dsp:droplet>
    </json:array>
  </json:object>
</dsp:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/previewJSON.jsp#2 $$Change: 651448 $--%>
