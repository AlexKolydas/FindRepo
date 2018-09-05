package kolydas.alex.findrepo

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val searchTerm = intent.getStringExtra("searchTerm")
        val callBack = object : Callback<GitHubSearchResult> {
            override fun onFailure(call: Call<GitHubSearchResult>, t: Throwable) {
                println("it's not working")
            }

            override fun onResponse(call: Call<GitHubSearchResult>, response: Response<GitHubSearchResult>) {
                val searchResult = response?.body()

                if (searchResult != null) {
                    for (repo in searchResult!!.items) {
                        println(repo.html_url)
                    }

                    val listView = findViewById<ListView>(R.id.searchListView)
                    listView.setOnItemClickListener { parent, view, position, id ->
                        val selectedRepo = searchResult!!.items[position]
                        //OPEN URL IN THE BROWSER
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedRepo.html_url))
                        startActivity(intent)
                    }

                    val adapter = repoAdapter(this@SearchResultActivity, android.R.layout.simple_list_item_1, searchResult!!.items)
                    listView.adapter = adapter
                }

            }


        }
        val retriever = GitHubRetriver()
        retriever.searchRepos(callBack, searchTerm)
    }
}


class repoAdapter(context: Context?, resource: Int, objects: List<Repo>?) : ArrayAdapter<Repo>(context, resource, objects) {

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val repoView = inflator.inflate(R.layout.repo_list_layout, parent, false)

        val textView=repoView.findViewById<TextView>(R.id.repoText)
        val imageView=repoView.findViewById<ImageView>(R.id.repoImageView)
        val repo = getItem(position)

        //DISPLAY THE PHOTOS FROM THE GITHUB PROFILES
        Picasso.with(context).load(Uri.parse(repo.owner.avatar_url)).into(imageView)
        textView.text = repo.full_name


        return repoView
    }
}