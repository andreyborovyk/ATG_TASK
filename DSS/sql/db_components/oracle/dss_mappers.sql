


--  @version $Id: //product/DSS/version/10.0.3/templates/DSS/sql/dss_mappers.xml#2 $$Change: 651448 $

create table dss_audit_trail (
	id	varchar2(32)	not null,
	timestamp	date	null,
	label	varchar2(255)	null,
	profileid	varchar2(25)	null,
	segmentName	varchar2(254)	null,
	processName	varchar2(254)	null,
	sessionid	varchar2(100)	null,
	parentsessionid	varchar2(100)	null);




