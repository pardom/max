package max

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.net.URI

class NavigatorTest : Spek({
    describe("Navigator") {
        val navigator = TestNavigator(Uri.parse("max://navigator/foo")) { handler ->
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

        shouldPush(navigator, Uri.parse("max://navigator/foo/bar"))
        shouldPopToRoot(navigator, Uri.parse("max://navigator/foo"))
        shouldPush(navigator, Uri.parse("max://navigator/foo/bar"))
        shouldPush(navigator, Uri.parse("max://navigator/foo/bar/baz"))
        shouldPop(navigator, Uri.parse("max://navigator/foo/bar"))
        shouldPopTo(navigator, Uri.parse("max://navigator/foo"))
        shouldNotPush(navigator, Uri.parse("max://navigator/bar/foo"))
        shouldNotPopToRoot(navigator, Uri.parse("max://navigator/foo"))
        shouldPush(navigator, Uri.parse("max://navigator/foo/bar/asdf/"), "qux" to "asdf")
    }
})
