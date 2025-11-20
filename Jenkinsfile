pipeline {
    agent any

    environment {
        IMAGE_NAME = "smartcontactmanager-pipeline-app"
        //CONTAINER_NAME = "smart-contact-manager"
        //COMPOSE_FILE = "docker-compose.yml"
        TERRAFORM_DIR = "terraform"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git 'https://github.com/shashi485/SmartContactManager.git'
            }
        }

        stage('Build JAR (Skip Tests)') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $IMAGE_NAME .'
            }
        }
        
        stage('Terraform Init') {
            steps {
                dir("${TERRAFORM_DIR}") {
                    sh "terraform init"
                }
            }
        }

        stage('Terraform Plan') {
            steps {
                dir("${TERRAFORM_DIR}") {
                    sh "terraform plan"
                }
            }
        }

        stage('Terraform Apply') {
            steps {
                dir("${TERRAFORM_DIR}") {
                    sh "terraform apply -auto-approve"
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    // Simple retry logic to wait for the app to be ready
                    retry(5) {
                        sh 'sleep 5'
                        sh 'curl --fail http://localhost:8085 || exit 1'
                    }
                }
            }
        }
    }

    post {
        success {
            echo '✅ Deployment successful!'
        }
        failure {
            echo '❌ Deployment failed.'
        }
        always {
            echo '🔁 CI/CD pipeline run completed.'
        }
    }
}
