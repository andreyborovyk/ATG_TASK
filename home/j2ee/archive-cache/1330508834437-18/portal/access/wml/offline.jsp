<%@ page contentType="text/vnd.wap.wml" import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM/DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

<fmt:setBundle var="accessbundle" basename="atg.portal.access"/>
<wml>
    <card id="offline" title="<fmt:message key="offline-title" bundle="${accessbundle}"/>">
      <p align="center" mode="nowrap">
        <big>
          <b><fmt:message key="offline-heading" bundle="${accessbundle}"/></b>
        </big>
      </p>
      <p mode="wrap"><fmt:message key="offline-message" bundle="${accessbundle}"/></p>
    </card>
</wml>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/access/wml/offline.jsp#2 $$Change: 651448 $--%>
