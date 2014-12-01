


--  @version $ $

create table dss_user_bpmarkers (
	marker_id	varchar(40)	not null,
	profile_id	varchar(40)	not null,
	marker_key	varchar(100)	not null,
	marker_value	varchar(100)	null,
	marker_data	varchar(100)	null,
	creation_date	datetime	null,
	version	integer	not null,
	marker_type	integer	null
,constraint dssprofilebp_p primary key (marker_id,profile_id));

commit;


