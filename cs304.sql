CREATE DATABASE project3

DROP TABLE borrower;
CREATE TABLE Borrower
(
	bid INT PRIMARY KEY,
	password VARCHAR (30) NOT NULL,
	name VARCHAR (30) NOT NULL,
	address VARCHAR (50) NOT NULL,
	phone INT NOT NULL,
	emailAddress VARCHAR (30),
	sinOrStNo INT NOT NULL,
	expiryDATE DATE NOT NULL,
	type VARCHAR (8) NOT NULL
);

DROP TABLE BorrowerType;
CREATE TABLE BorrowerType 
(
	type VARCHAR (8) PRIMARY KEY,
	bookTimeLimit DATE NOT NULL
);

DROP TABLE Book;
CREATE TABLE Book 
(
	callNumber VARCHAR (16) PRIMARY KEY, 
	isbn INT NOT NULL,
	title VARCHAR (50) NOT NULL,
	mainAuthor VARCHAR (40) NOT NULL,
	publisher VARCHAR (30) NOT NULL,
	year INT NOT NULL
);

DROP TABLE hasAuthor;
CREATE TABLE hasAuthor 
(
	callNumber VARCHAR (16),
	name VARCHAR (30),
	PRIMARY KEY( callNumber, name )
);

DROP TABLE hasSubject;
CREATE TABLE hasSubject
(
	callNumber VARCHAR (16),
	subject VARCHAR (20),
	PRIMARY KEY( callNumber, subject )
);

DROP TABLE BookCopy;
CREATE TABLE BookCopy
(
	callNumber VARCHAR (16),
	copyNo VARCHAR (3),
	status VARCHAR (7) NOT NULL,
	PRIMARY KEY( callNumber, copyNo )
);

DROP TABLE HoldRequest;
CREATE TABLE HoldRequest
(
	hid INT PRIMARY KEY,
	bid INT NOT NULL,
	callNumber VARCHAR (16) NOT NULL,
	issuedDate DATE NOT NULL
);

DROP TABLE Borrowing;
CREATE TABLE Borrowing
(
	borid INT PRIMARY KEY,
	bid INT NOT NULL,
	callNumber VARCHAR (16) NOT NULL,
	copyNo VARCHAR (3) NOT NULL,
	outDate DATE NOT NULL,
	inDate DATE NOT NULL
);

DROP TABLE Fine;
CREATE TABLE Fine
(
	fid INT PRIMARY KEY,
	amount INT NOT NULL,
	issuedDate DATE NOT NULL,
	paidDate DATE,
	borid INT NOT NULL
);

DROP SEQUENCE bid_counter;
CREATE SEQUENCE bid_counter
START WITH 1;

DROP SEQUENCE borid_counter;
CREATE SEQUENCE borid_counter
START WITH 1;

DROP SEQUENCE hid_counter;
CREATE SEQUENCE hid_counter
START WITH 1;

DROP SEQUENCE fid_counter;
CREATE SEQUENCE fid_counter
START WITH 1;
