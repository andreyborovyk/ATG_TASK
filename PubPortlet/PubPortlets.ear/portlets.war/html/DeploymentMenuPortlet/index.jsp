<!-- BEGIN FILE index.jsp -->
  <%--
  AdminMenuPortlet index page. 
  Left Menu for the ATG Admin communities home page.
  
  @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentMenuPortlet/index.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ page import="javax.portlet.*" %>
<%@ page import="atg.portal.framework.*" %>
<%@ page import="atg.portal.servlet.*" %>
<%@ page import="atg.servlet.*" %>


<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
<portlet:defineObjects/>
<paf:context var="portalContext"/>

<h3 style="border-right: 1px solid rgb(196, 196, 196);"><fmt:message key="dep-menu-group-name" bundle="${depBundle}"/></h3>
<c:forEach items="${portalContext.community.pages}" var="portalPage" varStatus="st">
  <c:set var="count" value="${st.count}"/>
  <%
   if(portletConfig.getInitParameter("deploymentUIPageIndex").equals(pageContext.findAttribute("count")+"")) { %>
    <c:set var="depUIPortalPage" value="${portalPage}"/>
    <%}%>
</c:forEach>

<%
     //Let portal fun begin
     PortalServletResponse portalServletResponse =
         (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
     PortalServletRequest portalServletRequest =
         (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
     
	PortalContextImpl portalContext = new PortalContextImpl(portalServletRequest);
  Page deploymentUIPage = (Page) pageContext.findAttribute("depUIPortalPage");
   
	portalContext.setPage(deploymentUIPage);
    String baseURI=portalServletRequest.getPortalContextPath() + deploymentUIPage.getPageURI();

    String overviewParams="?atg_admin_menu_group=deployment&atg_admin_menu_1=overview&from_menu=true";
    String configParams="?atg_admin_menu_group=deployment&atg_admin_menu_1=configuration&from_menu=true";
    String overviewURI=baseURI+overviewParams;
    String configURI=baseURI+configParams;
    String overviewEncodedURL=portalServletResponse.encodePortalURL(overviewURI, portalContext);
    String configEncodedURL=portalServletResponse.encodePortalURL(configURI, portalContext);
    pageContext.setAttribute("overviewURL", overviewEncodedURL, pageContext.REQUEST_SCOPE);
    pageContext.setAttribute("configURL", configEncodedURL, pageContext.REQUEST_SCOPE);

      String menuGroup =  ServletUtil.getDynamoRequest(request).getParameter("atg_admin_menu_group");
      String menu1 =   ServletUtil.getDynamoRequest(request).getParameter("atg_admin_menu_1");
      pageContext.setAttribute("atg_admin_menu_group", menuGroup, pageContext.REQUEST_SCOPE);
      pageContext.setAttribute("atg_admin_menu_1", menu1, pageContext.REQUEST_SCOPE);
      
    %>
<%--
<portlet:actionURL var="overviewURL">	
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
</portlet:actionURL>
<portlet:actionURL var="configURL">	
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="configuration"/>
</portlet:actionURL>
--%>
<c:if test="${atg_admin_menu_group != null}">
  <c:set var="atgAdminMenuGroupForMenu" value="${atg_admin_menu_group}" scope="session"/>
</c:if>
<c:if test="${atg_admin_menu_1 != null}">
  <c:set var="atgAdminMenu1ForMenu" value="${atg_admin_menu_1}" scope="session"/>
</c:if>
<div class="leftNav">
<c:forEach items="${portalContext.community.pages}" var="portalPage" varStatus="st">

		
			<table cellpadding="0" cellspacing="0" border="0" class="dataTable" id="campaignEditNav" style="border: none;">
<c:choose>
  <c:when test="${st.count == 1 }"> <%-- Duplicate this when tag for each extension to the menu --%>
    <c:choose>    
      <c:when test="${atgAdminMenu1ForMenu == 'overview'}">

			<tr class="rowHighlightCurrent" onmouseover="this.className='rowHighlightHover';" onmouseout="this.className='rowHighlightCurrent';" onmousedown="doRedirect('<c:out value="${overviewURL}"/>')">
			<td style="border-left: none;" class="leftAligned"><span class="tableInfo"><a href='<c:out value="${overviewURL}"/>' title="" onmouseover="status='';return true;"><fmt:message key="overview" bundle="${depBundle}"/></a></span></td>	
			</tr>
			<tr class="rowHighlight" onmouseover="this.className='rowHighlightHover';" onmouseout="this.className='rowHighlight';" onmousedown="doRedirect('<c:out value="${configURL}"/>')">
			<td style="border-left: none;" class="leftAligned"><span class="tableInfo"><a href='<c:out value="${configURL}"/>' title="" onmouseover="status='';return true;"><fmt:message key="configuration" bundle="${depBundle}"/></a></span></td>	
			</tr>
      </c:when>
      <c:when test="${atgAdminMenu1ForMenu == 'configuration'}">
			<tr class="rowHighlight" onmouseover="this.className='rowHighlightHover';" onmouseout="this.className='rowHighlight';" onmousedown="doRedirect('<c:out value="${overviewURL}"/>')">
			<td style="border-left: none;" class="leftAligned"><span class="tableInfo"><a href='<c:out value="${overviewURL}"/>' title="" onmouseover="status='';return true;"><fmt:message key="overview" bundle="${depBundle}"/></a></span></td>	
			</tr>
			<tr class="rowHighlightCurrent" onmouseover="this.className='rowHighlightHover';" onmouseout="this.className='rowHighlightCurrent';" onmousedown="doRedirect('<c:out value="${configURL}"/>')">
			<td style="border-left: none;" class="leftAligned"><span class="tableInfo"><a href='<c:out value="${configURL}"/>' title="" onmouseover="status='';return true;"><fmt:message key="configuration" bundle="${depBundle}"/></a></span></td>	
			</tr>
      </c:when>
      <c:otherwise>
			<tr class="rowHighlightCurrent" onmouseover="this.className='rowHighlightHover';" onmouseout="this.className='rowHighlightCurrent';" onmousedown="doRedirect('<c:out value="${overviewURL}"/>')">
			<td style="border-left: none;" class="leftAligned"><span class="tableInfo"><a href='<c:out value="${overviewURL}"/>' title="" onmouseover="status='';return true;"><fmt:message key="overview" bundle="${depBundle}"/></a></span></td>	
			</tr>
			<tr class="rowHighlight" onmouseover="this.className='rowHighlightHover';" onmouseout="this.className='rowHighlight';" onmousedown="doRedirect('<c:out value="${configURL}"/>')">
			<td style="border-left: none;" class="leftAligned"><span class="tableInfo"><a href='<c:out value="${configURL}"/>' title="" onmouseover="status='';return true;"><fmt:message key="configuration" bundle="${depBundle}"/></a></span></td>	
			</tr>
       </c:otherwise>
    </c:choose>
  </c:when>
</c:choose>         

</c:forEach>
			</table>

		</div>

</dsp:page>

<!-- END FILE index.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentMenuPortlet/index.jsp#2 $$Change: 651448 $--%>
