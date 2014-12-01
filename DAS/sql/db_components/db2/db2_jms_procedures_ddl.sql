


create procedure dms_topic_flag
(IN Pbatch_size    bigint
,IN Pread_lock     bigint
,IN Pdelivery_date int
,IN Psubscriber_id     bigint
,OUT Pupdate_count int)
  LANGUAGE SQL
  BEGIN
     DECLARE SQLSTATE CHAR(5);
     UPDATE dms_topic_entry te 
       SET te.read_state = Pread_lock 
       WHERE te.delivery_date < Pdelivery_date 
         AND te.read_state = 0 
         AND te.subscriber_id = Psubscriber_id
         and te.subscriber_id in (select k1.subscriber_id from dms_topic_entry k1
            where k1.delivery_date < Pdelivery_date
              AND k1.read_state = 0 
              AND k1.subscriber_id = Psubscriber_id
              and k1.subscriber_id = te.subscriber_id
              and k1.msg_id = te.msg_id fetch first 5000 rows only)
         and te.msg_id in (select k2.msg_id from dms_topic_entry k2
            where k2.delivery_date < Pdelivery_date
              AND k2.read_state = 0 
              AND k2.subscriber_id = Psubscriber_id
              and k2.subscriber_id = te.subscriber_id
              and k2.msg_id = te.msg_id fetch first 5000 rows only);
     GET DIAGNOSTICS Pupdate_count = ROW_COUNT;
  END
         
@

create procedure dms_queue_flag
(IN Pbatch_size    bigint
,IN Pread_lock     bigint
,IN Pdelivery_date int
,IN Pclient_id       bigint
,IN Pqueue_id        bigint
,OUT Pupdate_count   int)
 LANGUAGE SQL
BEGIN
    DECLARE SQLSTATE CHAR(5);
    UPDATE dms_queue_entry qe 
    SET qe.handling_client_id = Pclient_id, 
        qe.read_state = Pread_lock 
    WHERE qe.handling_client_id < 0  
         AND qe.delivery_date < Pdelivery_date
         AND qe.queue_id = Pqueue_id
         and qe.queue_id in 
             (select k1.queue_id from dms_queue_entry k1
                 where k1.handling_client_id < 0
AND k1.delivery_date < Pdelivery_date
AND k1.queue_id = Pqueue_id
and k1.queue_id = qe.queue_id
and k1.msg_id = qe.msg_id fetch first 5000 rows only)
         and qe.msg_id in 
             (select k2.msg_id from dms_queue_entry k2
                 where k2.handling_client_id < 0
AND k2.delivery_date < Pdelivery_date
AND k2.queue_id = Pqueue_id
and k2.queue_id = qe.queue_id
and k2.msg_id = qe.msg_id fetch first 5000 rows only);
     GET DIAGNOSTICS Pupdate_count = ROW_COUNT;
END
         
@

commit;
