package com.example.chatbot

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

public class ChatViewModel : ViewModel() {

    val messageList by lazy{
        mutableStateListOf<MessageModel>()
    }

    val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = Constants.apiKey
    )

    fun sendMessage(question: String){
        viewModelScope.launch {

            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role){text(it.message)}
                    }.toList()
                )
                messageList.add(MessageModel(question, "User"))
                messageList.add(MessageModel("Typing....","Assistant"))
                val aiResponse = chat.sendMessage(question)
                messageList.removeAt(messageList.size-1)
                messageList.add(MessageModel(aiResponse.text.toString(), "Assistant"))
            } catch (e : Exception){
                messageList.removeAt(messageList.size-1)
                messageList.add(MessageModel("Unable to generate response." + e.printStackTrace(),"Assistant"))

            }

        }
    }
}
