pipeline {
    agent any
    environment {
        DOCKERHUB_CREDENTIALS = "dockerhub-cred"
        IMAGE_NAME = "khoukhaaaaa/student-management"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        DOCKER_BUILDKIT = '0'
        KUBECONFIG = "C:/Vagrant-ESPRIT/.kube/config"  // IMPORTANT
    }
    tools {
        maven 'M3'
        jdk 'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Unit Tests') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    // Vérifier si SonarQube est accessible
                    def sonarStatus = bat(script: 'curl -s -o /dev/null -w "%{http_code}" http://localhost:9000', returnStdout: true).trim()
                    if (sonarStatus != "200") {
                        echo "⚠️ SonarQube n'est pas accessible. Déploiement dans Kubernetes..."
                        bat """
                            kubectl apply -f k8s/sonarqube.yaml -n devops
                            timeout /t 60 /nobreak
                        """
                    }
                }
                withCredentials([string(credentialsId: 'sonar-token-student', variable: 'SONAR_TOKEN')]) {
                    bat """
                        mvn sonar:sonar ^
                        -Dsonar.projectKey=Student-Management-Khaoula ^
                        -Dsonar.projectName="Student Management - Khaoula" ^
                        -Dsonar.host.url=http://localhost:9000 ^
                        -Dsonar.token=%SONAR_TOKEN%
                    """
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                bat "docker build -t %IMAGE_NAME%:%IMAGE_TAG% ."
                bat "docker tag %IMAGE_NAME%:%IMAGE_TAG% %IMAGE_NAME%:latest"
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: DOCKERHUB_CREDENTIALS,
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                    bat "docker push %IMAGE_NAME%:%IMAGE_TAG%"
                    bat "docker push %IMAGE_NAME%:latest"
                }
            }
        }

        stage('Deploy Database') {
            steps {
                bat """
                    kubectl apply -f k8s/mysql.yaml -n devops
                    timeout /t 30 /nobreak
                    kubectl wait --for=condition=ready pod -l app=mysql -n devops --timeout=120s
                """
            }
        }

        stage('Deploy Application') {
            steps {
                bat """
                    kubectl apply -f k8s/spring-app.yaml -n devops
                    kubectl rollout status deployment/spring-deployment -n devops --timeout=120s
                """
            }
        }

        stage('Verification') {
            steps {
                script {
                    // Attendre que le service soit prêt
                    sleep 10
                    
                    // Récupérer l'URL du service
                    def serviceUrl = bat(script: 'minikube service spring-service -n devops --url', returnStdout: true).trim()
                    
                    // Tester l'application
                    bat """
                        curl -f %serviceUrl%/department/getAllDepartment
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Déploiement Kubernetes réussi"
            bat """
                kubectl get pods -n devops
                kubectl get svc -n devops
            """
        }
        failure {
            echo "❌ Échec du pipeline"
            bat """
                kubectl describe pods -n devops
                kubectl logs -l app=spring-app -n devops --tail=50
            """
        }
    }
}