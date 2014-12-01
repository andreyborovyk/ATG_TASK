<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,java.util.*" contentType="text/vnd.wap.wml" errorPage="/error.jsp" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<fmt:setLocale value="${request.locale}"/>
<fmt:setBundle var="templatesbundle" basename="atg.portal.templates"/>

<dsp:page>
<dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>

<c:set var="PORTALSERVLETREQUEST"><%= Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="PORTALSERVLETRESPONSE"><%= Attribute.PORTALSERVLETRESPONSE %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="portalServletResponse"    value="${requestScope[PORTALSERVLETRESPONSE]}"/>
<c:set var="community"                value="${portalServletRequest.community}"/>
<c:set var="page"                     value="${portalServletRequest.page}"/>
<c:set var="communityName"            value="${community.name}"/>
<c:set var="pageName"                 value="${page.name}"/>
<c:set var="pageId"                   value="${page.id}"/>
<c:set var="regionMap"                value="${page.regions}"/>

<c:set var="contextPath"><%= request.getContextPath() %></c:set>
<paf:encodeURL var="loginURL"     url="${contextPath}/userprofiling/login.jsp"/>
<paf:encodeURL var="logoutURL"    url="${contextPath}/userprofiling/logout.jsp"/>

<?xml version="1.0"?> 
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml"> 
 
<%-- If WML 1.2 or 1.3 features are required, then use one of the following DOCTYPE instead: 
  --
  -- <!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.2//EN" "http://www.wapforum.org/DTD/wml12.dtd"> 
  -- <!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.3//EN" "http://www.wapforum.org/DTD/wml13.dtd"> 
  --%> 
<wml> 
 
  <card id="init" title="<c:out value="${communityName}"/>">
    <onevent type="ontimer">
      <go href="<c:out value="#page${pageId}"/>"/>
    </onevent>
    <timer value="25"/>
 
   <p align="center">
      <big><b><c:out value="${communityName}"/></b></big><br/>
      <b><c:out value="${pageName}"/></b>
    </p>

  </card>

  <%-- Render Toplevel Page --%>
  <card id="<c:out value="page${pageId}"/>" title="<c:out value="${pageName}"/>"> 
 
      <%-- Render Login/Logout --%>
      <p>
          <c:choose>
            <c:when test="${profile.transient == false}">
              <a href="<c:out value="${logoutURL}"/>"><fmt:message key="page-label-logout" bundle="${templatesbundle}"/></a>
            </c:when>   
            <c:otherwise>
              <a href="<c:out value="${loginURL}"/>"><fmt:message key="page-label-login" bundle="${templatesbundle}" /></a>
            </c:otherwise>
          </c:choose>
      </p>  

      <%-- Render Gear Titles --%>
      <p>
          <c:forEach    var="region"
			items="${regionMap}">

              <c:set var="gears" value="${region.value.gears}"/>
              <c:forEach	var="gear"
				items="${gears}">
		
			<a href="<c:out value="#gear${gear.id}"/>"><c:out value="${gear.name}"/></a><br/>

              </c:forEach>

          </c:forEach>
      </p>
    </card>


    <%--
      -- Render
      --%>
    <c:forEach  var="region"
		items="${regionMap}">

       <c:set var="gears" value="${region.value.gears}"/>
       <c:forEach	var="gear"
			items="${gears}">
        

            <card id="<c:out value="gear${gear.id}"/>" title="<c:out value="${gear.name}"/>">	
	      <do type="prev" label="Prev">
                <prev/>
              </do>
										
              <%-- Render Gear Contents --%>
              <% 
                        pageContext.setAttribute("gearContextType",GearContext.class);
			pageContext.setAttribute("gearMode",GearMode.CONTENT);		
	      %>
              <paf:context var="gearContext" type="${gearContextType}"/>
              <c:set target="${gearContext}" property="gear" value="${gear}"/>
              <c:set target="${gearContext}" property="gearMode" value="${gearMode}"/>
              <paf:include context="${gearContext}"/>
              
						
            </card>
        </c:forEach>
    </c:forEach>

         
</wml>
</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/templates/page/wml/shared.jsp#2 $$Change: 651448 $--%>
