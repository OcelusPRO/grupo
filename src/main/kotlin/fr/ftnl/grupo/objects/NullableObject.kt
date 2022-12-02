package fr.ftnl.grupo.objects

/**
 * Represents a nullable object.
 *
 * @param T [Any] type
 * @property value [T] value
 */
data class NullableObject<T>(val value: T?)
