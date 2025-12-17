pipeline {
    agent any
    environment {
        DOCKERHUB_CREDENTIALS = "dockerhub-cred"
        IMAGE_NAME = "khoukhaaaaa/student-management"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        DOCKER_BUILDKIT = '0'
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

        stage('Deploy to Kubernetes') {
            steps {
                bat """
                    vagrant upload k8s /tmp/k8s-manifests
                    vagrant ssh -c "kubectl apply -n devops -f /tmp/k8s-manifests"
                """
            }
        }
    }

    post {
        success {
            echo "✅ Déploiement Kubernetes réussi"
        }
        failure {
            echo "❌ Échec du pipeline"
        }
    }
}
