


--  @version $Id: //product/DSS/version/10.0.3/templates/DSSJ2EEDemo/sql/qf_j2ee_ddl.xml#3 $$Change: 655658 $
-- DDL FOR QUINCY FUNDS J2EE DEMO                                                       
-- There are multiple repositories ( 7 ) associated - and used exclusively - by the Quincy Funds J2EE Demo.  This SQL scripts defines them all. Each table "family" has the repository name of the repository which maps the tables to its descriptors.                                                  

create table dss_qf_ftr_fldr (
	id	varchar(254)	not null,
	parentFolder	varchar(254)	null,
	name	varchar(254)	null
,constraint dss_qf_ftr_fldr_pk primary key (id))


create table dss_qf_ftr (
	id	varchar(254)	not null,
	last_modified	datetime	null,
	language	varchar(12)	null,
	title	varchar(254)	null,
	headline	varchar(254)	null,
	the_date	datetime	null,
	parentFolder	varchar(254)	null,
	name	varchar(254)	null,
	author	varchar(254)	null,
	relativePath	varchar(254)	null,
	smallImageURL	varchar(254)	null,
	targets	varchar(254)	null,
	description	varchar(512)	null,
	file_content	varchar(254)	null,
	length	integer	null
,constraint dss_qf_ftr_pk primary key (id))


create table dss_qf_ftr_trgs (
	id	varchar(254)	not null,
	seq_num	integer	not null,
	targets	varchar(254)	null
,constraint dss_qf_ftr_trgs_pk primary key (id,seq_num)
,constraint dss_qf_ftr_trgs_fk foreign key (id) references dss_qf_ftr (id))


create table dss_qf_ftr_keys (
	id	varchar(254)	not null,
	seq_num	integer	not null,
	keywords	varchar(254)	null
,constraint dss_qf_ftr_keys_pk primary key (id,seq_num)
,constraint dss_qf_ftr_keys_fk foreign key (id) references dss_qf_ftr (id))


create table dss_qf_ftr_cont (
	id	varchar(254)	not null,
	file_content	text	null
,constraint dss_qf_ftr_cont_p primary key (id)
,constraint dss_qf_ftr_cont_f foreign key (id) references dss_qf_ftr (id))


create table dss_qf_fund_fldr (
	id	varchar(254)	not null,
	parentFolder	varchar(254)	null,
	name	varchar(254)	null
,constraint dss_qf_fund_fldrpk primary key (id))


create table dss_qf_fund (
	id	varchar(254)	not null,
	fundName	varchar(254)	null,
	aggressiveIndex	integer	null,
	symbol	varchar(254)	null,
	category	varchar(254)	null,
	price	numeric(25,6)	null,
	parentFolder	varchar(254)	null,
	objective	varchar(1024)	null,
	strategy	varchar(1024)	null,
	inceptionDate	datetime	null,
	last_modified	datetime	null,
	ytd	varchar(254)	null,
	oneyear	varchar(254)	null,
	threeyear	varchar(254)	null,
	fiveyear	varchar(254)	null,
	tenyear	varchar(254)	null,
	sinceInception	varchar(254)	null,
	intro	varchar(1024)	null,
	compFundName	varchar(254)	null,
	zeroToTen	varchar(254)	null,
	tenToHundred	varchar(254)	null,
	overHundred	varchar(254)	null,
	relativePath	varchar(254)	null,
	file_content	varchar(254)	null,
	language	varchar(12)	null
,constraint dss_qf_fund_pk primary key (id))


create table dss_qf_fund_trgs (
	id	varchar(254)	not null,
	seq_num	integer	not null,
	subject	varchar(254)	null
,constraint dss_qf_fund_trgspk primary key (id,seq_num)
,constraint dss_qf_fund_trgsfk foreign key (id) references dss_qf_fund (id))


create table dss_qf_fund_types (
	id	varchar(254)	not null,
	seq_num	integer	not null,
	subject	varchar(254)	null
,constraint dss_qf_fund_typepk primary key (id,seq_num)
,constraint dss_qf_fund_typefk foreign key (id) references dss_qf_fund (id))


create table dss_qf_fund_cont (
	id	varchar(254)	not null,
	file_content	text	not null
,constraint dss_qf_fund_cont_p primary key (id)
,constraint dss_qf_fund_cont_f foreign key (id) references dss_qf_fund (id))


create table dss_qf_news_fldr (
	id	varchar(254)	not null,
	parentFolder	varchar(254)	null,
	name	varchar(254)	null
,constraint dss_qf_news_fldrpk primary key (id))


create table dss_qf_news (
	id	varchar(254)	not null,
	author	varchar(254)	null,
	headline	varchar(254)	null,
	the_date	datetime	null,
	goLiveDate	datetime	null,
	status	varchar(254)	null,
	authorId	varchar(254)	null,
	editorId	varchar(254)	null,
	notes	varchar(254)	null,
	relativePath	varchar(254)	null,
	parentFolder	varchar(254)	null,
	contentName	varchar(254)	null,
	language	varchar(12)	null,
	file_content	varchar(254)	null,
	news_content	text	null
,constraint dss_qf_news_pk primary key (id))


create table dss_qf_news_trgs (
	id	varchar(254)	not null,
	seq_num	integer	not null,
	subject	varchar(254)	null
,constraint dss_qf_news_trg_pk primary key (id,seq_num)
,constraint dss_qf_news_trg_fk foreign key (id) references dss_qf_news (id))


create table dss_qf_offer_fldr (
	id	varchar(254)	not null,
	parentFolder	varchar(254)	null,
	name	varchar(254)	null
,constraint dss_qf_offer_fldpk primary key (id))


create table dss_qf_offer (
	id	varchar(254)	not null,
	author	varchar(254)	null,
	description	varchar(254)	null,
	lastModified	datetime	null,
	length	integer	null,
	name	varchar(254)	null,
	relativePath	varchar(254)	null,
	parentFolder	varchar(254)	null,
	title	varchar(254)	null,
	imageURL	varchar(254)	null,
	language	varchar(12)	null,
	file_content	varchar(254)	null
,constraint dss_qf_offer_pk primary key (id))


create table dss_qf_offer_keys (
	id	varchar(254)	not null,
	seq_num	integer	not null,
	keywords	varchar(254)	null
,constraint dss_qf_offer_keypk primary key (id,seq_num)
,constraint dss_qf_offer_keyfk foreign key (id) references dss_qf_offer (id))


create table dss_qf_tip_fldr (
	id	varchar(254)	not null,
	parentFolder	varchar(254)	null,
	name	varchar(254)	null
,constraint dss_qf_tip_fldr_pk primary key (id))


create table dss_qf_tip (
	id	varchar(254)	not null,
	author	varchar(254)	null,
	description	varchar(254)	null,
	lastModified	datetime	null,
	length	integer	null,
	name	varchar(254)	null,
	relativePath	varchar(254)	null,
	parentFolder	varchar(254)	null,
	title	varchar(254)	null,
	imageURL	varchar(254)	null,
	language	varchar(12)	null,
	file_content	varchar(254)	null
,constraint dss_qf_tip_pk primary key (id))


create table dss_qf_tip_keys (
	id	varchar(254)	not null,
	seq_num	integer	not null,
	keywords	varchar(254)	null
,constraint dss_qf_tip_keys_pk primary key (id,seq_num)
,constraint dss_qf_tip_keys_fk foreign key (id) references dss_qf_tip (id))


create table dss_qf_tip_trgs (
	id	varchar(254)	not null,
	seq_num	integer	not null,
	targets	varchar(254)	null
,constraint dss_qf_tip_trgs_pk primary key (id,seq_num)
,constraint dss_qf_tip_trgs_fk foreign key (id) references dss_qf_tip (id))


create table dss_qf_email_fldr (
	id	varchar(254)	not null,
	parentFolder	varchar(254)	null,
	name	varchar(254)	null
,constraint dss_qf_email_fldpk primary key (id))


create table dss_qf_email (
	id	varchar(254)	not null,
	author	varchar(254)	null,
	description	varchar(254)	null,
	lastModified	datetime	null,
	length	integer	null,
	name	varchar(254)	null,
	relativePath	varchar(254)	null,
	parentFolder	varchar(254)	null,
	title	varchar(254)	null,
	agg_content	varchar(1024)	null,
	ModContent	varchar(1024)	null,
	ConsContent	varchar(1024)	null,
	BrokerSignature	varchar(254)	null,
	language	varchar(12)	null,
	file_content	varchar(254)	null
,constraint dss_qf_email_pk primary key (id))


create table dss_qf_email_keys (
	id	varchar(254)	not null,
	seq_num	integer	not null,
	keywords	varchar(254)	null
,constraint dss_qf_email_keypk primary key (id,seq_num)
,constraint dss_qf_email_keyfk foreign key (id) references dss_qf_email (id))


create table dss_qf_img_fldr (
	id	varchar(254)	not null,
	parentFolder	varchar(254)	null,
	name	varchar(254)	null
,constraint dss_qf_img_fldr_pk primary key (id))


create table dss_qf_img (
	id	varchar(254)	not null,
	author	varchar(254)	null,
	description	varchar(254)	null,
	lastModified	datetime	null,
	length	integer	null,
	name	varchar(254)	null,
	relativePath	varchar(254)	null,
	parentFolder	varchar(254)	null,
	title	varchar(254)	null,
	alt	varchar(1024)	null,
	imageURL	varchar(1024)	null,
	dimensions	varchar(1024)	null,
	file_content	varchar(254)	null
,constraint dss_qf_img_pk primary key (id))


create table dss_qf_img_types (
	id	varchar(254)	not null,
	seq_num	integer	not null,
	types	varchar(254)	null
,constraint dss_qf_img_typespk primary key (id,seq_num)
,constraint dss_qf_img_typesfk foreign key (id) references dss_qf_img (id))


create table dss_qf_user (
	id	varchar(40)	not null,
	number_news_items	integer	null,
	num_feature_items	integer	null,
	broker_id	varchar(40)	null,
	strategy	integer	null,
	goals	integer	null,
	actual_strategy	integer	null,
	actual_goals	integer	null,
	aggressive_index	integer	null,
	pub_privileges	integer	null
,constraint dss_qf_user_pk primary key (id))


create table dss_qf_interest (
	id	varchar(40)	not null,
	sequence_num	integer	not null,
	interest	varchar(40)	null
,constraint dss_qf_interest_pk primary key (id,sequence_num))


create table dss_qf_fund_list (
	id	varchar(40)	not null,
	sequence_num	integer	not null,
	fund_identifier	varchar(72)	null
,constraint dss_qf_fund_listpk primary key (id,sequence_num))


create table dss_qf_fund_share (
	id	varchar(40)	not null,
	sequence_num	integer	not null,
	share_count	integer	null
,constraint dss_qf_fund_sharpk primary key (id,sequence_num))


create table dss_qf_fnds_viewd (
	id	varchar(40)	not null,
	sequence_num	integer	not null,
	fund_identifier	varchar(64)	null
,constraint dss_qf_fnds_viewpk primary key (id,sequence_num))


create table dss_qf_investor (
	id	varchar(40)	not null,
	asset_value	numeric(25,6)	null
,constraint dss_qf_investor_pk primary key (id))


create table dss_qf_broker (
	id	varchar(40)	not null,
	commission_pct	integer	null
,constraint dss_qf_broker_pk primary key (id))



go
