
-- the source for this section is 
-- id_generator.sql





create table das_id_generator (
	id_space_name	varchar(60)	not null,
	seed	numeric(19,0)	not null,
	batch_size	integer	not null,
	prefix	varchar(10)	default null,
	suffix	varchar(10)	default null
,constraint das_id_generator_p primary key (id_space_name));


create table das_secure_id_gen (
	id_space_name	varchar(60)	not null,
	seed	numeric(19,0)	not null,
	batch_size	integer	not null,
	ids_per_batch	integer	default null,
	prefix	varchar(10)	default null,
	suffix	varchar(10)	default null
,constraint das_secure_id_ge_p primary key (id_space_name));

commit;



-- the source for this section is 
-- cluster_name_ddl.sql





create table das_cluster_name (
	cluster_name	varchar(255)	not null,
	saved_date	bigint	not null);

commit;



-- the source for this section is 
-- dms_limbo_ddl.sql




-- This table is where limbo instance/clients identify themselves. --There should only be one entry in this table for each Dynamo running PatchBay with message delays enabled.

create table dms_limbo (
	limbo_name	varchar(250)	not null,
	limbo_id	numeric(19,0)	not null
,constraint limbo_pk primary key (limbo_name));

-- The following four tables comprise the parts of the stored messages.

create table dms_limbo_msg (
	msg_id	numeric(19,0)	not null,
	limbo_id	numeric(19,0)	not null,
	delivery_date	numeric(19,0)	not null,
	delivery_count	numeric(2,0)	not null,
	msg_src_name	varchar(250)	not null,
	port_name	varchar(250)	not null,
	msg_class	varchar(250)	not null,
	msg_class_type	numeric(1,0)	not null,
	jms_type	varchar(250)	default null,
	jms_expiration	numeric(19,0)	default null,
	jms_correlationid	varchar(250)	default null
,constraint limbo_msg_pk primary key (msg_id));

create index dms_limbo_read_idx on dms_limbo_msg (limbo_id,delivery_date);
-- If serialized reply-to destinations are known to be small enough, this binary column's size can be reduced for efficiency.  The size here has been chosen to work with local dms and SQL-JMS.

create table dms_limbo_replyto (
	msg_id	numeric(19,0)	not null,
	jms_replyto	blob(500)	default null
,constraint limbo_replyto_pk primary key (msg_id));

-- If serialized message bodies are known to be small enough, this binary column's size can be reduced for efficiency.  The size here has been chosen to work with almost all types of messages but may be larger than necessary.  Actual usage analysis is recommended for finding the optimal binary column size.

create table dms_limbo_body (
	msg_id	numeric(19,0)	not null,
	msg_body	blob(32768)	default null
,constraint limbo_body_pk primary key (msg_id));

-- This table represents a map of properties for messages stored above, i.e.  there can be multiple rows in this table for a single row in the preceeding tables.

create table dms_limbo_props (
	msg_id	numeric(19,0)	not null,
	prop_name	varchar(250)	not null,
	prop_value	varchar(250)	not null
,constraint limbo_props_pk primary key (msg_id,prop_name));


create table dms_limbo_ptypes (
	msg_id	numeric(19,0)	not null,
	prop_name	varchar(250)	not null,
	prop_type	numeric(1,0)	not null
,constraint dms_limbo_ptypes_p primary key (msg_id,prop_name));


create table dms_limbo_delay (
	msg_id	numeric(19,0)	not null,
	delay	numeric(19,0)	not null,
	max_attempts	numeric(2,0)	not null,
	failure_port	varchar(250)	not null,
	jms_timestamp	numeric(19,0)	default null,
	jms_deliverymode	numeric(10,0)	default null,
	jms_priority	numeric(10,0)	default null,
	jms_messageid	varchar(250)	default null,
	jms_redelivered	numeric(1,0)	default null,
	jms_destination	varchar(500)	default null
,constraint limbo_delay_pk primary key (msg_id));

commit;



-- the source for this section is 
-- create_sql_jms_ddl.sql





create table dms_client (
	client_name	varchar(250)	not null,
	client_id	numeric(19,0)	default null
,constraint dms_client_p primary key (client_name));


create table dms_queue (
	queue_name	varchar(250)	default null,
	queue_id	numeric(19,0)	not null,
	temp_id	numeric(19,0)	default null
,constraint dms_queue_p primary key (queue_id));


create table dms_queue_recv (
	client_id	numeric(19,0)	default null,
	receiver_id	numeric(19,0)	not null,
	queue_id	numeric(19,0)	default null
,constraint dms_queue_recv_p primary key (receiver_id));


create table dms_queue_entry (
	queue_id	numeric(19,0)	not null,
	msg_id	numeric(19,0)	not null,
	delivery_date	numeric(19,0)	default null,
	handling_client_id	numeric(19,0)	default null,
	read_state	numeric(19,0)	default null
,constraint dms_queue_entry_p primary key (queue_id,msg_id));


create table dms_topic (
	topic_name	varchar(250)	default null,
	topic_id	numeric(19,0)	not null,
	temp_id	numeric(19,0)	default null
,constraint dms_topic_p primary key (topic_id));


create table dms_topic_sub (
	client_id	numeric(19,0)	default null,
	subscriber_name	varchar(250)	default null,
	subscriber_id	numeric(19,0)	not null,
	topic_id	numeric(19,0)	default null,
	durable	numeric(1,0)	default null,
	active	numeric(1,0)	default null
,constraint dms_topic_sub_p primary key (subscriber_id));


create table dms_topic_entry (
	subscriber_id	numeric(19,0)	not null,
	msg_id	numeric(19,0)	not null,
	delivery_date	numeric(19,0)	default null,
	read_state	numeric(19,0)	default null
,constraint dms_topic_entry_p primary key (subscriber_id,msg_id));

create index dms_topic_msg_idx on dms_topic_entry (msg_id,subscriber_id);
create index dms_topic_read_idx on dms_topic_entry (read_state,delivery_date);

create table dms_msg (
	msg_class	varchar(250)	default null,
	has_properties	numeric(1,0)	default null,
	reference_count	numeric(10,0)	default null,
	msg_id	numeric(19,0)	not null,
	timestamp	numeric(19,0)	default null,
	correlation_id	varchar(250)	default null,
	reply_to	numeric(19,0)	default null,
	destination	numeric(19,0)	default null,
	delivery_mode	numeric(1,0)	default null,
	redelivered	numeric(1,0)	default null,
	type	varchar(250)	default null,
	expiration	numeric(19,0)	default null,
	priority	numeric(1,0)	default null,
	small_body	blob(250)	default null,
	large_body	blob(1048576)	default null
,constraint dms_msg_p primary key (msg_id));


create table dms_msg_properties (
	msg_id	numeric(19,0)	not null,
	data_type	numeric(1,0)	default null,
	name	varchar(250)	not null,
	value	varchar(250)	default null
,constraint dms_msg_properti_p primary key (msg_id,name));

commit;

-- the source for this section is 
-- create_staff_ddl.sql




-- SQL for creating the Dynamo Staff Repository for the GSA
-- Primary account table.
--  Type=1 is a login account.  The first_name, last_name, and password         fields are valid for this type of account.
-- Type=2 is a group account.  The description field is valid for        this type of account.
-- Type=4 is a privilege account.  The description field is valid for        this type of account.

create table das_account (
	account_name	varchar(254)	not null,
	type	integer	not null,
	first_name	varchar(254)	default null,
	last_name	varchar(254)	default null,
	password	varchar(254)	default null,
	description	varchar(254)	default null,
	lastpwdupdate	timestamp	default null
,constraint das_account_p primary key (account_name));


create table das_group_assoc (
	account_name	varchar(254)	not null,
	sequence_num	integer	not null,
	group_name	varchar(254)	not null
,constraint das_grp_asc_p primary key (account_name,sequence_num));

-- Adding the previous password information

create table das_acct_prevpwd (
	account_name	varchar(254)	not null,
	seq_num	integer	not null,
	prevpwd	varchar(35)	default null
,constraint das_prevpwd_p primary key (account_name,seq_num)
,constraint das_prvpwd_d_f foreign key (account_name) references das_account (account_name));

commit;



-- the source for this section is 
-- create_gsa_subscribers_ddl.sql





create table das_gsa_subscriber (
	id	integer	not null,
	address	varchar(50)	not null,
	port	integer	not null,
	repository	varchar(256)	not null,
	itemdescriptor	varchar(256)	not null
,constraint das_gsa_subscrib_p primary key (id));

commit;



-- the source for this section is 
-- create_sds.sql





create table das_sds (
	sds_name	varchar(50)	not null,
	curr_ds_name	varchar(50)	default null,
	dynamo_server	varchar(80)	default null,
	last_modified	timestamp	default null
,constraint das_sds_p primary key (sds_name));

commit;



-- the source for this section is 
-- integration_data_ddl.sql





create table if_integ_data (
	item_id	varchar(40)	not null,
	descriptor	varchar(64)	not null,
	repository	varchar(255)	not null,
	state	integer	not null,
	last_import	timestamp	default null,
	version	integer	not null
,constraint if_int_data_p primary key (item_id,descriptor,repository));

commit;



-- the source for this section is 
-- nucleus_security_ddl.sql





create table das_nucl_sec (
	func_name	varchar(254)	not null,
	policy	varchar(254)	not null
,constraint func_name_pk primary key (func_name));


create table das_ns_acls (
	id	varchar(254)	not null,
	index_num	integer	not null,
	acl	varchar(254)	not null
,constraint id_index_pk primary key (id,index_num));

commit;



-- the source for this section is 
-- media_ddl.sql




--     media content repository tables.  

create table media_folder (
	folder_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	timestamp	default null,
	description	varchar(254)	default null,
	name	varchar(254)	not null,
	path	varchar(254)	not null,
	parent_folder_id	varchar(40)	default null
,constraint md_folder_p primary key (folder_id)
,constraint md_foldparnt_fl_f foreign key (parent_folder_id) references media_folder (folder_id));

create index fldr_mfldrid_idx on media_folder (parent_folder_id);
create index md_fldr_path_idx on media_folder (path);

create table media_base (
	media_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	timestamp	default null,
	description	varchar(254)	default null,
	name	varchar(254)	not null,
	path	varchar(254)	not null,
	parent_folder_id	varchar(40)	not null,
	media_type	integer	default null
,constraint media_p primary key (media_id)
,constraint medparnt_fl_f foreign key (parent_folder_id) references media_folder (folder_id));

create index med_mfldrid_idx on media_base (parent_folder_id);
create index media_path_idx on media_base (path);
create index media_type_idx on media_base (media_type);

create table media_ext (
	media_id	varchar(40)	not null,
	url	varchar(254)	not null
,constraint media_ext_p primary key (media_id)
,constraint medxtmed_d_f foreign key (media_id) references media_base (media_id));


create table media_bin (
	media_id	varchar(40)	not null,
	length	integer	not null,
	last_modified	timestamp	not null,
	data	blob(1048576)	not null
,constraint media_bin_p primary key (media_id)
,constraint medbnmed_d_f foreign key (media_id) references media_base (media_id));


create table media_txt (
	media_id	varchar(40)	not null,
	length	integer	not null,
	last_modified	timestamp	not null,
	data	varchar(20480)	not null
,constraint media_txt_p primary key (media_id)
,constraint medtxtmed_d_f foreign key (media_id) references media_base (media_id));

commit;



-- the source for this section is 
-- deployment_ddl.sql




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



-- the source for this section is 
-- sitemap_ddl.sql




-- Table for siteindex repository item

create table das_siteindex (
	siteindex_id	varchar(40)	not null,
	lastmod	timestamp	default null,
	filename	varchar(100)	default null,
	xml	clob	default null
,constraint siteindex_pk primary key (siteindex_id));

-- Table for sitemap repository item

create table das_sitemap (
	sitemap_id	varchar(40)	not null,
	lastmod	timestamp	default null,
	filename	varchar(100)	default null,
	xml	clob	default null
,constraint sitemap_pk primary key (sitemap_id));

commit;



-- the source for this section is 
-- seo_ddl.sql




-- Table for seo-tag repository item

create table das_seo_tag (
	seo_tag_id	varchar(40)	not null,
	display_name	varchar(100)	default null,
	title	varchar(254)	default null,
	description	varchar(254)	default null,
	keywords	varchar(254)	default null,
	content_key	varchar(100)	default null
,constraint das_seo_tag_pk primary key (seo_tag_id));


create table das_seo_sites (
	seo_tag_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint das_seo_site_pk primary key (seo_tag_id,site_id));

commit;



-- the source for this section is 
-- site_ddl.sql




-- This file contains create table statements, which will configureyour database for use MultiSite

create table site_template (
	id	varchar(40)	not null,
	name	varchar(254)	not null,
	description	varchar(254)	not null,
	item_mapping_id	varchar(40)	not null
,constraint site_template1_p primary key (id));


create table site_configuration (
	id	varchar(40)	not null,
	name	varchar(254)	not null,
	description	varchar(254)	default null,
	template	varchar(40)	default null,
	production_url	varchar(254)	default null,
	enabled	numeric(1)	not null,
	site_down_url	varchar(254)	default null,
	open_date	timestamp	default null,
	pre_opening_url	varchar(254)	default null,
	closing_date	timestamp	default null,
	post_closing_url	varchar(254)	default null,
	modification_time	timestamp	default null,
	creation_time	timestamp	default null,
	author	varchar(254)	default null,
	last_modified_by	varchar(254)	default null,
	site_icon	varchar(254)	default null,
	favicon	varchar(254)	default null,
	site_priority	integer	default null,
	context_root	varchar(254)	default null
,constraint site_configurat1_p primary key (id));


create table site_additional_urls (
	id	varchar(40)	not null,
	additional_production_url	varchar(254)	default null,
	idx	integer	not null
,constraint siteadditio_url1_p primary key (id,idx));


create table site_types (
	id	varchar(40)	not null,
	site_type	varchar(254)	not null
,constraint site_types1_p primary key (id,site_type));


create table site_group (
	id	varchar(40)	not null,
	display_name	varchar(254)	not null
,constraint site_group1_p primary key (id));


create table site_group_sites (
	site_id	varchar(40)	not null,
	site_group_id	varchar(40)	not null
,constraint site_group_sites_p primary key (site_id,site_group_id)
,constraint site_group_site1_f foreign key (site_id) references site_configuration (id)
,constraint site_group_site2_f foreign key (site_group_id) references site_group (id));

create index site_group_site1_x on site_group_sites (site_id);
create index site_group_site2_x on site_group_sites (site_group_id);

create table site_group_shareable_types (
	shareable_types	varchar(254)	not null,
	site_group_id	varchar(40)	not null
,constraint site_group_share_p primary key (shareable_types,site_group_id)
,constraint site_group_shar1_f foreign key (site_group_id) references site_group (id));

create index site_group_shar1_x on site_group_shareable_types (site_group_id);
commit;


