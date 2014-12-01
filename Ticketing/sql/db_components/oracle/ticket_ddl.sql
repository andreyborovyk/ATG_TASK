


--  @version $Id: //product/Ticketing/version/10.0.3/src/sql/ticket_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements, which will configure your database for use with the new ticketing schema.
-- Ticketing sub status

create table tkt_sub_status (
	sub_status_id	number(10)	not null,
	sub_status_name	varchar2(80)	not null,
	parent_status	number(10)	not null
,constraint tkt_sub_status_p primary key (sub_status_id));

-- Ticketing reason

create table tkt_reason (
	reason_id	number(10)	not null,
	reason_name	varchar2(80)	not null,
	description	varchar2(254)	null
,constraint tkt_reason_p primary key (reason_id));

-- Activity Data table

create table tkt_ads_act_data (
	act_data_id	varchar2(40)	not null,
	creation_time	timestamp	not null,
	type	number(10)	not null
,constraint tkt_ads_act_data_p primary key (act_data_id));

-- Ticketing customer details

create table tkt_cust_details (
	cust_details_id	varchar2(40)	not null,
	first_name	varchar2(40)	null,
	last_name	varchar2(40)	null,
	full_name	varchar2(80)	null,
	email_address	varchar2(254)	null,
	phone_numbers	varchar2(254)	null,
	mobile	varchar2(254)	null,
	address	varchar2(254)	null,
	city	varchar2(254)	null,
	state_code	varchar2(254)	null,
	country	varchar2(254)	null,
	postal_code	varchar2(254)	null
,constraint tkt_customer_p primary key (cust_details_id));

-- Fundmental ticket table

create table tkt_ticket (
	ticket_id	varchar2(40)	not null,
	creation_time	timestamp	not null,
	last_mod_time	timestamp	null,
	description	varchar2(254)	null,
	extern_ticket_id	varchar2(254)	null,
	external_system	varchar2(40)	null,
	inbound_chan_addr	varchar2(254)	null,
	application	number(10)	null,
	is_mergeable	number(1)	not null,
	creation_channel	number(10)	not null,
	default_out_chan	varchar2(40)	not null,
	sub_status_id	number(10)	not null,
	escalation_level	number(10)	not null,
	escalation_count	number(10)	not null,
	has_pending_owner	number(1)	not null,
	pending_time	timestamp	null,
	release_time	timestamp	null,
	priority	number(10)	not null,
	user_id	varchar2(40)	null,
	cust_details_id	varchar2(40)	null,
	due_time	timestamp	null,
	owning_group_id	varchar2(40)	null,
	group_assn_act_id	varchar2(40)	null,
	owning_agent_id	varchar2(40)	null,
	agent_assn_act_id	varchar2(40)	null,
	orig_ticket_id	varchar2(40)	null,
	dup_of_ticket_id	varchar2(40)	null,
	work_in_progress	blob	null,
	in_progress_act_id	varchar2(40)	null,
	sla_base_timestamp	timestamp	null,
	pushable	number(1)	default 0 not null,
	loaded	number(1)	null,
	loaded_timestamp	timestamp	null
,constraint tkt_ticket_p primary key (ticket_id)
,constraint tkt_ticket_cust_f foreign key (cust_details_id) references tkt_cust_details (cust_details_id)
,constraint tkt_ticket_orig_f foreign key (orig_ticket_id) references tkt_ticket (ticket_id)
,constraint tkt_ticket_ndup_f foreign key (dup_of_ticket_id) references tkt_ticket (ticket_id)
,constraint tkt_ticket_sub_f foreign key (sub_status_id) references tkt_sub_status (sub_status_id));

create index tkt_ticket_cus1_ix on tkt_ticket (cust_details_id);
create index tkt_ticket_cus4_ix on tkt_ticket (orig_ticket_id);
create index tkt_ticket_cus5_ix on tkt_ticket (dup_of_ticket_id);
create index tkt_ticket_cus6_ix on tkt_ticket (sub_status_id);
create index tkt_pending_tm_ix on tkt_ticket (pending_time);
create index tkt_release_tm_ix on tkt_ticket (release_time);
create index tkt_own_agent_ix on tkt_ticket (owning_agent_id);
-- The base ticket activity

create table tkt_activity (
	activity_id	varchar2(40)	not null,
	ticket_id	varchar2(40)	not null,
	type	number(10)	null,
	creation_time	timestamp	not null,
	agent_profile_id	varchar2(40)	null,
	application	number(10)	null,
	heading	varchar2(254)	null,
	abstract	varchar2(4000)	null,
	reason_id	number(10)	null,
	in_progress	number(1)	null,
	act_data_id	varchar2(40)	null
,constraint tkt_activity_p primary key (activity_id)
,constraint tkt_act_ticket_f foreign key (ticket_id) references tkt_ticket (ticket_id)
,constraint tkt_act_reason_f foreign key (reason_id) references tkt_reason (reason_id)
,constraint tkt_act_data_f foreign key (act_data_id) references tkt_ads_act_data (act_data_id));

create index tkt_act_ticket1_ix on tkt_activity (ticket_id);
create index tkt_act_reason_ix on tkt_activity (reason_id);
create index tkt_act_data_ix on tkt_activity (act_data_id);
-- Ticket Queue Assignment Activity

create table tkt_act_owngrp (
	activity_id	varchar2(40)	not null,
	owning_group_id	varchar2(40)	null,
	sla_minutes	number(10)	not null,
	completion_time	timestamp	null
,constraint tkt_act_owngrp_p primary key (activity_id)
,constraint tkt_a_grp_act_f foreign key (activity_id) references tkt_activity (activity_id));

-- Owning Agent Assignment Activity

create table tkt_act_ownagnt (
	activity_id	varchar2(40)	not null,
	owning_agent_id	varchar2(40)	null,
	completion_time	timestamp	null,
	is_push	number(1)	not null
,constraint tkt_act_ownagnt_p primary key (activity_id)
,constraint tkt_a_agnt_act_f foreign key (activity_id) references tkt_activity (activity_id));

-- Work Note Activity

create table tkt_act_worknote (
	activity_id	varchar2(40)	not null,
	text_content	varchar2(4000)	null,
	is_public	number(1)	not null,
	user_id	varchar2(40)	null
,constraint tkt_act_wrknt_p primary key (activity_id)
,constraint tkt_a_wrknt_act_f foreign key (activity_id) references tkt_activity (activity_id));

-- Status Change Activity

create table tkt_act_statc (
	activity_id	varchar2(40)	not null,
	prev_sub_status	number(10)	null,
	new_sub_status	number(10)	not null
,constraint tkt_act_statc_p primary key (activity_id)
,constraint tkt_a_statc_act_f foreign key (activity_id) references tkt_activity (activity_id)
,constraint tkt_a_statc_sub_f foreign key (new_sub_status) references tkt_sub_status (sub_status_id)
,constraint tkt_a_statc_su2_f foreign key (prev_sub_status) references tkt_sub_status (sub_status_id));

create index tkt_a_statc_su1_ix on tkt_act_statc (new_sub_status);
create index tkt_a_statc_su2_ix on tkt_act_statc (prev_sub_status);

create table tkt_act_escal (
	activity_id	varchar2(40)	not null,
	prev_esc_level	number(10)	null,
	new_esc_level	number(10)	not null
,constraint tkt_act_esc_p primary key (activity_id)
,constraint tkt_a_esc_act_f foreign key (activity_id) references tkt_activity (activity_id));

-- Outbound Message Activity

create table tkt_act_message (
	activity_id	varchar2(40)	not null,
	channel	number(10)	null,
	from_address	varchar2(254)	null,
	has_attachments	number(1)	null,
	to_address	varchar2(254)	null,
	cc_address	varchar2(254)	null,
	bcc_address	varchar2(254)	null,
	reply_to_addr	varchar2(254)	null,
	subject	varchar2(254)	null,
	body_text	clob	null,
	due_time	timestamp	null,
	char_encoding	varchar2(20)	null,
	is_text_only	number(1)	null
,constraint tkt_act_mess_p primary key (activity_id)
,constraint tkt_a_mess_act_f foreign key (activity_id) references tkt_activity (activity_id));

-- Attachment Activity

create table tkt_attachment (
	attachment_id	varchar2(40)	not null,
	filename	varchar2(254)	not null,
	mime_type	varchar2(254)	not null,
	encoding	varchar2(254)	not null,
	encoded_content	blob	not null
,constraint tkt_attach_p primary key (attachment_id));

-- Profile Created Activity

create table tkt_act_pcreate (
	id	varchar2(40)	not null,
	profile_id	varchar2(40)	not null);

-- Password Changed Activity

create table tkt_act_pswchange (
	id	varchar2(40)	not null,
	profile_id	varchar2(40)	not null);

-- Profile Update Activity

create table tkt_update_prof (
	id	varchar2(40)	not null,
	updated_item_id	varchar2(40)	null);

-- Property Updates

create table tkt_upd_props (
	id	varchar2(40)	not null,
	update_type	number(10)	not null,
	activity_id	varchar2(40)	null,
	property_name	varchar2(100)	null,
	old_value	varchar2(255)	null,
	new_value	varchar2(255)	null,
	version	number(10)	not null
,constraint csrt_upd_prof_p primary key (id));

-- List of Ticket Attachments

create table tkt_attch_list (
	activity_id	varchar2(40)	not null,
	idx	number(10)	not null,
	attachment_id	varchar2(40)	not null
,constraint tkt_attchl_p primary key (activity_id,idx)
,constraint tkt_attchl_a_f foreign key (activity_id) references tkt_activity (activity_id)
,constraint tkt_attchl_at_f foreign key (attachment_id) references tkt_attachment (attachment_id));

create index tkt_attchl_at1_ix on tkt_attch_list (attachment_id);
-- Related Tickets

create table tkt_related (
	ticket_id	varchar2(40)	not null,
	related_ticket_id	varchar2(40)	not null,
	idx	number(10)	not null
,constraint tkt_related_p primary key (ticket_id,idx)
,constraint tkt_related_t_f foreign key (ticket_id) references tkt_ticket (ticket_id)
,constraint tkt_related_t2_f foreign key (related_ticket_id) references tkt_ticket (ticket_id));

create index tkt_related_t2_ix on tkt_related (related_ticket_id);
-- External Reference

create table tkt_ext_ref (
	external_ref_id	varchar2(40)	not null,
	extern_ref	varchar2(254)	not null,
	description	varchar2(254)	null,
	last_used_time	timestamp	null,
	activity_id	varchar2(40)	null
,constraint tkt_ext_ref_p primary key (external_ref_id)
,constraint tkt_extr_a_f foreign key (activity_id) references tkt_activity (activity_id));

create index tkt_extr_a1_ix on tkt_ext_ref (activity_id);

create table tkt_extref_list (
	ticket_id	varchar2(40)	not null,
	idx	number(10)	not null,
	external_ref_id	varchar2(40)	not null
,constraint tkt_extrl_p primary key (ticket_id,idx)
,constraint tkt_extrl_t_f foreign key (ticket_id) references tkt_ticket (ticket_id)
,constraint tkt_extrl_x_f foreign key (external_ref_id) references tkt_ext_ref (external_ref_id));

create index tkt_extrl_x1_ix on tkt_extref_list (external_ref_id);

create table tkt_act_map (
	activity_id	varchar2(40)	not null,
	key_name	varchar2(254)	not null,
	value_text	varchar2(254)	not null
,constraint tkt_actmap_a_f foreign key (activity_id) references tkt_activity (activity_id));

create index tkt_actmap_a1_ix on tkt_act_map (activity_id);
-- Ticketing reason context

create table tkt_rea_context (
	rea_context_id	number(10)	not null,
	context_name	varchar2(80)	not null,
	description	varchar2(254)	null,
	default_reason_id	number(10)	null
,constraint tkt_rea_context_p primary key (rea_context_id)
,constraint tkt_reactx_rea_f foreign key (default_reason_id) references tkt_reason (reason_id));

create index tkt_reactx_rea1_x on tkt_rea_context (default_reason_id);

create table tkt_rea_ctx_list (
	reason_id	number(10)	not null,
	idx	number(10)	not null,
	rea_context_id	number(10)	not null
,constraint tkt_reactxl_p primary key (rea_context_id,idx)
,constraint tkt_reactxl_r_f foreign key (reason_id) references tkt_reason (reason_id)
,constraint tkt_reactxl_c_f foreign key (rea_context_id) references tkt_rea_context (rea_context_id));

create index tkt_reactxl_r1_ix on tkt_rea_ctx_list (reason_id);

create table tkt_q_stats (
	tkt_q_stats_id	number(10)	not null,
	tkt_q_id	varchar2(40)	not null,
	request_count	number(10)	not null,
	hit_count	number(10)	not null,
	miss_count	number(10)	not null,
	release_count	number(10)	not null
,constraint tkt_q_stats_p primary key (tkt_q_stats_id));


create table tkt_dist_srv_stat (
	server_stats_id	number(10)	not null,
	server_id	varchar2(40)	not null,
	update_duration	number(19)	not null,
	last_updated	timestamp	not null
,constraint server_stats_p primary key (server_stats_id));


create table tkt_q_stat_set (
	server_stats_id	number(10)	not null,
	tkt_q_stats_id	number(10)	not null
,constraint srv_tkt_q_p primary key (server_stats_id,tkt_q_stats_id)
,constraint svr_tkt_q_srv_f foreign key (server_stats_id) references tkt_dist_srv_stat (server_stats_id)
,constraint svr_tkt_q_f foreign key (tkt_q_stats_id) references tkt_q_stats (tkt_q_stats_id));

create index svr_tkt_q_srv_ix on tkt_q_stat_set (tkt_q_stats_id);



