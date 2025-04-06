pipeline {
    agent any

    environment {
        ALLURE_RESULTS_API = 'allure-results-api'
        ALLURE_RESULTS_UI = 'allure-results-ui'
        ALLURE_RESULTS_MERGED = 'allure-results'
        DISPLAY = ":99" // виртуальный дисплей для UI
    }

    stages {
        stage('Parallel Tests') {
            parallel {
                stage('Api Test') {
                    agent any
                    steps {
                        script {
                            def mvnHome = tool name: 'maven', type: 'maven'
                            sh "mkdir -p ${env.ALLURE_RESULTS_API}"
                            sh "${mvnHome}/bin/mvn clean test -Dsurefire.excludes=SampleUITest.java -Dallure.results.directory=${env.ALLURE_RESULTS_API}"
                        }
                    }
                }

                stage('Playwright UI Tests') {
                    agent any
                    steps {
                        script {
                            def mvnHome = tool name: 'maven', type: 'maven'

                            sh 'sudo Xvfb :99 -screen 0 1280x1024x24 &'
                            sh "sudo -E ${mvnHome}/bin/mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args=\"install-deps\""

                            sh "mkdir -p ${env.ALLURE_RESULTS_UI}"
                            sh "${mvnHome}/bin/mvn clean test -Dtest=SampleUITest -Dallure.results.directory=${env.ALLURE_RESULTS_UI}"
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Собираем результаты Allure из ${env.ALLURE_RESULTS_API} и ${env.ALLURE_RESULTS_UI}"

            sh "mkdir -p ${env.ALLURE_RESULTS_MERGED}"
            sh "cp -r ${env.ALLURE_RESULTS_API}/* ${env.ALLURE_RESULTS_MERGED}/ || true"
            sh "cp -r ${env.ALLURE_RESULTS_UI}/* ${env.ALLURE_RESULTS_MERGED}/ || true"

            allure([
                    includeProperties: false,
                    jdk: '',
                    results: [[path: "${env.ALLURE_RESULTS_MERGED}"]],
                    reportBuildPolicy: 'ALWAYS'
            ])
        }
    }
}
