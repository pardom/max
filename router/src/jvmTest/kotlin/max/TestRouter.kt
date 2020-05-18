package max

import max.Router.Request

class TestRouter private constructor(
    private val handler: Handler,
    private val router: Router<out Request>
) {

    val route get() = handler.route
    val params get() = handler.params

    fun handle(path: Uri): Boolean {
        return router.handle(path)
    }

    class Handler : Router.Handler<Request> {

        var route = Uri.parse("")
        var params = emptyMap<String, Any?>()

        override fun handle(request: Request) {
            this.route = request.route
            this.params = request.params + (Router.SPLAT to request.splat)
        }

    }

    companion object {

        operator fun invoke(init: Router.Builder<out Request>.(Handler) -> Unit): TestRouter {
            val handler = Handler()
            return TestRouter(handler, Router { init(handler) })
        }

    }

}
