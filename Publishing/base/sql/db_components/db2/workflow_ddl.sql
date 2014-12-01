


--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/workflow_ddl.xml#2 $
--     Collective workflow process instances.  These instances represent  
--     all subjects (processes) going through the workflow process.  

create table epub_coll_workflow (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	default null,
	modification_time	numeric(19)	default null,
	segment_name	varchar(255)	default null,
	creator_id	varchar(40)	default null,
	state	varchar(16)	default null,
	num_retries	integer	default null
,constraint epub_coll_wf_pkey primary key (id));

--     Individual workflow process instances.  Each of these instances  
--     represents a single subject (process) going through the workflow  
--     process.  

create table epub_ind_workflow (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	default null,
	modification_time	numeric(19)	default null,
	segment_name	varchar(255)	default null,
	creator_id	varchar(40)	default null,
	state	varchar(16)	default null,
	process_id	varchar(40)	not null,
	num_retries	integer	default null
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
	id	varchar(40)	not null,
	tag	varchar(25)	not null,
	context_str	varchar(255)	default null
,constraint epub_wf_str_pkey primary key (id,tag)
,constraint epub_wf_str_fkey foreign key (id) references epub_ind_workflow (id));

--     Boolean context variables associated with individual workflow  
--     process instances.  

create table epub_workflow_bls (
	id	varchar(40)	not null,
	tag	varchar(25)	not null,
	context_bool	numeric(1)	default null
,constraint epub_wf_bl_pkey primary key (id,tag)
,constraint epub_wf_bl_fkey foreign key (id) references epub_ind_workflow (id));

--     Long context variables associated with individual workflow process  
--     instances.  

create table epub_workflow_lngs (
	id	varchar(40)	not null,
	tag	varchar(25)	not null,
	context_long	numeric(19)	default null
,constraint epub_wf_lng_pkey primary key (id,tag)
,constraint epub_wf_lng_fkey foreign key (id) references epub_ind_workflow (id));

--     Double context variables associated with individual workflow  
--     process instances.  

create table epub_workflow_dbls (
	id	varchar(40)	not null,
	tag	varchar(25)	not null,
	context_dbl	numeric(15,4)	default null
,constraint epub_wf_dbl_pkey primary key (id,tag)
,constraint epub_wf_dbl_fkey foreign key (id) references epub_ind_workflow (id));

--     Date context variables associated with individual workflow process  
--     instances.  

create table epub_workflow_dats (
	id	varchar(40)	not null,
	tag	varchar(25)	not null,
	context_date	timestamp	default null
,constraint epub_wf_dat_pkey primary key (id,tag)
,constraint epub_wf_dat_fkey foreign key (id) references epub_ind_workflow (id));

--     Repository item context variables associated with individual   
--     workflow process instances.  

create table epub_workflow_ris (
	id	varchar(40)	not null,
	tag	varchar(255)	not null,
	context_item	varchar(255)	default null
,constraint epub_wf_ri_pkey primary key (id,tag)
,constraint epub_wf_ri_fkey foreign key (id) references epub_ind_workflow (id));

--     Virtual file context variables associated with individual   
--     workflow process instances.  

create table epub_workflow_vfs (
	id	varchar(40)	not null,
	tag	varchar(25)	not null,
	context_file	varchar(255)	default null
,constraint epub_wf_vf_pkey primary key (id,tag)
,constraint epub_wf_vf_fkey foreign key (id) references epub_ind_workflow (id));

--     Workflow infos.  Each of these infos corresponds to a workflow  
--     process definition created via the ACC UI.  

create table epub_workflow_info (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	default null,
	workflow_status	integer	default null,
	modification_time	numeric(19)	default null,
	creation_time	numeric(19)	default null,
	author	varchar(254)	default null,
	last_modified_by	varchar(254)	default null,
	psm_version	integer	default null,
	wdl	blob(1048576)	default null
,constraint epub_wf_info_pkey primary key (id));


create table epub_wf_mig_info (
	id	varchar(40)	not null,
	workflow_info_id	varchar(40)	not null,
	workflow_name	varchar(255)	default null,
	modification_time	numeric(19)	default null,
	psm_version	integer	default null,
	migration_status	integer	default null,
	wdl	blob(1048576)	default null
,constraint epub_wf_mig_inf_pk primary key (id)
,constraint epub_wf_mig_inf_fk foreign key (workflow_info_id) references epub_workflow_info (id));

create index epub_wf_mig_info_x on epub_wf_mig_info (workflow_info_id);

create table epub_wf_mg_inf_seg (
	id	varchar(40)	not null,
	idx	integer	not null,
	segment_name	varchar(255)	default null
,constraint epub_wf_mginfs_pk primary key (id,idx)
,constraint epub_wf_mginfs_fk foreign key (id) references epub_wf_mig_info (id));

--     Workflow template infos.  Each of these infos corresponds to a  
--     workflow template process definition created via the ACC UI.  

create table epub_wf_templ_info (
	id	varchar(40)	not null,
	template_name	varchar(255)	default null,
	modification_time	numeric(19)	default null,
	creation_time	numeric(19)	default null,
	author	varchar(254)	default null,
	last_modified_by	varchar(254)	default null,
	wdl	blob(1048576)	default null
,constraint epub_tl_info_pkey primary key (id));

--     Pending collective transitions associated with workflow processes.  

create table epub_wf_coll_trans (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	default null,
	modification_time	numeric(19)	default null,
	server_id	varchar(40)	default null,
	event_type	varchar(255)	default null,
	segment_name	varchar(255)	default null,
	state	varchar(16)	default null,
	coll_workflow_id	varchar(40)	default null,
	step	integer	default null,
	current_count	integer	default null,
	last_query_id	varchar(40)	default null,
	message_bean	blob(1048576)	default null
,constraint epub_wf_ctran_pkey primary key (id)
,constraint epub_wf_ctran_fkey foreign key (coll_workflow_id) references epub_coll_workflow (id));

create index epub_wfcolltrans_x on epub_wf_coll_trans (coll_workflow_id);
--     Pending individual transitions associated with workflow processes.  

create table epub_wf_ind_trans (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	default null,
	modification_time	numeric(19)	default null,
	server_id	varchar(40)	default null,
	event_type	varchar(255)	default null,
	segment_name	varchar(255)	default null,
	state	varchar(16)	default null,
	last_query_id	varchar(40)	default null,
	message_bean	blob(1048576)	default null
,constraint epub_wf_itran_pkey primary key (id));

--     Pending deletions associated with workflow processes.  

create table epub_wf_deletion (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	default null,
	modification_time	numeric(19)	default null
,constraint epub_wf_del_pkey primary key (id));


create table epub_wf_del_segs (
	id	varchar(40)	not null,
	idx	integer	not null,
	segment_name	varchar(255)	default null
,constraint epub_wf_delsg_pkey primary key (id,idx)
,constraint epub_wf_delsg_fkey foreign key (id) references epub_wf_deletion (id));

--     Pending migrations associated with workflow processes.  

create table epub_wf_migration (
	id	varchar(40)	not null,
	workflow_name	varchar(255)	default null,
	old_mod_time	numeric(19)	default null,
	new_mod_time	numeric(19)	default null,
	migration_info_id	varchar(40)	default null
,constraint epub_wf_mig_pkey primary key (id));


create table epub_wf_mig_segs (
	id	varchar(40)	not null,
	idx	integer	not null,
	segment_name	varchar(255)	default null
,constraint epub_wf_migsg_pkey primary key (id,idx)
,constraint epub_wf_migsg_fkey foreign key (id) references epub_wf_migration (id));

--     Table that keeps track of how the various workflow process manager  
--     servers are classified.  

create table epub_wf_server_id (
	server_id	varchar(40)	not null,
	server_type	integer	not null
,constraint epub_wf_server_pk primary key (server_id));

commit;


