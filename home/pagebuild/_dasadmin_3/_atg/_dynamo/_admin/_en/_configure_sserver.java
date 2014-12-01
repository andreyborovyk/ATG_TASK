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

public class _configure_sserver
extends atg.servlet.jsp.DynamoJspPageServlet implements atg.servlet.pagecompile.AttrCompiledServlet {
  public String getFileCacheAttributeName() {
    return "__002fatg_002fdynamo_002fadmin_002fen_002fconfigure_sserver_xjhtml";
  }
  
  public static final long SOURCE_MODIFIED_TIME = 1308307596000L;
  static final atg.droplet.PropertyName _beanName0 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/Configure.serverName");
  static final atg.droplet.PropertyName _beanName1 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/Configure.serverName");
  static final atg.droplet.PropertyName _beanName2 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/Configure.serverName");
  public class _Param_0_original extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      ByteFileData __fileData = null;
      {
        __fileData = (ByteFileData)         request.getAttribute(getFileCacheAttributeName())        ;
        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        ByteBufferedBodyContent out = (ByteBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            {
              AnchorTag _anchor = ((DropletEventServlet)request.getAttribute(DropletConstants.DROPLET_EVENT_ATTRIBUTE)).addAnchor(request, response, "/atg/dynamo/admin/en/configure-server.jhtml.1_A", "/atg/dynamo/admin/Configure.serverName", null, null);
              if (_anchor != null) {
                 String href = ServletUtil.getRequestURI(request, "configure-server-services.jhtml");
                _anchor.addHref(href);
              }
            }
            request.addQueryParameter("_DAV", "original");
            request.addQueryParameter("_dynSessConf", Long.toString(request.getSessionConfirmationNumber()));            /*** lines: 62-62 */
            __fileData.writeBytes (1628, 66, out);
            out.print(request.encodeURL ("configure-server-services.jhtml", true, true, false, true));
            /*** lines: 62-62 */
            __fileData.writeBytes (1725, 17, out);
            /*** lines: 62-62 */
            __fileData.writeBytes (1769, 1, out);
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 62-70 */
            __fileData.writeBytes (1786, 151, out);
          }
          finally {
            if (_saveBaseDir != null) request.setBaseDirectory(_saveBaseDir);
          }
        } catch (Exception e) {
          // out.clear();
          out.clearBuffer();
          pageContext.handlePageException(e);
        }
      }
    }
  }
  _Param_0_original m_Param_0_original = this. new _Param_0_original();
  {
    m_Param_0_original.setParent(this);
  }
  public class _Param_1_default_0020configuration extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      ByteFileData __fileData = null;
      {
        __fileData = (ByteFileData)         request.getAttribute(getFileCacheAttributeName())        ;
        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        ByteBufferedBodyContent out = (ByteBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            {
              AnchorTag _anchor = ((DropletEventServlet)request.getAttribute(DropletConstants.DROPLET_EVENT_ATTRIBUTE)).addAnchor(request, response, "/atg/dynamo/admin/en/configure-server.jhtml.2_A", "/atg/dynamo/admin/Configure.serverName", null, null);
              if (_anchor != null) {
                 String href = ServletUtil.getRequestURI(request, "configure-server-services.jhtml");
                _anchor.addHref(href);
              }
            }
            request.addQueryParameter("_DAV", request.getParameter("element"));
            request.addQueryParameter("_dynSessConf", Long.toString(request.getSessionConfirmationNumber()));            /*** lines: 73-73 */
            __fileData.writeBytes (2036, 41, out);
            out.print(request.encodeURL ("configure-server-services.jhtml", true, true, false, true));
            /*** lines: 73-73 */
            __fileData.writeBytes (2108, 17, out);
            /*** lines: 73-73 */
            __fileData.writeBytes (2152, 16, out);
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 73-82 */
            __fileData.writeBytes (2189, 159, out);
          }
          finally {
            if (_saveBaseDir != null) request.setBaseDirectory(_saveBaseDir);
          }
        } catch (Exception e) {
          // out.clear();
          out.clearBuffer();
          pageContext.handlePageException(e);
        }
      }
    }
  }
  _Param_1_default_0020configuration m_Param_1_default_0020configuration = this. new _Param_1_default_0020configuration();
  {
    m_Param_1_default_0020configuration.setParent(this);
  }
  public class _Param_2_default extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      ByteFileData __fileData = null;
      {
        __fileData = (ByteFileData)         request.getAttribute(getFileCacheAttributeName())        ;
        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        ByteBufferedBodyContent out = (ByteBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            {
              AnchorTag _anchor = ((DropletEventServlet)request.getAttribute(DropletConstants.DROPLET_EVENT_ATTRIBUTE)).addAnchor(request, response, "/atg/dynamo/admin/en/configure-server.jhtml.3_A", "/atg/dynamo/admin/Configure.serverName", null, null);
              if (_anchor != null) {
                 String href = ServletUtil.getRequestURI(request, "configure-server-services.jhtml");
                _anchor.addHref(href);
              }
            }
            request.addQueryParameter("_DAV", request.getParameter("element"));
            request.addQueryParameter("_dynSessConf", Long.toString(request.getSessionConfirmationNumber()));            /*** lines: 85-85 */
            __fileData.writeBytes (2429, 41, out);
            out.print(request.encodeURL ("configure-server-services.jhtml", true, true, false, true));
            /*** lines: 85-85 */
            __fileData.writeBytes (2501, 17, out);
            /*** lines: 85-85 */
            __fileData.writeBytes (2545, 16, out);
            /*** lines: 85-89 */
            __fileData.writeBytes (2582, 1, out);
            if (!request.serviceParameter("element", request, response, PageCompileServlet.getEscapeHTMLTagConverter(), null)) {
            }
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 89-92 */
            __fileData.writeBytes (2618, 59, out);
          }
          finally {
            if (_saveBaseDir != null) request.setBaseDirectory(_saveBaseDir);
          }
        } catch (Exception e) {
          // out.clear();
          out.clearBuffer();
          pageContext.handlePageException(e);
        }
      }
    }
  }
  _Param_2_default m_Param_2_default = this. new _Param_2_default();
  {
    m_Param_2_default.setParent(this);
  }
  public class _SubServlet_3 extends PageSubServlet {
    {
      this.setParameter("original", m_Param_0_original);
      this.setParameter("default configuration", m_Param_1_default_0020configuration);
      this.setParameter("default", m_Param_2_default);
    }
  }

  _SubServlet_3 m_SubServlet_3 = this. new _SubServlet_3();
  {
    m_SubServlet_3.setParent(this);
  }
  public class _Param_4_output extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      ByteFileData __fileData = null;
      {
        __fileData = (ByteFileData)         request.getAttribute(getFileCacheAttributeName())        ;
        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        ByteBufferedBodyContent out = (ByteBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            /*** lines: 56-58 */
            __fileData.writeBytes (1468, 20, out);
            try {
              request.pushFrame();
              request.setParameter("value", request.getObjectParameter("element"));
              m_SubServlet_3.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 58);
            }
            finally {
              request.popFrame();
            }
            /*** lines: 93-96 */
            __fileData.writeBytes (2703, 23, out);
          }
          finally {
            if (_saveBaseDir != null) request.setBaseDirectory(_saveBaseDir);
          }
        } catch (Exception e) {
          // out.clear();
          out.clearBuffer();
          pageContext.handlePageException(e);
        }
      }
    }
  }
  _Param_4_output m_Param_4_output = this. new _Param_4_output();
  {
    m_Param_4_output.setParent(this);
  }
  public class _SubServlet_5 extends PageSubServlet {
    {
      this.setParameter("output", m_Param_4_output);
    }
  }

  _SubServlet_5 m_SubServlet_5 = this. new _SubServlet_5();
  {
    m_SubServlet_5.setParent(this);
  }
  public class _Param_6_0 extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      ByteFileData __fileData = null;
      {
        __fileData = (ByteFileData)         request.getAttribute(getFileCacheAttributeName())        ;
        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        ByteBufferedBodyContent out = (ByteBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            /*** lines: 140-147 */
            __fileData.writeBytes (4035, 271, out);
            {
              Object _t = DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/NSAPIConfigure.minVersion", true, PageCompileServlet.getEscapeHTMLTagConverter(), null);
              if (_t == null) {
              }
                out.print(ServletUtil.toString(_t));
            }
            /*** lines: 147-151 */
            __fileData.writeBytes (4360, 82, out);
          }
          finally {
            if (_saveBaseDir != null) request.setBaseDirectory(_saveBaseDir);
          }
        } catch (Exception e) {
          // out.clear();
          out.clearBuffer();
          pageContext.handlePageException(e);
        }
      }
    }
  }
  _Param_6_0 m_Param_6_0 = this. new _Param_6_0();
  {
    m_Param_6_0.setParent(this);
  }
  public class _Param_7_default extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      ByteFileData __fileData = null;
      {
        __fileData = (ByteFileData)         request.getAttribute(getFileCacheAttributeName())        ;
        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        ByteBufferedBodyContent out = (ByteBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 153-153 */
            __fileData.writeBytes (4482, 145, out);
            out.print(request.encodeURL ("config-cms-path.jhtml", true, true, false, true));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 153-159 */
            __fileData.writeBytes (4648, 59, out);
          }
          finally {
            if (_saveBaseDir != null) request.setBaseDirectory(_saveBaseDir);
          }
        } catch (Exception e) {
          // out.clear();
          out.clearBuffer();
          pageContext.handlePageException(e);
        }
      }
    }
  }
  _Param_7_default m_Param_7_default = this. new _Param_7_default();
  {
    m_Param_7_default.setParent(this);
  }
  public class _SubServlet_8 extends PageSubServlet {
    {
      this.setParameter("0", m_Param_6_0);
      this.setParameter("default", m_Param_7_default);
    }
  }

  _SubServlet_8 m_SubServlet_8 = this. new _SubServlet_8();
  {
    m_SubServlet_8.setParent(this);
  }
  public class _Param_9_true extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      ByteFileData __fileData = null;
      {
        __fileData = (ByteFileData)         request.getAttribute(getFileCacheAttributeName())        ;
        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        ByteBufferedBodyContent out = (ByteBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            /*** lines: 125-134 */
            __fileData.writeBytes (3385, 442, out);
            if (!DropletDescriptor.setPropertyValue(request, response, "/atg/dynamo/admin/NSAPIConfigure.minVersion", "6.0", null, null)) return;
            /*** lines: 135-138 */
            __fileData.writeBytes (3882, 16, out);
            try {
              request.pushFrame();
              request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/NSAPIConfigure.numWinVersions", true, null, null));
              m_SubServlet_8.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 138);
            }
            finally {
              request.popFrame();
            }
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 161-161 */
            __fileData.writeBytes (4732, 43, out);
            out.print(request.encodeURL ("config-iis-scriptdir.jhtml", true, true, false, true));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 161-169 */
            __fileData.writeBytes (4801, 58, out);
          }
          finally {
            if (_saveBaseDir != null) request.setBaseDirectory(_saveBaseDir);
          }
        } catch (Exception e) {
          // out.clear();
          out.clearBuffer();
          pageContext.handlePageException(e);
        }
      }
    }
  }
  _Param_9_true m_Param_9_true = this. new _Param_9_true();
  {
    m_Param_9_true.setParent(this);
  }
  public class _Param_10_false extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      ByteFileData __fileData = null;
      {
        __fileData = (ByteFileData)         request.getAttribute(getFileCacheAttributeName())        ;
        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        ByteBufferedBodyContent out = (ByteBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 171-171 */
            __fileData.writeBytes (4893, 262, out);
            out.print(request.encodeURL ("config-cms-path.jhtml", true, true, false, true));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 171-185 */
            __fileData.writeBytes (5176, 63, out);
          }
          finally {
            if (_saveBaseDir != null) request.setBaseDirectory(_saveBaseDir);
          }
        } catch (Exception e) {
          // out.clear();
          out.clearBuffer();
          pageContext.handlePageException(e);
        }
      }
    }
  }
  _Param_10_false m_Param_10_false = this. new _Param_10_false();
  {
    m_Param_10_false.setParent(this);
  }
  public class _SubServlet_11 extends PageSubServlet {
    {
      this.setParameter("true", m_Param_9_true);
      this.setParameter("false", m_Param_10_false);
    }
  }

  _SubServlet_11 m_SubServlet_11 = this. new _SubServlet_11();
  {
    m_SubServlet_11.setParent(this);
  }
  public class _Param_12_true extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      ByteFileData __fileData = null;
      {
        __fileData = (ByteFileData)         request.getAttribute(getFileCacheAttributeName())        ;
        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        ByteBufferedBodyContent out = (ByteBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 108-121 */
            __fileData.writeBytes (2976, 221, out);
            {
              Object _t = DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/Configure.thisHostname", true, PageCompileServlet.getEscapeHTMLTagConverter(), null);
              if (_t == null) {
              }
                out.print(ServletUtil.toString(_t));
            }
            /*** lines: 121-123 */
            __fileData.writeBytes (3250, 10, out);
            try {
              request.pushFrame();
              request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/NSAPIConfigure.windows", true, null, null));
              m_SubServlet_11.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 123);
            }
            finally {
              request.popFrame();
            }
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 186-186 */
            __fileData.writeBytes (5259, 164, out);
            out.print(request.encodeURL ("../../docs/", true, true, false, true));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 186-196 */
            __fileData.writeBytes (5434, 68, out);
          }
          finally {
            if (_saveBaseDir != null) request.setBaseDirectory(_saveBaseDir);
          }
        } catch (Exception e) {
          // out.clear();
          out.clearBuffer();
          pageContext.handlePageException(e);
        }
      }
    }
  }
  _Param_12_true m_Param_12_true = this. new _Param_12_true();
  {
    m_Param_12_true.setParent(this);
  }
  public class _SubServlet_13 extends PageSubServlet {
    {
      this.setParameter("true", m_Param_12_true);
    }
  }

  _SubServlet_13 m_SubServlet_13 = this. new _SubServlet_13();
  {
    m_SubServlet_13.setParent(this);
  }
  
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
    _imports.addImport("/atg/dynamo/admin/Configure");
    _imports.addImport("/atg/dynamo/admin/NSAPIConfigure");
    _imports.addImport("/atg/dynamo/droplet/ForEach");
    _imports.addImport("/atg/dynamo/droplet/Switch");
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
        /*** lines: 6-8 */
        __fileData.writeBytes (156, 2, out);

  atg.service.dynamo.NSAPIConfig NSAPIConfigure =
   (atg.service.dynamo.NSAPIConfig)
   request.resolveName("/atg/dynamo/admin/NSAPIConfigure");

  // turn on debug logging
  //NSAPIConfigure.setLoggingDebug(true);

  // reset to clear out any cached values.  this is to ensure that
  // if this is a Windows machine we get the latest values from the
  // registry.
  NSAPIConfigure.winReset();
        /*** lines: 20-20 */
        __fileData.writeBytes (568, 61, out);
        out.print(request.encodeURL ("/atg/dynamo/admin/admin.css", false, true, true, true));
        /*** lines: 20-20 */
        __fileData.writeBytes (656, 126, out);
        out.print(request.encodeURL ("images/config-banner.gif", false, true, true, true));
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 20-20 */
        __fileData.writeBytes (806, 88, out);
        out.print(request.encodeURL ("/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 20-44 */
        __fileData.writeBytes (895, 280, out);
        {
          Object _t = DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/Configure.thisHostname", true, PageCompileServlet.getEscapeHTMLTagConverter(), null);
          if (_t == null) {
          }
            out.print(ServletUtil.toString(_t));
        }
        /*** lines: 44-53 */
        __fileData.writeBytes (1228, 124, out);
        try {
          request.pushFrame();
          request.setParameter("array", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/Configure.serverNames", true, null, null));
          m_SubServlet_5.serviceByName(request, response, "/atg/dynamo/droplet/ForEach", 53);
        }
        finally {
          request.popFrame();
        }
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 97-97 */
        __fileData.writeBytes (2748, 26, out);
        out.print(request.encodeURL ("config-individual.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 97-106 */
        __fileData.writeBytes (2797, 52, out);
        try {
          request.pushFrame();
          request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/Configuration.dynamoAppServer", true, null, null));
          m_SubServlet_13.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 106);
        }
        finally {
          request.popFrame();
        }
        /*** lines: 197-201 */
        __fileData.writeBytes (5522, 18, out);
/* @version $Id: //product/DAS/version/10.0.3/release/DAS/admin/atg/dynamo/admin/en/configure-server.jhtml#2 $$Change: 651448 $ */        /*** lines: 201-201 */
        __fileData.writeBytes (5683, 1, out);
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
