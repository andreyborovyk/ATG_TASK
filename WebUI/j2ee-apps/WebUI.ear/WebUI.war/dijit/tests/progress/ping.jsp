<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<% 
	int i;
	double j;
	i = Integer.parseInt(request.getParameter("i"));
	j = Math.ceil(Math.random() * 10);
	out.println(i+j);
%>

</dsp:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/dijit/tests/progress/ping.jsp#2 $$Change: 651448 $--%>
