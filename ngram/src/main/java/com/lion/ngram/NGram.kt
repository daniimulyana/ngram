package com.lion.ngram

import com.lion.ngram.interfaces.NormalizedStringDistance

/**
 * N-Gram Similarity as defined by Kondrak, "N-Gram Similarity and Distance",
 * String Processing and Information Retrieval, Lecture Notes in Computer
 * Science Volume 3772, 2005, pp 115-126.
 *
 * The algorithm uses affixing with special character '\n' to increase the
 * weight of first characters. The normalization is achieved by dividing the
 * total similarity score the original length of the longest word.
 *
 * http://webdocs.cs.ualberta.ca/~kondrak/papers/spire05.pdf
 */

/**
 * Instantiate with default value for n-gram length (1).
 */
class NGram : NormalizedStringDistance {
    private val n: Int

    /**
     * Instantiate with given value for n-gram length.
     * @param n
     */
    constructor(n: Int) {
        this.n = n
    }

    /**
     * Instantiate with default value for n-gram length (2).
     */
    constructor() {
        n = DEFAULT_N
    }

    /**
     * Compute n-gram distance.
     * @param s0 The first string to compare.
     * @param s1 The second string to compare.
     * @return The computed n-gram distance in the range [0, 1]
     * @throws NullPointerException if s0 or s1 is null.
     */
    override fun distance(s0: String?, s1: String?): Double {
        if (s0 == null) {
            throw NullPointerException("source must not be null")
        }

        if (s1 == null) {
            throw NullPointerException("target must not be null")
        }

        if (s0 == s1) {
            return 0.0
        }

        val special = '\n'
        val sourceLength = s0.length
        val targetLength = s1.length

        /**
         * return 1.0 when one of the string is empty
         */
        if (sourceLength == 0 || targetLength == 0) {
            return 1.0
        }

        var cost = 0

        /**
         * handle if one of the string length is less than n
         */
        if (sourceLength < n || targetLength < n) {
            val shortestStringLength = Math.min(sourceLength, targetLength)
            for (i in 0 until shortestStringLength) {
                if (s0[i] == s1[i]) {
                    cost++
                }
            }
            return (cost.toDouble() / Math.max(sourceLength, targetLength))
        }

        val sourceArray = CharArray(sourceLength + n - 1)

        //'previous' cost array, horizontally
        var previousArray = DoubleArray(sourceLength + 1)

        // cost array, horizontally
        var distanceArray = DoubleArray(sourceLength + 1)

        var tempDistanceArray: DoubleArray //placeholder to assist in swapping previousArray and distanceArray

        /**
         * construct sourceArray with prefix
         */
        for (i in sourceArray.indices) {
            if (i < n - 1) {
                sourceArray[i] = special //add prefix
            } else {
                sourceArray[i] = s0[i - n + 1]
            }
        }

        var targetArray = CharArray(n) // jth n-gram of t

        for (sourceIndex in 0..sourceLength) {
            previousArray[sourceIndex] = sourceIndex.toDouble()
        }

        for (targetIndex in 1..targetLength) {
            //construct target array
            if (targetIndex < n) {
                for (ti in 0 until n - targetIndex) {
                    targetArray[ti] = special //add prefix
                }
                for (ti in n - targetIndex until n) {
                    targetArray[ti] = s1[ti - (n - targetIndex)]
                }
            } else {
                targetArray = s1.substring(targetIndex - n, targetIndex).toCharArray()
            }
            distanceArray[0] = targetIndex.toDouble()

            for(sourceIndex in 1..sourceLength) {
                cost = 0
                var tn = n
                //compare sourceArray to targetArray
                for (ni in 0 until n) {
                    if (sourceArray[sourceIndex - 1 + ni] != targetArray[ni]) {
                        cost++
                    } else if (sourceArray[sourceIndex - 1 + ni] == special) {
                        //discount matches on prefix
                        tn--
                    }
                }
                val ec = cost.toFloat() / tn
                // minimum of cell to the left+1, to the top+1,
                // diagonally left and up +cost
                distanceArray[sourceIndex] = Math.min(
                    Math.min(
                        distanceArray[sourceIndex - 1] + 1,
                        previousArray[sourceIndex] + 1
                    ),
                    previousArray[sourceIndex - 1] + ec
                )
            }

            // copy current distance counts to 'previous row' distance counts
            tempDistanceArray = previousArray
            previousArray = distanceArray
            distanceArray = tempDistanceArray
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return (previousArray[sourceLength] / Math.max(targetLength, sourceLength))
    }

    companion object {
        private const val DEFAULT_N = 1
    }
}