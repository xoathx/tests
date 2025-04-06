pipeline {
    agent any

    environment {
        ALLURE_RESULTS_DIRECTORY = 'allure-results'
        DISPLAY = ":99"
    }

    stages {
        stage('Parallel Tests') {
            parallel {
                stage('Api Test') {
                    steps {
                        script {
                            def mvnHome = tool name: 'maven', type: 'maven'
                            sh "${mvnHome}/bin/mvn clean test -Dsurefire.excludes=SampleUITest.java"
                            sh 'mkdir -p allure-results-api'
                            sh 'cp -r allure-results/* allure-results-api/ || true'
                            sh 'rm -rf allure-results'
                        }
                    }
                }

                stage('Playwright UI Tests') {
                    steps {
                        script {
                            def mvnHome = tool name: 'maven', type: 'maven'
                            sh 'sudo Xvfb :99 -screen 0 1280x1024x24 &'
                            sh "sudo -E ${mvnHome}/bin/mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args=\"install-deps\""
                            sh "${mvnHome}/bin/mvn clean test -Dtest=SampleUITest"
                            sh 'mkdir -p allure-results-ui'
                            sh 'cp -r allure-results/* allure-results-ui/ || true'
                            sh 'rm -rf allure-results'
                        }
                    }
                }
            }
        }

        stage('Merge Allure Results') {
            steps {
                sh 'mkdir -p allure-results'
                sh '''
                    if [ -d "allure-results-api" ]; then
                        cp -r allure-results-api/* allure-results/ || true
                    fi
                    if [ -d "allure-results-ui" ]; then
                        cp -r allure-results-ui/* allure-results/ || true
                    fi
                '''
            }
        }
    }

    post {
        always {
            echo "ALLURE_RESULTS_DIRECTORY=${env.ALLURE_RESULTS_DIRECTORY}"
            allure([
                    includeProperties: false,
                    jdk: '',
                    results: [[path: 'allure-results']],
                    reportBuildPolicy: 'ALWAYS'
            ])
        }
    }
}
