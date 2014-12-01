<%--
  CSS containing default styles for AssetPicker iframe

  @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/templates/style/css/assetPickerStyle.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ page contentType="text/css" %>

<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

#browser {
 display:none;
 position:fixed;
 top:70px;
 left:45%;
 z-index:2000;
}

*+html #browser {
 display:none;
 /*position:fixed !important;*/
 position:absolute;
 top:70px;
 left:45%;
 z-index:2000;
 /* IE5.5+/Win - this is more specific than the NS4 version */
 left: expression( ( 400 + ( ignoreMe2 =
   ( document.documentElement && document.documentElement.scrollLeft ) ?
   document.documentElement.scrollLeft : document.body.scrollLeft ) ) +
   'px' );

 top: expression( ( 40 + ( ignoreMe = ( document.documentElement && document.documentElement.scrollTop ) ?  document.documentElement.scrollTop : document.body.scrollTop ) ) +
   'px' );
 }

* html #browser {
 display:none;
 /*position:fixed !important;*/
 position:absolute;
 top:70px;
 left:45%;
 z-index:2000;
 /* IE5.5+/Win - this is more specific than the NS4 version */
 left: expression( ( 400 + ( ignoreMe2 =
   ( document.documentElement && document.documentElement.scrollLeft ) ?
   document.documentElement.scrollLeft : document.body.scrollLeft ) ) +
   'px' );

 top: expression( ( 40 + ( ignoreMe = ( document.documentElement && document.documentElement.scrollTop ) ?  document.documentElement.scrollTop : document.body.scrollTop ) ) +
   'px' );
 }


#browserIframe {
 padding-left:6px;
 width:406px;
 height:469px;
 background:url(../../../images/bg_browser.png) repeat-y left top !important;
 background:url(../../../images/bg_browser.gif) repeat-y left top;
 }
#browserIframeLarge {
 padding-left:6px;
 width:506px;
 height:469px;
 background:url(../../../images/bg_browserLarge.png) repeat-y left top
!important;
 background:url(../../../images/bg_browserLarge.gif) repeat-y left top;
 }
#iFrame {
 position:absolute;
 margin:0;
 padding:0;
 width:392px;
 height:467px;
 background-color:#A7BAC9;
 border:1px solid #42596D;
 }
#browserBottom {
 width:406px;
 height:9px;
 background:url(../../../images/bg_browserBottom.png) no-repeat left top
!important;
 background:url(../../../images/bg_browserBottom.gif) no-repeat left top;
 }
#browserBottomLarge {
 width:506px;
 height:9px;
 background:url(../../../images/bg_browserBottomLarge.png) no-repeat left top
!important;
 background:url(../../../images/bg_browserBottomLarge.gif) no-repeat left top;
 }

</dsp:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/templates/style/css/assetPickerStyle.jsp#2 $$Change: 651448 $--%>
