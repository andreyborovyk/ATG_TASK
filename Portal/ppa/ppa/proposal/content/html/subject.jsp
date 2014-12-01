<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.beans.*,java.beans.*,java.util.*" errorPage="/error.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"  %>
<%@ taglib uri="dsp" prefix="dsp" %>

<%
 //Obtain request/response
 GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
 GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);
%>

<fmt:bundle basename="atg.portlet.workflow.Resources">
<dsp:page>
<dsp:importbean bean="/atg/workflow/portal/communityproposal/WorkflowSubjectLookup"/>

<dsp:droplet name="WorkflowSubjectLookup">
<dsp:param name="id" param="pwf_subjectId"/>
<dsp:param name="elementName" value="subject"/>
<dsp:oparam name="output">
  <dsp:getvalueof id="subject" param="subject">

<%
      try {
        DynamicBeanInfo beanInfo = DynamicBeans.getBeanInfo(subject);
        DynamicBeanDescriptor beanDescriptor = beanInfo.getBeanDescriptor();
        DynamicPropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
        ArrayList itemProperties = new ArrayList(Arrays.asList(properties));
        String beanName = beanDescriptor.getName();
        if (beanName == null && subject instanceof atg.repository.RepositoryItem) {
          beanName = ((atg.repository.RepositoryItem)subject).getItemDisplayName();
        }

        pageContext.setAttribute("itemProperties", itemProperties);
        pageContext.setAttribute("beanName", beanName);
%>
         <table>
           <tr align="right"><td><a href="<%= gearServletResponse.encodeGearURL(gearServletRequest.getPortalRequestURI()) %>"><fmt:message key="content.taskinfo-label"/></a></td></tr>
         </table>
         <p><center><font color="cc0000"><strong><fmt:message key="content.subject-label"/>: <c:out value="${beanName}"/></strong></font></center></p>
        <table>
          <th><fmt:message key="content.property-label"/></th><th><fmt:message key="content.value-label"/></th>
          <c:forEach items="${itemProperties}" var="item">
            <tr>
              <th><c:out value="${item.displayName}"/></th>
              <c:set var="propertyName" value="${item.name}"/>
<%
              Object propertyName = pageContext.getAttribute("propertyName");
              Object property = null;
              try {
                property = DynamicBeans.getPropertyValue(subject, (String)propertyName);
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
        System.err.println("exception: " + ie);
      }
%>
  </dsp:getvalueof>

</dsp:oparam>
</dsp:droplet>
</dsp:page>
</fmt:bundle>
<%-- @version $Id: //app/portal/version/10.0.3/ppa/proposal/content/html/subject.jsp#2 $$Change: 651448 $--%>
