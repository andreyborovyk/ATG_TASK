<%--
  Main panel for Flex-based Asset Manager UI.

  @version $Id: //product/BCC/version/10.0.3/src/web-apps/ControlCenter/asseteditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

  <%! String flashVars = null; %>
  <%
    // Assemble request parameters info FlashVars.
    // NB: There is probably an easier way to do this...
    StringBuffer buf = new StringBuffer();
    java.util.Enumeration names = request.getParameterNames();
    boolean first = true;
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      String value = request.getParameter(name);
      if (value != null) {
        if (first)
          first = false;
        else
          buf.append('&');
        buf.append(name + "=" + value);
      }
    }
    flashVars = buf.toString();
  %>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
      <title></title>
      <style type="text/css">
        body, html {
          margin:   0px;
          padding:  0px;
          overflow: hidden;
          height:   100%;
        }
      </style>
      <script type="text/javascript">
        function onBeforeUnload() {
          var id = window.windowId;
          var controlCenterApp = document.getElementById("controlCenterApp");
          if (controlCenterApp && controlCenterApp.getUID) {
            var uid = controlCenterApp.getUID();
            // Would it be possible for us to issue an Ajax request here, to
            // invoke a page that would traverse through all of the beans in
            // the FlexDestinationRegistry and, for those beans that implement
            // an interface with the removeWindowInfo(windowId) method, invoke
            // that method with the returned UID?  Or is it unsafe to issue a
            // request from an unloading page?
          }
        }
      </script>
    </head>

    <body scroll="no" onbeforeunload="onBeforeUnload()">
      <!-- TODO: How is this classid generated? -->
      <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
              id="controlCenterApp"
              width="100%" height="100%"
              codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
        <param name="movie" value="AssetEditorStandalone.swf"/>
        <param name="FlashVars" value="<%= flashVars %>"/>
        <param name="quality" value="high"/>
        <param name="bgcolor" value="#869ca7"/>
        <param name="allowScriptAccess" value="sameDomain"/>
        <embed name="controlCenterApp" src="AssetEditorStandalone.swf"
               FlashVars="<%= flashVars %>"
               quality="high" bgcolor="#869ca7"
               width="100%" height="100%" align="middle"
               play="true" loop="false"
               allowScriptAccess="sameDomain"
               type="application/x-shockwave-flash"
               pluginspage="http://www.adobe.com/go/getflashplayer">
        </embed>
      </object>
    </body>
  </html>

<%-- @version $Id: //product/BCC/version/10.0.3/src/web-apps/ControlCenter/asseteditor.jsp#2 $$Change: 651448 $--%>
