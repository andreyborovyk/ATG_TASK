<%@ page import="java.util.ArrayList,
                 java.util.Date"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="caf" uri="http://www.atg.com/taglibs/caf" %> 

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

<div align="center">

<caf:table id="table1" css="" width="100%" cellPadding="3" border="0">
<caf:tableRow>
  <caf:tableCell width="33%">
    <caf:displayList
      width="100%"
      id="displayList1"
      items="${items}"
      title="Display List Test - Strings"
      property="name"
      sortTitle="true"
      sortKey="name"
      sortFunctionName="testSort"
      sortReverseToggle="true"
      alternateRowShading="false" />
  </caf:tableCell>
  <caf:tableCell width="33%">
    <caf:displayList
      width="100%"
      id="displayList2"
      items="${items}"
      title="Display List Test - Dates"
      property="date"
      sortTitle="true"
      sortKey="date"
      sortFunctionName="testSort"
      sortReverseToggle="true"
      alternateRowShading="true" />
  </caf:tableCell>
  <caf:tableCell width="33%">
    <caf:displayList
      width="100%"
      id="displayList3"
      items="${items}"
      title="Display List Test - Numbers"
      property="number"
      sortTitle="false"
      alternateRowShading="false" />
  </caf:tableCell>
</caf:tableRow>
</caf:table>
</body>
</html>
</dspel:page>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/displayList_test.jsp#2 $$Change: 651448 $--%>
