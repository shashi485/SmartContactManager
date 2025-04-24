pipeline {
    agent any

    environment {
        IMAGE_NAME = "smartcontactmanager-pipeline-app"
        CONTAINER_NAME = "smart-contact-manager"
        COMPOSE_FILE = "docker-compose.yml"
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

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    // Stop and remove any existing containers
                    sh 'docker-compose down || true'
                    // Start containers
                    sh 'docker-compose up -d --build'
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
