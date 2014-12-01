<%-- 
Page:           favoriteEdit.jsp
Gear:           Favorite Communities
gearmode:       userConfig
displayMode:    full
Author:         Aimee Reveno
Description:    This is the full view of the edit mode for Favorite Communities.
                Use of this page by the user allows them to change the list of 
                their favorite communities, which generally displays in either 
                a drop-down or shared gear version on a page in the community.
--%>

<%@ page import="java.io.*,java.util.*,atg.repository.*" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/fcg-taglib" prefix="fcg" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">
<i18n:bundle baseName="atg.gears.communities.communities" localeAttribute="userLocale" changeResponseLocale="false" />

<blockquote>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:getvalueof id="profile" bean="Profile">
<dsp:importbean bean="/atg/portal/gear/communities/EditFavorites"/>

<fcg:UserCommunities id="faves" user="<%= (atg.repository.RepositoryItem) profile %>">

<% String strActionURL = null; %>

<core:CreateUrl id="actionURL" url="<%= pafEnv.getOriginalRequestURI()%>">
   <core:UrlParam param="paf_dm" value='<%=request.getParameter("paf_dm")%>'/>
   <core:UrlParam param="paf_gm" value='<%=request.getParameter("paf_gm")%>'/>
   <core:UrlParam param="paf_gear_id" value='<%=request.getParameter("paf_gear_id")%>'/>
<%  strActionURL = actionURL.getNewUrl(); 
%>
</core:CreateUrl>

<dsp:form name="rearrange" method="post" action="<%= strActionURL %>">

<!-- need hidden vars for edit gear mode -->
<input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>" >
<input type="hidden" name="paf_gear_id" value="<%=pafEnv.getGear().getId() %>" >

<dsp:input type="hidden" bean="EditFavorites.userId"
	value="<%= ((atg.repository.RepositoryItem) profile).getRepositoryId() %>"/>
<dsp:input type="hidden" bean="EditFavorites.strFavorites"
	paramvalue="favorite_hidden_0"/>
<dsp:input type="hidden" bean="EditFavorites.successURL" 
	value="<%= pafEnv.getOriginalRequestURI() %>"/>


<br><font size="-1"><b><i18n:message key="edit-favorites-title"/></b></font><br>

<core:Switch value='<%=request.getParameter("javascript")%>'>
<core:Case value="false">
<script language="javascript">
<!--
function doinit(){}
//-->
</script>
</core:Case>
<core:DefaultCase>
<script language="javascript">

<!--

var pcImg = new Array();
pcImg[0] = new Image;
pcImg[1] = new Image;
pcImg[2] = new Image;
pcImg[3] = new Image;
pcImg[4] = new Image;
pcImg[0].src = "/gear/communities/images/arrow_up_16x16_dim.gif";
pcImg[1].src = "/gear/communities/images/arrow_right_16x16_dim.gif";
pcImg[2].src = "/gear/communities/images/arrow_left_16x16_dim.gif";
pcImg[3].src = "/gear/communities/images/arrow_down_16x16_dim.gif";
pcImg[4].src = "/gear/communities/images/arrow_x_16x16_dim.gif";

var pcImgo = new Array();
pcImgo[0] = new Image;
pcImgo[1] = new Image;
pcImgo[2] = new Image;
pcImgo[3] = new Image;
pcImgo[4] = new Image;
pcImgo[0].src = "/gear/communities/images/arrow_up_16x16.gif";
pcImgo[1].src = "/gear/communities/images/arrow_right_16x16.gif";
pcImgo[2].src = "/gear/communities/images/arrow_left_16x16.gif";
pcImgo[3].src = "/gear/communities/images/arrow_down_16x16.gif";
pcImgo[4].src = "/gear/communities/images/arrow_x_16x16.gif";

var arrowSeq                 = new Array("up","right","left","down","x");
var arrowSeqRuleLeft     = new Array("up","right","","down","x");
var arrowSeqRuleMiddle = new Array("up","right","left","down","x");
var arrowSeqRuleRight   = new Array("up","","left","down","x");

var regionDef = new Array();
var redraw = new Array(false,false,false,false,false,false);

var isIE = ( navigator.appVersion.indexOf('MSIE') > -1 ) ? true : false ;

function highLight( srcSelect , rule  ) {
  srcObj = eval("document.forms['rearrange'].favorite_selection_"+srcSelect);
       sideway = false;
       currRegionStyle = regionDef[srcSelect];
       for (j=0; j < regionDef.length; j++) { 
              if(srcSelect != j ) {
                   if ( regionDef[srcSelect] == regionDef[j] )    { sideway = true ;} 
              };
       };
      if ( srcObj[srcObj.selectedIndex].value.indexOf("zzz")  != 0 ) { sideway = true ;}
        for (  i=0 ;  i<arrowSeq.length  ;   i++ ) {
         id =  "img"+srcSelect+"_"+arrowSeq[i] ;
         var theImage = FWFindImage(document, id, 0);
         if (theImage) { theImage.src = pcImg[i].src; }
          if (srcObj.options[srcObj.selectedIndex].value != "") {
           if  (      ( !  (  (  srcObj.selectedIndex == 0 ) && ( i == 0 ) )  )
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
                     objRef = eval("document.forms['rearrange'].favorite_selection_"+selectRef);
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

function remove(selectRef , direction, rule ) {
    objRef = eval("document.forms['rearrange'].favorite_selection_"+selectRef);

    objRefR = eval("document.forms['rearrange'].favorite_removed");
    if ( objRefR.value != "" ) {
     objRefR.value = objRefR.value +","+ objRef.options[objRef.selectedIndex].value+"";
    } else {
      objRefR.value =  objRef.options[objRef.selectedIndex].value+"";
    }
    objRef.options[objRef.selectedIndex].value = "";
    redraw[selectRef] = true;
    rewriteHidden();
}

function move( selectRef , direction, rule ) {
              redraw = new Array(false,false,false,false,false,false);
              if ( document.images['img'+selectRef+'_'+direction].src.indexOf("dim.gif") !=  -1 ) {  return ; }
              redraw[selectRef] = true;
              objRef = eval("document.forms['rearrange'].favorite_selection_"+selectRef);
              trigTarRedraw = 1;
              if (direction == "down" ||  direction == "up") {
                      moveUD(selectRef , direction, rule);
                     highLight( selectRef , rule );
              } else if (direction == "left" ||  direction == "right") {
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
                         tarL =   (  tempOptionV.indexOf("n") == 0    ) ?    "n" : "w" ;
                         if (dirN == -1) {
                           for (j=0; j < regionDef.length; j++) { 
                              if ( (selectRef != j ) && ( regionDef[selectRef] == regionDef[j] ) )  { refN = j ; break;} 
                           };
                         } else {
                            for ( j=regionDef.length; j > -1  ; j--) { 
                             if ( (selectRef != j ) && ( regionDef[selectRef] ==  regionDef[j] ) )   { refN = j ; break;}  };
                         };
                     };
                if ( refN > -1 ) {
                 targetObjRef = eval(        "document.forms['rearrange'].favorite_selection_"+ refN );
                 targetObjRef.options[ targetObjRef.length ] = new Option (  tempOptionT  ,  tempOptionV , true , true );


                   for ( z=0 ; z < targetObjRef.options.length ; z++) {
                     targetObjRef.options[z].selected = false;
                   }

                 redraw[selectRef] = true;
                 redraw[refN]         = true;
                }

              dimAll(selectRef);

              }; 
              rewriteHidden();

             // highLight( selectRef,   rule );
              if ( trigTarRedraw == 1) { 
                  //highLight( refN  ,       rule );
              }
       
};


function dimAll(numRef) {
    for (  m=0 ;  m<arrowSeq.length  ;   m++ ) {
         id =  "img"+numRef+"_"+arrowSeq[m] ;
         var theImage = FWFindImage(document, id, 0);
         if (theImage) { theImage.src = pcImg[m].src; }

    }

}

function rewriteHidden() {
      for ( k = 0 ; k < redraw.length-1 ; k++ ) {
                     if ( redraw[k] == true ) {
                            tempT = "";
                            objRef =  eval( "document.forms['rearrange'].favorite_selection_"+k );
                            for ( i = 0 ; i < objRef.options.length ; i++ ){
                                if ( objRef.options[i].value == "" ) {                                  
                                      objRef.options[i] = null;
                                }
                            };
                            for ( i = 0 ; i < objRef.options.length ; i++ ){
                              tempT = tempT + objRef.options[i].value;
     if ( ( i < objRef.options.length -1 ) && ( objRef.options[i+1].value  != "" )  )  tempT = tempT + ",";
                            };
                            objRefHidden = eval("document.forms['rearrange'].favorite_hidden_"+k  );
                            objRefHidden.value = tempT;
                     };
         
          };
}

function doinit() {
        thisForm = eval ( " document.forms['rearrange'] " );
        if (thisForm != null ) {
   for ( i = 0 ; i < 15 ; i++ ) {
                   if ( eval ( "thisForm.favorite_selection_"+i ) != null ) { 
           regionDef[i] = "n";
           redraw[i] = true;
        }
    }
        }
  rewriteHidden();
} 

function doNull(){}

//-->

</script>
</core:DefaultCase>
</core:Switch>

<table border=0 cellspacing="0" cellpadding="3" width="320">

<core:If value="<%= faves.getFavorites().isEmpty() %>">
<tr>
	<td colspan=3>
		<font size="-1"><BR><i18n:message key="no-faves-msg1"/>&nbsp;&nbsp;
		<i18n:message key="no-faves-msg2"/></font>
	</td>
</tr>
</core:If>

<core:IfNot value="<%= faves.getFavorites().isEmpty() %>">
<tr>

<input type="hidden" name="paf_gear_id" value="<%=request.getParameter("paf_gear_id")%>">
<input type="hidden" name="paf_gm" value="<%=request.getParameter("paf_gm")%>">
<input type="hidden" name="paf_dm" value="<%=request.getParameter("paf_dm")%>">

<td align="right" rowspan="2">

<core:Switch value='<%=request.getParameter("javascript")%>'>
<core:Case value="false">

 <select size="12" name="favorite_selection_0" size="10"  style="width:190">
  <core:ForEach id="communityLinks" values="<%= faves.getFavorites() %>"
	castClass="atg.portal.framework.Community" elementId="community">
     <option value="<%= community.getId( )%>"><%= community.getName(response.getLocale())%></option>
  </core:ForEach>
 <option value="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>

</core:Case>
<core:DefaultCase>

<select size="12" name="favorite_selection_0" size="10"  style="width:190"  onChange="highLight(0,'Middle',this)">
  <core:ForEach id="communityLinks" values="<%= faves.getFavorites() %>"
	castClass="atg.portal.framework.Community" elementId="community">
     <option value="<%= community.getId( )%>"><%= community.getName(response.getLocale())%></option>
  </core:ForEach>
 <option value="">_____________________________</option>

</core:DefaultCase>
</core:Switch>
</select>

<core:Switch value='<%=request.getParameter("javascript")%>'>
<core:Case value="false">
</td><td valign="top">
<input type="image" value="up" name="up"  src="/gear/communities/images/arrow_up_16x16.gif" width="16" height="16" border="0"><br>
<input type="image" value="down" name="down"  src="/gear/communities/images/arrow_down_16x16.gif" width="16" height="16" border="0"><br><br><br>
<input type="image" value="remove" name="remove"  src="/gear/communities/images/arrow_x_16x16.gif" width="16" height="16" border="0">


</core:Case>

<core:DefaultCase>
</td><td valign="top">

<a 
href="Javascript:doNull();" 
onClick="move(0,'up','Middle');return true;"
><img name="img0_up" src="<%= pafEnv.getGear().getServletContext() %>/images/arrow_up_16x16_dim.gif" width="16" height="16" border="0"></a><br>
<a
 href="Javascript:doNull()"
 onClick="move(0,'down','Middle');return true;"


><img name="img0_down"  src="<%= pafEnv.getGear().getServletContext() %>/images/arrow_down_16x16_dim.gif" width="16" height="16" border="0"></a><br><br><br>
<a href="Javascript:doNull()" 
onclick="remove(0,'x','Middle');return true;"><img name="img0_x"  src="<%= pafEnv.getGear().getServletContext() %>/images/arrow_x_16x16_dim.gif" width="16" height="16" border="0"></a>


</core:DefaultCase>
</core:Switch>

</td>
</tr>
<tr><td colspan="1" valign="bottom">
<i18n:message id="doneButtonText" key="done-button-text"/>
<dsp:input type="submit" bean="EditFavorites.updateFavorites" name="Submit"  value="<%= doneButtonText %>"/>&nbsp;&nbsp;

</td>
</tr>
<tr><td colspan="3">

<core:IfNull value='<%=request.getParameter("favorite_hidden_0")%>'  >

 <% String communityFieldList = null; %>

   <core:ForEach id="communityLinks" values="<%= faves.getFavorites() %>"
	castClass="atg.portal.framework.Community" elementId="community">
 
    <core:IfNotNull value="<%= communityFieldList%>"  >
     <% communityFieldList = communityFieldList+","+community.getId();  %>
    </core:IfNotNull>
   
    <core:IfNull    value="<%= communityFieldList%>"  >
     <% communityFieldList = community.getId();  %>
    </core:IfNull>
  
   </core:ForEach>
 
 <dsp:input type="hidden" name="favorite_hidden_0" value="<%=communityFieldList%>" bean="EditFavorites.strFavorites"/>
</core:IfNull>
<!--  HERE  -->
<dsp:input type="hidden" name="favorite_removed" bean="EditFavorites.strFavesRemoved" paramvalue="favorite_removed" />

<core:IfNotNull value='<%=request.getParameter("favorite_hidden_0")%>'  >

<dsp:input type="hidden" name="favorite_hidden_0" bean="EditFavorites.strFavorites" paramvalue="favorite_hidden_0" />



</core:IfNotNull>

<core:CreateUrl id="fullChildPageUrl" url="<%= pafEnv.getPageURI(pafEnv.getPage()) %>">

<dsp:valueof param="success"/>

<input type="hidden" name="successUrl" value="<%=fullChildPageUrl.getNewUrl()%>"/>
</core:CreateUrl>
<br>
<core:Switch value='<%=request.getParameter("javascript")%>'>
<core:Case value="false">
<input type="hidden" name="javascript" value="false">

<core:CreateUrl id="noJavascriptUrl" url="<%= pafEnv.getOriginalRequestURI()%>">
   <core:UrlParam param="paf_dm" value='<%=request.getParameter("paf_dm")%>'/> 
   <core:UrlParam param="paf_gm" value='<%=request.getParameter("paf_gm")%>'/> 
   <core:UrlParam param="paf_gear_id" value='<%=request.getParameter("paf_gear_id")%>'/> 
   <core:UrlParam param="javascript" value="true"/> 

<font size=-2 face="helvetica,verdana,arial,geneva"><a href="<%= noJavascriptUrl.getNewUrl()%>"><i18n:message key="reactivate-javascript-link"/></a></font>

</core:CreateUrl>



</core:Case>
<core:DefaultCase>
<script language="Javascript">
<!--

document.writeln("<input type='hidden' name='javascript' value='true'>");

//-->
</script>
<%--
<core:CreateUrl id="noJavascriptUrl" url="<%= pafEnv.getOriginalRequestURI()%>">
   <core:UrlParam param="paf_dm" value='<%=request.getParameter("paf_dm")%>'/> 
   <core:UrlParam param="paf_gm" value='<%=request.getParameter("paf_gm")%>'/> 
   <core:UrlParam param="paf_gear_id" value='<%=request.getParameter("paf_gear_id")%>'/> 
   <core:UrlParam param="javascript" value="false"/> 

<font size=-2 face="helvetica,verdana,arial,geneva"><a href="<%= noJavascriptUrl.getNewUrl()%>"><i18n:message key="deactivate-javascript-link"/></a></font>

</core:CreateUrl>
--%>
</core:DefaultCase>
</core:Switch>
</td>
</tr>

</core:IfNot>
</table>
</dsp:form>



</blockquote>


</fcg:UserCommunities>
</dsp:getvalueof>

<script language="javascript">
<!--
 doinit();
//-->
</script>

</paf:InitializeGearEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/communities/communities.war/favoriteEdit.jsp#2 $$Change: 651448 $--%>
