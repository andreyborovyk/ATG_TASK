<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
<xsl:template match="/fund">

  <p><b>ファンドタイプ </b>
    <xsl:value-of select="type"/></p>

  <p><b>目標</b><br d="" />
    <xsl:value-of select="objective"/></p>

  <p><b>戦略</b><br d="" />
    <xsl:value-of select="strategy"/></p>
  
  <b>パフォーマンス</b> <table border="0" cellpadding="4" cellspacing="1" width="495">
    <tr valign="bottom" bgcolor="cccccc">
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

  <p><b>競争情報</b><br d="" />
  <xsl:value-of select="competitiveInfo/intro"/></p>  

  <table border="0" cellpadding="4" cellspacing="1" width="495">
    <tr valign="bottom" bgcolor="cccccc">
      <th>ファンド</th>
      <th>今年度本日現在</th>
      <th>１年</th>
      <th>３年</th>
      <th>５年</th>
      <th>10 年</th>
      <th>設定来</th>
    </tr>

  <xsl:for-each select="competitiveInfo/item"> <tr align="center" bgcolor="dddddd">
      <td align="left"><xsl:value-of select="compFundName" /></td>
      <td><xsl:value-of select="ytd" /></td>
      <td><xsl:value-of select="oneyear" /></td>
      <td><xsl:value-of select="threeyear" /></td>
      <td><xsl:value-of select="fiveyear" /></td>
      <td><xsl:value-of select="tenyear" /></td>
      <td><xsl:value-of select="sinceInception" /></td>  
    </tr>
  </xsl:for-each> </table>

  <p><b>コミッション体系</b> <table border="0" cellpadding="4" cellspacing="1">
    <tr>
      <td>0 - 10,000 ドル</td>
      <td><xsl:value-of select="commissionStructure/zeroToTen" /></td>
    </tr>
    <tr>
      <td>10,000 - 100,000 ドル</td>
      <td><xsl:value-of select="commissionStructure/tenToHundred" /></td>
    </tr>
    <tr>
      <td>100,000 ドル -</td>
      <td><xsl:value-of select="commissionStructure/overHundred" /></td>
    </tr>
  </table>
  </p>
  <xsl:apply-templates/>
</xsl:template>

  <!-- ignore all not matched -->
  <xsl:template match="*" priority="-1"/>

</xsl:stylesheet>
<!-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/funds-broker-template.xsl#2 $$Change: 651448 $-->
