package max

import com.google.common.truth.Truth.assertThat
import org.spekframework.spek2.style.specification.Suite
import java.net.URI

fun Suite.shouldHandle(router: TestRouter, uri: URI, vararg captures: Pair<String, Any>) =
    matching(router, uri, true, if (captures.isNotEmpty()) captures.toMap() else null)

fun Suite.shouldNotHandle(router: TestRouter, uri: URI) =
    matching(router, uri, false, null)

private fun Suite.matching(router: TestRouter, uri: URI, handled: Boolean, captures: Map<String, Any>?) {
    context("on uri of '$uri'") {
        if (handled) {
            it("it should handle '$uri'") {
                assertThat(router.handle(uri)).isTrue()
            }
            if (captures != null) {
                it("it should capture '$captures'") {
                    val actual = normalizeForComparison(router.params)
                    val expected = normalizeForComparison(captures)
                    assertThat(actual).isEqualTo(expected)
                }
            }
        } else {
            it("it should not handle '$uri'") {
                assertThat(router.handle(uri)).isFalse()
            }
        }
    }
}

private fun normalizeForComparison(map: Map<*, *>): Map<*, *> {
    val normalized = map.toMutableMap()
    normalized[Router.SPLAT] =
            if (normalized.containsKey(Router.SPLAT)) {
                (normalized[Router.SPLAT] as Array<*>).toMutableList()
            } else {
                mutableListOf<String>()
            }
    return normalized
}
