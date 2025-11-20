FROM debian:bookworm

ENV NAGIOS_VERSION=4.5.7
ENV PLUGINS_VERSION=2.4.6

RUN apt-get update && apt-get install -y \
    build-essential wget apache2 php gcc make supervisor \
    libgd-dev libssl-dev unzip libc6-dev libperl-dev \
    libapache2-mod-php php-gd && \
    apt-get clean

# Create user + group for Nagios
RUN useradd nagios && groupadd nagcmd && \
    usermod -a -G nagcmd nagios && \
    usermod -a -G nagcmd www-data

# Install Nagios Core
RUN wget https://assets.nagios.com/downloads/nagioscore/releases/nagios-${NAGIOS_VERSION}.tar.gz && \
    tar xzf nagios-${NAGIOS_VERSION}.tar.gz && \
    cd nagios-${NAGIOS_VERSION} && \
    ./configure --with-command-group=nagcmd && \
    make all && \
    make install && \
    make install-commandmode && \
    make install-init && \
    make install-config && \
    make install-webconf

# Install Nagios Plugins
RUN wget https://nagios-plugins.org/download/nagios-plugins-${PLUGINS_VERSION}.tar.gz && \
    tar xzf nagios-plugins-${PLUGINS_VERSION}.tar.gz && \
    cd nagios-plugins-${PLUGINS_VERSION} && \
    ./configure --with-nagios-user=nagios --with-nagios-group=nagios && \
    make && make install

# Enable Apache modules
RUN a2enmod cgi rewrite

# Create Nagios admin user
RUN echo "nagiosadmin:$(openssl passwd -apr1 admin)" > /usr/local/nagios/etc/htpasswd.users

# Copy Supervisor config
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

EXPOSE 80

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]

