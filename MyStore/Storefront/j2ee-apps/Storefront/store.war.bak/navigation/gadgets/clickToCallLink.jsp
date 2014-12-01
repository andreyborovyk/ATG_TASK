<dsp:page>
  <%-- if the click to call feature is disabled, this entire div can be safely be bypassed --%>
  <dsp:importbean var="c2cConfig" bean="/atg/clicktoconnect/Configuration"/>
  <c:if test="${not empty c2cConfig}">
    <%-- add c2c scripts etc to output --%>
    <dsp:droplet name="/atg/adc/droplet/InsertTag">
      <dsp:param name="location" value="body" />
      <dsp:oparam name="output">
        <dsp:valueof param="data" converter="valueishtml" />
      </dsp:oparam>
    </dsp:droplet>
    <%-- and display the base div --%>
    <dsp:getvalueof var="pageName" param="pageName"/>
    <div id="atg_store_c2c_${pageName}">&nbsp;</div>
  </c:if>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/navigation/gadgets/clickToCallLink.jsp#3 $$Change: 635816 $--%>