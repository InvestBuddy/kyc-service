pipeline {
    agent any

    tools {
        maven 'Maven_3.9.9'  // Ensure this matches the name of your Maven installation in Jenkins
        jdk 'JDK_21'        // Ensure this matches the name of your JDK installation in Jenkins
    }

    environment {
        IMAGE_NAME = "abakhar217/kyc-service:kyc-service-${BUILD_NUMBER}"
        DEPLOYMENT_NAME = 'kyc-service-deployment'
        // qualityGateFailed = false // Flag to track Quality Gate failure
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the code from the repository using the configured Git credentials
                checkout([$class: 'GitSCM',
                          branches: [[name: 'master']],
                          userRemoteConfigs: [[url: 'https://github.com/InvestBuddy/kyc-service.git', credentialsId: 'Git']]])
            }
        }

        stage('Build JAR') {
            steps {
                script {
                    // Run mvn clean package to build the application
                    bat 'mvn clean package -Dmaven.test.failure.ignore=true'
                }
            }
        }

        stage('Check if JAR exists') {
            steps {
                script {
                    // Ensure the JAR file exists before proceeding
                    if (!fileExists('target/kyc-service-1.0-SNAPSHOT.jar')) {
                        error "kyc-service-1.0-SNAPSHOT.jar not found! Build failed."
                    }
                }
            }
        }
       
       stage('Build and SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    bat 'mvn sonar:sonar -Dsonar.login=%SONAR_TOKEN%'
                }
            }
        }


        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image with the .jar file inside the image
                    bat "docker build -t ${IMAGE_NAME} ."
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    // Use the credentials stored in Jenkins for Docker Hub
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        // Log in to Docker Hub
                        bat "echo %DOCKER_PASSWORD% | docker login -u %DOCKER_USERNAME% -p %DOCKER_PASSWORD% "
                        // Push the image to Docker Hub
                        bat "docker push ${IMAGE_NAME}"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    withKubeConfig([credentialsId: 'kubectl']) {
                        // Ensure kubectl is configured and available in the Jenkins environment
                        bat """
                            kubectl apply -f kyc-service-db-deployment.yml
                            kubectl apply -f kyc-service-db-service.yml
                            kubectl apply -f kyc-service-deployment.yml
                            kubectl apply -f kyc-service-service.yml
                        """
                    }
                }
            }
        }
    }

	post {
	    always {
	        echo "Pipeline completed. Final status: ${currentBuild.currentResult}"
	        bat 'docker system prune -f'
	    }
	    success {
	        echo "Pipeline succeeded! Build number: ${env.BUILD_NUMBER}, Job name: ${env.JOB_NAME}"
	    }
	    unstable {
	        echo "Pipeline marked as UNSTABLE. Possible cause: Quality Gate failure or warnings."
	    }
	    failure {
	        echo "Pipeline failed!"
	        echo "Error Details: ${currentBuild.description ?: 'No detailed error provided.'}"
	        script {
	            currentBuild.description = "Failure occurred during ${env.STAGE_NAME}. Check logs."
	        }
	    }
	    aborted {
	        echo "Pipeline was aborted by user or timeout."
	    }
	}


}
