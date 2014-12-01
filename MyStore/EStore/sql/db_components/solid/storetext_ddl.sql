


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/solid/storetext_ddl.sql#3 $$Change: 635816 $

create table crs_store_text (
	text_id	varchar(40)	not null,
	key_id	varchar(254)	not null,
	text_template	varchar(4000)	null,
	tag	varchar(40)	null,
	text_type	integer	null
, primary key (text_id,key_id));

create index crs_txt_key_id on crs_store_text (key_id);
alter table crs_store_text set pessimistic;


create table crs_store_long_txt (
	text_id	varchar(40)	not null,
	text_template	long varchar	null
, primary key (text_id));

alter table crs_store_long_txt set pessimistic;

commit work;


