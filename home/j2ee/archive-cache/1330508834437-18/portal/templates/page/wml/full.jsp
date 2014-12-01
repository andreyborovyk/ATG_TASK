<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,java.util.*" contentType="text/vnd.wap.wml" errorPage="/error.jsp" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>

<fmt:setLocale value="${request.locale}"/>

<?xml version="1.0"?> 
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml"> 


<dsp:page>

<%-- If WML 1.2 or 1.3 features are required, then use one of the following DOCTYPE instead: 
  --
  -- <!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.2//EN" "http://www.wapforum.org/DTD/wml12.dtd"> 
  -- <!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.3//EN" "http://www.wapforum.org/DTD/wml13.dtd"> 
  --%> 
<wml> 
  <template> 
    <do type="prev" label="Prev">
      <prev/>
    </do>
  </template>
  
<c:set var="gearId" value="${param.paf_gear_id}"/>
<c:if test="${gearId != null}">
<%
  Portal portal = (Portal)request.getAttribute(Attribute.PORTAL);
  Gear gear = null;
  if(portal != null)
    gear = portal.getGearById((String)pageContext.findAttribute("gearId"));
  
  request.setAttribute(Attribute.GEAR,gear);
%>
</c:if>
<c:set var="gearMode" value="${param.paf_gm}"/>
<c:if test="${gearMode != null}">

 <c:choose>
   <c:when test="${gearMode == 'content'}">
     <% request.setAttribute(Attribute.GEARMODE,GearMode.CONTENT); %>
   </c:when>
   <c:when test="${gearMode == 'userConfig'}">
     <% request.setAttribute(Attribute.GEARMODE,GearMode.USERCONFIG); %>
   </c:when>
   <c:when test="${gearMode == 'help'}">
     <% request.setAttribute(Attribute.GEARMODE,GearMode.HELP); %>
   </c:when>
   <c:when test="${gearMode == 'about'}">
     <% request.setAttribute(Attribute.GEARMODE,GearMode.ABOUT); %>
   </c:when>
   <c:when test="${gearMode == 'preview'}">
     <% request.setAttribute(Attribute.GEARMODE,GearMode.PREVIEW); %>
   </c:when>
   <c:when test="${gearMode == 'instanceConfig'}">
     <% request.setAttribute(Attribute.GEARMODE,GearMode.INSTANCECONFIG); %>
   </c:when>
   <c:when test="${gearMode == 'initialConfig'}">
     <% request.setAttribute(Attribute.GEARMODE,GearMode.INITIALCONFIG); %>
   </c:when>
   <c:when test="${gearMode == 'installConfig'}">
     <% request.setAttribute(Attribute.GEARMODE,GearMode.INSTALLCONFIG); %>
   </c:when>
   <c:otherwise>
     <% request.setAttribute(Attribute.GEARMODE,GearMode.CONTENT); %>
   </c:otherwise>
 </c:choose>

</c:if>


     <%-- Render Gear Contents --%>
     <c:set var="GEAR"><%= Attribute.GEAR %></c:set>
     <c:set var="GEARMODE"><%= Attribute.GEARMODE %></c:set>
     <c:set var="gear"                     value="${requestScope[GEAR]}"/>
     <c:set var="gearMode"                 value="${requestScope[GEARMODE]}"/>
     <% 
                pageContext.setAttribute("gearContextType",GearContext.class);		
     %>
     <paf:context var="gearContext" type="${gearContextType}"/>
     <c:set target="${gearContext}" property="gear" value="${gear}"/>
     <c:set target="${gearContext}" property="gearMode" value="${gearMode}"/>
     <paf:include context="${gearContext}"/>     

</wml>
</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/templates/page/wml/full.jsp#2 $$Change: 651448 $--%>
