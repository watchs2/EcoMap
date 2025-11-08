package amov.a2020157100.ecomap.ui.viewmodels

import amov.a2020157100.ecomap.model.toUser
import amov.a2020157100.ecomap.model.User
import amov.a2020157100.ecomap.utils.firebase.FAuthUtil
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FirebaseViewModel : ViewModel() {
    private val _user = mutableStateOf(FAuthUtil.currentUser?.toUser())
    val user: State<User?>
        get() = _user

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?>
        get() = _error

    fun createUserWithEmail(email: String, password: String, passwordConfirm: String){
        if(email.isBlank() || password.isBlank() || passwordConfirm.isBlank()){
            return;
        }
        if(password != passwordConfirm){
            _error.value = "Passwords are not maching"
            return;
        }
        viewModelScope.launch {
            FAuthUtil.createUserWithEmail(email,password) { exception ->
                if(exception == null){
                    _user.value = FAuthUtil.currentUser?.toUser()
                }
                _error.value = exception?.message
            }
        }

    }

    fun signInWithEmail(email: String,password: String){
        if(email.isBlank() || password.isBlank()){
            return
        }
        viewModelScope.launch {
            FAuthUtil.signInWithEmail(email,password){ exception ->
                if(exception == null){
                    _user.value = FAuthUtil.currentUser?.toUser()
                }
                _error.value = exception?.message
            }
        }
    }

    fun signOut(){
        FAuthUtil.signOut()
        _user.value = null
        _error.value = null
    }
}