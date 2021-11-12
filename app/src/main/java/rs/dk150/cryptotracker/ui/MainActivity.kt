package rs.dk150.cryptotracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import rs.dk150.cryptotracker.R

/**
 * Entry-point Activity using JetPack navigation to show page fragments
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}