package com.ogata_k.mobile.code_lab.common

import org.junit.Assert.assertEquals
import org.junit.Test

class ObjectFormatterTest {

    sealed interface TestIntent {
        data object NavigateToDetail : TestIntent
        data class Search(val query: String) : TestIntent
    }

    data object TopLevelLikeObject

    @Test
    fun formatAsSimple_SealedDataObject_Depth2() {
        val result = ObjectFormatter.formatAsSimple(TestIntent.NavigateToDetail, depth = 2)
        assertEquals("TestIntent.NavigateToDetail", result)
    }

    @Test
    fun formatAsSimple_SealedDataObject_Depth3() {
        val result = ObjectFormatter.formatAsSimple(TestIntent.NavigateToDetail, depth = 3)
        assertEquals("ObjectFormatterTest.TestIntent.NavigateToDetail", result)
    }

    @Test
    fun formatAsSimple_SealedDataClass_Depth2() {
        val instance = TestIntent.Search("hello")
        val result = ObjectFormatter.formatAsSimple(instance, depth = 2)
        assertEquals("TestIntent.Search(query=hello)", result)
    }

    @Test
    fun formatAsSimple_TopLevelLikeObject_Depth1() {
        val result = ObjectFormatter.formatAsSimple(TopLevelLikeObject, depth = 1)
        assertEquals("TopLevelLikeObject", result)
    }

    @Test
    fun formatAsSimple_String() {
        val result = ObjectFormatter.formatAsSimple("test", depth = 1)
        assertEquals("test", result)

        val result2 = ObjectFormatter.formatAsSimple("test", depth = 2)
        assertEquals("kotlin.test", result2)
    }

    @Test
    fun formatAsSimple_Int() {
        val result = ObjectFormatter.formatAsSimple(123, depth = 1)
        assertEquals("123", result)
    }

    @Test
    fun formatAsSimple_Depth0() {
        val result = ObjectFormatter.formatAsSimple(TopLevelLikeObject, depth = 0)
        assertEquals("TopLevelLikeObject", result)
    }

    @Test
    fun formatAsSimple_Depth1() {
        val result = ObjectFormatter.formatAsSimple(TopLevelLikeObject, depth = 1)
        assertEquals("TopLevelLikeObject", result)
    }

    @Test
    fun formatAsSimple_Depth2() {
        val result = ObjectFormatter.formatAsSimple(TopLevelLikeObject, depth = 2)
        assertEquals("ObjectFormatterTest.TopLevelLikeObject", result)
    }

    @Test
    fun formatAsSimple_AnonymousObject() {
        val anon = object {
            override fun toString() = "anonymous"
        }
        val result = ObjectFormatter.formatAsSimple(anon)
        assertEquals("anonymous", result)
    }
}
