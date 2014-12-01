


--  @version $Id: //app/portal/version/10.0.3/paf/sql/membership_ddl.xml#2 $$Change: 651448 $

create table mem_membership_req (
	id	varchar2(40)	not null,
	internal_version	number(10)	default 0 not null,
	user_id	varchar2(40)	not null,
	community_id	varchar2(40)	not null,
	request_type	number(1,0)	not null,
	creation_date	timestamp	not null
,constraint mem_membershiprq_p primary key (id)
,constraint mem_membershiprq_c check (request_type in (0,1)));




