<%--
  --  toolbarResults.jsp
  --  
  --  This page produces the json response to success when the  
  --  quietFormSubmitter.js is used to submit to a form handler
  --  from toolbar.jsp.
  --
  --  The json response contains the following properties: 
  --     publishToTopic : The name of the topic to which this data should
  --             be published so that the UI can respond to it. 
  --     multiEditAssetCount : The number of assets on the multiedit tab. 
  --             This will be set only on addTo- and removeFromMultiEdit 
  --             actions.  The value will be used to update the tab label.
  --     clearChecks : An indication of whether the checks were cleared 
  --             in the server side model.  This is a hint to the UI that 
  --             it should clear the checks in the view.
  --     refresh : An indication of whether model's data has changed. 
  --             This is a hint to the UI that it should refresh the view.
  --     dataStale : An indication of whether the model's data has changed
  --             in such a way that the right pane data might become stale. 
  --             This is a hint to the UI that it should refresh the right 
  --             pane.
  --     successMessage: A localized message indicating the successful 
  --             completion of the action.
  --     errors: An array of localized error messages generated during 
  --             the execution of the action.
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dsp"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="json"  uri="http://www.atg.com/taglibs/json"                  %>
<%@ page contentType="application/json" %>

<dsp:page>

  <dsp:getvalueof var="multiEditAssetCount" param="multiEditAssetCount"/>
  <dsp:getvalueof var="clearChecks"         param="clearChecks"/>
  <dsp:getvalueof var="formHandlerPath"     param="formHandlerPath"/>
  <dsp:getvalueof var="dataStale"           param="dataStale"/>
  <dsp:getvalueof var="refresh"             param="refresh"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

  <dsp:importbean var="formHandler" bean="${formHandlerPath}"/>
  <json:object>
    <json:property name="publishToTopic">
      <c:out value="/atg/assetmanager/toolbarResults"/>
    </json:property>
    <json:property name="multiEditAssetCount">
      <c:out value="${multiEditAssetCount}"/>
    </json:property>    
    <json:property name="clearChecks">
      <c:out value="${clearChecks}"/>
    </json:property>
    <json:property name="refresh">
      <c:out value="${refresh}"/>
    </json:property>
    <json:property name="dataStale">
      <c:out value="${dataStale}"/>
    </json:property>
    <json:property name="successMessage">
      <c:out value="${formHandler.successMessage}"/>
    </json:property>
    <json:array name="errors">
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="${formHandlerPath}.formExceptions" name="exceptions"/>
          <dsp:oparam name="output">
            <json:property>
              <dsp:valueof valueishtml="true" param="message"/>
            </json:property>
          </dsp:oparam>
      </dsp:droplet>
    </json:array>
  </json:object>
</dsp:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/json/toolbarResults.jsp#2 $$Change: 651448 $--%>
