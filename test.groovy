pipeline {
    agent any

    environment {
        ALLURE_RESULTS_DIRECTORY = 'allure-results'
        ALLURE_REPORT_DIRECTORY = 'allure-report'
        DISPLAY = ":99"  // Указываем виртуальный дисплей Xvfb
    }

    stages {
        stage('Parallel Tests') {
            parallel {
                stage('Api Test') {
                    agent any
                    steps {
                        script {
                            def mvnHome = tool name: 'maven', type: 'maven'
                            sh "${mvnHome}/bin/mvn clean test -Dsurefire.excludes=SampleUITest.java"
                        }
                    }
                }

                stage('Playwright UI Tests') {
                    agent any
                    steps {
                        script {
                            def mvnHome = tool name: 'maven', type: 'maven'

                            // Запуск Xvfb
                            sh 'sudo Xvfb :99 -screen 0 1280x1024x24 &'

                            // Устанавливаем зависимости для Playwright
                            sh "sudo -E ${mvnHome}/bin/mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args=\"install-deps\""

                            // Запуск UI тестов
                            sh "${mvnHome}/bin/mvn clean test -Dtest=SampleUITest"

                            // Проверка результатов
                            sh 'ls -la allure-results'  // Проверка наличия отчётов
                        }
                    }
                }
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
