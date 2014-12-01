



create table srch_tw_set (
	tw_set_id	varchar2(40)	not null,
	tw_set_name	varchar2(200)	not null,
	tw_set_desc	varchar2(1000)	null,
	tw_set_language	varchar2(200)	not null,
	tw_set_readonly	number(1)	null,
	tw_set_last_mod	date	not null
,constraint srch_tw_set1_p primary key (tw_set_id)
,constraint srch_tw_set1_u unique (tw_set_name));


create table srch_tw_term (
	tw_term_id	varchar2(40)	not null,
	tw_term_set_id	varchar2(40)	null,
	tw_term_name	varchar2(200)	not null,
	tw_term_weight	number(5)	not null
,constraint srch_tw_term1_p primary key (tw_term_id)
,constraint srch_tw_set1_f foreign key (tw_term_set_id) references srch_tw_set (tw_set_id));

create index srch_tw_set1_x on srch_tw_term (tw_term_set_id);
create index srch_tw_term1_i on srch_tw_term (tw_term_name,tw_term_set_id);



