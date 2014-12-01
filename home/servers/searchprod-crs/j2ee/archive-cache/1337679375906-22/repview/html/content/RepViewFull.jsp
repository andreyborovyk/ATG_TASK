<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>

<%--
   Repository View Gear
   gearmode = content 
   displaymode = full
  
   This page includes the appropriate page fragment for 
   any of the following functions based on the value of
   request parameter "rpvmode":
      list items
      display single item

   For displaying one item, we include the configuable relative url, so that 
   the user may specify a customized page or use the one included with this
   gear.  See this gear's configuration guide for more information.

   NOTE use jsp:include here because it keeps the page size small
--%>


<dsp:page>

<core:urlParamValue id="rpvmode" param="rpvmode">
  <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param name="value" value="<%= rpvmode %>" />

    <dsp:oparam name="oneItem">
       <jsp:include page="/html/content/displayOneItem.jsp" flush="true"/>
    </dsp:oparam>

    <dsp:oparam name="list">
      <jsp:include page="/html/content/fullList.jsp" flush="true"/>
    </dsp:oparam>

  </dsp:droplet>
</core:urlParamValue>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/content/RepViewFull.jsp#2 $$Change: 651448 $--%>
