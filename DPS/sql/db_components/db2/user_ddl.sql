


--  @version $Id: //product/DPS/version/10.0.3/templates/DPS/sql/user_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements, which will configure your database for use with the new DPS schema.
-- Add the organization definition.  It's out of place, but since dps_user references it, it's got to be defined up here

create table dps_organization (
	org_id	varchar(40)	not null,
	name	varchar(254)	not null,
	description	varchar(254)	default null,
	parent_org	varchar(40)	default null
,constraint dps_organization_p primary key (org_id)
,constraint dps_orgnparnt_rg_f foreign key (parent_org) references dps_organization (org_id));

create index dps_orgparent_org on dps_organization (parent_org);
-- Default Profile User Template
-- Basic user info.  note: the password field size must be at a minimum 35 characters       if DPS stores a hash of the password and not the actual value.

create table dps_user (
	id	varchar(40)	not null,
	login	varchar(40)	not null,
	auto_login	numeric(1,0)	default null,
	password	varchar(35)	default null,
	member	numeric(1,0)	default null,
	first_name	varchar(40)	default null,
	middle_name	varchar(40)	default null,
	last_name	varchar(40)	default null,
	user_type	integer	default null,
	locale	integer	default null,
	lastactivity_date	timestamp	default null,
	lastpwdupdate	timestamp	default null,
	generatedpwd	numeric(1)	default null,
	registration_date	timestamp	default null,
	email	varchar(255)	default null,
	email_status	integer	default null,
	receive_email	integer	default null,
	last_emailed	timestamp	default null,
	gender	integer	default null,
	date_of_birth	timestamp	default null,
	securityStatus	integer	default null,
	description	varchar(254)	default null
,constraint dps_user_p primary key (id)
,constraint dps_user_u unique (login)
,constraint dps_user1_c check (auto_login in (0,1))
,constraint dps_user2_c check (member in (0,1)));


create table dps_contact_info (
	id	varchar(40)	not null,
	user_id	varchar(40)	default null,
	prefix	varchar(40)	default null,
	first_name	varchar(100)	default null,
	middle_name	varchar(100)	default null,
	last_name	varchar(100)	default null,
	suffix	varchar(40)	default null,
	job_title	varchar(100)	default null,
	company_name	varchar(40)	default null,
	address1	varchar(50)	default null,
	address2	varchar(50)	default null,
	address3	varchar(50)	default null,
	city	varchar(30)	default null,
	state	varchar(20)	default null,
	postal_code	varchar(10)	default null,
	county	varchar(40)	default null,
	country	varchar(40)	default null,
	phone_number	varchar(30)	default null,
	fax_number	varchar(30)	default null
,constraint dps_contact_info_p primary key (id));


create table dps_user_address (
	id	varchar(40)	not null,
	home_addr_id	varchar(40)	default null,
	billing_addr_id	varchar(40)	default null,
	shipping_addr_id	varchar(40)	default null
,constraint dps_user_address_p primary key (id)
,constraint dps_usrddrssid_f foreign key (id) references dps_user (id));

create index dps_usr_adr_shp_id on dps_user_address (shipping_addr_id);
create index dps_usr_blng_ad_id on dps_user_address (billing_addr_id);
create index dps_usr_home_ad_id on dps_user_address (home_addr_id);

create table dps_other_addr (
	user_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	address_id	varchar(40)	not null
,constraint dps_other_addr_p primary key (user_id,tag)
,constraint dps_othrddrusr_d_f foreign key (user_id) references dps_user (id));


create table dps_mailing (
	id	varchar(40)	not null,
	name	varchar(255)	default null,
	subject	varchar(80)	default null,
	uniq_server_id	varchar(255)	default null,
	from_address	varchar(255)	default null,
	replyto	varchar(255)	default null,
	template_URL	varchar(255)	default null,
	alt_template_URL	varchar(255)	default null,
	batch_exec_id	varchar(40)	default null,
	cc	varchar(4096)	default null,
	bcc	varchar(4096)	default null,
	send_as_html	integer	default null,
	send_as_text	integer	default null,
	params	blob(1048576)	default null,
	start_time	timestamp	default null,
	end_time	timestamp	default null,
	status	integer	default null,
	num_profiles	integer	default null,
	num_sent	integer	default null,
	num_bounces	integer	default null,
	num_soft_bounces	integer	default null,
	num_errors	integer	default null,
	num_skipped	integer	default null,
	fill_from_templ	numeric(1,0)	default null,
	is_batched	numeric(1)	default null,
	ignore_fatigue	numeric(1)	default null,
	batch_size	integer	default null
,constraint dps_mailing_p primary key (id));


create table dps_mail_trackdata (
	mailing_id	varchar(40)	not null,
	map_key	varchar(254)	not null,
	map_value	varchar(254)	default null
,constraint dps_mail_trackd_p primary key (mailing_id,map_key)
,constraint dps_mail_trackd_f foreign key (mailing_id) references dps_mailing (id));


create table dps_mail_batch (
	mailing_id	varchar(40)	not null,
	start_idx	integer	not null,
	uniq_server_id	varchar(254)	default null,
	start_time	timestamp	default null,
	end_time	timestamp	default null,
	status	integer	default null,
	num_profiles	integer	default null,
	num_sent	integer	default null,
	num_bounces	integer	default null,
	num_errors	integer	default null,
	num_skipped	integer	default null,
	is_summarized	numeric(1)	default null
,constraint dps_mail_batch_p primary key (mailing_id,start_idx)
,constraint dps_mailbatch_d_f foreign key (mailing_id) references dps_mailing (id));


create table dps_mail_server (
	uniq_server_id	varchar(254)	not null,
	last_updated	timestamp	default null
,constraint dps_mail_server_p primary key (uniq_server_id));


create table dps_user_mailing (
	mailing_id	varchar(40)	not null,
	user_id	varchar(40)	not null,
	idx	integer	not null
,constraint dps_user_mailing_p primary key (mailing_id,user_id)
,constraint dps_usrmmalng_d_f foreign key (mailing_id) references dps_mailing (id)
,constraint dps_usrmlngusr_d_f foreign key (user_id) references dps_user (id));

create index dps_u_mail_uid on dps_user_mailing (user_id);

create table dps_email_address (
	mailing_id	varchar(40)	not null,
	email_address	varchar(255)	not null,
	idx	integer	not null
,constraint dps_email_addres_p primary key (mailing_id,idx)
,constraint dps_emldmalng_d_f foreign key (mailing_id) references dps_mailing (id));

-- Organizations/roles

create table dps_role (
	role_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	name	varchar(40)	not null,
	description	varchar(254)	default null
,constraint dps_role_p primary key (role_id));

-- Extending the user profile to include references to the roles that are assigned to this user

create table dps_user_roles (
	user_id	varchar(40)	not null,
	atg_role	varchar(40)	not null
,constraint dps_user_roles_p primary key (user_id,atg_role)
,constraint dps_usrrlsatg_rl_f foreign key (atg_role) references dps_role (role_id)
,constraint dps_usrrlsusr_d_f foreign key (user_id) references dps_user (id));

create index dps_usr_roles_id on dps_user_roles (atg_role);

create table dps_org_role (
	org_id	varchar(40)	not null,
	atg_role	varchar(40)	not null
,constraint dps_org_role_p primary key (org_id,atg_role)
,constraint dps_orgrlorg_d_f foreign key (org_id) references dps_organization (org_id)
,constraint dps_orgrlatg_rl_f foreign key (atg_role) references dps_role (role_id));

create index dps_org_rolerole on dps_org_role (atg_role);

create table dps_role_rel_org (
	organization	varchar(40)	not null,
	sequence_num	integer	not null,
	role_id	varchar(40)	not null
,constraint dps_role_rel_org_p primary key (organization,sequence_num)
,constraint dps_rolrorgnztn_f foreign key (organization) references dps_organization (org_id)
,constraint dps_rolrlrgrol_d_f foreign key (role_id) references dps_role (role_id));

create index dps_role_rel_org on dps_role_rel_org (role_id);

create table dps_relativerole (
	role_id	varchar(40)	not null,
	dps_function	varchar(40)	not null,
	relative_to	varchar(40)	not null
,constraint dps_relativerole_p primary key (role_id)
,constraint dps_reltreltv_t_f foreign key (relative_to) references dps_organization (org_id)
,constraint dps_reltvrlrol_d_f foreign key (role_id) references dps_role (role_id));

create index dps_relativerole on dps_relativerole (relative_to);

create table dps_user_org (
	organization	varchar(40)	not null,
	user_id	varchar(40)	not null
,constraint dps_user_org_p primary key (organization,user_id)
,constraint dps_usrrgorgnztn_f foreign key (organization) references dps_organization (org_id)
,constraint dps_usrrgusr_d_f foreign key (user_id) references dps_user (id));

create index dps_usr_orgusr_id on dps_user_org (user_id);

create table dps_user_org_anc (
	user_id	varchar(40)	not null,
	sequence_num	integer	not null,
	anc_org	varchar(40)	not null
,constraint dps_user_org_anc_p primary key (user_id,sequence_num)
,constraint dps_usrranc_rg_f foreign key (anc_org) references dps_organization (org_id)
,constraint dps_usrrgncusr_d_f foreign key (user_id) references dps_user (id));

create index dps_usr_org_ancanc on dps_user_org_anc (anc_org);

create table dps_org_chldorg (
	org_id	varchar(40)	not null,
	child_org_id	varchar(40)	not null
,constraint dps_org_chldorg_p primary key (org_id,child_org_id)
,constraint dps_orgcchild_rg_f foreign key (child_org_id) references dps_organization (org_id)
,constraint dps_orgcorg_d_f foreign key (org_id) references dps_organization (org_id));

create index dps_org_chldorg_id on dps_org_chldorg (child_org_id);

create table dps_org_ancestors (
	org_id	varchar(40)	not null,
	sequence_num	integer	not null,
	anc_org	varchar(40)	not null
,constraint dps_org_ancestor_p primary key (org_id,sequence_num)
,constraint dps_orgnanc_rg_f foreign key (anc_org) references dps_organization (org_id)
,constraint dps_orgnorg_d_f foreign key (org_id) references dps_organization (org_id));

create index dps_org_ancanc_org on dps_org_ancestors (anc_org);
-- Adding the folder information

create table dps_folder (
	folder_id	varchar(40)	not null,
	type	integer	not null,
	name	varchar(40)	not null,
	parent	varchar(40)	default null,
	description	varchar(254)	default null
,constraint dps_folder_p primary key (folder_id)
,constraint dps_foldrparnt_f foreign key (parent) references dps_folder (folder_id));

create index dps_folderparent on dps_folder (parent);

create table dps_child_folder (
	folder_id	varchar(40)	not null,
	child_folder_id	varchar(40)	not null
,constraint dps_child_folder_p primary key (folder_id,child_folder_id)
,constraint dps_chilchild_fl_f foreign key (child_folder_id) references dps_folder (folder_id)
,constraint dps_chilfoldr_d_f foreign key (folder_id) references dps_folder (folder_id));

create index dps_chld_fldr_fld on dps_child_folder (child_folder_id);

create table dps_rolefold_chld (
	rolefold_id	varchar(40)	not null,
	role_id	varchar(40)	not null
,constraint dps_rolefold_chl_p primary key (rolefold_id,role_id)
,constraint dps_rolfrolfld_d_f foreign key (rolefold_id) references dps_folder (folder_id)
,constraint dps_rolfrol_d_f foreign key (role_id) references dps_role (role_id));

create index dps_rolfldchldrole on dps_rolefold_chld (role_id);
-- Adding the previous password information

create table dps_user_prevpwd (
	id	varchar(40)	not null,
	seq_num	integer	not null,
	prevpwd	varchar(35)	default null
,constraint dps_prevpwd_p primary key (id,seq_num)
,constraint dps_prvpwd_d_f foreign key (id) references dps_user (id));

commit;


