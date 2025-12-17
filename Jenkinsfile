pipeline {
    agent any
    environment {
        DOCKERHUB_CREDENTIALS = "dockerhub-cred"
        IMAGE_NAME = "khoukhaaaaa/student-management"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        APP_PORT = "8082"
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
                echo "✅ Analyse SonarQube envoyée avec succès ! Résultat sur http://localhost:9000"
            }
        }
        
        stage('Package') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }
        
        stage('Verify Docker') {
            steps {
                bat 'docker version || echo "Docker non disponible ! Vérifie Docker Desktop sur Windows."'
                bat 'docker info || echo "Docker daemon ne répond pas."'
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
        
        stage('Deploy Locally') {
            steps {
                bat '''
                    docker stop student-management || echo "Aucun conteneur à arrêter"
                    docker rm student-management || echo "Aucun conteneur à supprimer"
                    docker run -d -p %APP_PORT%:8080 --name student-management %IMAGE_NAME%:latest
                '''
                echo "🚀 Application déployée ! Accès : http://localhost:%APP_PORT%"
            }
        }
        
        stage('Cleanup Old Images') {
            steps {
                bat 'docker image prune -f'
            }
        }
    }
    
    post {
        always {
            echo "Pipeline terminé - Khaoula Ben Slimane 💪"
        }
        success {
            echo "✅ SUCCÈS ! App sur http://localhost:%APP_PORT%"
            echo "🔍 SonarQube : http://localhost:9000"
            echo "🐳 DockerHub : https://hub.docker.com/r/khoukhaaaaa/student-management"
        }
        failure {
            echo "❌ Échec de la pipeline. Vérifie les logs, surtout Docker."
        }
    }
}