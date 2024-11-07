package com.example.appupload

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Launcher para iniciar a atividade de seleção de arquivo e obter o resultado
    private val selectFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val arquivo = tempfile(it)
            upload(arquivo)
        }
    }

    // Função para abrir o seletor de arquivos
    fun actEnviar(view: View) {
        selectFileLauncher.launch("*/*")
    }

    // Função para copiar o conteúdo do URI para um arquivo temporário e retornar seu caminho
    private fun tempfile(contentUri: Uri): String {
        var retorno = ""
        try {
            val extensao = "." + (contentResolver.getType(contentUri)?.split("/")?.get(1) ?: "tmp")
            val outputDir = cacheDir
            val outputFile = File.createTempFile("upload", extensao, outputDir)
            retorno = outputFile.absolutePath

            val inputStream: InputStream? = contentResolver.openInputStream(contentUri)
            val outputStream: OutputStream = FileOutputStream(outputFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (input.read(buffer).also { length = it } > 0) {
                        output.write(buffer, 0, length)
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return retorno
    }

    // Função para fazer o upload do arquivo
    private fun upload(arquivo: String) {
        val pb: ProgressBar = findViewById(R.id.progresso)
        pb.visibility = View.VISIBLE

        val file = File(arquivo)
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val fileToUpload = MultipartBody.Part.createFormData("arquivo", file.name, requestBody)
        val filename = RequestBody.create(MediaType.parse("text/plain"), file.name)

        val apiConfig = AppConfig.getRetrofit().create(ApiConfig::class.java)
        val call = apiConfig.uploadFile(fileToUpload, filename)

        call.enqueue(object : Callback<ServerResponse> {
            override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                val serverResponse = response.body()
                if (serverResponse != null) {
                    if (serverResponse.success) {
                        Toast.makeText(applicationContext, serverResponse.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, serverResponse.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    serverResponse?.let {
                        println("Response: ${it.toString()}")
                    }
                }
                pb.visibility = View.GONE
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                pb.visibility = View.GONE
                Toast.makeText(applicationContext, "Upload falhou: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
