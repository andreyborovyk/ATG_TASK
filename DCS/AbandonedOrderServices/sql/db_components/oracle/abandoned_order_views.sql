


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/AbandonedOrderServices/sql/abandoned_order_views.xml#2 $$Change: 655658 $
create or replace view drpt_abandon_ord
as
      select oa.abandonment_date as abandonment_date, ai.amount as amount, decode(oa.abandon_state, 'CONVERTED', 100, 0) as converted from dcspp_order o, dcspp_ord_abandon oa, dcspp_amount_info ai where oa.order_id=o.order_id and o.price_info=ai.amount_info_id
         ;


create or replace view drpt_tns_abndn_ord
as
      select date_time as abandonment_date, amount as amount from drpt_session_ord where submitted=0
         ;



