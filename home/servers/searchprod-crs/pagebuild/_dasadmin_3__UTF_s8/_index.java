package _dasadmin_3__UTF_s8;

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

public class _index
extends atg.servlet.jsp.DynamoJspPageServlet implements atg.servlet.pagecompile.AttrCompiledServlet {
  public String getFileCacheAttributeName() {
    return "__002findex_xjhtml";
  }
  
  public static final long SOURCE_MODIFIED_TIME = 1308307596000L;
  
  public static final String[] INCLUDED_SOURCE_URIS = null;
  public static final long[] INCLUDED_SOURCE_MODIFIED_TIMES = null;
  public static String[] _jspDynGetSourceURIs() {
    return INCLUDED_SOURCE_URIS;
  }
  
  public final static String _JSP_ENCODING =   "UTF-8"  ;
  
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
    CharFileData __fileData = null;
    try {
      __fileData = (CharFileData)
      request.getAttribute(getFileCacheAttributeName())      ;      JspFactory _jspFactory = DynamoJspFactory.getDynamoJspFactory();
      DynamoJspPageContext pageContext = (DynamoJspPageContext)_jspFactory.getPageContext(
        this, request, response, 
        null,true, JspWriter.DEFAULT_BUFFER, true);
        ServletConfig config = getServletConfig();
        ServletContext application = config.getServletContext();
        HttpSession session = pageContext.getSession();
        Object page = this;
      
      CharBufferedBodyContent out = (CharBufferedBodyContent)pageContext.getOut();
      
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

response.sendLocalRedirect(response.encodeURL("atg/dynamo/admin/" +
    result + "/"), request);

        /*** lines: 25-26 */
        __fileData.writeChars (637, 1, (CharFileDataSink)out);
/* @version $Id: //product/DAS/version/10.0.3/release/DAS/admin/index.jhtml#2 $$Change: 651448 $*/        /*** lines: 26-27 */
        __fileData.writeChars (749, 2, (CharFileDataSink)out);
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
