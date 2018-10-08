package max

import java.net.URI

class TestNavigator(
    private val handler: Handler,
    private val navigator: Navigator
) {

    fun set(routes: Collection<URI>): Boolean {
        return navigator.set(routes)
    }

    fun push(route: URI): Result {
        return Result(
            navigator.push(route),
            handler.request
        )
    }

    fun pop(): Result {
        return Result(
            navigator.pop(),
            handler.request
        )
    }

    fun popTo(route: URI): Result {
        return Result(
            navigator.popTo(route),
            handler.request
        )
    }

    fun popToRoot(): Result {
        return Result(
            navigator.popToRoot(),
            handler.request
        )
    }

    class Handler : Router.Handler<Navigator.Request> {

        lateinit var request: Navigator.Request

        override fun handle(request: Navigator.Request) {
            this.request = request
        }

    }

    class Result(
        val handled: Boolean,
        val request: Navigator.Request
    )

    companion object {

        operator fun invoke(initialRoute: URI, init: RouterBody<Navigator.Request>.(Handler) -> Unit): TestNavigator {
            val handler = Handler()
            return TestNavigator(handler, Navigator(initialRoute) { init(handler) })
        }

    }

}
