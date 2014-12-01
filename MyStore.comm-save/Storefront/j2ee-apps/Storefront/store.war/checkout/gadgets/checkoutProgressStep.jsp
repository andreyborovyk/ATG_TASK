<dsp:page>
  <dsp:getvalueof var="cssClass" vartype="java.lang.String" param="cssClass"/>
  <dsp:getvalueof var="step" vartype="java.lang.String" param="step"/>
  <dsp:getvalueof var="stepDescription" vartype="java.lang.String" param="stepDescription"/>
  <li class="${cssClass}">
    <c:if test="${not empty step}">
      <span class="atg_store_checkoutStageNumber"><c:out value="${step}"/></span>
    </c:if>
    <span class="atg_store_checkoutStageName"><c:out value="${stepDescription}"/></span>
  </li>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/checkout/gadgets/checkoutProgressStep.jsp#3 $$Change: 635816 $--%>