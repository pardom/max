[navigator](../../index.md) / [max](../index.md) / [Navigator](./index.md)

# Navigator

(common) `interface Navigator`

### Types

| Name | Summary |
|---|---|
| (common) [Direction](-direction/index.md) | `enum class Direction` |
| (common) [Request](-request/index.md) | `class Request : Request` |

### Functions

| Name | Summary |
|---|---|
| (common) [param](param.md) | `abstract fun param(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| (common) [params](params.md) | `abstract fun params(): `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| (common) [pop](pop.md) | `abstract fun pop(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| (common) [popTo](pop-to.md) | `abstract fun popTo(uri: Uri): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| (common) [popToRoot](pop-to-root.md) | `abstract fun popToRoot(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| (common) [push](push.md) | `abstract fun push(uri: Uri): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| (common) [routes](routes.md) | `abstract fun routes(): `[`Collection`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)`<Uri>` |
| (common) [set](set.md) | `abstract fun set(vararg routes: Uri): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>`abstract fun set(uris: `[`Collection`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)`<Uri>): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| (common) [splat](splat.md) | `abstract fun splat(): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| (common) [top](top.md) | `abstract fun top(): Uri` |

### Companion Object Properties

| Name | Summary |
|---|---|
| (common) [SPLAT](-s-p-l-a-t.md) | `const val SPLAT: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| (common) [invoke](invoke.md) | `operator fun invoke(initialRoute: Uri, init: Builder<Request>.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Navigator`](./index.md) |
