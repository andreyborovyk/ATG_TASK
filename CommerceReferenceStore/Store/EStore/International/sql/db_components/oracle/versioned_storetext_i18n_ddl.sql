


-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/sql/ddlgen/storetext_i18n_ddl.xml#2 $$Change: 651448 $

create table crs_txt_xlate (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	date	null,
	translation_id	varchar2(254)	not null,
	text_template	varchar2(4000)	null,
	text_type	number(10)	null
,constraint crs_txt_xlate_p primary key (translation_id,asset_version));

create index crs_txt_xlate_wsx on crs_txt_xlate (workspace_id);
create index crs_txt_xlate_cix on crs_txt_xlate (checkin_date);

create table crs_txt_txt_xlate (
	asset_version	number(19)	not null,
	text_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(254)	not null
,constraint crs_txt_txt_xlt_p primary key (text_id,locale,asset_version));

create index crs_txt_xlt_tr_id on crs_txt_txt_xlate (translation_id);

create table crs_txt_long_xlate (
	asset_version	number(19)	not null,
	translation_id	varchar2(254)	not null,
	text_template	clob	null
,constraint crs_lng_txt_xlt_p primary key (translation_id,asset_version));




