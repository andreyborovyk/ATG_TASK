


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/oracle/storetext_ddl.sql#3 $$Change: 635816 $

create table crs_store_text (
	text_id	varchar2(40)	not null,
	key_id	varchar2(254)	not null,
	text_template	varchar2(4000)	null,
	tag	varchar2(40)	null,
	text_type	number(10)	null
,constraint crs_txt_key_p primary key (text_id,key_id));

create index crs_txt_key_id on crs_store_text (key_id);

create table crs_store_long_txt (
	text_id	varchar2(40)	not null,
	text_template	clob	null
,constraint crs_lng_txt_key_p primary key (text_id));




