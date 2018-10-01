package max

import com.google.common.truth.Truth.assertThat
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

fun SpecBody.matcher(pattern: String, body: SpecBody.(Matcher) -> Unit) {
    given("a matcher of '$pattern'") {
        body(Matcher.create(pattern))
    }
}

fun SpecBody.pattern(pattern: String, body: SpecBody.(String) -> Unit) {
    group("on pattern of '$pattern'") {
        body(pattern)
    }
}

fun SpecBody.shouldMatch(matcher: Matcher, pattern: String, vararg captures: Pair<String, Any>) =
    matching(matcher, pattern, true, if (captures.isNotEmpty()) captures.toMap() else null)

fun SpecBody.shouldNotMatch(matcher: Matcher, pattern: String) =
    matching(matcher, pattern, false, null)

private fun SpecBody.matching(matcher: Matcher, pattern: String, matches: Boolean, captures: Map<String, Any>?) {
    pattern(pattern) {
        if (matches) {
            it("should match '$it'") {
                assertThat(matcher.matches(it)).isTrue()
            }
            if (captures != null) {
                it("should capture '$captures'") {
                    val actual = normalizeForComparison(matcher.match(it))
                    val expected = normalizeForComparison(captures)
                    assertThat(actual).isEqualTo(expected)
                }
            }
        } else {
            it("should not match '$it'") {
                assertThat(matcher.matches(it)).isFalse()
            }
        }
    }
}

private fun normalizeForComparison(map: Map<*, *>): Map<*, *> {
    val normalized = map.toMutableMap()
    normalized[Matcher.PARAM_SPLAT] =
            if (normalized.containsKey(Matcher.PARAM_SPLAT))
                (normalized[Matcher.PARAM_SPLAT] as Array<*>).toMutableList()
            else mutableListOf<String>()
    return normalized
}
