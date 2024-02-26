pipeline {
    agent any

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['Staging', 'Production'], description: 'Select environment to deploy')
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    def appPort
                    def cloudSQLInstance
                    if (params.ENVIRONMENT == 'Staging') {
                        appPort = 4000
                        cloudSQLInstance = 'staging-instance'
                    } else {
                        appPort = 3000
                        cloudSQLInstance = 'production-instance'
                    }

                    sh 'gcloud container clusters create my-cluster --num-nodes=3 --zone=us-central1-a'

                    sh "gcloud sql instances create ${cloudSQLInstance} --zone=us-central1-a"
                    sh "gcloud sql databases create mydatabase --instance=${cloudSQLInstance} --charset=utf8 --collation=utf8_general_ci"
                    sh "gcloud sql users create user --instance=${cloudSQLInstance} --password=password"
                }
            }
        }
        stage('Build') {
            steps {
                sh 'npm install'
            }
        }
        stage('Deploy') {
            steps {
                script {
                    sh "kubectl apply -f kubernetes/deployment.yaml --namespace=${params.ENVIRONMENT.toLowerCase()}"
                    sh "kubectl expose deployment/my-app --type=LoadBalancer --port=${appPort} --target-port=80 --namespace=${params.ENVIRONMENT.toLowerCase()}"
                }
            }
        }
    }

    post {
        success {
            script {
                def cpuUsage = sh(script: "kubectl top pods --no-headers --namespace=${params.ENVIRONMENT.toLowerCase()} | awk '{print \$2}'", returnStdout: true).trim()
                def cpuUsagePercentage = cpuUsage.toInteger()
                if (cpuUsagePercentage >= 50) {
                    sh "kubectl scale deployment/my-app --replicas=5 --namespace=${params.ENVIRONMENT.toLowerCase()}"
                }
            }
        }
    }
}
