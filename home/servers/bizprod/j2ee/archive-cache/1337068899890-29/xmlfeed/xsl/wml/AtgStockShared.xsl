<?xml version="1.0" encoding="ISO-8859-1" ?> 

<!-- set up non breaking spaces to be typed w/o number -->

<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp   "&#160;">
]>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- make it so we can use break elements -->
<xsl:output method="xml" omit-xml-declaration="yes"/>

<xsl:param name="ticker" />
<xsl:param name="last" />
<xsl:param name="change" />


<xsl:template match="/">
   <xsl:apply-templates select="//AtgStock"/>
</xsl:template>


<xsl:template match="AtgStock">

   <p mode="nowrap">
   <table columns="3">
   
   <tr><td><xsl:value-of select="$ticker" /></td>
       <td><xsl:value-of select="$last" /></td>
       <td><xsl:value-of select="$change" /></td>
   </tr>
     <xsl:for-each select="Stock">


    
       <tr>
         <td><xsl:value-of select="ticker" /></td>

     
         <td><xsl:value-of select="last" /></td>

     
         <td><xsl:value-of select="substring-before(change,'(')" /></td>

       </tr>

     
     </xsl:for-each>
  </table>
  </p>
    
</xsl:template>



</xsl:stylesheet>
<!-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/xsl/wml/AtgStockShared.xsl#2 $$Change: 651448 $-->
