package com.melgosadev.examenkotlin.ui.uploadimages

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

const val FILE_REQUEST_CODE = 1
const val CAMERA_PERMISSION_REQUEST_CODE = 2
const val TAKE_PHOTO_REQUEST_CODE = 3

class UploadImagesViewModel : ViewModel() {
    companion object{
        const val USER_PATH = "user"
        const val LABEL_PACKAGE = "package"
        const val IMAGE_SUFFIX = ".jpg"
        const val IMAGE_PREFIX = "JPEG_"
        const val AUTHORITY_FILE_PROVIDER = "com.melgosadev.examenkotlin.fileprovider"
    }

    private val database = Firebase.database
    private val myRef = database.getReference(USER_PATH)
    private var mUri: Uri? = null

    val messageResult = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()

    /**
     * Abre el explorador de archivos habilitado en el dispositivo, habilitando la selección múltiple
     * de archivos de cualquier tipo
     */
    fun openFileManager(activity: UploadImagesFragment) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "*/*"
        activity.startActivityForResult(intent, FILE_REQUEST_CODE)

    }

    fun manageDataFromIntent(data: Intent){
        val clipData = data.clipData

        //Si la información asociada al intent devuelto no es nula (tiene más de un archivo
        //asociado)
        if (clipData != null) {
            //Por cada item/archivo existente en clipdata, se obtiene la uri y se envia como
            //parámetro para subir los archivos mediante el método uploadFile
            for (i in 0 until clipData.itemCount) {
                val uri = clipData.getItemAt(i).uri
                uri?.let { uploadFile(it, clipData.itemCount, i) }
            }
        } else {
            //Solo se selecionó un archivo, por lo tanto no hay clipdata, y se sube solo
            //un archivo
            val uri = data.data
            uri?.let { uploadFile(it, 1, 1) }
        }
    }

    /**
     * Subir el archivo al storage de Firebase y generar la url para descargar el archivo que se
     * acaba de guardar en el storage
     */
    fun uploadFile(mUri: Uri, totalFiles: Int, index: Int) {
        //Si no existe, se crea la carpeta User en el storage de Firebase, y en ambos  casos
        //(creación o prexistencia) se crea la referencia.
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("User")
        //Se obtiene el ultimo string de la uri donde esta guardado el archivo seleccionado
        val path = mUri.lastPathSegment.toString()
        //Se crea la referencia con el nombre del archivo, quitando el diagonal (/)
        val fileName: StorageReference =
            folder.child(path.substring(path.lastIndexOf('/') + 1))

        //Se sube el archivo al storage, y se espera su respuesta
        fileName.putFile(mUri).addOnSuccessListener {
            fileName.downloadUrl.addOnSuccessListener { uri ->
                val hashMap = HashMap<String, String>()
                hashMap["url"] = java.lang.String.valueOf(uri)

                myRef.child(myRef.push().key.toString()).setValue(hashMap)
                if (totalFiles == 1)
                    messageResult.value = "Archivo subido correctamente"
                else
                    messageResult.value = "Archivo ${index + 1}/$totalFiles subido correctamente"
            }
        }.addOnFailureListener { exception ->
            errorMessage.value = exception.toString()
        }
    }

    fun checkCameraPermissions(activity: UploadImagesFragment){
        if(ContextCompat.checkSelfPermission(
                activity.requireActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ){
            requestCameraPermission(activity)
        }else{
            //Permiso ya esta aceptado
            takePictureIntent(activity)
        }

    }

    fun requestCameraPermission(activity: UploadImagesFragment){
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                activity.requireActivity(),
                Manifest.permission.CAMERA
            )){
            //El usuario ya ha rechazado los permisos, debemos mostrale que debe ir a ajustes
            this.openAppSettings(activity)
        }else{
            //El usuario nunca ha aceptado ni rechazado los permisos, solicitamos permiso
            ActivityCompat.requestPermissions(
                activity.requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun openAppSettings(activity: UploadImagesFragment) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts(LABEL_PACKAGE, activity.requireActivity().packageName, null)
        intent.data = uri
        activity.startActivity(intent)
    }

    fun takePictureIntent(activity: UploadImagesFragment){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity.requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(activity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity.requireActivity(),
                        AUTHORITY_FILE_PROVIDER,
                        it
                    )
                    mUri = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activity.startActivityForResult(
                        takePictureIntent,
                        TAKE_PHOTO_REQUEST_CODE
                    )
                }
            }
        }
    }

    fun createImageFile(activity: UploadImagesFragment): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //Guarda en el directorio privado de la app
        val storageDir: File? = activity.requireActivity().getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            "$IMAGE_PREFIX${timeStamp}_", /* prefix */
            IMAGE_SUFFIX, /* suffix */
            storageDir /* directory */
        )
    }

    fun uploadImageFromCamera(){
        uploadFile(mUri!!, 1, 1)
    }
}