<%--
  This page displays registration form
  and list of benefits for registered customers.
--%>

<dsp:page>
  <crs:pageContainer index="false" follow="false" bodyClass="atg_store_pageRegistration atg_store_rightCol">    
    <jsp:body>
      <dsp:importbean bean="/atg/store/profile/RegistrationFormHandler"/>
      <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
      <div class="atg_store_nonCatHero">
      <dsp:include page="/global/gadgets/pageIntro.jsp" flush="true">
        <dsp:param name="divId" value="atg_store_registerIntro" />
        <dsp:param name="titleKey" value="myaccount_registration.title" />        
      </dsp:include>
      </div>
      <dsp:getvalueof var="regFormExceptions" vartype="java.lang.Object" bean="RegistrationFormHandler.formExceptions"/>
      <c:if test="${not empty regFormExceptions}">
          <dsp:include page="gadgets/myAccountErrorMessage.jsp">
            <dsp:param name="formHandler" bean="RegistrationFormHandler"/>
          </dsp:include>         
      </c:if>
          
      <dsp:include page="gadgets/register.jsp" flush="true" >
        <dsp:param name="restrictionDroplet" value="/atg/store/droplet/CountryListDroplet"/>
      </dsp:include>
          
      <dsp:include page="gadgets/benefits.jsp"/>
    </jsp:body>
  </crs:pageContainer>  
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/myaccount/registration.jsp#3 $$Change: 635816 $--%>
