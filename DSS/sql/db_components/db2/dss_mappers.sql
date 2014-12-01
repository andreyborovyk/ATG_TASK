


--  @version $Id: //product/DSS/version/10.0.3/templates/DSS/sql/dss_mappers.xml#2 $$Change: 651448 $

create table dss_audit_trail (
	id	varchar(32)	not null,
	timestamp	timestamp	default null,
	label	varchar(255)	default null,
	profileid	varchar(25)	default null,
	segmentName	varchar(254)	default null,
	processName	varchar(254)	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null);

commit;


