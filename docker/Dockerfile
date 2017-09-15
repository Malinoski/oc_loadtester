FROM ubuntu:14.04.4

### TOOLS ###
RUN apt-get update && apt-get install -y \
        zip \
        wget \
        vim \
        software-properties-common \
        python-software-properties

RUN apt-get update && apt-get install -y locales && rm -rf /var/lib/apt/lists/* \
        && localedef -i pt_BR -c -f UTF-8 -A /usr/share/locale/locale.alias pt_BR.UTF-8
ENV LANG pt_BR.utf8

### POSTGRESQL ###
RUN \
        apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8; \
        echo "deb http://apt.postgresql.org/pub/repos/apt/ precise-pgdg main" > /etc/apt/sources.list.d/pgdg.list; \
        apt-get update; \
        apt-get install -y \
                python-software-properties \
                software-properties-common \
                postgresql-9.3 \
                postgresql-client-9.3 \
                postgresql-contrib-9.3

USER postgres

RUN \
        /etc/init.d/postgresql start &&\
        psql --command "CREATE USER admin WITH SUPERUSER PASSWORD 'admin';" &&\
        psql --command "ALTER USER admin CREATEDB;" &&\
        echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/9.3/main/pg_hba.conf &&\
        echo "listen_addresses='*'" >> /etc/postgresql/9.3/main/postgresql.conf

USER root

# ENTRYPOINT ["/usr/lib/postgresql/9.3/bin/postgres", "-D", "/var/lib/postgresql/9.3/main", "-c", "config_file=/etc/postgresql/9.3/main/postgresql.conf"]

### APACHE ###
RUN \
        apt-get --assume-yes install apache2; \
        sed -i '1s/^/ServerName localhost \n/' /etc/apache2/apache2.conf

### PHP ###
RUN \
        add-apt-repository ppa:ondrej/php &&\
        apt-get update && \
        apt-get install --assume-yes \
                php5.6 \
                apache2 \
                libapache2-mod-php5.6 \
                php5.6-pgsql \
                php5.6-gd \
                php5.6-json \
                php5.6-curl \
                php5.6-intl \
                php5.6-imagick \
                php5.6-mcrypt \
                php5.6-zip \
                php5.6-dom \
                php5.6-xmlwriter \
                php5.6-mbstring &&\
        echo "<?php echo 'Hello'; phpinfo();?>" > /var/www/html/phpinfo.php
        
### OWNCLOUD ###
RUN \
        wget https://download.owncloud.org/community/owncloud-8.1.12.tar.bz2 &&\
        chmod +x owncloud-8.1.12.tar.bz2 &&\
        tar -xjf owncloud-8.1.12.tar.bz2 -C /var/www/html/ &&\
        chown -R www-data:www-data /var/www/html/owncloud

# JAVA (for Gatling)
RUN apt-get install -y \
        openjdk-7-jre && \
        rm -rf /var/lib/apt/lists/*

# GATLING and OC_LOADTESTER
RUN \
        wget https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/2.1.6/gatling-charts-highcharts-bundle-2.1.6-bundle.zip &&\
        unzip gatling-charts-highcharts-bundle-2.1.6-bundle.zip -d /var/www/html/ &&\
        wget https://github.com/Malinoski/oc_loadtester/archive/master.zip &&\
        unzip master.zip -d /var/www/html/gatling-charts-highcharts-bundle-2.1.6/user-files/simulations

### FINAL CONFIG ###

EXPOSE 80
EXPOSE 5432

## To be executed when container start ##
ADD ./start.sh /start.sh
RUN chmod +x /start.sh
CMD ["/bin/bash", "/start.sh"]