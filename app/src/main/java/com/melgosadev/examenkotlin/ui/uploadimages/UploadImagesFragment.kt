package com.melgosadev.examenkotlin.ui.uploadimages

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.melgosadev.examenkotlin.MainActivity
import com.melgosadev.examenkotlin.R
import com.melgosadev.examenkotlin.databinding.FragmentUploadImagesBinding

class UploadImagesFragment : Fragment() {

    private lateinit var uploadImagesViewModel: UploadImagesViewModel
    private var _binding: FragmentUploadImagesBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uploadImagesViewModel =
            ViewModelProvider(this).get(UploadImagesViewModel::class.java)

        _binding = FragmentUploadImagesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        uploadImagesViewModel.messageResult.observe(viewLifecycleOwner, { message ->
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        })

        uploadImagesViewModel.errorMessage.observe(viewLifecycleOwner, { errorMessage ->
            showDialog(errorMessage)
        })

        val btnLoadImages: Button = binding.btnLoadFiles
        val btnTakePicture: Button = binding.btnTakePicture

        btnLoadImages.setOnClickListener {
            uploadImagesViewModel.openFileManager(this)
        }

        btnTakePicture.setOnClickListener {
            uploadImagesViewModel.checkCameraPermissions(this)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Cuando el intent ha sido ejecutado, regresa una respuesta. Esta se valida en este método
     * sobreesrito y de acuerdo a la validación se ejecutan distintas acciones.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            FILE_REQUEST_CODE -> {
                if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                    uploadImagesViewModel.manageDataFromIntent(data)
                }
            }
            TAKE_PHOTO_REQUEST_CODE -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    uploadImagesViewModel.uploadImageFromCamera()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //El usuario ha aceptado el permiso, abrir camara para tomar foto
                    uploadImagesViewModel.takePictureIntent(this)
                } else {
                    //El usuario ha rechazado el permiso
                    showDialog(getString(R.string.dialog_message_no_podras_usar_funcionalidad))
                }
                return
            }
            else -> {

            }
        }
    }

    /**
     * Muestra un diálogo simple, con un botón de acción
     */
    private fun showDialog(message: String){
        let { activity ->
            val builder = AlertDialog.Builder(activity as MainActivity)
            builder.apply {
                setPositiveButton(R.string.accept) { dialog, _ ->
                    dialog.dismiss()
                }
                setTitle(R.string.dialog_title_atencion)
                setMessage(message)
                create()
                show()
            }
        }
    }
}