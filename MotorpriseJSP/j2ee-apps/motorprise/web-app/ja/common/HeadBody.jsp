<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<DECLAREPARAM NAME="pagetitle" 
              CLASS="java.lang.String" 
              DESCRIPTION="The text to display as page title.  If not set, then
              defaults to Streb Auto Supply"
              OPTIONAL>

<HTML>

<HEAD>
  <TITLE>Motorprise - <dsp:valueof param="pagetitle"/></TITLE>
  <LINK REL=STYLESHEET 
	TYPE="text/css" 
	HREF="<%=request.getContextPath()%>/en/common/Style.css"
	TITLE="B2B デモ用スタイルシート">
</HEAD>

<BODY>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/common/HeadBody.jsp#2 $$Change: 651448 $--%>
