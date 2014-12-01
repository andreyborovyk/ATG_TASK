


--  @version $Id: //product/DSS/version/10.0.3/templates/DSS/sql/business_process_rpt_ddl.xml#2 $$Change: 651448 $

create table drpt_stage_reached (
	id	varchar2(40)	not null,
	owner_id	varchar2(40)	not null,
	process_start_time	date	not null,
	event_time	date	not null,
	bp_name	varchar2(255)	not null,
	bp_stage	varchar2(255)	null,
	is_transient	number(1,0)	not null,
	bp_stage_sequence	number(10)	not null
,constraint drpt_bpstage_c check (is_transient in (0,1)));




