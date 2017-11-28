
drop database if exists library;

create database if not exists LIBRARY;

use library;

drop table if exists book;

CREATE TABLE BOOK (
    Isbn VARCHAR(10) NOT NULL,
    Title VARCHAR(1000) NOT NULL DEFAULT 'NA',
    PRIMARY KEY (Isbn)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

-- 
-- Loading data from books.csv into table book
--

LOAD DATA LOCAL INFILE  'C:/users/tr3nzu/eclipse-workspace/DBProject/books.csv'
INTO TABLE book
IGNORE 1 rows
(ISBN, @ignore, Title,@ignore);

-- select * from book;
drop table if exists library_branch;
CREATE TABLE library_branch (
    branch_id INT(11) NOT NULL,
    branch_name VARCHAR(100) DEFAULT NULL,
    address VARCHAR(2083) DEFAULT NULL,
    PRIMARY KEY (`branch_id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;


--
-- Loading data into library_branch
--

LOAD DATA LOCAL  INFILE  'C:/users/tr3nzu/eclipse-workspace/DBProject/library_branch.csv'
INTO TABLE library_branch
IGNORE 1 rows
(branch_id,branch_name,address);

-- select * from library_branch;

-- From library_db tables for author and book_authors
drop temporary table if exists authors_2;
create temporary table AUTHORS_2(Isbn varchar(10) NOT NULL,author_name varchar(255) NOT NULL, primary key(Isbn));

LOAD DATA LOCAL INFILE  'C:/users/tr3nzu/eclipse-workspace/DBProject/books.csv'
INTO TABLE authors_2
IGNORE 1 rows
(ISBN, @ignore, @ignore,author_name);

-- select * from authors_2;
-- select * from book_authors;
-- select * from authors_4 join book_authors  on isbn=book_id and authors_4.author_name != book_authors.author_name;

drop temporary table if exists authors_3;
create temporary table AUTHORS_3(Isbn varchar(10) NOT NULL,author_name varchar(255) NOT NULL);
insert ignore into authors_3
select
  authors_2.isbn,
  SUBSTRING_INDEX(SUBSTRING_INDEX(authors_2.author_name, ',', numbers.n), ',', -1) author_name
from
  (select distinct 1 n union all
   select distinct 2 union all select 3 union all
   select distinct 4 union all select 5) numbers INNER JOIN authors_2
  on CHAR_LENGTH(authors_2.author_name)
     -CHAR_LENGTH(REPLACE(authors_2.author_name, ',', ''))>=numbers.n-1
order by
  ISBN, n;

-- select * from authors_3;

drop temporary table if exists authors_4;

create temporary table AUTHORS_4(Isbn varchar(10) NOT NULL,author_name varchar(255) NOT NULL);

insert into authors_4
SELECT isbn,author_name FROM authors_3 GROUP BY isbn,author_name;

alter table authors_4 add author_id int auto_increment primary key;

-- alter table authors_4 drop column title;

alter table authors_4 add title varchar(10) default null;

alter table authors_4 
add column fname varchar(15),
add column mname varchar(15) default null,
add column lname varchar(15),
add column suffix varchar(10) default null after title;

-- select * from authors_4;

drop temporary table if exists authors_5;

CREATE temporary TABLE Authors_5 (
  author_id int(11) NOT NULL AUTO_INCREMENT,
  fullname varchar(1000) NOT NULL,
  title varchar(100) DEFAULT NULL,
  fname varchar(100) DEFAULT NULL,
  mname varchar(100) DEFAULT NULL,
  lname varchar(100) DEFAULT NULL,
  suffix varchar(50) Default null,
  PRIMARY KEY (author_id)
);

insert into authors_5
select
  min(authors_4.author_id), authors_4.author_name, authors_4.title, SUBSTRING_INDEX(SUBSTRING_INDEX(author_name, ' ', 1), ' ', -1) AS fname,
   If(  length(author_name) - length(replace(author_name, ' ', ''))>1,  
       SUBSTRING_INDEX(SUBSTRING_INDEX(author_name, ' ', 2), ' ', -1) ,NULL) 
           as mname,
   SUBSTRING_INDEX(SUBSTRING_INDEX(author_name, ' ', 3), ' ', -1) AS lname, authors_4.suffix
FROM authors_4 group by authors_4.author_name;

drop temporary table if exists book_authors_1;

CREATE temporary TABLE BOOK_AUTHORS_1 (
  Isbn varchar(10) NOT NULL,
  author_id int(11) NOT NULL,
  PRIMARY KEY (`Isbn`,`author_id`));
-- CONSTRAINT `book_authors_fk1` FOREIGN KEY (`Isbn`) REFERENCES `BOOK` (`Isbn`))  ENGINE=INNODB DEFAULT CHARSET=UTF8;
-- CONSTRAINT `book_authors_fk2` FOREIGN KEY (`author_id`) REFERENCES `authors_5` (`author_id`)

insert into BOOK_AUTHORS_1
select
  authors_4.isbn, authors_5.author_id
FROM authors_4 join authors_5 on authors_5.fullname = authors_4.author_name;

-- alter table book_authors_1 add CONSTRAINT `book_authors_fk1` FOREIGN KEY (`Isbn`) REFERENCES `BOOK` (`Isbn`);
-- alter table BOOK_AUTHORS_1 add CONSTRAINT `book_authors_fk2` FOREIGN KEY (`author_id`) REFERENCES `authors_5` (`author_id`);


--
-- Table structure for authors
--
DROP table if exists authors;
CREATE TABLE Authors (
  author_id int(11) NOT NULL AUTO_INCREMENT,
  fullname varchar(1000) NOT NULL,
  title varchar(100) DEFAULT NULL,
  fname varchar(100) DEFAULT NULL,
  mname varchar(100) DEFAULT NULL,
  lname varchar(100) DEFAULT NULL,
  suffix varchar(50) Default null,
  PRIMARY KEY (author_id)
);

--
-- Loading data into authors
--
insert into authors
select * from authors_5;

-- select * from authors;

--
-- Table structure for Book_Authors
--
DROP table if exists book_authors;
CREATE TABLE BOOK_AUTHORS (
  Isbn varchar(10) NOT NULL,
  author_id int(11) NOT NULL,
  PRIMARY KEY (`Isbn`,`author_id`),
  CONSTRAINT `book_authors_fk1` FOREIGN KEY (`Isbn`) REFERENCES `BOOK` (`Isbn`),
  CONSTRAINT `book_authors_fk2` FOREIGN KEY (`author_id`) REFERENCES `authors` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Loading data into book_authors
--
insert into book_authors
select * from book_authors_1;

-- select * from book_authors where author_id in (select author_id from authors where authors.fullname='Lewis Grizzard');
--
-- Table structure for book_cover
--
DROP table if exists book_cover;
CREATE TABLE BOOK_COVER (
    Isbn VARCHAR(10) NOT NULL,
    cover VARCHAR(2083) DEFAULT NULL,
    PRIMARY KEY (Isbn),
    CONSTRAINT `book_cover_fk` FOREIGN KEY (Isbn)
        REFERENCES `book` (Isbn)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

--
-- Loading data into book_cover
--

LOAD DATA LOCAL INFILE  'C:/users/tr3nzu/eclipse-workspace/DBProject/books.csv'
INTO TABLE book_cover
IGNORE 1 rows
(ISBN,@ignore,@ignore,@ignore, cover);

-- select * from book_cover;

--
-- Table structure for book_copies
--
DROP table if exists book_copies;
CREATE TABLE book_copies (
    Isbn VARCHAR(10) NOT NULL,
    branch_id INT(11) NOT NULL,
    no_of_copies INT(11) DEFAULT NULL,
    PRIMARY KEY (Isbn , branch_id),
    KEY `book_copies_fk2` (branch_id),
    CONSTRAINT `book_copies_fk1` FOREIGN KEY (`Isbn`)
        REFERENCES `book` (`Isbn`),
    CONSTRAINT `book_copies_fk2` FOREIGN KEY (`branch_id`)
        REFERENCES `library_branch` (`branch_id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

--
-- Loading data into book_copies
--

LOAD DATA LOCAL INFILE  'C:/users/tr3nzu/eclipse-workspace/DBProject/book_copies.csv'
INTO TABLE book_copies
IGNORE 1 rows
(ISBN, branch_id, no_of_copies);

-- select * from book_copies where no_of_copies = 0;

--
-- Table structure for borrower
--
DROP TABLE IF EXISTS borrower;
CREATE TABLE borrower (
    `card_no` INT(11) NOT NULL AUTO_INCREMENT,
    ssn VARCHAR(15) NOT NULL,
    `fname` VARCHAR(100) DEFAULT NULL,
    `lname` VARCHAR(100) DEFAULT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `address` VARCHAR(255) DEFAULT NULL,
    `city` VARCHAR(30) DEFAULT NULL,
    `state` VARCHAR(20) DEFAULT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    PRIMARY KEY (`card_no`)
)  ENGINE=INNODB AUTO_INCREMENT=000001 DEFAULT CHARSET=UTF8;

--
-- Loading data into borrower
--

LOAD DATA LOCAL INFILE  'C:/users/tr3nzu/eclipse-workspace/DBProject/borrowers.csv'
INTO TABLE borrower
FIELDS terminated by ','
IGNORE 1 rows
(@ignore,ssn,fname,lname,email,address,city,state,phone);

-- select * from borrower where card_no='000005';

--
-- Table structure for book_loans
--
DROP table if exists book_loans;
CREATE TABLE `book_loans` (
    Loan_id INT(15) NOT NULL AUTO_INCREMENT,
    Isbn VARCHAR(10) NOT NULL,
    `branch_id` INT(11) NOT NULL,
    `card_no` INT(11) NOT NULL,
    `date_out` DATE DEFAULT NULL,
    `due_date` DATE DEFAULT NULL,
    `date_in` DATE DEFAULT '1885-01-01',
    PRIMARY KEY (loan_id),
    CONSTRAINT `book_loans_fk1` FOREIGN KEY (`Isbn`)
        REFERENCES `book` (`Isbn`),
    CONSTRAINT `book_loans_fk2` FOREIGN KEY (`branch_id`)
        REFERENCES `library_branch` (`branch_id`),
    CONSTRAINT `book_loans_fk3` FOREIGN KEY (`card_no`)
        REFERENCES `borrower` (`card_no`)
)  ENGINE=INNODB AUTO_INCREMENT=0000001 DEFAULT CHARSET=UTF8;

--
-- Loading data into book_loans
--

LOAD DATA LOCAL INFILE  'C:/users/tr3nzu/eclipse-workspace/DBProject/book_loans.csv'
INTO TABLE book_loans
(@ignore,isbn,branch_id,card_no,date_out,due_date,date_in);

-- select * from book_loans;

--
-- Table structure for fines
--

-- select loan_id, datediff(date_in,due_date)*0.25 as date_1 from book_loans where due_date < date_in;
-- select * from book_loans where Loan_id = '93';

DROP table if exists FINES;
CREATE TABLE fines (
    Loan_id INT(15) NOT NULL,
    fine_amt DECIMAL(10,2),
    paid boolean default false,
    PRIMARY KEY (Loan_id),
    CONSTRAINT `fines_fk` FOREIGN KEY (`loan_id`)
        REFERENCES `book_loans` (`loan_id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

-- select * from fines;
-- `library`

insert into fines 
select b.Loan_id, if(datediff(b.date_in,b.due_date)>0,datediff(b.date_in,b.due_date)*0.25,0) as fine_amt, true from book_loans b;

delete from fines
where fine_amt =0;

select * from fines;
select * from book_loans where due_date<date_in;

-- select b.Loan_id, f.fine_amt, b.due_date, b.date_in from fines f join book_loans b on b.loan_id = f.loan_id;

-- select c.isbn, c.branch_id, c.no_of_copies, l.loan_id, l.isbn, l.branch_id from book_copies c left join book_loans l on c.isbn= l.isbn 
-- where c.branch_id = l.branch_id;