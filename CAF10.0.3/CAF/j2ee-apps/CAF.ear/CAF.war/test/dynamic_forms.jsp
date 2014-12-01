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
    <title>PPR Dynamic Forms Test Page</title>
    <script type="text/javascript" src='<c:out value="${CAFUIConfig.contextRoot}/scripts/atg-ui_common.js"/>' ></script>
    <script type="text/javascript" src='<c:out value="${CAFUIConfig.contextRoot}/scripts/partial_page_renderer.js"/>' ></script>
    <script type="text/javascript">
      function testDynamic() {
        var ppr = window.__ppr_dynamicFormsAction;
        if (ppr) {
          // create a statement protocol
          // with fixed size delimiters, you can avoid an expensive split operation
          // just substring(0, 0) to get the index, substring(2, 4) to get the type, substring(6) to get the text
          // and assemble objects as needed
          ppr.addForm("statements", "0:typ:goal");
          ppr.addForm("statements", "0:sec:none");
          ppr.addForm("statements", "0:txt:Goal text 1");

          ppr.addForm("statements", "1:typ:fix");
          ppr.addForm("statements", "1:sec:none");
          ppr.addForm("statements", "1:txt:Fix text 2");

          ppr.transact();
        }
      }
    </script>
  </head>
  <body>
    <div id="test">
    </div>
    <caf:history/>
    <caf:action actionId="dynamicFormsAction"
                bean="/atg/web/formhandlers/test/PartialPageRendererTestFormHandler"
                debugLevel="verbose"
                debugElementId="debugOutput"
                errorUrl="/CAF/test/dynamic_forms_renderer.jsp"
                handler="dynamicTest"
                method="post"
                protocol="formhandlers"
                recordHistory="true"
                successUrl="/CAF/test/dynamic_forms_renderer.jsp"
                url="${pageContext.request.contextPath}${pageContext.request.servletPath}">
      <%-- This is necessary to tweak the nucleus state --%>
      <caf:inputElement property="statements"
                        value=""/>
    </caf:action>
    <table border=0
           cellpadding=0
           cellspacing=0>
      <tr>
        <td class="cell">
          <button id="testDynamicButton"
                  name="testDynamicButton"
                  onclick="javascript:testDynamic()"
                  size=8>Test</button>
        </td>
      </tr>
    </table>
    <table border=0
           cellpadding=0
           cellspacing=0
           width="100%">
      <tr>
        <td>
          <div id="divTestOutput">
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
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/dynamic_forms.jsp#2 $$Change: 651448 $--%>
