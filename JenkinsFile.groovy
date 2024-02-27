pipeline {
    agent any
    
    environment {
        EKS_CLUSTER_NAME = 'skl-cluster'
        AWS_DEFAULT_REGION = 'ap-southeast-1'
        DOCKER_IMAGE_NAME = 'piyachet/skl-nodejs'
        DOCKER_HUB_CREDENTIALS = 'dockerHub'
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
                    docker.image("${DOCKER_IMAGE_NAME}:latest").push()
                    
                    // // Optionally, tag and push latest version
                    // docker.image("${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}").tag("${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}")
                    // docker.image("${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}").push()
                }
            }
        }
        
        stage('Deploy to EKS Staging') {
            environment {
                NAMESPACE = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD | grep -q "develop" && echo "staging" || echo "production"').trim()
            }
            steps {
                script {
                    withAWS(credentials: 'aws-credentials-id', region: AWS_DEFAULT_REGION) {
                        sh "aws eks update-kubeconfig --name skl-cluster"
                    }
                    
                    sh "kubectl apply -f path/to/your/kubernetes/manifests/${NAMESPACE}"
                }
            }
        }
    }
}

//         stage('Deploy to EKS Staging') {
//             steps {
//                 script {
//                     // Authenticate with EKS cluster
//                     withAWS(credentials: 'aws-credentials-id', region: AWS_DEFAULT_REGION) {
//                         sh "aws eks update-kubeconfig --name ${EKS_CLUSTER_NAME}"
//                     }
                    
//                     // Apply Kubernetes manifests for staging namespace
//                     sh "kubectl apply -f path/to/your/kubernetes/manifests/staging"
//                 }
//             }
//         }
        
//         stage('Deploy to EKS Production') {
//             steps {
//                 script {
//                     // Authenticate with EKS cluster
//                     withAWS(credentials: 'aws-credentials-id', region: AWS_DEFAULT_REGION) {
//                         sh "aws eks update-kubeconfig --name ${EKS_CLUSTER_NAME}"
//                     }
                    
//                     // Apply Kubernetes manifests for production namespace
//                     sh "kubectl apply -f path/to/your/kubernetes/manifests/production"
//                 }
//             }
//         }
//     }
// }