FROM mysql:latest

ENV MYSQL_ROOT_PASSWORD=root

EXPOSE 3306
COPY mysql.conf /etc/mysql/my.cnf
RUN echo "CREATE DATABASE Baloot7;" > /docker-entrypoint-initdb.d/init.sql


