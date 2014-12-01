


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/reporting_views2.xml#3 $$Change: 655658 $
--        drptw_prod_sales calculates several statistics over each week on a per-product basis    
-- drptw_prod_sales calculates several statistics over each week on a per-product basis    
create view drptw_prod_sales
(week,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,total_units_sold_p,weeks_on_hand,total_rev,cost_of_goods_sold,maintained_markup,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
select 
	DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) as week, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / wot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price * i.quantity)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / wot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	wb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / wb.browses) as shop_to_purc_conv, 
	wc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / wc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptw_browses wb, 
	drptw_carts wc, 
	drpt_sku_stock si, 
	drptw_orders wot, 
	dcs_sku s
where 
	o.order_id = i.order_ref 
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = wc.product_id 
	and i.product_id = wb.product_id 
	and DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) = wb.week 
	and DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) = wc.week 
	and si.product_id = i.product_id 
	and wot.week = DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) 
	and i.catalog_ref_id = s.sku_id
group by 
	DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	wb.browses, 
	wc.adds_to_cart, 
	wot.total_units_sold,
	wot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock         ;


--        drptm_prod_sales calculates several statistics over each month on a per-product basis    
create view drptm_prod_sales
(month,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,total_units_sold_p,months_on_hand,total_rev,cost_of_goods_sold,maintained_markup,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_FORMAT(o.submitted_date, '%Y-%m-01') as month, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / mot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as months_on_hand,
	sum(ai.amount) as total_rev, 
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price * i.quantity)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / mot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	mb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / mb.browses) as shop_to_purc_conv, 
	mc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / mc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptm_browses mb, 
	drptm_carts mc, 
	drpt_sku_stock si, 
	drptm_orders mot, 
	dcs_sku s
where 
	o.order_id = i.order_ref 
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = mc.product_id 
	and i.product_id = mb.product_id 
	and DATE_FORMAT(o.submitted_date, '%Y-%m-01') = mb.month 
	and DATE_FORMAT(o.submitted_date, '%Y-%m-01') = mc.month 
	and si.product_id = i.product_id 
	and mot.month = DATE_FORMAT(o.submitted_date, '%Y-%m-01') 
	and i.catalog_ref_id = s.sku_id
group by 
	DATE_FORMAT(o.submitted_date, '%Y-%m-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup,
	pri.units_on_hand, 
	mb.browses, 
	mc.adds_to_cart, 
	mot.total_units_sold,
	mot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock
         ;


--        drptq_prod_sales calculates several statistics over each quarter on a per-product basis    
create view drptq_prod_sales
(quarter,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,total_units_sold_p,quarters_on_hand,total_rev,cost_of_goods_sold,maintained_markup,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH)
 as quarter, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / qot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as quarters_on_hand,
	sum(ai.amount) as total_rev, 
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price * i.quantity)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / qot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	qb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / qb.browses) as shop_to_purc_conv, 
	qc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / qc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptq_browses qb, 
	drptq_carts qc, 
	drpt_sku_stock si, 
	drptq_orders qot, 
	dcs_sku s
where 
	o.order_id = i.order_ref 
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = qc.product_id 
	and i.product_id = qb.product_id 
	and DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH)
 = qb.quarter 
	and DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH)
 = qc.quarter 
	and si.product_id = i.product_id 
	and qot.quarter = DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH)
 
	and i.catalog_ref_id = s.sku_id
group by 
	DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH)
, 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup,
	pri.units_on_hand, 
	qb.browses, 	
	qc.adds_to_cart, 
	qot.total_units_sold,
	qot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock
         ;


--        drpta_prod_sales calculates several statistics over each year on a per-product basis    
create view drpta_prod_sales
(year,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,total_units_sold_p,years_on_hand,total_rev,cost_of_goods_sold,maintained_markup,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_FORMAT(o.submitted_date, '%Y-01-01') as year, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / aot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as years_on_hand,
	sum(ai.amount) as total_rev, 
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price * i.quantity)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / aot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	ab.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / ab.browses) as shop_to_purc_conv, 
	ac.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / ac.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drpta_browses ab, 
	drpta_carts ac, 
	drpt_sku_stock si, 
	drpta_orders aot, 
	dcs_sku s
where 
	o.order_id = i.order_ref 
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = ac.product_id 
	and i.product_id = ab.product_id 
	and DATE_FORMAT(o.submitted_date, '%Y-01-01') = ab.year 
	and DATE_FORMAT(o.submitted_date, '%Y-01-01') = ac.year 
	and si.product_id = i.product_id 
	and aot.year = DATE_FORMAT(o.submitted_date, '%Y-01-01') 
	and i.catalog_ref_id = s.sku_id
group by 
	DATE_FORMAT(o.submitted_date, '%Y-01-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup,
	pri.units_on_hand, 
	ab.browses, 
	ac.adds_to_cart, 
	aot.total_units_sold,
	aot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock
         ;


--        drptw_promo_sales calculates totals about products that were discounted   
--        by the sample 'promo60003' promotion over each week   
create view drptw_promo_sales
(week,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,promo_units_sold_p,total_units_sold_p,weeks_on_hand,total_rev,cost_of_goods_sold,maintained_markup,promo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) as week, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / wpt.total_units_sold) as promo_units_sold_p, 
	(sum(i.quantity) / wot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / wpt.total_dollar_sales) as promo_rev_p,
	(sum(ai.amount) / wot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	wb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / wb.browses) as shop_to_purc_conv, 
	wc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / wc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptw_browses wb, 
	drptw_carts wc, 
	drpt_sku_stock si, 
	drptw_orders wot, 
	dcs_sku s, 
	drptw_promotion wpt, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = wc.product_id 
	and i.product_id = wb.product_id 
	and DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) = wb.week 
	and DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) = wc.week 
	and si.product_id = i.product_id 
	and wot.week = DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) 
	and wpt.week = DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) 
	and i.catalog_ref_id = s.sku_id
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by 
	DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	wb.browses, 
	wc.adds_to_cart, 
	wot.total_units_sold, 
	wot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	wpt.total_units_sold, 
	wpt.total_dollar_sales
         ;


--        drptm_promo_sales calculates totals about products that were discounted   
--        by the sample 'promo60003' promotion over each month   
create view drptm_promo_sales
(month,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,promo_units_sold_p,total_units_sold_p,months_on_hand,total_rev,cost_of_goods_sold,maintained_markup,promo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_FORMAT(o.submitted_date, '%Y-%m-01') as month, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / mpt.total_units_sold) as promo_units_sold_p, 
	(sum(i.quantity) / mot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as months_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / mpt.total_dollar_sales) as promo_rev_p,
	(sum(ai.amount) / mot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	mb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / mb.browses) as shop_to_purc_conv, 
	mc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / mc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptm_browses mb, 
	drptm_carts mc, 
	drpt_sku_stock si, 
	drptm_orders mot, 
	dcs_sku s, 
	drptm_promotion mpt, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = mc.product_id 
	and i.product_id = mb.product_id 
	and DATE_FORMAT(o.submitted_date, '%Y-%m-01') = mb.month 
	and DATE_FORMAT(o.submitted_date, '%Y-%m-01') = mc.month 
	and si.product_id = i.product_id 
	and mot.month = DATE_FORMAT(o.submitted_date, '%Y-%m-01') 
	and mpt.month = DATE_FORMAT(o.submitted_date, '%Y-%m-01') 
	and i.catalog_ref_id = s.sku_id
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by 
	DATE_FORMAT(o.submitted_date, '%Y-%m-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	mb.browses, 
	mc.adds_to_cart, 
	mot.total_units_sold, 
	mot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	mpt.total_units_sold, 
	mpt.total_dollar_sales
         ;


--        drptq_promo_sales calculates totals about products that were discounted   
--        by the sample 'promo60003' promotion over each quarter   
create view drptq_promo_sales
(quarter,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,promo_units_sold_p,total_units_sold_p,quarters_on_hand,total_rev,cost_of_goods_sold,maintained_markup,promo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH), 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / qpt.total_units_sold) as promo_units_sold_p, 
	(sum(i.quantity) / qot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as quarters_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / qpt.total_dollar_sales) as promo_rev_p,
	(sum(ai.amount) / qot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	qb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / qb.browses) as shop_to_purc_conv, 
	qc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / qc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptq_browses qb, 
	drptq_carts qc, 
	drpt_sku_stock si, 
	drptq_orders qot,
	dcs_sku s, 
	drptq_promotion qpt, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = qc.product_id 
	and i.product_id = qb.product_id 
	and DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) = qb.quarter 
	and DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) = qc.quarter 
	and si.product_id = i.product_id 
	and qot.quarter = DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) 
	and qpt.quarter = DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) 
	and i.catalog_ref_id = s.sku_id
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by 
	DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	qb.browses, 
	qc.adds_to_cart, 
	qot.total_units_sold, 
	qot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	qpt.total_units_sold, 
	qpt.total_dollar_sales
         ;


--        drpta_promo_sales calculates totals about products that were discounted   
--        by the sample 'promo60003' promotion over each year   
create view drpta_promo_sales
(year,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,promo_units_sold_p,total_units_sold_p,years_on_hand,total_rev,cost_of_goods_sold,maintained_markup,promo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_FORMAT(o.submitted_date, '%Y-01-01') as year, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / apt.total_units_sold) as promo_units_sold_p, 
	(sum(i.quantity) / aot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as years_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / apt.total_dollar_sales) as promo_rev_p,
	(sum(ai.amount) / aot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	ab.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / ab.browses) as shop_to_purc_conv, 
	ac.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / ac.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drpta_browses ab, 
	drpta_carts ac, 
	drpt_sku_stock si, 
	drpta_orders aot, 
	dcs_sku s, 
	drpta_promotion apt, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = ac.product_id 
	and i.product_id = ab.product_id 
	and DATE_FORMAT(o.submitted_date, '%Y-01-01') = ab.year 
	and DATE_FORMAT(o.submitted_date, '%Y-01-01') = ac.year 
	and si.product_id = i.product_id 
	and aot.year = DATE_FORMAT(o.submitted_date, '%Y-01-01') 
	and apt.year = DATE_FORMAT(o.submitted_date, '%Y-01-01') 
	and i.catalog_ref_id = s.sku_id
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by 
	DATE_FORMAT(o.submitted_date, '%Y-01-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	ab.browses, 
	ac.adds_to_cart, 
	aot.total_units_sold, 
	aot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	apt.total_units_sold, 
	apt.total_dollar_sales
         ;


--        drptw_m_18_25_sales calculates totals about products that were purchased   
--        by males aged 18-25 each week   
create view drptw_m18_25_sales
(week,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,demo_units_sold_p,total_units_sold_p,weeks_on_hand,total_rev,cost_of_goods_sold,maintained_markup,demo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) as week, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / wpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / wot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / wpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / wot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	wb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / wb.browses) as shop_to_purc_conv, 
	wc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / wc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptw_browses wb, 
	drptw_carts wc, 
	drpt_sku_stock si, 
	drptw_orders wot, 
	dcs_sku s, 
	drptw_male_18_25 wpt, 
	dps_user u
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = wc.product_id 
	and i.product_id = wb.product_id 
	and DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) = wb.week 
	and DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) = wc.week 
	and si.product_id = i.product_id 
	and wot.week = DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) 
	and wpt.week = DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = '2' 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 >= 18) 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 < 25)
group by 
	DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	wb.browses, 
	wc.adds_to_cart, 
	wot.total_units_sold, 
	wot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	wpt.total_units_sold, 
	wpt.total_dollar_sales
         ;


--        drptm_m_18_25_sales calculates totals about products that were purchased   
--        by males aged 18-25 each month   
create view drptm_m18_25_sales
(month,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,demo_units_sold_p,total_units_sold_p,months_on_hand,total_rev,cost_of_goods_sold,maintained_markup,demo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_FORMAT(o.submitted_date, '%Y-%m-01') as month, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / mpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / mot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as months_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / mpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / mot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	mb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / mb.browses) as shop_to_purc_conv, 
	mc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / mc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptm_browses mb, 
	drptm_carts mc, 
	drpt_sku_stock si, 
	drptm_orders mot, 
	dcs_sku s, 
	drptm_male_18_25 mpt, 
	dps_user u
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = mc.product_id 
	and i.product_id = mb.product_id 
	and DATE_FORMAT(o.submitted_date, '%Y-%m-01') = mb.month 
	and DATE_FORMAT(o.submitted_date, '%Y-%m-01') = mc.month 
	and si.product_id = i.product_id 
	and mot.month = DATE_FORMAT(o.submitted_date, '%Y-%m-01') 
	and mpt.month = DATE_FORMAT(o.submitted_date, '%Y-%m-01') 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = '2' 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 >= 18) 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 < 25)
group by 
	DATE_FORMAT(o.submitted_date, '%Y-%m-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	mb.browses, 
	mc.adds_to_cart, 
	mot.total_units_sold, 
	mot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	mpt.total_units_sold, 
	mpt.total_dollar_sales
         ;


--        drptq_m_18_25_sales calculates totals about products that were purchased   
--        by males aged 18-25 each quarter   
create view drptq_m18_25_sales
(quarter,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,demo_units_sold_p,total_units_sold_p,quarters_on_hand,total_rev,cost_of_goods_sold,maintained_markup,demo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) as quarter, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / qpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / qot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as quarters_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / qpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / qot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	qb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / qb.browses) as shop_to_purc_conv, 
	qc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / qc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptq_browses qb, 
	drptq_carts qc, 
	drpt_sku_stock si, 
	drptq_orders qot, 
	dcs_sku s, 
	drptq_male_18_25 qpt, 
	dps_user u
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = qc.product_id 
	and i.product_id = qb.product_id 
	and DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) = qb.quarter 
	and DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) = qc.quarter 
	and si.product_id = i.product_id 
	and qot.quarter = DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) 
	and qpt.quarter = DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = '2' 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 >= 18) 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 < 25)
group by 
	DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	qb.browses, 
	qc.adds_to_cart, 
	qot.total_units_sold, 
	qot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	qpt.total_units_sold, 
	qpt.total_dollar_sales
         ;


--        drpta_m_18_25_sales calculates totals about products that were purchased   
--        by males aged 18-25 each year   
create view drpta_m18_25_sales
(year,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,demo_units_sold_p,total_units_sold_p,years_on_hand,total_rev,cost_of_goods_sold,maintained_markup,demo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_FORMAT(o.submitted_date, '%Y-01-01') as year, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / apt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / aot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as years_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / apt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / aot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	ab.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / ab.browses) as shop_to_purc_conv, 
	ac.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / ac.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drpta_browses ab, 
	drpta_carts ac, 
	drpt_sku_stock si, 
	drpta_orders aot, 
	dcs_sku s, 
	drpta_male_18_25 apt, 
	dps_user u
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = ac.product_id 
	and i.product_id = ab.product_id 
	and DATE_FORMAT(o.submitted_date, '%Y-01-01') = ab.year 
	and DATE_FORMAT(o.submitted_date, '%Y-01-01') = ac.year 
	and si.product_id = i.product_id 
	and aot.year = DATE_FORMAT(o.submitted_date, '%Y-01-01') 
	and apt.year = DATE_FORMAT(o.submitted_date, '%Y-01-01') 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id
	and u.gender = '2' 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 >= 18) 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 < 25)
group by 
	DATE_FORMAT(o.submitted_date, '%Y-01-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	ab.browses, 
	ac.adds_to_cart, 
	aot.total_units_sold, 
	aot.merch_rev,
	pri.number_of_skus, 
	si.skus_in_stock, 
	apt.total_units_sold, 
	apt.total_dollar_sales
         ;


--        drptw_m_25_39_sales calculates totals about products that were purchased   
--        by males aged 25-39 each week   
create view drptw_m25_39_sales
(week,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,demo_units_sold_p,total_units_sold_p,weeks_on_hand,total_rev,cost_of_goods_sold,maintained_markup,demo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) as week, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / wpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / wot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / wpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / wot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	wb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / wb.browses) as shop_to_purc_conv, 
	wc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / wc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptw_browses wb, 
	drptw_carts wc, 
	drpt_sku_stock si, 
	drptw_orders wot, 
	dcs_sku s, 
	drptw_male_25_39 wpt, 
	dps_user u
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = wc.product_id 
	and i.product_id = wb.product_id 
	and DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) = wb.week 
	and DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) = wc.week 
	and si.product_id = i.product_id 
	and wot.week = DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) 
	and wpt.week = DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY) 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = '2' 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 >= 25) 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 < 39)
group by 
	DATE_SUB(DATE(o.submitted_date), INTERVAL DAYOFWEEK(o.submitted_date)-1 DAY), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	wb.browses, 
	wc.adds_to_cart, 
	wot.total_units_sold, 
	wot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	wpt.total_units_sold, 
	wpt.total_dollar_sales
         ;


--        drptm_m_25_39_sales calculates totals about products that were purchased   
--        by males aged 25-39 each month   
create view drptm_m25_39_sales
(month,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,demo_units_sold_p,total_units_sold_p,months_on_hand,total_rev,cost_of_goods_sold,maintained_markup,demo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_FORMAT(o.submitted_date, '%Y-%m-01') as month, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / mpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / mot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as months_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / mpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / mot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	mb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / mb.browses) as shop_to_purc_conv, 
	mc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / mc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptm_browses mb, 
	drptm_carts mc, 
	drpt_sku_stock si, 
	drptm_orders mot, 
	dcs_sku s, 
	drptm_male_25_39 mpt, 
	dps_user u
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = mc.product_id 
	and i.product_id = mb.product_id 
	and DATE_FORMAT(o.submitted_date, '%Y-%m-01') = mb.month 
	and DATE_FORMAT(o.submitted_date, '%Y-%m-01') = mc.month 
	and si.product_id = i.product_id 
	and mot.month = DATE_FORMAT(o.submitted_date, '%Y-%m-01') 
	and mpt.month = DATE_FORMAT(o.submitted_date, '%Y-%m-01') 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = '2' 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 >= 25) 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 < 39)
group by 
	DATE_FORMAT(o.submitted_date, '%Y-%m-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	mb.browses, 
	mc.adds_to_cart, 
	mot.total_units_sold, 
	mot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	mpt.total_units_sold, 
	mpt.total_dollar_sales
         ;


--        drptq_m_25_39_sales calculates totals about products that were purchased   
--        by males aged 25-39 each quarter   
create view drptq_m25_39_sales
(quarter,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,demo_units_sold_p,total_units_sold_p,quarters_on_hand,total_rev,cost_of_goods_sold,maintained_markup,demo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) as quarter, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / qpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / qot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as quarters_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / qpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / qot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	qb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / qb.browses) as shop_to_purc_conv, 
	qc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / qc.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptq_browses qb, 
	drptq_carts qc, 
	drpt_sku_stock si, 
	drptq_orders qot, 
	dcs_sku s, 
	drptq_male_25_39 qpt, 
	dps_user u
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = qc.product_id 
	and i.product_id = qb.product_id 
	and DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) = qb.quarter 
	and DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) = qc.quarter 
	and si.product_id = i.product_id 
	and qot.quarter = DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) 
	and qpt.quarter = DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH) 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = '2' 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 >= 25) 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 < 39)
group by 
	DATE_ADD(DATE_FORMAT(o.submitted_date, '%Y-01-01'), INTERVAL (QUARTER(o.submitted_date)-1)*3 MONTH), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	qb.browses, 
	qc.adds_to_cart, 
	qot.total_units_sold, 
	qot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	qpt.total_units_sold, 
	qpt.total_dollar_sales
         ;


--        drpta_m_25_39_sales calculates totals about products that were purchased   
--        by males aged 25-39 each year   
create view drpta_m25_39_sales
(year,product_id,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_on_hand,units_sold,demo_units_sold_p,total_units_sold_p,years_on_hand,total_rev,cost_of_goods_sold,maintained_markup,demo_rev_p,total_rev_p,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	DATE_FORMAT(o.submitted_date, '%Y-01-01') as year, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / apt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / aot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as years_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / apt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / aot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	ab.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / ab.browses) as shop_to_purc_conv, 
	ac.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / ac.adds_to_cart) as cart_to_purc_conv
from 
	dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drpta_browses ab, 
	drpta_carts ac, 
	drpt_sku_stock si, 
	drpta_orders aot, 
	dcs_sku s, 
	drpta_male_25_39 apt, 
	dps_user u
where 
	o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = ac.product_id 
	and i.product_id = ab.product_id 
	and DATE_FORMAT(o.submitted_date, '%Y-01-01') = ab.year 
	and DATE_FORMAT(o.submitted_date, '%Y-01-01') = ac.year 
	and si.product_id = i.product_id 
	and aot.year = DATE_FORMAT(o.submitted_date, '%Y-01-01') 
	and apt.year = DATE_FORMAT(o.submitted_date, '%Y-01-01') 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = '2' 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 >= 25) 
	and (PERIOD_DIFF(DATE_FORMAT(o.submitted_date, '%Y%m'), DATE_FORMAT(u.date_of_birth, '%Y%m'))/12 < 39)
group by 
	DATE_FORMAT(o.submitted_date, '%Y-01-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	ab.browses, 
	ac.adds_to_cart, 
	aot.total_units_sold, 
	aot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	apt.total_units_sold, 	
	apt.total_dollar_sales
         ;


commit;
