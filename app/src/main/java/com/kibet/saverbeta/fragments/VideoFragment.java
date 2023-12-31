package com.kibet.saverbeta.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.kibet.saverbeta.Adapters.VideoAdapter;
import com.kibet.saverbeta.Model.ModelStatus;
import com.kibet.saverbeta.R;
import com.kibet.saverbeta.utils.MyConstants;
import com.kibet.saverbeta.utils.RecyclerItemClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.recyclerViewVideo) RecyclerView recyclerView;
    @BindView(R.id.progressBarVideo) ProgressBar progressBar;
    @BindView(R.id.contentVideoView) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.textVideoView) TextView textView;
    public static ArrayList<ModelStatus> data;
    LinearLayout opacity;
    ArrayList<ModelStatus> dataSelected= new ArrayList<>();
    ActionMode actionMode;
    Menu contextMenu;
    boolean isMultiSelect = false;

    VideoAdapter videoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.contentVideoView);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        data = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        videoAdapter = new VideoAdapter(getContext(), data, dataSelected,VideoFragment.this);
        recyclerView.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position);

            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    dataSelected = new ArrayList<ModelStatus>();
                    isMultiSelect = true;

                    if (actionMode == null) {
                        actionMode = getActivity().startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);

            }
        }));

    }

    public void loadData() {
        data = new ArrayList<>();
        final String path = MyConstants.WhatsAppDirectoryPath;
        File directory = new File(path);
        if (directory.exists()) {
            final File[] files = directory.listFiles();
            Arrays.sort(files, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
            final String[] paths = {""};
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].getName().endsWith(".mp4")) {
                            paths[0] = path + "" + files[i].getName();
                            ModelStatus modelStatus = new ModelStatus(paths[0], files[i].getName().substring(0, files[i].getName().length() - 4), 0);
                            data.add(modelStatus);
                        }
                    }
                    return null;
                }

                @SuppressLint("StaticFieldLeak")
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (!(data.toArray().length > 0)) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("No Status Found \n Check out some Status & come back again...");
                    }
                    progressBar.setVisibility(View.GONE);
                    videoAdapter = new VideoAdapter(getContext(), data, dataSelected,VideoFragment.this);
                    recyclerView.setAdapter(videoAdapter);
                    videoAdapter.notifyDataSetChanged();

                }
            }.execute();
        } else {
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText("No Status Found \n Check out some Status & come back again...");

            Snackbar.make(getActivity().findViewById(android.R.id.content), "WhatsApp Not Installed",
                    Snackbar.LENGTH_SHORT).show();
        }
        refreshItems();
    }

    @Override
    public void onRefresh() {
        loadData();
        if(actionMode!=null) {
            actionMode.finish();
        }
        isMultiSelect = false;
        refreshAdapter();
    }

    public void refreshItems() {
        onItemsLoadComplete();
    }

    public void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void multi_select(int position) {
        if (actionMode != null) {
            if (dataSelected.contains(data.get(position))) {
                dataSelected.remove(data.get(position));
            }else {
                dataSelected.add(data.get(position));
            }
            if (dataSelected.size() > 0)
                actionMode.setTitle("" + dataSelected.size());
            else
                actionMode.setTitle("");

            refreshAdapter();

        }
    }

    @Override
    public void onResume(){
        onRefresh();
        super.onResume();
    }

    public void refreshAdapter()
    {
        videoAdapter.selected_videoList=dataSelected;
        videoAdapter.videoList=data;
        videoAdapter.notifyDataSetChanged();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_menu, menu);
            contextMenu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true; // Return false if nothing is done
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_shared:
                    shareNow();
                    if (actionMode != null) {
                        actionMode.finish();
                    }
                    return true;
                case R.id.action_save:
                    for(int i=0;i<dataSelected.size();i++) {
                        String array = dataSelected.get(i).getFull_path();
                        copyFileOrDirectory(array, MyConstants.APP_DIR);
                    }
                    videoAdapter.notifyDataSetChanged();

                    if (actionMode != null) {
                        actionMode.finish();
                    }
                    return true;
                case R.id.action_delete:
                    startDialog();
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            isMultiSelect = false;
            dataSelected = new ArrayList<ModelStatus>();
            refreshAdapter();
            //onRefresh();
        }
    };

    public void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (String file : files) {
                    String src1 = (new File(src, file).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
            Toast.makeText(getContext(), "Video Saved", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(destFile));
            getActivity().sendBroadcast(intent);

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void startDialog() {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getContext());
        myAlertDialog.setTitle("Delete Status");
        myAlertDialog.setMessage("You are about to delete this videos permanently. This action cannot be undone");

        myAlertDialog.setPositiveButton("Delete",
                (arg0, arg1) -> {
                    for(int i = 0; i < dataSelected.size(); i++){
                        File file = new File(dataSelected.get(i).getFull_path());
                        file.delete();
                    }
                    videoAdapter.notifyDataSetChanged();
                    onRefresh();
                    if (actionMode != null) {
                        actionMode.finish();
                    }
                    Toast.makeText(getContext(), "Status Deleted", Toast.LENGTH_SHORT).show();
                });

        myAlertDialog.setNegativeButton("Cancel",
                (arg0, arg1) -> {
                    //
                });
        myAlertDialog.show();
    }

    public void shareNow(){
        ArrayList<Uri> uri = new ArrayList<Uri>();
        for(int i = 0; i < dataSelected.size(); i++) {
            File file = new File(dataSelected.get(i).getFull_path());
            uri.add(FileProvider.getUriForFile(this.getContext(), "com.techvinz.wahstatussaver.FileProvider",file));
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setType("video/*");
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.send_to)));
    }

    public void deletePost(String myPath){
        File file = new File(myPath);
        file.delete();
        Toast.makeText(getContext(), "Status Deleted", Toast.LENGTH_SHORT).show();
    }

}
