def buildPipeline(def userConfig = [:]) {
try {
node
{
  def rtGradle = Artifactory.newGradleBuild()
    env.JAVA_HOME="${tool 'jdk8u162'}"
    def config = [:]
    config.serviceRepo = 'git@github.com:varunasunmathi/test.git'
    config.gitBranch = 'master'
     stage('Clone sources') {
           echo "check out branch ${config.gitBranch} from ${config.serviceRepo}"
           git branch: config.gitBranch, url: config.serviceRepo
    }

    stage('build') {
        rtGradle.tool = "gradle"
        def buildInfo = rtGradle.run tasks: 'sonarqube'
    }

    pmd canComputeNew: false,
    defaultEncoding: '',
    healthy: '',
    pattern: '**/test.xml',

    unHealthy: ''

    publishHTML([allowMissing: true,
    alwaysLinkToLastBuild: true,
    keepAll: true,
    reportDir: '/var/lib/jenkins/workspace/bnym/build/reports/tests/test',
    reportFiles: 'index.html',
    reportName: 'Junit Reports',
    reportTitles: 'Junit Reports'])

    publishHTML([allowMissing: true,
    alwaysLinkToLastBuild: true,
    keepAll: true,
    reportDir: '/var/lib/jenkins/workspace/bnympipeline/build/reports/pmd',
    reportFiles: 'main.html',
    reportName: 'PMD main Reports',
    reportTitles: 'PMD main Reports'])

    stage('Docker build'){
        sh 'sudo docker login -u vkotteswaran -p Varun@33'
        sh 'sudo docker build -t $JOB_BASE_NAME:$BUILD_ID $WORKSPACE'
    }
     stage('push to artifactory'){
        sh 'sudo docker tag $JOB_BASE_NAME:$BUILD_ID vkotteswaran/$JOB_BASE_NAME:$BUILD_ID'
        sh 'sudo docker tag $JOB_BASE_NAME:$BUILD_ID vkotteswaran/$JOB_BASE_NAME:latest'
        sh 'sudo docker push vkotteswaran/$JOB_BASE_NAME:$BUILD_ID'
        sh 'sudo docker push vkotteswaran/$JOB_BASE_NAME:latest'
    }

  stage('DeployToDev'){
        sh 'sudo docker stop altidemo || true && sudo docker rm altidemo || true'
        sh 'sudo docker run --rm --memory="1400m" --cpus=0.250 --name altidemo -d -p 8085:8080 vkotteswaran/$JOB_BASE_NAME:$BUILD_ID'
    }

    }
} catch (e){
        echo "${e}"
}
}
return this;

