package com.company.android.myapplication.Firebase;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.company.android.myapplication.Login.LoginActivity;

import com.company.android.myapplication.MainActivity;
import com.company.android.myapplication.MapActivity;
import com.company.android.myapplication.R;
import com.company.android.myapplication.navigation.BottomNavigationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class FirebaseMethods {
    private FirebaseAuth mAuth;
    private Context mContext;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");
    private DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Likes");
    private DatabaseReference notifRef;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UserData");
    private DatabaseReference postPhotoRef = FirebaseDatabase.getInstance().getReference("PostsPhotos");

    private FirebaseUser mUser;
    private HashMap <Object, String> userData = new HashMap<>();
    private StorageReference mStorageReference, mStorageReference2;
    private FirebaseUser user;
    private String bio ="", storagePath = "User_Profile_Picture/", website="";
    private Uri mUri;
    private FirebaseUser mFirebaseUser;
    private String nickname;
    private Boolean exists;

    private BottomSheetDialog bottomSheetDialog;

    private boolean downloadOrOpen= false;
    private String[] storagePermission = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAADuzP3ng:APA91bFGLS3vW-BZfgKleNTA_bfZ3QIAJ8Rkb8A3q6BnVpMyVMCngmL3KoP3QjnX3EyWSsrRR9D4kEedBM12bn1isLCZlnDuQ7MiaYxZd-hk-6il2E-CzI78hEyYWlhAt9WMB1BSgkor";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    private String TOPIC;

    public FirebaseMethods(Context context) {

        mContext = context;

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("UserData");
        mStorageReference = getInstance().getReference();
        notifRef = FirebaseDatabase.getInstance().getReference("Notification");

    }


    //обычная регистрация
    public void registerNewUser(final String email, String password,
                                final String gender, final String login) {

        mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            Log.i("Register New user", "I'm here");
                            userData.put("uid", uid);
                            userData.put("email", email);
                            userData.put("gender", gender);
                            userData.put("nickname", login);
                            userData.put("bio", bio);
                            userData.put("website", "");


                            try
                            {

                                if( gender.equals("Female")) {
                                    mUri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.drawable.woman);
                                    InputStream inputStream = mContext.getContentResolver().openInputStream(mUri);
                                    userData.put("image", mUri.toString());
                                }
                                else if(gender.equals("Male"))
                                {
                                    mUri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.drawable.man);
                                    InputStream inputStream = mContext.getContentResolver().openInputStream(mUri);
                                    userData.put("image", mUri.toString());
                                } else if(gender.equals(""))
                                {
                                    mUri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.drawable.man);
                                    InputStream inputStream = mContext.getContentResolver().openInputStream(mUri);
                                    userData.put("image", mUri.toString());
                                }


                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }



                            FirebaseDatabase.getInstance().getReference("UserData")
                                    .child(uid).setValue(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(mContext, "Registered Succesfuly", Toast.LENGTH_SHORT).show();

                                            emailVerification();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else
                        {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "This email is already in use",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    public void emailVerification()
    {
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null)
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful())
                                Toast.makeText(mContext, "Couldn't send verification email", Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(mContext, "Verification link was sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                ((Activity)mContext).finish();
                            }
                        }
                    });
    }

    //вход в систему
    public void login(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mUser = mAuth.getCurrentUser();

                            try
                            {
                                if(mUser.isEmailVerified())
                                {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(mContext, "Login in successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(mContext, BottomNavigationActivity.class);
                                    mContext.startActivity(intent);
                                    ((Activity)mContext).finish();
                                }else
                                    Toast.makeText(mContext, "Please verify your email", Toast.LENGTH_SHORT).show();
                            }catch (NullPointerException ex)
                            {
                                Log.e("Firebase Methods", "" + ex.getMessage());
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //восстановление пароля
    public void recoverPassword(String email)
    {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(mContext, "Recovery link sent", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //страница профиля
    private void getDataForPost()
    {
        mUser = mAuth.getCurrentUser();
        Query query = mDatabaseReference.orderByChild("email").equalTo(mUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    nickname = ""+dataSnapshot.child("nickname").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getUserDataForPost", error.getDetails());
                Toast.makeText(mContext, error.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserDataProfile(final ImageView avatar, final TextView nickname, final TextView bio2, final TextView website) {
        mUser = mAuth.getCurrentUser();

        Query query = mDatabaseReference.orderByChild("uid").equalTo(mUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String nick = dataSnapshot.child("nickname").getValue(String.class);
                    String status = dataSnapshot.child("bio").getValue(String.class);
                    String image = dataSnapshot.child("image").getValue(String.class);
                    String web = dataSnapshot.child("website").getValue(String.class);

                    website.setText(web);


                    bio2.setText(status);
                    nickname.setText(nick);

                    if(image != null)
                        Glide.with(mContext).load(image).into(avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getUserDataProfile", error.getDetails());
                Toast.makeText(mContext, error.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserDataProfile(final ImageView avatar, final TextView nickname, final TextView bio2, String uid, final TextView website,final TextView title)
    {
        Query query = mDatabaseReference.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String status = dataSnapshot.child("bio").getValue(String.class);
                    String image = dataSnapshot.child("image").getValue(String.class);
                    String web = dataSnapshot.child("website").getValue(String.class);
                    String nick = dataSnapshot.child("nickname").getValue(String.class);

                    nickname.setText(nick);
                    website.setText(web);
                    bio2.setText(status);

                    if(image != null)
                        Glide.with(mContext).load(image).into(avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getUserDataProfile", error.getDetails());
                Toast.makeText(mContext, error.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //проверка свободен ли данный ник





    //обновление информации после редактирования профиля
    public void updateInfo(String nick, String bio, String web)
    {
        mUser = mAuth.getCurrentUser();
        HashMap<String, Object> newData = new HashMap<>();
        newData.put("nickname", nick);
        newData.put("bio", bio);
        newData.put("website", web);

        mDatabaseReference.child(mUser.getUid()).updateChildren(newData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "Updated:)", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, ""+e, Toast.LENGTH_SHORT);
            }
        });
    }

    //загрузка фотографии профиля
    public void uploadProfilePicture(Uri profilePic)
    {
        mUser = mAuth.getCurrentUser();
        String filePath = storagePath+""+ "_"+mUser.getUid();
        mStorageReference2 = mStorageReference.child(filePath);
        mStorageReference2.putFile(profilePic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image is uploaded to storage, now we have to store it in database using uri
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                while(!task.isSuccessful());
                Uri download = task.getResult();

                //check if image is uploaded
                if(task.isSuccessful())
                {
                    //update uri in database
                    HashMap<String, Object> photo = new HashMap<>();
                    photo.put("image", download.toString());
                    mDatabaseReference.child(mUser.getUid()).updateChildren(photo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(mContext, "Image is updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Error: "+e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }else
                {
                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //данные для редактирования профиля
    public void setDataForEditing(final EditText nickname, final EditText bio, final EditText website, final ImageView image)
    {
        mUser = mAuth.getCurrentUser();

        Query query = mDatabaseReference.orderByChild("email").equalTo(mUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String nick = dataSnapshot.child("nickname").getValue(String.class);
                    String bio2 = dataSnapshot.child("bio").getValue(String.class);
                    String website2 = dataSnapshot.child("website").getValue(String.class);
                    String avatar = dataSnapshot.child("image").getValue(String.class);

                    nickname.setText(nick);
                    bio.setText(bio2);
                    website.setText(website2);
                    Glide.with(mContext).load(avatar).into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("setDataForEditing", error.getDetails());
                Toast.makeText(mContext, "Database error", Toast.LENGTH_SHORT);
            }
        });
    }


    public void logoutUser(Activity activity)
    {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/" + mFirebaseUser.getUid());
        mAuth.signOut();
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent); 
        activity.finish();


    }

    public void addPost(String location, String description, String kilometers, String days,
                        String spinner, ArrayList<Uri> images, String postTime, int numberOfImages, Uri map, String country)
    {
        getDataForPost();
        mFirebaseUser =FirebaseAuth.getInstance().getCurrentUser();
                String postId = postRef.push().getKey();

                HashMap<Object, Object> hashMap = new HashMap<>();
                hashMap.put("uid", mUser.getUid());
                hashMap.put("time", postTime);
                hashMap.put("country", country);
                hashMap.put("description", description);
                hashMap.put("kilometers", kilometers);
                hashMap.put("days", days);
                hashMap.put("difficulty", spinner);
                hashMap.put("numOfImages", numberOfImages);
                hashMap.put("map", "");
                hashMap.put("postId", postId);

                postRef.child(postId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String pathMap = "Map/" + "map_" + map.getLastPathSegment();
                        StorageReference mapReference = FirebaseStorage.getInstance().getReference().child(pathMap);
                        for(int i = 0; i < numberOfImages; i++)
                        {
                            Uri uri = images.get(i);
                            String pathImage = "Posts/" + "post_" + uri.getLastPathSegment();
                            StorageReference imgReference = FirebaseStorage.getInstance().getReference().child(pathImage);

                            String key = "image" + (i+1);
                            imgReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            SendLinkImages(String.valueOf(uri), postId, key);
                                        }
                                    });
                                }
                            });
                        }

                        mapReference.putFile(map).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mapReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        SendLinkMap(String.valueOf(uri), postId);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }



    private void SendLinkImages(String downloadUri, String postId, String key) {
        HashMap <String , Object> image = new HashMap<>();
        image.put(key, downloadUri);

        postPhotoRef.child(postId).updateChildren(image);
    }

    private void SendLinkMap(String uri,String postId)
    {
        HashMap <String, Object> hashMap = new HashMap<>();
        hashMap.put("map", uri);
        postRef.child(postId).updateChildren(hashMap);
    }



   public void getCurrentUserNick(HashMap<String, Object> notificationData, RequestQueue mRequestQueue)
   {
       mUser = FirebaseAuth.getInstance().getCurrentUser();
       Query query = usersRef.orderByChild("uid").equalTo(mUser.getUid());

       query.addValueEventListener(new ValueEventListener() {
           String notification;
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        nickname =  dataSnapshot.child("nickname").getValue(String.class);

                    }
                    String notification = nickname  + notificationData.get("text");
                    notificationData.put("notification", notification);

                        createNotification(notificationData, mRequestQueue);
                }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               Log.e("getCurrentUserNick", error.getDetails());
               Toast.makeText(mContext, "Database error", Toast.LENGTH_SHORT);
           }
       });
   }

   public void createNotification(HashMap<String, Object> notificationData, RequestQueue requestQueue)
   {
       TOPIC = "/topics/" + notificationData.get("sendNotificationTo").toString();

       JSONObject notification = new JSONObject();
       JSONObject body = new JSONObject();

       try
       {
           body.put("title", "MounTrip");
           body.put("message", notificationData.get("notification"));

           notification.put("to", TOPIC);
           notification.put("data", body);
       }catch (JSONException e)
       {
           e.printStackTrace();
       }

       sendNotification(notification, notificationData, requestQueue);
   }

   private void sendNotification(JSONObject notification, HashMap<String, Object> notificationData, RequestQueue requestQueue)
   {
       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               Log.i(TAG, "onResponse: " + response.toString());
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               Toast.makeText(mContext, "Request error", Toast.LENGTH_LONG).show();
               Log.i(TAG, "onErrorResponse: Didn't work");
           }
       })
       {
           @Override
           public Map<String, String> getHeaders() throws AuthFailureError {
               Map params = new HashMap();
               params.put("Authorization", serverKey);
               params.put("Content-Type", contentType);

               return params;
           }
       };

       requestQueue.add(jsonObjectRequest);
       addNotification(notificationData, requestQueue);
   }

   private void addNotification(HashMap<String, Object> notificationData, RequestQueue requestQueue)
   {
       mUser = FirebaseAuth.getInstance().getCurrentUser();
       String key = notifRef.push().getKey();
       notificationData.put("date", String.valueOf(System.currentTimeMillis()));
       notificationData.put("uid", mUser.getUid());
       notificationData.put("notificationId", key);
       String sendNotificationTo = notificationData.get("sendNotificationTo").toString();

       notifRef.child(sendNotificationTo).child(key).setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {

               if(notificationData.get("type") == "reply" && !notificationData.get("postAuthorUid").toString().equals(mUser.getUid())
                       && !notificationData.get("postAuthorUid").toString().equals(sendNotificationTo)) {
                   notificationData.replace("text", " commented your post");
                   notificationData.replace("type", "comment");
                   notificationData.replace("sendNotificationTo", notificationData.get("postAuthorUid"));
                   notificationData.remove("postAuthorUid");
                   getCurrentUserNick(notificationData, requestQueue);
               }
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               e.printStackTrace();
           }
       });


   }

    public void mapOptions(Uri mapUri)
    {
        bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.bottom_map_menu);

        LinearLayout open = bottomSheetDialog.findViewById(R.id.open_map_bottom_menu);
        LinearLayout download = bottomSheetDialog.findViewById(R.id.download_map_bottom_menu);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadOrOpen = true;
                if(checkPermissions(bottomSheetDialog))
                    openMap(mapUri);
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissions(bottomSheetDialog))
                    openMap(mapUri);
            }
        });

        bottomSheetDialog.show();
    }

    private boolean checkPermissions(BottomSheetDialog bottomSheetDialog)
    {
        for(String permission : storagePermission)
        {
            if(mContext.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Storage permission denied", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
                return false;
            }
        }
        return true;
    }



    private void openMap(Uri mapUri)
    {
        String strPath = mapUri.getPath().toString();
        File file = new File(strPath);
        String filename = file.getName() + ".gpx";
        File searchFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

        if(searchFile.exists() && downloadOrOpen == true)
        {
            Intent intentMap = new Intent(mContext, MapActivity.class);
            intentMap.putExtra("MapCoordinates", searchFile);
            mContext.startActivity(intentMap);
        }
        else if(!searchFile.exists())
        {
            DownloadManager.Request request = new DownloadManager.Request(mapUri);
            request.setDescription("Downloading...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);


            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
            request.setRequiresCharging(false);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);
        }else
        {
            Toast.makeText(mContext, "This file is already uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void setLikeButtonStatus(final String key, ImageButton likes, TextView likesNum)
    {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int likesCounter = (int)snapshot.child(key).getChildrenCount();
                if(snapshot.child(key).hasChild(mFirebaseUser.getUid()))
                {
                    likes.setImageResource(R.drawable.ic_like);
                    likesNum.setText(likesCounter + " Likes");
                }else if(likesCounter == 0)
                {
                    likes.setImageResource(R.drawable.notification);
                    likesNum.setText("");
                }else
                {
                    likes.setImageResource(R.drawable.notification);
                    likesNum.setText(likesCounter + " Likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("setLikeButtonStatus", error.getDetails());
            }
        });
    }
}