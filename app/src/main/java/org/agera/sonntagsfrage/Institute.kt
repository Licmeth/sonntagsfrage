import org.agera.sonntagsfrage.Party

enum class Institute{
    FORSA {
        override fun getLabel() = "Forsa"
        override fun getPath() = "/umfragen/forsa.htm"
    },
    ALLENSBACH {
        override fun getLabel() = "Allensbach"
        override fun getPath() = "/umfragen/allensbach.htm"
    },
    POLITBAROMETER {
        override fun getLabel() = "Forschungsgruppe Wahlen"
        override fun getPath() = "/umfragen/politbarometer.htm"
    },
    DIMAP {
        override fun getLabel() = "Infratest Dimap"
        override fun getPath() = "/umfragen/dimap.htm"
    };

    fun getPartyColumnIndex(party: Party): Int {
        return when (party) {
            Party.SPD -> 3
            Party.UNION -> 2
            Party.GRUENE -> 4
            Party.FDP -> 5
            Party.LINKE -> 6
            Party.AFD -> 7
            Party.OTHERS -> 8
            else -> 0
        }
    }
    fun getDateColumnIndex(): Int = 0

    abstract fun getLabel(): String
    abstract fun getPath(): String

    override fun toString(): String {
        return getLabel()
    }
}