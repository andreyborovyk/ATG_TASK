


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/sql/db_components/mysql/multisite_i18n_ddl.sql#3 $$Change: 635816 $

create table crs_i18n_site_attr (
	id	varchar(40)	not null,
	default_lang	varchar(2)	null
,constraint crs_i18nsite_pattr primary key (id)
,constraint crs_i18nsite_fattr foreign key (id) references site_configuration (id));

commit;


