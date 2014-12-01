<%--
  Default property editor for enumerated values.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view
  
  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/property/facetValueRangesEdit.jsp#2 $$Change $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>
  <dspel:importbean var="config"
                    bean="/atg/commerce/search/web/Configuration"/>
  <fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
  <c:set var="formHandler"     value="${requestScope.formHandler}"/>

  <%-- Get an ID for the property's input element --%>  
  <c:set var="inputId" value="propertyValue_${propertyView.uniqueId}"/>
  <c:if test="${not empty requestScope.uniqueAssetID}">
    <c:set var="inputId" value="propertyValue_${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>  
  </c:if>

  <%-- determine which mode we're in --%>
  <c:choose>
    <c:when test="${view.itemMapping.mode == 'AssetManager.edit'}">
      <c:set var="disabled" value="false"/>
    </c:when>
    <c:otherwise>
      <c:set var="disabled" value="true"/>
      <c:set var="disabledText" value="disabled"/>
    </c:otherwise>
  </c:choose>
  
  <%-- Get the property type --%>
  <dspel:getvalueof var="propertyType" bean="${formHandlerPath}.value.propertyType"/>

  <%-- Iterate over each of the options in the enumeration descriptor --%>
  <ul class="formVerticalList selectionBullets">   
    <li>
      <dspel:input type="radio" value="false" iclass="radioBullet" bean="${propertyView.formHandlerProperty}"
                   onchange="markAssetModified()"
                   oninput="formFieldModified()"
                   onpropertychange="formFieldModified()"
                   id="range_false"
                   disabled="${disabled}" />
      <fmt:message key="noRanges" bundle="${bundle}"/>
    </li>
    <li>
      <dspel:input type="radio" value="free" iclass="radioBullet" bean="${propertyView.formHandlerProperty}"
                   onchange="markAssetModified()"
                   oninput="formFieldModified()"
                   onpropertychange="formFieldModified()"
                   id="range_free"
                   disabled="${disabled}" /> 
      <fmt:message key="dynamicRanges" bundle="${bundle}"/>:
      <ul>
        <li>
			<c:set var="propertyName" value="desired"/>
        	<c:if test="${not empty formHandler.propertyExceptions[propertyName] and formHandler.value.RANGE == 'free'}">
  				<span style="color: #ff0000">
    				<c:out value="${formHandler.propertyExceptions[propertyName].message}"/>
  				</span>
  				<br>
			</c:if>
          <fmt:message key="maxRanges" bundle="${bundle}"/>: <input type="text" id="free_desired" oninput="validatePositiveInteger(this)" onpropertychange="formFieldModified()" class="formTextField numberSmall" <c:out value='${disabledText}'/>
        </li>
        <li>
			<c:set var="propertyName" value="minimum"/>
        	<c:if test="${not empty formHandler.propertyExceptions[propertyName] and formHandler.value.RANGE == 'free'}">
  				<span style="color: #ff0000">
    				<c:out value="${formHandler.propertyExceptions[propertyName].message}"/>
  				</span>
  				<br>
			</c:if>
          <fmt:message key="minValuePerRange" bundle="${bundle}"/>: <input type="text" id="free_minimum" oninput="validatePositiveInteger(this)" onpropertychange="formFieldModified()" class="formTextField numberSmall" <c:out value='${disabledText}'/> />
        </li>
        <li>
			<c:set var="propertyName" value="increment"/>
        	<c:if test="${not empty formHandler.propertyExceptions[propertyName] and formHandler.value.RANGE == 'free'}">
  				<span style="color: #ff0000">
    				<c:out value="${formHandler.propertyExceptions[propertyName].message}"/>
  				</span>
  				<br>
			</c:if>
          <c:choose>
            <c:when test="${propertyType == 'integer' || propertyType == 'float'}">
              <fmt:message key="minNumberSpanInRange" bundle="${bundle}"/>: 
            </c:when>
            <c:otherwise>
              <fmt:message key="minLetterSpanInRange" bundle="${bundle}"/>: 
            </c:otherwise>
          </c:choose>
          <input type="text" id="free_increment" oninput="validatePositiveInteger(this)" onpropertychange="formFieldModified()" class="formTextField numberSmall" <c:out value='${disabledText}'/> />
        </li>
        <%-- Only render if this is a number facet --%>
        <c:if test="${propertyType == 'integer' || propertyType == 'float'}">
          <li>
            <c:set var="propertyName" value="round"/>
        	<c:if test="${not empty formHandler.propertyExceptions[propertyName]}">
  				<span style="color: #ff0000">
    				<c:out value="${formHandler.propertyExceptions[propertyName].message}"/>
  				</span>
  				<br>
			</c:if>
            <fmt:message key="roundValues" bundle="${bundle}"/>: <dspel:input id="round_values" type="text" iclass="formTextField numberSmall" bean="${formHandlerPath}.value.round" oninput="validatePositiveInteger(this)" onpropertychange="formFieldModified()"/>
          </li>
        </c:if>
      </ul>
    </li>
    <%-- Only render if this is a number facet --%>
    <c:if test="${propertyType == 'integer' || propertyType == 'float'}">
      <li>
        <dspel:input type="radio" value="fixed" iclass="radioBullet" bean="${propertyView.formHandlerProperty}"
                     onchange="markAssetModified()"
                     oninput="formFieldModified()" 
                     onpropertychange="formFieldModified()"
                     id="range_fixed"
                     disabled="${disabled}" />
        <fmt:message key="fixedRanges" bundle="${bundle}"/>:
        <ul>
          <li>
			<c:set var="propertyName" value="desired"/>
        	<c:if test="${not empty formHandler.propertyExceptions[propertyName] and formHandler.value.RANGE == 'fixed'}">
  				<span style="color: #ff0000">
    				<c:out value="${formHandler.propertyExceptions[propertyName].message}"/>
  				</span>
  				<br>
			</c:if>
            <%-- TODO: change out these resourced values --%>
            <fmt:message key="numberRanges" bundle="${bundle}"/>: <input type="text" id="fixed_desired" oninput="validatePositiveInteger(this)" onpropertychange="formFieldModified()" class="formTextField numberSmall" <c:out value='${disabledText}'/> />
          </li>
          <li>
			<c:set var="propertyName" value="increment"/>
        	<c:if test="${not empty formHandler.propertyExceptions[propertyName] and formHandler.value.RANGE == 'fixed'}">
  				<span style="color: #ff0000">
    				<c:out value="${formHandler.propertyExceptions[propertyName].message}"/>
  				</span>
  				<br>
			</c:if>
            <%-- TODO: change out these resourced values --%>
            <fmt:message key="numericalSize" bundle="${bundle}"/>: <input type="text" id="fixed_increment" oninput="validatePositiveInteger(this)" onpropertychange="formFieldModified()" class="formTextField numberSmall" <c:out value='${disabledText}'/> />
          </li>
        </ul>
      </li>
      <li>
        <dspel:input type="radio" value="explicit" iclass="radioBullet" bean="${propertyView.formHandlerProperty}"
                     onchange="markAssetModified()"
                     oninput="formFieldModified()" 
                     onpropertychange="formFieldModified()"
                     id="range_explicit"
                     disabled="${disabled}" />
        <fmt:message key="specifiedRanges" bundle="${bundle}"/>:
        <dspel:include page="numericalRangeEdit.jsp"/>
      </li>
    </c:if>
  </ul>

  <dspel:input type="hidden" id="desired"   bean="${formHandlerPath}.value.desired"/>
  <dspel:input type="hidden" id="increment" bean="${formHandlerPath}.value.increment"/>
  <dspel:input type="hidden" id="minimum"   bean="${formHandlerPath}.value.minimum"/>
  <dspel:input type="hidden" id="propertyType"  bean="${formHandlerPath}.facetingPropertyType"  value="${propertyType}"/>

  <script type="text/javascript">
    function initializeFields ()
    {
      var desired   = "<dspel:valueof bean='${formHandlerPath}.value.desired'/>";
      var increment = "<dspel:valueof bean='${formHandlerPath}.value.increment'/>";
      var minimum   = "<dspel:valueof bean='${formHandlerPath}.value.minimum'/>";
      var range_free = document.getElementById("range_free");
      if (range_free.checked) {
        document.getElementById("free_desired").value   = desired;
        document.getElementById("free_increment").value = increment;
        document.getElementById("free_minimum").value   = minimum;
      }
      var range_fixed = document.getElementById("range_fixed");
      if (range_fixed != null && range_fixed.checked) {
        document.getElementById("fixed_desired").value = desired;
        document.getElementById("fixed_increment").value = increment;
      }
    }
    
    function submitFields ()
    {
      var range_free = document.getElementById("range_free");
      if (range_free.checked) {
        var desired   = document.getElementById("free_desired").value;
        var increment = document.getElementById("free_increment").value;
        var minimum   = document.getElementById("free_minimum").value;
        document.getElementById("desired").value   = desired;
        document.getElementById("increment").value = increment;
        document.getElementById("minimum").value   = minimum;
      }
      var range_fixed = document.getElementById("range_fixed");
      if (range_fixed != null && range_fixed.checked) {
        var desired   = document.getElementById("fixed_desired").value;
        var increment = document.getElementById("fixed_increment").value;
        document.getElementById("desired").value = desired;
        document.getElementById("increment").value = increment;
      }
    }

    function verifyPropertyType ()
    {
      var propertyType = "<dspel:valueof bean='${formHandlerPath}.value.propertyType'/>";
      var range_false = document.getElementById("range_false");
      var range_free  = document.getElementById("range_free");
      var range_fixed = document.getElementById("range_fixed");
      if (propertyType != "integer" &&
          propertyType != "float")
      {
        if (range_fixed == null) {
          if (!range_false.checked &&
              !range_free.checked)
          {
            range_false.checked = true;
          }
        }
      }
    }

    

    function validatePositiveInteger(pText)
    {
    	if (!IsPositiveNumeric(pText.value)) {
    		alert("'"+pText.value+"' is an invalid entry - Please enter a positive number value");
    		document.getElementById(pText.id).value = "";
    	} else {
    		formFieldModified();
    	}
    }


    function IsPositiveNumeric(pText)
    {
       var ValidChars = "0123456789.";
       var IsNumber = true;
       var Char;
     
       for (i = 0; i < pText.length && IsNumber == true; i++) { 
          Char = pText.charAt(i); 
          if (ValidChars.indexOf(Char) == -1) {
             IsNumber = false;
          }
       }
       return IsNumber;
    }
        
    
    registerOnLoad(initializeFields);
    registerOnSubmit(verifyPropertyType);
    registerOnSubmit(submitFields);
  </script>

</dspel:page>

<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/property/facetValueRangesEdit.jsp#2 $$Change: 651448 $--%>
