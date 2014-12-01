



create table srch_adv_conf (
	id	varchar2(40)	not null,
	conf	clob	null
,constraint srch_adv_conf_p primary key (id));


create table srch_project (
	id	varchar2(40)	not null,
	srch_idx_id	varchar2(40)	null,
	cur_srch_idx_id	varchar2(40)	null,
	name	varchar2(254)	not null,
	description	varchar2(254)	null,
	min_inact_idxs	number(5)	null,
	preserve_cnt	number(5)	null,
	preserve_age	number(5)	null,
	srch_adv_c_id	varchar2(40)	null,
	force_full_index	number(1)	null,
	cached_cust	blob	null,
	no_content	number(1)	default 0 not null
,constraint srch_project_p primary key (id)
,constraint srch_project_u1 unique (name)
,constraint srch_project_fk2 foreign key (srch_adv_c_id) references srch_adv_conf (id));

create index srch_project_x2 on srch_project (srch_adv_c_id);

create table srch_reg_impl (
	id	varchar2(40)	not null,
	intface	varchar2(254)	null,
	classname	varchar2(254)	null,
	classloader	varchar2(254)	null,
	name	varchar2(254)	null,
	description	varchar2(254)	null
,constraint srch_reg_impl_p primary key (id));


create table srch_css_id_to_targ_ids (
	id	varchar2(40)	not null,
	srch_project_id	varchar2(40)	not null,
	srch_content_ss_id	varchar2(40)	not null,
	srch_idx_id	varchar2(40)	not null,
	srch_shared_lp_id	varchar2(40)	not null
,constraint srch_css_to_targ_p primary key (id)
,constraint srch_css_2_trg_fk1 foreign key (srch_project_id) references srch_project (id));

create index srch_csidtargid1_x on srch_css_id_to_targ_ids (srch_project_id);

create table srch_content (
	id	varchar2(40)	not null,
	name	varchar2(254)	null,
	srch_content_id	varchar2(40)	null,
	srch_adv_c_id	varchar2(40)	null,
	handservice	varchar2(254)	null
,constraint srch_content_p primary key (id)
,constraint srch_content_fk1 foreign key (srch_content_id) references srch_content (id)
,constraint srch_content_fk2 foreign key (srch_adv_c_id) references srch_adv_conf (id));

create index srch_content_x1 on srch_content (srch_content_id);
create index srch_content_x2 on srch_content (srch_adv_c_id);

create table srch_content_ss (
	id	varchar2(40)	not null,
	shared_id	varchar2(40)	not null,
	name	varchar2(254)	null,
	last_modified	timestamp	null,
	srch_content_id	varchar2(40)	not null,
	srch_content_ss_id	varchar2(40)	null,
	structured	number(10)	null,
	force_con_type	number(1)	null,
	srch_log_part_id	varchar2(40)	null,
	srch_adv_c_id	varchar2(40)	null,
	doc_set_subpath	varchar2(254)	null
,constraint srch_content_ss_p primary key (id)
,constraint srch_cont_ss_fk1 foreign key (srch_adv_c_id) references srch_adv_conf (id)
,constraint srch_cont_ss_fk2 foreign key (srch_content_id) references srch_content (id)
,constraint srch_cont_ss_fk3 foreign key (srch_content_ss_id) references srch_content_ss (id));

create index srch_cont_ss_x1 on srch_content_ss (srch_adv_c_id);
create index srch_cont_ss_x2 on srch_content_ss (srch_content_id);
create index srch_cont_ss_x3 on srch_content_ss (srch_content_ss_id);

create table srch_content_ss_mm (
	srch_content_ss_id	varchar2(40)	not null,
	members	varchar2(254)	not null
,constraint srch_cont_ss_mm_p primary key (srch_content_ss_id,members)
,constraint srch_cont_ss_mm_fk foreign key (srch_content_ss_id) references srch_content_ss (id));


create table srch_platform (
	id	varchar2(40)	not null,
	platformid	number(19)	null,
	maxpartsize	number(19)	null
,constraint srch_platform_p primary key (id));


create table srch_act_idx_v (
	id	varchar2(40)	not null,
	srch_platform_id	varchar2(40)	null,
	majorversion	number(19)	null,
	minorversion	number(19)	null,
	charsize	number(10)	null
,constraint srch_act_idx_v_p primary key (id)
,constraint srch_act_idx_v_f1 foreign key (srch_platform_id) references srch_platform (id));

create index srch_act_idx_v_x1 on srch_act_idx_v (srch_platform_id);

create table srch_sync_task_type (
	id	varchar2(40)	not null,
	name	varchar2(254)	not null,
	enum	number(5)	null
,constraint srch_task_type_p primary key (id));


create table srch_sync_step_type (
	id	varchar2(40)	not null,
	name	varchar2(254)	not null,
	enum	number(5)	null,
	super_step_enum	number(5)	null
,constraint srch_step_type_p primary key (id));


create table srch_base_sync_task (
	id	varchar2(40)	not null,
	base_type	number(5)	not null,
	type_id	varchar2(40)	not null,
	subname	varchar2(254)	null,
	srch_adv_c_id	varchar2(40)	null,
	is_all_css	number(1)	default 0 null,
	is_all_custom_data	number(1)	default 0 null,
	is_pause	number(1)	default 0 not null,
	src_srch_idx_id	varchar2(40)	null,
	srch_project_id	varchar2(40)	not null
,constraint srch_bsynctask_p primary key (id)
,constraint srch_bsynctask_f1 foreign key (srch_adv_c_id) references srch_adv_conf (id)
,constraint srch_bsynctask_f2 foreign key (srch_project_id) references srch_project (id)
,constraint srch_synctaskd_f3 foreign key (type_id) references srch_sync_task_type (id));

create index srch_bsynctask_x1 on srch_base_sync_task (srch_adv_c_id);
create index srch_bsynctask_x2 on srch_base_sync_task (srch_project_id);
create index srch_synctaskd_x3 on srch_base_sync_task (type_id);

create table srch_sync_task (
	id	varchar2(40)	not null,
	base_sync_task_id	varchar2(40)	not null,
	error_count	number(10)	null,
	index_id	varchar2(40)	null,
	start_tstamp	timestamp	not null,
	end_tstamp	timestamp	null
,constraint srch_sync_task_p primary key (id)
,constraint srch_sync_task_f2 foreign key (base_sync_task_id) references srch_base_sync_task (id));

create index srch_sync_task_x2 on srch_sync_task (base_sync_task_id);

create table srch_sync_step_option (
	id	varchar2(40)	not null,
	name	varchar2(254)	not null,
	option_type	number(5)	not null
,constraint srch_syncstepopt_p primary key (id));


create table srch_base_sync_step (
	id	varchar2(40)	not null,
	base_type	number(5)	not null,
	step_type_id	varchar2(40)	not null,
	step_option_id	varchar2(40)	not null
,constraint srch_bsyncstep_p primary key (id)
,constraint srch_bsyncstep_f1 foreign key (step_type_id) references srch_sync_step_type (id)
,constraint srch_bsyncstep_f2 foreign key (step_option_id) references srch_sync_step_option (id));

create index srch_bsyncstep_x1 on srch_base_sync_step (step_type_id);
create index srch_bsyncstep_x2 on srch_base_sync_step (step_option_id);

create table srch_sync_task_step_m (
	task_id	varchar2(40)	not null,
	idx	number(10)	not null,
	step_id	varchar2(40)	not null
,constraint srch_tskd_stp_m_p primary key (task_id,idx)
,constraint srch_task_stp_m_f1 foreign key (task_id) references srch_base_sync_task (id)
,constraint srch_task_stp_m_f2 foreign key (step_id) references srch_base_sync_step (id));

create index srch_task_stp_m_x2 on srch_sync_task_step_m (step_id);

create table srch_sync_task_type_step_m (
	task_type_id	varchar2(40)	not null,
	idx	number(10)	not null,
	step_type_id	varchar2(40)	not null
,constraint srch_tasktype_m_p primary key (task_type_id,idx)
,constraint srch_tasktype_m_f1 foreign key (task_type_id) references srch_sync_task_type (id)
,constraint srch_tasktype_m_f2 foreign key (step_type_id) references srch_sync_step_type (id));

create index srch_tasktype_m_x2 on srch_sync_task_type_step_m (step_type_id);

create table srch_sync_step_type_option_m (
	step_type_id	varchar2(40)	not null,
	idx	number(10)	not null,
	step_option_id	varchar2(40)	not null
,constraint srch_step_typeo_p primary key (step_type_id,idx)
,constraint srch_stp_type_o_f1 foreign key (step_type_id) references srch_sync_step_type (id)
,constraint srch_stp_type_o_f2 foreign key (step_option_id) references srch_sync_step_option (id));

create index srch_stp_type_o_x2 on srch_sync_step_type_option_m (step_option_id);

create table srch_sync_task_def (
	id	varchar2(40)	not null,
	last_modified	timestamp	null,
	base_sync_task_id	varchar2(40)	not null,
	enabled	number(1)	null,
	description	varchar2(254)	null,
	schedule	varchar2(254)	null,
	depl_type	varchar2(20)	null,
	depl_target	varchar2(255)	null
,constraint srch_synctaskdef_p primary key (id)
,constraint srch_sync_base_f2 foreign key (base_sync_task_id) references srch_base_sync_task (id)
,constraint srch_sync_task_chk check (enabled IN (0, 1)));

create index srch_sync_base_x2 on srch_sync_task_def (base_sync_task_id);

create table srch_sync_step_def (
	id	varchar2(40)	not null,
	task_definition_id	varchar2(40)	not null
,constraint srch_syncstepdef_p primary key (id)
,constraint srch_stepdef_f1 foreign key (task_definition_id) references srch_sync_task_def (id));

create index srch_stepdef_x1 on srch_sync_step_def (task_definition_id);

create table srch_hist_step (
	id	varchar2(40)	not null,
	step_definition_id	varchar2(40)	null,
	status_id	number(5)	not null,
	error_count	number(5)	not null,
	start_time	timestamp	null,
	end_time	timestamp	null
,constraint srch_hist_step_p primary key (id));


create table srch_step_error (
	id	varchar2(40)	not null,
	hist_step_id	varchar2(40)	not null,
	occurred_time	timestamp	not null,
	content_id	clob	null,
	error_type	number(5)	not null,
	error_message	clob	null,
	cause	clob	null,
	engine_resp	clob	null
,constraint srch_step_err_p primary key (id)
,constraint srch_hist_step_fk1 foreign key (hist_step_id) references srch_hist_step (id));

create index srch_hist_step_x2 on srch_step_error (hist_step_id);

create table srch_history (
	id	varchar2(40)	not null,
	act_part_size	number(12,0)	null,
	est_part_size	number(12,0)	null,
	est_psiz_uppr	number(12,0)	null,
	est_psiz_lowr	number(12,0)	null,
	item_count	number(10)	null,
	start_time	timestamp	null,
	end_time	timestamp	null,
	succeeded	number(1)	null
,constraint srch_history_p primary key (id));


create table srch_hist_stepm (
	srch_history_id	varchar2(40)	not null,
	srch_hist_step_id	varchar2(40)	not null,
	idx	number(10)	not null
,constraint srch_hist_stepm_p primary key (srch_history_id,idx)
,constraint srch_hist_stepm_f1 foreign key (srch_history_id) references srch_history (id)
,constraint srch_hist_stepm_f2 foreign key (srch_hist_step_id) references srch_hist_step (id));

create index srch_hist_stepm_x1 on srch_hist_stepm (srch_hist_step_id);

create table srch_env_p_m (
	srch_project_id	varchar2(40)	not null,
	search_env_id	varchar2(40)	not null
,constraint srch_env_p_m_p primary key (srch_project_id,search_env_id)
,constraint srch_env_p_m_f1 foreign key (srch_project_id) references srch_project (id));


create table srch_sorg (
	id	varchar2(40)	not null,
	name	varchar2(254)	null,
	parent	varchar2(254)	null
,constraint srch_sorg_p primary key (id));


create table srch_env_sorg_m (
	srch_project_id	varchar2(40)	not null,
	secure_org_id	varchar2(40)	not null
,constraint srch_env_sorg_m_p primary key (srch_project_id,secure_org_id)
,constraint srch_env_sorgm_f1 foreign key (srch_project_id) references srch_project (id)
,constraint srch_env_sorgm_f2 foreign key (secure_org_id) references srch_sorg (id));

create index srch_env_sorgm_x2 on srch_env_sorg_m (secure_org_id);

create table srch_sync_content_ss_m (
	sync_task_id	varchar2(40)	not null,
	content_ss_id	varchar2(40)	not null
,constraint srch_sync_cntss_p primary key (sync_task_id,content_ss_id)
,constraint srch_sync_cntss_f1 foreign key (sync_task_id) references srch_base_sync_task (id)
,constraint srch_sync_cntss_f2 foreign key (content_ss_id) references srch_content_ss (id));

create index srch_sync_cntss_x2 on srch_sync_content_ss_m (content_ss_id);

create table srch_sync_lp_m (
	sync_task_id	varchar2(40)	not null,
	log_part_id	varchar2(40)	not null
,constraint srch_sync_lp_p primary key (sync_task_id,log_part_id)
,constraint srch_sync_lp_f1 foreign key (sync_task_id) references srch_base_sync_task (id));

create index srch_sync_lp_x1 on srch_sync_lp_m (sync_task_id);

create table srch_sync_env_m (
	sync_task_id	varchar2(40)	not null,
	search_env_id	varchar2(40)	not null
,constraint srch_sync_env_m_p primary key (sync_task_id,search_env_id)
,constraint srch_sync_env_m_f1 foreign key (sync_task_id) references srch_base_sync_task (id));


create table srch_custom_ds (
	id	varchar2(40)	not null,
	shared_id	varchar2(40)	not null,
	select_type	number(10)	null,
	srch_content_id	varchar2(40)	not null,
	srch_adv_c_id	varchar2(40)	null,
	custom_dt	varchar2(254)	not null
,constraint srch_custom_ds_p primary key (id)
,constraint srch_custom_ds_fk1 foreign key (srch_content_id) references srch_content (id)
,constraint srch_custom_ds_fk6 foreign key (srch_adv_c_id) references srch_adv_conf (id));

create index srch_custom_ds_x1 on srch_custom_ds (srch_content_id);
create index srch_custom_ds_x6 on srch_custom_ds (srch_adv_c_id);

create table srch_project_custom_ds (
	id	varchar2(40)	not null,
	srch_project_id	varchar2(40)	null
,constraint srch_prjcust_ds_p primary key (id)
,constraint srch_prjcust_ds_f1 foreign key (id) references srch_custom_ds (id)
,constraint srch_prjcust_ds_f2 foreign key (srch_project_id) references srch_project (id));

create index srch_prjcust_ds_x2 on srch_project_custom_ds (srch_project_id);

create table srch_sync_task_custom_ds (
	id	varchar2(40)	not null,
	srch_sync_task_id	varchar2(40)	null
,constraint srch_snc_tsk_cst_p primary key (id)
,constraint srch_sc_tsk_cst_f1 foreign key (id) references srch_custom_ds (id)
,constraint srch_sc_tsk_cst_f2 foreign key (srch_sync_task_id) references srch_base_sync_task (id));

create index srch_sc_tsk_cst_x2 on srch_sync_task_custom_ds (srch_sync_task_id);

create table srch_css_custom_ds (
	id	varchar2(40)	not null,
	srch_content_ss_id	varchar2(40)	null
,constraint srch_csscust_ds_p primary key (id)
,constraint srch_csscust_ds_f1 foreign key (id) references srch_custom_ds (id)
,constraint srch_csscust_ds_f2 foreign key (srch_content_ss_id) references srch_content_ss (id));

create index srch_csscust_ds_x2 on srch_css_custom_ds (srch_content_ss_id);

create table srch_project_custom_dsm (
	srch_project_id	varchar2(40)	not null,
	idx	number(10)	not null,
	srch_project_custom_ds_id	varchar2(40)	null
,constraint srch_prjcst_dsm_p primary key (srch_project_id,idx)
,constraint srch_prjcst_dsm_f1 foreign key (srch_project_id) references srch_project (id)
,constraint srch_prjcst_dsm_f2 foreign key (srch_project_custom_ds_id) references srch_project_custom_ds (id));

create index srch_prjcst_x2 on srch_project_custom_dsm (srch_project_custom_ds_id);

create table srch_css_custom_dsm (
	srch_content_ss_id	varchar2(40)	not null,
	idx	number(10)	not null,
	srch_csscust_ds_id	varchar2(40)	null
,constraint srch_csscst_dsm_p primary key (srch_content_ss_id,idx)
,constraint srch_csscst_dsm_f1 foreign key (srch_content_ss_id) references srch_content_ss (id)
,constraint srch_csscst_dsm_f2 foreign key (srch_csscust_ds_id) references srch_css_custom_ds (id));

create index srch_csscst_x2 on srch_css_custom_dsm (srch_csscust_ds_id);

create table srch_custom_ds_mm (
	srch_custom_ds_id	varchar2(40)	not null,
	idx	number(10)	not null,
	sub	varchar2(254)	not null
,constraint srch_cust_ds_mm_p primary key (srch_custom_ds_id,idx)
,constraint srch_cust_ds_mm_fk foreign key (srch_custom_ds_id) references srch_custom_ds (id));


create table srch_file (
	id	varchar2(40)	not null,
	srch_content_id	varchar2(40)	not null,
	name	clob	not null,
	namehash	number(10)	not null,
	tstamp	varchar2(40)	null,
	gnumber	varchar2(40)	null
,constraint srch_file_p primary key (id)
,constraint srch_file_f1 foreign key (srch_content_id) references srch_content (id));

create index srch_file_x2 on srch_file (srch_content_id,namehash);

create table srch_queue (
	id	varchar2(40)	not null,
	base_sync_task_id	varchar2(40)	not null,
	qtime	timestamp	not null
,constraint srch_queue_p primary key (id));


create table srch_audit (
	id	varchar2(40)	not null,
	srch_sync_task_id	varchar2(40)	not null,
	el_name	varchar2(100)	not null,
	el_value	number(19)	not null
,constraint srch_audit_p primary key (id)
,constraint srch_audit_f1 foreign key (srch_sync_task_id) references srch_sync_task (id));

create index srch_audit_x1 on srch_audit (srch_sync_task_id);



