


--  @version $ $

create table dps_markers (
	marker_id	varchar2(40)	not null,
	marker_key	varchar2(100)	not null,
	marker_value	varchar2(100)	null,
	marker_data	varchar2(100)	null,
	creation_date	timestamp	null,
	version	number(10)	not null,
	marker_type	number(10)	null
,constraint dps_markers_p primary key (marker_id));


create table dps_usr_markers (
	profile_id	varchar2(40)	not null,
	marker_id	varchar2(40)	not null,
	sequence_num	number(10)	not null
,constraint dps_usr_markers_p primary key (profile_id,sequence_num)
,constraint dpsusrmarkers_u_f foreign key (profile_id) references dps_user (id)
,constraint dpsusrmarkers_m_f foreign key (marker_id) references dps_markers (marker_id));

create index dpsusrmarkers1_ix on dps_usr_markers (marker_id);



