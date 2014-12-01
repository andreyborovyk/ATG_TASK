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

public class _admin_scontext
extends atg.servlet.jsp.DynamoJspPageServlet implements atg.servlet.pagecompile.AttrCompiledServlet {
  public String getFileCacheAttributeName() {
    return "__002fatg_002fdynamo_002fadmin_002fen_002fadmin_scontext_xjhtml";
  }
  
  public static final long SOURCE_MODIFIED_TIME = 1308307594000L;
  static final atg.droplet.PropertyName _beanName0 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/context/AdminContextFormHandler.successURL");
  static final atg.droplet.PropertyName _beanName1 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/context/AdminContextFormHandler.providerAndCurrentContexts[param:index].selectedContextId");
  static final atg.droplet.PropertyName _beanName2 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/context/AdminContextFormHandler.clearAll");
  static final atg.droplet.PropertyName _beanName3 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/context/AdminContextFormHandler.setContexts");
  public class _Param_0_unset extends PageSubServlet {
    
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
            /*** lines: 41-41 */
            __fileData.writeBytes (1475, 1, out);
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
  _Param_0_unset m_Param_0_unset = this. new _Param_0_unset();
  {
    m_Param_0_unset.setParent(this);
  }
  public class _Param_1_null extends PageSubServlet {
    
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
            /*** lines: 42-42 */
            __fileData.writeBytes (1508, 1, out);
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
  _Param_1_null m_Param_1_null = this. new _Param_1_null();
  {
    m_Param_1_null.setParent(this);
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
            if (!DropletDescriptor.setPropertyValue(request, response, "/atg/dynamo/admin/context/AdminContextFormHandler.successURL", request.getObjectParameter("returnPage"), null, null)) return;
            /*** lines: 44-43 */
            __fileData.writeBytes (1621, 1, out);
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
      this.setParameter("unset", m_Param_0_unset);
      this.setParameter("null", m_Param_1_null);
      this.setParameter("default", m_Param_2_default);
    }
  }

  _SubServlet_3 m_SubServlet_3 = this. new _SubServlet_3();
  {
    m_SubServlet_3.setParent(this);
  }
  public class _Param_4_empty extends PageSubServlet {
    
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
            /*** lines: 51-51 */
            __fileData.writeBytes (1907, 40, out);
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
            /*** lines: 59-59 */
            __fileData.writeBytes (2380, 26, out);
            {
              String _t = request.getParameter("element.id");
              if (_t != null)
                out.print(ServletUtil.escapeHtmlString(_t));
            }
            /*** lines: 59-59 */
            __fileData.writeBytes (2422, 1, out);
            {
              Object _sv = request.getObjectParameter("_selectPropertyValue");
              if (ServletUtil.valuesMatch(_sv, request.getParameter("element.id")))
            if (_form != null && _form.isXMLMimeType(request))
              out.print(" selected=\"selected\"");
            else
                out.print(" selected");
            }
            /*** lines: 59-60 */
            __fileData.writeBytes (2423, 1, out);
            if (!request.serviceParameter("element.displayName", request, response, PageCompileServlet.getEscapeHTMLTagConverter(), null)) {
            /*** lines: 61-60 */
            __fileData.writeBytes (2461, 3, out);
            }
            /*** lines: 60-61 */
            __fileData.writeBytes (2474, 9, out);
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
      this.setParameter("output", m_Param_5_output);
    }
  }

  _SubServlet_6 m_SubServlet_6 = this. new _SubServlet_6();
  {
    m_SubServlet_6.setParent(this);
  }
  public class _Param_7_output extends PageSubServlet {
    
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
            /*** lines: 53-55 */
            __fileData.writeBytes (1984, 22, out);
            if (!request.serviceParameter("element.provider.displayName", request, response, PageCompileServlet.getEscapeHTMLTagConverter(), null)) {
            /*** lines: 56-55 */
            __fileData.writeBytes (2052, 3, out);
            }
            /*** lines: 55-55 */
            __fileData.writeBytes (2065, 17, out);
            {
              String _pn = DropletDescriptor.evalDynamicDimensions(_beanName1, request);
              out.print(" name=\"");
              out.print(_pn);
              out.print('"');
              /*** lines: 55-55 */
              __fileData.writeBytes (2170, 1, out);
              request.setParameter("_selectPropertyValue", DropletDescriptor.getPropertyValue(request, response, _beanName1, true, null, null));
              /*** lines: 55-55 */
              __fileData.writeBytes (2171, 27, out);
              {
                Object _sv = request.getObjectParameter("_selectPropertyValue");
                if (ServletUtil.valuesMatch(_sv, "null"))
              if (_form != null && _form.isXMLMimeType(request))
                out.print(" selected=\"selected\"");
              else
                  out.print(" selected");
              }
              /*** lines: 55-57 */
              __fileData.writeBytes (2198, 12, out);
              try {
                request.pushFrame();
                request.setParameter("array", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/context/AdminContextFormHandler.providerAndCurrentContexts[param:index].options", true, null, null));
                m_SubServlet_6.serviceByName(request, response, "/atg/dynamo/droplet/ForEach", 57);
              }
              finally {
                request.popFrame();
              }
              request.removeParameter("_selectPropertyValue");
              /*** lines: 62-62 */
              __fileData.writeBytes (2509, 16, out);
              if (_form != null) _form.addTag(request, response, null, _pn, null, null, DropletConstants.PRIORITY_DEFAULT, null, null, null, false);
            }
            /*** lines: 62-65 */
            __fileData.writeBytes (2525, 22, out);
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
  _Param_7_output m_Param_7_output = this. new _Param_7_output();
  {
    m_Param_7_output.setParent(this);
  }
  public class _SubServlet_8 extends PageSubServlet {
    {
      this.setParameter("empty", m_Param_4_empty);
      this.setParameter("output", m_Param_7_output);
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
  
  public final static String _JSP_ENCODING =   null  ;
  
  public static String _jspGetEncoding() {
    return _JSP_ENCODING;
  }
  

  //-------------------------------
  {
  
    DropletImports _imports = new DropletImports();
    this. setParameter("_imports", _imports);
    _imports.addImport("/atg/dynamo/admin/context/AdminContextFormHandler");
    _imports.addImport("/atg/dynamo/admin/ForEachNucleusComponent");
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
        /*** lines: 1-1 */
        __fileData.writeBytes (0, 59, out);
        out.print(request.encodeURL ("/atg/dynamo/admin/admin.css", false, true, true, true));
        /*** lines: 1-10 */
        __fileData.writeBytes (86, 131, out);
        /*** lines: 15-15 */
        __fileData.writeBytes (421, 13, out);
        out.print(request.encodeURL ("images/config-banner.gif", false, true, true, true));
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 15-15 */
        __fileData.writeBytes (458, 88, out);
        out.print(request.encodeURL ("/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 15-39 */
        __fileData.writeBytes (547, 832, out);
        try {
          request.pushFrame();
          request.setParameter("value", request.getObjectParameter("returnPage"));
          m_SubServlet_3.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 39);
        }
        finally {
          request.popFrame();
        }
        _form = ((DropletEventServlet)request.getAttribute(DropletConstants.DROPLET_EVENT_ATTRIBUTE)).addForm("/atg/dynamo/admin/en/admin-context.jhtml.1");
        if (_form != null) {
         String actionURI = ServletUtil.getRequestURI(request, "admin-context.jhtml        ");
        _form.addActionURL(actionURI);
        }
        request.setParameter("_form", _form);
        /*** lines: 44-44 */
        __fileData.writeBytes (1642, 16, out);
        out.print(request.encodeURL ("admin-context.jhtml", true, true, false, true));
        /*** lines: 44-44 */
        __fileData.writeBytes (1677, 26, out);
        {
          String _pn = "/atg/dynamo/admin/context/AdminContextFormHandler.successURL";
          out.print(" value=\"");
          out.print(DropletDescriptor.getPropertyHtmlStringValue(request, response, _beanName0, true, null, null));
          out.print('"');
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 44-44 */
          __fileData.writeBytes (1744, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "hidden", null, DropletConstants.PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 44-49 */
        __fileData.writeBytes (1745, 22, out);
        try {
          request.pushFrame();
          request.setParameter("array", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/context/AdminContextFormHandler.providerAndCurrentContexts", true, null, null));
          m_SubServlet_8.serviceByName(request, response, "/atg/dynamo/droplet/ForEach", 49);
        }
        finally {
          request.popFrame();
        }
        /*** lines: 66-66 */
        __fileData.writeBytes (2569, 35, out);
        /*** lines: 66-66 */
        __fileData.writeBytes (2643, 14, out);
        {
          String _pn = "/atg/dynamo/admin/context/AdminContextFormHandler.clearAll";
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 66-66 */
          __fileData.writeBytes (2657, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "submit", null, DropletConstants.SUBMIT_PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 66-66 */
        __fileData.writeBytes (2658, 24, out);
        /*** lines: 66-66 */
        __fileData.writeBytes (2724, 12, out);
        {
          String _pn = "/atg/dynamo/admin/context/AdminContextFormHandler.setContexts";
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 66-66 */
          __fileData.writeBytes (2736, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "submit", null, DropletConstants.SUBMIT_PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 66-70 */
        __fileData.writeBytes (2737, 1, out);
        if (_form != null && _form.needsEvents()){
        out.print("<input type=\"hidden\" name=\"_DARGS\" value=\"/atg/dynamo/admin/en/admin-context.jhtml.1\"");
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
        /*** lines: 70-71 */
        __fileData.writeBytes (2738, 8, out);
/* @version $Id: //product/DAS/version/10.0.3/release/DAS/admin/atg/dynamo/admin/en/admin-context.jhtml#2 $$Change: 651448 $*/        /*** lines: 71-71 */
        __fileData.writeBytes (2885, 1, out);
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
