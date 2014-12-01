<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<!-- begin footer -->
<div style="float: right; padding-right: 20px; padding-top: 5px; border-top: solid 1px #D8D8D8;" class="footer">
  <a href="#" onclick="popup('<c:url context="/atg" value="/about.jsp"/>', 'AboutWindow', 400, 440); return false;">
    <fmt:message key="product-name" bundle="${bundle}"/>
  </a>&nbsp;
</div>

<div id="footer" class="footer">
  <fmt:message key="copyright" bundle="${bundle}"/>
</div> <!-- end footer -->

</dsp:page>

<%-- @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/includes/footer.jsp#2 $$Change: 651448 $--%>
