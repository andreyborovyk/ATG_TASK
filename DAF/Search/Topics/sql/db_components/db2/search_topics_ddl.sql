



create table src_topic_set (
	topic_set_id	varchar(40)	not null,
	language_en	varchar(200)	default null,
	last_mod	date	not null
,constraint src_topic_set_pk primary key (topic_set_id));


create table src_topic (
	topic_id	varchar(40)	not null,
	node_type	integer	not null,
	parent_id	varchar(40)	default null,
	name	varchar(200)	default null,
	display_name	varchar(200)	default null,
	description	varchar(1000)	default null
,constraint src_topic_pk primary key (topic_id)
,constraint src_topic_fk1 foreign key (parent_id) references src_topic (topic_id));

create index src_topic1_ix on src_topic (parent_id);
create index src_topic_ix1 on src_topic (name);

create table src_topicchild_seq (
	parent_topic_id	varchar(40)	not null,
	child_topic_id	varchar(40)	not null,
	sequence_num	integer	not null
,constraint src_topicchild_p primary key (parent_topic_id,sequence_num)
,constraint src_topicchild_fk1 foreign key (parent_topic_id) references src_topic (topic_id));


create table src_roottopics_seq (
	topic_set_id	varchar(40)	not null,
	topic_id	varchar(40)	not null,
	sequence_num	integer	not null
,constraint src_roottpcs_seq_p primary key (topic_set_id,sequence_num)
,constraint src_roottopics_fk1 foreign key (topic_set_id) references src_topic_set (topic_set_id)
,constraint src_roottopics_fk2 foreign key (topic_id) references src_topic (topic_id));

create index src_rttopicsseq1_x on src_roottopics_seq (topic_id);

create table src_topic_macro (
	macro_id	varchar(40)	not null,
	macro_name	varchar(80)	not null,
	learn_id	varchar(40)	default null,
	def	varchar(1666)	not null
,constraint src_topicmacro_p primary key (macro_id));


create table src_topicmacro_seq (
	topic_id	varchar(40)	not null,
	macro_id	varchar(40)	not null,
	sequence_num	integer	not null
,constraint src_topicmacros_p primary key (topic_id,sequence_num)
,constraint src_topicmac_fk1 foreign key (topic_id) references src_topic (topic_id)
,constraint src_topicmac_fk2 foreign key (macro_id) references src_topic_macro (macro_id));

create index src_topicmac2_ix on src_topicmacro_seq (macro_id);

create table src_global_macro (
	macro_id	varchar(40)	not null,
	macro_name	varchar(80)	not null,
	learn_id	varchar(40)	default null,
	def	varchar(1666)	not null
,constraint src_globalmacro_p primary key (macro_id));


create table src_topic_pattern (
	pattern_id	varchar(40)	not null,
	learn_id	varchar(40)	default null,
	pattern	varchar(1000)	not null,
	weight	integer	not null,
	language	varchar(200)	default null,
	groups	varchar(200)	default null,
	is_enabled	numeric(1)	not null
,constraint src_topicpattern_p primary key (pattern_id));


create table src_topic_pat_seq (
	topic_id	varchar(40)	not null,
	pattern_id	varchar(40)	not null,
	sequence_num	integer	not null
,constraint src_topicpats_p primary key (topic_id,sequence_num)
,constraint src_topicpats_fk1 foreign key (topic_id) references src_topic (topic_id)
,constraint src_topicpats_fk2 foreign key (pattern_id) references src_topic_pattern (pattern_id));

create index src_topicpats2_ix on src_topic_pat_seq (pattern_id);

create table src_topic_label (
	label_id	varchar(40)	not null,
	name	varchar(200)	not null,
	topic_id	varchar(40)	not null
,constraint src_topic_label_p primary key (label_id)
,constraint srctopiclabel_fk1 foreign key (topic_id) references src_topic (topic_id));

create index srctopiclabel1_ix on src_topic_label (topic_id);
commit;


