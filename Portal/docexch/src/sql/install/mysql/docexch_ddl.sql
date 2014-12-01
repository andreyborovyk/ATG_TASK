


--  @version $Id: //app/portal/version/10.0.3/docexch/sql/docexch_ddl.xml#2 $$Change: 651448 $

create table dex_document (
	document_id	nvarchar(40)	not null,
	version	integer	default 0 not null,
	title	nvarchar(254)	null,
	description	nvarchar(400)	null,
	filename	nvarchar(254)	null,
	author	nvarchar(400)	null,
	filedatasize	integer	null,
	mimetype	nvarchar(100)	null,
	creation_date	datetime	null,
	gear_instance_ref	nvarchar(40)	null,
	annotation_ref	nvarchar(40)	null,
	status	integer	null,
	file_data	longblob	null
,constraint dex_documentpk primary key (document_id));

create index gear_inst_ref_idx on dex_document (gear_instance_ref);

create table dex_viewed_mapper (
	id	nvarchar(32)	not null,
	tstamp	datetime	null,
	doc_name	nvarchar(255)	null,
	author_name	nvarchar(255)	null,
	gear_id	nvarchar(32)	null,
	community_id	nvarchar(32)	null,
	user_id	nvarchar(32)	null,
	msg_type	nvarchar(255)	null);

create index dex_vewmap_comid on dex_viewed_mapper (community_id);
create index dex_vewmap_gearid on dex_viewed_mapper (gear_id);
create index dex_vewmap_usrid on dex_viewed_mapper (user_id);

create table dex_split_doc (
	document_id	nvarchar(40)	not null,
	version	integer	not null,
	title	nvarchar(254)	null,
	description	nvarchar(400)	null,
	filename	nvarchar(254)	null,
	author	nvarchar(400)	null,
	filedatasize	integer	null,
	mimetype	nvarchar(100)	null,
	creation_date	datetime	null,
	gear_instance_ref	nvarchar(40)	null,
	annotation_ref	nvarchar(40)	null,
	status	integer	null
,constraint dex_split_doc_pk primary key (document_id));

create index gear_spl_ref_idx on dex_split_doc (gear_instance_ref);

create table dex_split_file (
	document_id	nvarchar(40)	not null,
	file_data	longblob	null
,constraint dex_split_file_pk primary key (document_id));

commit;


