package ipvc.estg.mapapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class AdicionarProblema : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_problema)

        val spinner: Spinner = findViewById(R.id.spinner2)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.tipoProb,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val tipoProb = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val imageButton = findViewById<Button>(R.id.imagem)
        imageButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else {
                    pickImageFromGallery();
                }
            }else {
                pickImageFromGallery()
            }
        }
    }
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object{
        private val IMAGE_PICK_CODE = 1000;
        private val PERMISSION_CODE = 1001;
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val image = findViewById<ImageView>(R.id.imageView)

            image.setImageURI(data?.data)
        }
    }
}