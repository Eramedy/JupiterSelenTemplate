pipeline {
    agent {
        docker { image 'openjdk:14.0' }
    }

    stages {
        stage('Tests') {
            steps {
                script {
                    sh './gradlew clean test'
                }
            }
        }
    }
    post {
        always {
            script {
                allure([
                        includeProperties: false,
                        jdk              : '',
                        properties       : [],
                        reportBuildPolicy: 'ALWAYS',
                        results          : [[path: 'build/allure-results']]
                ])
            }
        }
    }
}
