package me.josecl200.remodel;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.hardware.camera2.*;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class FullscreenActivity extends AppCompatActivity {
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    private Camera mCamera;
    private CamPreview mPreview;
    private ArFragment arFragment;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button modelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        drawerLayout=findViewById(R.id.cameraview);
        setContentView(R.layout.activity_fullscreen);
        arFragment=(ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        recyclerView=findViewById(R.id.modelList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        modelButton=findViewById(R.id.modelBtn);
        ArrayList<String> modelList = new ArrayList<>();
        modelList.add("Gavetero");
        modelList.add("Modelo 2");
        mAdapter=new ModelAdapter(modelList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setVisibility(View.INVISIBLE);
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ModelRenderable.builder()
                        .setSource(this, Uri.parse("model.sfb"))
                        .build()
                        .thenAccept(modelRenderable -> addModelToScene(anchor,modelRenderable))
                        .exceptionally(throwable -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage(throwable.getMessage()).show();
                            return null;
                        });
            }
        });
        recyclerView.setOnClickListener(v -> recyclerView.setVisibility(View.INVISIBLE));
        modelButton.setOnClickListener(v ->{
            if(recyclerView.getVisibility()==View.VISIBLE){
                recyclerView.setVisibility(View.INVISIBLE);

            }else{
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void addModelToScene(Anchor anchor,ModelRenderable modelRenderable){
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }
}
