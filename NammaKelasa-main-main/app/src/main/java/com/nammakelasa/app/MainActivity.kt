package com.nammakelasa.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    private lateinit var btnWorker: CardView
    private lateinit var btnCustomer: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnWorker = findViewById(R.id.btnWorker)
        btnCustomer = findViewById(R.id.btnCustomer)

        btnWorker.setOnClickListener {
            startActivity(
                Intent(this, WorkerActivity::class.java)
            )
        }

        btnCustomer.setOnClickListener {
            startActivity(
                Intent(this, CustomerActivity::class.java)
            )
        }
    }
}