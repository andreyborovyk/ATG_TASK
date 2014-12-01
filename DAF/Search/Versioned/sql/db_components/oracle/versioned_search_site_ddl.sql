


--      @version $Id: //product/DAF/version/10.0.3/Search/Base/src/sql/search_site_ddl.xml#2 $$Change: 651448 $  
--     Table containing site to search content set assocaitions  

create table srch_site_cntnt_pri (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	timestamp	null,
	id	varchar2(40)	not null,
	content_id	varchar2(40)	not null
,constraint srch_site_cntnt_pp primary key (id,asset_version));

create index srch_site_cntn_wsx on srch_site_cntnt_pri (workspace_id);
create index srch_site_cntn_cix on srch_site_cntnt_pri (checkin_date);

create table srch_site_cntnt (
	sec_asset_version	number(19)	not null,
	asset_version	number(19)	not null,
	cntn_id	varchar2(40)	not null,
	site_id	varchar2(40)	not null,
	content_label	varchar2(40)	not null
,constraint srch_site_cntnt_p primary key (cntn_id,asset_version,sec_asset_version));




