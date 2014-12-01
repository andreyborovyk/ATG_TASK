


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/Versioned/sql/db_components/mssql/versioned_storetext_ddl.sql#3 $$Change: 635816 $

create table crs_store_text (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	text_id	varchar(40)	not null,
	key_id	varchar(254)	not null,
	text_template	varchar(4000)	null,
	tag	varchar(40)	null,
	text_type	integer	null
,constraint crs_txt_key_p primary key (text_id,key_id,asset_version))

create index crs_txt_key_id on crs_store_text (key_id)
create index crs_store_text_wsx on crs_store_text (workspace_id)
create index crs_store_text_cix on crs_store_text (checkin_date)

create table crs_store_long_txt (
	asset_version	numeric(19)	not null,
	text_id	varchar(40)	not null,
	text_template	text	null
,constraint crs_lng_txt_key_p primary key (text_id,asset_version))



go
