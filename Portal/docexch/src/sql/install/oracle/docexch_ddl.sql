


--  @version $Id: //app/portal/version/10.0.3/docexch/sql/docexch_ddl.xml#2 $$Change: 651448 $

create table dex_document (
	document_id	varchar2(40)	not null,
	version	number(10)	default 0 not null,
	title	varchar2(254)	null,
	description	varchar2(400)	null,
	filename	varchar2(254)	null,
	author	varchar2(400)	null,
	filedatasize	number(10)	null,
	mimetype	varchar2(100)	null,
	creation_date	timestamp	null,
	gear_instance_ref	varchar2(40)	null,
	annotation_ref	varchar2(40)	null,
	status	number(10)	null,
	file_data	blob	null
,constraint dex_documentpk primary key (document_id));

create index gear_inst_ref_idx on dex_document (gear_instance_ref);

create table dex_viewed_mapper (
	id	varchar2(40)	not null,
	tstamp	date	null,
	doc_name	varchar2(255)	null,
	author_name	varchar2(255)	null,
	gear_id	varchar2(40)	null,
	community_id	varchar2(40)	null,
	user_id	varchar2(40)	null,
	msg_type	varchar2(255)	null);

create index dex_vewmap_comid on dex_viewed_mapper (community_id);
create index dex_vewmap_gearid on dex_viewed_mapper (gear_id);
create index dex_vewmap_usrid on dex_viewed_mapper (user_id);

create table dex_split_doc (
	document_id	varchar2(40)	not null,
	version	number(10)	not null,
	title	varchar2(254)	null,
	description	varchar2(400)	null,
	filename	varchar2(254)	null,
	author	varchar2(400)	null,
	filedatasize	number(10)	null,
	mimetype	varchar2(100)	null,
	creation_date	timestamp	null,
	gear_instance_ref	varchar2(40)	null,
	annotation_ref	varchar2(40)	null,
	status	number(10)	null
,constraint dex_split_doc_pk primary key (document_id));

create index gear_spl_ref_idx on dex_split_doc (gear_instance_ref);

create table dex_split_file (
	document_id	varchar2(40)	not null,
	file_data	blob	null
,constraint dex_split_file_pk primary key (document_id));




