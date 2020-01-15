package com.sinichkin.timofey.redradio


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home//, R.id.nav_about, R.id.nav_about_rkr, R.id.nav_articles_rpr, R.id.nav_program_rpr, R.id.nav_library_fra, R.id.nav_rkr
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_share ){
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Красное Радио ФРА")
            shareIntent.putExtra(Intent.EXTRA_TEXT,"Расскажи товарищам! [тут будет ссылка]")

//            val bitmap =
//                BitmapFactory.decodeResource(resources, R.drawable.rpw_logo) // your bitmap
//            val bs = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs)
//
//            //shareIntent.putExtra("image/*", bs.toByteArray())
//            val pict = R.drawable.home_fon_akustika
//            val imageUri =
////            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
////                    resources.getResourcePackageName(pict) + '/' +
////                    resources.getResourceTypeName(pict) + '/' +
////                    pict.toString())
//
//            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ packageName +"/"+pict)
//
//            shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            shareIntent.data = imageUri
//           // shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
//           shareIntent.type = "image/*"
//            //shareIntent.setPackage("com.vkontakte.android");

             startActivity(Intent.createChooser(shareIntent, "Красное Радио")) //

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
   // val imageUri: Uri = Uri.parse(pictureFile.getAbsolutePath())
    // Returns the URI path to the Bitmap displayed in specified ImageView


}
