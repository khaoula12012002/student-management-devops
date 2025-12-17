pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = "dockerhub-cred"
        IMAGE_NAME            = "khoukhaaaaa/student-management"
        IMAGE_TAG             = "${env.BUILD_NUMBER}"
        APP_PORT              = "8082"
    }

    tools {
        maven 'M3'
        jdk   'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Create Dockerfile') {
            steps {
                bat '''
                    (
                        echo FROM eclipse-temurin:17-jre-alpine
                        echo WORKDIR /app
                        echo COPY target/*.jar app.jar
                        echo EXPOSE 8080
                        echo ENTRYPOINT ["java","-jar","/app/app.jar"]
                    ) > Dockerfile
                '''
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
                    withSonarQubeEnv('SonarQube Server') {
                        bat '''
                            mvn sonar:sonar ^
                                -Dsonar.projectKey=student-management-khaoula ^
                                -Dsonar.projectName="Student Management - Khaoula" ^
                                -Dsonar.host.url=http://localhost:9000 ^
                                -Dsonar.token=%SONAR_TOKEN%
                        '''
                    }
                }
                echo "✅ Analyse SonarQube envoyée ! (elle peut prendre du temps à apparaître sur le site)"
            }
        }

        stage('Package') {
            steps {
                bat 'mvn clean package -DskipTests'
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
                    docker stop student-management || echo "Conteneur déjà arrêté"
                    docker rm student-management || echo "Conteneur déjà supprimé"
                    docker run -d -p %APP_PORT%:8080 --name student-management %IMAGE_NAME%:latest
                '''
                echo "🚀 Application déployée ! Accède-la ici : http://localhost:%APP_PORT%"
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
            echo "✅ SUCCÈS TOTAL ! Ton app tourne sur http://localhost:%APP_PORT%"
            echo "🔍 Analyse SonarQube disponible (ou en cours) sur http://localhost:9000"
            echo "🐳 Image publiée : https://hub.docker.com/r/khoukhaaaaa/student-management"
        }
        failure {
            echo "❌ Échec du pipeline. Vérifie les logs ci-dessus."
        }
    }
}