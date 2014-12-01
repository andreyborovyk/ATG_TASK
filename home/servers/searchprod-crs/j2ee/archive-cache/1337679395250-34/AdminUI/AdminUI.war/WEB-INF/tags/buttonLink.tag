<%@ tag language="java" %>
<%@ attribute name="titleKey" required="true" %>
<%@ attribute name="tooltipKey" required="true" %>
<%@ attribute name="href" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setBundle basename="atg.searchadmin.WebAppResources"/>

<input type="button" onclick="return loadRightPanel('${href}');"
  value="<fmt:message key='${titleKey}'/>" title="<fmt:message key='${tooltipKey}'/>" />
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/buttonLink.tag#2 $$Change: 651448 $--%>
