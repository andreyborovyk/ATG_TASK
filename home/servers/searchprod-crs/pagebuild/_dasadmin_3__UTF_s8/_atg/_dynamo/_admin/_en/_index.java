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

public class _index
extends atg.servlet.jsp.DynamoJspPageServlet implements atg.servlet.pagecompile.AttrCompiledServlet {
  public String getFileCacheAttributeName() {
    return "__002fatg_002fdynamo_002fadmin_002fen_002findex_xjhtml";
  }
  
  public static final long SOURCE_MODIFIED_TIME = 1308307596000L;
  static final atg.droplet.PropertyName _beanName0 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/LoadControl.acceptingNewSessions");
  static final atg.droplet.PropertyName _beanName1 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/admin/LoadControl.acceptingNewSessions");
  public class _Param_0_unset extends PageSubServlet {
    
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
  _Param_0_unset m_Param_0_unset = this. new _Param_0_unset();
  {
    m_Param_0_unset.setParent(this);
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
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 60-60 */
            __fileData.writeChars (1428, 24, (CharFileDataSink)out);
            out.print(request.encodeURL ("/atg/dynamo/admin/en/configure-server.jhtml", true, true, false, true));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 60-63 */
            __fileData.writeChars (1495, 124, (CharFileDataSink)out);
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
  public class _Param_2_true extends PageSubServlet {
    
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
            /*** lines: 64-68 */
            __fileData.writeChars (1656, 190, (CharFileDataSink)out);
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
  _Param_2_true m_Param_2_true = this. new _Param_2_true();
  {
    m_Param_2_true.setParent(this);
  }
  public class _SubServlet_3 extends PageSubServlet {
    {
      this.setParameter("unset", m_Param_0_unset);
      this.setParameter("default", m_Param_1_default);
      this.setParameter("true", m_Param_2_true);
    }
  }

  _SubServlet_3 m_SubServlet_3 = this. new _SubServlet_3();
  {
    m_SubServlet_3.setParent(this);
  }
  public class _Param_4_unset extends PageSubServlet {
    
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
  _Param_4_unset m_Param_4_unset = this. new _Param_4_unset();
  {
    m_Param_4_unset.setParent(this);
  }
  public class _Param_5_default extends PageSubServlet {
    
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
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 80-80 */
            __fileData.writeChars (2240, 23, (CharFileDataSink)out);
            out.print(request.encodeURL ("/atg/dynamo/admin/en/start-acc.jhtml", true, true, false, true));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 80-83 */
            __fileData.writeChars (2299, 99, (CharFileDataSink)out);
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
  _Param_5_default m_Param_5_default = this. new _Param_5_default();
  {
    m_Param_5_default.setParent(this);
  }
  public class _Param_6_true extends PageSubServlet {
    
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
            /*** lines: 84-88 */
            __fileData.writeChars (2435, 166, (CharFileDataSink)out);
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
  _Param_6_true m_Param_6_true = this. new _Param_6_true();
  {
    m_Param_6_true.setParent(this);
  }
  public class _SubServlet_7 extends PageSubServlet {
    {
      this.setParameter("unset", m_Param_4_unset);
      this.setParameter("default", m_Param_5_default);
      this.setParameter("true", m_Param_6_true);
    }
  }

  _SubServlet_7 m_SubServlet_7 = this. new _SubServlet_7();
  {
    m_SubServlet_7.setParent(this);
  }
  public class _Param_8_true extends PageSubServlet {
    
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
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 106-106 */
            __fileData.writeChars (3244, 78, (CharFileDataSink)out);
            out.print(request.encodeURL ("/nucleus/atg/dynamo/service/j2ee/J2EEContainer", true, true, false, true));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 106-106 */
            __fileData.writeChars (3368, 169, (CharFileDataSink)out);
            out.print(request.encodeURL ("/atg/dynamo/admin/en/performance.jhtml", true, true, false, true));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 106-118 */
            __fileData.writeChars (3575, 129, (CharFileDataSink)out);
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
  _Param_8_true m_Param_8_true = this. new _Param_8_true();
  {
    m_Param_8_true.setParent(this);
  }
  public class _Param_9_false extends PageSubServlet {
    
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
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 119-119 */
            __fileData.writeChars (3739, 81, (CharFileDataSink)out);
            out.print(request.encodeURL ("/atg/dynamo/admin/en/performance-monitor.jhtml", true, true, false, true));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 119-127 */
            __fileData.writeChars (3866, 101, (CharFileDataSink)out);
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
  _Param_9_false m_Param_9_false = this. new _Param_9_false();
  {
    m_Param_9_false.setParent(this);
  }
  public class _SubServlet_10 extends PageSubServlet {
    {
      this.setParameter("true", m_Param_8_true);
      this.setParameter("false", m_Param_9_false);
    }
  }

  _SubServlet_10 m_SubServlet_10 = this. new _SubServlet_10();
  {
    m_SubServlet_10.setParent(this);
  }
  public class _Param_11_true extends PageSubServlet {
    
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
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 149-149 */
            __fileData.writeChars (4808, 77, (CharFileDataSink)out);
            out.print(request.encodeURL ("/atg/dynamo/admin/en/show-dynamo-log.jhtml", true, true, false, true));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 149-157 */
            __fileData.writeChars (4927, 90, (CharFileDataSink)out);
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
  _Param_11_true m_Param_11_true = this. new _Param_11_true();
  {
    m_Param_11_true.setParent(this);
  }
  public class _SubServlet_12 extends PageSubServlet {
    {
      this.setParameter("true", m_Param_11_true);
    }
  }

  _SubServlet_12 m_SubServlet_12 = this. new _SubServlet_12();
  {
    m_SubServlet_12.setParent(this);
  }
  public class _Param_13_unset extends PageSubServlet {
    
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
  _Param_13_unset m_Param_13_unset = this. new _Param_13_unset();
  {
    m_Param_13_unset.setParent(this);
  }
  public class _SubServlet_14 extends PageSubServlet {
  }

  _SubServlet_14 m_SubServlet_14 = this. new _SubServlet_14();
  {
    m_SubServlet_14.setParent(this);
  }
  public class _Param_15_default extends PageSubServlet {
    
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
            /*** lines: 168-169 */
            __fileData.writeChars (5490, 11, (CharFileDataSink)out);

          request.setParameter("adminHomePageURL",
            request.getParameter("element.adminHomePagePrefixURL") + 
            request.getParameter("adminLang") + 
            request.getParameter("element.adminHomePageFragmentURL") );
                      /*** lines: 174-175 */
            __fileData.writeChars (5767, 11, (CharFileDataSink)out);
            try {
              request.pushFrame();
              request.pushDefaultParameters(m_SubServlet_14.getParameters());
              ServletUtil.embed(request.getParameter("adminHomePageURL"), request, response);
            }
            finally {
              request.popFrame();
            }
            /*** lines: 175-176 */
            __fileData.writeChars (5826, 9, (CharFileDataSink)out);
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
  _Param_15_default m_Param_15_default = this. new _Param_15_default();
  {
    m_Param_15_default.setParent(this);
  }
  public class _SubServlet_16 extends PageSubServlet {
    {
      this.setParameter("unset", m_Param_13_unset);
      this.setParameter("default", m_Param_15_default);
    }
  }

  _SubServlet_16 m_SubServlet_16 = this. new _SubServlet_16();
  {
    m_SubServlet_16.setParent(this);
  }
  public class _Param_17_output extends PageSubServlet {
    
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
            /*** lines: 164-165 */
            __fileData.writeChars (5293, 7, (CharFileDataSink)out);
            try {
              request.pushFrame();
              request.setParameter("value", request.getObjectParameter("element.adminHomePageFragmentURL"));
              m_SubServlet_16.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 165);
            }
            finally {
              request.popFrame();
            }
            /*** lines: 177-178 */
            __fileData.writeChars (5861, 5, (CharFileDataSink)out);
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
  _Param_17_output m_Param_17_output = this. new _Param_17_output();
  {
    m_Param_17_output.setParent(this);
  }
  public class _SubServlet_18 extends PageSubServlet {
    {
      this.setParameter("output", m_Param_17_output);
    }
  }

  _SubServlet_18 m_SubServlet_18 = this. new _SubServlet_18();
  {
    m_SubServlet_18.setParent(this);
  }
  public class _Param_19_false extends PageSubServlet {
    
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
            request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
            /*** lines: 202-202 */
            __fileData.writeChars (6799, 156, (CharFileDataSink)out);
            out.print(request.encodeURL ("/atg/dynamo/admin/en/demolink.jhtml", true, true, false, true));
            request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
            /*** lines: 202-211 */
            __fileData.writeChars (6990, 137, (CharFileDataSink)out);
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
  _Param_19_false m_Param_19_false = this. new _Param_19_false();
  {
    m_Param_19_false.setParent(this);
  }
  public class _SubServlet_20 extends PageSubServlet {
    {
      this.setParameter("false", m_Param_19_false);
    }
  }

  _SubServlet_20 m_SubServlet_20 = this. new _SubServlet_20();
  {
    m_SubServlet_20.setParent(this);
  }
  public class _Param_21_true extends PageSubServlet {
    
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
            /*** lines: 232-232 */
            __fileData.writeChars (7845, 28, (CharFileDataSink)out);
            /*** lines: 232-232 */
            __fileData.writeChars (7934, 9, (CharFileDataSink)out);
            /*** lines: 232-232 */
            __fileData.writeChars (7962, 36, (CharFileDataSink)out);
            {
              String _pn = "/atg/dynamo/admin/LoadControl.acceptingNewSessions";
              out.print(" name=\"");
              out.print(_pn);
              out.print('"');
              /*** lines: 232-232 */
              __fileData.writeChars (7998, 1, (CharFileDataSink)out);
              if (_form != null) _form.addTag(request, response, null, _pn, "submit", null, DropletConstants.SUBMIT_PRIORITY_DEFAULT,"false",null, null, false);
            }
            /*** lines: 232-235 */
            __fileData.writeChars (7999, 7, (CharFileDataSink)out);
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
  _Param_21_true m_Param_21_true = this. new _Param_21_true();
  {
    m_Param_21_true.setParent(this);
  }
  public class _Param_22_false extends PageSubServlet {
    
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
            /*** lines: 236-236 */
            __fileData.writeChars (8043, 28, (CharFileDataSink)out);
            /*** lines: 236-236 */
            __fileData.writeChars (8132, 1, (CharFileDataSink)out);
            /*** lines: 236-236 */
            __fileData.writeChars (8151, 37, (CharFileDataSink)out);
            {
              String _pn = "/atg/dynamo/admin/LoadControl.acceptingNewSessions";
              out.print(" name=\"");
              out.print(_pn);
              out.print('"');
              /*** lines: 236-236 */
              __fileData.writeChars (8188, 1, (CharFileDataSink)out);
              if (_form != null) _form.addTag(request, response, null, _pn, "submit", null, DropletConstants.SUBMIT_PRIORITY_DEFAULT,"true",null, null, false);
            }
            /*** lines: 236-238 */
            __fileData.writeChars (8189, 7, (CharFileDataSink)out);
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
  _Param_22_false m_Param_22_false = this. new _Param_22_false();
  {
    m_Param_22_false.setParent(this);
  }
  public class _SubServlet_23 extends PageSubServlet {
    {
      this.setParameter("true", m_Param_21_true);
      this.setParameter("false", m_Param_22_false);
    }
  }

  _SubServlet_23 m_SubServlet_23 = this. new _SubServlet_23();
  {
    m_SubServlet_23.setParent(this);
  }
  public class _Param_24_true extends PageSubServlet {
    
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
            /*** lines: 229-230 */
            __fileData.writeChars (7675, 5, (CharFileDataSink)out);
            try {
              request.pushFrame();
              request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/admin/LoadControl.acceptingNewSessions", true, null, null));
              m_SubServlet_23.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 230);
            }
            finally {
              request.popFrame();
            }
            /*** lines: 239-240 */
            __fileData.writeChars (8220, 3, (CharFileDataSink)out);
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
  _Param_24_true m_Param_24_true = this. new _Param_24_true();
  {
    m_Param_24_true.setParent(this);
  }
  public class _SubServlet_25 extends PageSubServlet {
    {
      this.setParameter("true", m_Param_24_true);
    }
  }

  _SubServlet_25 m_SubServlet_25 = this. new _SubServlet_25();
  {
    m_SubServlet_25.setParent(this);
  }
  public class _Param_26_true extends PageSubServlet {
    
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
            /*** lines: 249-251 */
            __fileData.writeChars (8586, 108, (CharFileDataSink)out);
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
  _Param_26_true m_Param_26_true = this. new _Param_26_true();
  {
    m_Param_26_true.setParent(this);
  }
  public class _SubServlet_27 extends PageSubServlet {
    {
      this.setParameter("true", m_Param_26_true);
    }
  }

  _SubServlet_27 m_SubServlet_27 = this. new _SubServlet_27();
  {
    m_SubServlet_27.setParent(this);
  }
  public class _Param_28_true extends PageSubServlet {
    
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
            /*** lines: 218-218 */
            __fileData.writeChars (7315, 51, (CharFileDataSink)out);
            out.print(request.encodeURL ("/atg/dynamo/admin/images/bluedot.gif", false, true, true, true));
            _form = ((DropletEventServlet)request.getAttribute(DropletConstants.DROPLET_EVENT_ATTRIBUTE)).addForm("/atg/dynamo/admin/en/index.jhtml.17");
            if (_form != null) {
             String actionURI = ServletUtil.getRequestURI(request, "/atg/dynamo/admin/en/index.jhtml            ");
            _form.addActionURL(actionURI);
            }
            request.setParameter("_form", _form);
            /*** lines: 218-227 */
            __fileData.writeChars (7402, 134, (CharFileDataSink)out);
            try {
              request.pushFrame();
              request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/Configuration.drpEnabled", true, null, null));
              m_SubServlet_25.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 227);
            }
            finally {
              request.popFrame();
            }
            /*** lines: 241-247 */
            __fileData.writeChars (8249, 199, (CharFileDataSink)out);
            try {
              request.pushFrame();
              request.setParameter("value", ServletUtil.toString(request.getNucleus().getShellRestart()));
              m_SubServlet_27.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 247);
            }
            finally {
              request.popFrame();
            }
            /*** lines: 252-255 */
            __fileData.writeChars (8720, 22, (CharFileDataSink)out);
            if (_form != null && _form.needsEvents()){
            out.print("<input type=\"hidden\" name=\"_DARGS\" value=\"/atg/dynamo/admin/en/index.jhtml.17\"");
            if (_form != null && _form.isXMLMimeType(request))
            out.print("/>");
            else
            out.print(">");
            out.print("<input type=\"hidden\" name=\"_dynSessConf\" value=\"" + request.getSessionConfirmationNumber() + "\"");            if (_form != null && _form.isXMLMimeType(request))
            out.print("/>");
            else
            out.print(">");
            }
            _form = null;
            /*** lines: 255-257 */
            __fileData.writeChars (8742, 16, (CharFileDataSink)out);
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
  _Param_28_true m_Param_28_true = this. new _Param_28_true();
  {
    m_Param_28_true.setParent(this);
  }
  public class _SubServlet_29 extends PageSubServlet {
    {
      this.setParameter("true", m_Param_28_true);
    }
  }

  _SubServlet_29 m_SubServlet_29 = this. new _SubServlet_29();
  {
    m_SubServlet_29.setParent(this);
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
    _imports.addImport("/atg/dynamo/Configuration");
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
        __fileData.writeChars (585, 2, (CharFileDataSink)out);
        /*** lines: 29-29 */
        __fileData.writeChars (688, 100, (CharFileDataSink)out);
        out.print(request.encodeURL ("/atg/dynamo/admin/admin.css", false, true, true, true));
        /*** lines: 29-29 */
        __fileData.writeChars (815, 93, (CharFileDataSink)out);
        out.print(request.encodeURL ("images/admin-banner.gif", false, true, true, true));
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 29-29 */
        __fileData.writeChars (931, 89, (CharFileDataSink)out);
        out.print(request.encodeURL ("/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 29-48 */
        __fileData.writeChars (1021, 80, (CharFileDataSink)out);
        {
          Object _t = DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/Configuration.version", true, PageCompileServlet.getEscapeHTMLTagConverter(), null);
          if (_t == null) {
            /*** lines: 49-48 */
            __fileData.writeChars (1151, 1, (CharFileDataSink)out);
          }
            out.print(ServletUtil.toString(_t));
        }
        /*** lines: 48-57 */
        __fileData.writeChars (1162, 92, (CharFileDataSink)out);
        try {
          request.pushFrame();
          request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/Configuration.standaloneAssembledEarFile", true, null, null));
          m_SubServlet_3.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 57);
        }
        finally {
          request.popFrame();
        }
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 69-69 */
        __fileData.writeChars (1871, 48, (CharFileDataSink)out);
        out.print(request.encodeURL ("/nucleus/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 69-77 */
        __fileData.writeChars (1928, 138, (CharFileDataSink)out);
        try {
          request.pushFrame();
          request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/Configuration.standaloneAssembledEarFile", true, null, null));
          m_SubServlet_7.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 77);
        }
        finally {
          request.popFrame();
        }
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 89-89 */
        __fileData.writeChars (2626, 48, (CharFileDataSink)out);
        out.print(request.encodeURL ("/atg/dynamo/admin/en/change-password.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 89-89 */
        __fileData.writeChars (2716, 147, (CharFileDataSink)out);
        out.print(request.encodeURL ("/atg/dynamo/admin/en/jdbcbrowser/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 89-104 */
        __fileData.writeChars (2896, 200, (CharFileDataSink)out);
        try {
          request.pushFrame();
          request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/Configuration.dynamoAppServer", true, null, null));
          m_SubServlet_10.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 104);
        }
        finally {
          request.popFrame();
        }
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 128-128 */
        __fileData.writeChars (3989, 41, (CharFileDataSink)out);
        out.print(request.encodeURL ("/atg/dynamo/admin/en/webservice/index.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 128-128 */
        __fileData.writeChars (4073, 212, (CharFileDataSink)out);
        out.print(request.encodeURL ("/atg/dynamo/admin/en/batchcompiler.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 128-128 */
        __fileData.writeChars (4325, 167, (CharFileDataSink)out);
        out.print(request.encodeURL ("/atg/dynamo/admin/en/conf-reporter.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 128-147 */
        __fileData.writeChars (4532, 128, (CharFileDataSink)out);
        try {
          request.pushFrame();
          request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/Configuration.dynamoAppServer", true, null, null));
          m_SubServlet_12.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 147);
        }
        finally {
          request.popFrame();
        }
        /*** lines: 158-160 */
        __fileData.writeChars (5039, 5, (CharFileDataSink)out);
        try {
          request.pushFrame();
          request.setParameter("array", DropletDescriptor.getPropertyValue(request, response, "/atg/modules/ModuleManager.modules", true, null, null));
          m_SubServlet_18.serviceByName(request, response, "/atg/dynamo/droplet/ForEach", 160);
        }
        finally {
          request.popFrame();
        }
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 179-179 */
        __fileData.writeChars (5888, 74, (CharFileDataSink)out);
        out.print(request.encodeURL ("/atg/dynamo/admin/en/sitemap/sitemap-admin.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 179-179 */
        __fileData.writeChars (6010, 276, (CharFileDataSink)out);
        out.print(request.encodeURL ("/atg/dynamo/docs/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 179-179 */
        __fileData.writeChars (6303, 144, (CharFileDataSink)out);
        out.print(request.encodeURL ("/atg/dynamo/admin/en/running-products.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        /*** lines: 179-200 */
        __fileData.writeChars (6490, 146, (CharFileDataSink)out);
        try {
          request.pushFrame();
          request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/Configuration.dynamoAppServerStandAloneMode", true, null, null));
          m_SubServlet_20.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 200);
        }
        finally {
          request.popFrame();
        }
        /*** lines: 212-216 */
        __fileData.writeChars (7149, 18, (CharFileDataSink)out);
        try {
          request.pushFrame();
          request.setParameter("value", DropletDescriptor.getPropertyValue(request, response, "/atg/dynamo/Configuration.dynamoAppServer", true, null, null));
          m_SubServlet_29.serviceByName(request, response, "/atg/dynamo/droplet/Switch", 216);
        }
        finally {
          request.popFrame();
        }
        /*** lines: 258-265 */
        __fileData.writeChars (8780, 47, (CharFileDataSink)out);
/* @version $Id: //product/DAS/version/10.0.3/release/DAS/admin/atg/dynamo/admin/en/index.jhtml#2 $$Change: 651448 $*/        /*** lines: 265-265 */
        __fileData.writeChars (8958, 1, (CharFileDataSink)out);
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
