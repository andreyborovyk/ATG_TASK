


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/rl_example_ddl.xml#2 $$Change: 651448 $

create table rlex_file_folder (
	folder_id	varchar2(40)	not null,
	folder_name	varchar2(255)	not null,
	parent_folder	varchar2(40)	null
,constraint file_folder_pk primary key (folder_id));

create index rlex_file_fold_id1 on rlex_file_folder (folder_name);

create table rlex_file_asset (
	file_asset_id	varchar2(40)	not null,
	asset_type	number(19)	not null,
	filename	varchar2(255)	not null,
	last_modified	date	null,
	size_bytes	number(19)	null,
	parent_folder	varchar2(40)	null
,constraint file_asset_rl_pk primary key (file_asset_id)
,constraint rlex_file_fold_fk foreign key (parent_folder) references rlex_file_folder (folder_id));

create index rlex_file_asst_id2 on rlex_file_asset (parent_folder);
create index rlex_file_asst_id1 on rlex_file_asset (filename);
create index rlex_file_asst_id3 on rlex_file_asset (size_bytes);
create index rlex_file_asst_id4 on rlex_file_asset (last_modified);

create table rlex_text_file (
	text_file_id	varchar2(40)	not null,
	text_content	clob	null
,constraint rlex_text_file_pk primary key (text_file_id)
,constraint rlex_text_file_fk foreign key (text_file_id) references rlex_file_asset (file_asset_id));


create table rlex_binary_file (
	binary_file_id	varchar2(40)	not null,
	binary_content	blob	null
,constraint rlex_binary_fil_pk primary key (binary_file_id)
,constraint rlex_binary_fil_fk foreign key (binary_file_id) references rlex_file_asset (file_asset_id));


create table rlex_article_file (
	article_file_id	varchar2(40)	not null,
	published_date	date	null,
	keywords	varchar2(80)	null
,constraint rlex_article_fi_pk primary key (article_file_id)
,constraint rlex_article_fi_fk foreign key (article_file_id) references rlex_file_asset (file_asset_id));


create table rlex_user (
	id	varchar2(32)	not null,
	nam_col	varchar2(32)	null,
	age_col	varchar2(32)	null
,constraint rlex_user_p primary key (id));


create table rlex_address (
	addr_id	varchar2(32)	not null,
	user_id	varchar2(32)	null,
	street	varchar2(32)	null,
	city	varchar2(32)	null
,constraint rlex_address_p primary key (addr_id)
,constraint rlex_address_f1 foreign key (user_id) references rlex_user (id));


create table rlex_contact (
	con_id	varchar2(32)	not null,
	rank	number(10)	not null,
	user_id	varchar2(32)	null,
	email	varchar2(255)	null,
	url	varchar2(255)	null
,constraint rlex_contact_p primary key (con_id,rank)
,constraint rlex_contact_f1 foreign key (user_id) references rlex_user (id));


create table rlex_phone (
	phone_id	varchar2(32)	not null,
	kind	varchar2(64)	not null,
	user_id	varchar2(32)	null,
	pnumber	varchar2(64)	null
,constraint rlex_phone_p primary key (phone_id,kind)
,constraint rlex_phone_f1 foreign key (user_id) references rlex_user (id));


create table rlex_job (
	id	varchar2(32)	not null,
	jobtype	varchar2(32)	null,
	title	varchar2(32)	null
,constraint rlex_job_p primary key (id)
,constraint rlex_job_f1 foreign key (id) references rlex_user (id));


create table rlex_subjects (
	id	varchar2(32)	not null,
	seq_num	number(10)	not null,
	subject	varchar2(32)	null
,constraint rlex_subjects_p primary key (id,seq_num)
,constraint rlex_subjects_f1 foreign key (id) references rlex_user (id));


create table rlex_worst (
	id	varchar2(32)	not null,
	seq_num	number(10)	not null,
	subject	varchar2(32)	null
,constraint rlex_worst_p primary key (id,seq_num)
,constraint rlex_worst_f1 foreign key (id) references rlex_user (id));


create table rlex_credit_card (
	id	varchar2(32)	not null,
	card_key	varchar2(32)	not null,
	card_num	varchar2(32)	null
,constraint rlex_credit_card_p primary key (id,card_key)
,constraint rlex_credit_f1 foreign key (id) references rlex_user (id));




