


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/Search/common/sql/custom_catalog_refinement_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements, which will configure your database for use with the new catalog-extended refinement repository

create table dcs_refcfg_custom (
	id	varchar(40)	not null,
	inherit_catalog	tinyint	not null
,constraint dcs_rfcf_custom_p primary key (id))


create table dcs_catinfo_refcfg (
	id	varchar(40)	not null,
	refine_config_id	varchar(40)	not null
,constraint dcs_catinf_rfcfg_p primary key (id))


create table dcs_catalog_refcfg (
	id	varchar(40)	not null,
	refine_config_id	varchar(40)	null
,constraint dcs_cata_rfcfg_p primary key (id))



go
