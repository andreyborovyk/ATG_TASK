<dsp:page>

  <%--
    Container page for Email A friend template
    Parameters - 
    - senderName - Name of the sender of the Email
    - recipientEmail - Name of the recipient of the Email
    - message - Message to be embedded as part of email
    - productUrl - Landing page URL for the product
    - product - Repository item for the product being mailed
    - isProductUrlEmpty - Boolean indicates whether template is set
  --%>
<div style="width:100%">
  <dsp:include page="gadgets/emailAFriendMessage.jsp">
    <dsp:param name="recipientEmail" param="recipientEmail"/>
    <dsp:param name="senderName" param="senderName"/>
    <dsp:param name="message" param="message"/>
    <dsp:param name="product" param="product"/>
  </dsp:include>

  <dsp:include page="gadgets/emailAFriendProductDetails.jsp">
    <dsp:param name="productUrl" param="productUrl"/>
    <dsp:param name="product" param="product"/>
    <dsp:param name="isProductUrlEmpty" param="isProductUrlEmpty"/>
  </dsp:include>
</div>
</dsp:page>

<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/emailtemplates/emailAFriendContentsContainer.jsp#3 $$Change: 635816 $--%>
