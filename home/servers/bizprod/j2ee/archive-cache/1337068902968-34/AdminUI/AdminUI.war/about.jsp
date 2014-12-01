<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/dynamo/service/VersionService" var="version"/>
  <html>
    <head>
      <title>About ATG Search 10.0.3</title>
      <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
      <link type="text/css" href="css/about.css" rel="stylesheet" media="all"/>
    </head>

    <body class="popup">
      <div id="assetBrowserHeader">
        <h2>ATG Search 10.0.3</h2>
      </div>
      <div id="nonTableContent">
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td><p><c:out value="${version.fullVersionString}"/></p>
              <p>Copyright &copy; 1998-2011 Art Technology Group, Inc. All rights reserved.  ATG and Art
                 Technology Group are registered trademarks and ATG Wisdom is a trademark of Art Technology
                 Group, Inc.  All other product names, service marks and trademarks mentioned herein are the
                 trademarks of their respective owners.</p>
              <p>This product includes the Sentry Spelling-Checker Engine Copyright &copy; 1994-2003 Wintertree
                 Software Inc.; the French, Spanish, German, Dutch, Danish, Swedish, Finish, Italian, Portuguese
                 and Norwegian Dictionaries Copyright &copy; 1999 Wintertree Software Inc.; Dojo Toolkit Copyright
                 (c) 2007 The Dojo Foundation; SwiftNote (SMSJDK) Copyright (c) 2001 NCL Technologies Ltd.; XStream Copyright (c) 2003-2007, Joe Walnes;  software developed by the Apache Software Foundation
                 (<a href="http://www.apache.org/" target="_blank">http://www.apache.org/</a>). The Apache Software License, Version 1.1 Copyright &copy; 2000 The
                 Apache Software Foundation. All rights reserved.  See License Agreements in the product
                 installation for additional details.</p>
              <H2 class="seperate">Contact</H2>
              <p>ATG<br />
                One Main Street<br />
                Cambridge, MA 02142<br />
                617.386.1000 phone<br />
                617.386.1111 fax<br />
                www.atg.com</p>
              <p>For support, visit <a href="http://www.atg.com/support/" target="_blank">http://www.atg.com/support/</a> or
                e-mail: <a href="mailto:support@atg.com">support@atg.com</a></p></td>
          </tr>
        </table>
      </div>
    </body>
  </html>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/about.jsp#3 $$Change: 651648 $--%>
