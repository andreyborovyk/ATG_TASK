<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<% 

response.setContentType("application/Octet-Stream");
response.setHeader("Content- Disposition","attachment;filename=fileName");

Thread.sleep(5000); 


%>
This was a slow download, how was the experience for you?

</dsp:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tests/general/ajaxDownload/delay.jsp#2 $$Change: 651448 $--%>
