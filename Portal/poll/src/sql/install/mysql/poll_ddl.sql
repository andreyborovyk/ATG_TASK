


--  @version $Id: //app/portal/version/10.0.3/poll/sql/poll_ddl.xml#2 $$Change: 651448 $

create table plg_poll (
	id	nvarchar(40)	not null,
	title	nvarchar(100)	not null,
	question_text	nvarchar(500)	null,
	creation_date	datetime	not null,
	version	integer	default 0 not null
,constraint plg_poll_pk primary key (id));


create table plg_poll_response (
	id	nvarchar(40)	not null,
	poll_id	nvarchar(40)	not null,
	response_text	nvarchar(200)	not null,
	short_name	nvarchar(50)	null,
	bar_color	nvarchar(6)	null,
	sort_order	integer	null,
	response_count	integer	null,
	version	integer	default 0 not null
,constraint plg_poll_rsp_pk primary key (id)
,constraint plg_poll_rsp_fk foreign key (poll_id) references plg_poll (id));

create index plg_pollrespix on plg_poll_response (poll_id);

create table plg_gear_poll_rel (
	id	nvarchar(40)	not null,
	gear_id	nvarchar(40)	not null,
	poll_id	nvarchar(40)	not null,
	version	integer	default 0 not null
,constraint plg_poll_rel_pk primary key (poll_id,gear_id)
,constraint plg_poll_rel_fk foreign key (poll_id) references plg_poll (id));


create table plg_vote_mapper (
	id	nvarchar(40)	not null,
	tstamp	datetime	null,
	poll_id	nvarchar(40)	null,
	poll_selection	nvarchar(40)	null,
	gear_id	nvarchar(40)	null,
	community_id	nvarchar(40)	null,
	user_id	nvarchar(40)	null,
	msg_type	nvarchar(255)	null);

create index plg_vtmp_comid on plg_vote_mapper (community_id);
create index plg_vtmp_gearid on plg_vote_mapper (gear_id);
create index plg_vtmp_usrid on plg_vote_mapper (user_id);
commit;


