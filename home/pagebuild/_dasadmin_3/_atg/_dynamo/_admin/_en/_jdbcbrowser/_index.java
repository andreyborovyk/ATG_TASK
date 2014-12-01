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

public class _index
extends atg.servlet.jsp.DynamoJspPageServlet implements atg.servlet.pagecompile.AttrCompiledServlet {
  public String getFileCacheAttributeName() {
    return "__002fatg_002fdynamo_002fadmin_002fen_002fjdbcbrowser_002findex_xjhtml";
  }
  
  public static final long SOURCE_MODIFIED_TIME = 1308316596000L;
  public class _Param_0_false extends PageSubServlet {
    
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
            /*** lines: 29-35 */
            __fileData.writeBytes (959, 137, out);
            {
              Object _t = DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/jdbcbrowser/ConnectionPoolPointer.driver", true, PageCompileServlet.getEscapeHTMLTagConverter(), null);
              if (_t == null) {
              }
                out.print(ServletUtil.toString(_t));
            }
            /*** lines: 35-37 */
            __fileData.writeBytes (1155, 40, out);
            {
              Object _t = DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/jdbcbrowser/ConnectionPoolPointer.URL", true, PageCompileServlet.getEscapeHTMLTagConverter(), null);
              if (_t == null) {
              }
                out.print(ServletUtil.toString(_t));
            }
            /*** lines: 37-39 */
            __fileData.writeBytes (1251, 47, out);
            {
              Object _t = DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/jdbcbrowser/ConnectionPoolPointer.autoCommit", true, PageCompileServlet.getEscapeHTMLTagConverter(), null);
              if (_t == null) {
              }
                out.print(ServletUtil.toString(_t));
            }
            /*** lines: 39-42 */
            __fileData.writeBytes (1361, 36, out);
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
  _Param_0_false m_Param_0_false = this. new _Param_0_false();
  {
    m_Param_0_false.setParent(this);
  }
  public class _Param_1_true extends PageSubServlet {
    
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
            /*** lines: 52-54 */
            __fileData.writeBytes (1809, 41, out);
            {
              Object _t = DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/jdbcbrowser/ConnectionPoolPointer.driver", true, PageCompileServlet.getEscapeHTMLTagConverter(), null);
              if (_t == null) {
              }
                out.print(ServletUtil.toString(_t));
            }
            /*** lines: 54-56 */
            __fileData.writeBytes (1909, 48, out);
            {
              Object _t = DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/jdbcbrowser/ConnectionPoolPointer.URL", true, PageCompileServlet.getEscapeHTMLTagConverter(), null);
              if (_t == null) {
              }
                out.print(ServletUtil.toString(_t));
            }
            /*** lines: 56-57 */
            __fileData.writeBytes (2013, 17, out);
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
  _Param_1_true m_Param_1_true = this. new _Param_1_true();
  {
    m_Param_1_true.setParent(this);
  }
  public class _SubServlet_2 extends PageSubServlet {
    {
      this.setParameter("true", m_Param_1_true);
    }
  }

  _SubServlet_2 m_SubServlet_2 = this. new _SubServlet_2();
  {
    m_SubServlet_2.setParent(this);
  }
  public class _Param_3_true extends PageSubServlet {
    
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
            /*** lines: 43-49 */
            __fileData.writeBytes (1429, 145, out);
            {
              Object _t = DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/jdbcbrowser/ConnectionPoolPointer.XADataSource", true, PageCompileServlet.getEscapeHTMLTagConverter(), null);
              if (_t == null) {
              }
                out.print(ServletUtil.toString(_t));
            }
            /*** lines: 49-50 */
            __fileData.writeBytes (1639, 15, out);
            try {
              request.pushFrame();
              request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/jdbcbrowser/ConnectionPoolPointer.isFakeXADataSource", true, null, null));
              m_SubServlet_2.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 50);
            }
            finally {
              request.popFrame();
            }
            /*** lines: 58-61 */
            __fileData.writeBytes (2054, 26, out);
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
  _Param_3_true m_Param_3_true = this. new _Param_3_true();
  {
    m_Param_3_true.setParent(this);
  }
  public class _SubServlet_4 extends PageSubServlet {
    {
      this.setParameter("false", m_Param_0_false);
      this.setParameter("true", m_Param_3_true);
    }
  }

  _SubServlet_4 m_SubServlet_4 = this. new _SubServlet_4();
  {
    m_SubServlet_4.setParent(this);
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
    _imports.addImport("/atg/dynamo/admin/jdbcbrowser/ConnectionPoolPointer");
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
        /*** lines: 3-3 */
        __fileData.writeBytes (85, 119, out);
        out.print(request.encodeURL ("images/jdbc-banner.gif", false, true, true, true));
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 3-3 */
        __fileData.writeBytes (226, 79, out);
        out.print(request.encodeURL ("/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 3-16 */
        __fileData.writeBytes (306, 135, out);
        {
          Object _t = DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/jdbcbrowser/ConnectionPoolPointer.connectionPoolName", true, PageCompileServlet.getEscapeHTMLTagConverter(), null);
          if (_t == null) {
          }
            out.print(ServletUtil.toString(_t));
        }
        /*** lines: 16-27 */
        __fileData.writeBytes (512, 305, out);
        try {
          request.pushFrame();
          request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/jdbcbrowser/ConnectionPoolPointer.isDataSource", true, null, null));
          m_SubServlet_4.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 27);
        }
        finally {
          request.popFrame();
        }
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2100, 50, out);
        out.print(request.encodeURL ("createTable.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2167, 32, out);
        out.print(request.encodeURL ("dropTable.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2214, 30, out);
        out.print(request.encodeURL ("executeQuery.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2262, 33, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=tables", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2335, 31, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=columns", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2407, 38, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=typeInfo", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2487, 34, out);
        out.print(request.encodeURL ("viewMetaData.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2539, 79, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=procedures", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2662, 30, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=procedureColumns", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2742, 36, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=schemas", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2819, 27, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=catalogs", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2888, 28, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=tableTypes", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (2960, 30, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=columnPrivileges", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (3040, 36, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=tablePrivileges", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (3125, 35, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=bestRowIdentifier", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (3211, 37, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=versionColumns", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (3296, 34, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=primaryKeys", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (3375, 31, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=importedKeys", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (3452, 32, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=exportedKeys", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (3530, 32, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=crossReference", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 62-62 */
        __fileData.writeBytes (3610, 34, out);
        out.print(request.encodeURL ("viewMetaDataTable.jhtml?operation=indexInfo", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 62-100 */
        __fileData.writeBytes (3687, 47, out);
/* @version $Id: //product/DAS/version/10.0.3/release/DAS/admin/atg/dynamo/admin/en/jdbcbrowser/index.jhtml#2 $$Change: 651448 $ */        /*** lines: 100-101 */
        __fileData.writeBytes (3878, 2, out);
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
