


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/deployment_ddl.xml#2 $
--     These tables are for the daf deployment system  

create table das_deployment (
	deployment_id	varchar(40)	not null,
	version	integer	not null,
	start_time	datetime	null,
	end_time	datetime	null,
	failure_time	datetime	null,
	status	integer	null,
	prev_status	integer	null,
	status_detail	varchar(255)	null,
	current_phase	integer	null,
	rep_high_mark	integer	null,
	rep_marks_avail	integer	null,
	file_high_mark	integer	null,
	file_marks_avail	integer	null,
	thread_batch_size	integer	null,
	failure_count	integer	null,
	purge_deploy_data	tinyint	null
,constraint daf_depl_pk primary key (deployment_id))

create index das_dpl_start_idx on das_deployment (start_time)

create table das_depl_progress (
	deployment_id	varchar(40)	not null,
	version	integer	not null,
	work_completed	integer	null,
	total_work	integer	null
,constraint daf_depl_prg_pk primary key (deployment_id))


create table das_thread_batch (
	thread_batch_id	varchar(40)	not null,
	version	integer	not null,
	deployment	varchar(40)	null,
	type	integer	null,
	owner	varchar(255)	null,
	thread_name	varchar(255)	null,
	lower_bound	integer	null,
	upper_bound	integer	null,
	last_update_time	datetime	null,
	running	tinyint	not null
,constraint das_dpl_tb_pk primary key (thread_batch_id))

create index das_tb_deployment on das_thread_batch (deployment)
create index das_tb_owner on das_thread_batch (owner)
create index das_tb_thread_nam on das_thread_batch (thread_name)

create table das_deploy_data (
	deploy_data_id	varchar(40)	not null,
	version	integer	not null,
	type	integer	null,
	source	varchar(255)	null,
	destination	varchar(255)	null,
	dest_server	varchar(255)	null,
	deployment	varchar(40)	null
,constraint deploy_data_pk primary key (deploy_data_id)
,constraint dd_deployment_fk foreign key (deployment) references das_deployment (deployment_id))

create index dd_deployment_idx on das_deploy_data (deployment)

create table das_deploy_mark (
	marker_id	varchar(40)	not null,
	version	integer	not null,
	type	integer	null,
	status	integer	null,
	index_number	integer	null,
	marker_action	integer	null,
	f_src_devline_id	varchar(40)	null,
	r_src_devline_id	varchar(40)	null,
	deployment_id	varchar(40)	null,
	deployment_data	varchar(40)	null
,constraint marker_pk primary key nonclustered  (marker_id))

create index marker_index_idx on das_deploy_mark (index_number)

create table das_rep_mark (
	rep_marker_id	varchar(40)	not null,
	item_desc_name	varchar(255)	null,
	item_id	varchar(255)	null
,constraint rep_marker_pk primary key nonclustered  (rep_marker_id))


create table das_file_mark (
	file_marker_id	varchar(40)	not null,
	file_path	varchar(1000)	null
,constraint file_marker_pk primary key nonclustered  (file_marker_id))


create table das_depl_depldat (
	deployment_id	varchar(40)	not null,
	sequence_num	integer	not null,
	deployment_data	varchar(40)	null
,constraint das_dpl_depdat_pk primary key (deployment_id,sequence_num))


create table das_depl_options (
	deployment_id	varchar(40)	not null,
	tag	varchar(255)	not null,
	optionValue	varchar(255)	null
,constraint das_dpl_depopt_pk primary key (deployment_id,tag))


create table das_depl_repmaps (
	deployment_id	varchar(40)	not null,
	source	varchar(255)	not null,
	destination	varchar(255)	null
,constraint das_dpl_repmap_pk primary key (deployment_id,source))


create table das_depl_item_ref (
	item_ref_id	varchar(40)	not null,
	version	integer	not null,
	deployment_id	varchar(40)	null,
	repository	varchar(255)	null,
	item_desc_name	varchar(255)	null,
	item_id	varchar(255)	null,
	item_index	integer	null
,constraint das_dpl_itmref_pk primary key (item_ref_id))

create index das_dpl_itmx_idx on das_depl_item_ref (item_index)
create index das_dpl_dplid_idx on das_depl_item_ref (deployment_id)

create table das_dd_markers (
	deploy_data_id	varchar(40)	not null,
	sequence_num	integer	not null,
	marker	varchar(40)	null
,constraint das_dpl_dd_mrk_pk primary key (deploy_data_id,sequence_num)
,constraint marker_fk foreign key (marker) references das_deploy_mark (marker_id))

create index marker_idx on das_dd_markers (marker)

create table das_dep_fail_info (
	id	varchar(40)	not null,
	deployment_id	varchar(40)	not null,
	marker_id	varchar(40)	null,
	severity	varchar(40)	null,
	failure_message	varchar(255)	null,
	failure_time	datetime	null,
	error_code	varchar(40)	null,
	cause	text	null)



go
