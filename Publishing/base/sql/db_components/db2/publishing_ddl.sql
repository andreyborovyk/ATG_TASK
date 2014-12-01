


--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/publishing_ddl.xml#2 $
--     The tables here are for the various parts of the epublishing system.  

create table epub_history (
	history_id	varchar(40)	not null,
	profile	varchar(40)	default null,
	timestamp	timestamp	default null,
	action	varchar(255)	default null,
	action_type	varchar(255)	default null,
	description	varchar(4096)	default null
,constraint history_pk primary key (history_id));

create index his_profile_idx on epub_history (profile);
create index his_timestamp_idx on epub_history (timestamp);
create index his_action_idx on epub_history (action);

create table epub_his_act_parm (
	history_id	varchar(40)	not null,
	action_param	varchar(255)	default null,
	sequence_num	bigint	not null
,constraint epub_his_ac_pk primary key (history_id,sequence_num)
,constraint epub_hisact_id_fk foreign key (history_id) references epub_history (history_id));


create table epub_taskinfo (
	taskinfo_id	varchar(40)	not null,
	version	bigint	not null,
	process_id	varchar(40)	not null,
	process_name	varchar(255)	not null,
	segment_name	varchar(255)	not null,
	task_element_id	varchar(255)	not null,
	acl	varchar(2048)	default null,
	priority	bigint	default null,
	owner_name	varchar(255)	default null,
	last_actor_name	varchar(255)	default null,
	last_action_date	timestamp	default null,
	last_outcome_id	varchar(255)	default null
,constraint taskinfo_pk primary key (taskinfo_id));

create index taskinfo_pname_ix on epub_taskinfo (process_name);

create table epub_agent_trnprt (
	transport_id	varchar(40)	not null,
	version	bigint	not null,
	transport_type	numeric(1)	not null,
	jndi_name	varchar(255)	default null,
	rmi_uri	varchar(255)	default null);


create table epub_agent (
	agent_id	varchar(40)	not null,
	version	bigint	not null,
	creation_time	timestamp	default null,
	display_name	varchar(255)	default null,
	description	varchar(1024)	default null,
	main_agent_id	varchar(40)	default null,
	transport	varchar(40)	not null
,constraint target_agent_pk primary key (agent_id));


create table epub_target (
	target_id	varchar(40)	not null,
	snapshot_name	varchar(255)	default null,
	version	bigint	not null,
	creation_time	timestamp	default null,
	main_target_id	varchar(40)	default null,
	display_name	varchar(255)	default null,
	description	varchar(1024)	default null,
	halted	numeric(1)	default null,
	flag_agents	numeric(1)	default null,
	target_type	numeric(1)	default null
,constraint targets_pk primary key (target_id));


create table epub_tr_dest (
	target_id	varchar(40)	not null,
	target_source	varchar(100)	not null,
	target_destination	varchar(100)	not null
,constraint epub_trd_id_pk primary key (target_id,target_source));


create table epub_topology (
	topology_id	varchar(40)	not null,
	version	bigint	not null,
	primary_tl	numeric(1)	default null
,constraint topology_pk primary key (topology_id));


create table epub_tl_targets (
	topology_id	varchar(40)	not null,
	target_id	varchar(40)	not null,
	sequence_num	bigint	not null
,constraint pr_tp_tr_pk primary key (topology_id,target_id));


create table epub_tr_agents (
	target_id	varchar(40)	not null,
	agent_id	varchar(40)	not null
,constraint pr_ag_pk primary key (target_id,agent_id)
,constraint pt_tr_pr_id_fk foreign key (target_id) references epub_target (target_id)
,constraint pt_ag_ag_id_fk foreign key (agent_id) references epub_agent (agent_id));

create index epub_tr_agents_x on epub_tr_agents (agent_id);

create table epub_princ_asset (
	agent_id	varchar(40)	not null,
	principal_assets	varchar(40)	not null
,constraint princ_asset_pk primary key (agent_id,principal_assets)
,constraint princ_aset_id_fk foreign key (agent_id) references epub_agent (agent_id));


create table epub_includ_asset (
	agent_id	varchar(40)	not null,
	include_assets	varchar(255)	not null
,constraint includ_asset_pk primary key (agent_id,include_assets)
,constraint includ_aset_id_fk foreign key (agent_id) references epub_agent (agent_id));


create table epub_exclud_asset (
	agent_id	varchar(40)	not null,
	exclude_assets	varchar(255)	not null
,constraint exclud_asset_pk primary key (agent_id,exclude_assets)
,constraint exclud_aset_id_fk foreign key (agent_id) references epub_agent (agent_id));


create table epub_dest_map (
	agent_id	varchar(40)	not null,
	source	varchar(255)	not null,
	destination	varchar(255)	not null
,constraint dest_map_pk primary key (agent_id,source)
,constraint dest_map_id_fk foreign key (agent_id) references epub_agent (agent_id));


create table epub_project (
	project_id	varchar(40)	not null,
	version	bigint	not null,
	acl	varchar(2048)	default null,
	display_name	varchar(255)	default null,
	description	varchar(1024)	default null,
	creator	varchar(40)	default null,
	workspace	varchar(255)	not null,
	workflow_id	varchar(40)	default null,
	checked_in	numeric(1)	default null,
	locked	numeric(1)	default null,
	editable	numeric(1)	default null,
	status	bigint	default null,
	status_detail	varchar(255)	default null,
	checkin_date	timestamp	default null,
	creation_date	timestamp	default null,
	completion_date	timestamp	default null
,constraint project_pk primary key (project_id)
,constraint pr_editable_chk check (editable in (0,1)));

create index pr_workspace_idx on epub_project (workspace);
create index pr_disp_name_idx on epub_project (display_name);
create index pr_creator_idx on epub_project (creator);
create index pr_creat_date_idx on epub_project (creation_date);
create index pr_comp_date_idx on epub_project (completion_date);

create table epub_prj_targt_ws (
	project_id	varchar(40)	not null,
	target_id	varchar(40)	not null,
	workspace_id	varchar(40)	default null
,constraint epub_prw_id_pk primary key (project_id,target_id));


create table epub_pr_tg_status (
	project_id	varchar(40)	not null,
	target_id	varchar(40)	not null,
	status_code	numeric(1)	default null
,constraint epub_pr_ap_id_pk primary key (project_id,target_id));


create table epub_prj_tg_snsht (
	project_id	varchar(40)	not null,
	target_id	varchar(40)	not null,
	snapshot_id	varchar(40)	default null
,constraint epub_prs_id_pk primary key (project_id,target_id));


create table epub_pr_tg_dp_ts (
	project_id	varchar(40)	not null,
	target_id	varchar(40)	not null,
	deployment_time	timestamp	default null
,constraint epub_pr_dt_id_pk primary key (project_id,target_id));


create table epub_pr_tg_dp_id (
	project_id	varchar(40)	not null,
	target_id	varchar(40)	not null,
	deployment_id	varchar(40)	default null
,constraint epub_pr_dp_id_pk primary key (project_id,target_id));


create table epub_pr_tg_ap_ts (
	project_id	varchar(40)	not null,
	target_id	varchar(40)	not null,
	approval_time	timestamp	default null
,constraint epub_ap_ts_id_pk primary key (project_id,target_id));


create table epub_pr_history (
	project_id	varchar(40)	not null,
	sequence_num	bigint	not null,
	history	varchar(40)	not null
,constraint pr_hist_pk primary key (project_id,sequence_num)
,constraint pt_hist_id_fk foreign key (project_id) references epub_project (project_id)
,constraint pt_hist_hist_fk foreign key (history) references epub_history (history_id));

create index pr_hist_hist_idx on epub_pr_history (history);

create table epub_process (
	process_id	varchar(40)	not null,
	version	numeric(19)	not null,
	acl	varchar(2048)	default null,
	display_name	varchar(255)	default null,
	description	varchar(1024)	default null,
	creator	varchar(40)	default null,
	project	varchar(40)	default null,
	process_data	varchar(40)	default null,
	workflow_id	varchar(40)	default null,
	activity	varchar(255)	default null,
	site_id	varchar(40)	default null,
	status	numeric(19)	default null,
	status_detail	varchar(255)	default null,
	creation_date	timestamp	default null,
	completion_date	timestamp	default null
,constraint process_pk primary key (process_id));

create index prc_disp_name_idx on epub_process (display_name);
create index prc_creator_idx on epub_process (creator);
create index prc_creat_date_idx on epub_process (creation_date);
create index prc_comp_date_idx on epub_process (completion_date);
create index prc_project_idx on epub_process (project);

create table epub_proc_prv_prj (
	process_id	varchar(40)	not null,
	sequence_num	numeric(19)	not null,
	project_id	varchar(40)	not null
,constraint proc_prv_prj_pk primary key (process_id,sequence_num)
,constraint prc_pvprj_pcid_fk foreign key (process_id) references epub_process (process_id)
,constraint prc_prv_prj_id_fk foreign key (project_id) references epub_project (project_id));

create index epub_prv_prj_id_x on epub_proc_prv_prj (project_id);

create table epub_proc_history (
	process_id	varchar(40)	not null,
	sequence_num	numeric(19)	not null,
	history	varchar(40)	not null
,constraint proc_hist_pk primary key (process_id,sequence_num)
,constraint proc_hist_id_fk foreign key (process_id) references epub_process (process_id)
,constraint proc_hist_hist_fk foreign key (history) references epub_history (history_id));

create index epub_proc_hist_x on epub_proc_history (history);

create table epub_proc_taskinfo (
	id	varchar(40)	not null,
	taskinfo_id	varchar(40)	not null
,constraint prc_ti_pk primary key (taskinfo_id)
,constraint prc_ti_prc_id_fk foreign key (id) references epub_process (process_id)
,constraint prc_ti_ti_id_fk foreign key (taskinfo_id) references epub_taskinfo (taskinfo_id));

create index epub_prctaskinfo_x on epub_proc_taskinfo (id);

create table epub_deployment (
	deployment_id	varchar(40)	not null,
	version	bigint	not null,
	target_id	varchar(255)	default null,
	deploy_time	timestamp	not null,
	create_time	timestamp	not null,
	creator	varchar(40)	default null,
	uri	varchar(100)	default null,
	next_dep_id	varchar(40)	default null,
	previous_dep_id	varchar(40)	default null,
	force_full	numeric(1)	default null,
	dep_type	numeric(1)	default null,
	status	numeric(3)	default null,
	message_code	varchar(255)	default null,
	strict_file_op	numeric(3)	default null,
	strict_repo_op	numeric(3)	default null
,constraint deployment_pk primary key (deployment_id));

create index depl_time_idx on epub_deployment (deploy_time);

create table epub_deploy_proj (
	deployment_id	varchar(40)	not null,
	project_id	varchar(40)	not null,
	sequence_num	bigint	not null
,constraint epub_dep_proj_pk primary key (deployment_id,project_id));


create table epub_dep_err_parm (
	deployment_id	varchar(40)	not null,
	error_param	varchar(255)	default null,
	sequence_num	bigint	not null
,constraint epub_dep_er_pk primary key (deployment_id,sequence_num)
,constraint epub_dep_id_fk foreign key (deployment_id) references epub_deployment (deployment_id));

--     Log of deployments that have occured. See the epub repository  
--     definition file for more details  

create table epub_dep_log (
	log_id	varchar(40)	not null,
	dep_id	varchar(40)	not null,
	target_name	varchar(255)	not null,
	log_time	timestamp	not null,
	begin_time	timestamp	not null,
	end_time	timestamp	not null,
	actor_id	varchar(40)	default null,
	type	integer	not null,
	dep_mode	integer	not null,
	status	integer	not null,
	deli_proj_ids	varchar(255)	default null,
	delimiter	varchar(5)	not null,
	strict_file_op	numeric(3)	default null,
	strict_repo_op	numeric(3)	default null
,constraint dep_log_pk primary key (log_id));

commit;


