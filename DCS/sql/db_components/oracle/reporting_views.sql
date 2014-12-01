


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/reporting_views.xml#3 $$Change: 655658 $
--        In the comments, the time periods indicated are calendar time, meaning that   
--        the current period should be calculated from the start of the calendar time period,   
--        rather than in real time.  For example, if it is Thursday, July 12,   
--        the most current row in a view calculating totals for a week    
--        would run from Sunday, July 8 - July 12, rather than July 5 - July 12.   
--        drpt_order gathers basic information about each order   
create or replace view drpt_order
as
select o.order_id as order_id, 
	o.submitted_date as submitted_date, 
	ai.amount as amount, 
	count(i.quantity) as distinct_items, 
	sum(i.quantity) as total_items,
	ba.state as state, 
	ba.country as country, 
	o.price_info as price_info
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcspp_pay_group pg, 
	dcspp_bill_addr ba
where o.order_id = i.order_ref 
	and o.price_info = ai.amount_info_id 
	and o.order_id = pg.order_ref
	and pg.payment_group_id = ba.payment_group_id
group by o.order_id, 
	o.submitted_date, 
	ai.amount, 
	ba.state, 
	ba.country,
	 o.price_info
         ;


--        drpt_cost_of_goods calculates the total wholesale cost   
--        of the items purchased in each order   
create or replace view drpt_cost_of_goods
as
select i.order_ref as order_id, 
	sum(i.quantity * s.wholesale_price) as cost_of_goods
from dcspp_item i, 
	dcs_sku s
where i.catalog_ref_id = s.sku_id
group by i.order_ref
         ;


--        drpt_discount calculates the total amount discounted   
--        from each order via promotions   
create or replace view drpt_discount
as
select o.order_id as order_id, 
	o.submitted_date as submitted_date, 
	(0 - sum(pa.adjustment)) as discount
from dcspp_order o, 
	dcspp_item i, 
	dcspp_amtinfo_adj aa, 
	dcspp_price_adjust pa
where o.order_id = i.order_ref 
	and i.price_info = aa.amount_info_id 
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model is not null
group by o.order_id, 
	o.submitted_date
         ;


--        drptw_discounts calculates the number of orders that   
--        were discounted by promotions over each week   
--        The "UNION" operation allows weeks in which there   
--        were no discounts to have a row in the view   
create or replace view drptw_discounts
as
select trunc(submitted_date, 'DAY') as week, 
	count(order_id) as num_of_discounts
from drpt_discount
group by trunc(submitted_date, 'DAY')
UNION
select trunc(submitted_date, 'DAY') as week, 
	0 as num_of_discounts
from dcspp_order 
where trunc(submitted_date, 'DAY') not in (select trunc(submitted_date, 'DAY') from drpt_discount)
         ;


--        drptm_discounts calculates the number of orders that   
--        were discounted by promotions over each month   
--        The "UNION" operation allows months in which there   
--        were no discounts to have a row in the view   
create or replace view drptm_discounts
as
select trunc(submitted_date, 'MON') as month, 
	count(order_id) as num_of_discounts
from drpt_discount
group by trunc(submitted_date, 'MON')
UNION
select trunc(submitted_date, 'MON') as month, 
	0 as num_of_discounts
from dcspp_order 
where trunc(submitted_date, 'MON') not in (select trunc(submitted_date, 'MON') from drpt_discount)
         ;


--        drptq_discounts calculates the number of orders that   
--        were discounted by promotions over each quarter   
--        The "UNION" operation allows quarters in which there   
--        were no discounts to have a row in the view   
create or replace view drptq_discounts
as
select trunc(submitted_date, 'Q') as quarter, 
	count(order_id) as num_of_discounts
from drpt_discount
group by trunc(submitted_date, 'Q')
UNION
select trunc(submitted_date, 'Q') as quarter, 
	0 as num_of_discounts
from dcspp_order 
where trunc(submitted_date, 'Q') not in (select trunc(submitted_date, 'Q') from drpt_discount)
         ;


--        drpta_discounts calculates the number of orders that   
--        were discounted by promotions over each year   
--        The "UNION" operation allows years in which there   
--        were no discounts to have a row in the view   
create or replace view drpta_discounts
as
select trunc(submitted_date, 'YEAR') as year, 
	count(order_id) as num_of_discounts
from drpt_discount
group by trunc(submitted_date, 'YEAR')
UNION
select trunc(submitted_date, 'YEAR') as year, 
	0 as num_of_discounts
from dcspp_order 
where trunc(submitted_date, 'YEAR') not in (select trunc(submitted_date, 'YEAR') from drpt_discount)
         ;


--        drpt_ordered_items comprises a list of each item purchased   
create or replace view drpt_ordered_items
as
select o.order_id as order_id, 
	o.submitted_date as submitted_date, 
	i.catalog_ref_id as catalog_ref_id,
	i.product_id as product_id, 
	i.quantity as quantity, 
	ai.amount as price
from dcspp_order o, 
	dcspp_item i, 
	dcspp_amount_info ai
where o.order_id = i.order_ref 
	and i.price_info = ai.amount_info_id
         ;


--        drpt_sku_stock calculates the number of unique skus in stock per product   
--        in the catalog.  The "UNION" operation exists so that a row will   
--        appear for products that have no skus in stock   
create or replace view drpt_sku_stock
as
select pc.product_id as product_id, 
	count(i.catalog_ref_id) as skus_in_stock
from dcs_prd_chldsku pc, 
	dcs_inventory i
where pc.sku_id = i.catalog_ref_id 
	and i.stock_level > 0
group by pc.product_id
UNION
select product_id, 
	0 as skus_in_stock 
from dcs_prd_chldsku 
where product_id not in 
(select product_id 
from dcs_prd_chldsku pc, 
	dcs_inventory i 
where pc.sku_id = i.catalog_ref_id 
	and i.stock_level > 0)
         ;


--        drptw_browses calculatess the number of times each product's page   
--        has been viewed online each week   
create or replace view drptw_browses
as
select repositoryid as product_id, 
	trunc(timestamp, 'DAY') as week, 
	count(timestamp) as browses
from dss_dps_view_item
group by repositoryid, 
	trunc(timestamp, 'DAY')
         ;


--        drptm_browses calculates the number of times each product's page   
--        has been viewed online each month   
create or replace view drptm_browses
as
select repositoryid as product_id, 
	trunc(timestamp, 'MON') as month, 
	count(timestamp) as browses
from dss_dps_view_item
group by repositoryid, 
	trunc(timestamp, 'MON')
         ;


--        drptq_browses calculates the number of times each product's page   
--        has been viewed online each quarter   
create or replace view drptq_browses
as
select repositoryid as product_id, 
	trunc(timestamp, 'Q') as quarter, 
	count(timestamp) as browses
from dss_dps_view_item
group by repositoryid, 
	trunc(timestamp, 'Q')
         ;


--        drpta_browses calculates the number of times each product's page   
--        has been viewed online each year   
create or replace view drpta_browses
as
select repositoryid as product_id, 
	trunc(timestamp, 'YEAR') as year, 
	count(timestamp) as browses
from dss_dps_view_item
group by repositoryid,
	trunc(timestamp, 'YEAR')
         ;


--        drptw_carts calculates the number of times each product has   
--        been added to a user's shopping cart each week   
--        Note:  A single user adding a quantity greater than one   
--        to their cart at one time is considered a single "add to cart"   
create or replace view drptw_carts
as
select pc.product_id as product_id, 
	trunc(ce.timestamp, 'DAY') as week, 
	count(ce.id) as adds_to_cart
from dcs_cart_event ce, 
	dcs_prd_chldsku pc
where ce.itemid = pc.sku_id
group by pc.product_id, 
	trunc(ce.timestamp, 'DAY')
         ;


--        drptm_carts calculates the number of times each product has   
--        been added to a user's shopping cart each month   
--        Note:  A single user adding a quantity greater than one   
--        to their cart at one time is considered a single "add to cart"   
create or replace view drptm_carts
as
select pc.product_id as product_id, 
	trunc(ce.timestamp, 'MON') as month, 
	count(ce.id) as adds_to_cart
from dcs_cart_event ce, 
	dcs_prd_chldsku pc
where ce.itemid = pc.sku_id
group by pc.product_id, 
	trunc(ce.timestamp, 'MON')
         ;


--        drptq_carts calculates the number of times each product has   
--        been added to a user's shopping cart each quarter   
--        Note:  A single user adding a quantity greater than one   
--        to their cart at one time is considered a single "add to cart"   
create or replace view drptq_carts
as
select pc.product_id as product_id, 
	trunc(ce.timestamp, 'Q') as quarter, 
	count(ce.id) as adds_to_cart
from dcs_cart_event ce, 
	dcs_prd_chldsku pc
where ce.itemid = pc.sku_id
group by pc.product_id, 
	trunc(ce.timestamp, 'Q')
         ;


--        drpta_carts calculates the number of times each product has   
--        been added to a user's shopping cart each year   
--        Note:  A single user adding a quantity greater than one   
--        to their cart at one time is considered a single "add to cart"   
create or replace view drpta_carts
as
select pc.product_id as product_id, 
	trunc(ce.timestamp, 'YEAR') as year, 
	count(ce.id) as adds_to_cart
from dcs_cart_event ce, 
	dcs_prd_chldsku pc
where ce.itemid = pc.sku_id
group by pc.product_id, 
	trunc(ce.timestamp, 'YEAR')
         ;


--        drpt_shipping compiles the total shipping cost for each shipping group   
create or replace view drpt_shipping
as
select o.order_id as order_id, 
	ai.amount as shipping_cost
from dcspp_order o, 
	dcspp_ship_group sg, 
	dcspp_amount_info ai
where o.order_id = sg.order_ref 
	and sg.price_info = ai.amount_info_id
         ;


--        drpt_taxes compiles the total tax cost for each order   
create or replace view drpt_taxes
as
select o.order_id as order_id, 
	ai.amount as tax
from dcspp_order o, 
	dcspp_amount_info ai
where o.tax_price_info = ai.amount_info_id
         ;


--        drpt_cancels compiles information about orders that failed or   
--        were cancelled   
create or replace view drpt_cancels
as
select o.order_id as order_id, 
	o.submitted_date as submitted_date,
	ai.amount as amount, 
	si.shipping_cost as shipping_cost,
	ti.tax as tax
from dcspp_order o, 
	dcspp_amount_info ai, 
	drpt_shipping si, 
	drpt_taxes ti
where (o.state = 'FAILED' or o.state = 'REMOVED') 
	and o.price_info = ai.amount_info_id
	and o.order_id = si.order_id 
	and o.order_id = ti.order_id
         ;


--        drptw_cancels calculates the number of orders that failed or   
--        were cancelled each week.  It also calculates the total   
--        amount, shipping costs, and tax costs from those orders.   
create or replace view drptw_cancels
as
select trunc(submitted_date, 'DAY') as week, 
	count(order_id) as number_of_cancels,
	sum(amount) as amount, 
	sum(shipping_cost) as shipping_cost, 
	sum(tax) as tax
from drpt_cancels
group by trunc(submitted_date, 'DAY')
UNION
select trunc(submitted_date, 'DAY') as week, 
	0 as number_of_cancels, 
	0 as amount,
	0 as shipping_cost, 
	0 as tax
from dcspp_order
where trunc(submitted_date, 'DAY') not in (select trunc(submitted_date, 'DAY') from drpt_cancels)
group by trunc(submitted_date, 'DAY')
         ;


--        drptm_cancels calculates the number of orders that failed or   
--        were cancelled each month.  It also calculates the total   
--        amount, shipping costs, and tax costs from those orders.   
create or replace view drptm_cancels
as
select trunc(submitted_date, 'MON') as month, 
	count(order_id) as number_of_cancels,
	sum(amount) as amount, 
	sum(shipping_cost) as shipping_cost, 
	sum(tax) as tax
from drpt_cancels
group by trunc(submitted_date, 'MON')
UNION
select trunc(submitted_date, 'MON') as month, 
	0 as number_of_cancels, 
	0 as amount,
	0 as shipping_cost, 
	0 as tax
from dcspp_order
where trunc(submitted_date, 'MON') not in (select trunc(submitted_date, 'MON') from drpt_cancels)
group by trunc(submitted_date, 'MON')
         ;


--        drptq_cancels calculates the number of orders that failed or   
--        were cancelled each quarter.  It also calculates the total   
--        amount, shipping costs, and tax costs from those orders.   
create or replace view drptq_cancels
as
select trunc(submitted_date, 'Q') as quarter, 
	count(order_id) as number_of_cancels,
	sum(amount) as amount, 
	sum(shipping_cost) as shipping_cost, 
	sum(tax) as tax
from drpt_cancels
group by trunc(submitted_date, 'Q')
UNION
select trunc(submitted_date, 'Q') as quarter, 
	0 as number_of_cancels, 
	0 as amount,
	0 as shipping_cost, 
	0 as tax
from dcspp_order
where trunc(submitted_date, 'Q') not in (select trunc(submitted_date, 'Q') from drpt_cancels)
group by trunc(submitted_date, 'Q')
         ;


--        drpta_cancels calculates the number of orders that failed or   
--        were cancelled in the past year.  It also calculates the total   
--        amount, shipping costs, and tax costs from those orders.   
create or replace view drpta_cancels
as
select trunc(submitted_date, 'YEAR') as year, 
	count(order_id) as number_of_cancels,
	sum(amount) as amount, 
	sum(shipping_cost) as shipping_cost, 
	sum(tax) as tax
from drpt_cancels
group by trunc(submitted_date, 'YEAR')
UNION
select trunc(submitted_date, 'YEAR') as year, 
	0 as number_of_cancels, 
	0 as amount,
	0 as shipping_cost, 
	0 as tax
from dcspp_order
where trunc(submitted_date, 'YEAR') not in (select trunc(submitted_date, 'YEAR') from drpt_cancels)
group by trunc(submitted_date, 'YEAR')
         ;


--        drptw_gift_certs calculates the number of gift certificates   
--        that were redeemed each week   
create or replace view drptw_gift_certs
as
select trunc(o.submitted_date, 'DAY') as week, 
	count(gc.payment_group_id) as num_of_gift_certs
from dcspp_order o, 
	dcspp_pay_group pg, 
	dcspp_gift_cert gc
where o.order_id = pg.order_ref 
	and pg.payment_group_id = gc.payment_group_id
group by trunc(o.submitted_date, 'DAY')
UNION
select trunc(submitted_date, 'DAY') as week, 
	0 as num_of_gift_certs
from dcspp_order
where order_id not in 
(select pg.order_ref 
from dcspp_pay_group pg, 
	dcspp_gift_cert gc 
where pg.payment_group_id = gc.payment_group_id)
group by trunc(submitted_date, 'DAY')
         ;


--        drptm_gift_certs calculates the number of gift certificates   
--        that were redeemed each month   
create or replace view drptm_gift_certs
as
select trunc(o.submitted_date, 'MON') as month, 
	count(gc.payment_group_id) as num_of_gift_certs
from dcspp_order o, 
	dcspp_pay_group pg, 
	dcspp_gift_cert gc
where o.order_id = pg.order_ref 
	and pg.payment_group_id = gc.payment_group_id
group by trunc(o.submitted_date, 'MON')
UNION
select trunc(submitted_date, 'MON') as month, 
	0 as num_of_gift_certs
from dcspp_order
where order_id not in 
(select pg.order_ref 
from dcspp_pay_group pg, 
	dcspp_gift_cert gc 
where pg.payment_group_id = gc.payment_group_id)
group by trunc(submitted_date, 'MON')
         ;


--        drptq_gift_certs calculates the number of gift certificates   
--        that were redeemed each quarter   
create or replace view drptq_gift_certs
as
select trunc(o.submitted_date, 'Q') as quarter, 
	count(gc.payment_group_id) as num_of_gift_certs
from dcspp_order o, 
	dcspp_pay_group pg, 
	dcspp_gift_cert gc
where o.order_id = pg.order_ref 
	and pg.payment_group_id = gc.payment_group_id
group by trunc(o.submitted_date, 'Q')
UNION
select trunc(submitted_date, 'Q') as quarter, 
	0 as num_of_gift_certs
from dcspp_order
where order_id not in 
(select pg.order_ref 
from dcspp_pay_group pg, 
	dcspp_gift_cert gc 
where pg.payment_group_id = gc.payment_group_id)
group by trunc(submitted_date, 'Q')
         ;


--        drpta_gift_certs calculates the number of gift certificates   
--        that were redeemed each year   
create or replace view drpta_gift_certs
as
select trunc(o.submitted_date, 'YEAR') as year, 
	count(gc.payment_group_id) as num_of_gift_certs
from dcspp_order o, 
	dcspp_pay_group pg, 
	dcspp_gift_cert gc
where o.order_id = pg.order_ref 
	and pg.payment_group_id = gc.payment_group_id
group by trunc(o.submitted_date, 'YEAR')
UNION
select trunc(submitted_date, 'YEAR') as year, 
	0 as num_of_gift_certs
from dcspp_order
where order_id not in 
(select pg.order_ref 
from dcspp_pay_group pg, 
	dcspp_gift_cert gc 
where pg.payment_group_id = gc.payment_group_id)
group by trunc(submitted_date, 'YEAR')
         ;


--        drptw_orders calculates various totals from orders over each week   
create or replace view drptw_orders
as
select trunc(dri.submitted_date, 'DAY') as week, 
	(sum(ai.amount) + sum(si.shipping_cost) + sum(ti.tax)) as total_dollar_sales,
	(wct.amount + wct.shipping_cost + wct.tax) as dollar_cancels,
	(sum(ai.amount) - wct.amount) as merch_rev,
	(sum(si.shipping_cost) + sum(ti.tax) - wct.shipping_cost - wct.tax) as shipping_tax_rev,
	sum(cog.cost_of_goods) as cost_of_goods_sold,
	(count(dri.order_id)) as number_of_orders, 
	(wct.number_of_cancels) as cancelled_orders,
	(count(dri.order_id) - wct.number_of_cancels) as net_num_of_orders,
	sum(dri.total_items) as total_units_sold,
	wdt.num_of_discounts as num_of_discounts,
	wgct.num_of_gift_certs as num_of_gift_certs
from dcspp_amount_info ai, 
	drpt_shipping si, 
	drpt_taxes ti,
	drptw_cancels wct, 
	drptw_discounts wdt,
	drptw_gift_certs wgct, 
	drpt_order dri, 
	drpt_cost_of_goods cog
where dri.price_info = ai.amount_info_id 
	and dri.order_id = si.order_id 
	and dri.order_id = ti.order_id 
	and trunc(dri.submitted_date, 'DAY') = wct.week 
	and trunc(dri.submitted_date, 'DAY') = wdt.week 
	and trunc(dri.submitted_date, 'DAY') = wgct.week 
	and dri.order_id = cog.order_id
group by trunc(dri.submitted_date, 'DAY'), 
	wct.number_of_cancels, 
	wct.amount, 
	wct.shipping_cost, 
	wct.tax, 
	wdt.num_of_discounts, 
	wgct.num_of_gift_certs
         ;


--        drptm_orders calculates various totals from orders over each month   
create or replace view drptm_orders
as
select trunc(dri.submitted_date, 'MON') as month, 
	(sum(ai.amount) + sum(si.shipping_cost) + sum(ti.tax)) as total_dollar_sales,
	(mct.amount + mct.shipping_cost + mct.tax) as dollar_cancels,
	(sum(ai.amount) - mct.amount) as merch_rev,
	(sum(si.shipping_cost) + sum(ti.tax) - mct.shipping_cost - mct.tax) as shipping_tax_rev,
	sum(cog.cost_of_goods) as cost_of_goods_sold,
	(count(dri.order_id)) as number_of_orders, 
	(mct.number_of_cancels) as cancelled_orders,
	(count(dri.order_id) - mct.number_of_cancels) as net_num_of_orders,
	sum(dri.total_items) as total_units_sold,
	mdt.num_of_discounts as num_of_discounts,
	mgct.num_of_gift_certs as num_of_gift_certs
from dcspp_amount_info ai, 
	drpt_shipping si, 
	drpt_taxes ti,
	drptm_cancels mct, 
	drptm_discounts mdt,
	drptm_gift_certs mgct, 
	drpt_order dri, 
	drpt_cost_of_goods cog
where dri.price_info = ai.amount_info_id 
	and dri.order_id = si.order_id 
	and dri.order_id = ti.order_id 
	and trunc(dri.submitted_date, 'MON') = mct.month 
	and trunc(dri.submitted_date, 'MON') = mdt.month 
	and trunc(dri.submitted_date, 'MON') = mgct.month 
	and dri.order_id = cog.order_id
group by trunc(dri.submitted_date, 'MON'), 
	mct.number_of_cancels, mct.amount, 
	mct.shipping_cost, 
	mct.tax, 
	mdt.num_of_discounts, 
	mgct.num_of_gift_certs
         ;


--        drptq_orders calculates various totals from orders over each quarter   
create or replace view drptq_orders
as
select trunc(dri.submitted_date, 'Q') as quarter, 
	(sum(ai.amount) + sum(si.shipping_cost) + sum(ti.tax)) as total_dollar_sales,
	(qct.amount + qct.shipping_cost + qct.tax) as dollar_cancels,
	(sum(ai.amount) - qct.amount) as merch_rev,
	(sum(si.shipping_cost) + sum(ti.tax) - qct.shipping_cost - qct.tax) as shipping_tax_rev,
	sum(cog.cost_of_goods) as cost_of_goods_sold,
	(count(dri.order_id)) as number_of_orders, 
	(qct.number_of_cancels) as cancelled_orders,
	(count(dri.order_id) - qct.number_of_cancels) as net_num_of_orders,
	sum(dri.total_items) as total_units_sold,
	qdt.num_of_discounts as num_of_discounts,
	qgct.num_of_gift_certs as num_of_gift_certs
from dcspp_amount_info ai, 
	drpt_shipping si, 
	drpt_taxes ti,
	drptq_cancels qct, 
	drptq_discounts qdt,
	drptq_gift_certs qgct, 
	drpt_order dri, 
	drpt_cost_of_goods cog
where dri.price_info = ai.amount_info_id 
	and dri.order_id = si.order_id 
	and dri.order_id = ti.order_id 
	and trunc(dri.submitted_date, 'Q') = qct.quarter 
	and trunc(dri.submitted_date, 'Q') = qdt.quarter 
	and trunc(dri.submitted_date, 'Q') = qgct.quarter 
	and dri.order_id = cog.order_id
group by trunc(dri.submitted_date, 'Q'), 
	qct.number_of_cancels, 
	qct.amount, 
	qct.shipping_cost, 
	qct.tax, 
	qdt.num_of_discounts, 
	qgct.num_of_gift_certs
         ;


--        drpta_orders calculates various totals from orders over each year   
create or replace view drpta_orders
as
select trunc(dri.submitted_date, 'YEAR') as year, 
	(sum(ai.amount) + sum(si.shipping_cost) + sum(ti.tax)) as total_dollar_sales,
	(act.amount + act.shipping_cost + act.tax) as dollar_cancels,
	(sum(ai.amount) - act.amount) as merch_rev,
	(sum(si.shipping_cost) + sum(ti.tax) - act.shipping_cost - act.tax) as shipping_tax_rev,
	sum(cog.cost_of_goods) as cost_of_goods_sold,
	(count(dri.order_id)) as number_of_orders, 
	(act.number_of_cancels) as cancelled_orders,
	(count(dri.order_id) - act.number_of_cancels) as net_num_of_orders,
	sum(dri.total_items) as total_units_sold,
	adt.num_of_discounts as num_of_discounts,
	agct.num_of_gift_certs as num_of_gift_certs
from dcspp_amount_info ai, 
	drpt_shipping si, 
	drpt_taxes ti,
	drpta_cancels act, 
	drpta_discounts adt,
	drpta_gift_certs agct, 
	drpt_order dri, 
	drpt_cost_of_goods cog
where dri.price_info = ai.amount_info_id 
	and dri.order_id = si.order_id 
	and dri.order_id = ti.order_id 
	and trunc(dri.submitted_date, 'YEAR') = act.year 
	and trunc(dri.submitted_date, 'YEAR') = adt.year 
	and trunc(dri.submitted_date, 'YEAR') = agct.year 
	and dri.order_id = cog.order_id
group by trunc(dri.submitted_date, 'YEAR'), 
	act.number_of_cancels, 
	act.amount, 
	act.shipping_cost, 
	act.tax, 
	adt.num_of_discounts, 
	agct.num_of_gift_certs
         ;


create or replace view drpt_visitor
(profileid,day,state,country)
as
 select distinct 
	vi.profileid, 
	TRUNC(vi.timestamp, 'DATE') as day, 
	ci.state, 
	ci.country
from 
	dss_dps_view_item vi, 
	dps_user_address ua, 
	dps_contact_info ci
where 
	vi.profileid = ua.id 
	and ua.billing_addr_id = ci.id
         ;


--        drpt_cart calculates the total quantity and amount of   
--        items that have been added to shopping carts over each day   
create or replace view drpt_cart
(orderid,day,quantity,amount)
as
select 
	orderid as orderid, 
	trunc(timestamp) as day, 
	sum(quantity) as quantity, 
	sum(amount) as amount
from dcs_cart_event
group by orderid, 
	trunc(timestamp)
         ;


--        drptw_fiscal_info caclulates several revenue-related totals over each week   
create or replace view drptw_fiscal_info
as
select wot.week as week, 
	wot.total_dollar_sales as total_dollar_sales,
	wot.dollar_cancels as dollar_cancels, 
	(wot.total_dollar_sales - wot.dollar_cancels) as net_dollar_sales,
	wot.merch_rev as merch_rev,
	wot.shipping_tax_rev as shipping_tax_rev, 
	wot.number_of_orders as number_of_orders,
	wot.cancelled_orders as cancelled_orders, 
	wot.net_num_of_orders as net_num_of_orders,
	count(distinct ce.orderid) as num_of_carts, 
	((wot.total_dollar_sales - wot.dollar_cancels) / wot.net_num_of_orders) as avg_order_rev,
	(wot.merch_rev / wot.net_num_of_orders) as avg_order_merc_rev,
	count(distinct vi.sessionid) as number_of_shoppers,
	(wot.number_of_orders / count(distinct vi.sessionid)) as shop_to_purc_conv,
	(count(distinct ce.orderid) / count(distinct vi.sessionid)) as shop_to_cart_conv,
	(wot.number_of_orders / count(distinct ce.orderid)) as cart_to_purc_conv,
	wot.num_of_discounts as num_of_discounts, 
	wot.num_of_gift_certs as num_of_gift_certs
from drptw_orders wot, 
	dcs_cart_event ce, 
	dss_dps_view_item vi
where trunc(ce.timestamp, 'DAY') = wot.week 
	and trunc(vi.timestamp, 'DAY') = wot.week
group by wot.week, 
	wot.total_dollar_sales, 
	wot.dollar_cancels,
	wot.merch_rev, 
	wot.shipping_tax_rev, 
	wot.number_of_orders,
	wot.cancelled_orders, 
	wot.net_num_of_orders, 
	wot.num_of_discounts,
	wot.num_of_gift_certs
         ;


--        drptm_fiscal_info caclulates several revenue-related totals over each month   
create or replace view drptm_fiscal_info
as
select mot.month as month, 
	mot.total_dollar_sales as total_dollar_sales,
	mot.dollar_cancels as dollar_cancels, 
	(mot.total_dollar_sales - mot.dollar_cancels) as net_dollar_sales,
	mot.merch_rev as merch_rev,
	mot.shipping_tax_rev as shipping_tax_rev, 
	mot.number_of_orders as number_of_orders,
	mot.cancelled_orders as cancelled_orders, 
	mot.net_num_of_orders as net_num_of_orders,
	count(distinct ce.orderid) as num_of_carts, 
	((mot.total_dollar_sales - mot.dollar_cancels) / mot.net_num_of_orders) as avg_order_rev,
	(mot.merch_rev / mot.net_num_of_orders) as avg_order_merc_rev,
	count(distinct vi.sessionid) as number_of_shoppers,
	(mot.number_of_orders / count(distinct vi.sessionid)) as shop_to_purc_conv,
	(count(distinct ce.orderid) / count(distinct vi.sessionid)) as shop_to_cart_conv,
	(mot.number_of_orders / count(distinct ce.orderid)) as cart_to_purc_conv,
	mot.num_of_discounts as num_of_discounts, 
	mot.num_of_gift_certs as num_of_gift_certs
from drptm_orders mot, 
	dcs_cart_event ce, 
	dss_dps_view_item vi
where trunc(ce.timestamp, 'MON') = mot.month 
	and trunc(vi.timestamp, 'MON') = mot.month
group by mot.month, 
	mot.total_dollar_sales, 
	mot.dollar_cancels,
	mot.merch_rev, 
	mot.shipping_tax_rev, 
	mot.number_of_orders,
	mot.cancelled_orders, 
	mot.net_num_of_orders, 
	mot.num_of_discounts,
	mot.num_of_gift_certs
         ;


--        drptq_fiscal_info caclulates several revenue-related totals over each quarter   
create or replace view drptq_fiscal_info
as
select qot.quarter as quarter, 
	qot.total_dollar_sales as total_dollar_sales,
	qot.dollar_cancels as dollar_cancels, 
	(qot.total_dollar_sales - qot.dollar_cancels) as net_dollar_sales,
	qot.merch_rev as merch_rev,
	qot.shipping_tax_rev as shipping_tax_rev, 
	qot.number_of_orders as number_of_orders,
	qot.cancelled_orders as cancelled_orders, 
	qot.net_num_of_orders as net_num_of_orders,
	count(distinct ce.orderid) as num_of_carts, 
	((qot.total_dollar_sales - qot.dollar_cancels) / qot.net_num_of_orders) as avg_order_rev,
	(qot.merch_rev / qot.net_num_of_orders) as avg_order_merc_rev,
	count(distinct vi.sessionid) as number_of_shoppers,
	(qot.number_of_orders / count(distinct vi.sessionid)) as shop_to_purc_conv,
	(count(distinct ce.orderid) / count(distinct vi.sessionid)) as shop_to_cart_conv,
	(qot.number_of_orders / count(distinct ce.orderid)) as cart_to_purc_conv,
	qot.num_of_discounts as num_of_discounts, 
	qot.num_of_gift_certs as num_of_gift_certs
from drptq_orders qot, 
	dcs_cart_event ce, 
	dss_dps_view_item vi
where trunc(ce.timestamp, 'Q') = qot.quarter 
	and trunc(vi.timestamp, 'Q') = qot.quarter
group by qot.quarter, 
	qot.total_dollar_sales, 
	qot.dollar_cancels,
	qot.merch_rev, 
	qot.shipping_tax_rev, 
	qot.number_of_orders,
	qot.cancelled_orders, 
	qot.net_num_of_orders, 
	qot.num_of_discounts,
	qot.num_of_gift_certs
         ;


--        drpta_fiscal_info caclulates several revenue-related totals over each year   
create or replace view drpta_fiscal_info
as
select aot.year as year, 
	aot.total_dollar_sales as total_dollar_sales,
	aot.dollar_cancels as dollar_cancels, 
	(aot.total_dollar_sales - aot.dollar_cancels) as net_dollar_sales,
	aot.merch_rev as merch_rev,
	aot.shipping_tax_rev as shipping_tax_rev,
	aot.number_of_orders as number_of_orders,
	aot.cancelled_orders as cancelled_orders, 
	aot.net_num_of_orders as net_num_of_orders,
	count(distinct ce.orderid) as num_of_carts, 
	((aot.total_dollar_sales - aot.dollar_cancels) / aot.net_num_of_orders) as avg_order_rev,
	(aot.merch_rev / aot.net_num_of_orders) as avg_order_merc_rev,
	count(distinct vi.sessionid) as number_of_shoppers,
	(aot.number_of_orders / count(distinct vi.sessionid)) as shop_to_purc_conv,
	(count(distinct ce.orderid) / count(distinct vi.sessionid)) as shop_to_cart_conv,
	(aot.number_of_orders / count(distinct ce.orderid)) as cart_to_purc_conv,
	aot.num_of_discounts as num_of_discounts, 
	aot.num_of_gift_certs as num_of_gift_certs
from drpta_orders aot, 
	dcs_cart_event ce, 
	dss_dps_view_item vi
where trunc(ce.timestamp, 'YEAR') = aot.year 
	and trunc(vi.timestamp, 'YEAR') = aot.year
group by aot.year, 
	aot.total_dollar_sales, 
	aot.dollar_cancels,
	aot.merch_rev, 
	aot.shipping_tax_rev, 
	aot.number_of_orders,
	aot.cancelled_orders, 
	aot.net_num_of_orders, 
	aot.num_of_discounts,
	aot.num_of_gift_certs
         ;


--        drptw_promotion calculates totals about orders that were discounted   
--        by the sample 'promo60003' promotion over each week   
create or replace view drptw_promotion
as
select trunc(o.submitted_date, 'DAY') as week, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by trunc(o.submitted_date, 'DAY')
         ;


--        drptm_promotion calculates totals about orders that were discounted   
--        by the sample 'promo60003' promotion over each month   
create or replace view drptm_promotion
as
select trunc(o.submitted_date, 'MON') as month, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by trunc(o.submitted_date, 'MON')
         ;


--        drptq_promotion calculates totals about orders that were discounted   
--        by the sample 'promo60003' promotion over each quarter   
create or replace view drptq_promotion
as
select trunc(o.submitted_date, 'Q') as quarter, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by trunc(o.submitted_date, 'Q')
         ;


--        drpta_promotion calculates totals about orders that were discounted   
--        by the sample 'promo60003' promotion over each year   
create or replace view drpta_promotion
as
select trunc(o.submitted_date, 'YEAR') as year, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by trunc(o.submitted_date, 'YEAR')
         ;


--        drptw_male_18_25 calculates totals about orders that were placed   
--        each week by males aged 18-25   
create or replace view drptw_male_18_25
as
select trunc(o.submitted_date, 'DAY') as week, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 >= 18) 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 < 25)
group by trunc(o.submitted_date, 'DAY')
         ;


--        drptm_male_18_25 calculates totals about orders that were placed   
--        each month by males aged 18-25   
create or replace view drptm_male_18_25
as
select trunc(o.submitted_date, 'MON') as month, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 >= 18) 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 < 25) 
group by trunc(o.submitted_date, 'MON')
         ;


--        drptq_male_18_25 calculates totals about orders that were placed   
--        each quarter by males aged 18-25   
create or replace view drptq_male_18_25
as
select trunc(o.submitted_date, 'Q') as quarter, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 >= 18) 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 < 25) 
group by trunc(o.submitted_date, 'Q')
         ;


--        drpta_male_18_25 calculates totals about orders that were placed   
--        each year by males aged 18-25   
create or replace view drpta_male_18_25
as
select trunc(o.submitted_date, 'YEAR') as year, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 >= 18) 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 < 25) 
group by trunc(o.submitted_date, 'YEAR')
         ;


--        drptw_male_25_39 calculates totals about orders that were placed   
--        each week by males aged 25-39   
create or replace view drptw_male_25_39
as
select trunc(o.submitted_date, 'DAY') as week, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id
	and u.gender = 2 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 >= 25) 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 < 39)
group by trunc(o.submitted_date, 'DAY')
         ;


--        drptm_male_25_39 calculates totals about orders that were placed   
--        each month by males aged 25-39   
create or replace view drptm_male_25_39
as
select trunc(o.submitted_date, 'MON') as month, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 >= 25) 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 < 39) 
group by trunc(o.submitted_date, 'MON')
         ;


--        drptq_male_25_39 calculates totals about orders that were placed   
--        each quarter by males aged 25-39   
create or replace view drptq_male_25_39
as
select trunc(o.submitted_date, 'Q') as quarter, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 >= 25) 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 < 39) 
group by trunc(o.submitted_date, 'Q')
         ;


--        drpta_male_25_39 calculates totals about orders that were placed   
--        each year by males aged 25-39   
create or replace view drpta_male_25_39
as
select trunc(o.submitted_date, 'YEAR') as year, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 >= 25) 
	and (MONTHS_BETWEEN(o.submitted_date, u.date_of_birth)/12 < 39) 
group by trunc(o.submitted_date, 'YEAR')
         ;



