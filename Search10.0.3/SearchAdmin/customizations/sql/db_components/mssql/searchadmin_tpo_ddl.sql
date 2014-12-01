



create table srch_tpo_sets (
	tpo_set_id	varchar(40)	not null,
	tpo_set_name	varchar(254)	not null,
	tpo_set_desc	varchar(254)	null,
	tpo_set_created	datetime	null,
	tpo_set_date	datetime	null,
	tpo_set_level	integer	not null,
	tpo_set_project_id	varchar(40)	null
,constraint srch_tpo_sets_p primary key (tpo_set_id))

create index srch_tpo_sets_i1 on srch_tpo_sets (tpo_set_level)
create index srch_tpo_sets_i2 on srch_tpo_sets (tpo_set_project_id)

create table srch_tpo_opts (
	tpo_opt_id	varchar(40)	not null,
	tpo_opt_name	varchar(254)	not null,
	tpo_opt_set_id	varchar(40)	not null
,constraint srch_tpo_opts_p primary key (tpo_opt_id))


create table srch_tpo_values (
	tpo_value_opt_id	varchar(40)	not null,
	tpo_value_item	varchar(100)	null,
	tpo_value_seq	integer	not null
,constraint srch_tpo_values_p primary key (tpo_value_opt_id,tpo_value_seq))



go
