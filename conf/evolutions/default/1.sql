# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table app_data (
  id                        bigint auto_increment not null,
  app_id                    varchar(255),
  access_token              varchar(255),
  access_token_expires      varchar(255),
  permissions               varchar(255),
  redirect_url              varchar(255),
  secret                    varchar(255),
  constraint pk_app_data primary key (id))
;

create table app_logger (
  id                        bigint auto_increment not null,
  app_name                  varchar(255),
  event                     varchar(255),
  timestamp                 varchar(255),
  content                   varchar(255),
  display_id                varchar(255),
  constraint pk_app_logger primary key (id))
;

create table surround_sound_fb_cover (
  owner_id                  bigint auto_increment not null,
  offset_y                  integer,
  cover_id                  varchar(255),
  source                    varchar(255),
  constraint pk_surround_sound_fb_cover primary key (owner_id))
;

create table surround_sound_fb_event (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  owner_name                varchar(255),
  owner_id                  varchar(255),
  description               varchar(10000),
  start_time                datetime,
  end_time                  datetime,
  location                  varchar(255),
  rsvp_status               varchar(255),
  privacy                   varchar(255),
  update_time               datetime,
  page_id                   bigint,
  cover_id                  varchar(255),
  cover_source              varchar(255),
  cover_offset_y            varchar(255),
  cover_offset_x            varchar(255),
  date                      varchar(255),
  time                      varchar(255),
  day                       varchar(255),
  cover_offset              bigint,
  attendance                integer,
  place                     varchar(255),
  link                      varchar(255),
  karma                     integer,
  time_flag                 varchar(255),
  constraint pk_surround_sound_fb_event primary key (id))
;

create table surround_sound_fb_page (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  about                     varchar(255),
  access_token              varchar(255),
  can_post                  varchar(255),
  category                  varchar(255),
  checkins                  integer,
  company_overview          varchar(1000),
  description               varchar(5000),
  founded                   varchar(255),
  general_info              varchar(255),
  is_community_page         tinyint(1) default 0,
  is_published              tinyint(1) default 0,
  likes                     bigint,
  link                      varchar(255),
  mission                   varchar(255),
  phone                     varchar(255),
  pictures                  varchar(255),
  products                  varchar(1000),
  talking_about_count       bigint,
  username                  varchar(255),
  constraint pk_surround_sound_fb_page primary key (id))
;

create table surround_sound_fb_user (
  event_id                  bigint auto_increment not null,
  id                        bigint auto_increment not null,
  big_avatar                varchar(255),
  picture                   varchar(255),
  name                      varchar(255))
;

create table surround_sound_last_update (
  id                        bigint auto_increment not null,
  last_update               datetime,
  constraint pk_surround_sound_last_update primary key (id))
;

create table surround_sound_location (
  city                      varchar(255) not null,
  country                   varchar(255),
  latitude                  double,
  longitude                 double,
  state                     varchar(255),
  street                    varchar(255),
  zip                       varchar(255),
  constraint pk_surround_sound_location primary key (city))
;

create table surround_sound_organizer (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  constraint pk_surround_sound_organizer primary key (id))
;

create table surround_sound_venue (
  id                        bigint auto_increment not null,
  street                    varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  zip                       varchar(255),
  country                   varchar(255),
  latitude                  double,
  longitude                 double,
  constraint pk_surround_sound_venue primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table app_data;

drop table app_logger;

drop table surround_sound_fb_cover;

drop table surround_sound_fb_event;

drop table surround_sound_fb_page;

drop table surround_sound_fb_user;

drop table surround_sound_last_update;

drop table surround_sound_location;

drop table surround_sound_organizer;

drop table surround_sound_venue;

SET FOREIGN_KEY_CHECKS=1;

