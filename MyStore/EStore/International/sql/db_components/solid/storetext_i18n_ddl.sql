


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/sql/db_components/solid/storetext_i18n_ddl.sql#3 $$Change: 635816 $

create table crs_txt_xlate (
	translation_id	varchar(254)	not null,
	text_template	varchar(4000)	null,
	text_type	integer	null
, primary key (translation_id));

alter table crs_txt_xlate set pessimistic;


create table crs_txt_txt_xlate (
	text_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(254)	not null
, primary key (text_id,locale)
, foreign key (translation_id) references crs_txt_xlate (translation_id));

create index crs_txt_xlt_tr_id on crs_txt_txt_xlate (translation_id);
alter table crs_txt_txt_xlate set pessimistic;


create table crs_txt_long_xlate (
	translation_id	varchar(254)	not null,
	text_template	long varchar	null
, primary key (translation_id));

alter table crs_txt_long_xlate set pessimistic;

commit work;


