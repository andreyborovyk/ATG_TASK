<%@ taglib uri="/dspTaglib" prefix="dsp" %> 
<%@ page contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="atg.servlet.*"%> 
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<html><head><title>Mes objectifs</title></head>

<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>
<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.userType" name="value"/>
  <dsp:oparam name="guest">
    Vous n'êtes pas connecté. Si vous désirez modifier vos objectifs, vous devez vous inscrire ou vous connecter. <dsp:a href="login.jsp">Connexion</dsp:a></dsp:oparam>

  <dsp:oparam name="default">
    <dsp:droplet name="Switch">
      <dsp:param bean="ProfileFormHandler.formError" name="value"/>
      <dsp:oparam name="true">
        <font color=cc0000><STRONG><UL>
        <dsp:droplet name="ProfileErrorMessageForEach">
          <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
          <dsp:oparam name="output">
            <LI> <dsp:valueof param="message"/>
          </dsp:oparam>
        </dsp:droplet>
        </UL></STRONG></font>
      </dsp:oparam>
    </dsp:droplet>

    <table border=0 cellpadding=4 cellspacing=0>
      <tr valign=top>
        <td width=120 bgcolor=#003366 rowspan=2>
        <!-- left bar navigation -->
        <dsp:include page="nav.jsp" ></dsp:include></td>
        
        <td>
        <table border=0>
          <tr>
            <td colspan=2><img src="images/banner-editgoals.gif"></td>
          </tr>
          <tr valign=top>
           <td>
           <table width=430>
             <tr>
               <td colspan=5>
               <b>Faites évoluer vos objectifs d'investissements avec Quincy :</b>
               <p>
               Dites-nous quels sont vos projets et nous vous aideront à atteindre vos objectifs. Faisons fructifier ensemble votre portefeuille pour qu'il vous rapporte les gains que vous attendez. Avec Quincy Funds, vous emprunterez toujours les bons chemins. C'est presque trop facile ! 
               <p>
               <b>Faites-nous part de vos choix en termes d'investissement :</b>
               <br>&nbsp;<br></td>
             </tr>

             <dsp:form action="<%=ServletUtil.getRequestURI(request)%>" method="POST">
    <dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="index.jsp"/>
             <dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="index.jsp"/>

             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.strategy" name="style" type="radio" value="aggressive"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-aa.gif"></td>
               <td>&nbsp;</td>
               <td>Offensif (risque élevé / retours importants)</td>
             </tr>

             <tr>
                <td><dsp:input bean="ProfileFormHandler.value.strategy" name="style" type="radio" value="moderate"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-bb.gif"></td>
               <td>&nbsp;</td>
               <td>Dynamique</td>
             </tr>
  
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.strategy" name="style" type="radio" value="conservative"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-cc.gif"></td>
               <td>&nbsp;</td>
               <td>Équilibré (faible risque / retours moins élevés)</td>
             </tr>  
     
             <tr>
               <td colspan=5>&nbsp;<br>
               <b>Maintenant, à quel terme souhaitez vous réaliser vos placements :</b>
               <br>&nbsp;</td>
             </tr>
  
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.goals" name="range" type="radio" value="short-term"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-sr.gif"></td>
               <td>&nbsp;</td> 
               <td>Court terme</td>
             </tr>
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.goals" name="range" type="radio" value="long-term"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-lr.gif"></td>
               <td>&nbsp;</td> 
               <td>Long terme</td>
             </tr>
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.goals" name="range" type="radio" value="retirement-focus"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-rr.gif"></td>
               <td>&nbsp;</td> 
               <td>Retraite</td> 
            </tr>
     
            <tr>
              <td colspan=5><br>
              <dsp:input bean="ProfileFormHandler.update" type="submit" value="Modifier mes objectifs"/>
              <br>&nbsp;<br>&nbsp;</td>
            </tr>
          </table>
          </dsp:form>
          </td>
        </tr>
      </table>
      </td>
    </tr>
  </table>

  </dsp:oparam>
</dsp:droplet>
</body>
</html>
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/mygoals.jsp#2 $$Change: 651448 $*/%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/mygoals.jsp#2 $$Change: 651448 $--%>
