


-- ATG Reporting Framework DDL
-- ===========================
-- Dimension: TIME            
-- Levels: YEAR               
--         QUARTER            
--         MONTH              
--         WEEK               
--         DAY                
--         HOUR               

create table ARF_CAL_DOW (
	ID	varchar(40)	not null,
	DAY_OF_WEEK	numeric(3)	not null,
	SHORT_NAME	varchar(3)	default null,
	LONG_NAME	varchar(9)	default null,
	SHORT_NAME_EN	varchar(3)	not null,
	LONG_NAME_EN	varchar(9)	not null
,constraint ARF_CAL_DOY_P primary key (ID));

create index ARF_CAL_DOW_X1 on ARF_CAL_DOW (DAY_OF_WEEK);
create index ARF_CAL_DOW_X2 on ARF_CAL_DOW (SHORT_NAME_EN);
create index ARF_CAL_DOW_X3 on ARF_CAL_DOW (LONG_NAME_EN);

create table ARF_CAL_MOY (
	ID	varchar(40)	not null,
	MONTH_OF_YEAR	numeric(3)	not null,
	SHORT_NAME	varchar(4)	default null,
	LONG_NAME	varchar(9)	default null,
	SHORT_NAME_EN	varchar(4)	not null,
	LONG_NAME_EN	varchar(9)	not null
,constraint ARF_CAL_MOY_P primary key (ID));

create index ARF_CAL_MOY_X1 on ARF_CAL_MOY (MONTH_OF_YEAR);
create index ARF_CAL_MOY_X2 on ARF_CAL_MOY (SHORT_NAME_EN);
create index ARF_CAL_MOY_X3 on ARF_CAL_MOY (LONG_NAME_EN);

create table ARF_CAL_QOY (
	ID	varchar(40)	not null,
	QTR_OF_YEAR	numeric(3)	not null,
	SHORT_NAME	varchar(4)	default null,
	LONG_NAME	varchar(9)	default null,
	SHORT_NAME_EN	varchar(4)	not null,
	LONG_NAME_EN	varchar(9)	not null
,constraint ARF_CAL_QOY_P primary key (ID));

create index ARF_CAL_QOY_X1 on ARF_CAL_QOY (QTR_OF_YEAR);
create index ARF_CAL_QOY_X2 on ARF_CAL_QOY (SHORT_NAME_EN);
create index ARF_CAL_QOY_X3 on ARF_CAL_QOY (LONG_NAME_EN);

create table ARF_TIME_YEAR (
	ID	varchar(40)	not null,
	YEAR_TIMESTAMP	timestamp	not null,
	YEAR_OF_ERA	smallint	not null
,constraint ARF_TIME_YEAR_P primary key (ID)
,constraint ARF_TIME_YEAR_U1 unique (YEAR_TIMESTAMP));

create index ARF_TIME_YOE_X2 on ARF_TIME_YEAR (YEAR_OF_ERA);

create table ARF_TIME_QTR (
	ID	varchar(40)	not null,
	YEAR_ID	varchar(40)	not null,
	QOY_ID	varchar(40)	not null,
	QTR_TIMESTAMP	timestamp	not null,
	QTR_OF_YEAR	numeric(3)	not null
,constraint ARF_TIME_QTR_P primary key (ID)
,constraint ARF_TIME_QTR_U1 unique (QTR_TIMESTAMP)
,constraint ARF_TIME_QYID_F1 foreign key (YEAR_ID) references ARF_TIME_YEAR (ID)
,constraint ARF_TIME_QOYID_F2 foreign key (QOY_ID) references ARF_CAL_QOY (ID));

create index ARF_TIME_QOY_X1 on ARF_TIME_QTR (QOY_ID);
create index ARF_TIME_QYI_X2 on ARF_TIME_QTR (YEAR_ID);
create index ARF_TIME_QOY_X4 on ARF_TIME_QTR (QTR_OF_YEAR);

create table ARF_TIME_MONTH (
	ID	varchar(40)	not null,
	YEAR_ID	varchar(40)	not null,
	QTR_ID	varchar(40)	not null,
	MOY_ID	varchar(40)	not null,
	MONTH_TIMESTAMP	timestamp	not null,
	MONTH_OF_YEAR	numeric(3)	not null
,constraint ARF_TIME_MONTH_P primary key (ID)
,constraint ARF_TIME_MONTH_U1 unique (MONTH_TIMESTAMP)
,constraint ARF_TIME_MYID_F1 foreign key (YEAR_ID) references ARF_TIME_YEAR (ID)
,constraint ARF_TIME_MQID_F2 foreign key (QTR_ID) references ARF_TIME_QTR (ID)
,constraint ARF_TIME_MOYID_F3 foreign key (MOY_ID) references ARF_CAL_MOY (ID));

create index ARF_TIME_MYI_X1 on ARF_TIME_MONTH (YEAR_ID);
create index ARF_TIME_MQI_X2 on ARF_TIME_MONTH (QTR_ID);
create index ARF_TIME_MOY_X3 on ARF_TIME_MONTH (MOY_ID);
create index ARF_TIME_MOY_X5 on ARF_TIME_MONTH (MONTH_OF_YEAR);

create table ARF_TIME_WEEK (
	ID	varchar(40)	not null,
	YEAR_ID	varchar(40)	not null,
	WEEK_TIMESTAMP	timestamp	not null,
	WEEK_OF_YEAR	numeric(3)	not null
,constraint ARF_TIME_WEEK_P primary key (ID)
,constraint ARF_TIME_WEEK_U1 unique (WEEK_TIMESTAMP)
,constraint ARF_TIME_WYID_F1 foreign key (YEAR_ID) references ARF_TIME_YEAR (ID));

create index ARF_TIME_WYI_X1 on ARF_TIME_WEEK (YEAR_ID);
create index ARF_TIME_WOY_X3 on ARF_TIME_WEEK (WEEK_OF_YEAR);

create table ARF_TIME_DAY (
	ID	varchar(40)	not null,
	WEEK_ID	varchar(40)	not null,
	MONTH_ID	varchar(40)	not null,
	DOW_ID	varchar(40)	not null,
	DAY_TIMESTAMP	timestamp	not null,
	DAY_OF_WEEK	numeric(3)	not null,
	DAY_OF_MONTH	numeric(3)	not null,
	DAY_OF_YEAR	smallint	not null
,constraint ARF_TIME_DAY_P primary key (ID)
,constraint ARF_TIME_DAY_U1 unique (DAY_TIMESTAMP)
,constraint ARF_TIME_DWID_F1 foreign key (WEEK_ID) references ARF_TIME_WEEK (ID)
,constraint ARF_TIME_DMID_F2 foreign key (MONTH_ID) references ARF_TIME_MONTH (ID)
,constraint ARF_TIME_DOWID_F3 foreign key (DOW_ID) references ARF_CAL_DOW (ID));

create index ARF_TIME_DWI_X1 on ARF_TIME_DAY (WEEK_ID);
create index ARF_TIME_DMI_X2 on ARF_TIME_DAY (MONTH_ID);
create index ARF_TIME_DOW_X3 on ARF_TIME_DAY (DOW_ID);
create index ARF_TIME_DOW_X5 on ARF_TIME_DAY (DAY_OF_WEEK);
create index ARF_TIME_DOM_X6 on ARF_TIME_DAY (DAY_OF_MONTH);
create index ARF_TIME_DOY_X7 on ARF_TIME_DAY (DAY_OF_YEAR);

create table ARF_TIME_HOUR (
	ID	varchar(40)	not null,
	DAY_ID	varchar(40)	not null,
	HOUR_TIMESTAMP	timestamp	not null,
	HOUR_OF_DAY	numeric(3)	not null
,constraint ARF_TIME_HOUR_P primary key (ID)
,constraint ARF_TIME_HOUR_U1 unique (HOUR_TIMESTAMP)
,constraint ARF_TIME_HDID_F1 foreign key (DAY_ID) references ARF_TIME_DAY (ID));

create index ARF_TIME_DID_X1 on ARF_TIME_HOUR (DAY_ID);
create index ARF_TIME_HOD_X2 on ARF_TIME_HOUR (HOUR_OF_DAY);

create table ARF_TIME_TOD (
	ID	integer	not null,
	HOUR_OF_DAY	numeric(3)	not null,
	MIN_OF_HOUR	numeric(3)	not null,
	MIN_OF_DAY	smallint	not null,
	HALFDAY_OF_DAY	numeric(3)	not null,
	HOUR_OF_HALFDAY	numeric(3)	not null
,constraint ARF_TIME_TOD_P primary key (ID));

create index ARF_TIME_HOD_X1 on ARF_TIME_TOD (HOUR_OF_DAY);
create index ARF_TIME_MOH_X2 on ARF_TIME_TOD (MIN_OF_HOUR);
create index ARF_TIME_HDOD_X3 on ARF_TIME_TOD (HALFDAY_OF_DAY);
create index ARF_TIME_HOHD_X4 on ARF_TIME_TOD (HOUR_OF_HALFDAY);

create table ARF_TIME_INTERVAL (
	ID	numeric(3)	not null,
	NAME	varchar(254)	not null,
	NAME_EN	varchar(254)	default null,
	ABBREVIATION	varchar(8)	default null,
	ABBREVIATION_EN	varchar(8)	default null,
	START_TIMESTAMP	timestamp	not null,
	END_TIMESTAMP	timestamp	not null
,constraint ARF_TIME_INTVL_P primary key (ID));

-- Dimension: CURRENCY        
-- Levels: CURRENCY           

create table ARF_CURRENCY (
	ID	smallint	not null,
	CURRENCY_NAME	varchar(254)	not null,
	CURRENCY_NAME_EN	varchar(254)	default null,
	ISO4217_ALPHA3	char(3)	not null,
	ISO4217_NUM3	smallint	default null,
	RECORD_START_DATE	timestamp	default null,
	RECORD_END_DATE	timestamp	default null
,constraint ARF_CURRENCY_P primary key (ID));

create index ARF_CURNCY_IA3_X1 on ARF_CURRENCY (ISO4217_ALPHA3);
create index ARF_CURNCY_IN3_X2 on ARF_CURRENCY (ISO4217_NUM3);
-- Dimension: LANGUAGE        
-- Levels: LANGUAGE           

create table ARF_LANGUAGE (
	ID	smallint	not null,
	LANG_NAME	varchar(254)	not null,
	LANG_NAME_EN	varchar(254)	default null,
	ISO639_1_ALPHA2	char(2)	default null,
	ISO639_2_ALPHA3	char(3)	not null,
	RECORD_START_DATE	timestamp	default null,
	RECORD_END_DATE	timestamp	default null
,constraint ARF_LANGUAGE_P primary key (ID));

create index ARF_LANG_IA2_X1 on ARF_LANGUAGE (ISO639_1_ALPHA2);
create index ARF_LANG_IA3_X2 on ARF_LANGUAGE (ISO639_2_ALPHA3);
-- Dimension: GEOGRAPHY       
-- Levels: COUNTRY            
-- Levels: REGION            

create table ARF_GEO_COUNTRY (
	ID	smallint	not null,
	COUNTRY_NAME	varchar(254)	not null,
	COUNTRY_NAME_EN	varchar(254)	default null,
	ISO3166_1_ALPHA2	char(2)	default null,
	ISO3166_1_ALPHA3	char(3)	default null,
	ISO3166_1_NUM3	smallint	default null,
	FIPS10_4_CODE	char(2)	default null,
	RECORD_START_DATE	timestamp	default null,
	RECORD_END_DATE	timestamp	default null
,constraint ARF_GEO_CNTRY_P primary key (ID));

create index ARF_CNTRY_IA2_X1 on ARF_GEO_COUNTRY (ISO3166_1_ALPHA2);
create index ARF_CNTRY_IA3_X2 on ARF_GEO_COUNTRY (ISO3166_1_ALPHA3);
create index ARF_CNTRY_IN3_X3 on ARF_GEO_COUNTRY (ISO3166_1_NUM3);

create table ARF_GEO_REGION (
	ID	smallint	not null,
	COUNTRY_ID	smallint	not null,
	REGION_NAME	varchar(254)	not null,
	REGION_NAME_EN	varchar(254)	default null,
	ISO3166_2_CODE	varchar(6)	default null,
	FIPS10_4_CODE	char(4)	default null,
	RECORD_START_DATE	timestamp	default null,
	RECORD_END_DATE	timestamp	default null
,constraint ARF_GEO_REGION_P primary key (ID)
,constraint ARF_GREGION_GC_F1 foreign key (COUNTRY_ID) references ARF_GEO_COUNTRY (ID));

create index ARF_GREGION_CI_X1 on ARF_GEO_REGION (COUNTRY_ID);
create index ARF_GREGION_IC_X2 on ARF_GEO_REGION (ISO3166_2_CODE);
create index ARF_GREGION_FC_X3 on ARF_GEO_REGION (FIPS10_4_CODE);
-- Dimension: GENDER           
-- Levels: GENDER              

create table ARF_GENDER (
	ID	numeric(3)	not null,
	GENDER_NAME	varchar(40)	not null,
	GENDER_NAME_EN	varchar(40)	default null
,constraint ARF_GENDER_P primary key (ID));

-- Dimension: MARITAL STATUS   
-- Levels: MARITAL STATUS      

create table ARF_MARITAL_STATUS (
	ID	numeric(3)	not null,
	MARITAL_STATUS_NAME	varchar(40)	not null,
	MARITAL_STATUS_NAME_EN	varchar(40)	default null
,constraint ARF_MARITALSTAT_P primary key (ID));

-- Dimension: AGE GROUP         
-- Levels: AGE GROUP            

create table ARF_AGE_GROUP (
	ID	numeric(3)	not null,
	AGE_GROUP_NAME	varchar(40)	not null,
	AGE_GROUP_NAME_EN	varchar(40)	default null,
	AGE_MIN	numeric(3)	not null,
	AGE_MAX	numeric(3)	not null
,constraint ARF_AGE_GROUP_P primary key (ID));

-- Dimension: ORGANIZATION   
-- Levels: ORGANIZATION      

create table ARF_ORGANIZATION (
	ID	integer	not null,
	NORGANIZATION_ID	varchar(40)	not null,
	PARENT_ORG_ID	integer	default null,
	NAME	varchar(254)	not null,
	DESCRIPTION	varchar(254)	default null,
	RECORD_LAST_UPDATE	timestamp	default null,
	RECORD_START_DATE	timestamp	default null,
	RECORD_END_DATE	timestamp	default null,
	MOST_RECENT	numeric(1)	default 1 not null,
	DELETED	numeric(1)	default 0 not null
,constraint ARF_ORGANIZATION_P primary key (ID)
,constraint ARF_ORG_F1 foreign key (PARENT_ORG_ID) references ARF_ORGANIZATION (ID));

create index ARF_ORG_NOID_X1 on ARF_ORGANIZATION (NORGANIZATION_ID);
create index ARF_ORG_POID_X2 on ARF_ORGANIZATION (PARENT_ORG_ID);
-- Dimension: USER           
-- Levels: USER              

create table ARF_USER (
	ID	integer	not null,
	NUSER_ID	varchar(40)	not null,
	LOGIN	varchar(40)	not null,
	FIRST_NAME	varchar(40)	default null,
	MIDDLE_NAME	varchar(40)	default null,
	LAST_NAME	varchar(40)	default null,
	EMAIL	varchar(255)	default null,
	DATE_OF_BIRTH	date	default null,
	LANG_ID	smallint	not null,
	GENDER_ID	numeric(3)	default 0 not null,
	MARITAL_STATUS_ID	numeric(3)	default 0 not null,
	HOME_REGION_ID	smallint	default 0 not null,
	ORGANIZATION_ID	integer	default 0 not null,
	RECORD_LAST_UPDATE	timestamp	default null,
	RECORD_START_DATE	timestamp	default null,
	RECORD_END_DATE	timestamp	default null,
	MOST_RECENT	numeric(1)	default 1 not null,
	DELETED	numeric(1)	default 0 not null
,constraint ARF_USER_P primary key (ID)
,constraint ARF_USER_LANG_F1 foreign key (LANG_ID) references ARF_LANGUAGE (ID)
,constraint ARF_USER_GNDR_F2 foreign key (GENDER_ID) references ARF_GENDER (ID)
,constraint ARF_USER_HRGN_F3 foreign key (HOME_REGION_ID) references ARF_GEO_REGION (ID)
,constraint ARF_USER_MRST_F4 foreign key (MARITAL_STATUS_ID) references ARF_MARITAL_STATUS (ID)
,constraint ARF_USER_ORGN_F5 foreign key (ORGANIZATION_ID) references ARF_ORGANIZATION (ID));

create index ARF_USER_NUID_X1 on ARF_USER (NUSER_ID);
create index ARF_USER_LANG_X2 on ARF_USER (LANG_ID);
create index ARF_USER_GNDR_X3 on ARF_USER (GENDER_ID);
create index ARF_USER_HRGN_X4 on ARF_USER (HOME_REGION_ID);
create index ARF_USER_MRST_X5 on ARF_USER (MARITAL_STATUS_ID);
create index ARF_USER_ORGN_X6 on ARF_USER (ORGANIZATION_ID);
-- Dimension: DEMOGRAPHIC       
-- Levels: DEMOGRAPHIC          

create table ARF_DEMOGRAPHIC (
	ID	smallint	not null,
	GENDER_ID	numeric(3)	not null,
	AGE_GROUP_ID	numeric(3)	not null,
	MARITAL_STATUS_ID	numeric(3)	not null,
	REGION_ID	smallint	not null
,constraint ARF_DEMOGRAPHIC_P primary key (ID)
,constraint ARF_DEMOGRAPHX_F1 foreign key (GENDER_ID) references ARF_GENDER (ID)
,constraint ARF_DEMOGRAPHX_F2 foreign key (AGE_GROUP_ID) references ARF_AGE_GROUP (ID)
,constraint ARF_DEMOGRAPHX_F3 foreign key (MARITAL_STATUS_ID) references ARF_MARITAL_STATUS (ID)
,constraint ARF_DEMOGRAPHX_F4 foreign key (REGION_ID) references ARF_GEO_REGION (ID));

create index ARF_DEMOGRAPHX_X1 on ARF_DEMOGRAPHIC (GENDER_ID);
create index ARF_DEMOGRAPHX_X2 on ARF_DEMOGRAPHIC (AGE_GROUP_ID);
create index ARF_DEMOGRAPHX_X3 on ARF_DEMOGRAPHIC (MARITAL_STATUS_ID);
create index ARF_DEMOGRAPHX_X4 on ARF_DEMOGRAPHIC (REGION_ID);
-- Dimension: SEGMENT           
-- Levels: SEGMENT              

create table ARF_SEGMENT (
	ID	smallint	not null,
	NSEGMENT_ID	varchar(254)	not null,
	SEGMENT_NAME	varchar(254)	not null,
	SEGMENT_DESC	varchar(254)	default null,
	RECORD_LAST_UPDATE	timestamp	default null,
	RECORD_START_DATE	timestamp	default null,
	RECORD_END_DATE	timestamp	default null,
	MOST_RECENT	numeric(1)	default 1 not null,
	DELETED	numeric(1)	default 0 not null
,constraint ARF_SEGMENT_P primary key (ID));

create index ARF_SEGMENT_X1 on ARF_SEGMENT (NSEGMENT_ID);

create table ARF_SEGCLSTR (
	ID	integer	not null,
	NAME	varchar(254)	not null,
	HASH_VALUE	varchar(254)	not null,
	LENGTH	numeric(3)	not null
,constraint ARF_SEGCLSTR_P primary key (ID));

create index ARF_SEGCLSTR_X1 on ARF_SEGCLSTR (HASH_VALUE);

create table ARF_SEGCLSTR_MBRS (
	SEGCLSTR_ID	integer	not null,
	SEGMENT_ID	smallint	not null,
	MEMBER	numeric(3)	default 1 not null
,constraint ARF_SEGCLTR_MBRS_P primary key (SEGCLSTR_ID,SEGMENT_ID,MEMBER)
,constraint ARF_SEGC_MBRS_F1 foreign key (SEGCLSTR_ID) references ARF_SEGCLSTR (ID)
,constraint ARF_SEGC_MBRS_F2 foreign key (SEGMENT_ID) references ARF_SEGMENT (ID));

create index ARF_SEGC_MBRS1_X on ARF_SEGCLSTR_MBRS (SEGMENT_ID);
-- Dimension: STIMULUS          
-- Levels: STIMULUS             

create table ARF_STIMULUS (
	ID	integer	not null,
	NSTIMULUS_ID	varchar(254)	not null,
	STIMULUS_NAME	varchar(254)	not null,
	STIMULUS_DESC	varchar(254)	default null,
	STIMULUS_TYPE	varchar(254)	default null,
	RECORD_LAST_UPDATE	timestamp	default null,
	RECORD_START_DATE	timestamp	default null,
	RECORD_END_DATE	timestamp	default null,
	MOST_RECENT	numeric(1)	default 1 not null,
	DELETED	numeric(1)	default 0 not null
,constraint ARF_STIMULUS_P primary key (ID));

create index ARF_STIMULUS_X1 on ARF_STIMULUS (NSTIMULUS_ID);

create table ARF_STIMGRP (
	ID	integer	not null,
	NAME	varchar(254)	not null,
	HASH_VALUE	varchar(254)	not null,
	LENGTH	smallint	not null
,constraint ARF_STIMGRP_P primary key (ID));

create index ARF_STIMGRP_X1 on ARF_STIMGRP (HASH_VALUE);

create table ARF_STIMGRP_MBRS (
	STIMGRP_ID	integer	not null,
	STIMULUS_ID	integer	not null
,constraint ARF_STIMGRP_MBRS_P primary key (STIMGRP_ID,STIMULUS_ID)
,constraint ARF_STIMG_MBRS_F1 foreign key (STIMGRP_ID) references ARF_STIMGRP (ID)
,constraint ARF_STIMG_MBRS_F2 foreign key (STIMULUS_ID) references ARF_STIMULUS (ID));

create index ARF_STIMG_MBRS1_X on ARF_STIMGRP_MBRS (STIMULUS_ID);
-- Dimension: SITE            
-- Levels: SITE               

create table ARF_SITE (
	ID	smallint	not null,
	NSITE_ID	varchar(40)	not null,
	NAME	varchar(254)	not null,
	NAME_EN	varchar(254)	default null,
	DESCRIPTION	varchar(254)	default null,
	DESCRIPTION_EN	varchar(254)	default null,
	HOME_PAGE	varchar(254)	default null,
	LAUNCH_DATE	timestamp	default null,
	CLOSE_DATE	timestamp	default null,
	ENABLED	numeric(1)	not null,
	CURRENCY_NAME	varchar(254)	default null,
	CURRENCY_ALPHA3	char(3)	default null,
	LANGUAGE_NAME	varchar(254)	default null,
	LANGUAGE_ALPHA2	char(2)	default null,
	LANGUAGE_ALPHA3	char(3)	default null,
	DEFAULT_CATALOG_NAME	varchar(254)	default null,
	RECORD_LAST_UPDATE	timestamp	default null,
	RECORD_START_DATE	timestamp	default null,
	RECORD_END_DATE	timestamp	default null,
	MOST_RECENT	numeric(1)	default 1 not null,
	DELETED	numeric(1)	default 0 not null
,constraint ARF_SITE_P primary key (ID));

create index ARF_SITE_NSID_X1 on ARF_SITE (NSITE_ID);
-- Dimension: USER_AGENT      
-- Levels: USER_AGENT         

create table ARF_USER_AGENT (
	ID	smallint	not null,
	USER_AGENT	varchar(254)	not null,
	AGENT_NAME	varchar(40)	not null,
	AGENT_DESCRIPTION	varchar(254)	default null,
	BROWSER_NAME	varchar(40)	not null,
	BROWSER_VERSION	varchar(20)	not null,
	OPERATINGSYSTEM_NAME	varchar(40)	not null,
	OPERATINGSYSTEM_VERSION	varchar(20)	not null,
	OPERATINGSYSTEM_ARCHITECTURE	varchar(20)	not null,
	PLATFORM_NAME	varchar(40)	not null,
	SECURITY	varchar(20)	not null,
	COMPATIBLE_FLAG	numeric(1)	not null,
	GECKO_FLAG	numeric(1)	not null,
	IS_ROBOT	numeric(1)	not null,
	IS_BROWSER	numeric(1)	not null,
	RECORD_LAST_UPDATE	timestamp	default null,
	RECORD_START_DATE	timestamp	default null,
	RECORD_END_DATE	timestamp	default null
,constraint ARF_USER_AGNT_P primary key (ID));

-- Dimension: REFERRER      
-- Levels: REFERRER         

create table ARF_REFERRER (
	ID	smallint	not null,
	REFERRER_URL	varchar(254)	not null,
	REFERRER_SITE_NAME	varchar(254)	not null,
	REFERRER_SITE_DESCRIPTION	varchar(254)	default null,
	REFERRER_URL_SCHEME	varchar(20)	not null,
	REFERRER_URL_HOST	varchar(40)	not null,
	REFERRER_URL_DOMAIN	varchar(40)	not null,
	REFERRER_URL_DOMAIN_TYPE	varchar(20)	not null,
	REFERRER_URL_PATH	varchar(254)	not null,
	IS_SEARCH_ENGINE	numeric(1)	not null,
	RECORD_LAST_UPDATE	timestamp	default null,
	RECORD_START_DATE	timestamp	default null,
	RECORD_END_DATE	timestamp	default null
,constraint ARF_REFERRER_P primary key (ID));

-- Fact: SITE_VISITS                      
-- Dimensions: START DATE                 
--             START TIME                 
--             END DATE                   
--             END TIME                   
--             VISITOR                    
--             STIMULUS GROUP             
--             DEMOGRAPHIC                
-- Measures:   DURATION SECONDS           
--             NUMBER OF PAGE VISITS      

create table ARF_SITE_VISIT (
	SITE_VISIT_ID	bigint	not null,
	START_TIMESTAMP	timestamp	default null,
	START_DAY_ID	varchar(40)	not null,
	START_TIME_ID	integer	not null,
	END_DAY_ID	varchar(40)	not null,
	END_TIME_ID	integer	not null,
	START_VISIT_TIMESTAMP	timestamp	default null,
	SITE_VISIT_START_DAY_ID	varchar(40)	not null,
	SITE_VISIT_START_TIME_ID	integer	not null,
	SITE_VISIT_END_DAY_ID	varchar(40)	not null,
	SITE_VISIT_END_TIME_ID	integer	not null,
	VISIT_END_DAY_ID	varchar(40)	not null,
	VISIT_END_TIME_ID	integer	not null,
	VISITOR_ID	integer	not null,
	NVISITOR_ID	varchar(40)	default null,
	STIMGRP_ID	integer	not null,
	DEMOGRAPHIC_ID	smallint	not null,
	SITE_ID	smallint	default 0 not null,
	ENTRY_SITE_ID	smallint	default 0 not null,
	EXIT_SITE_ID	smallint	default 0 not null,
	REFERRING_SITE_ID	smallint	default 0 not null,
	SEQUENCE_NUM	numeric(3)	not null,
	USER_AGENT_ID	smallint	default 0 not null,
	REFERRER_ID	smallint	default 0 not null,
	SESSION_ID	varchar(128)	not null,
	NUM_PAGE_VIEWS	smallint	not null,
	DURATION_SECONDS	integer	not null,
	TOTAL_ELAPSED_DURATION_SECONDS	integer	not null,
	SITE_VISIT_DURATION_SECONDS	integer	not null,
	RESOURCE_DURATION_SECONDS	integer	not null
,constraint ARF_SITE_VISIT_P primary key (SITE_VISIT_ID)
,constraint ARF_SITE_VISIT_F1 foreign key (START_DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_SITE_VISIT_F2 foreign key (START_TIME_ID) references ARF_TIME_TOD (ID)
,constraint ARF_SITE_VISIT_F3 foreign key (VISITOR_ID) references ARF_USER (ID)
,constraint ARF_SITE_VISIT_F4 foreign key (STIMGRP_ID) references ARF_STIMGRP (ID)
,constraint ARF_SITE_VISIT_F5 foreign key (DEMOGRAPHIC_ID) references ARF_DEMOGRAPHIC (ID)
,constraint ARF_SITE_VISIT_F6 foreign key (END_DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_SITE_VISIT_F7 foreign key (END_TIME_ID) references ARF_TIME_TOD (ID)
,constraint ARF_SITE_VISIT_F8 foreign key (SITE_ID) references ARF_SITE (ID)
,constraint ARF_SITE_VISIT_F9 foreign key (USER_AGENT_ID) references ARF_USER_AGENT (ID)
,constraint ARF_SITE_VISIT_F10 foreign key (REFERRER_ID) references ARF_REFERRER (ID)
,constraint ARF_SITE_VISIT_F11 foreign key (ENTRY_SITE_ID) references ARF_SITE (ID)
,constraint ARF_SITE_VISIT_F12 foreign key (EXIT_SITE_ID) references ARF_SITE (ID)
,constraint ARF_SITE_VISIT_F13 foreign key (REFERRING_SITE_ID) references ARF_SITE (ID)
,constraint ARF_SITE_VISIT_F14 foreign key (SITE_VISIT_START_DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_SITE_VISIT_F15 foreign key (SITE_VISIT_START_TIME_ID) references ARF_TIME_TOD (ID)
,constraint ARF_SITE_VISIT_F16 foreign key (SITE_VISIT_END_DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_SITE_VISIT_F17 foreign key (SITE_VISIT_END_TIME_ID) references ARF_TIME_TOD (ID)
,constraint ARF_SITE_VISIT_F18 foreign key (VISIT_END_DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_SITE_VISIT_F19 foreign key (VISIT_END_TIME_ID) references ARF_TIME_TOD (ID));

create index ARF_SITE_VISIT_X9 on ARF_SITE_VISIT (SITE_ID);
create index ARF_SITE_VISIT_X10 on ARF_SITE_VISIT (USER_AGENT_ID);
create index ARF_SITE_VISIT_X11 on ARF_SITE_VISIT (REFERRER_ID);
create index ARF_SITE_VISIT_X12 on ARF_SITE_VISIT (ENTRY_SITE_ID);
create index ARF_SITE_VISIT_X13 on ARF_SITE_VISIT (EXIT_SITE_ID);
create index ARF_SITE_VISIT_X14 on ARF_SITE_VISIT (REFERRING_SITE_ID);
create index ARF_SITE_VISIT_X15 on ARF_SITE_VISIT (SITE_VISIT_START_DAY_ID);
create index ARF_SITE_VISIT_X16 on ARF_SITE_VISIT (SITE_VISIT_START_TIME_ID);
create index ARF_SITE_VISIT_X17 on ARF_SITE_VISIT (SITE_VISIT_END_DAY_ID);
create index ARF_SITE_VISIT_X18 on ARF_SITE_VISIT (SITE_VISIT_END_TIME_ID);
create index ARF_SITE_VISIT_X19 on ARF_SITE_VISIT (VISIT_END_DAY_ID);
create index ARF_SITE_VISIT_X20 on ARF_SITE_VISIT (VISIT_END_TIME_ID);
create index ARF_SITE_VISIT_X1 on ARF_SITE_VISIT (START_DAY_ID);
create index ARF_SITE_VISIT_X2 on ARF_SITE_VISIT (START_TIME_ID);
create index ARF_SITE_VISIT_X3 on ARF_SITE_VISIT (VISITOR_ID);
create index ARF_SITE_VISIT_X4 on ARF_SITE_VISIT (STIMGRP_ID);
create index ARF_SITE_VISIT_X5 on ARF_SITE_VISIT (DEMOGRAPHIC_ID);
create index ARF_SITE_VISIT_X6 on ARF_SITE_VISIT (SESSION_ID);
create index ARF_SITE_VISIT_X7 on ARF_SITE_VISIT (END_DAY_ID);
create index ARF_SITE_VISIT_X8 on ARF_SITE_VISIT (END_TIME_ID);
create index ARF_SITE_VISIT_X21 on ARF_SITE_VISIT (NVISITOR_ID);
-- Fact: REGISTRATIONS                    
-- Dimensions: REGISTRATION DATE          
--             REGISTRATION TIME          
--             REGISTRANT                 
--             STIMULUS GROUP             
--             SEGMENT CLUSTER            
--             DEMOGRAPHIC                
--             SITE                       
--             SITE VISIT                 
-- Measures:                              
-- 

create table ARF_REGISTRATION (
	REGISTRATION_ID	integer	not null,
	REGISTRATION_TIMESTAMP	timestamp	default null,
	REGISTRATION_DAY_ID	varchar(40)	not null,
	REGISTRATION_TIME_ID	integer	not null,
	REGISTRANT_ID	integer	not null,
	STIMGRP_ID	integer	not null,
	SEGCLSTR_ID	integer	not null,
	DEMOGRAPHIC_ID	smallint	not null,
	SITE_ID	smallint	not null,
	SITE_VISIT_ID	bigint	not null,
	SESSION_ID	varchar(128)	not null
,constraint ARF_REGISTRATION_P primary key (REGISTRATION_ID)
,constraint ARF_REGISTRATIO_F1 foreign key (REGISTRATION_DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_REGISTRATIO_F2 foreign key (REGISTRATION_TIME_ID) references ARF_TIME_TOD (ID)
,constraint ARF_REGISTRATIO_F3 foreign key (REGISTRANT_ID) references ARF_USER (ID)
,constraint ARF_REGISTRATIO_F4 foreign key (STIMGRP_ID) references ARF_STIMGRP (ID)
,constraint ARF_REGISTRATIO_F5 foreign key (SEGCLSTR_ID) references ARF_SEGCLSTR (ID)
,constraint ARF_REGISTRATIO_F6 foreign key (DEMOGRAPHIC_ID) references ARF_DEMOGRAPHIC (ID)
,constraint ARF_REGISTRATIO_F7 foreign key (SITE_ID) references ARF_SITE (ID)
,constraint ARF_REGISTRATIO_F8 foreign key (SITE_VISIT_ID) references ARF_SITE_VISIT (SITE_VISIT_ID));

create index ARF_REGISTRATIO_X1 on ARF_REGISTRATION (REGISTRATION_DAY_ID);
create index ARF_REGISTRATIO_X2 on ARF_REGISTRATION (REGISTRATION_TIME_ID);
create index ARF_REGISTRATIO_X3 on ARF_REGISTRATION (REGISTRANT_ID);
create index ARF_REGISTRATIO_X4 on ARF_REGISTRATION (STIMGRP_ID);
create index ARF_REGISTRATIO_X5 on ARF_REGISTRATION (SEGCLSTR_ID);
create index ARF_REGISTRATIO_X6 on ARF_REGISTRATION (DEMOGRAPHIC_ID);
create index ARF_REGISTRATIO_X7 on ARF_REGISTRATION (SITE_ID);
create index ARF_REGISTRATIO_X8 on ARF_REGISTRATION (SITE_VISIT_ID);
-- ATG Reporting Framework Internal Users DDL
-- ==========================================

create table ARF_IU_DIR_LVL (
	ID	varchar(40)	not null,
	NAME_EN	varchar(40)	not null,
	DPTH	smallint	not null,
	PARENT_ID	varchar(40)	default null
,constraint ARF_IDIRLVL_P primary key (ID)
,constraint ARF_IDIRLVL_PI_F1 foreign key (PARENT_ID) references ARF_IU_DIR_LVL (ID));

create index ARF_IDIRLVL_X1 on ARF_IU_DIR_LVL (PARENT_ID);

create table ARF_IU_ORG (
	ID	varchar(40)	not null,
	ORG_ID	varchar(40)	not null,
	NAME_EN	varchar(254)	not null,
	DESCRIPTION_EN	varchar(254)	default null,
	PARENT_ORG_ID	varchar(40)	default null,
	DIR_LVL_ID	varchar(40)	not null,
	LAST_UPDATED	timestamp	not null,
	DELETED	numeric(1,0)	not null
,constraint ARF_IORG_P primary key (ID)
,constraint ARF_IORG_PO_F1 foreign key (PARENT_ORG_ID) references ARF_IU_ORG (ID)
,constraint ARF_IORG_DLVL_F1 foreign key (DIR_LVL_ID) references ARF_IU_DIR_LVL (ID)
,constraint ARF_IORG_C1 check (DELETED in (0,1)));

create index ARF_IORG_OID_X1 on ARF_IU_ORG (ORG_ID);
create index ARF_IORG_NAME_X2 on ARF_IU_ORG (NAME_EN);
create index ARF_IORG_DEL_X3 on ARF_IU_ORG (DELETED);
create index ARF_IORG_LSTU_X4 on ARF_IU_ORG (LAST_UPDATED);
create index ARF_IORG_POID_X5 on ARF_IU_ORG (PARENT_ORG_ID);
create index ARF_IORG_LID_X6 on ARF_IU_ORG (DIR_LVL_ID);

create table ARF_IU_USER (
	ID	varchar(40)	not null,
	USER_ID	varchar(40)	not null,
	LOGIN	varchar(40)	not null,
	FIRST_NAME	varchar(40)	default null,
	LAST_NAME	varchar(40)	default null,
	PARENT_ORG_ID	varchar(40)	default null,
	DIR_LVL_ID	varchar(40)	not null,
	LAST_UPDATED	timestamp	not null,
	DELETED	numeric(1,0)	not null
,constraint ARF_IUSR_P primary key (ID)
,constraint ARF_IUSR_PO_F1 foreign key (PARENT_ORG_ID) references ARF_IU_ORG (ID)
,constraint ARF_IUSR_DLVL_F1 foreign key (DIR_LVL_ID) references ARF_IU_DIR_LVL (ID)
,constraint ARF_IUSR_C1 check (DELETED in (0,1)));

create index ARF_IUSR_UID_X1 on ARF_IU_USER (USER_ID);
create index ARF_IUSR_LOGN_X2 on ARF_IU_USER (LOGIN);
create index ARF_IUSR_FSTN_X3 on ARF_IU_USER (FIRST_NAME);
create index ARF_IUSR_LSTN_X4 on ARF_IU_USER (LAST_NAME);
create index ARF_IUSR_DEL_X5 on ARF_IU_USER (DELETED);
create index ARF_IUSR_LSTU_X6 on ARF_IU_USER (LAST_UPDATED);
create index ARF_IUSR_POID_X7 on ARF_IU_USER (PARENT_ORG_ID);
create index ARF_IUSR_LID_X8 on ARF_IU_USER (DIR_LVL_ID);
-- ATG Reporting Framework Search DDL
-- ==========================================

create table ARF_QUESTION_TYPE (
	ID	smallint	not null,
	NAME	varchar(40)	not null,
	NAME_EN	varchar(40)	default null
,constraint ARF_QUEST_TYPE_PK primary key (ID)
,constraint ARF_QUEST_TYPE_IX1 unique (NAME));


create table ARF_QUESTION_TYPE_GROUP (
	ID	integer	not null,
	NAME	varchar(254)	not null,
	HASH_VALUE	varchar(254)	not null,
	LENGTH	smallint	not null
,constraint ARF_QTYPE_GROUP_PK primary key (ID));

create index ARF_QTYPE_GRP_IX1 on ARF_QUESTION_TYPE_GROUP (HASH_VALUE);
create index ARF_QTYPE_GRP_IX2 on ARF_QUESTION_TYPE_GROUP (LENGTH);

create table ARF_QUESTION_TYPE_GROUP_MBRS (
	QUESTION_TYPE_GROUP_ID	integer	not null,
	QUESTION_TYPE_ID	smallint	not null
,constraint ARF_QT_GRP_MBRS_PK primary key (QUESTION_TYPE_ID,QUESTION_TYPE_GROUP_ID)
,constraint ARF_QT_MBRS_FK1 foreign key (QUESTION_TYPE_GROUP_ID) references ARF_QUESTION_TYPE_GROUP (ID)
,constraint ARF_QT_MBRS_FK2 foreign key (QUESTION_TYPE_ID) references ARF_QUESTION_TYPE (ID));

create index ARF_QT_MBRS_IX1 on ARF_QUESTION_TYPE_GROUP_MBRS (QUESTION_TYPE_GROUP_ID);

create table ARF_SEARCH_NUMBER (
	ID	integer	not null,
	QUANTITY	varchar(40)	not null
,constraint ARF_SEARCH_NUM_PK primary key (ID));


create table ARF_SEARCH_NUMBER_GROUP (
	ID	integer	not null,
	NAME	varchar(254)	not null,
	HASH_VALUE	varchar(254)	not null,
	LENGTH	smallint	not null
,constraint ARF_SRCH_NUM_GR_PK primary key (ID));

create index ARF_SRCH_NUMGRP1_X on ARF_SEARCH_NUMBER_GROUP (HASH_VALUE);
create index ARF_SRCH_NUMGRP2_X on ARF_SEARCH_NUMBER_GROUP (LENGTH);

create table ARF_SEARCH_NUMBER_GROUP_MBRS (
	SEARCH_NUMBER_GROUP_ID	integer	not null,
	SEARCH_NUMBER_ID	integer	not null
,constraint ARF_SRCH_NGRPMBR_P primary key (SEARCH_NUMBER_ID,SEARCH_NUMBER_GROUP_ID)
,constraint ARF_SRCH_NGRMBR1_F foreign key (SEARCH_NUMBER_GROUP_ID) references ARF_SEARCH_NUMBER_GROUP (ID)
,constraint ARF_SRCH_NGRMBR2_F foreign key (SEARCH_NUMBER_ID) references ARF_SEARCH_NUMBER (ID));

create index ARF_SRCH_NGRMBR1_X on ARF_SEARCH_NUMBER_GROUP_MBRS (SEARCH_NUMBER_GROUP_ID);

create table ARF_QUESTION (
	ID	integer	not null,
	NORMAL_QUESTION	varchar(900)	not null,
	QUESTION	varchar(2000)	not null,
	QUESTION_TYPE_GROUP_ID	integer	not null,
	WORD_COUNT	smallint	not null,
	QUESTION_NUMBER_GROUP_ID	integer	not null
,constraint ARF_QUESTION_PK primary key (ID)
,constraint ARF_QUESTION_FK1 foreign key (QUESTION_TYPE_GROUP_ID) references ARF_QUESTION_TYPE_GROUP (ID)
,constraint ARF_QUESTION_FK2 foreign key (QUESTION_NUMBER_GROUP_ID) references ARF_SEARCH_NUMBER_GROUP (ID));

create index ARF_QUESTION_IX1 on ARF_QUESTION (QUESTION_TYPE_GROUP_ID);
create index ARF_QUESTION_IX4 on ARF_QUESTION (QUESTION_NUMBER_GROUP_ID);
create index ARF_QUESTION_IX2 on ARF_QUESTION (NORMAL_QUESTION);
create index ARF_QUESTION_IX3 on ARF_QUESTION (WORD_COUNT);
commit;


