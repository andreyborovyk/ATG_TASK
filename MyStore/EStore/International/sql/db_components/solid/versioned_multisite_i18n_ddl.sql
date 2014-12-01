


--     @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/sql/db_components/solid/versioned_multisite_i18n_ddl.sql#3 $$Change: 635816 $  

create table crs_i18n_site_attr (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	default_lang	varchar(2)	null
, primary key (id,asset_version));

alter table crs_i18n_site_attr set pessimistic;

commit work;


