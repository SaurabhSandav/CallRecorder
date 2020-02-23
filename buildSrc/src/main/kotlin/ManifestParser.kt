import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.FileInputStream
import javax.xml.parsers.DocumentBuilderFactory

class ManifestParser(private val projectDir: String) {

    lateinit var appPackage: String
    lateinit var permissions: List<String>
    lateinit var launcherActivity: String

    fun parse() {

        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val input = FileInputStream("$projectDir/src/main/AndroidManifest.xml")
        val doc = builder.parse(input)

        val manifestTag = doc.documentElement

        parsePackage(manifestTag)
        parsePermissions(manifestTag)
        parseLauncherActivity(manifestTag)
    }

    private fun parsePackage(manifestTag: Element) {
        appPackage = manifestTag.getAttribute("package")
    }

    private fun parsePermissions(manifestTag: Element) {

        val permissionTags = manifestTag.getElementsByTagName("uses-permission")

        permissions = permissionTags.iterable()
            .map { it as Element }
            .map { it.getAttribute("android:name") }
    }

    private fun parseLauncherActivity(manifestTag: Element) {

        val applicationTag = manifestTag.getElementsByTagName("application").item(0) as Element
        val activityTags = applicationTag.getElementsByTagName("activity")

        val launcherActivityTags = activityTags.iterable()
            .map { it as Element }
            .filter { isLauncherActivity(it) }

        launcherActivity = launcherActivityTags.first().getAttribute("android:name")
    }

    private fun isLauncherActivity(activityTag: Element): Boolean {

        val intentFilterTag = getIntentFilterTag(activityTag) ?: return false

        getMainActionTag(intentFilterTag) ?: return false
        getLauncherCategoryTag(intentFilterTag) ?: return false

        return true
    }

    private fun getIntentFilterTag(activityTag: Element): Element? {
        return activityTag.getElementsByTagName("intent-filter")
            .iterable()
            .singleOrNull() as Element?
    }

    private fun getMainActionTag(intentFilterTag: Element): Element? {
        return intentFilterTag.getElementsByTagName("action")
            .iterable()
            .singleOrNull {
                it.attributes.getNamedItem("android:name").nodeValue == "android.intent.action.MAIN"
            } as Element?
    }

    private fun getLauncherCategoryTag(intentFilterTag: Element): Element? {
        return intentFilterTag.getElementsByTagName("category")
            .iterable()
            .singleOrNull {
                it.attributes.getNamedItem("android:name").nodeValue == "android.intent.category.LAUNCHER"
            } as Element?
    }
}

private fun NodeList.iterable() = Iterable { NodeIterator(this) }

private class NodeIterator(private val nodeList: NodeList) : Iterator<Node> {

    private var index = 0

    override fun hasNext(): Boolean = index < nodeList.length

    override fun next(): Node {
        if (!hasNext())
            throw NoSuchElementException()
        return nodeList.item(index++)
    }
}
