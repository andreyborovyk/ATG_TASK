<%--
  An "expression editor" that displays a button for popping up a panel containing
  information about a root expression.

  @param  expression  The expression to be rendered
  @param  container   The ID of the container for this expression editor
  @param  editorId    A unique identifier for this expression editor

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/debug.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ page import="atg.beans.*,atg.repository.*,atg.ui.expreditor.*,java.io.*"%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"       %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramExpression" param="expression"/>
  <dspel:getvalueof var="paramContainer"  param="container"/>
  <dspel:getvalueof var="paramEditorId"   param="editorId"/>

  <%-- Derive IDs for page elements --%>
  <c:set var="idSuffix" value="${paramEditorId}${paramExpression.identifier}"/>
  <c:set var="labelId" value="terminalLabel_${idSuffix}"/>
  <c:set var="menuId"  value="menu_${idSuffix}"/>

  <%-- Render the link --%>
  <a id="<c:out value='${labelId}'/>"
     class="terminalLabel expreditorControl"
     hidefocus="true"
     href="javascript:atg.expreditor.displayTerminalMenu({containerId: '<c:out value="${paramContainer}"/>',
                                                          editorId:    '<c:out value="${paramEditorId}"/>',
                                                          terminalId:  '<c:out value="${paramExpression.identifier}"/>',
                                                          menuId:      '<c:out value="${menuId}"/>',
                                                          anchorId:    '<c:out value="${labelId}"/>'})"
  >?</a>

  <%-- Create an initially invisible popup menu containing a representation of the expression --%>

  <style type="text/css">
    .debugMenu {
      width: 800px;
      height: 400px;
      overflow: auto;
      background-color: #ffffff;
      border: solid 1px #000000;
      padding: 2px;
      line-height: 100%;
      z-index: 2000;
    }
    .currentTerminal {
      font-weight: bold;
    }
    .isTerminal {
      color: #00f;
    }
    .nonTerminal {
      color: #888;
    }
    .indentSpacer {
      padding:  0 10px 0 10px;
    }
    .indentLevel0 {
      color: #000;
    }
    .indentLevel1 {
      color: #f00;
    }
    .indentLevel2 {
      color: #0e0;
    }
    .indentLevel3 {
      color: #66f;
    }
  </style>

  <div id="<c:out value='${menuId}'/>"
       class="debugMenu"
       style="display: none">

    <%
class ExpressionWriter {
  public String printRootExpression(Expression pExpression) {
    try {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(stream);
      printExpression(pExpression, writer, 0);
      writer.flush();
      return stream.toString();
    }
    catch (Exception e) {
      return e.getClass().getName() + ": " + e.getMessage();
    }
  }

  private void printExpression(Expression pExpression, PrintWriter pWriter, int pLevel) {

    String exprString = getExpressionString(pExpression);
    String textClass = (pExpression instanceof TerminalExpression ? "isTerminal" : "nonTerminal");
    if (isCurrentTerminal(pExpression))
      textClass += " currentTerminal";

    printIndent(pWriter, pLevel);
    pWriter.println("<span class=\"" + textClass + "\">" + getExpressionString(pExpression) + "</span><br/>");

    if (pExpression instanceof NodeExpressionImpl) {
      NodeExpressionImpl nodeExpression = (NodeExpressionImpl) pExpression;
      for (int i = 0; i < nodeExpression.getNumElements(); i++) {
        Expression subExpression = (nodeExpression.isRealized(i) ? nodeExpression.getElement(i) : null);
        if (subExpression != null) {
          try {
            printExpression(subExpression, pWriter, pLevel + 1);
          }
          catch (Exception e) {
            e.printStackTrace(pWriter);
          }
        }
      }
    }
  }

  private void printIndent(PrintWriter pWriter, int pLevel) {
    for (int i = 0; i < pLevel; i++) {
      String colorClass = "indentLevel" + Integer.toString(i % 4);
      pWriter.print("<span class=\"indentSpacer " + colorClass + "\">|</span>");
    }
  }

  private String getExpressionString(Expression pExpression) {
                    
    String fullClassName = pExpression.getClass().getName();
    String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
    if (className.endsWith("Expression"))
      className = className.substring(0, className.lastIndexOf("Expression"));
      
    if (pExpression instanceof TokenExpression) {
      TokenConstruct construct = (TokenConstruct) (pExpression.getConstruct());
      String desc = construct.getDescription();
      if (desc != null)
        className = className + " \"" + desc + "\"";
    }
    else if (pExpression instanceof LiteralExpression) {
      String value = ((LiteralExpression) pExpression).getValueAsText();
      if (value != null)
        className = className + " \"" + value + "\"";
      else
        className = className + " (no value)";
    }
    else {
      String constructName = pExpression.getConstruct().getName();
      if (constructName != null)
        className = className + " (" + constructName + ")";
    }
    
    className += " <i>";
    ExpressionType type = pExpression.getType();
    if (type instanceof AnyType)
      className += "any";
    else if (type instanceof NoType)
      className += "none";
    else if (type instanceof PropertyType) {
      PropertyType pt = (PropertyType) type;
      className += "prop ";
      DynamicPropertyDescriptor desc = pt.getPropertyDescriptor();
      if (desc != null) {
        className += desc.getName();
        if (desc instanceof RepositoryPropertyDescriptor) {
          RepositoryPropertyDescriptor rpd = (RepositoryPropertyDescriptor) desc;
          className += " in " + rpd.getItemDescriptor().getRepository().getRepositoryName() + ":" + rpd.getItemDescriptor().getItemDescriptorName();
        }
      }
      else
        className += "(null)";
    }
    else if (type instanceof RepositoryItemType) {
      RepositoryItemType rt = (RepositoryItemType) type;
      className += "item " + rt.getRepositoryName() + ":" + rt.getItemDescriptorName();
    }
    else if (type instanceof ClassType) {
      ClassType ct = (ClassType) type;
      className += "class " + ct.getTypeClass().getName();
    }
    else if (type == null) {
      className += "null";
    }
    else {
      className += type.getClass().getName();
    }
    
    className += "</i>";

    return className;
  }

  private boolean isCurrentTerminal(Expression pExpression) {

    Expression child = pExpression;
    Expression parent = pExpression.getParent();
      
    while (parent != null) {
      if (parent instanceof ChoiceExpression) {
        ChoiceExpression choice = (ChoiceExpression) parent;
        int choiceIndex = choice.getChoiceIndex();
        if (choiceIndex >= 0 && choiceIndex < choice.getNumElements()) {
          if (choice.getElement(choiceIndex) != child)
            return false;
        }
        else
          return false;
      }
      child = parent;
      parent = child.getParent();
    }
    
    return true;
  }
};
      Expression expression = (Expression) pageContext.getAttribute("paramExpression");
      String result = new ExpressionWriter().printRootExpression(expression);
      pageContext.setAttribute("info", result);
    %>    
    <c:out value="${info}" escapeXml="false"/>
  </div>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/debug.jsp#2 $$Change: 651448 $ --%>
