



create table srch_tpo_sets (
	tpo_set_id	varchar2(40)	not null,
	tpo_set_name	varchar2(254)	not null,
	tpo_set_desc	varchar2(254)	null,
	tpo_set_created	date	null,
	tpo_set_date	date	null,
	tpo_set_level	number(10)	not null,
	tpo_set_project_id	varchar2(40)	null
,constraint srch_tpo_sets_p primary key (tpo_set_id));

create index srch_tpo_sets_i1 on srch_tpo_sets (tpo_set_level);
create index srch_tpo_sets_i2 on srch_tpo_sets (tpo_set_project_id);

create table srch_tpo_opts (
	tpo_opt_id	varchar2(40)	not null,
	tpo_opt_name	varchar2(254)	not null,
	tpo_opt_set_id	varchar2(40)	not null
,constraint srch_tpo_opts_p primary key (tpo_opt_id));


create table srch_tpo_values (
	tpo_value_opt_id	varchar2(40)	not null,
	tpo_value_item	varchar2(100)	null,
	tpo_value_seq	number(10)	not null
,constraint srch_tpo_values_p primary key (tpo_value_opt_id,tpo_value_seq));




