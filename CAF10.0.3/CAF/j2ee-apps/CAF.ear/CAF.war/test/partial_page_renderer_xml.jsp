<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="caf"   uri="http://www.atg.com/taglibs/caf" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page import="atg.web.formhandlers.test.PartialPageRendererModel,
                 javax.servlet.http.HttpSession"%>

<%
  PartialPageRendererModel model = (PartialPageRendererModel)session.getAttribute("model");
  if (model == null)
  {
    model = new PartialPageRendererModel();
    session.setAttribute("model", model);
  }
%>

<dspel:importbean var="CAFUIConfig" bean="/atg/web/ui/UIConfiguration"/>

<html>
  <head>
    <title>Test Page for Partial Page Renderer</title>
    <script type="text/javascript" src='<c:out value="${CAFUIConfig.contextRoot}/scripts/atg-ui_common.js"/>' ></script>
    <script type="text/javascript" src='<c:out value="${CAFUIConfig.contextRoot}/scripts/partial_page_renderer.js"/>' ></script>
  </head>
  <body>
    <caf:action actionId="languageListElementXml"
                bean="/atg/web/formhandlers/test/PartialPageRendererTestFormHandler"
                debugLevel="verbose"
                debugElementId="debugOutput"
                errorUrl="/test/test_renderer_xml.jsp"
                handler="getLocalesForLanguageXml"
                method="post"
                protocol="formhandlers"
                successUrl="/test/test_renderer_xml.jsp"
                url="${pageContext.request.contextPath}${pageContext.request.servletPath}">
      <caf:inputElement accessScript="document.getElementById('listField').value"
                        property="language"/>
      <caf:outputXml accessScript="innerHTML"
                     targetId="divLocalesForLanguage1"
                     xsltUrl="${pageContext.request.contextPath}/test/localesForLanguage.xsl"/>
      <caf:outputXml accessScript="innerHTML"
                     targetId="divLocalesForLanguage2"
                     xsltUrl="${pageContext.request.contextPath}/test/localesForLanguage.xsl"/>
    </caf:action>
    <table border=0
           cellpadding=0
           cellspacing=0>
      <tr>
        <td class="cell" rowspan=2>
          <select id="listField"
                  name="listField"
                  onchange="<caf:transact actionId='languageListElementXml'/>"
                  size=8>
            <c:forEach var="languageEntry" items="${model.languages}">
              <option value="<c:out value="${languageEntry.value.displayName}"/>"><c:out value="${languageEntry.value.displayName}"/></option>
            </c:forEach>
          </select>
        </td>
      </tr>
    </table>
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
    </table>
    <h3>
      Debugging Output
    </h3>
    <div id="debugOutput">
      [Debugging output goes here...]
    </div>
  </body>
</html>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/partial_page_renderer_xml.jsp#2 $$Change: 651448 $--%>
