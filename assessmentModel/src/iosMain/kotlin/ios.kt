package org.sagebionetworks.assessmentmodel

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import platform.Foundation.*
import platform.posix.uname
import platform.posix.utsname
import kotlin.reflect.KClass

actual class Platform actual constructor() {
    actual val platform: String = "iOS"
}

actual class Product(actual val user: String) {
    val model: String = memScoped {
        val systemInfo = alloc<utsname>()
        uname(systemInfo.ptr)
        systemInfo.machine.toKString()
    }

    fun iosSpecificOperation() {
        println("I am $model")
    }

    override fun toString() = "iOS product of $user for $model"
}

actual object Factory {
    actual fun create(config: Map<String, String>) =
        Product(config["user"]!!)

    actual val platform: String = "ios"
}

actual object UUIDGenerator {
    actual fun uuidString(): String = NSUUID.UUID().UUIDString
}

actual object DateGenerator {
    actual fun nowString(): String = iso8601Formatter.stringFromDate(NSDate.now)

    private val iso8601Formatter: NSDateFormatter = {
        val formatter = NSDateFormatter.new()!!
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
        formatter.locale = NSLocale.localeWithLocaleIdentifier("en_US_POSIX")
        formatter
    }()

    actual fun currentYear(): Int = NSCalendar(NSISO8601Calendar).component(NSCalendarUnitYear, NSDate.now).toInt()
}
