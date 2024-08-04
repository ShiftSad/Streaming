import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class DownloadUrlTask : DefaultTask() {
    @Input
    lateinit var sourceUrl: String

    @OutputFile
    lateinit var target: File

    @TaskAction
    fun download() {
        ant.withGroovyBuilder {
            "get"("src" to sourceUrl, "dest" to target)
        }
    }
}

tasks.register<DownloadUrlTask>("downloadMovenetLightningModel") {
    val modelMovenetLightningDownloadUrl = "https://tfhub.dev/google/lite-model/movenet/singlepose/lightning/tflite/float16/4?lite-format=tflite"
    doFirst {
        println("Downloading $modelMovenetLightningDownloadUrl")
    }
    sourceUrl = modelMovenetLightningDownloadUrl
    target = file("src/main/assets/movenet_lightning.tflite")
}

tasks.register<DownloadUrlTask>("downloadMovenetThunderModel") {
    val modelMovenetThunderDownloadUrl = "https://tfhub.dev/google/lite-model/movenet/singlepose/thunder/tflite/float16/4?lite-format=tflite"
    doFirst {
        println("Downloading $modelMovenetThunderDownloadUrl")
    }
    sourceUrl = modelMovenetThunderDownloadUrl
    target = file("src/main/assets/movenet_thunder.tflite")
}

tasks.register("downloadModel") {
    dependsOn("downloadMovenetLightningModel")
    dependsOn("downloadMovenetThunderModel")
}