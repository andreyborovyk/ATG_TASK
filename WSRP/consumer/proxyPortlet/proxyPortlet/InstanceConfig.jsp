<%@ page import="atg.portal.framework.GearDefinition"%>
 <%--
 InstanceConfig gear mode main page for the consumer proxy portlet.
 It will show all the registered producers and their offered portlets added to the consumer.
 --%>


<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>


<paf:hasCommunityRole roles="leader,gear-manager">

<dspel:page>

<%--<paf:InitializeGearEnvironment id="pafEnv">--%>


<dspel:importbean bean="/atg/wsrp/consumer/PortletFormHandler" var="PortletFormHandler"/>

    <c:choose>
    <c:when test="${PortletFormHandler.instanceConfigState eq 'PortletDescription'}">
        <jsp:include page="portletDescription.jsp"/>
    </c:when>

    <c:otherwise>
        <jsp:include page="configProxyPortlet.jsp"/>
    </c:otherwise>

    </c:choose>

<%--</paf:InitializeGearEnvironment>--%>

</dspel:page>

</paf:hasCommunityRole>
<%-- @version $Id: //product/WSRP/version/10.0.3/consumer/proxyPortlet/proxyPortlet/InstanceConfig.jsp#2 $$Change: 651448 $--%>
