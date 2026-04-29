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
                    echo Cleaning up containers...
                    REM Stop and remove container if exists
                    docker stop student-app 2>nul
                    docker rm student-app 2>nul
                    
                    REM Find and kill any container using port 8081
                    for /f "tokens=*" %%i in ('docker ps -q --filter "publish=8081"') do (
                        echo Stopping container %%i that uses port 8081
                        docker stop %%i 2>nul
                        docker rm %%i 2>nul
                    )
                    
                    echo Cleanup completed
                '''
            }
        }

        stage('Run Container') {
            steps {
                bat "docker run -d -p ${HOST_PORT}:${CONTAINER_PORT} --name student-app ${APP_NAME}:latest"
                bat 'echo Container started on port 8081'
            }
        }
        
        stage('Verify Container') {
            steps {
                bat 'timeout /t 3 /nobreak > nul'
                bat 'docker ps --filter "name=student-app"'
                bat 'echo App is running on http://localhost:8081'
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
        failure {
            echo '❌ Pipeline failed. Check logs above.'
        }
    }
}