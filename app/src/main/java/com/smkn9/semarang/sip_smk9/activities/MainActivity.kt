package com.smkn9.semarang.sip_smk9.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.util.Pair
import android.util.Size
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.SimilarityClassifier.Recognition
import com.smkn9.semarang.sip_smk9.activities.database.DatabaseAccess
import com.smkn9.semarang.sip_smk9.activities.kirimpresensiwajah.KirimPresensiWajahActivity
import com.smkn9.semarang.sip_smk9.activities.presensi.ResponseInputPresensiHadirPulang
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.helper.Tanggal
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_input_presensi_kehadiran_siswa.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.ReadOnlyBufferException
import java.nio.channels.FileChannel
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import org.tensorflow.lite.Interpreter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.experimental.inv

class MainActivity : AppCompatActivity() {
    var detector: FaceDetector? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    var previewView: PreviewView? = null
    lateinit var face_preview: ImageView
    lateinit var tfLite: Interpreter
    lateinit var reco_name: TextView
    lateinit var preview_info: TextView
    lateinit var recognize: Button
    lateinit var camera_switch: Button
    lateinit var actions: Button
    lateinit var add_face: ImageButton
    var cameraSelector: CameraSelector? = null
    var start = true
    var flipX = false
    var context: Context = this@MainActivity
    var cam_face = CameraSelector.LENS_FACING_FRONT //Default Back Camera
    lateinit var intValues: IntArray
    var inputSize = 112 //Input size for model
    var isModelQuantized = false
    lateinit var embeedings: Array<FloatArray>
    var IMAGE_MEAN = 128.0f
    var IMAGE_STD = 128.0f
    var OUTPUT_SIZE = 192 //Output size of model
    lateinit var cameraProvider: ProcessCameraProvider
    var modelFile = "mobile_face_net.tflite" //model name
    lateinit var pbLoading: ProgressBar
    lateinit var pbProgres: ProgressBar
    var wajahGerak = false
    var kodePresensi: String? = null

    //variabel untuk menyimpan wajah
    private var registered = HashMap<String, Recognition>() //saved Faces
    lateinit var service: ServiceClient
    lateinit var urlPresensiSiswa: String
    lateinit var nis: String
    lateinit var nama: String
    lateinit var jenisPresensi: String
    lateinit var lokasiPresensi:String
    lateinit var noWa:String
    lateinit var pesanA: String
    lateinit var androidId:String

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        urlPresensiSiswa = Siswa.getLinkJurnalKelas(coordinatorLayout)
        nama = Siswa.getNamaSiswa(coordinatorLayout)
        nis = Siswa.getNIS(coordinatorLayout)
        jenisPresensi = intent.getStringExtra(Constant.BUNDLE_DETAIL).toString()
        lokasiPresensi = intent.getStringExtra(Constant.SISWA_LOKASI_PRESENSI).toString()
        noWa = intent.getStringExtra("wa").toString()
        service = ServiceNetwork.getService(coordinatorLayout)

        pbLoading = findViewById(R.id.pb_loading)
        pbProgres = findViewById(R.id.pb_progress)
        pbProgres.setMax(50)
        registered = readFromSP() //Load saved faces from memory when app starts
        face_preview = findViewById(R.id.imageView)
        reco_name = findViewById(R.id.textView)
        preview_info = findViewById(R.id.textView2)

        //ini untuk menambahkan wajah logo +
        add_face = findViewById(R.id.imageButton)
        add_face.setVisibility(View.INVISIBLE)
        face_preview.setVisibility(View.INVISIBLE)
        //ini tombol untuk mengenali wajah
        recognize = findViewById(R.id.button3)
        camera_switch = findViewById(R.id.button5)
        actions = findViewById(R.id.button2)
        //        preview_info.setText("\n        Recognized Face:");

        //Camera Permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE)
        }

        //ini untuk menunjukan list nama yang sudah di catat
        //On-screen Action Button
        actions.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Select Action:")

            // add a checkbox list
            val names = arrayOf(
                "View Recognition List",
                "Update Recognition List",
                "Save Recognitions",
                "Load Recognitions",
                "Clear All Recognitions",
                "Import Photo (Beta)"
            )
            builder.setItems(names) { dialog, which ->
                when (which) {
                    0 -> displaynameListview()
                    1 -> updatenameListview()
                    2 -> insertToSP(registered, false)
                    3 -> registered.putAll(readFromSP())
                    4 -> clearnameList()
                    5 -> loadphoto()
                }
            }
            builder.setPositiveButton("OK") { dialog, which -> }
            builder.setNegativeButton("Cancel", null)

            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        })

        //On-screen switch to toggle between Cameras.
        camera_switch.setOnClickListener(View.OnClickListener {
            if (cam_face == CameraSelector.LENS_FACING_BACK) {
                cam_face = CameraSelector.LENS_FACING_FRONT
                flipX = true
            } else {
                cam_face = CameraSelector.LENS_FACING_BACK
                flipX = false
            }
            cameraProvider!!.unbindAll()
            cameraBind()
        })
        add_face.setOnClickListener(View.OnClickListener { addFace() })

        //langkah 1
        //tombol yang ketika diklik adalah mengenali wajah
        recognize.setOnClickListener(View.OnClickListener {
            if (recognize.getText().toString() == "Recognize") {
                start = true
                recognize.setText("Add Face")
                add_face.setVisibility(View.INVISIBLE)
                reco_name.setVisibility(View.VISIBLE)
                face_preview.setVisibility(View.INVISIBLE)
                preview_info.setText("\n    Recognized Face:")
                //preview_info.setVisibility(View.INVISIBLE);
            } else {
                recognize.setText("Recognize")
                add_face.setVisibility(View.VISIBLE)
                reco_name.setVisibility(View.INVISIBLE)
                face_preview.setVisibility(View.VISIBLE)
                preview_info.setText("1.Bring Face in view of Camera.\n\n2.Your Face preview will appear here.\n\n3.Click Add button to save face.")
            }
        })

        //Load model
        try {
            tfLite = Interpreter(loadModelFile(this@MainActivity, modelFile))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //Initialize Face Detector
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .build()
        detector = FaceDetection.getClient(highAccuracyOpts)


        //ini fungsi yang digunakan untuk melakukan pencocokan wajah
        cameraBind()
    }

    //ini memasukan nama dan wajah ke database
    private fun addFace() {
        run {

            //variabel start digunakan untuk triger mulai atau mati fitur  pencocokan wajah
            start = false
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Masukan NIP atau NIK")

            // Set up the input
            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            // Set up the buttons
            builder.setPositiveButton(
                "ADD",
                DialogInterface.OnClickListener { dialog, which -> //Toast.makeText(context, input.getText().toString(), Toast.LENGTH_SHORT).show();

                    //Create and Initialize new object with Face embeddings and Name.
                    val result = Recognition(
                        "0", "", -1f
                    )

                    //embeedings dari mana ini
                    result.extra = embeedings

                    //daftarkan nama ke dalam data yang terdaftar
                    //ambil nama dari database berdasarkan NIP atau NIK
                    val databaseAccess = DatabaseAccess.getInstance(this@MainActivity)
                    databaseAccess.open()
                    val nip = input.text.toString()
                    val nama = databaseAccess.getNamaPegawai(nip)
                    if (nama != "n/a") {
                        registered[nama] = result
                    } else {
                        Toast.makeText(
                            context,
                            "NIP atau NIK $nip tidak ada dalam database",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }


                    //ini code aslinya
//                    registered.put( input.getText().toString(),result);

                    //menyimpannya ke dalam shareprefered
                    insertToSP(registered, false)

                    //kemudian pengenalan wajah otomatisnya dijalankan lagi
                    start = true
                })
            builder.setNegativeButton("Cancel") { dialog, which ->
                start = true
                dialog.cancel()
            }
            builder.show()
        }
    }

    private fun clearnameList() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Do you want to delete all Recognitions?")
        builder.setPositiveButton("Delete All") { dialog, which ->
            registered.clear()
            Toast.makeText(context, "Recognitions Cleared", Toast.LENGTH_SHORT).show()
        }
        insertToSP(registered, true)
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun updatenameListview() {
        val builder = AlertDialog.Builder(context)
        if (registered.isEmpty()) {
            builder.setTitle("No Faces Added!!")
            builder.setPositiveButton("OK", null)
        } else {
            builder.setTitle("Select Recognition to delete:")

            // add a checkbox list
            val names = arrayOfNulls<String>(registered.size)
            val checkedItems = BooleanArray(registered.size)
            var i = 0
            for ((key) in registered) {
                //System.out.println("NAME"+entry.getKey());
                names[i] = key
                checkedItems[i] = false
                i = i + 1
            }
            builder.setMultiChoiceItems(
                names,
                checkedItems
            ) { dialog, which, isChecked -> // user checked or unchecked a box
                //Toast.makeText(MainActivity.this, names[which], Toast.LENGTH_SHORT).show();
                checkedItems[which] = isChecked
            }
            builder.setPositiveButton("OK") { dialog, which -> // System.out.println("status:"+ Arrays.toString(checkedItems));
                for (i in checkedItems.indices) {
                    //System.out.println("status:"+checkedItems[i]);
                    if (checkedItems[i]) {
//                                Toast.makeText(MainActivity.this, names[i], Toast.LENGTH_SHORT).show();
                        registered.remove(names[i])
                    }
                }
                Toast.makeText(context, "Recognitions Updated", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("Cancel", null)

            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun displaynameListview() {
        val builder = AlertDialog.Builder(context)
        // System.out.println("Registered"+registered);
        if (registered.isEmpty()) builder.setTitle("No Faces Added!!") else builder.setTitle("Recognitions:")

        // add a checkbox list
        val names = arrayOfNulls<String>(registered.size)
        val checkedItems = BooleanArray(registered.size)

//        Log.d("wajah", "displaynameListview: "+registered.size());
        var i = 0
        for ((key) in registered) {
            //System.out.println("NAME"+entry.getKey());
            names[i] = key
            checkedItems[i] = false
            i = i + 1
        }
        builder.setItems(names, null)
        builder.setPositiveButton("OK") { dialog, which -> }

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity, MODEL_FILE: String): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    //Bind camera and preview view
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun cameraBind() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        //ini tampilan gambar utama yang di atas
        previewView = findViewById(R.id.previewView)
        cameraProviderFuture!!.addListener(Runnable {
            try {
                cameraProvider = cameraProviderFuture!!.get()
                bindPreview(cameraProvider)
            } catch (e: ExecutionException) {
                // No errors need to be handled for this in Future.
                // This should never be reached.
            } catch (e: InterruptedException) {
            }
        }, ContextCompat.getMainExecutor(this))
    }

    //menangkap wajah di tampilan
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder()
            .build()
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cam_face)
            .build()
        preview.setSurfaceProvider(previewView!!.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
            .build()
        val executor: Executor = Executors.newSingleThreadExecutor()
        imageAnalysis.setAnalyzer(executor, ImageAnalysis.Analyzer { imageProxy ->
            var image: InputImage? = null
            @SuppressLint("UnsafeExperimentalUsageError") val mediaImage// Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)
                    = imageProxy.image
            if (mediaImage != null) {
                image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                println("Rotation " + imageProxy.imageInfo.rotationDegrees)
            }
            println("ANALYSIS")

            //Process acquired image to detect faces, untuk mendateksi, adakah wajah atau tidak
            val result = detector!!.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.size != 0) {
                        val face = faces[0] //Get first face from detected faces
                        println(face)

                        //ini untuk detect pergerakan di sumbu y
                        val y = face.headEulerAngleY.toDouble()
                        face.getLandmark(FaceLandmark.RIGHT_EYE)
                        ////                                                    float mataKanan = face.getRightEyeOpenProbability();
                        var rightEyeOpenProb = 0.0f
                        if (face.rightEyeOpenProbability != null) {
                            rightEyeOpenProb = face.rightEyeOpenProbability
                        }
                        val angka = Math.ceil(y).toString().replace(".0", "").toInt() * -1
                        Log.d("wajahGerak", "onSuccess: $angka mata kiri : $rightEyeOpenProb")
                        if (angka > 2 && angka < 10) {
                            pbProgres!!.max = 25
                        } else if (angka > 20 && angka < 80) {
                            wajahGerak = true
                            pbProgres!!.max = 50
                        }
                        pbProgres!!.progress = angka

                        //ini awal
                        if (wajahGerak) {
                            //mediaImage to Bitmap
                            val frame_bmp = toBitmap(mediaImage)
                            val rot = imageProxy.imageInfo.rotationDegrees

                            //Adjust orientation of Face
                            val frame_bmp1 = rotateBitmap(frame_bmp, rot, false, false)


                            //Get bounding box of face
                            val boundingBox = RectF(face.boundingBox)

                            //Crop out bounding box from whole Bitmap(image)
                            var cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox)
                            if (flipX) cropped_face = rotateBitmap(cropped_face, 0, flipX, false)
                            //Scale the acquired Face to 112*112 which is required input for model
                            val scaled = getResizedBitmap(cropped_face, 112, 112)
                            if (start) recognizeImage(scaled) //Send scaled bitmap to create face embeddings.
                            println(boundingBox)
                            try {
                                Thread.sleep(10) //Camera preview refreshed every 10 millisec(adjust as required)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }


                        //ini akhir
                    } else {
                        if (registered.isEmpty()) reco_name!!.text =
                            "Add Face" else reco_name!!.text = "No Face Detected!"
                    }
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }
                .addOnCompleteListener {
                    imageProxy.close() //v.important to acquire next frame for analysis
                }
        })
        cameraProvider.bindToLifecycle(
            (this as LifecycleOwner),
            cameraSelector!!,
            imageAnalysis,
            preview
        )
    }

    //ini untuk mengenali wajah secara realtime
    fun recognizeImage(bitmap: Bitmap) {

        // set Face to Preview
        face_preview!!.setImageBitmap(bitmap)

        //Create ByteBuffer to store normalized image
        val imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        imgData.order(ByteOrder.nativeOrder())
        intValues = IntArray(inputSize * inputSize)

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        imgData.rewind()
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues[i * inputSize + j]
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((pixelValue shr 16 and 0xFF).toByte())
                    imgData.put((pixelValue shr 8 and 0xFF).toByte())
                    imgData.put((pixelValue and 0xFF).toByte())
                } else { // Float model
                    imgData.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                }
            }
        }
        //imgData is input to our model
        val inputArray = arrayOf<Any>(imgData)
        val outputMap: MutableMap<Int, Any> = HashMap()
        embeedings =
            Array(1) { FloatArray(OUTPUT_SIZE) } //output of model will be stored in this variable
        outputMap[0] = embeedings
        tfLite!!.runForMultipleInputsOutputs(inputArray, outputMap) //Run model
        var distance = Float.MAX_VALUE
        val id = "0"
        var label: String? = "?"

        //Compare new face with saved Faces.
        if (registered.size > 0) {
            val nearest = findNearest(embeedings[0],nis) //Find closest matching face
            if (nearest != null) {
                val nip = nearest.first
                label = nip
                distance = nearest.second
                //di pengenalan wajah, semakin sedikit selisihnya semakin besar kemungkinan wajahnya mirip
                if (distance < 1.000f) { //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                    start = false
                    reco_name.text = nip
//                    pbLoading.visibility = View.VISIBLE

                    startActivity(intentFor<KirimPresensiWajahActivity>(
                        Constant.BUNDLE_DETAIL to jenisPresensi,
                        Constant.SISWA_LOKASI_PRESENSI to lokasiPresensi,
                        "wa" to noWa

                    ))
                    finish()

//                    inputPresensiHadirPulang()
//                    Call<ResponsePresensi> sendPresensi = service.presensi(
//                            "presensi",
//                            "" + nip
//                    );
//
//                    sendPresensi.enqueue(new Callback<ResponsePresensi>() {
//                        @Override
//                        public void onResponse(Call<ResponsePresensi> call, Response<ResponsePresensi> response) {
//                            pbLoading.setVisibility(View.GONE);
//
//                            wajahGerak = false;
//                            String hasil = response.body().getHasil();
//                            if (hasil.equals("sukses")) {
//                                Toast.makeText(context, "Presensi dengan ID : " +nip + " berhasil", Toast.LENGTH_SHORT).show();
//                                start = true;
//                                finish();
//                            } else {
//                                Toast.makeText(context, "Presensi dengan ID : " + nip + " gagal", Toast.LENGTH_SHORT).show();
//                                start = true;
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponsePresensi> call, Throwable t) {
//                            wajahGerak = false;
//                            pbLoading.setVisibility(View.GONE);
//                            Toast.makeText(context, "Presensi dengan ID : " + nip + " gagal", Toast.LENGTH_SHORT).show();
//                            start = true;
//                        }
//                    });
                } else reco_name!!.text = "Unknown"
                println("nearest: $nip - distance: $distance")
            } else {
                Toast.makeText(
                    this,
                    "Maaf kode Presensi $nis tidak ada dalam database",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }


//            final int numDetectionsOutput = 1;
//            final ArrayList<SimilarityClassifier.Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
//            SimilarityClassifier.Recognition rec = new SimilarityClassifier.Recognition(
//                    id,
//                    label,
//                    distance);
//
//            recognitions.add( rec );
    }

    //    public void register(String name, SimilarityClassifier.Recognition rec) {
    //        registered.put(name, rec);
    //    }
    //Compare Faces by distance between face embeddings
    private fun findNearest(emb: FloatArray, nama: String?): Pair<String, Float>? {
        var ret: Pair<String, Float>? = null
        for ((name, value) in registered) {
            if (name == nama) {
                //ini awal
                val knownEmb = (value.extra as Array<FloatArray>)[0]
                var distance = 0f
                for (i in emb.indices) {
                    val diff = emb[i] - knownEmb[i]
                    distance += diff * diff
                }
                distance = Math.sqrt(distance.toDouble()).toFloat()
                if (ret == null || distance < ret.second) {
                    ret = Pair(name, distance)
                }
                //ini akhir
            }
        }
        return ret
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }

    private fun toBitmap(image: Image?): Bitmap {
        val nv21 = YUV_420_888toNV21(image)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image!!.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
        val imageBytes = out.toByteArray()
        //System.out.println("bytes"+ Arrays.toString(imageBytes));

        //System.out.println("FORMAT"+image.getFormat());
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    //Save Faces to Shared Preferences.Conversion of Recognition objects to json string
    private fun insertToSP(jsonMap: HashMap<String, Recognition>, clear: Boolean) {
        if (clear) jsonMap.clear() else jsonMap.putAll(readFromSP())
        val jsonString = Gson().toJson(jsonMap)
        //        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : jsonMap.entrySet())
//        {
//            System.out.println("Entry Input "+entry.getKey()+" "+  entry.getValue().getExtra());
//        }
        getSharedPreferences("HashMap", MODE_PRIVATE).edit().putString("map", jsonString).apply()
        //        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("map", jsonString);
//        //System.out.println("Input josn"+jsonString.toString());
//        editor.apply();
        Toast.makeText(context, "Recognitions Saved", Toast.LENGTH_SHORT).show()
    }

    //Load Faces from Shared Preferences.Json String to Recognition object
    private fun readFromSP(): HashMap<String, Recognition> {
        val sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE)
        val defValue = Gson().toJson(HashMap<String, Recognition>())
        val json = sharedPreferences.getString("map", defValue)
        // System.out.println("Output json"+json.toString());
        val token: TypeToken<HashMap<String?, Recognition?>?> =
            object : TypeToken<HashMap<String?, Recognition?>?>() {}
        val retrievedMap = Gson().fromJson<HashMap<String, Recognition>>(json, token.type)
        // System.out.println("Output map"+retrievedMap.toString());

        //During type conversion and save/load procedure,format changes(eg float converted to double).
        //So embeddings need to be extracted from it in required format(eg.double to float).
        for ((_, value) in retrievedMap) {
            val output = Array(1) { FloatArray(OUTPUT_SIZE) }
            var arrayList = value.extra as ArrayList<*>
            arrayList = arrayList[0] as ArrayList<*>
            for (counter in arrayList.indices) {
                output[0][counter] = (arrayList[counter] as Double).toFloat()
            }
            value.extra = output

            //System.out.println("Entry output "+entry.getKey()+" "+entry.getValue().getExtra() );
        }
        //        System.out.println("OUTPUT"+ Arrays.deepToString(outut));
        Toast.makeText(context, "Recognitions Loaded", Toast.LENGTH_SHORT).show()
        return retrievedMap
    }

    //Load Photo from phone storage
    private fun loadphoto() {
        start = false
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE)
    }

    //Similar Analyzing Procedure
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                val selectedImageUri = data!!.data
                try {
                    val impphoto = InputImage.fromBitmap(getBitmapFromUri(selectedImageUri), 0)
                    detector!!.process(impphoto).addOnSuccessListener { faces ->
                        if (faces.size != 0) {
                            recognize!!.text = "Recognize"
                            add_face!!.visibility = View.VISIBLE
                            reco_name!!.visibility = View.INVISIBLE
                            face_preview!!.visibility = View.VISIBLE
                            preview_info!!.text =
                                "1.Bring Face in view of Camera.\n\n2.Your Face preview will appear here.\n\n3.Click Add button to save face."
                            val face = faces[0]
                            println(face)

                            //write code to recreate bitmap from source
                            //Write code to show bitmap to canvas
                            var frame_bmp: Bitmap? = null
                            try {
                                frame_bmp = getBitmapFromUri(selectedImageUri)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            val frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false)

                            //face_preview.setImageBitmap(frame_bmp1);
                            val boundingBox = RectF(face.boundingBox)
                            val cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox)
                            val scaled = getResizedBitmap(cropped_face, 112, 112)
                            // face_preview.setImageBitmap(scaled);
                            recognizeImage(scaled)
                            addFace()
                            println(boundingBox)
                            try {
                                Thread.sleep(100)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                    }.addOnFailureListener {
                        start = true
                        Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show()
                    }
                    face_preview!!.setImageBitmap(getBitmapFromUri(selectedImageUri))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri?): Bitmap {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(
            uri!!, "r"
        )
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    companion object {
        private const val SELECT_PICTURE = 1
        private const val MY_CAMERA_REQUEST_CODE = 100
        private fun getCropBitmapByCPU(source: Bitmap?, cropRectF: RectF): Bitmap {
            val resultBitmap = Bitmap.createBitmap(
                cropRectF.width().toInt(),
                cropRectF.height().toInt(), Bitmap.Config.ARGB_8888
            )
            val cavas = Canvas(resultBitmap)

            // draw background
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
            paint.color = Color.WHITE
            cavas.drawRect( //from  w w  w. ja v  a  2s. c  om
                RectF(0F, 0F, cropRectF.width(), cropRectF.height()),
                paint
            )
            val matrix = Matrix()
            matrix.postTranslate(-cropRectF.left, -cropRectF.top)
            cavas.drawBitmap(source!!, matrix, paint)
            if (!source.isRecycled) {
                source.recycle()
            }
            return resultBitmap
        }

        private fun rotateBitmap(
            bitmap: Bitmap?, rotationDegrees: Int, flipX: Boolean, flipY: Boolean
        ): Bitmap {
            val matrix = Matrix()

            // Rotate the image back to straight.
            matrix.postRotate(rotationDegrees.toFloat())

            // Mirror the image along the X or Y axis.
            matrix.postScale(if (flipX) -1.0f else 1.0f, if (flipY) -1.0f else 1.0f)
            val rotatedBitmap =
                Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)

            // Recycle the old bitmap if it has changed.
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            return rotatedBitmap
        }

        //IMPORTANT. If conversion not done ,the toBitmap conversion does not work on some devices.
        private fun YUV_420_888toNV21(image: Image?): ByteArray {
            val width = image!!.width
            val height = image.height
            val ySize = width * height
            val uvSize = width * height / 4
            val nv21 = ByteArray(ySize + uvSize * 2)
            val yBuffer = image.planes[0].buffer // Y
            val uBuffer = image.planes[1].buffer // U
            val vBuffer = image.planes[2].buffer // V
            var rowStride = image.planes[0].rowStride
            assert(image.planes[0].pixelStride == 1)
            var pos = 0
            if (rowStride == width) { // likely
                yBuffer[nv21, 0, ySize]
                pos += ySize
            } else {
                var yBufferPos = -rowStride.toLong() // not an actual position
                while (pos < ySize) {
                    yBufferPos += rowStride.toLong()
                    yBuffer.position(yBufferPos.toInt())
                    yBuffer[nv21, pos, width]
                    pos += width
                }
            }
            rowStride = image.planes[2].rowStride
            val pixelStride = image.planes[2].pixelStride
            assert(rowStride == image.planes[1].rowStride)
            assert(pixelStride == image.planes[1].pixelStride)
            if (pixelStride == 2 && rowStride == width && uBuffer[0] == vBuffer[1]) {
                // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
                val savePixel = vBuffer[1]
                try {
                    vBuffer.put(1, savePixel.inv() as Byte)
                    if (uBuffer[0] == savePixel.inv() as Byte) {
                        vBuffer.put(1, savePixel)
                        vBuffer.position(0)
                        uBuffer.position(0)
                        vBuffer[nv21, ySize, 1]
                        uBuffer[nv21, ySize + 1, uBuffer.remaining()]
                        return nv21 // shortcut
                    }
                } catch (ex: ReadOnlyBufferException) {
                    // unfortunately, we cannot check if vBuffer and uBuffer overlap
                }

                // unfortunately, the check failed. We must save U and V pixel by pixel
                vBuffer.put(1, savePixel)
            }

            // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
            // but performance gain would be less significant
            for (row in 0 until height / 2) {
                for (col in 0 until width / 2) {
                    val vuPos = col * pixelStride + row * rowStride
                    nv21[pos++] = vBuffer[vuPos]
                    nv21[pos++] = uBuffer[vuPos]
                }
            }
            return nv21
        }
    }

    fun inputPresensiHadirPulang() {
//        val pdPresensi = ProgressDialog(this)
//        pdPresensi.setMessage("Mengirim presensi ...")
//        pdPresensi.setCancelable(false)
//        pdPresensi.show()
        pbLoading.visibility = View.VISIBLE

        val sendPresensiHadirPulang = service.sendPresensiHadirPulang(
            "" + urlPresensiSiswa,
            "presensiSiswa",
            "" + Tanggal.getTanggal(),
            "" + Tanggal.getBulan(),
            "" + nama,
            "" + nis,
            ""+androidId+""+nis,
            "" + jenisPresensi,
            ""+lokasiPresensi,
            ""+noWa
        )

        sendPresensiHadirPulang.enqueue(object : Callback<ResponseInputPresensiHadirPulang> {
            override fun onFailure(call: Call<ResponseInputPresensiHadirPulang>, t: Throwable) {
//                pdPresensi.dismiss()
                pbLoading.visibility = View.GONE
                toast("" + t.message)
            }

            override fun onResponse(
                call: Call<ResponseInputPresensiHadirPulang>,
                response: Response<ResponseInputPresensiHadirPulang>
            ) {
                pbLoading.visibility = View.GONE
//                pdPresensi.dismiss()
                val status = response.body()?.hasil

                if (status == "succes") {
                    pesanA = "Selamat presensi berhasil dimasukan"
                }else if(status == "failed") {
                    pesanA = "Presensi Gagal dimasukan"
                }else if(status == "denied") {
                    pesanA = "Presensi Gagal, harap menggunakan Hp yang telah di daftarkan"
                }else if(status == "sekolah") {
                    pesanA = "Presensi Gagal, Anda seharusnya presensi di Sekolah"
                }else if(status == "rumah") {
                    pesanA = "Presensi Gagal, Anda seharusnya presensi di Rumah"
                }else{
                    pesanA = "Presensi Gagal, Anda seharusnya presensi di tempat Magang"
                }

                alert {
                    title = "Konfirmasi"
                    message = pesanA
                    okButton {
                        finish()
                    }
                }.show()
            }

        })
    }
}