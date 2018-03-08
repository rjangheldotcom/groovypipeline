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


    }
} catch (e){
        echo "${e}"
}
}
return this;

