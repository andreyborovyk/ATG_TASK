<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt"                     %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:bundle basename="${config.resourceBundle}">
    <fmt:message var="message" key="tree.emptymessage"/>
    <c:out value="${message}"/>
  </fmt:bundle>

</dsp:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tree/emptyTreeView.jsp#2 $$Change: 651448 $--%>
