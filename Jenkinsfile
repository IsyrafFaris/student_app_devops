pipeline {
    agent any

    environment {
        APP_NAME = 'student-app'
        HOST_PORT = '8081'
        CONTAINER_PORT = '8080'
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

        stage('Cleanup Old Container') {
            steps {
                bat '''
                    echo Cleaning up existing container...
                    docker ps -a --filter "name=student-app" --format "{{.ID}}" > container_id.txt
                    set /p CONTAINER_ID=<container_id.txt
                    if defined CONTAINER_ID (
                        docker stop %CONTAINER_ID% 2>nul
                        docker rm %CONTAINER_ID% 2>nul
                        echo Removed existing container
                    ) else (
                        echo No existing container found
                    )
                    del container_id.txt 2>nul
                '''
            }
        }

        stage('Run Container') {
            steps {
                bat "docker run -d -p ${HOST_PORT}:${CONTAINER_PORT} --name student-app ${APP_NAME}:latest"
                bat 'echo Container started successfully on port 8081'
            }
        }
        
        stage('Verify Container') {
            steps {
                bat 'timeout /t 3 /nobreak > nul'
                bat 'docker ps --filter "name=student-app"'
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished'
        }
        success {
            echo " Build and deployment successful! App running on http://localhost:8081"
        }
        failure {
            echo ' Pipeline failed. Check logs above.'
        }
    }
}