package com.amartin.marvelapplication.api

import com.amartin.marvelapplication.api.model.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.parser.Tag
import org.jsoup.select.Elements
import java.io.IOException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object MarvelScraping {

    private val redirected = listOf(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP, 307)

    private fun getFinalUrl(url: String): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.instanceFollowRedirects = false
        conn.connect()
        conn.inputStream
        val responseCode = conn.responseCode
        val next = conn.getHeaderField("Location")
        conn.disconnect()
        if (responseCode in redirected) {
            return getFinalUrl(next)
        }
        return url
    }

    @Suppress("UNCHECKED_CAST")
    object Json {
        private val gson: Gson = GsonBuilder().create()
        private val genericJsonType = object : TypeToken<Map<String, Any>>() {}.type
        private val jsonFieldKeys = arrayListOf("props", "initialProps", "fullPageData", "pathContentData", "content")
        private const val lastFieldKey = "components"

        private fun getData(url: String): String {
            try {
                // json is in script module without properties
                val document = Jsoup.connect(url).followRedirects(true).get()
//                println("document:\n$document")
                return document.select("script").not("[src]").not("[type]").first()
                    // on first line, first value "__NEXT_DATA__ = { ... }"
                    .html().split(System.lineSeparator()).first().split("=", limit = 2)[1]
            }catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        private fun parseUtilData(data: String, fieldList: List<String> = jsonFieldKeys, lastField: String = lastFieldKey ): Scrapped {
            try {
                val map: Map<String, Any> = gson.fromJson(data, genericJsonType)
                // navigate in json
                var mapRef = map
                for (field in fieldList) {
                    mapRef = mapRef[field] as Map<String, Any>
                }
                return parseComponents(mapRef[lastField] as List<Map<String, Any>>)
            }catch (e: Exception) {
                e.printStackTrace()
            }
            return Scrapped(listOf(), listOf(), null)
        }

        private fun parseComponents(components: List<Map<String, Any>>): Scrapped {
            var character: ScrappedCharacter? = null
            var elementList: List<ScrappedElement> = listOf()
            var powerGridList: List<ScrappedPowerGrid> = listOf()
            for (component in components) {
                if (component.containsKey("component_type")) {
                    when (component["component_type"]) {
                        "power_grid" -> {
                            if (component.containsKey("data")) {
                                powerGridList = parsePowerGrid(component["data"] as List<Map<String, Any>>)
                            }
                        }
                        "two_column" -> {
                            if (component.containsKey("content_components")) {
                                elementList = parseContentBlock(component["content_components"] as List<Map<String, Any>>)
                            }
                            if (component.containsKey("rail_components")) {
                                character = parseCharacter(component["rail_components"] as List<Map<String, Any>>)
                            }
                        }
                        "featured" -> {
                            elementList = listOf(ScrappedElement("p", component["body_text"].toString()))
                        }
                    }
                }
            }
            return Scrapped(elementList, powerGridList, character)
        }

        private fun parsePhysicalTraits(c: Map<String, Any>): PhysicalTraits {
            val defaultVal: () -> String = { "" }
            return PhysicalTraits(
                c.getOrElse("eye_color", defaultVal) as String,
                c.getOrElse("height", defaultVal) as String,
                c.getOrElse("weight", defaultVal) as String,
                c.getOrElse("hair_color", defaultVal) as String,
                c.getOrElse("gender", defaultVal) as String)
        }

        private fun parseBioData(components: List<Map<String, Any>>): Map<String, String> {
            val bioData = mutableMapOf<String, String>()
            for (component in components) {
                if (component.containsKey("label") && component.containsKey("body")) {
                    val bodyArr = component["body"] as List<Map<String, Any>>
                    bodyArr.firstOrNull()?.let {
                        bioData[component["label"] as String] = it.getOrElse("body", {""}) as String
                    }
                }
            }
            return bioData.toMap()
        }

        private fun parseCharacter(components: List<Map<String, Any>>): ScrappedCharacter? {
            var character: ScrappedCharacter? = null
            var physicalTraits: PhysicalTraits? = null
            var bioData: Map<String, String> = mapOf()
            for (component in components) {
                if (component.containsKey("component_type")) {
                    when (component["component_type"]) {
                        "rail_explore_bio" -> {
                            if (component.containsKey("physical_traits")) {
                                physicalTraits = parsePhysicalTraits(component["physical_traits"] as Map<String, Any>)
                            }
                            if (component.containsKey("bioData")) {
                                bioData = parseBioData(component["bioData"] as List<Map<String, Any>>)
                            }
                            character = ScrappedCharacter(physicalTraits, bioData)
                        }
                    }
                }
            }
            return character
        }

        private fun <O> iterateApplyingFunc(components: List<Map<String, Any>>, f: (Map<String, Any>, MutableList<O>)->Unit): List<O> {
            val out = mutableListOf<O>()
            try {
                for (component in components) {
                    f(component, out)
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
            return out.toList()
        }

        private fun parsePowerGrid(components: List<Map<String, Any>>): List<ScrappedPowerGrid> =
            iterateApplyingFunc(components) { component, out ->
                out.add(ScrappedPowerGrid(component["label"] as String, component["powerRating"] as Double))
            }

        private fun parseContentBlock(components: List<Map<String, Any>>): List<ScrappedElement> =
            iterateApplyingFunc(components) { component, out ->
                if (component["component_type"] == "content_block") {
                    val title = component["title"]
                    if (title == "Featured Video") {
                        return@iterateApplyingFunc
                    }
                    if (title != null &&  title != "") {
                        out.add(ScrappedElement("h2", component["title"].toString()))
                    }
                    val content = component["body_content"] as Map<String, Any>
                    if (content["type"] == "text") {
                        out.add(ScrappedElement("", content["value"].toString()))
                    }
                }
            }

        suspend fun moreInfoFromWiki(baseUrl: String):Scrapped = withContext(Dispatchers.IO) {
//            val url = getFinalUrl(baseUrl)
            val plainData = getData(baseUrl)
            parseUtilData(plainData)
        }
    }

    object Html {
        private const val baseUrl = "https://www.marvel.com/characters/"
        private const val onScreen = "/on-screen"
        private const val inComics = "/in-comics"

        private const val select = ".content-block__body"
        private val tags = arrayListOf<Tag>(Tag.valueOf("h3"), Tag.valueOf("h4"), Tag.valueOf("p"), Tag.valueOf("a"))

        suspend fun tryGetMoreInfo(characterName: String): List<ScrappedElement> = withContext(Dispatchers.IO) {
            val out = mutableListOf<ScrappedElement>()
            try {
                val url = composeUrl(characterName)
                val selection = selection(url, tries = arrayListOf("", inComics, onScreen))
                classifyElements(selection, out)
            }catch (e: Exception) {
                // probably 404, silent
            }
            out.toList()
        }

        private fun selection(baseUrl: String, tries: List<String> = arrayListOf("")): Elements {
            for (i in tries) {
                try {
                    val url = getFinalUrl(baseUrl + i)
                    val data = Jsoup.connect(url).get()
                    val selection = data.select(select)
                    if (selection.size > 0) {
                        println("found in ${url+i}")
                        return selection
                    }
                }catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return Elements()
        }

        private fun classifyElements(elements: Elements, out: MutableList<ScrappedElement> = mutableListOf()) {
            for (e in elements) {
                if (tags.contains(e.tag())) {
                    out.add(ScrappedElement(e.tagName(), e.ownText()))
//                if (tags.contains(e.tag()) && e.childNodeSize() == 1) {
//                    out.add(ScrappedElement(e.tagName(), e.ownText()))
//                }else if (e.tag() == Tag.valueOf("div") && e.attr("class") == "text"){
//                    out.add(ScrappedElement(e.tagName(), e.ownText()))
                }else if (e.childNodeSize() > 0) {
                    classifyElements(e.children(), out)
                }
            }
        }

        private fun composeUrl(characterName: String): String {
            val name = characterName.toLowerCase(Locale.ROOT).replace("(", "").replace(")", "").replace(" ", "-")
            return getFinalUrl("$baseUrl$name")
        }
    }
}