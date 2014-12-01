


--  @version $Id: //product/DSS/version/10.0.3/templates/DSS/sql/das_mappers.xml#2 $$Change: 651448 $

create table dss_das_event (
	id	varchar(32)	not null,
	timestamp	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null);


create table dss_das_form (
	id	varchar(32)	not null,
	clocktime	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	formname	varchar(254)	null);

commit;


