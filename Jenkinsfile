pipeline {
    agent any
    environment {
        DOCKERHUB_CREDENTIALS = "dockerhub-cred"
        IMAGE_NAME = "khoukhaaaaa/student-management"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        APP_PORT = "8082"
        DOCKER_BUILDKIT = '0'  // Au cas où sur Windows
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

        stage('Deploy Locally') {
            steps {
                bat '''
                    docker stop student-management || echo "Pas de conteneur à arrêter"
                    docker rm student-management || echo "Pas de conteneur à supprimer"
                    docker run -d -p %APP_PORT%:8080 --name student-management %IMAGE_NAME%:latest
                '''
                echo "🚀 Application déployée sur http://localhost:%APP_PORT%"
            }
        }

        stage('Cleanup') {
            steps {
                bat 'docker image prune -f'
            }
        }
    }

    post {
        success {
            echo "✅ PIPELINE RÉUSSIE ! App → http://localhost:%APP_PORT%"
            echo "🐳 Image sur https://hub.docker.com/r/khoukhaaaaa/student-management"
        }
        failure {
            echo "❌ Échec – vérifie les logs"
        }
        always {
            echo "Pipeline terminée – Khaoula Ben Slimane 💪"
        }
    }
}