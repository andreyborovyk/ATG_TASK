


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/sql/db_components/oracle/storetext_i18n_ddl.sql#3 $$Change: 635816 $

create table crs_txt_xlate (
	translation_id	varchar2(254)	not null,
	text_template	varchar2(4000)	null,
	text_type	number(10)	null
,constraint crs_txt_xlate_p primary key (translation_id));


create table crs_txt_txt_xlate (
	text_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(254)	not null
,constraint crs_txt_txt_xlt_p primary key (text_id,locale)
,constraint crs_txt_xlate_f foreign key (translation_id) references crs_txt_xlate (translation_id));

create index crs_txt_xlt_tr_id on crs_txt_txt_xlate (translation_id);

create table crs_txt_long_xlate (
	translation_id	varchar2(254)	not null,
	text_template	clob	null
,constraint crs_lng_txt_xlt_p primary key (translation_id));




