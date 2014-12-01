<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<tr>
  <td dojoattachpoint="propertyName">[[Property Name]]</td>
  <td dojoattachpoint="sliderView">
  </td>
  <td>
      <span dojoattachpoint="propertyWeightNumeric" style="float: left; width: 3em; text-align:right">[[XX %]]</span>
      <span dojoattachpoint="propertyWeightGraphic" class="weightBar"></span>
  </td>
  <td>
    <a href="[[URL For the link]]" dojoattachpoint="propertyValueRankLink">[[text for the link]]</a>
  </td>
</tr>

</dsp:page>

<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/javascript/dijit/propPriority/templates/ppWeightSet.jsp#2 $$Change: 651448 $--%>
