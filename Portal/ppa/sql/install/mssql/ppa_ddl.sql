


--  @version $Id: //app/portal/version/10.0.3/ppa/sql/xml/ppa_ddl.xml#2 $
--     Community Proposal  

create table ppa_cm_prpsl (
	id	varchar(40)	not null,
	name	varchar(254)	null,
	url	varchar(254)	null,
	template_id	varchar(40)	null,
	community_id	varchar(40)	null,
	creator_id	varchar(40)	null,
	version	integer	null,
	creation_date	datetime	not null,
	last_modified	datetime	not null
,constraint ppa_cm_prpsl_p primary key (id)
,constraint ppa_cp_tid1_f foreign key (template_id) references paf_comm_template (template_id)
,constraint ppa_cp_cid2_f foreign key (community_id) references paf_community (community_id)
,constraint ppa_cp_cid3_f foreign key (creator_id) references dps_user (id))

create index ppa_cp_tid1_id on ppa_cm_prpsl (template_id)
create index ppa_cp_tid2_id on ppa_cm_prpsl (community_id)
create index ppa_cp_tid3_id on ppa_cm_prpsl (creator_id)
--     Collective workflow process instances.  These instances represent  
--     all subjects going through the workflow process.  

create table ppa_coll_workflow (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	null,
	modification_time	numeric(19)	null,
	segment_name	varchar(255)	null,
	creator_id	varchar(40)	null,
	machine_state	varchar(16)	null,
	num_retries	integer	null
,constraint ppa_coll_wf_p primary key (id)
,constraint ppa_clwf_cid1_f foreign key (creator_id) references ppa_coll_workflow (id))

create index ppa_clwf_cid1_id on ppa_coll_workflow (creator_id)
--     Individual workflow process instances.  Each of these instances  
--     represents a single subject going through the workflow  
--     process.  

create table ppa_ind_workflow (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	null,
	modification_time	numeric(19)	null,
	segment_name	varchar(255)	null,
	creator_id	varchar(40)	null,
	machine_state	varchar(16)	null,
	cm_prpsl_id	varchar(40)	not null,
	num_retries	integer	null
,constraint ppa_ind_wf_p primary key (id)
,constraint ppa_ind_wf1_f foreign key (cm_prpsl_id) references ppa_cm_prpsl (id)
,constraint ppa_ind_wf_cid2_f foreign key (creator_id) references ppa_coll_workflow (id))

create index ppa_ind_wkf1_id on ppa_ind_workflow (cm_prpsl_id)
create index ppa_ind_wkf2_id on ppa_ind_workflow (creator_id)
--     Indices are needed on these tables to prevent table locks from being  
--     acquired.  The primary key indices do not prevent this since they  
--     are on multiple keys.  
-- 
--     String context variables associated with individual workflow  
--     process instances.  

create table ppa_workflow_strs (
	id	varchar(40)	not null,
	tag	varchar(25)	not null,
	context_str	varchar(255)	null
,constraint ppa_wf_str_p primary key (id,tag)
,constraint ppa_wf_str1_f foreign key (id) references ppa_ind_workflow (id))

--     Boolean context variables associated with individual workflow  
--     process instances.  

create table ppa_workflow_bls (
	id	varchar(40)	not null,
	tag	varchar(25)	not null,
	context_bool	numeric(1)	null
,constraint ppa_wf_bl_p primary key (id,tag)
,constraint ppa_wf_bl1_f foreign key (id) references ppa_ind_workflow (id))

--     Long context variables associated with individual workflow process  
--     instances.  

create table ppa_workflow_lngs (
	id	varchar(40)	not null,
	tag	varchar(25)	not null,
	context_long	numeric(19)	null
,constraint ppa_wf_lng_p primary key (id,tag)
,constraint ppa_wf_lng1_f foreign key (id) references ppa_ind_workflow (id))

--     Double context variables associated with individual workflow  
--     process instances.  

create table ppa_workflow_dbls (
	id	varchar(40)	not null,
	tag	varchar(25)	not null,
	context_dbl	numeric(15,4)	null
,constraint ppa_wf_dbl_p primary key (id,tag)
,constraint ppa_wf_dbl1_f foreign key (id) references ppa_ind_workflow (id))

--     Date context variables associated with individual workflow process  
--     instances.  

create table ppa_workflow_dats (
	id	varchar(40)	not null,
	tag	varchar(25)	not null,
	context_date	datetime	null
,constraint ppa_wf_dat_p primary key (id,tag)
,constraint ppa_wf_dat1_f foreign key (id) references ppa_ind_workflow (id))

--     Workflow infos.  Each of these infos corresponds to a workflow  
--     process definition created via the ACC UI.  

create table ppa_workflow_info (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	null,
	workflow_status	integer	null,
	modification_time	numeric(19)	null,
	creation_time	numeric(19)	null,
	author	varchar(25)	null,
	last_modified_by	varchar(25)	null,
	psm_version	integer	null,
	wdl	image	null
,constraint ppa_workflowinfo_p primary key (id)
,constraint ppa_workflowinf1_c check (workflow_status in (0,1,2)))


create table ppa_wf_mig_info (
	id	varchar(40)	not null,
	workflow_info_id	varchar(40)	not null,
	workflow_name	varchar(255)	null,
	modification_time	numeric(19)	null,
	psm_version	integer	null,
	migration_status	integer	null,
	wdl	image	null
,constraint ppa_wf_mig_inf_p primary key (id)
,constraint ppa_wf_mig_inf1_f foreign key (workflow_info_id) references ppa_workflow_info (id)
,constraint ppa_wf_mig_info1_c check (migration_status in (0,1,2)))

create index ppa_wf_miginf1_id on ppa_wf_mig_info (workflow_info_id)

create table ppa_wf_mg_inf_seg (
	id	varchar(40)	not null,
	idx	integer	not null,
	segment_name	varchar(255)	null
,constraint ppa_wf_mginfs_p primary key (id,idx)
,constraint ppa_wf_mginfs1_f foreign key (id) references ppa_wf_mig_info (id))

--     Workflow template infos.  Each of these infos corresponds to a  
--     workflow template process definition created via the ACC UI.  

create table ppa_wf_templ_info (
	id	varchar(40)	not null,
	template_name	varchar(255)	null,
	modification_time	numeric(19)	null,
	creation_time	numeric(19)	null,
	author	varchar(25)	null,
	last_modified_by	varchar(25)	null,
	wdl	image	null
,constraint ppa_tl_info_p primary key (id))

--     Pending collective transitions associated with workflow processes.  

create table ppa_wf_coll_trans (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	null,
	modification_time	numeric(19)	null,
	server_id	varchar(40)	null,
	event_type	varchar(255)	null,
	segment_name	varchar(255)	null,
	machine_state	varchar(16)	null,
	coll_workflow_id	varchar(40)	null,
	transition_step	integer	null,
	current_count	integer	null,
	last_query_id	varchar(40)	null,
	message_bean	image	null
,constraint ppa_wf_ctran_p primary key (id)
,constraint ppa_wf_ctran1_f foreign key (coll_workflow_id) references ppa_coll_workflow (id))

create index ppa_wf_ctran1_id on ppa_wf_coll_trans (coll_workflow_id)
--     Pending individual transitions associated with workflow processes.  

create table ppa_wf_ind_trans (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	null,
	modification_time	numeric(19)	null,
	server_id	varchar(40)	null,
	event_type	varchar(255)	null,
	segment_name	varchar(255)	null,
	machine_state	varchar(16)	null,
	last_query_id	varchar(40)	null,
	message_bean	image	null
,constraint ppa_wf_itran_p primary key (id))

--     Pending deletions associated with workflow processes.  

create table ppa_wf_deletion (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	null,
	modification_time	numeric(19)	null
,constraint ppa_wf_del_p primary key (id))


create table ppa_wf_del_segs (
	id	varchar(40)	not null,
	idx	integer	not null,
	segment_name	varchar(255)	null
,constraint ppa_wf_delsg_p primary key (id,idx)
,constraint ppa_wf_delsg1_f foreign key (id) references ppa_wf_deletion (id))

--     Pending migrations associated with workflow processes.  

create table ppa_wf_migration (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	null,
	old_mod_time	numeric(19)	null,
	new_mod_time	numeric(19)	null,
	migration_info_id	varchar(40)	null
,constraint ppa_wf_mig_p primary key (id))


create table ppa_wf_mig_segs (
	id	varchar(40)	not null,
	idx	integer	not null,
	segment_name	varchar(255)	null
,constraint ppa_wf_migsg_p primary key (id,idx)
,constraint ppa_wf_migsg1_f foreign key (id) references ppa_wf_migration (id))

--     Table that keeps track of how the various workflow process manager  
--     servers are classified.  

create table ppa_wf_server_id (
	server_id	varchar(40)	not null,
	server_type	integer	not null
,constraint ppa_wf_server_p primary key (server_id)
,constraint ppa_wfserver_id1_c check (server_type in (0,1,2)))


create table ppa_taskinfo (
	taskinfo_id	varchar(40)	not null,
	version	numeric(19)	not null,
	cm_prpsl_id	varchar(40)	not null,
	process_name	varchar(255)	not null,
	segment_name	varchar(255)	not null,
	task_element_id	varchar(255)	not null,
	acl	text	null,
	priority	numeric(19)	null,
	owner_name	varchar(255)	null,
	last_actor_name	varchar(255)	null,
	last_action_date	datetime	null,
	last_outcome_id	varchar(255)	null
,constraint ppa_taskinfo_p primary key (taskinfo_id)
,constraint ppa_taskinfo1_f foreign key (cm_prpsl_id) references ppa_cm_prpsl (id))

create index ppa_taskinfo1_id on ppa_taskinfo (cm_prpsl_id)

create table ppa_cp_taskinfo (
	id	varchar(40)	not null,
	taskinfo_id	varchar(40)	not null
,constraint ppa_ti_p primary key (taskinfo_id)
,constraint ppa_ti_pr_id1_f foreign key (id) references ppa_cm_prpsl (id)
,constraint ppa_ti_ti_id2_f foreign key (taskinfo_id) references ppa_taskinfo (taskinfo_id))

create index ppa_ti_pr_id1_id on ppa_cp_taskinfo (id)


go
