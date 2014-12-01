


--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/publishing_ddl.xml#2 $
--     The tables here are for the various parts of the epublishing system.  

create table epub_history (
	history_id	varchar2(40)	not null,
	profile	varchar2(40)	null,
	timestamp	timestamp	null,
	action	varchar2(255)	null,
	action_type	varchar2(255)	null,
	description	clob	null
,constraint history_pk primary key (history_id));

create index his_profile_idx on epub_history (profile);
create index his_timestamp_idx on epub_history (timestamp);
create index his_action_idx on epub_history (action);

create table epub_his_act_parm (
	history_id	varchar2(40)	not null,
	action_param	varchar2(255)	null,
	sequence_num	number(19)	not null
,constraint epub_his_ac_pk primary key (history_id,sequence_num)
,constraint epub_hisact_id_fk foreign key (history_id) references epub_history (history_id));


create table epub_taskinfo (
	taskinfo_id	varchar2(40)	not null,
	version	number(19)	not null,
	process_id	varchar2(40)	not null,
	process_name	varchar2(255)	not null,
	segment_name	varchar2(255)	not null,
	task_element_id	varchar2(255)	not null,
	acl	varchar2(2048)	null,
	priority	number(19)	null,
	owner_name	varchar2(255)	null,
	last_actor_name	varchar2(255)	null,
	last_action_date	timestamp	null,
	last_outcome_id	varchar2(255)	null
,constraint taskinfo_pk primary key (taskinfo_id));

create index taskinfo_pname_ix on epub_taskinfo (process_name);

create table epub_agent_trnprt (
	transport_id	varchar2(40)	not null,
	version	number(19)	not null,
	transport_type	number(1)	not null,
	jndi_name	varchar2(255)	null,
	rmi_uri	varchar2(255)	null);


create table epub_agent (
	agent_id	varchar2(40)	not null,
	version	number(19)	not null,
	creation_time	timestamp	null,
	display_name	varchar2(255)	null,
	description	varchar2(1024)	null,
	main_agent_id	varchar2(40)	null,
	transport	varchar2(40)	not null
,constraint target_agent_pk primary key (agent_id));


create table epub_target (
	target_id	varchar2(40)	not null,
	snapshot_name	varchar2(255)	null,
	version	number(19)	not null,
	creation_time	timestamp	null,
	main_target_id	varchar2(40)	null,
	display_name	varchar2(255)	null,
	description	varchar2(1024)	null,
	halted	number(1)	null,
	flag_agents	number(1)	null,
	target_type	number(1)	null
,constraint targets_pk primary key (target_id));


create table epub_tr_dest (
	target_id	varchar2(40)	not null,
	target_source	varchar2(100)	not null,
	target_destination	varchar2(100)	not null
,constraint epub_trd_id_pk primary key (target_id,target_source));


create table epub_topology (
	topology_id	varchar2(40)	not null,
	version	number(19)	not null,
	primary_tl	number(1)	null
,constraint topology_pk primary key (topology_id));


create table epub_tl_targets (
	topology_id	varchar2(40)	not null,
	target_id	varchar2(40)	not null,
	sequence_num	number(19)	not null
,constraint pr_tp_tr_pk primary key (topology_id,target_id));


create table epub_tr_agents (
	target_id	varchar2(40)	not null,
	agent_id	varchar2(40)	not null
,constraint pr_ag_pk primary key (target_id,agent_id)
,constraint pt_tr_pr_id_fk foreign key (target_id) references epub_target (target_id)
,constraint pt_ag_ag_id_fk foreign key (agent_id) references epub_agent (agent_id));

create index epub_tr_agents_x on epub_tr_agents (agent_id);

create table epub_princ_asset (
	agent_id	varchar2(40)	not null,
	principal_assets	varchar2(40)	not null
,constraint princ_asset_pk primary key (agent_id,principal_assets)
,constraint princ_aset_id_fk foreign key (agent_id) references epub_agent (agent_id));


create table epub_includ_asset (
	agent_id	varchar2(40)	not null,
	include_assets	varchar2(255)	not null
,constraint includ_asset_pk primary key (agent_id,include_assets)
,constraint includ_aset_id_fk foreign key (agent_id) references epub_agent (agent_id));


create table epub_exclud_asset (
	agent_id	varchar2(40)	not null,
	exclude_assets	varchar2(255)	not null
,constraint exclud_asset_pk primary key (agent_id,exclude_assets)
,constraint exclud_aset_id_fk foreign key (agent_id) references epub_agent (agent_id));


create table epub_dest_map (
	agent_id	varchar2(40)	not null,
	source	varchar2(255)	not null,
	destination	varchar2(255)	not null
,constraint dest_map_pk primary key (agent_id,source)
,constraint dest_map_id_fk foreign key (agent_id) references epub_agent (agent_id));


create table epub_project (
	project_id	varchar2(40)	not null,
	version	number(19)	not null,
	acl	varchar2(2048)	null,
	display_name	varchar2(255)	null,
	description	varchar2(1024)	null,
	creator	varchar2(40)	null,
	workspace	varchar2(255)	not null,
	workflow_id	varchar2(40)	null,
	checked_in	number(1)	null,
	locked	number(1)	null,
	editable	number(1)	null,
	status	number(19)	null,
	status_detail	varchar2(255)	null,
	checkin_date	timestamp	null,
	creation_date	timestamp	null,
	completion_date	timestamp	null
,constraint project_pk primary key (project_id)
,constraint pr_editable_chk check (editable in (0,1)));

create index pr_workspace_idx on epub_project (workspace);
create index pr_disp_name_idx on epub_project (display_name);
create index pr_creator_idx on epub_project (creator);
create index pr_creat_date_idx on epub_project (creation_date);
create index pr_comp_date_idx on epub_project (completion_date);

create table epub_prj_targt_ws (
	project_id	varchar2(40)	not null,
	target_id	varchar2(40)	not null,
	workspace_id	varchar2(40)	null
,constraint epub_prw_id_pk primary key (project_id,target_id));


create table epub_pr_tg_status (
	project_id	varchar2(40)	not null,
	target_id	varchar2(40)	not null,
	status_code	number(1)	null
,constraint epub_pr_ap_id_pk primary key (project_id,target_id));


create table epub_prj_tg_snsht (
	project_id	varchar2(40)	not null,
	target_id	varchar2(40)	not null,
	snapshot_id	varchar2(40)	null
,constraint epub_prs_id_pk primary key (project_id,target_id));


create table epub_pr_tg_dp_ts (
	project_id	varchar2(40)	not null,
	target_id	varchar2(40)	not null,
	deployment_time	timestamp	null
,constraint epub_pr_dt_id_pk primary key (project_id,target_id));


create table epub_pr_tg_dp_id (
	project_id	varchar2(40)	not null,
	target_id	varchar2(40)	not null,
	deployment_id	varchar2(40)	null
,constraint epub_pr_dp_id_pk primary key (project_id,target_id));


create table epub_pr_tg_ap_ts (
	project_id	varchar2(40)	not null,
	target_id	varchar2(40)	not null,
	approval_time	timestamp	null
,constraint epub_ap_ts_id_pk primary key (project_id,target_id));


create table epub_pr_history (
	project_id	varchar2(40)	not null,
	sequence_num	number(19)	not null,
	history	varchar2(40)	not null
,constraint pr_hist_pk primary key (project_id,sequence_num)
,constraint pt_hist_id_fk foreign key (project_id) references epub_project (project_id)
,constraint pt_hist_hist_fk foreign key (history) references epub_history (history_id));

create index pr_hist_hist_idx on epub_pr_history (history);

create table epub_process (
	process_id	varchar2(40)	not null,
	version	number(19)	not null,
	acl	varchar2(2048)	null,
	display_name	varchar2(255)	null,
	description	varchar2(1024)	null,
	creator	varchar2(40)	null,
	project	varchar2(40)	null,
	process_data	varchar2(40)	null,
	workflow_id	varchar2(40)	null,
	activity	varchar2(255)	null,
	site_id	varchar2(40)	null,
	status	number(19)	null,
	status_detail	varchar2(255)	null,
	creation_date	timestamp	null,
	completion_date	timestamp	null
,constraint process_pk primary key (process_id));

create index prc_disp_name_idx on epub_process (display_name);
create index prc_creator_idx on epub_process (creator);
create index prc_creat_date_idx on epub_process (creation_date);
create index prc_comp_date_idx on epub_process (completion_date);
create index prc_project_idx on epub_process (project);

create table epub_proc_prv_prj (
	process_id	varchar2(40)	not null,
	sequence_num	number(19)	not null,
	project_id	varchar2(40)	not null
,constraint proc_prv_prj_pk primary key (process_id,sequence_num)
,constraint prc_pvprj_pcid_fk foreign key (process_id) references epub_process (process_id)
,constraint prc_prv_prj_id_fk foreign key (project_id) references epub_project (project_id));

create index epub_prv_prj_id_x on epub_proc_prv_prj (project_id);

create table epub_proc_history (
	process_id	varchar2(40)	not null,
	sequence_num	number(19)	not null,
	history	varchar2(40)	not null
,constraint proc_hist_pk primary key (process_id,sequence_num)
,constraint proc_hist_id_fk foreign key (process_id) references epub_process (process_id)
,constraint proc_hist_hist_fk foreign key (history) references epub_history (history_id));

create index epub_proc_hist_x on epub_proc_history (history);

create table epub_proc_taskinfo (
	id	varchar2(40)	not null,
	taskinfo_id	varchar2(40)	not null
,constraint prc_ti_pk primary key (taskinfo_id)
,constraint prc_ti_prc_id_fk foreign key (id) references epub_process (process_id)
,constraint prc_ti_ti_id_fk foreign key (taskinfo_id) references epub_taskinfo (taskinfo_id));

create index epub_prctaskinfo_x on epub_proc_taskinfo (id);

create table epub_deployment (
	deployment_id	varchar2(40)	not null,
	version	number(19)	not null,
	target_id	varchar2(255)	null,
	deploy_time	timestamp	not null,
	create_time	timestamp	not null,
	creator	varchar2(40)	null,
	uri	varchar2(100)	null,
	next_dep_id	varchar2(40)	null,
	previous_dep_id	varchar2(40)	null,
	force_full	number(1)	null,
	dep_type	number(1)	null,
	status	number(3)	null,
	message_code	varchar2(255)	null,
	strict_file_op	number(3)	null,
	strict_repo_op	number(3)	null
,constraint deployment_pk primary key (deployment_id));

create index depl_time_idx on epub_deployment (deploy_time);

create table epub_deploy_proj (
	deployment_id	varchar2(40)	not null,
	project_id	varchar2(40)	not null,
	sequence_num	number(19)	not null
,constraint epub_dep_proj_pk primary key (deployment_id,project_id));


create table epub_dep_err_parm (
	deployment_id	varchar2(40)	not null,
	error_param	varchar2(255)	null,
	sequence_num	number(19)	not null
,constraint epub_dep_er_pk primary key (deployment_id,sequence_num)
,constraint epub_dep_id_fk foreign key (deployment_id) references epub_deployment (deployment_id));

--     Log of deployments that have occured. See the epub repository  
--     definition file for more details  

create table epub_dep_log (
	log_id	varchar2(40)	not null,
	dep_id	varchar2(40)	not null,
	target_name	varchar2(255)	not null,
	log_time	timestamp	not null,
	begin_time	timestamp	not null,
	end_time	timestamp	not null,
	actor_id	varchar2(40)	null,
	type	number(10)	not null,
	dep_mode	number(10)	not null,
	status	number(10)	not null,
	deli_proj_ids	varchar2(255)	null,
	delimiter	varchar2(5)	not null,
	strict_file_op	number(3)	null,
	strict_repo_op	number(3)	null
,constraint dep_log_pk primary key (log_id));







--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/process_data_ddl.xml#2 $
--     These tables are for the ProcessDataRepository  

create table epub_process_data (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	timestamp	null,
	process_data_id	varchar2(40)	not null,
	type	number(19)	not null
,constraint process_data_pk primary key (process_data_id,asset_version));

create index epub_process_d_wsx on epub_process_data (workspace_id);
create index epub_process_d_cix on epub_process_data (checkin_date);






--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/versionmanager_ddl.xml#2 $
--     These tables are for the version manager  

create table avm_devline (
	id	varchar2(40)	not null,
	type	number(19)	not null,
	name	varchar2(255)	not null,
	parent	varchar2(40)	null,
	date_created	timestamp	null
,constraint avm_devline_pk primary key (id)
,constraint avm_dl_name_unq unique (name));

create index avm_devline_pt_idx on avm_devline (parent);

create table avm_workspace (
	ws_id	varchar2(40)	not null,
	checked_in	number(1)	not null,
	ci_time	timestamp	null,
	userid	varchar2(255)	null,
	locked	number(1)	not null,
	editable	number(1)	not null,
	change_was	clob	null
,constraint avm_workspace_pk primary key (ws_id)
,constraint avm_workspace_fk foreign key (ws_id) references avm_devline (id));


create table avm_asset_lock (
	repository_name	varchar2(255)	not null,
	descriptor_name	varchar2(255)	not null,
	repository_id	varchar2(255)	not null,
	workspace_id	varchar2(40)	not null
,constraint avm_lock_pk primary key (repository_name,descriptor_name,repository_id)
,constraint avm_lock_ws_fk foreign key (workspace_id) references avm_workspace (ws_id));

create index avm_asset_lock_x on avm_asset_lock (workspace_id);






--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/file_repository_ddl.xml#2 $
--     These tables are for the storing versioned files for Publishing  

create table epub_file_folder (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	timestamp	null,
	folder_id	varchar2(40)	not null,
	acl	varchar2(2048)	null,
	folder_name	varchar2(255)	not null,
	parent_folder	varchar2(40)	null
,constraint content_folder_pk primary key (folder_id,asset_version));

create index ff_folder_name_idx on epub_file_folder (folder_name);
create index epub_file_fold_wsx on epub_file_folder (workspace_id);
create index epub_file_fold_cix on epub_file_folder (checkin_date);

create table epub_file_asset (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	timestamp	null,
	file_asset_id	varchar2(40)	not null,
	type	number(19)	not null,
	acl	varchar2(2048)	null,
	filename	varchar2(255)	not null,
	last_modified	timestamp	null,
	size_bytes	number(19)	null,
	checksum	number(19)	null,
	parent_folder	varchar2(40)	null
,constraint file_asset_pk primary key (file_asset_id,asset_version));

create index fa_folder_name_idx on epub_file_asset (filename);
create index fa_parent_fldr_idx on epub_file_asset (parent_folder);
create index fa_size_idx on epub_file_asset (size_bytes);
create index fa_last_mod_idx on epub_file_asset (last_modified);
create index epub_file_asse_wsx on epub_file_asset (workspace_id);
create index epub_file_asse_cix on epub_file_asset (checkin_date);

create table epub_text_file (
	asset_version	number(19)	not null,
	text_file_id	varchar2(40)	not null,
	text_content	clob	null
,constraint tf_file_id_pk primary key (text_file_id,asset_version));


create table epub_binary_file (
	asset_version	number(19)	not null,
	binary_file_id	varchar2(40)	not null,
	binary_content	blob	null
,constraint bf_file_id_pk primary key (binary_file_id,asset_version));







--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/workflow_ddl.xml#2 $
--     Collective workflow process instances.  These instances represent  
--     all subjects (processes) going through the workflow process.  

create table epub_coll_workflow (
	id	varchar2(40)	not null,
	workflow_name	varchar2(255)	null,
	modification_time	number(19)	null,
	segment_name	varchar2(255)	null,
	creator_id	varchar2(40)	null,
	state	varchar2(16)	null,
	num_retries	number(10)	null
,constraint epub_coll_wf_pkey primary key (id));

--     Individual workflow process instances.  Each of these instances  
--     represents a single subject (process) going through the workflow  
--     process.  

create table epub_ind_workflow (
	id	varchar2(40)	not null,
	workflow_name	varchar2(255)	null,
	modification_time	number(19)	null,
	segment_name	varchar2(255)	null,
	creator_id	varchar2(40)	null,
	state	varchar2(16)	null,
	process_id	varchar2(40)	not null,
	num_retries	number(10)	null
,constraint epub_ind_wf_pkey primary key (id)
,constraint epub_ind_wf_fkey foreign key (process_id) references epub_process (process_id));

create index epub_indworkflow_x on epub_ind_workflow (process_id);
--     Indices are needed on these tables to prevent table locks from being  
--     acquired.  The primary key indices do not prevent this since they  
--     are on multiple keys.  
-- 
--     String context variables associated with individual workflow  
--     process instances.  

create table epub_workflow_strs (
	id	varchar2(40)	not null,
	tag	varchar2(25)	not null,
	context_str	varchar2(255)	null
,constraint epub_wf_str_pkey primary key (id,tag)
,constraint epub_wf_str_fkey foreign key (id) references epub_ind_workflow (id));

--     Boolean context variables associated with individual workflow  
--     process instances.  

create table epub_workflow_bls (
	id	varchar2(40)	not null,
	tag	varchar2(25)	not null,
	context_bool	number(1)	null
,constraint epub_wf_bl_pkey primary key (id,tag)
,constraint epub_wf_bl_fkey foreign key (id) references epub_ind_workflow (id));

--     Long context variables associated with individual workflow process  
--     instances.  

create table epub_workflow_lngs (
	id	varchar2(40)	not null,
	tag	varchar2(25)	not null,
	context_long	number(19)	null
,constraint epub_wf_lng_pkey primary key (id,tag)
,constraint epub_wf_lng_fkey foreign key (id) references epub_ind_workflow (id));

--     Double context variables associated with individual workflow  
--     process instances.  

create table epub_workflow_dbls (
	id	varchar2(40)	not null,
	tag	varchar2(25)	not null,
	context_dbl	number(15,4)	null
,constraint epub_wf_dbl_pkey primary key (id,tag)
,constraint epub_wf_dbl_fkey foreign key (id) references epub_ind_workflow (id));

--     Date context variables associated with individual workflow process  
--     instances.  

create table epub_workflow_dats (
	id	varchar2(40)	not null,
	tag	varchar2(25)	not null,
	context_date	date	null
,constraint epub_wf_dat_pkey primary key (id,tag)
,constraint epub_wf_dat_fkey foreign key (id) references epub_ind_workflow (id));

--     Repository item context variables associated with individual   
--     workflow process instances.  

create table epub_workflow_ris (
	id	varchar2(40)	not null,
	tag	varchar2(255)	not null,
	context_item	varchar2(255)	null
,constraint epub_wf_ri_pkey primary key (id,tag)
,constraint epub_wf_ri_fkey foreign key (id) references epub_ind_workflow (id));

--     Virtual file context variables associated with individual   
--     workflow process instances.  

create table epub_workflow_vfs (
	id	varchar2(40)	not null,
	tag	varchar2(25)	not null,
	context_file	varchar2(255)	null
,constraint epub_wf_vf_pkey primary key (id,tag)
,constraint epub_wf_vf_fkey foreign key (id) references epub_ind_workflow (id));

--     Workflow infos.  Each of these infos corresponds to a workflow  
--     process definition created via the ACC UI.  

create table epub_workflow_info (
	id	varchar2(40)	not null,
	workflow_name	varchar2(255)	null,
	workflow_status	number(10)	null,
	modification_time	number(19)	null,
	creation_time	number(19)	null,
	author	varchar2(254)	null,
	last_modified_by	varchar2(254)	null,
	psm_version	number(10)	null,
	wdl	blob	null
,constraint epub_wf_info_pkey primary key (id));


create table epub_wf_mig_info (
	id	varchar2(40)	not null,
	workflow_info_id	varchar2(40)	not null,
	workflow_name	varchar2(255)	null,
	modification_time	number(19)	null,
	psm_version	number(10)	null,
	migration_status	number(10)	null,
	wdl	blob	null
,constraint epub_wf_mig_inf_pk primary key (id)
,constraint epub_wf_mig_inf_fk foreign key (workflow_info_id) references epub_workflow_info (id));

create index epub_wf_mig_info_x on epub_wf_mig_info (workflow_info_id);

create table epub_wf_mg_inf_seg (
	id	varchar2(40)	not null,
	idx	number(10)	not null,
	segment_name	varchar2(255)	null
,constraint epub_wf_mginfs_pk primary key (id,idx)
,constraint epub_wf_mginfs_fk foreign key (id) references epub_wf_mig_info (id));

--     Workflow template infos.  Each of these infos corresponds to a  
--     workflow template process definition created via the ACC UI.  

create table epub_wf_templ_info (
	id	varchar2(40)	not null,
	template_name	varchar2(255)	null,
	modification_time	number(19)	null,
	creation_time	number(19)	null,
	author	varchar2(254)	null,
	last_modified_by	varchar2(254)	null,
	wdl	blob	null
,constraint epub_tl_info_pkey primary key (id));

--     Pending collective transitions associated with workflow processes.  

create table epub_wf_coll_trans (
	id	varchar2(40)	not null,
	workflow_name	varchar2(255)	null,
	modification_time	number(19)	null,
	server_id	varchar2(40)	null,
	event_type	varchar2(255)	null,
	segment_name	varchar2(255)	null,
	state	varchar2(16)	null,
	coll_workflow_id	varchar2(40)	null,
	step	number(10)	null,
	current_count	number(10)	null,
	last_query_id	varchar2(40)	null,
	message_bean	blob	null
,constraint epub_wf_ctran_pkey primary key (id)
,constraint epub_wf_ctran_fkey foreign key (coll_workflow_id) references epub_coll_workflow (id));

create index epub_wfcolltrans_x on epub_wf_coll_trans (coll_workflow_id);
--     Pending individual transitions associated with workflow processes.  

create table epub_wf_ind_trans (
	id	varchar2(40)	not null,
	workflow_name	varchar2(255)	null,
	modification_time	number(19)	null,
	server_id	varchar2(40)	null,
	event_type	varchar2(255)	null,
	segment_name	varchar2(255)	null,
	state	varchar2(16)	null,
	last_query_id	varchar2(40)	null,
	message_bean	blob	null
,constraint epub_wf_itran_pkey primary key (id));

--     Pending deletions associated with workflow processes.  

create table epub_wf_deletion (
	id	varchar2(40)	not null,
	workflow_name	varchar2(255)	null,
	modification_time	number(19)	null
,constraint epub_wf_del_pkey primary key (id));


create table epub_wf_del_segs (
	id	varchar2(40)	not null,
	idx	number(10)	not null,
	segment_name	varchar2(255)	null
,constraint epub_wf_delsg_pkey primary key (id,idx)
,constraint epub_wf_delsg_fkey foreign key (id) references epub_wf_deletion (id));

--     Pending migrations associated with workflow processes.  

create table epub_wf_migration (
	id	varchar2(40)	not null,
	workflow_name	varchar2(255)	null,
	old_mod_time	number(19)	null,
	new_mod_time	number(19)	null,
	migration_info_id	varchar2(40)	null
,constraint epub_wf_mig_pkey primary key (id));


create table epub_wf_mig_segs (
	id	varchar2(40)	not null,
	idx	number(10)	not null,
	segment_name	varchar2(255)	null
,constraint epub_wf_migsg_pkey primary key (id,idx)
,constraint epub_wf_migsg_fkey foreign key (id) references epub_wf_migration (id));

--     Table that keeps track of how the various workflow process manager  
--     servers are classified.  

create table epub_wf_server_id (
	server_id	varchar2(40)	not null,
	server_type	number(10)	not null
,constraint epub_wf_server_pk primary key (server_id));







--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/internal_user_profile_ddl.xml#2 $
--     The tables here are for the user profile extensions needed by EPublishing.  

create table epub_int_user (
	user_id	varchar2(40)	not null,
	title	varchar2(255)	null,
	expert	number(1)	null,
	def_listing	number(19)	null,
	def_ip_listing	number(19)	null,
	allow_applets	number(1)	null
,constraint epub_int_user_pk primary key (user_id));


create table epub_int_prj_hist (
	user_id	varchar2(40)	not null,
	sequence_num	number(19)	not null,
	project_id	varchar2(40)	not null
,constraint user_i_prj_hist_pk primary key (user_id,sequence_num)
,constraint user_i_prj_hist_fk foreign key (user_id) references dpi_user (id));




