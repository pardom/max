package max

class TestNavigator(
    private val handler: Handler,
    private val navigator: Navigator
) {

    fun set(routes: Collection<Uri>): Boolean {
        return navigator.set(routes)
    }

    fun push(route: Uri): Result {
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

    fun popTo(route: Uri): Result {
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

        operator fun invoke(
            initialRoute: Uri,
            init: Router.Builder<Navigator.Request>.(Handler) -> Unit
        ): TestNavigator {
            val handler = Handler()
            return TestNavigator(handler, Navigator(initialRoute) { init(handler) })
        }

    }

}
