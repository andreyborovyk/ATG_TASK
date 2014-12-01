<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<% 
	int i;
	double j;
	i = Integer.parseInt(request.getParameter("i"));
	j = Math.ceil(Math.random() * (100-i));
	if(j>30) j = Math.ceil(j/3);
%>

{
    'value':<%= j+i %>,
    'state':'Good',
    'message':'Everything is awesome'
}

</dsp:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/dijit/tests/progress/ping2.jsp#2 $$Change: 651448 $--%>
