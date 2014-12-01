


--  Contains tables for saving click to connect session details @version $Id: //product/ClickToConnect/version/10.0.3/src/sql/click_to_connect_ddl.xml#2 $$Change: 651448 $

create table c2c_session_data (
	c2c_session_id	varchar(60)	not null,
	session_type	integer	not null,
	profile_id	varchar(40)	not null,
	session_start_time	timestamp	not null,
	site_id	varchar(40)	default null
,constraint c2c_id_p primary key (c2c_session_id));

commit;


