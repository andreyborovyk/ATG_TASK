<%--
  Configure page for Slot portlet
  Used to display and modify configuration settings for the slot portlet.
  
  @version $Id: //app/portal/version/10.0.3/portlet/slot/slot/html/edit.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page import="javax.portlet.*" %>

<%@ page errorPage="/error.jsp"%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"   %>
<%@ taglib prefix="dspel"   uri="atg-dspjspEL"                  %>

<fmt:setBundle var="slotbundle" basename="atg.portlet.slot.slot"/>

<dspel:importbean var="slotFormHandler" bean="/atg/portlet/slot/SlotFormHandler"/>

<portlet:defineObjects/>

<dspel:page>
<%
  PortletPreferences prefs = renderRequest.getPreferences();
  String currentSlotName = prefs.getValue("slotComponent", "");
  String currentPresentationPage = prefs.getValue("presentationPage", "defaultPresentation.jsp");

  pageContext.setAttribute("currentSlotName", currentSlotName);
  pageContext.setAttribute("currentPresentationPage", currentPresentationPage);
%>
<p>

<c:set var="actionURL"><portlet:actionURL/></c:set>

<dspel:form action="${actionURL}" method="post">
  <table>
    <tr>
      <td><fmt:message key="selectSlot" bundle="${slotbundle}"/>:</td>
      <td>
        <dspel:select bean="SlotFormHandler.slotName">
          <c:set var="selectedVal" value="false"/>
          <c:if test="${empty currentSlotName}">
            <c:set var="selectedVal" value="true"/>
          </c:if>
          <dspel:option value="" selected="${selectedVal}"><fmt:message key="none" bundle="${slotbundle}"/></dspel:option>
          <c:set var="selectedVal" value="false"/>
          <c:forEach items="${slotFormHandler.availableSlots}" var="slot">
            <c:if test="${slot == currentSlotName}">
              <c:set var="selectedVal" value="true"/>
            </c:if>
            <dspel:option value="${slot}" selected="${selectedVal}"><c:out value="${slot}"/></dspel:option>
            <c:set var="selectedVal" value="false"/>
          </c:forEach>
        </dspel:select>
      </td>
    </tr>
    <tr>
      <td><fmt:message key="presentationPage" bundle="${slotbundle}"/>:</td>
      <td><dspel:input type="text" bean="SlotFormHandler.presentationPage" value="${currentPresentationPage}" size="30"/></td>
    </tr>
    <tr>
      <fmt:message key="save" var="saveLabel" bundle="${slotbundle}"/>
      <fmt:message key="cancel" var="cancelLabel" bundle="${slotbundle}"/>
      <td>&nbsp;</td>
      <td>
        <dspel:input type="submit" value="${saveLabel}" bean="SlotFormHandler.update"/>
        <dspel:input type="submit" value="${cancelLabel}" bean="SlotFormHandler.cancel"/>
      </td>
    </tr>
  </table>
</dspel:form>
</p>
</dspel:page>
<%-- @version $Id: //app/portal/version/10.0.3/portlet/slot/slot/html/edit.jsp#2 $$Change: 651448 $--%>
