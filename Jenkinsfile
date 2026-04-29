pipeline {
    agent any

    environment {
        // Application settings
        APP_NAME = 'student-app'
        DOCKER_IMAGE = 'student-app:latest'
        
        // Kubernetes settings
        K8S_DEPLOYMENT = 'springboot-app'
        K8S_SERVICE = 'springboot-app-service'
        K8S_NAMESPACE = 'default'
        NODE_PORT = '30082'
        
        // Port configurations
        APP_PORT = '8082'
    }

    stages {
        stage('📦 Clone Repository') {
            steps {
                echo 'Cloning repository from GitHub...'
                git branch: 'main', 
                    url: 'https://github.com/IsyrafFaris/student_app_devops.git'
                echo 'Repository cloned successfully'
            }
        }

        stage('🔨 Build Application') {
            steps {
                echo 'Building Spring Boot application with Maven...'
                bat 'mvn clean package -DskipTests'
                echo 'Build completed successfully'
            }
        }

        stage('🧪 Run Tests') {
            steps {
                echo 'Running unit tests...'
                bat 'mvn test'
                echo 'All tests passed'
            }
        }

        stage('🐳 Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                bat "docker build -t ${DOCKER_IMAGE} ."
                echo 'Docker image built successfully'
            }
        }

        stage('💾 Load Image to Minikube') {
            steps {
                echo 'Loading Docker image to Minikube cluster...'
                bat '''
                    minikube image load student-app:latest
                    if %errorlevel% neq 0 (
                        echo Failed to load image to Minikube
                        exit /b 1
                    )
                '''
                echo 'Image loaded to Minikube successfully'
            }
        }

        stage('🗑️ Cleanup Old Kubernetes Resources') {
            steps {
                echo 'Cleaning up existing Kubernetes resources...'
                bat '''
                    echo Deleting old deployment if exists...
                    kubectl delete deployment %K8S_DEPLOYMENT% --ignore-not-found=true
                    
                    echo Deleting old service if exists...
                    kubectl delete service %K8S_SERVICE% --ignore-not-found=true
                    
                    echo Cleanup completed
                '''
            }
        }

        stage('🚀 Deploy to Kubernetes') {
            steps {
                echo 'Deploying application to Kubernetes...'
                
                // Create deployment.yaml
                bat '''
                    echo Creating deployment.yaml...
                    (
                        echo apiVersion: apps/v1
                        echo kind: Deployment
                        echo metadata:
                        echo   name: springboot-app
                        echo spec:
                        echo   replicas: 2
                        echo   selector:
                        echo     matchLabels:
                        echo       app: springboot-app
                        echo   template:
                        echo     metadata:
                        echo       labels:
                        echo         app: springboot-app
                        echo     spec:
                        echo       containers:
                        echo       - name: springboot-app
                        echo         image: student-app:latest
                        echo         imagePullPolicy: Never
                        echo         ports:
                        echo         - containerPort: 8082
                        echo         env:
                        echo         - name: SERVER_PORT
                        echo           value: "8082"
                    ) > deployment.yaml
                '''
                
                // Create service.yaml
                bat '''
                    echo Creating service.yaml...
                    (
                        echo apiVersion: v1
                        echo kind: Service
                        echo metadata:
                        echo   name: springboot-app-service
                        echo spec:
                        echo   type: NodePort
                        echo   selector:
                        echo     app: springboot-app
                        echo   ports:
                        echo     - port: 8082
                        echo       targetPort: 8082
                        echo       nodePort: 30082
                    ) > service.yaml
                '''
                
                // Apply Kubernetes configurations
                bat '''
                    echo Applying Kubernetes deployment...
                    kubectl apply -f deployment.yaml
                    
                    echo Applying Kubernetes service...
                    kubectl apply -f service.yaml
                '''
                
                echo 'Kubernetes resources created successfully'
            }
        }

        stage('⏳ Wait for Deployment') {
            steps {
                echo 'Waiting for deployment to be ready...'
                bat '''
                    echo Waiting for pods to start...
                    timeout /t 5 /nobreak > nul
                    
                    echo Checking deployment status...
                    kubectl rollout status deployment/springboot-app --timeout=120s
                    
                    if %errorlevel% neq 0 (
                        echo Deployment failed
                        kubectl describe pods
                        exit /b 1
                    )
                '''
                echo 'Deployment is ready'
            }
        }

        stage('✅ Verify Deployment') {
    steps {
        echo 'Verifying Kubernetes deployment...'
        bat '''
            echo ========================================
            echo PODS STATUS:
            echo ========================================
            kubectl get pods
            
            echo.
            echo ========================================
            echo SERVICES STATUS:
            echo ========================================
            kubectl get svc
            
            echo.
            echo ========================================
            echo DEPLOYMENT DETAILS:
            echo ========================================
            kubectl get deployment springboot-app
            
            echo.
            echo ========================================
            echo ACCESS INFORMATION:
            echo ========================================
            echo Application is accessible at:
            echo - NodePort: 30082
            echo - URL: http://localhost:30082
            echo.
            
            echo Waiting for application to be ready...
            timeout /t 10 /nobreak > nul
            
            echo Testing application endpoint...
            
            REM Test root endpoint
            echo Testing http://localhost:30082/
            curl -s -o nul -w "%%{http_code}" http://localhost:30082/ > status.txt
            set /p STATUS=<status.txt
            if "%STATUS%"=="200" (
                echo ✓ Application is responding on root endpoint
            ) else (
                echo Application returned status: %STATUS%
            )
            
            REM Test health endpoint if available
            echo Testing http://localhost:30082/actuator/health
            curl -s -o nul -w "%%{http_code}" http://localhost:30082/actuator/health > status2.txt 2>nul
            set /p STATUS2=<status2.txt
            if "%STATUS2%"=="200" (
                echo ✓ Health check passed
            ) else (
                echo Health endpoint not available (this is optional)
            )
            
            REM Show pod logs for debugging
            echo.
            echo ========================================
            echo POD LOGS (last 20 lines):
            echo ========================================
            kubectl logs --tail=20 -l app=springboot-app
        '''
    }
}

    post {
        always {
            echo '''
                ========================================
                PIPELINE EXECUTION COMPLETED
                ========================================
            '''
            echo "Build Number: ${env.BUILD_NUMBER}"
            echo "Job Name: ${env.JOB_NAME}"
        }
        
        success {
            echo '''
                ╔══════════════════════════════════════════╗
                ║  ✅ DEPLOYMENT SUCCESSFUL!               ║
                ╚══════════════════════════════════════════╝
            '''
            echo '''
                Application is now running on Kubernetes:
                - 2 replicas running
                - Exposed via NodePort 30082
                - Access at: http://localhost:30082
                
                To check pod status:
                kubectl get pods
                
                To view logs:
                kubectl logs -l app=springboot-app
                
                To scale the application:
                kubectl scale deployment springboot-app --replicas=3
            '''
        }
        
        failure {
            echo '''
                ╔══════════════════════════════════════════╗
                ║  ❌ DEPLOYMENT FAILED                    ║
                ╚══════════════════════════════════════════╝
            '''
            echo 'Pipeline failed. Debugging steps:'
            echo '1. Check pod logs: kubectl logs -l app=springboot-app'
            echo '2. Describe pods: kubectl describe pods'
            echo '3. Check events: kubectl get events'
            echo '4. Verify Minikube is running: minikube status'
            echo '5. Check if image is loaded: minikube image ls | grep student-app'
        }
        
        cleanup {
            echo 'Cleaning up temporary files...'
            bat '''
                if exist deployment.yaml del deployment.yaml
                if exist service.yaml del service.yaml
                exit 0
            '''
        }
    }
}