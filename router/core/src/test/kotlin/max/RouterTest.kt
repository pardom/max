package max

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import java.net.URI

class RouterTest : Spek({
    describe("Router") {
        given("a router") {
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
            shouldHandle(router, URI("max://router/foo/bar?baz=qux"), "baz" to "qux")
        }
    }
})
