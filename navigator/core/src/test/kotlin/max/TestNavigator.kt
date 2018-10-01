package max

import java.net.URI

class TestNavigator(
    private val handler: Handler,
    private val navigator: Navigator
) {

    val route get() = handler.route
    val params get() = handler.params
    val direction get() = handler.direction

    fun set(routes: Collection<URI>): Boolean {
        return navigator.set(routes)
    }

    fun push(route: URI): Boolean {
        return navigator.push(route)
    }

    fun pop(): Boolean {
        return navigator.pop()
    }

    fun popTo(route: URI): Boolean {
        return navigator.popTo(route)
    }

    fun popToRoot(): Boolean {
        return navigator.popToRoot()
    }

    class Handler : Router.Handler<Navigator.Request> {

        var route = URI("")
        var params = emptyMap<String, Any?>()
        var direction = Navigator.Direction.REPLACE

        override fun handle(request: Navigator.Request) {
            this.route = request.route
            this.params = request.params
            this.direction = request.direction
        }

    }

    companion object {

        operator fun invoke(initialRoute: URI, init: RouterBody<Navigator.Request>.(Handler) -> Unit): TestNavigator {
            val handler = Handler()
            return TestNavigator(handler, Navigator(initialRoute) { init(handler) })
        }

    }
}
