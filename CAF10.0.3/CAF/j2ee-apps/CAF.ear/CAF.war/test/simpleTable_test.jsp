<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="caf" uri="http://www.atg.com/taglibs/caf" %> 
<%@ page import="java.util.ArrayList,
                 java.util.Date"%>

<%!
  public class TestData {
    public TestData(String name, Date date, Integer number) {
      mName   = name;
      mDate   = date;
      mNumber = number;
    }
    private String  mName;
    private Date    mDate;
    private Integer mNumber;
    public String  getName()   { return mName;   }
    public Date    getDate()   { return mDate;   }
    public Integer getNumber() { return mNumber; }
  }
%>

<%
  ArrayList list = new ArrayList();
  
  list.add(new TestData("Sashimi", new Date(), new Integer(1)));
  list.add(new TestData("Hamachi", new Date(), new Integer(2)));
  list.add(new TestData("Tamago", new Date(), new Integer(4)));
  list.add(new TestData("Sake", new Date(), new Integer(9)));
  list.add(new TestData("Maguro", new Date(), new Integer(15)));
  list.add(new TestData("Kanikama", new Date(), new Integer(236)));
  pageContext.setAttribute("items", list);
%>

<dspel:page>
<html>

<head>
<link rel="stylesheet" href="../atg-ui.css">

<script language="JavaScript">
function testSort(key) {
  alert("Key for sorting is " + key);
}
</script>
</head>

<body>

<c:set var="title1" value="Name"/>
<c:set var="title2" value="Date"/>
<c:set var="title3" value="Number"/>
<c:set var="title4" value="Misc"/>

<caf:table id="table1" width="600px" cellPadding="3" border="0">
  <caf:tableHead>
     <caf:tableHeadCell title="${title1}" sortTitle="true" sortFunctionName="testSort" sortKey="name" />
     <caf:tableHeadCell title="${title2}" sortTitle="true" sortFunctionName="testSort" sortKey="date" />
     <caf:tableHeadCell title="${title3}" sortTitle="true" sortFunctionName="testSort" sortKey="number" />
     <caf:tableHeadCell css="" title="${title4}" sortTitle="false" />
  </caf:tableHead>
  <caf:tableBody var="item" items="${items}" alternateRowShading="true">
    <caf:tableRow>
      <caf:tableCell property="name" cssShadingOn="atgHTML_tableCellShadingOn" cssShadingOff="atgHTML_tableCellShadingOff">
      </caf:tableCell>
      <caf:tableCell property="date">
      </caf:tableCell>
      <caf:tableCell property="number">
      </caf:tableCell>
      <caf:tableCell css="atgHTML_tableCellAlt">
      Cell contents
      </caf:tableCell>
    </caf:tableRow>
  </caf:tableBody>
</caf:table>

</body>
</html>
</dspel:page>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/simpleTable_test.jsp#2 $$Change: 651448 $--%>
