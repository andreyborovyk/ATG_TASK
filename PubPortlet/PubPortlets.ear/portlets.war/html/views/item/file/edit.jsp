<%--
  Default file asset edit view
  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<!-- Begin BIZUI's /views/item/file/edit.jsp -->

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
 <dspel:page>
<fmt:bundle basename="atg.epub.ViewResources">

  <div id="nonTableContent" style="margin-top: -10px;">
    <c:if test="${view.attributes.title}">
      <b><c:out value="${view.attributes.title}" escapeXml="false"/></b><br />
    </c:if>
    <c:if test="${view.attributes.helpTextKey ne null}">
    <%-- help bubble --%>
    <p id="toggleDetails3">
      <a href="#" onclick="displayToggle('box3','box','noBox');">&nbsp;</a>
    </p>
    <div class="positionBox">
      <div class="noBox" id="box3">
       <h3><fmt:message key="help-text"/></h3>
        <p><fmt:message key="${view.attributes.helpTextKey}"/></p>
        <div>
          <a class="more" href="#" id="closePageInfo3" onclick="displayToggle('box3','box','noBox');">
            <fmt:message key="helpBubble.close"/>
          </a>
        </div>
      </div>
    </div>
      </c:if>
      </p>
          <div id="formKey" style="margin-top: -10px;">
      <table border="0" cellpadding="0" cellspacing="3" style="float: right; margin-left: 45px;">
        <tr>
          <td class="formLabel formLabelRequired formLabelKey">&nbsp;<fmt:message key="required-field"/></td>
        </tr>
      </table>

    </div>

    <c:forEach items="${view.propertyMappings}" var="mpv">
      <c:set value="${mpv}" var="mpv" scope="request"/>
      <c:if test="${mpv.value.mapped}">
        <c:set value="${mpv.value}" var="mpv" scope="request"/>
        <c:if test="${ !empty mpv.value.uri}">
          <c:out value="<!-- Begin jsp URI ${mpv.value.uri} -->" escapeXml="false"/>
          <dspel:include otherContext="${mpv.value.contextRoot}" page="${mpv.value.uri}"/>
          <c:out value="<!-- End jsp URI ${mpv.value.uri} -->" escapeXml="false"/>
          <br />
        </c:if>
      </c:if>
    </c:forEach>
  </div>
</fmt:bundle>
</dspel:page>

<!-- End BIZUI's /views/item/file/edit.jsp -->
<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/item/file/edit.jsp#2 $$Change: 651448 $--%>
