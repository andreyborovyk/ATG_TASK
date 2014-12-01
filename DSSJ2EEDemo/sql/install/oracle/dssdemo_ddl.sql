
-- the source for this section is 
-- qf_j2ee_ddl.sql




-- DDL FOR QUINCY FUNDS J2EE DEMO                                                       
-- There are multiple repositories ( 7 ) associated - and used exclusively - by the Quincy Funds J2EE Demo.  This SQL scripts defines them all. Each table "family" has the repository name of the repository which maps the tables to its descriptors.                                                  

create table dss_qf_ftr_fldr (
	id	varchar2(254)	not null,
	parentFolder	varchar2(254)	null,
	name	varchar2(254)	null
,constraint dss_qf_ftr_fldr_pk primary key (id));


create table dss_qf_ftr (
	id	varchar2(254)	not null,
	last_modified	date	null,
	language	varchar2(12)	null,
	title	varchar2(254)	null,
	headline	varchar2(254)	null,
	the_date	date	null,
	parentFolder	varchar2(254)	null,
	name	varchar2(254)	null,
	author	varchar2(254)	null,
	relativePath	varchar2(254)	null,
	smallImageURL	varchar2(254)	null,
	targets	varchar2(254)	null,
	description	varchar2(512)	null,
	file_content	varchar2(254)	null,
	length	number(10)	null
,constraint dss_qf_ftr_pk primary key (id));


create table dss_qf_ftr_trgs (
	id	varchar2(254)	not null,
	seq_num	number(10)	not null,
	targets	varchar2(254)	null
,constraint dss_qf_ftr_trgs_pk primary key (id,seq_num)
,constraint dss_qf_ftr_trgs_fk foreign key (id) references dss_qf_ftr (id));


create table dss_qf_ftr_keys (
	id	varchar2(254)	not null,
	seq_num	number(10)	not null,
	keywords	varchar2(254)	null
,constraint dss_qf_ftr_keys_pk primary key (id,seq_num)
,constraint dss_qf_ftr_keys_fk foreign key (id) references dss_qf_ftr (id));


create table dss_qf_ftr_cont (
	id	varchar2(254)	not null,
	file_content	clob	null
,constraint dss_qf_ftr_cont_p primary key (id)
,constraint dss_qf_ftr_cont_f foreign key (id) references dss_qf_ftr (id));


create table dss_qf_fund_fldr (
	id	varchar2(254)	not null,
	parentFolder	varchar2(254)	null,
	name	varchar2(254)	null
,constraint dss_qf_fund_fldrpk primary key (id));


create table dss_qf_fund (
	id	varchar2(254)	not null,
	fundName	varchar2(254)	null,
	aggressiveIndex	number(10)	null,
	symbol	varchar2(254)	null,
	category	varchar2(254)	null,
	price	number(25,6)	null,
	parentFolder	varchar2(254)	null,
	objective	varchar2(765)	null,
	strategy	varchar2(1024)	null,
	inceptionDate	date	null,
	last_modified	date	null,
	ytd	varchar2(254)	null,
	oneyear	varchar2(254)	null,
	threeyear	varchar2(254)	null,
	fiveyear	varchar2(254)	null,
	tenyear	varchar2(254)	null,
	sinceInception	varchar2(254)	null,
	intro	varchar2(1024)	null,
	compFundName	varchar2(254)	null,
	zeroToTen	varchar2(254)	null,
	tenToHundred	varchar2(254)	null,
	overHundred	varchar2(254)	null,
	relativePath	varchar2(254)	null,
	file_content	varchar2(254)	null,
	language	varchar2(12)	null
,constraint dss_qf_fund_pk primary key (id));


create table dss_qf_fund_trgs (
	id	varchar2(254)	not null,
	seq_num	number(10)	not null,
	subject	varchar2(254)	null
,constraint dss_qf_fund_trgspk primary key (id,seq_num)
,constraint dss_qf_fund_trgsfk foreign key (id) references dss_qf_fund (id));


create table dss_qf_fund_types (
	id	varchar2(254)	not null,
	seq_num	number(10)	not null,
	subject	varchar2(254)	null
,constraint dss_qf_fund_typepk primary key (id,seq_num)
,constraint dss_qf_fund_typefk foreign key (id) references dss_qf_fund (id));


create table dss_qf_fund_cont (
	id	varchar2(254)	not null,
	file_content	clob	not null
,constraint dss_qf_fund_cont_p primary key (id)
,constraint dss_qf_fund_cont_f foreign key (id) references dss_qf_fund (id));


create table dss_qf_news_fldr (
	id	varchar2(254)	not null,
	parentFolder	varchar2(254)	null,
	name	varchar2(254)	null
,constraint dss_qf_news_fldrpk primary key (id));


create table dss_qf_news (
	id	varchar2(254)	not null,
	author	varchar2(254)	null,
	headline	varchar2(254)	null,
	the_date	date	null,
	goLiveDate	date	null,
	status	varchar2(254)	null,
	authorId	varchar2(254)	null,
	editorId	varchar2(254)	null,
	notes	varchar2(254)	null,
	relativePath	varchar2(254)	null,
	parentFolder	varchar2(254)	null,
	contentName	varchar2(254)	null,
	language	varchar2(12)	null,
	file_content	varchar2(254)	null,
	news_content	clob	null
,constraint dss_qf_news_pk primary key (id));


create table dss_qf_news_trgs (
	id	varchar2(254)	not null,
	seq_num	number(10)	not null,
	subject	varchar2(254)	null
,constraint dss_qf_news_trg_pk primary key (id,seq_num)
,constraint dss_qf_news_trg_fk foreign key (id) references dss_qf_news (id));


create table dss_qf_offer_fldr (
	id	varchar2(254)	not null,
	parentFolder	varchar2(254)	null,
	name	varchar2(254)	null
,constraint dss_qf_offer_fldpk primary key (id));


create table dss_qf_offer (
	id	varchar2(254)	not null,
	author	varchar2(254)	null,
	description	varchar2(254)	null,
	lastModified	date	null,
	length	number(10)	null,
	name	varchar2(254)	null,
	relativePath	varchar2(254)	null,
	parentFolder	varchar2(254)	null,
	title	varchar2(254)	null,
	imageURL	varchar2(254)	null,
	language	varchar2(12)	null,
	file_content	varchar2(254)	null
,constraint dss_qf_offer_pk primary key (id));


create table dss_qf_offer_keys (
	id	varchar2(254)	not null,
	seq_num	number(10)	not null,
	keywords	varchar2(254)	null
,constraint dss_qf_offer_keypk primary key (id,seq_num)
,constraint dss_qf_offer_keyfk foreign key (id) references dss_qf_offer (id));


create table dss_qf_tip_fldr (
	id	varchar2(254)	not null,
	parentFolder	varchar2(254)	null,
	name	varchar2(254)	null
,constraint dss_qf_tip_fldr_pk primary key (id));


create table dss_qf_tip (
	id	varchar2(254)	not null,
	author	varchar2(254)	null,
	description	varchar2(254)	null,
	lastModified	date	null,
	length	number(10)	null,
	name	varchar2(254)	null,
	relativePath	varchar2(254)	null,
	parentFolder	varchar2(254)	null,
	title	varchar2(254)	null,
	imageURL	varchar2(254)	null,
	language	varchar2(12)	null,
	file_content	varchar2(254)	null
,constraint dss_qf_tip_pk primary key (id));


create table dss_qf_tip_keys (
	id	varchar2(254)	not null,
	seq_num	number(10)	not null,
	keywords	varchar2(254)	null
,constraint dss_qf_tip_keys_pk primary key (id,seq_num)
,constraint dss_qf_tip_keys_fk foreign key (id) references dss_qf_tip (id));


create table dss_qf_tip_trgs (
	id	varchar2(254)	not null,
	seq_num	number(10)	not null,
	targets	varchar2(254)	null
,constraint dss_qf_tip_trgs_pk primary key (id,seq_num)
,constraint dss_qf_tip_trgs_fk foreign key (id) references dss_qf_tip (id));


create table dss_qf_email_fldr (
	id	varchar2(254)	not null,
	parentFolder	varchar2(254)	null,
	name	varchar2(254)	null
,constraint dss_qf_email_fldpk primary key (id));


create table dss_qf_email (
	id	varchar2(254)	not null,
	author	varchar2(254)	null,
	description	varchar2(254)	null,
	lastModified	date	null,
	length	number(10)	null,
	name	varchar2(254)	null,
	relativePath	varchar2(254)	null,
	parentFolder	varchar2(254)	null,
	title	varchar2(254)	null,
	agg_content	varchar2(1024)	null,
	ModContent	varchar2(1024)	null,
	ConsContent	varchar2(1024)	null,
	BrokerSignature	varchar2(254)	null,
	language	varchar2(12)	null,
	file_content	varchar2(254)	null
,constraint dss_qf_email_pk primary key (id));


create table dss_qf_email_keys (
	id	varchar2(254)	not null,
	seq_num	number(10)	not null,
	keywords	varchar2(254)	null
,constraint dss_qf_email_keypk primary key (id,seq_num)
,constraint dss_qf_email_keyfk foreign key (id) references dss_qf_email (id));


create table dss_qf_img_fldr (
	id	varchar2(254)	not null,
	parentFolder	varchar2(254)	null,
	name	varchar2(254)	null
,constraint dss_qf_img_fldr_pk primary key (id));


create table dss_qf_img (
	id	varchar2(254)	not null,
	author	varchar2(254)	null,
	description	varchar2(254)	null,
	lastModified	date	null,
	length	number(10)	null,
	name	varchar2(254)	null,
	relativePath	varchar2(254)	null,
	parentFolder	varchar2(254)	null,
	title	varchar2(254)	null,
	alt	varchar2(1024)	null,
	imageURL	varchar2(1024)	null,
	dimensions	varchar2(1024)	null,
	file_content	varchar2(254)	null
,constraint dss_qf_img_pk primary key (id));


create table dss_qf_img_types (
	id	varchar2(254)	not null,
	seq_num	number(10)	not null,
	types	varchar2(254)	null
,constraint dss_qf_img_typespk primary key (id,seq_num)
,constraint dss_qf_img_typesfk foreign key (id) references dss_qf_img (id));


create table dss_qf_user (
	id	varchar2(40)	not null,
	number_news_items	number(10)	null,
	num_feature_items	number(10)	null,
	broker_id	varchar2(40)	null,
	strategy	number(10)	null,
	goals	number(10)	null,
	actual_strategy	number(10)	null,
	actual_goals	number(10)	null,
	aggressive_index	number(10)	null,
	pub_privileges	number(10)	null
,constraint dss_qf_user_pk primary key (id));


create table dss_qf_interest (
	id	varchar2(40)	not null,
	sequence_num	number(10)	not null,
	interest	varchar2(40)	null
,constraint dss_qf_interest_pk primary key (id,sequence_num));


create table dss_qf_fund_list (
	id	varchar2(40)	not null,
	sequence_num	number(10)	not null,
	fund_identifier	varchar2(72)	null
,constraint dss_qf_fund_listpk primary key (id,sequence_num));


create table dss_qf_fund_share (
	id	varchar2(40)	not null,
	sequence_num	number(10)	not null,
	share_count	number(10)	null
,constraint dss_qf_fund_sharpk primary key (id,sequence_num));


create table dss_qf_fnds_viewd (
	id	varchar2(40)	not null,
	sequence_num	number(10)	not null,
	fund_identifier	varchar2(64)	null
,constraint dss_qf_fnds_viewpk primary key (id,sequence_num));


create table dss_qf_investor (
	id	varchar2(40)	not null,
	asset_value	number(25,6)	null
,constraint dss_qf_investor_pk primary key (id));


create table dss_qf_broker (
	id	varchar2(40)	not null,
	commission_pct	number(10)	null
,constraint dss_qf_broker_pk primary key (id));




