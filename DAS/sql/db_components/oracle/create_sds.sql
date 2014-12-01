


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/create_sds.xml#2 $$Change: 651448 $

create table das_sds (
	sds_name	varchar2(50)	not null,
	curr_ds_name	varchar2(50)	null,
	dynamo_server	varchar2(80)	null,
	last_modified	date	null
,constraint das_sds_p primary key (sds_name));




