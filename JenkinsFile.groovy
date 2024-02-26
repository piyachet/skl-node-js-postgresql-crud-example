pipeline {
    agent any
    
    environment {
        EKS_CLUSTER_NAME = 'skl-cluster'
        AWS_DEFAULT_REGION = 'ap-southeast-1'
    }
    
    stages {
        stage('Initialization') {
            steps {
                script {
                    // Clean workspace
                    deleteDir()
                    
                    // Checkout code from repository
                    git branch: 'main', url: 'https://github.com/piyachet/skl-node-js-postgresql-crud-example.git'
                }
            }
        }
        
        stage('Build') {
            steps {
                script {
                    // Build Docker image
                    docker.build('skl-crud-nodejs')
                }
            }
        }
        
        stage('Deploy to EKS') {
            steps {
                script {
                    // Authenticate with EKS cluster
                    withAWS(credentials: 'aws-credentials-id', region: AWS_DEFAULT_REGION) {
                        sh "aws eks update-kubeconfig --name ${EKS_CLUSTER_NAME}"
                    }
                    
                    // Apply Kubernetes manifests
                    sh "kubectl apply -f path/to/your/kubernetes/manifests"
                }
            }
        }
    }
}
