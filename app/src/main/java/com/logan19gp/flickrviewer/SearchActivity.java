package com.logan19gp.flickrviewer;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.logan19gp.flickrviewer.api.OnEndOfListListener;
import com.logan19gp.flickrviewer.api.Photo;
import com.logan19gp.flickrviewer.api.Photos;
import com.logan19gp.flickrviewer.api.ResponsePhoto;
import com.logan19gp.flickrviewer.images.ImageAdapter;
import com.logan19gp.flickrviewer.images.PanZoomImageViewPager;
import com.logan19gp.flickrviewer.network.ResponseOrError;
import com.logan19gp.flickrviewer.network.ServerAPIClient;
import com.logan19gp.flickrviewer.network.ServerRequestTask;

public class SearchActivity extends AppCompatActivity
{
    public final static String API_KEY = "c93e179575934af299f7a6f99ee84473";
    public final static String SEARCH_STR = "SEARCH_STR";
    public final static String IMAGE_OPENED = "IMAGE_OPENED";
    public final static String PAGE_ON_VIEW = "PAGE_ON_VIEW";
    public final static Integer PER_PAGE = 5;
    private ListView listView;
    private PanZoomImageViewPager viewPager;
    private LinearLayout pagerContainer;
    private String savedSearchString;
    private SearchView.SearchAutoComplete theTextArea;
    private ImageAdapter imageAdapter;
    private Integer pageOnView;
    private View progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progress_bar = findViewById(R.id.progress_bar);
        listView = (ListView) findViewById(R.id.listView);
        pagerContainer = (LinearLayout) findViewById(R.id.pager_container);
        viewPager = (PanZoomImageViewPager) findViewById(R.id.pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pagerContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pagerContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause()
    {
        MainApplication.saveToPrefs(SEARCH_STR, theTextArea.getText().toString().trim());
        if (pagerContainer.getVisibility() == View.VISIBLE)
        {
            MainApplication.saveToPrefsInt(SearchActivity.IMAGE_OPENED, viewPager.getCurrentItem());
        }
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        savedSearchString = MainApplication.loadFromPrefs(SEARCH_STR, "");
        if (savedSearchString.length() > 0)
        {
            pageOnView = MainApplication.loadFromPrefsInt(PAGE_ON_VIEW, 1);
            showSearchResult(savedSearchString, pageOnView, pageOnView * PER_PAGE);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        if (pagerContainer.getVisibility() == View.VISIBLE)
        {
            pagerContainer.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        viewPager = (PanZoomImageViewPager) findViewById(R.id.pager);
        searchView.setQueryHint("Search a photo by name");
        searchView.setIconifiedByDefault(true);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        theTextArea = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        savedSearchString = MainApplication.loadFromPrefs(SEARCH_STR, "");
        if (savedSearchString.length() > 0)
        {
            theTextArea.setText(savedSearchString);
            pageOnView = MainApplication.loadFromPrefsInt(PAGE_ON_VIEW, 1);
            showSearchResult(savedSearchString, pageOnView, pageOnView * PER_PAGE);
        }
        searchView.setOnCloseListener(new SearchView.OnCloseListener()
        {
            @Override
            public boolean onClose()
            {
                if (getSupportActionBar() != null)
                {
                    if (pagerContainer.getVisibility() == View.VISIBLE)
                    {
                        pagerContainer.setVisibility(View.GONE);
                    }
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    MainApplication.saveToPrefs(SEARCH_STR, "");
                }
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (getSupportActionBar() != null)
                {
                    if (pagerContainer.getVisibility() == View.VISIBLE)
                    {
                        pagerContainer.setVisibility(View.GONE);
                    }
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    MainApplication.saveToPrefs(SEARCH_STR, theTextArea.getText().toString().trim());
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (pagerContainer.getVisibility() == View.VISIBLE)
                {
                    pagerContainer.setVisibility(View.GONE);
                }
                if (imageAdapter != null)
                {
                    imageAdapter.clear();
                }
                MainApplication.saveToPrefs(SEARCH_STR, theTextArea.getText().toString().trim());
                showSearchResult(query, 1, PER_PAGE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText)
            {
                if (pagerContainer.getVisibility() == View.VISIBLE)
                {
                    pagerContainer.setVisibility(View.GONE);
                }
                return false;
            }

        });

        return true;
    }

    private void showSearchResult(final String query, final Integer pageNo, final Integer perPage)
    {
        if (query == null || query.length() < 1 || imageAdapter == null)
        {
            imageAdapter = new ImageAdapter(SearchActivity.this, viewPager, pagerContainer, new Photos(), new OnEndOfListListener()
            {
                @Override
                public void addMorePhotos()
                {
                    pageOnView++;
                    showSearchResult(query, pageOnView, perPage);
                }
            });
            listView.setAdapter(imageAdapter);
        }
        else
        {
            new ServerRequestTask()
            {
                @Override
                protected void onPreExecute()
                {
                    if (progress_bar != null)
                    {
                        progress_bar.setVisibility(View.VISIBLE);
                    }
                    super.onPreExecute();
                }

                @Override
                protected ResponseOrError<?> doInBackground()
                {
                    ServerAPIClient client = new ServerAPIClient();
                    String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + API_KEY +
                            "&format=json&nojsoncallback=1&page=" + pageNo + "&per_page=" + perPage + "&text=observatory&safe_search=" + query.trim();
                    ResponseOrError<ResponsePhoto> response = client.addRequest_synchronous_custom(Request.Method.GET, url, null, ResponsePhoto.class, null, null);
                    return response;
                }

                @Override
                protected void processSuccessResponse(ResponseOrError successResponse)
                {
                    if (successResponse != null && successResponse.getResponse() instanceof ResponsePhoto)
                    {
                        ResponsePhoto photosResponse = (ResponsePhoto) successResponse.getResponse();
                        Photos photos = photosResponse.getPhotos();
                        Log.d(this.getClass().getSimpleName(), "photos size:" + (photos.getPhotos() == null ? "null" : photos.getPhotos().length));
                        for (Photo item : photos.getPhotos())
                        {
                            if (imageAdapter.getPosition(item) < 0)
                            {
                                imageAdapter.addItem(item);
                            }
                        }
                    }
                }

                @Override
                protected void onPostExecute(ResponseOrError result)
                {
                    if (progress_bar != null)
                    {
                        progress_bar.setVisibility(View.GONE);
                    }
                    super.onPostExecute(result);
                }
            }.execute();
        }
    }
}
