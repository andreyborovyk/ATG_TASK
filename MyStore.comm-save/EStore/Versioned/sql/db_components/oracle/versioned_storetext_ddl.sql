


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/Versioned/sql/db_components/oracle/versioned_storetext_ddl.sql#3 $$Change: 635816 $

create table crs_store_text (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	date	null,
	text_id	varchar2(40)	not null,
	key_id	varchar2(254)	not null,
	text_template	varchar2(4000)	null,
	tag	varchar2(40)	null,
	text_type	number(10)	null
,constraint crs_txt_key_p primary key (text_id,key_id,asset_version));

create index crs_txt_key_id on crs_store_text (key_id);
create index crs_store_text_wsx on crs_store_text (workspace_id);
create index crs_store_text_cix on crs_store_text (checkin_date);

create table crs_store_long_txt (
	asset_version	number(19)	not null,
	text_id	varchar2(40)	not null,
	text_template	clob	null
,constraint crs_lng_txt_key_p primary key (text_id,asset_version));




