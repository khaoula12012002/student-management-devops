pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = "dockerhub-cred"
        IMAGE_NAME            = "khoukhaaaaa/student-management"  
        IMAGE_TAG             = "latest"  
    }

    tools {
        maven 'M3'
        jdk   'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/khaoula12012002/student-management-devops.git', branch: 'main'
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
                // Si des tests MySQL/Postgres posent problème, ajoute -P!postgres ou change version DB comme avant
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
                        timeout(time: 10, unit: 'MINUTES') {
                            bat '''
                                mvn sonar:sonar ^
                                    -Dsonar.projectKey=student-management-khaoula ^
                                    -Dsonar.host.url=http://localhost:9000 ^
                                    -Dsonar.token=%SONAR_TOKEN% ^
                                    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
            }
        }

        stage('Package') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat "docker build -f Dockerfile -t %IMAGE_NAME%:%IMAGE_TAG% ."
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKERHUB_CREDENTIALS}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                    bat "docker push %IMAGE_NAME%:%IMAGE_TAG%"
                }
            }
        }

        stage('Deploy Container') {
            steps {
                bat '''
                    docker stop student-management || exit 0
                    docker rm student-management || exit 0
                    docker run -d -p 8082:8080 --name student-management %IMAGE_NAME%:%IMAGE_TAG%
                '''
            }
        }
    }

    post {
        always {
            echo "Pipeline finished - Khaoula Ben Slimane"
        }
        success {
            echo "Build succeeded! App deployed on http://localhost:8082"
        }
        failure {
            echo "Build failed! Check logs"
        }
    }
}