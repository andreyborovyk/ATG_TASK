


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/Search/common/sql/commerce_refinement_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements, which will configure your database for use with the new catalog-extended refinement repository

create table dcs_refine_config (
	id	varchar(40)	not null,
	inherit_global	tinyint	not null,
	inherit_category	tinyint	not null,
	can_child_inherit	tinyint	not null,
	is_global	tinyint	not null
,constraint dcs_refine_cfg_p primary key (id));


create table dcs_refcfg_genels (
	id	varchar(40)	not null,
	seq	integer	not null,
	refine_element	varchar(40)	not null
,constraint dcs_rfcfg_genels_p primary key (id,seq));


create table dcs_cat_refcfg (
	id	varchar(40)	not null,
	refine_config_id	varchar(40)	null
,constraint dcs_cat_refcfg_p primary key (id));

commit;


