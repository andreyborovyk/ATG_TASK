


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/db2/storetext_ddl.sql#3 $$Change: 635816 $

create table crs_store_text (
	text_id	varchar(40)	not null,
	key_id	varchar(254)	not null,
	text_template	varchar(4000)	default null,
	tag	varchar(40)	default null,
	text_type	integer	default null
,constraint crs_txt_key_p primary key (text_id,key_id));

create index crs_txt_key_id on crs_store_text (key_id);

create table crs_store_long_txt (
	text_id	varchar(40)	not null,
	text_template	varchar(4000)	default null
,constraint crs_lng_txt_key_p primary key (text_id));

commit;


