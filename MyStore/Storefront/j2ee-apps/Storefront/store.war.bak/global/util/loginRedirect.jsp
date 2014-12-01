<dsp:page>
<%-- The reason this page is needed is that if you try to set a bean in an
     anchor tag whose target page is a restricted page, the AccessControlServlet
     intervenes before the bean property set method is called --%>

<HTML>
    <META HTTP-EQUIV=Refresh CONTENT="0; URL=../../myaccount/login.jsp?error=${param['error']}">
</HTML>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/global/util/loginRedirect.jsp#3 $$Change: 635816 $--%>
