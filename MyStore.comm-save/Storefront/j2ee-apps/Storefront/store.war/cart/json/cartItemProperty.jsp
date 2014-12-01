<%-- Renders a single property of a cart item as a JSON object with 2 properties - 'name' and 'value' 
     Only renders the JSON object is the propertyValue is not empty --%>
<dsp:page>
  <dsp:getvalueof var="value" param="propertyValue"/>
  <dsp:getvalueof var="nameKey" param="propertyNameKey"/>
  
  <c:if test="${!empty value}">
    <json:object>
      <json:property name="name">
        <fmt:message key="${nameKey}"/>
      </json:property>
      <json:property name="value" value="${value}"/>
    </json:object>
  </c:if>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/cart/json/cartItemProperty.jsp#3 $$Change: 635816 $--%>
