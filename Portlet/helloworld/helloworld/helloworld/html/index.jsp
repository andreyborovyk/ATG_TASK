<%@ page errorPage="/error.jsp" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"   %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<%-- Import a bean from nucleus  --%>
<dspel:importbean bean="/atg/portlet/helloworld/HelloWorld"/>
<dspel:importbean bean="/atg/portlet/helloworld/HelloWorldFormHandler"/>

<%-- Uses the resource bundle defined in web.xml for all messages --%>

<dspel:page>
  <c:set var="actionURL"><portlet:actionURL/></c:set>

  <%-- Render the value of the HelloWorld.message property  --%>
  <p class="portlet-msg-alert"><dspel:valueof bean="HelloWorld.message" /></p>

  <%-- HelloWorldFormHandler  --%>
  <dspel:form method="post" action="${actionURL}">
    <table>
      <tr>
        <td><label class="portlet-form-label" for="message"><fmt:message key="label-message"/></label></td>
        <td><dspel:input iclass="portlet-form-input-field" type="text" bean="HelloWorldFormHandler.message" id="message"/></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td><dspel:input iclass="portlet-form-button" type="submit" bean="HelloWorldFormHandler.update"/></td>
      </tr>
    </table>
  </dspel:form>

</dspel:page>
<%-- @version $Id: //app/portal/version/10.0.3/portlet/helloworld/helloworld/html/index.jsp#2 $$Change: 651448 $--%>
