


--  @version $Id: //product/ARF/version/10.0.3/ARF/DW/Search/sql/xml/search_datawarehouse_ddl.xml#1 $$Change: 651360 $
-- This file contains create table statements, which will configureyour database for use with the search report summarization service.

create table ARF_SEARCH_PROJECT (
	ID	number(5)	not null,
	NPROJECT_ID	varchar2(40)	not null,
	NAME	varchar2(254)	not null,
	NAME_EN	varchar2(254)	null,
	RECORD_LAST_UPDATE	timestamp	null,
	RECORD_START_DATE	timestamp	null,
	RECORD_END_DATE	timestamp	null,
	MOST_RECENT	number(1)	default 1 not null,
	DELETED	number(1)	default 0 not null
,constraint ARF_PROJECT_PK primary key (ID));

create index ARF_PROJECT_IX1 on ARF_SEARCH_PROJECT (NPROJECT_ID);

create table ARF_SEARCH_ENVIRONMENT (
	ID	number(5)	not null,
	NENVIRONMENT_ID	varchar2(40)	not null,
	NAME	varchar2(254)	not null,
	NAME_EN	varchar2(254)	null,
	RECORD_LAST_UPDATE	timestamp	null,
	RECORD_START_DATE	timestamp	null,
	RECORD_END_DATE	timestamp	null,
	MOST_RECENT	number(1)	default 1 not null,
	DELETED	number(1)	default 0 not null,
	PROJECT_ID	number(5)	not null
,constraint ARF_SRCH_ENV_PK primary key (ID)
,constraint ARF_SRCH_ENV_FK1 foreign key (PROJECT_ID) references ARF_SEARCH_PROJECT (ID));

create index ARF_SRCH_ENV_IX1 on ARF_SEARCH_ENVIRONMENT (PROJECT_ID);
create index ARF_SRCH_ENV_IX2 on ARF_SEARCH_ENVIRONMENT (NENVIRONMENT_ID);

create table ARF_QUERY_TYPE (
	ID	number(3)	not null,
	NTYPE_ID	varchar2(40)	not null,
	NAME	varchar2(40)	not null,
	NAME_EN	varchar2(40)	null
,constraint ARF_QUERY_TYPE_PK primary key (ID)
,constraint ARF_QUERY_TYPE_IX1 unique (NAME));

create index ARF_QUERY_TYPE_IX2 on ARF_QUERY_TYPE (NTYPE_ID);

create table ARF_CONTENT (
	ID	number(10)	not null,
	TITLE	varchar2(500)	null,
	URL_KEY	varchar2(254)	not null,
	URL	varchar2(900)	not null,
	CONTENT_TYPE	varchar2(100)	not null
,constraint ARF_CONTENT_PK primary key (ID));

create index ARF_CONTENT_IX1 on ARF_CONTENT (URL_KEY,CONTENT_TYPE);
create index ARF_CONTENT_IX2 on ARF_CONTENT (URL);

create table ARF_SEARCH_TOPIC (
	ID	number(10)	not null,
	NTOPIC_ID	varchar2(40)	not null,
	PARENT_TOPIC_ID	number(10)	null,
	NAME	varchar2(254)	not null,
	NAME_EN	varchar2(254)	null,
	DESCRIPTION	varchar2(1000)	null,
	DESCRIPTION_EN	varchar2(1000)	null,
	RECORD_LAST_UPDATE	timestamp	null,
	RECORD_START_DATE	timestamp	null,
	RECORD_END_DATE	timestamp	null,
	MOST_RECENT	number(1)	default 1 not null,
	DELETED	number(1)	default 0 not null,
	TOPIC_PATH	varchar2(4000)	null
,constraint ARF_TOPIC_PK primary key (ID)
,constraint ARF_TOPIC_FK1 foreign key (PARENT_TOPIC_ID) references ARF_SEARCH_TOPIC (ID));

create index ARF_TOPIC_IX1 on ARF_SEARCH_TOPIC (PARENT_TOPIC_ID);
create index ARF_TOPIC_IX2 on ARF_SEARCH_TOPIC (NTOPIC_ID);

create table ARF_TOPIC_GROUP (
	ID	number(10)	not null,
	NAME	varchar2(254)	not null,
	HASH_VALUE	varchar2(254)	not null,
	LENGTH	number(5)	not null
,constraint ARF_TOPICGRP_PK primary key (ID));

create index ARF_TOPICGRP_IX1 on ARF_TOPIC_GROUP (HASH_VALUE);

create table ARF_TOPIC_GROUP_MBRS (
	TOPIC_GROUP_ID	number(10)	not null,
	TOPIC_ID	number(10)	not null
,constraint ARF_TPC_MBRS_PK primary key (TOPIC_ID,TOPIC_GROUP_ID)
,constraint ARF_TPCG_MBRS_FK1 foreign key (TOPIC_GROUP_ID) references ARF_TOPIC_GROUP (ID)
,constraint ARF_TPCG_MBRS_FK2 foreign key (TOPIC_ID) references ARF_SEARCH_TOPIC (ID));

create index ARF_TPCG_MBRS_IX1 on ARF_TOPIC_GROUP_MBRS (TOPIC_GROUP_ID);

create table ARF_WORD (
	ID	number(10)	not null,
	WORD	varchar2(100)	not null
,constraint ARF_WORD_PK primary key (ID));


create table ARF_WORD_GROUP (
	ID	number(10)	not null,
	NAME	varchar2(254)	not null,
	HASH_VALUE	varchar2(254)	not null,
	NUM_WORDS	number(5)	not null
,constraint ARF_WORD_GROUP_PK primary key (ID));

create index ARF_WORD_GROUP_IX1 on ARF_WORD_GROUP (HASH_VALUE);
create index ARF_WORD_GROUP_IX2 on ARF_WORD_GROUP (NUM_WORDS);

create table ARF_WORD_GROUP_MBRS (
	WORD_GROUP_ID	number(10)	not null,
	WORD_ID	number(10)	not null
,constraint ARF_W_GRP_MBRS_PK primary key (WORD_ID,WORD_GROUP_ID)
,constraint ARF_W_GRP_MBRS_FK1 foreign key (WORD_GROUP_ID) references ARF_WORD_GROUP (ID)
,constraint ARF_W_GRP_MBRS_FK2 foreign key (WORD_ID) references ARF_WORD (ID));

create index ARF_W_GRP_MBRS_IX1 on ARF_WORD_GROUP_MBRS (WORD_GROUP_ID);

create table ARF_DICTIONARY_ADAPTER (
	ID	number(5)	not null,
	NAME	varchar2(200)	not null
,constraint ARF_DICT_ADPTR_PK primary key (ID));


create table ARF_CUSTOM_TERM (
	ID	number(10)	not null,
	NAME	varchar2(100)	not null,
	ADAPTER_ID	number(5)	not null
,constraint ARF_CUSTOM_TERM_PK primary key (ID)
,constraint ARF_CUSTOM_TRM_FK1 foreign key (ADAPTER_ID) references ARF_DICTIONARY_ADAPTER (ID));

create index ARF_CUSTOM_TRM_IX1 on ARF_CUSTOM_TERM (ADAPTER_ID);

create table ARF_CUSTTRM_GRP (
	ID	number(10)	not null,
	NAME	varchar2(254)	not null,
	HASH_VALUE	varchar2(254)	not null,
	LENGTH	number(5)	not null
,constraint ARF_CUSTRM_GRP_PK primary key (ID));

create index ARF_CUSTTRM_GR_IX1 on ARF_CUSTTRM_GRP (HASH_VALUE);
create index ARF_CUSTTRM_GR_IX2 on ARF_CUSTTRM_GRP (LENGTH);

create table ARF_CUSTTRM_GRP_MBRS (
	CUSTOM_TERM_GROUP_ID	number(10)	not null,
	CUSTOM_TERM_ID	number(10)	not null
,constraint ARF_CSTR_GR_MB_PK primary key (CUSTOM_TERM_ID,CUSTOM_TERM_GROUP_ID)
,constraint ARF_CSTR_GR_MB_FK1 foreign key (CUSTOM_TERM_GROUP_ID) references ARF_CUSTTRM_GRP (ID)
,constraint ARF_CSTR_GR_MB_FK2 foreign key (CUSTOM_TERM_ID) references ARF_CUSTOM_TERM (ID));

create index ARF_CSTR_GR_MB_IX1 on ARF_CUSTTRM_GRP_MBRS (CUSTOM_TERM_GROUP_ID);

create table ARF_FINDER_TERM (
	ID	number(10)	not null,
	NAME	varchar2(100)	not null
,constraint ARF_FINDER_TERM_PK primary key (ID));


create table ARF_FINDTRM_GRP (
	ID	number(10)	not null,
	NAME	varchar2(254)	not null,
	HASH_VALUE	varchar2(254)	not null,
	LENGTH	number(5)	not null
,constraint ARF_FINDTRM_GRP_PK primary key (ID));

create index ARF_FINDTRM_GR_IX1 on ARF_FINDTRM_GRP (HASH_VALUE);
create index ARF_FINDTRM_GR_IX2 on ARF_FINDTRM_GRP (LENGTH);

create table ARF_FINDTRM_GRP_MBRS (
	FINDER_TERM_GROUP_ID	number(10)	not null,
	FINDER_TERM_ID	number(10)	not null
,constraint ARF_FDTR_GR_MB_PK primary key (FINDER_TERM_ID,FINDER_TERM_GROUP_ID)
,constraint ARF_FNTR_GR_MB_FK1 foreign key (FINDER_TERM_GROUP_ID) references ARF_FINDTRM_GRP (ID)
,constraint ARF_FNTR_GR_MB_FK2 foreign key (FINDER_TERM_ID) references ARF_FINDER_TERM (ID));

create index ARF_FNTR_GR_MB_IX1 on ARF_FINDTRM_GRP_MBRS (FINDER_TERM_GROUP_ID);

create table ARF_NULL_TERM (
	ID	number(10)	not null,
	NAME	varchar2(100)	not null
,constraint ARF_NULL_TERM_PK primary key (ID));


create table ARF_NULLTRM_GRP (
	ID	number(10)	not null,
	NAME	varchar2(254)	not null,
	HASH_VALUE	varchar2(254)	not null,
	LENGTH	number(5)	not null
,constraint ARF_NULLTRM_GRP_PK primary key (ID));

create index ARF_NULLTRM_GR_IX1 on ARF_NULLTRM_GRP (HASH_VALUE);
create index ARF_NULLTRM_GR_IX2 on ARF_NULLTRM_GRP (LENGTH);

create table ARF_NULLTRM_GRP_MBRS (
	NULL_TERM_GROUP_ID	number(10)	not null,
	NULL_TERM_ID	number(10)	not null
,constraint ARF_NLTR_GR_MB_PK primary key (NULL_TERM_ID,NULL_TERM_GROUP_ID)
,constraint ARF_NLTR_GR_MB_FK1 foreign key (NULL_TERM_GROUP_ID) references ARF_NULLTRM_GRP (ID)
,constraint ARF_NLTR_GR_MB_FK2 foreign key (NULL_TERM_ID) references ARF_NULL_TERM (ID));

create index ARF_NLTR_GR_MB_IX1 on ARF_NULLTRM_GRP_MBRS (NULL_TERM_GROUP_ID);

create table ARF_NULX_TERM (
	ID	number(10)	not null,
	NAME	varchar2(100)	not null
,constraint ARF_NULX_TERM_PK primary key (ID));


create table ARF_NULXTRM_GRP (
	ID	number(10)	not null,
	NAME	varchar2(254)	not null,
	HASH_VALUE	varchar2(254)	not null,
	LENGTH	number(5)	not null
,constraint ARF_NULXTRM_GRP_PK primary key (ID));

create index ARF_NULXTRM_GR_IX1 on ARF_NULXTRM_GRP (HASH_VALUE);
create index ARF_NULXTRM_GR_IX2 on ARF_NULXTRM_GRP (LENGTH);

create table ARF_NULXTRM_GRP_MBRS (
	NULX_TERM_GROUP_ID	number(10)	not null,
	NULX_TERM_ID	number(10)	not null
,constraint ARF_NXTR_GR_MB_PK primary key (NULX_TERM_ID,NULX_TERM_GROUP_ID)
,constraint ARF_NXTR_GR_MB_FK1 foreign key (NULX_TERM_GROUP_ID) references ARF_NULXTRM_GRP (ID)
,constraint ARF_NXTR_GR_MB_FK2 foreign key (NULX_TERM_ID) references ARF_NULX_TERM (ID));

create index ARF_NXTR_GR_MB_IX1 on ARF_NULXTRM_GRP_MBRS (NULX_TERM_GROUP_ID);

create table ARF_PROFILE_TYPE (
	ID	number(5)	not null,
	REPOSITORY_NAME	varchar2(40)	not null
,constraint ARF_PROF_TYPE_PK primary key (ID));


create table ARF_SEARCH_CONFIG (
	ID	number(5)	not null,
	NSEARCH_CONFIG_ID	varchar2(40)	not null,
	NAME	varchar2(254)	not null,
	DESCRIPTION	varchar2(254)	null,
	RECORD_LAST_UPDATE	timestamp	null,
	RECORD_START_DATE	timestamp	null,
	RECORD_END_DATE	timestamp	null,
	MOST_RECENT	number(1)	default 1 not null,
	DELETED	number(1)	default 0 not null
,constraint ARF_SRCH_CONF_PK primary key (ID));

create index ARF_SRCH_CONF_IX1 on ARF_SEARCH_CONFIG (NSEARCH_CONFIG_ID);

create table ARF_SEARCH_RULE_TYPE (
	ID	number(5)	not null,
	NSEARCH_RULE_TYPE_ID	varchar2(40)	not null,
	NAME	varchar2(254)	not null,
	NAME_EN	varchar2(254)	null
,constraint ARF_SRCH_RULE_T_PK primary key (ID));

create index ARF_SRCH_RL_TP_IX1 on ARF_SEARCH_RULE_TYPE (NSEARCH_RULE_TYPE_ID);

create table ARF_SEARCH_RULE (
	ID	number(10)	not null,
	NSEARCH_RULE_ID	varchar2(40)	not null,
	NAME	varchar2(254)	not null,
	DESCRIPTION	varchar2(254)	null,
	RULE_TYPE_ID	number(5)	not null,
	RECORD_LAST_UPDATE	timestamp	null,
	RECORD_START_DATE	timestamp	null,
	RECORD_END_DATE	timestamp	null,
	MOST_RECENT	number(1)	default 1 not null,
	DELETED	number(1)	default 0 not null
,constraint ARF_SRCH_RULE_PK primary key (ID)
,constraint ARF_SRCH_RULE_FK1 foreign key (RULE_TYPE_ID) references ARF_SEARCH_RULE_TYPE (ID));

create index ARF_SRCH_RULE_IX1 on ARF_SEARCH_RULE (RULE_TYPE_ID);
create index ARF_SRCH_RULE_IX2 on ARF_SEARCH_RULE (NSEARCH_RULE_ID);

create table ARF_SEARCH_RULE_GRP (
	ID	number(10)	not null,
	NAME	varchar2(254)	not null,
	HASH_VALUE	varchar2(254)	not null,
	LENGTH	number(5)	not null
,constraint ARF_SRCH_RL_GR_PK primary key (ID));

create index ARF_SRCH_RL_GR_IX1 on ARF_SEARCH_RULE_GRP (HASH_VALUE);
create index ARF_SRCH_RL_GR_IX2 on ARF_SEARCH_RULE_GRP (LENGTH);

create table ARF_SEARCH_RULE_GRP_MBRS (
	SEARCH_RULE_GROUP_ID	number(10)	not null,
	SEARCH_RULE_ID	number(10)	not null
,constraint ARF_SERL_GR_MB_PK primary key (SEARCH_RULE_ID,SEARCH_RULE_GROUP_ID)
,constraint ARF_SRRL_GR_MB_FK1 foreign key (SEARCH_RULE_GROUP_ID) references ARF_SEARCH_RULE_GRP (ID)
,constraint ARF_SRRL_GR_MB_FK2 foreign key (SEARCH_RULE_ID) references ARF_SEARCH_RULE (ID));

create index ARF_SRRL_GR_MB_IX1 on ARF_SEARCH_RULE_GRP_MBRS (SEARCH_RULE_GROUP_ID);

create table ARF_SRCH_SITE_CONSTR (
	ID	number(10)	not null,
	NAME	varchar2(254)	not null,
	HASH_VALUE	varchar2(254)	not null,
	LENGTH	number(5)	not null
,constraint ARF_SRCHSTCN_PK primary key (ID));

create index ARF_SRCHSTCN_IX1 on ARF_SRCH_SITE_CONSTR (HASH_VALUE);

create table ARF_SRCH_SITE_CONSTR_MBRS (
	SITE_CONSTR_ID	number(10)	not null,
	SITE_ID	number(5)	not null
,constraint ARF_SRCHSTCNMB_PK primary key (SITE_ID,SITE_CONSTR_ID)
,constraint ARF_SRCHSTCNMB_FK1 foreign key (SITE_CONSTR_ID) references ARF_SRCH_SITE_CONSTR (ID)
,constraint ARF_SRCHSTCNMB_FK2 foreign key (SITE_ID) references ARF_SITE (ID));

create index ARF_SRCHSTCNMB_IX1 on ARF_SRCH_SITE_CONSTR_MBRS (SITE_CONSTR_ID);

create table ARF_QUERY (
	ID	number(10)	not null,
	QUERY_TIMESTAMP	timestamp	null,
	QUERY_DAY_ID	varchar2(40)	not null,
	QUERY_TIME_ID	number(10)	not null,
	NQUERY_ID	varchar2(40)	not null,
	EXTERNAL_PROFILE_ID	number(10)	not null,
	INTERNAL_PROFILE_ID	varchar2(40)	not null,
	PROFILE_TYPE_ID	number(5)	not null,
	SEARCH_ENVIRONMENT_ID	number(5)	not null,
	LANGUAGE_ID	number(5)	not null,
	QUERY_TYPE_ID	number(3)	not null,
	QUESTION_ID	number(10)	not null,
	SEGCLSTR_ID	number(10)	not null,
	DEMOGRAPHIC_ID	number(5)	not null,
	SITE_VISIT_ID	number(19)	not null,
	WORD_GROUP_ID	number(10)	not null,
	CUSTOM_TERM_GROUP_ID	number(10)	not null,
	FINDER_TERM_GROUP_ID	number(10)	not null,
	NULL_TERM_GROUP_ID	number(10)	not null,
	NULX_TERM_GROUP_ID	number(10)	not null,
	TOPIC_GROUP_ID	number(10)	not null,
	SELECTED_TOPIC_GROUP_ID	number(10)	not null,
	SEARCH_CONFIG_ID	number(5)	not null,
	SEARCH_RULE_GROUP_ID	number(10)	not null,
	RESPONSE_TIME	number(10)	not null,
	TOP_SCORE	number(19,7)	default 0 not null,
	RESULTS_COUNT	number(10)	not null,
	LAST_SELECTED	number(1)	not null,
	SESSION_ID	varchar2(128)	null,
	SITE_ID	number(5)	not null,
	SITE_CONSTR_ID	number(10)	not null
,constraint ARF_QUERY_PK primary key (ID)
,constraint ARF_QUERY_FK1 foreign key (QUERY_DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_QUERY_FK2 foreign key (QUERY_TIME_ID) references ARF_TIME_TOD (ID)
,constraint ARF_QUERY_FK4 foreign key (SEARCH_ENVIRONMENT_ID) references ARF_SEARCH_ENVIRONMENT (ID)
,constraint ARF_QUERY_FK5 foreign key (LANGUAGE_ID) references ARF_LANGUAGE (ID)
,constraint ARF_QUERY_FK6 foreign key (QUERY_TYPE_ID) references ARF_QUERY_TYPE (ID)
,constraint ARF_QUERY_FK7 foreign key (QUESTION_ID) references ARF_QUESTION (ID)
,constraint ARF_QUERY_FK8 foreign key (SEGCLSTR_ID) references ARF_SEGCLSTR (ID)
,constraint ARF_QUERY_FK9 foreign key (DEMOGRAPHIC_ID) references ARF_DEMOGRAPHIC (ID)
,constraint ARF_QUERY_FK10 foreign key (SITE_VISIT_ID) references ARF_SITE_VISIT (SITE_VISIT_ID)
,constraint ARF_QUERY_FK11 foreign key (EXTERNAL_PROFILE_ID) references ARF_USER (ID)
,constraint ARF_QUERY_FK12 foreign key (INTERNAL_PROFILE_ID) references ARF_IU_USER (ID)
,constraint ARF_QUERY_FK13 foreign key (WORD_GROUP_ID) references ARF_WORD_GROUP (ID)
,constraint ARF_QUERY_FK19 foreign key (CUSTOM_TERM_GROUP_ID) references ARF_CUSTTRM_GRP (ID)
,constraint ARF_QUERY_FK20 foreign key (FINDER_TERM_GROUP_ID) references ARF_FINDTRM_GRP (ID)
,constraint ARF_QUERY_FK21 foreign key (NULL_TERM_GROUP_ID) references ARF_NULLTRM_GRP (ID)
,constraint ARF_QUERY_FK22 foreign key (NULX_TERM_GROUP_ID) references ARF_NULXTRM_GRP (ID)
,constraint ARF_QUERY_FK14 foreign key (TOPIC_GROUP_ID) references ARF_TOPIC_GROUP (ID)
,constraint ARF_QUERY_FK15 foreign key (SELECTED_TOPIC_GROUP_ID) references ARF_TOPIC_GROUP (ID)
,constraint ARF_QUERY_FK24 foreign key (SEARCH_CONFIG_ID) references ARF_SEARCH_CONFIG (ID)
,constraint ARF_QUERY_FK25 foreign key (SEARCH_RULE_GROUP_ID) references ARF_SEARCH_RULE_GRP (ID)
,constraint ARF_QUERY_FK16 foreign key (PROFILE_TYPE_ID) references ARF_PROFILE_TYPE (ID)
,constraint ARF_QUERY_FK26 foreign key (SITE_ID) references ARF_SITE (ID)
,constraint ARF_QUERY_FK27 foreign key (SITE_CONSTR_ID) references ARF_SRCH_SITE_CONSTR (ID));

create index ARF_QUERY_IX1 on ARF_QUERY (QUERY_DAY_ID);
create index ARF_QUERY_IX2 on ARF_QUERY (QUERY_TIME_ID);
create index ARF_QUERY_IX4 on ARF_QUERY (SEARCH_ENVIRONMENT_ID);
create index ARF_QUERY_IX5 on ARF_QUERY (LANGUAGE_ID);
create index ARF_QUERY_IX6 on ARF_QUERY (QUERY_TYPE_ID);
create index ARF_QUERY_IX7 on ARF_QUERY (QUESTION_ID);
create index ARF_QUERY_IX8 on ARF_QUERY (SEGCLSTR_ID);
create index ARF_QUERY_IX9 on ARF_QUERY (DEMOGRAPHIC_ID);
create index ARF_QUERY_IX10 on ARF_QUERY (SITE_VISIT_ID);
create index ARF_QUERY_IX11 on ARF_QUERY (EXTERNAL_PROFILE_ID);
create index ARF_QUERY_IX12 on ARF_QUERY (INTERNAL_PROFILE_ID);
create index ARF_QUERY_IX13 on ARF_QUERY (WORD_GROUP_ID);
create index ARF_QUERY_IX19 on ARF_QUERY (CUSTOM_TERM_GROUP_ID);
create index ARF_QUERY_IX20 on ARF_QUERY (FINDER_TERM_GROUP_ID);
create index ARF_QUERY_IX21 on ARF_QUERY (NULL_TERM_GROUP_ID);
create index ARF_QUERY_IX22 on ARF_QUERY (NULX_TERM_GROUP_ID);
create index ARF_QUERY_IX14 on ARF_QUERY (TOPIC_GROUP_ID);
create index ARF_QUERY_IX15 on ARF_QUERY (SELECTED_TOPIC_GROUP_ID);
create index ARF_QUERY_IX24 on ARF_QUERY (SEARCH_CONFIG_ID);
create index ARF_QUERY_IX25 on ARF_QUERY (SEARCH_RULE_GROUP_ID);
create index ARF_QUERY_IX16 on ARF_QUERY (PROFILE_TYPE_ID);
create index ARF_QUERY_IX26 on ARF_QUERY (SITE_ID);
create index ARF_QUERY_IX27 on ARF_QUERY (SITE_CONSTR_ID);
create index ARF_QUERY_IX17 on ARF_QUERY (NQUERY_ID);
create index ARF_QUERY_IX18 on ARF_QUERY (SESSION_ID);

create table ARF_VIEW_CONTENT (
	ID	number(10)	not null,
	VIEW_TIMESTAMP	timestamp	null,
	VIEW_DAY_ID	varchar2(40)	not null,
	VIEW_TIME_ID	number(10)	not null,
	SEARCH_ENVIRONMENT_ID	number(5)	not null,
	LANGUAGE_ID	number(5)	not null,
	CONTENT_ID	number(10)	not null,
	QUERY_ID	number(10)	not null,
	NQUERY_ID	varchar2(40)	not null,
	EXTERNAL_PROFILE_ID	number(10)	not null,
	INTERNAL_PROFILE_ID	varchar2(40)	not null,
	PROFILE_TYPE_ID	number(5)	not null,
	SEGCLSTR_ID	number(10)	not null,
	DEMOGRAPHIC_ID	number(5)	not null,
	SITE_VISIT_ID	number(19)	not null,
	TOPIC_GROUP_ID	number(10)	not null,
	LAST_VIEWED	number(1)	not null,
	SESSION_ID	varchar2(128)	null,
	SITE_ID	number(5)	not null
,constraint ARF_VIEW_CNT_PK primary key (ID)
,constraint ARF_VIEW_CNT_FK1 foreign key (VIEW_DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_VIEW_CNT_FK2 foreign key (VIEW_TIME_ID) references ARF_TIME_TOD (ID)
,constraint ARF_VIEW_CNT_FK4 foreign key (SEARCH_ENVIRONMENT_ID) references ARF_SEARCH_ENVIRONMENT (ID)
,constraint ARF_VIEW_CNT_FK5 foreign key (LANGUAGE_ID) references ARF_LANGUAGE (ID)
,constraint ARF_VIEW_CNT_FK6 foreign key (CONTENT_ID) references ARF_CONTENT (ID)
,constraint ARF_VIEW_CNT_FK7 foreign key (QUERY_ID) references ARF_QUERY (ID)
,constraint ARF_VIEW_CNT_FK8 foreign key (SEGCLSTR_ID) references ARF_SEGCLSTR (ID)
,constraint ARF_VIEW_CNT_FK9 foreign key (DEMOGRAPHIC_ID) references ARF_DEMOGRAPHIC (ID)
,constraint ARF_VIEW_CNT_FK10 foreign key (SITE_VISIT_ID) references ARF_SITE_VISIT (SITE_VISIT_ID)
,constraint ARF_VIEW_CNT_FK11 foreign key (EXTERNAL_PROFILE_ID) references ARF_USER (ID)
,constraint ARF_VIEW_CNT_FK12 foreign key (INTERNAL_PROFILE_ID) references ARF_IU_USER (ID)
,constraint ARF_VIEW_CNT_FK13 foreign key (TOPIC_GROUP_ID) references ARF_TOPIC_GROUP (ID)
,constraint ARF_VIEW_CNT_FK14 foreign key (PROFILE_TYPE_ID) references ARF_PROFILE_TYPE (ID)
,constraint ARF_VIEW_CNT_FK17 foreign key (SITE_ID) references ARF_SITE (ID));

create index ARF_VIEW_CNT_IX1 on ARF_VIEW_CONTENT (VIEW_DAY_ID);
create index ARF_VIEW_CNT_IX2 on ARF_VIEW_CONTENT (VIEW_TIME_ID);
create index ARF_VIEW_CNT_IX4 on ARF_VIEW_CONTENT (SEARCH_ENVIRONMENT_ID);
create index ARF_VIEW_CNT_IX5 on ARF_VIEW_CONTENT (LANGUAGE_ID);
create index ARF_VIEW_CNT_IX6 on ARF_VIEW_CONTENT (CONTENT_ID);
create index ARF_VIEW_CNT_IX7 on ARF_VIEW_CONTENT (QUERY_ID);
create index ARF_VIEW_CNT_IX8 on ARF_VIEW_CONTENT (SEGCLSTR_ID);
create index ARF_VIEW_CNT_IX9 on ARF_VIEW_CONTENT (DEMOGRAPHIC_ID);
create index ARF_VIEW_CNT_IX10 on ARF_VIEW_CONTENT (SITE_VISIT_ID);
create index ARF_VIEW_CNT_IX11 on ARF_VIEW_CONTENT (EXTERNAL_PROFILE_ID);
create index ARF_VIEW_CNT_IX12 on ARF_VIEW_CONTENT (INTERNAL_PROFILE_ID);
create index ARF_VIEW_CNT_IX13 on ARF_VIEW_CONTENT (TOPIC_GROUP_ID);
create index ARF_VIEW_CNT_IX14 on ARF_VIEW_CONTENT (PROFILE_TYPE_ID);
create index ARF_VIEW_CNT_IX17 on ARF_VIEW_CONTENT (SITE_ID);
create index ARF_VIEW_CNT_IX15 on ARF_VIEW_CONTENT (SESSION_ID);
create index ARF_VIEW_CNT_IX16 on ARF_VIEW_CONTENT (NQUERY_ID);

create table ARF_SEARCH_SITE_VISIT (
	ID	number(10)	not null,
	SSV_TIMESTAMP	timestamp	null,
	SSV_DAY_ID	varchar2(40)	not null,
	SSV_TIME_ID	number(10)	not null,
	SEARCH_ENVIRONMENT_ID	number(5)	not null,
	EXTERNAL_PROFILE_ID	number(10)	not null,
	INTERNAL_PROFILE_ID	varchar2(40)	not null,
	PROFILE_TYPE_ID	number(5)	not null,
	SEGCLSTR_ID	number(10)	not null,
	DEMOGRAPHIC_ID	number(5)	not null,
	SITE_VISIT_ID	number(19)	not null,
	LANGUAGE_ID	number(5)	not null,
	SESSION_ID	varchar2(128)	null,
	SITE_ID	number(5)	not null
,constraint ARF_SEARCH_SV_PK primary key (ID)
,constraint ARF_SEARCH_SV_FK1 foreign key (SSV_DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_SEARCH_SV_FK2 foreign key (SSV_TIME_ID) references ARF_TIME_TOD (ID)
,constraint ARF_SEARCH_SV_FK4 foreign key (SEARCH_ENVIRONMENT_ID) references ARF_SEARCH_ENVIRONMENT (ID)
,constraint ARF_SEARCH_SV_FK6 foreign key (LANGUAGE_ID) references ARF_LANGUAGE (ID)
,constraint ARF_SEARCH_SV_FK7 foreign key (DEMOGRAPHIC_ID) references ARF_DEMOGRAPHIC (ID)
,constraint ARF_SEARCH_SV_FK8 foreign key (EXTERNAL_PROFILE_ID) references ARF_USER (ID)
,constraint ARF_SEARCH_SV_FK9 foreign key (INTERNAL_PROFILE_ID) references ARF_IU_USER (ID)
,constraint ARF_SEARCH_SV_FK10 foreign key (SEGCLSTR_ID) references ARF_SEGCLSTR (ID)
,constraint ARF_SEARCH_SV_FK11 foreign key (PROFILE_TYPE_ID) references ARF_PROFILE_TYPE (ID)
,constraint ARF_SEARCH_SV_FK13 foreign key (SITE_ID) references ARF_SITE (ID));

create index ARF_SEARCH_SV_IX1 on ARF_SEARCH_SITE_VISIT (SSV_DAY_ID);
create index ARF_SEARCH_SV_IX2 on ARF_SEARCH_SITE_VISIT (SSV_TIME_ID);
create index ARF_SEARCH_SV_IX4 on ARF_SEARCH_SITE_VISIT (SEARCH_ENVIRONMENT_ID);
create index ARF_SEARCH_SV_IX6 on ARF_SEARCH_SITE_VISIT (LANGUAGE_ID);
create index ARF_SEARCH_SV_IX7 on ARF_SEARCH_SITE_VISIT (DEMOGRAPHIC_ID);
create index ARF_SEARCH_SV_IX8 on ARF_SEARCH_SITE_VISIT (EXTERNAL_PROFILE_ID);
create index ARF_SEARCH_SV_IX9 on ARF_SEARCH_SITE_VISIT (INTERNAL_PROFILE_ID);
create index ARF_SEARCH_SV_IX10 on ARF_SEARCH_SITE_VISIT (SEGCLSTR_ID);
create index ARF_SEARCH_SV_IX11 on ARF_SEARCH_SITE_VISIT (PROFILE_TYPE_ID);
create index ARF_SEARCH_SV_IX13 on ARF_SEARCH_SITE_VISIT (SITE_ID);
create index ARF_SEARCH_SV_IX12 on ARF_SEARCH_SITE_VISIT (SESSION_ID);



