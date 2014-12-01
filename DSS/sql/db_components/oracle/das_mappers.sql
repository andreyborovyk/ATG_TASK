


--  @version $Id: //product/DSS/version/10.0.3/templates/DSS/sql/das_mappers.xml#2 $$Change: 651448 $

create table dss_das_event (
	id	varchar2(32)	not null,
	timestamp	date	null,
	sessionid	varchar2(100)	null,
	parentsessionid	varchar2(100)	null);


create table dss_das_form (
	id	varchar2(32)	not null,
	clocktime	date	null,
	sessionid	varchar2(100)	null,
	parentsessionid	varchar2(100)	null,
	formname	varchar2(254)	null);




