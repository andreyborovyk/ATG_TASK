


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/create_sql_jms_ddl.xml#2 $$Change: 651448 $

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
