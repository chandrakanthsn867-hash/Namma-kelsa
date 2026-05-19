package com.nammakelasa.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore

class CustomerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var adapter: WorkerAdapter
    private lateinit var etAiSearch: EditText

    private var fullList: MutableList<Worker> = mutableListOf()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        recyclerView = findViewById(R.id.recyclerView)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        chipGroup = findViewById(R.id.chipGroup)
        etAiSearch = findViewById(R.id.etAiSearch)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = WorkerAdapter(mutableListOf())
        recyclerView.adapter = adapter

        // FIND BUTTON SEARCH

        findViewById<Button>(R.id.btnAiSearch).setOnClickListener {

            searchWorkers(etAiSearch.text.toString())
        }

        findViewById<View>(R.id.btnRefresh).setOnClickListener {
            loadWorkers()
        }

        findViewById<Chip>(R.id.chipAll).setOnClickListener {
            filterAndSort("All")
        }

        findViewById<Chip>(R.id.chipPainter).setOnClickListener {
            filterAndSort("Painter")
        }

        findViewById<Chip>(R.id.chipPlumber).setOnClickListener {
            filterAndSort("Plumber")
        }

        findViewById<Chip>(R.id.chipElectrician).setOnClickListener {
            filterAndSort("Electrician")
        }

        findViewById<Chip>(R.id.chipGardener).setOnClickListener {
            filterAndSort("Gardener")
        }

        findViewById<Chip>(R.id.chipCarpenter).setOnClickListener {
            filterAndSort("Carpenter")
        }

        findViewById<Chip>(R.id.chipTiler).setOnClickListener {
            filterAndSort("Tiler")
        }

        // LIVE SEARCH

        etAiSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                searchWorkers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        loadWorkers()
    }

    private fun loadWorkers() {

        db.collection("workers")
            .get()
            .addOnSuccessListener { documents ->

                fullList.clear()

                for (document in documents) {

                    val worker = Worker(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        skill = document.getString("skill") ?: "",
                        daily_rate = document.getString("daily_rate") ?: "",
                        location = document.getString("location") ?: "",
                        phone = document.getString("phone") ?: "",
                        is_available = document.getBoolean("is_available") ?: false
                    )

                    fullList.add(worker)
                }

                filterAndSort("All")

                findViewById<Chip>(R.id.chipAll).isChecked = true

                findViewById<HorizontalScrollView>(
                    R.id.horizontalScrollView
                ).scrollTo(0, 0)
            }

            .addOnFailureListener {

                tvEmptyState.visibility = View.VISIBLE

                tvEmptyState.text = "Failed to load workers"
            }
    }

    private fun filterAndSort(skill: String) {

        val filtered = if (skill == "All") {
            fullList.toList()
        } else {
            fullList.filter {
                it.skill.trim() == skill.trim()
            }
        }

        val sorted = filtered.sortedByDescending {
            it.is_available
        }

        adapter.updateList(sorted.toMutableList())

        if (sorted.isEmpty()) {

            tvEmptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

        } else {

            tvEmptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun searchWorkers(query: String) {

        val filteredList = fullList.filter {

            it.name.contains(query, true) ||
                    it.skill.contains(query, true) ||
                    it.location.contains(query, true)
        }

        adapter.updateList(filteredList.toMutableList())

        if (filteredList.isEmpty()) {

            tvEmptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

        } else {

            tvEmptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}