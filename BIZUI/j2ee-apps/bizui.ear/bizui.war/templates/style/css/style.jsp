<%@ page contentType="text/css" %>
<%@ page import="java.io.*,java.util.*,javax.servlet.http.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>

<dsp:page>


<%
  int ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;
  int ONE_DAY_SECONDS = 60 * 60 * 24;
  long now = System.currentTimeMillis();
  ((HttpServletResponse)response).setDateHeader("Last-Modified", now);
  now += ONE_DAY_MILLIS; // 7 days from now
  ((HttpServletResponse)response).setDateHeader("Expires", now);
  ((HttpServletResponse)response).setHeader("Cache-Control", "max-age=" + ONE_DAY_SECONDS);
%>
<c:set var="contextPath">
<%= request.getContextPath() %>
</c:set>

/*<group=tag selectors>*/

<%
  String b = request.getHeader("User-Agent");
  if (b != null && b.indexOf("MSIE") != -1) pageContext.setAttribute("isIE", "true");
%>

html {
  height: 100%;
  <c:if test="${!isIE}">overflow: auto;</c:if>
}

BODY {
  height: 100%;
  <c:if test="${!isIE}">overflow: auto;</c:if>
  font: 75% Arial, Verdana, Helvetica, sans-serif;
  margin: 0px;
  padding: 0px;
  line-height:  13px;
  background: #C1C1C1 url(../../../images/BCC_home/bg_body.gif) repeat-x right top;
}

BODY.popup{
  background-color: #FFF;
  background-image: none;
  border: none;
  font-size: 75%;
  width: 100%;
}

BODY.popup p{
  padding-bottom: 13px;
  text-align: left;
}


BODY.assetBrowser #nonTableContent{
  background-color: #FFF;
  margin-top: 0px;
  padding: 10px;
}

BODY.assetBrowser #nonTableContent2{
  background-color: #CCCCCC;
  margin-top: 0px;
  padding: 10px;
}

BODY.actionContent {
  background-color: #ccc;
  background-image: none;
  border: none;
  text-align: left;
  font-size: 75%;
}

BODY.login {
  background-image: url(../../../images/loginBack.gif);
  background-repeat: repeat-x;
  background-position: top;
  background-attachment: fixed;
  background-color: #E8E8EA;
}

p { font-size: 0.5em; }
img {
  border: none;
  }

A:Link {
  color: #666;
  text-decoration: none;
}

A:Visited {
  color: #666;
  text-decoration: none;
}

A:Hover {
  color: #000;
  text-decoration: none;
}

A:Active {
  color: #000;
  text-decoration: none;
}


A.stepComplete:Link {
  color: #97B8D9;
  text-decoration: none;
  line-height:  1em;
}

A.stepComplete:Visited {
  color: #97B8D9;
  text-decoration: none;
  line-height:  1em;
}

A.stepComplete:Hover {
  color: #003870;
  text-decoration: none;
  line-height:  1em;
}

A.stepComplete:Active {
  color: #97B8D9;
  text-decoration: none;
  line-height:  1em;
}


A.current:Link {
  color: #00AC10;
  text-decoration: none;
  line-height:  8px;
}

A.current:Visited {
  color: #00AC10;
  text-decoration: none;
  line-height:  8px;
}

A.current:Hover {
  color: #007B0B;
  text-decoration: none;
  line-height:  8px;
}

A.current:Active {
  color: #00FF17;
  text-decoration: none;
  line-height:  8px;
}

A.pending:Link {
  color: #878787;
  text-decoration: none;
  line-height:  8px;
}

A.pending:Visited {
  color: #878787;
  text-decoration: none;
  line-height:  8px;
}

A.pending:Hover {
  color: #545454;
  text-decoration: none;
  line-height:  8px;
}

A.pending:Active {
  color: #CCC;
  text-decoration: none;
  line-height:  8px;
}

A.black:Link {
  color: #545454;
  text-decoration: none;
  line-height:  8px;
}

A.black:Visited {
  color: #545454;
  text-decoration: none;
  line-height:  8px;
}

A.black:Hover {
  color: #000000;
  text-decoration: none;
  line-height:  8px;
}

A.black:Active {
  color: #FFFFFF;
  text-decoration: none;
  line-height:  8px;
}

A.yellow:Link {
  color: #F9EB6A;
  text-decoration: none;
  line-height:  8px;
}

A.yellow:Visited {
  color: #F9EB6A;
  text-decoration: none;
  line-height:  8px;
}

A.yellow:Hover {
  color: #FFF000;
  text-decoration: none;
  line-height:  8px;
}

A.yellow:Active {
  color: #FFFDD7;
  text-decoration: none;
  line-height:  8px;
}

A.alert:Link {
  color: #f00;
  text-decoration: none;
  line-height:  14px;
}

A.alert:Visited {
  color: #F00;
  text-decoration: none;
  line-height:  14px;
}

A.alert:Hover {
  color: #900;
  text-decoration: none;
  line-height:  14px;
}

A.alert:Active {
  color: #F03;
  text-decoration: none;
  line-height:  14px;
}

A.fixed:Link {
  color: #666;
  text-decoration: none;
  line-height:  8px;
}

A.fixed:Visited {
  color: #666;
  text-decoration: none;
  line-height:  8px;
}

A.fixed:Hover {
  color: #333;
  text-decoration: none;
  line-height:  8px;
}

A.fixed:Active {
  color: #999;
  text-decoration: none;
  line-height:  8px;
}

form {
  display: inline;
  }

UL {
  list-style-type: none;
  margin: 0px;
  padding: 0px;
  margin-bottom: 10px;
}

H1 {
  display: block;
  font-family: "Trebuchet MS",verdana,tahoma,sans-serif;
  font-size: 1.6em;
  font-weight: 900;
  color: #585858;
  margin: 0px;
  padding: 10px 0px 0px 12px;
  background-color: #FFF;
}

H2 {
  display: block;
  font-family: "Trebuchet MS",verdana,tahoma,sans-serif;
  font-size: 1.2em;
  font-weight: 800;
  color: #545454;
  margin: 0px;
  padding: 0px 5px 5px 5px;
}

H2 .pathSub{
  color: #999;
}

H2 EM {
  color: #000000;
  font-style: normal;
}

H2.seperate {
  border-bottom: 1px dotted #999;
  margin: 12px 0 8px 0;
  font-size: 1em;
  padding-left: 0;
}

H2.seperateLight {
  border-bottom: 1px dotted #dcdcdc;
  margin: 12px 0 8px 0;
  font-size: 1em;
  padding-left: 0;
}

H2.login {
  font-size: 1.2em;
  font-weight: bold;
  color: #333;
  margin: 6px 0 14px 45px;
  padding: 0;
}

.introHead {
  margin-left: -5px;
}

H3 {
  font-family: "Trebuchet MS",verdana,tahoma,sans-serif;
  font-size: .9em;
  margin: 0px;
  color: #545454;
  padding: 4px 3px 3px 18px;
  background-color: #F9F9F0;
  background-image: url(../../../images/bg_h3_2.gif);
  border-bottom: solid 1px #959595;
  border-top: 1px solid #E5E5E5;
  border-right: 1px solid #E5E5DF;
}


H3.homeDisplayResults {
  width: 378px;
  border-top: solid 1px #FFF;
}

H4 {
  font-family: "Trebuchet MS",verdana,tahoma,sans-serif;
  font-size: .9em;
  font-weight: 500;
  border-top: solid 1px #333;
  border-bottom: solid 1px #333;
  padding-top: 6px;
  padding-bottom: 8px;
  padding-left: 12px;
  white-space: nowrap;
  background-color: #545454;
  color: #FFFFFF;
  margin: 0px;
}


H4.home {
  font-size: 1em;
  font-weight: bold;
  height: 10px;
  padding-top: 8px;
  padding-bottom: 12px;
  background-image: url(../../../images/home_head_back.gif);
  border-top: solid 1px #D4D4D4;
  border-left: solid 1px #D4D4D4;
}


#portletRightBottom th {
  background-color: #EEEEEE;
}


P {
  margin-top: 0px;
  margin-bottom: 0px;
  font-size: 1em;
}

SUP#userName {
  position: relative;
  top:-5px;
  font-size: 1em;
  white-space: nowrap;
  margin: 0px;
  padding: 0px;
  margin-top: 6px;
  padding-right: 10px;
}

SELECT {
  width: 200px;
  font-size: .9em;
  text-align: left;
  vertical-align: middle;
  margin-top: 0px;
  margin-right: 10px;
  margin-bottom: 0px;
  margin-left: 5px;
}

SELECT.medium {
  width: 110px;
}

SELECT.small {
  width: 50px;
}

SELECT.table {
  width: 110px;
}

INPUT {
  font-size: .9em;
  margin: 0px 0px 0px 0px;
  vertical-align: middle;
}

textarea {


}

/*</group>*/

/*<group=class selectors>*/

.alternateRowHighlight {
  background-color: #F0F0F0;
}

.alternateRowHighlightBrowser {
  background-color: #F8F8F8;
}

.hierarchyTopIndent {
  margin-left: 30px;
}

.hierarchySubIndent {
  margin-left: 45px;
}

.hierarchySubIndentHead {
  margin-left: 45px;
}

.hierarchySubSubIndent {
  margin-left: 63px;
}

.hierarchySubTable {
  padding-left: 0px;
  height: 0px;
  padding: 0px;
  border-left: 0px;
  border-bottom: 0px;
}

.hierarchySub {
  background-image: url(../../../images/bg_hierarchySub.gif);
  background-repeat: no-repeat;
  background-position: 10px;
}

.hierarchyBottom {
  background-image: url(../../../images/bg_hierarchySub.gif);
  background-color: #E7E7DE;
  background-repeat: no-repeat;
  background-position: 10px;
}

.hierarchySubBottom {
  background-image: url(../../../images/bg_hierarchySub.gif);
  background-color: #E7E7DE;
  background-repeat: no-repeat;
  background-position: 0px;
}

.hierarchyT {
  background-image: url(../../../images/bg_hierarchySubT.gif);
  background-repeat: no-repeat;
  background-position: 10px 0px;
}

.hierarchySubT {
  background-image: url(../../../images/bg_hierarchySubT.gif);
  background-repeat: no-repeat;
  background-position: 0px;
}


.hierarchyLine {
  background-image: url(../../../images/bg_hierarchyLineSub.gif);
  background-repeat: repeat-y;
  background-position: 10px;
}

.hierarchyLineSub {
  background-image: url(../../../images/bg_hierarchyLineSub.gif);
  background-repeat: repeat-y;
  background-position: 0px;
}

.alternateRowHighlightBrowser {
  background-color: #F8F8F8;
}

.panelOpen {
  display: block;
}

.panelClosed {
  display: none;
}

.dataTable TD.current {
  border-left: solid 6px #00AC10;
  padding-left: 12px;
}

.dataTableNoGrid TD.flag {
  border-left: solid 6px #E25406;
  padding-left: 11px;
}

.dataTable TD .pending {
  color: #545454;
}

.backToProcess {
  font-size: .8em;
  width: 100px;
  background-color: #4C6279;
  border-top: solid 5px #FFF;
  border-right: none;
  border-bottom: solid 2px #364656;
  border-left: solid 5px #FFF;
  padding-left: 10px;
  padding-top: 3px;
  vertical-align: middle;
}

.backToPage {
  font-size: .8em;
  background-color: #FFF;
  border-bottom: solid 1px #B5B5B5;
  padding-left: 6px;
  padding-right: 10px;
  padding-top: 3px;
  vertical-align: middle;
  color: #4C6279;
  white-space: nowrap;
}

.panelTitle {
  background-color: #fff;
  border-top: solid 1px #FFF;
  border-left: solid 1px #FFF;
  border-bottom: solid 1px #DBDBDB;
  border-right: solid 1px #DBDBDB;
  font-size: .8em;
  font-weight: 500;
  padding-top: 7px;
  padding-bottom: 5px;
  vertical-align: middle;
  padding-left: 25px;
}

.panelTitleOn {
  background-color: #CDE0FA;
  border-top: solid 1px #FFF;
  border-left: solid 1px #FFF;
  border-bottom: solid 1px #DBDBDB;
  border-right: solid 1px #DBDBDB;
  font-size: .8em;
  font-weight: 500;
  padding-top: 7px;
  padding-bottom: 5px;
  vertical-align: middle;
  padding-left: 25px;
}


.colapsablePanelTitle {
  background-color: #EDEDE7;
  border-top: solid 1px #FFF;
  border-bottom: solid 1px #D2D2CB;
  font-size: .8em;
  font-weight: 500;
  padding-top: 7px;
  padding-bottom: 5px;
  vertical-align: middle;
  padding-left: 22px;
}

.hierarchyPanelTitle {
  background-color: #EDEDE7;
  border-top: solid 1px #FFF;
  border-bottom: solid 1px #D2D2CB;
  font-size: .8em;
  font-weight: 500;
  padding-top: 7px;
  padding-bottom: 5px;
  vertical-align: middle;
  padding-left: 30px;
}

.colapsablePanelTitleRow {
  display: block;
  margin-left: -12px;
  padding-left: 12px;
  width: 100%;
}

.colapsablePanelBottom {
  background-color: #EDEDE7;
  padding-right: 15px;
  text-align: right;
  border-top: solid 1px #FFF;
  border-bottom: solid 1px #D2D2CB;
  height:  14px;
  vertical-align: middle;
}

.colapsablePanelBottom A.goToProcess{
  padding-right: 10px;
  background-repeat: no-repeat;
  background-position: right center;
  font-size: .8em;
}

.colapsablePanelBottom A.goToProcess:Link {
  color: #545454;
  text-decoration: none;
  line-height:  8px;

}

.colapsablePanelBottom A.goToProcess:Visited {
  color: #545454;
  text-decoration: none;
  line-height:  8px;

}

.colapsablePanelBottom A.goToProcess:Hover {
  color: #000000;
  text-decoration: none;
  line-height:  8px;

}

.colapsablePanelBottom A.goToProcess:Active {
  color: #FFFFFF;
  text-decoration: none;
  line-height:  8px;
}

.containerIconOpenTop {
  background-image: url(../../../images/minusHierarchy.gif);
  background-repeat: no-repeat;
  background-position: 17px 6px;
}

.containerIconOpen {
  background-image: url(../../../images/minus.gif);
  background-repeat: no-repeat;
  background-position: 7px 6px;
}

.containerIconOpenSub {
  background-image: url(../../../images/minusHierarchy.gif);
  background-repeat: no-repeat;
  background-position: 30px 6px;
}

.containerIconClosedTop {
  background-image: url(../../../images/plusHierarchy.gif);
  background-repeat: no-repeat;
  background-position: 17px 6px;
}

.containerIconClosed {
  background-image: url(../../../images/plus.gif);
  background-repeat: no-repeat;
  background-position: 7px 6px;
}

.containerIconClosedSub {
  background-image: url(../../../images/plusHierarchy.gif);
  background-repeat: no-repeat;
  background-position: 30px 6px;
}

.contentTab {
  font-size: .8em;
  font-weight: bold;
  border-bottom: none;
  height: 11px;
  text-align: right;
  margin: 0px;
  background-color: #transparent;
  vertical-align: bottom;
}

.contentTab .tabOn {
  color: #FFFFF1;
  display: block;
  margin: 0px;
  height: 11px;
  line-height: 10px;
  font-weight: normal;
  text-decoration: none;
  padding-top: 7px;
  padding-right: 10px;
  padding-bottom: 2px;
  border-bottom: none;
  border-top: solid 1px #B5B5B5;
  border-left: solid 1px #B5B5B5;
  border-right: solid 1px #475C70;
  padding-left: 24px;
  white-space: nowrap;
  background-color: #545454;
}

.contentTabProcess .tabOn {
  background-color: #166297;
}

.contentTabAsset .tabOn {
  background-color: #166297;
}


.contentTab .tabOff {
  display: block;
  margin: 0px;
  height: 11px;
  line-height: 10px;
  font-weight: normal;
  text-decoration: none;
  padding-top: 7px;
  padding-right: 10px;
  padding-bottom: 2px;
  border-bottom: none;
  border-top: solid 1px #B5B5B5;
  border-left: solid 1px #B5B5B5;
  border-right: solid 1px #B5B5B5;
  padding-left: 24px;
  white-space: nowrap;
}

.contentTabOnBorder {
  height: 1px;
  background-color: #545454;
}

.contentTabOnBorderAsset {
  height: 1px;
  background-color: #166297;
}

.contentTabOnBorderProcess {
  height: 1px;
  background-color: #166297;
}

.contentTabOffBorder {
  height: 1px;
  background-color: #FFF;
}

.contentActions {
  background-color: #E7E7DE;
  border-left: solid 1px #CCC;
  border-right: solid 1px #CCC;
  border-bottom: solid 1px #BBBBB4;
  height: 25px;
  color: #545454;
}


.contentActions2 {
  background-color: #D2D2CB;
  border-left: solid 1px #CCC;
  border-right: solid 1px #CCC;
  border-bottom: solid 1px #BBBBB4;
  height: 22px;
  color: #545454;
  font-family: "trebuchet ms";
  font-size: .9em;
}

.contentActionsAgents {
  background-color: #E7E7DE;
  border-left: solid 1px #CCC;
  border-right: solid 1px #CCC;
  border-bottom: solid 1px #BBBBB4;
  height: 50px;
  color: #545454;
}

.contentActions .alt {
  background-color: transparent;
  margin-top: 7px;
  border-top: dotted 1px #BBBBB4;
  height: 25px;
  color: #545454;
}

.contentActionsHome {
  background-color: #E7E7DE;
  height: 25px;
}
.contentActionsHomeTasks {
  background-color: #E7E7DE;
  height: 20px;
}

.contentActionsHome TABLE {
  height: 25px;
  width:  100%;
  padding-right: 0px;
  border-top: solid 1px #9F9F9F;
}

.contentActions H2 {
  font-size: 1em;
  padding-top: 5px;
  margin-left: -4px;
  vertical-align: middle;
  white-space: nowrap;
  color: #545454;
  text-align: left;
  display: inline;
}

.contentActions TABLE {
  height: 25px;
  width:  100%;
  padding-right: 0px;
  border-top: solid 1px #9F9F9F;
}

.contentActionsAgents H2 {
  font-size: 1em;
  padding-top: 5px;
  margin-left: -4px;
  vertical-align: middle;
  white-space: nowrap;
  color: #545454;
  text-align: left;
  display: inline;
}

.contentActionsAgents TABLE {
  height: 25px;
  width:  100%;
  padding-right: 0px;
  padding-top: 5px;
  border-top: solid 1px #9F9F9F;
}

.contentActions2 TABLE {
  height: 22px;
  width:  100%;
  padding-right: 0px;
  border-top: solid 1px #9F9F9F;
}

.contentActions TD {
  font-size: .9em;
}

.contentActionsAgents TD {
  font-size: .9em;
}

.contentActions2 TD {
  font-size: .9em;
}

.contentActions .blankSpace {
  padding-right: 10px;
}

.contentActionsAgents .blankSpace {
  padding-right: 10px;
}

.contentActions2 .blankSpace {
  padding-right: 10px;
}

.contentActions .add {
  display: block;
  background-color: #DBDBC9;
  color: #6B6B6B;
  vertical-align: middle;
  font-size: .8em;
  float: right;
  padding: 3px;
  padding-left: 10px;
  padding-right: 10px;
  border-left: solid 1px #F6F6EB;
  border-right: solid 1px #BDBDAC;
}

.contentActions .add IMG {
  border-style: none;

}

.contentActions A.add:link, .contentActions A.add:active, .contentActions A.add:visited {
    font-size: .9em;
  color:  #6B6B6B;
}

.contentActions A.add:hover {
  color:  #000;
  background-color: #D1D1C0;
}

.destinationSelect {
  height: 70px;
  }

.destinationLabel {
  vertical-align: top;
  font-weight: bold;
  }

.profileGroup{
  background-color: transparent;
  margin-top: 10px;
  padding-bottom: 3px;
  border: 0;
  border-top: dotted 0px #BBBBB4;
  height: 25px;
  color: #545454;
  text-align: right;
}

.profileGroup table{
  border-top: 0;
}

.displayResults {
  text-align: right;
  color: #545454;
  font-weight: 600;
  font-size: .8em;
  padding: 5px;
  padding-right: 5px;
  padding-bottom: 4px;
  padding-top: 3px;
  margin-top: 3px;
}

.displayResults a {
  line-height: 13px;
}

.displayResultsTop {
  border-bottom: solid 1px #B5B5B5;
  margin-bottom: 1px;
  padding-bottom: 2px;
}


.displayResultsOverview {
  float: right;
  width: 200px;
  text-align: right;
  color: #545454;
  font-weight: 600;
  font-size: .9em;
  padding: 5px;
  padding-right: 5px;
  padding-bottom: 4px;
  padding-top: 0;
  margin-top: 0;
}

.stageShowAll {
  float: right;
  width: 200px;
  text-align: right;
  color: #545454;
  font-weight: 600;
  font-size: .9em;
  padding: 5px;
  padding-right: 10px;
  padding-bottom: 4px;
  padding-top: 0;
  margin-top: 0;
}

.documentIcon {
  background-image: url(../../../images/icon_document.gif);
  background-repeat: no-repeat;
  background-position: 30px 4px;
}

.documentIconSub {
  background-image: url(../../../images/icon_document.gif);
  background-repeat: no-repeat;
  background-position: 43px 4px;
}

.documentIconSubSub {
  background-image: url(../../../images/icon_document.gif);
  background-repeat: no-repeat;
  background-position: 63px 4px;
}

a.tabOff:link, a.tabOff:visited {
  background-color: #F8F8F8;
  background-image: none;
  color: #545454;
  border-top: solid 1px #B5B5B5;
  border-bottom: solid 1px #B5B5B5;
}

a.tabOff:hover, a.tabOff:active {
  background-color: #ECECEC;
  background-image: none;
  color: #000;
  text-decoration:  none;
  border-top: solid 1px #B5B5B5;
  border-bottom: solid 1px #C5C5C5;
}

.contentTabShadow {
  height: 1px;
  border-bottom: solid 1px #B5B5B5;
  background-color: #FFF;
}

.tabNote {
  text-align: right;
  padding-right: 10px;
  font-weight: 600;
  font-size: .8em;
  color: #545454;
  width: 100%;
}

.tabNote EM {
  text-align: right;
  padding-right: 5px;
  color: #000000;
  font-style: normal;
}

.dataTable {
  border: solid 1px #CCC;
  vertical-align: top;
  width: 100%;
}

.dataTable .missing {
  color: #C43C00;
}

.dataTable .fixed {
  color: #878787;
}

.dataTable .conflict {
  background: #FFF1B1;
}

.dataTable td.error {
  padding: 15px;
  color: #f00;
}

.dataTableSub {
  border-top: dotted 0px #CCC;
  vertical-align: top;
  width: 100%;
}

.dataTable TABLE {
  width: 100%;
}

.tableInfo {
  font-size: .9em;
}

.tableHeader {
  font-size: .8em;
}

.dataTable TD {
  border-bottom: solid 1px #DCDCDC;
  border-left: solid 1px #DCDCDC;
  padding: 3px;
  padding-right: 17px;
  padding-left: 17px;
  height: 15px;
}

.dataTable TD.noWrap {
  white-space: nowrap;
}

.dataTable TD.noBorder {
  border-bottom: 0px;
}

TD.sub {
  padding-left: 27px;

}

.dataTable TH {
  border-top: solid 1px #E9E9E9;
  border-bottom: solid 1px #B2B2B2;
  border-left: solid 1px #FFFFFF;
  border-right: solid 1px #D5D5C4;
  padding: 0px;
  padding-left: 17px;
  padding-right: 17px;
  margin: 0px;
  font-weight: bold;
  text-align: center;
  background-color: #F8F8F8;
  height: 20px;
  white-space: nowrap;
}

.dataTable .rightAligned {
  text-align: right;
}

.dataTable .leftAligned {
  text-align: left;
}

.centerAligned {
  text-align: center;
}

.dataTable .checkBox {
  text-align: center;
  padding-top: 3px;
  padding-right: 10px;
  padding-bottom: 3px;
  padding-left: 10px;
}

.dataTable .leftColumn {
  padding-right: 4px;
  border-right: none;
  text-align: right;
}

.dataTable .rightColumn {
  padding-left: 4px;
  border-left: none;
  border-right: none;
  text-align:left;
}

.dataTableNoGrid {
  border-right: solid 1px #CCC;
  border-left: solid 1px #CCC;
  vertical-align: top;
  width: 100%;
}

.dataTableNoGrid TD {
  white-space: nowrap;
  border: none;
  padding: 3px;
  padding-left: 17px;
  padding-right: 17px;
  padding-bottom: 2px;
  line-height: 1.3em;
}

.dataTableNoGrid TD.selectLine {
  border-right: solid 1px #CCC;
}

.dataTableNoGrid td.error {
  padding: 15px;
  color: #f00;
}

.dataTableNoGrid TD.wrapBorder {
  border-bottom: solid 1px #CCCCCC;
  color: #545454;
  white-space: normal;
}

.dataTableNoGrid TD.wrapNoBorder {
  border: none;
  white-space: normal;
  line-height: 1.3em;
}

.dataTableNoGrid TD.wrapNoBorder A{
  line-height: 1.3em;
}

.dataTableNoGrid .leftColumn {
  padding-right: 0px;
  border-right: none;
  text-align: right;
}

.dataTableNoGrid .rightColumn {
  padding-left: 4px;
  border-left: none;
  border-right: none;
  text-align:left;
}

.dataTableNoGrid .rightAligned {
  text-align: right;
}

.dataTableNoGrid .leftAligned {
  text-align: left;
}

.dataTableNoGrid .centerAligned {
  text-align: center;
}

.dataTableNoGrid TD.leftColumn {
  vertical-align: middle;
}

.dataTableNoGrid TD.rightColumn {
  vertical-align: middle;
}

.dataTableNoGrid TH {
  border-top: solid 1px #E9E9E9;
  border-bottom: solid 1px #B2B2B2;
  border-left: solid 1px #FFFFFF;
  border-right: solid 1px #D5D5C4;
  padding: 0px;
  padding-left: 12px;
  padding-right: 12px;
  margin: 0px;
  font-weight: 600;
  text-align: center;
  background-color: #F8F8F8;
  height: 20px;
  white-space: nowrap;
}

.dataTableNoGrid .checkBox {
  text-align: center;
  vertical-align: middle;
  padding-top: 3px;
  padding-right: 10px;
  padding-bottom: 3px;
  padding-left: 10px;
}

.dataTable .nowrap{
  white-space: nowrap;
}

.deleteProcess {
  padding-left: 10px;
  border-left: solid 1px #CCC;
  margin-left: 5px;
}

.diffLine {
  background-image: url(../../../images/diffLineBack.gif);
  background-repeat: repeat-y;
  background-position: 7px;
}

.frameBottom {
  border-bottom: 1px dotted #A4A4A4;
}

.formLabelNoBorder {
  color: #545454;
  font-size: .9em;
}

.formLabelNoBorderFixed {
  color: #545454;
  font-size: .9em;
}

.formLabelNoBorderGray {
  color: #333;
  font-size: .9em;
}

.formPadding {
  padding: 4px 4px 4px 5px;
}

.formText {
  color: #333;
  font-size: .9em;
}

.footer {
  padding-top: 10px;
  padding-left: 20px;
  font-size: 9px;
  color: #A1A1A1;
  font-weight: 600;
}

.goButton {
  font-size: 1.2em;
  font-weight: 600;
  padding-top: 0px;
  padding-right: 6px;
  padding-bottom: 0px;
  padding-left: 6px;
  margin-left: 10px;
  margin-bottom: 0px;
  border-left: solid 1px #C9C9C2;
  background: url(../../../images/icon_go.gif);
  background-position: right bottom;
  background-repeat: no-repeat;
}

.goButtonSmall {
  font-size: 1em;
  font-weight: 600;
  padding-top: 0px;
  padding-right: 6px;
  padding-bottom: 0px;
  padding-left: 6px;
  margin-left: 4px;
  margin-bottom: 0px;
  border-left: solid 1px #C9C9C2;
  background: url(../../../images/icon_go.gif);
  background-position: right bottom;
  background-repeat: no-repeat;
}

.goButtonBack {
  font-size: 1.2em;
  font-weight: 600;
  padding-top: 0px;
  padding-right: 6px;
  padding-bottom: 0px;
  padding-left: 6px;
  margin-left: 10px;
  margin-bottom: 0px;
  border-right: solid 1px #C9C9C2;
  background: url(../../../images/icon_goBack.gif);
  background-position: left bottom;
  background-repeat: no-repeat;
}

.nextButton {
  font-size: 1em;
  font-weight: 600;
  padding-top: 0px;
  padding-right: 6px;
  margin-right: 10px;
  padding-bottom: 0px;
  padding-left: 6px;
  margin-left: 0;
  margin-bottom: 0px;
  background: url(../../../images/icon_go.gif);
  background-position: right bottom;
  background-repeat: no-repeat;
}

.prevButton {
  font-size: 1em;
  font-weight: 600;
  padding-top: 0px;
  padding-right: 9px;
  padding-bottom: 0px;
  padding-left: 7px;
  margin-left: 8px;
  margin-bottom: 0px;
  border-right: solid 1px #999;
  background: url(../../../images/icon_goBack.gif);
  background-position: left bottom;
  background-repeat: no-repeat;
}



.cancelButton {
  font-size: 1.2em;
  font-weight: 600;
  padding-top: 0px;
  padding-right: 6px;
  padding-bottom: 0px;
  padding-left: 6px;
  margin-bottom: 0px;
  margin-left: 10px;
  border-left: solid 1px #C9C9C2;
  background: url(../../../images/icon_cancel.gif);
  background-position: right bottom;
  background-repeat: no-repeat;
}

.refreshButton {
  font-weight: 600;
  padding-right: 6px;
  padding-left: 14px;
  margin-right: 6px;
  background: url(../../../images/icon_refresh.gif);
  background-position: left bottom;
  background-repeat: no-repeat;
}

.error, a.error, a.error:link {
  color: #f00;
}

a.error:active {
  color: #f66;
}

a.error:hover, a.error:visited {
  color: #d40000;
}

.leftArrow {
  vertical-align: middle;
  margin-left: -8px;
  margin-right: 3px;
}

.leftNav {
  border-bottom: solid 1px #fff;
}

.leftNav td {
  padding: 6px;
  padding-left: 26px;
  }

.leftNav .links {
  display: none;
  border-bottom: solid 1px #A9A99E;
}

.leftNav .linkRow {
  display: block;
  vertical-align: middle;
  background-repeat: no-repeat;
  background-position: 10px 7px;
  padding-left: 18px;
  padding-top: 4px;
  padding-bottom: 4px;
  border-bottom: solid 1px #EBEBDF;
  background-color: #FFF;
  color: #000;
}

.leftNav a.linkRow {
  background-color: #FFF;
  color: #000;
  font-size: .8em;
}

.leftNav a.linkRow:Link {
  background-color: #F2F2E9;
  color: #000;
}

.leftNav a.linkRow:Visited {
  background-color: #FFF;
  color: #000;
}

.leftNav a.linkRow:Hover {
  background-color: #E1EEFA;
  color: #000;
}

.leftNav a.linkRow:Active {
  background-color: #F2F2E9;
  color: #000;
}


.rightAlign {
  text-align: right;
}

.rowHighlight {
  background-color: #FFF;
}

.rowHighlightCurrent {
  background-color: #CDE0FA;
}

.rowHighlightHover {
  cursor: pointer;
  background-color: #e4edff;
}

.mainContentButton {
  height: 12px;
  font-size: .9em;
  background-color: #FFE0AB;
  padding-left: 20px;
  padding-right: 10px;
  padding-bottom: 5px;
  text-align: right;
  display: block;
  vertical-align: middle;
  padding-top: 5px;
  white-space: nowrap;
  background-color: transparent;
  border-top: 1px solid #9ACFF2;
  border-left: 1px solid #9ACFF2;
  border-right: 1px solid #6CA1C0;
  border-bottom: 1px solid #6CA1C0;
}

.mainContentButton.add {
  background-repeat:  no-repeat;
  background-position: 6px 7px;
}

.mainContentButton.loginBack {
  background-image: url(../../../images/loginButtonBack.gif);
  background-color: transparent;
  border: 1px solid #393939;
  padding-right: 20px;
}

.mainContentButton.company {
  background-image: url(../../../images/icon_company.gif);
  background-repeat:  no-repeat;
  background-position: 9px 10px;
}

.mainContentButton.cancel {
  background-image: url(../../../images/icon_cancel.gif);
  background-repeat:  no-repeat;
  background-position: 9px 10px;
}

.mainContentButton.delete {
  background-image: url(../../../images/icon_delete.gif);
  background-repeat:  no-repeat;
  background-position: 6px 7px;
}

.mainContentButton.deploy {
  background-image: url(../../../images/icon_deploy.gif);
  background-repeat:  no-repeat;
  background-position: 6px 7px;
}

.mainContentButton.diff {
  background-image: url(../../../images/icon_diff.gif);
  background-repeat:  no-repeat;
  background-position: 5px 8px;
}

.mainContentButton.go {
  background-image: url(../../../images/icon_go.gif);
  background-repeat:  no-repeat;
  background-position: 9px 9px;
}

.mainContentButton.merge {
  background-image: url(../../../images/icon_merge.gif);
  background-repeat:  no-repeat;
  background-position: 6px 7px;
}

.mainContentButton.modify {
  background-image: url(../../../images/icon_modify.gif);
  background-repeat:  no-repeat;
  background-position: 6px 7px;
}

.mainContentButton.preview {
  background-image: url(../../../images/icon_preview.gif);
  background-repeat:  no-repeat;
  background-position: 7px 7px;
}

.mainContentButton.reject {
  background-image: url(../../../images/icon_reject.gif);
  background-repeat:  no-repeat;
  background-position: 6px 7px;
}

.mainContentButton.remove {
  background-image: url(../../../images/icon_remove.gif);
  background-repeat:  no-repeat;
  background-position: 6px 7px;
}

.mainContentButton.request {
  background-image: url(../../../images/icon_request.gif);
  background-repeat:  no-repeat;
  background-position: 6px 7px;
}

.mainContentButton.reset {
  background-image: url(../../../images/icon_reset.gif);
  background-repeat:  no-repeat;
  background-position: 6px 8px;
}

.mainContentButton.revert {
  background-image: url(../../../images/icon_revert.gif);
  background-repeat:  no-repeat;
  background-position: 6px 8px;
}

.mainContentButton.rollback {
  background-image: url(../../../images/icon_rollback.gif);
  background-repeat:  no-repeat;
  background-position: 6px 8px;
}

.mainContentButton.save {
  background-image: url(../../../images/icon_save.gif);
  background-repeat:  no-repeat;
  background-position: 6px 7px;
}

a.mainContentButton:link,  a.mainContentButton:visited {
  color: #000;
  }

a.mainContentButton:hover, a.mainContentButton:active {
  color: #00658D;
}

.okAlt a.mainContentButton:link,  .okAlt a.mainContentButton:visited {
  color: #000;
  }

.okAlt a.mainContentButton:hover, .okAlt a.mainContentButton:active {
  background-color: #999;
  color: #fff;
}

.confirmOK a.mainContentButton:link,  .confirmOK a.mainContentButton:visited {
  color: #000;
  }

.confirmOK a.mainContentButton:hover, .confirmOK a.mainContentButton:active {
  background-color: #999;
  color: #fff;
}


.actionOK a.mainContentButton:link,  .actionOK a.mainContentButton:visited {
  color: #000;
  }

.actionOK a.mainContentButton:hover, .actionOK a.mainContentButton:active {
  background-color: #999;
  color: #fff;
}

.moreInfoButton {
  height: 12px;
  font-size: .9em;
/*
  background-color: #CFCFCF;
  border-left: solid 1px #FFFFFF;
  border-bottom: 1px solid #BBBBB4;
  border-right: solid 1px #BBBBB4;
*/
  padding-left: 10px;
  padding-right: 10px;
  padding-bottom: 5px;
  text-align: right;
  display: block;
  vertical-align: middle;
  padding-top: 5px;
  white-space: nowrap;
}

.moreInfoButton.company {
  background-image: url(../../../images/icon_company.gif);
  background-repeat:  no-repeat;
  background-position: 0px 9px;
}

a.moreInfoButton:link,  a.moreInfoButton:visited {
  color: #6B6B6B;
  }

a.moreInfoButton:hover, a.moreInfoButton:active {
/*  background-color: #DBDBDB; */
  color: #000;
}

.siteAlert {
  background-image: url(../../../images/bg_siteAlert.gif);
}

.siteAlert H1 {
  background-color: transparent;
}

.siteAlertText {
  font-size: 1.7em;
  font-weight: bold;
  color: #F00;
  height: 1.7em;
  margin: 0px;
  margin-bottom: -10px;
  padding: 0px;
}

.siteAlertSubText {
  color: #000;
}

.siteAlertSubLink {
  font-weight: bold;
}

.subSelectIndent {
  margin-left: 30px;
}

.tableSep {
  border-bottom: dotted 1px #B0B0A8;
  }

.adminDetails {
  padding-left: 25px;
  background-position: 10px 1px;
  }

.contentActionsHomeTasks .mainContentButton {
  padding-top: 4px;
  padding-bottom: 3px;
  border: none;
  background-color: #D4D4C4;
  border-left: 1px solid #fff;
  font-size: .8em;
  }


/*</group>*/

/*<group=id selectors>*/

#addBottom {
  border-top: 1px dotted #ccc;
  padding-top: 5px;
  margin-top: 10px;
  text-align: right;
  }

#adminTabOff {
  position: relative;
  z-index:3;
  margin-left: -20px;
}

#adminTabOn {
  position: relative;
  z-index:7;
  margin-left: -20px;
}

#adminToDoRight {
  border-right: 1px solid #ccc;
  vertical-align: top;
  padding-top: 15px;
  }

#assetBrowser {
  position: fixed;
  top: 0px;
  z-index: 100;
  height: 400px;
  width: 700px;
  left: 200px;

  border-left: solid 1px #FFF;
  border-bottom: solid 2px #CCC;
  border-right: solid 1px #DBDBD2;
  background-color: #E7E7DE;
  margin-right: auto;
  margin-left: auto;
 }

 div#assetBrowser, div#deployAction {
  /* IE5.5+/Win - this is more specific than the NS4 version */
  left: expression( ( 200 + ( ignoreMe2 = ( document.documentElement && document.documentElement.scrollLeft ) ? document.documentElement.scrollLeft : document.body.scrollLeft ) ) + 'px' );
  top: expression( ( 0+ ( ignoreMe = ( document.documentElement && document.documentElement.scrollTop ) ? document.documentElement.scrollTop : document.body.scrollTop ) ) + 'px' );

  z-index: 100;
  height: 482px;
  width: 700px;
  border-left: solid 1px #FFF;
  border-bottom: solid 2px #999;
  border-right: solid 1px #DBDBD2;
  background-color: #FFF;
  margin-right: auto;
  margin-left: auto;
 }

#assetBrowserContent #nonTableContent P {
  margin: 0px;
  padding: 0px;
  padding-bottom: 15px;
}

#assetBrowserContent td.formLabel {
  vertical-align: top;
  padding-top: 3px;
}

#assetBrowserDirectoryList {
  padding-top: 3px;
  padding-bottom: 3px;
  border-bottom: 1px dotted #A4A4A4;
  border-top: 1px dotted #A4A4A4;
}

#assetBrowserFolderList {
  height: 80px;
  background-color: #fff;
  overflow: auto;
  overflow-x: hidden
}

#assetBrowserCheck {
  padding-top: 6px;
  padding-bottom: 6px;
  padding-left: 3px;
  border-bottom: 1px dotted #A4A4A4;
  border-top: 1px dotted #A4A4A4;
}

#assetBrowserCheck table {
  width: 150px;
  margin-top: 0px;
  padding-top: 0px;
  font-size: 1em;
}

#assetBrowserButton {
  border-left: solid 1px #E1E1D6;
  background-repeat: repeat-x;
  background-image: none;
}

#assetBrowserButton A:link {
  color: #F56A24;
}

#assetBrowserButton A:visited {
  color: #F56A24;
}

#assetBrowserButton A:hover {
  color: #D24D0A;
}

#assetBrowserButton A:active {
  color: #FBC900;
}

 #assetBrowser IFRAME {
  width: 700px;
  height: 482px;
  position: absolute;
  border: none;
  background-color: #F3F3ED;
  background-image: url(../../../images/assetBrowser_loading.gif);
  background-repeat: no-repeat;
  background-position: 260px 150px;
}

 #assetBrowser IFRAME .alt {
  height: 482px;
}

#assetBrowser .ok {
  top: 423px;
  padding-right: 0px;

  text-align:  right;
  vertical-align: middle;
  position: absolute;
  padding-top: 6px;
}

.okAlt {
  position: absolute;
  top: 424px;
  padding-right: 21px;
  background-color: transparent;

  text-align:  right;
  vertical-align: middle;
  position: absolute;
  padding-top: 6px;
}

.okAlt .mainContentButton {
  border: 1px solid #fff;
  background-color: transparent;
}

.okAlt .buttonImage {
  background-image: none;
}

#assetBrowser2 .closeWin {
  top: 398px;
  right: 10px;
  height:  20px;
  text-align:  right;
  vertical-align: middle;
  position: absolute;
  padding-top: 6px;
  padding-right: 20px;
  padding-left: 20px;
  font-size: .9em;
  background-color: #E7E7DE;
  border-bottom: solid 3px #CCC;
  border-right: solid 1px #CCC;
  border-left: solid 1px #FFFFF1;
}


#assetBrowser .closeWin A {
  color: #456731;
}

#assetBrowser2 .closeWin A {
  color: #456731;
}

#assetBrowserResults {
  height: 320px;
  width: 655px;
  overflow: auto;
  overflow-x: hidden;
  margin: 0 auto 0 auto;
  border-bottom: 1px solid #F0F0F0;
  border-left: 1px solid #AAAEB0;
  border-top: 1px solid #E5E5E3;
  background-color: #fff;
}

.assetBrowserIframe {
  border-style: none;
  width: 100%;
  height: 160px;
  background-image: url(../../../images/assetBrowser_loading.gif);
  background-repeat: no-repeat;
  background-position: 20px 50px;
}

.browserIframe {
  border-style: none;
  width: 100%;
  height: 160px;
  background-image: url(../../../images/assetBrowser_loading.gif);
  background-repeat: no-repeat;
  background-position: 20px 50px;
}

.browseResultsIframe {
  border-style: none;
  width: 100%;
  height: 160px;
  background: url(../../../images/loading_white.gif) no-repeat 20px 50px;
}

#overviewHeader {
  background-image: url(../../../images/asset_back_top.jpg);
  background-color: #E5E5E5;
  background-position: left bottom;
  background-repeat: repeat-x;
  border-bottom: solid 1px #A6A5A5;
  padding: 3px 0 0 15px;
  text-align: left;
}
#assetBrowserHeader {
  background-image: url(../../../images/asset_back_top.jpg);
  border-bottom: solid 1px #A6A5A5;
  padding: 3px 0 0 15px;
  text-align: left;
  height: 35px;

}

#assetBrowserHeader H2 {
  color: #666;
  margin-bottom: 0px;
  margin-top: 13px;
  font-size: 16px;
  background: transparent;
}

#assetEditor.assetEdit {
  border-left: solid 1px #CCC;
  border-right: solid 1px #CCC;
  width: 100%;
  padding: 5px;
  background-color: #FFFFFF;
}

#assetEditor.assetEdit DIV {
  background-color: #FFFFFF;
  border-top: solid 1px #E7E7DE;
  border-left: solid 1px #E7E7DE;
  font-size: 1em;
  margin-bottom: 8px;
  padding: 0px;
  padding-left: 8px;
}

#assetEditor.assetEdit DIV#generatedForm {
  border: none;
}

#assetEditor.assetEdit H1 {
  font-size: 1em;
  color: #545454;
  background-color: transparent;
  margin: 0px;
  padding: 0px;
  padding-left: 12px;
  padding-bottom: 3px;
  margin-top: 5px;
}

#assetEditor.assetEdit TH {
  font-size: .9em;
  color: #545454;
  margin: 0px;
  padding: 0px;
  padding-left: 1px;
  padding-bottom: 3px;
}

#assetEditor.assetEdit H2 {
  font-size: .9em;
  color: #545454;
  background-color: transparent;
  margin: 0px;
  padding: 0px;
  padding-left: 1px;
  padding-bottom: 3px;
}

#assetEditor.assetEdit H3 {
  font-size: 1em;
  font-weight: normal;
  color: #000;
  background-color: transparent;
  margin: 0px;
  padding: 0px;
  padding: 2px;
  background-image: none;
  border: 0px;
}

#assetEditor.assetEdit H3.required {
  border-left: solid 8px #B0B0A8;
  margin-left: -8px;
}

#assetEditor.assetEdit H3.disabled {
  color: #999;
}

#assetEditor.assetEdit P.disabled {
  color: #999;
}

#assetEditor.assetEdit P LABEL {
  color: #333;
}

#assetEditor.assetEdit P.disabled LABEL {
  color: #999;
}

#assetEditor.assetEdit TD {
  border: none;
  vertical-align: top;
}

#assetEditor.assetEdit TD.left {
  padding-right: 15px;
  width: 50%
}

#assetEditor.assetEdit TD.right {
  padding-left: 15px;
  width: 50%
}

#assetEditor.assetEdit STRONG {
  font-size: 1em;
  font-weight: normal;
  color: #000;
}

#assetEditor.assetEdit P {
  font-size: .9em;
  line-height: 16px;
  padding-left: 12px;
}

#assetEditor.assetEdit P textarea {
  width: 95%;
  height: 75px;
}

#assetEditor.assetEdit P select {
  width: auto;
  margin: 0px;
}

#assetEditor.assetEdit P .text {
  width: 220px;
}


#assetEditor.assetEdit P .button {
  text-align: center;
}

#assetEditor.assetEdit P .password {
  width: 120px;
}

#assetEditor.assetEdit P .checkbox {
  margin-right: 5px;
}

#assetEditor.assetEdit P .radio {
  margin-right: 5px;
}

#assetEditor.assetEdit P .submit {
  width: auto;
  text-align: center
}

#assetEditor.assetEdit P .reset {
  width: auto;
  text-align: center
}

#assetEditor.assetEdit P .file {
}

#assetEditor.assetEdit P .hidden {
}

#assetEditor.assetEdit P .image {
  width: auto;
}

#assetEditor.assetEdit P .button {
  width: auto;
}

#assetEditor .error {
  color:  #F00;
}

#assetEditor SPAN.error {
  display: block;
  margin: 0px;
  padding: 0px;
  line-height: 1em;
}

#assetEditor.assetEdit P.required {
  border-left: solid 8px #B0B0A8;
  margin-left: -8px;
}

#assetEditor.assetEdit TABLE.map {
}

#assetEditor.assetEdit TABLE.map TD {
  padding: 3px;
  width: 40%;
}

#assetEditor.assetEdit TABLE.map TD.checkBox {
  text-align: center;
  padding-top: 3px;
  padding-right: 3px;
  padding-bottom: 3px;
  padding-left: 3px;
  width: 6%;
}

#assetEditor.assetEdit TABLE.map TD.button {
  padding: 3px;
  width: 10%;
  text-align: center;
}

#assetEditor.assetEdit TABLE.map TH {
  background-color: transparent;
  text-align: center;
  border: none;
  padding: 0px;
  height: 1em;
}

#assetEditor.assetEdit TABLE.map TD INPUT.textField {
  width: 98%;
}

#assetEditor.assetEdit DIV.mapButtons {
  border: none;
  border-top: dotted 1px #888;
  margin-top: 4px;
  padding: 4px;
  text-align: right;
}

#assetForm .hierarchyPaneClosed {
  display: none;
  border: none;
  margin-left: 0px;
}
#assetForm .hierarchyPaneOpen {
  display: block;
  border: none;
  margin-left: 0px;
}

.hierarchyPaneClosed {
  display: none;
  border: none;
  margin-left: 0px;
}
.hierarchyPaneOpen {
  display: block;
  border: none;
  margin-left: 0px;
}


#processTools {
  position: absolute;
  height: 33px;
  top: 85px;
  right: 0px;
  white-space: nowrap;
  font-size: .8em;
  font-weight:  600;
}

#processTools TD {
  height: 33px;
  padding:  0px;
  padding-right: 10px;
  padding-left: 5px;
  vertical-align: middle;
}

#processTools IMG {
  vertical-align: middle;
  border: none;
  padding-right: 5px;
}

#atgLogo {
  position: absolute;
  top: 0px;
  left: 0px;
  padding-right: 10px;
}

#currentProcess {
  position: absolute;
  top: 95px;
  padding-left: 20px;
  text-align: left;
  color: #898981;
  font-size: .9em;
  font-weight: 500;
}

#currentProcess EM {
  color: #545454;
  font-style: normal;
}

#modifyList {
  margin-top: 12px;
}

#nonTableContent {
  padding-left: 17px;
  padding-right: 17px;
  padding-top: 15px;
  padding-bottom: 0px;
  border-left: solid 1px #CCC;
  border-right: solid 1px #CCC;
  margin: 0px;
}

#nonTableContent.noborder {
  border: 0;
}

#nonTableContent P {
  font-size: .9em;
  color: #545454;
}

#nonTableContent .sep {
  border-top: dotted 1px #B0B0A8;
}

.sep {
  border-top: dotted 1px #B0B0A8;
}

#nonTableContent .radio {
  margin: 6px 4px 6px 5px;
}

#nonTableContent EM {
  color: #000;
  font-style: normal;
}

#nonTableContent H5 {
  margin: 0px;
  padding: 0px;
  font-size: 1em;
  color: #777774;
}

#nonTableContent .verticalAligned {
  vertical-align: top;
}

#nonTableContent .diffDate {
  border: 2px solid #ccc;
}

#nonTableContent .diffDate h5{
  color: #666;
}


.nopadding {
  padding: 0;
}

#attributeBar {
  background-color: #545454;
  border-top: none;
  padding: 10px;
  border-left: solid 1px #AEC3E2;
  border-right: solid 1px #52687E;
  border-bottom: solid 1px #545454;
  text-align: left;
  vertical-align: top;
  height: 25px;
}

#attributeBarAsset {
  background-color: #166297;
  border-top: none;
  padding: 10px;
  border-left: solid 1px #AEC3E2;
  border-right: solid 1px #52687E;
  border-bottom: solid 1px #545454;
  text-align: left;
  vertical-align: top;
  height: 25px;
}

#attributeBarProcess {
  background-color: #166297;
  border-top: none;
  padding: 10px;
  border-left: solid 1px #AEC3E2;
  border-right: solid 1px #52687E;
  border-bottom: solid 1px #545454;
  text-align: left;
  vertical-align: top;
  height: 25px;
}

#attributeBar A:Link, #attributeBarProcess A:Link, #attributeBarAsset A:Link {
  color: #F9EB6A;
  text-decoration: none;
  line-height:  8px;
}

#attributeBar A:Visited, #attributeBarProcess A:Visited, #attributeBarAsset A:Visited {
  color: #F9EB6A;
  text-decoration: none;
  line-height:  8px;
}

#attributeBar A:Hover, #attributeBarProcess A:Hover, #attributeBarAsset A:Hover {
  color: #FFF000;
  text-decoration: none;
  line-height:  8px;
}

#attributeBar A:Active, #attributeBarProcess A:Active, #attributeBarAsset A:Active {
  color: #FFFDD7;
  text-decoration: none;
  line-height:  8px;
}

#attributeBar P, #attributeBarProcess P, #attributeBarAsset P {
  margin-bottom: -1px;
  padding: 0px;
  line-height: 3px;
  color: #FFF;
}

#attributeBar DIV, #attributeBarProcess DIV, #attributeBarAsset DIV {
  width: 360px;
}

#attributeBar DIV.attributes, #attributeBarProcess DIV.attributes, #attributeBarAsset DIV.attributes {
  float: left;
  padding-left: 2px;
  width: 360px;
}

#attributeBar DIV.attributes P, #attributeBarProcess DIV.attributes P, #attributeBarAsset DIV.attributes P {
  color: #E8E8E8;
  font-size: .9em;
  line-height: 13px;
  padding: 1px;
}

#attributeBar DIV.attributeExtras, #attributeBarProcess DIV.attributeExtras, #attributeBarAsset DIV.attributeExtras {
  text-align: right;
  float: right;
  width: 400px;
  clear: none;
  white-space: nowrap;
  text-align: left;
  vertical-align: top;
}

#attributeBar DIV.attributeExtras select.rightIndent, #attributeBarProcess DIV.attributeExtras select.rightIndent, #attributeBarAssett DIV.attributeExtras select.rightIndent {
  margin-right: 93px;
}


#attributeBar TD.attributeExtras, #attributeBarProcess TD.attributeExtras, #attributeBarAsset TD.attributeExtras {
  vertical-align: top;
  font-size: .9em;
}

#attributeBar TD.leftAligned, #attributeBarProcess TD.leftAligned, #attributeBarAsset TD.leftAligned {
  text-align: left;
  white-space: nowrap;
}

#attributeBar TD.rightAligned, #attributeBarProcess TD.rightAligned, #attributeBarAsset TD.rightAligned {
  text-align: right;
  color: #E8E8E8;
  width: 100%;
  padding-right:  5px;
}

#attributeBar DIV.attributeExtras P, #attributeBarProcess DIV.attributeExtras P, #attributeBarAsset DIV.attributeExtras P {
  color: #E8E8E8;
  font-size: .9em;
  text-align: right;
}

#attributeBar EM, #attributeBarProcess EM, #attributeBarAsset EM {
  color: #FFFFF1;
  background-color: transparent;
  font-style: normal;
  /* font-size: .9em; */
}

#attributeBar .goButton, #attributeBarProcess .goButton, #attributeBarAsset .goButton {
  background: url(../../../images/icon_goAttributes.gif);
  background-position: right bottom;
  background-repeat: no-repeat;
  padding-right: 8px;
  font-weight: 500;
}

#attributeBar .goButtonBack, #attributeBarProcess .goButtonBack, #attributeBarAsset .goButtonBack {
  background: url(../../../images/icon_goAttributesBack.gif);
  background-position: left bottom;
  background-repeat: no-repeat;
  padding-right: 8px;
  font-weight: 500;
}

#beingRevised {
  position: absolute;
  top: 100px;
  white-space: nowrap;
  z-index: 30;
  font-size: 1.3em;
  font-weight: 800;
  text-align: left;
  width: 90%;
  color: #CCCCCC;
  padding-left: 20px;
  width: 300px;
}

#bgBrand {
  position: absolute;
  top: 0px;
  right: 0px;
  display: block;
}

#browseForm {
  position: relative;
  left: 50%;
  width: 550px;
  margin-left: -266px;
  white-space: nowrap;
  vertical-align: middle;
  padding-bottom: 120px;
}

#browseForm .formLabel {
  width: 250px;
  border: solid 1px #E5E5E5;
  padding-right: 5px;
  vertical-align: middle;
  text-align: right;
  color: #545454;
  font-size: .9em;
  border-left: solid 1px #E5E5E5;
}

#browseForm .formLabelDescription {
  padding: 4px 5px 4px 0;
}

#browseForm .formLabelRequired {
  border-left: solid 8px #E5E5E5;
}

#browseForm .formElement {
  text-align: left;
  vertical-align: middle;
  font-size: .9em;
  width: 200px;
  margin: 4px 4px 4px 5px;
}

#adminDeployment .formElement {
  text-align: left;
  vertical-align: middle;
  font-size: .9em;
  width: 200px;
  margin: 4px 4px 4px 5px;
}


#adminDeployment .radio {
  margin: 6px 4px 6px 5px;
}

#loginForm .formElementLogin {
  text-align: left;
  vertical-align: middle;
  font-size: 1em;
  width: 188px;
  margin: 0px 4px 0px 5px;
  padding: 2px;
  background-color: #BCBDBE;
  border: 1px inset #848587;
}

#browseForm .select {
  width: auto;
  margin: 4px 0px 4px 5px;
  font-size: .9em;
}

#browseForm textarea {
  width: 160px;
  height: 60px;
  margin: 4px 0px 4px 5px;
  font-size: .9em;
}

#browseForm .formElementInputText {
  width: 140px;
}

#browseForm .formElementSmallSelect {
  width: 50px;
}

#browseForm P .radio {
  margin: 4px 4px 4px 5px;
}

#browseForm .radio {
  margin: 4px 4px 4px 5px;
}

#loginForm{
  left: 0px;
  padding-bottom: 0px;
  padding-top: 0px;
  padding-left: 8px;
}

#mainContent #loginForm p {
  color: #FFFFFF;
}

#loginForm .formLabel {
  width: 100px;
  text-align: left;
  border: none;
  color: #FFFFFF;

}

#browseForm.login .goButton {
  margin-left: 5px;
}

#browseForm .hierarchyPaneClosed {
  display: none;
  border: none;
  margin-left: 0px;
}
#browseForm .hierarchyPaneOpen {
  display: block;
  border: none;
  margin-left: 0px;
}

#commerceNav {
  width: 300px;
  height: 275px;
  overflow: auto;
  background-color: #fff;
  border-bottom: 1px solid #818181;
  white-space: nowrap;
}

.assetBrowserDataTable {
  vertical-align: top;
  width: 640px;
  background-color: #E0EFF8;
}

.assetBrowserDataTable .missing {
  color: #C43C00;
}

.assetBrowserDataTable .fixed {
  color: #878787;
}

.assetBrowserDataTable .conflict {
  background: #FFF1B1;
}

.assetBrowserDataTable td.error {
  padding: 15px;
  color: #f00;
}

.assetBrowserDataTableSub {
  border-top: dotted 0px #CCC;
  vertical-align: top;
  width: 100%;
}

.assetBrowserDataTable TABLE {
  width: 95%;
}

.assetBrowserDataTable TD {
  border-bottom: solid 1px #DCDCDC;
  border-left: solid 1px #DCDCDC;
  padding: 3px;
  padding-right: 17px;
  padding-left: 17px;
  height: 15px;
}

.assetBrowserDataTable TD.noWrap {
  white-space: nowrap;
}

.assetBrowserDataTable TH {
  border-top: solid 1px #E9E9E9;
  border-bottom: solid 1px #B2B2B2;
  border-left: solid 1px #FFFFFF;
  border-right: solid 1px #D5D5C4;
  padding: 0px;
  padding-left: 17px;
  padding-right: 17px;
  margin: 0px;
  font-weight: bold;
  text-align: center;
  background-color: #F8F8F8;
  height: 20px;
  white-space: nowrap;
}

.assetBrowserDataTable .rightAligned {
  text-align: right;
}

.assetBrowserDataTable .leftAligned {
  text-align: left;
}

.assetBrowserDataTable .centerAligned {
  text-align: center;
}

.assetBrowserDataTable .checkBox {
  text-align: center;
  padding-top: 3px;
  padding-right: 10px;
  padding-bottom: 3px;
  padding-left: 10px;
}

.assetBrowserDataTable TD.current {
  border-left: solid 6px #00AC10;
  padding-left: 12px;
}

.assetBrowserDataTable .nowrap{
  white-space: nowrap;
}

#assetForm {
  position: relative;
  left: 50%;
  width: 510px;
  margin-left: -266px;
  white-space: nowrap;
  font-size: .9em;
}

#assetForm .formLabel {
  width: 150px;
  border: solid 1px #E5E5E5;
  padding-right: 5px;
  vertical-align: middle;
  text-align: right;
  color: #545454;
  font-size: .9em;
  border-left: solid 1px #E5E5E5;
}

#assetForm .formLabelHidden {
  border: 0;

}

#assetForm .formLabelRequired {
  border-left: solid 8px #DCDCDC;
}

#assetForm .formLabelRequiredHidden {
  border-left: 0;
}

#assetForm .formElement {
  text-align: left;
  vertical-align: middle;
  font-size: .9em;
  width: 200px;
  margin: 4px 4px 4px 5px;
}

#assetForm .select {
  width: auto;
  margin: 4px 0px 4px 5px;
  font-size: .9em;
}

#assetForm textarea {
  width: 160px;
  height: 60px;
  margin: 4px 0px 4px 5px;
  font-size: .9em;
}

#assetForm .formElementInputText {
  width: 140px;
}

#assetForm .formElementSmallSelect {
  width: 50px;
}

#assetForm P .radio {
  margin: 4px 4px 4px 5px;
}

#assetForm .radio {
  margin: 4px 4px 4px 5px;
}

#assetForm.login{
  left: 0%;
  width: 250px;
  margin: 30px auto 0px auto;
  padding-bottom: 0px;
}

#assetForm.login .formLabel {
  width: 100px;
}

#assetForm.login .goButton {
  margin-left: 120px;
}

.assetFormLabel {
  text-align: right;
  color: #666;
  width: 150px;
}

.assetFormLine1 {
  font-size: 1em;
  background-color: #ccc;
  border-bottom: solid 1px #999;
  margin: 0px;
}

.assetFormLine2 {
  font-size: 1em;
  background-color: #E5E5E5;
  border-top: solid 1px #fff;
  margin: 0px;
}

.assetFormInFolder {
  font-size: 1em;
  background-color: #E5E5E5;
  margin: 0px;
}

.assetFormLine3 {
  font-size: 1em;
  background-color: #E5E5E5;
  border-bottom: solid 1px #E3E3E3;
  margin: 0px;
}

#browseTabOff {
  position: relative;
  z-index:4;
  margin-left: -20px;
}

#browseTabOn {
  position: relative;
  z-index:7;
  margin-left: -20px;
}

#browseResults a {
  line-height: 18px;
}

#clientBrand {
  position: absolute;
  top: 15px;
  right: 37px;
}

#contentHolder {
  position: absolute;
  top: 119px;
  left: 0px;
  right: 0px;
  bottom: 0px;
}

#contentHolder.loginScreen {
  top: 0px;
  left: 0px;
  margin-left: 0px;
  text-align: left;
}

#footer {
  border-top: solid 1px #D8D8D8;
  height: 23px;
  padding-top: 5px;
}

#homeTabOff {
  position: relative;
  z-index:6;
  margin-left: -20px;
}

#homeTabOn {
  position: relative;
  z-index:7;
  margin-left: -20px;
}

#htmlView {
  width: 600px;
  height: 300px;
  border: 1px #999 solid;
  overflow: auto;
  padding: 5px;
}

#htmlViewFloat {
  float: left;
}

#intro {

}

#intro H2 {
  display: block;
  font-family: "Trebuchet MS",verdana,tahoma,sans-serif;
  font-size: 1.2em;
  font-weight: 800;
  color: #545454;
  margin: 0px;
}

#intro H2 A{
  font-family: "Trebuchet MS",verdana,tahoma,sans-serif;
  font-weight: 800;
  margin: 0px;
  padding:  0px;
  line-height: 0;
}

#intro P {
  font-size: .9em;
  color: #545454;
  margin-bottom: 10px;
}

#intro P.path {
  margin-top: -6px;
}

#intro P.path a {
  margin-right: 15px;
}

#introText {
  padding-top: 0px;
  padding-bottom: 15px;
}

#introText P {
  color: #545454;
  font-size: .9em;
}

#iWantTo {
  vertical-align: middle;
  position: absolute;
  left: 0px;
  top: 52px;
  padding-left: 12px;
  padding-top: 8px;
  white-space: nowrap;
  height: 30px;
  color: #545454;
  font-size: .9em;
  font-weight: 600;
}

#iWantTo IMG {
  display: inline;
  vertical-align: middle;
}

#iWantTo SELECT {
  margin-bottom: 2px;
}

#legacyBrowserMessage {
  display: none;
}

#login {
  position: relative;
  left: 0px;
  margin: 30px auto 0px auto;
  width: 509px;
  height: 385px;
  background-image: url(../../../images/loginPanelBack.jpg);
}


#login #mainContent {
  padding: 0px;
  background-color: transparent;
}

#mainContent {
  padding: 15px;
  background-color: #FFF;
}

#mainContent.noPadding {
  padding: 0px;
}

#createScreen {
  padding: 15px;
  background-color: #EEF2F4;
}

#mainContent P {
  padding: 5px;
  color: #545454;
}

#mainContent P.introText {
  padding-bottom: 15px;
  color: #545454;
  font-size: .9em;
}

#mainContent #nonTableContent P {
  margin: 0px;
  padding: 0px;
  padding-bottom: 15px;
}

#mainTable {
  width: 100%;
}

#mainContent.noPadding #mainTable {
  border-bottom: solid 1px #A8A8A8;
  width: 100%;
}

#nonTableContent DIV.flagged {
  text-align: right;
  float: right;
  width: 52px;
  clear: none;
  white-space: nowrap;
  text-align: left;
  vertical-align: top;
}

#nonTableContent textarea.propertyEdit {
  width: 610px;
  height: 150px;
}

select.propertyEditSelect {
  width: auto;
  margin: 4px 4px 4px 5px;
}

#nonTableContent textarea.assetEdit {
  font: 100% Arial, Verdana, Helvetica, sans-serif;	
  width: 600px;
  height: 200px;
}

#nonTableContent input.propertyEditInputText {
  width: 300px;
}

#nonTableContent .formLabel, #taskDetail .formLabel {
  width: 150px;
  border: solid 1px #E5E5E5;
  padding-right: 5px;
  padding-left: 10px;
  vertical-align: middle;
  text-align: right;
  color: #545454;
  font-size: .9em;
  border-left: solid 1px #E5E5E5;
}

#nonTableContent .formLabelKey, #taskDetail .formLabelKey {
  width: 70px;
  border: none;
  padding: 4px 5px 4px 8px;
  vertical-align: middle;
  text-align: right;
  color: #545454;
  font-size: .9em;
  border-left: solid 1px #E5E5E5;
  white-space: nowrap;
}

#intro .formLabel {
  width: 150px;
  border: solid 1px #E5E5E5;
  padding-right: 5px;
  padding-left: 10px;
  vertical-align: middle;
  text-align: right;
  color: #545454;
  font-size: .9em;
  border-left: solid 1px #E5E5E5;
}

#intro .formLabelKey {
  border: none;
  padding: 4px 5px 4px 8px;
  vertical-align: middle;
  text-align: right;
  color: #545454;
  font-size: .9em;
  border-left: solid 1px #E5E5E5;
  white-space: nowrap;
}


#nonTableContent .formElement {
  text-align: left;
  vertical-align: middle;
  font-size: .9em;
  width: 200px;
  margin: 4px 4px 4px 5px;
}

#nonTableContent .formElementCustom {
  text-align: left;
  vertical-align: middle;
  font-size: .9em;
  margin: 4px 4px 4px 5px;
}

#nonTableContent .formElementSmallSelect {
  width: 50px;
}

#nonTableContent .formElementSmall {
  width: 70px;
}


#nonTableContent .formLabelRequired {
  /*border-left: solid 8px #DCDCDC;*/
  background-image: url(../../../images/labelRequired.gif);
  background-repeat: repeat-y;
}

#adminRight {
  border-left: solid 1px #B4B4B2;
  background-color: #FFF;
}

/*
#plainView {
  float: center;
}
*/

#plainView UL {
  margin-right: 8px;
  list-style: disc inside;
}

#plainView LI {
  line-height: 20px;
  white-space: nowrap;
  padding-left: 5px;
  padding-right: 5px;
  border: 1px solid #F9F9F9;
  color: #0066CC;
  vertical-align: middle;
}

#plainView td.cat {
  font-size: 1.1em;
  font-weight: 600;
  padding: 8px;
  color: #0066CC;
  border-left: 1px solid #EEF2F4;
  border-bottom: 1px solid #EEF2F4;
  border-top: 1px solid #EEF2F4;
}

#plainView .description {
  padding-left: 10px;
  width: 350px;
}

#plainView td.alignment {
  vertical-align: top;
  padding: 0;
}

#plainView .description P {
  color: #7F96A1;
  height: 142px;
  width: 340px;
  background-color: #fff;
  border-bottom: 1px solid #999;
  border-right: 1px solid #999;
  border-top: 1px solid #fff;
  display: none;
  }

#plainView table.borderTopper {
  border-top: 1px solid #EEF2F4;
  }

#plainView .description p .descriptionHeader{
  font-size: 1em;
  border-bottom: 1px dotted #999;
  font-weight: 600;
  color: #666;
  line-height: 10px;
  padding-top: 10px;
  padding-bottom: 5px;
  margin-bottom: 10px;
  display: block;
}

#plainView .inlineButtons {
  height: 40px;
  text-align: right;
}

#plainView INPUT {
  margin-right: 5px;
}

#plainView .options {
  color: #0066CC;
  font-size: .9em;
}

#propertyView td.right {
  text-align: left;
  vertical-align: middle;

}

#propertyView td.left {
  text-align: left;
  vertical-align: top;
  width: 130px;
}

#placeholderLogoHome {
  margin: 10px 0px 0px 10px;
  position: absolute;
  top: 40px;
  left: 540px;
}

#placeholderIntroHome {
  margin: 10px 0px 0px 10px;
}

#portletLeft {
  width: 230px;
  vertical-align: top;
  background-color: #C3C3C3;
}

#adminDeployment select {
  width: auto;
  padding-right: 0px;
  margin-right: 0px;
}

#adminDeployment H1 {
  margin-left: 0px;
  padding-left: 0px;
}

#adminDeployment .rightAligned {
  text-align: right;
}

#adminDeployment .leftAligned {
  text-align: left;
}

#adminDeployment P {
  padding: 1px;
  font-size: .9em;
}

#adminDeployment TABLE.overview {
  border-bottom: 1px solid #999;
  border-left: 1px solid #CCC;
  border-right: 1px solid #CCC;
}

#adminDeployment P.sep {
  border-top: dotted 1px #B0B0A8;
}

.adminDeploymentLeftStyle {
  width: 40%;
  vertical-align: top;
  border-left: 1px #ccc solid;
  border-right: 1px #ccc solid;
}

#adminDeployment input, #adminDeployment select {
  font-size: 1em;
}

#adminDeployment table#adminDeploymentLeft {
  border: none;
}

#adminDeployment table#adminDeploymentLeft th {
  border-right: none;
}
#adminDeployment table#adminDeploymentLeft td {
  border-left: none;
}

#adminDeployment .formLabel {
  width: 150px;
  border: solid 1px #E5E5E5;
  padding-right: 5px;
  padding-left: 10px;
  vertical-align: middle;
  text-align: right;
  color: #545454;
  font-size: .9em;
  border-left: solid 1px #E5E5E5;
}


#adminDeployment textarea.propertyEdit {
  width: 410px;
  height: 150px;
}

TD.padding {
  padding: 15px;
}

#intro.admin {
  margin-top: 8px;
  margin-left: 8px;
}

.buttonImage {
  background-image: url(../../../images/buttonBack.jpg);
  background-repeat: repeat-x;
  background-position: left top;
  border-bottom: 1px solid #BBBBB4;
}

.buttonImageLeft {
  background-image: url(../../../images/buttonBackLeft.jpg);
  background-repeat: repeat-x;
  background-position: left top;
  border-bottom: 1px solid #BBBBB4;
}

.hierarchyText {
  font-size: .8em;
  line-height: 11px;
  }

.colapsablePanelData TD {
  border-bottom: solid 1px #DCDCDC;
  border-left: solid 1px #545454;
  padding: 3px;
  padding-right: 5px;
  padding-left: 18px;
  height: 15px;
  font-size: .8em;
  line-height: 11px;
  vertical-align: middle;
}

.colapsablePanelDataSub TD {
  padding: 3px;
  padding-right: 5px;
  padding-left: 15px;
  height: 15px;
  line-height: 11px;
  vertical-align: middle;
}

.hierarchyPanelDataSub TD {
  padding: 3px;
  padding-right: 5px;
  padding-left: 15px;
  height: 15px;
  line-height: 11px;
  vertical-align: middle;
}

.hierarchyPanelData TD {
  padding: 3px;
  padding-right: 5px;
  padding-left: 30px;
  height: 15px;
  line-height: 11px;
  vertical-align: middle;
}

#myprocessSub_1 .hierarchyPanelDataSub TD {
  padding: 3px;
  padding-right: 5px;
  padding-left: 18px;
  height: 15px;
  line-height: 11px;
  vertical-align: middle;
}


#myprocessSub_1 .colapsablePanelDataSub TD {
  padding: 3px;
  padding-right: 5px;
  padding-left: 18px;
  height: 15px;
  line-height: 11px;
  vertical-align: middle;
}

.colapsablePanelData TD {
  border-bottom: solid 1px #DCDCDC;
  border-left: solid 1px #DCDCDC;
  padding: 3px;
  padding-right: 5px;
  padding-left: 18px;
  height: 15px;
  font-size: .8em;
  line-height: 11px;
  vertical-align: middle;
}

.colapsablePanelData TD.checkbox {
  padding: 0;
}

.colapsablePanelData TH {
  text-align: left;
  color: #4F4F4F;
  margin: 0px;
  padding: 5px 3px 3px 18px;
  background-color: #EEE;
  border-top: 1px solid #FFF;
  border-bottom: solid 1px #B7B7B7;
  border-left: none;
  border-right: none;
  font-size: .8em;
  font-weight: 600;
}


.colapsablePanelData TH.checkbox {
  padding-left: 0;
  text-align: center;
}


#portletLeft EM {
  color: #000000;
  font-weight: normal;
  text-decoration: none;
  font-style: normal;
}

#portletLeft H3.static {
  font-family: "Trebuchet MS",verdana,tahoma,sans-serif;
  font-size: .9em;
  margin: 0px;
  color: #707070;
  padding: 5px 3px 4px 18px;
  background-color: #DFDADA;
  border-bottom: 1px solid #E5E5E5;
  background-image: none;
  border-top: 1px solid #E5E5E5;
  border-right: 1px  #E5E5DF;
}

#portletLeft .info {
  width: 60px;
}

#portletRight {
  border-left: solid 3px #545454;
  border-bottom: solid 3px #545454;
  background-color: #fff;
}

#portletRight.home {
  background-color: #eee;
}

#portletRight H1 {
  background-color: #FFF;
}

#portletRight H2 {
  background-color: #FFF;
  color: #545454;
  font-size: 1.4em;
  font-weight: 800;
  padding: 10px 5px 0px 10px;
}

#portletRight .nonTabAreaLeft {
  background-color: #FFF;
}

#portletRight .nonTabAreaRight {
  background-color: #FFF;
  width: 30px;
}

#portletRightBottom {
}

#portletRightBottom TD {
  height: 22px;
  font-size: .9em;
  padding-left: 4px;
  border-bottom: solid 1px #DCDCDC;
  border-left: solid 1px #DCDCDC;
  background-color: #fff;
}

#portletRightBottom TD.checkbox {
  height: 22px;
  font-size: .9em;
  padding-right: 2px;
  padding-left: 2px;
  text-align: center;
}

#portletRightBottom TH {
  height: 14px;
  border-bottom: solid 1px #DEDDDB;
  border-top: solid 1px #c7c7c7;
  border-left: solid 1px #E0E0D3;
  font-size: .8em;
  text-align: center;
  padding: 5px;
  padding-left: 4px;
  padding-right: 0;
  font-weight: 500;
  color: #333;
  text-align: left;
  background-color: #eee;
}

#portletRightTopSpace  {
  background-color: #545454;
  height: 36px;
}

#portletRightTop TD  {
  height: 22px;
  font-size: .9em;
  border-bottom: solid 1px #DCDCDC;
  border-left: solid 1px #DCDCDC;
  text-align: right;
  padding-right: 12px;
  white-space: nowrap;
}

#portletRightTop TH {
  background-color: #545454;
  color: #FFFFF1;
}

#portletRightTop TD.tableLeftAligned{
  padding-left: 12px;
  text-align: left;
}

#portletRightTop TH {
  border-bottom: solid 1px #43566A;
  font-size: .9em;
  text-align: center;
  font-weight: 500;
  color: #FFFFF1;
  border-right: solid 1px #536B84;
  border-left: solid 1px #80A0C3;
  padding-bottom: 2px;
  height: 15px;
}

#portletRightTop TH.leftColumn  {
  text-align: left;
  width:60%;
  padding-left: 12px;
}

#portletRight #portletRightContent {
  margin: 10px;
  padding: 10px;
}

#portletRight #portletRightContent P {
  font-size: .9em;
}

#portletRight H2 EM {
  color: #A7A69E;
}

#portletRight.admin {
  background-color: #FFF;
}

#portletRight.admin P {
  margin-bottom: 10px;
}
#processTabOff {
  position: relative;
  z-index:5;
  margin-left: -20px;
}

#processTabOn {
  position: relative;
  z-index:7;
  margin-left: -20px;
}

#productBrand {
  background-image: url(../../../images/productBrand2.jpg);
  background-repeat: no-repeat;
  position: absolute;
  top: -3px;
  left: 0px;
  width: 781px;
  height: 55px;
}

#overviewHeader H2.home {
  color: #666;
  margin-bottom: 5px;
  margin-top: 3px;
  margin-left: 4px;
  padding-left: 0;
  font-size: 16px;
  background: transparent;
}


#processTypeDesc p {
  display: none;
  width: 200px;
  margin: 4px 4px 4px 5px;
  font-size: .9em;
  padding: 10px;
  text-align: left;
  }

#actionDesc p {
  display: none;
  width: 100%;
  margin-left: 4px;
  color: #999;
  }

#sideBar {
  width: 200px;
  vertical-align: top;
  white-space: nowrap;
}

#sideBar DIV #assetBrowserDirectoryList, #sideBar DIV #assetBrowserFolderList, #sideBar DIV #assetBrowserCheck {
  margin-left: 0px;
  border: none;
}

#sideBar DIV {
  margin-left: 20px;
  border: solid 1px #CCC;
}

#sideBar DIV #assetBrowserDirectoryList, #sideBar DIV #assetBrowserFolderList, #sideBar DIV #assetBrowserCheck {
  margin-left: 0px;
  border-top: dotted 1px #CCC;
  border-bottom: none;
  border-left: none;
  border-right: none;
  font-size: 1.3em;
}

#sideBar TD {
  color: #545454;
  font-size: .8em;
  text-align: left;
}

#sideBar .overview {
  vertical-align: top;
  background-color: #F8F8F8;
  padding-left: 5px;
  padding-top: 6px;
  padding-bottom: 2px;
  white-space: normal;
  margin-left: 20px;
  border-bottom: none;
}

#sideBar .overview TD {
  padding-right: 2px;
  padding-top: 2px;
  padding-bottom: 2px;
}

#sideBar .overview TD EM {
  color: #000000;
  font-style: normal;
}

#sideBar .title {
  background-color: #E4E4E4;
  padding: 4px;
  border-left: solid 1px #FFFFFF;
  border-top: solid 1px #FFFFFF;
  border-bottom: solid 1px #A4A4A4;
  white-space: nowrap;
  font-weight: 600;
}

#sideBar TABLE {
  width: 160px;
}

#sideBar .action {
  text-align: right;
  padding-top: 15px;
  padding-right: 5px;
  padding-bottom: 10px;
}

#sideBar .formLabel {
  padding-left: 5px;
  padding-top: 10px;
  padding-bottom: 3px;
  vertical-align: middle;
}

#sideBar .formElement {
  vertical-align: middle;
  width: 142px;
  margin-bottom: 6px;
  font-size: 1em;
}

#hierarchyPane .formElement {
  vertical-align: middle;
  width: 142px;
  margin-bottom: 6px;
  font-size: 1em;
}

#sideBar .formElementInputText {
  margin-left: 5px;
  margin-right: 10px;
  margin-bottom: 5px;
  width: 135px;
}

#sideBar .formElementSmallSelect {
  vertical-align: middle;
  width: 50px;
  margin-bottom: 6px;
  font-size: 1em;
}

#sideBar .formElementRadio {
  margin: 2px 5px 4px 3px;
  vertical-align: middle;
}

#sideBar .hierarchyPaneClosed {
  display: none;
  border: none;
  margin-left: 0px;
}
#sideBar .hierarchyPaneOpen {
  display: block;
  border: none;
  margin-left: 0px;
}

#rightShadow {
  position: absolute;
  top: 85px;
  right: 0px;
}

#tabs {
  width:  463px;
  position: absolute;
  right: 0px;
  top: 59px;
  white-space: nowrap;
  height: 28px;
}

#taskDetail select {
  width: auto;
  padding-right: 0px;
  margin-right: 0px;
}

#taskDetail H1 {
  display: block;
  font-family: "Trebuchet MS",verdana,tahoma,sans-serif;
  font-size: 1.2em;
  font-weight: 800;
  color: #545454;
  margin: 0px;
  padding: 0px;
  vertical-align: middle;
}

#taskDetail H2 {
  display: block;
  font-family: "Trebuchet MS",verdana,tahoma,sans-serif;
  font-size: .9em;
  font-weight: 800;
  color: #545454;
  margin: 0px;
  padding: 0px;
  vertical-align: middle;
}

#taskDetail .rightAligned {
  text-align: right;
}

#taskDetail .leftAligned {
  text-align: left;
}

#taskDetail P {
  padding: 1px;
  font-size: .9em;
}

#taskDetail TABLE.overview {
  border-bottom: 1px solid #999;
  border-left: 1px solid #CCC;
  border-right: 1px solid #CCC;
  padding: 10px;
}

#taskDetail P.sep {
  border-top: dotted 1px #B0B0A8;
}


#userTools {
  line-height: 0px;
  position: absolute;
  left: 0px;
  top: 60px;
  padding-left: 67px;
  padding-top: 0px;
  padding-right: 15px;
  background: url(../../../images/userTools_back.gif);
  background-repeat: no-repeat;
  background-position: 18px 0px;
  border-left: none;
  white-space: nowrap;
  vertical-align: middle;
  font-size: .8em;
}

#userTools A {
  font-weight: bold;
  color: #E25406;
  text-decoration: none;
}

#userTools A:Link, #userTools A:Visited {
  font-weight: bold;
  color: #E25406;
  text-decoration: none;
}

#userTools A:Hover {
  font-weight: bold;
  color: #FF814F;
  text-decoration: none;
}

#userTools A:Active {
  font-weight: bold;
  color: #E25406;
  text-decoration: none;
  font-size: 1em;
}

#userTools IMG {
  display: inline;
  clear: none;
  vertical-align:middle;
  padding: 5px
}

#colapsablePanelContainer {
  width:320px;
  height: 215px;
  overflow: auto;
  overflow-x: hidden;
  width: 400px;
  border-bottom: solid 1px #949494;
  background-color: #C3C3C3;
}


#colapsablePanelContainer DIV {
  display: none;
}

#hierarchyPanelContainer {
 }

#hierarchyPanelContainer DIV {
  display: none;
}

ol#folderList {
  list-style-image: url(../../../images/icon_folderAsset.gif);
  list-style-type: circle;
  padding-top: 0px;
  margin-top: 5px;
  margin-left: 28px;
  padding-left: 5px;
}

#folderList li {
  margin-bottom: 4px;
  font-size: .9em;
  text-align: left;
  margin-left: 0;
  padding-left: 5px;
}


/* Action PopUp Styles */

.confirmNoteAction iframe {
  width: 350px;
  height: 245px;
  border: none;
  background-color: #F3F3ED;
  background-image: url(../../../images/assetBrowser_loading.gif);
  background-repeat: no-repeat;
  background-position: 85px 70px;
}

.confirmNoteAction {
  position: fixed;
  top: 0px;
  z-index: 100;
  height: 215px;
  width: 350px;
  left: 300px;
  border-left: solid 1px #FFF;
  border-bottom: solid 2px #CCC;
  border-right: solid 1px #DBDBD2;
  background-color: #E7E7DE;
}

div.confirmNoteAction {
  /* IE5.5+/Win - this is more specific than the NS4 version */
  left: expression( ( 300 + ( ignoreMe2 = ( document.documentElement && document.documentElement.scrollLeft ) ? document.documentElement.scrollLeft : document.body.scrollLeft ) ) + 'px' );
  top: expression( ( 0+ ( ignoreMe = ( document.documentElement && document.documentElement.scrollTop ) ? document.documentElement.scrollTop : document.body.scrollTop ) ) + 'px' );
  display: none;
  z-index: 100;
  height: 245px;
  width: 350px;
  background-color: #FFF;
  border-left: solid 1px #FFF;
  border-right: solid 1px #DBDBD2;
  margin-right: auto;
  margin-left: auto;
  border-bottom: solid 2px #999;
 }

.actionOK{
  top: 200px;
  padding-right: 15px;
  background-color: transparent;

  text-align:  right;
  vertical-align: middle;
  position: absolute;
  padding-top: 6px;
}


/* Confirmation Drop Down (NO FORM) - Begin________________________________________ */


.confirmAction iframe {
  width: 350px;
  height: 130px;
  background-color: #F3F3ED;
  border: none;
  background-image: url(../../../images/assetBrowser_loading.gif);
  background-repeat: no-repeat;
  background-position: 80px 15px;
}

.confirmAction {
  position: fixed;
  top: 0px;
  left: 300px;
}

div.confirmAction {
  /* IE5.5+/Win - this is more specific than the NS4 version */
  left: expression( ( 300 + ( ignoreMe2 = ( document.documentElement && document.documentElement.scrollLeft ) ? document.documentElement.scrollLeft : document.body.scrollLeft ) ) + 'px' );
  top: expression( ( 0+ ( ignoreMe = ( document.documentElement && document.documentElement.scrollTop ) ? document.documentElement.scrollTop : document.body.scrollTop ) ) + 'px' );

  display: none;
  z-index: 100;
  height: 130px;
  width: 350px;
  border-left: solid 1px #FFF;
  border-bottom: solid 2px #999;
  border-right: solid 1px #DBDBD2;
  background-color: #FFF;
  margin-right: auto;
  margin-left: auto;
 }

.confirmOK{
  top: 90px;
  padding-right: 5px;
  background-color: transparent;

  text-align:  right;
  vertical-align: middle;
  position: absolute;
  padding-top: 6px;
}

.actionOK .mainContentButton, .confirmOK .mainContentButton {
  border: 1px solid #fff;
  background-color: transparent;
}

.actionOK .buttonImage, .confirmOK .buttonImage {
  background-image: none;
}

.actionOK table td, .confirmOK table td {
  border-spacing: 4px;
  border-collapse: separate;
}

.popOK{
  position: relative;
  top: 30px;
  padding-right: 5px;
  background-color: transparent;

  text-align:  right;
  vertical-align: middle;
  padding-top: 6px;
}


.popOK .mainContentButton {
  border: 1px solid #ccc;
  background-color: #EAEAEA;
}

.popOK .buttonImage {
  background-image: none;
}

.popOK table td {
  border-spacing: 4px;
  border-collapse: separate;
}

.popOK a.mainContentButton:link,  .popOK a.mainContentButton:visited {
  color: #000;
  }

.popOK a.mainContentButton:hover, .popOK a.mainContentButton:active {
  background-color: #fff;
  color: #666;
}


.ok tr {
  background-color: #E7E7DE;
  }


#confirmHeader {
  background-image: url(../../../images/asset_back_top.jpg);
  background-position: left bottom;
  border-bottom: solid 1px #A6A5A5;
  padding: 3px 0 0 15px;
  text-align: left;
  height: 20px;
}

#confirmHeader H2 {
  color: #666;
  margin-bottom: 0px;
  margin-top: 3px;
  font-size: 12px;
}


/* Confirmation Drop Down (NO FORM) - End________________________________________ */




/*
#addNoteAction {
  position: relative;
  margin: 0px auto 0px auto;
  width: 350px;
  height: 240px;
  background: #999;
  border: 1px solid #ccc;
  z-index: 100;
  display: none;
  border-left: solid 1px #FFF;
  border-bottom: solid 2px #333;
  border-right: solid 2px #333;
  background-color: #acadad;
}
*/
#actionContent {
  position: relative;
  margin: 20px 20px 20px 20px;
  z-index: 60;
}

#actionContent H3 {
  font-size: 1em;
  font-weight: bold;
  color: #000;
  background-color: transparent;
  margin: 0px 0px 8px 0px;
  padding: 0px;
  background-image: none;
  border: 0px;
}

#actionContent H4 {
  font-size: 1.3em;
  font-weight: bold;
  color: #333;
  background-color: transparent;
  margin: 0px 0px 8px 0px;
  padding: 0px;
  background-image: none;
  border: 0px;
}


#actionContent P {
  font-size: .9em;
  line-height: 16px;
}

#actionContent P.alignRight {
  text-align: right;
}

#actionContent P LABEL {
  color: #333;
}

#actionContent P.error {
  color: #f00;
}

#actionContent textarea {
  width: 300px;
  height: 90px;
}

#actionContent .formElement {
  text-align: left;
  vertical-align: middle;
  font-size: .9em;
  width: 220px;
  margin: 4px 4px 4px 5px;
}

#newProcessButtonOpen {
  border-left: solid 1px #E1E1D6;
  background-repeat: repeat-x;
  background-image: none;
  background-image: url("../images/newProcessButton_bgOn.gif");
  padding-left: 10px;
}

#newProcessButtonClosed {
  border-left: solid 1px #E1E1D6;
  background-repeat: repeat-x;
  background-image: none;
  background-image: url("../images/newProcessButton_bgOff.gif");
  padding-left: 10px;
}

BODY.assetBrowser {
  background-color: #ccc;
  background-image: none;
  border: none;
  font-size: 75%;
  width: 100%;
}

BODY.assetBrowser TABLE.dataTable{
  width: 100%;
}

BODY.assetBrowser p.intro{
  margin: 20px 30px 30px 20px;
}

BODY.assetBrowserHierarchyIframe {
  background-image: none;
  background: #F3F3ED;
  }

BODY.assetBrowser .contentActions {
  background: transparent;
  border-top: 1px solid #fff;
  border-bottom: 0;
  margin-top: 0;
}


BODY.assetBrowser .contentActions h2 {
  padding-left: 11px;
  font-size: .9em;
  font-weight: normal;
}

BODY.assetBrowser .contentActions table {
  border-top: 0;
}

BODY.assetBrowser .contentActions a {
  margin-right: 23px;
}

.errorMessage {
  font-size: 1.1em;
  font-weight: bold;
  color: #f00;
  }

.keywordOptions {
  text-align: left;
  padding: 4px;
}

#addItem {
  width: 150px;
  text-align: right;
}



#reorderList {
  width: 150px;
  padding: 0px;
  border: none;
}

#reorderList TABLE {
  width: 150px;
  border: solid 1px #CFCFCF;
  padding: 0px;
}

.reorderListElement {
  color: #AAAAAA;
  font-size: .9em;
  padding: 2px;
}

.reorderListImg1 {
  width: 15px;
}

.reorderListImg1Up {
  background-image: url(../../../images/listup.gif);
  background-repeat: no-repeat;
  background-position: right center;
  width: 15px;
}

.reorderListImg1Down {
  background-image: url(../../../images/listdown.gif);
  background-repeat: no-repeat;
  background-position: right center;
  width: 15px;
}

.reorderListImg1Both {
  background-image: url(../../../images/listmove.gif);
  background-repeat: no-repeat;
  background-position: right center;
  width: 15px;
}

.reorderListImg1ReposUp {
  background-image: url(../../../images/listuptargetoff.gif);
  background-repeat: no-repeat;
  background-position: right center;
  width: 15px;
}

.reorderListImg1ReposDownOver {
  background-image: url(../../../images/listdowntargetover.gif);
  background-repeat: no-repeat;
  background-position: right center;
  width: 15px;
}

.reorderListImg1ReposUpOver {
  background-image: url(../../../images/listuptargetover.gif);
  background-repeat: no-repeat;
  background-position: right center;
  width: 15px;
}

.reorderListImg1ReposDown {
  background-image: url(../../../images/listdowntargetoff.gif);
  background-repeat: no-repeat;
  background-position: right center;
  width: 15px;
}

.reorderListImg2 {
  background-image: url(../../../images/listblank.gif);
  background-repeat: no-repeat;
  background-position: right center;
  width: 15px;
}

.reorderListImg2Show {
  background-image: url(../../../images/listremove.gif);
  background-repeat: no-repeat;
  background-position: right center;
  width: 15px;
}

.rlLink {
  font-size: 0.9em;
  text-decoration: none;
  padding-left: 0px;
}

#addtoListPane TABLE {
  width: 150px;
  border: solid 1px #CFCFCF;
  padding: 0px;
}

#nameToAdd {
  width: 132px;
}

.addToListPaneShow {
  border: none;
  padding-left: 15px;
  padding-top: 15px;
  padding-right: 15px;
  padding-bottom: 15px;
  width: 150px;
  background-color: #FFF;
  display: block;
}

.addToListPaneHidden {
  border: none;
  padding-left: 15px;
  padding-top: 15px;
  padding-right: 15px;
  padding-bottom: 15px;
  width: 150px;
  background-color: #FFF;
  display: none;
}

.sideBarHierarchy {
  margin-top: 3px;
  font-size: 1em;
  }

.alertMessage {
  text-align: right;
  width: 100%;
  padding-right: 15px;
  padding-left: 15px;
}

.assetBrowserAlertMessage {
  text-align: left;
  padding-left: 12px;
  font-size: .8em;
  font-weight: normal;
}

.toolBar_button {
  text-align: right;
  white-space: nowrap;
  vertical-align: middle;
}

.toolBar_button img {
  vertical-align: -2px;
}

.frameBorderBottom {
  border-bottom: 1px dotted #A4A4A4;
}


.dataTable td.missing {
  border-left: solid 8px #f00;
  padding-left: 10px
}

.dataTable td.fixed {
  border-left: solid 8px #999;
  padding-left: 10px
}

#intro td.missing {
  border-left: solid 8px #f00;
  padding-left: 10px
}


#formKey {
  width: 100%;
  text-align: left;
}


td.propertyLabelWidth {
  width: 400px;
  }

.tableWrapper {
  width: 100%;
  }

.tableWrapper td.columnWidth {
  width: 50%;
  }

.jointText {
  color: #999;
  margin-top: 5px;
  margin-left: 20px;
}

.actionNumber {
  font-size: 1em;
  color: #999;
  font-weight: bold;
}

.actionLabel {
  font-size: .8em;
  }


.actionLabel a:link {
  color: #0c3d6f;
  }

.actionLabel a:visited {
  color: #0c3d6f;
  }

.actionLabel a:hover {
  color: #003870;
  }

.actionLabel a:active {
  color: #0c3d6f;
  }

#assetBrowserClose {
  float: right;
  margin: 9px 23px 0 0;
  }

a.assetCloseButton {
  width: 16px;
  height: 16px;
  background-image: url(../../../images/asset_browser_close.jpg);
  display: block;
  text-decoration: none;
  border: 0;
  }

a.assetCloseButton:link {
  text-decoration: none;
  border: 0;
  }

a.assetCloseButton:visited {
  }

a.assetCloseButton:hover {
  background-image: url(../../../images/asset_browser_close_on.jpg);
  }

a.assetCloseButton:active {
  }

.assetBrowserDisplayResults {
  background-color: #F3F2ED;
  width: 656px;
  margin: 0 auto 0 auto;
  border-left: 1px solid #AAAEB0;
  border-bottom: 1px solid #A3A3A3;
  }

.matches {
  border-right: 1px solid #ccc;
  border-bottom: 1px solid #ccc;
  padding: 6px;
  vertical-align: middle;
  margin-right: 5px;
  }

A.toolbarButton:link, A.toolbarButton:visited {
  color: #fff;
  font-weight: bold;
  margin-right: 2px;
  padding: 0 5px 0 5px;
  border-top: 1px solid #CCC;
  border-left: 1px solid #CCC;
  border-right: 1px solid #808077;
  border-bottom: 1px solid #808077;
  background-color: #DBA046;
  background-repeat: repeat-x;
  background-position: left bottom;
  position: relative;
  white-space: nowrap;
/*  text-shadow: 2px 2px 0px #CCC;*/
}

A.toolbarButton:hover, A.toolbarButton:active {
  color: #fff;
  background-color: #F2B557;
}

A.standardButton:link, A.standardButton:visited {
  color: #494679;
  font-weight: normal;
  margin-right: 4px;
  padding: 1px 10px 1px 10px;
  border-top: 1px solid #CCC;
  border-left: 1px solid #CCC;
  border-right: 1px solid #808077;
  border-bottom: 1px solid #808077;
  background-color: #F7F7F7;
  background-image: url(../../../images/standardButtonBack.jpg);
  background-repeat: repeat-x;
  background-position: left bottom;
  position: relative;
  white-space: nowrap;
/*  text-shadow: 2px 2px 0px #CCC;*/
}

A.standardButton:hover, A.standardButton:active {
  color: #343257;
  background-color: #E7E6DF;
  background-image: url(../../../images/standardButtonBackOn.jpg);
}

A.standardButtonDisabled:link, A.standardButtonDisabled:visited {
  color: #CCC;
  font-weight: normal;
  margin-right: 4px;
  padding: 1px 10px 1px 10px;
  border-top: 1px solid #DCDCDC;
  border-left: 1px solid #DCDCDC;
  border-right: 1px solid #BCBAB6;
  border-bottom: 1px solid #BCBAB6;
  background-color: #FFF;
  background-image: url(../../../images/standardButtonBack.jpg);
  background-repeat: repeat-x;
  background-position: left bottom;
  position: relative;
  white-space: nowrap;
/*  text-shadow: 2px 2px 0px #CCC;*/
}

A.standardButtonDisabled:active {
  color: #CCC;
  background-color: #FFF;
}

.processControl {
  color: #999;
  }

.processControl img {
  margin-left: 2px;
  margin-right: 4px;
  vertical-align: -3px;
}

.processSeperate {
  padding-left: 10px;
  border-left: 1px solid #666;

}

.processType {
  height: 150px;
  width: 250px;
  overflow: auto;
  overflow-x: hidden;
}

.tasksBorder {
  border-right: solid 1px #E5E5DF;
  border-bottom: solid 1px #FFF;
}

.overviewText {
  font-size: .8em;
}

.homeLeftWidth {
  width: 20px;
  text-align: center;
}

.homeCenterWidth {
  width: 250px;
}

.homeRightWidth {
  width: 150px;
}

.homePanelContent {
  width: 100%;
  height: 100px;
  overflow: auto;
  overflow-x: hidden;
  background-color: #eee;
  border-bottom: solid 1px #898989;
}


.homePanelContentBottom {
  width: 100%;
  height: 100px;
  overflow: auto;
  overflow-x: hidden;
  background-color: #eee;
}

.assetIcon {
  margin-left: 2px;
  margin-right: 4px;
}

iframe {
  border-style: none;
  }

th.subhead {
  color: #666;
  }


.dataTable td.noData {
  padding: 15px;
  color: #757575;
  text-align: center;
}

.colapsablePanelData td.noData {
  padding: 15px;
  color: #757575;
  text-align: center;
}

#basicSelect, #campaignSelect {
  display: none;
  }

#processOption table {
  display: none;
  }

.resultNav {
  width: 155px;
  text-align: right;
  }

.resultSep {
  border-right: solid 1px #666;
  padding-right: 10px;
  font-weight: bold;
  }

.resultTally {
  color: #333;
  }

.resultCount {
  text-align: right;
  }

.formList {
  border-bottom: 1px dotted #ccc;
  margin-bottom: 0;
  padding-bottom: 5px;
  }



/* Merch UI addition */

#assetListHeader {
  margin:0;
  height:22px;
  width:auto;
  background:url(../../../images/bg_pinstripe.gif);
  border-bottom:1px solid #7788A2;
  border-top:1px solid #FFF;
  border-left: 1px solid #AEC3E2;
  border-right: 1px solid #ccc;
  line-height:normal;
  color:#545454;
  }

#chooseAssetType #assetListHeader {
  margin:0 0 10px 0;
  height:22px;
  width:100%;
  background:none;
  border:none;
  line-height:normal;
  color:#545454;
  }
#assetListSubHeader {
  margin:0;
  height:22px;
  width:auto;
  background:#ccc;
  border-bottom:1px solid #7788A2;
  line-height:normal;
  color:#545454;
  border-left: 1px solid #AEC3E2;
  border-right: 1px solid #ccc;
  }
#assetListSubHeader select,
#assetListSubHeader input,
#assetListHeader select,
#assetListHeader input {
  font-size:10px;
  }
#assetListHeaderLeft,
#assetListSubHeaderLeft {
  margin:0;
  padding:5px 0 0 5px;
  font-weight:normal;
  font-size:.9em;
  text-align:left;
  line-height: normal;
  }
#assetListHeaderLeft input.selectAll {
  margin-left:-1px !important;
  margin-left:-5px;
  font-size:11px;
  }
#assetListSubHeaderLeft input.selectAll {
  margin-left:-2px !important;
  margin-left:-6px;
  font-size:11px;
  }
#assetListSubHeaderLeft .selectAllContainer {
  display:inline;
  float:left;
  margin:-2px 5px 0 -5px;
  padding:2px 0 0 6px;
  width:14px;
  height:20px;
  background-color:#999;
  border-right:1px solid #666;
  }
#assetListSubHeaderLeft .label {
  float:left;
  }
#assetListSubHeaderLeft input {
  margin-top:-3px;
  }
#assetListHeaderLeft .selectAllContainer {
  display:inline;
  float:left;
  margin:-2px 5px 0 -5px;
  padding:2px 0 0 5px;
  width:15px;
  height:20px;
  background-color:#E6E6E6;
  border-right:1px solid #B1C0D5;
  }
#assetListHeaderRight,
#assetListSubHeaderRight{
  float:right;
  margin:0;
  padding:2px 5px 0 0;
  font-size:.9em;
  font-weight:normal;
  }
#assetListHeaderRightRefresh{
  float:right;
  margin:0;
  padding:7px 5px 0 0;
  font-size:.9em;
  font-weight:normal;
  }
#assetListHeader select,
#assetListHeader .buttonSmall,
#assetListHeader .buttonSmall {
  vertical-align:0 !important;
  vertical-align:-4px;
  }
#assetListHeaderRight a.buttonSmall {
  display:block;
  top:2px;
  vertical-align:-2px !important;
  }
.subHeaderText {
  vertical-align:50%;
  }
#assetListSubHeaderLeft .subHeaderText {
  float:left;
  margin-top:0px;
  }

#footerCount {
  margin:0;
  height:22px;
  background:#E4EDFC;
  border-bottom:1px solid #999;
  line-height:normal;
  color:#545454;
  }
#footerCount a {
  border-right:1px solid #CCC;
  text-decoration:none;
  padding-left:5px;
  padding-right:5px;
  }
#footerCount #footerCountRight a.current {
  text-decoration:underline;
  color:#333;
  cursor:default;
  }
#footerCount a:link,
#footerCount a:visited {
  color:#1374E0;
  }
#footerCount a:hover,
#footerCount a:active {
  color:#F26A1E;
  }
#rightPaneFooterLeft,
#footerCountLeft {
  margin:0;
  padding:2px 0 0 5px;
  font-size:.9em;
  font-weight:normal;
  text-align:left;
  }
#rightPaneFooterRight,
#footerCountRight {
  float:right;
  margin:0;
  padding:2px 5px 0 0;
  font-size:.9em;
  font-weight:normal;
  }


#assetListContentTable {
  margin:0;
  padding:0;
  width:100%;
  background:#fff;
  border-spacing:0;
  border-left: 1px solid #AEC3E2;
  border-right: 1px solid #ccc;
  }
#assetListContentTable td  {
  padding:0;
  width:100%;
  border-bottom:1px dotted #999;
  border-spacing:0;
  }
#assetListContentTable td.checkboxCell {
  width:10px;
  background:#ccc;
  padding: 3px;
  border-right:1px solid #666;
  }
#assetListContentTable td.emptySelectCell {
  width:10px;
  background:#ccc;
  border-right:1px solid #666;
  border-top:none;
  }
#assetListContentTable td.descriptionCell {
  display:block;
  margin:0;
  padding:3px 0px 4px 40px;
  width:auto;
  border-top:1px dotted #ccc;
  }
#assetListContentTable td.descriptionCell ul {
  margin:0;
  padding:0;
  list-style:none;
  }
#assetListContentTable td.descriptionCell li {
  margin-bottom:3px;
  color:#333;
  }
#assetListContentTable td.nameCell a {
  display:block;
  margin:0;
  padding:4px 0px 3px 5px;
  width:auto !important;
  width:100%;
  font-size:.9em;
  text-decoration:none;
  }
#assetListContentTable td.nameCell a:link,
#assetListContentTable td.nameCell a:visited  {
  color:#1374E0;
  }
#assetListContentTable td.nameCell:hover,
#assetListContentTable td.nameCell a:hover,
#assetListContentTable td.nameCell a:active {
  background:#EFF5FD;
  color:#0C498C;
  }
#assetListContentTable td.selected,
#assetListContentTable td.nameCell a.selected:link,
#assetListContentTable td.nameCell a.selected:visited,
#assetListContentTable td.nameCell a.selected:hover,
#assetListContentTable td.nameCell a.selected:active {
  background:#DDE7F7;
  color:#000;
  }
#scrollContainer {
  width:100%;
  background:#fff;
  overflow: auto;
  }
#scrollFooter {
  width:100%;
  border-spacing:0;
  border-left: 1px solid #AEC3E2;
  border-right: 1px solid #ccc;
  }
#chooseAssetType #scrollFooter {
  display:none;
  }

/*.go {
  font-size: 1.2em;
  font-weight: 600;
  padding-top: 0px;
  padding-right: 6px;
  padding-bottom: 0px;
  padding-left: 6px;
  margin-left: 10px;
  margin-bottom: 0px;
  border-left: solid 1px #999;
  background: url(../../../images/icon_go.gif);
  background-position: right bottom;
  background-repeat: no-repeat;
} */

#formSubmitButtons {
  float:right;
  padding:20px 20px 20px 20px;
  border: none;
  }

A.button:link, A.button:visited {
  color: #494679;
  font-weight: normal;
  margin-right: 4px;
  padding: 1px 10px 1px 10px;
  border-top: 1px solid #CCC;
  border-left: 1px solid #CCC;
  border-right: 1px solid #808077;
  border-bottom: 1px solid #808077;
  background-color: #F7F7F7;
  background-image: url(../../../images/standardButtonBack.jpg);
  background-repeat: repeat-x;
  background-position: left bottom;
  position: relative;
  white-space: nowrap;
/*  text-shadow: 2px 2px 0px #CCC;*/
}

A.button:hover, A.button:active {
  color: #343257;
  background-color: #E7E6DF;
  background-image: url(../../../images/standardButtonBackOn.jpg);
}

#searchOptions TD {
  color: #fff;
  font-size: .9em;
  text-align: left;
}

#searchOptions .formElement {
  width:auto;
}

/* Logo Header
------------------------------------------------------------------------------*/

#globalHeader {
  margin:0;
  }
#logoHeader {
  height:35px;

  }
#logoHeaderLeft {
  padding:9px 0px 0px 55px;
  height:35px !important;
  height:26px;
  background:url(../../../images/BCC_home/bg_logoHeaderLeft.gif) no-repeat left top;
	cursor: pointer;
  }
#logoHeaderRight {
  float:right;
  padding:15px 8px 0px 8px;
  width:auto;
  height:15px !important;
  height:20px;
  font-size:1.3em;
  font-weight: bold;
  text-align:right;
  text-transform:inherit;
  color:#999;
  }
#logoHeaderRight span {
  padding:0 5px 0 5px;
  color:#dadada;
  }
#logoHeaderRight a {
  color:#666;
  padding-left:22px;
  }
#logoHeaderRight a.home:link,
#logoHeaderRight a.home:visited {
  background:url(../../../images/BCC_home/icon_bccHome_off.gif) no-repeat left top;
  }
#logoHeaderRight a.home:hover,
#logoHeaderRight a.home:active {
  color:#333;
  background:url(../../../images/BCC_home/icon_bccHome_hover.gif) no-repeat left center;
  }
#logoHeaderRight a.logout:link,
#logoHeaderRight a.logout:visited  {
  background:url(../../../images/BCC_home/icon_logout_off.gif) no-repeat left center;
  }
#logoHeaderRight a.logout:hover,
#logoHeaderRight a.logout:active {
  color:#333;
  background:url(../../../images/BCC_home/icon_logout_hover.gif) no-repeat left center;
  }

#logoHeader h1 {
  margin: 0;
  padding: 5px 0 0 0;
  font-size: 1.6em;
  color: #424242;
  background-color:transparent;
  }
  #logoHeaderRight a.utils:link,
  #logoHeaderRight a.utils:visited  {
    background:url(../../../images/BCC_home/icon_utils_off.gif) no-repeat left top;
    }
  #logoHeaderRight a.utils:hover,
  #logoHeaderRight a.utils:active {
    color:#333;
    background:url(../../../images/BCC_home/icon_utils_hover.gif) no-repeat left top;
    }
  #utilDD {
    display: block;
    position: absolute;
    top: 35px;
    right: 140px;
    width: 90px;
    border-color: #244270 #17263E #17263E #17263E;
    border-width: 2px 1px 1px 1px;
    border-style: none solid solid solid;
    background-color: #1E3A65;
    padding-bottom: 3px;
    z-index: 100;
  }

  #utilDD.hide {
    display: none;
  }

  #utilDD ul, #utilDD li {

    padding: 0;
    margin: 0;
    list-style: none;
  }

  #utilDD li{

    display: block;

  }

  #utilDD li a{
    color: #FFF;
    display: block;
    width: 100%;
    height: auto;
    padding: 0;
    margin: 0;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 5px;
    padding-right: 5px;
    font-size: .9em;
  }

  #utilDD li a:link
  {
    color: #FFF;
  }

  #utilDD li a:hover
  {
    color: #FEFF26;
  }
  #contentHeaderRight div {
  color:#A4A1A5;
  float:left;
  padding:10px 0 10px 20px;
  }
  #contentHeaderRight div.currentProject {
  border-left:1px solid #D6D6D6;
  margin-left:10px;
  padding-left:10px;

  }
  #contentHeaderRight div.currentProject span,
  #contentHeaderRight div.currentTask span {
  color:#000000;
  }
  #contentHeaderRight div a {
  font-weight:bold;
  text-decoration:none;
  }
  #contentHeaderRight div a:link, #contentHeader h2 a:visited {
  color:#003366;
  }
  #contentHeaderRight div a:hover, #contentHeader h2 a:active {
  color:#336699;
  }
  #contentHeaderRight .buttonSmall {
  float:left;
  margin-top:5px !important;
  }
  #contentHeaderRight {
  float:right;
  padding:0 10px 0 30px;
  }
  
/* Top Navigation Tabs
------------------------------------------------------------------------------*/

#topNav {
  float:left;
  margin:0;
  width:100%;
  background:url(../../../images/bg_pinstripe2.gif) left bottom;
  line-height:normal;
  }
#topNav ul {
  margin:0;
  padding:12px 0 0 12px;
  list-style:none;
  }
#topNav li {
  float:left;
  margin:0 0 0 -3px;
  padding:0 0 0 4px;
  background:url(../../../images/tabLeft.gif) no-repeat left top;
  }
#topNav li a span {
  margin-left:5px;
  color:#7B8995;
  }
#topNav li.current {
  position:relative;
  z-index:2;
  margin:0px -6px;
  padding:0 0 0 6px;
  background:url(../../../images/tabLeftCurrent.png) no-repeat left top !important;
  background:url(../../../images/tabLeftCurrent.gif) no-repeat left top;
  }
#topNav a {
  display:block;
  padding:4px 25px 3px 8px;
  background:url(../../../images/tabRight.gif) no-repeat right top;
  text-decoration:none;
  }
#topNav li.current a {
  padding:5px 25px 3px 12px;
  background:url(../../../images/tabRightCurrent.png) no-repeat right top !important;
  background:url(../../../images/tabRightCurrent.gif) no-repeat right top;
  color:#000;
  cursor:default;
  }
#topNav a:link, #topNav a:visited {
  color:#355779;
  }
#topNav a:hover, #topNav a:active {
  background:url(../../../images/tabRight_on.gif) no-repeat right top;
  color:#F26A1E;
  }


/* HEADER AREA FONT FIX */

#globalHeader{
  font-size: 0.8em !important;
  padding: 5px 10px;
  border-bottom: 1px solid #d6d6d6
  }



/* BCC HOMEPAGE SPECIFIC STYLES */


.BCCContentTable
{
background: #FFFFFF;
font-size: .9em !important;

}

.BCCContentTable td
{
  padding: 5px;
  vertical-align: top;
  text-transform: inherit;
}

.BCCContentTable td.innerElement
{
  padding: 0px;
}
#projectResults .BCCContentTable td.innerElement
{
  border-bottom: 1px solid #ccc;
}
#otherOperations{

}

#opHeaderMain td,
#tdlHeaderMain td
{
  border: 0;
  padding-top: 0 !important;
  padding-bottom: 0 !important;
  padding-right: 0 !important;
  width: 33%;
}


#opHeaderMain
{
  height: 30px;
  background: #c3d8be;
}

#tdlHeaderMain
{
  height: 30px;
  background: #ccdcea;

}

#opHeaderMain h2, #tdlHeaderMain h2
{
  margin: 6px 0 0;
  padding: 0;
  padding-bottom: 0;
  padding-left: 9px;
  font-size: 1.2em;
  font-weight: bold;
}

#opHeaderMain h2
{
  /*color: #6E839F*/
  color: #333 !important;
}
#tdlHeaderMain h2 {
/*color: #6A9745*/
color: #333 !important;
}


.opHeaderOpen{
  background: #dfecdc;
  border: 0;
  height: 20px !important;
}
.tdlHeaderOpen
{
  background: #fff;
  border: 0;
  height: 20px !important;
}

.opHeaderClose{
  background: #dfecdc;
  border: 0;
  height: 20px !important;

}
.tdlHeaderClose
{
  background: #fff;
  border: 0;
  height: 20px !important;

}

.tdlHeaderOpen a.projectTitle, .opHeaderOpen a.projectTitle, .opHeaderOpen a.opTitle
{
  margin-left: 5px;
  padding-left: 20px;
  background-image: url(../../../images/BCC_home/icon_openNavItem.gif);
  background-repeat: no-repeat;
  background-position: 0 50%;
  font-size: 1.1em;
  font-weight: bold;
}

.tdlHeaderOpen a.projectTitle:hover, .opHeaderOpen a.projectTitle:hover, .opHeaderOpen a.opTitle:hover
{
  padding-left: 20px;
  background-image: url(../../../images/BCC_home/icon_openNavItem_hover.gif);
  background-repeat: no-repeat;
  background-position: 0 50%;
}


.tdlHeaderClose a, .tdlHeaderClose .gotoproject a{
color: #666 !important;
}

.tdlHeaderClose a:hover, .tdlHeaderClose .gotoproject a:hover { color: #000 !important; }

.tdlHeaderClose a.projectTitle, .opHeaderClose a.opTitle
{
  margin-left: 5px;
  padding-left: 20px;
  background-image: url(../../../images/BCC_home/icon_closeNavItem.gif);
  background-repeat: no-repeat;
  background-position: 0 50%;
color: #666;
font-weight: bold;
}

.tdlHeaderClose a.projectTitle:hover, .opHeaderClose a.opTitle:hover
{
  padding-left: 20px;
  background-image: url(../../../images/BCC_home/icon_closeNavItem_hover.gif);
  background-repeat: no-repeat;
  background-position: 0 50%;
color: #000;
}

a.opItem
{
  margin-left: 7px;
  padding-left: 18px;
  background-image: url(../../../images/arrow.gif);
  background-repeat: no-repeat;
  background-position: 0 50%;
}

a.opItem:hover
{
  margin-left: 7px;
  padding-left: 18px;
  background-image: url(../../../images/arrow_over.gif);
  background-repeat: no-repeat;
  background-position: 0 50%;
}


	#projectResults,
	#activitySources{
	    border-color: #798AA0;
	    border-width: 1px;
	    border-style: solid;
	    background-color: #E4E4E4;
		
	}
  #newContentHolder
  {
    margin-top: 20px;
  }

  #newMainContent
  {padding: 10px;
  }

/* content header styles from MerchUI */

h3.contentHeader {
  padding-top:5px;
  padding-left:0;
  width:40%;
  }

  #contentHeader {
    overflow: hidden;
  padding:0 5px 0 5px;
  font-size: .9em;
  }

  #contentHeader h2 a {
  font-weight:bold;
  text-decoration:none;
  }
#contentHeader h2 a:link, #contentHeader h2 a:visited {
  color:#036;
  }
#contentHeader h2 a:hover, #contentHeader h2 a:active {
  color:#369;
  }

  #contentHeader h2
{
    margin-top: 0pt;
    margin-right: 0pt;
    margin-bottom: 3px;
    margin-left: 10px;
    padding-top: 0pt;
    padding-right: 0pt;
    padding-bottom: 0pt;
    padding-left: 0pt;
    font-size: 1em;
    font-weight: normal;
}

.taskTitle
{
  background-image: url(../../../images/BCC_Home/icon_task.gif);
  background-position: 0 50%;
  padding-left: 20px;
  padding-top: 4px;
  padding-bottom: 4px;
  background-repeat: no-repeat;
}
.BCCContentTable p{
padding-bottom: 10px;
margin-left: 5px;
}

.assignTo{
float:right;
padding-bottom: 2px !important;
width: 210px;
}

.assignTo select {
width: 145px;
}

.assignTo .buttonSmall
{
  display: inline-block !important;
  margin: 0 !important;
  padding: 2px 5px 2px 5px !important;
  background: #fff url(../../../images/bg_button.gif) repeat-x left bottom !important;
  border: 1px solid !important;
  border-color: #C2C6CA #A1A4A7 #A1A4A7 #C2C6CA !important;
  font-weight: normal !important;
  text-decoration: none !important;
  text-align: center !important;
  white-space: nowrap;
  color: #000;
  /*font-family: small-caption;*/
}


.assignTo .buttonSmall:hover,
.assignTo .button:hover,
.assignTo .buttonSmall:active,
.assignTo .button:active {
  display:inline !important;
  background:#fff url(/bcc_images/bg_buttonOn.gif) repeat-x left bottom !important;
}

td.gotoproject
{
  width: 20% !important;
  text-align: right;
  padding-right: 10px;
}
td.gotoproject a:link,
td.gotoproject a:visited{
  font-weight: bold;
  color: #2a799f;
  font-size: 1.1em
}
td.gotoproject a:hover,
td.gotoproject a:active{
  color: #145270;
}
td.whatToShow
{
  background-color: #ccdcea;
  color: #7F7F7F;
  padding-left: 13px;
}

.projectTodoSelectedPage
{
  color: #000000;
}

.projectTodoPrevPaging, .projectTodoNextPaging
{
  font-size: small;
  font-weight: bold;
}

#projectTodoPaging
{
  text-align: center
}

#tdlOpenAll, #opOpenAll
{
  height: 16px;
  width: 18px;
  float: right;
  margin-right: 0px;
  background-image: url(../../../images/BCC_home/allopenclose.gif);
  background-repeat: no-repeat;
  background-position: -19px 0px;


  background-repeat: no-repeat;
  background-position: 0px 0px;
  padding-bottom:0px;
}

#tdlOpenAll:hover, #opOpenAll:hover
{
  background-position: 0px -16px;
}

#tdlCloseAll, #opCloseAll{
	height: 16px;
	width: 21px;
	float: right;
	margin-right: 0px;
  background-image: url(../../../images/BCC_home/allopenclose.gif);
  background-repeat: no-repeat;
  background-position: -17px 0px;

}

#tdlCloseAll:hover, #opCloseAll:hover{

background-position: -17px -16px;
}

/* Help Box Styles
------------------------------------------------------------------------------*/

/* position the whole box that contains the toggle link and the bubble*/

.positionBox {
  margin-top:0px;
  margin-bottom:30px;
  width:auto;
  height:auto;
  text-align:right;
  }

/* CSS for the box starts here */

#toggleDetails1, #toggleDetails3
{
  float: right;
  display: block;
  padding: 0 !important;
  margin: 0 0px 0px 0 !important;
  padding-top: 5px !important;
  padding-left: 0px !important;
  width: 25px !important;
  background-image: url("/atg/images/icon_helpText_off.gif");
  background-repeat: no-repeat;
  background-position: right bottom;
  cursor: pointer;
  z-index: 5000;
}
html>body #toggleDetails1 {
  margin: 0 0px 8px 0 !important;
  }
#toggleDetails3 {
  margin-right:2px !important;
  }
#toggleDetails1 a, #toggleDetails3 a{
  display:block;
  padding-top:0px;
  padding-bottom:0px;
  height:18px;
  text-decoration:none;
  z-index:5000;
  }
.toggleDetailsOff {
  background-image:url("/atg/images/icon_helpText_off.gif") !important;
  background-position:right bottom;
  background-repeat:no-repeat;
  }
.toggleDetailsOn {
  background-image:url("/atg/images/icon_helpText_on.gif") !important;
  background-position:right bottom;
  background-repeat:no-repeat;
  }
.noBox {
  display:none;
  }

.showBox{
display: block;

}

.box {
  padding:0px 0 0 10px;
  margin:23px 0px 10px 5px;
  background: url("/atg/images/box_01.gif") no-repeat 0px 0px;
  text-align:left;
  }
.box h3 {
  margin:0 0 0 0;
  padding:30px 0 10px 0;
  height:10px;
  background:url("/atg/images/box_02.gif") top right no-repeat !important;
  font-size:1em;
  color:#666;
  border:none;
  }
.box p,
.box dl {
  margin:0px 0px 0px -10px !important;
  padding:0px 10px 5px 10px !important;
  border:solid #B2B2B2;
  border-width:0 1px 0 1px;
  background:#FFFFFF;
  color:#666;
  font-size:.9em;
  line-height:1.5em;
  }
.box div {
  margin:0 0 0 -10px;
  padding-left:10px;
  background:url("/atg/images/box_01.gif") bottom left no-repeat;
  }
.box a.more {
  text-decoration:none;
  display:block;
  text-align:right;
  height:10px;
  padding:5px 10px 10px 0;
  font-size:.9em;
  background:url("/atg/images/box_02.gif") bottom right no-repeat;
  }
.box dt {
  font-weight:bold;
  }
.box dd {
  margin:0 0 0 10px;
  padding:0;
  }

/* Drop UP help bubble box
------------------------------------------------------------------------------*/

.boxUp
{
  padding: 2px 0px 0 -10px;
  margin: 0px 0px 0px 0px !important;
  text-align: left;
  background-image: url("/atg/images/box_04.gif");
  background-repeat: no-repeat;
  background-position: top left;
}
.boxUp h3
{
  margin: 0px 0px 0 10px;
  padding: 4px 0 0 5px;
  height: 15px;
  font-size: 1em;
  color: #666;
  border: none;
  background-image: url("/atg/images/box_03.gif");
  background-repeat: no-repeat;
  background-position: top right;
}
.boxUp p,
.boxUp dl
{
  margin: 0px 0px 0px 0px !important;
  padding: 5px 10px 0px 10px !important;
  border: solid #B2B2B2;
  border-width: 0 1px 0 1px;
  background: #FFFFFF;
  color: #666;
  font-size: .9em;
  line-height: 1.5em;
}
.boxUp div
{
  margin: 0 0 0 0px;
  background-image: url("/atg/images/box_04.gif");
  background-repeat: no-repeat;
  background-position: left bottom;
  padding: 0 0 0 10px;
}
.boxUp a.more
{
  text-decoration: none;
  display: block;
  text-align: right;
  height: 10px;
  padding: 10px 10px 30px 0;
  margin: 0 0 0 0px;
  font-size: .9em;
  background-image: url("/atg/images/box_03.gif");
  background-repeat: no-repeat;
  background-position: right bottom;
}
.boxUp dt {
  font-weight:bold;
  }
.boxUp dd {
  margin:0 0 0 10px;
  padding:0;
  }

.disabled,
.disabled:link,
.disabled:visited{
        display:inline !important;
        color:#999 !important;
        }
.disabled:hover,
.disabled:active{
        display:inline !important;
        color:#999 !important;
        opacity:50%;
        cursor:default;
        }

input.dateField {
  width: 80px;
}

input.timeField {
  width: 30px;
}

.dateTimeLabel {
  color: #545454;
  font-size: .9em;
}
</dsp:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/templates/style/css/style.jsp#3 $$Change: 659334 $--%>

