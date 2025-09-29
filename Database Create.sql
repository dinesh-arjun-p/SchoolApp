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
     superior varchar(100) null,
     constraint fk_superior foreign key (superior) references person (roll_no)
     on delete set null
     on update cascade,
     foreign key (role_id)references role(role_id)
     on delete set null
     on update cascade
);
insert into person(roll_no,name,pass,email,role_id)
values('zohoAdmin1','Admin','admin','dinesharjun.ec22@bitsathy.ac.in',1);
select * from person;


drop table if exists rule;
create table rule(
	rule_id int auto_increment primary key,
    status_limit int,
    priority int not null 
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
    rule_id INT,
    status INT DEFAULT 0,  -- count of approvals
    role ENUM('Reviewer','Executer') DEFAULT 'Reviewer',
    state enum('Pending','Executed','Rejected') default 'Pending',
    CONSTRAINT fk_requested_by FOREIGN KEY (requested_by) REFERENCES person(roll_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_action_for FOREIGN KEY (action_for) REFERENCES person(roll_no)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_rule_id_request_access FOREIGN KEY (rule_id) REFERENCES rule(rule_id)
        ON DELETE set null
        ON UPDATE CASCADE
);




drop table if exists request_reviewer;
CREATE TABLE request_reviewer (
    request_id INT NOT NULL,
    reviewer_roll_no VARCHAR(50) NOT NULL,
    decision ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
    role enum('Reviewer','Executer') ,
    updated enum('yes','no') default 'yes',
    CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES request_access(request_id)
        ON DELETE CASCADE
        on update cascade
    
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
    active_status enum('Active','Inactive','Deleted')default 'Active',
     constraint fk_rule_id_rule_work_flow foreign key(rule_id) references rule (rule_id)
		on delete 	cascade
        on update  cascade
);
insert into rule_work_flow (rule_id,incharge,role)values
(1,'zohoTeacher1','Reviewer'),
(1,'zohoTeacher1','Executer');




drop table if exists rule_condition;
CREATE TABLE rule_condition (
    rule_id INT,
    attribute VARCHAR(100) NOT NULL,
    operator enum('is','is not','contains') NOT NULL,
    value VARCHAR(100) NOT NULL,
    logic_op ENUM('AND','OR') DEFAULT 'AND',  
    order_id int not null,
    CONSTRAINT fk_rule FOREIGN KEY (rule_id) REFERENCES rule(rule_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
	CONSTRAINT fk_attribute FOREIGN KEY (attribute) REFERENCES attribute(attribute)
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
    log_time TIME NOT NULL DEFAULT (CURRENT_TIME)
);

drop table if exists attribute;
create table attribute(
	attribute varchar(100) primary key
);
insert into attribute values('action'),('role'),('name'),('superior'),('class');
drop table if exists operator;
create table operator(
	operator varchar(50) primary key
);
insert into operator values('is'),('is not'),('contains');

drop table if exists attribute_operator;
create table attribute_operator(
	attribute varchar(100) ,
    attribute_operator varchar(100),
    constraint fk_attribute_operator_attribute foreign key (attribute) references attribute(attribute)
    on delete cascade
    on update cascade,
    constraint fk_attribute_operator_operator foreign key (attribute_operator) references operator(operator)
    on delete cascade
    on update cascade
);
insert into attribute_operator  (attribute, attribute_operator)
select 'action',operator from operator;
insert into attribute_operator  (attribute, attribute_operator)
select 'role',operator from operator;
insert into attribute_operator  (attribute, attribute_operator)
select 'superior',operator from operator;
insert into attribute_operator  (attribute, attribute_operator)
select 'name',operator from operator;
insert into attribute_operator  (attribute, attribute_operator)
select 'class',operator from operator where operator <> 'contains';


drop table if exists attribute_value;
create table attribute_value(
	id int  auto_increment primary key,
	attribute varchar(100) ,
    attribute_value varchar(100),
    constraint fk_attribute_value_attribute foreign key (attribute) references attribute(attribute)
    on delete cascade
    on update cascade
);
insert into attribute_value (attribute, attribute_value)values('action','changePhoneNumber'),('action','changeClass'),('action','changeName');
insert into attribute_value (attribute, attribute_value) values ('class','1'),('class','2'),('class','3'),('class','4'),
('class','5');
INSERT INTO attribute_value (attribute, attribute_value)
SELECT 'role', role_name FROM role;
INSERT INTO attribute_value (attribute, attribute_value)
SELECT 'name', name FROM person;
INSERT INTO attribute_value (attribute, attribute_value)
SELECT 'superior', roll_no FROM person where role_id<3;


drop trigger if exists update_name_in_attribute_value;
DELIMITER $$

CREATE TRIGGER trg_person_after_insert
AFTER INSERT ON person
FOR EACH ROW
BEGIN

    INSERT INTO attribute_value (attribute, attribute_value)
    VALUES ('name', NEW.name);


    IF NEW.role_id < 3 THEN
        INSERT INTO attribute_value (attribute, attribute_value)
        VALUES ('superior', NEW.roll_no);
    END IF;
END$$

DELIMITER ;

drop trigger if exists trg_person_after_delete;
DELIMITER $$

CREATE TRIGGER trg_person_after_delete
after delete ON person
FOR EACH ROW
BEGIN
	DELETE FROM attribute_value
    WHERE attribute = 'name'
      AND attribute_value = OLD.name
    LIMIT 1;
    
    IF old.role_id < 3 THEN
        delete from attribute_value where attribute='superior' and attribute_value=old.roll_no;
    END IF;
END$$

DELIMITER ;

drop trigger if exists trg_person_after_update;
DELIMITER $$

CREATE TRIGGER trg_person_after_update
after update ON person
FOR EACH ROW
BEGIN
	update  attribute_value set
    attribute_value = new.name
    WHERE attribute = 'name'
      AND attribute_value = OLD.name
    LIMIT 1;
    
END$$

DELIMITER ;



use school;
set sql_safe_updates=0;
select * from role;
select * from person order by role_id;
select * from audit_logs order by id desc;

select * from request_access;
select * from request_reviewer;

select * from rule_work_flow;
select * from rule;
select * from rule_condition;
select * from attribute;
select * from operator;
select * from attribute_operator;
select * from attribute_value;

delete from request_access;

ALTER TABLE request_access AUTO_INCREMENT = 1;







SELECT * FROM request_access WHERE rule_id =1  and state='Pending';





drop trigger if exists set_default_rule_id;
DELIMITER $$

CREATE TRIGGER set_default_rule_id
BEFORE DELETE ON rule
FOR EACH ROW
BEGIN
    DECLARE limit_val INT;

    SELECT status_limit 
    INTO limit_val
    FROM rule
    WHERE rule_id = 1;


    UPDATE request_access
    SET rule_id = 1,
        status = 0,
        role = CASE 
                   WHEN limit_val = 0 THEN 'executor'
                   ELSE 'reviewer'
               END
    WHERE rule_id = OLD.rule_id;
END$$

DELIMITER ;






