package max

import com.google.common.truth.Truth.assertThat
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.it
import java.net.URI

fun SpecBody.shouldHandle(router: TestRouter, uri: URI, vararg captures: Pair<String, Any>) =
    matching(router, uri, true, if (captures.isNotEmpty()) captures.toMap() else null)

fun SpecBody.shouldNotHandle(router: TestRouter, uri: URI) =
    matching(router, uri, false, null)

private fun SpecBody.matching(router: TestRouter, uri: URI, handled: Boolean, captures: Map<String, Any>?) {
    group("on uri of '$uri'") {
        if (handled) {
            it("should handle '$uri'") {
                assertThat(router.handle(uri)).isTrue()
            }
            if (captures != null) {
                it("should capture '$captures'") {
                    val actual = normalizeForComparison(router.params)
                    val expected = normalizeForComparison(captures)
                    assertThat(actual).isEqualTo(expected)
                }
            }
        } else {
            it("should not handle '$uri'") {
                assertThat(router.handle(uri)).isFalse()
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
