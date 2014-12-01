


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/site_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements, which will configureyour database for use MultiSite

create table site_template (
	id	varchar(40)	not null,
	name	varchar(254)	not null,
	description	varchar(254)	not null,
	item_mapping_id	varchar(40)	not null
,constraint site_template1_p primary key (id))


create table site_configuration (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	id	varchar(40)	not null,
	name	varchar(254)	not null,
	description	varchar(254)	null,
	template	varchar(40)	null,
	production_url	varchar(254)	null,
	enabled	tinyint	not null,
	site_down_url	varchar(254)	null,
	open_date	datetime	null,
	pre_opening_url	varchar(254)	null,
	closing_date	datetime	null,
	post_closing_url	varchar(254)	null,
	modification_time	datetime	null,
	creation_time	datetime	null,
	author	varchar(254)	null,
	last_modified_by	varchar(254)	null,
	site_icon	varchar(254)	null,
	favicon	varchar(254)	null,
	site_priority	integer	null,
	context_root	varchar(254)	null
,constraint site_configurat1_p primary key (id,asset_version))

create index site_configura_wsx on site_configuration (workspace_id)
create index site_configura_cix on site_configuration (checkin_date)

create table site_additional_urls (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	additional_production_url	varchar(254)	null,
	idx	integer	not null
,constraint siteadditio_url1_p primary key (id,idx,asset_version))


create table site_types (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	site_type	varchar(254)	not null
,constraint site_types1_p primary key (id,site_type,asset_version))


create table site_group (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	id	varchar(40)	not null,
	display_name	varchar(254)	not null
,constraint site_group1_p primary key (id,asset_version))

create index site_group_wsx on site_group (workspace_id)
create index site_group_cix on site_group (checkin_date)

create table site_group_sites (
	asset_version	numeric(19)	not null,
	site_id	varchar(40)	not null,
	site_group_id	varchar(40)	not null
,constraint site_group_sites_p primary key (site_id,site_group_id,asset_version))


create table site_group_shareable_types (
	asset_version	numeric(19)	not null,
	shareable_types	varchar(254)	not null,
	site_group_id	varchar(40)	not null
,constraint site_group_share_p primary key (shareable_types,site_group_id,asset_version))



go
