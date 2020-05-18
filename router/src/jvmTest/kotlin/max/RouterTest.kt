package max

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RouterTest : Spek({

    fun normalizeCaptures(captures: Map<String, Any>): Map<String, Any> {
        return if (captures.contains(Matcher.SPLAT)) captures
        else captures + (Matcher.SPLAT to emptyList<String>())
    }

    fun Suite.matching(router: TestRouter, path: Uri, handled: Boolean, captures: Map<String, Any>?) {
        context("on uri of '$path'") {
            if (handled) {
                it("it should handle '$path'") {
                    assertTrue(router.handle(path))
                }
                if (captures != null) {
                    it("it should capture '$captures'") {
                        val expected = normalizeCaptures(captures)
                        val actual = router.params
                        assertEquals(expected, actual)
                    }
                }
            } else {
                it("it should not handle '$path'") {
                    assertFalse(router.handle(path))
                }
            }
        }
    }

    fun Suite.shouldHandle(router: TestRouter, path: String, vararg captures: Pair<String, Any>) =
        matching(router, Uri.parse(path), true, if (captures.isNotEmpty()) captures.toMap() else null)

    fun Suite.shouldNotHandle(router: TestRouter, path: String) =
        matching(router, Uri.parse(path), false, null)

    describe("Router") {
        val router = TestRouter { handler ->
            path("/foo") {
                path("/bar") {
                    route("", handler)
                    route("/:baz/?", handler)
                }
            }
        }
        shouldHandle(router, "/foo/bar")
        shouldNotHandle(router, "/foo/bar/")
        shouldHandle(router, "/foo/bar/baz", "baz" to "baz")
        shouldHandle(router, "/foo/bar/baz/", "baz" to "baz")
    }
})
