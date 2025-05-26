import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.InputQueue
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ziplinemobile.R
import com.example.ziplinemobile.network.RetrofitClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var uploadButton: Button
    private val api = RetrofitClient.instance

    @Override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        uploadButton = findViewById(R.id.uploadButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchFiles()

        val filePicker =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let { uploadFile(it) }
            }

        uploadButton.setOnClickListener {
            filePicker.launch("*/*")
        }
    }

    private fun fetchFiles() {
        api.getFiles().enqueue(object : InputQueue.Callback<List<FileItem>> {
            override fun onResponse(call: Call<List<FileItem>>, response: Response<List<FileItem>>) {
                if (response.isSuccessful) {
                    recyclerView.adapter = FilesAdapter(response.body() ?: emptyList())
                }
            }

            override fun onFailure(call: Call<List<FileItem>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadFile(uri: Uri) {
        val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        } ?: "unknown_file"

        val inputStream = contentResolver.openInputStream(uri) ?: return
        val requestBody = RequestBody.create(MediaType.parse(contentResolver.getType(uri) ?: "*/*"), inputStream.readBytes())
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestBody)

        api.uploadFile(filePart).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "File uploaded!", Toast.LENGTH_SHORT).show()
                    fetchFiles()
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Upload failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

data class FileItem()
data class UploadResponse()
