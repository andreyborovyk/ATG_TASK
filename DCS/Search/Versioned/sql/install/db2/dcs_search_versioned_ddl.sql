
-- the source for this section is 
-- versioned_commerce_refinement_ddl.sql




-- This file contains create table statements, which will configure your database for use with the new catalog-extended refinement repository

create table dcs_refine_config (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	inherit_global	numeric(3)	not null,
	inherit_category	numeric(3)	not null,
	can_child_inherit	numeric(3)	not null,
	is_global	numeric(3)	not null
,constraint dcs_refine_cfg_p primary key (id,asset_version));


create table dcs_refcfg_genels (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	seq	integer	not null,
	refine_element	varchar(40)	not null
,constraint dcs_rfcfg_genels_p primary key (id,seq,asset_version));


create table dcs_cat_refcfg (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	refine_config_id	varchar(40)	default null
,constraint dcs_cat_refcfg_p primary key (id,asset_version));

commit;



-- the source for this section is 
-- versioned_custom_catalog_refinement_ddl.sql




-- This file contains create table statements, which will configure your database for use with the new catalog-extended refinement repository

create table dcs_refcfg_custom (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	inherit_catalog	numeric(3)	not null
,constraint dcs_rfcf_custom_p primary key (id,asset_version));


create table dcs_catinfo_refcfg (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	refine_config_id	varchar(40)	not null
,constraint dcs_catinf_rfcfg_p primary key (id,asset_version));


create table dcs_catalog_refcfg (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	refine_config_id	varchar(40)	default null
,constraint dcs_cata_rfcfg_p primary key (id,asset_version));

commit;


