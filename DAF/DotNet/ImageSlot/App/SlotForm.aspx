<%@ Page language="c#" Codebehind="SlotForm.aspx.cs" AutoEventWireup="false" Inherits="SlotApp.WebForm1" %>
<%@ Register TagPrefix="cc1" Namespace="SlotWebControl" Assembly="SlotWebControl" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" >
<HTML>
	<HEAD>
		<title>WebForm1</title>
		<meta name="GENERATOR" Content="Microsoft Visual Studio 7.0">
		<meta name="CODE_LANGUAGE" Content="C#">
		<meta name="vs_defaultClientScript" content="JavaScript">
		<meta name="vs_targetSchema" content="http://schemas.microsoft.com/intellisense/ie5">
	</HEAD>
	<body MS_POSITIONING="GridLayout">
		<form id="Form1" method="post" runat="server">
			<cc1:ImageSlotWebControl id="ImageSlotWebControl1" style="Z-INDEX: 100; LEFT: 8px; POSITION: absolute; TOP: 8px" runat="server" Width="1px" Height="2px"></cc1:ImageSlotWebControl>
			<cc1:ImageSlotWebControl id="ImageSlotWebControl2" style="Z-INDEX: 101; LEFT: 9px; POSITION: absolute; TOP: 8px" runat="server" Width="89px" Height="39px" ImageRoot="http://localhost:8840/QuincyFunds/en/"></cc1:ImageSlotWebControl>
			&nbsp;
		</form>
	</body>
</HTML>
