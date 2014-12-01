



create table srch_dic_tr_pt (
	dic_tr_pt_id	varchar2(40)	not null,
	dic_tr_pt_type	number(10)	not null
,constraint srch_dic_tr_pt_1_p primary key (dic_tr_pt_id));


create table srch_dic_dict (
	dic_dict_id	varchar2(40)	not null,
	dic_dict_name	varchar2(200)	not null,
	dic_dict_desc	varchar2(1000)	null,
	dic_dict_language	varchar2(200)	not null,
	dic_dict_load_mode	varchar2(200)	not null,
	dict_adapter_name	varchar2(200)	null,
	dic_dict_max_exp	number(10)	null,
	dic_dict_last_mod	date	not null
,constraint srch_dic_dict1_p primary key (dic_dict_id)
,constraint srch_dic_dict1_u unique (dic_dict_name)
,constraint srch_dic_tr_pt1_f foreign key (dic_dict_id) references srch_dic_tr_pt (dic_tr_pt_id));


create table srch_dic_trm (
	dic_trm_id	varchar2(40)	not null,
	dic_trm_pt_id	varchar2(40)	null,
	dic_trm_seq	number(10)	null,
	dic_trm_name	varchar2(200)	not null,
	dic_trm_propagate	number(1)	not null,
	dic_trm_spch_part	varchar2(200)	not null,
	dic_trm_phr_type	varchar2(40)	not null,
	dic_trm_weight	number(5)	null,
	dic_trm_norm_name	varchar2(200)	null
,constraint srch_dic_trm1_p primary key (dic_trm_id)
,constraint srch_dic_tr_pt2_f foreign key (dic_trm_id) references srch_dic_tr_pt (dic_tr_pt_id)
,constraint srch_dic_tr_pt3_f foreign key (dic_trm_pt_id) references srch_dic_tr_pt (dic_tr_pt_id));

create index srch_dic_tr_pt3_x on srch_dic_trm (dic_trm_pt_id);
create index srch_dic_trm_x1 on srch_dic_trm (dic_trm_norm_name);

create table srch_dic_syn (
	dic_syn_id	varchar2(40)	not null,
	dic_syn_pt_id	varchar2(40)	null,
	dic_syn_name	varchar2(200)	not null,
	dic_syn_relation	varchar2(200)	not null,
	dic_syn_spch_part	varchar2(200)	null,
	dic_syn_phr_type	varchar2(40)	not null,
	dic_syn_language	varchar2(200)	null,
	dic_syn_weight	number(5)	null,
	dic_syn_norm_name	varchar2(200)	null
,constraint srch_dic_syn1_p primary key (dic_syn_id));

create index srch_dic_syn_x1 on srch_dic_syn (dic_syn_norm_name);



