



create table srch_dic_tr_pt (
	dic_tr_pt_id	varchar(40)	not null,
	dic_tr_pt_type	integer	not null
,constraint srch_dic_tr_pt_1_p primary key (dic_tr_pt_id))


create table srch_dic_dict (
	dic_dict_id	varchar(40)	not null,
	dic_dict_name	varchar(200)	not null,
	dic_dict_desc	varchar(1000)	null,
	dic_dict_language	varchar(200)	not null,
	dic_dict_load_mode	varchar(200)	not null,
	dict_adapter_name	varchar(200)	null,
	dic_dict_max_exp	integer	null,
	dic_dict_last_mod	datetime	not null
,constraint srch_dic_dict1_p primary key (dic_dict_id)
,constraint srch_dic_dict1_u unique (dic_dict_name)
,constraint srch_dic_tr_pt1_f foreign key (dic_dict_id) references srch_dic_tr_pt (dic_tr_pt_id))


create table srch_dic_trm (
	dic_trm_id	varchar(40)	not null,
	dic_trm_pt_id	varchar(40)	null,
	dic_trm_seq	integer	null,
	dic_trm_name	varchar(200)	not null,
	dic_trm_propagate	tinyint	not null,
	dic_trm_spch_part	varchar(200)	not null,
	dic_trm_phr_type	varchar(40)	not null,
	dic_trm_weight	smallint	null,
	dic_trm_norm_name	varchar(200)	null
,constraint srch_dic_trm1_p primary key (dic_trm_id)
,constraint srch_dic_tr_pt2_f foreign key (dic_trm_id) references srch_dic_tr_pt (dic_tr_pt_id)
,constraint srch_dic_tr_pt3_f foreign key (dic_trm_pt_id) references srch_dic_tr_pt (dic_tr_pt_id))

create index srch_dic_tr_pt3_x on srch_dic_trm (dic_trm_pt_id)
create index srch_dic_trm_x1 on srch_dic_trm (dic_trm_norm_name)

create table srch_dic_syn (
	dic_syn_id	varchar(40)	not null,
	dic_syn_pt_id	varchar(40)	null,
	dic_syn_name	varchar(200)	not null,
	dic_syn_relation	varchar(200)	not null,
	dic_syn_spch_part	varchar(200)	null,
	dic_syn_phr_type	varchar(40)	not null,
	dic_syn_language	varchar(200)	null,
	dic_syn_weight	smallint	null,
	dic_syn_norm_name	varchar(200)	null
,constraint srch_dic_syn1_p primary key (dic_syn_id))

create index srch_dic_syn_x1 on srch_dic_syn (dic_syn_norm_name)


go
