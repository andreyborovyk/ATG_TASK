package _dpsadmin_3__UTF_s8._atg._userprofiling._admin._en;

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

public class _dps_shome
extends atg.servlet.jsp.DynamoJspPageServlet implements atg.servlet.pagecompile.AttrCompiledServlet {
  public String getFileCacheAttributeName() {
    return "__002fatg_002fuserprofiling_002fadmin_002fen_002fdps_shome_xjhtml";
  }
  
  public static final long SOURCE_MODIFIED_TIME = 1308307614000L;
  
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
        /*** lines: 1-1 */
        __fileData.writeChars (0, 2, (CharFileDataSink)out);

    /* Do a test to see if compat mode is not on and show links to the demos
     * in this case */
    Object o = request.resolveName("/atg/userprofiling/ProfileAdapterRepository");
    if (o != null) {
          request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 6-6 */
        __fileData.writeChars (222, 41, (CharFileDataSink)out);
        out.print(request.encodeURL ("index.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 6-13 */
        __fileData.writeChars (274, 110, (CharFileDataSink)out);

    }
    else {
          /*** lines: 16-23 */
        __fileData.writeChars (417, 140, (CharFileDataSink)out);

    }
          /*** lines: 25-26 */
        __fileData.writeChars (579, 1, (CharFileDataSink)out);
/* @version $Id: //product/DPS/version/10.0.3/release/DPS/admin/atg/userprofiling/admin/en/dps-home.jhtml#2 $$Change: 651448 $*/        /*** lines: 26-27 */
        __fileData.writeChars (721, 2, (CharFileDataSink)out);
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
