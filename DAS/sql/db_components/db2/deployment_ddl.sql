


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/deployment_ddl.xml#2 $
--     These tables are for the daf deployment system  

create table das_deployment (
	deployment_id	varchar(40)	not null,
	version	integer	not null,
	start_time	timestamp	default null,
	end_time	timestamp	default null,
	failure_time	timestamp	default null,
	status	integer	default null,
	prev_status	integer	default null,
	status_detail	varchar(255)	default null,
	current_phase	integer	default null,
	rep_high_mark	integer	default null,
	rep_marks_avail	integer	default null,
	file_high_mark	integer	default null,
	file_marks_avail	integer	default null,
	thread_batch_size	integer	default null,
	failure_count	integer	default null,
	purge_deploy_data	numeric(1)	default null
,constraint daf_depl_pk primary key (deployment_id));

create index das_dpl_start_idx on das_deployment (start_time);

create table das_depl_progress (
	deployment_id	varchar(40)	not null,
	version	integer	not null,
	work_completed	integer	default null,
	total_work	integer	default null
,constraint daf_depl_prg_pk primary key (deployment_id));


create table das_thread_batch (
	thread_batch_id	varchar(40)	not null,
	version	integer	not null,
	deployment	varchar(40)	default null,
	type	integer	default null,
	owner	varchar(255)	default null,
	thread_name	varchar(255)	default null,
	lower_bound	integer	default null,
	upper_bound	integer	default null,
	last_update_time	timestamp	default null,
	running	numeric(1,0)	not null
,constraint das_dpl_tb_pk primary key (thread_batch_id));

create index das_tb_deployment on das_thread_batch (deployment);
create index das_tb_owner on das_thread_batch (owner);
create index das_tb_thread_nam on das_thread_batch (thread_name);

create table das_deploy_data (
	deploy_data_id	varchar(40)	not null,
	version	integer	not null,
	type	integer	default null,
	source	varchar(255)	default null,
	destination	varchar(255)	default null,
	dest_server	varchar(255)	default null,
	deployment	varchar(40)	default null
,constraint deploy_data_pk primary key (deploy_data_id)
,constraint dd_deployment_fk foreign key (deployment) references das_deployment (deployment_id));

create index dd_deployment_idx on das_deploy_data (deployment);

create table das_deploy_mark (
	marker_id	varchar(40)	not null,
	version	integer	not null,
	type	integer	default null,
	status	integer	default null,
	index_number	integer	default null,
	marker_action	integer	default null,
	f_src_devline_id	varchar(40)	default null,
	r_src_devline_id	varchar(40)	default null,
	deployment_id	varchar(40)	default null,
	deployment_data	varchar(40)	default null
,constraint marker_pk primary key (marker_id));

create index marker_index_idx on das_deploy_mark (index_number);

create table das_rep_mark (
	rep_marker_id	varchar(40)	not null,
	item_desc_name	varchar(255)	default null,
	item_id	varchar(255)	default null
,constraint rep_marker_pk primary key (rep_marker_id));


create table das_file_mark (
	file_marker_id	varchar(40)	not null,
	file_path	varchar(1000)	default null
,constraint file_marker_pk primary key (file_marker_id));


create table das_depl_depldat (
	deployment_id	varchar(40)	not null,
	sequence_num	integer	not null,
	deployment_data	varchar(40)	default null
,constraint das_dpl_depdat_pk primary key (deployment_id,sequence_num));


create table das_depl_options (
	deployment_id	varchar(40)	not null,
	tag	varchar(255)	not null,
	optionValue	varchar(255)	default null
,constraint das_dpl_depopt_pk primary key (deployment_id,tag));


create table das_depl_repmaps (
	deployment_id	varchar(40)	not null,
	source	varchar(255)	not null,
	destination	varchar(255)	default null
,constraint das_dpl_repmap_pk primary key (deployment_id,source));


create table das_depl_item_ref (
	item_ref_id	varchar(40)	not null,
	version	integer	not null,
	deployment_id	varchar(40)	default null,
	repository	varchar(255)	default null,
	item_desc_name	varchar(255)	default null,
	item_id	varchar(255)	default null,
	item_index	integer	default null
,constraint das_dpl_itmref_pk primary key (item_ref_id));

create index das_dpl_itmx_idx on das_depl_item_ref (item_index);
create index das_dpl_dplid_idx on das_depl_item_ref (deployment_id);

create table das_dd_markers (
	deploy_data_id	varchar(40)	not null,
	sequence_num	integer	not null,
	marker	varchar(40)	default null
,constraint das_dpl_dd_mrk_pk primary key (deploy_data_id,sequence_num)
,constraint marker_fk foreign key (marker) references das_deploy_mark (marker_id));

create index marker_idx on das_dd_markers (marker);

create table das_dep_fail_info (
	id	varchar(40)	not null,
	deployment_id	varchar(40)	not null,
	marker_id	varchar(40)	default null,
	severity	varchar(40)	default null,
	failure_message	varchar(255)	default null,
	failure_time	timestamp	default null,
	error_code	varchar(40)	default null,
	cause	varchar(20480)	default null);

commit;


