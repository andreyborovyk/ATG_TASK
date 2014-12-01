create table pagestats (
  pagename varchar(64) not null,
  rating1  int not null,
  rating2  int not null,
  rating3  int not null,
  primary key(pagename)
);

create table comment (
  pagename varchar(64) not null references pagestats(pagename),
  value varchar(500),
  index int null
);

