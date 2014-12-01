


--   This file contains create table statements needed to configure your  database for use with the ATG WSRP-producer subsystem.  
--   Tables for storing ATG WSRP producer data  

create table wsrpp_prpty_dsc (
	property_dsc_id	varchar(40)	not null,
	name	varchar(100)	not null,
	type	varchar(100)	not null,
	label	nvarchar(100)	null,
	label_xml_lang	varchar(32)	null,
	label_res_name	varchar(100)	null,
	ppty_hint	nvarchar(255)	null,
	hint_xml_lang	varchar(32)	null,
	hint_res_name	varchar(100)	null
,constraint wsrpp_prpty_dsc_p primary key (property_dsc_id));


create table wsrpp_mod_desc (
	model_id	varchar(40)	not null,
	extension	varchar(32)	null
,constraint wsrpp_mod_desc_p primary key (model_id));


create table wsrpp_modl_type (
	model_id	varchar(40)	not null,
	model_type	varchar(32)	not null
,constraint wsrpp_modl_type_p primary key (model_id,model_type)
,constraint wsrpp_modl_type1_f foreign key (model_id) references wsrpp_mod_desc (model_id));


create table wsrpp_rgprp_dsc (
	model_id	varchar(40)	not null,
	sequence_num	integer	not null,
	property_dsc_id	varchar(40)	null
,constraint wsrpp_rgprp_dsc_p primary key (model_id,sequence_num)
,constraint wsrpp_rgprp_dsc1_f foreign key (model_id) references wsrpp_mod_desc (model_id)
,constraint wsrpp_rgprp_dsc2_f foreign key (property_dsc_id) references wsrpp_prpty_dsc (property_dsc_id));

create index wsrpp_rgprp_dsc1_x on wsrpp_rgprp_dsc (property_dsc_id);

create table wsrpp_resource (
	resource_id	varchar(40)	not null,
	resource_name	varchar(100)	not null
,constraint wsrpp_resource_p primary key (resource_id));


create table wsrpp_res_value (
	resource_id	varchar(40)	not null,
	locale	varchar(32)	not null,
	res_value	nvarchar(512)	null
,constraint wsrpp_res_value_p primary key (resource_id,locale)
,constraint wsrpp_res_value1_f foreign key (resource_id) references wsrpp_resource (resource_id));


create table wsrpp_res_list (
	res_list_id	varchar(40)	not null,
	extension	varchar(32)	null
,constraint wsrpp_res_list_p primary key (res_list_id));


create table wsrpp_resources (
	res_list_id	varchar(40)	not null,
	resource_id	varchar(40)	not null
,constraint wsrpp_resources_p primary key (res_list_id,resource_id)
,constraint wsrpp_resources1_f foreign key (res_list_id) references wsrpp_res_list (res_list_id)
,constraint wsrpp_resources2_f foreign key (resource_id) references wsrpp_resource (resource_id));

create index wsrpp_resources1_x on wsrpp_resources (resource_id);

create table wsrpp_item_dsc (
	item_dsc_id	varchar(40)	not null,
	item_name	varchar(100)	not null,
	description	nvarchar(512)	null,
	dsc_xml_lang	varchar(32)	null,
	dsc_res_name	varchar(100)	null
,constraint wsrpp_item_dsc_p primary key (item_dsc_id));


create table wsrpp_mrkp_type (
	markup_type_id	varchar(40)	not null,
	mime_type	varchar(100)	not null
,constraint wsrpp_mrkp_type_p primary key (markup_type_id));


create table wsrpp_mrkp_mode (
	markup_type_id	varchar(40)	not null,
	markup_mode_id	varchar(40)	not null
,constraint wsrpp_mrkp_mode_p primary key (markup_type_id,markup_mode_id)
,constraint wsrpp_mrkp_mode1_f foreign key (markup_type_id) references wsrpp_mrkp_type (markup_type_id));


create table wsrpp_mrkp_wnst (
	markup_type_id	varchar(40)	not null,
	mrkp_wndst_id	varchar(40)	not null
,constraint wsrpp_mrkp_wnst_p primary key (markup_type_id,mrkp_wndst_id)
,constraint wsrpp_mrkp_wnst1_f foreign key (markup_type_id) references wsrpp_mrkp_type (markup_type_id));


create table wsrpp_mrkp_lcle (
	markup_type_id	varchar(40)	not null,
	markup_locale_id	varchar(40)	not null
,constraint wsrpp_mrkp_lcle_p primary key (markup_type_id,markup_locale_id)
,constraint wsrpp_mrkp_lcle1_f foreign key (markup_type_id) references wsrpp_mrkp_type (markup_type_id));


create table wsrpp_cncrt_prt (
	cncrt_prtlt_id	varchar(40)	not null,
	type	integer	null
,constraint wsrpp_cncrt_prt_p primary key (cncrt_prtlt_id));


create table wsrpp_jsr168_pt (
	cncrt_prtlt_id	varchar(40)	not null,
	gear_id	varchar(40)	not null
,constraint wsrpp_jsr168_pt_p primary key (cncrt_prtlt_id)
,constraint wsrpp_jsr168_pt1_f foreign key (cncrt_prtlt_id) references wsrpp_cncrt_prt (cncrt_prtlt_id));


create table wsrpp_prtlt_dsc (
	prtlt_dsc_id	varchar(40)	not null,
	handle	varchar(100)	not null,
	portlet_type	integer	not null,
	group_id	varchar(100)	null,
	description	nvarchar(512)	null,
	dsc_xml_lang	varchar(32)	null,
	dsc_res_name	varchar(100)	null,
	short_title	nvarchar(255)	null,
	shtitl_xml_lang	varchar(32)	null,
	shtitl_res_name	varchar(100)	null,
	title	nvarchar(255)	null,
	title_xml_lang	varchar(32)	null,
	title_res_name	varchar(100)	null,
	display_name	nvarchar(255)	null,
	dspnam_xml_lang	varchar(32)	null,
	dspnam_res_name	varchar(100)	null,
	uses_method_get	tinyint	default 0 null,
	dflt_mrkp_secur	tinyint	default 0 null,
	only_secure	tinyint	default 0 null,
	usr_ctxin_sessn	tinyint	default 0 null,
	tmplts_in_sessn	tinyint	default 0 null,
	has_usr_spec_st	tinyint	default 0 null,
	does_url_templt	tinyint	default 0 null,
	cncrt_prtlt_id	varchar(40)	not null
,constraint wsrpp_prtlt_dsc_p primary key (prtlt_dsc_id)
,constraint wsrpp_prtlt_dsc_u unique (handle)
,constraint wsrpp_prtlt_dsc1_f foreign key (cncrt_prtlt_id) references wsrpp_cncrt_prt (cncrt_prtlt_id)
,constraint wsrpp_prtlt_dsc1_c check (uses_method_get IN (0,1))
,constraint wsrpp_prtlt_dsc2_c check (dflt_mrkp_secur IN (0,1))
,constraint wsrpp_prtlt_dsc3_c check (only_secure IN (0,1))
,constraint wsrpp_prtlt_dsc4_c check (usr_ctxin_sessn IN (0,1))
,constraint wsrpp_prtlt_dsc5_c check (tmplts_in_sessn IN (0,1))
,constraint wsrpp_prtlt_dsc6_c check (has_usr_spec_st IN (0,1))
,constraint wsrpp_prtlt_dsc7_c check (does_url_templt IN (0,1)));

create index wsrpp_prtlt_dsc1_x on wsrpp_prtlt_dsc (cncrt_prtlt_id);

create table wsrpp_mrkp_typs (
	prtlt_dsc_id	varchar(40)	not null,
	markup_type_id	varchar(40)	not null
,constraint wsrpp_mrkp_typs_p primary key (prtlt_dsc_id,markup_type_id)
,constraint wsrpp_mrkp_typs1_f foreign key (prtlt_dsc_id) references wsrpp_prtlt_dsc (prtlt_dsc_id)
,constraint wsrpp_mrkp_typs2_f foreign key (markup_type_id) references wsrpp_mrkp_type (markup_type_id));

create index wsrpp_mrkp_typs1_x on wsrpp_mrkp_typs (markup_type_id);

create table wsrpp_usr_ctgrs (
	prtlt_dsc_id	varchar(40)	not null,
	user_category	varchar(100)	not null
,constraint wsrpp_usr_ctgrs_p primary key (prtlt_dsc_id,user_category)
,constraint wsrpp_usr_ctgrs1_f foreign key (prtlt_dsc_id) references wsrpp_prtlt_dsc (prtlt_dsc_id));


create table wsrpp_usrpr_itm (
	prtlt_dsc_id	varchar(40)	not null,
	usr_profle_item	varchar(100)	not null
,constraint wsrpp_usrpr_itm_p primary key (prtlt_dsc_id,usr_profle_item)
,constraint wsrpp_usrpr_itm1_f foreign key (prtlt_dsc_id) references wsrpp_prtlt_dsc (prtlt_dsc_id));


create table wsrpp_serv_desc (
	service_id	varchar(40)	not null,
	requires_reg	tinyint	default 0 null,
	req_init_cookie	integer	null,
	model_id	varchar(40)	null,
	res_list_id	varchar(40)	null
,constraint wsrpp_serv_desc_p primary key (service_id)
,constraint wsrpp_serv_desc1_f foreign key (model_id) references wsrpp_mod_desc (model_id)
,constraint wsrpp_serv_desc2_f foreign key (res_list_id) references wsrpp_res_list (res_list_id)
,constraint wsrpp_serv_desc1_c check (requires_reg IN (0,1)));

create index wsrpp_serv_desc1_x on wsrpp_serv_desc (model_id);
create index wsrpp_serv_desc2_x on wsrpp_serv_desc (res_list_id);

create table wsrpp_svcdsc_lc (
	service_id	varchar(40)	not null,
	sequence_num	integer	not null,
	locale	varchar(32)	null
,constraint wsrpp_svcdsc_lc_p primary key (service_id,sequence_num)
,constraint wsrpp_svcdsc_lc1_f foreign key (service_id) references wsrpp_serv_desc (service_id));


create table wsrpp_off_prtlt (
	service_id	varchar(40)	not null,
	portlet_id	varchar(40)	not null
,constraint wsrpp_off_prtlt_p primary key (service_id,portlet_id)
,constraint wsrpp_off_prtlt1_f foreign key (service_id) references wsrpp_serv_desc (service_id)
,constraint wsrpp_off_prtlt2_f foreign key (portlet_id) references wsrpp_prtlt_dsc (prtlt_dsc_id));

create index wsrpp_off_prtlt1_x on wsrpp_off_prtlt (portlet_id);

create table wsrpp_urct_idsc (
	service_id	varchar(40)	not null,
	description_id	varchar(40)	not null
,constraint wsrpp_urct_idsc_p primary key (service_id,description_id)
,constraint wsrpp_urct_idsc1_f foreign key (service_id) references wsrpp_serv_desc (service_id)
,constraint wsrpp_urct_idsc2_f foreign key (description_id) references wsrpp_item_dsc (item_dsc_id));

create index wsrpp_urct_idsc1_x on wsrpp_urct_idsc (description_id);

create table wsrpp_cprf_idsc (
	service_id	varchar(40)	not null,
	description_id	varchar(40)	not null
,constraint wsrpp_cprf_idsc_p primary key (service_id,description_id)
,constraint wsrpp_cprf_idsc1_f foreign key (service_id) references wsrpp_serv_desc (service_id)
,constraint wsrpp_cprf_idsc2_f foreign key (description_id) references wsrpp_item_dsc (item_dsc_id));

create index wsrpp_cprf_idsc1_x on wsrpp_cprf_idsc (description_id);

create table wsrpp_cwst_idsc (
	service_id	varchar(40)	not null,
	description_id	varchar(40)	not null
,constraint wsrpp_cwst_idsc_p primary key (service_id,description_id)
,constraint wsrpp_cwst_idsc1_f foreign key (service_id) references wsrpp_serv_desc (service_id)
,constraint wsrpp_cwst_idsc2_f foreign key (description_id) references wsrpp_item_dsc (item_dsc_id));

create index wsrpp_cwst_idsc1_x on wsrpp_cwst_idsc (description_id);

create table wsrpp_cmod_idsc (
	service_id	varchar(40)	not null,
	description_id	varchar(40)	not null
,constraint wsrpp_cmod_idsc_p primary key (service_id,description_id)
,constraint wsrpp_cmod_idsc1_f foreign key (service_id) references wsrpp_serv_desc (service_id)
,constraint wsrpp_cmod_idsc2_f foreign key (description_id) references wsrpp_item_dsc (item_dsc_id));

create index wsrpp_cmod_idsc1_x on wsrpp_cmod_idsc (description_id);

create table wsrpp_postal (
	postal_id	varchar(40)	not null,
	name	nvarchar(255)	null,
	street	nvarchar(255)	null,
	city	nvarchar(255)	null,
	stateprov	nvarchar(255)	null,
	postalcode	nvarchar(64)	null,
	country	nvarchar(255)	null,
	porganization	nvarchar(255)	null
,constraint wsrpp_postal_p primary key (postal_id));


create table wsrpp_telenum (
	telenum_id	varchar(40)	not null,
	intcode	varchar(32)	null,
	loccode	varchar(32)	null,
	tel_number	varchar(32)	null,
	ext	varchar(32)	null,
	tcomment	nvarchar(255)	null
,constraint wsrpp_telenum_p primary key (telenum_id));


create table wsrpp_telecom (
	telecom_id	varchar(40)	not null,
	telenphone_id	varchar(40)	null,
	fax_id	varchar(40)	null,
	mobile_id	varchar(40)	null,
	pager_id	varchar(40)	null
,constraint wsrpp_telecom_p primary key (telecom_id)
,constraint wsrpp_telecom1_f foreign key (telenphone_id) references wsrpp_telenum (telenum_id)
,constraint wsrpp_telecom2_f foreign key (fax_id) references wsrpp_telenum (telenum_id)
,constraint wsrpp_telecom3_f foreign key (mobile_id) references wsrpp_telenum (telenum_id)
,constraint wsrpp_telecom4_f foreign key (pager_id) references wsrpp_telenum (telenum_id));

create index wsrpp_telecom1_x on wsrpp_telecom (telenphone_id);
create index wsrpp_telecom2_x on wsrpp_telecom (fax_id);
create index wsrpp_telecom3_x on wsrpp_telecom (mobile_id);
create index wsrpp_telecom4_x on wsrpp_telecom (pager_id);

create table wsrpp_online (
	online_id	varchar(40)	not null,
	email	varchar(100)	null,
	uri	varchar(255)	null
,constraint wsrpp_online_p primary key (online_id));


create table wsrpp_contact (
	contact_id	varchar(40)	not null,
	name_prefix	nvarchar(255)	null,
	givenname	nvarchar(255)	null,
	familyname	nvarchar(255)	null,
	middlename	nvarchar(255)	null,
	suffix	nvarchar(255)	null,
	company	nvarchar(255)	null,
	title	nvarchar(255)	null,
	postal_id	varchar(40)	null,
	telecom_id	varchar(40)	null,
	online_id	varchar(40)	null,
	locale	integer	null
,constraint wsrpp_contact_p primary key (contact_id)
,constraint wsrpp_contact1_f foreign key (postal_id) references wsrpp_postal (postal_id)
,constraint wsrpp_contact2_f foreign key (telecom_id) references wsrpp_telecom (telecom_id)
,constraint wsrpp_contact3_f foreign key (online_id) references wsrpp_online (online_id));

create index wsrpp_contact1_x on wsrpp_contact (postal_id);
create index wsrpp_contact2_x on wsrpp_contact (telecom_id);
create index wsrpp_contact3_x on wsrpp_contact (online_id);

create table wsrpp_consumer (
	consumer_id	varchar(40)	not null,
	name	nvarchar(255)	not null,
	handle	varchar(100)	not null,
	agent	nvarchar(255)	not null,
	get_supported	tinyint	default 0 not null,
	locale	integer	null,
	lastactivity_dt	datetime	null,
	reg_date	datetime	null,
	last_modfd_dt	datetime	null,
	expiry_date	datetime	null,
	contact_id	varchar(40)	not null
,constraint wsrpp_consumer_p primary key (consumer_id)
,constraint wsrpp_consumer_u unique (handle)
,constraint wsrpp_consumer1_f foreign key (contact_id) references wsrpp_contact (contact_id)
,constraint wsrpp_consumer1_c check (get_supported IN (0,1)));

create index wsrpp_consumer1_x on wsrpp_consumer (contact_id);

create table wsrpp_modes (
	consumer_id	varchar(40)	not null,
	portlet_mode	varchar(100)	not null
,constraint wsrpp_modes_p primary key (consumer_id,portlet_mode)
,constraint wsrpp_modes1_f foreign key (consumer_id) references wsrpp_consumer (consumer_id));


create table wsrpp_windw_sts (
	consumer_id	varchar(40)	not null,
	window_state	varchar(100)	not null
,constraint wsrpp_windw_sts_p primary key (consumer_id,window_state)
,constraint wsrpp_windw_sts1_f foreign key (consumer_id) references wsrpp_consumer (consumer_id));


create table wsrpp_usr_scops (
	consumer_id	varchar(40)	not null,
	user_scope	varchar(100)	not null
,constraint wsrpp_usr_scops_p primary key (consumer_id,user_scope)
,constraint wsrpp_usr_scops1_f foreign key (consumer_id) references wsrpp_consumer (consumer_id));


create table wsrpp_usrpr_dat (
	consumer_id	varchar(40)	not null,
	usr_prfle_data	varchar(100)	not null
,constraint wsrpp_usrpr_dat_p primary key (consumer_id,usr_prfle_data)
,constraint wsrpp_usrpr_dat1_f foreign key (consumer_id) references wsrpp_consumer (consumer_id));

commit;


