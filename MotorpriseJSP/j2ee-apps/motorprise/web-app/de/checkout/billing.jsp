<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/projects/b2bstore/order/AuthorizedPaymentTypesDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Checkout"/></dsp:include>

<%--
This renders one of several pages based on which PaymentGroup types authorized for this current
user. The three possibilities are:
1) billing_invoice.jsp
3) billinc_cc.jsp
2) billing_invoice_cc.jsp

These pages expect an init request parameter. If one exists here, its value is passed along as a droplet
parameter. Otherwise init is passed with the value true.
--%>

<dsp:droplet name="Switch">
  <dsp:param name="value" param="init"/>
  <dsp:oparam name="unset">
    <dsp:droplet name="AuthorizedPaymentTypesDroplet">
      <dsp:param bean="Profile" name="profile"/>
      <dsp:oparam name="output">
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="potentialPaymentTypes.creditCard"/>
          <dsp:oparam name="true">
            <dsp:droplet name="Switch">
              <dsp:param name="value" param="potentialPaymentTypes.invoiceRequest"/>
              <dsp:oparam name="true">
                <dsp:droplet name="IsEmpty">
                  <dsp:param bean="Profile.paymentTypes" name="value"/>
                  <dsp:oparam name="true">
                    <dsp:include page="billing_invoice.jsp"><dsp:param name="init" value="true"/></dsp:include>
                  </dsp:oparam>
                  <dsp:oparam name="false">
                    <dsp:include page="billing_invoice_cc.jsp"><dsp:param name="init" value="true"/></dsp:include>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
              <dsp:oparam name="false">
                <dsp:droplet name="IsEmpty">
                  <dsp:param bean="Profile.paymentTypes" name="value"/>
                  <dsp:oparam name="true">
                    <dsp:include page="no_billing.jsp"></dsp:include>
                  </dsp:oparam>
                  <dsp:oparam name="false">
                    <dsp:include page="billing_cc.jsp"><dsp:param name="init" value="true"/></dsp:include>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
          <dsp:oparam name="false">
            <dsp:droplet name="Switch">
              <dsp:param name="value" param="potentialPaymentTypes.invoiceRequest"/>
              <dsp:oparam name="true">
                <dsp:include page="billing_invoice.jsp"><dsp:param name="init" value="true"/></dsp:include>
              </dsp:oparam>
              <dsp:oparam name="false">
                <dsp:include page="no_billing.jsp"></dsp:include>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="default">
    <dsp:droplet name="AuthorizedPaymentTypesDroplet">
      <dsp:param bean="Profile" name="profile"/>
      <dsp:oparam name="output">
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="potentialPaymentTypes.creditCard"/>
          <dsp:oparam name="true">
            <dsp:droplet name="Switch">
              <dsp:param name="value" param="potentialPaymentTypes.invoiceRequest"/>
              <dsp:oparam name="true">
                <dsp:droplet name="IsEmpty">
                  <dsp:param bean="Profile.paymentTypes" name="value"/>
                  <dsp:oparam name="true">
                    <dsp:getvalueof id="pval0" param="init"><dsp:include page="billing_invoice.jsp"><dsp:param name="init" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                  </dsp:oparam>
                  <dsp:oparam name="false">
                    <dsp:getvalueof id="pval0" param="init"><dsp:include page="billing_invoice_cc.jsp"><dsp:param name="init" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
              <dsp:oparam name="false">
                <dsp:droplet name="IsEmpty">
                  <dsp:param bean="Profile.paymentTypes" name="value"/>
                  <dsp:oparam name="true">
                    <dsp:include page="no_billing.jsp"></dsp:include>
                  </dsp:oparam>
                  <dsp:oparam name="false">
                    <dsp:getvalueof id="pval0" param="init"><dsp:include page="billing_cc.jsp"><dsp:param name="init" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
          <dsp:oparam name="false">
            <dsp:droplet name="Switch">
              <dsp:param name="value" param="potentialPaymentTypes.invoiceRequest"/>
              <dsp:oparam name="true">
                <dsp:getvalueof id="pval0" param="init"><dsp:include page="billing_invoice.jsp"><dsp:param name="init" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
              </dsp:oparam>
              <dsp:oparam name="false">
                <dsp:include page="no_billing.jsp"></dsp:include>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>


</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/billing.jsp#2 $$Change: 651448 $--%>
