package max

data class Uri(
    val scheme: String?,
    val authority: String?,
    val path: String,
    val query: String?,
    val fragment: String?
) {

    val userInfo: String? by lazy {
        authority
            ?.substringBefore('@', "")
            ?.ifEmpty { null }
    }

    val host: String? by lazy {
        authority
            ?.substringAfter('@')
            ?.substringBefore(':')
    }

    val port: Int? by lazy {
        authority
            ?.substringAfterLast(':', "")
            ?.ifEmpty { null }
            ?.toIntOrNull()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        if (scheme != null) {
            sb.append(scheme)
            sb.append(':')
        }
        if (authority != null) {
            sb.append("//")
            sb.append(authority)
        }
        sb.append(path)
        if (query != null) {
            sb.append('?')
            sb.append(query)
        }
        if (fragment != null) {
            sb.append('#')
            sb.append(fragment)
        }
        return sb.toString()
    }

    companion object {

        private val regex = Regex("""^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?""")

        fun parse(uri: String): Uri {
            val values = regex.find(uri)?.groupValues
                ?: throw IllegalArgumentException("Invalid Uri: $uri")
            return Uri(
                values[2].ifEmpty { null },
                values[4].ifEmpty { null },
                values[5],
                values[7].ifEmpty { null },
                values[9].ifEmpty { null }
            )
        }
    }
}
