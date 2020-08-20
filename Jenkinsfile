properties(
        [parameters([
                string(defaultValue: '', description: '', name: 'Test', trim: true)
        ])]
)

pipeline {
    agent any

    stages {
        stage('Tests') {
            tools {
                jdk "jdk13"
            }
            steps {
                script {
                    if (params.Test == '') {
                        sh './gradlew clean test'
                    } else {
                        sh "./gradlew clean test -Dtest=${params.Test}"
                    }
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
