package JTD.infrastructure.id

import kotlinx.coroutines.CompletableDeferred


sealed class IdMessage<T>

data class GetIdMessage<T>(val response: CompletableDeferred<T>) : IdMessage<T>()

data class FreeIdMessage<T>(val id: T) : IdMessage<T>()
