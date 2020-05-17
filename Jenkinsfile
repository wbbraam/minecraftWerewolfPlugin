pipeline {
    agent any
    stages {
        stage('Building') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }

        stage('Archive artifacts') {
            steps {
                archiveArtifacts(artifacts: 'target/TogglePVP-1.0.0-SNAPSHOT.jar', onlyIfSuccessful: true)
            }
        }
    }
}