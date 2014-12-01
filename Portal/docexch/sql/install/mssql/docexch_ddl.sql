


--  @version $Id: //app/portal/version/10.0.3/docexch/sql/docexch_ddl.xml#2 $$Change: 651448 $

create table dex_document (
	document_id	varchar(40)	not null,
	version	integer	default 0 not null,
	title	varchar(254)	null,
	description	varchar(400)	null,
	filename	varchar(254)	null,
	author	varchar(400)	null,
	filedatasize	integer	null,
	mimetype	varchar(100)	null,
	creation_date	datetime	null,
	gear_instance_ref	varchar(40)	null,
	annotation_ref	varchar(40)	null,
	status	integer	null,
	file_data	image	null
,constraint dex_documentpk primary key (document_id))

create index gear_inst_ref_idx on dex_document (gear_instance_ref)

create table dex_viewed_mapper (
	id	varchar(40)	not null,
	tstamp	datetime	null,
	doc_name	varchar(255)	null,
	author_name	varchar(255)	null,
	gear_id	varchar(40)	null,
	community_id	varchar(40)	null,
	user_id	varchar(40)	null,
	msg_type	varchar(255)	null)

create index dex_vewmap_comid on dex_viewed_mapper (community_id)
create index dex_vewmap_gearid on dex_viewed_mapper (gear_id)
create index dex_vewmap_usrid on dex_viewed_mapper (user_id)

create table dex_split_doc (
	document_id	varchar(40)	not null,
	version	integer	not null,
	title	varchar(254)	null,
	description	varchar(400)	null,
	filename	varchar(254)	null,
	author	varchar(400)	null,
	filedatasize	integer	null,
	mimetype	varchar(100)	null,
	creation_date	datetime	null,
	gear_instance_ref	varchar(40)	null,
	annotation_ref	varchar(40)	null,
	status	integer	null
,constraint dex_split_doc_pk primary key (document_id))

create index gear_spl_ref_idx on dex_split_doc (gear_instance_ref)

create table dex_split_file (
	document_id	varchar(40)	not null,
	file_data	image	null
,constraint dex_split_file_pk primary key (document_id))



go
