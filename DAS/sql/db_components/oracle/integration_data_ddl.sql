



create table if_integ_data (
	item_id	varchar2(40)	not null,
	descriptor	varchar2(64)	not null,
	repository	varchar2(255)	not null,
	state	number(10)	not null,
	last_import	date	null,
	version	number(10)	not null
,constraint if_int_data_p primary key (item_id,descriptor,repository));




