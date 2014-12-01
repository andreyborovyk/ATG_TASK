


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/reporting_views3.xml#3 $$Change: 655658 $
--        drptw_cat_sales calculates various statistics over each week on a per-category basis   
create view drptw_cat_sales
(week,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_sold,total_rev,cost_of_goods_sold,maintained_markup,total_units_sold_p,total_rev_p,units_on_hand,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	wps.week as week, 
	wps.category_name as category_name, 
	cri.avg_cost as avg_cost,
	cri.avg_list_price as avg_list_price, 
	cri.avg_sale_price as avg_sale_price,
	cri.avg_initial_markup as avg_initial_markup,
	sum(wps.units_sold) as units_sold, 
	sum(wps.total_rev) as total_rev,
	sum(wps.cost_of_goods_sold) as cost_of_goods_sold,
	((sum(wps.total_rev) - sum(wps.cost_of_goods_sold)) / sum(wps.total_rev)) as maintained_markup,
	(sum(wps.units_sold) / wot.total_units_sold) as total_units_sold_p,
	(sum(wps.total_rev) / wot.merch_rev) as total_rev_p, 
	sum(wps.units_on_hand) as units_on_hand,
	sum(wps.number_of_skus) as number_of_skus, 
	sum(wps.skus_in_stock) as skus_in_stock,
	(sum(wps.skus_in_stock) / sum(wps.number_of_skus)) as skus_in_stock_p,
	sum(wps.browses) as browses, 
	sum(wps.browse_conversions) as browse_conversions,
	(sum(wps.browse_conversions) / sum(wps.browses)) as shop_to_purc_conv,
	sum(wps.adds_to_cart) as adds_to_cart, 
	sum(wps.cart_conversions) as cart_conversions,
	(sum(wps.cart_conversions) / sum(wps.adds_to_cart)) as cart_to_purc_conv
from 
	drptw_prod_sales wps, 
	drpt_category cri, 
	drptw_orders wot
where 
	wps.category_name = cri.category_name 
	and wps.week = wot.week
group by 
	wps.week, 
	wps.category_name, 
	cri.avg_cost, 
	cri.avg_list_price, 
	cri.avg_sale_price,
	cri.avg_initial_markup,
	wot.total_units_sold, 
	wot.merch_rev
         ;


--        drptm_cat_sales calculates various statistics over each month on a per-category basis   
create view drptm_cat_sales
(month,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_sold,total_rev,cost_of_goods_sold,maintained_markup,total_units_sold_p,total_rev_p,units_on_hand,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	mps.month as month, 
	mps.category_name as category_name, 
	cri.avg_cost as avg_cost,
	cri.avg_list_price as avg_list_price, 
	cri.avg_sale_price as avg_sale_price, 
	cri.avg_initial_markup as avg_initial_markup,
	sum(mps.units_sold) as units_sold, 
	sum(mps.total_rev) as total_rev,
	sum(mps.cost_of_goods_sold) as cost_of_goods_sold,
	((sum(mps.total_rev) - sum(mps.cost_of_goods_sold)) / sum(mps.total_rev)) as maintained_markup,
	(sum(mps.units_sold) / mot.total_units_sold) as total_units_sold_p,
	(sum(mps.total_rev) / mot.merch_rev) as total_rev_p, 
	sum(mps.units_on_hand) as units_on_hand,
	sum(mps.number_of_skus) as number_of_skus, 
	sum(mps.skus_in_stock) as skus_in_stock,
	(sum(mps.skus_in_stock) / sum(mps.number_of_skus)) as skus_in_stock_p,
	sum(mps.browses) as browses, 
	sum(mps.browse_conversions) as browse_conversions,
	(sum(mps.browse_conversions) / sum(mps.browses)) as shop_to_purc_conv,
	sum(mps.adds_to_cart) as adds_to_cart, 
	sum(mps.cart_conversions) as cart_conversions,
	(sum(mps.cart_conversions) / sum(mps.adds_to_cart)) as cart_to_purc_conv
from 
	drptm_prod_sales mps, 
	drpt_category cri, 
	drptm_orders mot
where 
	mps.category_name = cri.category_name 
	and mps.month = mot.month
group by 
	mps.month, 
	mps.category_name, 
	cri.avg_cost, 
	cri.avg_list_price, 
	cri.avg_sale_price,
	cri.avg_initial_markup,
	mot.total_units_sold, 
	mot.merch_rev
         ;


--        drptq_cat_sales calculates various statistics over each quarter on a per-category basis   
create view drptq_cat_sales
(quarter,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_sold,total_rev,cost_of_goods_sold,maintained_markup,total_units_sold_p,total_rev_p,units_on_hand,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	qps.quarter as quarter, 
	qps.category_name as category_name, 
	cri.avg_cost as avg_cost,
	cri.avg_list_price as avg_list_price, 
	cri.avg_sale_price as avg_sale_price, 
	cri.avg_initial_markup as avg_initial_markup,
	sum(qps.units_sold) as units_sold, 
	sum(qps.total_rev) as total_rev,
	sum(qps.cost_of_goods_sold) as cost_of_goods_sold,
	((sum(qps.total_rev) - sum(qps.cost_of_goods_sold)) / sum(qps.total_rev)) as maintained_markup,
	(sum(qps.units_sold) / qot.total_units_sold) as total_units_sold_p,
	(sum(qps.total_rev) / qot.merch_rev) as total_rev_p, 
	sum(qps.units_on_hand) as units_on_hand,
	sum(qps.number_of_skus) as number_of_skus, 
	sum(qps.skus_in_stock) as skus_in_stock,
	(sum(qps.skus_in_stock) / sum(qps.number_of_skus)) as skus_in_stock_p,
	sum(qps.browses) as browses, 
	sum(qps.browse_conversions) as browse_conversions,
	(sum(qps.browse_conversions) / sum(qps.browses)) as shop_to_purc_conv,
	sum(qps.adds_to_cart) as adds_to_cart, 
	sum(qps.cart_conversions) as cart_conversions,
	(sum(qps.cart_conversions) / sum(qps.adds_to_cart)) as cart_to_purc_conv
from 
	drptq_prod_sales qps, 
	drpt_category cri, 
	drptq_orders qot
where 
	qps.category_name = cri.category_name 
	and qps.quarter = qot.quarter
group by 
	qps.quarter, 
	qps.category_name, 
	cri.avg_cost, 
	cri.avg_list_price, 
	cri.avg_sale_price,
	cri.avg_initial_markup,
	qot.total_units_sold, 
	qot.merch_rev
         ;


--        drpta_cat_sales calculates various statistics over each year on a per-category basis   
create view drpta_cat_sales
(year,category_name,avg_cost,avg_list_price,avg_sale_price,avg_initial_markup,units_sold,total_rev,cost_of_goods_sold,maintained_markup,total_units_sold_p,total_rev_p,units_on_hand,number_of_skus,skus_in_stock,skus_in_stock_p,browses,browse_conversions,shop_to_purc_conv,adds_to_cart,cart_conversions,cart_to_purc_conv)
as
             select 
	aps.year as year, 
	aps.category_name as category_name, 
	cri.avg_cost as avg_cost,
	cri.avg_list_price as avg_list_price, 
	cri.avg_sale_price as avg_sale_price, 
	cri.avg_initial_markup as avg_initial_markup,
	sum(aps.units_sold) as units_sold, 
	sum(aps.total_rev) as total_rev,
	sum(aps.cost_of_goods_sold) as cost_of_goods_sold,
	((sum(aps.total_rev) - sum(aps.cost_of_goods_sold)) / sum(aps.total_rev)) as maintained_markup,
	(sum(aps.units_sold) / aot.total_units_sold) as total_units_sold_p,
	(sum(aps.total_rev) / aot.merch_rev) as total_rev_p, 
	sum(aps.units_on_hand) as units_on_hand,
	sum(aps.number_of_skus) as number_of_skus, 
	sum(aps.skus_in_stock) as skus_in_stock,
	(sum(aps.skus_in_stock) / sum(aps.number_of_skus)) as skus_in_stock_p,
	sum(aps.browses) as browses, 
	sum(aps.browse_conversions) as browse_conversions,
	(sum(aps.browse_conversions) / sum(aps.browses)) as shop_to_purc_conv,
	sum(aps.adds_to_cart) as adds_to_cart, 
	sum(aps.cart_conversions) as cart_conversions,
	(sum(aps.cart_conversions) / sum(aps.adds_to_cart)) as cart_to_purc_conv
from 
	drpta_prod_sales aps, 
	drpt_category cri, 
	drpta_orders aot
where 
	aps.category_name = cri.category_name 
	and aps.year = aot.year
group by 
	aps.year, 
	aps.category_name, 
	cri.avg_cost, 
	cri.avg_list_price, 
	cri.avg_sale_price,
	cri.avg_initial_markup,
	aot.total_units_sold, 
	aot.merch_rev
         ;


commit;
