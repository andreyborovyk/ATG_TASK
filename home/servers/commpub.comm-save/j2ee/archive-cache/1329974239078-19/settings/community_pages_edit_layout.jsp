<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/portal/admin/PageLayoutFormHandler"/>

<%@ include file="fragments/form_messages.jspf"%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="PageLayoutFormHandler.formError"/>
  <dsp:oparam name="true">
     <dsp:setvalue bean="PageLayoutFormHandler.resetFormExceptions"/>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="PageLayoutFormHandler.reset"/>
  </dsp:oparam>
</dsp:droplet> 

<%@include file="fragments/community_pages_edit_nav.jspf"%>

<% String bigResetUrl = ""; %>

<%-- This is the seperator used to seperate the gear ids when creating the finalGearArrangement configurable
 on the PageLayoutFormHandler --%>

<dsp:getvalueof id="listSeperator" idtype="java.lang.String" bean="PageLayoutFormHandler.gearArrangementListSeperator">

<script language="javascript"><!--

var pcImg = new Array();
pcImg[0] = new Image;
pcImg[1] = new Image;
pcImg[2] = new Image;
pcImg[3] = new Image;
pcImg[4] = new Image;
pcImg[0].src = "images/arrow_up_16x16_dim.gif";
pcImg[1].src = "images/arrow_right_16x16_dim.gif";
pcImg[2].src = "images/arrow_left_16x16_dim.gif";
pcImg[3].src = "images/arrow_down_16x16_dim.gif";
pcImg[4].src = "images/arrow_x_16x16_dim.gif";

var pcImgo = new Array();
pcImgo[0] = new Image;
pcImgo[1] = new Image;
pcImgo[2] = new Image;
pcImgo[3] = new Image;
pcImgo[4] = new Image;
pcImgo[0].src = "images/arrow_up_16x16.gif";
pcImgo[1].src = "images/arrow_right_16x16.gif";
pcImgo[2].src = "images/arrow_left_16x16.gif";
pcImgo[3].src = "images/arrow_down_16x16.gif";
pcImgo[4].src = "images/arrow_x_16x16.gif";

var arrowSeq     = new Array("up","right","left","down","x");
var arrowSeqRuleLeft   = new Array("up","right","","down","x");
var arrowSeqRuleMiddle = new Array("up","right","left","down","x");
var arrowSeqRuleRight   = new Array("up","","left","down","x");

var regionDef = new Array();

var redraw = new Array(false,false,false,false,false,false,false,false,false,false,false,false,false,false);

var isIE = ( navigator.appVersion.indexOf('MSIE') > -1 ) ? true : false ;

function highLight( srcSelect , rule  ) {
  srcObj = eval("document.forms['rearrange'].region_selection_"+srcSelect);
     sideway = false;
     currRegionStyle = regionDef[srcSelect];
     for (j=0; j < regionDef.length; j++) { 
      if(srcSelect != j ) {
       if ( regionDef[srcSelect] == regionDef[j] )  { sideway = true ;} 
      };
     };
    if ( srcObj[srcObj.selectedIndex].value.indexOf("zzz")  != 0 ) { sideway = true ;}
  for (  i=0 ;  i<arrowSeq.length  ;   i++ ) {
   id =  "img"+srcSelect+"_"+arrowSeq[i] ;
   var theImage = FWFindImage(document, id, 0);
   if (theImage) { theImage.src = pcImg[i].src; }
    if (srcObj.options[srcObj.selectedIndex].value != "") {
     if  (    ( !  (  (  srcObj.selectedIndex == 0 ) && ( i == 0 ) )  )
    &&  ( !  (  ( (srcObj.length-1) == srcObj.selectedIndex) && ( i == 3 ) ) )   
    &&  (   eval("arrowSeqRule"+rule+"["+i+"]")  != "" )  ) {
       if (theImage ) {
       if ( i == 1 || i == 2 ) { 
        if ( sideway == true ) { theImage.src = pcImgo[i].src;   }
       } else {
         theImage.src = pcImgo[i].src;   
       }
       };
    };
    };
    };
}
function di20(id, newSrc) {
  var theImage = FWFindImage(document, id, 0);
  if (theImage) {
  theImage.src = newSrc;
  };
}
function FWFindImage(doc, name, j) {
  var theImage = false;
  if (doc.images) {
  theImage = doc.images[name];
  };
  if (theImage) {
  return theImage;
  };
  if (doc.layers) {
  for (j = 0; j < doc.layers.length; j++) {
    theImage = FWFindImage(doc.layers[j].document, name, 0);
    if (theImage) {
    return (theImage);
    };
  };
  };
  return (false);
}
function moveUD(selectRef , direction, rule) {
       objRef = eval("document.forms['rearrange'].region_selection_"+selectRef);
       if ( objRef.options.length < 1 || objRef.selectedIndex < 0 ) {   
        return; 
       }
       dirN = (direction == "down") ? 1 : -1;
       targetPos = objRef.selectedIndex + dirN ;
       tempOptionV = objRef.options[targetPos].value;
       tempOptionT = objRef.options[targetPos].text;
       if ( tempOptionV  != "" ) {
         objRef.options[objRef.selectedIndex+dirN].value = objRef.options[objRef.selectedIndex].value;
         objRef.options[objRef.selectedIndex+dirN].text = objRef.options[objRef.selectedIndex].text;
         objRef.options[objRef.selectedIndex].value = tempOptionV;
         objRef.options[objRef.selectedIndex].text = tempOptionT;
         objRef.selectedIndex = objRef.selectedIndex+dirN;
      } else {
         objRef.options[targetPos] = null;
      }
}
function move( selectRef , direction, rule ) {
    redraw = new Array(false,false,false,false,false,false);
    if ( document.images['img'+selectRef+'_'+direction].src.indexOf("dim.gif") !=  -1 ) {  return ; }
    redraw[selectRef] = true;
    objRef = eval("document.forms['rearrange'].region_selection_"+selectRef);
    trigTarRedraw = 1;
    if (direction == "down" ||  direction == "up") {
        moveUD(selectRef , direction, rule);
        highLight( selectRef , rule );
    } else if (direction == "left" ||  direction == "right" ||  direction == "x") {
      if ( objRef.selectedIndex == -1) { return; } 
      tempOptionV = objRef.options[objRef.selectedIndex].value;
      tempOptionT = objRef.options[objRef.selectedIndex].text;
      objRef.options[objRef.selectedIndex] = null;
      tempC = 0; 
      a = new Array();
      for (i=0; i < objRef.options.length; i++) {
        if (  objRef.options[i].value  != "" ) {
           a[i] = new Array ( objRef.options[i].text  ,   objRef.options[i].value ,  false ,  false  );
        }
      } 
      for (i=objRef.options.length; i > -1 ; i--) {
         objRef.options[i] = null;
      }
      for ( i= 0 ; i < a.length ; i++ ) {
       if ( a[i][1] != ""  ) {
         objRef.options[tempC] = new Option( a[i][0] ,a[i][1], a[i][2] ,a[i][3] );
         tempC = tempC + 1;
       } 
      }
      
      for ( z=0 ; z < objRef.options.length ; z++) {
        objRef.options[z].selected = false;
      }
      objRef.onchange;
      dirN = (direction == "right") ? 1 : -1;
      refN = -1;
      if (  tempOptionV.indexOf("zzz") != 0 ) {
         refN =  selectRef+dirN ;
      }  else {
         tarL =   (  tempOptionV.indexOf("n") == 0  ) ?  "n" : "w" ;
         if (dirN == -1) {
           for (j=0; j < regionDef.length; j++) { 
            if ( (selectRef != j ) && ( regionDef[selectRef] == regionDef[j] ) )  { refN = j ; break;} 
           };
         } else {
          for ( j=regionDef.length; j > -1  ; j--) { 
            if ( (selectRef != j ) && ( regionDef[selectRef] ==  regionDef[j] ) )  { refN = j ; break;}  };
         };
       };
      if ( refN > -1 && direction != "x" ) {
         targetObjRef = eval(  "document.forms['rearrange'].region_selection_"+ refN );
         targetObjRef.options[ targetObjRef.length ] = new Option (  tempOptionT  ,  tempOptionV , true , true );
         targetObjRef.selectedIndex = -1;
         redraw[selectRef] = true;
         redraw[refN]    = true;
      }
      dimAll(selectRef);     
    }; 
    rewriteHidden();
};


function dimAll(numRef) {
  for (  m=0 ;  m<arrowSeq.length  ;   m++ ) {
   id =  "img"+numRef+"_"+arrowSeq[m] ;
   var theImage = FWFindImage(document, id, 0);
   if (theImage) { theImage.src = pcImg[m].src; }

  }

}

function rewriteHidden() {
    for ( k = 0 ; k < redraw.length; k++ ) {
       if ( redraw[k] == true ) {

        tempT ="";
        objRef =  eval(  "document.forms['rearrange'].region_selection_"+k );
        for ( i = 0 ; i < objRef.options.length ; i++ ){
        if ( objRef.options[i].value == "" ) {          
            objRef.options[i] = null;
        }
        };
        for ( i = 0 ; i < objRef.options.length ; i++ ){
          tempT = tempT + objRef.options[i].value;
          if ( ( i < objRef.options.length -1 ) && ( objRef.options[i+1].value  != "" )  ) {
            tempT = tempT + "<%= listSeperator%>";
          };
        };
        objRefHidden = eval("document.forms['rearrange'].region_hidden_"+k  );
        objRefHidden.value = tempT;
       };
   
    };
}


function doinit() {
        thisForm = eval ( " document.forms['rearrange'] " );
        if (thisForm != null ) {
   for ( i = 0 ; i < 15 ; i++ ) {
                   if ( eval ( "thisForm.region_selection_"+i ) != null ) { 
     regionDef[i] = "n";
     redraw[i] = true;
  }
  }
        }
  rewriteHidden();
} 

//--></script>

<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<table border="0" cellpadding="0" cellspacing="0">
<dsp:form  name="rearrange" action="community_pages.jsp" synchronized="/atg/portal/admin/PageLayoutFormHandler">
  <tr>


<%
        String baseLayoutURL  = "";
        String baseLayoutURLplus = "";

atg.servlet.DynamoHttpServletRequest dynamoRequest=atg.servlet.ServletUtil.getDynamoRequest(request);
String dsp_page_id = dynamoRequest.getParameter("paf_page_id");
String dsp_community_id = dynamoRequest.getParameter("paf_community_id");
String dsp_ppage_id = dynamoRequest.getParameter("paf_ppage_id");
String dsp_page_url = dynamoRequest.getParameter("paf_page_url");
String previewLayoutId = dynamoRequest.getParameter("PRE_LAYOUT_ID");

%>
<dsp:setvalue bean="PageLayoutFormHandler.pageId" value="<%= dsp_page_id%>"/>
<dsp:setvalue bean="PageLayoutFormHandler.communityId" value="<%= dsp_community_id%>"/>
<dsp:getvalueof id="nameOfPage" idtype="java.lang.String" bean="PageLayoutFormHandler.pageId">

<dsp:getvalueof id="pageItem" idtype="atg.portal.framework.Page" bean="PageLayoutFormHandler.page">
  
  <core:IfNotNull value="<%=previewLayoutId%>">
    <dsp:setvalue bean="PageLayoutFormHandler.previewLayout" value='<%= previewLayoutId %>'/>
  </core:IfNotNull>
   <core:IfNull value="<%=previewLayoutId%>">
    <dsp:setvalue bean="PageLayoutFormHandler.previewLayout" value='<%= pageItem.getLayout().getId()%>'/>
  </core:IfNull>
  <dsp:getvalueof id="layoutIdInUse" idtype="java.lang.String" bean="PageLayoutFormHandler.previewLayout"> 

  <dsp:getvalueof id="idOfCommunity" idtype="java.lang.String" bean="PageLayoutFormHandler.communityId">

     <%-- create recursive url for layout links --%>
     <core:CreateUrl id="layoutRecUrl" url="/portal/settings/community_pages.jsp" > 
      <core:UrlParam param="mode" value="8"/> 
      <core:UrlParam param="paf_page_id" value="<%=dsp_page_id%>"/> 
      <core:UrlParam param="paf_community_id" value="<%=dsp_community_id %>"/>
      <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
        <% baseLayoutURLplus = (String) layoutRecUrl.getNewUrl(); %> 
     </core:CreateUrl> 



  <td valign="top" align="left" width="100" NOWRAP>
  <font class="smaller"><i18n:message key="community_layout_select_layout"/><br><br>
  <dsp:getvalueof id="layoutSettings" bean="PageLayoutFormHandler.layoutTemplate">
   <admin:GetAllItems id="items">
      <%-- Render all the layouts that have icons --%>

      <core:ForEach id="allLayouts"
          values="<%= items.getLayouts(atg.portal.framework.Comparators.getLayoutComparator()) %>"
          castClass="atg.portal.framework.Layout"
          elementId="layoutItem"> 

          <% String imageURL   = (String) layoutItem.getSmallImageURL(); %>
          <core:IfNotNull value="<%=imageURL%>">
            <core:ExclusiveIf> 
              <core:IfEqual object1="<%=layoutItem.getId()%>" object2="<%=layoutIdInUse%>">
                <a href="<%= baseLayoutURLplus+"&PRE_LAYOUT_ID="+layoutItem.getId() %>" ><img border=1 src="<%= layoutItem.getSmallImageURL() %>" alt="<%=layoutItem.getId()%>" ></a>
              </core:IfEqual>
              <core:DefaultCase>
                <a href="<%= baseLayoutURLplus +"&PRE_LAYOUT_ID="+layoutItem.getId() %>" bean="PageLayoutFormHandler.layoutTemplate" value="<%= layoutItem.getId() %>" ><img border=0 src="<%= layoutItem.getSmallImageURL()%>" alt="<%=layoutItem.getId()%>" ></a>
              </core:DefaultCase>
            </core:ExclusiveIf> 
          </core:IfNotNull>   
        <br><br>
      </core:ForEach>
    <%--  Now render the links , the ones with out icons --%>
    <core:ForEach id="allLayouts"
          values="<%= items.getLayouts(atg.portal.framework.Comparators.getLayoutComparator()) %>"
          castClass="atg.portal.framework.Layout"
          elementId="layoutItem">      

        <% String imageURL   = (String) layoutItem.getSmallImageURL(); %>
        <core:IfNull value="<%=imageURL%>">
          <core:ExclusiveIf> 
            <core:IfEqual object1="<%=layoutIdInUse%>" object2 = "<%=layoutItem.getId()%>">
              <font face="verdana,arial" size="-2"> 
              <a href="<%= baseLayoutURLplus+"&PRE_LAYOUT_ID="+layoutItem.getId() %>" ><b><%= layoutItem.getName() %></b></a></b><br></font>
            </core:IfEqual>
            <core:DefaultCase>
              <font face="verdana,arial" size="-2"> 
              <a href="<%= baseLayoutURLplus+"&PRE_LAYOUT_ID="+layoutItem.getId() %>" ><%= layoutItem.getName() %></a><br></font>
            </core:DefaultCase>
          </core:ExclusiveIf> 
        </core:IfNull>   
    </core:ForEach>
   </admin:GetAllItems>
 </dsp:getvalueof>
    </td>
    <td width="5">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

    <td valign="top" width="95%">






<core:If value="<%= ( pageItem.getGearSet().size() < 1) %>">
<font class="smaller">
  <i18n:message key="community_pages_edit_layout_no_gears_message"/><br><br>
</font>
</core:If>
<core:If value="<%= ( pageItem.getGearSet().size() > 0) %>">
<font class="smaller"><i18n:message key="community_pages_edit_layout_instruct"/><br><br>
</core:If>
    <table border="0" cellpadding="0" cellspacing="0">
      <tr>

        <%
          int regionIdx = 0;
          int regionCounter = 1; //region counter is always regionIdx + 1.
          java.util.Map regions = (java.util.Map) pageItem.getRegions();
        
          /*int regionLength = (pageDetails.getRegions()).size();*/
          int regionLength =  adminEnv.getPortal().getLayoutById(layoutIdInUse).getRegionDefinitionSet().size();//pageDetails.getNumRegions();
          String selectFieldPos = "Left"; // currently this cycles (left.middle...middle.right) works for one row
          String  gears = ""; 
          String IEwidth = "";
          boolean firstGear = true; 
        %>


          <core:ForEach id="regionDefinitionDetails"
              values="<%= adminEnv.getPortal().getLayoutById(layoutIdInUse).getRegionDefinitionSet() %>"
              castClass="atg.portal.framework.RegionDefinition"
              elementId="key">
             <%
              String selectName = "region_selection_" + regionIdx;
              String hiddenName = "region_hidden_" + regionIdx;
            %> 	
            <core:If value="<%=(regionIdx > 0) %>">
              <% selectFieldPos = "Middle"; %>
            </core:If>

            <core:If value="<%= regionCounter == regionLength %>">
              <% selectFieldPos = "Right"; %>
            </core:If>

            <td valign="top" align="center">
            <table border="0" cellpadding="0" cellspacing="2">
              <tr>
                <td valign="top"><font class="smaller">
                	<%=key.getName()%>&nbsp;
                  <% if (key.getWidth().equals(atg.portal.framework.RegionWidthEnum.NARROW) ) {
                     IEwidth = "100";
                  %>
                   <i18n:message key="community_layouts_narrow"/>
                   <% } else {
                     IEwidth = "160"; %>
                  <i18n:message key="community_layouts_wide"/>
                  <%}%>
                  </font></td>

                  </tr>
                  <tr>
                  <td valign="top"><select name="<%= selectName%>" size="10"  style='<%="width:"+IEwidth%>' onChange="<%="highLight("+ regionIdx +",'" + selectFieldPos +"',this)"%>">

                  <% gears = ""; 
                  firstGear = true; 
                  %>

                  <dsp:getvalueof id="regionDefinitionGearsMap" idtype="java.util.Map" bean="PageLayoutFormHandler.previewGearArrangement">
                    <% if(regionDefinitionGearsMap != null) { %>
                    <core:ForEach id="gearsForEach"
                      values="<%= regionDefinitionGearsMap.get(key.getId())%>"
                      castClass="atg.portal.framework.Gear"
                      elementId="gear">
                        <core:ExclusiveIf> 
                          <core:If value="<%=firstGear%>">
                            <% 
                              gears = gear.getId();
                              firstGear = false; 
                            %>
                          </core:If>
                          <core:DefaultCase>
                            <%   gears = gears + listSeperator + gear.getId(); %>
                          </core:DefaultCase>
                        </core:ExclusiveIf> 
                        <option value="<%=gear.getId()%>"><%= gear.getName(response.getLocale()) %></option>
                    </core:ForEach> 
  
                    <% } %>

                    <core:Switch value="<%= key.getWidth() %>">
                      <core:Case value="<%=atg.portal.framework.RegionWidthEnum.NARROW%>">
                        <option value="">_________________</option>
                      </core:Case>
                      <core:DefaultCase>
                        <option value="">_______________________</option>
                      </core:DefaultCase>
                    </core:Switch>

                  </dsp:getvalueof>
                  </select></td>
          
                  <td valign="top">

                  <a href="<%="Javascript:move("+regionIdx+",'up','"+ selectFieldPos+"');"%>"><img name="<%="img"+ regionIdx+"_up"%>" src="images/arrow_up_16x16_dim.gif" width="16" height="16" border="0"></a><br>
          
                 <core:IfNotEqual object1="<%=selectFieldPos%>" object2='<%="Right"%>'>
                  <a href="<%="Javascript:move("+regionIdx+",'right','"+ selectFieldPos+"');"%>"><img name="<%="img"+ regionIdx+"_right"%>"  src="images/arrow_right_16x16_dim.gif" width="16" height="16" border="0"></a><br>
                 </core:IfNotEqual>
          
                 <core:IfGreaterThan int1="<%=regionIdx%>" int2="<%=0%>">
                  <a href="<%="Javascript:move("+regionIdx+",'left','"+ selectFieldPos+"');"%>"><img name="<%="img"+regionIdx+"_left"%>"  src="images/arrow_left_16x16_dim.gif" width="16" height="16" border="0"></a><br>
                 </core:IfGreaterThan> 
          
                  <a href="<%="Javascript:move("+regionIdx+",'down','"+ selectFieldPos+"');"%>"><img name="<%="img"+ regionIdx+"_down"%>"  src="images/arrow_down_16x16_dim.gif" width="16" height="16" border="0"></a><br>
          
                <br>
                  <a href="<%="Javascript:move("+regionIdx+",'x','"+ selectFieldPos+"');"%>"><img name="img<%=  regionIdx%>_x"  src="images/arrow_x_16x16_dim.gif" width="16" height="16" border="0"></a>
                
    
                  </td>
                  </tr>
                  </table>
                  <img src="images/clear.gif" width="90" height="1" border="0">

                  <dsp:input type="hidden" bean='<%= "PageLayoutFormHandler.finalGearArrangement[" +  regionIdx + "]" %>' name="<%= hiddenName%>" value="<%= gears%>"/>

                   <dsp:input type="hidden" bean='<%= "PageLayoutFormHandler.finalRegionOrder[" +  regionIdx + "]" %>' value="<%= key.getId()%>"/>

                  </td>
                  <%
                  regionIdx++;
                  regionCounter++;
                  %>

          </core:ForEach>
        </dsp:getvalueof>           

</dsp:getvalueof>
      </tr>
    </table>
</dsp:getvalueof>
</dsp:getvalueof>


<core:CreateUrl id="CpagesURLsuccess"       url="/portal/settings/community_pages.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"   value='<%=request.getParameter("paf_community_id")%>'/>
  <core:UrlParam param="paf_page_id"        value='<%=request.getParameter("paf_page_id")%>'/>
  <core:UrlParam param="mode"               value='<%=request.getParameter("mode")%>'/>

   <dsp:input type="hidden"  bean="PageLayoutFormHandler.successURL" value="<%=CpagesURLsuccess.getNewUrl()%>"/>
   <dsp:input type="hidden"  bean="PageLayoutFormHandler.failureURL" value="<%=CpagesURLsuccess.getNewUrl()%>"/>
   <%  bigResetUrl = CpagesURLsuccess.getNewUrl(); %>
</core:CreateUrl>


 <%-- keep this a high priority,  this is for expired sessions --%>
 <dsp:input type="hidden"  bean="PageLayoutFormHandler.previewRegDefSize" priority="10" />

<table boreder="0" cellpadding="0" cellspacing="0" >
<i18n:message id="submitLabel" key="update"/>
<tr><td>
<dsp:input type="SUBMIT" value="<%=submitLabel%>" bean="PageLayoutFormHandler.updateLayoutAdminMode"/>
</td><td>
</dsp:form>&nbsp;&nbsp;&nbsp;
</td><td>
<form name="bigReset" action="<%=bigResetUrl%>" method="post">
</td>
<i18n:message id="reset01" key="reset" />
<td>
<input type="SUBMIT" value="<%=reset01%>" >
</td>
</tr>
</table>


    </td>
  </tr>

 </table>
 
 
 </td></tr></table>

<script language="javascript">
 <!--
  doinit();
 //-->
</script>

</dsp:getvalueof><%-- listSeperator --%>

</admin:InitializeAdminEnvironment>

</dsp:page>
<%-- Version: $Change: 651448 $$DateTime: 2011/06/07 13:55:45 $--%>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_pages_edit_layout.jsp#2 $$Change: 651448 $--%>
