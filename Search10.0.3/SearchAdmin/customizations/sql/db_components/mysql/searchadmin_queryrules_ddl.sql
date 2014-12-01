



create table srch_qr_pt_node (
	qr_pt_id	varchar(40)	not null,
	qr_pt_type	integer	not null
,constraint srch_qr_pt_p primary key (qr_pt_id));


create table srch_qr_sets (
	qr_set_id	varchar(40)	not null,
	qr_set_name	nvarchar(200)	not null,
	qr_set_dsc	nvarchar(1000)	null,
	qr_set_lst_mdf	date	null
,constraint srch_qr_set_p primary key (qr_set_id)
,constraint qr_set_name_u unique (qr_set_name));


create table srch_qr_groups (
	qr_grp_id	varchar(40)	not null,
	qr_grp_pt_id	varchar(40)	null,
	qr_grp_seq	integer	null,
	qr_grp_name	nvarchar(200)	not null
,constraint srch_qr_grp_p primary key (qr_grp_id));


create table srch_qr (
	qr_id	varchar(40)	not null,
	qr_prn_grp_id	varchar(40)	null,
	qr_seq	integer	null,
	qr_name	nvarchar(200)	not null
,constraint srch_qr_p primary key (qr_id));


create table srch_qr_patterns (
	qr_ptrn_id	varchar(40)	not null,
	qr_ptrn_qr_id	varchar(40)	null,
	qr_ptrn_is_enbl	tinyint	not null,
	qr_ptrn_name	nvarchar(200)	not null,
	qr_ptrn_weight	integer	null,
	qr_ptrn_grp	nvarchar(200)	null,
	qr_ptrn_lang	varchar(100)	not null
,constraint srch_qr_ptrn_p primary key (qr_ptrn_id));


create table srch_qr_actions (
	qr_act_id	varchar(40)	not null,
	qr_act_qr_id	varchar(40)	null,
	qr_act_is_enbl	tinyint	not null,
	qr_act_name	nvarchar(200)	not null,
	qr_act_lang	varchar(100)	not null
,constraint srch_qr_act_p primary key (qr_act_id));


create table srch_qr_gl_ms (
	qr_gl_m_id	varchar(40)	not null,
	qr_gl_m_name	nvarchar(40)	not null,
	qr_gl_m_def	nvarchar(900)	not null,
	qr_gl_m_type	integer	not null
,constraint srch_qr_gl_m_p primary key (qr_gl_m_id)
,constraint srch_qr_gl_m_u unique (qr_gl_m_name,qr_gl_m_type));

commit;


