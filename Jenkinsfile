pipeline {
    agent any
    tools {
        maven 'MAVEN'
    }

    environment {
        DEV_REMOTE_CREDENTIALS = credentials('remote-server-ssh')
        DEV_REMOTE_HOST = '192.168.10.40'
        REMOTE_QA = '192.168.10.81'
        DOCKER_CREDENTIALS_ID = 'docker-credentials-id'
        DOCKER_REGISTRY = 'registry.gitlab.com'
        IMAGE_NAME ='registry.gitlab.com/afrosoft-projects/zdf_bf/zdf-bf-backend/loans-module:latest'
    }

    stages {


        stage("Docker Login") {
            when {
                anyOf {
                    branch 'develop'
                    branch 'QA'
                }
            }
            steps {
                   script {

                           withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                              sh """
                                 docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD $DOCKER_REGISTRY
                                 """
                         }
                   }
        }
        }
        stage('Build Docker Image') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'QA'

                }
            }
            steps {
                script {

                    sh "docker build --pull -f ./Dockerfile -t $IMAGE_NAME ."
                }
            }
        }
        stage('Push Docker Image') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'QA'

                }
            }
            steps {
                script {

                    sh "docker push $IMAGE_NAME"
                }
            }
        }
        stage('Deploy DEV Application') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    def remote = [:]
                    remote.name = 'NRZDEV'
                    remote.host = '192.168.10.40'
                    remote.user = 'zdf'
                    remote.password = 'zdf@321'
                    remote.allowAnyHosts = true

                    sshCommand remote: remote, command: """
                        docker pull $IMAGE_NAME
                        docker stop zdf-loans-module || true
                        docker rm zdf-loans-module || true
                        docker run -e "SPRING_PROFILES_ACTIVE=dev" -v /docs:/docs  --name zdf-loans-module -d -p 7200:7200  $IMAGE_NAME
                    """
                     }
                }
        }

        stage('Deploy QA Application') {
            when {
                branch 'QA'
            }
            steps {
                script {
                    def remote = [:]
                    remote.name = 'NRZQA'
                    remote.host = '192.168.10.81'
                    remote.user = 'afroqa'
                    remote.password = 'afroqa@321'
                    remote.allowAnyHosts = true

                    sshCommand remote: remote, command: """
                        docker pull $IMAGE_NAME
                        docker stop zdf-loans-module || true
                        docker rm zdf-loans-module || true
                        docker volume create docs
                        docker run -e "SPRING_PROFILES_ACTIVE=qa" -v /docs:/docs  --name zdf-loans-module -d -p 7200:7200  $IMAGE_NAME
                    """
                     }
                }
        }
}
}


