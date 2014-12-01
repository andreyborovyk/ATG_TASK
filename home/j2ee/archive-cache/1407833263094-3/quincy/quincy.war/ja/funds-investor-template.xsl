<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
<xsl:template match="/fund">

  <p><b>ファンドタイプ： </b>
    <xsl:value-of select="type"/></p>

  <p><b>目標</b><br d="" />
    <xsl:value-of select="objective"/></p>

  <p><b>戦略</b><br d="" />
    <xsl:value-of select="strategy"/></p>
  
  <b>パフォーマンス</b> <table border="0" cellpadding="4" cellspacing="1" width="495">
       <tr valign="top" bgcolor="cccccc">
         <th>今年度本日現在</th>
         <th>１年</th>
         <th>３年</th>
         <th>５年</th>
         <th>10 年</th>
         <th>設定来</th>
       </tr>
       <tr bgcolor="dddddd" align="center">
         <td><xsl:value-of select="performanceNumbers/ytd"/></td>
         <td><xsl:value-of select="performanceNumbers/oneyear"/></td>
         <td><xsl:value-of select="performanceNumbers/threeyear"/></td>
         <td><xsl:value-of select="performanceNumbers/fiveyear"/></td>
         <td><xsl:value-of select="performanceNumbers/tenyear"/></td>
         <td><xsl:value-of select="performanceNumbers/sinceInception"/></td>
       </tr>

     </table> 

  <xsl:apply-templates/>
</xsl:template>

  <!-- ignore all not matched -->
  <xsl:template match="*" priority="-1"/>

</xsl:stylesheet>
<!-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/funds-investor-template.xsl#2 $$Change: 651448 $-->
