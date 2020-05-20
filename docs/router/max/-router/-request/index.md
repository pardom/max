[router](../../../index.md) / [max](../../index.md) / [Router](../index.md) / [Request](./index.md)

# Request

(common) `interface Request`

### Types

| Name | Summary |
|---|---|
| (common) [Creator](-creator/index.md) | `interface Creator<T : Request>` |
| (common) [Default](-default/index.md) | `data class Default : Request` |

### Properties

| Name | Summary |
|---|---|
| (common) [params](params.md) | `abstract val params: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| (common) [route](route.md) | `abstract val route: Uri` |
| (common) [splat](splat.md) | `abstract val splat: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
