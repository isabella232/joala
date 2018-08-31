pipeline {
  agent any

  tools {
    // Requires MAVEN3 to be configured in Global Tool Configuration
    maven "MAVEN3"
    // Requires JDK8 to be configured in Global Tool Configuration
    jdk "JDK8"
  }

  // Note, that parameters can only be configured after first run.
  parameters {
    booleanParam(name: 'RELEASE', defaultValue: false, description: 'Check this to trigger a release build.')
  }

  triggers {
    pollSCM('@daily')
  }

  stages {
    stage('Preparation') {
      steps {
        git 'https://github.com/CoreMedia/joala.git'
      }
    }
    stage('Build') {
      steps {
        sh "'${M2_HOME}/bin/mvn' -Dmaven.test.failure.ignore clean install"
      }
    }
    stage('Release') {
      when {
        expression { return params.RELEASE }
      }
      steps {
        withCredentials([
                usernamePassword(credentialsId: 'sonatype-jira', usernameVariable: 'OSS_USER', passwordVariable: 'OSS_PASSWORD'),
                string(credentialsId: 'github-oauth', variable: 'GITHUB_OAUTH'),
                certificate(credentialsId: 'gpg-sign', keystoreVariable: 'GPG_KEYSTORE_FILE', passwordVariable: 'GPG_KEYSTORE_PASSWORD')
        ]) {
          echo "Release not implemented yet."
          echo "The following will be hidden in log, it just shows the usage..."
          echo "    Configured Sonatype Credentials: ${OSS_USER} with password ${OSS_PASSWORD}"
          echo "    Configured GitHub OAUTH: ${GITHUB_OAUTH}"
          echo "    Configured GPG Sign: ${GPG_KEYSTORE_FILE}, password: ${GPG_KEYSTORE_PASSWORD}"
        }
      }
    }
  }
  post {
    always {
      junit '**/target/surefire-reports/TEST-*.xml'
      archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }
  }
}
