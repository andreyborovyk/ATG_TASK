


-- ATG Reporting Framework DDL - DCS.DW.Search
-- =================================
-- Fact: LINE ITEM QUERY
-- Dimensions: LINE ITEM
-- QUERY

create table ARF_LINE_ITEM_QUERY (
	ID	number(19)	not null,
	LINE_ITEM_ID	number(19)	not null,
	QUERY_ID	number(10)	not null,
	NLINE_ITEM_ID	varchar2(40)	not null,
	NQUERY_ID	varchar2(40)	not null
,constraint ARF_LI_QUERY_P primary key (ID)
,constraint ARF_LI_QUERY_F1 foreign key (LINE_ITEM_ID) references ARF_LINE_ITEM (LINE_ITEM_ID)
,constraint ARF_LI_QUERY_F2 foreign key (QUERY_ID) references ARF_QUERY (ID));

create index ARF_LI_QUERY_X13 on ARF_LINE_ITEM_QUERY (LINE_ITEM_ID);
create index ARF_LI_QUERY_X14 on ARF_LINE_ITEM_QUERY (QUERY_ID);
create index ARF_LI_QUERY_X15 on ARF_LINE_ITEM_QUERY (NLINE_ITEM_ID);
create index ARF_LI_QUERY_X16 on ARF_LINE_ITEM_QUERY (NQUERY_ID);



