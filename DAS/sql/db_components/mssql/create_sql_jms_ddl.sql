


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/create_sql_jms_ddl.xml#2 $$Change: 651448 $

create table dms_client (
	client_name	varchar(250)	not null,
	client_id	numeric(19,0)	null
,constraint dms_client_p primary key (client_name))


create table dms_queue (
	queue_name	varchar(250)	null,
	queue_id	numeric(19,0)	not null,
	temp_id	numeric(19,0)	null
,constraint dms_queue_p primary key (queue_id))


create table dms_queue_recv (
	client_id	numeric(19,0)	null,
	receiver_id	numeric(19,0)	not null,
	queue_id	numeric(19,0)	null
,constraint dms_queue_recv_p primary key (receiver_id))


create table dms_queue_entry (
	queue_id	numeric(19,0)	not null,
	msg_id	numeric(19,0)	not null,
	delivery_date	numeric(19,0)	null,
	handling_client_id	numeric(19,0)	null,
	read_state	numeric(19,0)	null
,constraint dms_queue_entry_p primary key (queue_id,msg_id))


create table dms_topic (
	topic_name	varchar(250)	null,
	topic_id	numeric(19,0)	not null,
	temp_id	numeric(19,0)	null
,constraint dms_topic_p primary key (topic_id))


create table dms_topic_sub (
	client_id	numeric(19,0)	null,
	subscriber_name	varchar(250)	null,
	subscriber_id	numeric(19,0)	not null,
	topic_id	numeric(19,0)	null,
	durable	numeric(1,0)	null,
	active	numeric(1,0)	null
,constraint dms_topic_sub_p primary key (subscriber_id))


create table dms_topic_entry (
	subscriber_id	numeric(19,0)	not null,
	msg_id	numeric(19,0)	not null,
	delivery_date	numeric(19,0)	null,
	read_state	numeric(19,0)	null
,constraint dms_topic_entry_p primary key (subscriber_id,msg_id))

create index dms_topic_msg_idx on dms_topic_entry (msg_id,subscriber_id)
create index dms_topic_read_idx on dms_topic_entry (read_state,delivery_date)

create table dms_msg (
	msg_class	varchar(250)	null,
	has_properties	numeric(1,0)	null,
	reference_count	numeric(10,0)	null,
	msg_id	numeric(19,0)	not null,
	timestamp	numeric(19,0)	null,
	correlation_id	varchar(250)	null,
	reply_to	numeric(19,0)	null,
	destination	numeric(19,0)	null,
	delivery_mode	numeric(1,0)	null,
	redelivered	numeric(1,0)	null,
	type	varchar(250)	null,
	expiration	numeric(19,0)	null,
	priority	numeric(1,0)	null,
	small_body	binary(250)	null,
	large_body	image	null
,constraint dms_msg_p primary key (msg_id))


create table dms_msg_properties (
	msg_id	numeric(19,0)	not null,
	data_type	numeric(1,0)	null,
	name	varchar(200)	not null,
	value	varchar(250)	null
,constraint dms_msg_properti_p primary key (msg_id,name))


go
      
create procedure dms_queue_flag
(@batchSize    INT
,@readLock     NUMERIC(19)
,@deliveryDate NUMERIC(19)
,@clientID     NUMERIC(19)
,@queueID      NUMERIC(19)
,@updateCount  NUMERIC(19) OUTPUT)
as
  SET ROWCOUNT @batchSize
  UPDATE dms_queue_entry 
  SET handling_client_id = @clientID, 
      read_state = @readLock 
  WHERE queue_id = @queueID
    AND handling_client_id < 0 
    AND delivery_date < @deliveryDate
  SELECT @updateCount = @@ROWCOUNT 
         
go

create procedure dms_topic_flag
(@batchSize    INT
,@readLock     NUMERIC(19)
,@deliveryDate NUMERIC(19)
,@subscriberID NUMERIC(19)
,@updateCount  NUMERIC(19) OUTPUT)
as
  SET ROWCOUNT @batchSize
  UPDATE dms_topic_entry 
  SET read_state = @readLock 
  WHERE subscriber_id = @subscriberID
    AND delivery_date < @deliveryDate 
    AND read_state = 0 
  SELECT @updateCount = @@ROWCOUNT 
         
go


