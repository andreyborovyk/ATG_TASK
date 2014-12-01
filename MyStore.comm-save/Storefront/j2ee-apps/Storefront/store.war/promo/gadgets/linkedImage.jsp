<dsp:page>
  <%-- This page expects the following parameters
       - promotionalContent - the promotionalContent repository item to display 
  --%>
  <crs:promotionalContentWrapper var="title">
    <jsp:body>
      <dsp:getvalueof var="imageurl" vartype="java.lang.String" param="promotionalContent.derivedImage"/>
      <dsp:getvalueof var="displayName" vartype="java.lang.String" param="promotionalContent.displayName"/>
      <dsp:getvalueof var="omitTooltip" vartype="java.lang.Boolean" param="omitTooltip"/>

      <div class="atg_store_promotionItem">
        <c:choose>
          <c:when test="${empty imageurl}">
            <fmt:message key="common.image"/>
          </c:when>
          <c:otherwise>
             <c:if test="${!omitTooltip}">
              <c:set var="tooltip" value="${displayName}"/>
            </c:if>
            <dsp:img src="${imageurl}" alt="${tooltip}"/>
          </c:otherwise>
        </c:choose>
      </div>
    </jsp:body>
  </crs:promotionalContentWrapper>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/promo/gadgets/linkedImage.jsp#3 $$Change: 635816 $--%>
