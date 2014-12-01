<%--
  Advanced inputs for the Search Testing.
  
  Commented out part of this page can be used as an example of how to add custom input controls to the JSP
  
  The following request-scoped variables are expected to be set:
  
  @param  formHandlerPath Nucleus path to the SearchTestingFormHandler
  @param  formHandler     SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/advancedInputs.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<%-- dspel:page>

<c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
<c:set var="formHandler"     value="${requestScope.formHandler}"/>

Advanced Inputs<br />
My Control: <dspel:input id="myControl" type="text" bean="${formHandlerPath}.advancedParameters.myControl" />
</dspel:page --%>

</dsp:page>

<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/advancedInputs.jsp#2 $$Change: 651448 $--%>
