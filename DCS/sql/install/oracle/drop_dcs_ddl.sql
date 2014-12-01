
-- the source for this section is 
-- drop_custom_catalog_reporting1.sql



drop view drpta_cat_sales;
drop view drptq_cat_sales;
drop view drptm_cat_sales;
drop view drptw_cat_sales;





-- the source for this section is 
-- drop_reporting_views2.sql



drop view drpta_m25_39_sales;
drop view drptq_m25_39_sales;
drop view drptm_m25_39_sales;
drop view drptw_m25_39_sales;
drop view drpta_m18_25_sales;
drop view drptq_m18_25_sales;
drop view drptm_m18_25_sales;
drop view drptw_m18_25_sales;
drop view drpta_promo_sales;
drop view drptq_promo_sales;
drop view drptm_promo_sales;
drop view drptw_promo_sales;
drop view drpta_prod_sales;
drop view drptq_prod_sales;
drop view drptm_prod_sales;
drop view drptw_prod_sales;





-- the source for this section is 
-- drop_custom_catalog_reporting.sql



drop view drpt_category;
drop view drpt_products;





-- the source for this section is 
-- drop_reporting_views.sql



drop view drpta_male_25_39;
drop view drptq_male_25_39;
drop view drptm_male_25_39;
drop view drptw_male_25_39;
drop view drpta_male_18_25;
drop view drptq_male_18_25;
drop view drptm_male_18_25;
drop view drptw_male_18_25;
drop view drpta_promotion;
drop view drptq_promotion;
drop view drptm_promotion;
drop view drptw_promotion;
drop view drpta_fiscal_info;
drop view drptq_fiscal_info;
drop view drptm_fiscal_info;
drop view drptw_fiscal_info;
drop view drpt_cart;
drop view drpt_visitor;
drop view drpta_orders;
drop view drptq_orders;
drop view drptm_orders;
drop view drptw_orders;
drop view drpta_gift_certs;
drop view drptq_gift_certs;
drop view drptm_gift_certs;
drop view drptw_gift_certs;
drop view drpta_cancels;
drop view drptq_cancels;
drop view drptm_cancels;
drop view drptw_cancels;
drop view drpt_cancels;
drop view drpt_taxes;
drop view drpt_shipping;
drop view drpta_carts;
drop view drptq_carts;
drop view drptm_carts;
drop view drptw_carts;
drop view drpta_browses;
drop view drptq_browses;
drop view drptm_browses;
drop view drptw_browses;
drop view drpt_sku_stock;
drop view drpt_ordered_items;
drop view drpta_discounts;
drop view drptq_discounts;
drop view drptm_discounts;
drop view drptw_discounts;
drop view drpt_discount;
drop view drpt_cost_of_goods;
drop view drpt_order;





-- the source for this section is 
-- drop_order_markers_ddl.sql



drop table dcspp_commerce_item_markers;
drop table dcs_order_markers;





-- the source for this section is 
-- drop_priceLists_ddl.sql



drop table dcs_plfol_chld;
drop table dcs_child_fol_pl;
drop table dcs_gen_fol_pl;
drop table dcs_price_level;
drop table dcs_price_levels;
drop table dcs_price;
drop table dcs_complex_price;
drop table dcs_price_list;





-- the source for this section is 
-- drop_claimable_ddl.sql



drop table dcspp_coupon_info;
drop table dcspp_coupon;
drop table dcspp_cp_folder;
drop table dcs_storecred_clm;
drop table dcspp_giftcert;
drop table dcspp_claimable;





-- the source for this section is 
-- drop_dcs_mappers.sql



drop table dcs_promo_grntd;
drop table dcs_promo_rvkd;
drop table dcs_ord_merge_evt;
drop table dcs_prom_used_evt;
drop table dcs_submt_ord_evt;
drop table dcs_cart_event;





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





-- the source for this section is 
-- drop_user_giftlist_ddl.sql



drop table dcs_user_otherlist;
drop table dcs_user_giftlist;
drop table dcs_user_wishlist;
drop table dcs_giftlist_item;
drop table dcs_giftitem;
drop table dcs_giftinst;
drop table dcs_giftlist;





-- the source for this section is 
-- drop_user_promotion_ddl.sql



drop table dcs_usr_usedpromo;
drop table dcs_usr_actvpromo;
drop table dcs_usr_promostat;





-- the source for this section is 
-- drop_promotion_ddl.sql



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





-- the source for this section is 
-- drop_inventory_ddl.sql



drop table dcs_inventory;





-- the source for this section is 
-- drop_custom_catalog_ddl.sql



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





-- the source for this section is 
-- drop_product_catalog_ddl.sql



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





-- the source for this section is 
-- drop_custom_catalog_user_ddl.sql



drop table dcs_user_catalog;





-- the source for this section is 
-- drop_commerce_user.sql



drop table dps_usr_creditcard;
drop table dcs_user;
drop table dps_credit_card;





-- the source for this section is 
-- drop_commerce_site_ddl.sql



drop table dcs_site;





-- the source for this section is 
-- drop_abandoned_order_ddl.sql



drop table drpt_session_ord;
drop table drpt_conv_order;
drop table dcs_user_abandoned;
drop table dcspp_ord_abandon;





-- the source for this section is 
-- drop_abandoned_order_views.sql



drop view drpt_tns_abndn_ord;
drop view drpt_abandon_ord;




