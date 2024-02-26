pipeline {
    agent any
    
    environment {
        EKS_CLUSTER_NAME = 'skl-cluster'
        AWS_DEFAULT_REGION = 'ap-southeast-1'
        DOCKER_IMAGE_NAME = 'piyachet/skl-nodejs'
        DOCKER_HUB_CREDENTIALS = 'dckr_pat_ivs5khrmSW1lYHsrx53pU6obdo0'
    }
    
    stages {
        stage('Initialization') {
            steps {
                script {
                    deleteDir()
                    
                    git branch: 'main', url: 'https://github.com/piyachet/skl-node-js-postgresql-crud-example.git'
                }
            }
        }
        
        stage('Build') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE_NAME}:latest", "-f Dockerfile .")
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                script {
                    // Login to Docker Hub
                    withCredentials([usernamePassword(credentialsId: DOCKER_HUB_CREDENTIALS, usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                        sh "docker login -u ${DOCKER_HUB_USERNAME} -p ${DOCKER_HUB_PASSWORD}"
                    }
                    
                    // Push Docker image to Docker Hub
                    docker.image("${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}").push()
                    
                    // Optionally, tag and push latest version
                    docker.image("${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}").tag("${DOCKER_IMAGE_NAME}:latest")
                    docker.image("${DOCKER_IMAGE_NAME}:latest").push()
                }
            }
        }
    }
}
