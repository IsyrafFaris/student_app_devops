pipeline {
    agent any

    environment {
        APP_NAME = 'student-app'
        DOCKER_IMAGE = 'student-app:latest'
        K8S_DEPLOYMENT = 'springboot-app'
        K8S_SERVICE = 'springboot-app-service'
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
                bat "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Load Image to Minikube') {
            steps {
                bat '''
                    echo Loading Docker image to Minikube...
                    minikube image load student-app:latest
                    echo Image loaded successfully
                '''
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                bat '''
                    echo Applying Kubernetes deployment...
                    kubectl apply -f deployment.yaml
                    kubectl apply -f service.yaml
                    
                    echo Waiting for deployment to be ready...
                    kubectl rollout status deployment/springboot-app --timeout=120s
                    
                    echo Deployment completed successfully
                '''
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed'
        }
        success {
            bat '''
                echo.
                echo ========================================
                echo ✅ DEPLOYMENT SUCCESSFUL!
                echo ========================================
                echo Your application is now running on Kubernetes
                echo Access it at: http://localhost:30082
                echo.
                echo To see running pods:
                echo   kubectl get pods
                echo.
                echo To view application logs:
                echo   kubectl logs -l app=springboot-app
                echo ========================================
            '''
        }
        failure {
            bat '''
                echo.
                echo ========================================
                echo ❌ DEPLOYMENT FAILED
                echo ========================================
                echo Debugging steps:
                echo 1. Check pod status: kubectl get pods
                echo 2. View pod logs: kubectl logs -l app=springboot-app
                echo 3. Describe pods: kubectl describe pods
                echo 4. Check Minikube status: minikube status
                echo ========================================
            '''
        }
    }
}