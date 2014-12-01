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

public class _conf_sreporter
extends atg.servlet.jsp.DynamoJspPageServlet implements atg.servlet.pagecompile.AttrCompiledServlet {
  public String getFileCacheAttributeName() {
    return "__002fatg_002fdynamo_002fadmin_002fen_002fconf_sreporter_xjhtml";
  }
  
  public static final long SOURCE_MODIFIED_TIME = 1308307594000L;
  
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
    _imports.addImport("/atg/dynamo/service/ConfigurationReporter");
    _imports.addImport("/atg/dynamo/droplet/Switch");
    _imports.addImport("/atg/dynamo/droplet/ForEach");
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
        /*** lines: 5-5 */
        __fileData.writeBytes (135, 63, out);
        out.print(request.encodeURL ("/atg/dynamo/admin/admin.css", false, true, true, true));
        /*** lines: 5-5 */
        __fileData.writeBytes (225, 148, out);
        out.print(request.encodeURL ("images/admin-banner.gif", false, true, true, true));
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 5-5 */
        __fileData.writeBytes (396, 95, out);
        out.print(request.encodeURL ("/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 5-5 */
        __fileData.writeBytes (492, 207, out);
        out.print(request.encodeURL ("config-reporter-admin-html-output.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 5-5 */
        __fileData.writeBytes (738, 302, out);
        out.print(request.encodeURL ("config-reporter-bean-representation.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 5-5 */
        __fileData.writeBytes (1081, 177, out);
        out.print(request.encodeURL ("config-reporter-property-representation.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 5-5 */
        __fileData.writeBytes (1303, 342, out);
        out.print(request.encodeURL ("config-reporter-conf-path-titled.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 5-56 */
        __fileData.writeBytes (1683, 137, out);
/* @version $Id: //product/DAS/version/10.0.3/release/DAS/admin/atg/dynamo/admin/en/conf-reporter.jhtml#2 $$Change: 651448 $*/        /*** lines: 56-56 */
        __fileData.writeBytes (1959, 1, out);
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
