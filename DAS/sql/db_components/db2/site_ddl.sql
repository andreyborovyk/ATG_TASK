


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/site_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements, which will configureyour database for use MultiSite

create table site_template (
	id	varchar(40)	not null,
	name	varchar(254)	not null,
	description	varchar(254)	not null,
	item_mapping_id	varchar(40)	not null
,constraint site_template1_p primary key (id));


create table site_configuration (
	id	varchar(40)	not null,
	name	varchar(254)	not null,
	description	varchar(254)	default null,
	template	varchar(40)	default null,
	production_url	varchar(254)	default null,
	enabled	numeric(1)	not null,
	site_down_url	varchar(254)	default null,
	open_date	timestamp	default null,
	pre_opening_url	varchar(254)	default null,
	closing_date	timestamp	default null,
	post_closing_url	varchar(254)	default null,
	modification_time	timestamp	default null,
	creation_time	timestamp	default null,
	author	varchar(254)	default null,
	last_modified_by	varchar(254)	default null,
	site_icon	varchar(254)	default null,
	favicon	varchar(254)	default null,
	site_priority	integer	default null,
	context_root	varchar(254)	default null
,constraint site_configurat1_p primary key (id));


create table site_additional_urls (
	id	varchar(40)	not null,
	additional_production_url	varchar(254)	default null,
	idx	integer	not null
,constraint siteadditio_url1_p primary key (id,idx));


create table site_types (
	id	varchar(40)	not null,
	site_type	varchar(254)	not null
,constraint site_types1_p primary key (id,site_type));


create table site_group (
	id	varchar(40)	not null,
	display_name	varchar(254)	not null
,constraint site_group1_p primary key (id));


create table site_group_sites (
	site_id	varchar(40)	not null,
	site_group_id	varchar(40)	not null
,constraint site_group_sites_p primary key (site_id,site_group_id)
,constraint site_group_site1_f foreign key (site_id) references site_configuration (id)
,constraint site_group_site2_f foreign key (site_group_id) references site_group (id));

create index site_group_site1_x on site_group_sites (site_id);
create index site_group_site2_x on site_group_sites (site_group_id);

create table site_group_shareable_types (
	shareable_types	varchar(254)	not null,
	site_group_id	varchar(40)	not null
,constraint site_group_share_p primary key (shareable_types,site_group_id)
,constraint site_group_shar1_f foreign key (site_group_id) references site_group (id));

create index site_group_shar1_x on site_group_shareable_types (site_group_id);
commit;


