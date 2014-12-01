



create table srch_qr_pt_node (
	qr_pt_id	varchar2(40)	not null,
	qr_pt_type	number(10)	not null
,constraint srch_qr_pt_p primary key (qr_pt_id));


create table srch_qr_sets (
	qr_set_id	varchar2(40)	not null,
	qr_set_name	varchar2(200)	not null,
	qr_set_dsc	varchar2(1000)	null,
	qr_set_lst_mdf	date	null
,constraint srch_qr_set_p primary key (qr_set_id)
,constraint qr_set_name_u unique (qr_set_name));


create table srch_qr_groups (
	qr_grp_id	varchar2(40)	not null,
	qr_grp_pt_id	varchar2(40)	null,
	qr_grp_seq	number(10)	null,
	qr_grp_name	varchar2(200)	not null
,constraint srch_qr_grp_p primary key (qr_grp_id));


create table srch_qr (
	qr_id	varchar2(40)	not null,
	qr_prn_grp_id	varchar2(40)	null,
	qr_seq	number(10)	null,
	qr_name	varchar2(200)	not null
,constraint srch_qr_p primary key (qr_id));


create table srch_qr_patterns (
	qr_ptrn_id	varchar2(40)	not null,
	qr_ptrn_qr_id	varchar2(40)	null,
	qr_ptrn_is_enbl	number(1)	not null,
	qr_ptrn_name	varchar2(200)	not null,
	qr_ptrn_weight	number(10)	null,
	qr_ptrn_grp	varchar2(200)	null,
	qr_ptrn_lang	varchar2(100)	not null
,constraint srch_qr_ptrn_p primary key (qr_ptrn_id));


create table srch_qr_actions (
	qr_act_id	varchar2(40)	not null,
	qr_act_qr_id	varchar2(40)	null,
	qr_act_is_enbl	number(1)	not null,
	qr_act_name	varchar2(200)	not null,
	qr_act_lang	varchar2(100)	not null
,constraint srch_qr_act_p primary key (qr_act_id));


create table srch_qr_gl_ms (
	qr_gl_m_id	varchar2(40)	not null,
	qr_gl_m_name	varchar2(40)	not null,
	qr_gl_m_def	varchar2(900)	not null,
	qr_gl_m_type	number(10)	not null
,constraint srch_qr_gl_m_p primary key (qr_gl_m_id)
,constraint srch_qr_gl_m_u unique (qr_gl_m_name,qr_gl_m_type));




