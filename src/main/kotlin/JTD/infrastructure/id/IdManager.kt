package JTD.infrastructure.id

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import java.util.*


interface IdManager<T> {
    suspend fun generateId(): T
    suspend fun freeId(id: T)
}


class DefaultIdManager<T>(init: MutableCollection<T>.() -> Unit) : IdManager<T> {
    private val idActor = GlobalScope.idActor(init)

    override suspend fun freeId(id: T) {
        idActor.send(FreeIdMessage(id))
    }

    override suspend fun generateId(): T {
        val id = CompletableDeferred<T>()
        idActor.send(GetIdMessage(id))
        return id.await()
    }
}


fun <T> CoroutineScope.idActor(init: MutableCollection<T>.() -> Unit) = actor<IdMessage<T>> {
    val availableIds = LinkedList<T>()
    availableIds.apply(init)

    for (mssg in channel) {
        when (mssg) {

            is GetIdMessage -> {
                if (availableIds.size == 0) {
                    mssg.response.completeExceptionally(GameIdsRanOut())
                } else {
                    mssg.response.complete(availableIds.remove())
                }
            }

            is FreeIdMessage -> availableIds.add(mssg.id)
        }
    }
}
