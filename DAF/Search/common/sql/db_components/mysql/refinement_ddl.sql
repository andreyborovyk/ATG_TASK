


--  @version $Id: //product/DAF/version/10.0.3/Search/common/src/sql/refinement_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements, which will configure your database for use with the new refinement repository
-- Add the SRCH_REFINE_ELEMS table, which lists refine elements

create table srch_refine_elems (
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
,constraint srch_refine_elem_p primary key (id));

-- Add the SRCH_REFINE_SUBELS table, which lists any child refine elementsfor a refine element.

create table srch_refine_subels (
	id	varchar(40)	not null,
	seq	integer	not null,
	refine_element	varchar(40)	null
,constraint srch_refine_subl_p primary key (id,seq));

-- Add the SRCH_REFEL_ORDER table which lists the explicit order values for a refine element

create table srch_refel_order (
	id	varchar(40)	not null,
	seq	integer	not null,
	order_value	nvarchar(254)	not null
,constraint srch_refel_order_p primary key (id,seq));

-- Add the SRCH_REFEL_RANGE table which lists the explicit range values for a refine element

create table srch_refel_range (
	id	varchar(40)	not null,
	seq	integer	not null,
	range_value	nvarchar(254)	not null
,constraint srch_refel_range_p primary key (id,seq));

-- Add the SRCH_REFEL_SELECT table which lists the select values for a refine element

create table srch_refel_select (
	id	varchar(40)	not null,
	seq	integer	not null,
	select_value	nvarchar(254)	not null
,constraint srch_refel_selct_p primary key (id,seq));

-- Add the SRCH_REFEL_EXCLUDE table which lists the excluded values for a refine element

create table srch_refel_exclude (
	id	varchar(40)	not null,
	exclude_value	nvarchar(254)	not null
,constraint srch_refel_excl_p primary key (id,exclude_value));

-- Add the SRCH_SORT_OPTIONS table which lists available sort options

create table srch_sort_options (
	id	varchar(40)	not null,
	label	nvarchar(254)	not null,
	property	nvarchar(254)	not null,
	property_type	integer	not null,
	ascending	tinyint	not null
,constraint srch_sort_opts_p primary key (id));

-- The SRCH_REFINE_CONFIG table, which lists the generated refinement configurations.

create table srch_refine_config (
	id	varchar(40)	not null,
	type	integer	not null,
	refine_cfg_name	nvarchar(80)	null
,constraint srch_refine_cfg_p primary key (id));

-- List of refine elements that belong to a refine config	

create table srch_refcfg_elems (
	id	varchar(40)	not null,
	seq	integer	not null,
	refine_element	varchar(40)	null
,constraint srch_refcfg_els_p primary key (id,seq));

-- Add the list of sort options per refine element

create table srch_refine_sort (
	id	varchar(40)	not null,
	sort_option	varchar(40)	not null
,constraint srch_refine_sort_p primary key (id,sort_option));

-- ============================================= Search Merchandising==================================
-- Add the SRCH_CFG_DIM table, dimensionNode

create table srch_cfg_dimnode (
	dim_id	varchar(40)	not null,
	dim_type	integer	not null,
	dim_name	nvarchar(254)	not null,
	dim_desc	varchar(254)	null,
	dim_value	varchar(254)	null,
	dim_service	varchar(254)	null
,constraint srch_cfg_dim_p primary key (dim_id));

-- Add the SRCH_CFG_DTINFO table, dimensionTreeInfo 

create table srch_cfg_dtinfo (
	dti_id	varchar(40)	not null,
	dti_name	nvarchar(254)	not null,
	dti_desc	varchar(254)	null,
	dti_app	integer	not null,
	dti_root_id	varchar(40)	null
,constraint srch_cfg_dti_p primary key (dti_id)
,constraint srch_cfg_dim1_f foreign key (dti_root_id) references srch_cfg_dimnode (dim_id));

create index srch_cfg_dim1_x on srch_cfg_dtinfo (dti_root_id);
-- Add the SRCH_CFG_FOL table, dimensionFolder

create table srch_cfg_fol (
	fol_id	varchar(40)	not null,
	fol_dim_svc	varchar(40)	null
,constraint srch_cfg_fol_p primary key (fol_id));

-- Add the SRCH_CFG_RULE table, searchRule 

create table srch_cfg_rule (
	r_id	varchar(40)	not null,
	r_type	integer	not null,
	r_display_name	nvarchar(254)	null,
	r_enabled	tinyint	not null,
	r_rule_type	integer	null,
	r_rule	longtext charset utf8	not null,
	r_action	longtext	not null,
	r_desc	nvarchar(254)	null
,constraint srch_cfg_rule_p primary key (r_id));

-- Add the SRCH_CFG_SYNSET table, synset 

create table srch_cfg_synset (
	syn_id	varchar(40)	not null,
	syn_base_term	nvarchar(254)	not null,
	syn_reciprocal	tinyint	not null
,constraint srch_cfg_synset_p primary key (syn_id));

-- Add the SRCH_CFG_APROP table, availableRankingProperty

create table srch_cfg_aprop (
	arp_id	varchar(40)	not null,
	arp_name	nvarchar(254)	not null,
	arp_data_type	integer	not null,
	arp_value_type	integer	not null,
	arp_app	integer	not null
,constraint srch_cfg_aprop_p primary key (arp_id));

-- Add the SRCH_CFG_RPROP table, rankingProperty

create table srch_cfg_rprop (
	rp_id	varchar(40)	not null,
	rp_arp	varchar(40)	not null,
	rp_def_rank	integer	not null,
	rp_weighting	numeric(5,2)	not null,
	rp_rng_met	integer	null
,constraint srch_cfg_rprop_p primary key (rp_id));

-- Add the SRCH_CFG_CFG table, genericSearchConfig

create table srch_cfg_cfg (
	cfg_id	varchar(40)	not null,
	cfg_app	integer	not null,
	cfg_locale	varchar(40)	null,
	cfg_rm	varchar(40)	null,
	cfg_base_id	varchar(40)	null,
	use_base_rankprps	tinyint	not null
,constraint srch_cfg_cfg_p primary key (cfg_id));

-- Add the SRCH_CFG_CFG table, genericSearchConfig.baseSearchConfig & derivedSearchConfigs

create table srch_cfg_base (
	cfg_id	varchar(40)	not null,
	cfg_base_id	varchar(40)	not null
,constraint srch_cfg_base_p primary key (cfg_id)
,constraint srch_cfg_base1_f foreign key (cfg_id) references srch_cfg_dimnode (dim_id)
,constraint srch_cfg_base2_f foreign key (cfg_base_id) references srch_cfg_dimnode (dim_id));

create index srch_cfg_base1_x on srch_cfg_base (cfg_base_id);
-- Add the SRCH_CFG_FOL_CHLDCFGS table, dimensionFolder.searchConfigs (list)

create table srch_cfg_fol_chldcfgs (
	folder_id	varchar(40)	not null,
	srch_cfg_id	varchar(40)	not null,
	chldcfgs_seq	integer	not null
,constraint srch_cfg_ccfgs_p primary key (folder_id,chldcfgs_seq)
,constraint srch_cfg_cfg1_f foreign key (folder_id) references srch_cfg_dimnode (dim_id)
,constraint srch_cfg_cfg2_f foreign key (srch_cfg_id) references srch_cfg_dimnode (dim_id));

create index srch_cffolchld1_x on srch_cfg_fol_chldcfgs (srch_cfg_id);
-- Add the SRCH_CFG_FOL_CHLDFOL table, dimensionFolder.childDimensionFolders (list)

create table srch_cfg_fol_chldfol (
	folder_id	varchar(40)	not null,
	chldfol_seq	integer	not null,
	chld_fol_id	varchar(40)	not null
,constraint srch_cfg_chldfol_p primary key (folder_id,chldfol_seq)
,constraint srch_cfg_fol_f foreign key (folder_id) references srch_cfg_dimnode (dim_id));

-- Add the SRCH_CFG_RPSET table, genericSearchConfig.rankingProperties (set)

create table srch_cfg_rpset (
	rpset_cfg_id	varchar(40)	not null,
	rpset_rp_id	varchar(40)	not null
,constraint srch_cfg_rpset_p primary key (rpset_cfg_id,rpset_rp_id)
,constraint srch_cfg_rpset1_f foreign key (rpset_cfg_id) references srch_cfg_cfg (cfg_id));

-- Add the SRCH_CFG_VRPSET table, genericSearchConfig.variableRankingProperties (set)

create table srch_cfg_vrpset (
	vrpset_cfg_id	varchar(40)	not null,
	vrpset_rp_id	varchar(40)	not null
,constraint srch_cfg_vrpset_p primary key (vrpset_cfg_id,vrpset_rp_id)
,constraint srch_cfg_vrpset1_f foreign key (vrpset_cfg_id) references srch_cfg_cfg (cfg_id));

-- Add the SRCH_CFG_SYNLNK table, genericSearchConfig.synsets (list)

create table srch_cfg_synlnk (
	slnk_cfg_id	varchar(40)	not null,
	slnk_syn_id	varchar(40)	not null,
	slnk_seq	integer	not null
,constraint srch_cfg_synlnk_p primary key (slnk_cfg_id,slnk_seq)
,constraint srch_cfg_synlnk1_f foreign key (slnk_cfg_id) references srch_cfg_cfg (cfg_id));

-- Add the SRCH_CFG_DRULE table, genericSearchConfig.disabledBaseRules (set)

create table srch_cfg_drule (
	dr_cfg_id	varchar(40)	not null,
	dr_id	varchar(40)	not null
,constraint srch_cfg_drule_p primary key (dr_cfg_id,dr_id)
,constraint srch_cfg_drule1_f foreign key (dr_cfg_id) references srch_cfg_cfg (cfg_id)
,constraint srch_cfg_drule2_f foreign key (dr_id) references srch_cfg_rule (r_id));

create index srch_cfg_drule1_x on srch_cfg_drule (dr_id);
-- Add the SRCH_CFG_DSYN table, genericSearchConfig.disabledBaseSynsets (set)

create table srch_cfg_dsyn (
	ds_cfg_id	varchar(40)	not null,
	ds_id	varchar(40)	not null
,constraint srch_cfg_dsyn_p primary key (ds_cfg_id)
,constraint srch_cfg_dsyn1_f foreign key (ds_cfg_id) references srch_cfg_cfg (cfg_id)
,constraint srch_cfg_dsyn2_f foreign key (ds_id) references srch_cfg_synset (syn_id));

create index srch_cfg_dsyn1_x on srch_cfg_dsyn (ds_id);
-- Add the SRCH_CFG_ERULE table, genericSearchConfig.exclusionRules (list)

create table srch_cfg_erule (
	er_cfg_id	varchar(40)	not null,
	er_id	varchar(40)	not null,
	er_seq	integer	not null
,constraint srch_cfg_erule_p primary key (er_id,er_seq)
,constraint srch_cfg_erule1_f foreign key (er_cfg_id) references srch_cfg_cfg (cfg_id)
,constraint srch_cfg_erule2_f foreign key (er_id) references srch_cfg_rule (r_id));

create index srch_cfg_erule1_x on srch_cfg_erule (er_cfg_id);
-- Add the SRCH_CFG_RRULE table, genericSearchConfig.redirectionRules (list)

create table srch_cfg_rrule (
	rr_cfg_id	varchar(40)	not null,
	rr_id	varchar(40)	not null,
	rr_seq	integer	not null
,constraint srch_cfg_rrule_p primary key (rr_id,rr_seq)
,constraint srch_cfg_rrule1_f foreign key (rr_cfg_id) references srch_cfg_cfg (cfg_id)
,constraint srch_cfg_rrule2_f foreign key (rr_id) references srch_cfg_rule (r_id));

create index srch_cfg_rrule1_x on srch_cfg_rrule (rr_cfg_id);
-- Add the SRCH_CFG_PRULE table, genericSearchConfig.positioningRules (list)

create table srch_cfg_prule (
	pr_cfg_id	varchar(40)	not null,
	pr_id	varchar(40)	not null,
	pr_seq	integer	not null
,constraint srch_cfg_prule_p primary key (pr_id,pr_seq)
,constraint srch_cfg_prule1_f foreign key (pr_cfg_id) references srch_cfg_cfg (cfg_id)
,constraint srch_cfg_prule2_f foreign key (pr_id) references srch_cfg_rule (r_id));

create index srch_cfg_prule1_x on srch_cfg_prule (pr_cfg_id);
-- Add the SRCH_CFG_TERM table, synset.terms

create table srch_cfg_term (
	trm_syn_id	varchar(40)	not null,
	trm_term	nvarchar(254)	not null
,constraint srch_cfg_term_p primary key (trm_syn_id,trm_term)
,constraint srch_cfg_term_f foreign key (trm_syn_id) references srch_cfg_synset (syn_id));

-- Add the SRCH_CFG_RANK table, rankingProperty.valueRanks 

create table srch_cfg_rank (
	rank_rp_id	varchar(40)	not null,
	rank_value	nvarchar(254)	not null,
	rank_rank	integer	not null
,constraint srch_cfg_rank_p primary key (rank_rp_id,rank_value)
,constraint srch_cfg_rank_f foreign key (rank_rp_id) references srch_cfg_rprop (rp_id));

commit;


