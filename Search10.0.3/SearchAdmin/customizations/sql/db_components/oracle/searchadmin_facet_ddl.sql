



create table srch_facet_base (
	facet_base_id	varchar2(40)	not null,
	facet_base_type	number(10)	not null
,constraint srch_facet_base_p primary key (facet_base_id));


create table srch_facet_set (
	facet_set_id	varchar2(40)	not null,
	facet_set_name	varchar2(254)	not null,
	facet_set_desc	varchar2(254)	null,
	facet_set_last_mod	date	not null
,constraint srch_facet_set_p primary key (facet_set_id)
,constraint srch_facet_set_u unique (facet_set_name));


create table srch_facet_set_mapping (
	facet_set_id	varchar2(40)	not null,
	facet_set_mapping_id	varchar2(40)	not null,
	facet_set_mapping	varchar2(254)	not null,
	sequence_num	number(10)	null
,constraint srch_fs_mapping_p primary key (facet_set_mapping_id));


create table srch_facet_item (
	facet_item_id	varchar2(40)	not null,
	facet_item_parent_id	varchar2(40)	null,
	facet_item_set_id	varchar2(40)	null,
	facet_item_name	varchar2(254)	not null,
	facet_item_prop	varchar2(254)	not null,
	facet_item_type	number(10)	not null,
	facet_item_levels	number(10)	null,
	facet_item_range	number(10)	not null,
	facet_item_select	varchar2(254)	null,
	facet_item_rng_num	number(10)	null,
	facet_item_rng_siz	number(10)	null,
	facet_item_min_val	number(10)	null,
	facet_item_valuedt	varchar2(254)	null,
	facet_item_case	number(1)	null,
	facet_item_filter	number(1)	null,
	facet_item_prec	number(10)	null,
	facet_item_round	number(10)	null,
	facet_item_sort	number(10)	not null,
	facet_item_pres	varchar2(254)	null
,constraint srch_facet_item_p primary key (facet_item_id));

create index srch_facet_item_i on srch_facet_item (facet_item_name,facet_item_set_id);

create table srch_facet_spec (
	facet_spec_item_id	varchar2(40)	not null,
	facet_spec_idx	varchar2(40)	not null,
	facet_spec_value	varchar2(254)	not null
,constraint srch_facet_spec_p primary key (facet_spec_item_id,facet_spec_idx));


create table srch_facet_exc (
	facet_exc_item_id	varchar2(40)	not null,
	facet_exc_idx	varchar2(40)	not null,
	facet_exc_value	varchar2(254)	not null
,constraint srch_facet_exc_p primary key (facet_exc_item_id,facet_exc_idx));


create table srch_facet_rngs (
	facet_rngs_item_id	varchar2(40)	not null,
	facet_rngs_idx	varchar2(40)	not null,
	facet_rngs_value	varchar2(254)	not null
,constraint srch_facet_rngs_p primary key (facet_rngs_item_id,facet_rngs_idx));




