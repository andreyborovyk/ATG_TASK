


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/sql/db_components/solid/promotion_i18n_ddl.sql#3 $$Change: 635816 $

create table crs_prm_xlate (
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	description	varchar(254)	null
, primary key (translation_id));

alter table crs_prm_xlate set optimistic;


create table crs_prm_prm_xlate (
	promotion_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
, primary key (promotion_id,locale)
, foreign key (translation_id) references crs_prm_xlate (translation_id));

create index crs_prm_xlt_tr_id on crs_prm_prm_xlate (translation_id);
alter table crs_prm_prm_xlate set optimistic;


create table crs_cq_xlate (
	translation_id	varchar(40)	not null,
	name	varchar(254)	null
, primary key (translation_id));

alter table crs_cq_xlate set optimistic;


create table crs_cq_cq_xlate (
	close_qualif_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
, primary key (close_qualif_id,locale)
, foreign key (translation_id) references crs_cq_xlate (translation_id));

create index crs_cq_xlt_tr_id on crs_cq_cq_xlate (translation_id);
alter table crs_cq_cq_xlate set optimistic;

commit work;


