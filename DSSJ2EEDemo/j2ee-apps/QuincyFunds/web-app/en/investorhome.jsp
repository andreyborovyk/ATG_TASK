<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<!-- main outside table. three columns -->
<table width=625 cellspacing=0 cellpadding=0 border=0>
  <tr valign=top>

    <!-- left column -->
    <td bgcolor=#003366 rowspan=2>
    <dsp:include page="nav.jsp" ></dsp:include>
    </td> <!-- this is the close after the center-->
    
    <!-- gutter-->
    <td>&nbsp;</td>
    
    <!-- banner area -->
    <td colspan=3><img src="images/banner-quincy.gif" 
    hspace=20 vspace=4 alt="quincy funds"></td>
        
  </tr>
  <tr valign=top>
    <!-- gutter-->
    <td>&nbsp;</td>

    <!-- middle column -->
	<td>
 		<!-- offers -->
  <dsp:droplet name="/atg/targeting/TargetingFirst">
    <dsp:param bean="/atg/registry/Slots/QFOfferSlot" name="targeter"/>
    <dsp:param name="howMany" value="1"/>
    <dsp:param name="fireContentEvent" value="false"/>
    <dsp:param name="fireContentTypeEvent" value="false"/>
    <dsp:oparam name="output">
				<dsp:a href="offer.jsp">
          <dsp:param name="ElementId" param="element.repositoryId"/>
          <img border="0" src="<dsp:valueof param="element.imageURL"/>"></dsp:a>

    </dsp:oparam>
  </dsp:droplet>



 
    <img src="images/banner-portfolio.gif" alt="my portfolio">
                
    <!-- The holdings begin here. -->
      <table width=250 cellspacing=0 cellpadding=0 border=0>
        <tr> 
	  <td colspan=8><img src="images/banner-holdings.gif"></td> 
	</tr>
        <tr bgcolor=#ffff99>
          <td colspan=8><img src="images/clear.gif" vspace=1></td>
        </tr>
        <dsp:getvalueof id="pval0" bean="/atg/userprofiling/Profile.repositoryId"><dsp:include page="portfoliodynamic.jsp" ><dsp:param name="ElementId" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
        <br>


         <table width=250 cellspacing=0 cellpadding=0 border=0 bgcolor=#ffffcc>
          <tr>
            <td><img src="images/banner-goals.gif">
            <img src="images/d.gif" vspace=2><br>
            &nbsp; MY ACTUAL PORTFOLIO<br>
        <dsp:droplet name="/atg/targeting/TargetingFirst">
          <dsp:param bean="/atg/registry/RepositoryTargeters/Images/actualStrategyImages" name="targeter"/>
          <dsp:param name="howMany" value="1"/>
          <dsp:oparam name="output">
            <img src="<dsp:valueof param="element.imageURL"/>" align="left">
          </dsp:oparam>
          <dsp:param name="fireContentEvent" value="false"/>
           <dsp:param name="fireContentTypeEvent" value="false"/>
        </dsp:droplet> 
        <dsp:valueof bean="Profile.actualstrategy"/><br clear=all>
        <dsp:droplet name="/atg/targeting/TargetingFirst">
          <dsp:param bean="/atg/registry/RepositoryTargeters/Images/actualGoalsImage" name="targeter"/>
          <dsp:param name="howMany" value="1"/>
          <dsp:oparam name="output">
            <img src="<dsp:valueof param="element.imageURL"/>" align="left">
          </dsp:oparam>
          <dsp:param name="fireContentEvent" value="false"/>
          <dsp:param name="fireContentTypeEvent" value="false"/>
        </dsp:droplet> 
        <dsp:valueof bean="Profile.actualgoals"/>
              <br><br>
        &nbsp; MY IDEAL PORTFOLIO<br>
        <dsp:droplet name="/atg/targeting/TargetingFirst">
           <dsp:param bean="/atg/registry/RepositoryTargeters/Images/strategyImages" name="targeter"/>
         <dsp:param name="howMany" value="1"/>
         <dsp:oparam name="output">
           <img src="<dsp:valueof param="element.imageURL"/>" align="left">
         </dsp:oparam>
         <dsp:param name="fireContentEvent" value="false"/>
         <dsp:param name="fireContentTypeEvent" value="false"/>
        </dsp:droplet>
      
        <dsp:valueof bean="Profile.strategy"/> <br clear=all>
        <dsp:droplet name="/atg/targeting/TargetingFirst">
          <dsp:param bean="/atg/registry/RepositoryTargeters/Images/goalsImages" name="targeter"/>
          <dsp:param name="howMany" value="1"/>
          <dsp:oparam name="output">
           <img src="<dsp:valueof param="element.imageURL"/>" align="left">
          </dsp:oparam>
          <dsp:param name="fireContentEvent" value="false"/>
          <dsp:param name="fireContentTypeEvent" value="false"/>
        </dsp:droplet>
        <dsp:valueof bean="Profile.goals"/>

        <br><br></font>
        <dsp:a href="mygoals.jsp"><img src="images/but-changegoals.gif" border=0></dsp:a></td>
    </tr>
    <dsp:droplet name="/atg/targeting/TargetingForEach">
      <dsp:param bean="/atg/registry/RepositoryTargeters/Images/aggressivePromo" name="targeter"/>
      
      <dsp:oparam name="output">
        <tr>
          <td bgcolor=ffffff><img src="images/d.gif" vspace=4></td>
        </tr>
        <tr>
          <td><img src="<dsp:valueof param="element.imageURL">images/d.gif</dsp:valueof>"></td>
        </tr>
      </dsp:oparam>
    </dsp:droplet>

    </table>

       <img src="images/d.gif" vspace=4><br>
       <img src="images/portfolio-graph.gif" alt="graph here">
  
	
            
    </td>
    <!-- gutter-->
    <td>&nbsp;</td>
    
    
    <!-- right column -->
    <td>
    <!-- Tuesday's Tips - only diplays on Tuesdays -->
    <dsp:droplet name="/atg/targeting/TargetingRandom">
      <dsp:param bean="/atg/registry/RepositoryTargeters/InvestmentTips/TipTargeter" name="targeter"/>
      <dsp:param name="howMany" value="1"/>
      <dsp:oparam name="output">
        <table width=250 cellspacing=0 cellpadding=0 border=0>
          <tr>
            <td colspan=5 bgcolor="#0063CE"><img src="images/d.gif" height=2></td>
          </tr>
          <tr valign=top>
             <td bgcolor="#0063CE"><img src="images/d.gif" width=2></td>
             <td bgcolor="#FFFFCC" colspan=3><img src="images/banner-tips.gif"></td>
             <td bgcolor="#0063CE"><img src="images/d.gif" width=2></td>
          </tr>
          <tr>
             <td bgcolor="#0063CE"><img src="images/d.gif"></td>
             <td bgcolor="#FFFFCC"> &nbsp</td>
             <td bgcolor="#FFFFCC"><dsp:valueof param="element.description"/></td>
             <td bgcolor="#FFFFCC">&nbsp</td>
             <td bgcolor="#0063CE"><img src="images/d.gif"></td>
          </tr>
          <tr>
            <td colspan=5 bgcolor="#0063CE"><img src="images/d.gif" height=2></td>
          </tr>
        </table>
        <img src="images/d.gif" vspace=4><br>
      </dsp:oparam>
    </dsp:droplet>

        
    <!-- display warning if language is Japanese and they don't have the
    character set -->
    <dsp:droplet name="/atg/targeting/TargetingFirst">
      <dsp:param bean="/atg/registry/RepositoryTargeters/Images/JapaneseWarning" name="targeter"/>
      <dsp:param name="howMany" value="1"/>
      <dsp:oparam name="output">
        <dsp:valueof param="element.description"/>
        <p>
      </dsp:oparam>
    </dsp:droplet>

    <!-- Features -->    
    <table width=250 cellspacing=0 cellpadding=0 border=0 background="images/purple-lines.gif">
      <tr valign=top>
        <td colspan=2>
        <img src="images/banner-features.gif"  alt="features"></td>
      </tr>
      <tr><td colspan=2><img src="images/d.gif" vspace=4></td></tr>
      
    <dsp:droplet name="/atg/targeting/TargetingRange">
      <dsp:param bean="/atg/registry/RepositoryTargeters/Features/Features" name="targeter"/>
      <dsp:param bean="Profile.numberfeatureitems" name="howMany"/>
      <dsp:param name="sortProperties" value="+title"/>
      <dsp:param name="fireContentEvent" value="false"/>
      <dsp:param name="fireContentTypeEvent" value="false"/>
      <dsp:oparam name="outputStart">
      </dsp:oparam>
      <dsp:oparam name="output">
        <tr valign=top>
	  <td><dsp:a href="feature.jsp">
          <dsp:param name="ElementId" param="element.repositoryId"/>
          <img height="73" vspace="2" border="0" hspace="8" width="73" src="<dsp:valueof param="element.SmallImageURL">images/features/noimage.gif</dsp:valueof>"></dsp:a></td>
	  <td><dsp:a href="feature.jsp">
          <dsp:param name="ElementId" param="element.repositoryId"/>
          <b><dsp:valueof param="element.title"/></b></dsp:a><br>
          <dsp:valueof param="element.headline"></font></dsp:valueof> </td>
        </tr>
      </dsp:oparam>
      <dsp:oparam name="empty">
        <tr>
          <td colspan=2>No Features today.</td>
        </tr>
      </dsp:oparam>
    </dsp:droplet>
    <tr>
      <td colspan=3><dsp:a href="featurelist.jsp"><img src="images/but-indexfeatures.gif" border=0></dsp:a></td>
    </tr>
    </table>

    <img src="images/d.gif" vspace=4><br>
    
    <table width=250 cellspacing=0 cellpadding=0 border=0 background="images/greenish-lines.gif">
      <tr>
        <td colspan=2>
        <img src="images/banner-news.gif" alt="news"></td>
      </tr>
      <tr><td colspan=2><img src="images/d.gif" vspace=4></td></tr>

      <!-- News -->
      <dsp:droplet name="/atg/targeting/TargetingRange">
	<dsp:param bean="/atg/registry/RepositoryTargeters/News/News" name="targeter"/>
	<dsp:param bean="Profile.numbernewsitems" name="howMany"/>
  <dsp:param name="fireContentEvent" value="false"/>
  <dsp:param name="fireContentTypeEvent" value="false"/>
	<dsp:param name="sortProperties" value="-date"/>

      	<dsp:oparam name="output">
	  <tr valign=top>
	    <td><dsp:a href="news.jsp">
            <dsp:param name="ElementId" param="element.repositoryId"/>
            <img src="images/news-bullet.gif" width=8 height=8 hspace=6 vspace=4 border=0></dsp:a></td>
	    <td><dsp:a href="news.jsp">
            <dsp:param name="ElementId" param="element.repositoryId"/>
            <dsp:valueof param="element.headline"/></dsp:a></td>
          </tr>
	  <tr>
           <td colspan=2><img src="images/d.gif" vspace=4></td>
          </tr>
        </dsp:oparam>
	<dsp:oparam name="empty">
	<tr valign=top>
	  <TD><img src="images/news-bullet.gif" hspace=6 vspace=4></TD>
	  <TD>No news today</TD>
        </tr>
	<tr>
          <td colspan=2><img src="images/d.gif" vspace=4></td>
        </tr>
      </dsp:oparam>
   </dsp:droplet>
        
  <tr>
    <td colspan=2><dsp:a href="newslist.jsp"><img src="images/but-readnews.gif" alt="more news" border=0></dsp:a></td>
  </tr>
</table>

<br>
</td>
</tr>
</table>
<!-- end news -->
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/en/investorhome.jsp#2 $$Change: 651448 $--%>
