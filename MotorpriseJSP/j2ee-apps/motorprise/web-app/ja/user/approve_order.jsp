<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/approval/ApprovalFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/profile/PersonLookup"/>

<%--
  Approves the given order using ApprovalFormHandler. Also uses PersonLookup to display
  information about the buyer of the order.
--%>

<DECLAREPARAM NAME="orderId" CLASS="java.lang.String" DESCRIPTION="The id of the order to reject">

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" 承認"/></dsp:include>
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="my_account.jsp">私のアカウント</dsp:a> &gt; 
    <dsp:a href="approvals.jsp">承認待ちのオーダー</dsp:a> &gt; 
    <dsp:a href="order_pending_approval.jsp"><dsp:param name="orderId" param="orderId"/>オーダー</dsp:a> &gt; オーダーの承認
    </span></td>
  </tr>
  
  <tr valign=top> 
    <td width=55>  
    
    </td>
    
    <!-- main content area -->
    <td  valign="top" width=745>  
    <table border=0 cellpadding=4 width=80%>
      <tr>
        <td colspan="2"><img src="../images/d.gif" vspace=0></td>
      </tr>
    <tr valign=top>
        <td colspan=2><span class=big>私のアカウント</span></td>
      </tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;オーダーの承認</td></tr></table>
        </td>
      </tr>
      
      
      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>
      
     <tr>
        <td><dsp:include page="ApprovalFormHandlerErrorMessages.jsp"></dsp:include></td>
        <td>オーダー<dsp:valueof param="orderId">オーダーなし</dsp:valueof><br>

        <%-- Get the order using OrderLookup, get the profile id of the order and display the info.
          regarding the buyer of the order using PersonLookup --%> 
        <dsp:droplet name="OrderLookup">
          <dsp:param name="orderId" param="orderId"/>
          <dsp:oparam name="output">
            <dsp:valueof date="yyyy/M/dd K:mm a" param="result.submittedDate">送信日なし</dsp:valueof>のオーダー<br>
            <dsp:droplet name="PersonLookup">
              <dsp:param name="id" param="result.profileId"/>
              <dsp:param name="elementName" value="Person"/>
              <dsp:oparam name="output">
                購入者<dsp:valueof param="Person.lastName">姓なし</dsp:valueof>
                <dsp:valueof param="Person.firstName">名なし</dsp:valueof>
              </dsp:oparam>
            </dsp:droplet>          
          </dsp:oparam>
        </dsp:droplet>
      </tr>
      <tr>
        <td colspan=2>

        <%-- Set the order id of ApprovalFormHandler to the order to be approved and
          provide submit buttons for approve/cancel. --%>
          <dsp:form action="approvals.jsp" method="post">
            <dsp:input bean="ApprovalFormHandler.orderId" paramvalue="orderId" type="hidden"/>
            <span class=help>メッセージを入力してください。</span><br>
            <dsp:textarea bean="ApprovalFormHandler.approverMessage" rows="7" cols="50"></dsp:textarea><p>
            <%-- page to be redirected if the order approval is successful --%>
            <dsp:input bean="ApprovalFormHandler.approveOrderSuccessURL" type="hidden" value="approve_confirm.jsp"/>
            <%-- page to be redirected if the order approval fails --%>
            <dsp:input bean="ApprovalFormHandler.approveOrderErrorURL" type="hidden" value="approve_order.jsp"/>
            <dsp:input bean="ApprovalFormHandler.approveOrder" type="submit" value="オーダーの承認"/> &nbsp;
            <input type="submit" value="キャンセル"> &nbsp;
            </dsp:form>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/user/approve_order.jsp#2 $$Change: 651448 $--%>
