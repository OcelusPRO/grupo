package fr.ftnl.grupo.objects

import org.joda.time.DateTime

data class EventTimeInfo(
    val startDateTime: DateTime, val repeatableDays: Int? = null
)