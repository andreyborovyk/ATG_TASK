<%-- Renders the "Please Notify Me" dialog when javascript is off --%>

<dsp:page>
  <dsp:getvalueof var="redirectURL" param="redirectURL"/>
  <dsp:getvalueof var="skuId" param="skuId"/>
  <dsp:getvalueof var="productId" param="productId"/>

	<div id="atg_store_noJsNotifyMeRequest">
    <crs:messageWithDefault key="browse_notifyMeRequestPopup.title"/>
    <c:if test="${!empty messageText}">
      <h2 class="title">
        ${messageText}
      </h2>
    </c:if>
    <crs:messageWithDefault key="browse_notifyMeRequestPopup.intro"/>
    <c:if test="${!empty messageText}">
      <p>
        ${messageText}
      </p>
    </c:if>
    
    <%-- Email form --%>
    <dsp:include page="/browse/gadgets/notifyMeRequest.jsp">
      <dsp:param name="redirectURL" param="redirectURL"/>
      <dsp:param name="skuId" param="skuId"/>
      <dsp:param name="productId" param="productId"/>
    </dsp:include>
  </div>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/browse/noJavascriptNotifyMeRequest.jsp#3 $$Change: 635816 $--%>
