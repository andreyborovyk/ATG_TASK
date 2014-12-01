<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="caf"   uri="http://www.atg.com/taglibs/caf" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<dspel:importbean var="testFormHandler"
                  bean="/atg/web/formhandlers/test/PartialPageRendererTestFormHandler" />

<caf:outputXhtml targetId="divTestOutput">
  ...
</caf:outputXhtml>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/dynamic_forms_renderer.jsp#2 $$Change: 651448 $--%>
