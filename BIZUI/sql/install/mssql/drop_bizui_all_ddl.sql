
-- the source for this section is 
-- drop_viewmapping_ddl.sql



drop table vmap_pvattrdef
drop table vmap_pv2pvad_rel
drop table vmap_ivattrdef
drop table vmap_iv2ivad_rel
drop table vmap_cattrval_rel
drop table vmap_attrval_rel
drop table vmap_attrval
drop table vmap_pvm
drop table vmap_ivm2pvm_rel
drop table vmap_pv
drop table vmap_iv
drop table vmap_im2ivm_rel
drop table vmap_ivm
drop table vmap_mode
drop table vmap_fh
drop table vmap_im



go


drop table epub_int_prj_hist
drop table epub_int_user



go


drop table epub_wf_server_id
drop table epub_wf_mig_segs
drop table epub_wf_migration
drop table epub_wf_del_segs
drop table epub_wf_deletion
drop table epub_wf_ind_trans
drop table epub_wf_coll_trans
drop table epub_wf_templ_info
drop table epub_wf_mg_inf_seg
drop table epub_wf_mig_info
drop table epub_workflow_info
drop table epub_workflow_vfs
drop table epub_workflow_ris
drop table epub_workflow_dats
drop table epub_workflow_dbls
drop table epub_workflow_lngs
drop table epub_workflow_bls
drop table epub_workflow_strs
drop table epub_ind_workflow
drop table epub_coll_workflow



go


drop table epub_binary_file
drop table epub_text_file
drop table epub_file_asset
drop table epub_file_folder



go


drop table avm_asset_lock
drop table avm_workspace
drop table avm_devline



go


drop table epub_process_data



go


drop table epub_dep_log
drop table epub_dep_err_parm
drop table epub_deploy_proj
drop table epub_deployment
drop table epub_proc_taskinfo
drop table epub_proc_history
drop table epub_proc_prv_prj
drop table epub_process
drop table epub_pr_history
drop table epub_pr_tg_ap_ts
drop table epub_pr_tg_dp_id
drop table epub_pr_tg_dp_ts
drop table epub_prj_tg_snsht
drop table epub_pr_tg_status
drop table epub_prj_targt_ws
drop table epub_project
drop table epub_dest_map
drop table epub_exclud_asset
drop table epub_includ_asset
drop table epub_princ_asset
drop table epub_tr_agents
drop table epub_tl_targets
drop table epub_topology
drop table epub_tr_dest
drop table epub_target
drop table epub_agent
drop table epub_agent_trnprt
drop table epub_taskinfo
drop table epub_his_act_parm
drop table epub_history



go


drop table alt_chan_usr_rel
drop table alt_channel
drop table alt_gear_def_rel
drop table alt_gear_def
drop table alt_gear_rel
drop table alt_gear
drop table alt_userpref_rel
drop table alt_user_pref
drop table alt_user_alert_rel
drop table alt_group
drop table alt_user



go


drop table mem_membership_req



go


drop table page_gear_rem
drop table page_gear_add
drop table comm_gear_rem
drop table comm_gear_add
drop table paf_page_visit



go


drop table paf_ct_gr_roles
drop table paf_ct_gr_acl
drop table paf_ct_roles
drop table paf_ct_alt_gr_rel
drop table paf_ct_alt_gear
drop table paf_ct_gears
drop table paf_ct_gr_fldrs
drop table paf_ct_gr_ln_descs
drop table paf_ct_gr_ln_names
drop table paf_ct_gr_iparams
drop table paf_ct_gear
drop table paf_ct_region_grs
drop table paf_ct_pg_regions
drop table paf_ct_region
drop table paf_ct_page
drop table paf_ct_pagefolder
drop table paf_ct_child_fldr
drop table paf_ct_folder
drop table paf_comm_template
drop table paf_gdf_child_item
drop table paf_pf_child_item
drop table paf_cf_gfldrs
drop table paf_cf_child_item
drop table paf_page_regions
drop table paf_page_acl
drop table paf_page_ln_descs
drop table paf_page_ln_names
drop table paf_page
drop table paf_comm_ldescs
drop table paf_comm_lnames
drop table paf_community_acl
drop table paf_base_gear_role
drop table paf_gear_roles
drop table paf_base_comm_role
drop table paf_comm_roles
drop table paf_comm_gfldrs
drop table paf_comm_gears
drop table paf_community
drop table paf_template
drop table paf_ptpl_ln_descs
drop table paf_ptpl_ln_names
drop table paf_page_template
drop table paf_layout_regdefs
drop table paf_layout
drop table paf_cpal_ln_descs
drop table paf_cpal_ln_names
drop table paf_col_palette
drop table paf_styl_ln_descs
drop table paf_styl_ln_names
drop table paf_style
drop table paf_region_gears
drop table paf_region
drop table paf_region_def
drop table paf_gear_ln_descs
drop table paf_gear_ln_names
drop table paf_gear_iparams
drop table paf_gear_acl
drop table paf_gear
drop table paf_gd_l10n_descs
drop table paf_gd_l10n_names
drop table paf_gd_uparams
drop table paf_gd_iparams
drop table paf_gd_cprops
drop table paf_gear_def
drop table paf_gear_modes
drop table paf_title_template
drop table paf_display_modes
drop table paf_device_output
drop table paf_device_outputs
drop table paf_gear_prmvals
drop table paf_gear_param
drop table paf_fldr_ln_descs
drop table paf_fldr_ln_names
drop table paf_child_folder
drop table paf_folder_acl
drop table paf_folder



go

-- the source for this section is 
-- drop_internal_scenario_ddl.sql



drop table dpi_scenario_value
drop table dsi_slot_priority
drop table dsi_slot_items
drop table dpi_user_slot
drop table dsi_profile_slot
drop table dsi_xref
drop table dsi_mig_seg_name
drop table dsi_migration
drop table dsi_del_seg_name
drop table dsi_server_id
drop table dsi_deletion
drop table dsi_ind_trans
drop table dsi_coll_trans
drop table dsi_template_info
drop table dsi_mig_info_seg
drop table dsi_scen_mig_info
drop table dsi_scenario_info
drop table dpi_user_scenario
drop table dsi_scenario_dates
drop table dsi_scenario_dbls
drop table dsi_scenario_longs
drop table dsi_scenario_bools
drop table dsi_scenario_strs
drop table dsi_ind_scenario
drop table dsi_coll_scenario



go

-- the source for this section is 
-- drop_profile_bp_markers_ddl.sql



drop table dss_user_bpmarkers



go

-- the source for this section is 
-- drop_business_process_rpt_ddl.sql



drop table drpt_stage_reached



go

-- the source for this section is 
-- drop_markers_ddl.sql



drop table dps_usr_markers
drop table dps_markers



go

-- the source for this section is 
-- drop_scenario_ddl.sql



drop table dss_slot_priority
drop table dss_slot_items
drop table dps_user_slot
drop table dss_profile_slot
drop table dss_xref
drop table dss_mig_seg_name
drop table dss_migration
drop table dss_del_seg_name
drop table dss_server_id
drop table dss_deletion
drop table dss_ind_trans
drop table dss_coll_trans
drop table dss_template_info
drop table dss_mig_info_seg
drop table dss_scen_mig_info
drop table dss_scenario_info
drop table dps_user_scenario
drop table dss_scenario_dates
drop table dss_scenario_dbls
drop table dss_scenario_longs
drop table dss_scenario_bools
drop table dss_scenario_strs
drop table dss_ind_scenario
drop table dss_coll_scenario



go

-- the source for this section is 
-- drop_dss_mappers.sql



drop table dss_audit_trail



go

-- the source for this section is 
-- drop_dps_mappers.sql



drop table dps_scenario_value
drop table dss_dps_admin_up
drop table dss_dps_update
drop table dss_dps_admin_prop
drop table dss_dps_property
drop table dss_dps_admin_reg
drop table dss_dps_inbound
drop table dss_dps_referrer
drop table dss_dps_click
drop table dss_dps_view_item
drop table dss_dps_page_visit
drop table dss_dps_event



go

-- the source for this section is 
-- drop_das_mappers.sql



drop table dss_das_form
drop table dss_das_event



go

-- the source for this section is 
-- drop_internal_user_ddl.sql



drop table dpi_user_prevpwd
drop table dpi_rolefold_chld
drop table dpi_child_folder
drop table dpi_folder
drop table dpi_org_ancestors
drop table dpi_org_chldorg
drop table dpi_user_org_anc
drop table dpi_user_sec_orgs
drop table dpi_user_org
drop table dpi_relativerole
drop table dpi_role_rel_org
drop table dpi_org_role
drop table dpi_user_roles
drop table dpi_role_right
drop table dpi_access_right
drop table dpi_template_role
drop table dpi_role
drop table dpi_email_address
drop table dpi_user_mailing
drop table dpi_mail_server
drop table dpi_mail_batch
drop table dpi_mail_trackdata
drop table dpi_mailing
drop table dpi_other_addr
drop table dpi_user_address
drop table dpi_contact_info
drop table dpi_user
drop table dpi_organization



go

-- the source for this section is 
-- drop_versioned_personalization.sql



drop table dps_seg_list_folder
drop table dps_seg_list_name
drop table dps_seg_list



go

-- the source for this section is 
-- drop_logging.sql



drop table dps_log_id
drop table dps_pgrp_con_sum
drop table dps_pgrp_req_sum
drop table dps_con_req_sum
drop table dps_con_req
drop table dps_session_sum
drop table dps_reqname_sum
drop table dps_request
drop table dps_user_event_sum
drop table dps_user_event
drop table dps_event_type



go

-- the source for this section is 
-- drop_user.sql



drop table dps_user_prevpwd
drop table dps_rolefold_chld
drop table dps_child_folder
drop table dps_folder
drop table dps_org_ancestors
drop table dps_org_chldorg
drop table dps_user_org_anc
drop table dps_user_org
drop table dps_relativerole
drop table dps_role_rel_org
drop table dps_org_role
drop table dps_user_roles
drop table dps_role
drop table dps_email_address
drop table dps_user_mailing
drop table dps_mail_server
drop table dps_mail_batch
drop table dps_mail_trackdata
drop table dps_mailing
drop table dps_other_addr
drop table dps_user_address
drop table dps_contact_info
drop table dps_user
drop table dps_organization



go

-- the source for this section is 
-- drop_versioned_site_ddl.sql



drop table site_group_shareable_types
drop table site_group_sites
drop table site_group
drop table site_types
drop table site_additional_urls
drop table site_configuration
drop table site_template



go

-- the source for this section is 
-- drop_versioned_seo_ddl.sql



drop table das_seo_sites
drop table das_seo_tag



go

-- the source for this section is 
-- drop_sitemap_ddl.sql



drop table das_sitemap
drop table das_siteindex



go

-- the source for this section is 
-- drop_deployment_ddl.sql



drop table das_dep_fail_info
drop table das_dd_markers
drop table das_depl_item_ref
drop table das_depl_repmaps
drop table das_depl_options
drop table das_depl_depldat
drop table das_file_mark
drop table das_rep_mark
drop table das_deploy_mark
drop table das_deploy_data
drop table das_thread_batch
drop table das_depl_progress
drop table das_deployment



go

-- the source for this section is 
-- drop_media.sql



drop table media_txt
drop table media_bin
drop table media_ext
drop table media_base
drop table media_folder



go

-- the source for this section is 
-- drop_nucleus_security_ddl.sql



drop table das_ns_acls
drop table das_nucl_sec



go

-- the source for this section is 
-- drop_integration_data_ddl.sql



drop table if_integ_data



go

-- the source for this section is 
-- drop_sds.sql



drop table das_sds



go

-- the source for this section is 
-- drop_gsa_subscribers_ddl.sql



drop table das_gsa_subscriber



go

-- the source for this section is 
-- drop_staff_ddl.sql



drop table das_acct_prevpwd
drop table das_group_assoc
drop table das_account



go

-- the source for this section is 
-- drop_sql_jms_ddl.sql



drop procedure dms_topic_flag
drop procedure dms_queue_flag
drop table dms_msg_properties
drop table dms_msg
drop table dms_topic_entry
drop table dms_topic_sub
drop table dms_topic
drop table dms_queue_entry
drop table dms_queue_recv
drop table dms_queue
drop table dms_client



go

-- the source for this section is 
-- drop_dms_limbo_ddl.sql



drop table dms_limbo_delay
drop table dms_limbo_ptypes
drop table dms_limbo_props
drop table dms_limbo_body
drop table dms_limbo_replyto
drop table dms_limbo_msg
drop table dms_limbo



go

-- the source for this section is 
-- drop_cluster_name_ddl.sql



drop table das_cluster_name



go

-- the source for this section is 
-- drop_id_generator.sql



drop table das_secure_id_gen
drop table das_id_generator



go
