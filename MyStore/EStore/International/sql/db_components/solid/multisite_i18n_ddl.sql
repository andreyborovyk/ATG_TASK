


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/sql/db_components/solid/multisite_i18n_ddl.sql#3 $$Change: 635816 $

create table crs_i18n_site_attr (
	id	varchar(40)	not null,
	default_lang	varchar(2)	null
, primary key (id)
, foreign key (id) references site_configuration (id));

alter table crs_i18n_site_attr set optimistic;

commit work;


