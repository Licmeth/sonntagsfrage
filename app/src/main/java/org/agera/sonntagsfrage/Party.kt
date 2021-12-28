package org.agera.sonntagsfrage

enum class Party {
    SPD {
        override fun getLabel() = "SPD"
        override fun getColor() = 0xff_ff_00_00.toInt()
    },
    UNION {
        override fun getLabel() = "Union"
        override fun getColor() = 0xff_00_00_00.toInt()
    },
    GRUENE {
        override fun getLabel() = "Grüne"
        override fun getColor() = 0xff_00_ff_00.toInt()
    },
    FDP {
        override fun getLabel() = "FDP"
        override fun getColor() = 0xff_ff_ff_00.toInt()
    },
    LINKE {
        override fun getLabel() = "Linke"
        override fun getColor() = 0xff_ff_00_ff.toInt()
    },
    AFD {
        override fun getLabel() = "AFD"
        override fun getColor() = 0xff_ff_ff_00.toInt()
    },
    OTHERS {
        override fun getLabel() = "Sonstige"
        override fun getColor() = 0xff_d0_d0_d0.toInt()
    };

    abstract fun getLabel(): String
    abstract fun getColor(): Int
}