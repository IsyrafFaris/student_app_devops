pipeline {
    agent any

    environment {
        APP_NAME = 'student-app'
    }

    stages {
        stage('Clone') {
            steps {
                git branch: 'main', url: 'https://github.com/IsyrafFaris/student_app_devops.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                bat 'echo "Running basic tests..."'
                bat 'mvn test'
            }
        }

        stage('Docker Build') {
            steps {
                bat "docker build -t ${APP_NAME}:latest ."
            }
        }

        stage('Run Container') {
            steps {
                bat 'docker stop student-app || ver > nul'
                bat 'docker rm student-app || ver > nul'
                bat "docker run -d -p 8081:8080 --name student-app ${APP_NAME}:latest"
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished'
        }
        success {
            echo '✅ Build and deployment successful!'
        }
    }
}
