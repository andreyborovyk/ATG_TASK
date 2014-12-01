<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<html><head><title>私の投資目標</title></head>

<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>
<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.userType" name="value"/>
  <dsp:oparam name="guest">
    現在ログインしていません。目標を変更するには、
    登録またはログインが必要です。
    <dsp:a href="login.jsp">ログイン</dsp:a>
  </dsp:oparam>

  <dsp:oparam name="default">
    <dsp:droplet name="Switch">
      <dsp:param bean="ProfileFormHandler.formError" name="value"/>
      <dsp:oparam name="true">
        <font color=cc0000><STRONG><UL>
        <dsp:droplet name="ProfileErrorMessageForEach">
          <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
          <dsp:oparam name="output">
            <LI> <dsp:valueof param="message"/>
          </dsp:oparam>
        </dsp:droplet>
        </UL></STRONG></font>
      </dsp:oparam>
    </dsp:droplet>

    <table border=0 cellpadding=4 cellspacing=0>
      <tr valign=top>
        <td width=120 bgcolor=#003366 rowspan=2>
        <!-- left bar navigation -->
        <dsp:include page="nav.jsp" ></dsp:include></td>
        
        <td>
        <table border=0>
          <tr>
            <td colspan=2><img src="images/banner-editgoals.gif"></td>
          </tr>
          <tr valign=top>
           <td>
           <table width=430>
             <tr>
               <td colspan=5>
               <b>Quincy での投資目標を変更してください。</b>
               <p>
               投資計画をお知らせいただくことによって、目標に合わせたお手伝いが
               できます。お客様の実際のポートフォリオとご希望の
               収益を照合することにより、現在の投資運用が正しい方向に進んでいるかどうかを
               確認できます。方法は簡単です。 
               <p>
               <b>まず、お客様の投資スタイルについてお聞かせください。</b>
               <br>&nbsp;<br></td>
             </tr>

             <dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">
             <dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="index.jsp"/>

             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.strategy" name="style" type="radio" value="aggressive"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-aa.gif"></td>
               <td>&nbsp;</td>
               <td>積極型（ハイリスク／ハイリターン）</td>
             </tr>

             <tr>
                <td><dsp:input bean="ProfileFormHandler.value.strategy" name="style" type="radio" value="moderate"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-bb.gif"></td>
               <td>&nbsp;</td>
               <td>バランス型</td>
             </tr>
  
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.strategy" name="style" type="radio" value="conservative"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-cc.gif"></td>
               <td>&nbsp;</td>
               <td>堅実型（ローリスク／ローリターン）</td>
             </tr>  
     
             <tr>
               <td colspan=5>&nbsp;<br>
               <b>次に、投資対象とする期間をお聞かせください。</b>
               <br>&nbsp;</td>
             </tr>
  
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.goals" name="range" type="radio" value="short-term"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-sr.gif"></td>
               <td>&nbsp;</td> 
               <td>短期</td>
             </tr>
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.goals" name="range" type="radio" value="long-term"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-lr.gif"></td>
               <td>&nbsp;</td> 
               <td>長期</td>
             </tr>
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.goals" name="range" type="radio" value="retirement-focus"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-rr.gif"></td>
               <td>&nbsp;</td> 
               <td>老後重点</td> 
            </tr>
     
            <tr>
              <td colspan=5><br>
              <dsp:input bean="ProfileFormHandler.update" type="submit" value="投資目標の変更"/>
              <br>&nbsp;<br>&nbsp;</td>
            </tr>
          </table>
          </dsp:form>
          </td>
        </tr>
      </table>
      </td>
    </tr>
  </table>

  </dsp:oparam>
</dsp:droplet>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/mygoals.jsp#2 $$Change: 651448 $--%>
