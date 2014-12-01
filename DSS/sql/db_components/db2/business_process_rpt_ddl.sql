


--  @version $Id: //product/DSS/version/10.0.3/templates/DSS/sql/business_process_rpt_ddl.xml#2 $$Change: 651448 $

create table drpt_stage_reached (
	id	varchar(40)	not null,
	owner_id	varchar(40)	not null,
	process_start_time	timestamp	not null,
	event_time	timestamp	not null,
	bp_name	varchar(255)	not null,
	bp_stage	varchar(255)	default null,
	is_transient	numeric(1,0)	not null,
	bp_stage_sequence	integer	not null
,constraint drpt_bpstage_c check (is_transient in (0,1)));

commit;


