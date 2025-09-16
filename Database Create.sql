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
     phone_number varchar(10),
     class int,
     foreign key (role_id)references role(role_id)
     on delete set null
     on update cascade
);
insert into person(roll_no,name,pass,email,role_id)
values('zohoAdmin1','Admin','admin','dinesharjun.ec22@bitsathy.ac.in',1);


drop table if exists rule;
create table rule(
	rule_id int auto_increment primary key,
    attribute varchar(100),
    operator varchar(100),
    status_limit int,
    priority int not null unique
);
insert into rule (rule_id,status_limit,priority)values(1,1,-1);


drop table if exists request_access;
CREATE TABLE request_access (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    request_date DATE DEFAULT (CURRENT_DATE),
    action VARCHAR(100) NOT NULL,
    action_for varchar(100),
    action_value varchar(100) ,
    requested_by VARCHAR(50) NOT NULL,
    rule_id INT NOT NULL,
    status INT DEFAULT 0,  -- count of approvals
    role ENUM('Reviewer','Executer') DEFAULT 'Reviewer',
    CONSTRAINT fk_requested_by FOREIGN KEY (requested_by) REFERENCES person(roll_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_action_for FOREIGN KEY (action_for) REFERENCES person(roll_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_rule_id_request_access FOREIGN KEY (rule_id) REFERENCES rule(rule_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


drop table if exists request_reviewer;
CREATE TABLE request_reviewer (
    request_id INT NOT NULL,
    reviewer_roll_no VARCHAR(50) NOT NULL,
    decision ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
    role enum('Reviewer','Executer') ,
    CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES request_access(request_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_reviewer FOREIGN KEY (reviewer_roll_no) REFERENCES person(roll_no)
        ON DELETE CASCADE
);



drop table if exists notification;
create table notification(
	notify_id int auto_increment primary key,
    requested_by varchar (100),
    action varchar(100),
    status enum('Reviewed','Executed','Rejected'),
    reviewed_by varchar(100),
    notify_date date default (current_date),
    notify_time time default (current_time)
);
select* from notification;

drop table if exists rule_work_flow;
create table rule_work_flow(
	rule_id int ,
	incharge varchar(100) ,
    role enum('Reviewer','Executer'),
    constraint fk_incharge foreign key (incharge) references person (roll_no)
		on delete set null
        on update cascade,
     constraint fk_rule_id_rule_work_flow foreign key(rule_id) references rule (rule_id)
		on delete 	cascade
        on update  cascade
);
insert into rule_work_flow (rule_id,incharge,role)
values(1,'zohoTeacher1','Reviewer'),
(1,'zohoTeacher1','Executer');


drop table if exists rule_condition;
CREATE TABLE rule_condition (
    rule_id INT,
    attribute VARCHAR(100) NOT NULL,
    operator VARCHAR(10) NOT NULL,
    value VARCHAR(100) NOT NULL,
    logic_op ENUM('AND','OR') DEFAULT 'AND',  
    order_id int not null,
    CONSTRAINT fk_rule FOREIGN KEY (rule_id) REFERENCES rule(rule_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


drop table  if exists audit_logs;
CREATE TABLE audit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    event VARCHAR(100) NOT NULL,
    reg VARCHAR(100) DEFAULT '',
    log_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    log_time TIME NOT NULL DEFAULT (CURRENT_TIME),
    CONSTRAINT fk_user_login_roll_no 
        FOREIGN KEY (username) REFERENCES person(roll_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);





set sql_safe_updates=0;
select * from role;
select * from person order by role_id;
select * from audit_logs;
delete from audit_logs;

select * from request_access;
select * from request_reviewer;

select * from rule_work_flow;
select * from rule;

ALTER TABLE audit_logs AUTO_INCREMENT = 1



