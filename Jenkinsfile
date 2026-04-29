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
        bat 'echo "Cleaning up existing container..."'
        bat 'docker ps -a --filter "name=student-app" --format "{{.ID}}" > container_id.txt'
        bat 'for /f %i in (container_id.txt) do docker stop %i || ver > nul'
        bat 'for /f %i in (container_id.txt) do docker rm %i || ver > nul'
        bat 'del container_id.txt'
        bat 'echo "Starting new container on port 8081..."'
        bat "docker run -d -p 8081:8080 --name student-app student-app:latest"
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
