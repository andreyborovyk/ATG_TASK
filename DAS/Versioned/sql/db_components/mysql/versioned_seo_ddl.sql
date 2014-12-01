


--     @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/seo_ddl.xml#2 $  
-- Table for seo-tag repository item

create table das_seo_tag (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	seo_tag_id	varchar(40)	not null,
	display_name	varchar(100)	null,
	title	varchar(254)	null,
	description	varchar(254)	null,
	keywords	varchar(254)	null,
	content_key	varchar(100)	null
,constraint das_seo_tag_pk primary key (seo_tag_id,asset_version));

create index das_seo_tag_wsx on das_seo_tag (workspace_id);
create index das_seo_tag_cix on das_seo_tag (checkin_date);

create table das_seo_sites (
	asset_version	bigint	not null,
	seo_tag_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint das_seo_site_pk primary key (seo_tag_id,site_id,asset_version));

commit;


