


--  @version $ $

create table dss_user_bpmarkers (
	marker_id	varchar2(40)	not null,
	profile_id	varchar2(40)	not null,
	marker_key	varchar2(100)	not null,
	marker_value	varchar2(100)	null,
	marker_data	varchar2(100)	null,
	creation_date	timestamp	null,
	version	number(10)	not null,
	marker_type	number(10)	null
,constraint dssprofilebp_p primary key (marker_id,profile_id));




