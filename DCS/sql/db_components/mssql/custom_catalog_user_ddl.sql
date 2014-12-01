


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/custom_catalog_user_ddl.xml#2 $$Change: 651448 $

create table dcs_user_catalog (
	user_id	varchar(40)	not null,
	user_catalog	varchar(40)	null
,constraint dcs_usr_catalog_pk primary key (user_id))



go
