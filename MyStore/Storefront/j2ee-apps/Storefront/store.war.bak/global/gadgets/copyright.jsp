<dsp:page>

  <%-- 
    This page displays the copyright information of the Store
    Parameters - 
    - copyrightDivId
  --%>
  <dsp:getvalueof var="copyrightDivId" param="copyrightDivId"/>

  <div id="<c:out value='${copyrightDivId}'/>">
    <fmt:message key="common.copyright"/>
  </div>

</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/global/gadgets/copyright.jsp#3 $$Change: 635816 $ --%>