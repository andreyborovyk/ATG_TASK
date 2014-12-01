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

public class _config_sreporter_sbean_srepresentation2
extends atg.servlet.jsp.DynamoJspPageServlet implements atg.servlet.pagecompile.AttrCompiledServlet {
  public String getFileCacheAttributeName() {
    return "__002fatg_002fdynamo_002fadmin_002fen_002fconfig_sreporter_sbean_srepresentation2_xjhtml";
  }
  
  public static final long SOURCE_MODIFIED_TIME = 1308307594000L;
  static final atg.droplet.PropertyName _beanName0 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/service/ConfigurationReporter.saveJVMPropertiesInBeanSerialization");
  static final atg.droplet.PropertyName _beanName1 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/service/ConfigurationReporter.saveConfigurationPathInBeanSerialization");
  static final atg.droplet.PropertyName _beanName2 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/service/ConfigurationReporter.componentFileName");
  static final atg.droplet.PropertyName _beanName3 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/service/ConfigurationReporter.serializedFileName");
  static final atg.droplet.PropertyName _beanName4 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/service/ConfigurationReporter.saveBeanRepresentation");
  static final atg.droplet.PropertyName _beanName5 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/service/ConfigurationReporter.outputComponentPropertiesInXML");
  static final atg.droplet.PropertyName _beanName6 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/service/ConfigurationReporter.serializedFileName");
  static final atg.droplet.PropertyName _beanName7 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/service/ConfigurationReporter.beanRepresentationXMLFileName");
  static final atg.droplet.PropertyName _beanName8 = atg.droplet.PropertyName.createPropertyName("/atg/dynamo/service/ConfigurationReporter.outputRepresentationToXML");
  
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
        __fileData.writeBytes (76, 63, out);
        out.print(request.encodeURL ("/atg/dynamo/admin/admin.css", false, true, true, true));
        /*** lines: 3-3 */
        __fileData.writeBytes (166, 145, out);
        out.print(request.encodeURL ("images/admin-banner.gif", false, true, true, true));
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 3-3 */
        __fileData.writeBytes (334, 95, out);
        out.print(request.encodeURL ("/", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 3-3 */
        __fileData.writeBytes (430, 21, out);
        out.print(request.encodeURL ("conf-reporter.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        request.setParameterDelimiter(ServletUtil.getParamDelimiter(request));
        /*** lines: 3-3 */
        __fileData.writeBytes (470, 342, out);
        out.print(request.encodeURL ("config-reporter-output-hierarchy-titled.jhtml", true, true, false, true));
        request.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        _form = ((DropletEventServlet)request.getAttribute(DropletConstants.DROPLET_EVENT_ATTRIBUTE)).addForm("/atg/dynamo/admin/en/config-reporter-bean-representation2.jhtml.3");
        if (_form != null) {
         String actionURI = ServletUtil.getRequestURI(request, "config-reporter-bean-representation2.jhtml        ");
        _form.addActionURL(actionURI);
        }
        request.setParameter("_form", _form);
        request.addQueryParameter("_DARGS", "/atg/dynamo/admin/en/config-reporter-bean-representation2.jhtml.3");
        request.addQueryParameter("_dynSessConf", Long.toString(request.getSessionConfirmationNumber()));        /*** lines: 3-3 */
        __fileData.writeBytes (857, 322, out);
        out.print(request.encodeURL ("config-reporter-bean-representation2.jhtml", true, true, false, true, false));
        /*** lines: 3-3 */
        __fileData.writeBytes (1221, 40, out);
        {
          String _pn = "/atg/dynamo/service/ConfigurationReporter.saveJVMPropertiesInBeanSerialization";
          out.print(" value=true");
          if (DropletDescriptor.matchesPropertyValue(request, response, _beanName0, "true", true,null, null))
          if (_form != null && _form.isXMLMimeType(request))
            out.print(" checked=\"checked\"");
          else
            out.print(" checked");
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 3-3 */
          __fileData.writeBytes (1330, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "checkbox", "false", DropletConstants.PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 3-3 */
        __fileData.writeBytes (1331, 97, out);
        {
          String _pn = "/atg/dynamo/service/ConfigurationReporter.saveConfigurationPathInBeanSerialization";
          out.print(" value=true");
          if (DropletDescriptor.matchesPropertyValue(request, response, _beanName1, "true", true,null, null))
          if (_form != null && _form.isXMLMimeType(request))
            out.print(" checked=\"checked\"");
          else
            out.print(" checked");
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 3-3 */
          __fileData.writeBytes (1501, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "checkbox", "false", DropletConstants.PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 3-3 */
        __fileData.writeBytes (1502, 105, out);
        /*** lines: 3-3 */
        __fileData.writeBytes (1657, 8, out);
        {
          String _pn = "/atg/dynamo/service/ConfigurationReporter.componentFileName";
          out.print(" value=\"");
          out.print(DropletDescriptor.getPropertyHtmlStringValue(request, response, _beanName2, true, null, null));
          out.print('"');
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 3-3 */
          __fileData.writeBytes (1665, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "text", null, DropletConstants.PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 3-3 */
        __fileData.writeBytes (1666, 54, out);
        /*** lines: 3-3 */
        __fileData.writeBytes (1771, 8, out);
        {
          String _pn = "/atg/dynamo/service/ConfigurationReporter.serializedFileName";
          out.print(" value=\"");
          out.print(DropletDescriptor.getPropertyHtmlStringValue(request, response, _beanName3, true, null, null));
          out.print('"');
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 3-3 */
          __fileData.writeBytes (1779, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "text", null, DropletConstants.PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 3-3 */
        __fileData.writeBytes (1780, 33, out);
        /*** lines: 3-3 */
        __fileData.writeBytes (1868, 41, out);
        {
          String _pn = "/atg/dynamo/service/ConfigurationReporter.saveBeanRepresentation";
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 3-3 */
          __fileData.writeBytes (1909, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "submit", null, DropletConstants.SUBMIT_PRIORITY_DEFAULT, null, null, null, false);
        }
        _form = ((DropletEventServlet)request.getAttribute(DropletConstants.DROPLET_EVENT_ATTRIBUTE)).addForm("/atg/dynamo/admin/en/config-reporter-bean-representation2.jhtml.4");
        if (_form != null) {
         String actionURI = ServletUtil.getRequestURI(request, "config-reporter-bean-representation2.jhtml        ");
        _form.addActionURL(actionURI);
        }
        request.setParameter("_form", _form);
        request.addQueryParameter("_DARGS", "/atg/dynamo/admin/en/config-reporter-bean-representation2.jhtml.4");
        request.addQueryParameter("_dynSessConf", Long.toString(request.getSessionConfirmationNumber()));        /*** lines: 3-3 */
        __fileData.writeBytes (1910, 223, out);
        out.print(request.encodeURL ("config-reporter-bean-representation2.jhtml", true, true, false, true, false));
        /*** lines: 3-3 */
        __fileData.writeBytes (2175, 39, out);
        {
          String _pn = "/atg/dynamo/service/ConfigurationReporter.outputComponentPropertiesInXML";
          out.print(" value=true");
          if (DropletDescriptor.matchesPropertyValue(request, response, _beanName5, "true", true,null, null))
          if (_form != null && _form.isXMLMimeType(request))
            out.print(" checked=\"checked\"");
          else
            out.print(" checked");
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 3-3 */
          __fileData.writeBytes (2277, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "checkbox", "false", DropletConstants.PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 3-3 */
        __fileData.writeBytes (2278, 79, out);
        /*** lines: 3-3 */
        __fileData.writeBytes (2408, 8, out);
        {
          String _pn = "/atg/dynamo/service/ConfigurationReporter.serializedFileName";
          out.print(" value=\"");
          out.print(DropletDescriptor.getPropertyHtmlStringValue(request, response, _beanName6, true, null, null));
          out.print('"');
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 3-3 */
          __fileData.writeBytes (2416, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "text", null, DropletConstants.PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 3-3 */
        __fileData.writeBytes (2417, 42, out);
        /*** lines: 3-3 */
        __fileData.writeBytes (2521, 8, out);
        {
          String _pn = "/atg/dynamo/service/ConfigurationReporter.beanRepresentationXMLFileName";
          out.print(" value=\"");
          out.print(DropletDescriptor.getPropertyHtmlStringValue(request, response, _beanName7, true, null, null));
          out.print('"');
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 3-3 */
          __fileData.writeBytes (2529, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "text", null, DropletConstants.PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 3-3 */
        __fileData.writeBytes (2530, 28, out);
        /*** lines: 3-3 */
        __fileData.writeBytes (2616, 24, out);
        {
          String _pn = "/atg/dynamo/service/ConfigurationReporter.outputRepresentationToXML";
          out.print(" name=\"");
          out.print(_pn);
          out.print('"');
          /*** lines: 3-3 */
          __fileData.writeBytes (2640, 1, out);
          if (_form != null) _form.addTag(request, response, null, _pn, "submit", null, DropletConstants.SUBMIT_PRIORITY_DEFAULT, null, null, null, false);
        }
        /*** lines: 3-58 */
        __fileData.writeBytes (2641, 43, out);
/* @version $Id: //product/DAS/version/10.0.3/release/DAS/admin/atg/dynamo/admin/en/config-reporter-bean-representation2.jhtml#2 $$Change: 651448 $*/        /*** lines: 58-59 */
        __fileData.writeBytes (2846, 2, out);
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
