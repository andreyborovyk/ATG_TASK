<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/userdirectory/droplet/HasFunction"/>
<dsp:importbean bean="/atg/commerce/approval/ApprovalRequiredDroplet"/>
<dsp:importbean bean="/atg/commerce/approval/ApprovedDroplet"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="私のアカウント"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
      私のアカウント</span></td>
  </tr>

  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
    <td valign="top" width=745>  
    <table border=0 cellpadding=4 width=80%>
      <tr><td><img src="../images/d.gif"></td></tr>
      <tr>
        <td colspan=3><span class=big>私のアカウント</span></td>
      </tr>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr>
        <td>
        <table border=0 cellpadding=4 cellspacing=0>
          <tr valign=top>
            <td colspan=3>
            <table width=100% cellpadding=3 cellspacing=0 border=0>
              <tr><td class=box-top>&nbsp;オーダー情報</td></tr></table>
            </td>
          </tr>  
  
          <!--  display link if user has approver role -->
          <dsp:droplet name="HasFunction">
            <dsp:param bean="Profile.id" name="userId"/>
            <dsp:param name="function" value="approver"/>
            <dsp:oparam name="true">
              <tr valign=top>
                <td><dsp:a href="approvals.jsp">承認の要求</dsp:a></td>
                <td>
                <dsp:droplet name="ApprovalRequiredDroplet">
                  <dsp:param bean="Profile.id" name="approverid"/>
                  <dsp:param name="state" value="open"/>
                  <dsp:oparam name="output">
                    <nobr><dsp:valueof param="totalCount"/> &nbsp;</nobr>
                  </dsp:oparam>
                  <dsp:oparam name="empty">
                    0
                  </dsp:oparam>
                </dsp:droplet>
                 </td>
                <td>承認が必要なオーダー。</td>
              </tr>
              <tr valign=top>
                <td><dsp:a href="approvals_past.jsp">解決済み承認要求</dsp:a></td>
                <td>
                <dsp:droplet name="ApprovedDroplet">
                  <dsp:param bean="Profile.id" name="approverid"/>
                  <dsp:param name="state" value="open"/>
                  <dsp:oparam name="output">
                    <nobr><dsp:valueof param="totalCount"/> &nbsp;</nobr>
                  </dsp:oparam>
                  <dsp:oparam name="empty">
                    0
                  </dsp:oparam>
                </dsp:droplet>
                 </td>
                <td>承認または拒否したオーダー。</td>
              </tr>
            </dsp:oparam>
          </dsp:droplet>
            
          <tr valign=top>
            <td><dsp:a href="orders_open.jsp">処理中のオーダー</dsp:a></td>
            <td>
              <dsp:droplet name="OrderLookup">
                <dsp:param bean="Profile.id" name="userId"/>
                <dsp:param name="state" value="open"/>
                <dsp:param name="queryTotalOnly" value="true"/>
                <dsp:oparam name="output">
                  <dsp:valueof param="total_count"/>
                </dsp:oparam>
                <dsp:oparam name="empty">
                  0
                </dsp:oparam>
              </dsp:droplet>
            </td>
            <td>現在処理が進められているオーダー。</td>
          </tr>
          <tr valign=top>
            <td><dsp:a href="orders_filled.jsp"><nobr>実行済みオーダー</dsp:a></nobr></td>
            <td>
              <dsp:droplet name="OrderLookup">
                <dsp:param bean="Profile.id" name="userId"/>
                <dsp:param name="state" value="no_pending_action"/>
                <dsp:param name="queryTotalOnly" value="true"/>
                <dsp:oparam name="output">
                  <dsp:valueof param="total_count"/>
                </dsp:oparam>
                <dsp:oparam name="empty">
                  0
                </dsp:oparam>
               </dsp:droplet>
            </td>
            <td>配達が完了したオーダー。</td>
          </tr>
         
          <!-- only buyers who require approval see rejected orders -->
          <dsp:droplet name="HasFunction">
            <dsp:param bean="Profile.id" name="userId"/>
            <dsp:param name="function" value="buyer"/>
            <dsp:oparam name="true">
              <dsp:droplet name="Switch">
                <dsp:param bean="Profile.approvalRequired" name="value"/>
                <dsp:oparam name="true">
                  <tr valign=top>
                    <td><dsp:a href="orders_rejected.jsp"><nobr>拒否されたオーダー</nobr></dsp:a></td>
                    <td>
                    <dsp:droplet name="OrderLookup">
                      <dsp:param bean="Profile.id" name="userId"/>
                      <dsp:param name="state" value="failed_approval"/>
                      <dsp:param name="queryTotalOnly" value="true"/>
                      <dsp:oparam name="output">
                        <dsp:valueof param="total_count"/>
                      </dsp:oparam>
                      <dsp:oparam name="empty">
                        0
                      </dsp:oparam>
                    </dsp:droplet>
                    </td>
                    <td>承認者から戻された、承認が必要なオーダー。</td>
                  </tr>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>

          <tr><td><img src="../images/d.gif"></td></tr>
          <tr valign=top>
            <td colspan=3>
            <table width=100% cellpadding=3 cellspacing=0 border=0>
              <tr><td class=box-top>&nbsp;購入ツール</td></tr></table>
            </td>
          </tr>
          <tr valign=top>
            <td><dsp:a href="purchase_lists.jsp"><nobr>購入リスト</nobr></dsp:a></td>
            <td></td>
            <td>オーダー頻度の高いアイテムのリストです。
            カタログからリストにアイテムを追加できます。</td>
          </tr>
          <tr valign=top>
            <td><dsp:a href="scheduled_orders.jsp"><nobr>スケジュール済みオーダー</nobr></dsp:a></td>
            <td></td>
            <td>あらかじめ定義されているスケジュールに従って、
            自動的に送信されるオーダーです。</td>
          </tr>
          <tr valign=top>
            <td><dsp:a href="saved_orders.jsp">保存済みオーダー</dsp:a></td>
            <td></td>
            <td>オーダーが未完了でまだ送信できない場合は、
            そのオーダーをいったん保存し、後で送信できます。</td>
          </tr>
          <tr><td><img src="../images/d.gif"></td></tr>
          <tr valign=top>
            <td colspan=3>
            <table width=100% cellpadding=3 cellspacing=0 border=0>
              <tr><td class=box-top>&nbsp;プロファイル情報</td></tr></table>
            </td>
          </tr>
          <tr valign=top>
            <td><dsp:a href="my_profile.jsp">私のプロファイル</dsp:a></td>
            <td></td>
            <td>連絡先情報およびデフォルトを編集します。</td>
          </tr>
        </table>
        </td>
      </tr>
    </table>
    </td>
  </tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/user/my_account.jsp#2 $$Change: 651448 $--%>
