package ift2905.moviebucket;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class AbootFragment extends Fragment{
    private  ImageView tmdb;
    private ImageView diro;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aboot_fragment_eh, container, false);
        setLogos(view);
        return view;

    }

    public void setLogos(View view){
        tmdb = (ImageView) view.findViewById(R.id.tmdb_logo);
        try { Picasso.with(getContext())
                    .load("https://www.themoviedb.org/assets/static_cache/9b3f9c24d9fd5f297ae433eb33d93514/images/v4/logos/408x161-powered-by-rectangle-green.png")
                    .into(tmdb);
        } catch (Exception e){
            // TODO : Find a default image
        }
        diro = (ImageView) view.findViewById(R.id.diro_logo);
        try { Picasso.with(getContext())
                .load("http://www-labs.iro.umontreal.ca/~lapalme/DIRO_LOGO/WEB%20%26%20Screen/15_DIRO_LOGO_RGB_Small.png")
                .into(diro);
        } catch (Exception e){
            // TODO : Find a default image
        }

    }
}
