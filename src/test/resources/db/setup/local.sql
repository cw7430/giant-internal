CREATE DATABASE giant;

CREATE DATABASE giant_test;

CREATE USER test_user WITH PASSWORD 'test1234';

\c giant_test

GRANT ALL PRIVILEGES ON DATABASE giant_test TO test_user;