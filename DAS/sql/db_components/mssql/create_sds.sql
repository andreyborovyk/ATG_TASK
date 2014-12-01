


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/create_sds.xml#2 $$Change: 651448 $

create table das_sds (
	sds_name	varchar(50)	not null,
	curr_ds_name	varchar(50)	null,
	dynamo_server	varchar(80)	null,
	last_modified	datetime	null
,constraint das_sds_p primary key (sds_name))



go
