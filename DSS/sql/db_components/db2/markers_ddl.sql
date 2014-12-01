


--  @version $ $

create table dps_markers (
	marker_id	varchar(40)	not null,
	marker_key	varchar(100)	not null,
	marker_value	varchar(100)	default null,
	marker_data	varchar(100)	default null,
	creation_date	timestamp	default null,
	version	integer	not null,
	marker_type	integer	default null
,constraint dps_markers_p primary key (marker_id));


create table dps_usr_markers (
	profile_id	varchar(40)	not null,
	marker_id	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dps_usr_markers_p primary key (profile_id,sequence_num)
,constraint dpsusrmarkers_u_f foreign key (profile_id) references dps_user (id)
,constraint dpsusrmarkers_m_f foreign key (marker_id) references dps_markers (marker_id));

create index dpsusrmarkers1_ix on dps_usr_markers (marker_id);
commit;


