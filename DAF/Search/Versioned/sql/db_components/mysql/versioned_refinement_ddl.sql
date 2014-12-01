


--  @version $Id: //product/DAF/version/10.0.3/Search/common/src/sql/refinement_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements, which will configure your database for use with the new refinement repository
-- Add the SRCH_REFINE_ELEMS table, which lists refine elements

create table srch_refine_elems (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	id	varchar(40)	not null,
	type	integer	null,
	label	nvarchar(254)	not null,
	property	nvarchar(254)	not null,
	property_type	integer	not null,
	range_type	integer	not null,
	desired	integer	null,
	minimum	integer	null,
	incr	integer	null,
	sort_type	integer	null,
	levels	integer	null,
	parent_refine_elem	varchar(40)	null,
	priority	integer	null,
	remove_when_used	tinyint	not null,
	display_fragment	nvarchar(254)	null,
	max_values	integer	null,
	round_free_range	integer	null
,constraint srch_refine_elem_p primary key (id,asset_version));

create index srch_refine_el_wsx on srch_refine_elems (workspace_id);
create index srch_refine_el_cix on srch_refine_elems (checkin_date);
-- Add the SRCH_REFINE_SUBELS table, which lists any child refine elementsfor a refine element.

create table srch_refine_subels (
	asset_version	bigint	not null,
	id	varchar(40)	not null,
	seq	integer	not null,
	refine_element	varchar(40)	null
,constraint srch_refine_subl_p primary key (id,seq,asset_version));

-- Add the SRCH_REFEL_ORDER table which lists the explicit order values for a refine element

create table srch_refel_order (
	asset_version	bigint	not null,
	id	varchar(40)	not null,
	seq	integer	not null,
	order_value	nvarchar(254)	not null
,constraint srch_refel_order_p primary key (id,seq,asset_version));

-- Add the SRCH_REFEL_RANGE table which lists the explicit range values for a refine element

create table srch_refel_range (
	asset_version	bigint	not null,
	id	varchar(40)	not null,
	seq	integer	not null,
	range_value	nvarchar(254)	not null
,constraint srch_refel_range_p primary key (id,seq,asset_version));

-- Add the SRCH_REFEL_SELECT table which lists the select values for a refine element

create table srch_refel_select (
	asset_version	bigint	not null,
	id	varchar(40)	not null,
	seq	integer	not null,
	select_value	nvarchar(254)	not null
,constraint srch_refel_selct_p primary key (id,seq,asset_version));

-- Add the SRCH_REFEL_EXCLUDE table which lists the excluded values for a refine element

create table srch_refel_exclude (
	asset_version	bigint	not null,
	id	varchar(40)	not null,
	exclude_value	nvarchar(254)	not null
,constraint srch_refel_excl_p primary key (id,exclude_value,asset_version));

-- Add the SRCH_SORT_OPTIONS table which lists available sort options

create table srch_sort_options (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	id	varchar(40)	not null,
	label	nvarchar(254)	not null,
	property	nvarchar(254)	not null,
	property_type	integer	not null,
	ascending	tinyint	not null
,constraint srch_sort_opts_p primary key (id,asset_version));

create index srch_sort_opti_wsx on srch_sort_options (workspace_id);
create index srch_sort_opti_cix on srch_sort_options (checkin_date);
-- The SRCH_REFINE_CONFIG table, which lists the generated refinement configurations.

create table srch_refine_config (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	id	varchar(40)	not null,
	type	integer	not null,
	refine_cfg_name	nvarchar(80)	null
,constraint srch_refine_cfg_p primary key (id,asset_version));

create index srch_refine_co_wsx on srch_refine_config (workspace_id);
create index srch_refine_co_cix on srch_refine_config (checkin_date);
-- List of refine elements that belong to a refine config	

create table srch_refcfg_elems (
	asset_version	bigint	not null,
	id	varchar(40)	not null,
	seq	integer	not null,
	refine_element	varchar(40)	null
,constraint srch_refcfg_els_p primary key (id,seq,asset_version));

-- Add the list of sort options per refine element

create table srch_refine_sort (
	asset_version	bigint	not null,
	id	varchar(40)	not null,
	sort_option	varchar(40)	not null
,constraint srch_refine_sort_p primary key (id,sort_option,asset_version));

-- ============================================= Search Merchandising==================================
-- Add the SRCH_CFG_DIM table, dimensionNode

create table srch_cfg_dimnode (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	dim_id	varchar(40)	not null,
	dim_type	integer	not null,
	dim_name	nvarchar(254)	not null,
	dim_desc	varchar(254)	null,
	dim_value	varchar(254)	null,
	dim_service	varchar(254)	null
,constraint srch_cfg_dim_p primary key (dim_id,asset_version));

create index srch_cfg_dimno_wsx on srch_cfg_dimnode (workspace_id);
create index srch_cfg_dimno_cix on srch_cfg_dimnode (checkin_date);
-- Add the SRCH_CFG_DTINFO table, dimensionTreeInfo 

create table srch_cfg_dtinfo (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	dti_id	varchar(40)	not null,
	dti_name	nvarchar(254)	not null,
	dti_desc	varchar(254)	null,
	dti_app	integer	not null,
	dti_root_id	varchar(40)	null
,constraint srch_cfg_dti_p primary key (dti_id,asset_version));

create index srch_cfg_dtinf_wsx on srch_cfg_dtinfo (workspace_id);
create index srch_cfg_dtinf_cix on srch_cfg_dtinfo (checkin_date);
-- Add the SRCH_CFG_FOL table, dimensionFolder

create table srch_cfg_fol (
	asset_version	bigint	not null,
	fol_id	varchar(40)	not null,
	fol_dim_svc	varchar(40)	null
,constraint srch_cfg_fol_p primary key (fol_id,asset_version));

-- Add the SRCH_CFG_RULE table, searchRule 

create table srch_cfg_rule (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	r_id	varchar(40)	not null,
	r_type	integer	not null,
	r_display_name	nvarchar(254)	null,
	r_enabled	tinyint	not null,
	r_rule_type	integer	null,
	r_rule	longtext charset utf8	not null,
	r_action	longtext	not null,
	r_desc	nvarchar(254)	null
,constraint srch_cfg_rule_p primary key (r_id,asset_version));

create index srch_cfg_rule_wsx on srch_cfg_rule (workspace_id);
create index srch_cfg_rule_cix on srch_cfg_rule (checkin_date);
-- Add the SRCH_CFG_SYNSET table, synset 

create table srch_cfg_synset (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	syn_id	varchar(40)	not null,
	syn_base_term	nvarchar(254)	not null,
	syn_reciprocal	tinyint	not null
,constraint srch_cfg_synset_p primary key (syn_id,asset_version));

create index srch_cfg_synse_wsx on srch_cfg_synset (workspace_id);
create index srch_cfg_synse_cix on srch_cfg_synset (checkin_date);
-- Add the SRCH_CFG_APROP table, availableRankingProperty

create table srch_cfg_aprop (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	arp_id	varchar(40)	not null,
	arp_name	nvarchar(254)	not null,
	arp_data_type	integer	not null,
	arp_value_type	integer	not null,
	arp_app	integer	not null
,constraint srch_cfg_aprop_p primary key (arp_id,asset_version));

create index srch_cfg_aprop_wsx on srch_cfg_aprop (workspace_id);
create index srch_cfg_aprop_cix on srch_cfg_aprop (checkin_date);
-- Add the SRCH_CFG_RPROP table, rankingProperty

create table srch_cfg_rprop (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	rp_id	varchar(40)	not null,
	rp_arp	varchar(40)	not null,
	rp_def_rank	integer	not null,
	rp_weighting	numeric(5,2)	not null,
	rp_rng_met	integer	null
,constraint srch_cfg_rprop_p primary key (rp_id,asset_version));

create index srch_cfg_rprop_wsx on srch_cfg_rprop (workspace_id);
create index srch_cfg_rprop_cix on srch_cfg_rprop (checkin_date);
-- Add the SRCH_CFG_CFG table, genericSearchConfig

create table srch_cfg_cfg (
	asset_version	bigint	not null,
	cfg_id	varchar(40)	not null,
	cfg_app	integer	not null,
	cfg_locale	varchar(40)	null,
	cfg_rm	varchar(40)	null,
	cfg_base_id	varchar(40)	null,
	use_base_rankprps	tinyint	not null
,constraint srch_cfg_cfg_p primary key (cfg_id,asset_version));

-- Add the SRCH_CFG_CFG table, genericSearchConfig.baseSearchConfig & derivedSearchConfigs

create table srch_cfg_base (
	asset_version	bigint	not null,
	cfg_id	varchar(40)	not null,
	cfg_base_id	varchar(40)	not null
,constraint srch_cfg_base_p primary key (cfg_id,asset_version));

-- Add the SRCH_CFG_FOL_CHLDCFGS table, dimensionFolder.searchConfigs (list)

create table srch_cfg_fol_chldcfgs (
	sec_asset_version	bigint	not null,
	asset_version	bigint	not null,
	folder_id	varchar(40)	not null,
	srch_cfg_id	varchar(40)	not null,
	chldcfgs_seq	integer	not null
,constraint srch_cfg_ccfgs_p primary key (folder_id,chldcfgs_seq,asset_version,sec_asset_version));

-- Add the SRCH_CFG_FOL_CHLDFOL table, dimensionFolder.childDimensionFolders (list)

create table srch_cfg_fol_chldfol (
	sec_asset_version	bigint	not null,
	asset_version	bigint	not null,
	folder_id	varchar(40)	not null,
	chldfol_seq	integer	not null,
	chld_fol_id	varchar(40)	not null
,constraint srch_cfg_chldfol_p primary key (folder_id,chldfol_seq,asset_version,sec_asset_version));

-- Add the SRCH_CFG_RPSET table, genericSearchConfig.rankingProperties (set)

create table srch_cfg_rpset (
	sec_asset_version	bigint	not null,
	asset_version	bigint	not null,
	rpset_cfg_id	varchar(40)	not null,
	rpset_rp_id	varchar(40)	not null
,constraint srch_cfg_rpset_p primary key (rpset_cfg_id,rpset_rp_id,asset_version,sec_asset_version));

-- Add the SRCH_CFG_VRPSET table, genericSearchConfig.variableRankingProperties (set)

create table srch_cfg_vrpset (
	asset_version	bigint	not null,
	vrpset_cfg_id	varchar(40)	not null,
	vrpset_rp_id	varchar(40)	not null
,constraint srch_cfg_vrpset_p primary key (vrpset_cfg_id,vrpset_rp_id,asset_version));

-- Add the SRCH_CFG_SYNLNK table, genericSearchConfig.synsets (list)

create table srch_cfg_synlnk (
	sec_asset_version	bigint	not null,
	asset_version	bigint	not null,
	slnk_cfg_id	varchar(40)	not null,
	slnk_syn_id	varchar(40)	not null,
	slnk_seq	integer	not null
,constraint srch_cfg_synlnk_p primary key (slnk_cfg_id,slnk_seq,asset_version,sec_asset_version));

-- Add the SRCH_CFG_DRULE table, genericSearchConfig.disabledBaseRules (set)

create table srch_cfg_drule (
	asset_version	bigint	not null,
	dr_cfg_id	varchar(40)	not null,
	dr_id	varchar(40)	not null
,constraint srch_cfg_drule_p primary key (dr_cfg_id,dr_id,asset_version));

-- Add the SRCH_CFG_DSYN table, genericSearchConfig.disabledBaseSynsets (set)

create table srch_cfg_dsyn (
	asset_version	bigint	not null,
	ds_cfg_id	varchar(40)	not null,
	ds_id	varchar(40)	not null
,constraint srch_cfg_dsyn_p primary key (ds_cfg_id,asset_version));

-- Add the SRCH_CFG_ERULE table, genericSearchConfig.exclusionRules (list)

create table srch_cfg_erule (
	asset_version	bigint	not null,
	er_cfg_id	varchar(40)	not null,
	er_id	varchar(40)	not null,
	er_seq	integer	not null
,constraint srch_cfg_erule_p primary key (er_id,er_seq,asset_version));

-- Add the SRCH_CFG_RRULE table, genericSearchConfig.redirectionRules (list)

create table srch_cfg_rrule (
	asset_version	bigint	not null,
	rr_cfg_id	varchar(40)	not null,
	rr_id	varchar(40)	not null,
	rr_seq	integer	not null
,constraint srch_cfg_rrule_p primary key (rr_id,rr_seq,asset_version));

-- Add the SRCH_CFG_PRULE table, genericSearchConfig.positioningRules (list)

create table srch_cfg_prule (
	asset_version	bigint	not null,
	pr_cfg_id	varchar(40)	not null,
	pr_id	varchar(40)	not null,
	pr_seq	integer	not null
,constraint srch_cfg_prule_p primary key (pr_id,pr_seq,asset_version));

-- Add the SRCH_CFG_TERM table, synset.terms

create table srch_cfg_term (
	asset_version	bigint	not null,
	trm_syn_id	varchar(40)	not null,
	trm_term	nvarchar(254)	not null
,constraint srch_cfg_term_p primary key (trm_syn_id,trm_term,asset_version));

-- Add the SRCH_CFG_RANK table, rankingProperty.valueRanks 

create table srch_cfg_rank (
	asset_version	bigint	not null,
	rank_rp_id	varchar(40)	not null,
	rank_value	nvarchar(254)	not null,
	rank_rank	integer	not null
,constraint srch_cfg_rank_p primary key (rank_rp_id,rank_value,asset_version));

commit;


