package in.thegforest.chatting.Main.Chat;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import in.thegforest.chatting.R;

public class ChatImageViewFragment extends DialogFragment {
    ImageView imageView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        String uri=getArguments().getString("uri");
        View view=inflater.inflate(R.layout.show_chat_image,container,false);
        imageView=view.findViewById(R.id.imageView);
        imageView.setImageURI(Uri.parse(uri));
        return view;
    }
}
