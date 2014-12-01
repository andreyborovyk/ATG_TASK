<%@ page contentType="application/json; charset=UTF-8" %>
<dsp:page>
<%--
     This page renders the contents of the cart as JSON data. 
     This is the top-level container page that just sets the appropriate mime type and includes
     the real data-generating page
--%>

  <dsp:include page="cartContentsData.jsp"/>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/cart/json/cartContents.jsp#3 $$Change: 635816 $--%>
