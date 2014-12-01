


--  @version $Id: //app/portal/version/10.0.3/bookmarks/sql/bookmarks_ddl.xml#2 $$Change: 651448 $

create table bmg_bookmark_node (
	id	varchar2(40)	not null,
	version	number(10)	not null,
	type	number(20,0)	null,
	name	varchar2(100)	null,
	description	varchar2(500)	null,
	add_date	timestamp	null,
	last_visit	timestamp	null,
	last_modified	timestamp	null
,constraint bmg_bookmarknodepk primary key (id));


create table bmg_folder_child (
	folder_id	varchar2(40)	not null,
	sequence_num	number(20,0)	not null,
	child	varchar2(40)	null
,constraint bmg_folderchildpk primary key (folder_id,sequence_num)
,constraint bmg_folder_chil1_f foreign key (child) references bmg_bookmark_node (id)
,constraint bmg_folder_chil2_f foreign key (folder_id) references bmg_bookmark_node (id));

create index bmg_fdchildchildix on bmg_folder_child (child);

create table bmg_bookmark (
	id	varchar2(40)	not null,
	link	varchar2(300)	not null
,constraint bmg_bookmarkpk primary key (id)
,constraint bmg_bookmark1_f foreign key (id) references bmg_bookmark_node (id));




