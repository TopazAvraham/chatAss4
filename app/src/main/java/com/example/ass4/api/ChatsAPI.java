package com.example.ass4.api;

import static java.lang.Thread.sleep;

import androidx.lifecycle.MutableLiveData;

import com.example.ass4.MyApplication;
import com.example.ass4.R;
import com.example.ass4.entities.Chat;
import com.example.ass4.entities.Message;
import com.example.ass4.entities.User;

import java.io.IOException;
import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatsAPI {
//private MutableLiveData<List<Chat>> ChatListData;
//private PostDao dao;
Retrofit retrofit;
WebServiceAPI webServiceAPI;
Chat tempChat;
List<Chat> tempChatList;
     public ChatsAPI() { //MutableLiveData<List<Chat>> postListData, PostDao dao
     /*
     this.postListData = postListData;
     this.dao = dao;
    */
      retrofit = new Retrofit.Builder()
      .baseUrl(MyApplication.getContext().getString(R.string.BaseUrl))
      .addConverterFactory(GsonConverterFactory.create())
      .build();
      webServiceAPI = retrofit.create(WebServiceAPI.class);
      }

    public List<Chat> getChats() {
        List<Chat> tempChatList = new ArrayList<>();
        Call<List<ResponseGetChatsAPI>> call = webServiceAPI.getChats(MyApplication.getToken());
        Response<List<ResponseGetChatsAPI>> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return tempChatList; // Return empty list if an error occurs
        }

        if (!response.isSuccessful()) {
            return tempChatList; // Return empty list if the response is not successful
        }

        List<ResponseGetChatsAPI> chats = response.body();
        if (chats != null) {
            for (ResponseGetChatsAPI chat : chats) {
                User user = new User(chat.getUser().getUsername(), chat.getUser().getProfilePic(), chat.getUser().getDisplayName());

                ResponseGetChatsAPI.Message last = chat.getLastMessage();
                Message lastMessage = new Message(last.getId(), last.getContent(), getDate(last.getCreated()), true);
                Chat chat1 = new Chat(chat.getId(), new ArrayList<>(), user, lastMessage);
                tempChatList.add(chat1);
            }
        }

        return tempChatList;
    }

    public Chat getChatByID(String id) {
      Call<ResponseGetChatByIDAPI> call = webServiceAPI.getChatById(id,MyApplication.getToken());
        call.enqueue(new Callback<ResponseGetChatByIDAPI>() {
                @Override
                public void onResponse(Call<ResponseGetChatByIDAPI> call, Response<ResponseGetChatByIDAPI> response) {
                if(!response.isSuccessful()){
                    return;
                }
                ResponseGetChatByIDAPI chat = response.body();
                List< Message> messages = new ArrayList<>();
                List<ResponseGetChatByIDAPI.Message> messages1 = chat.getMessages();
               for (ResponseGetChatByIDAPI.Message m : messages1){
                   Date date = getDate(m.getCreated());

                    Message message = new Message(m.getId(), m.getContent(), date,MyApplication.isThatMe(m.getSender().getUsername()));
                    messages.add(message);
                }
                ResponseGetChatByIDAPI.User otherUser = null;

                List<ResponseGetChatByIDAPI.User> users = chat.getUsers();
                for (ResponseGetChatByIDAPI.User user : users) {
                    if (!MyApplication.isThatMe(user.getUsername())) {
                        otherUser = user;
                        break; // Assuming you only want the first user that meets the condition
                    }
                }
                ResponseGetChatByIDAPI.Message last= chat.getLastMessage();
                Message lastMessage= new Message(last.getId(), last.getContent(), getDate(last.getCreated()),MyApplication.isThatMe(last.getSender().getUsername()));
               User user = new User(otherUser.getUsername(), otherUser.getProfilePic(), otherUser.getDisplayName());
                tempChat = new Chat(chat.getId(), messages,user,lastMessage);

            }

            @Override
            public void onFailure(Call<ResponseGetChatByIDAPI> call, Throwable t) {
                System.out.println("Failed to get posts");
            }
        });
         return tempChat;
        }
        public static Date getDate(String dateString){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Date date = null;
            try {
                date = dateFormat.parse(dateString);
                return date;
                // Use the parsed date object as needed
            } catch (ParseException e) {
                return null;
            }
        }
     }
