<%--
  A container for a non top-level view

  @param   mpv  the property view of the parent repository item property
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
--%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws"          %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<dspel:page>

  <%-- get the repository ID of the parent property --%>
  <dspel:getvalueof var="id" bean="${mpv.formHandlerProperty}.repositoryId"/>

  <c:if test="${ ! empty id }">

    <c:if test="${debug}">
      ID: <c:out value="${id}"/><br />
      repository: <c:out value="${mpv.repositoryItemDescriptor.repository}"/><br />
      item descriptor: <c:out value="${mpv.repositoryItemDescriptor.itemDescriptorName}"/><br />
    </c:if>

    <dspel:droplet name="/atg/targeting/RepositoryLookup">
      <dspel:param name="fireViewItemEvent" value="false"/>
      <dspel:param name="fireContentEvent" value="false"/>
      <dspel:param name="repository" value="${mpv.repositoryItemDescriptor.repository}"/>
      <dspel:param name="itemDescriptr" value="${mpv.repositoryItemDescriptor.itemDescriptorName}"/>
      <dspel:param name="id" value="${id}"/>
      <dspel:oparam name="output">

        <dspel:getvalueof var="subItem" param="element"/>
        <c:set var="subItem" value="${subItem}" scope="request"/>

        <%-- Save old 'imap' and restore it bellow, after rendering view --%>
        <c:set var="oldImap" value="${imap}"/>

        <%-- Get item mapping for the referenced item --%>
        <biz:getItemMapping var="imap" readOnlyMode="view" showExpert="${expert}" item="${subItem}" 
          parentPropertyView="${mpv}" propertyList="displayName">

          <%-- set the new item mapping into the request as 'imap' --%>
          <c:set var="imap" value="${imap}" scope="request"/>

          <c:forEach items="${imap.viewMappings}" var="aView" varStatus="vstat">
            <c:set var="view" value="${aView}"/>
            <c:set var="mpv" scope="request" value="${view.propertyMappings['descriptor1Ref.displayName']}"/>

	    <c:forEach items="${view.propertyMappings}" var="pm">
	      <c:out value="${pm.key}"/> val: <c:out value="${pm.value}"/>
	    </c:forEach>

	    <c:out value="${mpv.formHandlerProperty}"/>
            <dspel:include otherContext="${mpv.contextRoot}" page="${mpv.uri}"/>
          </c:forEach>
        </biz:getItemMapping>

        <%-- restore old item mapping for remainder of page --%>
        <c:set var="imap" value="${oldImap}"/>

      </dspel:oparam>
    </dspel:droplet>    

  </c:if>

</dspel:page>
<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/item/gsa/nestedViewContainer.jsp#2 $$Change: 651448 $--%>
