<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<dsp:page>
  <dsp:importbean bean="/atg/dynamo/service/VersionService" var="version"/>
  <div id="nonTableContent">
    <table border="0" cellpadding="0" cellspacing="0">
    <tr>
    <td>
      <p><c:out value="${version.fullVersionString}"/></p>

      <p>Copyright © 2005-2011 Art Technology Group, Inc.</p>

      <p>EditLive! Authoring Software<br/>
      <br/>
      (c) Copyright 2001-2011 Ephox Corporation. All Rights Reserved.
      'EditLive!' and 'Ephox' are registered trademarks of Ephox Corporation.<br/>
      <br/>
      This product includes code licensed from RSA Security, Inc.<br/>
      <br/>
      Some portions licensed from IBM are available at
      http://oss.software.ibm.com/icu4j/<br/>
      <br/>
      This product includes software developed by the Apache Software Foundation
      (http://www.apache.org/).<br/>
      <br/>
      Contains spell checking software from Wintertree Software Inc.<br/>
      The Sentry Spelling-Checker Engine Copyright © 2000-2011 Wintertree Software Inc.<br/>
      <br/>
      Some portions of this product are from:<br/>
      Quaqua Look And Feel © 2003-2011<br/>
      Werner Randelshofer, Staldenmattweg 2, Immensee, CH-6405, Switzerland<br/>
      http://www.randelshofer.ch/<br/>
      werner.randelshofer@bluewin.ch<br/>
      All Rights Reserved.<br/>
      Used under the Modified BSD License<br/>
      Redistribution and use in source and binary forms, with or without
      modification, are permitted provided that the following conditions are met:<br/>
      <br/>
      Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.<br/>
      <br/>
      Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.<br/>
      <br/>
      The name of the author may not be used to endorse or promote products
      derived from this software without specific prior written permission.<br/>
      <br/>
      THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
      IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
      OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
      IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
      INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
      BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
      USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
      ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
      (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
      THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.<br/>
      <br/>
      Some portions of this software are from:<br/>
      The Sun Java Tutorial<br/>
      <br/>
      Copyright© 1995-2011 Sun Microsystems, Inc., 4150 Network Circle,<br/>
      Santa Clara, California 95054, U.S.A. All rights reserved.<br/>
      <br/>
      Copyright (c) 2006-2011 Romain Guy <romain.guy@mac.com><br/>
      All rights reserved.<br/>
      <br/>
      Redistribution and use in source and binary forms, with or without
      modification, are permitted provided that the following conditions are met:<br/>
      <br/>
      Redistribution of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.<br/>
      <br/>
      Redistribution in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.<br/>
      <br/>
      Neither the name of Sun Microsystems, Inc. or the names of contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.<br/>
      <br/>
      This software is provided "AS IS," without a warranty of any kind. ALL
      EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
      ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
      OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
      AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
      AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
      DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
      REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
      INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE
      THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS
      SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.<br/>
      <br/>
      You acknowledge that this software is not designed, licensed or intended
      for use in the design, construction, operation or maintenance of any
      nuclear facility.<br/>
      <br/>
      The Ephox EditLive! software is subject to certain export restrictions
      of the United States Government. If you are (a) in a country to which
      export from the United States is restricted for anti-terrorism reasons,
      or a national of any such country, wherever located, (b) in a country to
      which the United States has embargoed or restricted the export of goods or
      services, or a national of any such country, wherever located, or (c) a
      person or entity who has been prohibited from participating in United States
      export transactions by any agency of the United States Government, then
      you may not install, download, access, use, or license the Software. By
      accepting this License, you warrant and represent to Ephox Corporation that
      (1) you do not match the criteria set forth in (a), (b), or (c) above,
      (2) that you will not export or re-export the Software to any country,
      person, or entity subject to U.S. export restrictions, including those
      persons and entities that match the criteria set forth in (a), (b), or
      (c) above, and (3) that neither the United States Bureau of Industry and
      Security, nor any other U.S. federal agency, has suspended, revoked, or
      denied your export privileges.<br/>
      </p>

      <h2 class="seperate">Contact</h2><br/>
      ATG<br/>
      One Main Street 6th Floor<br />
      Cambridge, MA 02142<br />
      617.386.1000 phone<br />
      617.386.1111 fax<br />
      www.atg.com
      <br/><br/>

      <p>For support, visit <a href="http://www.atg.com/support/" target="_blank">http://www.atg.com/support/</a> or e-mail: <a href="mailto:support@atg.com">support@atg.com</a></p>

    </td>
    </tr>
    </table>
  </div>
</dsp:page>
<%-- @version $Id: //product/BCC/version/10.0.3/src/web-apps/ControlCenter/about.jsp#2 $$Change: 651448 $--%>
