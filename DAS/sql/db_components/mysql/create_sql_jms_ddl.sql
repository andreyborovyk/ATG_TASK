


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/create_sql_jms_ddl.xml#2 $$Change: 651448 $

create table dms_client (
	client_name	varchar(250)	not null,
	client_id	bigint	null
,constraint dms_client_p primary key (client_name));


create table dms_queue (
	queue_name	varchar(250)	null,
	queue_id	bigint	not null,
	temp_id	bigint	null
,constraint dms_queue_p primary key (queue_id));


create table dms_queue_recv (
	client_id	bigint	null,
	receiver_id	bigint	not null,
	queue_id	bigint	null
,constraint dms_queue_recv_p primary key (receiver_id));


create table dms_queue_entry (
	queue_id	bigint	not null,
	msg_id	bigint	not null,
	delivery_date	bigint	null,
	handling_client_id	bigint	null,
	read_state	bigint	null
,constraint dms_queue_entry_p primary key (queue_id,msg_id));


create table dms_topic (
	topic_name	varchar(250)	null,
	topic_id	bigint	not null,
	temp_id	bigint	null
,constraint dms_topic_p primary key (topic_id));


create table dms_topic_sub (
	client_id	bigint	null,
	subscriber_name	varchar(250)	null,
	subscriber_id	bigint	not null,
	topic_id	bigint	null,
	durable	tinyint	null,
	active	tinyint	null
,constraint dms_topic_sub_p primary key (subscriber_id));


create table dms_topic_entry (
	subscriber_id	bigint	not null,
	msg_id	bigint	not null,
	delivery_date	bigint	null,
	read_state	bigint	null
,constraint dms_topic_entry_p primary key (subscriber_id,msg_id));

create index dms_topic_msg_idx on dms_topic_entry (msg_id,subscriber_id);
create index dms_topic_read_idx on dms_topic_entry (read_state,delivery_date);

create table dms_msg (
	msg_class	varchar(250)	null,
	has_properties	tinyint	null,
	reference_count	integer	null,
	msg_id	bigint	not null,
	timestamp	bigint	null,
	correlation_id	varchar(250)	null,
	reply_to	bigint	null,
	destination	bigint	null,
	delivery_mode	tinyint	null,
	redelivered	tinyint	null,
	type	varchar(250)	null,
	expiration	bigint	null,
	priority	tinyint	null,
	small_body	varbinary(250)	null,
	large_body	longblob	null
,constraint dms_msg_p primary key (msg_id));


create table dms_msg_properties (
	msg_id	bigint	not null,
	data_type	tinyint	null,
	name	varchar(250)	not null,
	value	varchar(250)	null
,constraint dms_msg_properti_p primary key (msg_id,name));

commit;
