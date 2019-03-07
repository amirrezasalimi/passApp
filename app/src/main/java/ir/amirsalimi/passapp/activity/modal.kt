package ir.amirsalimi.passapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ir.amirsalimi.passapp.R
import kotlinx.android.synthetic.main.activity_modal.*

class modal : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modal)
        hideSheet.setOnClickListener {
            finish()
        }
    }
}
