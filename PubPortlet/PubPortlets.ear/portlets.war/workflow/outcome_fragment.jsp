<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="atg-dspjspEL"                  %>
<%@ taglib uri="/pws-taglib" prefix="pws" %>

<dspel:page>

<pws:getProject var="project" projectId="${param.projectId}"/>

<p>
Project: <c:out value="${project.displayName}"/>
<br />
Are you sure you want to see some custom message here?
</p>
</dspel:page>
<%-- @version $Id: //product/Publishing/version/10.0.3/pws/jsp-src/workflow/outcome_fragment.jsp#2 $$Change: 651448 $--%>
