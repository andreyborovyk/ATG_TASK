


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/reporting_views1.xml#3 $$Change: 655658 $
--        drpt_products compiles information about each product in the catalog   
create view drpt_products
as
select p.product_id as product_id, 
	c.display_name as category_name, 
	avg(s.wholesale_price) as avg_cost, 
	avg(s.list_price) as avg_list_price, 
	avg(s.sale_price) as avg_sale_price, 
	((avg(s.list_price) - avg(s.wholesale_price)) / avg(s.wholesale_price)) as avg_initial_markup, 
	sum(inv.stock_level) as units_on_hand, 
	count(s.sku_id) as number_of_skus
from dcs_product p, 
	dcs_category c, 
	dcs_sku s, 
	dcs_prd_chldsku pc, 
	dcs_inventory inv
where p.parent_cat_id = c.category_id 
	and p.product_id = pc.product_id 
	and pc.sku_id = s.sku_id
	and pc.sku_id = inv.catalog_ref_id
group by p.product_id, 
	c.display_name
         ;


--        drpt_category calculates statistics about prices and costs on a per-category basis   
create view drpt_category
as
select c.display_name as category_name, 
	avg(s.wholesale_price)as avg_cost,
	avg(s.list_price) as avg_list_price,
	avg(s.sale_price) as avg_sale_price,
	((avg(s.list_price) - avg(s.wholesale_price)) / avg(s.wholesale_price)) as avg_initial_markup
from dcs_category c, 
	dcs_sku s, 
	dcs_prd_chldsku pc, 
	dcs_product p
where c.category_id = p.parent_cat_id 
	and pc.product_id = p.product_id 
	and pc.sku_id = s.sku_id
group by c.display_name
         ;


commit;
