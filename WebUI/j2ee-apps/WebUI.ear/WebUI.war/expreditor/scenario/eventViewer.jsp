<%--
  Page fragment that displays a read-only view of a scenario event expression.

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt"                %>

<dspel:page>

  <dspel:importbean var="eventModel" 
                    bean="/atg/web/scenario/EventExpressionModel"/>

  <%-- Render the expression. --%>
  <web-ui:renderExpression var="expression" expression="${eventModel.rootExpression}"/>
  <c:out value="${expression}"/>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/scenario/eventViewer.jsp#2 $$Change: 651448 $--%>
