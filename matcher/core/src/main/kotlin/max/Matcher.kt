package max

import java.lang.IllegalArgumentException

interface Matcher {

    fun match(path: String): Map<String, Any>

    fun matches(path: String): Boolean

    fun create(path: String): Matcher

    private class Impl(val names: List<String>, val regex: Regex) : Matcher {

        override fun match(path: String): Map<String, Any> {
            val match = regex.matchEntire(path) ?: return emptyMap()
            val groupValues = match.groupValues.drop(1)
            val map = mutableMapOf<String, Any>(SPLAT to arrayOf<String>())
            for ((i, groupValue) in groupValues.withIndex()) {
                val name = names[i]
                if (name == SPLAT) {
                    @Suppress("UNCHECKED_CAST")
                    map[name] = (map[SPLAT] as Array<String>) + groupValue
                } else {
                    map[name] = groupValue
                }
            }
            return map
        }

        override fun matches(path: String): Boolean {
            return regex.matches(path)
        }

        override fun create(path: String): Matcher {
            val sb = StringBuilder(regex.pattern)
            val names = names.toMutableList()
            val matchResults = PARAM_REGEX.findAll(path)
            for (matchResult in matchResults) {
                val value = matchResult.value
                when (value.first()) {
                    '/' -> {
                        sb.append("\\/")
                        sb.append(if (value.endsWith('?')) '*' else '+')
                    }
                    ':' -> {
                        sb.append("([^\\/:\\*\\?]")
                        sb.append(if (value.endsWith('?')) '*' else '+')
                        sb.append(")")
                        names.add(value.removePrefix(":").removeSuffix("?"))
                    }
                    '*' -> {
                        sb.append("(.*?)")
                        names.add(SPLAT)
                    }
                    else -> {
                        value.forEach { char ->
                            sb.append(encode(char))
                        }
                    }
                }
            }
            return Impl(names, Regex(sb.toString()))
        }

        companion object {

            private const val PATTERN_LITERAL = "([^\\/:\\*]+)"
            private const val PATTERN_PATH = "(\\/\\??)"
            private const val PATTERN_PARAM = "(:[a-zA-Z0-9_]+\\??)"
            private const val PATTERN_SPLAT = "(\\*)"

            private val PARAM_REGEX = Regex("$PATTERN_LITERAL|$PATTERN_PATH|$PATTERN_PARAM|$PATTERN_SPLAT")

            private fun encode(char: Char): String {
                if (char == '?') return char.toString()
                val escaped = Regex.escape(char.toString())
                val encodedUpper = pctEncode(char)
                val encodedLower = encodedUpper.toLowerCase()
                if (char == ' ') return "(?:$escaped|$encodedUpper|$encodedLower|${encode('+')})"
                return "(?:$escaped|$encodedUpper|$encodedLower)"
            }

            private fun pctEncode(c: Char): String {
                val cInt = c.toInt()
                val encoded = when {
                    cInt < 128 -> {
                        arrayOf(cInt)
                    }
                    cInt in 128..2048 -> {
                        arrayOf(
                            (cInt shr 6) or 192,
                            (cInt and 63) or 128
                        )
                    }
                    else -> {
                        arrayOf(
                            (cInt shr 12) or 224,
                            ((cInt shr 6) and 63) or 128,
                            (cInt and 63) or 128
                        )
                    }
                }
                val sb = StringBuilder()
                for (char in encoded) {
                    sb.append('%')
                    sb.append(Integer.toHexString(char).toUpperCase())
                }
                return sb.toString()
            }

        }

    }

    companion object {

        const val SPLAT = "splat"

        fun empty(): Matcher {
            return create("")
        }

        fun create(path: String): Matcher {
            return Impl(emptyList(), Regex("")).create(path)
        }

        fun params(params: Map<String, Any>): Map<String, String> {
            @Suppress("UNCHECKED_CAST")
            return (params - SPLAT) as? Map<String, String>
                ?: throw IllegalArgumentException("Input contains non-string values: $params")
        }

        fun splat(params: Map<String, Any>): Array<String> {
            @Suppress("UNCHECKED_CAST")
            return params[SPLAT] as? Array<String>
                ?: throw IllegalArgumentException("Input did not contain \"$SPLAT\" entry: $params")
        }

    }

}
