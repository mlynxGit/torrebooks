package com.maghribpress.torrebook.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import com.maghribpress.torrebook.BasicApp;
import com.maghribpress.torrebook.DataRepository;
import com.maghribpress.torrebook.ProgressListener;
import com.maghribpress.torrebook.classes.ApiTokenObject;
import com.maghribpress.torrebook.classes.TorrentFile;
import com.maghribpress.torrebook.network.GetBooksDataService;
import com.maghribpress.torrebook.network.RetrofitInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TorrentListViewModel extends AndroidViewModel {
    private final DataRepository mRepository;
    private final String mQuery;
    private final int mpage;
    private final ProgressListener mPl;
    private final MutableLiveData<List<TorrentFile>> mObservableTorrents;

    public TorrentListViewModel(@NonNull Application application, final String query, final int page, ProgressListener pl) {
        super(application);
        mQuery= query;
        mpage=page;
        mPl = pl;
        mRepository = ((BasicApp) application).getRepository();
        mObservableTorrents = mRepository.getTorrents(mQuery,mpage,mPl);
    }
    public MutableLiveData<List<TorrentFile>> getObservableTorrents() {
        return mObservableTorrents;
    }
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final String mQuery;

        private final DataRepository mRepository;
        private final int mPage;
        private final ProgressListener mPl;

        public Factory(@NonNull Application application, String query, int page, ProgressListener pl) {
            mApplication = application;
            mQuery = query;
            mPl=pl;
            mRepository = ((BasicApp) application).getRepository();
            mPage = page;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new TorrentListViewModel(mApplication, mQuery, mPage, mPl);
        }
    }
    public void getTorrents(String query, int page, ProgressListener pl) {
        List<TorrentFile> data = new ArrayList<TorrentFile>();
        GetBooksDataService service = RetrofitInstance.getRetrofitInstance().create(GetBooksDataService.class);
        Call<ResponseBody> torrentscall = service.getbook(query,ApiTokenObject.getInstance().getToken(),page);
        torrentscall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                List<TorrentFile> torrents = new ArrayList<TorrentFile>();
                if (response.isSuccessful()) {
                    Log.d("TORRENTTAG","Response Succesfull");
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.has("data")) {
                            JSONArray torrentsArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < torrentsArray.length(); i++) {
                                JSONObject torrent = torrentsArray.getJSONObject(i);
                                TorrentFile tmpTorrent = new TorrentFile();
                                tmpTorrent.setTitle(torrent.getString("title"));
                                tmpTorrent.setMagnet(torrent.getString("magnet"));
                                tmpTorrent.setTorrentsize(torrent.getString("filesize"));
                                tmpTorrent.setAdded(torrent.getString("added"));
                                tmpTorrent.setSeeders(Integer.valueOf(torrent.getString("seeders")));
                                tmpTorrent.setPopularity(torrent.getInt("popularity"));
                                tmpTorrent.setSource(torrent.getString("source"));
                                tmpTorrent.setType(torrent.getString("type"));
                                Log.d("TORRENTTAG", torrent.getString("title"));
                                torrents.add(tmpTorrent);
                            }
                            mObservableTorrents.setValue(torrents);
                            pl.stopProgress();
                        } else {
                            String error_message = "نعتدر حدث خطأ ما !";
                            //Toast.makeText(getContext(), error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        pl.stopProgress();
                        e.printStackTrace();

                    } catch (IOException e) {
                        pl.stopProgress();
                        e.printStackTrace();
                    }

                }
                // generateNewsList(response.body().getNewslist());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pl.stopProgress();
            }
        });
    }
    public void getTorrents(int page) {
        List<TorrentFile> data = new ArrayList<TorrentFile>();
        GetBooksDataService service = RetrofitInstance.getRetrofitInstance().create(GetBooksDataService.class);
        Call<ResponseBody> torrentscall = service.getbook(mQuery,ApiTokenObject.getInstance().getToken(),page);
        torrentscall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                List<TorrentFile> torrents = new ArrayList<TorrentFile>();
                if (response.isSuccessful()) {
                    Log.d("TORRENTTAG","Response Succesfull");
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.has("data")) {
                            JSONArray torrentsArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < torrentsArray.length(); i++) {
                                JSONObject torrent = torrentsArray.getJSONObject(i);
                                TorrentFile tmpTorrent = new TorrentFile();
                                tmpTorrent.setTitle(torrent.getString("title"));
                                tmpTorrent.setMagnet(torrent.getString("magnet"));
                                tmpTorrent.setTorrentsize(torrent.getString("filesize"));
                                tmpTorrent.setAdded(torrent.getString("added"));
                                tmpTorrent.setSeeders(Integer.valueOf(torrent.getString("seeders")));
                                tmpTorrent.setPopularity(torrent.getInt("popularity"));
                                tmpTorrent.setSource(torrent.getString("source"));
                                tmpTorrent.setType(torrent.getString("type"));
                                Log.d("TORRENTTAG", torrent.getString("title"));
                                torrents.add(tmpTorrent);
                            }
                            List<TorrentFile> tempTorrents = mObservableTorrents.getValue();
                            tempTorrents.addAll(torrents);
                            mObservableTorrents.setValue(tempTorrents);
                        } else {
                            String error_message = "نعتدر حدث خطأ ما !";
                            //Toast.makeText(getContext(), error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                // generateNewsList(response.body().getNewslist());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}
