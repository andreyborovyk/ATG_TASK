



create table srch_tw_set (
	tw_set_id	varchar(40)	not null,
	tw_set_name	varchar(200)	not null,
	tw_set_desc	varchar(1000)	null,
	tw_set_language	varchar(200)	not null,
	tw_set_readonly	tinyint	null,
	tw_set_last_mod	datetime	not null
,constraint srch_tw_set1_p primary key (tw_set_id)
,constraint srch_tw_set1_u unique (tw_set_name))


create table srch_tw_term (
	tw_term_id	varchar(40)	not null,
	tw_term_set_id	varchar(40)	null,
	tw_term_name	varchar(200)	not null,
	tw_term_weight	smallint	not null
,constraint srch_tw_term1_p primary key (tw_term_id)
,constraint srch_tw_set1_f foreign key (tw_term_set_id) references srch_tw_set (tw_set_id))

create index srch_tw_set1_x on srch_tw_term (tw_term_set_id)
create index srch_tw_term1_i on srch_tw_term (tw_term_name,tw_term_set_id)


go
