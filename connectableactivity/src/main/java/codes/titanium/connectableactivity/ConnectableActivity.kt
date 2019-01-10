package codes.titanium.connectableactivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.support.v7.app.AppCompatActivity

internal class ConnectableActivity : AppCompatActivity() {

    private lateinit var messenger: Messenger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messenger = intent.getParcelableExtra(EXTRA_MESSENGER)
        messenger.send(createMessage(ON_CREATE, this))
    }

    private fun createMessage(what: Int, obj: Any?, arg1: Int = 0): Message {
        val result = Message.obtain()
        result.what = what
        result.arg1 = arg1
        result.obj = obj
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        messenger.send(createMessage(ON_ACTIVITY_RESULT, data, resultCode))
        finishWithNoAnimation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        messenger.send(
            createMessage(
                ON_REQUEST_PERMISSION_RESULT,
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            )
        )
        finishWithNoAnimation()
    }

    /**
     * Finishes activity with no animation
     */
    fun Activity.finishWithNoAnimation() {
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onStart() {
        super.onStart()
        setVisible(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        messenger.send(createMessage(ON_DESTROY, this))
    }

}

/**
 * @param onCreate callback called as soon as activity onCreate was called. Do your activity logic here
 * @param onActivityResult callback is called when anActivityResult is called inside activity with intent and result code
 * @param onDeAttach if you want to define some specific logic that will be done on activity destroy - do it here
 * @param onRequestPermissionsResult called as soon as onRequestPermission result was called in activity
 */
@JvmOverloads
fun Context.launchConnectableActivity(
    onCreate: (Activity) -> Unit?,
    onActivityResult: (Int, Intent?) -> Unit = { _, _ -> },
    onRequestPermissionsResult: (Boolean) -> Unit = { },
    onDeAttach: () -> Unit = {}
) {
    val intent = Intent(this, ConnectableActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(
            EXTRA_MESSENGER,
            Messenger(ConnectableHandler(onCreate, onActivityResult, onRequestPermissionsResult, onDeAttach))
        )
    startActivity(intent)
}

val ON_CREATE = 0
val ON_ACTIVITY_RESULT = 1
val ON_REQUEST_PERMISSION_RESULT = 2
val ON_DESTROY = 3
val EXTRA_MESSENGER = "extra messenger"

private class ConnectableHandler(
    val onCreate: (Activity) -> Unit?,
    val onActivityResult: (Int, Intent?) -> Unit = { _, _ -> },
    val onRequestPermissionsResult: (Boolean) -> Unit = { },
    val onDeAttach: () -> Unit = {}
) : Handler() {

    override fun handleMessage(msg: Message?) {
        when (msg?.what) {
            ON_CREATE -> onCreate(msg.obj as Activity)
            ON_ACTIVITY_RESULT -> onActivityResult(msg.arg1, msg.obj as Intent?)
            ON_REQUEST_PERMISSION_RESULT -> onRequestPermissionsResult(msg.obj as Boolean)
            ON_DESTROY -> onDeAttach()
        }
    }

}
