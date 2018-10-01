package max

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import java.net.URI

class NavigatorTest : Spek({
    describe("Navigator") {
        given("a navigator") {
            val navigator = TestNavigator(URI("max://navigator/foo")) { handler ->
                path("/foo") {
                    route("/?", handler)
                    path("/bar") {
                        route("/?", handler)
                        route("/baz/?", handler)

                        path("/:qux") {
                            route("/?", handler)
                        }
                    }
                }
            }
            shouldPush(navigator, URI("max://navigator/foo/bar"))
            shouldPush(navigator, URI("max://navigator/foo/bar/baz"))
            shouldPop(navigator, URI("max://navigator/foo/bar"))
            shouldPopTo(navigator, URI("max://navigator/foo"))
            shouldNotPush(navigator, URI("max://navigator/bar/foo"))
            shouldPopToRoot(navigator, URI("max://navigator/foo"))
            shouldPush(navigator, URI("max://navigator/foo/bar/asdf/"), "qux" to "asdf")
        }
    }
})
