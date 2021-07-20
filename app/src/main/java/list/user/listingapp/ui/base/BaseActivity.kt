package list.user.listingapp.ui.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import list.user.listingapp.R

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
}