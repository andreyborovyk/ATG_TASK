<HTML>
<HEAD><TITLE>Repository Item in ATG XML Format</TITLE>
</HEAD>
<I>
<P>
This JSP page illustrates a quick and dirty way of expressing a repository 
item in the XML format understood by the ATG Repository Loader.
<P>
This can be useful as a easy way of reverse engineering the required format for 
a particular item descriptor. 
<P>
In this page we use the GetService to render a repository item of type "user." 
This type is defined as part of the Example submodule in the RL module. You will
need to have run the loader on the configured content root at least once for it
to produce any result.
<P>
Depending on your browser you will likely have to view the resulting page as source
in order to see the generated XML.
<P>
Generated output follows:
</I>
<hr>

<%
atg.repository.Repository r = (atg.repository.Repository) atg.nucleus.Nucleus.getGlobalNucleus().resolveName("/atg/rl-example/ExampleRepository");
if(r==null) {
  out.println("couldn't resolve repository instance, /atg/rl-example/ExampleRepository");
  return;
}
 
atg.repository.xml.GetService service = (atg.repository.xml.GetService) atg.nucleus.Nucleus.getGlobalNucleus().resolveName("/atg/repository/xml/GetService");
if(service==null) {
  out.println("couldn't resolve GetService instance.");
  return;
}
 
atg.repository.RepositoryItem item = r.getItem("user001", "user");
if(item==null) {
  out.println("couldn't find a repository with id, \"user001\", of item descriptor, \"user\"");
  return;
}
service.setIndentXMLOutput(false); // required
String xml = service.getItemAsXML(item, "nonContentTypeMapping.xml");
 
out.println("<br><br><br>");
out.println(xml);
                                                                                
%>


</HTML>
<%-- @version $Id: //product/DAS/version/10.0.3/release/RL/Example/j2ee-apps/example/web-app/itemAsXml.jsp#2 $$Change: 651448 $--%>
