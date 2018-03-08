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


    }
} catch (e){
        echo "${e}"
}
}
return this;

