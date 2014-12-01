<%--
  Edit view for password
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/passwordEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt" %>

<dspel:page>

  <c:set var="debug" value="false"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <dspel:importbean var="config"
                    bean="/atg/web/personalization/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Get an ID for the input field --%>
  <c:set var="inputId" value="propertyValue_${propertyView.uniqueId}"/>
  <c:if test="${not empty requestScope.uniqueAssetID}">
    <c:set var="inputId" value="propertyValue_${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>  
  </c:if>

  <%-- Determine the style for the input field.  An error style is used if
       there is an exception for the property. --%>
  <c:set var="inputClass" value="formTextField"/>
  <c:if test="${not empty formHandler.propertyExceptions[propertyView.propertyName]}">
    <c:set var="inputClass" value="${inputClass} error"/>
  </c:if>

  <c:if test="${debug}">
    [passwordEditor.jsp: id=<c:out value="${inputId}"/><br />
    [passwordEditor.jsp: inputClass=<c:out value="${inputClass}"/><br />
    [passwordEditor.jsp: inputSize=<c:out value="${inputSize}"/><br />
    [passwordEditor.jsp: inputMaxLength=<c:out value="${inputMaxLength}"/><br />
    [passwordEditor.jsp: propertyView.formHandlerProperty=<c:out value="${propertyView.formHandlerProperty}"/><br />
    [passwordEditor.jsp: formHandler=<c:out value="${formHandler}"/><br />
  </c:if>

  <table border="0" cellpadding="0" cellspacing="0">
  <tr>
  <td style="font-weight:700; text-align:right; vertical-align:top; white-space:nowrap;color:#545454;"><fmt:message key="userEditor.passwordHeading"/></td>
  <td><dspel:input type="password" 
               id="${inputId}"
               iclass="formTextField"
               oninput="formFieldModified()"
               onpropertychange="formFieldModified()"
               bean="${formHandler.absoluteName}.changedPassword"
               /></td>
  </tr>
  <tr>
  <td style="font-weight:700; text-align:right; vertical-align:top; white-space:nowrap;color:#545454;"><fmt:message key="userEditor.confirmPasswordHeading"/></td>
  <td><dspel:input type="password"
               id="${inputId}_confirm"
               iclass="formTextField"
               oninput="formFieldModified()"
               onpropertychange="formFieldModified()"
               bean="${formHandler.absoluteName}.confirmPassword"
               /></td>
  </tr>
  </table>

</dspel:page>
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/passwordEditor.jsp#2 $$Change: 651448 $--%>
