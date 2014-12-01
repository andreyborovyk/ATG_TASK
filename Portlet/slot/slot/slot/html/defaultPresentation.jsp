<%--
  Default presentation page for Slot portlet
  Displays content from slot using the TargetingFirst droplet.
  
  @version $Id: //app/portal/version/10.0.3/portlet/slot/slot/html/defaultPresentation.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page import="atg.servlet.*" %>
<%@ page import="javax.portlet.*" %>
<%@ page import="atg.beans.*" %>
<%@ page import="java.beans.*" %>
<%@ page import="java.util.*" %>

<%@ page errorPage="/error.jsp"%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"   %>
<%@ taglib prefix="dspel"   uri="atg-dspjspEL"                  %>

<fmt:setBundle var="slotbundle" basename="atg.portlet.slot.slot"/>

<portlet:defineObjects/>

<%
  String currentSlotName = (String)renderRequest.getAttribute("slotName");
  boolean slotCanBeResolved = false;
  if (currentSlotName == null) { 
    currentSlotName = ""; 
  }
  else {
    DynamoHttpServletRequest dynamoRequest = ServletUtil.getDynamoRequest(request);
    Object slot = dynamoRequest.resolveName(currentSlotName);
    slotCanBeResolved = slot == null ? false : true;
  }

  pageContext.setAttribute("slotCanBeResolved", new Boolean(slotCanBeResolved));

%>
<dspel:page>
  <c:choose>
    <c:when test="${empty slotName}">
      <fmt:message key="emptySlot" bundle="${slotbundle}"/>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${not slotCanBeResolved}">
          <fmt:message key="unresolvedSlot" bundle="${slotbundle}">
            <fmt:param value="${slotName}"/>
          </fmt:message>
        </c:when>
        <c:otherwise>
       
  <dspel:droplet name="/atg/targeting/TargetingFirst">
    <dspel:param name="targeter" bean="${slotName}"/>
    <dspel:oparam name="output">
      <dspel:getvalueof var="element" param="element">
        <fmt:message key="itemReturned" bundle="${slotbundle}"/>:
        <c:out value="${element}"/><br>
<%
  Object element = pageContext.getAttribute("element");
  if (element != null) {
    // The four basic types that Slots support
    if (element instanceof java.lang.String ||
        element instanceof java.util.Date ||
        element instanceof java.lang.Long ||
        element instanceof java.lang.Double) {
%>
        <fmt:message key="className" bundle="${slotbundle}"/>:
        <c:out value="${element.class.name}"/><br>
<%
    }
    else {
      // it is most likely a RepositoryItem, but we will use DynamicBeans
      // to get the details
      try {
        DynamicBeanInfo beanInfo = DynamicBeans.getBeanInfo(element);
        DynamicBeanDescriptor beanDescriptor = beanInfo.getBeanDescriptor();
        DynamicPropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
        ArrayList itemProperties = new ArrayList(Arrays.asList(properties));
        String beanName = beanDescriptor.getName();
        if (beanName == null && element instanceof atg.repository.RepositoryItem) {
          beanName = ((atg.repository.RepositoryItem)element).getItemDescriptor().getItemDescriptorName();
        }

        pageContext.setAttribute("itemProperties", itemProperties);
        pageContext.setAttribute("beanName", beanName);
%>
        bean name: <c:out value="${beanName}"/><br>
        <table>
          <th>property display name</th><th>value</th>
          <c:forEach items="${itemProperties}" var="item">
            <tr>
              <td><c:out value="${item.displayName}"/></td>
              <c:set var="propertyName" value="${item.name}"/>
<%
              Object propertyName = pageContext.getAttribute("propertyName");
              Object property = null;
              try {
                property = DynamicBeans.getPropertyValue(element, (String)propertyName);
              }
              catch (PropertyNotFoundException e) {
                //out.print("Property not found: " + e);
              }
              if (property == null) property = new String("null");
              pageContext.setAttribute("property", property);
%>
              <td><c:out value="${property}"/></td>
            </tr>
          </c:forEach>
        </table>
<%
      }
      catch (IntrospectionException ie) {
        System.out.println("exception: " + ie);
      }
    }
  }
%>
      </dspel:getvalueof>
    </dspel:oparam>
    <dspel:oparam name="empty">
      <fmt:message key="noItems" bundle="${slotbundle}"/>
    </dspel:oparam>
  </dspel:droplet>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
</dspel:page>
<%-- @version $Id: //app/portal/version/10.0.3/portlet/slot/slot/html/defaultPresentation.jsp#2 $$Change: 651448 $--%>
