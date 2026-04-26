# Docker Network

resource "docker_network" "scm_network" {
  name = "scm-network"
}


# MySQL Image

resource "docker_image" "mysql_image" {
  name = "mysql:8"
}


# MySQL Container

resource "docker_container" "mysql" {
  name  = "mysql-db"
  image = docker_image.mysql_image.name

  env = [
    "MYSQL_ROOT_PASSWORD=${var.mysql_root_password}",
    "MYSQL_DATABASE=${var.mysql_database}"
  ]

  networks_advanced {
    name = docker_network.scm_network.name
  }

  ports {
    internal = 3306
    external = 3307
  }

  volumes {
    host_path      = "${path.cwd}/data/mysql"
    container_path = "/var/lib/mysql"
  }
}


# Spring Boot App Container (uses Jenkins image)

resource "docker_container" "scm" {
  name  = "smart-contact-manager"
  image = var.image_name   # Jenkins pipeline image

  depends_on = [
    docker_container.mysql
  ]

  env = [
    "SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/${var.mysql_database}",
    "SPRING_DATASOURCE_USERNAME=root",
    "SPRING_DATASOURCE_PASSWORD=${var.mysql_root_password}"
  ]

  networks_advanced {
    name = docker_network.scm_network.name
  }

  ports {
    internal = 8080
    external = 8085
  }
}


# Nagios Image


resource "docker_image" "nagios_image" {
  name = "jasonrivers/nagios:latest"
}


# Nagios Container


resource "docker_container" "nagios" {
  name  = "nagios"
  image = docker_image.nagios_image.name

  depends_on = [
    docker_container.mysql,
    docker_container.scm
  ]

  networks_advanced {
    name = docker_network.scm_network.name
  }

  ports {
    internal = 80
    external = 9086
  }

  env = [
    "NAGIOSADMIN_USER=nagiosadmin",
    "NAGIOSADMIN_PASS=admin"
  ]

  volumes {
    host_path      = "${path.cwd}/nagios/etc"
    container_path = "/opt/nagios/etc"
  }

  volumes {
    host_path      = "${path.cwd}/nagios/var"
    container_path = "/opt/nagios/var"
  }
}


# Outputs

output "mysql_url" {
  value = "mysql://root:${var.mysql_root_password}@localhost:3307/${var.mysql_database}"
}

output "app_url" {
  value = "http://localhost:8085"
}

output "nagios_url" {
  value = "http://localhost:9086"
}
your client requires real time alerts whenever a web server goes down or cpu usage exceeds 85%.how can nagios be configured to monitor these parameters and send alerts to administrator.
