<%--
  This tag inserts hidden input field what is required for properly setting of form handler's property.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/setHiddenField.tag#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ tag body-content="empty" %>
<%@ include file="/templates/top.tagf" %>
<%@ attribute name="beanName" required="true" rtexprvalue="false" %>
<%@ attribute name="name" required="true" rtexprvalue="false" %>

<d:input type="hidden" bean="${beanName}.${name}" name="${name}" id="${name}hidden"/>
<script type="text/javascript">
  var inputHidden = document.getElementById("${name}hidden");
  inputHidden.parentNode.removeChild(inputHidden);
</script>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/setHiddenField.tag#2 $$Change: 651448 $--%>
