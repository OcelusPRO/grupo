package fr.ftnl.grupo.database.abstract

import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.toEntity
import org.joda.time.DateTime

abstract class BaseIntEntityClass<E : BaseIntEntity>(table: BaseIntIdTable) : IntEntityClass<E>(table) {
    
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    action.toEntity(this)?.updatedAt = DateTime.now()
                } catch (ignored: Exception) { /* ignored */
                }
            }
        }
    }
}