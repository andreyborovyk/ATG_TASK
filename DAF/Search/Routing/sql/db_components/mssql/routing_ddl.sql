


--  @version $Id: //product/DAF/version/10.0.3/Search/Routing/src/sql/routing_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements, which will configureyour database for use with the search routing schema.

create table rout_index (
	id	varchar(40)	not null,
	idx_start	datetime	null,
	idx_end	datetime	null,
	est_start	datetime	null,
	est_end	datetime	null,
	acc_exist_lps	tinyint	null
,constraint rout_index_pk primary key (id))


create table rout_lp_summary (
	id	varchar(40)	not null,
	duration_seconds	integer	null,
	estimated_bytes	bigint	null,
	actual_bytes	bigint	null,
	items_sent	integer	null,
	items_deferred	integer	null,
	error_count	integer	null
,constraint rout_lp_smry_pk primary key (id))


create table rout_lp_cmd_count (
	id	varchar(40)	not null,
	cmd_name	varchar(40)	null,
	cmd_count	integer	null
,constraint rout_cmd_cnt_p primary key (id))


create table rout_lp_smry_cmds (
	lp_summary_id	varchar(40)	not null,
	cmd_count_id	varchar(40)	not null,
	cmd_indx	integer	not null
,constraint rout_smry_cmds_m_p primary key (lp_summary_id,cmd_indx)
,constraint rout_lpsmryp_f1 foreign key (lp_summary_id) references rout_lp_summary (id)
,constraint rout_smry_fk2 foreign key (cmd_count_id) references rout_lp_cmd_count (id))

create index rout_smry1_x on rout_lp_smry_cmds (cmd_count_id)

create table rout_env (
	id	varchar(40)	not null,
	env_name	varchar(254)	not null,
	rollback_policy	tinyint	null,
	server_repl_policy	tinyint	null,
	current_index	varchar(40)	null,
	deploying_index	varchar(40)	null,
	current_deployment	varchar(40)	null,
	deploy_type	tinyint	null,
	env_type	tinyint	null,
	parent_project	varchar(40)	null,
	parent_proj_name	varchar(254)	null,
	shadow	tinyint	null,
	request_options	text	null,
	bulk_env_id	varchar(40)	null,
	indexable	tinyint	null,
	searchable	tinyint	null,
	multiplicity	integer	null,
	target_name	varchar(40)	null,
	temp_env	tinyint	null
,constraint rout_env_pk primary key (id)
,constraint env_unique unique (env_name)
,constraint rout_env_f1 foreign key (current_index) references rout_index (id)
,constraint rout_env_f2 foreign key (deploying_index) references rout_index (id))

create index rout_env1_x on rout_env (current_index)
create index rout_env2_x on rout_env (deploying_index)

create table rout_host_inf (
	id	varchar(40)	not null,
	hostname	varchar(64)	null,
	start_port	integer	null,
	end_port	integer	null,
	lnch_svc_port	integer	null,
	lnch_svc_name	varchar(40)	null,
	lnch_svc_protocol	varchar(40)	null,
	cpu_count	integer	null,
	os_name	varchar(254)	null,
	os_arch	varchar(254)	null,
	os_version	varchar(254)	null
,constraint rout_host_inf_pk primary key (id))


create table rout_host (
	id	varchar(40)	not null,
	host_info_id	varchar(40)	null,
	parent_env_id	varchar(40)	null,
	index_root_path	varchar(254)	null,
	num_engines	integer	null
,constraint rout_host_pk primary key (id)
,constraint rout_host_f1 foreign key (parent_env_id) references rout_env (id)
,constraint rout_host_f2 foreign key (host_info_id) references rout_host_inf (id))

create index rout_host1_x on rout_host (parent_env_id)
create index rout_host2_x on rout_host (host_info_id)

create table rout_host_rsrvd_lps (
	parent_host_id	varchar(40)	not null,
	shared_lp_id	varchar(40)	not null
,constraint rout_rsrvd_pk primary key (parent_host_id,shared_lp_id)
,constraint rout_rsrvd_f1 foreign key (parent_host_id) references rout_host (id))


create table rout_part (
	id	varchar(40)	not null,
	parent_index	varchar(40)	null,
	parent_lp	varchar(40)	null,
	last_partition	varchar(40)	null,
	content_guid	varchar(64)	null,
	date_created	datetime	null,
	files_exist	tinyint	null,
	space_used_kb	bigint	null,
	space_free_kb	bigint	null,
	items_indexed	bigint	null,
	ideamap	varchar(254)	null,
	storage	varchar(254)	null,
	hdocmap	varchar(254)	null,
	needs_content_guid	tinyint	not null,
	shared_id	varchar(40)	null,
	indexing_mode	tinyint	not null,
	def_wrk_path	varchar(254)	null,
	sent_bytes	bigint	null,
	content_bytes	bigint	null,
	max_bytes	bigint	null,
	idx_start	datetime	null,
	idx_end	datetime	null,
	bak_start	datetime	null,
	bak_end	datetime	null,
	part_full	tinyint	null,
	backup_ideamap	varchar(254)	null,
	backup_storage	varchar(254)	null,
	backup_ideamap_old	varchar(254)	null,
	backup_storage_old	varchar(254)	null,
	backup_time	datetime	null,
	backup_time_old	datetime	null
,constraint rout_part_pk primary key (id))


create table rout_engine (
	id	varchar(40)	not null,
	index_partition_id	varchar(40)	null,
	host_id	varchar(40)	null,
	port	integer	null,
	local_content_path	varchar(254)	null,
	server_state	tinyint	null,
	failures	integer	null,
	max_failures	integer	null,
	searchable	tinyint	null
,constraint rout_engine_pk primary key (id)
,constraint rout_engine_f1 foreign key (index_partition_id) references rout_part (id)
,constraint rout_engine_f2 foreign key (host_id) references rout_host (id))

create index rout_engine1_x on rout_engine (index_partition_id)
create index rout_engine2_x on rout_engine (host_id)

create table rout_log_part (
	id	varchar(40)	not null,
	lp_name	varchar(40)	null,
	lp_description	varchar(254)	null,
	parent_index	varchar(40)	null,
	partition_mode	tinyint	not null,
	orig_part_count	integer	not null,
	shared_id	varchar(40)	not null,
	flggd_for_full	tinyint	not null,
	curr_idx_full	tinyint	not null,
	last_partition	varchar(40)	null,
	summary_id	varchar(40)	null,
	indexing_state	tinyint	not null,
	est_start	datetime	null,
	est_end	datetime	null,
	content_label	varchar(40)	null,
	flggd_for_del	tinyint	null
,constraint rout_log_part_p primary key (id)
,constraint rout_log_part_f1 foreign key (parent_index) references rout_index (id))

create index rout_log_part1_x on rout_log_part (parent_index)

create table rout_phys_part_m (
	log_part_id	varchar(40)	not null,
	phys_part_id	varchar(40)	not null,
	phys_indx	integer	not null
,constraint rout_phys_part_m_p primary key (log_part_id,phys_indx)
,constraint rout_phys_m_f1 foreign key (log_part_id) references rout_log_part (id)
,constraint rout_phys_m_f2 foreign key (phys_part_id) references rout_part (id))

create index rout_phys_m1_x on rout_phys_part_m (phys_part_id)

create table rout_idx_log_parts (
	parent_index	varchar(40)	not null,
	log_part_id	varchar(40)	not null,
	log_indx	integer	not null
,constraint rout_lparts_m_p primary key (parent_index,log_indx)
,constraint rout_idxlogp_f1 foreign key (parent_index) references rout_index (id)
,constraint rout_idxlogp_fk2 foreign key (log_part_id) references rout_log_part (id))

create index rout_idxlogp1_x on rout_idx_log_parts (log_part_id)

create table rout_dep_hist (
	id	varchar(40)	not null,
	environment	varchar(40)	null,
	new_index	varchar(40)	null,
	time_started	datetime	null,
	time_ended	datetime	null,
	deploy_status	tinyint	null
,constraint rout_dep_hist_pk primary key (id)
,constraint rout_dep_hist_f1 foreign key (new_index) references rout_index (id))

create index rout_dep_hist1_x on rout_dep_hist (new_index)

create table rout_swpchk (
	id	varchar(40)	not null,
	appserver_name	varchar(254)	null,
	last_check_time	datetime	null,
	swap_state	tinyint	null
,constraint rout_swpch_pk primary key (id))


create table rout_ctnt_label (
	id	varchar(40)	not null
,constraint rout_ctnt_label_p primary key (id))


create table rout_target_type (
	id	varchar(40)	not null
,constraint rout_target_type_p primary key (id))


create table rout_instance (
	id	varchar(40)	not null,
	hostname	varchar(64)	not null,
	rmi_port	integer	null
,constraint rout_instance_p primary key (id))



go
