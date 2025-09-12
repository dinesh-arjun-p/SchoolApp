use school;

create table role(
	role_id int auto_increment primary key,
    role_name varchar(50)not null unique
);

insert into role (role_name) values ('SuperAdmin'),('Teacher'),('Student');


drop table person;
create table person(
	 roll_no varchar(50) primary key,
     userid varchar(50) unique,
     name varchar(100) not null,
     pass varchar(100) not null,
     email VARCHAR(255)  UNIQUE,
     role_id int,
     foreign key (role_id)references role(role_id)
     on delete set null
     on update cascade
);
insert into person(roll_no,name,pass,email,role_id)
values('zohoAdmin1','Admin','admin','dinesharjun.ec22@bitsathy.ac.in',1);


drop table request_access;
CREATE TABLE request_access (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    request_date DATE NOT NULL,
    department VARCHAR(100) NOT NULL,
    requested_by VARCHAR(50) NOT NULL,
    status ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
    reviewed_by VARCHAR(50) ,
    CONSTRAINT fk_requested_by FOREIGN KEY (requested_by) REFERENCES person(roll_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES person(roll_no)
        ON DELETE SET null
        ON UPDATE CASCADE
);


drop table notification;
create table notification(
	notification_id int auto_increment primary key,
    student_roll_no varchar(50) not null,
    department varchar(100) not null,
    reviewed_by varchar(50) ,
    status enum('Approved','Rejected') not null,
    created_at timestamp default current_timestamp,
    request_date date not null,
    constraint fk_notify_student_roll_no foreign key (student_roll_no) references person(roll_no)
    on delete cascade
    on update cascade,
    constraint fk_notify_reviewed_by foreign key (reviewed_by) references person(roll_no)
    on delete set null
    on update cascade
);

drop table login_history;
create table audit_logs(
	id int auto_increment primary key,
    username varchar(100) not null,
    event varchar(100) not null,
    time datetime default current_timestamp,
    constraint fk_user_login_roll_no foreign key (username) references person(roll_no)
	on delete cascade
    on update cascade
);

set sql_safe_updates=0;
delete from person where name='Student';
select * from person;
