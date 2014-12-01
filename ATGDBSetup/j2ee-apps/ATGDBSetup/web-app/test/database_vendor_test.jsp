
Enter DataSource Nucleus path:<p>

<form action="<%=request.getRequestURI()%>" method="GET">

<input type="text" name="nucleusPath"/>

<input type="submit"/>

</form>

<%

try {

   String nucleusName = request.getParameter("nucleusPath");

   if (nucleusName != null) {
   javax.sql.DataSource ds = (javax.sql.DataSource)
     atg.nucleus.Nucleus.getGlobalNucleus().resolveName(nucleusName);

   java.sql.Connection conn = ds.getConnection();
   String vendorName = conn.getMetaData().getDatabaseProductName();

   if (vendorName == null) out.println("vendor name is null");
    else out.println("Database Vendor Name:  " + vendorName);
   }

} catch (Exception e) {
  e.printStackTrace(new java.io.PrintWriter(out));
}

%>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/test/database_vendor_test.jsp#2 $$Change: 651448 $--%>
