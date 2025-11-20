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


# Spring Boot App Image

resource "docker_image" "scm_app" {
  name = "smartcontactmanager-app"

  build {
    context    = "../"
    dockerfile = "Dockerfile"
  }
}


# Spring Boot App Container

resource "docker_container" "scm" {
  name  = "smart-contact-manager"
  image = var.image_name

  depends_on = [
    docker_container.mysql
  ]

  env = [
    "SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/consma",
    "SPRING_DATASOURCE_USERNAME=root",
    "SPRING_DATASOURCE_PASSWORD=${var.mysql_root_password}"
  ]

  ports {
    internal = 8080
    external = 8085
  }

  networks_advanced {
    name = docker_network.scm_network.name
  }
}


# Outputs

output "app_url" {
  value = "http://localhost:8085"
}

output "mysql_port" {
  value = "3307"
}

  
  

