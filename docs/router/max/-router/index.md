[router](../../index.md) / [max](../index.md) / [Router](./index.md)

# Router

(common) `interface Router<T : Request>`

### Types

| Name | Summary |
|---|---|
| (common) [Builder](-builder/index.md) | `interface Builder<T : Request>` |
| (common) [Handler](-handler/index.md) | `interface Handler<in T : Request>` |
| (common) [Request](-request/index.md) | `interface Request` |

### Functions

| Name | Summary |
|---|---|
| (common) [handle](handle.md) | `abstract fun handle(route: Uri): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| (common) [match](match.md) | `abstract fun match(route: Uri): `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>` |
| (common) [matches](matches.md) | `abstract fun matches(route: Uri): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| (common) [routerFor](router-for.md) | `abstract fun routerFor(route: Uri): `[`Router`](./index.md)`<T>?` |

### Companion Object Properties

| Name | Summary |
|---|---|
| (common) [SPLAT](-s-p-l-a-t.md) | `const val SPLAT: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| (common) [invoke](invoke.md) | `operator fun invoke(init: Builder<out Request>.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Router`](./index.md)`<out Request>`<br>`operator fun <T : Request> invoke(init: Builder<T>.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, creator: Creator<T>): `[`Router`](./index.md)`<T>` |
