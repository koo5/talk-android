package com.nextcloud.talk.newarch.features.chat

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.nextcloud.talk.models.json.conversations.Conversation
import com.nextcloud.talk.newarch.conversationsList.mvp.BaseViewModel
import com.nextcloud.talk.newarch.domain.repository.offline.ConversationsRepository
import com.nextcloud.talk.newarch.domain.repository.offline.MessagesRepository
import com.nextcloud.talk.newarch.domain.usecases.ExitConversationUseCase
import com.nextcloud.talk.newarch.domain.usecases.JoinConversationUseCase
import com.nextcloud.talk.newarch.local.models.UserNgEntity
import com.nextcloud.talk.newarch.utils.ConversationsManager
import com.nextcloud.talk.newarch.utils.ConversationsManagerInterface
import kotlinx.coroutines.launch

class ChatViewModel constructor(application: Application,
                                private val joinConversationUseCase: JoinConversationUseCase,
                                private val exitConversationUseCase: ExitConversationUseCase,
                                private val conversationsRepository: ConversationsRepository,
                                private val messagesRepository: MessagesRepository,
                                private val conversationsManager: ConversationsManager) : BaseViewModel<ChatView>(application), ConversationsManagerInterface {
    lateinit var user: UserNgEntity
    val conversation: MutableLiveData<Conversation?> = MutableLiveData()
    var initConversation: Conversation? = null
    val messagesLiveData = Transformations.switchMap(conversation) {
        it?.let {
            messagesRepository.getMessagesWithUserForConversation(it.conversationId!!)
        }
    }
    var conversationPassword: String? = null


    fun init(user: UserNgEntity, conversationToken: String, conversationPassword: String?) {
        viewModelScope.launch {
            this@ChatViewModel.user = user
            this@ChatViewModel.initConversation = conversationsRepository.getConversationForUserWithToken(user.id!!, conversationToken)
            this@ChatViewModel.conversationPassword = conversationPassword
            conversationsManager.getConversation(conversationToken, this@ChatViewModel)
        }
    }

    fun sendMessage(message: CharSequence) {

    }

    override suspend fun gotConversationInfoForUser(userNgEntity: UserNgEntity, conversation: Conversation?, operationStatus: ConversationsManagerInterface.OperationStatus) {
        if (operationStatus == ConversationsManagerInterface.OperationStatus.STATUS_OK) {
            if (userNgEntity.id == user.id && conversation!!.token == initConversation?.token) {
                this.conversation.value = conversationsRepository.getConversationForUserWithToken(user.id!!, conversation.token!!)
                conversation.token?.let { conversationToken ->
                    conversationsManager.joinConversation(conversationToken, conversationPassword, this)
                }
            }
        }
    }

    override suspend fun joinedConversationForUser(userNgEntity: UserNgEntity, conversation: Conversation?, operationStatus: ConversationsManagerInterface.OperationStatus) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}