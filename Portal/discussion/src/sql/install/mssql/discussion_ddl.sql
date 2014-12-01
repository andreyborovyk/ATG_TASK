


--  @version $Id: //app/portal/version/10.0.3/discussion/sql/discussion_ddl.xml#3 $$Change: 655658 $

create table dtg_board (
	id	varchar(40)	not null,
	name	varchar(40)	not null,
	description	varchar(255)	null,
	owner	varchar(40)	null,
	delete_flag	numeric(1,0)	not null,
	board_type	integer	not null,
	num_posts	integer	not null,
	last_post_time	datetime	not null,
	creation_date	datetime	not null,
	version	integer	default 0 not null
,constraint dtg_boardpk primary key (id)
,constraint dlt_flag_is_bool check (delete_flag in (0,1)))


create table dtg_thread (
	id	varchar(40)	not null,
	userid	varchar(40)	null,
	subject	varchar(100)	not null,
	content	varchar(2000)	null,
	message_board	varchar(40)	not null,
	parent_thread	varchar(40)	null,
	ultimate_thread	varchar(40)	null,
	children_qty	integer	null,
	topic_resp_flag	numeric(1,0)	not null,
	delete_flag	numeric(1,0)	not null,
	creation_date	datetime	not null,
	version	integer	default 0 not null
,constraint dtg_thread_p primary key (id)
,constraint dtg_thread1_f foreign key (message_board) references dtg_board (id)
,constraint dtg_thread2_f foreign key (parent_thread) references dtg_thread (id)
,constraint dtg_thread3_f foreign key (ultimate_thread) references dtg_thread (id)
,constraint resp_flag_enum check (topic_resp_flag in (0,1,2))
,constraint thr_dlt_flag_bool check (delete_flag in (0,1)))

create index dtg_threadboardix on dtg_thread (message_board)
create index dtg_threadparentix on dtg_thread (parent_thread)
create index dtg_threadultix on dtg_thread (ultimate_thread)

create table dtg_gear_boards (
	id	varchar(40)	not null,
	gear_id	varchar(40)	not null,
	message_board_id	varchar(40)	not null
,constraint dtg_gearboardspk primary key (gear_id,message_board_id)
,constraint dtg_gear_boards1_f foreign key (message_board_id) references dtg_board (id))

create index dtg_gearboardix on dtg_gear_boards (message_board_id)

create table dtg_usr_gear_board (
	id	varchar(40)	not null,
	gear_id	varchar(40)	not null,
	message_board_id	varchar(40)	not null,
	user_id	varchar(40)	not null
,constraint dtg_usrgearbrdpk primary key (user_id,gear_id,message_board_id)
,constraint dtg_usr_gear_bo1_f foreign key (message_board_id) references dtg_board (id))

create index dtg_usrgrbrdbrdix on dtg_usr_gear_board (message_board_id)

      
go
        
      
create view dtg_view_search
(gear_id,id,userid,subject,content,message_board,parent_thread,ultimate_thread,children_qty,topic_resp_flag,delete_flag,creation_date)
as
  SELECT b.gear_id, t.id, t.userid, t.subject, t.content, t.message_board,
    t. parent_thread, t. ultimate_thread, t.children_qty, t.topic_resp_flag,
    t.delete_flag, t.creation_date
  FROM
    dtg_thread t, dtg_gear_boards b 
  WHERE t.message_board = b.message_board_id
         
go


