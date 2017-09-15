#!/bin/bash 

# Postgresql
/etc/init.d/postgresql start

# Apache (MUST BE EXECUTED AT LAST!)
/usr/sbin/apache2ctl -D FOREGROUND;