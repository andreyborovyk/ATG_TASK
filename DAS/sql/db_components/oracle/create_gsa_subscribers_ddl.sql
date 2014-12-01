


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/create_gsa_subscribers_ddl.xml#2 $$Change: 651448 $

create table das_gsa_subscriber (
	id	integer	not null,
	address	varchar2(50)	not null,
	port	integer	not null,
	repository	varchar2(256)	not null,
	itemdescriptor	varchar2(256)	not null
,constraint das_gsa_subscrib_p primary key (id));




