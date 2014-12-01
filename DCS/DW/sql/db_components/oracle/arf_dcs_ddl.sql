


-- ATG Reporting Framework DDL - DCS
-- =================================
-- Fact: CURRENCY CONVERSION
-- Dimensions: DAY
-- SOURCE CURRENCY
-- DESTINATION CURRENCY
-- Measures: SRC TO DEST CONV RATE
-- DEST TO SRC CONV RATE

create table ARF_CURRENCY_CONV (
	DAY_ID	varchar2(40)	not null,
	SRC_CURRENCY_ID	number(5)	not null,
	DST_CURRENCY_ID	number(5)	not null,
	SRC_DST_CONV_RATE	number(19,7)	not null,
	DST_SRC_CONV_RATE	number(19,7)	not null
,constraint ARF_CURNCY_CONV_P primary key (DAY_ID,SRC_CURRENCY_ID,DST_CURRENCY_ID)
,constraint ARF_CRNCY_CONV_F1 foreign key (DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_CRNCY_CONV_F2 foreign key (SRC_CURRENCY_ID) references ARF_CURRENCY (ID)
,constraint ARF_CRNCY_CONV_F3 foreign key (DST_CURRENCY_ID) references ARF_CURRENCY (ID));

create index ARF_CRNCY_CONV_X2 on ARF_CURRENCY_CONV (SRC_CURRENCY_ID);
create index ARF_CRNCY_CONV_X3 on ARF_CURRENCY_CONV (DST_CURRENCY_ID);
-- Dimension: CATEGORY
-- Levels: CATEGORY

create table ARF_CATEGORY (
	ID	number(10)	not null,
	NCATEGORY_ID	varchar2(40)	not null,
	NAME	varchar2(254)	not null,
	NAME_EN	varchar2(254)	null,
	DESCRIPTION	varchar2(254)	null,
	DESCRIPTION_EN	varchar2(254)	null,
	PARENT_CAT_ID	number(10)	null,
	RECORD_LAST_UPDATE	timestamp	null,
	RECORD_START_DATE	timestamp	null,
	RECORD_END_DATE	timestamp	null,
	MOST_RECENT	number(1)	default 1 not null,
	DELETED	number(1)	default 0 not null
,constraint ARF_CATEGORY_P primary key (ID)
,constraint ARF_CATEGORY_F1 foreign key (PARENT_CAT_ID) references ARF_CATEGORY (ID));

create index ARF_CAT_PCID_X2 on ARF_CATEGORY (PARENT_CAT_ID);
create index ARF_CAT_NCID_X1 on ARF_CATEGORY (NCATEGORY_ID);
-- Dimension: PRODUCT
-- Levels: PRODUCT

create table ARF_PRODUCT (
	ID	number(10)	not null,
	NPRODUCT_ID	varchar2(40)	not null,
	NAME	varchar2(254)	not null,
	NAME_EN	varchar2(254)	null,
	DESCRIPTION	varchar2(254)	null,
	DESCRIPTION_EN	varchar2(254)	null,
	PARENT_CAT_ID	number(10)	null,
	BRAND	varchar2(254)	null,
	BRAND_EN	varchar2(254)	null,
	RECORD_LAST_UPDATE	timestamp	null,
	RECORD_START_DATE	timestamp	null,
	RECORD_END_DATE	timestamp	null,
	MOST_RECENT	number(1)	default 1 not null,
	DELETED	number(1)	default 0 not null
,constraint ARF_PRODUCT_P primary key (ID)
,constraint ARF_PRODUCT_F1 foreign key (PARENT_CAT_ID) references ARF_CATEGORY (ID));

create index ARF_PROD_PCID_X2 on ARF_PRODUCT (PARENT_CAT_ID);
create index ARF_PROD_NPID_X1 on ARF_PRODUCT (NPRODUCT_ID);
-- Dimension: SKU
-- Levels: SKU

create table ARF_SKU (
	ID	number(10)	not null,
	NSKU_ID	varchar2(40)	not null,
	NAME	varchar2(254)	not null,
	NAME_EN	varchar2(254)	null,
	DESCRIPTION	varchar2(254)	null,
	DESCRIPTION_EN	varchar2(254)	null,
	PARENT_PROD_ID	number(10)	null,
	WHOLESALE_PRICE	number(19,7)	null,
	LIST_PRICE	number(19,7)	null,
	SALE_PRICE	number(19,7)	null,
	ON_SALE	number(1,0)	null,
	RECORD_LAST_UPDATE	timestamp	null,
	RECORD_START_DATE	timestamp	null,
	RECORD_END_DATE	timestamp	null,
	MOST_RECENT	number(1)	default 1 not null,
	DELETED	number(1)	default 0 not null
,constraint ARF_SKU_P primary key (ID)
,constraint ARF_SKU_F1 foreign key (PARENT_PROD_ID) references ARF_PRODUCT (ID));

create index ARF_SKU_PSID_X2 on ARF_SKU (PARENT_PROD_ID);
create index ARF_SKU_NSKUI_X1 on ARF_SKU (NSKU_ID);
-- Dimension: PROMOTION
-- Levels: PROMOTION

create table ARF_PROMOTION (
	ID	number(5)	not null,
	NPROMO_ID	varchar2(40)	not null,
	PROMO_NAME	varchar2(254)	not null,
	PROMO_NAME_EN	varchar2(254)	null,
	PROMO_DESC	varchar2(254)	null,
	PROMO_DESC_EN	varchar2(254)	null,
	PROMO_TYPE	varchar2(254)	null,
	PROMO_ENABLED	number(1,0)	null,
	PROMO_BEGIN_USABLE	timestamp	null,
	PROMO_END_USABLE	timestamp	null,
	PROMO_GLOBAL	number(1,0)	null,
	RECORD_LAST_UPDATE	timestamp	null,
	RECORD_START_DATE	timestamp	null,
	RECORD_END_DATE	timestamp	null,
	MOST_RECENT	number(1)	default 1 not null,
	DELETED	number(1)	default 0 not null
,constraint ARF_PROMOTION_P primary key (ID));

create index ARF_PROMO_NPID_X1 on ARF_PROMOTION (NPROMO_ID);

create table ARF_PROMOGRP (
	ID	number(5)	not null,
	NAME	varchar2(254)	not null,
	HASH_VALUE	varchar2(254)	not null,
	LENGTH	number(5)	not null
,constraint ARF_PROMOGRP_P primary key (ID));

create index ARF_PROMOGRP_X1 on ARF_PROMOGRP (HASH_VALUE);

create table ARF_PROMOGRP_MBRS (
	PROMOGRP_ID	number(5)	not null,
	PROMOTION_ID	number(5)	not null
,constraint ARF_PRMGRP_MBRS_P primary key (PROMOGRP_ID,PROMOTION_ID)
,constraint ARF_PROMOG_MBRS_F1 foreign key (PROMOGRP_ID) references ARF_PROMOGRP (ID)
,constraint ARF_PROMOG_MBRS_F2 foreign key (PROMOTION_ID) references ARF_PROMOTION (ID));

create index ARF_PROMOG_MBRS1_X on ARF_PROMOGRP_MBRS (PROMOTION_ID);
-- Dimension: Sales Channel
-- Levels: Sales Channel

create table ARF_SALES_CHANNEL (
	ID	number(3,0)	not null,
	NCODE	number(10)	not null,
	NAME	varchar2(254)	not null,
	NAME_EN	varchar2(254)	null,
	DESCRIPTION	varchar2(254)	null,
	DESCRIPTION_EN	varchar2(254)	null
,constraint ARF_SCHANNEL_P primary key (ID));

-- Fact: LINE ITEM
-- Dimensions: DAY
-- TIME
-- SKU
-- PRODUCT
-- CATEGORY
-- CUSTOMER
-- AGENT
-- ORIGIN SALES CHANNEL
-- SUBMIT SALES CHANNEL
-- STIMULUS GROUP
-- SEGMENT CLUSTER
-- PROMOTION GROUP
-- BILLING REGION
-- SHIPPING REGION
-- LOCAL CURRENCY
-- Measures: QUANTITY
-- LOCAL UNIT PRICE
-- LOCAL LINE ITEM GROSS REVENUE
-- LOCAL LINE ITEM DISCOUNT AMOUNT
-- LOCAL LINE ITEM MARKDOWN DISCOUNT AMOUNT
-- LOCAL ORDER DISCOUNT AMOUNT ALLOCATION
-- LOCAL ORDER TAX REVENUE ALLOCATION
-- LOCAL ORDER SHIPPING REVENUE ALLOCATION
-- LOCAL LINE ITEM NET REVENUE
-- LOCAL ORDER NET REVENUE
-- LOCAL ORDER MANUAL ADJUSTMENTS DEBIT ALLOCATION
-- LOCAL ORDER MANUAL ADJUSTMENTS CREDIT ALLOCATION
-- LOCAL LINE ITEM PRICE OVERRIDE AMOUNT
-- STANDARD UNIT PRICE
-- STANDARD LINE ITEM GROSS REVENUE
-- STANDARD LINE ITEM DISCOUNT AMOUNT
-- STANDARD LINE ITEM MARKDOWN DISCOUNT AMOUNT
-- STANDARD ORDER DISCOUNT AMOUNT ALLOCATION
-- STANDARD ORDER TAX REVENUE ALLOCATION
-- STANDARD ORDER SHIPPING REVENUE ALLOCATION
-- STANDARD LINE ITEM NET REVENUE
-- STANDARD ORDER NET REVENUE
-- STANDARD ORDER MANUAL ADJUSTMENTS DEBIT ALLOCATION
-- STANDARD ORDER MANUAL ADJUSTMENTS CREDIT ALLOCATION
-- STANDARD LINE ITEM PRICE OVERRIDE AMOUNT

create table ARF_LINE_ITEM (
	SUBMIT_TIMESTAMP	timestamp	null,
	SUBMIT_DAY_ID	varchar2(40)	not null,
	SUBMIT_TIME_ID	number(10)	not null,
	SKU_ID	number(10)	not null,
	PRODUCT_ID	number(10)	not null,
	CATEGORY_ID	number(10)	not null,
	CUSTOMER_ID	number(10)	not null,
	NCUSTOMER_ID	varchar2(40)	null,
	AGENT_ID	varchar2(40)	null,
	ORIGIN_SALES_CHANNEL_ID	number(3,0)	null,
	SUBMIT_SALES_CHANNEL_ID	number(3,0)	null,
	STIMGRP_ID	number(10)	not null,
	SEGCLSTR_ID	number(10)	not null,
	PROMOGRP_ID	number(5)	not null,
	BILLING_REGION_ID	number(5)	not null,
	SHIPPING_REGION_ID	number(5)	not null,
	LOCAL_CURRENCY_ID	number(5)	not null,
	DEMOGRAPHIC_ID	number(5)	not null,
	SITE_VISIT_ID	number(19)	not null,
	ORDER_ID	number(10)	not null,
	LINE_ITEM_ID	number(19)	not null,
	NORDER_ID	varchar2(40)	not null,
	NLINE_ITEM_ID	varchar2(40)	not null,
	SESSION_ID	varchar2(128)	null,
	QUESTION_ID	number(10)	not null,
	QUANTITY	number(10)	not null,
	IS_MARKDOWN	number(1)	default 0 not null,
	LOCAL_UNIT_PRICE	number(19,7)	not null,
	LOCAL_GROSS_REVENUE	number(19,7)	not null,
	LOCAL_DISCOUNT_AMOUNT	number(19,7)	not null,
	LOCAL_MARKDOWN_DISC_AMOUNT	number(19,7)	not null,
	LOCAL_ORDER_TAX_ALLOC	number(19,7)	not null,
	LOCAL_ORDER_SHIPPING_ALLOC	number(19,7)	not null,
	LOCAL_ORDER_DISCOUNT_ALLOC	number(19,7)	not null,
	LOCAL_NET_REVENUE	number(19,7)	not null,
	LOCAL_ORDER_NET_REVENUE	number(19,7)	not null,
	LOCAL_APPSMT_DBT_ALLOC_AMT	number(19,7)	null,
	LOCAL_APPSMT_CDT_ALLOC_AMT	number(19,7)	null,
	LOCAL_PRICE_OVERRIDE_AMT	number(19,7)	null,
	STANDARD_UNIT_PRICE	number(19,7)	not null,
	STANDARD_GROSS_REVENUE	number(19,7)	not null,
	STANDARD_DISCOUNT_AMOUNT	number(19,7)	not null,
	STANDARD_MARKDOWN_DISC_AMOUNT	number(19,7)	not null,
	STANDARD_ORDER_TAX_ALLOC	number(19,7)	not null,
	STANDARD_ORDER_SHIPPING_ALLOC	number(19,7)	not null,
	STANDARD_ORDER_DISCOUNT_ALLOC	number(19,7)	not null,
	STANDARD_NET_REVENUE	number(19,7)	not null,
	STANDARD_ORDER_NET_REVENUE	number(19,7)	not null,
	STANDARD_APPSMT_DBT_ALLOC_AMT	number(19,7)	null,
	STANDARD_APPSMT_CDT_ALLOC_AMT	number(19,7)	null,
	STANDARD_PRICE_OVERRIDE_AMT	number(19,7)	null,
	SUBMITTED_SITE_ID	number(5)	not null,
	ORIGIN_SITE_ID	number(5)	not null,
	ITEM_SITE_ID	number(5)	not null
,constraint ARF_LINE_ITEM_P primary key (LINE_ITEM_ID)
,constraint ARF_LINE_ITEM_F1 foreign key (SUBMIT_DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_LINE_ITEM_F2 foreign key (SUBMIT_TIME_ID) references ARF_TIME_TOD (ID)
,constraint ARF_LINE_ITEM_F3 foreign key (SKU_ID) references ARF_SKU (ID)
,constraint ARF_LINE_ITEM_F4 foreign key (PRODUCT_ID) references ARF_PRODUCT (ID)
,constraint ARF_LINE_ITEM_F5 foreign key (CATEGORY_ID) references ARF_CATEGORY (ID)
,constraint ARF_LINE_ITEM_F6 foreign key (CUSTOMER_ID) references ARF_USER (ID)
,constraint ARF_LINE_ITEM_F7 foreign key (STIMGRP_ID) references ARF_STIMGRP (ID)
,constraint ARF_LINE_ITEM_F8 foreign key (SEGCLSTR_ID) references ARF_SEGCLSTR (ID)
,constraint ARF_LINE_ITEM_F9 foreign key (PROMOGRP_ID) references ARF_PROMOGRP (ID)
,constraint ARF_LINE_ITEM_F10 foreign key (BILLING_REGION_ID) references ARF_GEO_REGION (ID)
,constraint ARF_LINE_ITEM_F11 foreign key (SHIPPING_REGION_ID) references ARF_GEO_REGION (ID)
,constraint ARF_LINE_ITEM_F12 foreign key (LOCAL_CURRENCY_ID) references ARF_CURRENCY (ID)
,constraint ARF_LINE_ITEM_F13 foreign key (DEMOGRAPHIC_ID) references ARF_DEMOGRAPHIC (ID)
,constraint ARF_LINE_ITEM_F14 foreign key (ORIGIN_SALES_CHANNEL_ID) references ARF_SALES_CHANNEL (ID)
,constraint ARF_LINE_ITEM_F15 foreign key (SUBMIT_SALES_CHANNEL_ID) references ARF_SALES_CHANNEL (ID)
,constraint ARF_LINE_ITEM_F16 foreign key (AGENT_ID) references ARF_IU_USER (ID)
,constraint ARF_LINE_ITEM_F17 foreign key (QUESTION_ID) references ARF_QUESTION (ID)
,constraint ARF_LINE_ITEM_F18 foreign key (SUBMITTED_SITE_ID) references ARF_SITE (ID)
,constraint ARF_LINE_ITEM_F19 foreign key (ORIGIN_SITE_ID) references ARF_SITE (ID)
,constraint ARF_LINE_ITEM_F20 foreign key (ITEM_SITE_ID) references ARF_SITE (ID));

create index ARF_LINE_ITEM_X23 on ARF_LINE_ITEM (SUBMITTED_SITE_ID);
create index ARF_LINE_ITEM_X24 on ARF_LINE_ITEM (ORIGIN_SITE_ID);
create index ARF_LINE_ITEM_X25 on ARF_LINE_ITEM (ITEM_SITE_ID);
create index ARF_LINE_ITEM_X1 on ARF_LINE_ITEM (SUBMIT_DAY_ID);
create index ARF_LINE_ITEM_X2 on ARF_LINE_ITEM (SUBMIT_TIME_ID);
create index ARF_LINE_ITEM_X3 on ARF_LINE_ITEM (SKU_ID);
create index ARF_LINE_ITEM_X4 on ARF_LINE_ITEM (PRODUCT_ID);
create index ARF_LINE_ITEM_X5 on ARF_LINE_ITEM (CATEGORY_ID);
create index ARF_LINE_ITEM_X6 on ARF_LINE_ITEM (CUSTOMER_ID);
create index ARF_LINE_ITEM_X7 on ARF_LINE_ITEM (STIMGRP_ID);
create index ARF_LINE_ITEM_X8 on ARF_LINE_ITEM (SEGCLSTR_ID);
create index ARF_LINE_ITEM_X9 on ARF_LINE_ITEM (PROMOGRP_ID);
create index ARF_LINE_ITEM_X10 on ARF_LINE_ITEM (BILLING_REGION_ID);
create index ARF_LINE_ITEM_X11 on ARF_LINE_ITEM (SHIPPING_REGION_ID);
create index ARF_LINE_ITEM_X12 on ARF_LINE_ITEM (LOCAL_CURRENCY_ID);
create index ARF_LINE_ITEM_X13 on ARF_LINE_ITEM (DEMOGRAPHIC_ID);
create index ARF_LINE_ITEM_X14 on ARF_LINE_ITEM (SITE_VISIT_ID);
create index ARF_LINE_ITEM_X15 on ARF_LINE_ITEM (SESSION_ID);
create index ARF_LINE_ITEM_X16 on ARF_LINE_ITEM (NORDER_ID);
create index ARF_LINE_ITEM_X17 on ARF_LINE_ITEM (ORDER_ID);
create index ARF_LINE_ITEM_X18 on ARF_LINE_ITEM (IS_MARKDOWN);
create index ARF_LINE_ITEM_X19 on ARF_LINE_ITEM (ORIGIN_SALES_CHANNEL_ID);
create index ARF_LINE_ITEM_X20 on ARF_LINE_ITEM (SUBMIT_SALES_CHANNEL_ID);
create index ARF_LINE_ITEM_X21 on ARF_LINE_ITEM (AGENT_ID);
create index ARF_LINE_ITEM_X22 on ARF_LINE_ITEM (QUESTION_ID);
create index ARF_LINE_ITEM_X26 on ARF_LINE_ITEM (NCUSTOMER_ID);
-- Dimension: Return Reason and Disposition

create table ARF_RET_REASON_DISPOSITION (
	ID	number(10)	not null,
	NREASON_ID	varchar2(40)	not null,
	DESCRIPTION	varchar2(254)	null,
	DESCRIPTION_EN	varchar2(254)	null,
	DISPOSITION	number(1)	default 0 null
,constraint ARF_RRD_P primary key (ID));

-- Fact: RETURN LINE ITEM
-- Dimensions: DAY
-- TIME
-- SKU
-- PRODUCT
-- CUSTOMER
-- LOCAL CURRENCY
-- RETURN SALES CHANNEL
-- LOCAL CURRENCY
-- AGENT
-- RETURN
-- RETURN REASON
-- ITEM SITE
-- SUBMITTED SITE
-- Measures: QUANTITY
-- LOCAL TAX REFUND AMOUNT ALLOCATION
-- LOCAL SHIPPING REFUND AMOUNT ALLOCATION
-- LOCAL OTHER REFUND AMOUNT ALLOCATION
-- LOCAL RETURN FEE AMOUNT ALLOCATION
-- LOCAL ITEM REFUND AMOUNT
-- LOCAL TOTAL ADJUSTMENTS AMOUNT
-- LOCAL TOTAL REFUND AMOUNT
-- LOCAL TAX REFUND AMOUNT ALLOCATION
-- LOCAL SHIPPING REFUND AMOUNT ALLOCATION
-- LOCAL OTHER REFUND AMOUNT ALLOCATION
-- LOCAL RETURN FEE AMOUNT ALLOCATION
-- LOCAL ITEM REFUND AMOUNT
-- LOCAL TOTAL ADJUSTMENTS AMOUNT
-- LOCAL TOTAL REFUND AMOUNT

create table ARF_RETURN_ITEM (
	SUBMIT_TIMESTAMP	timestamp	null,
	SUBMIT_DAY_ID	varchar2(40)	not null,
	SUBMIT_TIME_ID	number(10)	not null,
	SKU_ID	number(10)	not null,
	PRODUCT_ID	number(10)	not null,
	CUSTOMER_ID	number(10)	not null,
	LOCAL_CURRENCY_ID	number(5)	not null,
	NRETURN_ID	varchar2(40)	not null,
	NORIGINAL_ORDER_ID	varchar2(40)	not null,
	NEXCHANGE_ORDER_ID	varchar2(40)	null,
	RETURN_ITEM_ID	number(19)	not null,
	NRETURN_ITEM_ID	varchar2(40)	not null,
	QUANTITY	number(19)	not null,
	EXCHANGE	number(1)	default 0 not null,
	RETURN_SALES_CHANNEL_ID	number(3,0)	not null,
	AGENT_ID	varchar2(40)	not null,
	RETURN_REASON_ID	number(10)	not null,
	ITEM_SITE_ID	number(5)	not null,
	SUBMITTED_SITE_ID	number(5)	not null,
	LOCAL_ITEM_REFUND	number(19,7)	not null,
	LOCAL_TOTAL_REFUND	number(19,7)	not null,
	LOCAL_TOTAL_ADJ_ALLOC	number(19,7)	not null,
	LOCAL_SHIPPING_REFUND_ALLOC	number(19,7)	not null,
	LOCAL_TAX_REFUND_ALLOC	number(19,7)	not null,
	LOCAL_OTHER_REFUND_ALLOC	number(19,7)	not null,
	LOCAL_RETURN_FEE_ALLOC	number(19,7)	not null,
	STD_ITEM_REFUND	number(19,7)	not null,
	STD_TOTAL_REFUND	number(19,7)	not null,
	STD_TOTAL_ADJ_ALLOC	number(19,7)	not null,
	STD_SHIPPING_REFUND_ALLOC	number(19,7)	not null,
	STD_TAX_REFUND_ALLOC	number(19,7)	not null,
	STD_OTHER_REFUND_ALLOC	number(19,7)	not null,
	STD_RETURN_FEE_ALLOC	number(19,7)	not null
,constraint ARF_RI_P primary key (RETURN_ITEM_ID)
,constraint ARF_RI_F1 foreign key (SUBMIT_DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_RI_F2 foreign key (SUBMIT_TIME_ID) references ARF_TIME_TOD (ID)
,constraint ARF_RI_F3 foreign key (SKU_ID) references ARF_SKU (ID)
,constraint ARF_RI_F4 foreign key (PRODUCT_ID) references ARF_PRODUCT (ID)
,constraint ARF_RI_F5 foreign key (CUSTOMER_ID) references ARF_USER (ID)
,constraint ARF_RI_F6 foreign key (LOCAL_CURRENCY_ID) references ARF_CURRENCY (ID)
,constraint ARF_RI_F7 foreign key (RETURN_SALES_CHANNEL_ID) references ARF_SALES_CHANNEL (ID)
,constraint ARF_RI_F8 foreign key (AGENT_ID) references ARF_IU_USER (ID)
,constraint ARF_RI_F10 foreign key (RETURN_REASON_ID) references ARF_RET_REASON_DISPOSITION (ID)
,constraint ARF_RI_F11 foreign key (ITEM_SITE_ID) references ARF_SITE (ID)
,constraint ARF_RI_F12 foreign key (SUBMITTED_SITE_ID) references ARF_SITE (ID));

create index ARF_RI_X1 on ARF_RETURN_ITEM (SUBMIT_DAY_ID);
create index ARF_RI_X2 on ARF_RETURN_ITEM (SUBMIT_TIME_ID);
create index ARF_RI_X3 on ARF_RETURN_ITEM (SKU_ID);
create index ARF_RI_X4 on ARF_RETURN_ITEM (PRODUCT_ID);
create index ARF_RI_X5 on ARF_RETURN_ITEM (CUSTOMER_ID);
create index ARF_RI_X6 on ARF_RETURN_ITEM (AGENT_ID);
create index ARF_RI_X7 on ARF_RETURN_ITEM (LOCAL_CURRENCY_ID);
create index ARF_RI_X8 on ARF_RETURN_ITEM (NORIGINAL_ORDER_ID);
create index ARF_RI_X9 on ARF_RETURN_ITEM (RETURN_REASON_ID);
create index ARF_RI_X10 on ARF_RETURN_ITEM (RETURN_SALES_CHANNEL_ID);
create index ARF_RI_X11 on ARF_RETURN_ITEM (ITEM_SITE_ID);
create index ARF_RI_X12 on ARF_RETURN_ITEM (SUBMITTED_SITE_ID);

create table ARF_PROMOTION_USAGE (
	PROMOTION_USAGE_ID	number(19)	not null,
	PROMOTION_ID	number(5)	not null,
	USAGE_TIMESTAMP	timestamp	null,
	DAY_ID	varchar2(40)	not null,
	TIME_ID	number(10)	not null,
	SKU_ID	number(10)	not null,
	PRODUCT_ID	number(10)	not null,
	CATEGORY_ID	number(10)	not null,
	CUSTOMER_ID	number(10)	not null,
	SEGCLSTR_ID	number(10)	not null,
	BILLING_REGION_ID	number(5)	not null,
	LOCAL_CURRENCY_ID	number(5)	not null,
	DEMOGRAPHIC_ID	number(5)	not null,
	SITE_VISIT_ID	number(19)	not null,
	ORDER_ID	number(10)	not null,
	NORDER_ID	varchar2(40)	not null,
	SESSION_ID	varchar2(128)	null,
	AGENT_ID	varchar2(40)	null,
	ORIGIN_SALES_CHANNEL_ID	number(3,0)	null,
	SUBMIT_SALES_CHANNEL_ID	number(3,0)	null,
	QUANTITY	number(10)	not null,
	LOCAL_NET_REVENUE	number(19,7)	not null,
	LOCAL_ORDER_NET_REVENUE	number(19,7)	not null,
	LOCAL_DISCOUNT_AMOUNT	number(19,7)	not null,
	STANDARD_NET_REVENUE	number(19,7)	not null,
	STANDARD_ORDER_NET_REVENUE	number(19,7)	not null,
	STANDARD_DISCOUNT_AMOUNT	number(19,7)	not null,
	SITE_ID	number(5)	not null
,constraint ARF_PU_FACT_P primary key (PROMOTION_USAGE_ID)
,constraint ARF_PROMOUSAGE_F1 foreign key (PROMOTION_ID) references ARF_PROMOTION (ID)
,constraint ARF_PROMOUSAGE_F2 foreign key (DAY_ID) references ARF_TIME_DAY (ID)
,constraint ARF_PROMOUSAGE_F3 foreign key (TIME_ID) references ARF_TIME_TOD (ID)
,constraint ARF_PROMOUSAGE_F4 foreign key (SKU_ID) references ARF_SKU (ID)
,constraint ARF_PROMOUSAGE_F5 foreign key (PRODUCT_ID) references ARF_PRODUCT (ID)
,constraint ARF_PROMOUSAGE_F6 foreign key (CATEGORY_ID) references ARF_CATEGORY (ID)
,constraint ARF_PROMOUSAGE_F7 foreign key (CUSTOMER_ID) references ARF_USER (ID)
,constraint ARF_PROMOUSAGE_F8 foreign key (SEGCLSTR_ID) references ARF_SEGCLSTR (ID)
,constraint ARF_PROMOUSAGE_F9 foreign key (BILLING_REGION_ID) references ARF_GEO_REGION (ID)
,constraint ARF_PROMOUSAGE_F10 foreign key (LOCAL_CURRENCY_ID) references ARF_CURRENCY (ID)
,constraint ARF_PROMOUSAGE_F11 foreign key (DEMOGRAPHIC_ID) references ARF_DEMOGRAPHIC (ID)
,constraint ARF_PROMOUSAGE_F12 foreign key (AGENT_ID) references ARF_IU_USER (ID)
,constraint ARF_PROMOUSAGE_F13 foreign key (ORIGIN_SALES_CHANNEL_ID) references ARF_SALES_CHANNEL (ID)
,constraint ARF_PROMOUSAGE_F14 foreign key (SUBMIT_SALES_CHANNEL_ID) references ARF_SALES_CHANNEL (ID)
,constraint ARF_PROMOUSAGE_F15 foreign key (SITE_ID) references ARF_SITE (ID));

create index ARF_PROMOUSAGE_X1 on ARF_PROMOTION_USAGE (PROMOTION_ID);
create index ARF_PROMOUSAGE_X2 on ARF_PROMOTION_USAGE (DAY_ID);
create index ARF_PROMOUSAGE_X3 on ARF_PROMOTION_USAGE (TIME_ID);
create index ARF_PROMOUSAGE_X4 on ARF_PROMOTION_USAGE (SKU_ID);
create index ARF_PROMOUSAGE_X5 on ARF_PROMOTION_USAGE (PRODUCT_ID);
create index ARF_PROMOUSAGE_X6 on ARF_PROMOTION_USAGE (CATEGORY_ID);
create index ARF_PROMOUSAGE_X7 on ARF_PROMOTION_USAGE (CUSTOMER_ID);
create index ARF_PROMOUSAGE_X8 on ARF_PROMOTION_USAGE (SEGCLSTR_ID);
create index ARF_PROMOUSAGE_X9 on ARF_PROMOTION_USAGE (BILLING_REGION_ID);
create index ARF_PROMOUSAGE_X10 on ARF_PROMOTION_USAGE (LOCAL_CURRENCY_ID);
create index ARF_PROMOUSAGE_X11 on ARF_PROMOTION_USAGE (DEMOGRAPHIC_ID);
create index ARF_PROMOUSAGE_X12 on ARF_PROMOTION_USAGE (AGENT_ID);
create index ARF_PROMOUSAGE_X13 on ARF_PROMOTION_USAGE (ORIGIN_SALES_CHANNEL_ID);
create index ARF_PROMOUSAGE_X14 on ARF_PROMOTION_USAGE (SUBMIT_SALES_CHANNEL_ID);
create index ARF_PROMOUSAGE_X19 on ARF_PROMOTION_USAGE (SITE_ID);
create index ARF_PROMOUSAGE_X15 on ARF_PROMOTION_USAGE (SITE_VISIT_ID);
create index ARF_PROMOUSAGE_X16 on ARF_PROMOTION_USAGE (ORDER_ID);
create index ARF_PROMOUSAGE_X17 on ARF_PROMOTION_USAGE (NORDER_ID);
create index ARF_PROMOUSAGE_X18 on ARF_PROMOTION_USAGE (SESSION_ID);



