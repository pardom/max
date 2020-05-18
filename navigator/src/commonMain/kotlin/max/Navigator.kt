package max

interface Navigator {

    fun routes(): Collection<Uri>

    fun top(): Uri

    fun params(): Map<String, String>

    fun param(name: String): String?

    fun splat(): List<String>

    fun set(vararg routes: Uri): Boolean

    fun set(uris: Collection<Uri>): Boolean

    fun push(uri: Uri): Boolean

    fun pop(): Boolean

    fun popTo(uri: Uri): Boolean

    fun popToRoot(): Boolean

    enum class Direction {
        FORWARD, BACKWARD, REPLACE
    }

    class Request(
        override val route: Uri,
        override val params: Map<String, String>,
        override val splat: List<String>,
        val prevRoute: Uri,
        val prevParams: Map<String, String>,
        val prevSplat: List<String>,
        val direction: Direction
    ) : Router.Request {

        class Creator : Router.Request.Creator<Request> {

            private var route = Uri.parse("")
            private var params = emptyMap<String, String>()
            private var splat = emptyList<String>()
            private var direction = Direction.REPLACE

            fun onSet() {
                direction = Direction.REPLACE
            }

            fun onPush() {
                direction = Direction.FORWARD
            }

            fun onPop() {
                direction = Direction.BACKWARD
            }

            override fun create(route: Uri, params: Map<String, String>, splat: List<String>): Request {
                val request = Request(route, params, splat, this.route, this.params, this.splat, direction)
                this.route = route
                this.params = params
                this.splat = splat
                return request
            }

        }

    }

    private class Impl(
        initialRoute: Uri,
        private val router: Router<Request>,
        private val creator: Request.Creator
    ) : Navigator {

        private val stack = mutableListOf<Uri>()

        init {
            set(initialRoute)
        }

        override fun routes(): Collection<Uri> {
            return stack
        }

        override fun top(): Uri {
            return routes().lastOrNull()
                ?: throw IllegalStateException("Route stack is empty.")
        }

        override fun params(): Map<String, String> {
            val params = router.routerFor(top())?.match(top())
                ?: throw IllegalStateException("Top route not found in router.")
            return Matcher.params(params)
        }

        override fun param(name: String): String? {
            return params()[name]
        }

        override fun splat(): List<String> {
            return Matcher.splat(params())
        }

        override fun set(vararg routes: Uri): Boolean {
            return set(routes.toList())
        }

        override fun set(uris: Collection<Uri>): Boolean {
            if (uris.isEmpty()) {
                throw IllegalArgumentException("Navigator stack must not be empty.")
            }
            for (route in uris) {
                if (!hasRouter(route)) {
                    return false
                }
            }
            stack.clear()
            stack.addAll(uris)
            creator.onSet()
            router.handle(top())
            return true
        }

        override fun push(uri: Uri): Boolean {
            if (top() == uri) {
                return false
            }
            if (!hasRouter(uri)) {
                return false
            }
            stack.add(uri)
            creator.onPush()
            router.handle(uri)
            return true
        }

        override fun pop(): Boolean {
            if (stack.size <= 1) {
                return false
            }
            stack.removeAt(stack.lastIndex)
            creator.onPop()
            router.handle(top())
            return true
        }

        override fun popTo(uri: Uri): Boolean {
            if (!stack.contains(uri)) {
                return false
            }
            while (stack.size > 1 && top() != uri) {
                stack.removeAt(stack.lastIndex)
            }
            creator.onPop()
            router.handle(top())
            return true
        }

        override fun popToRoot(): Boolean {
            if (stack.size == 1) {
                return false
            }
            return popTo(stack.first())
        }

        private fun hasRouter(route: Uri): Boolean {
            return router.routerFor(route) != null
        }

    }

    companion object {

        const val SPLAT = Matcher.SPLAT

        operator fun invoke(initialRoute: Uri, init: Router.Builder<Request>.() -> Unit): Navigator {
            val creator = Request.Creator()
            return Impl(initialRoute, Router(init, creator), creator)
        }

    }

}
