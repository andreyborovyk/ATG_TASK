<dsp:page>

  <%-- This gadget renders the template URL of a promotionalContent item.

       Parameters:
        - promotionalContent - The promotionalContent repository item
  --%>
 
  <dsp:getvalueof var="pageurl" idtype="java.lang.String" param="promotionalContent.template.url"/>
  <dsp:include page="${pageurl}">
    <dsp:param name="promotionalContent" param="promotionalContent"/>
  </dsp:include>

</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/promo/gadgets/promotionalContentTemplateRenderer.jsp#3 $$Change: 635816 $--%>
