


--  @version $id: c:/work/platform/acton/daf/search/common/src/config/atg/search/repository/searchtestingrepository_ddl.xml $$change: 433350 $
-- This file contains create table statements, which will configure your database for use with the search testing repository
--     Add the tst_folders table, searchTestFolder

create table tst_folders (
	id	varchar2(40)	not null,
	application	number(10)	not null,
	owner_id	varchar2(40)	null,
	display_name	varchar2(254)	not null,
	folder_type	number(10)	not null,
	description	varchar2(254)	null,
	parent_folder_id	varchar2(40)	null
,constraint tst_folders_p primary key (id));

-- Add the tst_search_tests table, searchTest

create table tst_search_tests (
	id	varchar2(40)	not null,
	application	number(10)	not null,
	parent_folder_id	varchar2(40)	not null,
	owner_id	varchar2(40)	null,
	display_name	varchar2(254)	not null,
	description	varchar2(254)	null,
	creation_date	timestamp	not null,
	creation_author	varchar2(254)	not null,
	last_modified_date	timestamp	not null,
	last_modified_author	varchar2(254)	not null,
	search_config_name	varchar2(254)	null,
	refine_config_name	varchar2(254)	null,
	category_id	varchar2(40)	null,
	query_text	clob	null,
	facet_trail	clob	null,
	sort_by_type	number(10)	null,
	sort_prop	clob	null,
	results	number(10)	null,
	preview_user_id	varchar2(40)	null
,constraint tst_search_tests_p primary key (id)
,constraint tst_search_tests_f foreign key (parent_folder_id) references tst_folders (id));

create index tst_search_tests_x on tst_search_tests (parent_folder_id);
--     Add the tst_track_items table, searchTest.trackItems

create table tst_track_items (
	test_id	varchar2(40)	not null,
	track_item_id	varchar2(254)	not null
,constraint tst_track_items_p primary key (test_id,track_item_id)
,constraint tst_track_items_f foreign key (test_id) references tst_search_tests (id));

--     Add the tst_test_types table, searchTest.testTypes

create table tst_test_types (
	test_id	varchar2(40)	not null,
	test_type	varchar2(40)	not null
,constraint tst_test_types_p primary key (test_id,test_type)
,constraint tst_test_types_f foreign key (test_id) references tst_search_tests (id));

--     Add the tst_dimension_values table, searchTest.dimensionValues

create table tst_dimension_values (
	test_id	varchar2(40)	not null,
	dimension_name	varchar2(254)	not null,
	dimension_value	varchar2(254)	not null
,constraint tst_dimvalues_p primary key (test_id,dimension_name)
,constraint tst_dimvalues_f foreign key (test_id) references tst_search_tests (id));

--     Add the tst_advanced_parameters table, searchTest.advancedParameters

create table tst_advanced_parameters (
	test_id	varchar2(40)	not null,
	param_key	varchar2(254)	not null,
	param_value	clob	not null
,constraint tst_advparam_p primary key (test_id,param_key)
,constraint tst_advparam_f foreign key (test_id) references tst_search_tests (id));




