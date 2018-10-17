package max

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.net.URI

class RouterTest : Spek({
    describe("Router") {
        val router = TestRouter { handler ->
            path("/foo") {
                path("/bar") {
                    route("", handler)
                    route("/baz/?", handler)
                }
            }
        }
        shouldHandle(router, URI("max://router/foo/bar"))
        shouldNotHandle(router, URI("max://router/foo/bar/"))
        shouldHandle(router, URI("max://router/foo/bar/baz"))
        shouldHandle(router, URI("max://router/foo/bar/baz/"))
        shouldHandle(router, URI("max://router/foo/bar?a=1&b=2&c=3"), "a" to "1", "b" to "2", "c" to "3")
    }
})
