package com.tnc.crackertracker.UI;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.tnc.crackertracker.R;
import com.tnc.crackertracker.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignupActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Uri filePath;
    private StorageReference storageReference;
    private ShapeableImageView userprofile;
    private Button signupButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();
        // Initialize views
        usernameEditText = findViewById(R.id.user_name);
        emailEditText = findViewById(R.id.user_email);
        passwordEditText = findViewById(R.id.password_edit_txt);
        signupButton = findViewById(R.id.signup_button);
        userprofile = findViewById(R.id.userprofile);

        // Handle signup button click
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        userprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions()) {
                    showImagePickerDialog();
                }
            }
        });
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePickerDialog();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showImagePickerDialog() {
        CharSequence[] options = {"Choose from Gallery", "Take a Photo"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select or Take a new Picture");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
            } else if (options[item].equals("Take a Photo")) {
                Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePhoto.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePhoto, REQUEST_IMAGE_CAPTURE);
//                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    userprofile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    userprofile.setImageBitmap(imageBitmap);
                    filePath = getImageUri(imageBitmap); // Convert Bitmap to Uri
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        // Save the bitmap to a file and return its URI
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // or use getCacheDir() for cache
        File imageFile = new File(path, "profile_image_" + System.currentTimeMillis() + ".jpg");
        try {
            imageFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            // Create a file URI using FileProvider
            Context context = getApplicationContext();
            String packageName = context.getPackageName();
            Uri fileUri = FileProvider.getUriForFile(context, packageName + ".fileprovider", imageFile);

            return fileUri;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void signup() {
        final String username = usernameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if any field is empty
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (filePath == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadingIndicator();
        // Create user with email and password in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Save additional user data to Firebase Realtime Database
                                final String userId = user.getUid();
                                final User userData = new User(userId, username, email);

                                if (filePath != null) {
                                    StorageReference ref = storageReference.child("images/" + userId);
                                    ref.putFile(filePath)
                                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Uri> task) {
                                                                if (task.isSuccessful()) {
                                                                    Uri downloadUri = task.getResult();
                                                                    userData.setProfileImageUrl(downloadUri.toString());
                                                                    saveUserData(userId, userData);
                                                                } else {
                                                                    hideLoadingIndicator();
                                                                    Toast.makeText(SignupActivity.this, "Failed to get download URL: " + task.getException().getMessage(),
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        hideLoadingIndicator();
                                                        Toast.makeText(SignupActivity.this, "Failed to upload image: " + task.getException().getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    hideLoadingIndicator();
                                    Toast.makeText(SignupActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            hideLoadingIndicator();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserData(String userId, User userData) {
        databaseReference.child(userId).setValue(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            hideLoadingIndicator();
                            Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            navigateToLogin();
                        } else {
                            hideLoadingIndicator();
                            Toast.makeText(SignupActivity.this, "Failed to save user data: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showLoadingIndicator() {
        hideLoadingIndicator();
        loadingDialog = new ProgressDialog(SignupActivity.this);
        loadingDialog.setMessage("Signup in progress...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void hideLoadingIndicator() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();  // Close the signup activity
    }
}
