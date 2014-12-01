
-- the source for this section is 
-- drop_abandoned_order_views.sql



drop view drpt_tns_abndn_ord;
drop view drpt_abandon_ord;

commit;



-- the source for this section is 
-- drop_abandoned_order_ddl.sql



drop table drpt_session_ord;
drop table drpt_conv_order;
drop table dcs_user_abandoned;
drop table dcspp_ord_abandon;

commit;



-- the source for this section is 
-- drop_order_markers_ddl.sql



drop table dcspp_commerce_item_markers;
drop table dcs_order_markers;

commit;



-- the source for this section is 
-- drop_versioned_priceLists_ddl.sql



drop table dcs_plfol_chld;
drop table dcs_child_fol_pl;
drop table dcs_gen_fol_pl;
drop table dcs_price_level;
drop table dcs_price_levels;
drop table dcs_price;
drop table dcs_complex_price;
drop table dcs_price_list;

commit;



-- the source for this section is 
-- drop_versioned_claimable_ddl.sql



drop table dcspp_coupon_info;
drop table dcspp_coupon;
drop table dcspp_cp_folder;
drop table dcs_storecred_clm;
drop table dcspp_giftcert;
drop table dcspp_claimable;

commit;



-- the source for this section is 
-- drop_versioned_commerce_site_ddl.sql



drop table dcs_site;

commit;



-- the source for this section is 
-- drop_dcs_mappers.sql



drop table dcs_promo_grntd;
drop table dcs_promo_rvkd;
drop table dcs_ord_merge_evt;
drop table dcs_prom_used_evt;
drop table dcs_submt_ord_evt;
drop table dcs_cart_event;

commit;



-- the source for this section is 
-- drop_order_ddl.sql



drop table dcspp_schd_errmsg;
drop table dcspp_sched_error;
drop table dcspp_scherr_aux;
drop table dbcpp_sched_clone;
drop table dbcpp_sched_order;
drop table dcspp_manual_adj;
drop table dcspp_order_adj;
drop table dcspp_det_range;
drop table dcspp_det_price;
drop table dcspp_itmprice_det;
drop table dcspp_shipitem_tax;
drop table dcspp_ntaxshipitem;
drop table dcspp_taxshipitem;
drop table dcspp_shipitem_sub;
drop table dcspp_price_adjust;
drop table dcspp_amtinfo_adj;
drop table dcspp_ship_price;
drop table dcspp_tax_price;
drop table dcspp_item_price;
drop table dcspp_order_price;
drop table dcspp_amount_info;
drop table dcspp_payorder_rel;
drop table dcspp_payship_rel;
drop table dcspp_payitem_rel;
drop table dcspp_rel_range;
drop table dcspp_shipitem_rel;
drop table dcspp_cred_status;
drop table dcspp_debit_status;
drop table dcspp_auth_status;
drop table dcspp_sc_status;
drop table dcspp_gc_status;
drop table dcspp_cc_status;
drop table dcspp_pay_status;
drop table dcspp_bill_addr;
drop table dcspp_credit_card;
drop table dcspp_store_cred;
drop table dcspp_gift_cert;
drop table dcspp_item_ci;
drop table dcspp_subsku_item;
drop table dcspp_config_item;
drop table dcspp_pay_inst;
drop table dcspp_sg_hand_inst;
drop table dcspp_gift_inst;
drop table dcspp_hand_inst;
drop table dcspp_ship_addr;
drop table dcspp_ele_ship_grp;
drop table dcspp_hrd_ship_grp;
drop table dcspp_ship_inst;
drop table dcspp_order_rel;
drop table dcspp_order_item;
drop table dcspp_order_pg;
drop table dcspp_order_sg;
drop table dcspp_order_inst;
drop table dcspp_rel_orders;
drop table dcspp_relationship;
drop table dcspp_item;
drop table dcspp_pay_group;
drop table dcspp_ship_group;
drop table dcspp_order;

commit;



-- the source for this section is 
-- drop_user_giftlist_ddl.sql



drop table dcs_user_otherlist;
drop table dcs_user_giftlist;
drop table dcs_user_wishlist;
drop table dcs_giftlist_item;
drop table dcs_giftitem;
drop table dcs_giftinst;
drop table dcs_giftlist;

commit;



-- the source for this section is 
-- drop_user_promotion_ddl.sql



drop table dcs_usr_usedpromo;
drop table dcs_usr_actvpromo;
drop table dcs_usr_promostat;

commit;



-- the source for this section is 
-- drop_versioned_promotion_ddl.sql



drop table dcs_upsell_prods;
drop table dcs_prm_cls_vals;
drop table dcs_prm_tpl_vals;
drop table dcs_prm_site_grps;
drop table dcs_prm_sites;
drop table dcs_prm_cls_qlf;
drop table dcs_close_qualif;
drop table dcs_upsell_action;
drop table dcs_promo_upsell;
drop table dcs_discount_promo;
drop table dcs_promo_media;
drop table dcs_promotion;
drop table dcs_prm_folder;

commit;



-- the source for this section is 
-- drop_inventory_ddl.sql



drop table dcs_inventory;

commit;



-- the source for this section is 
-- drop_versioned_product_catalog_ddl.sql



drop table dcs_foreign_cat;
drop table dcs_config_opt;
drop table dcs_conf_options;
drop table dcs_config_prop;
drop table dcs_sku_conf;
drop table dcs_sku_replace;
drop table dcs_sku_aux_media;
drop table dcs_sku_media;
drop table dcs_sku_bndllnk;
drop table dcs_sku_link;
drop table dcs_sku_attr;
drop table dcs_prd_ancestors;
drop table dcs_prd_upslprd;
drop table dcs_prd_rltdprd;
drop table dcs_prd_groups;
drop table dcs_prd_skuattr;
drop table dcs_prd_chldsku;
drop table dcs_prd_aux_media;
drop table dcs_prd_media;
drop table dcs_prd_keywrds;
drop table dcs_cat_aux_media;
drop table dcs_cat_media;
drop table dcs_cat_keywrds;
drop table dcs_cat_rltdcat;
drop table dcs_cat_ancestors;
drop table dcs_cat_chldcat;
drop table dcs_cat_chldprd;
drop table dcs_cat_groups;
drop table dcs_sku;
drop table dcs_product_acl;
drop table dcs_product;
drop table dcs_category_acl;
drop table dcs_category;
drop table dcs_media_txt;
drop table dcs_media_bin;
drop table dcs_media_ext;
drop table dcs_media;
drop table dcs_folder;

commit;



-- the source for this section is 
-- drop_commerce_user.sql



drop table dps_usr_creditcard;
drop table dcs_user;
drop table dps_credit_card;

commit;



-- the source for this section is 
-- drop_versioned_custom_catalog_ddl.sql



drop table dcs_sku_sites;
drop table dcs_product_sites;
drop table dcs_category_sites;
drop table dcs_catalog_sites;
drop table dcs_sku_catalogs;
drop table dcs_prd_catalogs;
drop table dcs_cat_catalogs;
drop table dcs_prd_anc_cats;
drop table dcs_prd_prnt_cats;
drop table dcs_cat_prnt_cats;
drop table dcs_cat_anc_cats;
drop table dcs_ctlg_anc_cats;
drop table dcs_ind_anc_ctlgs;
drop table dcs_dir_anc_ctlgs;
drop table dcs_catfol_sites;
drop table dcs_catfol_chld;
drop table dcs_child_fol_cat;
drop table dcs_gen_fol_cat;
drop table dcs_skuinfo_rplc;
drop table dcs_sku_skuinfo;
drop table dcs_prdinfo_anc;
drop table dcs_prdinfo_rdprd;
drop table dcs_prd_prdinfo;
drop table dcs_catinfo_anc;
drop table dcs_cat_catinfo;
drop table dcs_cat_subroots;
drop table dcs_cat_subcats;
drop table dcs_sku_info;
drop table dcs_product_info;
drop table dcs_category_info;
drop table dcs_root_subcats;
drop table dcs_allroot_cats;
drop table dcs_root_cats;
drop table dcs_catalog;

commit;



-- the source for this section is 
-- drop_custom_catalog_user_ddl.sql



drop table dcs_user_catalog;

commit;


