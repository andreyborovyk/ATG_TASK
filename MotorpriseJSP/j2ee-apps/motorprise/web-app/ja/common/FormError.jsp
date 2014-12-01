<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CostCenterFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CreateInvoiceRequestFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserAddFormHandler"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/CreateOrganizationFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/purchase/SaveOrderFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CancelOrderFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/scheduled/ScheduledOrderFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>

<%/* Check for errors */%>
<dsp:droplet name="Switch">
  <dsp:param bean="PaymentGroupFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="PaymentGroupFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="propertyName"/>
          <dsp:oparam name="RequisitionNumber"><%/* This is so we don't display the same error twice */%></dsp:oparam>
          <dsp:oparam name="PONumber">
            発注番号または請求番号を入力して、続行してください。
          </dsp:oparam>
          <dsp:oparam name="default">
            <LI> <dsp:valueof param="message"/>
          </dsp:oparam>
        </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>

<%/* Check for errors */%>
<dsp:droplet name="Switch">
  <dsp:param bean="ShippingGroupFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="ShippingGroupFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>

<%/* Check for errors */%>
<dsp:droplet name="Switch">
  <dsp:param bean="CostCenterFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="CostCenterFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>


<%/* Check for errors */%> 
<dsp:droplet name="Switch">
  <dsp:param bean="CreateOrganizationFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="CreateOrganizationFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>

<%/* Check for errors */%>
<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>


<%/* Check for errors */%>
<dsp:droplet name="Switch">
  <dsp:param bean="MultiUserUpdateFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="MultiUserUpdateFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>


<%/* Check for errors in MultiUserAddFormHandler.
         There are three levels of errors: 
         (1) all exceptions - these are accessed via formExceptions
         
         (2) common exceptions - subset of (1) containing only those
          exceptions which are common to all users; these are accessed
          via commonFormExceptions
         (3) per-user exceptions - subset of (1) containing only those
          exceptions applicable to this user; these are accessed via
          the users[index].formExceptions
 
      */
%>
<dsp:droplet name="/atg/dynamo/droplet/ForEach">
  <dsp:param bean="MultiUserAddFormHandler.users" name="array"/>
  <dsp:oparam name="output">
    <dsp:droplet name="Switch">
      <dsp:param bean="MultiUserAddFormHandler.users[param:index].formError" name="value"/>
      <dsp:oparam name="true">

        <p>
	<%-- Render a label indicating which user each set of errors applies to, but 
	     only if we were creating more than one user at the same time.  Otherwise
	     the label is at best redundant, and at worse confusing --%>
	<dsp:droplet name="Switch">
	  <dsp:param name="value" bean="MultiUserAddFormHandler.count"/>
	  <dsp:oparam name="1"></dsp:oparam>
	  <dsp:oparam name="default">
	    ユーザ <dsp:valueof param="count"/> 
	    (<dsp:valueof bean="MultiUserAddFormHandler.users[param:index].value.lastName"/> 
	    <dsp:valueof bean="MultiUserAddFormHandler.users[param:index].value.firstName"/>):
	  </dsp:oparam>
	</dsp:droplet>
        <font color=cc0000><strong><ul>
          <dsp:droplet name="ProfileErrorMessageForEach">
            <dsp:param bean="MultiUserAddFormHandler.users[param:index].formExceptions" name="exceptions"/>
            <dsp:oparam name="output">
              <li> <dsp:valueof param="message"/>
            </dsp:oparam>
          </dsp:droplet>
        </ul></strong></font>
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

<dsp:droplet name="Switch">
  <dsp:param bean="MultiUserAddFormHandler.commonFormError" name="value"/>
  <dsp:oparam name="true">
    <p>
    <%-- Render a label indicating that the next set of errors isn't related 
         to any specific user, but only if we were creating more than one user 
	 at the same time.  --%>
    <dsp:droplet name="Switch">
      <dsp:param name="value" bean="MultiUserAddFormHandler.count"/>
      <dsp:oparam name="1"></dsp:oparam>
      <dsp:oparam name="default">一般エラー：</dsp:oparam>
    </dsp:droplet>
    
    <font color=cc0000><strong><ul>
      <dsp:droplet name="ProfileErrorMessageForEach">
        <dsp:param bean="MultiUserAddFormHandler.commonFormExceptions" name="exceptions"/>
        <dsp:oparam name="output">
          <li> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </ul></strong></font>
  </dsp:oparam>
</dsp:droplet>


<%/* Check for errors */%>
<dsp:droplet name="Switch">
  <dsp:param bean="SaveOrderFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="SaveOrderFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>

<%/* Check for errors */%> 
<dsp:droplet name="Switch">
  <dsp:param bean="CancelOrderFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="CancelOrderFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>

<%/* Check for errors */%>
<dsp:droplet name="Switch">
  <dsp:param bean="CommitOrderFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="CommitOrderFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>

<%/* Check for errors */%>
<dsp:droplet name="Switch">
  <dsp:param bean="CartModifierFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="CartModifierFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>

<%/* Check for errors */%>
<dsp:droplet name="Switch">
  <dsp:param bean="CreateInvoiceRequestFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="CreateInvoiceRequestFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>

<dsp:droplet name="Switch">
  <dsp:param bean="B2BRepositoryFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="B2BRepositoryFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>

<%/* Check for errors */%>
<dsp:droplet name="Switch">
  <dsp:param bean="ScheduledOrderFormHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="ScheduledOrderFormHandler.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/common/FormError.jsp#2 $$Change: 651448 $--%>
