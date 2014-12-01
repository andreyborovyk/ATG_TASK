
-- the source for this section is 
-- drop_b2b_reporting_views.sql



drop view drpt_part_purchsed
drop view drpt_ordr_org_cc
drop view drpt_ordr_buyr
drop view drpt_ordr_org
drop view drpt_ordr_by_date
drop view drpt_dlr_parts
drop view drpt_dlr_org_cc
drop view drpt_dlr_org_cc_o
drop view drpt_dlr_org_cc_s
drop view drpt_dlr_org_cc_i
drop view drpt_dlr_org_parts
drop view drpt_dlr_byr
drop view drpt_dlr_org



go

-- the source for this section is 
-- drop_organization_ddl.sql



drop table dbc_org_prefvndr
drop table dbc_org_billing
drop table dbc_org_shipping
drop table dbc_org_payment
drop table dbc_org_costctr
drop table dbc_org_approver
drop table dbc_org_contact
drop table dbc_organization



go

-- the source for this section is 
-- drop_b2b_user_ddl.sql



drop table dbc_buyer_plist
drop table dbc_buyer_prefvndr
drop table dbc_buyer_billing
drop table dbc_buyer_shipping
drop table dbc_buyer_payment
drop table dbc_buyer_approver
drop table dbc_buyer_costctr
drop table dbc_user
drop table dbc_cost_center



go

-- the source for this section is 
-- drop_contracts_ddl.sql



drop table dbc_contract_term
drop table dbc_contract



go

-- the source for this section is 
-- drop_invoice_ddl.sql



drop table dbc_invoice
drop table dbc_inv_pmt_terms
drop table dbc_inv_delivery



go

-- the source for this section is 
-- drop_b2b_order_ddl.sql



drop table dbcpp_pmt_req
drop table dbcpp_ccorder_rel
drop table dbcpp_ccship_rel
drop table dbcpp_ccitem_rel
drop table dbcpp_order_cc
drop table dbcpp_cost_center
drop table dbcpp_invoice_req
drop table dbcpp_appr_msgs
drop table dbcpp_apprsysmsgs
drop table dbcpp_authapprids
drop table dbcpp_approverids



go

-- the source for this section is 
-- drop_b2b_product_catalog_ddl.sql



drop table dbc_sku
drop table dbc_product
drop table dbc_measurement
drop table dbc_manufacturer



go
