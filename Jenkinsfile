properties(
        [parameters([
                string(defaultValue: '', description: '', name: 'Test', trim: true),
                string(defaultValue: '', description: 'Additional options', name: 'AddOpts', trim: true)
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
                    def command = "./gradlew clean test"
                    if (params.Test != '') {
                        command += "  --tests ${params.Test}"
                    }
                    if (params.AddOpts != '') {
                        command += " ${params.Test}"
                    }

                    sh command
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
