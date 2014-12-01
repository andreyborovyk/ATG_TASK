package _dasadmin_3._atg._dynamo._admin._en._jdbcbrowser;

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

public class _executeQuery
extends atg.servlet.jsp.DynamoJspPageServlet implements atg.servlet.pagecompile.AttrCompiledServlet {
  public String getFileCacheAttributeName() {
    return "__002fatg_002fdynamo_002fadmin_002fen_002fjdbcbrowser_002fexecuteQuery_xjhtml";
  }
  
  public static final long SOURCE_MODIFIED_TIME = 1308316596000L;
  static final atg.droplet.PropertyName _beanName0 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/jdbcbrowser/ExecuteQueryDroplet.query");
  static final atg.droplet.PropertyName _beanName1 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/jdbcbrowser/ExecuteQueryDroplet.longForm");
  static final atg.droplet.PropertyName _beanName2 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/jdbcbrowser/ExecuteQueryDroplet.operation");
  static final atg.droplet.PropertyName _beanName3 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/jdbcbrowser/ExecuteQueryDroplet.operation");
  public class _SubServlet_0 extends PageSubServlet {
  }

  _SubServlet_0 m_SubServlet_0 = this. new _SubServlet_0();
  {
    m_SubServlet_0.setParent(this);
  }
  public class _Param_1_displayResultSet extends PageSubServlet {
    
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
              request.setBaseDirectory("/atg/dynamo/admin/en/jdbcbrowser/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            /*** lines: 15-17 */
            __fileData.writeBytes (515, 28, out);
            try {
              request.pushFrame();
              m_SubServlet_0.serviceByName(request, response, "/atg/dynamo/admin/jdbcbrowser/ResultSetDroplet", 17);
            }
            finally {
              request.popFrame();
            }
            /*** lines: 18-19 */
            __fileData.writeBytes (591, 3, out);
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
  _Param_1_displayResultSet m_Param_1_displayResultSet = this. new _Param_1_displayResultSet();
  {
    m_Param_1_displayResultSet.setParent(this);
  }
  public class _Param_2_displayUpdateCount extends PageSubServlet {
    
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
              request.setBaseDirectory("/atg/dynamo/admin/en/jdbcbrowser/");
            FormTag _form = (FormTag) request.getObjectParameter(ServletUtil.FORM_NAME);
            DropletImports _imports = (DropletImports) request.getObjectParameter(ServletUtil.IMPORTS_NAME);
            /*** lines: 20-21 */
            __fileData.writeBytes (640, 21, out);
            if (!request.serviceParameter("updateCount", request, response, PageCompileServlet.getEscapeHTMLTagConverter(), null)) {
            }
            /*** lines: 21-22 */
            __fileData.writeBytes (700, 8, out);
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
  _Param_2_displayUpdateCount m_Param_2_displayUpdateCount = this. new _Param_2_displayUpdateCount();
  {
    m_Param_2_displayUpdateCount.setParent(this);
  }
  public class _SubServlet_3 extends PageSubServlet {
    {
      this.setParameter("displayResultSet", m_Param_1_displayResultSet);
      this.setParameter("displayUpdateCount", m_Param_2_displayUpdateCount);
    }
  }

  _SubServlet_3 m_SubServlet_3 = this. new _SubServlet_3();
  {
    m_SubServlet_3.setParent(this);
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
    _imports.addImport("/atg/dynamo/admin/jdbcbrowser/ExecuteQueryDroplet");
    _imports.addImport("/atg/dynamo/admin/jdbcbrowser/ResultSetDroplet");
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
        /*** lines: 4-4 */
        __fileData.writeBytes (133, 120, out);
        out.print(request.encodeURL ("images/jdbc-banner.gif", false, true, true, true));
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 4-4 */
        __fileData.writeBytes (275, 79, out);
        out.print(request.encodeURL ("/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 4-4 */
        __fileData.writeBytes (355, 21, out);
        out.print(request.encodeURL ("index.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 4-14 */
        __fileData.writeBytes (387, 57, out);
        try {
          request.pushFrame();
          m_SubServlet_3.serviceByName(request, response, "/atg/dynamo/admin/jdbcbrowser/ExecuteQueryDroplet", 14);
        }
        finally {
          request.popFrame();
        }
        _form = ((DropletEventServlet)request.getAttribute(DropletConstants.DROPLET_EVENT_ATTRIBUTE)).addForm("/atg/dynamo/admin/en/jdbcbrowser/executeQuery.jhtml.2");
        if (_form != null) {
         String actionURI = ServletUtil.getRequestURI(request, "executeQuery.jhtml        ");
        _form.addActionURL(actionURI);
        }
        request.setParameter("_form", _form);
        request.addQueryParameter("_DARGS", "/atg/dynamo/admin/en/jdbcbrowser/executeQuery.jhtml.2");
        request.addQueryParameter("_dynSessConf", Long.toString(request.getSessionConfirmationNumber()));        /*** lines: 23-23 */
        __fileData.writeBytes (728, 16, out);
        out.print(request.encodeURL ("executeQuery.jhtml", true, true, false, true, false));
        /*** lines: 23-23 */
        __fileData.writeBytes (762, 115, out);
        {
          String _pn = "/atg/dynamo/admin/jdbcbrowser/ExecuteQueryDroplet.query";
          /*** lines: 23-23 */
          __fileData.writeBytes (913, 16, out);
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 23-23 */
          __fileData.writeBytes (929, 1, out);
          out.print(DropletDescriptor.getPropertyHtmlStringValue(request, response, _pn, true, null, null));
          /*** lines: 23-23 */
          __fileData.writeBytes (930, 11, out);
          if (_form != null) _form.addTag(request, response, null, _pn, null, null, DropletConstants.PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 23-23 */
        __fileData.writeBytes (941, 26, out);
        {
          String _pn = "/atg/dynamo/admin/jdbcbrowser/ExecuteQueryDroplet.longForm";
          out.print(" value=true");
          if (DropletDescriptor.matchesPropertyValue(request, response, _beanName1, "true", true,null, null))
          if (_form != null && _form.isXMLMimeType(request))
            out.print(" checked=\"checked\"");
          else
            out.print(" checked");
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 23-23 */
          __fileData.writeBytes (1006, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "checkbox", "false", DropletConstants.PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 23-23 */
        __fileData.writeBytes (1007, 69, out);
        /*** lines: 23-23 */
        __fileData.writeBytes (1116, 27, out);
        {
          String _pn = "/atg/dynamo/admin/jdbcbrowser/ExecuteQueryDroplet.operation";
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 23-23 */
          __fileData.writeBytes (1143, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "submit", null, DropletConstants.SUBMIT_PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 23-23 */
        __fileData.writeBytes (1144, 20, out);
        /*** lines: 23-23 */
        __fileData.writeBytes (1204, 29, out);
        {
          String _pn = "/atg/dynamo/admin/jdbcbrowser/ExecuteQueryDroplet.operation";
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 23-23 */
          __fileData.writeBytes (1233, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "submit", null, DropletConstants.SUBMIT_PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 23-44 */
        __fileData.writeBytes (1234, 27, out);
/* @version $Id: //product/DAS/version/10.0.3/release/DAS/admin/atg/dynamo/admin/en/jdbcbrowser/executeQuery.jhtml#2 $$Change: 651448 $ */        /*** lines: 44-45 */
        __fileData.writeBytes (1412, 2, out);
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
