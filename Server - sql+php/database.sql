/* database.sql
 * Creates database with tables for users and raspberry pi's
*/

/*drop trigger if exists check_datetime;*/
drop table if exists PiAddress;
drop table if exists Users;

/*creates table that stores users of the app*/
create table Users(
	username varchar(255) not null,
	password varchar(255) not null,
	primary key(username)
);

/*creates table that stores Raspberry pi's addresses*/
create table PiAddress(
	name varchar(255) not null, /*hostname of camera*/
	ipAddress INT UNSIGNED not null, /*public ip address of camera*/
	lastUpdate datetime not null,
	primary key (name) 
);

