package org.agera.sonntagsfrage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferencesManager(private val dataStore: DataStore<Preferences>) {
    val selectedInstitute: Flow<Int>
        get() = dataStore.data.map { pref -> pref[SELECTED_INSTITUTE] ?: 0 }

    val showSpd: Flow<Boolean>
        get() = dataStore.data.map { pref -> pref[SHOW_SPD] ?: true }

    val showUnion: Flow<Boolean>
        get() = dataStore.data.map { pref -> pref[SHOW_UNION] ?: true }

    val showGruene: Flow<Boolean>
        get() = dataStore.data.map { pref -> pref[SHOW_GRUENE] ?: true }

    val showFdp: Flow<Boolean>
        get() = dataStore.data.map { pref -> pref[SHOW_FDP] ?: true }

    val showLinke: Flow<Boolean>
        get() = dataStore.data.map { pref -> pref[SHOW_LINKE] ?: true }

    val showAfd: Flow<Boolean>
        get() = dataStore.data.map { pref -> pref[SHOW_AFD] ?: true }

    val showOthers: Flow<Boolean>
        get() = dataStore.data.map { pref -> pref[SHOW_OTHERS] ?: true }

    suspend fun getPreferences(): Pair<Array<Party>, Int> {
        val state = dataStore.data.first()

        val parties = ArrayList<Party>()
        if(state[SHOW_SPD] != false) parties.add(Party.SPD)
        if(state[SHOW_UNION] != false) parties.add(Party.UNION)
        if(state[SHOW_GRUENE] != false) parties.add(Party.GRUENE)
        if(state[SHOW_FDP] != false) parties.add(Party.FDP)
        if(state[SHOW_LINKE] != false) parties.add(Party.LINKE)
        if(state[SHOW_AFD] != false) parties.add(Party.AFD)
        if(state[SHOW_OTHERS] != false) parties.add(Party.OTHERS)

        return Pair(parties.toTypedArray(), state[SELECTED_INSTITUTE] ?: 0)
    }

    suspend fun saveSelectedInstitute(instituteId: Int) {
        dataStore.edit { pref -> pref[SELECTED_INSTITUTE] = instituteId }
    }

    suspend fun saveShowSpd(showSpd: Boolean) {
        dataStore.edit { pref -> pref[SHOW_SPD] = showSpd }
    }

    suspend fun saveShowUnion(showUnion: Boolean) {
        dataStore.edit { pref -> pref[SHOW_UNION] = showUnion }
    }

    suspend fun saveShowGruene(showGruene: Boolean) {
        dataStore.edit { pref -> pref[SHOW_GRUENE] = showGruene }
    }

    suspend fun saveShowFdp(showFdp: Boolean) {
        dataStore.edit { pref -> pref[SHOW_FDP] = showFdp }
    }

    suspend fun saveShowLinke(showLinke: Boolean) {
        dataStore.edit { pref -> pref[SHOW_LINKE] = showLinke }
    }

    suspend fun saveShowAfd(showAfd: Boolean) {
        dataStore.edit { pref -> pref[SHOW_AFD] = showAfd }
    }

    suspend fun saveShowOthers(showOthers: Boolean) {
        dataStore.edit { pref -> pref[SHOW_OTHERS] = showOthers }
    }

    companion object {
        val SELECTED_INSTITUTE = intPreferencesKey("selected_institute")
        val SHOW_SPD = booleanPreferencesKey("show_spd")
        val SHOW_UNION = booleanPreferencesKey("show_union")
        val SHOW_GRUENE = booleanPreferencesKey("show_gruene")
        val SHOW_FDP = booleanPreferencesKey("show_fdp")
        val SHOW_LINKE = booleanPreferencesKey("show_linke")
        val SHOW_AFD = booleanPreferencesKey("show_afd")
        val SHOW_OTHERS = booleanPreferencesKey("show_others")
    }
}