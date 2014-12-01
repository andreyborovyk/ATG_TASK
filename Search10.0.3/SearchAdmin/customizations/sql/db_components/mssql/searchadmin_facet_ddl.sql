



create table srch_facet_base (
	facet_base_id	varchar(40)	not null,
	facet_base_type	integer	not null
,constraint srch_facet_base_p primary key (facet_base_id))


create table srch_facet_set (
	facet_set_id	varchar(40)	not null,
	facet_set_name	varchar(254)	not null,
	facet_set_desc	varchar(254)	null,
	facet_set_last_mod	datetime	not null
,constraint srch_facet_set_p primary key (facet_set_id)
,constraint srch_facet_set_u unique (facet_set_name))


create table srch_facet_set_mapping (
	facet_set_id	varchar(40)	not null,
	facet_set_mapping_id	varchar(40)	not null,
	facet_set_mapping	varchar(254)	not null,
	sequence_num	integer	null
,constraint srch_fs_mapping_p primary key (facet_set_mapping_id))


create table srch_facet_item (
	facet_item_id	varchar(40)	not null,
	facet_item_parent_id	varchar(40)	null,
	facet_item_set_id	varchar(40)	null,
	facet_item_name	varchar(254)	not null,
	facet_item_prop	varchar(254)	not null,
	facet_item_type	integer	not null,
	facet_item_levels	integer	null,
	facet_item_range	integer	not null,
	facet_item_select	varchar(254)	null,
	facet_item_rng_num	integer	null,
	facet_item_rng_siz	integer	null,
	facet_item_min_val	integer	null,
	facet_item_valuedt	varchar(254)	null,
	facet_item_case	tinyint	null,
	facet_item_filter	tinyint	null,
	facet_item_prec	integer	null,
	facet_item_round	integer	null,
	facet_item_sort	integer	not null,
	facet_item_pres	varchar(254)	null
,constraint srch_facet_item_p primary key (facet_item_id))

create index srch_facet_item_i on srch_facet_item (facet_item_name,facet_item_set_id)

create table srch_facet_spec (
	facet_spec_item_id	varchar(40)	not null,
	facet_spec_idx	varchar(40)	not null,
	facet_spec_value	varchar(254)	not null
,constraint srch_facet_spec_p primary key (facet_spec_item_id,facet_spec_idx))


create table srch_facet_exc (
	facet_exc_item_id	varchar(40)	not null,
	facet_exc_idx	varchar(40)	not null,
	facet_exc_value	varchar(254)	not null
,constraint srch_facet_exc_p primary key (facet_exc_item_id,facet_exc_idx))


create table srch_facet_rngs (
	facet_rngs_item_id	varchar(40)	not null,
	facet_rngs_idx	varchar(40)	not null,
	facet_rngs_value	varchar(254)	not null
,constraint srch_facet_rngs_p primary key (facet_rngs_item_id,facet_rngs_idx))



go
