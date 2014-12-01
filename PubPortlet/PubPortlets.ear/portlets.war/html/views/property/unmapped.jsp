<%--
  Default view for any unmapped properties
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

<table border="0" cellpadding="0" cellspacing="3">
<tr>
  
  <%--
   Display the property view title...
   --%>
  <c:set var="pvTitle" value="${mpv.propertyDescriptor.displayName}"/>
  <td class="formLabel">
    <c:out value="<label for=\"${mpv.propertyDescriptor.name}\">" escapeXml="false"/><c:out value="${pvTitle}" escapeXml="false"/>:</label>
  </td>

  <td class="formText formPadding">

  <%-- Show the actual field --%>
  <dspel:valueof bean="${mpv.formHandlerProperty}"/>&nbsp;

  <%-- Close out field --%>
  </td>

</tr>
</table>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/unmapped.jsp#2 $$Change: 651448 $--%>
