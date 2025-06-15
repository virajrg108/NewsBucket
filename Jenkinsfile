pipeline {
    agent any

    environment {
        AWS_ECR_LOGIN = '229182852745.dkr.ecr.ap-south-1.amazonaws.com'
        NEWS_SERVICE_DIR = 'news-service'
        CLIENT_DIR = 'client'
        IMAGE_TAG = 'latest'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/virajrg108/newsbucket'
            }
        }

        stage('Build News Service JAR') {
            steps {
                dir("${NEWS_SERVICE_DIR}") {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Docker Login to ECR') {
            steps {
                sh 'aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin $AWS_ECR_LOGIN'
            }
        }

        stage('Build & Push News Service Image') {
            steps {
                dir("${NEWS_SERVICE_DIR}") {
                    sh 'docker build -t $AWS_ECR_LOGIN/news-service:$IMAGE_TAG .'
                    sh 'docker push $AWS_ECR_LOGIN/news-service:$IMAGE_TAG'
                }
            }
        }

        stage('Build & Push Client Image') {
            steps {
                dir("${CLIENT_DIR}") {
                    sh 'docker build -t $AWS_ECR_LOGIN/client:$IMAGE_TAG .'
                    sh 'docker push $AWS_ECR_LOGIN/client:$IMAGE_TAG'
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker-compose down'
                sh 'docker-compose pull'
                sh 'docker-compose up -d'
            }
        }
    }
}
