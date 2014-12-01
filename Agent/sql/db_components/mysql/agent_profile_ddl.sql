


--  @version $ $

create table agent_profile_cmts (
	comment_id	varchar(40)	not null,
	profile_id	varchar(40)	not null,
	agent_id	varchar(40)	null,
	comment_data	varchar(2500)	not null,
	creation_date	datetime	null,
	version	integer	not null
,constraint agtprofilecmmt_p primary key (comment_id,profile_id)
,constraint agtprofilecmmt_f foreign key (profile_id) references dps_user (id));

create index agtprofilecmmt1_x on agent_profile_cmts (profile_id);
commit;


