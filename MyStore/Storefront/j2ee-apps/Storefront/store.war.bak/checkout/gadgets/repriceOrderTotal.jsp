<%-- This gadget reprices the order if there was an error in confirmation --%>

<dsp:page>

  <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>

  <dsp:getvalueof var="formHandlerComponent" param="formhandlerComponent" />
  <dsp:getvalueof var="formError" vartype="java.lang.String" bean="${formHandlerComponent}.formError"/>

  <c:if test='${formError == "true"}'>
    <%-- Need to reprice in case form error caused rollback and prices went away --%>
    <dsp:droplet name="RepriceOrderDroplet">
      <dsp:param name="pricingOp" value="ORDER_TOTAL"/>
    </dsp:droplet>
  </c:if>


</dsp:page>

<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/checkout/gadgets/repriceOrderTotal.jsp#3 $$Change: 635816 $--%>