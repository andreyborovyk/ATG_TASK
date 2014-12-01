


--  @version $Id: //product/DSS/version/10.0.3/templates/DSS/sql/dss_mappers.xml#2 $$Change: 651448 $

create table dss_audit_trail (
	id	varchar(32)	not null,
	timestamp	datetime	null,
	label	nvarchar(255)	null,
	profileid	varchar(25)	null,
	segmentName	nvarchar(254)	null,
	processName	nvarchar(254)	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null);

commit;


