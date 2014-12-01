<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="caf"   uri="http://www.atg.com/taglibs/caf" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page import="atg.web.formhandlers.test.PartialPageRendererModel,
                 javax.servlet.http.HttpSession"%>

<%
  PartialPageRendererModel model = (PartialPageRendererModel)session.getAttribute("model");
  if (model == null) {
    model = new PartialPageRendererModel();
    session.setAttribute("model", model);
  }
%>

<dspel:importbean var="CAFUIConfig" bean="/atg/web/ui/UIConfiguration"/>

<html>
  <head>
    <title>PPR Test Page</title>
    <script type="text/javascript" src='<c:out value="${CAFUIConfig.contextRoot}/scripts/atg-ui_common.js"/>' ></script>
    <script type="text/javascript" src='<c:out value="${CAFUIConfig.contextRoot}/scripts/partial_page_renderer.js"/>' ></script>
  </head>
  <body>
    <div id="test">
    </div>
    <caf:history/>
    <caf:action actionId="languageListElement"
                bean="/atg/web/formhandlers/test/PartialPageRendererTestFormHandler"
                debugLevel="verbose"
                debugElementId="debugOutput"
                errorUrl="/test/test_renderer.jsp"
                handler="getLocalesForLanguage"
                method="post"
                protocol="formhandlers"
                recordHistory="true"
                successUrl="/test/test_renderer.jsp"
                url="${pageContext.request.contextPath}${pageContext.request.servletPath}">
      <caf:hideElement elementId="divHide"/>
      <caf:inputElement accessScript="document.getElementById('listField').value"
                        isHistoryTitle="true"
                        property="language"/>
      <caf:showElement elementId="divShow"/>
    </caf:action>
    <table border=0
           cellpadding=0
           cellspacing=0>
      <tr>
        <td class="cell" rowspan=2>
          <select id="listField"
                  name="listField"
                  onchange="if (__ppr_languageListElement) { __ppr_languageListElement.transact(); }"
                  size=8>
            <c:forEach var="languageEntry" items="${model.languages}">
              <option value="<c:out value="${languageEntry.value.displayName}"/>"><c:out value="${languageEntry.value.displayName}"/></option>
            </c:forEach>
          </select>
        </td>
      </tr>
    </table>
    <div id="divShow" style="display:none;text-align:center;vertical-align:middle;width:100%;">
      <img alt="Please wait..."
           src="../images/loading.gif"/>
    </div>
    <div id="divHide">
      <table border=0
             cellpadding=0
             cellspacing=0
             width="100%">
        <tr>
          <td>
            <div id="divLocalesForLanguage1">
            </div>
          </td>
          <td>
            <div id="divLocalesForLanguage2">
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <div id="divLocalesForLanguage3">
            </div>
          </td>
          <td>
            <div id="divLocalesForLanguage4">
            </div>
          </td>
        </tr>
      </table>
      <h3>
        Debugging Output
      </h3>
      <div id="debugOutput">
        [Debugging output goes here...]
      </div>
    </div>
  </body>
</html>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/partial_page_renderer.jsp#2 $$Change: 651448 $--%>
