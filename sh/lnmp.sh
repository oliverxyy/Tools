#!/bin/bash
# update system
apt upgrade #Y
apt update
# install nginx
apt-add-repository ppa:nginx/development #ENTER
apt update
apt install nginx #Y
# install mysql phpmyadmin
apt install mysql-server mysql-client
#  Installing mysql-server these packages will be installed:
#  libaio1 libevent-core-2.0-5 mysql-client-5.7 mysql-client-core-5.7
#  mysql-common mysql-server mysql-server-5.7 mysql-server-core-5.7
apt install php7.0 php7.0-curl php7.0-dev php7.0-fpm php7.0-gd php7.0-mbstring php7.0-mcrypt php7.0-mysql php7.0-xml php7.0-zip #Y
#  Installing php7.0 these packages will be installed:
#  php-common php7.0-cli php7.0-common php7.0-json php7.0-opcache php7.0-readline
# autoremove
apt autoremove #Y














