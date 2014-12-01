


--     This file contains create table statements needed to configure your    database for use with the ATG WSRP-consumer subsystem.    
--     Tables for storing ATG WSRP consumer data    

create table wsrpc_resource (
	resource_id	varchar(40)	not null,
	resource_name	varchar(255)	not null
,constraint wsrpc_resource_p primary key (resource_id))


create table wsrpc_res_value (
	resource_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	res_value	varchar(512)	null
,constraint wsrpc_res_value_p primary key (resource_id,locale)
,constraint wsrpc_res_value_f foreign key (resource_id) references wsrpc_resource (resource_id))


create table wsrpc_res_list (
	res_list_id	varchar(40)	not null,
	extension	varchar(32)	null
,constraint wsrpc_res_list_p primary key (res_list_id))


create table wsrpc_resources (
	res_list_id	varchar(40)	not null,
	resource_id	varchar(40)	not null
,constraint wsrpc_resources_p primary key (res_list_id,resource_id)
,constraint wpc_res_lstid_f foreign key (res_list_id) references wsrpc_res_list (res_list_id)
,constraint wpc_res_resid_f foreign key (resource_id) references wsrpc_resource (resource_id))

create index wsrpc_resources1_x on wsrpc_resources (resource_id)

create table wsrpc_item_desc (
	item_desc_id	varchar(40)	not null,
	item_name	varchar(255)	not null,
	description	varchar(512)	null,
	dsc_xml_lang	varchar(512)	null,
	dsc_res_name	varchar(255)	null
,constraint wsrpc_item_desc_p primary key (item_desc_id))


create table wsrpc_serv_desc (
	service_id	varchar(40)	not null,
	requires_reg	tinyint	null,
	req_init_cookie	integer	null,
	res_list_id	varchar(40)	null
,constraint wsrpc_serv_desc_p primary key (service_id)
,constraint wpc_srv_dsc_rlid_f foreign key (res_list_id) references wsrpc_res_list (res_list_id)
,constraint wsrpc_serv_desc1_c check (requires_reg IN (0,1)))

create index wpc_sv_dsc_rlid_ix on wsrpc_serv_desc (res_list_id)

create table wsrpc_svcdsc_lc (
	service_id	varchar(40)	not null,
	sequence_num	integer	not null,
	locale	varchar(32)	null
,constraint wsrpc_svcdsc_lc_p primary key (service_id,sequence_num)
,constraint wpc_srvdsc_lc_id_f foreign key (service_id) references wsrpc_serv_desc (service_id))


create table wsrpc_urct_idsc (
	service_id	varchar(40)	not null,
	description_id	varchar(40)	not null
,constraint wsrpc_urct_idsc_p primary key (service_id,description_id)
,constraint wpc_urct_srv_id_f foreign key (service_id) references wsrpc_serv_desc (service_id)
,constraint wpc_urct_dsc_id_f foreign key (description_id) references wsrpc_item_desc (item_desc_id))

create index wsrpc_urct_idsc1_x on wsrpc_urct_idsc (description_id)

create table wsrpc_cprf_idsc (
	service_id	varchar(40)	not null,
	description_id	varchar(40)	not null
,constraint wsrpc_cprf_idsc_p primary key (service_id,description_id)
,constraint wpc_cprg_srv_id_f foreign key (service_id) references wsrpc_serv_desc (service_id)
,constraint wpc_cprf_dsc_id_f foreign key (description_id) references wsrpc_item_desc (item_desc_id))

create index wsrpc_cprf_idsc1_x on wsrpc_cprf_idsc (description_id)

create table wsrpc_cwst_dsc (
	service_id	varchar(40)	not null,
	description_id	varchar(40)	not null
,constraint wsrpc_cwst_dsc_p primary key (service_id,description_id)
,constraint wpc_cwst_srv_id_f foreign key (service_id) references wsrpc_serv_desc (service_id)
,constraint wpc_cwst_dsc_id_f foreign key (description_id) references wsrpc_item_desc (item_desc_id))

create index wsrpc_cwst_dsc1_x on wsrpc_cwst_dsc (description_id)

create table wsrpc_cmode_dsc (
	service_id	varchar(40)	not null,
	description_id	varchar(40)	not null
,constraint wsrpc_cmode_dsc_p primary key (service_id,description_id)
,constraint wpc_cmode_srv_id_f foreign key (service_id) references wsrpc_serv_desc (service_id)
,constraint wpc_cmode_dsc_id_f foreign key (description_id) references wsrpc_item_desc (item_desc_id))

create index wsrpc_cmode_dsc1_x on wsrpc_cmode_dsc (description_id)

create table wsrpc_mrkp_type (
	markup_type_id	varchar(40)	not null,
	mime_type	varchar(100)	not null
,constraint wsrpc_mrkp_type_p primary key (markup_type_id))


create table wsrpc_mrkp_mode (
	markup_type_id	varchar(40)	not null,
	markup_mode_id	varchar(255)	not null
,constraint wsrpc_mrkp_mode_p primary key (markup_type_id,markup_mode_id)
,constraint wpc_mrkp_mod_id_f foreign key (markup_type_id) references wsrpc_mrkp_type (markup_type_id))


create table wsrpc_mrkp_wnst (
	markup_type_id	varchar(40)	not null,
	mrkp_winstat_id	varchar(40)	not null
,constraint wsrpc_mrkp_wnst_p primary key (markup_type_id,mrkp_winstat_id)
,constraint wpc_mrkp_wnst_id_f foreign key (markup_type_id) references wsrpc_mrkp_type (markup_type_id))


create table wsrpc_mrkp_lcle (
	markup_type_id	varchar(40)	not null,
	mrkp_locale_id	varchar(40)	not null
,constraint wsrpc_mrkp_lcle_p primary key (markup_type_id,mrkp_locale_id)
,constraint wpc_mrkp_lcl_id_f foreign key (markup_type_id) references wsrpc_mrkp_type (markup_type_id))


create table wsrpc_prtlt_dsc (
	prtlt_dsc_id	varchar(40)	not null,
	handle	varchar(255)	not null,
	portlet_type	integer	not null,
	group_id	varchar(4000)	null,
	description	varchar(1024)	null,
	dsc_xml_lang	varchar(32)	null,
	dsc_res_name	varchar(100)	null,
	short_title	varchar(255)	null,
	shtitl_xml_lang	varchar(32)	null,
	shtitl_res_name	varchar(100)	null,
	title	varchar(255)	null,
	title_xml_lang	varchar(32)	null,
	title_res_name	varchar(100)	null,
	display_name	varchar(255)	null,
	dspnam_xml_lang	varchar(32)	null,
	dspnam_res_name	varchar(100)	null,
	uses_method_get	tinyint	null,
	dflt_mrkp_secur	tinyint	null,
	only_secure	tinyint	null,
	usr_ctxin_sessn	tinyint	null,
	tmplts_in_sessn	tinyint	null,
	has_usr_spec_st	tinyint	null,
	does_url_templt	tinyint	null,
	last_modfd_dt	datetime	null
,constraint wsrpc_prtlt_dsc_p primary key (prtlt_dsc_id)
,constraint wsrpc_prtlt_dsc1_c check (uses_method_get IN (0,1))
,constraint wsrpc_prtlt_dsc2_c check (dflt_mrkp_secur IN (0,1))
,constraint wsrpc_prtlt_dsc3_c check (only_secure IN (0,1))
,constraint wsrpc_prtlt_dsc4_c check (usr_ctxin_sessn IN (0,1))
,constraint wsrpc_prtlt_dsc5_c check (tmplts_in_sessn IN (0,1))
,constraint wsrpc_prtlt_dsc6_c check (has_usr_spec_st IN (0,1))
,constraint wsrpc_prtlt_dsc7_c check (does_url_templt IN (0,1)))


create table wsrpc_mrkp_typs (
	prtlt_dsc_id	varchar(40)	not null,
	markup_type_id	varchar(40)	not null
,constraint wsrpc_mrkp_typs_p primary key (prtlt_dsc_id,markup_type_id)
,constraint wpc_mktyp_pds_id_f foreign key (prtlt_dsc_id) references wsrpc_prtlt_dsc (prtlt_dsc_id)
,constraint wpc_mktyp_mtp_id_f foreign key (markup_type_id) references wsrpc_mrkp_type (markup_type_id))

create index wsrpc_mrkp_typs1_x on wsrpc_mrkp_typs (markup_type_id)

create table wsrpc_usr_ctgrs (
	prtlt_dsc_id	varchar(40)	not null,
	user_category	varchar(40)	not null
,constraint wsrpc_usr_ctgrs_p primary key (prtlt_dsc_id,user_category)
,constraint wpc_usrct_pds_id_f foreign key (prtlt_dsc_id) references wsrpc_prtlt_dsc (prtlt_dsc_id))


create table wsrpc_usrpr_itm (
	prtlt_dsc_id	varchar(40)	not null,
	usr_profile_itm	varchar(40)	not null
,constraint wsrpc_usrpr_itm_p primary key (prtlt_dsc_id,usr_profile_itm)
,constraint wpc_usrpr_pds_id_f foreign key (prtlt_dsc_id) references wsrpc_prtlt_dsc (prtlt_dsc_id))


create table wsrpc_producer (
	producer_id	varchar(40)	not null,
	name	varchar(255)	not null,
	producer_url	varchar(1024)	null,
	producer_dsc	varchar(1024)	null,
	reg_url	varchar(1024)	null,
	serv_desc_url	varchar(1024)	not null,
	markup_url	varchar(1024)	not null,
	prtlt_mgmt_url	varchar(1024)	null,
	reg_handle	varchar(255)	null,
	reg_state	image	null,
	service_id	varchar(40)	null,
	last_modfd_dt	datetime	null
,constraint wsrpc_producer_p primary key (producer_id)
,constraint wpc_srvdsc_id_f foreign key (service_id) references wsrpc_serv_desc (service_id))

create index wpc_prod_svdsc_ix on wsrpc_producer (service_id)

create table wsrpc_avl_prtlt (
	producer_id	varchar(40)	not null,
	portlet_id	varchar(40)	not null
,constraint wsrpc_avl_prtlt_p primary key (producer_id,portlet_id)
,constraint wpc_avlprt_prdid_f foreign key (producer_id) references wsrpc_producer (producer_id)
,constraint wpc_avlprt_prtid_f foreign key (portlet_id) references wsrpc_prtlt_dsc (prtlt_dsc_id))

create index wsrpc_avl_prtlt1_x on wsrpc_avl_prtlt (portlet_id)

create table wsrpc_regn_info (
	regn_info_id	varchar(40)	not null,
	name	varchar(255)	not null,
	agent	varchar(255)	not null,
	get_supported	tinyint	not null
,constraint wsrpc_regn_info_p primary key (regn_info_id)
,constraint wsrpc_regn_info1_c check (get_supported IN (0,1)))


create table wsrpc_modes (
	regn_info_id	varchar(40)	not null,
	portlet_mode	varchar(40)	not null
,constraint wsrpc_modes_p primary key (regn_info_id,portlet_mode)
,constraint wpc_modes_regid_f foreign key (regn_info_id) references wsrpc_regn_info (regn_info_id))


create table wsrpc_windw_sts (
	regn_info_id	varchar(40)	not null,
	window_state	varchar(40)	not null
,constraint wsrpc_windw_sts_p primary key (regn_info_id,window_state)
,constraint wpc_winst_regid_f foreign key (regn_info_id) references wsrpc_regn_info (regn_info_id))


create table wsrpc_usr_scops (
	regn_info_id	varchar(40)	not null,
	user_scope	varchar(40)	not null
,constraint wsrpc_usr_scops_p primary key (regn_info_id,user_scope)
,constraint wpc_usrscp_regid_f foreign key (regn_info_id) references wsrpc_regn_info (regn_info_id))


create table wsrpc_usrpr_dat (
	regn_info_id	varchar(40)	not null,
	usr_prf_data	varchar(40)	not null
,constraint wsrpc_usrpr_dat_p primary key (regn_info_id,usr_prf_data)
,constraint wpc_urpdat_regid_f foreign key (regn_info_id) references wsrpc_regn_info (regn_info_id))



go
