<dsp:page>

  <dsp:getvalueof var="displayQuantity" param="displayQuantity"/>
  <dsp:getvalueof var="quantity" param="quantity"/>
  <dsp:getvalueof var="price" param="price"/>
  <dsp:getvalueof var="oldPrice" param="oldPrice"/>
  
  <p class="price">
    <c:if test="${displayQuantity}">
      <fmt:formatNumber value="${quantity}" type="number"/>
      <fmt:message key="common.atRateOf"/>
     </c:if>
     
     <c:choose>
       <c:when test="${empty oldPrice}">
         <span>
           <dsp:include page="/global/gadgets/formattedPrice.jsp">
             <dsp:param name="price" value="${price }"/>
           </dsp:include>
         </span>
       </c:when>
       <c:otherwise>
         <span class="atg_store_newPrice">
           <dsp:include page="/global/gadgets/formattedPrice.jsp">
             <dsp:param name="price" value="${price}"/>
           </dsp:include>
         </span>
         <span class="atg_store_oldPrice">
           <fmt:message key="common.price.old"/>
           <del>
             <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" value="${oldPrice}"/>
            </dsp:include>
           </del>
         </span>
       </c:otherwise>
     </c:choose>
  </p>

</dsp:page>

<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/cart/gadgets/displayItemPrice.jsp#3 $$Change: 635816 $--%>