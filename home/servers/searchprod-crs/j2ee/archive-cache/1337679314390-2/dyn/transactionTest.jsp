<%@ taglib uri="/dspTaglib" prefix="dsp" %>

<%@ page import="javax.transaction.*" %>
<%@ page import="javax.naming.*" %>

<dsp:page>

<dsp:importbean bean="/atg/dynamo/transaction/droplet/Transaction"/>
<dsp:importbean bean="/atg/dynamo/transaction/droplet/EndTransaction"/>

<head><title>Dynamo Transaction Droplet Test JSP Page</title></head>
<body>
<h1>Dynamo Transaction Droplet Test JSP Page</h1>

<h2>Testing mode = REQUIRED</h2>

  <p>Should not be in a transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<dsp:droplet name="Transaction">
  <dsp:param name="transAttribute" value="required"/>
  <dsp:oparam name="output">

    <p>Executing the "output" of the Transaction.  Should be in a
  transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<%-- Nested transaction droplet --%>
<ul>
  <p>Should be in a transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<dsp:droplet name="Transaction">
  <dsp:param name="transAttribute" value="required"/>
  <dsp:oparam name="output">

    <p>Executing the "output" of the Transaction.  Should be in a
  transaction at this point.  Transaction status = <%=getTransactionStatus()%>

  </dsp:oparam>

  <dsp:oparam name="errorOutput">

    <p>Entering the errorOutput oparam
    <p>errorMessage = <dsp:valueof param="errorMessage"/>
    <p>errorStackTrace = <dsp:valueof param="errorStackTrace"/>

  </dsp:oparam>

  <dsp:oparam name="successOutput">

    <p>Entering the successOutput oparam

  </dsp:oparam>

</dsp:droplet>

<p>After the Transaction droplet - should be in a transaction at
  this point - transaction status = <%=getTransactionStatus()%>
</ul>
<%-- End nested transaction droplet --%>

  <dsp:droplet name="EndTransaction">
    <dsp:param name="op" value="commit"/>
  </dsp:droplet>

  </dsp:oparam>

  <dsp:oparam name="errorOutput">

    <p>Entering the errorOutput oparam
    <p>errorMessage = <dsp:valueof param="errorMessage"/>
    <p>errorStackTrace = <dsp:valueof param="errorStackTrace"/>

  </dsp:oparam>

  <dsp:oparam name="successOutput">

    <p>Entering the successOutput oparam

  </dsp:oparam>

</dsp:droplet>

<p>After the Transaction droplet - should not be in a transaction at
  this point - transaction status = <%=getTransactionStatus()%>



<h2>Testing mode = SUPPORTS</h2>

  <p>Should not be in a transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<dsp:droplet name="Transaction">
  <dsp:param name="transAttribute" value="supports"/>
  <dsp:oparam name="output">

    <p>Executing the "output" of the Transaction.  Should not be in a
  transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<%-- Nested transaction droplet --%>
<ul>
  <p>Should not be in a transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<dsp:droplet name="Transaction">
  <dsp:param name="transAttribute" value="supports"/>
  <dsp:oparam name="output">

    <p>Executing the "output" of the Transaction.  Should not be in a
  transaction at this point.  Transaction status = <%=getTransactionStatus()%>

  </dsp:oparam>

  <dsp:oparam name="errorOutput">

    <p>Entering the errorOutput oparam
    <p>errorMessage = <dsp:valueof param="errorMessage"/>
    <p>errorStackTrace = <dsp:valueof param="errorStackTrace"/>

  </dsp:oparam>

  <dsp:oparam name="successOutput">

    <p>Entering the successOutput oparam

  </dsp:oparam>

</dsp:droplet>

<p>After the Transaction droplet - should not be in a transaction at
  this point - transaction status = <%=getTransactionStatus()%>
</ul>
<%-- End nested transaction droplet --%>

  </dsp:oparam>

  <dsp:oparam name="errorOutput">

    <p>Entering the errorOutput oparam
    <p>errorMessage = <dsp:valueof param="errorMessage"/>
    <p>errorStackTrace = <dsp:valueof param="errorStackTrace"/>

  </dsp:oparam>

  <dsp:oparam name="successOutput">

    <p>Entering the successOutput oparam

  </dsp:oparam>

</dsp:droplet>

<p>After the Transaction droplet - should not be in a transaction at
  this point - transaction status = <%=getTransactionStatus()%>



<h2>Testing mode = REQUIRED then NOT_SUPPORTED</h2>

  <p>Should not be in a transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<dsp:droplet name="Transaction">
  <dsp:param name="transAttribute" value="required"/>
  <dsp:oparam name="output">

    <p>Executing the "output" of the Transaction.  Should be in a
  transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<%-- Nested transaction droplet --%>
<ul>
  <p>Should be in a transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<dsp:droplet name="Transaction">
  <dsp:param name="transAttribute" value="notSupported"/>
  <dsp:oparam name="output">

    <p>Executing the "output" of the Transaction.  Should not be in a
  transaction at this point.  Transaction status = <%=getTransactionStatus()%>

  </dsp:oparam>

  <dsp:oparam name="errorOutput">

    <p>Entering the errorOutput oparam
    <p>errorMessage = <dsp:valueof param="errorMessage"/>
    <p>errorStackTrace = <dsp:valueof param="errorStackTrace"/>

  </dsp:oparam>

  <dsp:oparam name="successOutput">

    <p>Entering the successOutput oparam

  </dsp:oparam>

</dsp:droplet>

<p>After the Transaction droplet - should be in a transaction at
  this point - transaction status = <%=getTransactionStatus()%>
</ul>
<%-- End nested transaction droplet --%>

  </dsp:oparam>

  <dsp:oparam name="errorOutput">

    <p>Entering the errorOutput oparam
    <p>errorMessage = <dsp:valueof param="errorMessage"/>
    <p>errorStackTrace = <dsp:valueof param="errorStackTrace"/>

  </dsp:oparam>

  <dsp:oparam name="successOutput">

    <p>Entering the successOutput oparam

  </dsp:oparam>

</dsp:droplet>

<p>After the Transaction droplet - should not be in a transaction at
  this point - transaction status = <%=getTransactionStatus()%>


<h2>Testing mode = REQUIRED then NEVER</h2>

  <p>Should not be in a transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<dsp:droplet name="Transaction">
  <dsp:param name="transAttribute" value="required"/>
  <dsp:oparam name="output">

    <p>Executing the "output" of the Transaction.  Should be in a
  transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<%-- Nested transaction droplet --%>
<ul>
  <p>Should be in a transaction at this point.  Transaction status = <%=getTransactionStatus()%>

<dsp:droplet name="Transaction">
  <dsp:param name="transAttribute" value="never"/>
  <dsp:oparam name="output">

    <p>Executing the "output" of the Transaction.  Should not be in a
  transaction at this point.  Transaction status = <%=getTransactionStatus()%>

  </dsp:oparam>

  <dsp:oparam name="errorOutput">

    <p>Entering the errorOutput oparam (as expected)
    <p>errorMessage = <dsp:valueof param="errorMessage"/>
    <p>errorStackTrace = <dsp:valueof param="errorStackTrace"/>

  </dsp:oparam>

  <dsp:oparam name="successOutput">

    <p>Entering the successOutput oparam

  </dsp:oparam>

</dsp:droplet>

<p>After the Transaction droplet - should be in a transaction at
  this point - transaction status = <%=getTransactionStatus()%>
</ul>
<%-- End nested transaction droplet --%>

  </dsp:oparam>

  <dsp:oparam name="errorOutput">

    <p>Entering the errorOutput oparam
    <p>errorMessage = <dsp:valueof param="errorMessage"/>
    <p>errorStackTrace = <dsp:valueof param="errorStackTrace"/>

  </dsp:oparam>

  <dsp:oparam name="successOutput">

    <p>Entering the successOutput oparam

  </dsp:oparam>

</dsp:droplet>

<p>After the Transaction droplet - should not be in a transaction at
  this point - transaction status = <%=getTransactionStatus()%>



</body>
</html>
</dsp:page>



<%!

public String getTransactionStatus ()
{
  try {
    //    UserTransaction ut = (UserTransaction) (new InitialContext ().lookup ("java:comp/UserTransaction"));
    UserTransaction ut = (UserTransaction) (new InitialContext ().lookup ("dynamo:/atg/dynamo/transaction/UserTransaction"));
    int status = ut.getStatus ();

    switch (status) {
    case Status.STATUS_ACTIVE:
      return "STATUS_ACTIVE";
    case Status.STATUS_COMMITTED:
      return "STATUS_COMMITTED";
    case Status.STATUS_COMMITTING:
      return "STATUS_COMMITTING";
    case Status.STATUS_MARKED_ROLLBACK:
      return "STATUS_MARKED_ROLLBACK";
    case Status.STATUS_NO_TRANSACTION:
      return "STATUS_NO_TRANSACTION";
    case Status.STATUS_PREPARED:
      return "STATUS_PREPARED";
    case Status.STATUS_PREPARING:
      return "STATUS_PREPARING";
    case Status.STATUS_ROLLEDBACK:
      return "STATUS_ROLLEDBACK";
    case Status.STATUS_ROLLING_BACK:
      return "STATUS_ROLLING_BACK";
    case Status.STATUS_UNKNOWN:
      return "STATUS_UNKNOWN";
    default:
      return "????";
    }
  }
  catch (SystemException exc) {
    return exc.toString ();
  }
  catch (NamingException exc) {
    return exc.toString ();
  }
}
%>
<%-- @version $Id: //product/DAS/version/10.0.3/templates/DAF/j2ee-apps/dyn/transactionTest.jsp#2 $$Change: 651448 $--%>
