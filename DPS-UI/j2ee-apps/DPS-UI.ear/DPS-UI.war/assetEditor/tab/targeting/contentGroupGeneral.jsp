<%--
  General info tab for content group assets.

  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <%-- Display selected form handler and Nucleus properties --%>
  <%-- NB: We need to figure out how to use view mapping here, too! --%>

  <%-- amitj: Include the Component Editor to show Content Source and Content Type as drop down lists --%> 
  <dspel:include page="/assetEditor/tab/targeting/componentProperties.jsp">
    <dspel:param name="tabName" value="contentGroupGeneral"/>
  </dspel:include>
 
</dspel:page>
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/targeting/contentGroupGeneral.jsp#2 $$Change: 651448 $--%>
