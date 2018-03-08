def buildPipeline(def userConfig = [:]) {
try {
node
{
    def config = [:]

BUILD_NUM = "${env.DEV_Build_Number}"
sh "echo $BUILD_NUM"
  stage('DeployToQA'){
        sh 'sudo docker stop altidemoqa || true && sudo docker rm altidemoqa || true'
       sh  "sudo docker run --rm  --memory="1400m" --cpus=0.250 --name altidemoqa -d -p 8082:8080 vkotteswaran/$JOB_BASE_NAME:$BUILD_NUM"
    }

 stage('Deploy to Prod?') {
        timeout(time: 35000, unit: 'SECONDS'){
        input message: 'Need approval to proceed...', ok: 'Approve'
        input message: 'Are you Sure?', ok: 'yes'
             mail body: 'Approved to Deploy in Prod',
                     from: 'smohanram@altimetrik.com',
                     replyTo: 'smohanram@altimetrik.com',
                     subject: 'Approved to Deploy in PROD',
                     to: 'smohanram@altimetrik.com'
        }
        sh 'sudo docker stop altidemoprod || true && sudo docker rm altidemoprod || true'
        sh "sudo docker run --rm --memory="1400m" --cpus=0.250 --name altidemoprod -d -p 8083:8080 vkotteswaran/$JOB_BASE_NAME:$BUILD_NUM"
    }

    }
} catch (e){
        echo "${e}"
}
}
return this;
