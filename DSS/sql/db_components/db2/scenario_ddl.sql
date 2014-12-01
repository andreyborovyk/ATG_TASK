


--  @version $Id: //product/DSS/version/10.0.3/templates/DSS/sql/scenario_ddl.xml#2 $$Change: 651448 $

create table dss_coll_scenario (
	id	varchar(25)	not null,
	scenario_name	varchar(255)	default null,
	modification_time	numeric(19,0)	default null,
	segment_name	varchar(255)	default null,
	creator_id	varchar(25)	default null,
	state	varchar(16)	default null
,constraint dss_coll_scenari_p primary key (id));

-- user_id references the user table but because it is a backwards referencewe cannot have a REFERENCES(dps_user) here.

create table dss_ind_scenario (
	id	varchar(25)	not null,
	scenario_name	varchar(255)	default null,
	modification_time	numeric(19,0)	default null,
	segment_name	varchar(255)	default null,
	creator_id	varchar(25)	default null,
	state	varchar(16)	default null,
	user_id	varchar(25)	not null
,constraint dss_ind_scenario_p primary key (id));

create index dss_indscenario1_x on dss_ind_scenario (modification_time);

create table dss_scenario_strs (
	id	varchar(25)	not null,
	tag	varchar(25)	not null,
	context_str	varchar(255)	default null
,constraint dss_scenario_str_p primary key (id,tag)
,constraint dss_scenrstrsid_f foreign key (id) references dss_ind_scenario (id));

create index dss_scn_st_idx on dss_scenario_strs (id);

create table dss_scenario_bools (
	id	varchar(25)	not null,
	tag	varchar(25)	not null,
	context_bool	numeric(1,0)	default null
,constraint dss_scenario_boo_p primary key (id,tag)
,constraint dss_scenrblsid_f foreign key (id) references dss_ind_scenario (id));

create index dss_scn_bo_idx on dss_scenario_bools (id);

create table dss_scenario_longs (
	id	varchar(25)	not null,
	tag	varchar(25)	not null,
	context_long	numeric(19,0)	default null
,constraint dss_scenario_lon_p primary key (id,tag)
,constraint dss_scenrlngsid_f foreign key (id) references dss_ind_scenario (id));

create index dss_scn_lg_idx on dss_scenario_longs (id);

create table dss_scenario_dbls (
	id	varchar(25)	not null,
	tag	varchar(25)	not null,
	context_dbl	numeric(15,4)	default null
,constraint dss_scenario_dbl_p primary key (id,tag)
,constraint dss_scenrdblsid_f foreign key (id) references dss_ind_scenario (id));

create index dss_scn_db_idx on dss_scenario_dbls (id);

create table dss_scenario_dates (
	id	varchar(25)	not null,
	tag	varchar(25)	not null,
	context_date	timestamp	default null
,constraint dss_scenario_dat_p primary key (id,tag)
,constraint dss_scenrdtsid_f foreign key (id) references dss_ind_scenario (id));

create index dss_scn_dt_idx on dss_scenario_dates (id);

create table dps_user_scenario (
	id	varchar(40)	not null,
	ind_scenario_id	varchar(25)	not null
,constraint dps_user_scenari_p primary key (ind_scenario_id)
,constraint dps_usrscnrid_f foreign key (id) references dps_user (id)
,constraint dps_usrsind_scnr_f foreign key (ind_scenario_id) references dss_ind_scenario (id));

create index dps_uscn_u_idx on dps_user_scenario (id);

create table dss_scenario_info (
	id	varchar(25)	not null,
	scenario_name	varchar(255)	default null,
	scenario_status	integer	default null,
	modification_time	numeric(19,0)	default null,
	creation_time	numeric(19,0)	default null,
	author	varchar(254)	default null,
	last_modified_by	varchar(254)	default null,
	sdl	blob(1048576)	default null,
	psm_version	integer	default null
,constraint dss_scenario_inf_p primary key (id));


create table dss_scen_mig_info (
	id	varchar(25)	not null,
	scenario_info_id	varchar(25)	not null,
	scenario_name	varchar(255)	default null,
	modification_time	numeric(19,0)	default null,
	psm_version	integer	default null,
	sdl	blob(1048576)	default null,
	migration_status	integer	default null
,constraint dss_scenmiginfo_pk primary key (id)
,constraint dss_scenmiginfo_fk foreign key (scenario_info_id) references dss_scenario_info (id));

create index dss_scenmiginfo_id on dss_scen_mig_info (scenario_info_id);

create table dss_mig_info_seg (
	id	varchar(25)	not null,
	idx	integer	not null,
	segment_name	varchar(255)	default null
,constraint dss_mig_infoseg_pk primary key (id,idx)
,constraint dss_mig_infoseg_fk foreign key (id) references dss_scen_mig_info (id));


create table dss_template_info (
	id	varchar(25)	not null,
	template_name	varchar(255)	default null,
	modification_time	numeric(19,0)	default null,
	creation_time	numeric(19,0)	default null,
	author	varchar(254)	default null,
	last_modified_by	varchar(254)	default null,
	sdl	blob(1048576)	default null
,constraint dss_template_inf_p primary key (id));


create table dss_coll_trans (
	id	varchar(25)	not null,
	scenario_name	varchar(255)	default null,
	modification_time	numeric(19,0)	default null,
	server_id	varchar(40)	default null,
	message_bean	blob(1048576)	default null,
	event_type	varchar(255)	default null,
	segment_name	varchar(255)	default null,
	state	varchar(16)	default null,
	coll_scenario_id	varchar(25)	default null,
	step	integer	default null,
	current_count	integer	default null,
	last_query_id	varchar(25)	default null
,constraint dss_coll_trans_p primary key (id)
,constraint dss_collcoll_scn_f foreign key (coll_scenario_id) references dss_coll_scenario (id));

create index dss_ctrcid_idx on dss_coll_trans (coll_scenario_id);

create table dss_ind_trans (
	id	varchar(25)	not null,
	scenario_name	varchar(255)	default null,
	modification_time	numeric(19,0)	default null,
	server_id	varchar(40)	default null,
	message_bean	blob(1048576)	default null,
	event_type	varchar(255)	default null,
	segment_name	varchar(255)	default null,
	state	varchar(16)	default null,
	last_query_id	varchar(25)	default null
,constraint dss_ind_trans_p primary key (id));


create table dss_deletion (
	id	varchar(25)	not null,
	scenario_name	varchar(255)	default null,
	modification_time	numeric(19,0)	default null
,constraint dss_deletion_p primary key (id));


create table dss_server_id (
	server_id	varchar(40)	not null,
	server_type	integer	not null
,constraint dss_server_id_p primary key (server_id));


create table dss_del_seg_name (
	id	varchar(25)	not null,
	idx	integer	not null,
	segment_name	varchar(255)	default null
,constraint dss_del_seg_name_p primary key (id,idx));

-- migration_info_id references the migration info table, but we don't have aREFERENCES dss_scen_mig_info(id) here, because we want to be ableto delete the migration info without deleting this row

create table dss_migration (
	id	varchar(25)	not null,
	scenario_name	varchar(255)	default null,
	old_mod_time	numeric(19,0)	default null,
	new_mod_time	numeric(19,0)	default null,
	migration_info_id	varchar(25)	default null
,constraint dss_migration_pk primary key (id));


create table dss_mig_seg_name (
	id	varchar(25)	not null,
	idx	integer	not null,
	segment_name	varchar(255)	default null
,constraint dss_mig_segname_pk primary key (id,idx)
,constraint dss_mig_segname_fk foreign key (id) references dss_migration (id));


create table dss_xref (
	id	varchar(25)	not null,
	scenario_name	varchar(255)	default null,
	reference_type	varchar(25)	default null,
	reference_target	varchar(255)	default null
,constraint dss_xref_p primary key (id));

-- user_id references the user table but because it is a backwards referencewe cannot have a REFERENCES(dps_user) here.

create table dss_profile_slot (
	id	varchar(25)	not null,
	slot_name	varchar(255)	default null,
	item_offset	numeric(19,0)	default null,
	user_id	varchar(25)	not null
,constraint dss_profile_slot_p primary key (id));


create table dps_user_slot (
	id	varchar(40)	not null,
	profile_slot_id	varchar(25)	not null
,constraint dps_user_slot_p primary key (id,profile_slot_id)
,constraint dps_usrsltid_f foreign key (id) references dps_user (id)
,constraint dps_usrsprofl_sl_f foreign key (profile_slot_id) references dss_profile_slot (id));

create index dps_usr_sltprfl_id on dps_user_slot (profile_slot_id);

create table dss_slot_items (
	slot_id	varchar(25)	not null,
	item_id	varchar(255)	default null,
	idx	integer	not null
,constraint dss_slot_items_p primary key (slot_id,idx)
,constraint dss_slotslot_d_f foreign key (slot_id) references dss_profile_slot (id));


create table dss_slot_priority (
	slot_id	varchar(25)	not null,
	idx	integer	not null,
	priority	numeric(19,0)	not null
,constraint dss_slot_priorit_p primary key (slot_id,idx)
,constraint dss_slotpslot_d_f foreign key (slot_id) references dss_profile_slot (id));

commit;


