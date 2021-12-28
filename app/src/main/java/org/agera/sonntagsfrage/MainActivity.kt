package org.agera.sonntagsfrage

import InstituteData
import SurveyResult
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.io.Console

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var preferencesManager: PreferencesManager

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var instituteSpinner: Spinner

    lateinit var surveys: MutableList<InstituteData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Preferences
        preferencesManager = PreferencesManager(dataStore)

        // UI
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        instituteSpinner = navigationView.getHeaderView(0).findViewById(R.id.institute_spinner)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer_menu, R.string.close_drawer_menu)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener { toggleMenuItemChecked(it) }
        navigationView.menu.forEach { item ->
            run {
                val switch: CompoundButton? = item.actionView as? CompoundButton
                switch?.setOnClickListener { toggleMenuItemChecked(item) }
                //switch?.setOnCheckedChangeListener { _, checked -> setMenuItemChecked(item, checked) }
            }
        }
        instituteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                lifecycleScope.launch { preferencesManager.saveSelectedInstitute(p2) }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Institute.values())
        instituteSpinner.adapter = adapter

        observePreferences()




        // Fetch survey data
        surveys = SonntagsfrageScraper.listSurveys()

        lifecycleScope.launch{ refreshChart() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun refreshChart() {
        val preferences = preferencesManager.getPreferences()
        val parties = preferences.first
        val selectedInstituteIndex = preferences.second

        val lineData = LineData(parties.map { party -> mapToLineDataSet(surveys[selectedInstituteIndex], { s -> Entry(s.date.toEpochDay().toFloat(), s.result.getOrDefault(party, Float.NaN)) }, party.getLabel(), party.getColor()) })

        val chart = findViewById<LineChart>(R.id.chart)
        chart.data = lineData
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.valueFormatter = DateAxisFormatter()
        chart.invalidate()  // refresh chart
    }

    private fun observePreferences() {
        preferencesManager.selectedInstitute.asLiveData().observe(this, { instituteSpinner.setSelection(it) })
        preferencesManager.showSpd.asLiveData().observe(this, { setSwitch(navigationView.menu.getItem(0), it) })
        preferencesManager.showUnion.asLiveData().observe(this, { setSwitch(navigationView.menu.getItem(1), it) })
        preferencesManager.showGruene.asLiveData().observe(this, { setSwitch(navigationView.menu.getItem(2), it) })
        preferencesManager.showFdp.asLiveData().observe(this, { setSwitch(navigationView.menu.getItem(3), it) })
        preferencesManager.showLinke.asLiveData().observe(this, { setSwitch(navigationView.menu.getItem(4), it) })
        preferencesManager.showAfd.asLiveData().observe(this, { setSwitch(navigationView.menu.getItem(5), it) })
        preferencesManager.showOthers.asLiveData().observe(this, { setSwitch(navigationView.menu.getItem(6), it) })
    }

    private fun setSwitch(menuItem: MenuItem?, active: Boolean) {
        val switch: CompoundButton? = menuItem?.actionView as? CompoundButton
        switch?.isChecked = active
        lifecycleScope.launch{ refreshChart() }
    }

    private fun toggleMenuItemChecked(item: MenuItem): Boolean {
        val switch: CompoundButton? = item.actionView as? CompoundButton
        if(switch != null) {
            setMenuItemChecked(item, !switch.isChecked)
        }
        return true
    }

    private fun setMenuItemChecked(item: MenuItem, checked: Boolean) {
        val switch: CompoundButton? = item.actionView as? CompoundButton
        if(switch != null && switch.isChecked != checked) {
            switch.isChecked = checked
            lifecycleScope.launch {
                when(item.itemId) {
                    R.id.miSpd -> preferencesManager.saveShowSpd(switch.isChecked)
                    R.id.miUnion -> preferencesManager.saveShowUnion(switch.isChecked)
                    R.id.miGruene -> preferencesManager.saveShowGruene(switch.isChecked)
                    R.id.miFdp -> preferencesManager.saveShowFdp(switch.isChecked)
                    R.id.miLinke -> preferencesManager.saveShowLinke(switch.isChecked)
                    R.id.miAfd -> preferencesManager.saveShowAfd(switch.isChecked)
                    R.id.miOthers -> preferencesManager.saveShowOthers(switch.isChecked)
                }
            }
        }
    }

    private fun mapToLineDataSet(data: InstituteData, action: (SurveyResult) -> Entry, label: String, color: Int): LineDataSet {
        val entries = ArrayList<Entry>()
        data.surveyResults.forEach { entries.add(action(it)) }

        val lineDataSet = LineDataSet(entries, label)
        lineDataSet.color = color
        lineDataSet.lineWidth = 3f
        lineDataSet.setDrawCircles(false)

        return lineDataSet
    }
}