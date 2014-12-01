


--  @version $Id: //app/portal/version/10.0.3/bookmarks/sql/bookmarks_ddl.xml#2 $$Change: 651448 $

create table bmg_bookmark_node (
	id	varchar(40)	not null,
	version	integer	not null,
	type	integer	null,
	name	varchar(100)	null,
	description	varchar(500)	null,
	add_date	datetime	null,
	last_visit	datetime	null,
	last_modified	datetime	null
,constraint bmg_bookmarknodepk primary key (id))


create table bmg_folder_child (
	folder_id	varchar(40)	not null,
	sequence_num	integer	not null,
	child	varchar(40)	null
,constraint bmg_folderchildpk primary key (folder_id,sequence_num)
,constraint bmg_folder_chil1_f foreign key (child) references bmg_bookmark_node (id)
,constraint bmg_folder_chil2_f foreign key (folder_id) references bmg_bookmark_node (id))

create index bmg_fdchildchildix on bmg_folder_child (child)

create table bmg_bookmark (
	id	varchar(40)	not null,
	link	varchar(300)	not null
,constraint bmg_bookmarkpk primary key (id)
,constraint bmg_bookmark1_f foreign key (id) references bmg_bookmark_node (id))



go
