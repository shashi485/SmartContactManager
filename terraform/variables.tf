variable "mysql_root_password" {
  description = "Root password for MySQL database"
  type        = string
  default     = "1@Shashikant"
}

variable "mysql_database" {
  description = "MySQL database name"
  type        = string
  default     = "consma"
}

variable "image_name" {
  description = "Docker image name for the Spring Boot app"
  type        = string
  default     = "smartcontactmanager-pipeline-app"
}
