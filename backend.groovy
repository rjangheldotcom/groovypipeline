def buildPipeline(def userConfig = [:]) {
node('master')
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
}

}
