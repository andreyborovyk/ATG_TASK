<%--
Generates page title and store it into "headerText" request scope variable.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/bread_crumbs_set_title.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="stateNode" var="stateNode"/>

  <c:set var="node" value="${stateNode.node}" />
  <c:set var="refParams" value="${stateNode.refParams}" />

  <c:set var="headerText" value="${node.title}"/>
  
  <c:choose>
    <%-- Workbench UI section --%>

    <%--Term Dictionaries--%>
    <c:when test="${node.type=='Term'}">
      <dictionary:termFindByPrimaryKey termId="${refParams.termId}" var="term" />
      <fmt:message var="headerText" key="term.bread.crumbs.title">
        <fmt:param value="${term.name}"/>
      </fmt:message>
    </c:when>
    <c:when test="${node.type=='sao_edit_set'}">
      <tpo:textProcessingOptionsSetFindByPrimaryKey textProcessingOptionsSetId="${refParams.saoSetId}" var="saoSet"/>
      <c:set var="headerText" value="${saoSet.name}"/>
    </c:when>
    <c:when test="${node.type=='sao_set_default'}">
      <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"
            var="activeProjectId"/>
      <common:searchProjectFindByPrimaryKey searchProjectId="${activeProjectId}" var="project"/>
      <fmt:message var="headerText" key="sao_set.default_set">
        <fmt:param value="${project.name}"/>
      </fmt:message>
    </c:when>
    <%--Term Weight--%>
    <c:when test="${node.type == 'term_weight_sets'}">
      <fmt:message var="headerText" key="term_weight_sets.weight_sets"/>
    </c:when>
    <c:when test="${node.type=='term_weight_set'}">
      <termweights:termWeightSetFindByPrimaryKey termWeightSetId="${refParams.termWeightId}" var="termWeightSet"/>
      <fmt:message var="headerText" key="term_weight.edit.title">
        <fmt:param value="${termWeightSet.name}"/>
      </fmt:message>
    </c:when>
    <%-- Query Sets --%>
    <c:when test="${node.type == 'query_rule_sets'}">
      <fmt:message var="headerText" key="querysets_general.bread.crumbs.title"/>
    </c:when>
    <c:when test="${node.type=='query_set'}">
      <queryrule:queryRuleSetFindByPrimaryKey queryRuleSetId="${refParams.querySetId}" var="queryRuleSet"/>
      <fmt:message var="headerText" key="queryset_general.bread.edit.crumbs.title">
        <fmt:param value="${queryRuleSet.name}"/>
      </fmt:message>
    </c:when>
    <c:when test="${node.type=='query_set_group'}">
      <%--TODO: Get query rule group name by ID using ${refParams.initId}--%>
      <queryrule:queryRuleGroupFindByPrimaryKey queryRuleGroupId="${refParams.queryGroupId}" var="queryRuleGroup"/>
      <fmt:message var="headerText" key="query_rule_group.bread.crumbs.title">
        <fmt:param value="${queryRuleGroup.name}"/>
      </fmt:message>
    </c:when>
    <c:when test="${node.type=='query_rule'}">
      <queryrule:queryRuleFindByPrimaryKey queryRuleId="${refParams.queryRuleId}" var="queryRule"/>
      <fmt:message var="headerText" key="queryrule.edit.bread.crumbs.title">
        <fmt:param value="${queryRule.name}"/>
      </fmt:message>
    </c:when>
    <%-- Topic Set --%>
    <c:when test="${node.type=='topic_set'}">
      <topic:topicSetFindByPrimaryKey topicSetId="${refParams.topicSetId}" var="topicset"/>
      <fmt:message var="headerText" key="search_workbench.node.topicset">
        <fmt:param value="${topicset.name}"/>
      </fmt:message>
    </c:when>
    <c:when test="${node.type=='topic'}">
      <topic:topicFindByPrimaryKey topicId="${refParams.topicId}" var="topic"/>
      <fmt:message var="headerText" key="search_workbench.node.topic">
        <fmt:param value="${topic.name}"/>
      </fmt:message>
    </c:when>
    <%-- TPO--%>
    <c:when test="${node.type=='tpo_edit_set'}">
      <tpo:textProcessingOptionsSetFindByPrimaryKey textProcessingOptionsSetId="${refParams.tpoSetId}" var="tpoSet"/>
      <fmt:message var="headerText" key="search_workbench.node.tposet">
        <fmt:param value="${tpoSet.name}"/>
      </fmt:message>
    </c:when>
    <%-- Refine config --%>
    <c:when test="${node.type=='facetSets'}">
      <fmt:message var="headerText" key="facetsets_general.bread_crumb.title" />
    </c:when>
    <c:when test="${node.type=='facetSet'}">
      <facets:facetSetFindByPrimaryKey facetSetId="${refParams.facetSetId}" var="facetSet"/>
      <fmt:message var="headerText" key="facetset.bread_crumb.title">
        <fmt:param value="${facetSet.name}"/>
      </fmt:message>
    </c:when>
    <c:when test="${node.type=='facet'}">
      <facets:facetFindByPrimaryKey facetId="${refParams.facetId}" var="facet"/>
      <fmt:message var="headerText" key="facet.bread_crumb.title">
        <fmt:param value="${facet.name}"/>
      </fmt:message>
    </c:when>
    <c:when test="${node.type=='facetIndexed'}">
      <d:getvalueof var="activeProjectId" bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"/>
      <admin-beans:getIndexedFacetSets var="indexedFacet" projectId="${activeProjectId}"
                                       parentId="${refParams.adapterItemId}" />
      <fmt:message var="headerText" key="facet.bread_crumb.title">
        <fmt:param value="${indexedFacet.name}"/>
      </fmt:message>
    </c:when>
    <c:when test="${node.type=='facetSetIndexed'}">
      <d:getvalueof var="activeProjectId" bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"/>
      <admin-beans:getIndexedFacetSets var="indexedFacet" projectId="${activeProjectId}"
                                       parentId="${refParams.adapterItemId}" />
      <fmt:message var="headerText" key="facetset.bread_crumb.title">
        <fmt:param value="${indexedFacet.name}"/>
      </fmt:message>
    </c:when>
    <c:when test="${node.type=='facetSetIndexedGeneral'}">
      <d:getvalueof var="activeProjectId" bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"/>
      <common:searchProjectFindByPrimaryKey searchProjectId="${activeProjectId}" var="activeProject"/>
      <fmt:message var="headerText" key="facetsets_general_readonly.bread.crumb">
        <fmt:param value="${activeProject.name}"/>
      </fmt:message>
    </c:when>
  
    <c:otherwise>
      <fmt:message var="headerText" key="${stateNode.title}">
        <c:forEach var="titleParam" items="${stateNode.titleParameters}">
          <fmt:param value="${titleParam}" />
        </c:forEach>
      </fmt:message>
    </c:otherwise>
  </c:choose>

  <c:set var="headerText" scope="request" value="${headerText}" />
</d:page>

<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/bread_crumbs_set_title.jsp#2 $$Change: 651448 $--%>
