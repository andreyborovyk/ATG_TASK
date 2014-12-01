<dsp:page>

  <%--
      This gadget renders a field to allow an anonymous user to enter an email address for sending
      of the order-confirmation email

      Form Condition:
      - This gadget must be contained inside of a form.
      - CommitOrderFormHandler must be invoked from a submit button in this form for fields in this
        page to be processed
  --%>

  <dsp:importbean bean="/atg/store/order/purchase/CommitOrderFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <dsp:getvalueof var="transient" vartype="java.lang.String" bean="Profile.transient"/>

  <c:if test='${transient == "true"}'>
    <div id="atg_store_confirmEmail">

          <dsp:input bean="CommitOrderFormHandler.confirmEmailAddress" value=""
                       name="email" type="text" id="atg_store_confirmEmailInput"/>
   
          <label for="atg_store_confirmEmailInput">
            <fmt:message key="checkout_confirmEmail.provideEmail"/>
          </label>
    </div>
  </c:if>

</dsp:page>

<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/checkout/gadgets/confirmEmail.jsp#3 $$Change: 635816 $--%>