


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/sql/db_components/solid/versioned_seo_i18n_ddl.sql#3 $$Change: 635816 $

create table crs_seo_xlate (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	timestamp	null,
	translation_id	varchar(40)	not null,
	title	varchar(254)	null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	keywords	varchar(254)	null
, primary key (translation_id,asset_version));

create index crs_seo_xlate_wsx on crs_seo_xlate (workspace_id);
create index crs_seo_xlate_cix on crs_seo_xlate (checkin_date);
alter table crs_seo_xlate set pessimistic;


create table crs_seo_seo_xlate (
	asset_version	numeric(19)	not null,
	seo_tag_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
, primary key (seo_tag_id,locale,asset_version));

alter table crs_seo_seo_xlate set pessimistic;

commit work;


