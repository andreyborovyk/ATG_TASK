package _dasadmin_3._atg._dynamo._admin._en;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import atg.nucleus.*;
import atg.naming.*;
import atg.service.filecache.*;
import atg.servlet.*;
import atg.droplet.*;
import atg.servlet.pagecompile.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import atg.servlet.jsp.*;
import atg.applauncher.*;
import atg.core.util.*;

public class _running_sproducts
extends atg.servlet.jsp.DynamoJspPageServlet implements atg.servlet.pagecompile.AttrCompiledServlet {
  public String getFileCacheAttributeName() {
    return "__002fatg_002fdynamo_002fadmin_002fen_002frunning_sproducts_xjhtml";
  }
  
  public static final long SOURCE_MODIFIED_TIME = 1308316596000L;
  
  public static final String[] INCLUDED_SOURCE_URIS = null;
  public static final long[] INCLUDED_SOURCE_MODIFIED_TIMES = null;
  public static String[] _jspDynGetSourceURIs() {
    return INCLUDED_SOURCE_URIS;
  }
  
  public final static String _JSP_ENCODING =   null  ;
  
  public static String _jspGetEncoding() {
    return _JSP_ENCODING;
  }
  

  //-------------------------------
  {
  
    DropletImports _imports = new DropletImports();
    this. setParameter("_imports", _imports);
  }
  
  //-------------- The _jspService method
  public void _jspService (DynamoHttpServletRequest request,
                       DynamoHttpServletResponse response)
      throws ServletException, IOException
  {
    ByteFileData __fileData = null;
    try {
      __fileData = (ByteFileData)       request.getAttribute(getFileCacheAttributeName())      ;
      JspFactory _jspFactory = DynamoJspFactory.getDynamoJspFactory();
      DynamoJspPageContext pageContext = (DynamoJspPageContext)_jspFactory.getPageContext(
        this, request, response, 
        null,true, JspWriter.DEFAULT_BUFFER, true);
        ServletConfig config = getServletConfig();
        ServletContext application = config.getServletContext();
        HttpSession session = pageContext.getSession();
        Object page = this;
      
      ByteBufferedBodyContent out = (ByteBufferedBodyContent)pageContext.getOut();
      
      int _jspTempReturn;
      
      try {

        FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
        DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);


String languages = request.getHeader("ACCEPT-LANGUAGE");
String result = "en";
if (languages != null) {
    for (;;) {
      String attempt = languages.substring(0,2);
      // check attempt
      String path = request.getRealPath("/atg/dynamo/admin/" + attempt);
      if ( (new java.io.File (path)).isDirectory() ) {
        // success
        result = attempt;
        break;
      }
      int index = languages.indexOf(",");
      if (index == -1)
        break;
      languages = languages.substring(index+1);
    }
}

request.setParameter( "adminLang", result );

        /*** lines: 24-26 */
        __fileData.writeBytes (585, 2, out);
        /*** lines: 29-29 */
        __fileData.writeBytes (653, 95, out);
        out.print(request.encodeURL ("/atg/dynamo/admin/admin.css", false, true, true, true));
        /*** lines: 29-29 */
        __fileData.writeBytes (775, 93, out);
        out.print(request.encodeURL ("images/admin-banner.gif", false, true, true, true));
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 29-29 */
        __fileData.writeBytes (891, 84, out);
        out.print(request.encodeURL ("/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 29-54 */
        __fileData.writeBytes (976, 232, out);

   AppLauncher launcher = 
      AppLauncher.getAppLauncher(AppLauncher.class);
   
   if(launcher != null) {
     List modules = launcher.getModules();
     Collections.sort(modules, new Comparator<AppModule>() 
    {
      public int compare(AppModule o1, AppModule o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
      public boolean equals(Object o) {
        return o == this;
      }
    }
    );
   
    for(Object o : modules) {
       AppModule module = (AppModule)o;
       String prodName = module.getAttribute(AppModuleInterface.PRODUCT_NAME);
       String modName = module.getName();

       // See if there's a patch version. If not, use the product version
       String prodVersion = module.getAttribute
	(AppModuleInterface.PRODUCT_PATCH_VERSION);
       if(StringUtils.isBlank(prodVersion))
         prodVersion = module.getAttribute(AppModuleInterface.PRODUCT_VERSION);

       // Same thing for build number
       String buildNum =
	 module.getAttribute(AppModuleInterface.PATCH_BUILD_NUMBER);
       if(StringUtils.isBlank(buildNum)) 
         buildNum = module.getAttribute(AppModuleInterface.BUILD_NUMBER);
     
       if(prodName == null)
         prodName = modName;
       if(prodVersion == null)
         prodVersion = "";
       if(buildNum == null || buildNum.equals("0"))
         buildNum = "";
     
       out.println("<tr>");
       out.println("<td>");
       out.println(modName);
       out.println("</td>");
       out.println("<td>");
       out.println(prodName);
       out.println("</td>");
       out.println("<td>");
       out.println(prodVersion);
       out.println("</td>");
       out.println("<td>");
       out.println(buildNum);
       out.println("</td>");
       out.println("</tr>");
     }
   }
          /*** lines: 111-116 */
        __fileData.writeBytes (3025, 27, out);
/* @version $Id: //product/DAS/version/10.0.3/release/DAS/admin/atg/dynamo/admin/en/running-products.jhtml#2 $$Change: 651448 $*/        /*** lines: 116-116 */
        __fileData.writeBytes (3194, 1, out);
      } catch (Exception e) {
        if (!(e instanceof EndOfPageException)) {
          // out.clear();
          out.clearBuffer();
          pageContext.handlePageException(e);
        }
      }
      finally {
        pageContext.cleanupPoppedBodyContent();
        out.close();
        _jspFactory.releasePageContext(pageContext);
      }
    }
    finally {
      if (__fileData != null) __fileData.close();
    }
  }
}
