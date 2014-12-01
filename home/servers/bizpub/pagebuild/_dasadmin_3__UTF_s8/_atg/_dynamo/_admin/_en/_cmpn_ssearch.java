package _dasadmin_3__UTF_s8._atg._dynamo._admin._en;

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

public class _cmpn_ssearch
extends atg.servlet.jsp.DynamoJspPageServlet implements atg.servlet.pagecompile.AttrCompiledServlet {
  public String getFileCacheAttributeName() {
    return "__002fatg_002fdynamo_002fadmin_002fen_002fcmpn_ssearch_xjhtml";
  }
  
  public static final long SOURCE_MODIFIED_TIME = 1308307594000L;
  public class _Param_0_true extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      CharFileData __fileData = null;
      {
        __fileData = (CharFileData)
        request.getAttribute(getFileCacheAttributeName())        ;        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        CharBufferedBodyContent out = (CharBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            /*** lines: 33-36 */
            __fileData.writeChars (1017, 160, (CharFileDataSink)out);
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
  _Param_0_true m_Param_0_true = this. new _Param_0_true();
  {
    m_Param_0_true.setParent(this);
  }
  public class _Param_1_default extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      CharFileData __fileData = null;
      {
        __fileData = (CharFileData)
        request.getAttribute(getFileCacheAttributeName())        ;        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        CharBufferedBodyContent out = (CharBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            /*** lines: 37-40 */
            __fileData.writeChars (1216, 159, (CharFileDataSink)out);
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
  _Param_1_default m_Param_1_default = this. new _Param_1_default();
  {
    m_Param_1_default.setParent(this);
  }
  public class _SubServlet_2 extends PageSubServlet {
    {
      this.setParameter("true", m_Param_0_true);
      this.setParameter("default", m_Param_1_default);
    }
  }

  _SubServlet_2 m_SubServlet_2 = this. new _SubServlet_2();
  {
    m_SubServlet_2.setParent(this);
  }
  public class _Param_3_unset extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      CharFileData __fileData = null;
      {
        __fileData = (CharFileData)
        request.getAttribute(getFileCacheAttributeName())        ;        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        CharBufferedBodyContent out = (CharBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
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
  _Param_3_unset m_Param_3_unset = this. new _Param_3_unset();
  {
    m_Param_3_unset.setParent(this);
  }
  public class _Param_4_empty extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      CharFileData __fileData = null;
      {
        __fileData = (CharFileData)
        request.getAttribute(getFileCacheAttributeName())        ;        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        CharBufferedBodyContent out = (CharBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            /*** lines: 65-65 */
            __fileData.writeChars (2247, 35, (CharFileDataSink)out);
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
  _Param_4_empty m_Param_4_empty = this. new _Param_4_empty();
  {
    m_Param_4_empty.setParent(this);
  }
  public class _Param_5_output extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      CharFileData __fileData = null;
      {
        __fileData = (CharFileData)
        request.getAttribute(getFileCacheAttributeName())        ;        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        CharBufferedBodyContent out = (CharBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            /*** lines: 66-66 */
            __fileData.writeChars (2324, 25, (CharFileDataSink)out);
            out.print(ServletUtil.toString(((((Integer)request.getObjectParameter("count")).intValue() % 2) == 1) ? "even" : "odd"));
            /*** lines: 66-68 */
            __fileData.writeChars (2438, 13, (CharFileDataSink)out);
            request.setParameter("path", request.getObjectParameter("element.absoluteName"), 
null, null);
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 69-69 */
            __fileData.writeChars (2509, 16, (CharFileDataSink)out);
            out.print(request.encodeURL ("/nucleus/" + ServletUtil.toString(request.getParameter("path")), true, true, false, true));
            /*** lines: 69-69 */
            __fileData.writeChars (2564, 2, (CharFileDataSink)out);
            if (!request.serviceParameter("element.absoluteName", request, response, PageCompileServlet.getEscapeHTMLTagConverter(), null)) {
            /*** lines: 70-69 */
            __fileData.writeChars (2604, 3, (CharFileDataSink)out);
            }
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 69-70 */
            __fileData.writeChars (2617, 25, (CharFileDataSink)out);
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
  _Param_5_output m_Param_5_output = this. new _Param_5_output();
  {
    m_Param_5_output.setParent(this);
  }
  public class _SubServlet_6 extends PageSubServlet {
    {
      this.setParameter("context", "/");
      this.setParameter("recursive", "true");
      this.setParameter("sortBy", "path");
      this.setParameter("ignoreRequest", "false");
      this.setParameter("ignoreSession", "false");
      this.setParameter("empty", m_Param_4_empty);
      this.setParameter("output", m_Param_5_output);
    }
  }

  _SubServlet_6 m_SubServlet_6 = this. new _SubServlet_6();
  {
    m_SubServlet_6.setParent(this);
  }
  public class _Param_7_default extends PageSubServlet {
    
    //-------------- The service method
    public void service (DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response)
        throws ServletException, IOException
    {
      CharFileData __fileData = null;
      {
        __fileData = (CharFileData)
        request.getAttribute(getFileCacheAttributeName())        ;        DynamoJspPageContext pageContext = (DynamoJspPageContext)request.getAttribute(DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);
        
        CharBufferedBodyContent out = (CharBufferedBodyContent)pageContext.getOut();
        
        int _jspTempReturn;
        
        try {
          String _saveBaseDir = null;
          try {
            if ((_saveBaseDir = request.getBaseDirectory()) != null)
              request.setBaseDirectory("/atg/dynamo/admin/en/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            /*** lines: 53-57 */
            __fileData.writeChars (1740, 92, (CharFileDataSink)out);
            try {
              request.pushFrame();
              request.setParameter("showAll", request.getObjectParameter("showAll"));
              request.setParameter("nameExpression", request.getObjectParameter("query"));
              m_SubServlet_6.serviceByName(request, response, "/atg/dynamo/admin/ForEachNucleusComponent", 57);
            }
            finally {
              request.popFrame();
            }
            /*** lines: 71-73 */
            __fileData.writeChars (2670, 24, (CharFileDataSink)out);
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
      this.setParameter("unset", m_Param_3_unset);
      this.setParameter("default", m_Param_7_default);
    }
  }

  _SubServlet_8 m_SubServlet_8 = this. new _SubServlet_8();
  {
    m_SubServlet_8.setParent(this);
  }
  
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
    _imports.addImport("/atg/dynamo/admin/ForEachNucleusComponent");
    _imports.addImport("/atg/dynamo/droplet/Switch");
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
        __fileData.writeChars (0, 59, (CharFileDataSink)out);
        out.print(request.encodeURL ("/atg/dynamo/admin/admin.css", false, true, true, true));
        /*** lines: 1-10 */
        __fileData.writeChars (86, 132, (CharFileDataSink)out);
        /*** lines: 13-13 */
        __fileData.writeChars (328, 13, (CharFileDataSink)out);
        out.print(request.encodeURL ("images/config-banner.gif", false, true, true, true));
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 13-13 */
        __fileData.writeChars (365, 88, (CharFileDataSink)out);
        out.print(request.encodeURL ("/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        _form = ((DropletEventServlet)request.getAttribute(DropletConstants.DROPLET_EVENT_ATTRIBUTE)).addForm("/atg/dynamo/admin/en/cmpn-search.jhtml.1");
        if (_form != null) {
         String actionURI = ServletUtil.getRequestURI(request, "cmpn-search.jhtml        ");
        _form.addActionURL(actionURI);
        }
        request.setParameter("_form", _form);
        /*** lines: 13-13 */
        __fileData.writeChars (454, 253, (CharFileDataSink)out);
        out.print(request.encodeURL ("cmpn-search.jhtml", true, true, false, true));
        /*** lines: 13-13 */
        __fileData.writeChars (724, 83, (CharFileDataSink)out);
        {
          String _t = request.getParameter("query");
          if (_t != null)
            out.print(ServletUtil.escapeHtmlString(_t));
        }
        /*** lines: 13-31 */
        __fileData.writeChars (818, 100, (CharFileDataSink)out);
        try {
          request.pushFrame();
          request.setParameter("value", request.getObjectParameter("showAll"));
          m_SubServlet_2.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 31);
        }
        finally {
          request.popFrame();
        }
        /*** lines: 41-48 */
        __fileData.writeChars (1399, 191, (CharFileDataSink)out);
        if (_form != null && _form.needsEvents()){
        out.print("<input type=\"hidden\" name=\"_DARGS\" value=\"/atg/dynamo/admin/en/cmpn-search.jhtml.1\"");
        if (_form != null && _form.isXMLMimeType(request))
        out.print("/>");
        else
        out.print(">");
        out.print("<input type=\"hidden\" name=\"_dynSessConf\" value=\"" + request.getSessionConfirmationNumber() + "\"");        if (_form != null && _form.isXMLMimeType(request))
        out.print("/>");
        else
        out.print(">");
        }
        _form = null;
        /*** lines: 48-50 */
        __fileData.writeChars (1590, 13, (CharFileDataSink)out);
        try {
          request.pushFrame();
          request.setParameter("value", request.getObjectParameter("query"));
          m_SubServlet_8.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 50);
        }
        finally {
          request.popFrame();
        }
        /*** lines: 74-78 */
        __fileData.writeChars (2718, 18, (CharFileDataSink)out);
/* @version $Id: //product/DAS/version/10.0.3/release/DAS/admin/atg/dynamo/admin/en/cmpn-search.jhtml#2 $$Change: 651448 $*/        /*** lines: 78-78 */
        __fileData.writeChars (2873, 1, (CharFileDataSink)out);
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
