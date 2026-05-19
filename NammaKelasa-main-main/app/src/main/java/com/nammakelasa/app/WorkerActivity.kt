package com.nammakelasa.app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class WorkerActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var spinnerSkill: Spinner
    private lateinit var etDailyRate: EditText
    private lateinit var etLocation: EditText
    private lateinit var etPhone: EditText
    private lateinit var switchAvailable: SwitchCompat
    private lateinit var btnSave: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker)

        etName = findViewById(R.id.etName)
        spinnerSkill = findViewById(R.id.spinnerSkill)
        etDailyRate = findViewById(R.id.etDailyRate)
        etLocation = findViewById(R.id.etLocation)
        etPhone = findViewById(R.id.etPhone)
        switchAvailable = findViewById(R.id.switchAvailable)
        btnSave = findViewById(R.id.btnSave)

        val skills = arrayOf(
            "Select Skill",
            "Painter",
            "Plumber",
            "Electrician",
            "Gardener",
            "Carpenter",
            "Tiler"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            skills
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerSkill.adapter = adapter

        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {

        val name = etName.text.toString().trim()
        val skill = spinnerSkill.selectedItem.toString()
        val dailyRate = etDailyRate.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val isAvailable = switchAvailable.isChecked

        if (
            name.isEmpty() ||
            dailyRate.isEmpty() ||
            location.isEmpty() ||
            phone.isEmpty()
        ) {
            Toast.makeText(
                this,
                "Please fill all fields",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (skill == "Select Skill") {
            Toast.makeText(
                this,
                "Please select a skill",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val workerData = hashMapOf(
            "name" to name,
            "skill" to skill,
            "daily_rate" to dailyRate,
            "location" to location,
            "phone" to phone,
            "is_available" to isAvailable,
            "timestamp" to System.currentTimeMillis()
        )

        CoroutineScope(Dispatchers.IO).launch {

            try {

                db.collection("workers")
                    .add(workerData)
                    .await()

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        this@WorkerActivity,
                        "Profile Saved Successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    clearForm()
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        this@WorkerActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun clearForm() {

        etName.text.clear()
        spinnerSkill.setSelection(0)
        etDailyRate.text.clear()
        etLocation.text.clear()
        etPhone.text.clear()
        switchAvailable.isChecked = false
    }
}