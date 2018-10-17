package max

import max.Router.Request
import java.net.URI

class TestRouter private constructor(
    private val handler: Handler,
    private val router: Router<out Request>
) {

    val route get() = handler.route
    val params get() = handler.params

    fun handle(uri: URI): Boolean {
        return router.handle(uri)
    }

    class Handler : Router.Handler<Request> {

        var route = URI("")
        var params = emptyMap<String, Any?>()

        override fun handle(request: Request) {
            this.route = request.route
            this.params = request.params + (Router.SPLAT to request.splat)
        }

    }

    companion object {

        operator fun invoke(init: RouterBody<out Request>.(Handler) -> Unit): TestRouter {
            val handler = Handler()
            return TestRouter(handler, Router { init(handler) })
        }

    }

}
