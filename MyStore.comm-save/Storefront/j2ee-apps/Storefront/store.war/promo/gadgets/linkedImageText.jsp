<dsp:page>

  <%-- This page expects the following parameters 
       promotionalContent - the promotionalContent repository item to display
  --%>
  <crs:promotionalContentWrapper var="title">
    <jsp:body>
      <dsp:getvalueof var="imageurl" vartype="java.lang.String" param="promotionalContent.derivedImage"/>
      <dsp:getvalueof var="displayName" vartype="java.lang.String" param="promotionalContent.displayName"/>

      <div class="atg_store_promotionItem">
        <c:choose>
          <c:when test="${empty imageurl}">
            <fmt:message key="common.image"/>
          </c:when>
          <c:otherwise>
            <dsp:img src="${imageurl}" alt="${displayName}"/>
          </c:otherwise>
        </c:choose>
      </div>
      <dsp:getvalueof id="description" param="promotionalContent.description"/>
      <c:if test="${not empty description}">
        <div class="atg_store_promoCopy">
          <%-- Do not escape description, cause it can contain HTML to be displayed. --%>
          <c:out value="${description}" escapeXml="false"/>
        </div>
      </c:if>
    </jsp:body>
  </crs:promotionalContentWrapper>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/promo/gadgets/linkedImageText.jsp#3 $$Change: 635816 $--%>
