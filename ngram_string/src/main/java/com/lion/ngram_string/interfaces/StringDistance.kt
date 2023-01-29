package com.lion.ngram_string.interfaces

import java.io.Serializable

interface StringDistance : Serializable {

    /**
     * Compute and return a measure of distance.
     * Must be &gt;= 0.
     * @param s1
     * @param s2
     * @return
     */
    fun distance(s1: String?, s2: String?): Double

}
