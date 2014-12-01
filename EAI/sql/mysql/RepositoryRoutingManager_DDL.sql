CREATE TABLE eai_transform (
	id 			VARCHAR(32)	NOT NULL,
	reference 		VARCHAR(255)	NULL,
	PRIMARY KEY(id)
);

CREATE TABLE eai_map (
	id 			VARCHAR(32)	NOT NULL,
	reference 		VARCHAR(255)	NULL,
	PRIMARY KEY(id)
);

CREATE TABLE eai_channel (
	id 			VARCHAR(32)	NOT NULL,
	reference 		VARCHAR(255)	NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE eai_parser (
	id 			VARCHAR(32)	NOT NULL,
	reference 		VARCHAR(255)	NULL,
	PRIMARY KEY(id)
);

CREATE TABLE eai_renderer (
	id 			VARCHAR(32)	NOT NULL,
	reference 		VARCHAR(255)	NULL,
	PRIMARY KEY(id)
);

CREATE TABLE eai_route (
	id 			VARCHAR(32)	NOT NULL,
	type 			INTEGER	NOT NULL,
	name 			VARCHAR(32)	NULL,
	jms_type 		VARCHAR(255)	NULL,
	channel_id 		VARCHAR(32)	NOT NULL REFERENCES eai_channel(id),
	PRIMARY KEY(id)
);

CREATE TABLE eai_transform_list (
	route_id 		VARCHAR(32)	NOT NULL REFERENCES eai_route(id),
	idx 			INTEGER	NOT NULL,
	transform_id 		VARCHAR(32)	NULL REFERENCES eai_transform(id),
	PRIMARY KEY(route_id, idx)
);

CREATE INDEX eai_transform_list_route_idx ON eai_transform_list(route_id);

CREATE TABLE eai_map_list (
	route_id 		VARCHAR(32)	NOT NULL REFERENCES eai_route(id),
	idx 			INTEGER	not null,
	map_id 			VARCHAR(32)	NULL REFERENCES eai_map(id),
	PRIMARY KEY(route_id, idx)
);

CREATE INDEX eai_map_list_route_idx ON eai_map_list(route_id);

CREATE TABLE eai_outgoing (
	route_id 		VARCHAR(32)	NOT NULL REFERENCES eai_route(id),
	render_id 		VARCHAR(32)	NULL REFERENCES eai_renderer(id),
	PRIMARY KEY(route_id)
);

CREATE INDEX eai_outgoing_route_idx ON eai_outgoing(route_id);

CREATE TABLE eai_incoming (
	route_id 		VARCHAR(32)	NOT NULL REFERENCES eai_route(id),
	parser_id 		VARCHAR(32)	NULL REFERENCES eai_parser(id),
	PRIMARY KEY(route_id)
);

CREATE INDEX eai_incoming_route_idx ON eai_incoming(route_id);

COMMIT;

