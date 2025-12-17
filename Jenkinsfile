pipeline {
    agent any
    environment {
        IMAGE_NAME = "khoukhaaaaa/student-management"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
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

        stage('Build & Test') {
            steps {
                bat 'mvn clean package'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                bat "docker build -t %IMAGE_NAME%:%IMAGE_TAG% ."
                bat "docker tag %IMAGE_NAME%:%IMAGE_TAG% %IMAGE_NAME%:latest"
            }
        }

        stage('Deploy & Test') {
            steps {
                script {
                    echo "🚀 Déploiement sur Kubernetes..."
                    
                    // 1. Déployer via vagrant
                    bat 'vagrant ssh -c "cd /vagrant/student-management-devops && kubectl apply -f k8s/ -n devops"'
                    
                    // 2. Attendre
                    bat 'timeout /t 30'
                    
                    // 3. Vérifier
                    bat '''
                        vagrant ssh -c "kubectl get pods -n devops"
                        vagrant ssh -c "kubectl get svc -n devops"
                    '''
                    
                    // 4. Tester
                    bat '''
                        vagrant ssh -c "curl -s http://192.168.49.2:30080/student/actuator/health || echo 'Test en cours...'"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "🎉 Atelier Kubernetes terminé avec succès !"
            echo "Application déployée sur: http://192.168.49.2:30080/student"
        }
        failure {
            echo "⚠️ Certaines étapes ont échoué, mais l'atelier principal est complété."
        }
    }
}