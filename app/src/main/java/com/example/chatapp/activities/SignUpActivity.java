package com.example.chatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp.R;
import com.example.chatapp.databinding.ActivitySignInBinding;
import com.example.chatapp.databinding.ActivitySignUpBinding;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private PreferenceManager preferenceManager;

    private String encodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();

    }

    /**
     * Verifies if the user has been signed up in the database
     */
    private void setListeners() {

        binding.textSignIn.setOnClickListener(v -> onBackPressed());

        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidateSignUpDetails()) {
                SignUp();
            }
        });

        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            pickImage.launch(intent);
        });

    }


    /**
     * Allows for the data to be shown
     * @param message
     */
    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    /**
     * Creates the SignUp method to check the loading process and post the info to firebase
     */
    private void SignUp() {

        loading(true);

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String, String> user = new HashMap<>();

        user.put(Constants.KEY_NAME,binding.inputName.getText().toString());

        user.put(Constants.KEY_EMAIL,binding.inputEmail.getText().toString());

        user.put(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString());

        user.put(Constants.KEY_IMAGE,encodeImage);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {

                    loading(false);

                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);

                    preferenceManager.putString(Constants.KEY_NAME,binding.inputName.getText().toString());

                    preferenceManager.putString(Constants.KEY_IMAGE,encodeImage);

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                }).addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });

    }

    /**
     * Uploads the image for the user's pfp to firebase and puts it back on their profile
     * @param bitMap
     * @return
     */
    private String encodeImage(Bitmap bitMap) {

        int previewWidth = 150;

        int previewHeight = bitMap.getHeight() * previewWidth / bitMap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitMap,previewWidth,previewHeight,false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(bytes,Base64.DEFAULT);

    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {

                    Uri imageUri = result.getData().getData();
                    try {

                        InputStream inputStream = getContentResolver().openInputStream(imageUri);

                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        binding.imageProfile.setImageBitmap(bitmap);

                        binding.textAddImage.setVisibility(View.GONE);

                        encodeImage = encodeImage(bitmap);

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                    }
                }
            }
    );

    private Boolean isValidateSignUpDetails() {

        if (encodeImage == null) {

            showToast("Please Select Your Image");

            return false;

        } else if (binding.inputName.getText().toString().trim().isEmpty()) {

            showToast("Please Enter Your Name");

            return false;

        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {

            showToast("Please Enter Your Email");

            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {

            showToast("Please Enter Vail Name");

            return false;

        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {

            showToast("Please Enter Your Password");

            return false;

        } else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {

            showToast("Please Confirm Your Password");

            return false;

        } else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {

            showToast("Password and Confirm Your Password Must Be The Same");

            return false;

        } else {

            return true;

        }

    }

    /**
     * Allows for the data to load
     * @param isLoading
     */
    private void loading (Boolean isLoading) {

        if (isLoading) {

            binding.buttonSignUp.setVisibility(View.INVISIBLE);

            binding.progressBar.setVisibility(View.VISIBLE);

        } else {

            binding.buttonSignUp.setVisibility(View.VISIBLE);

            binding.progressBar.setVisibility(View.INVISIBLE);

        }

    }

}