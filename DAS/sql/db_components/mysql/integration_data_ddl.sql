



create table if_integ_data (
	item_id	varchar(40)	not null,
	descriptor	nvarchar(64)	not null,
	repository	nvarchar(255)	not null,
	state	integer	not null,
	last_import	datetime	null,
	version	integer	not null
,constraint if_int_data_p primary key (item_id,descriptor,repository));

commit;


