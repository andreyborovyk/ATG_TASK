


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/sql/db_components/solid/seo_i18n_ddl.sql#3 $$Change: 635816 $

create table crs_seo_xlate (
	translation_id	varchar(40)	not null,
	title	varchar(254)	null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	keywords	varchar(254)	null
, primary key (translation_id));

alter table crs_seo_xlate set optimistic;


create table crs_seo_seo_xlate (
	seo_tag_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
, primary key (seo_tag_id,locale)
, foreign key (translation_id) references crs_seo_xlate (translation_id));

create index crs_seo_tag_xlt_id on crs_seo_seo_xlate (translation_id);
alter table crs_seo_seo_xlate set optimistic;

commit work;


